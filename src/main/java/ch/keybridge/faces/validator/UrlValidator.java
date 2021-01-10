/*
 * Copyright 2019 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.faces.validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.net.ssl.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * HTTP / HTTPS URL link validator. Validates an HTTP URL to determine if the
 * referenced end point is publicly available. This validator also updates the
 * input component CSS to include a BS-4 status indicator.
 * <p>
 * Note: Since v4.1.0 this uses a trusting client; for HTTPS links this does NOT
 * validate the server certificate.
 *
 * @author Key Bridge
 * @since v0.6.0 created 01/27/19
 * @since v4.1.0 use a trusting client to accept all SSL certificates
 * @since v5.0.0 append "http://" if scheme is missing
 * @since v5.0.11 rewrite 2020-12-05 to first validate the URL format, then test
 * link availability
 */
public class UrlValidator extends AbstractValidator {

  private static final Logger LOG = Logger.getLogger(UrlValidator.class.getName());

  /**
   * A fake user agent string, to ensure the query is not intercepted by a robot
   * interceptor.
   */
  private static final String MOZILLA = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";

  /**
   * {@inheritDoc}
   * <p>
   * Validates an HTTP link to determine if the link is publicly available.
   *
   * @param value an HTTP(S) link reference
   * @throws ValidatorException if the link cannot be reached
   */
  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (value == null) {
      return;
    }
    String urlText = ((String) value).trim();
    if (urlText.isEmpty()) {
      return;
    }
    /**
     * Use a trusting client to ignore credential errors.
     */
    try {
      URL url = new URL(urlText);
      boolean validityStatus = testLinkValidity(url);
      setValidityStatus(component, validityStatus); // update the jsf component
      if (!validityStatus) {
        throwErrorException("Not available", "This resource could not be verified");
      }
    } catch (MalformedURLException ex) {
      setValidityStatus(component, false);
      throwErrorException("Invalid URL", "This does not appear to be a valid URL format");
    }
  }

  /**
   * Test whether a link is available or not. Uses the HEAD method to just
   * verify that the resource is available.
   *
   * @param url the link URL
   * @return TRUE if the resource is available for download
   */
  public boolean testLinkValidity(final URL url) {
    /**
     * Use a trusting client to ignore credential errors.
     */
    try {
      Response response = trustingClient()
        .target(url.toURI())
        .request()
        .header(HttpHeaders.USER_AGENT, MOZILLA)
        .head();
      LOG.log(Level.FINEST, "debug testLinkValidity {0} retrieved OK", url);
      return response != null;
    } catch (Exception exception) {
      /**
       * Conditionally try again without HTTPS.
       */
      try {
        URI uri = URI.create(url.toString());
        if (uri.getScheme().equalsIgnoreCase("https")) {
          URL httpUrl = new URL("http", url.getHost(), url.getPort(), url.getFile());
          Response response = trustingClient()
            .target(httpUrl.toURI())
            .request()
            .header(HttpHeaders.USER_AGENT, MOZILLA)
            .head();
          return response != null;
        }
      } catch (Exception exception2) {
        LOG.log(Level.INFO, "Link {0} is not available. Also tried http.", url);
        return false;
      }
    }
    return false;
  }

  /**
   * From org.glassfish.jersey.client.ClientProperties
   */
  private static final String READ_TIMEOUT = "jersey.config.client.readTimeout";
  /**
   * From org.glassfish.jersey.client.ClientProperties
   */
  private static final String CONNECT_TIMEOUT = "jersey.config.client.connectTimeout";

  /**
   * 1,000 milliseconds = 1 seconds.
   * <p>
   * Connect timeout interval, in milliseconds. The value MUST be an instance
   * convertible to Integer. A value of zero (0) is equivalent to an interval of
   * infinity. The default value is infinity (0).
   */
  private static final int TIMEOUT_CONNECT = 1000;
  /**
   * 1,000 milliseconds = 1 seconds.
   * <p>
   * Read timeout interval, in milliseconds. The value MUST be an instance
   * convertible to Integer. A value of zero (0) is equivalent to an interval of
   * infinity. The default value is infinity (0).
   */
  private static final int TIMEOUT_READ = 1000;

  /**
   * Build a trusting HTTPS client. Uses TLSv1.2.
   *
   * @return @throws Exception
   */
  protected Client trustingClient() throws Exception {
    SSLContext sc = SSLContext.getInstance("TLSv1.2");  //Java 8   // NoSuchAlgorithmException
    System.setProperty("https.protocols", "TLSv1.2");   //Java 8
    TrustManager[] trustAllCerts = {new InsecureTrustManager()};
    sc.init(null, trustAllCerts, new java.security.SecureRandom()); // KeyManagementException
    HostnameVerifier allHostsValid = (String string, SSLSession ssls) -> true;
    Client client = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
    client.property(CONNECT_TIMEOUT, TIMEOUT_CONNECT); // should immediately connect
    client.property(READ_TIMEOUT, TIMEOUT_READ); // wait for processing
    return client;
  }

  /**
   * Manage which X509 certificates may be used to authenticate the remote side
   * of a secure socket
   */
  private class InsecureTrustManager implements X509TrustManager {

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
      // Everyone is trusted!
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
      // Everyone is trusted!
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }
  }
}
