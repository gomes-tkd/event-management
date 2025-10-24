package com.github.gomestkd.eventmanagement.file.exporter.contract;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

public interface ItemExporter {
    Resource exportItems(Set<ItemDTO> items) throws IOException, Exception;
    Resource exportItem(ItemDTO item) throws IOException, Exception;
}
