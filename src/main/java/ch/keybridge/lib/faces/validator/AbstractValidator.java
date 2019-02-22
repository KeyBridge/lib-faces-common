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
package ch.keybridge.lib.faces.validator;

import javax.enterprise.inject.spi.CDI;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * An abstract Faces Validator implementation. This class provides helpful
 * shortcuts for implementing a Faces validator.
 *
 * @author Jesse Caulfield
 */
public abstract class AbstractValidator implements Validator {

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
   * Access to the current container {@code Context &amp; Dependency Injection}
   * context and select a child Instance for the given required type.
   * Parameters: subtype - a Class representing the required type
   * <p>
   * Developer note: Originally the Named beans were injected directly into
   * Validators and Converters using OmniFaces CDI bean manager. However this
   * appears to have broken / is not working after upgrading to GF 4.1.1. A
   * (more portable) solution is to access the container CDI directly. This
   * method provides a shortcut.
   *
   * @param <U>       the required type
   * @param namedBean the CDI managed bean class; should be annotated with
   *                  {@code @Named}
   * @return an instance of the required type
   */
  protected final <U> U CDISelect(Class<U> namedBean) {
    return CDI.current().select(namedBean).get();
  }

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
   * Shortcut to throw a SEVERITY_WARN exception.
   *
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwWarningException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_WARN, messageSummary, messageDetail);
  }

  /**
   * Shortcut to throw a SEVERITY_ERROR exception.
   *
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwErrorException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_ERROR, messageSummary, messageDetail);
  }

  /**
   * Shortcut to throw a SEVERITY_FATAL exception.
   *
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwFatalException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_FATAL, messageSummary, messageDetail);
  }

  /**
   * Throw a new ValidatorException instance with a descriptive explanation (for
   * email)
   *
   * @param severity       the message severity
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwValidatorException(FacesMessage.Severity severity, String messageSummary, String messageDetail) {
    throw new ValidatorException(new FacesMessage(severity, messageSummary, messageDetail));
  }

}
