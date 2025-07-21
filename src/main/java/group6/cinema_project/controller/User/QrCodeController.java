package group6.cinema_project.controller.User;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class QrCodeController {
    @GetMapping(value = "/qr/{code}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getQrCode(@PathVariable String code, HttpServletResponse response) throws IOException {
        generateQrImage(code, response);
    }

    @GetMapping(value = "/qr/food/{bookingId}/{foodName}", produces = MediaType.IMAGE_PNG_VALUE)
    public void getFoodQrCode(@PathVariable Integer bookingId, @PathVariable String foodName, HttpServletResponse response) throws IOException {
        String qrText = "Booking: " + bookingId + ", Food: " + foodName;
        generateQrImage(qrText, response);
    }

    private void generateQrImage(String text, HttpServletResponse response) throws IOException {
        int width = 200;
        int height = 200;
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", response.getOutputStream());
        } catch (WriterException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tạo QR code");
        }
    }
} 