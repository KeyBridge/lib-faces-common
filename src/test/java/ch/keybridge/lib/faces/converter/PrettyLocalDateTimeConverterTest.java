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
package ch.keybridge.lib.faces.converter;

import java.time.LocalDateTime;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class PrettyLocalDateTimeConverterTest {

  public PrettyLocalDateTimeConverterTest() {
  }

  @Test
  public void testGetAsString() {

    LocalDateTime ldt = LocalDateTime.now().minusHours(2);

    PrettyLocalDateTimeConverter converter = new PrettyLocalDateTimeConverter();

    String string = converter.getAsString(null, null, ldt);

    System.out.println("In    " + ldt);
    System.out.println("Out   " + string);

  }

}
