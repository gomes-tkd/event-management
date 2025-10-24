package com.github.gomestkd.eventmanagement.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class QRCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeService.class);

    public InputStream generateQRCode(String url, int width, int height) throws WriterException, IOException {
        logger.info("Starting QR code generation for URL: {}", url);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;

        try {
            bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height);
            logger.debug("QR code matrix successfully created. Dimensions: {}x{}", width, height);
        } catch (WriterException e) {
            logger.error("Error generating QR code matrix for URL: {}", url, e);
            throw e;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            logger.info("QR code image successfully written to stream for URL: {}", url);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            logger.error("I/O error while writing QR code to stream for URL: {}", url, e);
            throw e;
        }
    }
}
