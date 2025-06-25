package group6.cinema_project.service.impl;

import group6.cinema_project.service.QRCodeService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import io.nayuki.qrcodegen.QrCode;

@Service
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public String generateQRCode(String bookingCode) {
        try {
            // Tạo dữ liệu QR code
            String qrData = "BOOKING:" + bookingCode;
            
            // Tạo QR code với thư viện qrcodegen
            QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);
            
            // Chuyển đổi thành BufferedImage
            BufferedImage image = toBufferedImage(qr, 4, 10);
            
            // Chuyển đổi thành Base64
            return convertImageToBase64(image);
            
        } catch (Exception e) {
            e.printStackTrace();
            return generateDefaultQRCode();
        }
    }

    @Override
    public String generateTicketQRCode(String bookingCode, String movieName, String seatNames) {
        try {
            // Tạo QR code với thông tin chi tiết vé
            String qrData = String.format("TICKET:%s|MOVIE:%s|SEATS:%s", 
                bookingCode, movieName, seatNames);
            
            // Tạo QR code với thư viện qrcodegen
            QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);
            
            // Chuyển đổi thành BufferedImage
            BufferedImage image = toBufferedImage(qr, 4, 10);
            
            // Chuyển đổi thành Base64
            return convertImageToBase64(image);
            
        } catch (Exception e) {
            e.printStackTrace();
            return generateDefaultQRCode();
        }
    }

    /**
     * Chuyển đổi QrCode thành BufferedImage (chuẩn Nayuki qrcodegen)
     */
    private BufferedImage toBufferedImage(QrCode qr, int scale, int border) {
        int size = qr.size;
        int imgSize = (size + border * 2) * scale;
        BufferedImage img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgSize, imgSize);
        g.setColor(Color.BLACK);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (qr.getModule(x, y)) {
                    g.fillRect((x + border) * scale, (y + border) * scale, scale, scale);
                }
            }
        }
        g.dispose();
        return img;
    }

    /**
     * Chuyển đổi BufferedImage thành Base64 string
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }

    private String generateDefaultQRCode() {
        // QR code mặc định 1x1 pixel màu đen
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==";
    }
} 