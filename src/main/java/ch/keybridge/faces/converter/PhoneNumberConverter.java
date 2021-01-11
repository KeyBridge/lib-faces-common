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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * JSF converter to pretty print phone numbers using the Google `libphonenumber`
 * library.
 *
 * @see <a href="https://github.com/google/libphonenumber">libphonenumber</a>
 * @author Key Bridge
 * @since v0.41.4 created 2020-08-08
 */
public class PhoneNumberConverter implements Converter {

  private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

  /**
   * {@inheritDoc} capture a pretty print phone number.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    }
    try {
      Phonenumber.PhoneNumber phoneNumber = PHONE_UTIL.parse(value, "US");
      return String.valueOf(phoneNumber.getNationalNumber());
    } catch (NumberParseException numberParseException) {
      return value;
    }
  }

  /**
   * {@inheritDoc} pretty print a phone number.
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null) {
      return null;
    }
    String numberSequence = String.valueOf(value);
    try {
      Phonenumber.PhoneNumber phoneNumber = PHONE_UTIL.parse(numberSequence, "US");
      return PHONE_UTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
    } catch (NumberParseException numberParseException) {
      return numberSequence;
    }
  }

}
