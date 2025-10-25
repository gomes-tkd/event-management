package com.github.gomestkd.eventmanagement.controllers;

import com.github.gomestkd.eventmanagement.controllers.docs.EventControllerDocs;
import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.exception.ResourceNotFoundException;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.services.EventService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Tag(name = "Events", description = "Endpoints for managing events")
@RestController
@RequestMapping("/api/v1/events")
public class EventController implements EventControllerDocs {
    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService =  eventService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public ResponseEntity<PagedModel<EntityModel<EventDTO>>> findAll(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /guests?page={}&size={}&direction={}", page, size, direction);
        validatePaginationParams(page, size, direction);

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<EventDTO>> events = eventService.findAll(pageable);

        if (events == null || events.getContent().isEmpty()) {
            log.warn("No events found on page {}", page);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} guests.", events.getContent().size());
        return ResponseEntity.ok(events);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public EventDTO findById(@PathVariable("id") Long id) {
        log.info("📥 Request received: GET /guests/{}", id);
        validateIdParam(id);

        EventDTO eventDTO = eventService.findById(id);
        log.debug("🎯 Guest found: {}", eventDTO);
        return eventDTO;
    }

    @GetMapping(
            value = "/findEventByName/{name}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByName(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /events/findByName/{}?page={}&size={}&direction={}", name, page, size, direction);
        validatePaginationParams(page, size, direction);

        verifyPathVariable(name);

        Pageable pageable = buildPageable(page, size, direction, "name");
        PagedModel<EntityModel<EventDTO>> events = eventService.findEventByName(name, pageable);

        if (events == null || events.getContent().isEmpty()) {
            log.warn("No events found with name '{}'.", name);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} events with name '{}'.", events.getContent().size(), name);
        return ResponseEntity.ok(events);
    }

