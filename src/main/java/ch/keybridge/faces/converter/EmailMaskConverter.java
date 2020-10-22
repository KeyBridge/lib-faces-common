package ch.keybridge.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * JSF converter to mask an email address by masking the email portion and
 * revealing the domain. e.g. "info@example.com" becomes "----@example.com".
 *
 * @author Key Bridge
 * @since v0.39.0 created 2020-01-24
 */
@FacesConverter(value = "emailMaskConverter")
public class EmailMaskConverter implements Converter {

  /**
   * {@inheritDoc }
   * <p>
   * No modification.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String email) {
    return email.contains("@")
           ? email.split("@")[0].replaceAll(".", "x") + email.split("@")[1]
           : email;
  }

  /**
   * {@inheritDoc }
   * <p>
   * Return the NAME portion of an email address.
   */
  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    String email = String.valueOf(value);
    return email.contains("@")
           ? email.split("@")[0].replaceAll(".", "x") + "@" + email.split("@")[1]
           : email;
  }

}
