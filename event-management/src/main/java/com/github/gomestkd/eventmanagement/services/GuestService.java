package com.github.gomestkd.eventmanagement.services;

import com.github.gomestkd.eventmanagement.controllers.GuestController;
import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.exception.FileStorageException;
import com.github.gomestkd.eventmanagement.file.exporter.contract.GuestExporter;
import com.github.gomestkd.eventmanagement.file.exporter.factory.FileExporterFactory;
import com.github.gomestkd.eventmanagement.file.importer.contract.GuestImporter;
import com.github.gomestkd.eventmanagement.file.importer.factory.FileImporterFactory;
import com.github.gomestkd.eventmanagement.model.Guest;
import com.github.gomestkd.eventmanagement.repositories.GuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class GuestService {
    Logger logger = LoggerFactory.getLogger(GuestService.class);

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private PagedResourcesAssembler<GuestDTO> assembler;

    @Autowired
    private FileImporterFactory fileImporter;

    @Autowired
    private FileExporterFactory fileExporter;

    public GuestService(GuestRepository guestRepository, PagedResourcesAssembler<GuestDTO> assembler, FileImporterFactory fileImporter, FileExporterFactory fileExporter) {
        this.guestRepository = guestRepository;
        this.assembler = assembler;
        this.fileImporter = fileImporter;
        this.fileExporter = fileExporter;
    }

    public PagedModel<EntityModel<GuestDTO>> findAll(Pageable pageable) {
        logger.info("Fetching all guests - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Guest> guestsPage = guestRepository.findAll(pageable);
        logger.debug("Found {} guests", guestsPage.getTotalElements());
        return buildPagedModel(pageable, guestsPage);
    }

    public GuestDTO findById(Long id) {
        logger.info("Searching for guest with ID: {}", id);

        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Guest with ID: {} not found", id);
                    return new RuntimeException("Guest not found");
                });

        GuestDTO guestDTO = parseObject(guest, GuestDTO.class);
        addHateoasLinks(guestDTO);

        logger.info("Guest found: {}", guestDTO.getName());

        return guestDTO;
    }

    public PagedModel<EntityModel<GuestDTO>> findByName(String name, Pageable pageable) {
        logger.info("Searching guests by name: '{}'", name);
        Page<Guest> guests = guestRepository.findGuestsByName(name, pageable);

        logger.debug("Found {} guests for name '{}'", guests.getTotalElements(), name);

        return buildPagedModel(pageable, guests);

    }

    public PagedModel<EntityModel<GuestDTO>> findGuestByEmail(String email, Pageable pageable) {
        logger.info("Searching guests by email: '{}'", email);
        Page<Guest> guests = guestRepository.findGuestsByEmail(email, pageable);

        logger.debug("Found {} guests for email '{}'", guests.getTotalElements(), email);
        return buildPagedModel(pageable, guests);
    }

    public PagedModel<EntityModel<GuestDTO>> findGuestByPhone(String phone, Pageable pageable) {
        logger.info("Searching guests by phone: '{}'", phone);
        Page<Guest> guests = guestRepository.findGuestByPhone(phone, pageable);

        logger.debug("Found {} guests for phone '{}'", guests.getTotalElements(), phone);
        return buildPagedModel(pageable, guests);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting page of guests, format: {}", acceptHeader);

        Set<GuestDTO> guests = guestRepository
            .findAll(pageable)
            .map(guest -> parseObject(guest, GuestDTO.class))
            .toSet();

        try {
            GuestExporter exporter = this. fileExporter.getGuestExporter(acceptHeader);
            Resource resource = exporter.exportGuests(guests);

            logger.info("Export successful - {} guests exported", guests.size());
            return resource;
        } catch (Exception e) {
            logger.error("Error during guests export: {}", e.getMessage(), e);
            throw new RuntimeException("Error during guests export!", e);
        }
    }

    public Resource exportGuest(Long id, String acceptHeader) {
        logger.info("Exporting single guest with ID: {}, format: {}", id, acceptHeader);

        Guest guest = guestRepository.findById(id)
            .orElseThrow(() -> {
                logger.error("Guest with ID: {} not found for export", id);
                return new RuntimeException("Guest not found");
            });

        GuestDTO guestDTO = parseObject(guest, GuestDTO.class);

        try {
            GuestExporter exporter = this.fileExporter.getGuestExporter(acceptHeader);
            Resource resource = exporter.exportGuest(guestDTO);
            logger.info("Export successful for guest ID: {}", id);
            return resource;
        } catch (Exception e) {
            logger.error("Error during export of guest ID: {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error during guest export!", e);
        }
    }

    public Set<GuestDTO> massCreation(MultipartFile file) {
        logger.info("Mass creation started via file upload: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.error("Uploaded file is empty!");
            throw new RuntimeException("Please set a Valid File!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String filename = Optional.ofNullable(file.getOriginalFilename())
                .orElseThrow(() -> new BadRequestException("Please set a Valid File"));

            GuestImporter importer = this.fileImporter.getGuestImporter(filename);

            Set<Guest> entities = importer.importGuests(inputStream).stream()
                .map(dto -> {
                    Guest saved = guestRepository.save(parseObject(dto, Guest.class));
                    logger.debug("Saved imported guest: {}", saved.getName());
                    return saved;
                })
                .collect(Collectors.toSet());

            logger.info("Successfully imported {} guests", entities.size());

            return entities.stream()
                    .map(entity -> {
                        GuestDTO dto = parseObject(entity, GuestDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    }).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("Error processing file during mass creation: {}", e.getMessage(), e);
            throw new FileStorageException("Error processing the file!");
        }
    }

    public GuestDTO create(GuestDTO guestDTO) {
        logger.info("Creating new guest: {}", guestDTO != null ? guestDTO.getName() : "NULL");

        if (guestDTO == null) {
            logger.error("Attempted to create null guestDTO");
            throw new IllegalArgumentException("GuestDTO is null");
        }

        Guest guest = parseObject(guestDTO, Guest.class);
        Guest savedGuest = guestRepository.save(guest);

        GuestDTO savedGuestDTO = parseObject(savedGuest, GuestDTO.class);
        addHateoasLinks(savedGuestDTO);

        logger.info("Guest created with ID: {}", savedGuestDTO.getId());
        return savedGuestDTO;
    }

    public GuestDTO update(GuestDTO guestDTO) {
        logger.info("Updating guest with ID: {}", guestDTO != null ? guestDTO.getId() : "NULL");

        if (guestDTO == null || guestDTO.getId() == null) {
            logger.error("GuestDTO or Guest ID is null");
            throw new IllegalArgumentException("GuestDTO or Guest ID is null");
        }

        if (!guestRepository.existsById(guestDTO.getId())) {
            logger.error("Guest with ID: {} not found for update", guestDTO.getId());
            throw new RuntimeException("Guest not found");
        }

        Guest guest = parseObject(guestDTO, Guest.class);
        Guest updatedGuest = guestRepository.save(guest);

        GuestDTO updatedGuestDTO = parseObject(updatedGuest, GuestDTO.class);
        addHateoasLinks(updatedGuestDTO);

        logger.info("Guest updated with ID: {}", updatedGuestDTO.getId());
        return updatedGuestDTO;
    }

    public void delete(Long id) {
        logger.info("Deleting guest with ID: {}", id);
        Guest guest = guestRepository.findById(id).orElseThrow(() -> {
            logger.error("No guest found for deletion with ID: {}", id);
            return new RuntimeException("No records found for this ID!");
        });
        guestRepository.delete(guest);
        logger.info("Guest with ID {} successfully deleted", id);
    }

    private PagedModel<EntityModel<GuestDTO>> buildPagedModel(Pageable pageable, Page<Guest> guests) {
        logger.debug("Building PagedModel for {} guests", guests.getTotalElements());

        Page<GuestDTO> guestDTOSWithLink = guests.map(guest -> {
            GuestDTO guestDTO = parseObject(guest, GuestDTO.class);
            addHateoasLinks(guestDTO);
            return guestDTO;
        });

        Link findAllLink = linkTo(
                methodOn(GuestController.class)
                        .findAll(
                            pageable.getPageNumber(),
                            pageable.getPageSize(),
                            String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();
        return assembler.toModel(guestDTOSWithLink, findAllLink);
    }

    private void addHateoasLinks(GuestDTO guestDTO) {
        logger.trace("Adding HATEOAS links to guest: {}", guestDTO.getId());
        try {
            guestDTO.add(linkTo(methodOn(GuestController.class).findAll(1, 15, "asc")).withRel("findAll").withType("GET").withTitle("Find All"));
            guestDTO.add(linkTo(methodOn(GuestController.class).findById(guestDTO.getId())).withRel("findById").withType("GET").withTitle("Find By Id"));
            guestDTO.add(linkTo(methodOn(GuestController.class).findByName("", 1, 15, "asc")).withRel("findByName").withType("GET").withTitle("Find By Name"));
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .findGuestByEmail("", 1, 15, "asc")
                    )
                            .withRel("findByEmail")
                            .withType("GET")
                            .withTitle("Find By Email")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .findGuestByPhone("", 1, 15, "asc")
                    )
                            .withRel("findByPhone")
                            .withType("GET")
                            .withTitle("Find by Phone")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .exportPage(1, 15, "asc", null)
                    )
                            .withRel("exportPage")
                            .withType("GET")
                            .withTitle("Export Guests")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .exportGuest(guestDTO.getId(), null)
                    )
                            .withRel("exportGuest")
                            .withType("GET")
                            .withTitle("Export Guest")
            );
            guestDTO.add(
                    linkTo(methodOn(GuestController.class))
                            .slash(guestDTO)
                            .withRel("massCreation")
                            .withType("POST")
                            .withTitle("Massive Creation")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .create(guestDTO)
                    )
                            .withRel("create")
                            .withType("POST")
                            .withTitle("Create Guest")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .update(guestDTO)
                    )
                            .withRel("updateGuest")
                            .withType("PUT")
                            .withTitle("Update Guest")
            );
            guestDTO.add(
                    linkTo(
                            methodOn(GuestController.class)
                                    .delete(guestDTO.getId())
                    )
                            .withRel("delete")
                            .withType("DELETE")
                            .withTitle("Delete Guest")
            );
        } catch (Exception e) {
            logger.warn("Failed to add HATEOAS links for item {}: {}", guestDTO.getId(), e.getMessage());
        }
    }
}
