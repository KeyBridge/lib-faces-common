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
import java.util.Date;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * ZonedDateTime JSF converter.
 * <p>
 * When using ZonedDateTime with an appropriate TIMESTAMP WITH TIME ZONE DB
 * column type, use the below JSF converter to convert between String in the UI
 * and ZonedDateTime in the model. This converter will lookup the pattern and
 * locale attributes from the parent component. If the parent component doesn't
 * natively support a pattern or locale attribute, simply add them as
 * &lt;f:attribute name="..." value="..."&gt;. If the locale attribute is
 * absent, the (default) &lt;f:view locale&gt; will be used instead. There is no
 * timeZone attribute for the reason as explained in #1 here above.
 * <p>
 * Example use:
 * <pre>
 * &lt;h:outputText id="display" value="#{bean.dateTime}"&gt;
 *   &lt;f:converter converterId="zonedDateTimeConverter" /&gt;
 *   &lt;f:attribute name="pattern" value="dd-MMM-yyyy hh:mm:ss a Z" /&gt;
 *   &lt;f:attribute name="timeZone" value="Asia/Kolkata" /&gt;
 * &lt;/h:outputText&gt;</pre>
 *
 * @author Key Bridge
 * @since v0.11.0 added 12/05/17 from stackoverflow example
 * @see
 * <a href="https://stackoverflow.com/questions/34883270/how-to-use-java-time-zoneddatetime-localdatetime-in-pcalendar">Dealing
 * with LocalDateTime in JSF</a>
 */
@FacesConverter("prettyZonedDateTimeConverter")
public class PrettyZonedDateTimeConverter implements Converter {

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "never";
    }
    if (modelValue instanceof ZonedDateTime) {
      return new PrettyTime(getLocale(context, component)).format(toDate((ZonedDateTime) modelValue));
    } else if (modelValue instanceof String) {
      return new PrettyTime(getLocale(context, component)).format(toDate((String) modelValue));
    } else {
      throw new ConverterException(new FacesMessage(modelValue + " is not a valid ZonedDateTime"));
    }
  }

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    return null;
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

  /**
   * Convert a String representation of a java.time.ZonedDateTime instance to a
   * java.util.Date.
   *
   * @param temporal the java.lang.String instance
   * @return the java.util intance
   */
  public static Date toDate(String temporal) {
    ZonedDateTime zdt = ZonedDateTime.parse(temporal);
    return toDate(zdt);
  }
}
