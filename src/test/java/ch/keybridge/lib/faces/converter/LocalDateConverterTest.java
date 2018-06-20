/*
 * Copyright 2018 Key Bridge. All rights reserved. Use is subject to license
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
package ch.keybridge.lib.faces.converter;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class LocalDateConverterTest {

  public LocalDateConverterTest() {
  }

  @Before
  public void setUp() {
  }

  @Test
  public void testChronoField() {
    int year = LocalDate.now().get(ChronoField.YEAR);
    System.out.println("year is: " + year);

    LocalDateConverter converter = new LocalDateConverter();

    System.out.println("as String: " + converter.getAsString(null, null, LocalDate.now()));
  }

}
