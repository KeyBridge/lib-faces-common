/*
 * Copyright 2015 Caulfield IP Holdings (Caulfield) and affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by copyright. Caulfield hereby
 * reserves all rights and copyrights and no license is
 * granted under said copyrights in this Software License Agreement.
 * Caulfield generally licenses software for commercialization
 * pursuant to the terms of either a Standard Software Source Code
 * License Agreement or a Standard Product License Agreement.
 * A copy of these agreements may be obtained by sending a request
 * via email to info@caufield.org.
 */
package ch.keybridge.lib.faces.validator;

import javax.faces.validator.ValidatorException;
import junit.framework.TestCase;

/**
 *
 * @author Key Bridge LLC
 */
public class ValidatePasswordTest extends TestCase {

  public ValidatePasswordTest(String testName) {
    super(testName);
  }

  public void testValidator() {
    System.out.println("Test Password Validator ");

    ValidatePassword p = new ValidatePassword();

    for (String string : new String[]{"abcd", "aBc45DSD_sdf", "password", "afv", "1234", "reallylon)*()*09809809:LJLKJLKJLKJLKJLKJgpassword", "1agdA*$#", "1agdA*$#", "@12X*567", "1#Zv96g@*Yfasd4", "#67jhgt@erd"}) {
      try {
        p.validate(null, null, string);
        System.out.println("  Valid password\t" + string);
      } catch (ValidatorException validatorException) {
        System.err.println("  " + validatorException.getMessage() + "\t" + string);
      }
    }
  }

}
