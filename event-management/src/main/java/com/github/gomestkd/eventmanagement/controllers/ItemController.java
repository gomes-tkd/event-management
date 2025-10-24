package com.github.gomestkd.eventmanagement.controllers;

import com.github.gomestkd.eventmanagement.controllers.docs.ItemControllerDoc;
import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.services.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import java.util.Map;
import java.util.Set;

@Tag(name = "Items", description = "Endpoints for managing items")
@RestController
@RequestMapping("/api/v1/items")
public class ItemController implements ItemControllerDoc {

    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping(produces = { MediaType.APPLICATION_JSON_VALUE })
    @Override
    public ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        return ResponseEntity.ok(itemService.findAll(pageable));
    }


    @GetMapping(
            value = { "/findById/{id}" },
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ItemDTO findById(@PathVariable("id") Long id) {
        return itemService.findById(id);
    }

    @GetMapping(
            value = { "/findByName/{name}" },
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByName(
            @PathVariable("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        return ResponseEntity.ok(itemService.findByName(name, pageable));
    }

    @GetMapping(
            value = { "/findByDescription/{description}" },
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByDescription(
            @PathVariable("description") String description,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        return ResponseEntity.ok(itemService.findByDescription(description, pageable));
    }

    @GetMapping(
            value = {"/findByPriceRange/{minPrice}/{maxPrice}" },
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<ItemDTO>>> findByPriceRange(
            @PathVariable("minPrice") String minPrice,
            @PathVariable("maxPrice") String maxPrice,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));

        try {
            Double min = Double.parseDouble(minPrice);
            Double max = Double.parseDouble(maxPrice);
            return ResponseEntity.ok(itemService.findByPriceRange(min, max, pageable));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

    }

    @GetMapping(
            value = { "/export" },
            produces = {
                    MediaTypes.APPLICATION_XLSX_VALUE,
                    MediaTypes.APPLICATION_CSV_VALUE,
                    MediaTypes.APPLICATION_PDF_VALUE
            }
    )
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "0") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf"
        );
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "name"));
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        Resource file = itemService.exportPage(pageable, acceptHeader);

        String fileExtension = extensionMap.getOrDefault(acceptHeader, "");
        String contentType = (acceptHeader != null) ? acceptHeader : "application/octet-stream";

        String filename = "items_exported" + fileExtension;

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .body(file);
    }

    @GetMapping(
            value = "/export/{id}",
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ResponseEntity<Resource> exportItem(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
        Resource file = itemService.exportItem(id, acceptHeader);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(acceptHeader))
                .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"item_" + id + ".pdf\""
                )
                .body(file);
    }

    @PostMapping(
            value = "/massCreation",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public Set<ItemDTO> massCreation(@RequestParam("file") MultipartFile file) {
        return itemService.massCreation(file);
    }

    @PostMapping(
            value = "/create",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ItemDTO create(@RequestBody ItemDTO itemDTO) {
        return itemService.create(itemDTO);
    }

    @PutMapping(
            value = "/updateItem",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public ItemDTO update(@RequestBody ItemDTO itemDTO) {
        return itemService.update(itemDTO);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
