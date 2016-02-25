/*
 *  Copyright (C) 2011 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 *
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *  reserves all rights in and to Caulfield Copyrights and no license is
 *  granted under Caulfield Copyrights in this Software License Agreement.
 *  Caulfield generally licenses Caulfield Copyrights for commercialization
 *  pursuant to the terms of either Caulfield's Standard Software Source Code
 *  License Agreement or Caulfield's Standard Product License Agreement.
 *  A copy of Caulfield's either License Agreement can be obtained on request
 *  by email from: info@caufield.org.
 */
package ch.keybridge.lib.faces;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.faces.FactoryFinder;
import static javax.faces.FactoryFinder.APPLICATION_FACTORY;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Collection of utility methods for the JSF API that are mainly shortcuts for
 * obtaining stuff from the thread local {@link FacesContext}.
 * <p>
 * Some methods forked from Omnifaces v2.2; some homegrown.
 *
 * @author Jesse Caulfield
 * @since 1.0.0
 */
public class FacesUtil {

  /**
   * Returns the current faces context.
   * <p>
   * <i>Note that whenever you absolutely need this method to perform a general
   * task, you might want to consider to submit a feature request to OmniFaces
   * in order to add a new utility method which performs exactly this general
   * task.</i>
   *
   * @return The current faces context.
   * @see FacesContext#getCurrentInstance()
   */
  public static FacesContext getContext() {
    return FacesContext.getCurrentInstance();
  }

  /**
   * Returns the faces context that's stored in an ELContext.
   * <p>
   * Note that this only works for an ELContext that is created in the context
   * of JSF.
   *
   * @param elContext the EL context to obtain the faces context from.
   * @return the faces context that's stored in the given ELContext.
   * @since 1.2
   */
  public static FacesContext getContext(ELContext elContext) {
    return (FacesContext) elContext.getContext(FacesContext.class);
  }

  /**
   * Returns the current external context.
   * <p>
   * <i>Note that whenever you absolutely need this method to perform a general
   * task, you might want to consider to submit a feature request to OmniFaces
   * in order to add a new utility method which performs exactly this general
   * task.</i>
   *
   * @return The current external context.
   * @see FacesContext#getExternalContext()
   */
  public static ExternalContext getExternalContext() {
    return getContext().getExternalContext();
  }

  /**
   * Gets the JSF Application singleton from the FactoryFinder.
   * <p>
   * This method is an alternative for {@link Faces#getApplication()} for those
   * situations where the {@link FacesContext} isn't available.
   *
   * @return The faces application singleton.
   */
  public static Application getApplicationFromFactory() {
    return ((ApplicationFactory) FactoryFinder.getFactory(APPLICATION_FACTORY)).getApplication();
  }

  /**
   * Returns the implementation information of currently loaded JSF
   * implementation. E.g. "Mojarra 2.1.7-FCS".
   *
   * @return The implementation information of currently loaded JSF
   *         implementation.
   * @see Package#getImplementationTitle()
   * @see Package#getImplementationVersion()
   */
  public static String getImplInfo() {
    Package jsfPackage = FacesContext.class.getPackage();
    return jsfPackage.getImplementationTitle() + " " + jsfPackage.getImplementationVersion();
  }

  /**
   * Signals JSF that the validations phase of the current request has failed.
   * This can be invoked in any other phase than the validations phase. The
   * value can be read by {@link #isValidationFailed()} in Java and by
   * <code>#{facesContext.validationFailed}</code> in EL.
   *
   * @see FacesContext#validationFailed()
   */
  public static void validationFailed() {
    getContext().validationFailed();
  }

  /**
   * Returns whether the validations phase of the current request has failed.
   *
   * @return <code>true</code> if the validations phase of the current request
   *         has failed, otherwise <code>false</code>.
   * @see FacesContext#isValidationFailed()
   */
  public static boolean isValidationFailed() {
    return getContext().isValidationFailed();
  }

  /**
   * Post a JSF FacesMessage.
   *
   * @param severity The FacesMessage.Severity
   * @param summary  The short summary message label
   * @param detail   The message detail
   */
  public static void postMessage(FacesMessage.Severity severity, String summary, String detail) {
    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
  }

