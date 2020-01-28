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

import java.util.Locale;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class LocaleConverterTest {

  private LocaleConverter converter;

  public LocaleConverterTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @Before
  public void setUp() {
    this.converter = new LocaleConverter();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testSomeMethod() {

    String en = "en";

//    Locale locale = (Locale) converter.getAsObject(null, null, en);
    Locale locale = new Locale(en);
    System.out.println("  getLanguage   " + locale.getLanguage());
    System.out.println("  toLanguageTag " + locale.toLanguageTag());
    System.out.println("  getDisplayLanguage " + locale.getDisplayLanguage());

    String fr = "fr";

    Locale french = new Locale(fr);
    System.out.println("  french " + french);
    System.out.println("  getLanguage   " + french.getLanguage());
    System.out.println("  toLanguageTag " + french.toLanguageTag());
    System.out.println("  getDisplayLanguage " + french.getDisplayLanguage());

    Locale france = new Locale(fr, "fr");
//  french = (Locale) converter.getAsObject(null, null, fr);

    System.out.println(" + full french " + france);
    System.out.println(" + getLanguage   " + france.getLanguage());
    System.out.println(" + toLanguageTag " + france.toLanguageTag());
    System.out.println(" + getDisplayLanguage " + france.getDisplayLanguage());

    Locale frenchCanadian = new Locale("fr", "ca");

    System.out.println("  frenchCanadian " + frenchCanadian);
    System.out.println("  getLanguage   " + frenchCanadian.getLanguage());
    System.out.println("  toLanguageTag " + frenchCanadian.toLanguageTag());
    System.out.println("  getDisplayLanguage " + frenchCanadian.getDisplayLanguage());

  }

//  @Test
//  public void testLanguages() {
//
//    for (Locale locale : Locale.getAvailableLocales()) {
//      System.out.println("  " + locale.getCountry() + " | " + locale.getLanguage() + " | " + locale.getDisplayLanguage());
//    }
//
//  }
}
