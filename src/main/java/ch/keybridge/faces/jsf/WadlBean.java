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
package ch.keybridge.faces.jsf;

import ch.keybridge.faces.FacesUtil;
import ch.keybridge.faces.converter.MarkdownConverter;
import ch.keybridge.faces.wadl.AbstractWadlBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * WADL label provider bean that searches in the wadl.properties file.
 *
 * @author Key Bridge
 * @since v0.8.4 added 05/01/17
 * @since v4.0.0 moved from web-app-template 0.2.2 to faces-common
 */
@Named(value = "wadlBean")
@RequestScoped
public class WadlBean extends AbstractWadlBean {

  private static final Logger LOG = Logger.getLogger(WadlBean.class.getName());

  /**
   * The default context root - localhost.
   */
  private static final String CONTEXT_DEFAULT = "http://localhost:8080";

  /**
   * A markdown to HTML text converter.
   */
  private MarkdownConverter markdownConverter;

  /**
   * Load the WADL from the 'api' path.
   */
  @PostConstruct
  protected void postConstruct() {
    /**
     * Autoload the WADL file if the rest context is "api", "rest", "resource",
     * "resources", "webresources". Else must directly call
     * {@code super.load(rest-context)}.
     */
    this.autoload();
//    super.load(buildWadlUrl("rest-context"));  // ONLY if not typical
    /**
     * Initialize a MarkdownConverter instance. This is used to convert MD
     * labels and descriptions.
     */
    this.markdownConverter = new MarkdownConverter();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Search includes the various common rest-contexts such as: ["api", "rest",
   * "resource", "resources", "webresources"]
   */
  @Override
  public void autoload() {
    /**
     * Get the current context path (i.e. the application context root). This is
     * copied from FacesUtil.getContextPath() and excludes common port numbers
     * from the URI when present.
     */
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    String contextRoot = new StringBuilder()
      .append(externalContext.getRequestScheme())
      .append("://")
      .append(externalContext.getRequestServerName())
      .append((externalContext.getRequestServerPort() != 80 && externalContext.getRequestServerPort() != 443)
              ? ":" + externalContext.getRequestServerPort()
              : "")
      .append(externalContext.getRequestContextPath())
      .toString();
    /**
     * Try to load the WADL from various commonly used JAXRS contexts. If the
     * application declares a custom context is must be specified. Try to
     * connect to the localhost. If that fails then try the currently declared
     * URL host.
     */
    for (String context : new String[]{CONTEXT_DEFAULT, contextRoot}) {
      for (String restContext : new String[]{"api", "rs", "rest", "resource", "resources", "webresources"}) {
        load(buildWadlUrl(context, restContext));
        if (wadl != null) {
          break;
        }
      }
      if (wadl == null) {
        LOG.log(Level.FINE, "Error downloading WADL file.");
      }
    }
  }

  /**
   * Get the WADL location.
   *
   * @return the WADL location.
   */
  private String buildWadlUrl(String contextRoot, String restContext) {
//    return FacesUtil.getContextPath() + "/" + restContext + "/application.wadl"; // legacy strategy - generally works but includes port numbers
    return new StringBuilder()
      .append(contextRoot)
      .append(contextRoot.endsWith("/") ? "" : "/")
      .append(restContext)
      .append(restContext.endsWith("/") ? "" : "/")
      .append("application.wadl")
      .toString();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Split a CamelCase string by inserting a space character BEFORE every
   * capital character. e.g. "CalculateCountryIntersect" is transformed to
   * "Calculate Country Intersect"
   */
  @Override
  public String parseMethodId(String methodId) {
    return splitCamelCase(methodId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLabel(String key) {
    try {
      String response = FacesUtil.evaluateExpression("#{wadl." + key + "}");
      if (response.startsWith("???")) {
        LOG.log(Level.INFO, "wadlLabelProvider label: {0}", key);
      }
      return response.startsWith("???") ? null : toHtml(response);
    } catch (Exception e) {
      LOG.log(Level.INFO, "{0} label not found.", key);
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * The response may be Markdown formatted.
   */
  @Override
  public String getLabel(String method, String parameter) {
    try {
      String response = FacesUtil.evaluateExpression("#{wadl." + method + "_" + parameter + "}");
      if (response.startsWith("???")) {
        LOG.log(Level.INFO, "wadlLabelProvider label: {0}_{1}", new Object[]{method, parameter});
      }
      return response.startsWith("???") ? null : toHtml(response);
    } catch (Exception e) {
      LOG.log(Level.INFO, "{0}-{1} label not found.", new Object[]{method, parameter});
      return null;
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * The response may be Markdown formatted.
   */
  @Override
  public String getMethodDescription(String method) {
    try {
      String response = FacesUtil.evaluateExpression("#{wadl." + method + "}");
      if (response.startsWith("???")) {
        LOG.log(Level.INFO, "wadlLabelProvider description: {0}", method);
      }
      return response.startsWith("???") ? null : toHtml(response);
    } catch (Exception e) {
      LOG.log(Level.INFO, "{0} description not found.", method);
      return null;
    }
  }

  /**
   * Convert (potential) markdown text to HTML text
   *
   * @param markdown markdown text
   * @return HTML text
   */
  private String toHtml(String markdown) {
    return markdownConverter.getAsString(null, null, markdown);
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
   * @param inputString the source string
   * @return the transformed string
   * @since v4.0.0 copied from TextUtility
   */
  public String toProperCase(final String inputString) {
    if (inputString == null || inputString.trim().isEmpty()) {
      return null;
    }
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
   * Split a CamelCase string by inserting a space character BEFORE every
   * capital character. e.g. {@code "CalculateCountryIntersect"} is transformed
   * to {@code "Calculate Country Intersec"}
   *
   * @param inputString the source string
   * @return the tranformed string
   * @since v4.0.0 copied from TextUtility
   */
  public String splitCamelCase(final String inputString) {
    if (inputString == null || inputString.trim().isEmpty()) {
      return null;
    }
    String string = inputString.trim();

    /**
     * If the string contains enough characters to be two words then process
     * each word.
     */
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      char character = string.charAt(i);
      if (Character.isUpperCase(character)) {
        sb.append(" ");
      }
      sb.append(character);
    }
    return sb.toString().trim();
  }

}
