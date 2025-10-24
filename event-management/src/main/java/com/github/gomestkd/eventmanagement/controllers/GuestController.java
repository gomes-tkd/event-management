package com.github.gomestkd.eventmanagement.controllers;

import com.github.gomestkd.eventmanagement.controllers.docs.GuestControllerDoc;
import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.services.GuestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Tag(name = "Guests", description = "Endpoints for managing guests")
@RestController
@RequestMapping("/api/v1/guests")
public class GuestController implements GuestControllerDoc {

    private final GuestService guestService;

    @Autowired
    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findAll(pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(guests);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public GuestDTO findById(@PathVariable Long id) {
        validateIdParam(id);

        return guestService.findById(id);
    }


    @GetMapping(value = "/findByName/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findByName(
            @PathVariable("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O parâmetro 'name' não pode estar vazio.");
        }

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findByName(name, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(guests);
    }

    // =======================================================
    // 🔹 FIND BY EMAIL
    // =======================================================
    @GetMapping(value = "/findByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByEmail(
            @PathVariable("email") String email,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("O parâmetro 'email' não pode estar vazio.");
        }

        Pageable pageable = buildPageable(page, size, direction, "email");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findGuestByEmail(email, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(guests);
    }

    // =======================================================
    // 🔹 FIND BY PHONE
    // =======================================================
    @GetMapping(value = "/findByPhone/{phone}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByPhone(
            @PathVariable("phone") String phone,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("O parâmetro 'phone' não pode estar vazio.");
        }

        Pageable pageable = buildPageable(page, size, direction, "phone");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findGuestByPhone(phone, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(guests);
    }

    // =======================================================
    // 🔹 EXPORT PAGE (Com validações e suporte a múltiplos tipos)
    // =======================================================
    @GetMapping(
            value = "/export",
            produces = {
                    MediaTypes.APPLICATION_CSV_VALUE,
                    MediaTypes.APPLICATION_PDF_VALUE,
                    MediaTypes.APPLICATION_XLSX_VALUE,
                    MediaTypes.APPLICATION_XML_VALUE
            }
    )
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        validatePaginationParams(page, size, direction);

        final Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf",
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx"
        );

        Pageable pageable = buildPageable(page, size, direction, "name");

        String acceptHeader = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        // trata múltiplos valores no Accept Header
        String selectedMediaType = Arrays.stream(acceptHeader.split(","))
                .map(String::trim)
                .filter(extensionMap::containsKey)
                .findFirst()
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        Resource file = null;
        try {
            file = guestService.exportPage(pageable, selectedMediaType);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting datas: " + e.getMessage(), e);
        }

        if (file == null || !file.exists()) {
            return ResponseEntity.noContent().build();
        }

        String filename = "guests_exported" + extensionMap.get(selectedMediaType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(selectedMediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(file);
    }

    // =======================================================
    // 🔹 EXPORT GUEST (Com validações e suporte a múltiplos tipos)
    // =======================================================
    @GetMapping(
            value = { "/exportGuest/{id}" },
            produces = {
                    MediaTypes.APPLICATION_XLSX_VALUE,
                    MediaTypes.APPLICATION_CSV_VALUE,
                    MediaTypes.APPLICATION_PDF_VALUE
            }
    )
    @Override
    public ResponseEntity<Resource> exportGuest(
            @Parameter(description = "ID of the item", example = "1")
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        validateIdParam(id);

        final Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf",
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx"
        );

        String acceptHeader = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        String selectedMediaType = Arrays.stream(acceptHeader.split(","))
            .map(String::trim)
            .filter(extensionMap::containsKey)
            .findFirst()
            .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        Resource file = null;

        try {
            file = guestService.exportGuest(id, selectedMediaType);
        } catch (Exception e) {
            throw new RuntimeException("Error exporting guest: " + e.getMessage(), e);

        }

        if (file == null || !file.exists()) {
            return ResponseEntity.noContent().build();
        }

        String filename = "guest_" + id + extensionMap.get(selectedMediaType);

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(acceptHeader))
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\""
            ).body(file);
    }

    // =======================================================
    // 🔹 MASS CREATION (Importa uma lista de Guests a partir de um arquivo)
    // =======================================================
    @PostMapping(
            value = "/massCreation",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public Set<GuestDTO> massCreation(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo de entrada não pode estar vazio.");
        }

        return guestService.massCreation(file);
    }

    // =======================================================
    // 🔹 CREATE A GUEST (Com validação do corpo da requisição)
    // =======================================================

    @PostMapping(
            value = "/create",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public GuestDTO create(@RequestBody GuestDTO guestDTO) {
        validateGuestDTO(guestDTO);

        return guestService.create(guestDTO);
    }

    // =======================================================
    // 🔹 UPDATING A GUEST (Com validação do corpo da requisição)
    // =======================================================
    @PutMapping(
            value = "/updateGuest",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public GuestDTO update(@RequestBody GuestDTO guestDTO) {
        validateIdParam(guestDTO.getId());
        validateGuestDTO(guestDTO);

        return guestService.update(guestDTO);
    }

    // =======================================================
    // 🔹 UPDATING A GUEST (Com validação do corpo da requisição)
    // =======================================================
    @DeleteMapping(value = "/delete/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        validateIdParam(id);

        guestService.delete(id);

        return ResponseEntity.ok().build();
    }


    private void validateGuestDTO(GuestDTO guestDTO) {
        if (guestDTO == null) {
            throw new IllegalArgumentException("O corpo da requisição não pode ser nulo.");
        }
        if (guestDTO.getName() == null || guestDTO.getName().isBlank()) {
            throw new IllegalArgumentException("O nome do convidado é obrigatório.");
        }
        if (guestDTO.getEmail() == null || guestDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("O email do convidado é obrigatório.");
        }
        if (guestDTO.getPhone() == null || guestDTO.getPhone().isBlank()) {
            throw new IllegalArgumentException("O telefone do convidado é obrigatório.");
        }
    }

    private void validateIdParam(Long id) {
        if (id == null || id <= 0) {
            throw new ResourceNotFoundException("Guest with id " + id + " not found");
        }
    }

    private void validatePaginationParams(Integer page, Integer size, String direction) {
        if (page == null || page < 0) {
            throw new IllegalArgumentException("O parâmetro 'page' deve ser >= 0.");
        }
        if (size == null || size <= 0 || size > 500) {
            throw new IllegalArgumentException("O parâmetro 'size' deve estar entre 1 e 500.");
        }
        if (direction == null ||
                (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))) {
            throw new IllegalArgumentException("O parâmetro 'direction' deve ser 'asc' ou 'desc'.");
        }
    }

    private Pageable buildPageable(Integer page, Integer size, String direction, String sortBy) {
        Sort.Direction sort = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(sort, sortBy));
    }
}
