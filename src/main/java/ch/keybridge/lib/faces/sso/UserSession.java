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
package ch.keybridge.lib.faces.sso;

import ch.keybridge.lib.faces.FacesUtil;
import ch.keybridge.lib.faces.sso.client.SSO;
import ch.keybridge.lib.faces.sso.client.SSOSOAPClient;
import ch.keybridge.lib.faces.sso.client.SSOSession;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * A Session Scoped Bean that handles findUser authentication (log in and log
 * out). *** UserSession also includes various utility methods to inspect and
 * determine findUser roles and access entitlements.
 *
 * @author Jesse Caulfield
 */
@Named(value = "userSession")
@RequestScoped
public class UserSession {

  private static final Logger LOGGER = Logger.getLogger(UserSession.class.getName());

  /**
   * The Sign in page. If the referring page is the sign-in page then the
   * browser should be bounced to the context-root.
   */
  private static final String PAGE_SIGN_IN = "sign-in.xhtml";

  /**
   * The findUser email address.
   */
  private String userName;
  /**
   * The findUser password.
   */
  private String password;
  /**
   * Boolean indicator that the findUser wishes to stay logged in.
   */
  private boolean remember;

  /**
   * Creates a new instance of UserSessionBean
   */
  public UserSession() {
  }

  //<editor-fold defaultstate="collapsed" desc="Getter and Setter">
  /**
   * Returns the remote (i.e. signed in) user name. This is typically an email
   * address.
   *
   * @return null if not signed in
   */
  public String getUserName() {
    if (getCurrentUser() != null) {
      userName = getCurrentUser();
    }
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isRemember() {
    return remember;
  }

  public void setRemember(boolean remember) {
    this.remember = remember;
  }//</editor-fold>

  /**
   * AJAX backing method when a findUser clicks the Sign In button. This
   * attempts to validate the current HTTP session with the findUser name and
   * password.
   */
  public void signIn() {
    /**
     * Developer note: DO NOT attempt to validate the session if the user is not
     * already logged in. If the user is already signed in an Error will be
     * thrown "SEVERE: Attempt to re-login while the user identity already
     * exists".
     * <p>
     * If the user is already logged in then sign them out and re-validate the
     * session.
     */
    if (FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null) {
      HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
      session.invalidate();
    }
    /**
     * Validate the session.
     */
    HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    /**
     * If the user is already signed in then redirect them to the main page
     * (e.g. the context root). getContextPath Returns the portion of the
     * request URI that indicates the context of the request. The context path
     * always comes first in a request URI. The path starts with a "/" character
     * but does not end with a "/" character. For servlets in the default (root)
     * context, this method returns "".
     */
    if (httpServletRequest.getRemoteUser() != null) {
      FacesUtil.redirectLocal("index.xhtml");
    } else {
      /**
       * Try to sign in the user.
       */
      try {
        httpServletRequest.login(userName, password);
        /**
         * If the program has reached here then the user is successfully logged
         * in.
         * <p>
         * Instantiate a SSO Manager SOAP Client. Set a browser cookie if the
         * user asked for one, touch the User record. Ignore all errors.
         */
        handleSSO();
        /**
         * If the user got here from a page then redirect them back to that
         * page. Otherwise direct them to the main content page. Since bugzee
         * allows signing from the main page check that the calling page is not
         * the main page to avoid an infinite loop of browser redirects.
         */
        String refererPage = FacesUtil.getRequestHeader("referer");
        if (refererPage != null && refererPage.contains(PAGE_SIGN_IN)) {
          /**
           * If the user signed in from the sign-in page directly then look for
           * a "referer" query parameter that may have been carried over from
           * the hidden input field. If no referer value was given then bounce
           * the user to the the main welcome page (context root).
           */
          refererPage = FacesUtil.getRequestParameter("referer");
          /**
           * Test that the referer parameter is not the sign_in page to avoid
           * infinite loops.
           */
          if (refererPage != null && !refererPage.isEmpty() && !refererPage.contains(PAGE_SIGN_IN)) {
            FacesUtil.redirect(refererPage);
          } else {
            FacesUtil.redirectLocal("index.xhtml");
          }
        } else {
          /**
           * Otherwise bounce the user back to their page of origin.
           */
          FacesUtil.redirect(refererPage);
        }
      } catch (ServletException servletException) {
        /**
         * Catch all exceptions here as HttpServletRequest throws
         * com.sun.enterprise.security.auth.login.common.LoginException if the
         * Login failed: Security Exception
         */
        FacesUtil.postMessage(FacesMessage.SEVERITY_ERROR,
                              "Sign in error",
                              "Either the user name was not recognized or the password did not match. Please try again.");
      } catch (Exception ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Internal method to interact with the SSO SOAP service. This method fails
   * gracefully for the user. A warning is logged on error.
   */
  private void handleSSO() {
    /**
     * If the program has reached here then the user is successfully logged in.
     * <p>
     * Instantiate a SSO Manager SOAP Client. Set a browser cookie if the user
     * asked for one, touch the User record. Ignore all errors.
     */
    try {
      SSO sso = SSOSOAPClient.getInstance();
      /**
       * Optionally enable SOAP logging. Surround with try/catch as
       * ResourceBundle does not fail gracefully.
       */
      try {
        if (Boolean.valueOf(ResourceBundle.getBundle(SSOSOAPClient.BUNDLE).getString("sso.enable.logging"))) {
          LOGGER.log(Level.INFO, "UserSession enabling SOAPService logging");
          SSOSOAPClient.enableLogging(sso); // log to info
        }
      } catch (Exception e) {
        /**
         * The bundle does not contain an entry for "sso.enable.logging".
         */
      }
      /**
       * Update the user's last-seen timestamp.
       */
      sso.updateLastSeen(userName);
      /**
       * Set a SSO cookie if the user has checked "Remember me". The cookie is
       * automatically recorded in the Glassfish SSO Manager upon construction
       * by the 'addCookie()' method.
       */
      if (remember) {
        String ssoUuid = sso.createSession(userName,
                                           SSOSession.encrypt(userName, password),
                                           FacesUtil.getRemoteAddr());
        FacesUtil.addCookie(SSOSession.buildCookie(ssoUuid));
      }
    } catch (Exception exception) {
      /**
       * The SSO service probably failed. Check the logs for:
       * javax.xml.ws.WebServiceException: Failed to access the WSDL at:
       * http://localhost:8080/service/sso?wsdl. It failed with:
       * http://localhost:8080/service/sso?wsdl. or Caused by:
       * java.io.FileNotFoundException: http://localhost:8080/service/sso?wsdl
       */
      LOGGER.log(Level.WARNING, "SSO {0}", exception.getMessage());
    }
  }

  /**
   * AJAX backing method when a findUser clicks the Sign OUT button. This
   * invalidates the current HTTP session.
   */
  public void signOut() {
    /**
     * Clear the SSO cookie if it is set.
     */
    Cookie cookie = FacesUtil.getCookie(SSOSession.COOKIE_NAME);
    if (cookie != null) {
      try {
        SSOSOAPClient.getInstance().clearSession(cookie.getValue());
      } catch (Exception exception) {
      }
      cookie.setMaxAge(0);
      FacesUtil.addCookie(cookie);
    }
    HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    session.invalidate();
    /**
     * Refresh the page and redirect the user to the application root.
     */
    FacesUtil.redirectLocal("index.xhtml");
  }

  /**
   * Returns true if the findUser is signed in.
   *
   * @return
   */
  public boolean isSignedIn() {
    return FacesUtil.isSignedIn();
  }

  /**
   * Determine if the current findUser is in the specified GlassFish ROLE.
   * <p>
   * GlassFish Roles are defined as an entry in the 'glassfish.glassfish_group'
   * table. Roles are assigned to findUsers by an association in the
   * 'glassfish.glassfish_role' table.
   * <p>
   * A GlassFish User may be associated with more than one GlassFish Role.
   * <p>
   * This method inspects the External FacesContext.
   *
   * @param role A GlassFish role.
   * @return True if the findUser is in the GlassFish role.
   */
  public boolean isInRole(String role) {
    return FacesUtil.isUserInRole(role);
  }

  /**
   * Shortcut to Determine if the current findUser is in the ENTERPRISE
   * GlassFish ROLE.
   *
   * @return True if the findUser is in the GlassFish role ENTERPRISE.
   */
  public boolean isEnterprise() {
    return isInRole("ENTERPRISE");
  }

  /**
   * Shortcut to Determine if the current findUser is in the ADMINISTRATOR
   * GlassFish ROLE.
   *
   * @return True if the findUser is in the GlassFish role ADMINISTRATOR.
   */
  public boolean isAdministrator() {
    return isInRole("ADMINISTRATOR");
  }

  /**
   * Returns the remote (i.e. signed in) findUser name. This is typically an
   * email address.
   *
   * @return null if not signed in
   */
  public String getCurrentUser() {
    return FacesUtil.getCurrentUser();
  }

  /**
   * Returns the NAME portion of the remote (i.e. signed in) findUser name. For
   * example: "findUser"@domain.com will return "findUser"
   *
   * @return "Guest" if not signed in.
   */
  public String getCurrentUserName() {
    if (isAdministrator()) {
      return "ADMIN";
    }
    String name = FacesUtil.getCurrentUser();
    if (name != null && name.contains("@")) {
      return name.split("@")[0];
    }
    return "Guest";
  }

  /**
   * Get the HTTP referer header parameter. This is the referring page.
   *
   * @return the HTTP "referer" parameter
   */
  public String getReferrer() {
    return FacesUtil.getRequestHeader("referer");
  }

  /**
   * Get the HTTP request URL for the current page.
   * <p>
   * Returns the part of this request URL from the protocol name up to the query
   * string in the first line of the HTTP request.
   * <p>
   * Reconstructs the URL the client used to make the request, using information
   * in the HttpServletRequest object. The returned URL contains a protocol,
   * server name, port number, and server path, but it does not include query
   * string parameters. Because this method returns a StringBuffer, not a
   * string, you can modify the URL easily, for example, to append query
   * parameters.
   *
   * @return the HTTP request URL for the current page
   */
  public String getRequestURL() {
    return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString();
  }
}
