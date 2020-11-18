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
package ch.keybridge.faces;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static java.util.regex.Pattern.quote;
import static javax.faces.FactoryFinder.APPLICATION_FACTORY;

/**
 * Collection of utility methods for the JSF API that are mainly shortcuts for
 * obtaining stuff from the thread local {@code FacesContext}.
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
   * task, you might want to consider to add a new utility method which performs
   * exactly this general task.</i>
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
   * Return the project stage for the currently running application instance.
   * The default value is ProjectStage.Production
   *
   * @return the project stage for the currently running application
   */
  public static ProjectStage getProjectStage() {
    return FacesUtil.getContext().getApplication().getProjectStage();
  }

  /**
   * Return any existing session instance associated with the current request,
   * or return null if there is no such session.
   * <p>
   * Servlet: This returns the result of calling getSession(false) on the
   * underlying <code>javax.servlet.http.HttpServletRequest</code> instance.
   *
   * @return the current HTTP Session
   */
  public static HttpSession getHttpSession() {
    return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
  }

  /**
   * Return the environment-specific object instance for the current request.
   * This is be the current request
   * <code>javax.servlet.http.HttpServletRequest</code> instance.
   *
   * @return the immediate HTTP servlet request
   */
  public static HttpServletRequest getHttpServletRequest() {
    return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
  }

  /**
   * Return the environment-specific object instance for the current response.
   * This is be the current response
   * <code>javax.servlet.http.HttpServletResponse</code> instance. It has
   * methods to access HTTP headers and cookies. The servlet container creates
   * an HttpServletResponse object and passes it as an argument to the servlet's
   * service methods (doGet, doPost, etc).
   *
   * @return the immediate HTTP servlet response
   */
  public static HttpServletResponse getHttpServletResponse() {
    return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
  }

  /**
   * Gets the JSF Application singleton from the FactoryFinder.
   * <p>
   * This method is an alternative for {@code #getApplication()} for those
   * situations where the {@code FacesContext} isn't available.
   *
   * @return The faces application singleton.
   */
  public static Application getApplicationFromFactory() {
    return ((ApplicationFactory) FactoryFinder.getFactory(APPLICATION_FACTORY)).getApplication();
  }

  /**
   * Returns the implementation information of currently loaded JSF
   * implementation. E.g. "Mojarra 2.1.7-FCS".
   * <p>
   * Gets the package for this class. The class loader of this class is used to
   * find the package. If the class was loaded by the bootstrap class loader the
   * set of packages loaded from CLASSPATH is searched to find the package of
   * the class. Null is returned if no package object was created by the class
   * loader of this class.
   * <p>
   * Packages have attributes for versions and specifications only if the
   * information was defined in the manifests that accompany the classes, and if
   * the class loader created the package instance with the attributes from the
   * manifest.
   *
   * @return The implementation information of currently loaded JSF
   *         implementation.
   * @see Package#getImplementationTitle()
   * @see Package#getImplementationVersion()
   */
  public static String getImplementationVersion() {
    Package jsfPackage = FacesContext.class.getPackage();
    return jsfPackage.getImplementationTitle() + " " + jsfPackage.getImplementationVersion();
  }

  /**
   * Determine if the current servlet request uses HTTPS (secure) protocol.
   *
   * @return TRUE if the current request uses HTTPS
   */
  public static boolean isHttps() {
    return "https".equalsIgnoreCase(getHttpServletRequest().getScheme());
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
   * Return the portion of the request URI that identifies the web application
   * context for this request. Servlet: This is the value returned by the
   * javax.servlet.http.HttpServletRequest method getContextPath().
   * <p>
   * For example, the application "gis" URL
   * "<strong>http://127.0.01:8080/gis</strong>/documentation/boundary.xhtml"
   * will return "/gis".
   *
   * @return the current application context root
   */
  public static String getContextRoot() {
    return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
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

  //<editor-fold defaultstate="collapsed" desc="JSF Messaging and Eval">
  /**
   * Post a JSF FacesMessage.
   * <p>
   * Note that the {@code  clientId} references the DESTINATION UIComponent ID,
   * not the source, and is generally a {@code messages} or {@code growl}
   * widget. This method appends a FacesMessage to the set of messages
   * associated with the specified client identifier, if clientId is not null.
   * If clientId is null the FacesMessage is assumed to not be associated with
   * any specific component instance and will be picked up by ALL message
   * rendering components..
   *
   * @param clientId The client identifier to which this message is directed (if
   *                 any)
   * @param severity The FacesMessage.Severity
   * @param summary  Localized summary message text
   * @param detail   Localized detail message text
   */
  public static void postMessage(String clientId, FacesMessage.Severity severity, String summary, String detail) {
    FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, detail));
  }

  /**
   * Post a global JSF FacesMessage.
   * <p>
   * Appends a FacesMessage to the global set of messages.
   *
   * @param severity The FacesMessage.Severity
   * @param summary  Localized summary message text
   * @param detail   Localized detail message text
   */
  public static void postMessage(FacesMessage.Severity severity, String summary, String detail) {
    postMessage(getAjaxFacesSource(), severity, summary, detail);
  }

  /**
   * Post a global JSF FacesMessage.
   * <p>
   * Appends a FacesMessage to the global set of messages.
   *
   * @param severity The FacesMessage.Severity
   * @param summary  Localized summary message text
   * @param detail   Localized detail message text
   */
  public static void postMessageGlobal(FacesMessage.Severity severity, String summary, String detail) {
    postMessage(null, severity, summary, detail);
  }

  /**
   * Evaluates the expression relative to the provided context, and returns the
   * resulting value.
   * <p>
   * The resulting value is automatically coerced to the type returned by
   * getExpectedType(), which was provided to the ExpressionFactory when this
   * expression was created.
   *
   * @param expression a JSF expression. i.e. #{link.property}
   * @return the evaluated value
   */
  public static String evaluateExpression(String expression) {
    FacesContext context = FacesContext.getCurrentInstance();
    ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
    ELContext elContext = context.getELContext();
    ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, expression, String.class);
    String result = (String) valueExpression.getValue(elContext);
    return result;
  }

  /**
   * Signals JSF that the validations phase of the current request has failed.
   * This can be invoked in any other phase than the validations phase. The
   * value can be read by {@code #isValidationFailed()} in Java and by
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
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Current user info">
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
    return InetAddressUtility.getRemoteAddr((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
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
    return InetAddressUtility.getHostName((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Request Headers and Parameters">
  /**
   * Returns the HTTP request parameter values map. This is a key / value map
   * encoded as <code>&lt;String, String[]&gt;</code>
   * <p>
   * Returns an immutable Map whose keys are the set of request parameters names
   * included in the current request, and whose values (of type String[]) are
   * all of the values for each parameter name returned by the underlying
   * request.
   *
   * @return The HTTP request parameter values map.
   * @see ExternalContext#getRequestParameterValuesMap()
   */
  public static Map<String, String[]> getRequestParameterValuesMap() {
    return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap();
  }

  /**
   *
   * Returns the HTTP request parameter values map. This is a key / value map
   * encoded as <code>&lt;String, String&gt;</code>.
   * <p>
   * Developer note: getRequestParameterMap() returns an immutable Map whose
   * keys are the set of request parameters names included in the current
   * request, and whose values (of type String) are the first (or only) value
   * for each parameter name returned by the underlying request.
   *
   * @return The HTTP request parameter values map.
   */
  public static Map<String, String> getRequestParameterMap() {
    return new TreeMap(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap());
  }

  /**
   * Get an HTTP request parameter value based upon its index (or key). Query
   * the FacesContext request parameters included in the current request.
   * <p>
   * This fetches a request value from GET URIs. For example, if the URI query
   * was "?key1=value1&amp;key2=value2" then this method would return "value2"
   * for the query key "key2".
   *
   * @param queryKey the URI query key
   * @return the corresponding URI query value
   */
  public static String getRequestParameter(String queryKey) {
    /**
     * Developer note: getRequestParameterMap() returns an immutable Map whose
     * keys are the set of request parameters names included in the current
     * request, and whose values (of type String) are the first (or only) value
     * for each parameter name returned by the underlying request.
     */
    return getRequestParameterMap().get(queryKey);
  }

  /**
   * Debugging and inspection utility to print all HTTP request parameters to
   * the console.
   *
   * @return the request parameters in a pretty print column format
   */
  public static String dumpRequestParameters() {
    StringBuilder sb = new StringBuilder();
    sb.append("--------- RequestParameters ----------------------------\n");
    for (Map.Entry<String, String> entry : FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().entrySet()) {
      sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
    }
    sb.append("--------- RequestParameters ----------------------------\n");
    return sb.toString();
  }

  /**
   * Get an HTTP request header value based upon its index (or key). Query the
   * FacesContext request parameters included in the current request.
   * <p>
   * This fetches a request value from GET URIs. For example, if the URI query
   * was "?key1=value1&amp;key2=value2" then this method would return "key2" for
   * the query key "key2".
   *
   * @return the corresponding URI query value
   */
  public static Map<String, String> getRequestHeaderMap() {
    /**
     * Developer note: getRequestHeaderMap() returns an immutable Map whose keys
     * are the set of request header names included in the current request, and
     * whose values (of type String) are the first (or only) value for each
     * parameter name returned by the underlying request.
     */
    return new TreeMap<>(FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap());
  }

  /**
   * Get an HTTP request header value based upon its index (or key). Query the
   * FacesContext request parameters included in the current request.
   * <p>
   * This fetches a request value from GET URIs. For example, if the URI query
   * was "?key1=value1&amp;key2=value2" then this method would return "key2" for
   * the query key "key2".
   *
   * @param queryKey the URI query key
   * @return the corresponding URI query value
   */
  public static String getRequestHeader(String queryKey) {
    /**
     * Developer note: getRequestHeaderMap() returns an immutable Map whose keys
     * are the set of request header names included in the current request, and
     * whose values (of type String) are the first (or only) value for each
     * parameter name returned by the underlying request.
     */
    return FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().get(queryKey);
  }

  /**
   * Shortcut to get the HTTP referer.
   * <p>
   * The HTTP referer (originally a misspelling of referrer) is an HTTP header
   * field that identifies the address of the webpage (i.e. the URI or IRI) that
   * linked to the resource being requested. By checking the referrer, the new
   * webpage can see where the request originated
   *
   * @return the HTTP referer
   */
  public static String getReferer() {
    /**
     * Sometimes the "referer" header is capitalized. Check both options.
     */
    return getRequestHeader("referer") != null ? getRequestHeader("referer") : getRequestHeader("Referer");
  }

  /**
   * Debugging and inspection utility to print all HTTP request headers to the
   * console.
   *
   * @return the request headers in a pretty print column format
   */
  public static String dumpRequestHeaders() {
    StringBuilder sb = new StringBuilder();
    sb.append("--------- RequestHeaders -------------------------------\n");
    for (Map.Entry<String, String> entry : FacesContext.getCurrentInstance().getExternalContext().getRequestHeaderMap().entrySet()) {
      sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
    }
    sb.append("--------- RequestHeaders -------------------------------\n");
    return sb.toString();
  }

  /**
   * The User-Agent request header contains a characteristic string that allows
   * the network protocol peers to identify the application type, operating
   * system, software vendor or software version of the requesting software user
   * agent.
   *
   * @return the HTTP user agent request header.
   */
  public static String getUserAgent() {
    return getRequestHeader("User-Agent");
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Browser redirection">
  /**
   * Redirect the browser by setting a relocate header parameter. This method is
   * a gentler redirection that can also set cookies. This is used for user sign
   * out.
   * <p>
   * The URI may be an absolute or relative page reference. e.g.
   * {@code index.xhtml} or {@code https://www.example.com/path/page.xhtml}
   * <p>
   * References should identify the fully qualified page name (including
   * suffix). Try to avoid directory level references if possible.
   *
   * @param pageUri the redirect URI
   */
  public static void relocate(String pageUri) {
    /**
     * Redirect a request to the specified URL, and cause the responseComplete()
     * method to be called on the FacesContext instance for the current request.
     */
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    /**
     * Status code (302) indicating that the resource reside temporarily under a
     * different URI. Since the redirection might be altered on occasion, the
     * client should continue to use the Request-URI for future requests.
     */
    externalContext.setResponseStatus(HttpServletResponse.SC_FOUND);
    externalContext.setResponseHeader(HttpHeaders.LOCATION, pageUri);
    externalContext.setResponseHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
    externalContext.setResponseHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML);
    externalContext.setResponseHeader("Clear-Site-Data", "cache");
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
   * You can use {@code String#format(String, Object...)} placeholder
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
    externalContext.setResponseHeader(HttpHeaders.LOCATION, url);
    externalContext.setResponseHeader("Connection", "close");
    FacesContext.getCurrentInstance().responseComplete();
  }

  /**
   * Redirect the browser.
   * <p>
   * URI may be an absolute or relative page reference. e.g. {@code index.xhtml}
   * or {@code https://www.example.com/path/page.xhtml}
   * <p>
   * References should identify the fully qualified page name (including
   * suffix). Try to avoid directory level references if possible.
   *
   * @param pageUri the absolute URL to which the client should be redirected
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
   * The provided {@code pageUri} field will be pre-pended with the current
   * context root and should be relative to the application context root.
   * <p>
   * The {@code pageUri} field need not start with "/" as one will be
   * conditionally added if needed. E.g. if the application context root is
   * "app" and the provide pageUri is "status.xhtml" or "/status.xhtml" the
   * browser will be forwarded to "/app/status.xhtml".
   *
   * @param pageUri the page URI within the current application. e.g.
   *                "/director/index.xhtml"
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
    if (pageUri != null) {
      sb.append(pageUri.startsWith("/") ? pageUri.replaceFirst("/", "") : pageUri);
    }
    /**
     * Do it.
     */
    redirect(sb.toString());
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Cookie monster">
  /**
   * Build and add a new HTTP Cookie configuration.
   * <p>
   * The name must conform to RFC 2109. However, vendors may provide a
   * configuration option that allows cookie names conforming to the original
   * Netscape Cookie Specification to be accepted. The name of a cookie cannot
   * be changed once the cookie has been created.
   * <p>
   * The value can be anything the server chooses to send. Its value is probably
   * of interest only to the server. The cookie's value can be changed after
   * creation with the setValue method. By default, cookies are created
   * according to the Netscape cookie specification.
   * <p>
   * Developer note: The {@code javax.servlet.http.Cookie} object is not
   * compatible with JAXB marshalling / un-marshalling.
   * <p>
   * Domain specifies the interet domain within which this cookie should be
   * presented. The form of the domain name is specified by RFC 2109. A domain
   * name begins with a dot (<code>.foo.com</code>) and means that the cookie is
   * visible to servers in a specified Domain Name System (DNS) zone (for
   * example, <code>www.foo.com</code>, but not <code>a.b.foo.com</code>). By
   * default, cookies are only returned to the server that sent them.
   *
   * @param name    the name of the cookie per RFC 2109
   * @param value   the value of the cookie.
   * @param path    the URI path for which the cookie is valid. This is the
   *                application path (i.e. context root) under which the client
   *                should return the cookie. (default is all applications: "/")
   * @param domain  the domain name within which this cookie is visible; form is
   *                according to RFC 2109. May be a domain (e.g. ".example.com"
   *                or a host (e.g. "www.example.com"). (default null) Do not
   *                set when testing. For production, specify the entire domain
   *                within which this cookie should be presented. e.g.
   *                ".example.com"
   * @param comment Specifies a comment that describes a cookie's purpose. The
   *                comment is useful if the browser presents the cookie to the
   *                user.
   * @param maxAge  the maximum age in seconds for this Cookie. A negative value
   *                means that the cookie is not stored persistently and will be
   *                deleted when the Web browser exits.
   * @param secure  specifies whether the cookie will only be sent over a secure
   *                connection.
   * @return an HTTP version 1 cookie.
   * @see <a href="https://tools.ietf.org/html/rfc6265">HTTP State Management
   * Mechanism</a>
   */
  private static Cookie buildCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure) {
    /**
     * Developer note: This method cannot be included in a Session entity object
     * as the HTTP Cookie class is not compatible with JAXB marshalling /
     * un-marshalling.
     */
    Cookie cookie = new Cookie(name, value);
    /**
     * HttpOnly cookies are not supposed to be exposed to client-side scripting
     * code, and may therefore help mitigate certain kinds of cross-site
     * scripting attacks
     */
    cookie.setHttpOnly(secure);
    /**
     * Indicates to the browser whether the cookie should only be sent using a
     * secure protocol, such as HTTPS or SSL. The default value is false.
     */
    cookie.setSecure(secure);
    /**
     * Do not set a null cookie domain value; throws NPE. Also, do not set when
     * testing. For production, specify the entire domain within which this
     * cookie should be presented. e.g. ".keybridgewireless.com"
     */
    if (domain != null) {
      cookie.setDomain(domain);
    }
    if (comment != null) {
      cookie.setComment(comment);
    }
    /**
     * Specifies a path for the cookie to which the client should return the
     * cookie. The cookie is visible to all the pages in the directory you
     * specify, and all the pages in that directory's subdirectories. If not
     * specified set the cookie to respond to all applications.
     */
    cookie.setPath(path != null ? path : "/");
    /**
     * Sets the version of the cookie protocol that this Cookie complies with.
     * Version 1 complies with RFC 2109.
     */
    cookie.setVersion(1);
    /**
     * Sets the maximum age in seconds for this Cookie. A positive value
     * indicates that the cookie will expire after that many seconds have
     * passed. Note that the value is the maximum age when the cookie will
     * expire, not the cookie's current age. A negative value means that the
     * cookie is not stored persistently and will be deleted when the Web
     * browser exits. A zero value causes the cookie to be deleted.
     */
    cookie.setMaxAge(maxAge);
    /**
     * Done.
     */
    return cookie;
  }

  /**
   * Add a session cookie to the HttpServletResponse.
   * <p>
   * The cookie is set with the current context path, version 1, and expires at
   * the end of the current session. This method can be called multiple times to
   * set more than one cookie.
   *
   * @param name  the cookie name
   * @param value the cookie value
   */
  public static void addCookie(String name, String value) {
    addCookie(name, value, getExternalContext().getRequestContextPath(), null, null, -1, isHttps());
  }

  /**
   * Add a session cookie with a root ("/") path to the HttpServletResponse.
   * <p>
   * The path for the cookie to which the client should return the cookie is set
   * to "/". The cookie is visible to all applications on this server.
   * <p>
   * This method can be called multiple times to set more than one cookie. The
   * cookie is set with version10 and expires at the end of the current session.
   *
   * @param name  the cookie name
   * @param value the cookie value
   */
  public static void addCookieGlobal(String name, String value) {
    addCookie(name, value, "/", null, null, -1, isHttps());
  }

  /**
   * Build and add a new HTTP Cookie configuration.
   * <p>
   * The name must conform to RFC 2109. However, vendors may provide a
   * configuration option that allows cookie names conforming to the original
   * Netscape Cookie Specification to be accepted. The name of a cookie cannot
   * be changed once the cookie has been created.
   * <p>
   * The value can be anything the server chooses to send. Its value is probably
   * of interest only to the server. The cookie's value can be changed after
   * creation with the setValue method. By default, cookies are created
   * according to the Netscape cookie specification.
   * <p>
   * Developer note: The {@code javax.servlet.http.Cookie} object is not
   * compatible with JAXB marshalling / un-marshalling.
   * <p>
   * Domain specifies the interet domain within which this cookie should be
   * presented. The form of the domain name is specified by RFC 2109. A domain
   * name begins with a dot (<code>.foo.com</code>) and means that the cookie is
   * visible to servers in a specified Domain Name System (DNS) zone (for
   * example, <code>www.foo.com</code>, but not <code>a.b.foo.com</code>). By
   * default, cookies are only returned to the server that sent them.
   *
   * @param name    the name of the cookie per RFC 2109
   * @param value   the value of the cookie.
   * @param path    the URI path for which the cookie is valid. This is the
   *                application path (i.e. context root) under which the client
   *                should return the cookie. (default is all applications: "/")
   * @param domain  the domain name within which this cookie is visible; form is
   *                according to RFC 2109. May be a domain (e.g. ".example.com"
   *                or a host (e.g. "www.example.com"). (default null) Do not
   *                set when testing. For production, specify the entire domain
   *                within which this cookie should be presented. e.g.
   *                ".example.com"
   * @param comment Specifies a comment that describes a cookie's purpose. The
   *                comment is useful if the browser presents the cookie to the
   *                user.
   * @param maxAge  the maximum age in seconds for this Cookie. A negative value
   *                means that the cookie is not stored persistently and will be
   *                deleted when the Web browser exits.
   * @param secure  specifies whether the cookie will only be sent over a secure
   *                connection.
   * @see <a href="https://tools.ietf.org/html/rfc6265">HTTP State Management
   * Mechanism</a>
   */
  public static void addCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure) {
    addCookie(buildCookie(name, value, path, domain, comment, maxAge, secure));
  }

  /**
   * Add a cookie to the HttpServletResponse. This method can be called multiple
   * times to set more than one cookie.
   *
   * @param cookie the cookie to add to the response.
   */
  public static void addCookie(Cookie cookie) {
    if (cookie != null) {
      ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse()).addCookie(cookie);
    }
  }

  /**
   * Add a session cookie to the HttpServletResponse.
   * <p>
   * The cookie is set with the current context path, version 1, and expires at
   * the end of the current session. This method can be called multiple times to
   * set more than one cookie.
   *
   * @param name the cookie name
   */
  public static void removeCookie(String name) {
    addCookie(name, null, getExternalContext().getRequestContextPath(), null, null, -1, isHttps());
  }

  /**
   * Add a session cookie to the HttpServletResponse.
   * <p>
   * The cookie is set with the current context path, version 1, and expires at
   * the end of the current session. This method can be called multiple times to
   * set more than one cookie.
   *
   * @param name the cookie name
   */
  public static void removeCookieGlobal(String name) {
    addCookie(name, null, "/", null, null, -1, isHttps());
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
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Server Side Ajax">
  /**
   * Get the {@code ClientID} of the page component that triggered the current
   * AJAX action. This typically returns the JSF ID of the command widget
   * (button or link) that was clicked.
   * <p>
   * This method returns the HTTP POST parameter "javax.faces.source", which
   * generally corresponds to the desired component. When in doubt is it
   * generally best to reference the Client ID manually.
   *
   * @return the HTTP POST parameter "javax.faces.source"
   */
  public static String getAjaxFacesSource() {
    return FacesUtil.getExternalContext().getRequestParameterMap().get("javax.faces.source");
  }

  /**
   * Update the entire view.
   *
   * @see PartialViewContext#setRenderAll(boolean)
   * @since 1.5
   */
  public static void ajaxUpdateAll() {
    FacesUtil.getContext().getPartialViewContext().setRenderAll(true);
  }

  /**
   * Update the given client IDs in the current ajax response.
   * <p>
   * Note that those client IDs should not start with the naming container
   * separator character like <code>:</code>. This method also supports the
   * client ID keywords <code>@all</code>, <code>@form</code> and
   * <code>@this</code> which respectively refers the entire view, the currently
   * submitted form as obtained by {@code Components#getCurrentForm()} and the
   * currently processed component as obtained by
   * {@code UIComponent#getCurrentComponent(FacesContext)}. Any other client ID
   * starting with <code>@</code> is by design ignored, including
   * <code>@none</code>.
   * <p>
   * This utility class an easy way of programmatically (from inside a managed
   * bean method) specifying new client IDs which should be ajax-updated, also
   * {@code UIData} rows or columns on specific index, specifying callback
   * scripts which should be executed on complete of the Ajax response and
   * adding arguments to the JavaScript scope. The added arguments are during
   * the "on complete" phase as a JSON object available by
   * <code>OmniFaces.Ajax.data</code> in JavaScript context.
   * <p>
   * The JSON object is been encoded by {@code Json#encode(Object)} which
   * supports standard Java types {@code Boolean}, {@code Number},
   * {@code CharSequence} and {@code Date} arrays, {@code Collection}s and
   * {@code Map}s of them and as last resort it will use the
   * {@code Introspector} to examine it as a Javabean and encode it like a
   * {@code Map}.
   * <p>
   * Note that {@code #updateRow(UIData, int)} and
   * {@code #updateColumn(UIData, int)} can only update cell content when it has
   * been wrapped in some container component with a fixed ID.
   *
   * @param clientIds The client IDs to be updated in the current ajax response.
   * @see PartialViewContext#getRenderIds()
   */
  public static void ajaxUpdate(String... clientIds) {
    /**
     * Get the current partial view context (the ajax context).
     */
    PartialViewContext context = FacesUtil.getContext().getPartialViewContext();
    Collection<String> renderIds = context.getRenderIds();

    for (String clientId : clientIds) {
      if (clientId.charAt(0) != '@') {
        renderIds.add(clientId);
      } else if (clientId.equals("@all")) {
        context.setRenderAll(true);
      } else if (clientId.equals("@form")) {
        UIComponent currentForm = getCurrentForm();

        if (currentForm != null) {
          renderIds.add(currentForm.getClientId());
        }
      } else if (clientId.equals("@this")) {
        UIComponent currentComponent = getCurrentComponent();

        if (currentComponent != null) {
          renderIds.add(currentComponent.getClientId());
        }
      }
    }
  }

  /**
   * Returns the current UI component from the EL context.
   *
   * @return The current UI component from the EL context.
   * @see UIComponent#getCurrentComponent(FacesContext)
   */
  private static UIComponent getCurrentComponent() {
    return UIComponent.getCurrentComponent(FacesUtil.getContext());
  }

  /**
   * Returns the currently submitted UI form component, or <code>null</code> if
   * there is none, which may happen when the current request is not a postback
   * request at all, or when the view has been changed by for example a
   * successful navigation. If the latter is the case, you'd better invoke this
   * method before navigation.
   *
   * @return The currently submitted UI form component.
   * @see UIForm#isSubmitted()
   */
  private static UIForm getCurrentForm() {
    FacesContext context = FacesContext.getCurrentInstance();
    if (!context.isPostback()) {
      return null;
    }
    UIViewRoot viewRoot = context.getViewRoot();
    /**
     * The initial implementation has visited the tree for UIForm components
     * which returns true on isSubmitted(). But with testing it turns out to
     * return false on ajax requests where the form is not included in execute!
     * The current implementation just walks through the request parameter map
     * instead.
     */
    for (String name : context.getExternalContext().getRequestParameterMap().keySet()) {
      if (name.startsWith("javax.faces.")) {
        continue; // Quick skip.
      }
      UIComponent component = findComponentIgnoringIAE(viewRoot, stripIterationIndexFromClientId(name));
      if (component instanceof UIForm) {
        return (UIForm) component;
      } else if (component != null) {
        UIForm form = getClosestParent(component, UIForm.class);
        if (form != null) {
          return form;
        }
      }
    }
    return null;
  }

  /**
   * Strip UIData/UIRepeat iteration index in pattern <code>:[0-9+]:</code> from
   * given component client ID.
   */
  private static String stripIterationIndexFromClientId(String clientId) {
    String separatorChar = Character.toString(UINamingContainer.getSeparatorChar(FacesUtil.getContext()));
    return clientId.replaceAll(quote(separatorChar) + "[0-9]+" + quote(separatorChar), separatorChar);
  }

  /**
   * Use {@code UIViewRoot#findComponent(String)} and ignore the potential
   * {@code IllegalArgumentException} by returning null instead.
   */
  private static UIComponent findComponentIgnoringIAE(UIViewRoot viewRoot, String clientId) {
    try {
      return viewRoot.findComponent(clientId);
    } catch (IllegalArgumentException ignore) {
      return null; // May occur when view has changed by for example a successful navigation.
    }
  }

  /**
   * Returns from the given component the closest parent of the given parent
   * type, or <code>null</code> if none is found.
   *
   * @param <C>        The generic component type.
   * @param component  The component to return the closest parent of the given
   *                   parent type for.
   * @param parentType The parent type.
   * @return From the given component the closest parent of the given parent
   *         type, or <code>null</code> if none is found.
   */
  private static <C extends UIComponent> C getClosestParent(UIComponent component, Class<C> parentType) {
    UIComponent parent = component.getParent();
    while (parent != null && !parentType.isInstance(parent)) {
      parent = parent.getParent();
    }
    return parentType.cast(parent);
  }//</editor-fold>

}
