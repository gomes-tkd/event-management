package com.github.gomestkd.eventmanagement.file.exporter.implementation.guest;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.GuestExporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class GuestCsvExporter implements GuestExporter {

    @Override
    public Resource exportGuests(Set<GuestDTO> guests) throws IOException, Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat
                .Builder
                .create()
                .setHeader("ID", "Name", "Email", "Phone")
                .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (GuestDTO guest : guests) {
                csvPrinter.printRecord(
                        guest.getId(),
                        guest.getName(),
                        guest.getEmail(),
                        guest.getPhone()
                );
            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportGuest(GuestDTO guest) throws IOException, Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat
            .Builder
            .create()
            .setHeader("ID", "Name", "Email", "Phone")
            .setSkipHeaderRecord(false)
            .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            csvPrinter.printRecord(
                guest.getId(),
                guest.getName(),
                guest.getEmail(),
                guest.getPhone()
            );
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}