    @GetMapping(
            value = "/findEventByDescription/{description}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByDescription(
            @PathVariable("description") String description,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "0") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);

        verifyPathVariable(description);

        Pageable pageable = buildPageable(page, size, direction, "description");
        PagedModel<EntityModel<EventDTO>> events =  eventService.findEventsByDescription(description, pageable);

        if (events == null || events.getContent().isEmpty()) {
            log.warn("No events found with description '{}'.", description);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} events with description '{}'.", events.getContent().size(), description);
        return ResponseEntity.ok(events);
    }

    @GetMapping(
            value = "/findEventByLocation/{location}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByLocation(
            @PathVariable("location") String location,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "0") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        validatePaginationParams(page, size, direction);

        verifyPathVariable(location);

        Pageable pageable = buildPageable(page, size, direction, "location");
        PagedModel<EntityModel<EventDTO>> events =  eventService.findEventsByDescription(location, pageable);

        if (events == null || events.getContent().isEmpty()) {
            log.warn("No events found with location '{}'.", location);
            return ResponseEntity.noContent().build();
        }

        log.info("✅ Returning {} events with location '{}'.", events.getContent().size(), location);
        return ResponseEntity.ok(events);
    }

    @GetMapping(
            value = "/findEventByTime/{start-time}/{end-time}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<EventDTO>>> findEventsByTime(
            @PathVariable("start-time") Date startTime,
            @PathVariable("end-time") Date endTime,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        log.info("📥 Request received: GET /events/findByTime?start-time={}&end-time={}&page={}&size={}&direction={}",
                startTime, endTime, page, size, direction);

        log.debug("🧩 Validating pagination params and date range...");
        validateDateRange(startTime, endTime);
        validatePaginationParams(page, size, direction);

        Pageable pageable = buildPageable(page, size, direction, "startTime");

        PagedModel<EntityModel<EventDTO>> events = eventService.findEventsByTime(startTime, endTime, pageable);

        if (events == null || events.getContent().isEmpty()) {
            log.warn("No events found between {} and {}", startTime, endTime);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(events);
    }

    @GetMapping(
            value = "/export",
            produces = {
                MediaTypes.APPLICATION_CSV_VALUE,
                MediaTypes.APPLICATION_PDF_VALUE,
                MediaTypes.APPLICATION_XLSX_VALUE,
            }
    )
    @Override
    public ResponseEntity<Resource> exportPage(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "15") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("📤 Request received: GET /event/export?page={}&size={}&direction={}", page, size, direction);
        validatePaginationParams(page, size, direction);

        final Map<String, String> extensionMap = Map.of(
                MediaTypes.APPLICATION_CSV_VALUE, ".csv",
                MediaTypes.APPLICATION_PDF_VALUE, ".pdf",
                MediaTypes.APPLICATION_XLSX_VALUE, ".xlsx"
        );

        Pageable pageable = buildPageable(page, size, direction, "name");

        String acceptHeader = Optional
                .ofNullable(request.getHeader(HttpHeaders.ACCEPT))
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        String selectedMediaType = Arrays.stream(acceptHeader.split(","))
                .map(String::trim)
                .filter(extensionMap::containsKey)
                .findFirst()
                .orElse(MediaTypes.APPLICATION_CSV_VALUE);

        try {
            Resource file = eventService.exportPage(pageable, selectedMediaType);

            if (file == null || !file.exists()) {
                log.warn("No export file generated for type '{}'.", selectedMediaType);
                return ResponseEntity.noContent().build();
            }

            String filename = "event_exported" + extensionMap.get(selectedMediaType);
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

    @GetMapping(
            value = "/exportGuest/{id}",
            produces = {
                    MediaTypes.APPLICATION_CSV_VALUE,
                    MediaTypes.APPLICATION_PDF_VALUE,
                    MediaTypes.APPLICATION_XLSX_VALUE,
            }
    )
    @Override
    public ResponseEntity<Resource> exportEvent(
            @PathVariable("id") Long id,
            HttpServletRequest request
    ) {
        log.info("📤 Request received: GET /event/exportGuest/{}", id);
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
            Resource file = eventService.exportEvent(id, selectedMediaType);
            if (file == null || !file.exists()) {
                log.warn("No export file generated for event id={} with type '{}'.", id, selectedMediaType);
                return ResponseEntity.noContent().build();
            }

            String filename = "event_" + id + extensionMap.get(selectedMediaType);
            log.info("✅ Event export generated successfully: {}", filename);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(selectedMediaType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(file);

        } catch (Exception e) {
            log.error("❌ Error exporting event id={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error exporting event: " + e.getMessage(), e);
        }
    }

    @PostMapping(
            value = "/massCreation",
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public Set<EventDTO> massCreation(@RequestParam("file") MultipartFile file) {
        log.info("📤 Request received: POST /event/massCreation");
        if (file == null || file.isEmpty()) {
            log.warn("Uploaded file is empty or null.");
            throw new IllegalArgumentException("The input file cannot be empty.");
        }

        Set<EventDTO> createdEvents = eventService.massCreation(file);
        log.info("✅ Successfully imported {} guests from file '{}'.", createdEvents.size(), file.getOriginalFilename());
        return createdEvents;
    }

    @PostMapping(
            value = "/create",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public EventDTO create(@RequestBody EventDTO eventDTO) {
        log.info("📤 Request received: POST /event/create");
        log.debug("Payload: {}", eventDTO);
        validateEventDTO(eventDTO);

        EventDTO created =  eventService.create(eventDTO);
        log.info("✅ Event created successfully: id={}", created.getId());

        return created;
    }

    @PutMapping(
            value = "/updateGuest",
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE }
    )
    @Override
    public EventDTO update(EventDTO eventDTO) {
        log.info("📤 Request received: PUT /event/updateGuest");
        log.debug("Payload: {}", eventDTO);
        validateIdParam(eventDTO.getId());
        validateEventDTO(eventDTO);

        EventDTO updated = eventService.update(eventDTO);
        log.info("✅ Guest updated successfully: id={}", updated.getId());
        return updated;
    }

    @DeleteMapping(value = "/delete/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        log.info("🗑️ Request received: DELETE /event/delete/{}", id);
        validateIdParam(id);
        eventService.delete(id);
        log.info("✅ Event deleted successfully: id={}", id);
        return ResponseEntity.ok().build();    }

    private void validateDateRange(Date startTime, Date endTime) {
        if (startTime == null || endTime == null) {
            log.warn("Null date(s) provided. startTime={}, endTime={}", startTime, endTime);
            throw new IllegalArgumentException("Both startTime and endTime must be provided.");
        }

        if (endTime.before(startTime)) {
            log.warn("Invalid date range: endTime {} is before startTime {}", endTime, startTime);
            throw new IllegalArgumentException("endTime cannot be before startTime.");
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

        if (
            direction == null || (
                    !direction.equalsIgnoreCase("asc")
                            && !direction.equalsIgnoreCase("desc")
            )
        ) {
            log.warn("Invalid 'direction' param: {}", direction);
            throw new IllegalArgumentException("The 'direction' parameter must be 'asc' or 'desc'.");
        }
    }

    private Pageable buildPageable(
        Integer page, Integer size, String direction, String sortBy
    ) {
        Sort.Direction sort = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        log.debug("Building pageable: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);

        return PageRequest.of(page, size, Sort.by(sort, sortBy));
    }

    private PagedModel<EntityModel<EventDTO>> toPagedModel(Page<EventDTO> page) {
        List<EntityModel<EventDTO>> events = page
                .getContent()
                .stream()
                .map(EntityModel::of)
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages()
        );

        return PagedModel.of(events, metadata);
    }

    private void validateIdParam(Long id) {
        if (id == null || id <= 0) {
            log.error("Invalid ID received: {}", id);
            throw new ResourceNotFoundException("Guest with id " + id + " not found");
        }
    }

    private static void verifyPathVariable(String variable) {
        if  (variable == null || variable.isBlank()) {
            log.warn("Invalid parameter '" + variable + "': '{}'", variable);
            throw new IllegalArgumentException("The 'name' parameter cannot be empty.");
        }
    }

    private void validateEventDTO(EventDTO eventDTO) {
        if (eventDTO == null) {
            log.warn("EventDTO is null.");
            throw new IllegalArgumentException("Request body cannot be null.");
        }
        if (eventDTO.getName() == null || eventDTO.getName().isBlank()) {
            log.warn("Event name is missing.");
            throw new IllegalArgumentException("Event name is required.");
        }

        if (eventDTO.getLocation() == null || eventDTO.getLocation().isBlank()) {
            log.warn("Event location is missing.");
            throw new IllegalArgumentException("Event location is required.");
        }

        if (eventDTO.getDescription() == null || eventDTO.getDescription().isBlank()) {
            log.warn("Event description is missing.");
            throw new IllegalArgumentException("Event description is required.");
        }

        if (eventDTO.getStartTime() == null) {
            log.warn("Event start time is missing.");
            throw new IllegalArgumentException("Event start time is required.");
        }

        if (eventDTO.getEndTime() == null) {
            log.warn("Event end time is missing.");
            throw new IllegalArgumentException("Event end time is required.");
        }

        if (eventDTO.getEndTime().before(eventDTO.getStartTime())) {
            log.warn("⚠️ Invalid event time range: endTime ({}) is before startTime ({}).",
                    eventDTO.getEndTime(), eventDTO.getStartTime());
            throw new IllegalArgumentException("The event end time cannot be before the start time.");
        }
    }

    private Date parseDate(String dateStr, String paramName) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            log.error("Invalid date format for {}: {}", paramName, dateStr);
            throw new IllegalArgumentException("Invalid date format for " + paramName +
                    ". Expected format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

}
