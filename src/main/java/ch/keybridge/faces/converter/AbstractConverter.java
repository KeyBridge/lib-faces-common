/*
 *  Copyright (C) 2014 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 *
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby reserves
 *  all rights in and to Caulfield Copyrights and no license is granted under
 *  Caulfield Copyrights in this Software License Agreement. Caulfield generally
 *  licenses Caulfield Copyrights for commercialization pursuant to the terms of
 *  either Caulfield's Standard Software Source Code License Agreement or
 *  Caulfield's Standard Product License Agreement.
 *
 *  A copy of either License Agreement can be obtained on request by email from:
 *  info@caufield.org.
 */
package ch.keybridge.faces.converter;

import ch.keybridge.faces.FacesUtil;
import java.time.*;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * An abstract validating Faces converter. This class provides helpful shortcuts
 * for applying validation css when converting a ui component.
 *
 * @author Jesse Caulfield
 * @since v4.1.1 created 10/29/19
 */
public abstract class AbstractConverter implements Converter {

  /**
   * The JSF styleclass tag.
   */
  protected static final String STYLECLASS = "styleClass";
  /**
   * "is-invalid". The BS4 css marking an invalid form input.
   */
  protected static final String INVALID_CSS = "is-invalid";
  /**
   * "is-valid". The BS4 css marking a valid form input.
   */
  protected static final String VALID_CSS = "is-valid";

  /**
   * Add CSS to the indicated (form input) component marking it as valid or
   * invalid. This method adds a BS4 form validation CSS to indicate valid
   * (green) or invalid (red).
   *
   * @param component the form component
   * @param isValid   the validity status.
   * @since v4.0.0 added 01/28/19
   */
  protected void setValidityStatus(UIComponent component, boolean isValid) {
    /**
     * Get and clear the current styleclass, then add the validity status.
     */
    if (component != null) {
      String styleClass = (String) component.getAttributes().get(STYLECLASS);
      styleClass = styleClass.replaceAll(INVALID_CSS, "");
      styleClass = styleClass.replaceAll(VALID_CSS, "");
      styleClass += " " + (isValid ? VALID_CSS : INVALID_CSS);
      component.getAttributes().put(STYLECLASS, styleClass);
    }
  }

  /**
   * Remove the validity status from the form input component.
   *
   * @param component the form component
   * @since v4.1.1 added 10/12/19
   */
  protected void unsetValidityStatus(UIComponent component) {
    /**
     * Get and clear the current styleclass, then add the validity status.
     */
    if (component != null) {
      String styleClass = (String) component.getAttributes().get(STYLECLASS);
      styleClass = styleClass.replaceAll(INVALID_CSS, "");
      styleClass = styleClass.replaceAll(VALID_CSS, "");
      component.getAttributes().put(STYLECLASS, styleClass);
    }
  }

  /**
   * Extract the Locale provided as a component attribute. If none is provided
   * then try to get the Locale from a {@code .locale} cookie.
   *
   * @param context   the context
   * @param component the component
   * @return the locale, if provided, otherwise the default locale
   */
  protected Locale getLocale(FacesContext context, UIComponent component) {
    try {
      Object locale = component.getAttributes().get("locale");
      /**
       * If locale was not specified then try to get it from the cookie.
       */
      if (locale == null) {
        locale = FacesUtil.getCookie(".locale") != null
                 ? FacesUtil.getCookie(".locale").getValue()
                 : null;
      }
      return (locale instanceof Locale)
             ? (Locale) locale
             : (locale instanceof String)
               ? new Locale((String) locale)
               : context.getViewRoot().getLocale();
    } catch (Exception e) {
      return Locale.getDefault();
    }
  }

  /**
   * Extract the time zone id provided as a component attribute. If none is
   * provided then try to get the Locale from a {@code .tzid} cookie.
   *
   * @param component the component
   * @return the time zone
   */
  protected ZoneId getZoneId(UIComponent component) {
    Object timeZone = component.getAttributes().get("timeZone");
    if (timeZone == null) {
      timeZone = FacesUtil.getCookie(".tzid") != null
                 ? FacesUtil.getCookie(".tzid").getValue()
                 : "UTC";
    }
    return (timeZone instanceof TimeZone)
           ? ((TimeZone) timeZone).toZoneId()
           : (timeZone instanceof String)
             ? ZoneId.of((String) timeZone)
             : null;
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
  protected String getPattern(UIComponent component) {
    return component != null ? (String) component.getAttributes().get("pattern") : null;
  }

  /**
   * The system default time-zone. This queries TimeZone.getDefault() to find
   * the default time-zone and converts it to a ZoneId.
   */
  protected static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

  protected static final ZoneId UTC_ZONE = ZoneOffset.UTC;

  /**
   * Convert Date to a LocalDate. Gets the LocalDate part of this date-time.
   * This returns a LocalDate with the same year, month and day as this
   * date-time. Returns: the date part of this date-time, not null
   *
   * @param date the Date instance
   * @return a LocalDate instance in the default (system) time zone.
   */
  protected LocalDate toLocalDate(java.util.Date date) {
    if (date == null) {
      return null;
    }
    if (date instanceof java.sql.Date) {
      return ((java.sql.Date) date).toLocalDate();
    } else {
      return date.toInstant().atZone(SYSTEM_ZONE).toLocalDate();
    }
  }

  /**
   * Convert Date to a LocalDateTime. Gets the ZonedDateTime part of this
   * date-time. This returns a ZonedDateTime with the same year, month and day
   * as this date-time.
   * <p>
   * If the ZoneId is null then the default (system) zone is used.
   *
   * @param date the Date instance
   * @param zone the time zone
   * @return a ZonedDateTime instance in the indicated time zone
   */
  protected ZonedDateTime toZonedDateTime(java.util.Date date, ZoneId zone) {
    if (date == null) {
      return null;
    }
    if (date instanceof java.sql.Timestamp) {
      return ((java.sql.Timestamp) date).toLocalDateTime().atZone(zone != null ? zone : SYSTEM_ZONE);
    } else {
      return date.toInstant().atZone(zone != null ? zone : SYSTEM_ZONE);
    }
  }

  /**
   * Convert a java.time.LocalDate instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  protected Date toDate(LocalDate temporal) {
    return toDate(temporal.atTime(LocalTime.now()));
  }

  /**
   * Convert a java.time.LocalDateTime instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  protected Date toDate(LocalDateTime temporal) {
    return toDate(temporal.atZone(ZoneOffset.UTC));
  }

  /**
   * Convert a java.time.ZonedDateTime instance to a java.util.Date.
   *
   * @param temporal the java.time instance
   * @return the java.util intance
   */
  protected Date toDate(ZonedDateTime temporal) {
    Instant instant = temporal.toInstant();
    return Date.from(instant);
  }

  /**
   * Convert a String representation of a java.time.ZonedDateTime instance to a
   * java.util.Date.
   *
   * @param temporal the java.lang.String instance
   * @return the java.util.Date instance
   */
  protected Date parseZonedDateTime(String temporal) {
    ZonedDateTime zdt = ZonedDateTime.parse(temporal);
    return toDate(zdt);
  }

  /**
   * Convert a String representation of a java.time.LocalDateTime instance to a
   * java.util.Date.
   *
   * @param temporal the java.lang.String instance
   * @return the java.util.Date instance
   */
  protected Date parseLocalDateTime(String temporal) {
    LocalDateTime dt = LocalDateTime.parse(temporal);
    return toDate(dt);
  }

}
