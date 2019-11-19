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
package ch.keybridge.faces.converter;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Key Bridge
 */
public class XmlGregorianCalendarConverterTest {

  private XmlGregorianCalendarConverter converter;

  public XmlGregorianCalendarConverterTest() {
  }

  @Before
  public void setUp() {
    this.converter = new XmlGregorianCalendarConverter();
  }

  @Test
  public void testSomeMethod() throws DatatypeConfigurationException {
    GregorianCalendar calendar = new GregorianCalendar();
    XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    String string = converter.getAsString(null, null, xmlCalendar);
    System.out.println("to string: " + string);

  }

}
