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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.*;

/**
 *
 * @author Key Bridge
 */
public class ZoneIdConverterTest {

  public ZoneIdConverterTest() {
  }

  @BeforeClass
  public static void setUpClass() {
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
  public void testGetAsString() {
    int offset = -240;

    ZoneId zone = ZoneId.ofOffset("UTC", ZoneOffset.ofHoursMinutes(offset / 60, offset % 60));
    System.out.println("zone " + zone);

    String[] candidates = new String[]{"America/Los_Angeles",
                                       "America/Denver",
                                       "America/Chicago",
                                       "America/New_York",
                                       "Europe/London"};

//    for (String candidate : ZoneId.getAvailableZoneIds()) {    }
    for (String candidate : candidates) {
      ZoneId candidateZoneId = ZoneId.of(candidate);
      ZoneOffset candidateOffset = candidateZoneId.getRules().getOffset(Instant.now());
      if (candidateOffset.equals(zone.getRules().getOffset(Instant.now()))) {
        System.out.println("  zone equals " + candidateZoneId);
      }
    }

    ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(offset / 60, offset % 60);

  }

//  @Test
  public void testParseDate() {
    String date = "Sun Oct 25 2020 19:09:06 GMT-0400";
    ZonedDateTime z = ZonedDateTime.parse(date);
    System.out.println("  " + z);

  }

}
