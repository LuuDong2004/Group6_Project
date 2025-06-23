package group6.cinema_project.service;

public interface EmailService {
    
    void sendPasswordResetEmail(String to, String resetLink, String userName);
    
    void sendPasswordResetSuccessEmail(String to, String userName);
    
    void sendAdminPasswordResetEmail(String to, String userName, String newPassword, String adminName);
} 