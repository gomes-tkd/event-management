package com.github.gomestkd.eventmanagement.file.exporter.implementation.event;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.EventExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class EventXlsxExporter implements EventExporter {
    private static final String[] HEADERS = {
            "id", "name", "description", "start_time",
            "end_time", "location", "created_at", "updated_at"
    };

    @Override
    public Resource exportEvents(Set<EventDTO> events) throws IOException, Exception {
        return exportToXlsx(events);
    }

    @Override
    public Resource exportEvent(EventDTO event) throws IOException, Exception {
        return exportEvents(Set.of(event));
    }

    private Resource exportToXlsx(Collection<EventDTO> events) throws IOException, Exception {
        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ) {
            Sheet sheet = workbook.createSheet("Events");
            CellStyle headerStyle = createHeaderCellStyle(workbook);
            createHeaderRow(sheet, headerStyle);
            fillEventRows(sheet, events);
            autoSizeColumns(sheet, HEADERS.length);

            workbook.write(outputStream);
            workbook.close();

            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillEventRows(Sheet sheet, Collection<EventDTO> events) {
        int rowIndex = 1;
        for (EventDTO event : events) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(event.getId());
            row.createCell(1).setCellValue(event.getName());
            row.createCell(2).setCellValue(event.getDescription());
            row.createCell(3).setCellValue(event.getStartTime());
            row.createCell(4).setCellValue(event.getEndTime());
            row.createCell(5).setCellValue(event.getLocation());
            row.createCell(6).setCellValue(event.getCreatedAt());
            row.createCell(7).setCellValue(event.getUpdatedAt());
        }
    }

    private CellStyle createHeaderCellStyle(Workbook workbook) {
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerCellStyle;
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
