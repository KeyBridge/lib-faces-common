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
import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

/**
 * RequestScoped scoped managed bean to support user locale selection. Detects
 * the locale for an application based on the initial browser request.
 * <p>
 * To set the current locale of the views, bind it to the &lt;f:view&gt; of your
 * master template.
 * <p>
 * &lt;f:view locale="#{localeManager.locale}"&gt;
 * <p>
 * To change it, bind it to a &lt;h:selectOneMenu&gt; with language options.
 * <p>
 * &lt;h:form&gt; &lt;h:selectOneMenu value="#{localeManager.language}"
 * onchange="submit()"&gt; &lt;f:selectItem itemValue="en" itemLabel="English"
 * /&gt; &lt;f:selectItem itemValue="nl" itemLabel="Nederlands" /&gt;
 * &lt;f:selectItem itemValue="es" itemLabel="Español" /&gt;
 * &lt;/h:selectOneMenu&gt; &lt;/h:form&gt;
 * <p>
 * To improve SEO of internationalized pages (otherwise it would be marked as
 * duplicate content), bind language to html as well.
 * <p>
 * &lt;html lang="#{localeManager.language}"&gt;
 * <p>
 * Developer note: A convenient way to change the locale from a JSF page is to
 * call the method {@code getLanguage(String language)} using an ISO2 language
 * code. For example {@code 'en'} for English or {@code 'fr'} for French. e.g.
 * {@code &lt;h:commandLink action="#{localeBean.setLanguage('fr')}"&gt;FR&lt;/h:commandLink&gt;}
 * <p>
 * This implementation is based upon Balusc's excellent explanation at
 * http://stackoverflow.com/questions/5388426/jsf-2-0-set-locale-throughout-session-from-browser-and-programmatically
 * and
 * <a href="http://jdevelopment.nl/internationalization-jsf-utf8-encoded-properties-files/">jdevelopment</a>
 * and
 * <a href="http://stackoverflow.com/questions/4830588/localization-in-jsf-how-to-remember-selected-locale-per-session-instead-of-per">Stack
 * Overflow</a>
 *
 * @author Key Bridge
 * @since v0.2.0 created 03/08/17 to support French Canada translation
 * @since v3.0.0 moved 01/15/18 to faces-common
 * @see
 * <a href="https://www.oracle.com/technetwork/java/javase/javase7locales-334809.html">Locales</a>
 */
public class LocaleBean implements Serializable {

  /**
   * ".locale" The locale cookie name.
   */
  private static final String LOCALE_COOKIE = ".locale";

  /**
   * Reference
   * http://stackoverflow.com/questions/4830588/localization-in-jsf-how-to-remember-selected-locale-per-session-instead-of-per
   * and
   * http://jdevelopment.nl/internationalization-jsf-utf8-encoded-properties-files/
   */
  /**
   * The Locale. A Locale object represents a specific geographical, political,
   * or cultural region. An operation that requires a Locale to perform its task
   * is called locale-sensitive and uses the Locale to tailor information for
   * the user. For example, displaying a number is a locale-sensitive operation—
   * the number should be formatted according to the customs and conventions of
   * the user's native country, region, or culture.
   */
  private Locale locale = Locale.ENGLISH;

  /**
   * Initialize the Locale configuration. If there is no requested locale in the
   * FacesContext then the system default locale is used.
   */
  @PostConstruct
  public void postConstruct() {
    /**
     * Get the locale specified in the user cookie.
     */
    Cookie localeCookie = FacesUtil.getCookie(LOCALE_COOKIE);
    if (localeCookie != null) {
      /**
       * Set the locale to the desired language.
       */
      locale = new Locale(localeCookie.getValue());
    } else {
      /**
       * Return the preferred Locale in which the client will accept content.
       */
      locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    }
    /**
     * Always provide a locale.
     */
    if (locale == null) {
      locale = Locale.getDefault();
    }
    /**
     * Set a new global, long lived, locale cookie if needed.
     */
    if (localeCookie == null) {
      FacesUtil.addCookieGlobal(LOCALE_COOKIE, locale.getLanguage());
    }
  }

  /**
   * Parse a locale from a language.
   *
   * @param language anISO 639 language code
   * @return the locale; null if not found
   */
  private Locale fromLanguage(String language) {
    for (Locale availableLocale : Locale.getAvailableLocales()) {
      if (availableLocale.getLanguage().equals(language)) {
        return availableLocale;
      }
    }
    return new Locale(language);
  }

  /**
   * Get the current Locale.
   *
   * @return the current Locale
   */
  public Locale getLocale() {
    if (locale == null) {
      postConstruct();
    }
    return locale;
  }

  /**
   * Set the current Locale.
   *
   * @param locale the current Locale
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
    FacesUtil.addCookie(LOCALE_COOKIE, locale.getLanguage());
    FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
  }

  /**
   * Get the language. This is translated from the current Locale.
   *
   * @return the locale language.
   */
  public String getLanguage() {
    return getLocale().getLanguage();
  }

  /**
   * Set the language. This is translated into the current Locale.
   *
   * @param language a locale language
   */
  public void setLanguage(String language) {
    setLocale(new Locale(language));
    FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
  }
}
