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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * LocalDate JSF converter.
 * <p>
 * When using UTC based LocalDate with an appropriate UTC based TIMESTAMP
 * (without time zone!) DB column type, use the below JSF converter to convert
 * between String in the UI and LocalDateTime in the model. This converter will
 * lookup the pattern, timeZone and locale attributes from the parent component.
 * If the parent component doesn't natively support a pattern, timeZone and/or
 * locale attribute, simply add them as &lt;f:attribute name="..."
 * value="..."&gt;. The timeZone attribute must represent the fallback time zone
 * of the input string (when the pattern doesn't contain a time zone), and the
 * time zone of the output string.
 * <p>
 * Example use: {@code &lt;h:outputText id="display" value="#{bean.dateTime}" converter="localDateTimeConverter" &gt;
 * &lt;f:attribute name="pattern" value="dd-MMM-yyyy hh:mm:ss a Z" /&gt;
 * &lt;f:attribute name="timeZone" value="Asia/Kolkata" /&gt;
 * &lt;/h:outputText&gt;
 * }
 *
 * @author Key Bridge
 * @since v0.11.0 added 12/05/17 from stackoverflow example
 * @see
 * <a href="https://stackoverflow.com/questions/34883270/how-to-use-java-time-zoneddatetime-localdatetime-in-pcalendar">Dealing
 * with LocalDateTime in JSF</a>
 */
@FacesConverter("localDateConverter")
public class LocalDateConverter implements Converter {

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "";
    }
    TemporalAccessor localDate = null;
    if (modelValue instanceof LocalDate) {
      localDate = (TemporalAccessor) modelValue;
    } else if (modelValue instanceof Date) {
      localDate = toLocalDate((Date) modelValue);
    } else if (modelValue instanceof String) {
      localDate = LocalDate.parse((CharSequence) modelValue);
    }
    /**
     * NPE defense.
     */
    if (localDate == null) {
      return "";
    }
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withLocale(getLocale(context, component));
    return formatter.format(localDate);
  }

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    return null;
  }

  /**
   * Build a date time formatter.
   *
   * @param context   the context
   * @param component the parent component
   * @return a Formatter instnace
   */
  private DateTimeFormatter getFormatter(FacesContext context, UIComponent component) {
    String pattern = getPattern(component);
    return pattern == null
           ? DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(getLocale(context, component)).withZone(ZONE_ID)
           : DateTimeFormatter.ofPattern(getPattern(component), getLocale(context, component)).withZone(ZONE_ID);
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
    return component != null ? (String) component.getAttributes().get("pattern") : null;
  }

  /**
   * Extract the Locale provided as a component attribute
   *
   * @param context   the context
   * @param component the component
   * @return the locale, if provided, otherwise the default locale
   */
  private Locale getLocale(FacesContext context, UIComponent component) {
    try {
      Object locale = component.getAttributes().get("locale");
      return (locale instanceof Locale) ? (Locale) locale
             : (locale instanceof String) ? new Locale((String) locale)
               : context.getViewRoot().getLocale();
    } catch (Exception e) {
      return Locale.getDefault();
    }
  }

  private static final ZoneId ZONE_ID = ZoneId.systemDefault();

  /**
   * Convert Date to a LocalDate. Gets the LocalDate part of this date-time.
   * This returns a LocalDate with the same year, month and day as this
   * date-time. Returns: the date part of this date-time, not null
   *
   * @param date the Date instance
   * @return a LocalDate instance in the default (system) time zone.
   */
  public static LocalDate toLocalDate(java.util.Date date) {
    if (date == null) {
      return null;
    }
    if (date instanceof java.sql.Date) {
      return ((java.sql.Date) date).toLocalDate();
    } else {
      return date.toInstant().atZone(ZONE_ID).toLocalDate();
    }
  }

}