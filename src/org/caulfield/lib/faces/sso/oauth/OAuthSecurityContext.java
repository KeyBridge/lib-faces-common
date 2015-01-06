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
package org.caulfield.lib.faces.sso.oauth;

import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import static javax.ws.rs.core.SecurityContext.CLIENT_CERT_AUTH;
import org.caulfield.lib.faces.sso.client.GlassfishSSO;

/**
 * Security Context Implementation to support OAuth that provides access to
 * security related information.
 * <p/>
 * javax.ws.rs.core.SecurityContext is an injectable interface that provides
 * access to security related information.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public class OAuthSecurityContext implements SecurityContext {

  /**
   * The current OAuth SSO Session.
   */
  private final GlassfishSSO sso;

  /**
   * Boolean indicating whether this request was made using a secure channel,
   * such as HTTPS.
   */
  private final boolean secure;

  public OAuthSecurityContext(GlassfishSSO session, boolean secure) {
    this.sso = session;
    this.secure = secure;
  }

  /**
   * Returns a java.security.Principal object containing the name of the current
   * authenticated user. For API users this is the oauth_consumer_key
   * <p/>
   * @return a java.security.Principal containing the oauth_consumer_key of the
   *         client application making this request;
   */
  @Override
  public Principal getUserPrincipal() {
    return new Principal() {
      @Override
      public String getName() {
        return sso.getUserName();
      }
    };
  }

  /**
   * Returns a boolean indicating whether the authenticated user is included in
   * the specified logical "role".
   * <p/>
   * @param securityRole the required security ROLE identified in the portal
   *                     web.xml file.
   * @return a boolean indicating whether the user making the request belongs to
   *         a given role.
   */
  @Override
  public boolean isUserInRole(String securityRole) {
    return sso.isInRole(securityRole);
  }

  /**
   * Returns a boolean indicating whether this request was made using a secure
   * channel, such as HTTPS.
   * <p/>
   * @return true if the request was made using a secure channel, false
   *         otherwise
   */
  @Override
  public boolean isSecure() {
    return secure;
  }

  /**
   * Returns the string value of the authentication scheme used to protect the
   * resource. The return options are one of the static members BASIC_AUTH,
   * FORM_AUTH, CLIENT_CERT_AUTH, DIGEST_AUTH (suitable for == comparison) or
   * the container-specific string indicating the authentication scheme, or null
   * if the request was not authenticated.
   * <p/>
   * @return By default if the resource is not authenticated, null is returned.
   *         This implementation always returns 'CLIENT_CERT_AUTH'.
   */
  @Override
  public String getAuthenticationScheme() {
    return CLIENT_CERT_AUTH;
  }
}
