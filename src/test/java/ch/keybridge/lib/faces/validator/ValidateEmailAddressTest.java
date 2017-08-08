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
package ch.keybridge.lib.faces.validator;

import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class ValidateEmailAddressTest {

  public ValidateEmailAddressTest() {
  }

  @Test
  public void testEmailValidator() {

    ValidateEmailAddress validator = new ValidateEmailAddress();
    for (String email : new String[]{"\"Fred Bloggs\"@example.com", "user@.invalid.com", "Chuck Norris <gmail@chucknorris.com>", "webmaster@müller.de", "matteo@78.47.122.114"}) {

      System.out.println("  " + email + " is valid " + validator.isValidEmailAddress(email));

    }

  }
}
