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
import java.lang.reflect.Method;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import org.caulfield.lib.faces.sso.client.SSO;

/**
 * A DynamicFilter that assigns the OAuthContainerFilter to all REST methods not
 * annotated with PermitAll. If configured in the annotation the RequiredRoles
 * are passed to the OAuthContainerFilter.
 * <p>
 * Usage: To add OAuth filtering to a Web Application extend this abstract class
 * and add the following annotation to your implementation:
 * <code>@Provider @Priority(Priorities.AUTHORIZATION)</code>
 * <p>
 * This implementation is based upon the Jersey RolesAllowedDynamicFeature
 * class, which provides very basic role analysis, to match RolesAllowed entries
 * with the roles assigned to an OAuth SSO Session.
 * <p>
 * Provider
 * <p>
 * By default, i.e. if no name binding is applied to the filter implementation
 * class, the filter instance is applied globally, however only after the
 * incoming request has been matched to a particular resource by JAX-RS runtime.
 * If there is a @NameBinding annotation applied to the filter, the filter will
 * also be executed at the post-match request extension point, but only in case
 * the matched resource or sub-resource method is bound to the same name-binding
 * annotation.
 * <p>
 * In case the filter should be applied at the pre-match extension point, i.e.
 * before any request matching has been performed by JAX-RS runtime, the filter
 * MUST be annotated with a @PreMatching annotation.
 * <p>
 * Use a pre-match request filter to update the input to the JAX-RS matching
 * algorithm, e.g., the HTTP method, Accept header, return cached responses etc.
 * Otherwise, the use of a request filter invoked at the post-match request
 * extension point (after a successful resource method matching) is recommended.
 * <p>
 * Filters implementing this interface must be annotated with @Provider to be
 * discovered by the JAX-RS runtime. Container request filter instances may also
 * be discovered and bound dynamically to particular resource methods.
 * <p>
 * DynamicFeature:
 * <p>
 * A JAX-RS meta-provider for dynamic registration of post-matching providers
 * during a JAX-RS application setup at deployment time.
 * <p>
 * Dynamic feature is used by JAX-RS runtime to register providers that shall be
 * applied to a particular resource class and method and overrides any
 * annotation-based binding definitions defined on any registered resource
 * filter or interceptor instance.
 * <p>
 * A JAX-RS meta-provider for dynamic registration of post-matching providers
 * during a JAX-RS application setup at deployment time. Dynamic feature is used
 * by JAX-RS runtime to register providers that shall be applied to a particular
 * resource class and method and overrides any annotation-based binding
 * definitions defined on any registered resource filter or interceptor
 * instance.
 * <p>
 * Providers implementing this interface MAY be annotated with @Provider
 * annotation in order to be discovered by JAX-RS runtime when scanning for
 * resources and providers. This provider types is supported only as part of the
 * Server API.
 * <p>
 * @see <a
 * href="https://jersey.java.net/apidocs/2.6/jersey/org/glassfish/jersey/server/filter/RolesAllowedDynamicFeature.html">RolesAllowedDynamicFeature</a>
 * @see <a
 * href="https://github.com/jersey/jersey/blob/master/core-server/src/main/java/org/glassfish/jersey/server/filter/RolesAllowedDynamicFeature.java">RolesAllowedDynamicFeature.java</a>
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public abstract class AOAuthDynamicFeature implements DynamicFeature {

  @SuppressWarnings("ProtectedField")
  protected SSO sso;

  @Override
  @SuppressWarnings("unchecked")
  public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
    Method method = resourceInfo.getResourceMethod();
    /**
     * DenyAll on the method take precedence over RolesAllowed and PermitAll on
     * the class. DenyAll cannot be attached to the class.
     */
    if (method.isAnnotationPresent(DenyAll.class)) {
      featureContext.register(new ContainerRequestFilter() {

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
          throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
      });
      return;
    }

    RolesAllowed rolesAllowed = method.getAnnotation(RolesAllowed.class);
    /**
     * RolesAllowed on the method takes precedence over PermitAll on the class.
     */
    if (rolesAllowed != null) {
      featureContext.register(new OAuthContainerFilter(sso, rolesAllowed.value()));
      return;
    }

    /**
     * PermitAll on the method takes precedence over DenyAll and RolesAllowed on
     * the class.
     */
    if (method.isAnnotationPresent(PermitAll.class)) {
      /**
       * NO OP
       */
      return;
    }
    /**
     * DenyAll cannot be attached to classes.
     * <p>
     * RolesAllowed on the class with no superseding method annotation.
     * <p>
     * RolesAllowed on the class takes precedence over PermitAll
     */
    rolesAllowed = resourceInfo.getResourceClass().getAnnotation(RolesAllowed.class);
    if (rolesAllowed != null) {
      featureContext.register(new OAuthContainerFilter(sso, rolesAllowed.value()));
    }
    /**
     * PermitAll on the class. NOOP.
     */
  }
}
