/*
 *  Copyright (C) 2014 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 *  All rights reserved. Use is subject to license terms.
 *
 *  Software Code is protected by Caulfield Copyrights. Caulfield hereby reserves
 *  all rights in and to Caulfield Copyrights and no license is granted under
 *  Caulfield Copyrights in this Software License Agreement. Caulfield generally
 *  licenses Caulfield Copyrights for commercialization pursuant to the terms of
 *  either Caulfield's Standard Software Source Code License Agreement or
 *  Caulfield's Standard Product License Agreement.
 *
 *  A copy of either License Agreement can be obtained on request by email from:
 *  info@caufield.org.
 */
package ch.keybridge.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;

/**
 * An abstract validating Faces converter. This class provides helpful shortcuts
 * for applying validation css when converting a ui component.
 *
 * @author Jesse Caulfield
 * @since v4.1.1 created 10/29/19
 */
public abstract class AbstractConverter implements Converter {

  /**
   * The JSF styleclass tag.
   */
  protected static final String STYLECLASS = "styleClass";
  /**
   * "is-invalid". The BS4 css marking an invalid form input.
   */
  protected static final String INVALID_CSS = "is-invalid";
  /**
   * "is-valid". The BS4 css marking a valid form input.
   */
  protected static final String VALID_CSS = "is-valid";

  /**
   * Add CSS to the indicated (form input) component marking it as valid or
   * invalid. This method adds a BS4 form validation CSS to indicate valid
   * (green) or invalid (red).
   *
   * @param component the form component
   * @param isValid   the validity status.
   * @since v4.0.0 added 01/28/19
   */
  protected void setValidityStatus(UIComponent component, boolean isValid) {
    /**
     * Get and clear the current styleclass, then add the validity status.
     */
    if (component != null) {
      String styleClass = (String) component.getAttributes().get(STYLECLASS);
      styleClass = styleClass.replaceAll(INVALID_CSS, "");
      styleClass = styleClass.replaceAll(VALID_CSS, "");
      styleClass += " " + (isValid ? VALID_CSS : INVALID_CSS);
      component.getAttributes().put(STYLECLASS, styleClass);
    }
  }

  /**
   * Remove the validity status from the form input component.
   *
   * @param component the form component
   * @since v4.1.1 added 10/12/19
   */
  protected void unsetValidityStatus(UIComponent component) {
    /**
     * Get and clear the current styleclass, then add the validity status.
     */
    if (component != null) {
      String styleClass = (String) component.getAttributes().get(STYLECLASS);
      styleClass = styleClass.replaceAll(INVALID_CSS, "");
      styleClass = styleClass.replaceAll(VALID_CSS, "");
      component.getAttributes().put(STYLECLASS, styleClass);
    }
  }

}
