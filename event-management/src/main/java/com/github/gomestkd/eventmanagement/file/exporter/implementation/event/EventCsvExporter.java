package com.github.gomestkd.eventmanagement.file.exporter.implementation.event;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.EventExporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class EventCsvExporter implements EventExporter {
    private static final CSVFormat CSV_FORMAT = CSVFormat
            .Builder
            .create()
            .setHeader(
                    "id", "name", "description", "start_time",
                    "end_time", "location", "created_at", "updated_at"
            )
            .setSkipHeaderRecord(false)
            .build();

    @Override
    public Resource exportEvents(Set<EventDTO> events) throws IOException, Exception {
        return exportToCsv(events);
    }

    @Override
    public Resource exportEvent(EventDTO event) throws IOException, Exception {
        return exportToCsv(Set.of(event));
    }

    private Resource exportToCsv(Collection<EventDTO> events) throws IOException, Exception {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                CSVPrinter csvPrinter = new CSVPrinter(writer, CSV_FORMAT);

        ) {
            for (EventDTO event : events) {
                csvPrinter.printRecord(
                        event.getId(),
                        event.getName(),
                        event.getDescription(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getLocation(),
                        event.getCreatedAt(),
                        event.getUpdatedAt()
                );

            }

            csvPrinter.flush();
            return new  ByteArrayResource(outputStream.toByteArray());
        }
    }
}
