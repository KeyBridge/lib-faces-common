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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

/**
 * Pretty print a currency amount. This attempts to output a pretty-print label
 * with the currency symbol, the amount, and the currency name.
 * <p>
 * The currency is declared with an f:attribute component.
 *
 * @author Key Bridge
 * @since v5.0.5 created 2020-10-24
 */
public class CurrencyRateConverter extends AbstractConverter {

  private static final Currency US_DOLLAR = Currency.getInstance(Locale.US);
  private static final Map<Currency, String> SYMBOLS;

  static {
    SYMBOLS = new HashMap<>();
    SYMBOLS.put(Currency.getInstance("AUD"), "&#x24;");
    SYMBOLS.put(Currency.getInstance("CAD"), "&#x24;");
    SYMBOLS.put(Currency.getInstance("CHF"), "&#x20a3;");
    SYMBOLS.put(Currency.getInstance("EUR"), "&#x20ac;");
    SYMBOLS.put(Currency.getInstance("GBP"), "&#xa3;");
    SYMBOLS.put(Currency.getInstance("HKD"), "&#x24;");
    SYMBOLS.put(Currency.getInstance("ILS"), "&#x20aa;");
    SYMBOLS.put(Currency.getInstance("TWD"), "&#x24;");
    SYMBOLS.put(Currency.getInstance("USD"), "&#x24;");
  }

  /**
   * {@inheritDoc}
   *
   * @param value A pretty print currency label. e.g.	"$38.15 USD" or HTML
   *              "&#x24;38.15 USD"
   * @return a BigDecimal instance
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    Currency currency = US_DOLLAR;
    String number = value;
    if (number.contains(" ")) {
      String[] tokens = number.split(" ");
      number = tokens[0];
      currency = Currency.getInstance(tokens[1]);
    }
    if (number.contains(";")) {
      number = number.split(";")[1];
    }
    if (number.matches("^\\D.*")) {
      number = number.substring(1);
    }
    return new BigDecimal(Double.parseDouble(number)).setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
  }

  /**
   * {@inheritDoc}
   *
   * @return Produces a pretty print currency label. e.g.	"$38.15 USD"
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null) {
      return null;
    }
    Currency currency = getCurrency(component);
    BigDecimal number = (BigDecimal) value;
    number = number.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    return (SYMBOLS.containsKey(currency) ? SYMBOLS.get(currency) : "")
      + number
      + " " + currency.getCurrencyCode();
  }

  /**
   * Get a currency from the UI component 'currency' attribute. If none is
   * provided then return US Dollars.
   *
   * @param context   the context
   * @param component the parent component
   * @return a non-null currency instance
   */
  private Currency getCurrency(UIComponent component) {
    Currency currency = component == null ? US_DOLLAR : (Currency) component.getAttributes().get("currency");
    return (currency == null)
           ? US_DOLLAR
           : currency;

  }

}
