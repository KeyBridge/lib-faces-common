/*
 *   Copyright (C) 2012 Caulfield IP Holdings (Caulfield)
 *   and/or its affiliates.
 *   All rights reserved. Use is subject to license terms.
 *
 *   Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *   reserves all rights in and to Caulfield Copyrights and no license is
 *   granted under Caulfield Copyrights in this Software License Agreement.
 *   Caulfield generally licenses Caulfield Copyrights for commercialization
 *   pursuant to the terms of either Caulfield's Standard Software Source Code
 *   License Agreement or Caulfield's Standard Product License Agreement.
 *
 *   A copy of Caulfield's either License Agreement can be obtained on request
 *   by email from: info@caufield.org.
 */
package ch.keybridge.lib.faces.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A JSF converter for handling XMLGregorianCalendar DATETIME objects.
 * XMLGregorianCalendar objects are passed by some SOAP web services.
 * <p>
 * This class is designed only to be used as a text converter for
 * pretty-printing DATE + TIME values, not for marshalling/unmarshalling in a
 * selection.
 * <p>
 * The output is formatted as: MMMM dd, yyyy. e.g. "August 28, 2018"
 *
 * @author jesse
 * @since v3.6.1 - created
 */
@FacesConverter(value = "xmlGregorianCalendarDateTimeConverter")
public class XmlGregorianCalendarDateTimeConverter implements Converter {

  /**
   * Convert from a pretty-print string to an XMLGregorian calendar.
   *
   * @param context   the faces context
   * @param component the faces component
   * @param value     a SimpleDateFormat SHORT configuration
   * @return a XMLGregorianCalendar instance
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    /**
     * This method is not used
     */
    try {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(SimpleDateFormat.getDateInstance(DateFormat.LONG).parse(value));
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    } catch (ParseException | DatatypeConfigurationException | NullPointerException ex) {
//      Logger.getLogger(ConvertXMLGregorianCalendar.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }

  /**
   * Convert from an XMLGregorian calendar set by the SOAP implementation to a
   * pretty-print string.
   *
   * @param context   the faces context
   * @param component the faces component
   * @param value     the XMLGregorianCalendar instance
   * @return the formatted calendar
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value instanceof XMLGregorianCalendar) {
      return SimpleDateFormat.getDateInstance(DateFormat.LONG).format(((XMLGregorianCalendar) value).toGregorianCalendar().getTime());
    }
    return "";
  }
}
