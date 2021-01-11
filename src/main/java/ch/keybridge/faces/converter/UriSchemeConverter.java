/*
 * Copyright 2020 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.faces.converter;

import java.net.URI;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Simple converter to conditionally append an http scheme to a URI. JSF renders
 * _relative_ links for URI references having no scheme prefix. This tool adds a
 * scheme if none is present.
 * <p>
 * For example: JSF will render "maylawoffices.com" as a local relative page
 * link, wheras "http://maylawoffices.com" will render as a correct output link.
 *
 * @author Key Bridge
 * @since v0.40.4 created 2020-08-08
 */
public class UriSchemeConverter implements Converter {

  /**
   * {@inheritDoc} Strip the scheme from a URI input. This returns just the web
   * host plus path.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      URI uri = URI.create(value);
      return uri.getHost() + uri.getPath();
    } catch (Exception e) {
      return value;
    }
  }

  /**
   * {@inheritDoc} This the method called when the page is rendered.
   * Conditionally append a scheme to a domain name.
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object object) {
    if (object == null) {
      return null;
    }
    String value = (String) object;
    if (value.isEmpty()) {
      return null;
    }
    if (value.toLowerCase().startsWith("http")) {
      return value;
    } else {
      return "http://" + value;
    }
  }

}
