package com.github.gomestkd.eventmanagement.file.exporter.implementation.guest;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.GuestExporter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public class GuestXlsxExporter  implements GuestExporter {
    private static final String[] HEADERS = {"ID", "Name", "Email", "Phone"};


    @Override
    public Resource exportGuests(Set<GuestDTO> guests) throws IOException, Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheetWithHeader(workbook);

            int rowIndex = 1;

            for (GuestDTO guestDTO : guests) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(guestDTO.getId());
                row.createCell(1).setCellValue(guestDTO.getName());
                row.createCell(2).setCellValue(guestDTO.getEmail());
                row.createCell(3).setCellValue(guestDTO.getPhone());
            }

            autoSizeColumns(sheet, HEADERS.length);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    @Override
    public Resource exportGuest(GuestDTO guest) throws IOException, Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = createSheetWithHeader(workbook);

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue(guest.getId());
            row.createCell(1).setCellValue(guest.getName());
            row.createCell(2).setCellValue(guest.getEmail());
            row.createCell(3).setCellValue(guest.getPhone());

            autoSizeColumns(sheet, HEADERS.length);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
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

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private Sheet createSheetWithHeader(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Guests");
        Row headerRow = sheet.createRow(0);

        CellStyle  headerStyle = createHeaderStyle(workbook);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        return sheet;
    }
}
