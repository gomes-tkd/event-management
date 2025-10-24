package com.github.gomestkd.eventmanagement.file.importer.implementation.item;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ItemCsvImporter implements ItemImporter {
    @Override
    public Set<ItemDTO> importItems(InputStream inputStream) throws Exception {
        CSVFormat csvFormat = CSVFormat
                .Builder
                .create()
                    .setSkipHeaderRecord(true)
                    .setIgnoreEmptyLines(true)
                    .setTrim(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(new InputStreamReader(inputStream));
        return parseRecordsToItemDTOs(records);
    }

    private Set<ItemDTO> parseRecordsToItemDTOs(Iterable<CSVRecord> records) {
        Set<ItemDTO> items = new HashSet<>();

        for (CSVRecord record : records) {
            ItemDTO item = new ItemDTO();
            item.setName(record.get("Name"));
            item.setDescription(record.get("Description"));
            item.setPrice(Double.parseDouble(record.get("Price")));
            items.add(item);
        }

        return items;
    }
}