  /**
   * Sends a permanent (301) redirect to the given URL.
   * <p>
   * If the given URL does <b>not</b> start with <code>http://</code>,
   * <code>https://</code> or <code>/</code>, then the request context path will
   * be prepended, otherwise it will be the unmodified redirect URL. So, when
   * redirecting to another page in the same web application, always specify the
   * full path from the context root on (which in turn does not need to start
   * with <code>/</code>).
   * <pre>
   * Faces.redirectPermanent("other.xhtml");
   * </pre>
   * <p>
   * You can use {@link String#format(String, Object...)} placeholder
   * <code>%s</code> in the redirect URL to represent placeholders for any
   * request parameter values which needs to be URL-encoded. Here's a concrete
   * example:
   * <pre>
   * Faces.redirectPermanent("other.xhtml?foo=%s&amp;bar=%s", foo, bar);
   * </pre>
   * <p>
   * This method does by design not work on ajax requests. It is not possible to
   * return a "permanent redirect" via JSF ajax XML response.
   *
   * @param url         The URL to redirect the current response to.
   * @param paramValues The request parameter values which you'd like to put
   *                    URL-encoded in the given URL.
   * @throws NullPointerException When url is <code>null</code>.
   * @see ExternalContext#setResponseStatus(int)
   * @see ExternalContext#setResponseHeader(String, String)
   */
  public static void redirectPermanent(String url, String... paramValues) {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.getFlash().setRedirect(true); // MyFaces also requires this for a redirect in current request (which is incorrect).
    externalContext.setResponseStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
    externalContext.setResponseHeader("Location", url);
    externalContext.setResponseHeader("Connection", "close");
    FacesContext.getCurrentInstance().responseComplete();
  }

