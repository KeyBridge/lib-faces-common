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

import javax.ejb.Remote;

/**
 * Remote EJB interface to GlassfishSSOManager.
 * <p>
 * GlassfishSSOManager is a stateless, singleton manager of ephemeral (e.g. not
 * persisted) HTTP cookies and their corresponding SSO Session credentials.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
@Remote
public interface GlassfishSSOManagerRemote {

  /**
   * Add a user SSO Session Cookie to the current SSO Manager state. Returns a
   * GlassfishSSO session for the given user name and password. If a SSO session
   * already exists for the user then that session is REPLACED. If no session
   * exists then a new one is created with the user name and password fields.
   * <p>
   * This method is called when a user logs in from a JSF sign in page.
   * <p>
   * @param glassfishSSO the SSO Session - this is created by the User Session *
   *                     manager when the user sign in.
   */
  public void set(GlassfishSSO glassfishSSO);

  /**
   * Add a user SSO Session Cookie to the current SSO Manager state. Returns a
   * GlassfishSSO session for the given user name and password. If a SSO session
   * already exists for the user then that is returned. If no session exists
   * then a NEW session is created with the user name and password fields.
   * <p>
   * This method is called when a user logs in from a JSF sign in page.
   * <p>
   * @param userName the user name
   * @param password the user password
   * @return the UUID from the SSO session for the user
   */
  public String add(String userName, String password);

  /**
   * Remove the SSO Session for the indicated cookie name.
   * <p>
   * @param uuid the cookie UUID
   */
  public void clear(String uuid);

  /**
   * Find and return the USER SSO Session corresponding to the provided
   * "JSESSIONSSO" cookie UUID. This method supports Portal sign in.
   * <p>
   * @param uuid the browser cookie UUID
   * @return the corresponding SSOSession instance, null if not found
   */
  public GlassfishSSO findUser(String uuid);

  /**
   * Find and return the API SSO Session corresponding to the provided
   * oauth_consumer_key. This method supports API queries.
   * <p>
   * @param oauth_consumer_key the API outh consumer key
   * @return the corresponding SSOSession instance, null if not found the
   *         database
   */
  public GlassfishSSO findOauth(String oauth_consumer_key);

  /**
   * Update the GlassfishUser "dateLastSeen" field
   * <p/>
   * @param username the user email address
   */
  public void updateLastSeen(String username);
}
