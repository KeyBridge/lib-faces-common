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
package org.caulfield.lib.faces.validator;

import javax.faces.application.FacesMessage;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 * An abstract Faces Validator implementation. This class provides helpful
 * shortcuts for implementing a Faces validator.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public abstract class AValidator implements Validator {

  /**
   * Shortcut to throw a SEVERITY_WARN exception.
   * <p>
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwWarningException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_WARN, messageSummary, messageDetail);
  }

  /**
   * Shortcut to throw a SEVERITY_ERROR exception.
   * <p>
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwErrorException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_ERROR, messageSummary, messageDetail);
  }

  /**
   * Shortcut to throw a SEVERITY_FATAL exception.
   * <p>
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwFatalException(String messageSummary, String messageDetail) {
    throwValidatorException(FacesMessage.SEVERITY_FATAL, messageSummary, messageDetail);
  }

  /**
   * Throw a new ValidatorException instance with a descriptive explanation (for
   * email)
   * <p>
   * @param severity       the message severity
   * @param messageSummary the message summary
   * @param messageDetail  the message detail
   */
  protected void throwValidatorException(FacesMessage.Severity severity, String messageSummary, String messageDetail) {
    throw new ValidatorException(new FacesMessage(severity, messageSummary, messageDetail));
  }

}
