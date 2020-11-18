/*
 * Copyright 2020 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.faces.jsf;

import ch.keybridge.faces.FacesUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Simple JSF user authentication bean.
 *
 * @author Key Bridge
 * @since v0.9.9 created 2020-11-18
 */
@Named(value = "authNBean")
@RequestScoped
public class AuthNBean {

  private static final Logger LOG = Logger.getLogger(AuthNBean.class.getName());

  /**
   * The OpenId end session page (or api). The user is redirected here after
   * sign-out.
   * <p>
   * An RP can notify the OP that the End-User has logged out of the site and
   * might want to log out of the OP as well. In this case, the RP, after having
   * logged the End-User out of the RP, redirects the End-User's User Agent to
   * the OP's logout endpoint URL.
   */
  private static final String END_SESSION = "https://keybridgewireless.com/openid/api/endsession";

  /**
   * Creates a new instance of AuthNBean
   */
  public AuthNBean() {
  }

  /**
   * JSF backing method called when a user clicks the Sign OUT button or menu
   * command.
   * <p>
   * This invalidates the current HTTP session, removes all cookies and
   * redirects the user to the Key Bridge OpenId end session end point.
   */
  public void signOut() {
    LOG.log(Level.INFO, "User sign out {0}", FacesUtil.getCurrentUser());
    /**
     * Invalidate the current HTTP session.
     */
    FacesUtil.getHttpSession().invalidate();
    /**
     * Clear the session cookies if present.
     */
    FacesUtil.clearCookies();
    /**
     * Redirect the user to the end session page. This will also terminate any
     * OpenId sessions on the server.
     */
    FacesUtil.redirect(END_SESSION);
  }

}
