/*
 *  Copyright (C) 2012 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 *
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *  reserves all rights in and to Caulfield Copyrights and no license is
 *  granted under Caulfield Copyrights in this Software License Agreement.
 *  Caulfield generally licenses Caulfield Copyrights for commercialization
 *  pursuant to the terms of either Caulfield's Standard Software Source Code
 *  License Agreement or Caulfield's Standard Product License Agreement.
 *  A copy of Caulfield's either License Agreement can be obtained on request
 *  by email from: info@caufield.org.
 */
package ch.keybridge.lib.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "convertLength128")
public class ConvertLength128 implements Converter {

  private static final int LENGTH = 128;

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return value;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value != null && !value.toString().isEmpty() && value.toString().length() > LENGTH) {
      return value.toString().substring(0, LENGTH - 3) + "...";
    }
    return value != null ? value.toString() : "";
  }
}
