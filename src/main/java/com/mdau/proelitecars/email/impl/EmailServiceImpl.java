package com.mdau.proelitecars.email.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdau.proelitecars.email.service.EmailService;
import com.mdau.proelitecars.inquiry.entity.Inquiry;
import com.mdau.proelitecars.user.entity.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final ObjectMapper objectMapper;
    private final Configuration freemarkerConfig;

    @Value("${app.brevo.sender-email}")
    private String fromEmail;

    @Value("${app.brevo.sender-name}")
    private String fromName;

    @Value("${app.brevo.api-key:}")
    private String brevoApiKey;

    @Value("${app.admin.email}")
    private String adminEmail;

    private static final String BREVO_API_URL =
            "https://api.brevo.com/v3/smtp/email";

    private OkHttpClient httpClient;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            ObjectMapper objectMapper,
                            Configuration freemarkerConfig) {
        this.javaMailSender   = javaMailSender;
        this.objectMapper     = objectMapper;
        this.freemarkerConfig = freemarkerConfig;
    }

    @PostConstruct
    public void init() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        String method = (brevoApiKey != null && !brevoApiKey.isBlank())
                ? "Brevo API" : "SMTP fallback";
        log.info("✅ EmailService initialized — method: {}, from: {}",
                method, fromEmail);
    }

    // ── Inquiry alert to admin ─────────────────────────────────────────────
    @Async
    @Override
    public CompletableFuture<Boolean> sendInquiryAlertToAdmin(Inquiry inquiry) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("inquiryId",     inquiry.getId().toString());
            model.put("customerName",  inquiry.getCustomerName());
            model.put("customerEmail", inquiry.getEmail());
            model.put("customerPhone", inquiry.getPhone() != null
                    ? inquiry.getPhone() : "—");
            model.put("vehicleTitle",  inquiry.getVehicleTitle() != null
                    ? inquiry.getVehicleTitle() : "General Inquiry");
            model.put("message",       inquiry.getMessage() != null
                    ? inquiry.getMessage() : "—");
            model.put("type",          inquiry.getType().name()
                    .replace("_", " "));
            model.put("source",        inquiry.getSource() != null
                    ? inquiry.getSource().name().replace("_", " ") : "—");

            boolean sent = sendHtmlEmail(
                    adminEmail,
                    "New Inquiry: " + inquiry.getCustomerName()
                    + " — " + (inquiry.getVehicleTitle() != null
                            ? inquiry.getVehicleTitle() : "General"),
                    "inquiry-alert-admin.ftl",
                    model
            );
            if (sent) log.info("✅ Admin alert sent for inquiry: {}",
                    inquiry.getId());
            else      log.error("❌ Failed admin alert for inquiry: {}",
                    inquiry.getId());
            return CompletableFuture.completedFuture(sent);
        } catch (Exception e) {
            log.error("❌ Exception sending admin alert: {}", e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    // ── Inquiry confirmation to customer ──────────────────────────────────
    @Async
    @Override
    public CompletableFuture<Boolean> sendInquiryConfirmationToCustomer(
            Inquiry inquiry) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("customerName", inquiry.getCustomerName());
            model.put("vehicleTitle", inquiry.getVehicleTitle() != null
                    ? inquiry.getVehicleTitle() : "your vehicle of interest");
            model.put("type",         inquiry.getType().name()
                    .replace("_", " "));
            model.put("message",      inquiry.getMessage() != null
                    ? inquiry.getMessage() : "—");
            model.put("inquiryId",    inquiry.getId().toString());

            boolean sent = sendHtmlEmail(
                    inquiry.getEmail(),
                    "We received your inquiry — Pro Elite Motors",
                    "inquiry-confirmation-customer.ftl",
                    model
            );
            if (sent) log.info("✅ Customer confirmation sent to: {}",
                    inquiry.getEmail());
            else      log.error("❌ Failed customer confirmation to: {}",
                    inquiry.getEmail());
            return CompletableFuture.completedFuture(sent);
        } catch (Exception e) {
            log.error("❌ Exception sending customer confirmation: {}",
                    e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    // ── Welcome email ─────────────────────────────────────────────────────
    @Async
    @Override
    public CompletableFuture<Boolean> sendWelcomeEmail(User user) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("firstName", user.getFirstName());
            model.put("email",     user.getEmail());

            boolean sent = sendHtmlEmail(
                    user.getEmail(),
                    "Welcome to Pro Elite Motors",
                    "welcome.ftl",
                    model
            );
            if (sent) log.info("✅ Welcome email sent to: {}", user.getEmail());
            else      log.error("❌ Failed welcome email to: {}", user.getEmail());
            return CompletableFuture.completedFuture(sent);
        } catch (Exception e) {
            log.error("❌ Exception sending welcome email: {}",
                    e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
    }

    // ── Core HTML email sender ────────────────────────────────────────────
    @Override
    public boolean sendHtmlEmail(String to, String subject,
                                  String templateName,
                                  Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            String html = FreeMarkerTemplateUtils
                    .processTemplateIntoString(template, model);

            if (brevoApiKey != null && !brevoApiKey.isBlank()) {
                try {
                    sendViaBrevoApi(to, subject, html);
                    log.debug("📧 Email sent via Brevo to: {}", to);
                    return true;
                } catch (IOException e) {
                    log.warn("⚠️ Brevo failed, falling back to SMTP: {}",
                            e.getMessage());
                }
            }

            sendViaSMTP(to, subject, html);
            log.debug("📧 Email sent via SMTP to: {}", to);
            return true;

        } catch (Exception e) {
            log.error("❌ All email methods failed for {}: {}",
                    to, e.getMessage(), e);
            return false;
        }
    }

    // ── Brevo REST API ─────────────────────────────────────────────────────
    private void sendViaBrevoApi(String toEmail,
                                  String subject,
                                  String htmlContent) throws IOException {
        Map<String, Object> payload = new HashMap<>();

        Map<String, String> sender = new HashMap<>();
        sender.put("email", fromEmail);
        sender.put("name",  fromName);
        payload.put("sender", sender);

        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", toEmail);
        payload.put("to", new Map[]{recipient});

        payload.put("subject",     subject);
        payload.put("htmlContent", htmlContent);

        String json = objectMapper.writeValueAsString(payload);
        RequestBody body = RequestBody.create(
                json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(BREVO_API_URL)
                .addHeader("api-key",      brevoApiKey)
                .addHeader("Content-Type", "application/json")
                .addHeader("accept",       "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String err = response.body() != null
                        ? response.body().string() : "no body";
                throw new IOException(
                        "Brevo error " + response.code() + ": " + err);
            }
        }
    }

    // ── SMTP fallback ──────────────────────────────────────────────────────
    private void sendViaSMTP(String toEmail,
                              String subject,
                              String htmlContent) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail, fromName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }
}