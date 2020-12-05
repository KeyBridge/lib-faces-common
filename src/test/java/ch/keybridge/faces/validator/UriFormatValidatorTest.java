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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.junit.*;

/**
 *
 * @author Key Bridge
 */
public class UriFormatValidatorTest {

  private static UrlFormatValidator validator;

  public UriFormatValidatorTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    validator = new UrlFormatValidator();
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
  public void testValidate() throws URISyntaxException, MalformedURLException {
    String uriText = "http://example.com/path?query=123abc#fragmet";
//    String uriText = "example.com/path?query=123abc#fragmet";

    URI uri = new URI(uriText);

    System.out.println("  as uri: " + uri);

    URL url = new URL(uriText);

    System.out.println("  as url: " + url);
  }

}
