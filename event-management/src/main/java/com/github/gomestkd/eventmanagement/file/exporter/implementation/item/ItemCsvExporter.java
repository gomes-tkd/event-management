package com.github.gomestkd.eventmanagement.file.exporter.implementation.item;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
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
public class ItemCsvExporter implements ItemExporter {
    @Override
    public Resource exportItems(Set<ItemDTO> items) throws IOException, Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat
                .Builder
                .create()
                    .setHeader("ID", "Name", "Description", "Price")
                    .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (ItemDTO item : items) {
                csvPrinter.printRecord(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getPrice()
                );
            }
        }
        return new ByteArrayResource(outputStream.toByteArray());
    }

    @Override
    public Resource exportItem(ItemDTO item) throws IOException, Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat csvFormat = CSVFormat
                .Builder
                .create()
                    .setHeader("ID", "Name", "Description", "Price")
                    .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            csvPrinter.printRecord(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getPrice()
            );
        }

        return new ByteArrayResource(outputStream.toByteArray());
    }
}
