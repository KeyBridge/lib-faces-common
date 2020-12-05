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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class UrlValidatorTest {

  private UrlValidator validator;

  public UrlValidatorTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @Before
  public void setUp() {
    this.validator = new UrlValidator();
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testSomeMethod() throws MalformedURLException {
    String urlText = "https://keybridgewireless.com";

    URL url = new URL(urlText);

//    String string = "https://google.com";
    boolean valid = validator.testLinkValidity(url);
//    validator.validate(null, null, url);
    System.out.println(" is valid " + url + " ? " + valid);

  }

//  @Test
  public void testRegex() {
    /**
     * A bit of hackery to test case insensitive regex
     */

    String url = "HTTps://keybridgewireless.com";

    if (url.toLowerCase().startsWith("https")) {
      System.out.println("  starts with https ");

      String newUrl = url.replaceFirst("(?)https", "http");

      System.out.println("  old url " + url);
      System.out.println("  new url " + newUrl);

      Pattern p = Pattern.compile("https", Pattern.CASE_INSENSITIVE);
      Matcher m = p.matcher(url);
      if (m.find()) {
        System.out.println("  match !");
        System.out.println("  " + m.group(0));
        newUrl = url.replace(m.group(0), "http");
      }

      System.out.println("  old url " + url);
      System.out.println("  new url " + newUrl);
    }

  }

}
