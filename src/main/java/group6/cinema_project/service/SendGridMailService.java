package group6.cinema_project.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendGridMailService {
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String htmlContent) throws Exception {
        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);
        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println("SendGrid response status: " + response.getStatusCode());
            if (response.getStatusCode() >= 400) {
                System.err.println("SendGrid error: " + response.getBody());
                throw new RuntimeException("SendGrid API error: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
} 