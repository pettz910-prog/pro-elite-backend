<#import "base-email.ftl" as layout>
<@layout.base
  subject="Welcome to Pro Elite Motors"
  customerName=firstName
  headerLabel="Welcome">

  <div class="content">
    <p>Welcome to Pro Elite Motors — your trusted premium US dealership. Your account has been created and you now have full access to our platform.</p>
    <p>Here is what you can do right now.</p>
  </div>

  <div class="highlight-block">
    <div class="hl-title">What Awaits You</div>
    <div class="hl-row">
      <span class="hl-label">500+ Vehicles</span>
      <span class="hl-value">New, Used &amp; Certified Pre-Owned</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Multiple Locations</span>
      <span class="hl-value">Across the United States</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Financing</span>
      <span class="hl-value">Flexible plans available</span>
    </div>
    <div class="hl-row">
      <span class="hl-label">Rating</span>
      <span class="hl-value hl-value-gold">4.9 ★ Customer Rated</span>
    </div>
  </div>

  <div class="button-container">
    <a href="https://proelitemotorsllc.com/inventory" class="cta-button">Browse Inventory</a>
  </div>

  <div class="info-box">
    <p>Have questions? Our team is here to help. Reach us at
    <a href="mailto:hello@proelitemotors.com">hello@proelitemotors.com</a>
    or <strong>(800) 555-0142</strong>.</p>
  </div>

</@layout.base>