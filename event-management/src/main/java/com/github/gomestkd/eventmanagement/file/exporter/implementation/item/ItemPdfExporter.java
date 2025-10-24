package com.github.gomestkd.eventmanagement.file.exporter.implementation.item;

import com.github.gomestkd.eventmanagement.dto.ItemDTO;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
import com.github.gomestkd.eventmanagement.services.QRCodeService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class ItemPdfExporter implements ItemExporter {

    private final QRCodeService qrCodeService;

    public ItemPdfExporter(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @Override
    public Resource exportItems(Set<ItemDTO> items) throws IOException, Exception {
        return null;
    }

    @Override
    public Resource exportItem(ItemDTO item) throws IOException, Exception {
        return null;
    }
}
