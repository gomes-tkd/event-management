package com.github.gomestkd.eventmanagement.controllers;

import com.github.gomestkd.eventmanagement.controllers.docs.GuestControllerDoc;
import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.services.GuestService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(GuestController.class);

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
        log.info("📥 Request received: GET /guests?page={}&size={}&direction={}", page, size, direction);
        validatePaginationParams(page, size, direction);

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findAll(pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            log.warn("No guests found on page {}", page);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} guests.", guests.getContent().size());
        return ResponseEntity.ok(guests);
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public GuestDTO findById(@PathVariable("id") Long id) {
        log.info("📥 Request received: GET /guests/{}", id);
        validateIdParam(id);

        GuestDTO guest = guestService.findById(id);
        log.debug("🎯 Guest found: {}", guest);
        return guest;
    }


    @GetMapping(value = "/findByName/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findByName(
            @PathVariable("name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /guests/findByName/{}?page={}&size={}&direction={}", name, page, size, direction);
        validatePaginationParams(page, size, direction);

        if (name == null || name.isBlank()) {
            log.warn("Invalid parameter 'name': '{}'", name);
            throw new IllegalArgumentException("The 'name' parameter cannot be empty.");
        }

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findByName(name, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            log.warn("No guests found with name '{}'.", name);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} guests with name '{}'.", guests.getContent().size(), name);
        return ResponseEntity.ok(guests);
    }

    @GetMapping(value = "/findByEmail/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByEmail(
            @PathVariable("email") String email,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /guests/findByEmail/{}", email);
        validatePaginationParams(page, size, direction);

        if (email == null || email.isBlank()) {
            log.warn("Invalid parameter 'email': '{}'", email);
            throw new IllegalArgumentException("The 'email' parameter cannot be empty.");
        }

        Pageable pageable = buildPageable(page, size, direction, "email");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findGuestByEmail(email, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            log.warn("No guests found with email '{}'.", email);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} guests with email '{}'.", guests.getContent().size(), email);
        return ResponseEntity.ok(guests);
    }

    @GetMapping(value = "/findByPhone/{phone}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<GuestDTO>>> findGuestByPhone(
            @PathVariable("phone") String phone,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /guests/findByPhone/{}", phone);
        validatePaginationParams(page, size, direction);

        if (phone == null || phone.isBlank()) {
            log.warn("Invalid parameter 'phone': '{}'", phone);
            throw new IllegalArgumentException("The 'phone' parameter cannot be empty.");
        }

        Pageable pageable = buildPageable(page, size, direction, "phone");
        PagedModel<EntityModel<GuestDTO>> guests = guestService.findGuestByPhone(phone, pageable);

        if (guests == null || guests.getContent().isEmpty()) {
            log.warn("No guests found with phone '{}'.", phone);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} guests with phone '{}'.", guests.getContent().size(), phone);
        return ResponseEntity.ok(guests);
    }

    @PostMapping(value = "/massCreation", produces = { MediaType.APPLICATION_JSON_VALUE })
    @Override
    public Set<GuestDTO> massCreation(@RequestParam("file") MultipartFile file) {
        log.info("📤 Request received: POST /guests/massCreation");
        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is empty or null.");
            throw new IllegalArgumentException("The input file cannot be empty.");
        }

        Set<GuestDTO> createdGuests = guestService.massCreation(file);
        log.info("✅ Successfully imported {} guests from file '{}'.", createdGuests.size(), file.getOriginalFilename());
        return createdGuests;
    }

    @GetMapping(value = "/export", produces = {
            MediaTypes.APPLICATION_CSV_VALUE,
            MediaTypes.APPLICATION_PDF_VALUE,
            MediaTypes.APPLICATION_XLSX_VALUE,
    })
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("📤 Request received: GET /guests/export?page={}&size={}&direction={}", page, size, direction);
        validatePaginationParams(page, size, direction);

        final Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf",
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx"
        );

        Pageable pageable = buildPageable(page, size, direction, "name");

        String acceptHeader = Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        String selectedMediaType = Arrays.stream(acceptHeader.split(","))
                .map(String::trim)
                .filter(extensionMap::containsKey)
                .findFirst()
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        try {
            Resource file = guestService.exportPage(pageable, selectedMediaType);

            if (file == null || !file.exists()) {
                log.warn("No export file generated for type '{}'.", selectedMediaType);
                return ResponseEntity.noContent().build();
            }

            String filename = "guests_exported" + extensionMap.get(selectedMediaType);
            log.info("✅ Export generated successfully as '{}'.", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(selectedMediaType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);

        } catch (Exception e) {
            log.error("❌ Error exporting page: {}", e.getMessage(), e);
            throw new RuntimeException("Error exporting data: " + e.getMessage(), e);
        }
    }

    @GetMapping(value = "/exportGuest/{id}", produces = {
            MediaTypes.APPLICATION_XLSX_VALUE,
            MediaTypes.APPLICATION_CSV_VALUE,
            MediaTypes.APPLICATION_PDF_VALUE
    })
    @Override
    public ResponseEntity<Resource> exportGuest(
            @Parameter(description = "Guest ID", example = "1") @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        log.info("📤 Request received: GET /guests/exportGuest/{}", id);
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

        try {
            Resource file = guestService.exportGuest(id, selectedMediaType);
            if (file == null || !file.exists()) {
                log.warn("No export file generated for guest id={} with type '{}'.", id, selectedMediaType);
                return ResponseEntity.noContent().build();
            }

            String filename = "guest_" + id + extensionMap.get(selectedMediaType);
            log.info("✅ Guest export generated successfully: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(selectedMediaType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);

        } catch (Exception e) {
            log.error("❌ Error exporting guest id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error exporting guest: " + e.getMessage(), e);
        }
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public GuestDTO create(@RequestBody GuestDTO guestDTO) {
        log.info("📤 Request received: POST /guests/create");
        log.debug("Payload: {}", guestDTO);
        validateGuestDTO(guestDTO);
        GuestDTO created = guestService.create(guestDTO);
        log.info("✅ Guest created successfully: id={}", created.getId());
        return created;
    }

    @PutMapping(value = "/updateGuest", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public GuestDTO update(@RequestBody GuestDTO guestDTO) {
        log.info("📤 Request received: PUT /guests/updateGuest");
        log.debug("Payload: {}", guestDTO);
        validateIdParam(guestDTO.getId());
        validateGuestDTO(guestDTO);
        GuestDTO updated = guestService.update(guestDTO);
        log.info("✅ Guest updated successfully: id={}", updated.getId());
        return updated;
    }

    @DeleteMapping(value = "/delete/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        log.info("🗑️ Request received: DELETE /guests/delete/{}", id);
        validateIdParam(id);
        guestService.delete(id);
        log.info("✅ Guest deleted successfully: id={}", id);
        return ResponseEntity.ok().build();
    }

    private void validateGuestDTO(GuestDTO guestDTO) {
        if (guestDTO == null) {
            log.warn("GuestDTO is null.");
            throw new IllegalArgumentException("Request body cannot be null.");
        }
        if (guestDTO.getName() == null || guestDTO.getName().isBlank()) {
            log.warn("Guest name is missing.");
            throw new IllegalArgumentException("Guest name is required.");
        }
        if (guestDTO.getEmail() == null || guestDTO.getEmail().isBlank()) {
            log.warn("Guest email is missing.");
            throw new IllegalArgumentException("Guest email is required.");
        }
        if (guestDTO.getPhone() == null || guestDTO.getPhone().isBlank()) {
            log.warn("Guest phone is missing.");
            throw new IllegalArgumentException("Guest phone is required.");
        }
    }

    private void validateIdParam(Long id) {
        if (id == null || id <= 0) {
            log.error("Invalid ID received: {}", id);
            throw new ResourceNotFoundException("Guest with id " + id + " not found");
        }
    }

    private void validatePaginationParams(Integer page, Integer size, String direction) {
        if (page == null || page < 0) {
            log.warn("Invalid 'page' param: {}", page);
            throw new IllegalArgumentException("The 'page' parameter must be >= 0.");
        }
        if (size == null || size <= 0 || size > 500) {
            log.warn("Invalid 'size' param: {}", size);
            throw new IllegalArgumentException("The 'size' parameter must be between 1 and 500.");
        }
        if (direction == null ||
                (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))) {
            log.warn("Invalid 'direction' param: {}", direction);
            throw new IllegalArgumentException("The 'direction' parameter must be 'asc' or 'desc'.");
        }
    }

    private Pageable buildPageable(Integer page, Integer size, String direction, String sortBy) {
        Sort.Direction sort = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        log.debug("Building pageable: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        return PageRequest.of(page, size, Sort.by(sort, sortBy));
    }
}
