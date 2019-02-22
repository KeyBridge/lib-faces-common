/*
 * Copyright 2019 Key Bridge. All rights reserved. Use is subject to license
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
package ch.keybridge.lib.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * HTTP link validator. Validates an HTTP link to determine if the link is
 * publicly available.
 * <p>
 * This validator also updates the input component CSS to include a BS-4 status
 * indicator.
 *
 * @author Key Bridge
 * @since v0.6.0 created 01/27/19
 */
@FacesValidator("linkValidator")
public class LinkValidator extends AbstractValidator {

  /**
   * A fake user agent string, to ensure the query is not intercepted by a robot
   * interceptor.
   */
  private static final String MOZILLA = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";

  /**
   * {@inheritDoc}
   * <p>
   * Validates an HTTP link to determine if the link is publicly available.
   *
   * @param value an HTTP(S) link reference
   * @throws ValidatorException if the link cannot be reached
   */
  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (value == null) {
      return;
    }
    String url = (String) value;
    if (url.isEmpty()) {
      return;
    }
    try {
      Response response = ClientBuilder.newClient().target((String) value).request()
        .header(HttpHeaders.USER_AGENT, MOZILLA)
        .get();
    } catch (Exception e) {
      setValidityStatus(component, false);
      throwErrorException("Not available", "This resource is not available. " + e.getMessage());
    }
  }
}
