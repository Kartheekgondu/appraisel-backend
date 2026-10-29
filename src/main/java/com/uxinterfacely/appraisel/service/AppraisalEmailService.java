package com.uxinterfacely.appraisel.service;

import com.uxinterfacely.appraisel.entity.Appraisal;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class AppraisalEmailService {

    private final JavaMailSender mailSender;
    private final PdfGenerationService pdfGenerationService;

    public AppraisalEmailService(JavaMailSender mailSender,
                                 PdfGenerationService pdfGenerationService) {
        this.mailSender = mailSender;
        this.pdfGenerationService = pdfGenerationService;
    }

    public void sendAppraisal(Appraisal appraisal) {
        if (appraisal.getEmployeeEmail() == null ||
                appraisal.getEmployeeEmail().isBlank()) {
            throw new IllegalArgumentException("Employee email address is missing.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(appraisal.getEmployeeEmail());
            helper.setSubject("Appraisal Letter - " + appraisal.getEmployeeName());
            helper.setText(
                    "Dear " + appraisal.getEmployeeName() + ",\n\n" +
                    "Please find attached your appraisal and salary revision letter.\n\n" +
                    "Regards,\nHR Department\nUXINTERFACELY IT SOLUTIONS LLP"
            );

            byte[] pdf = pdfGenerationService.generatePdf(appraisal);
            helper.addAttachment(
                    "appraisal-" + appraisal.getId() + ".pdf",
                    new ByteArrayResource(pdf),
                    "application/pdf"
            );
            mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new IllegalStateException("Unable to send appraisal email. Check SMTP settings.", exception);
        }
    }
}
