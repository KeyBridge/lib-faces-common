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

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * LocalDateTime JSF converter.
 * <p>
 * When using UTC based LocalDateTime with an appropriate UTC based TIMESTAMP
 * (without time zone!) DB column type, use the below JSF converter to convert
 * between String in the UI and LocalDateTime in the model. This converter will
 * lookup the pattern, timeZone and locale attributes from the parent component.
 * If the parent component doesn't natively support a pattern, timeZone and/or
 * locale attribute, simply add them as &lt;f:attribute name="..."
 * value="..."&gt;. The timeZone attribute must represent the fallback time zone
 * of the input string (when the pattern doesn't contain a time zone), and the
 * time zone of the output string.
 * <p>
 * Example use:
 * <pre>
 * &lt;h:outputText id="display" value="#{bean.dateTime}"&gt;
 *   &lt;f:converter converterId="prettyLocalDateTimeConverter" /&gt;
 *   &lt;f:attribute name="pattern" value="dd-MMM-yyyy hh:mm:ss a Z" /&gt;
 *   &lt;f:attribute name="timeZone" value="Asia/Kolkata" /&gt;
 * &lt;/h:outputText&gt;</pre>
 *
 *
 * @author Key Bridge
 * @since v0.11.0 added 12/05/17 from stackoverflow example
 * @see
 * <a href="https://stackoverflow.com/questions/34883270/how-to-use-java-time-zoneddatetime-localdatetime-in-pcalendar">Dealing
 * with LocalDateTime in JSF</a>
 */
@FacesConverter("prettyLocalDateTimeConverter")
public class PrettyLocalDateTimeConverter implements Converter {

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "";
    }

    if (modelValue instanceof LocalDateTime) {
      return new PrettyTime().format(toDate((LocalDateTime) modelValue));
    } else {
      throw new ConverterException(new FacesMessage(modelValue + " is not a valid LocalDateTime"));
    }
  }

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    if (submittedValue == null || submittedValue.isEmpty()) {
      return null;
    }
    try {
      return ZonedDateTime.parse(submittedValue, getFormatter(context, component)).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    } catch (DateTimeParseException e) {
      throw new ConverterException(new FacesMessage(submittedValue + " is not a valid local date time"), e);
    }
  }

  /**
   * Build a date time formatter.
   *
   * @param context   the context
   * @param component the parent component
   * @return a Formatter instnace
   */
  private DateTimeFormatter getFormatter(FacesContext context, UIComponent component) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getPattern(component), getLocale(context, component));
    ZoneId zone = getZoneId(component);
    return (zone != null) ? formatter.withZone(zone) : formatter;
  }

  /**
   * Extract the converter output pattern provided as a component attribute.
   * <p>
   * Example:
   * {@code &lt;f:attribute name="pattern" value="dd-MMM-yyyy hh:mm:ss a Z"/&gt;}
   *
   * @param component the UI component
   * @return the output pattern.
   */
  private String getPattern(UIComponent component) {
    String pattern = (String) component.getAttributes().get("pattern");

    if (pattern == null) {
      throw new IllegalArgumentException("Pattern attribute is required");
    }

    return pattern;
  }

  /**
   * Extract the Locale provided as a component attribute
   *
   * @param context   the context
   * @param component the component
   * @return the locale, if provided, otherwise the default locale
   */
  private Locale getLocale(FacesContext context, UIComponent component) {
    Object locale = component.getAttributes().get("locale");
    return (locale instanceof Locale) ? (Locale) locale
           : (locale instanceof String) ? new Locale((String) locale)
             : context.getViewRoot().getLocale();
  }

  /**
   * Extract the time zone id provided as a component attribute
   *
   * @param component the component
   * @return the time zone
   */
  private ZoneId getZoneId(UIComponent component) {
    Object timeZone = component.getAttributes().get("timeZone");
    return (timeZone instanceof TimeZone) ? ((TimeZone) timeZone).toZoneId()
           : (timeZone instanceof String) ? ZoneId.of((String) timeZone)
             : null;
  }

  /**
   * Convert a java.time.LocalDate instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  public static Date toDate(LocalDate temporal) {
    return toDate(temporal.atTime(LocalTime.now()));
  }

  /**
   * Convert a java.time.LocalDateTime instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  public static Date toDate(LocalDateTime temporal) {
    return toDate(temporal.atZone(ZoneOffset.UTC));
  }

  /**
   * Convert a java.time.ZonedDateTime instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  public static Date toDate(ZonedDateTime temporal) {
    Instant instant = temporal.toInstant();
    return Date.from(instant);
  }
}