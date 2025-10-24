package com.github.gomestkd.eventmanagement.services;

import com.github.gomestkd.eventmanagement.controllers.ItemController;
import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.exception.FileStorageException;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
import com.github.gomestkd.eventmanagement.file.exporter.factory.FileExporterFactory;
import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
import com.github.gomestkd.eventmanagement.file.importer.factory.FileImporterFactory;
import com.github.gomestkd.eventmanagement.model.Item;
import com.github.gomestkd.eventmanagement.repositories.ItemRepository;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class ItemService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PagedResourcesAssembler<ItemDTO> assembler;

    @Autowired
    private FileImporterFactory fileImporter;

    @Autowired
    private FileExporterFactory fileExporter;

    public ItemService(
            ItemRepository itemRepository,
            PagedResourcesAssembler<ItemDTO> assembler,
            FileImporterFactory fileImporter,
            FileExporterFactory fileExporter
    ) {
        this.itemRepository = itemRepository;
        this.assembler = assembler;
        this.fileImporter = fileImporter;
        this.fileExporter = fileExporter;
    }

    public PagedModel<EntityModel<ItemDTO>> findAll(Pageable pageable) {
        logger.info("Fetching all items - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Item> itemPage = itemRepository.findAll(pageable);

        logger.debug("Found {} items", itemPage.getTotalElements());
        return buildPagedModel(itemPage);
    }

    public ItemDTO findById(Long id) {
        logger.info("Searching for item with ID: {}", id);

        Item item = itemRepository.findById(id).orElseThrow(() -> {
            logger.error("No item found for ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });

        ItemDTO itemDTO = parseObject(item, ItemDTO.class);
        addHateoasLinks(itemDTO);

        logger.info("Item found: {}", itemDTO.getName());
        return itemDTO;
    }

    public PagedModel<EntityModel<ItemDTO>> findByName(String name, Pageable pageable) {
        logger.info("Searching items by name: '{}'", name);

        Page<Item> items = itemRepository.findItemsByName(name, pageable);

        logger.debug("Found {} items for name '{}'", items.getTotalElements(), name);

        return buildPagedModel(items);
    }

    public PagedModel<EntityModel<ItemDTO>> findByDescription(String description, Pageable pageable) {
        logger.info("Searching items by description containing: '{}'", description);

        Page<Item> items = itemRepository.findItemsByDescription(description, pageable);

        logger.debug("Found {} items for description '{}'", items.getTotalElements(), description);

        return buildPagedModel(items);
    }

    public PagedModel<EntityModel<ItemDTO>> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        logger.info("Searching items by price range: {} - {}", minPrice, maxPrice);
        Page<Item> items = itemRepository.findItemsByPriceRange(minPrice, maxPrice, pageable);
        logger.debug("Found {} items in price range {} - {}", items.getTotalElements(), minPrice, maxPrice);
        return buildPagedModel(items);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting page of items, format: {}", acceptHeader);

        Set<ItemDTO> items = itemRepository
                .findAll(pageable)
                .map(item -> parseObject(item, ItemDTO.class))
                .toSet();

        try {
            ItemExporter exporter = this.fileExporter.getItemExporter(acceptHeader);
            Resource resource = exporter.exportItems(items);
            logger.info("Export successful - {} items exported", items.size());
            return resource;
        } catch (Exception e) {
            logger.error("Error during file export: {}", e.getMessage(), e);
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public Resource exportItem(Long id, String acceptHeader) {
        logger.info("Exporting single item with ID: {}, format: {}", id, acceptHeader);
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            logger.error("No item found for export with ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });
        ItemDTO itemDTO = parseObject(item, ItemDTO.class);

        try {
            ItemExporter exporter = this.fileExporter.getItemExporter(acceptHeader);
            logger.debug("Exporting item: {}", itemDTO.getName());
            return exporter.exportItem(itemDTO);
        } catch (Exception e) {
            logger.error("Error exporting item with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error during file export!", e);
        }
    }

    public Set<ItemDTO> massCreation(MultipartFile file) {
        logger.info("Mass creation started via file upload: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.error("Uploaded file is empty!");
            throw new BadRequestException("Please select a valid file!");
        }

        try (InputStream inputStream = file.getInputStream()) {
            String filename = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("Please select a valid file!"));
            ItemImporter importer = this.fileImporter.getItemImporter(filename);
            Set<Item> entities = importer.importItems(inputStream).stream()
                    .map(dto -> {
                        Item saved = itemRepository.save(parseObject(dto, Item.class));
                        logger.debug("Saved imported item: {}", saved.getName());
                        return saved;
                    })
                    .collect(Collectors.toSet());

            logger.info("Successfully imported {} items", entities.size());

            return entities.stream()
                    .map(entity -> {
                        var dto = parseObject(entity, ItemDTO.class);
                        addHateoasLinks(dto);
                        return dto;
                    }).collect(Collectors.toSet());
        } catch (Exception e) {
            logger.error("Error processing file during mass creation: {}", e.getMessage(), e);
            throw new FileStorageException("Error processing the file!");
        }
    }

    public ItemDTO create(ItemDTO itemDTO) {
        logger.info("Creating new item: {}", itemDTO != null ? itemDTO.getName() : "NULL");
        if (itemDTO == null) {
            logger.error("Attempted to create null itemDTO");
            throw new BadRequestException("ItemDTO cannot be null");
        }

        Item entity = parseObject(itemDTO, Item.class);
        ItemDTO entityDTO = parseObject(itemRepository.save(entity), ItemDTO.class);
        addHateoasLinks(entityDTO);
        logger.info("Item successfully created: {}", entityDTO.getName());
        return entityDTO;
    }

    public ItemDTO update(ItemDTO itemDTO) {
        logger.info("Updating item with ID: {}", itemDTO != null ? itemDTO.getId() : "NULL");
        if (itemDTO == null) {
            logger.error("Attempted to update null itemDTO");
            throw new BadRequestException("ItemDTO cannot be null");
        }

        Item item = itemRepository.findById(itemDTO.getId()).orElseThrow(() -> {
            logger.error("No item found for update with ID: {}", itemDTO.getId());
            return new ResourceNotFoundException("No records found for this ID!");
        });

        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setPrice(itemDTO.getPrice());

        ItemDTO entityDTOUpdated = parseObject(itemRepository.save(item), ItemDTO.class);
        addHateoasLinks(entityDTOUpdated);
        logger.info("Item successfully updated: {}", entityDTOUpdated.getName());

        return entityDTOUpdated;
    }

    public void delete(Long id) {
        logger.info("Deleting item with ID: {}", id);
        Item item = itemRepository.findById(id).orElseThrow(() -> {
            logger.error("No item found for deletion with ID: {}", id);
            return new ResourceNotFoundException("No records found for this ID!");
        });
        itemRepository.delete(item);
        logger.info("Item with ID {} successfully deleted", id);
    }

    // --- Private Methods ---

    private PagedModel<EntityModel<ItemDTO>> buildPagedModel(Page<Item> itemPage) {
        logger.debug("Building PagedModel for {} items", itemPage.getTotalElements());

        // CORRECTION: Map the entities to DTOs and add links to each one
        Page<ItemDTO> itemDTOPage = itemPage.map(item -> {
            ItemDTO itemDTO = parseObject(item, ItemDTO.class);
            addHateoasLinks(itemDTO);
            return itemDTO;
        });

        // CORRECTION: Use the assembler directly on the page.
        // It will automatically generate the correct pagination links (`first`, `self`, `next`, `last`).
        return assembler.toModel(itemDTOPage);
    }

    private void addHateoasLinks(ItemDTO itemDTO) {
        logger.trace("Adding HATEOAS links to item: {}", itemDTO.getId());
        try {
            // Self link is a common pattern, and findById is safe to use with methodOn
            itemDTO.add(linkTo(methodOn(ItemController.class).findById(itemDTO.getId())).withSelfRel());

            // For endpoints with @RequestBody or other complex types, build the link manually
            itemDTO.add(linkTo(ItemController.class).slash("create").withRel("create").withType("POST").withTitle("Create Item"));
            itemDTO.add(linkTo(ItemController.class).slash("updateItem").withRel("updateItem").withType("PUT").withTitle("Update Item"));
            itemDTO.add(linkTo(ItemController.class).slash(itemDTO.getId()).withRel("delete").withType("DELETE").withTitle("Delete Item"));
            itemDTO.add(linkTo(ItemController.class).slash("massCreation").withRel("massCreation").withType("POST").withTitle("Massive Creation"));

            // For GET endpoints, using methodOn with dummy values is generally safe
            itemDTO.add(linkTo(methodOn(ItemController.class).findAll(0, 15, "asc")).withRel("findAll").withType("GET").withTitle("Find All"));
            itemDTO.add(linkTo(methodOn(ItemController.class).exportPage(0, 15, "asc", null)).withRel("exportPage").withType("GET").withTitle("Export Items"));
            itemDTO.add(linkTo(methodOn(ItemController.class).exportItem(itemDTO.getId(), null)).withRel("exportItem").withType("GET").withTitle("Export Item"));

            // For templated links with PathVariables
            itemDTO.add(linkTo(methodOn(ItemController.class).findByName("{name}", 0, 15, "asc")).withRel("findByName").withType("GET").withTitle("Find By Name"));
            itemDTO.add(linkTo(methodOn(ItemController.class).findByDescription("{description}", 0, 15, "asc")).withRel("findByDescription").withType("GET").withTitle("Find By Description"));
            itemDTO.add(linkTo(methodOn(ItemController.class).findByPriceRange("{minPrice}", "{maxPrice}", 0, 15, "asc")).withRel("findByPriceRange").withType("GET").withTitle("Find By Price Range"));

        } catch (Exception e) {
            // Log the warning but don't stop the process
            logger.warn("Failed to add HATEOAS links for item {}: {}", itemDTO.getId(), e.getMessage());
        }
    }
}



//package com.github.gomestkd.eventmanagement.services;
//
//import com.github.gomestkd.eventmanagement.controllers.GuestController;
//import com.github.gomestkd.eventmanagement.controllers.ItemController;
//import com.github.gomestkd.eventmanagement.dto.ItemDTO;
//import com.github.gomestkd.eventmanagement.exception.BadRequestException;
//import com.github.gomestkd.eventmanagement.exception.FileStorageException;
//import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
//import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
//import com.github.gomestkd.eventmanagement.file.exporter.factory.FileExporterFactory;
//import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
//import com.github.gomestkd.eventmanagement.file.importer.factory.FileImporterFactory;
//import com.github.gomestkd.eventmanagement.model.Item;
//import com.github.gomestkd.eventmanagement.repositories.ItemRepository;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.Resource;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.web.PagedResourcesAssembler;
//import org.springframework.hateoas.EntityModel;
//import org.springframework.hateoas.Link;
//import org.springframework.hateoas.PagedModel;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.InputStream;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static com.github.gomestkd.eventmanagement.mapper.ObjectMapper.parseObject;
//import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
//
//@Service
//public class ItemService {
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    @Autowired
//    private PagedResourcesAssembler<ItemDTO> assembler;
//
//    @Autowired
//    private FileImporterFactory fileImporter;
//
//    @Autowired
//    private FileExporterFactory fileExporter;
//
//    public ItemService(
//            ItemRepository itemRepository,
//            PagedResourcesAssembler<ItemDTO> assembler,
//            FileImporterFactory fileImporter,
//            FileExporterFactory fileExporter
//    ) {
//        this.itemRepository = itemRepository;
//        this.assembler = assembler;
//        this.fileImporter = fileImporter;
//        this.fileExporter = fileExporter;
//    }
//
//    public PagedModel<EntityModel<ItemDTO>> findAll(Pageable pageable) {
//        logger.info("Fetching all items - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
//
//        Page<Item> itemPage = itemRepository.findAll(pageable);
//
//        logger.debug("Found {} items", itemPage.getTotalElements());
//        return buildPagedModel(pageable, itemPage);
//    }
//
//    public ItemDTO findById(Long id) {
//        logger.info("Searching for item with ID: {}", id);
//
//        Item item = itemRepository.findById(id).orElseThrow(() -> {
//            logger.error("No item found for ID: {}", id);
//            return new ResourceNotFoundException("No records found for this ID!");
//        });
//
//        ItemDTO itemDTO = parseObject(item, ItemDTO.class);
//        addHateoasLinks(itemDTO);
//
//        logger.info("Item found: {}", itemDTO.getName());
//        return itemDTO;
//    }
//
//    public PagedModel<EntityModel<ItemDTO>> findByName(String name, Pageable pageable) {
//        logger.info("Searching items by name: '{}'", name);
//
//        Page<Item> items = itemRepository.findItemsByName(name, pageable);
//
//        logger.debug("Found {} items for name '{}'", items.getTotalElements(), name);
//
//        return buildPagedModel(pageable, items);
//    }
//
//    public PagedModel<EntityModel<ItemDTO>> findByDescription(String description, Pageable pageable) {
//        logger.info("Searching items by description containing: '{}'", description);
//
//        Page<Item> items = itemRepository.findItemsByDescription(description, pageable);
//
//        logger.debug("Found {} items for description '{}'", items.getTotalElements(), description);
//
//        return buildPagedModel(pageable, items);
//    }
//
//    public PagedModel<EntityModel<ItemDTO>> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
//        logger.info("Searching items by price range: {} - {}", minPrice, maxPrice);
//        Page<Item> items = itemRepository.findItemsByPriceRange(minPrice, maxPrice, pageable);
//        logger.debug("Found {} items in price range {} - {}", items.getTotalElements(), minPrice, maxPrice);
//        return buildPagedModel(pageable, items);
//    }
//
//    public Resource exportPage(Pageable pageable, String acceptHeader) {
//        logger.info("Exporting page of items, format: {}", acceptHeader);
//
//        Set<ItemDTO> items = itemRepository
//                .findAll(pageable)
//                .map(item -> parseObject(item, ItemDTO.class))
//                .toSet();
//
//        try {
//            ItemExporter exporter = this.fileExporter.getItemExporter(acceptHeader);
//            Resource resource = exporter.exportItems(items);
//            logger.info("Export successful - {} items exported", items.size());
//            return resource;
//        } catch (Exception e) {
//            logger.error("Error during file export: {}", e.getMessage(), e);
//            throw new RuntimeException("Error during file export!", e);
//        }
//    }
//
//    public Resource exportItem(Long id, String acceptHeader) {
//        logger.info("Exporting single item with ID: {}, format: {}", id, acceptHeader);
//        Item item = itemRepository.findById(id).orElseThrow(() -> {
//            logger.error("No item found for export with ID: {}", id);
//            return new RuntimeException("No records found for this ID!");
//        });
//        ItemDTO itemDTO = parseObject(item, ItemDTO.class);
//
//        try {
//            ItemExporter exporter = this.fileExporter.getItemExporter(acceptHeader);
//            logger.debug("Exporting item: {}", itemDTO.getName());
//            return exporter.exportItem(itemDTO);
//        } catch (Exception e) {
//            logger.error("Error exporting item with ID {}: {}", id, e.getMessage(), e);
//            throw new RuntimeException("Error during file export!", e);
//        }
//    }
//
//    public Set<ItemDTO> massCreation(MultipartFile file) {
//        logger.info("Mass creation started via file upload: {}", file.getOriginalFilename());
//
//        if (file.isEmpty()) {
//            logger.error("Uploaded file is empty!");
//            throw new RuntimeException("Please set a Valid File!");
//        }
//
//        try (InputStream inputStream = file.getInputStream()) {
//            String filename = Optional.ofNullable(file.getOriginalFilename())
//                    .orElseThrow(() -> new BadRequestException("Please set a Valid File"));
//            ItemImporter importer = this.fileImporter.getItemImporter(filename);
//            Set<Item> entities = importer.importItems(inputStream).stream()
//                    .map(dto -> {
//                        Item saved = itemRepository.save(parseObject(dto, Item.class));
//                        logger.debug("Saved imported item: {}", saved.getName());
//                        return saved;
//                    })
//                    .collect(Collectors.toSet());
//
//            logger.info("Successfully imported {} items", entities.size());
//
//            return entities.stream()
//                    .map(entity -> {
//                        var dto = parseObject(entity, ItemDTO.class);
//                        addHateoasLinks(dto);
//                        return dto;
//                    }).collect(Collectors.toSet());
//        } catch (Exception e) {
//            logger.error("Error processing file during mass creation: {}", e.getMessage(), e);
//            throw new FileStorageException("Error processing the file!");
//        }
//    }
//
//    public ItemDTO create(ItemDTO itemDTO) {
//        logger.info("Creating new item: {}", itemDTO != null ? itemDTO.getName() : "NULL");
//        if (itemDTO == null) {
//            logger.error("Attempted to create null itemDTO");
//            throw new IllegalArgumentException("ItemDTO is null");
//        }
//
//        Item entity = parseObject(itemDTO, Item.class);
//        ItemDTO entityDTO = parseObject(itemRepository.save(entity), ItemDTO.class);
//        addHateoasLinks(entityDTO);
//        logger.info("Item successfully created: {}", entityDTO.getName());
//        return entityDTO;
//    }
//
//    public ItemDTO update(ItemDTO itemDTO) {
//        logger.info("Updating item with ID: {}", itemDTO != null ? itemDTO.getId() : "NULL");
//        if (itemDTO == null) {
//            logger.error("Attempted to update null itemDTO");
//            throw new IllegalArgumentException("ItemDTO is null");
//        }
//
//        Item item = itemRepository.findById(itemDTO.getId()).orElseThrow(() -> {
//            logger.error("No item found for update with ID: {}", itemDTO.getId());
//            return new RuntimeException("No records found for this ID!");
//        });
//
//        item.setName(itemDTO.getName());
//        item.setDescription(itemDTO.getDescription());
//        item.setPrice(itemDTO.getPrice());
//
//        ItemDTO entityDTOUpdated = parseObject(itemRepository.save(item), ItemDTO.class);
//        addHateoasLinks(entityDTOUpdated);
//        logger.info("Item successfully updated: {}", entityDTOUpdated.getName());
//
//        return entityDTOUpdated;
//    }
//
//    public void delete(Long id) {
//        logger.info("Deleting item with ID: {}", id);
//        Item item = itemRepository.findById(id).orElseThrow(() -> {
//            logger.error("No item found for deletion with ID: {}", id);
//            return new RuntimeException("No records found for this ID!");
//        });
//        itemRepository.delete(item);
//        logger.info("Item with ID {} successfully deleted", id);
//    }
//
//    private PagedModel<EntityModel<ItemDTO>> buildPagedModel(Pageable pageable, Page<Item> items) {
//        logger.debug("Building PagedModel for {} items", items.getTotalElements());
//        Page<ItemDTO> itemDTOSWithLink = items.map(item -> {
//            ItemDTO itemDTO = parseObject(item, ItemDTO.class);
//            addHateoasLinks(itemDTO);
//            return itemDTO;
//        });
//
//        Link findAllLink = linkTo(
//                methodOn(ItemController.class)
//                        .findAll(
//                                pageable.getPageNumber(),
//                                pageable.getPageSize(),
//                                String.valueOf(pageable.getSort())
//                        )
//        ).withSelfRel();
//
//        return assembler.toModel(itemDTOSWithLink, findAllLink);
//    }
//
//    private void addHateoasLinks(ItemDTO itemDTO) {
//        logger.trace("Adding HATEOAS links to item: {}", itemDTO.getId());
//
//        try {
//            itemDTO.add(linkTo(methodOn(ItemController.class).findAll(1, 15, "asc")).withRel("findAll").withType("GET").withTitle("Find All"));
//            itemDTO.add(linkTo(methodOn(ItemController.class).findById(itemDTO.getId())).withRel("findById").withType("GET").withTitle("Find By Id"));
//            itemDTO.add(linkTo(methodOn(ItemController.class).findByName("", 1, 15, "asc")).withRel("findByName").withType("GET").withTitle("Find By Name"));
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .exportPage(1, 15, "asc", null)
//                    )
//                            .withRel("exportPage")
//                            .withType("GET")
//                            .withTitle("Export Items")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .exportItem(itemDTO.getId(), null)
//                    )
//                            .withRel("exportItem")
//                            .withType("GET")
//                            .withTitle("Export Item")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .findByDescription(null, 1, 15, "asc")
//                    )
//                            .withRel("findByDescription")
//                            .withType("GET")
//                            .withTitle("Find By Description")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .findByPriceRange(null, null, 1, 15, "asc")
//                    )
//                            .withRel("findByPriceRange")
//                            .withType("GET")
//                            .withTitle("Find By Price Range")
//            );
//            itemDTO.add(
//                    linkTo(methodOn(GuestController.class))
//                            .slash(itemDTO)
//                            .withRel("massCreation")
//                            .withType("POST")
//                            .withTitle("Massive Creation")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .create(itemDTO)
//                    )
//                            .withRel("create")
//                            .withType("POST")
//                            .withTitle("Create Item")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .update(itemDTO)
//                    )
//                            .withRel("updateItem")
//                            .withType("PUT")
//                            .withTitle("Update Item")
//            );
//            itemDTO.add(
//                    linkTo(
//                            methodOn(ItemController.class)
//                                    .delete(itemDTO.getId())
//                    )
//                            .withRel("delete")
//                            .withType("DELETE")
//                            .withTitle("Delete Item")
//            );
//        } catch (Exception e) {
//            logger.warn("Failed to add HATEOAS links for item {}: {}", itemDTO.getId(), e.getMessage());
//        }
//    }
//}
