/*
 *  Copyright (C) 2012 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 *
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *  reserves all rights in and to Caulfield Copyrights and no license is
 *  granted under Caulfield Copyrights in this Software License Agreement.
 *  Caulfield generally licenses Caulfield Copyrights for commercialization
 *  pursuant to the terms of either Caulfield's Standard Software Source Code
 *  License Agreement or Caulfield's Standard Product License Agreement.
 *  A copy of Caulfield's either License Agreement can be obtained on request
 *  by email from: info@caufield.org.
 */
package org.caulfield.lib.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Email address validator. This uses the JavaMail API to confirm the email
 * conforms to RFC 822 syntax rules.
 * <p>
 * @author jesse
 */
@FacesValidator("validateEmailAddress")
public class ValidateEmailAddress extends AValidator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    isValidEmailAddress(String.valueOf(value));
  }

  /**
   * Use the JavaMail API to confirm the email conforms to RFC 822 syntax rules.
   * <p/>
   * @param emailAddress the email address to validate
   * @return TRUE if the email address conforms with the syntax rules of RFC 822
   */
  public boolean isValidEmailAddress(String emailAddress) {
    try {
      InternetAddress internetAddress = new InternetAddress(emailAddress);
      internetAddress.validate();
      return true;
    } catch (AddressException ex) {
      throwErrorException("Invalid Email address",
                          emailAddress + " does not appear to be a valid email address");
    }
    return false;
  }
}
