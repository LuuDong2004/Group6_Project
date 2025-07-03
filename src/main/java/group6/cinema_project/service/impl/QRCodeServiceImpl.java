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

    @Override
    public byte[] generateQRCodeBytes(String bookingCode) {
        try {
            String qrData = "BOOKING:" + bookingCode;
            QrCode qr = QrCode.encodeText(qrData, QrCode.Ecc.MEDIUM);
            BufferedImage image = toBufferedImage(qr, 4, 10);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
} 