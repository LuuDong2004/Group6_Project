package group6.cinema_project.service;

import group6.cinema_project.dto.VnpayRequest;
import group6.cinema_project.dto.BookingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class VnpayServiceIntegrationTest {

    private VnpayService vnpayService;
    
    @Mock
    private IBookingService bookingService;
    
    private MockHttpServletRequest request;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        vnpayService = new VnpayService();
        vnpayService.bookingService = bookingService;
        
        request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
    }

    @Test
    public void testCreatePayment_Success() throws Exception {
        // Arrange
        Integer bookingId = 123;
        BookingDto mockBooking = new BookingDto();
        mockBooking.setId(bookingId);
        mockBooking.setTotalPrice(100000.0); // 100,000 VND
        
        when(bookingService.getBookingById(bookingId)).thenReturn(mockBooking);
        
        VnpayRequest paymentRequest = new VnpayRequest();
        paymentRequest.setBookingId(bookingId);
        paymentRequest.setBankCode("NCB");

        // Act
        String paymentUrl = vnpayService.createPayment(paymentRequest, request);

        // Assert
        assertNotNull(paymentUrl);
        assertTrue(paymentUrl.startsWith("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"));
        assertTrue(paymentUrl.contains("vnp_Amount=10000000")); // 100000 * 100
        assertTrue(paymentUrl.contains("vnp_SecureHash="));
        assertTrue(paymentUrl.contains("Booking%20ID%3A%20123")); // URL encoded "Booking ID: 123"
        
        verify(bookingService, times(1)).getBookingById(bookingId);
    }

    @Test
    public void testCreatePayment_BookingNotFound() {
        // Arrange
        Integer bookingId = 999;
        when(bookingService.getBookingById(bookingId)).thenReturn(null);
        
        VnpayRequest paymentRequest = new VnpayRequest();
        paymentRequest.setBookingId(bookingId);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vnpayService.createPayment(paymentRequest, request);
        });
        
        assertTrue(exception.getMessage().contains("Không tìm thấy booking"));
    }

    @Test
    public void testCreatePayment_NullBookingId() {
        // Arrange
        VnpayRequest paymentRequest = new VnpayRequest();
        paymentRequest.setBookingId(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            vnpayService.createPayment(paymentRequest, request);
        });
        
        assertTrue(exception.getMessage().contains("Booking ID không được null"));
    }

    @Test
    public void testVerifyPaymentReturn_ValidSignature() {
        // Arrange
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Amount", "10000000");
        vnpParams.put("vnp_BankCode", "NCB");
        vnpParams.put("vnp_OrderInfo", "Test payment");
        vnpParams.put("vnp_ResponseCode", "00");
        vnpParams.put("vnp_TmnCode", "NXMB98DA");
        vnpParams.put("vnp_TxnRef", "12345678");
        
        // Generate valid hash for test data
        String validHash = group6.cinema_project.config.VnpayConfig.hashAllFields(vnpParams);
        vnpParams.put("vnp_SecureHash", validHash);

        // Act
        boolean result = vnpayService.verifyPaymentReturn(vnpParams);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testVerifyPaymentReturn_InvalidSignature() {
        // Arrange
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Amount", "10000000");
        vnpParams.put("vnp_BankCode", "NCB");
        vnpParams.put("vnp_OrderInfo", "Test payment");
        vnpParams.put("vnp_ResponseCode", "00");
        vnpParams.put("vnp_TmnCode", "NXMB98DA");
        vnpParams.put("vnp_TxnRef", "12345678");
        vnpParams.put("vnp_SecureHash", "invalid_hash");

        // Act
        boolean result = vnpayService.verifyPaymentReturn(vnpParams);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testVerifyPaymentReturn_MissingSignature() {
        // Arrange
        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Amount", "10000000");
        vnpParams.put("vnp_ResponseCode", "00");

        // Act
        boolean result = vnpayService.verifyPaymentReturn(vnpParams);

        // Assert
        assertFalse(result);
    }
}
