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
package ch.keybridge.lib.faces.validator;

import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class PhoneValidatorTest {

  public PhoneValidatorTest() {
  }

  @Test
  public void testDigits() {

    PhoneValidator validator = new PhoneValidator();

    for (String digits : new String[]{
      "+44 (23) 92846438",
      "(023) 92846438",
      "+1 555 890 0988",
      "(555) 789 0988",
      "555-0988",
      "555_0988",
      "555.0988",
      "555 789-0988 ",
      "555-789-0988 ",
      "555_789_0988 ",
      "555.789.0988 ", //      "55.0988 ",
    //      "5.0988 ",
    //      ".$@$@#$@#$@#$0988 ",
    }) {
      validator.validate(null, null, digits);
    }
  }

}
