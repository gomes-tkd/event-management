package com.github.gomestkd.eventmanagement.file.importer.factory;

import com.github.gomestkd.eventmanagement.exception.BadRequestException;
import com.github.gomestkd.eventmanagement.file.importer.contract.EventImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.event.EventCsvImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.event.EventXlsxImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.guest.GuestCsvImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.guest.GuestXlsxImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.item.ItemCsvImporter;
import com.github.gomestkd.eventmanagement.file.importer.implementation.item.ItemXlsxImporter;
import com.github.gomestkd.eventmanagement.file.importer.contract.GuestImporter;
import com.github.gomestkd.eventmanagement.file.importer.contract.ItemImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileImporterFactory {

    private static final Logger logger = LoggerFactory.getLogger(FileImporterFactory.class);
    private final ApplicationContext applicationContext;

    public FileImporterFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ItemImporter getItemImporter(String fileName) throws IOException {
        logger.info("Solicitação de importação de Item recebida para o arquivo: {}", fileName);
        return getImporter(
                fileName,
                ItemCsvImporter.class,
                ItemXlsxImporter.class,
                "Item"
        );
    }

    public GuestImporter getGuestImporter(String fileName) throws IOException {
        logger.info("Solicitação de importação de Guest recebida para o arquivo: {}", fileName);
        return (GuestImporter) getImporter(
                fileName,
                GuestCsvImporter.class,
                GuestXlsxImporter.class,
                "Guest"
        );
    }

    public EventImporter getEventImporter(String fileName) throws IOException {
        logger.info("Solicitação de importação de Event recebida para o arquivo: {}", fileName);
        return (EventImporter) getImporter(
                fileName,
                EventCsvImporter.class,
                EventXlsxImporter.class,
                "Event"
        );
    }

    private <T> T getImporter(String fileName,
                              Class<? extends T> csvClass,
                              Class<? extends T> xlsxClass,
                              String typeName) throws IOException {

        logger.info("Iniciando importação ({}) para o arquivo: {}", typeName, fileName);

        if (fileName == null || fileName.isBlank()) {
            logger.error("[{}] Nome de arquivo inválido: está nulo ou vazio", typeName);
            throw new BadRequestException("File name cannot be null or empty!");
        }

        try {
            if (fileName.toLowerCase().endsWith(".csv")) {
                logger.debug("[{}] Detectado formato CSV para arquivo: {}", typeName, fileName);
                return applicationContext.getBean(csvClass);
            } else if (fileName.toLowerCase().endsWith(".xlsx") || fileName.toLowerCase().endsWith(".xls")) {
                logger.debug("[{}] Detectado formato Excel para arquivo: {}", typeName, fileName);
                return applicationContext.getBean(xlsxClass);
            } else {
                logger.warn("[{}] Formato de arquivo não suportado: {}", typeName, fileName);
                throw new BadRequestException("Invalid File Format!");
            }
        } catch (Exception e) {
            logger.error("[{}] Erro ao obter o bean de importação para o arquivo {}: {}",
                    typeName, fileName, e.getMessage(), e);
            throw e;
        }
    }
}
