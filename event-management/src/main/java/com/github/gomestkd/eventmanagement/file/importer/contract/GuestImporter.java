package com.github.gomestkd.eventmanagement.file.importer.contract;

import com.github.gomestkd.eventmanagement.dto.GuestDTO;

import java.io.InputStream;
import java.util.Set;

public interface GuestImporter {
    Set<GuestDTO> importGuests(InputStream inputStream) throws Exception;
}
