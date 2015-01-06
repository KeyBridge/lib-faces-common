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
package org.caulfield.lib.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * JSF Converter class to convert new line characters to HTML BR entries.
 * <p>
 * The newline character (\n) is equivalent to the ASCII linefeed character (hex
 * 0A).
 * <p/>
 * @author Jesse Caulfield <jesse@caulfield.org> 07/26/14
 */
@FacesConverter(value = "convertNewline")
public class ConvertNewline implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return value;
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    return String.valueOf(value).replaceAll("\n", "<br/>\n");
  }
}
