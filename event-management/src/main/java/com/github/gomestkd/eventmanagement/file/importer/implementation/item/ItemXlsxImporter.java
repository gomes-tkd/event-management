package com.github.gomestkd.eventmanagement.file.importer.implementation.item;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
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
public class ItemXlsxImporter implements ItemImporter {

    @Override
    public Set<ItemDTO> importItems(InputStream inputStream) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            return parseRowsToItemDTOs(rowIterator);
        }
    }

    private Set<ItemDTO> parseRowsToItemDTOs(Iterator<Row> rows) {
        Set<ItemDTO> items = new HashSet<>();

        while (rows.hasNext()) {
            Row row = rows.next();
            if (isRowValid(row)) {
                items.add(parseRowTOItemDTO(row));
            }
        }

        return items;
    }

    private ItemDTO parseRowTOItemDTO(Row row) {
        ItemDTO item = new ItemDTO();
        item.setName(row.getCell(0).getStringCellValue());
        item.setDescription(row.getCell(1).getStringCellValue());
        item.setPrice(row.getCell(2).getNumericCellValue());
        return item;
    }

    private static boolean isRowValid(Row row) {
        return row.getCell(0) != null && row.getCell(0).getCellType() != CellType.BLANK;
    }
}
