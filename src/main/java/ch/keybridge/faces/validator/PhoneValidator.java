/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any.
 * Key Bridge reserves all rights in and to Copyrights and
 * no license is granted under Copyrights in this Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 *
 * All information contained herein is the property of {project.organization!user}
 * and its suppliers, if any. The intellectual and technical concepts contained
 * herein are proprietary.
 */
package ch.keybridge.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * A (very) basic telephone number JSF validator.
 * <p>
 * Most all phone numbers contain at _least_ seven digits. This validator simply
 * checks the digit count and rejects candidates having less than seven digits.
 * The validator makes no further evaluation beyond digit count.
 * <p>
 * The International Telecommunication Union (ITU) has established a
 * comprehensive numbering plan, designated E.164, for uniform network
 * interoperability. It is an open numbering plan with a maximum length of 15
 * digits.
 *
 * @author Key Bridge
 * @since v2.8.0 created 12/13/17
 */
public class PhoneValidator extends AbstractValidator {

  private static final int REQUIRED_DIGITS = 7;

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    /**
     * Strip all non-digit characters then count the digits by measuring the
     * String length.
     */
    if (String.valueOf(value).replaceAll("\\D", "").length() < REQUIRED_DIGITS) {
      setValidityStatus(component, false);
      throwErrorException("Invalid number", "The phone number does not match any recognized dialing sequence.");
    }
    setValidityStatus(component, true);
  }

}
