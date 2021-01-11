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
package ch.keybridge.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * A JSF String converter for string manipulation in JSF components. Provides
 * the following functions.
 * <ul>
 * <li>{@code length [integer]} shortens a string to the user specified length.
 * </li>
 * <li> {@code case ["upper", "lower", "proper", "camel"]} changes case. </li>
 * <li>{@code empty [true, false]} allow empty strings; set to false to force
 * empty strings to NULL.</li>
 * <li>{@code linebreak [true, false]} replace new lines with &lt;br&gt;; for
 * HTML output.</li>
 * </ul>
 * <p>
 * Example use:
 * <pre>
 * &lt;h:outputText value="#{bean.stringValue}"&gt;
 *   &lt;f:converter converterId="stringConverter" /&gt;
 *   &lt;f:attribute name="case" value="camel" /&gt;
 *   &lt;f:attribute name="length" value="12" /&gt;
 * &lt;/h:outputText&gt;</pre>
 *
 * @author Key Bridge
 * @since v2.9.0 added 12/16/17 replaces ConvertLength[X] classes
 */
public class StringConverter implements Converter {

  private static final int DEFAULT_LENGTH = 8;

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null || String.valueOf(value).trim().isEmpty()) {
      return String.valueOf(value);
    }
    /**
     * Make a copy of the value.
     */
    String string = String.valueOf(value).trim();
    /**
     * Trim the string length.
     */
    Integer maxStringLength = getMaxStringLength(component);
    if (maxStringLength != null && string.length() > maxStringLength) {
      string = string.substring(0, maxStringLength - 3) + "...";
    }
    /**
     * Convert the case.
     */
    String stringCase = getCase(component);
    if (stringCase != null) {
      switch (stringCase) {
        case "upper":
          string = string.toUpperCase();
          break;
        case "lower":
          string = string.toLowerCase();
          break;
        case "camel":
          string = camelCase(string);
          break;
        case "proper":
          string = properCase(string);
          break;
      }
    }
    /**
     * Check for empty.
     */
    if (!getEmpty(component)) {
      return string.trim().isEmpty() ? null : string;
    }
    /**
     * Check for html line breaks.
     */
    if (getBreak(component)) {
      string = string.replaceAll("\n", "<br/>\n");
    }
    /**
     * Return the string.
     */
    return string;
  }

  /**
   * Extract the "length" parameter provided as a component attribute. If no
   * length is provided then the default default length of 8 characters is used.
   * <p>
   * Example: {@code &lt;f:attribute name="length" value="16"/&gt;}
   *
   * @param component the UI component
   * @return the maximum string length
   */
  private Integer getMaxStringLength(UIComponent component) {
    return component.getAttributes().containsKey("length")
           ? Integer.parseInt((String) component.getAttributes().get("length"))
           : null;
  }

  /**
   * Extract the "case" attribute, which determines whether and how the string
   * case should be modified. Expect one of ["upper", "lower", "proper",
   * "camel"]
   * <p>
   * Example: {@code &lt;f:attribute name="case" value="upper"/&gt;}
   *
   * @param component the UI component
   * @return the string case conversion rule.
   */
  private String getCase(UIComponent component) {
    return component.getAttributes().containsKey("case")
           ? (String) component.getAttributes().get("case")
           : null;
  }

  /**
   * Extract the "empty" attribute, which determines whether empty strings are
   * allowed. If set to false then empty strings are set to NULL. Default is
   * TRUE.
   * <p>
   * Example: {@code &lt;f:attribute name="empty" value="false"/&gt;}
   *
   * @param component the UI component
   * @return the empty string conversion rule.
   */
  private boolean getEmpty(UIComponent component) {
    return component.getAttributes().containsKey("empty")
           ? Boolean.parseBoolean((String) component.getAttributes().get("empty"))
           : true;
  }

  /**
   * Extract the "break" attribute, which determines whether new lines should be
   * converters to &lt;br&gt; html. Default is FALSE.
   * <p>
   * Example: {@code &lt;f:attribute name="break" value="true"/&gt;}
   *
   * @param component the UI component
   * @return the new line conversion rule.
   */
  private boolean getBreak(UIComponent component) {
    return component.getAttributes().containsKey("break")
           ? Boolean.parseBoolean((String) component.getAttributes().get("break"))
           : false;
  }

  /**
   * Format the input string to Proper-Case by capitalizing the first character
   * of each word and forcing all other characters to lower case.
   * <p>
   * This method parses words on the following regex: {@code [\\s/+_-]+} and
   * includes a null check to ignore null or empty strings. e.g.
   * "Akin_Gump-Strauss+Hauer Feld LLP" is converted to "Akin Gump Strauss Hauer
   * Feld Llp".
   *
   * @param inputString A free-text string. May contain one or more words.
   * @return The input string converted to Proper case.
   */
  private String properCase(final String inputString) {
    String string = inputString.trim().toLowerCase();
    /**
     * If the string is a single character just return it in uppercase.
     */
    if (string.length() == 1) {
      return string.toUpperCase();
    }
    if (string.length() > 2) {
      /**
       * If the string contains enough characters to be two words then process
       * each word.
       */
      StringBuilder sb = new StringBuilder();
      for (String subString : string.split("[\\s/+_-]+")) {
        if (!sb.toString().trim().isEmpty()) {
          sb.append(" ");
        }
        /**
         * Surround with a try catch since this is throwing 'String index out of
         * range: 1' errors.
         */
        try {
          sb.append(subString.substring(0, 1).toUpperCase()).append(subString.substring(1));
        } catch (Exception e) {
          sb.append(subString.toLowerCase().trim());
        }
      }
      return sb.toString();
    } else {
      /**
       * The string is a single word.
       */
      return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
  }

  /**
   * Format the provided string by capitalizing the first character and forcing
   * the rest to lower case, then eliminating any spaces. e.g. The String "Camel
   * CASE" is converted to "CamelCase".
   *
   * @see <a href="http://en.wikipedia.org/wiki/CamelCase">CamelCase</a>
   * @param string the input string converted to Camel case.
   * @return the string converted to CamelCase
   */
  private String camelCase(final String string) {
    return properCase(string).replaceAll("\\W", "");
  }
}
