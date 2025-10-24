package com.github.gomestkd.eventmanagement.file.exporter.implementation.event;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.EventExporter;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

public class EventPdfExporter implements EventExporter {
    @Override
    public Resource exportEvents(Set<EventDTO> events) throws IOException, Exception {
        return null;
    }

    @Override
    public Resource exportEvent(EventDTO event) throws IOException, Exception {
        return null;
    }
}
