package com.github.gomestkd.eventmanagement.file.exporter.implementation.guest;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.GuestExporter;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Set;

public class GuestPdfExporter implements GuestExporter {
    @Override
    public Resource exportGuests(Set<GuestDTO> guests) throws IOException, Exception {
        return null;
    }

    @Override
    public Resource exportGuest(GuestDTO guest) throws IOException, Exception {
        return null;
    }
}
