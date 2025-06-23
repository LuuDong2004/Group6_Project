package group6.cinema_project.dto;

import jakarta.validation.constraints.NotNull;

public class AdminPasswordResetDto {
    
    @NotNull(message = "User ID không được để trống")
    private Integer userId;
    
    private boolean sendEmail = true;
    
    private String customPassword;
    
    private boolean generateRandomPassword = true;
    
    public AdminPasswordResetDto() {
    }
    
    public AdminPasswordResetDto(Integer userId) {
        this.userId = userId;
    }
    
    public AdminPasswordResetDto(Integer userId, boolean sendEmail, String customPassword, boolean generateRandomPassword) {
        this.userId = userId;
        this.sendEmail = sendEmail;
        this.customPassword = customPassword;
        this.generateRandomPassword = generateRandomPassword;
    }
    
    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public boolean isSendEmail() {
        return sendEmail;
    }
    
    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
    
    public String getCustomPassword() {
        return customPassword;
    }
    
    public void setCustomPassword(String customPassword) {
        this.customPassword = customPassword;
    }
    
    public boolean isGenerateRandomPassword() {
        return generateRandomPassword;
    }
    
    public void setGenerateRandomPassword(boolean generateRandomPassword) {
        this.generateRandomPassword = generateRandomPassword;
    }
    
    public boolean hasCustomPassword() {
        return customPassword != null && !customPassword.trim().isEmpty();
    }
} 