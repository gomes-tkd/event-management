package com.github.gomestkd.eventmanagement.file.exporter.factory;

import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.file.MediaTypes;
import com.github.gomestkd.eventmanagement.file.exporter.contract.EventExporter;
import com.github.gomestkd.eventmanagement.file.exporter.contract.GuestExporter;
import com.github.gomestkd.eventmanagement.file.exporter.contract.ItemExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.event.EventCsvExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.event.EventPdfExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.event.EventXlsxExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.guest.GuestCsvExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.guest.GuestPdfExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.guest.GuestXlsxExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.item.ItemCsvExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.item.ItemPdfExporter;
import com.github.gomestkd.eventmanagement.file.exporter.implementation.item.ItemXlsxExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileExporterFactory {
    private static final Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);

    private final ApplicationContext applicationContext;

    public FileExporterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ItemExporter getItemExporter(String acceptHeader) throws IOException {
        return this.<ItemExporter>getExporter(
                acceptHeader,
                ItemCsvExporter.class,
                ItemXlsxExporter.class,
                ItemPdfExporter.class,
                "Item"
        );
    }

    public GuestExporter getGuestExporter(String acceptHeader) throws IOException {
        return this.<GuestExporter>getExporter(
                acceptHeader,
                GuestCsvExporter.class,
                GuestXlsxExporter.class,
                GuestPdfExporter.class,
                "Guest"
        );
    }

    public EventExporter getEventExporter(String acceptHeader) throws IOException {
        return this.<EventExporter>getExporter(
                acceptHeader,
                EventCsvExporter.class,
                EventXlsxExporter.class,
                EventPdfExporter.class,
                "Event"
        );
    }

    private <T> T getExporter(
            String acceptHeader,
            Class<? extends T> csvClass,
            Class<? extends T> xlsxClass,
            Class<? extends T> pdfClass,
            String typeName
    ) throws IOException {
        logger.info("Iniciando exportação ({}) com Accept: {}", typeName, acceptHeader);

        if (acceptHeader == null || acceptHeader.isBlank()) {
            logger.error("Accept header inválido: está nulo ou vazio");
            throw new BadRequestException("Accept header cannot be null or empty!");
        }

        try {
            if (acceptHeader.contains(MediaTypes.APPLICATION_CSV_VALUE)) {
                logger.debug("[{}] Detectado formato CSV", typeName);
                return applicationContext.getBean(csvClass);
            } else if (acceptHeader.contains(MediaTypes.APPLICATION_XLSX_VALUE)) {
                logger.debug("[{}] Detectado formato XLSX", typeName);
                return applicationContext.getBean(xlsxClass);
            } else if (acceptHeader.contains(MediaTypes.APPLICATION_PDF_VALUE)) {
                logger.debug("[{}] Detectado formato PDF", typeName);
                return applicationContext.getBean(pdfClass);
            } else {
                logger.warn("[{}] Tipo de mídia não suportado: {}", typeName, acceptHeader);
                throw new BadRequestException("Unsupported media type: " + acceptHeader);
            }
        } catch (Exception e) {
            logger.error("[{}] Erro ao obter bean de exportação: {}", typeName, e.getMessage(), e);
            throw e;
        }
    }

//    // Retorna o exportador correto para "Item"
//    public ItemExporter getItemExporter(String acceptHeader) {
//        if (acceptHeader == null || acceptHeader.isBlank()) {
//            throw new BadRequestException("Header 'Accept' não pode ser nulo ou vazio");
//        }
//
//        if (acceptHeader.contains(MediaTypes.APPLICATION_CSV_VALUE)) {
//            return context.getBean(ItemCsvExporter.class);
//        } else if (acceptHeader.contains(MediaTypes.APPLICATION_XLSX_VALUE)) {
//            return context.getBean(ItemXlsxExporter.class);
//        } else if (acceptHeader.contains(MediaTypes.APPLICATION_PDF_VALUE)) {
//            return context.getBean(ItemPdfExporter.class);
//        }
//
//        throw new BadRequestException("Formato de mídia não suportado: " + acceptHeader);
//    }
//
//    // Retorna o exportador correto para "Guest"
//    public GuestExporter getGuestExporter(String acceptHeader) {
//        if (acceptHeader == null || acceptHeader.isBlank()) {
//            throw new BadRequestException("Header 'Accept' não pode ser nulo ou vazio");
//        }
//
//        if (acceptHeader.contains(MediaTypes.APPLICATION_CSV_VALUE)) {
//            return context.getBean("guestCsvExporter", GuestExporter.class);
//        } else if (acceptHeader.contains(MediaTypes.APPLICATION_XLSX_VALUE)) {
//            return context.getBean("guestXlsxExporter", GuestExporter.class);
//        } else if (acceptHeader.contains(MediaTypes.APPLICATION_PDF_VALUE)) {
//            return context.getBean("guestPdfExporter", GuestExporter.class);
//        }
//
//        throw new BadRequestException("Formato de mídia não suportado: " + acceptHeader);
//    }
}
