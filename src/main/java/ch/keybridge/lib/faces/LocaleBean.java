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
package ch.keybridge.lib.faces;

import java.io.Serializable;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 * Session scoped managed bean to support user locale selection. Detects the
 * locale for an application based on the initial browser request and use it
 * throughout the browsing session untill the user specifically changes the
 * locale.
 * <p>
 * To set the current locale of the views, bind it to the <f:view> of your
 * master template.
 * <p>
 * <f:view locale="#{localeManager.locale}">
 * <p>
 * To change it, bind it to a <h:selectOneMenu> with language options.
 * <p>
 * <h:form>
 * <h:selectOneMenu value="#{localeManager.language}" onchange="submit()">
 * <f:selectItem itemValue="en" itemLabel="English" />
 * <f:selectItem itemValue="nl" itemLabel="Nederlands" />
 * <f:selectItem itemValue="es" itemLabel="Español" />
 * </h:selectOneMenu>
 * </h:form>
 * <p>
 * To improve SEO of your internationalized pages (otherwise it would be marked
 * as duplicate content), bind language to html as well.
 * <p>
 * &lt;html lang="#{localeManager.language}"&gt;
 * <p>
 * Developer note: A convenient way to change the locale from a JSF page is to
 * call the method {@code getLanguage(String language)} using an ISO2 language
 * code. For example {@code 'en'} for English or {@code 'fr'} for French. e.g.
 * {@code &lt;h:commandLink action="#{localeBean.setLanguage('fr')}"&gt;FR&lt;/h:commandLink&gt;}
 * <p>
 * This is based upon Balusc's excellent explanation at
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
 */
@Named(value = "localeBean")
@SessionScoped
public class LocaleBean implements Serializable {

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
  private Locale locale;

  /**
   * Initialize the Locale configuration. If there is no requested locale in the
   * FacesContext then the system default locale is used.
   */
  @PostConstruct
  public void init() {
    locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    /**
     * Null check.
     */
    if (locale == null) {
      locale = Locale.getDefault();
    }
  }

  /**
   * Get the current Locale.
   *
   * @return the current Locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Set the current Locale.
   *
   * @param locale the current Locale
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
    FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
  }

  /**
   * Get the language. This is translated from the current Locale.
   *
   * @return the locale language.
   */
  public String getLanguage() {
    return locale.getLanguage();
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
