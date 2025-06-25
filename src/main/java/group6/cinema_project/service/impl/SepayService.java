package group6.cinema_project.service;

import group6.cinema_project.dto.SepayRequest;
import group6.cinema_project.dto.SepayResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class SepayService {
    private final WebClient webClient;
    private final String apiKey;
    private final String merchantCode;

    public SepayService(
            @Value("${sepay.api.base-url}") String baseUrl,
            @Value("${sepay.api.key}") String apiKey,
            @Value("${sepay.api.merchant-code}") String merchantCode
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
        this.merchantCode = merchantCode;
    }

    /**
     * Gửi request tạo QR code thanh toán Sepay
     */
    public SepayResponse createPaymentQR(SepayRequest request) {
        try {

            if (request.getMerchantCode() == null || request.getMerchantCode().isEmpty()) {
                request.setMerchantCode(merchantCode);
            }
            return webClient.post()
                    .uri("/payment/qr/create")
                    .header("x-api-key", apiKey)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(SepayResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi gọi Sepay: " + e.getMessage());
            error.setErrorCode("HTTP_" + e.getRawStatusCode());
            return error;
        } catch (Exception e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi hệ thống: " + e.getMessage());
            error.setErrorCode("SYSTEM_ERROR");
            return error;
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán Sepay
     */
    public SepayResponse checkPaymentStatus(String transactionId) {
        try {
            return webClient.get()
                    .uri("/api/payment/qr/status?transactionId=" + transactionId)
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .bodyToMono(SepayResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi gọi Sepay: " + e.getMessage());
            error.setErrorCode("HTTP_" + e.getRawStatusCode());
            return error;
        } catch (Exception e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi hệ thống: " + e.getMessage());
            error.setErrorCode("SYSTEM_ERROR");
            return error;
        }
    }

    /**
     * Xác nhận thanh toán thành công Sepay (nếu Sepay hỗ trợ)
     */
    public SepayResponse confirmPayment(String transactionId) {
        try {
            return webClient.post()
                    .uri("/api/payment/qr/confirm")
                    .header("x-api-key", apiKey)
                    .bodyValue(transactionId)
                    .retrieve()
                    .bodyToMono(SepayResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi gọi Sepay: " + e.getMessage());
            error.setErrorCode("HTTP_" + e.getRawStatusCode());
            return error;
        } catch (Exception e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi hệ thống: " + e.getMessage());
            error.setErrorCode("SYSTEM_ERROR");
            return error;
        }
    }

    public SepayResponse checkTransactionByReference(String referenceNumber) {
        try {
            String url = "/transactions/list?reference_number=" + referenceNumber;
            return webClient.get()
                    .uri(url)
                    .header("x-api-key", apiKey)
                    .retrieve()
                    .bodyToMono(SepayResponse.class)
                    .block();
        } catch (Exception e) {
            SepayResponse error = new SepayResponse();
            error.setSuccess(false);
            error.setMessage("Lỗi hệ thống: " + e.getMessage());
            error.setErrorCode("SYSTEM_ERROR");
            return error;
        }
    }
}
// Hướng dẫn cấu hình:
// Thêm vào application.properties:
// sepay.api.base-url=https://api.sepay.vn
// sepay.api.key=YOUR_API_KEY
// sepay.api.merchant-code=YOUR_MERCHANT_CODE 