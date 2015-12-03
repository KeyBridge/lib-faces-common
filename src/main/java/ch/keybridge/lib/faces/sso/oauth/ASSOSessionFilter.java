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
package ch.keybridge.lib.faces.sso.oauth;

import ch.keybridge.lib.faces.sso.client.SSO;
import ch.keybridge.lib.faces.sso.client.SSOCookie;
import ch.keybridge.lib.faces.sso.client.SSOSOAPClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Extend this class and add WebFilter annotation to implement a complete web
 * filter. e.g.
 * <p>
 * <code>@WebFilter({"/index.xhtml", "/new_api_key.xhtml"})</code>
 * <p>
 * or <code>@WebFilter(filterName = "SSOSessionFilter",
 * urlPatterns = {"/*"},
 * initParams = {\@WebInitParam(name = "role", value = "PORTAL")})</code>
 * <p>
 * This is an Abstract Container filter that looks for the presence of a
 * logged-in user or a JSESSIONSSO cookie in the HTTP request and attempts to
 * automatically log the user in.
 * <p>
 * Usage: This employs a SOAP client to query the Key Bridge Access Manager. The
 * portal.properties file must identify the SSO host WSDL.
 * <p>
 * This filter is processed by the container at deployment time, and is applied
 * to the specified URL patterns, servlets, and dispatcher types.
 *
 * @author Jesse Caulfield
 */
public abstract class ASSOSessionFilter implements Filter {

  private static final Logger logger = Logger.getLogger(ASSOSessionFilter.class.getName());

  /**
   * The WebServiceRef annotation is used to define a reference to a web service
   * and (optionally) an injection target for it. It can be used to inject both
   * service and proxy instances.
   */
  private SSO sso;

  /**
   * Called by the web container to indicate to a filter that it is being placed
   * into service.
   * <p>
   * The servlet container calls the init method exactly once after
   * instantiating the filter. The init method must complete successfully before
   * the filter is asked to do any filtering work.
   * <p>
   * The web container cannot place the filter into service if the init method
   * either (1) Throws a ServletException or (2) Does not return within a time
   * period defined by the web container
   *
   * @param filterConfig
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    /**
     * If you have any <init-param> in web.xml, then you could get them here by
     * config.getInitParameter("name") and assign it as field.
     * <p>
     * Developer note: Only use REST client. The EJB is typically not available,
     * and trying to link when it is causes EJB / JSF lifecycle timing issues.
     */
    if (sso == null) {
      try {
        this.sso = SSOSOAPClient.getInstance();
      } catch (Exception ex) {
        throw new ServletException(ex.getMessage());
      }
      logger.log(Level.INFO, "ASSOSessionFilter initialize SOAP client");
    }
  }

  /**
   * Called by the web container to indicate to a filter that it is being taken
   * out of service.
   * <p>
   * This method is only called once all threads within the filter's doFilter
   * method have exited or after a timeout period has passed. After the web
   * container calls this method, it will not call the doFilter method again on
   * this instance of the filter.
   * <p>
   * This method gives the filter an opportunity to clean up any resources that
   * are being held (for example, memory, file handles, threads) and make sure
   * that any persistent state is synchronized with the filter's current state
   * in memory.
   */
  @Override
  public void destroy() {
    /**
     * If you have assigned any expensive resources as field of this Filter
     * class, then you could clean/close them here.
     */
  }

  /**
   * The doFilter method of the Filter is called by the container each time a
   * request/response pair is passed through the chain due to a client request
   * for a resource at the end of the chain. The FilterChain passed in to this
   * method allows the Filter to pass on the request and response to the next
   * entity in the chain.
   * <p>
   * A typical implementation of this method would follow the following pattern:
   * <ol>
   * <li>Examine the request</li>
   * <li>Optionally wrap the request object with a custom implementation to
   * filter content or headers for input filtering</li>
   * <li>Optionally wrap the response object with a custom implementation to
   * filter content or headers for output filtering</li>
   * <li> <ul>
   * <li>Either invoke the next entity in the chain using the FilterChain object
   * (chain.doFilter()),</li>
   * <li>or not pass on the request/response pair to the next entity in the
   * filter chain to block the request processing </li>
   * </ul></ul>
   * <li>Directly set headers on the response after invocation of the next
   * entity in the filter chain. </li>
   * </ol>
   *
   * @param request
   * @param response
   * @param chain
   * @throws IOException
   * @throws ServletException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (excludeFromFilter(httpServletRequest)) {
      chain.doFilter(request, response);
      return;
    }

    /**
     * If the HttpServletRequest does not have a Remote User but there are
     * cookies to inspect then try to automatically validated the session.
     * <p>
     * If the user is not in the required role this sends an error response to
     * the client and clears the buffer: Status code (403) indicating the server
     * understood the request but refused to fulfill it.
     */
    if (httpServletRequest.getRemoteUser() == null) {
      /**
       * The user is not logged in. Look for a Cookie. If the cookie exists then
       * use it to try validating the session.
       */
      Cookie cookie = findCookieByName(SSOCookie.COOKIE_NAME, httpServletRequest.getCookies());
      if (cookie != null) {
        /**
         * Try to get the SSO Session from the ephemeral GlassfishSSOManager
         * user cache and use the session information to log the user in.
         */
        SSOCookie ssoCookie = sso.findCookieUser(cookie.getValue());
        if (ssoCookie != null) {
          try {
            /**
             * Try to log in the user. If successful then update the user date
             * last seen.
             */
            httpServletRequest.login(ssoCookie.getUserName(), ssoCookie.getPassword());
            sso.updateLastSeen(ssoCookie.getUserName());
            /**
             * At this point the user is signed in.
             * <p>
             * Note that this has no bearing regarding sufficient privileges to
             * view the resource.
             */
            logger.log(Level.INFO, "SSOSessionFilter signed in {0}", ssoCookie.getUserName());
          } catch (ServletException servletException) {
            /**
             * Login failed. Has the user changed their password?
             */
          }
        }
      }
    }
    /**
     * Keep the FilterChain party going.
     */
    chain.doFilter(request, response);
  }

  /**
   * Exclude certain content from this filter. This exclusion inspects the
   * servlet path and excludes all resources.
   *
   * @param httpServletRequest the httpServletRequest object
   * @return TRUE if the path starts with javax.faces.resource
   */
  private boolean excludeFromFilter(HttpServletRequest httpServletRequest) {
    return httpServletRequest.getServletPath().startsWith("/javax.faces.resource");
  }

  /**
   * Internal helper method to scan an array of Cookies and identify the one
   * cookie with the matching name.
   *
   * @param cookieName the cookie name to identify
   * @param cookies    an array of cookies.
   * @return the matching cookie, null if not found.
   */
  private Cookie findCookieByName(String cookieName, Cookie[] cookies) {
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookieName.equals(cookie.getName())) {
          return cookie;
        }
      }
    }
    return null;
  }
}
