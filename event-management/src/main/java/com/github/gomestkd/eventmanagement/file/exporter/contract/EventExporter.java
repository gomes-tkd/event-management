package com.github.gomestkd.eventmanagement.file.exporter.contract;

import com.github.gomestkd.eventmanagement.dto.EventDTO;
import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

public interface EventExporter {
    Resource exportEvents(Set<EventDTO> events) throws IOException, Exception;
    Resource exportEvent(EventDTO event) throws IOException, Exception;
}
