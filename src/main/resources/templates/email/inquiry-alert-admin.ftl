<#import "base-email.ftl" as layout>
<@layout.base
  subject="New Inquiry — Pro Elite Motors"
  customerName="Team"
  headerLabel="New Lead">

  <div class="content">
    <p>A new inquiry has been submitted. Review the details below and follow up promptly.</p>
  </div>

  <div class="highlight-block">
    <div class="hl-title">Inquiry Details</div>
    <div class="hl-row">
      <span class="hl-label">Customer</span>
      <span class="hl-value">${customerName}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Email</span>
      <span class="hl-value">${customerEmail}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Phone</span>
      <span class="hl-value">${customerPhone}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Vehicle</span>
      <span class="hl-value">${vehicleTitle}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Type</span>
      <span class="hl-value hl-value-gold">${type}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Source</span>
      <span class="hl-value">${source}</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Inquiry ID</span>
      <span class="hl-value">${inquiryId}</span>
    </div>
  </div>

  <#if message != "—">
  <div class="highlight-block">
    <div class="hl-title">Customer Message</div>
    <div class="content" style="margin-bottom:0;">
      <p>${message}</p>
    </div>
  </div>
  </#if>

  <div class="button-container">
    <a href="https://proelitemotorsllc.com/admin/inquiries/${inquiryId}"
       class="cta-button">View in Dashboard</a>
  </div>

  <div class="info-box">
    <p><strong>Tip:</strong> Respond within 1 hour to maximize conversion.
    Reply directly to <a href="mailto:${customerEmail}">${customerEmail}</a>
    or call <strong>${customerPhone}</strong>.</p>
  </div>

</@layout.base>