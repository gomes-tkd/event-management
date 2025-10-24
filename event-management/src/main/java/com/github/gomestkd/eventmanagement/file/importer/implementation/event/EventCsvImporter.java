package com.github.gomestkd.eventmanagement.file.importer.implementation.event;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.EventImporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class EventCsvImporter implements EventImporter {
    @Override
    public Set<EventDTO> importEvents(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat
                .Builder.create()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));
        return parseRecordsToEventDTOs(records);
    }

    private Set<EventDTO> parseRecordsToEventDTOs(Iterable<CSVRecord> records) {
        Set<EventDTO> events = new HashSet<>();

        for (CSVRecord record : records) {
            EventDTO event = new EventDTO();
            event.setName(record.get("name"));
            event.setDescription(record.get("description"));
            event.setStartTime(new Date(Long.parseLong(record.get("start_time"))));
            event.setEndTime(new Date(Long.parseLong(record.get("end_time"))));
            event.setLocation(record.get("location"));
            event.setCreatedAt(new Date(Long.parseLong(record.get("created_at"))));
            event.setUpdatedAt(new Date(Long.parseLong(record.get("updated_at"))));
            events.add(event);
        }

        return events;
    }
}
