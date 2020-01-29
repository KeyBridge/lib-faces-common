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

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class LinkValidatorTest {

  private LinkValidator validator;

  public LinkValidatorTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @Before
  public void setUp() {
    this.validator = new LinkValidator();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testSomeMethod() {
    String string = "https://keybridgewireless.com";
//    String string = "https://google.com";

    validator.validate(null, null, string);
  }

}
