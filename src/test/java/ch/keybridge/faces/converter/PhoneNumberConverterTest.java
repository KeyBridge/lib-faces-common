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
package ch.keybridge.faces.converter;

import ch.keybridge.faces.converter.PhoneNumberConverter;
import org.junit.*;

/**
 * Example phone numbers
 * <p>
 * When you want to show an example phone number, use a US number in the range
 * (800) 555-0100 through (800) 555-0199. That range is reserved for use in
 * examples and in fiction.
 * <p>
 * Never use a real phone number in examples.
 *
 * @author Key Bridge
 * @since v0.41.3
 */
public class PhoneNumberConverterTest {

  private static PhoneNumberConverter converter;

  public PhoneNumberConverterTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    converter = new PhoneNumberConverter();
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testConvertPhoneNumber() {
    String numberSequence = " (800)   555-0100   ";
    String formatted = converter.getAsString(null, null, numberSequence);
//    System.out.println("  raw    " + numberSequence);
//    System.out.println("  format " + formatted);
    Assert.assertEquals("(800) 555-0100", formatted);
    Object simplified = converter.getAsObject(null, null, formatted);
//    System.out.println("  simple " + simplified);
    Assert.assertEquals("8005550100", simplified);

    System.out.println("testConvertPhoneNumber OK");
  }

}
