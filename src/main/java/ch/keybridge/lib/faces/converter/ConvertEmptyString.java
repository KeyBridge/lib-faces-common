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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Simple string converter to preclude the submission of empty fields and to
 * trim the submitted entry.
 *
 * @author Key Bridge
 * @since v0.9.5 added 06/15/17
 */
@FacesConverter(value = "convertEmptyString")
public class ConvertEmptyString implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return value != null && !value.trim().isEmpty() ? value.trim() : null;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    String v = (String) value;
    return v != null && !v.trim().isEmpty() ? v.trim() : null;
  }

}
