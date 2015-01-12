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

import javax.servlet.http.Cookie;

/**
 * A set of SSO convenience methods.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public class SSOUtil {

  /**
   * Get a Session Cookie named "AMSSO" containing the SSOSession UUID.
   * <p>
   * This method cannot be included in the SSOSession container object as http
   * Cookie is not compatible with JAXB marshalling / un-marshalling.
   * <p>
   * @param uuid the SSO Session UUID from which to build a cookie.
   * @return an HTTP version 1 cookie.
   */
  public static Cookie getCookie(String uuid) {
    Cookie cookie = new Cookie(GlassfishSSO.COOKIE_NAME, uuid);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    /**
     * Do not set the cookie domain when testing.
     */
//    cookie.setDomain(SSOSession.COOKIE_DOMAIN);
    cookie.setComment("Single sign on");
    cookie.setVersion(1);
    /**
     * Set the cookie to respond to all key bridge applications.
     */
    cookie.setPath("/");
    /**
     * (30days) x (24hrs/1day) x (60min/1hrs) x (60sec/1min) x (1000ms/1sec) =
     * 2,592,000,000 ms
     */
    cookie.setMaxAge(Integer.MAX_VALUE);
    return cookie;
  }
}
