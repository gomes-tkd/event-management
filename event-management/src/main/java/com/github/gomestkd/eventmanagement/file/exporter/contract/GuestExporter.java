package com.github.gomestkd.eventmanagement.file.exporter.contract;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

public interface GuestExporter {
    Resource exportGuests(Set<GuestDTO> guests) throws IOException, Exception;
    Resource exportGuest(GuestDTO guest) throws IOException, Exception;
}
