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
package ch.keybridge.faces;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Bread crumbs indicate the current page's location within a navigational
 * hierarchy. This managed bean provides a Primefaces menu model to
 * programmatically build a Breadcrumb widget.
 *
 * @author Jesse Caulfield
 * @since 2.2.0 added 05/17/16
 */
@FacesComponent(value = "breadCrumbBean")
public class BreadCrumbBean extends UINamingContainer {

  /**
   * The PrimeFaces Menu model.
   * <p>
   * Developer note: This must be a {@code MenuModel} interface to avoid
   * ClassCastExceptions (manifested as IllegalArgumentException) at
   * {@code org.primefaces.component.breadcrumb.BreadCrumb.getModel()}
   */
  private List<Item> breadcrumb;

  /**
   * Creates a new instance of BreadCrumbBean
   */
  public BreadCrumbBean() {
  }

  /**
   * Get the Page file name, proper formatted. If the current page is the
   * default (i.e. "index.xhtml") then the immediate directory name is used
   * instead.
   * <p>
   * For example, the URI path "http://example.com/directory/index.xhtml" will
   * return the page label "Directory".
   *
   * @return the URI page file name.
   */
  public String getPage() {
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    String[] uriPath = request.getRequestURI().replaceFirst(request.getContextPath(), "").split("/");
    String page = null;
    for (String destination : uriPath) {
      if ("index.xhtml".equals(destination)) {
        continue;
      }
      page = toProperCase(destination.replace(".xhtml", ""));
    }
    return page;
  }

  /**
   * Get a list of entries in the URI, each with a component URI and label,
   * using "Home" as the root label.
   * <p>
   * This is used to dynamically build a bread crumb menu.
   *
   * @return a list of Item enties.
   */
  public List<Item> getBreadcrumb() {
    return getBreadcrumb("Home");
  }

  /**
   * Get a list of entries in the URI, each with a component URI and label.
   * <p>
   * This is used to dynamically build a bread crumb menu.
   *
   * @param applicationName the application name
   * @return a list of Item enties.
   */
  public List<Item> getBreadcrumb(String applicationName) {
    breadcrumb = new ArrayList<>();
    /**
     * Split the URI into its components, then build a menu item for each.
     * <p>
     * The URI looks like '/[contextRoot]/registration/lpaux.xhtml'.
     * <p>
     * The URL is fully qualified and looks like
     * 'http://localhost:8080/[contextRoot]/registration/lpaux.xhtml'
     * <p>
     * Use the HttpServletRequest to remove the 'contextRoot', then build a
     * bread crumb menu.
     * <p>
     * Use replaceFirst to only clear the contextPath. replace replaces all
     * instances of the search string.
     */
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    /**
     * Add the ROOT menu item
     */
    breadcrumb.add(new Item(applicationName != null ? applicationName : "Home", request.getContextPath()));
    /**
     * Add the current path.
     */
    String[] uriPath = request.getRequestURI().replaceFirst(request.getContextPath(), "").split("/");
    StringBuilder buildUriPath = new StringBuilder(request.getContextPath());
    for (String destination : uriPath) {
      if (!destination.isEmpty()) {
        if ("index.xhtml".equals(destination)) {
          continue;
        }
        buildUriPath.append("/").append(destination);
        breadcrumb.add(new Item(toProperCase(destination.replace(".xhtml", "")), buildUriPath.toString()));
      }
    }
    return breadcrumb;
  }

  /**
   * Format the input string to Proper-Case by capitalizing the first character
   * of each word and forcing all other characters to lower case.
   * <p>
   * This method includes a null check and will ignore null or empty strings.
   *
   * @param string A free-text string. May contain one or more words.
   * @return The input string converted to Proper case.
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  private String toProperCase(String string) {
    if (string == null || string.isEmpty()) {
      return null;
    }
    string = string.toLowerCase();
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
      for (String subString : string.split("[\\s/+()@_-]")) {
        if (!sb.toString().isEmpty()) {
          sb.append(" ");
        }
        /**
         * Surround with a try catch since this is throwing 'String index out of
         * range: 1' errors.
         */
        try {
          sb.append(subString.substring(0, 1).toUpperCase()).append(subString.substring(1));
        } catch (Exception e) {
          sb.append(subString.toLowerCase());
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
   * Inner class describing a Menu Item container for a URL and label.
   */
  public static class Item {

    /**
     * The Text label.
     */
    private String label;
    /**
     * The relative URL.
     */
    private String url;

    public Item(String label, String url) {
      this.label = label;
      this.url = url;
    }

    /**
     * Get the label.
     *
     * @return the label.
     */
    public String getLabel() {
      return label;
    }

    /**
     * Set the label.
     *
     * @param label the label.
     */
    public void setLabel(String label) {
      this.label = label;
    }

    /**
     * Get the URL.
     *
     * @return the url.
     */
    public String getUrl() {
      return url;
    }

    /**
     * Set the url.
     *
     * @param url the url.
     */
    public void setUrl(String url) {
      this.url = url;
    }
  }
}
