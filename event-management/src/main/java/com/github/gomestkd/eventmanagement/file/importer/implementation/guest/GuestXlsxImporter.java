package com.github.gomestkd.eventmanagement.file.importer.implementation.guest;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.GuestImporter;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class GuestXlsxImporter implements GuestImporter {

    @Override
    public Set<GuestDTO> importGuests(InputStream inputStream) throws Exception {
        try(XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            return parseRowsToGuestDTOs(rowIterator);
        }
    }

    private Set<GuestDTO> parseRowsToGuestDTOs(Iterator<Row> rows) {
        Set<GuestDTO> dtos = new HashSet<>();

        while(rows.hasNext()) {
            Row row = rows.next();

            if (isRowValid(row)) {
                dtos.add(parseRowToGuestDTO(row));
            }

        }

        return dtos;
    }

    private GuestDTO parseRowToGuestDTO(Row row) {
        GuestDTO dto = new GuestDTO();
        dto.setName(row.getCell(0).getStringCellValue());
        dto.setEmail(row.getCell(1).getStringCellValue());
        dto.setPhone(row.getCell(2).getStringCellValue());

        return dto;
    }

    private static boolean isRowValid(Row row) {
        return (row.getCell(0) != null) && (row.getCell(0).getCellType() != CellType.BLANK);
    }
}
