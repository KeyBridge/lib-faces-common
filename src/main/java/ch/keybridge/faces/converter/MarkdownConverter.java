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

import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.gitlab.GitLabExtension;
import com.vladsch.flexmark.ext.macros.MacrosExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.KeepType;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;
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
 * @since v3.0.0 added 01/16/18
 * @since v4.0.0 extended 02/18/19 to include MacrosExtension
 * @see <a href="https://daringfireball.net/projects/markdown/">Markdown</a>
 */
@FacesConverter("markdownConverter")
public class MarkdownConverter implements Converter {

  /**
   * The HTML renderer.
   */
  private final HtmlRenderer renderer;
  /**
   * The Markdown processor.
   */
  private final Parser parser;

  public MarkdownConverter() {
    /**
     * Set extensions.
     * <p>
     * TablesExtension enables tables using pipes. <br>
     * GitLabExtension parses and renders GitLab Flavoured Markdown including
     * math (via Katex) and charts (via Mermaid). <br>
     * TaskListExtension renders check boxes in lists (cute).
     * <p>
     * Macro Definitions are block elements which can contain any markdown
     * element(s) but can be expanded in a block or inline context, allowing
     * block elements to be used where only inline elements are permitted by the
     * syntax. See https://github.com/vsch/flexmark-java/wiki/Macros-Extension
     */
    MutableDataSet options = new MutableDataSet();
    options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(),
                                                 GitLabExtension.create(),
                                                 TaskListExtension.create(),
                                                 MacrosExtension.create()));
    /**
     * Parser.REFERENCES_KEEP defines the behavior of references when duplicate
     * references are defined in the source. In this case it is configured to
     * keep the last value, whereas the default behavior is to keep the first
     * value.
     * <p>
     * TablesExtension added for full GFM table compatibility.
     */
    options.set(Parser.REFERENCES_KEEP, KeepType.LAST)
      .set(HtmlRenderer.INDENT_SIZE, 2)
      .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
      .set(TablesExtension.CLASS_NAME, "table")
      .set(TablesExtension.COLUMN_SPANS, false)
      .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
      .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
      .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
    this.parser = Parser.builder(options).build();
    this.renderer = HtmlRenderer.builder(options).build();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Convert markdown text to HTML.
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
    if (modelValue == null) {
      return "";
    }
    /**
     * Cast the object to String since we only expect this converter to be used
     * in a text widget.
     */
    Node node = parser.parse((String) modelValue);
    return renderer.render(node);
  }

  /**
   * {@inheritDoc}
   * <p>
   * RETURNS NULL.
   * <p>
   * TODO: html to markdown converter. See Html To Markdown in
   * flexmark-html-parser.
   * <p>
   * See also https://github.com/domchristie/turndown as a potential candidate.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
    return null;
  }

}
