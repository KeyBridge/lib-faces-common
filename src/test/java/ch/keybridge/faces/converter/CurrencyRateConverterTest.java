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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;
import org.junit.*;

/**
 *
 * @author Key Bridge
 */
public class CurrencyRateConverterTest {

  private static CurrencyRateConverter converter;

  public CurrencyRateConverterTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    converter = new CurrencyRateConverter();
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
  public void testGetAsObject() {
//    String value = "$38.15 USD";
    String value = "&#x24;38.15 USD";
    BigDecimal expected = new BigDecimal(38.15).setScale(Currency.getInstance(Locale.US).getDefaultFractionDigits(), RoundingMode.HALF_UP);
    Object object = converter.getAsObject(null, null, value);
    Assert.assertEquals(expected, object);
    String string = converter.getAsString(null, null, object);
    Assert.assertEquals(value, string);
//    System.out.println("testGetAsObject " + object + " OK");
  }

  @Test
  public void testGetAsString() {
    BigDecimal expected = new BigDecimal(38.15).setScale(Currency.getInstance(Locale.US).getDefaultFractionDigits(), RoundingMode.HALF_UP);
    String string = converter.getAsString(null, null, expected);
//    System.out.println("testGetAsString " + string);
    Object object = converter.getAsObject(null, null, string);
    Assert.assertEquals(expected, object);
  }

}
