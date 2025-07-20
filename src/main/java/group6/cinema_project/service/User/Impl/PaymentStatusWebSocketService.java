package group6.cinema_project.service.User.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class PaymentStatusWebSocketService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendPaymentStatus(String transactionId, String status, Integer bookingId, Double amount) {
        messagingTemplate.convertAndSend("/topic/payment-status/" + transactionId,
                Map.of(
                        "transactionId", transactionId,
                        "status", status,
                        "bookingId", bookingId,
                        "amount", amount
                ));
    }
} 