  /**
   * Redirect the browser.
   * <p>
   * URI may be an absolute or relative page reference. Absolute references
   * should include the WAR application name, whereas relative reference need
   * not.
   * <p>
   * All page references should identify the fully qualified page name
   * (including suffix).
   *
   * @param pageUri e.g. index.xhtml
   */
  public static void redirect(String pageUri) {
    try {
      /**
       * Redirect a request to the specified URL, and cause the
       * responseComplete() method to be called on the FacesContext instance for
       * the current request.
       */
      ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
      externalContext.getFlash().setRedirect(true); // MyFaces also requires this for a redirect in current request (which is incorrect).
      externalContext.redirect(pageUri);
//      FacesContext.getCurrentInstance().getExternalContext().redirect(pageUri);
      /**
       * Send a temporary redirect response to the client using the specified
       * redirect location URL and clears the buffer. The buffer will be
       * replaced with the data set by this method. Calling this method sets the
       * status code to SC_FOUND 302 (Found). This method can accept relative
       * URLs;the servlet container must convert the relative URL to an absolute
       * URL before sending the response to the client. If the location is
       * relative without a leading '/' the container interprets it as relative
       * to the current request URI.
       */
      /**
       * Signal the JavaServer Faces implementation that the HTTP response for
       * this request has already been generated (such as an HTTP redirect), and
       * that the request processing lifecycle should be terminated as soon as
       * the current phase is completed.
       */
//      FacesContext.getCurrentInstance().responseComplete();
    } catch (IOException ex) {
      Logger.getLogger(FacesUtil.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Redirect the browser to a page within the current application.
   * <p>
   * URIS may be relative or absolute PAGE references but are pre-pended with
   * the current context root.
   * <p>
   * Page references with no path are assumed to be in the context root.
   * <p>
   * E.g. if the application is "app" and the pageUri is "status.xhtml" or
   * "/status.xhtml" the browser will be forwarded to "/app/status.xhtml".
   *
   * @param pageUri e.g. index.xhtml
   */
  public static void redirectLocal(String pageUri) {
    /**
     * The context path always comes first in a request URI. The path starts
     * with a "/" character but does not end with a "/" character. For servlets
     * in the default (root) context, this method returns "".
     */
    String contextpath = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getContextPath();
    /**
     * Start with the context path with a suffix slash "/".
     */
    StringBuilder sb = new StringBuilder(contextpath.isEmpty() ? "/" : contextpath + "/");
    /**
     * Append the page, stripping a prefix slash "/" if present to avoid double
     * slashing.
     */
    sb.append(pageUri.startsWith("/") ? pageUri.replaceFirst("/", "") : pageUri);
    /**
     * Do it.
     */
    redirect(sb.toString());
  }

  /**
   * Returns the HTTP request parameter values map.
   *
   * @return The HTTP request parameter values map.
   * @see ExternalContext#getRequestParameterValuesMap()
   */
  public static Map<String, String[]> getRequestParameterValuesMap() {
    return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
  }

  /**
   * Get an HTTP request parameter value based upon its index (or key). Query
   * the FacesContext request parameters included in the current request.
   * <p>
   * This fetches a request value from GET URIs. For example, if the URI query
   * was "?key1=value1&key2=value2" then this method would return "value2" for
   * the query key "key2".
   *
   * @param queryKey the URI query key
   * @return the corresponding URI query value
   */
  public static String getRequestParameter(String queryKey) {
    /**
     * Developer note: getRequestParameterMap() returns an immutable Map whose
     * keys are the set of request parameters names included in the current
     * request, and whose values (of type String) are the first (or only) value
     * for each parameter name returned by the underlying request. The returned
     * Map must implement the entire contract for an unmodifiable map as
     * described in the JavaDocs for java.util.Map.
     */
    return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(queryKey);
  }

  /**
   * Debugging and inspection utility to print all HTTP request parameters to
   * the console.
   */
  public static void dumpRequestParameters() {
    System.out.println("--------- RequestParameters ----------------------------");
    for (Map.Entry<String, String> entry : FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().entrySet()) {
      System.out.println(String.format("%50s : %s", entry.getKey(), entry.getValue()));
    }
    System.out.println("--------- RequestParameters ----------------------------");
  }

  /**
   * Get an HTTP request header value based upon its index (or key). Query the
   * FacesContext request parameters included in the current request.
   * <p>
   * This fetches a request value from GET URIs. For example, if the URI query
   * was "?key1=value1&key2=value2" then this method would return "value2" for
   * the query key "key2".
   *
   * @param queryKey the URI query key
   * @return the corresponding URI query value
   */
  public static String getRequestHeader(String queryKey) {
    /**
     * Developer note: getRequestParameterMap() returns an immutable Map whose
     * keys are the set of request header names included in the current request,
     * and whose values (of type String) are the first (or only) value for each
     * parameter name returned by the underlying request. The returned Map must
     * implement the entire contract for an unmodifiable map as described in the
     * JavaDocs for java.util.Map.
     */
    return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get(queryKey);
  }

  /**
   * Debugging and inspection utility to print all HTTP request headers to the
   * console.
   */
  public static void dumpRequestHeaders() {
    System.out.println("--------- RequestHeaders -------------------------------");
    for (Map.Entry<String, String> entry : FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().entrySet()) {
      System.out.println(String.format("%50s : %s", entry.getKey(), entry.getValue()));
    }
    System.out.println("--------- RequestHeaders -------------------------------");
  }

  /**
   * Add a SSO cookie to the HttpServletResponse. This method can be called
   * multiple times to set more than one cookie.
   *
   * @param cookie the cookie to add to the response.
   */
  public static void addCookie(Cookie cookie) {
    if (cookie != null) {
      ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse()).addCookie(cookie);
    }
  }

  /**
   * Scan the array of all of the Cookie objects the client sent with this
   * request and return the one with the corresponding name.
   *
   * @param cookieName the cookie name
   * @return the cookie with the corresponding name
   */
  public static Cookie getCookie(String cookieName) {
    try {
      for (Cookie cookie : ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getCookies()) {
        if (cookie.getName().equals(cookieName)) {
          return cookie;
        }
      }
    } catch (NullPointerException e) {
    }
    return null;
  }

  /**
   * If a corresponding cookie is found then set its max age to zero. This will
   * cause the browser to delete the cookie from its cache.
   *
   * @param cookieName the cookie name.
   */
  public static void clearCookie(String cookieName) {
    Cookie cookie = getCookie(cookieName);
    if (cookie != null) {
      cookie.setMaxAge(0);
      addCookie(cookie);
    }
  }

  /**
   * Get the requesting page URI. Returns the part of this request's URL from
   * the protocol name up to the query string in the first line of the HTTP
   * request. The web container does not decode this String. For example: First
   * line of HTTP request
   * <p>
   * For example: GET <code>http://foo.bar/a.html</code> HTTP/1.0 RETURNS
   * <code>/a.html</code>
   *
   * @return the requesting page URI
   */
  public static String getRequestURI() {
    return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURI();
  }

  /**
   * Get the current application context path. This returns the URI prefix up to
   * and including the current application context root.
   * <p>
   * The PORT number is noted ONLY if it is not :80 or :443.
   * <p>
   * For example, the application "gis" URL
   * "<strong>http://127.0.01:8080/gis</strong>/documentation/boundary.xhtml"
   * will return "http://127.0.01:8080/gis".
   *
   * @return the current application context path
   */
  public static String getContextPath() {
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
    return new StringBuilder()
            .append(context.getRequestScheme())
            .append("://")
            .append(context.getRequestServerName())
            .append((context.getRequestServerPort() != 80 && context.getRequestServerPort() != 443)
                    ? ":" + context.getRequestServerPort()
                    : "")
            .append(context.getRequestContextPath())
            .toString();
  }

  /**
   * Returns true if the user is signed in.
   *
   * @return TRUE if the current user is signed in
   */
  public static boolean isSignedIn() {
    return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser() != null;
  }

  /**
   * Determine if the current user is in the specified GlassFish ROLE. Returns
   * true if the currently authenticated user is included in the specified role.
   * Otherwise, returns false.
   * <p>
   * GlassFish Roles are defined as an entry in the
   * {@code glassfish_realm.groups} table. Roles are assigned to users by an
   * association in the {@code glassfish_realm.role} table.
   * <p>
   * A GlassFish User may be associated with more than one GlassFish Role.
   * <p>
   * This method inspects the External FacesContext.
   *
   * @param role A GlassFish role.
   * @return True if the user is in the GlassFish role.
   */
  public static boolean isUserInRole(String role) {
    return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
  }

  /**
   * Returns the remote (i.e. signed in) user name. This is typically an email
   * address.
   *
   * @return null if not signed in
   */
  public static String getCurrentUser() {
    return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
  }

  /**
   * Returns the v4 Internet Protocol (IP) address of the client or last proxy
   * that sent the request. For HTTP servlets, same as the value of the CGI
   * variable REMOTE_ADDR.
   * <p>
   * Developer note: If the remote IP address is a private IP address (e.g.
   * starts with "10." or "192.") then this method assumes the application is
   * behind a load balancer and looks for the "X-Forwarded-For" request
   * parameter.
   * <p>
   * The X-Forwarded-For request header helps you identify the IP address of a
   * client. Because load balancers intercept traffic between clients and
   * servers, your server access logs contain only the IP address of the load
   * balancer. To see the IP address of the client, use the X-Forwarded-For
   * request header.
   *
   * @return a String containing the IP address of the client that sent the
   *         request
   */
  public static String getRemoteAddr() {
    return InetAddressUtility.getAddressFromRequest((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
  }

  /**
   * Returns the fully qualified name of the client or the last proxy that sent
   * the request. If the engine cannot or chooses not to resolve the hostname
   * (to improve performance), this method returns the dotted-string form of the
   * IP address. For HTTP servlets, same as the value of the CGI variable
   * REMOTE_HOST.
   *
   * @return a String containing the fully qualified name of the client
   */
  public static String getRemoteHost() {
    return InetAddressUtility.getHostnameFromRequest((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
  }

  /**
   * Return the value of the specified application initialization parameter (if
   * any).
   * <p>
   * Servlet: This must be the result of the javax.servlet.ServletContext method
   * getInitParameter(name). It is valid to call this method during application
   * startup or shutdown. If called during application startup or shutdown, this
   * method calls through to the actual container context to return the init
   * parameter value.
   *
   * @param name Name of the requested initialization parameter
   * @return the value of the specified application initialization parameter (if
   *         any).
   */
  public static String getInitPrameter(String name) {
    return FacesContext.getCurrentInstance().getExternalContext().getInitParameter(name);
  }

  /**
   * Sends an error response to the client using the specified status code and
   * clears the buffer. The server will preserve cookies and may clear or update
   * any headers needed to serve the error page as a valid response. If an
   * error-page declaration has been made for the web application corresponding
   * to the status code passed in, it will be served back the error page.
   * <p>
   * If the response has already been committed, this method throws an
   * IllegalStateException.
   * <p>
   * After using this method, the response should be considered to be committed
   * and should not be written to.
   *
   * @param statusCode the error status code. Suggest using HttpServletResponse
   *                   enumerated codes.
   */
  public static void sendError(int statusCode) {
    try {
      ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse()).sendError(statusCode);
      FacesContext.getCurrentInstance().responseComplete();
    } catch (IOException iOException) {
    }
  }

}
