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
package org.caulfield.lib.faces.sso;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.caulfield.lib.faces.sso.client.GlassfishSSO;
import org.caulfield.lib.faces.sso.client.GlassfishSSOManagerClient;
import org.caulfield.lib.faces.sso.client.GlassfishSSOManager;
import org.caulfield.lib.faces.sso.client.SSOUtil;
import org.caulfield.lib.faces.util.FacesUtil;

/**
 * A Session Scoped Bean that handles findUser authentication (log in and log
 * out). *** UserSession also includes various utility methods to inspect and
 * determine findUser roles and access entitlements.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
@Named(value = "userSession")
@RequestScoped
public class UserSession {

  private static final long serialVersionUID = 1L;

  /**
   * The Sign in page. If the referring page is the sign-in page then the
   * browser should be bounced to the context-root.
   */
  private static final String PAGE_SIGN_IN = "sign-in.xhtml";
  /**
   * The Glassfish Group required to access resources available from auto-login.
   */
  private static final String ROLE_COMMUNITY = "COMMUNITY";
  /**
   * The Glassfish Group required to access resources available from auto-login
   * for ENTERPRISE.
   */
  private static final String ROLE_ENTERPRISE = "ENTERPRISE";

//  @Inject  private PropertiesBean propertiesBean;
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
   * <p/>
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
   * AJAX method to automatically log in a findUser after the page loads based
   * upon the presence of an SSO cookie.
   * <p>
   * This method runs with the 'PORTAL' role.
   * <p>
   * Developer note: To use this method the web.xml must be configured to
   * recognize the required role. At minimum, web.xml must recognize the PORTAL
   * role.
   */
  public void autoRun() {
    autoRun(ROLE_COMMUNITY);
  }

  /**
   * AJAX method to automatically log in a findUser after the page loads based
   * upon the presence of an SSO cookie.
   * <p>
   * Developer note: To use this method the web.xml must be configured to
   * recognize the required role. At minimum, web.xml must recognize the PORTAL
   * role.
   * <p>
   * @param role the required user role for this resource. Defaults to 'PORTAL'
   *             if null or empty.
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  public void autoRun(String role) {
    HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    /**
     * Try to validate the session using an SSO cookie.S
     */
    if (httpServletRequest.getRemoteUser() == null) {
      /**
       * The user is not logged in. Look for a Cookie. If the cookie exists then
       * use it to try validating the session.
       */
      Cookie cookie = FacesUtil.getCookie(GlassfishSSO.COOKIE_NAME);
      if (cookie != null) {
        /**
         * Instantiate a GlassFish SSO Manager REST Client.
         */
        GlassfishSSOManager ssoManager = new GlassfishSSOManagerClient();
        /**
         * Try to get the SSO Session from the ephemeral GlassfishSSOManager
         * user cache and use the session information to log the user in.
         */
        GlassfishSSO sso = ssoManager.findUser(cookie.getValue());
        if (sso != null) {
          try {
            /**
             * Try to log in the user. If successful then update the user date
             * last seen.
             */
            httpServletRequest.login(sso.getUserName(), sso.getPassword());
            ssoManager.updateLastSeen(sso.getUserName());
            /**
             * Confirm that the user has valid ROLE privileges to view this
             * resource. If the user is not in the required role then invalidate
             * the session.
             * <p>
             * If no role was declared then at minimum require the PORTAL role.
             */
            if (role == null || role.isEmpty()) {
              role = ROLE_COMMUNITY;
            }
            if (!httpServletRequest.isUserInRole(role)) {
              httpServletRequest.logout();
            } else {
              /**
               * At this point the user is signed in and a member of the
               * required role. Post a welcome message.
               */
              FacesUtil.postMessage(FacesMessage.SEVERITY_INFO, "Welcome back", sso.getUserName());
            }
          } catch (ServletException servletException) {
            System.err.println("ERROR UserSession autoRun login error: " + servletException.getMessage());
          }
        }
      }
    }
  }

  /**
   * AJAX backing method when a findUser clicks the Sign In button. This
   * attempts to validate the current HTTP session with the findUser name and
   * password.
   */
  public void signIn() {
    /**
     * Developer note: NO NOT attempt to validate the session if the user is not
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
    if (httpServletRequest.getRemoteUser() == null) {
      try {
        httpServletRequest.login(userName, password);
        /**
         * If the program has reached here then the use is successfully logged
         * in. Instantiate a Glassfish SSO Manager REST Client. Touch the
         * Glassfish User record. Ignore all errors.
         */
        GlassfishSSOManager ssoManager = new GlassfishSSOManagerClient();
        ssoManager.updateLastSeen(userName);
        /**
         * Set a SSO cookie if the user has checked "Remember me". The cookie is
         * automatically recorded in the Glassfish SSO Manager.
         */
        if (remember) {
          String ssoUuid = ssoManager.add(userName, password);
          FacesUtil.addCookie(SSOUtil.getCookie(ssoUuid));
        }
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
                              "Either the email address was not recognized or the password did not match. Please try again.");
      }
    } else {
      /**
       * Return to the context root. getContextPath Returns the portion of the
       * request URI that indicates the context of the request. The context path
       * always comes first in a request URI. The path starts with a "/"
       * character but does not end with a "/" character. For servlets in the
       * default (root) context, this method returns "".
       */
      FacesUtil.redirectLocal("index.xhtml");
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
    Cookie cookie = FacesUtil.getCookie(GlassfishSSO.COOKIE_NAME);
    if (cookie != null) {
      new GlassfishSSOManagerClient().clear(cookie.getValue());
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
   * <p/>
   * @return
   */
  public boolean isSignedIn() {
    return FacesUtil.isSignedIn();
  }

  /**
   * Determine if the current findUser is in the specified GlassFish ROLE.
   * <p/>
   * GlassFish Roles are defined as an entry in the 'glassfish.glassfish_group'
   * table. Roles are assigned to findUsers by an association in the
   * 'glassfish.glassfish_role' table.
   * <p/>
   * A GlassFish User may be associated with more than one GlassFish Role.
   * <p/>
   * This method inspects the External FacesContext.
   * <p/>
   * @param role A GlassFish role.
   * @return True if the findUser is in the GlassFish role.
   */
  public boolean isInRole(String role) {
    return FacesUtil.isUserInRole(role);
  }

  /**
   * Shortcut to Determine if the current findUser is in the ENTERPRISE
   * GlassFish ROLE.
   * <p>
   * @return True if the findUser is in the GlassFish role ENTERPRISE.
   */
  public boolean isEnterprise() {
    return isInRole("ENTERPRISE");
  }

  /**
   * Shortcut to Determine if the current findUser is in the ADMINISTRATOR
   * GlassFish ROLE.
   * <p>
   * @return True if the findUser is in the GlassFish role ADMINISTRATOR.
   */
  public boolean isAdministrator() {
    return isInRole("ADMINISTRATOR");
  }

  /**
   * Returns the remote (i.e. signed in) findUser name. This is typically an
   * email address.
   * <p/>
   * @return null if not signed in
   */
  public String getCurrentUser() {
    return FacesUtil.getCurrentUser();
  }

  /**
   * Returns the NAME portion of the remote (i.e. signed in) findUser name. For
   * example: "findUser"@domain.com will return "findUser"
   * <p/>
   * @return "Guest" if not signed in.
   */
  public String getCurrentUserName() {
    String name = FacesUtil.getCurrentUser();
    if (name != null && name.contains("@")) {
      return name.split("@")[0];
    }
    return "Guest";
  }

  /**
   * Get the HTTP referer header parameter. This is the referring page.
   * <p>
   * @return the HTTP "referer" parameter
   */
  public String getReferrer() {
    return FacesUtil.getRequestHeader("referer");
  }

  /**
   * Get the HTTP request URL for the current page.
   * <p>
   * @return the HTTP request URL for the current page
   */
  public String getRequestURL() {
    /**
     * Returns the part of this request URL from the protocol name up to the
     * query string in the first line of the HTTP request.
     * <p>
     * Reconstructs the URL the client used to make the request, using
     * information in the HttpServletRequest object. The returned URL contains a
     * protocol, server name, port number, and server path, but it does not
     * include query string parameters. Because this method returns a
     * StringBuffer, not a string, you can modify the URL easily, for example,
     * to append query parameters.
     */
    return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString();
  }
}
