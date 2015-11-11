/*
 *  Copyright (C) 2011 Caulfield IP Holdings (Caulfield) and/or its affiliates.
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
package ch.keybridge.lib.faces.validator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * UUID validator instance.
 *
 * @author Jesse Caulfield
 */
@FacesValidator("validateUUID")
public class ValidateUUID extends AValidator {

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    String uuid = value.toString().trim();
    if (uuid != null) {
      if (uuid.length() != 36) {
        throwErrorException("Invalid token",
                            "This does not appear to be a valid token. "
                            + "Security tokens are exactly 36 characters long and formated like "
                            + "'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'. "
                            + "Did you copy the token exactly as it was sent? "
                            + "Are there any extra white space characters?");
      }
    }
  }
}
