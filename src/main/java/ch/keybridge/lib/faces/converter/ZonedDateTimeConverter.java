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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

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
 * Example use: {@code &lt;h:outputText id="display" value="#{bean.dateTime}" converter="zonedDateTimeConverter"&gt;
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
@FacesConverter("zonedDateTimeConverter")
public class ZonedDateTimeConverter implements Converter {

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "";
    }

    if (modelValue instanceof ZonedDateTime) {
      return getFormatter(context, component).format((ZonedDateTime) modelValue);
    } else {
      throw new ConverterException(new FacesMessage(modelValue + " is not a valid ZonedDateTime"));
    }
  }

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    if (submittedValue == null || submittedValue.isEmpty()) {
      return null;
    }

    try {
      return ZonedDateTime.parse(submittedValue, getFormatter(context, component));
    } catch (DateTimeParseException e) {
      throw new ConverterException(new FacesMessage(submittedValue + " is not a valid zoned date time"), e);
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
    String pattern = getPattern(component);
    return pattern == null
           ? DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(getLocale(context, component))
           : DateTimeFormatter.ofPattern(getPattern(component), getLocale(context, component));
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

}
