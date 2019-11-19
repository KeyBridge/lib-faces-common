/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package ch.keybridge.faces.validator;

import javax.faces.validator.ValidatorException;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class EmailValidatorTest {

  public EmailValidatorTest() {
  }

  @Test
  public void testEmailValidator() {

    EmailValidator validator = new EmailValidator();
    for (String email : new String[]{"\"Fred Bloggs\"@example.com", "user@.invalid.com", "Chuck Norris <gmail@chucknorris.com>", "webmaster@müller.de", "matteo@78.47.122.114"}) {

      try {
        validator.validate(null, null, email);
        System.out.println("  " + email + " is valid ");
      } catch (ValidatorException validatorException) {
        System.out.println("  " + email + " is NOT valid ");
      }

    }

  }
}
