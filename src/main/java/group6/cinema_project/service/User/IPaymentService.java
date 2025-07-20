package group6.cinema_project.service.User;
import group6.cinema_project.dto.TransactionSepayDto;

import java.util.Map;


public interface IPaymentService {
    void handleSepayWebhook(String transactionId);
    Map<String, Object> createSepayTransaction(TransactionSepayDto transactionSepayDto);
    Integer cancelSepayTransaction(String transactionId);
}