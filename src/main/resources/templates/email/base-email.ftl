<#macro base subject customerName="" headerLabel="">
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <title>${subject}</title>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600;700&family=Inter:wght@400;500;600&display=swap');

    * { margin:0; padding:0; box-sizing:border-box; }
    body {
      font-family: 'Inter', 'Segoe UI', Arial, sans-serif;
      background-color: #F5F0E8;
      color: #1A1A1A;
      width: 100% !important;
      -webkit-font-smoothing: antialiased;
    }

    .email-wrapper {
      width: 100%;
      background-color: #F5F0E8;
      padding: 48px 16px 56px;
    }

    .email-container {
      max-width: 600px;
      margin: 0 auto;
      background-color: #FFFFFF;
      border-radius: 2px;
      overflow: hidden;
      border: 1px solid #E5DDD0;
    }

    /* ── Header ── */
    .email-header {
      background-color: #1A1A1A;
      padding: 28px 48px;
    }

    .logo-text {
      font-family: 'Playfair Display', Georgia, serif;
      font-size: 20px;
      font-weight: 700;
      color: #FFFFFF;
      letter-spacing: 0.05em;
      text-transform: uppercase;
    }

    .logo-dot {
      color: #C9A84C;
      margin: 0 4px;
    }

    .logo-sub {
      font-family: 'Inter', Arial, sans-serif;
      font-size: 9px;
      font-weight: 600;
      letter-spacing: 0.25em;
      text-transform: uppercase;
      color: #C9A84C;
      display: block;
      margin-top: 4px;
    }

    .header-label {
      font-size: 9px;
      font-weight: 600;
      letter-spacing: 0.2em;
      text-transform: uppercase;
      color: #8A8A8A;
    }

    /* ── Accent bar ── */
    .accent-bar {
      height: 3px;
      background: linear-gradient(90deg, #7B1C2E 0%, #C9A84C 100%);
    }

    /* ── Body ── */
    .email-body {
      padding: 44px 48px 40px;
      background-color: #FFFFFF;
    }

    .greeting {
      font-family: 'Playfair Display', Georgia, serif;
      font-size: 22px;
      font-weight: 700;
      color: #1A1A1A;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid #EDE9E3;
      letter-spacing: -0.2px;
    }

    .content {
      font-size: 15px;
      color: #4A4A4A;
      line-height: 1.8;
      margin-bottom: 20px;
    }

    .content p { margin-bottom: 12px; }
    .content p:last-child { margin-bottom: 0; }
    .content strong { color: #1A1A1A; font-weight: 600; }
    .content a { color: #7B1C2E; text-decoration: none; font-weight: 500; }

    /* ── Highlight block ── */
    .highlight-block {
      background-color: #FAF8F5;
      border: 1px solid #E5DDD0;
      border-left: 3px solid #7B1C2E;
      padding: 22px 24px;
      margin: 24px 0;
      border-radius: 0 2px 2px 0;
    }

    .hl-title {
      font-family: 'Playfair Display', Georgia, serif;
      font-size: 15px;
      font-weight: 700;
      color: #1A1A1A;
      margin-bottom: 14px;
      letter-spacing: -0.1px;
    }

    .hl-row {
      padding: 7px 0;
      border-bottom: 1px solid #EDE9E3;
      font-size: 13.5px;
      display: flex;
      justify-content: space-between;
    }

    .hl-row:last-child { border-bottom: none; padding-bottom: 0; }
    .hl-label { color: #8A8A8A; }
    .hl-value { color: #1A1A1A; font-weight: 600; text-align: right; }
    .hl-value-gold { color: #C9A84C; font-weight: 700; }

    /* ── CTA button ── */
    .button-container { margin: 28px 0; }

    .cta-button {
      display: inline-block;
      padding: 14px 36px;
      background-color: #7B1C2E;
      color: #FFFFFF !important;
      text-decoration: none;
      border-radius: 2px;
      font-family: 'Inter', Arial, sans-serif;
      font-weight: 600;
      font-size: 13px;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    /* ── Info box ── */
    .info-box {
      background-color: #FAF8F5;
      border: 1px solid #E5DDD0;
      border-left: 3px solid #C9A84C;
      padding: 16px 20px;
      margin: 20px 0;
      border-radius: 0 2px 2px 0;
    }

    .info-box p {
      font-size: 13.5px;
      color: #4A4A4A;
      line-height: 1.75;
      margin: 0;
    }

    .info-box p + p { margin-top: 6px; }
    .info-box a { color: #7B1C2E; text-decoration: none; font-weight: 500; }

    /* ── Divider ── */
    .divider {
      height: 1px;
      background-color: #EDE9E3;
      margin: 28px 0;
      border: 0;
    }

    .body-footer-note {
      font-size: 12px;
      color: #ABABAB;
      text-align: center;
      line-height: 1.9;
    }

    .body-footer-note a { color: #7B1C2E; text-decoration: none; }

    /* ── Footer ── */
    .email-footer {
      background-color: #1A1A1A;
      padding: 28px 48px 32px;
      text-align: center;
    }

    .footer-logo {
      font-family: 'Playfair Display', Georgia, serif;
      font-size: 14px;
      font-weight: 700;
      color: #FFFFFF;
      letter-spacing: 0.06em;
      text-transform: uppercase;
      margin-bottom: 6px;
    }

    .footer-logo .dot { color: #C9A84C; }

    .footer-tagline {
      font-size: 10px;
      color: #C9A84C;
      letter-spacing: 0.2em;
      text-transform: uppercase;
      margin-bottom: 20px;
    }

    .footer-links { margin-bottom: 16px; }

    .footer-links a {
      color: #8A8A8A;
      text-decoration: none;
      margin: 0 10px;
      font-size: 11.5px;
      font-weight: 500;
    }

    .footer-divider {
      height: 1px;
      background-color: #2E2E2E;
      margin: 16px 0;
      border: 0;
    }

    .copyright {
      font-size: 11px;
      color: #5A5A5A;
      line-height: 2;
    }

    .copyright a { color: #5A5A5A; text-decoration: none; }

    /* ── Responsive ── */
    @media only screen and (max-width: 620px) {
      .email-wrapper  { padding: 0 !important; }
      .email-container {
        border-radius: 0 !important;
        border-left: 0 !important;
        border-right: 0 !important;
      }
      .email-header { padding: 24px !important; }
      .email-body   { padding: 32px 24px !important; }
      .email-footer { padding: 24px 20px !important; }
      .header-label { display: none !important; }
      .greeting     { font-size: 19px !important; }
      .cta-button   { display: block !important; text-align: center !important; }
      .hl-row       { display: block !important; }
      .hl-value     { text-align: left !important; margin-top: 2px; }
    }
  </style>
</head>
<body>
<div class="email-wrapper">
  <div class="email-container">

    <div class="email-header">
      <table width="100%" cellpadding="0" cellspacing="0">
        <tr>
          <td>
            <span class="logo-text">Pro Elite<span class="logo-dot">&bull;</span>Motors</span>
            <span class="logo-sub">Premium US Dealership</span>
          </td>
          <#if headerLabel?has_content>
          <td style="text-align:right; vertical-align:middle;">
            <span class="header-label">${headerLabel}</span>
          </td>
          </#if>
        </tr>
      </table>
    </div>

    <div class="accent-bar"></div>

    <div class="email-body">

      <#if customerName?has_content>
        <div class="greeting">Dear ${customerName},</div>
      <#else>
        <div class="greeting">Hello,</div>
      </#if>

      <#nested>

      <hr class="divider">
      <div class="body-footer-note">
        This email was sent by Pro Elite Motors LLC on behalf of your account.<br>
        Questions? Contact us at
        <a href="mailto:hello@proelitemotors.com">hello@proelitemotors.com</a>
        or call <strong>(800) 555-0142</strong>
      </div>
    </div>

    <div class="email-footer">
      <div class="footer-logo">Pro Elite<span class="dot"> &bull; </span>Motors</div>
      <div class="footer-tagline">Drive Happy. Every Single Day.</div>
      <div class="footer-links">
        <a href="https://proelitemotorsllc.com">Home</a>
        <a href="https://proelitemotorsllc.com/inventory">Inventory</a>
        <a href="https://proelitemotorsllc.com/financing">Financing</a>
        <a href="https://proelitemotorsllc.com/contact">Contact</a>
      </div>
      <hr class="footer-divider">
      <div class="copyright">
        &copy; ${.now?string('yyyy')} Pro Elite Motors LLC. All rights reserved.
        &nbsp;&middot;&nbsp; Multiple US Locations<br>
        <a href="https://proelitemotorsllc.com/privacy">Privacy Policy</a>
        &nbsp;&middot;&nbsp;
        <a href="https://proelitemotorsllc.com/contact">Support</a>
      </div>
    </div>

  </div>
</div>
</body>
</html>
</#macro>