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

import ch.keybridge.faces.converter.UriSchemeConverter;
import java.net.URI;
import org.junit.*;

/**
 *
 * @author Key Bridge
 * @since v0.40.4 created 2020-08-08
 */
public class UriSchemeConverterTest {

  private static UriSchemeConverter converter;

  public UriSchemeConverterTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    converter = new UriSchemeConverter();
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
  public void testUriSchemeConvert() {

//    String domain = "dowlohnes.com/a/b/c/d/foo.xhtml";
    String domain = "https://dowlohnes.com";
    URI uri = URI.create(domain);
//    System.out.println("  uri " + uri);
    String url = converter.getAsString(null, null, domain);
    Assert.assertEquals("https://dowlohnes.com", url);
//    System.out.println("  domain  " + domain);
//    System.out.println("  url     " + url);
    Object host = converter.getAsObject(null, null, url);
//    System.out.println("  host    " + host);

    Assert.assertEquals("dowlohnes.com", host);
    System.out.println("testUriSchemeConvert OK");

  }

}
