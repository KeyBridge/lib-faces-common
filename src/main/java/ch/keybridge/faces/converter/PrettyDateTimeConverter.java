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
package ch.keybridge.faces.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Generic pretty date time converter. String converter accepts ZonedDateTime,
 * LocalDateTime, LocalDate, java.util.Calendar, java.util.Date, and String (of
 * ZonedDateTime).
 * <p>
 * This is a one-way `getAsString` converter. The Object converter always return
 * null.
 * <p>
 * LocalDateTime: When using UTC based LocalDateTime with an appropriate UTC
 * based TIMESTAMP (without time zone!) DB column type, use the below JSF
 * converter to convert between String in the UI and LocalDateTime in the model.
 * This converter will lookup the pattern, timeZone and locale attributes from
 * the parent component. If the parent component doesn't natively support a
 * pattern, timeZone and/or locale attribute, simply add them as &lt;f:attribute
 * name="..." value="..."&gt;. The timeZone attribute must represent the
 * fallback time zone of the input string (when the pattern doesn't contain a
 * time zone), and the time zone of the output string.
 * <p>
 * ZonedDateTime: When using ZonedDateTime with an appropriate TIMESTAMP WITH
 * TIME ZONE DB column type, use the below JSF converter to convert between
 * String in the UI and ZonedDateTime in the model. This converter will lookup
 * the pattern and locale attributes from the parent component. If the parent
 * component doesn't natively support a pattern or locale attribute, simply add
 * them as &lt;f:attribute name="..." value="..."&gt;. If the locale attribute
 * is absent, the (default) &lt;f:view locale&gt; will be used instead. There is
 * no timeZone attribute for the reason as explained in #1 here above.
 * <p>
 * Example use:
 * <pre>
 * &lt;h:outputText id="display" value="#{bean.dateTime}"&gt;
 *   &lt;f:converter converterId="prettyDateTimeConverter" /&gt;
 *   &lt;f:attribute name="pattern" value="dd-MMM-yyyy hh:mm:ss a Z" /&gt;
 *   &lt;f:attribute name="timeZone" value="Asia/Kolkata" /&gt;
 * &lt;/h:outputText&gt;</pre>
 *
 * @author Key Bridge
 * @since v0.11.0 added 12/05/17 from stackoverflow example
 * @see
 * <a href="https://stackoverflow.com/questions/34883270/how-to-use-java-time-zoneddatetime-localdatetime-in-pcalendar">Dealing
 * with LocalDateTime in JSF</a>
 * @since v4.0.0 created 01/14/19 to consolidate different pretty converters
 * @see <a href="http://www.ocpsoft.org/prettytime/">prettytime</a>
 */
public class PrettyDateTimeConverter extends AbstractConverter {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "never";
    }

    if (modelValue instanceof ZonedDateTime) {
      return new PrettyTime(getLocale(context, component)).format(toDate((ZonedDateTime) modelValue));
    } else if (modelValue instanceof LocalDateTime) {
      return new PrettyTime(getLocale(context, component)).format(toDate((LocalDateTime) modelValue));
    } else if (modelValue instanceof LocalDate) {
      return new PrettyTime(getLocale(context, component)).format(toDate((LocalDate) modelValue));
    } else if (modelValue instanceof Calendar) {
      return new PrettyTime(getLocale(context, component)).format((Calendar) modelValue);
    } else if (modelValue instanceof Date) {
      return new PrettyTime(getLocale(context, component)).format((Date) modelValue);
    } else if (modelValue instanceof String) {
      return new PrettyTime(getLocale(context, component)).format(parseZonedDateTime((String) modelValue));
    } else {
      throw new ConverterException(new FacesMessage(modelValue + " is not a valid date or time representation."));
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    return null;
  }

  /**
   * Convert a String representation of a java.time.ZonedDateTime instance to a
   * java.util.Date.
   *
   * @param temporal the java.lang.String instance
   * @return the java.util intance
   */
  public Date toDate(String temporal) {
    ZonedDateTime zdt = ZonedDateTime.parse(temporal);
    return toDate(zdt);
  }
}
