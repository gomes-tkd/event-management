package com.github.gomestkd.eventmanagement.file.importer.contract;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;

import java.io.InputStream;
import java.util.Set;

public interface ItemImporter {
    Set<ItemDTO> importItems(InputStream inputStream) throws  Exception;
}
