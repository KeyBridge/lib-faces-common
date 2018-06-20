/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package ch.keybridge.lib.faces.converter;

import java.util.Locale;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Locale Converter. Locale converts to and from well-formed IETF BCP 47
 * language tag representing the locale.
 * <p>
 * If this Locale has a language, country, or variant that does not satisfy the
 * IETF BCP 47 language tag syntax requirements, this method handles these
 * fields as described below:
 *
 * @author Key Bridge
 * @since v2.3.0 create 03/28/17
 */
@FacesConverter("localeConverter")
public class LocaleConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return Locale.forLanguageTag(value);
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    return value != null ? ((Locale) value).toLanguageTag() : null;
  }

}
