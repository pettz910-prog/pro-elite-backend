<#import "base-email.ftl" as layout>
<@layout.base
  subject="We received your inquiry — Pro Elite Motors"
  customerName=customerName
  headerLabel="Inquiry Received">

  <div class="content">
    <p>Thank you for reaching out to Pro Elite Motors. We have received your inquiry and a member of our team will be in touch with you shortly — typically within 1 business hour.</p>
  </div>

  <div class="highlight-block">
    <div class="hl-title">Your Inquiry Summary</div>
    <div class="hl-row">
      <span class="hl-label">Vehicle</span>
      <span class="hl-value">${vehicleTitle}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Request Type</span>
      <span class="hl-value hl-value-gold">${type}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Reference ID</span>
      <span class="hl-value">${inquiryId}</span>
    </div>
  </div>

  <#if message != "—">
  <div class="highlight-block">
    <div class="hl-title">Your Message</div>
    <div class="content" style="margin-bottom:0;">
      <p>${message}</p>
    </div>
  </div>
  </#if>

  <div class="content">
    <p>While you wait, feel free to browse our full inventory or explore financing options on our website.</p>
  </div>

  <div class="button-container">
    <a href="https://proelitemotorsllc.com/inventory" class="cta-button">Browse Inventory</a>
  </div>

  <div class="info-box">
    <p>Need immediate assistance? Call us at <strong>(800) 555-0142</strong> or email
    <a href="mailto:hello@proelitemotors.com">hello@proelitemotors.com</a>.</p>
    <p>Our team is available Mon–Sat, 9am–7pm EST.</p>
  </div>

</@layout.base>