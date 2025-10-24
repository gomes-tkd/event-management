package com.github.gomestkd.eventmanagement.file.importer.contract;

import com.github.gomestkd.eventmanagement.dto.EventDTO;

import java.io.InputStream;
import java.util.Set;

public interface EventImporter {
    Set<EventDTO> importEvents(InputStream inputStream) throws Exception;
}
