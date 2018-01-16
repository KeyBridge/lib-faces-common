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

import ch.keybridge.lib.markdown.Markdown;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Markdown text JSF converter.
 * <p>
 * Converts Markdown encoded text to HTML.
 *
 * @author Key Bridge
 * @since 3.0.0 added 01/16/18
 * @see <a href="https://daringfireball.net/projects/markdown/">Markdown</a>
 */
@FacesConverter("markdownConverter")
public class MarkdownConverter implements Converter {

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "";
    }
    return new Markdown().toHtml(String.valueOf(modelValue));
  }

  /**
   * {@inheritDoc}
   * <p>
   * RETURNS NULL.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    return null;
  }

}