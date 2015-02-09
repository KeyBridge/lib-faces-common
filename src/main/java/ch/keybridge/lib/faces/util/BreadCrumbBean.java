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
package ch.keybridge.lib.faces.util;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

/**
 * Bread crumbs indicate the current page's location within a navigational
 * hierarchy. This managed bean provides a Primefaces menu model to
 * programmatically build a Breadcrumb widget.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
@Named(value = "breadCrumbBean")
@RequestScoped
public class BreadCrumbBean {

  /**
   * The PrimeFaces Menu model.
   */
  private DefaultMenuModel menuModel;

  /**
   * Creates a new instance of BreadCrumbBean
   */
  public BreadCrumbBean() {
  }

  /**
   * Get a Primefaces MenuModel. This is used to dynamically build a bread crumb
   * menu. MenuModel API is used to create PrimeFaces menu components like menu,
   * tieredMenu, menubar programmatically. This is a very useful feature since
   * in many cases application menus are not static and vary depending on user
   * roles.
   * <p/>
   * @return a Primefaces MenuModel instance
   */
  public MenuModel getMenuModel() {
    menuModel = new DefaultMenuModel();
    /**
     * Add the HOME menu item
     */
    DefaultMenuItem home = new DefaultMenuItem("Home");
    home.setUrl("/index.xhtml");
    menuModel.addElement(home);
    /**
     * Split the URI into its components, then build a menu item for each.
     * <p/>
     * The URI looks like '/[contextRoot]/registration/lpaux.xhtml'.
     * <p/>
     * The URL is fully qualified and looks like
     * 'http://localhost:8080/[contextRoot]/registration/lpaux.xhtml'
     * <p/>
     * Use the HttpServletRequest to remove the 'contextRoot', then build a
     * bread crumb menu.
     * <p/>
     * Use replaceFirst to only clear the contextPath. replace replaces all
     * instances of the search string.
     */
    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    String[] uriPath = request.getRequestURI().replaceFirst(request.getContextPath(), "").split("/");
    StringBuilder buildUriPath = new StringBuilder();
    for (String destination : uriPath) {
      if (!destination.isEmpty()) {
        if ("index.xhtml".equals(destination)) {
          continue;
        }
        buildUriPath.append("/").append(destination);
        DefaultMenuItem menuItem = new DefaultMenuItem(toProperCase(destination.replace(".xhtml", "")));
        menuItem.setUrl(buildUriPath.toString());
        menuModel.addElement(menuItem);
      }
    }
    return menuModel;
  }

  /**
   * Format the input string to Proper-Case by capitalizing the first character
   * of each word and forcing all other characters to lower case.
   * <p/>
   * This method includes a null check and will ignore null or empty strings.
   * <p/>
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
}
