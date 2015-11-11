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

import java.util.regex.Pattern;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;

/**
 * Password validator requiring a minimum of 6 and maximum of 32 characters, one
 * upper and lower alpha and one numeric.
 * <p>
 * Developer note: This validator may be superceded with the
 * &lt;o:validateEquals&gt; widget when using OmniFaces.
 * <p>
 * This is based upon the BALUSC example.
 *
 * @see <a
 * href="http://balusc.blogspot.com/2007/12/validator-for-multiple-fields.html">Validator
 * for multiple fields</a>
 * @author jesse
 */
@FacesValidator("validatePassword")
public class ValidatePassword extends AValidator {

  /**
   * Password matching expression. Password must be at least 4 characters, no
   * more than 8 characters, and must include at least one upper case letter,
   * one lower case letter, and one numeric digit.
   */
  private static final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,32}$";

  @Override
  public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    /**
     * Cast the value of the entered password to String.
     */
    String password = (String) value;
    /**
     * Check that the value is filled in. Let required="true" do its job.
     */
    if (password == null || password.isEmpty()) {
      return; // Let required="true" do its job.
    }
    if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
      throwErrorException("Invalid password", "Password must be between six and 32 characters and include at least one upper case letter, one lower case letter and one numeric digit.");
    }
  }
}
