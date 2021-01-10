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
package ch.keybridge.faces.validator;

import java.net.MalformedURLException;
import java.net.URL;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * Simple JSF component to validate that an input value is a correct and
 * complete URL format.
 *
 * @author Key Bridge
 * @since v5.0.11 created 2020-12-05
 */
public class UrlFormatValidator extends AbstractValidator {

  /**
   * {@inheritDoc} Validates that an input string is a proper URL format. This
   * tests that the string can be converted in to URL.
   */
  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    if (value == null) {
      return;
    }
    if (value instanceof String) {
      String text = (String) value;
      if (text.trim().isEmpty()) {
        unsetValidityStatus(component);
        return;
      }
      try {
        URL url = new URL(text);
        setValidityStatus(component, true);
      } catch (MalformedURLException ex) {
        setValidityStatus(component, false);
        throwErrorException("Invalid URL", "This does not appear to be a valid URL format");
      }
    }
  }

}
