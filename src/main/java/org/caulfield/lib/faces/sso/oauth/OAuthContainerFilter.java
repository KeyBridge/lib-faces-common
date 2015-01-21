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

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import org.caulfield.lib.faces.sso.client.SSO;
import org.caulfield.lib.faces.sso.client.SSOCookie;

/**
 * A ContainerRequestFilter instance providing OAuth Web Service Validation.
 * <p>
 * Implements 2-legged OAuth authentication for WEB Service calls using the
 * public and sharedSecret key fields stored in the the securityParameter table.
 * <p>
 * Usage: This ContainerRequestFilter is dynamically registered from within the
 * AOAuthDynamicFeature class to REST classes and methods from a DynamicFeature
 * provider.
 * <p>
 * To add OAUTH authentication extend the AOAuthDynamicFeature and add the
 * following annotation to the new class (e.g. OAuthDynamicFeature in your REST
 * package): <code>@Provider @Priority(Priorities.AUTHORIZATION)</code>
 * <p>
 * Configuration: OAuthContainerFilter is a SOAP client to the
 * GlassfishSSOManager Service. The (possibly) remote service host must be
 * provided in the constructor and must contain a host name or IP address
 * (optionally plus port number).
 * <p>
 * Developer note re: Annotations
 * <p>
 * Priority(Priorities.AUTHENTICATION): The Priority annotation can be applied
 * to classes to indicate in what order the classes should be used. The effect
 * of using the Priority annotation in any particular instance is defined by
 * other specifications that define the use of a specific class.
 * <p>
 * The @Provider annotation is used for anything that is of interest to the
 * JAX-RS runtime.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
@Priority(Priorities.AUTHORIZATION)
public class OAuthContainerFilter implements ContainerRequestFilter {

  /**
   * A GlassfishSSOManager client instance. Depending upon the server
   * configuration this may be an EJB or a REST client.
   */
  private final SSO sso;

  /**
   * An injectable class to access the resource class and resource method
   * matched by the current request.
   */
  private final Set<String> rolesAllowed;

  /**
   * Construct a new OAuthContainerFilter. This ContainerRequestFilter
   * implementation is dynamically registered by the DynamicFeatureFilter based
   * upon the REST method annotation. Any REST method not having the PermitAll
   * annotation are registered as requiring OAUTH.
   * <p>
   * The constructor tries to load the host (and optionally port number) running
   * the GlassfishSSOManager Service from the sso.properties file. See
   * {@link #postContruct()} for details.
   * <p>
   * @param sso          a Glassfish SSO Manager instance - this may be either
   *                     an EJB, REST or SOAP client, depending upon the server
   *                     configuration.
   * @param rolesAllowed a non-null array containing one or more roles
   *                     configured in the RolesAllowed annotation.
   */
  public OAuthContainerFilter(SSO sso, Collection<String> rolesAllowed) {
    this.sso = sso;
    this.rolesAllowed = rolesAllowed != null ? new HashSet<>(rolesAllowed) : new HashSet<String>();
  }

  /**
   * Construct a new OAuthContainerFilter. This ContainerRequestFilter
   * implementation is dynamically registered by the DynamicFeatureFilter based
   * upon the REST method annotation. Any REST method not having the PermitAll
   * annotation are registered as requiring OAUTH.
   * <p>
   * The constructor tries to load the host (and optionally port number) running
   * the GlassfishSSOManager Service from the sso.properties file. See
   * {@link #postContruct()} for details.
   * <p>
   * @param sso          a Glassfish SSO Manager instance - this may be either
   *                     an EJB, REST or SOAP client, depending upon the server
   *                     configuration.
   * @param rolesAllowed a non-null array containing one or more roles
   *                     configured in the RolesAllowed annotation.
   */
  public OAuthContainerFilter(SSO sso, String[] rolesAllowed) {
    this.sso = sso;
    this.rolesAllowed = rolesAllowed != null ? new HashSet<>(Arrays.asList(rolesAllowed)) : new HashSet<String>();
  }

  /**
   * Filter the REST request.
   * <p>
   * @param requestContext the Container Request Context
   * @throws IOException is OAUTH validation fails, shows UNAUTHORIZED
   */
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    /**
     * If the GlassfishSSOManager is not available then there is an error on the
     * Key Bridge system. Allow the request but log an error.
     */
    if (sso == null) {
      System.err.println("OAuthContainerFilter ERROR: SSO is not available. ");
      return;
    }
    /**
     * Validate all requests using 2-legged OAuth. If successful then set the
     * container Request SecurityContext, which enables REST methods to apply
     * role-based security to their individual methods.
     * <p>
     * If the header has an authorization entry then parse it to identify the
     * consumer key. Use the oauth_consumer_key to look up a Glassfish SSO
     * Session.
     */
    if (requestContext.getHeaderString("authorization") != null) {
      SSOCookie ssoCookie = sso.findCookieOauth(oauthMap(requestContext.getHeaderString("authorization")).get("oauth_consumer_key"));
      if (ssoCookie != null) {
        requestContext.setSecurityContext(new OAuthSecurityContext(ssoCookie,
                                                                   requestContext.getSecurityContext().isSecure()));
      }
    }
    /**
     * See if the (new) security context matches any of the required Roles.
     */
    for (String role : rolesAllowed) {
      if (requestContext.getSecurityContext().isUserInRole(role)) {
        return;
      }
    }
    /**
     * Either the header did not have an authorization entry OR no credential
     * record corresponding to the oauth_consumer_key was found or the OAUTH
     * credential does not match the method RolesAllowed annotation.
     */
    throw new ForbiddenException();
//    throw new WebApplicationException(Response.Status.UNAUTHORIZED);
  }

  /**
   * Scan the authorization header value and parse it into a HashMap.
   * <p>
   * The resulting HashMap (OAuth) keys are: findOauth_signature,
   * findOauth_nonce, findOauth_consumer_key, findOauth_signature_method,
   * findOauth_timestamp
   * <p>
   * @param authorization the HTTP Request header authorization value
   * @return a non-null HashMap instance
   */
  private Map<String, String> oauthMap(String authorization) {
    Map<String, String> oauthMap = new HashMap<>();
    for (String token : authorization.split(", ")) {
      /**
       * Developer note: oauth_signature may contain percent characters, and
       * oauth_consumer_key may contain dash characters.
       */
      Pattern p = Pattern.compile("(\\w+)=\"([-%\\w]+)\"");
      Matcher m = p.matcher(token);
      if (m.find()) {
        oauthMap.put(m.group(1), m.group(2));
      }
    }
    return oauthMap;
  }
}
