package com.github.gomestkd.eventmanagement.file.importer.implementation.event;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.EventImporter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class EventXlsxImporter implements EventImporter {
    @Override
    public Set<EventDTO> importEvents(InputStream inputStream) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            return parseRowsToEventDTOs(rowIterator);
        }
    }

    private Set<EventDTO> parseRowsToEventDTOs(Iterator<Row> rows) {
        Set<EventDTO> events = new HashSet<>();

        while(rows.hasNext()) {
            Row row =  rows.next();
            if(isRowValid(row)) {
                events.add(parseRowTOEventDTO(row));
            }
        }

        return events;
    }

    private EventDTO parseRowTOEventDTO(Row row) {
        EventDTO event = new EventDTO();
        event.setName(row.getCell(0).getStringCellValue());
        event.setDescription(row.getCell(1).getStringCellValue());
        event.setStartTime(new Date(Long.parseLong(row.getCell(2).getStringCellValue())));
        event.setEndTime(new Date(Long.parseLong(row.getCell(3).getStringCellValue())));
        event.setLocation(row.getCell(4).getStringCellValue());
        event.setCreatedAt(new Date(Long.parseLong(row.getCell(5).getStringCellValue())));
        event.setUpdatedAt(new Date(Long.parseLong(row.getCell(6).getStringCellValue())));
        return event;
    }

    private static boolean isRowValid(Row row) {
        return (row.getCell(0) != null) && (row.getCell(0).getCellType() != CellType.BLANK);
    }
}
