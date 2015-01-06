/*
 *  Copyright (C) 2014 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 * 
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby reserves
 *  all rights in and to Caulfield Copyrights and no license is granted under
 *  Caulfield Copyrights in this Software License Agreement. Caulfield generally
 *  licenses Caulfield Copyrights for commercialization pursuant to the terms of
 *  either Caulfield's Standard Software Source Code License Agreement or
 *  Caulfield's Standard Product License Agreement.
 * 
 *  A copy of either License Agreement can be obtained on request by email from:
 *  info@caufield.org.
 */
package org.caulfield.lib.faces.sso.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public class GlassfishSSOManagerClient implements GlassfishSSOManagerRemote {

  private WebTarget WEB_TARGET;

  /**
   * Construct a new GlassfishSSOManager REST Client.
   * <p>
   * This constructor creates a permissive HostnameVerifier to ignore the SSL
   * certificate when creating HTTPS connections.
   */
  public GlassfishSSOManagerClient() {
    /**
     * Force the client to accept HTTPS connections.
     */
    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

      @Override
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    });
    /**
     * Initialize the web target.
     */
    init();
  }

  /**
   * Initialize the client with values from the application configuration file.
   * A "sso.properties" file, located in the "{user.home}/etc/" directory or in
   * the "/usr/loca/etc/" directory.
   * <p>
   * The "sso.properties" file must contain at least one entry with the
   * "sso.host" key identifying the host (and optionally port number) of the
   * (possibly) remote GlassfishSSOManager Service. And example entry is :
   * <code>sso.host=192.168.1.10</code>
   * <p>
   * If no sso.properties file is found a development environment is assumed and
   * the sso.host is configured to the localhost on port 8080.
   */
  private void init() {
    String contextRoot = null;
    Exception e = null;
    try {
      /**
       * Assume a *NIX deployment and look in /usr/local/etc.
       */
      File propertiesFile = new File("/usr/local/etc/sso.properties");
      /**
       * If not in /usr/local/etc then look in the user home directory. Note:
       * this is the Glassfish user, so in production environments will be
       * "/var/www/etc/".
       */
      if (!propertiesFile.exists()) {
        propertiesFile = new File(System.getProperty("user.home") + File.separator + "etc/sso.properties");
      }
      /**
       * If the file 'sso.properties' does not exist then look for the file
       * 'portal.properties'.
       */
      if (!propertiesFile.exists()) {
        propertiesFile = new File("/usr/local/etc/portal.properties");
      }
      if (!propertiesFile.exists()) {
        propertiesFile = new File(System.getProperty("user.home") + File.separator + "etc/portal.properties");
      }

      /**
       * Read the properties file is present.
       */
      String ssoHost = null;
      if (propertiesFile.exists()) {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        ssoHost = properties.getProperty("sso.host");
      }
      /**
       * If no properties file was found, or if it did not contain an "sso.host"
       * entry, then assume a DEV environment and default to the localhost.
       */
      if (ssoHost == null) {
        System.err.println("GlassfishSSOManagerClient did not find a properties file. Defaulting to localhost:8080");
        ssoHost = "http://localhost:8080";
      }
      /**
       * REST IS HTTPS and SOAP is HTTP. Replace the HTTP port if present. If no
       * URI scheme is present then assume HTTPS.
       */
      if (!ssoHost.toLowerCase().startsWith("http")) {
        contextRoot = "https://" + ssoHost + "/am/rest/sso";
      } else {
        contextRoot = ssoHost + "/am/rest/sso";
      }
    } catch (IOException exception) {
      System.err.println("===================== GlassfishSSOManagerClient ERROR: GlassfishSSOManager is not configured. " + exception.getMessage());
    }
    WEB_TARGET = ClientBuilder.newClient().target(contextRoot);
//    WEB_TARGET.property(ClientProperties.CONNECT_TIMEOUT, 2000);
//    WEB_TARGET.property(ClientProperties.READ_TIMEOUT, 1000);
  }

  /**
   * Find and return the USER SSO Session corresponding to the provided
   * "JSESSIONSSO" cookie UUID. This method supports Portal sign in.
   * <p>
   * @param uuid the browser cookie UUID
   * @return the corresponding SSOSession instance, null if not found
   */
  @Override
  public GlassfishSSO findUser(String uuid) {
    try {
      return WEB_TARGET.path("user").path(uuid).request().get(GlassfishSSO.class);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Find and return the API SSO Session corresponding to the provided
   * oauth_consumer_key. This method supports API queries.
   * <p>
   * @param oauth_consumer_key the API outh consumer key
   * @return the corresponding SSOSession instance, null if not found the
   *         database
   */
  @Override
  public GlassfishSSO findOauth(String oauth_consumer_key) {
    try {
      return WEB_TARGET.path("oauth").path(oauth_consumer_key).request().get(GlassfishSSO.class);
    } catch (Exception e) {
      /**
       * If the REST service is not available (404 error) then assume positive
       * intent and allow the query. Do this by returning a valid (forged) SSO
       * response.
       */
      if (e instanceof javax.ws.rs.NotFoundException) {
        System.err.println("DEBUG GlassfishSSOManagerClient.findOauth SSO REST Service is unavailable: " + e.getMessage());
        return GlassfishSSO.getInstance(oauth_consumer_key, null, null, Arrays.asList(new String[]{"API"}));
      }
      /**
       * Unexpected ERROR. Log the error.
       */
      Logger.getLogger(GlassfishSSOManagerClient.class.getName()).log(Level.SEVERE, null, e);
      return null;
    }
  }

  /**
   * Add a user SSO Session Cookie to the current SSO Manager state. Returns a
   * GlassfishSSO session for the given user name and password. If a SSO session
   * already exists for the user then that session is REPLACED. If no session
   * exists then a new one is created with the user name and password fields.
   * <p>
   * This method is called when a user logs in from a JSF sign in page.
   * <p>
   * @param glassfishSSO the SSO Session - this is created by the User Session
   *                     manager when the user sign in.
   */
  @Override
  public void set(GlassfishSSO glassfishSSO) {
    try {
      WEB_TARGET.path("set").request().put(Entity.entity(glassfishSSO, MediaType.APPLICATION_XML));
    } catch (Exception e) {
    }
  }

  /**
   * Add a user SSO Session Cookie to the current SSO Manager state. Returns a
   * GlassfishSSO session for the given user name and password. If a SSO session
   * already exists for the user then that is returned. If no session exists
   * then a NEW session is created with the user name and password fields.
   * <p>
   * This method is called when a user logs in from a JSF sign in page.
   * <p>
   * @param username the user name
   * @param password the user password
   * @return the UUID for a SSO session for the user
   */
  @Override
  public String add(String username, String password) {
    try {
      return WEB_TARGET.path("add").path(username).path(password).request().get(String.class);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Remove the SSO Session for the indicated cookie name.
   * <p>
   * @param uuid the cookie UUID
   */
  @Override
  public void clear(String uuid) {
    try {
      WEB_TARGET.path("clear").path(uuid).request().delete();
    } catch (Exception e) {
    }
  }

  /**
   * Update the GlassfishUser "dateLastSeen" field
   * <p/>
   * @param username the user email address
   */
  @Override
  public void updateLastSeen(String username) {
    try {
      WEB_TARGET.path("touch").path(username).request().get();
    } catch (Exception e) {
    }
  }

}
