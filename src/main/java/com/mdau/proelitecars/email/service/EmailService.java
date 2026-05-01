package com.mdau.proelitecars.email.service;

import com.mdau.proelitecars.inquiry.entity.Inquiry;
import com.mdau.proelitecars.user.entity.User;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<Boolean> sendInquiryAlertToAdmin(Inquiry inquiry);

    CompletableFuture<Boolean> sendInquiryConfirmationToCustomer(Inquiry inquiry);

    CompletableFuture<Boolean> sendWelcomeEmail(User user);

    boolean sendHtmlEmail(String to, String subject,
                          String templateName, Map<String, Object> model);
}