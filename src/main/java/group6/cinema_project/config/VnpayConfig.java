package group6.cinema_project.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class VnpayConfig {
    public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String vnp_QRUrl = "https://sandbox.vnpayment.vn/paymentv2/qrcode";

    // Default return URL - will be overridden by environment variable or system property
    public static String vnp_ReturnUrl = "http://localhost:8080/payment/vnpay/return";
    public static String vnp_TmnCode = "NXMB98DA";
    public static String vnp_HashSecret = "LFQJM2QCXS766Y4PBIG5YNTFUO80A6PV";
    public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // Merchant account information
    public static String vnp_MerchantName = "CINEMA PROJECT";
    public static String vnp_MerchantEmail = "cinema@example.com";
    public static String vnp_MerchantPhone = "0123456789";
    public static String vnp_MerchantAddress = "123 Cinema Street, City";

    public static String md5(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    public static String Sha256(String message) {
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            digest = sb.toString();
        } catch (UnsupportedEncodingException ex) {
            digest = "";
        } catch (NoSuchAlgorithmException ex) {
            digest = "";
        }
        return digest;
    }

    // Util for VNPAY - Theo đúng chuẩn VNPay documentation
    public static String hashAllFields(Map<String, String> fields) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // HashData: không encode, chỉ nối chuỗi theo chuẩn VNPay
                if (!first) {
                    sb.append("&");
                }
                sb.append(fieldName).append("=").append(fieldValue);
                first = false;
            }
        }
        return hmacSHA512(vnp_HashSecret, sb.toString());
    }

    public static String hmacSHA512(final String key, final String data) {
        try {

            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAdress;
        try {
            ipAdress = request.getHeader("X-FORWARDED-FOR");
            if (ipAdress == null) {
                ipAdress = request.getRemoteAddr();
            }
        } catch (Exception e) {
            ipAdress = "Invalid IP:" + e.getMessage();
        }
        return ipAdress;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Lấy URL return động - có thể cấu hình từ environment variable
     */
    public static String getReturnUrl() {
        // Check environment variable first
        String envReturnUrl = System.getenv("VNPAY_RETURN_URL");
        if (envReturnUrl != null && !envReturnUrl.isEmpty()) {
            return envReturnUrl;
        }

        // Check system property
        String propReturnUrl = System.getProperty("vnpay.return.url");
        if (propReturnUrl != null && !propReturnUrl.isEmpty()) {
            return propReturnUrl;
        }

        // Fallback to default URL
        return vnp_ReturnUrl;
    }

    // Phương thức tạo URL thanh toán - Theo đúng chuẩn VNPay
    public static String getPaymentUrl(Map<String, String> vnp_Params) throws UnsupportedEncodingException {
        // Sắp xếp tham số theo thứ tự a-z
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        // Tạo chuỗi URL
        StringBuilder query = new StringBuilder();
        StringBuilder hashData = new StringBuilder();

        // Tạo hashData theo đúng chuẩn VNPay
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // HashData: không encode, chỉ nối chuỗi theo chuẩn VNPay
                if (!first) {
                    hashData.append("&");
                }
                hashData.append(fieldName).append("=").append(fieldValue);
                first = false;

                // Query URL: encode UTF-8 cho URL
                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
            }
        }

        // Tính toán chữ ký
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        // Thêm chữ ký vào URL
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        // Tạo URL hoàn chỉnh
        return vnp_PayUrl + "?" + query.toString();
    }

    // Add method to generate QR code URL
    public static String getQRCodeUrl(Map<String, String> vnp_Params) throws UnsupportedEncodingException {
        // Sort parameters alphabetically
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (!first) {
                    hashData.append("&");
                }
                hashData.append(fieldName).append("=").append(fieldValue);
                first = false;

                if (query.length() > 0) {
                    query.append("&");
                }
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
            }
        }

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnp_QRUrl + "?" + query.toString();
    }
}