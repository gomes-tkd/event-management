package com.github.gomestkd.eventmanagement.file.exporter.implementation.item;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

@Component
public class ItemXlsxExporter implements ItemExporter {

    private static final String[] HEADERS = {"ID", "Name", "Description", "Price"};

    @Override
    public Resource exportItems(Set<ItemDTO> items) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheetWithHeader(workbook);

            int rowIndex = 1;
            for (ItemDTO item : items) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getName());
                row.createCell(2).setCellValue(item.getDescription());
                row.createCell(3).setCellValue(item.getPrice());
            }

            autoSizeColumns(sheet, HEADERS.length);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    @Override
    public Resource exportItem(ItemDTO item) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheetWithHeader(workbook);

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getName());
            row.createCell(2).setCellValue(item.getDescription());
            row.createCell(3).setCellValue(item.getPrice());

            autoSizeColumns(sheet, HEADERS.length);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    private Sheet createSheetWithHeader(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Items");
        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        return sheet;
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }
}
