package group6.cinema_project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        

        mailSender.setHost("smtp.sendgrid.net");
        mailSender.setPort(587);
        mailSender.setUsername("apikey"); // Với SendGrid, username luôn là "apikey"
        mailSender.setPassword(sendgridApiKey); // API key từ SendGrid
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "false"); // Đặt true nếu muốn debug
        
        return mailSender;
    }
}
