package com.github.gomestkd.eventmanagement.file.importer.implementation.guest;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.GuestImporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class GuestCsvImporter implements GuestImporter {
    @Override
    public Set<GuestDTO> importGuests(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat
                .Builder
                .create()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));
        return parseRecordsToGuestDTOs(records);
    }

    private Set<GuestDTO> parseRecordsToGuestDTOs(Iterable<CSVRecord> records) {
        Set<GuestDTO> guest = new HashSet<>();

        for (CSVRecord record : records) {
            GuestDTO dto = new GuestDTO();
            dto.setName(record.get("Name"));
            dto.setEmail(record.get("Email"));
            dto.setPhone(record.get("Phone"));
            guest.add(dto);
        }

        return guest;
    }
}
