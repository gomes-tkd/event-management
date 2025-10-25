package com.github.gomestkd.eventmanagement.services;

import com.github.gomestkd.eventmanagement.controllers.EventController;
import com.github.gomestkd.eventmanagement.dto.EventDTO;

import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.exception.FileStorageException;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.exporter.contract.EventExporter;
import com.github.gomestkd.eventmanagement.file.exporter.factory.FileExporterFactory;
import com.github.gomestkd.eventmanagement.file.importer.contract.EventImporter;
import com.github.gomestkd.eventmanagement.file.importer.factory.FileImporterFactory;
import com.github.gomestkd.eventmanagement.model.Event;
import com.github.gomestkd.eventmanagement.repositories.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EventService implements Serializable {
    Logger logger = LoggerFactory.getLogger(GuestService.class);

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PagedResourcesAssembler<EventDTO> assembler;


    @Autowired
    private FileImporterFactory fileImporter;

    @Autowired
    private FileExporterFactory fileExporter;

    public EventService(
            EventRepository eventRepository,
            PagedResourcesAssembler<EventDTO> assembler,
            FileExporterFactory fileExporterFactory,
            FileImporterFactory fileImporterFactory
    ) {
        this.eventRepository = eventRepository;
        this.assembler = assembler;
        this.fileImporter = fileImporterFactory;
        this.fileExporter = fileExporterFactory;
    }

    public PagedModel<EntityModel<EventDTO>> findAll(Pageable pageable) {
        logger.info("Fetching all events - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Event> eventPage = eventRepository.findAll(pageable);

        logger.debug("Found {} events", eventPage.getTotalElements());
        return buildPagedModel(eventPage);
    }

    public EventDTO findById(Long id) {
        if (id == null) {
            logger.error("Id is null");
            throw new ResourceNotFoundException("Event with ID is null");
        }

        logger.info("Searching for event with ID: {}", id);

        Event event = eventRepository.findById(id).orElseThrow(() -> {
            logger.error("No item found for ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });
        EventDTO eventDTO = parseObject(event, EventDTO.class);
        addHateoasLinks(eventDTO);

        logger.info("Item found: {}", eventDTO.getName());
        return eventDTO;
    }

    public PagedModel<EntityModel<EventDTO>> findEventByName(String name, Pageable pageable) {
        if (name.isBlank()) {
            logger.error("Name is null");
            throw new ResourceNotFoundException("Event with name is null");
        }

        Page<Event> events = eventRepository.findEventsByName(name, pageable);
        logger.debug("Found {} events for name '{}'", events.getTotalElements(), name);

        return buildPagedModel(events);
    }

    public PagedModel<EntityModel<EventDTO>> findEventsByDescription(String description, Pageable pageable) {
        if (description.isBlank()) {
            logger.error("Description is null");
            throw new ResourceNotFoundException("Event with description is null");
        }

        logger.info("Searching events by description containing: '{}'", description);
        Page<Event> events = eventRepository.findEventsByDescription(description, pageable);

        logger.debug("Found {} items for description '{}'", events.getTotalElements(), description);
        return buildPagedModel(events);
    }

    public PagedModel<EntityModel<EventDTO>> findEventsByLocation(String location, Pageable pageable) {
        if (location.isBlank()) {
            logger.error("Location is null");
            throw new ResourceNotFoundException("Event with location is null");
        }

        logger.info("Searching events by location containing: '{}'", location);
        Page<Event> events = eventRepository.findEventsByLocation(location, pageable);

        logger.debug("Found {} items for location '{}'", events.getTotalElements(), location);
        return buildPagedModel(events);
    }

    public PagedModel<EntityModel<EventDTO>> findEventsByTime(Date start, Date end, Pageable pageable) {
        if (end.before(start)) {
            logger.warn("Invalid date range: endTime {} is before startTime {}", end, start);
            throw new IllegalArgumentException("endTime cannot be before startTime.");
        }
        logger.info("Searching for events between {} and {}", start, end);

        Page<Event> events = eventRepository.findEventsByTimeRange(start, end, pageable);

        if (events.isEmpty()) {
            logger.warn("No events found between {} and {}", start, end);
        }

        return buildPagedModel(events);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        verifyAcceptHeader(acceptHeader);

        logger.info("Exporting page of events, format: {}", acceptHeader);

        Set<EventDTO> events = eventRepository
                .findAll(pageable)
                .map(event -> parseObject(event, EventDTO.class))
                .toSet();

        try {
            EventExporter exporter = this.fileExporter.getEventExporter(acceptHeader);
            Resource resource = exporter.exportEvents(events);
            logger.info("Export successful - {} events exported", events.size());
            return resource;
        } catch (Exception e) {
            logger.error("Error during file export: {}", e.getMessage(), e);
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public Resource exportEvent(Long id, String acceptHeader) {
        verifyAcceptHeader(acceptHeader);

        logger.info("Exporting single event with ID: {}, format: {}", id, acceptHeader);

        Event event = eventRepository.findById(id).orElseThrow(() -> {
            logger.error("No event found for export with ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });

        EventDTO eventDTO = parseObject(event, EventDTO.class);

        try {
            EventExporter exporter = this.fileExporter.getEventExporter(acceptHeader);
            logger.debug("Exporting event: {}", event.getName());
            return exporter.exportEvent(eventDTO);
        } catch (Exception e) {
            logger.error("Error exporting event with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public Set<EventDTO> massCreation(MultipartFile file) {
        logger.info("Mass creation started via file upload: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.error("Uploaded file is empty!");
            throw new BadRequestException("Please select a valid file!");
        }

        try(InputStream inputStream = file.getInputStream()) {
            String filename = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("Please select a valid file!"));

            EventImporter importer = this.fileImporter.getEventImporter(filename);
            Set<Event> entities = importer
                    .importEvents(inputStream)
                    .stream()
                    .map(dto -> {
                        Event event = eventRepository.save(parseObject(dto, Event.class));
                        logger.debug("Saved imported item: {}", event.getName());

                        return event;
                    })
                    .collect(Collectors.toSet());

            logger.info("Successfully imported {} events", entities.size());

            return entities.stream()
                    .map(entity -> {
                        EventDTO dto = parseObject(entity, EventDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    })
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            logger.error("Error processing file during mass creation: {}", e.getMessage(), e);
            throw new FileStorageException("Error processing the file!");
        }
    }

    public EventDTO create(EventDTO dto) {
        verifyEventDTO(dto);

        Event entity = parseObject(dto, Event.class);
        EventDTO eventDTO = parseObject(eventRepository.save(entity), EventDTO.class);
        addHateoasLinks(eventDTO);

        logger.info("Event successfully created: {}", eventDTO.getName());

        return eventDTO;
    }

    public EventDTO update(EventDTO dto) {
        verifyEventDTO(dto);

        Event event = eventRepository.findById(dto.getId()).orElseThrow(() -> {
            logger.error("No item found for update with ID: {}", dto.getId());
            return new ResourceNotFoundException("No records found for this ID!");
        });

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
        event.setLocation(dto.getLocation());

        EventDTO dtoUpdated = parseObject(eventRepository.save(event), EventDTO.class);
        addHateoasLinks(dtoUpdated);
        logger.info("Event successfully updated: {}", dtoUpdated.getName());

        return dtoUpdated;
    }

    public void delete(Long id) {
        logger.info("Deleting event with ID: {}", id);
        Event event = eventRepository.findById(id).orElseThrow(() -> {
            logger.error("No event found for deletion with ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });
        eventRepository.delete(event);
        logger.info("Event with ID {} successfully deleted", id);

    }

    private PagedModel<EntityModel<EventDTO>> buildPagedModel(Page<Event> eventPage) {
        logger.debug("Building PagedModel for {} events", eventPage.getTotalElements());
        Page<EventDTO> eventDTOPage = eventPage.map(event -> {
            EventDTO eventDTO = parseObject(event, EventDTO.class);
            addHateoasLinks(eventDTO);
            return eventDTO;
        });

        return assembler.toModel(eventDTOPage);
    }

    private void addHateoasLinks(EventDTO eventDTO) {
        logger.trace("Adding HATEOAS links to event: {}", eventDTO.getId());

        try {
            eventDTO.add(linkTo(methodOn(EventController.class).findById(eventDTO.getId())).withSelfRel());

            eventDTO.add(linkTo(EventController.class).slash("create").withRel("create").withType("POST").withTitle("Create Event"));
            eventDTO.add(linkTo(EventController.class).slash("updateEvent").withRel("updateEvent").withType("PUT").withTitle("Update Event"));
            eventDTO.add(linkTo(EventController.class).slash(eventDTO.getId()).withRel("delete").withType("DELETE").withTitle("Delete Event"));
            eventDTO.add(linkTo(EventController.class).slash("massCreation").withRel("massCreation").withType("POST").withTitle("Massive Creation"));

            eventDTO.add(linkTo(methodOn(EventController.class).findAll(0, 15, "asc")).withRel("findAll").withType("GET").withTitle("Find All"));
            eventDTO.add(linkTo(methodOn(EventController.class).exportPage(0, 15, "asc", null)).withRel("exportPage").withType("GET").withTitle("Export Events"));
            eventDTO.add(linkTo(methodOn(EventController.class).exportEvent(eventDTO.getId(), null)).withRel("exportEvent").withType("GET").withTitle("Export Event"));

            eventDTO.add(linkTo(methodOn(EventController.class).findEventsByName("{name}", 0, 15, "asc")).withRel("findByName").withType("GET").withTitle("Find By Name"));
            eventDTO.add(linkTo(methodOn(EventController.class).findEventsByDescription("{description}", 0, 15, "asc")).withRel("findByDescription").withType("GET").withTitle("Find By Description"));
        } catch (Exception e) {
            // Log the warning but don't stop the process
            logger.warn("Failed to add HATEOAS links for Event {}: {}", eventDTO.getId(), e.getMessage());
        }
    }

    private void verifyAcceptHeader(String  acceptHeader) {
        if (acceptHeader.isBlank()) {
            logger.warn("Header is null.");
            throw new IllegalArgumentException("Header is null.");
        }
    }

    private void verifyEventDTO(EventDTO eventDTO) {
        logger.info("Creating new item: {}", eventDTO != null ? eventDTO.getName() : "NULL");
        if (eventDTO == null) {
            logger.error("Attempted to create null eventDTO");
            throw new BadRequestException("EventDTO cannot be null");
        }
    }
}