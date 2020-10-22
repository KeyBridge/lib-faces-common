package ch.keybridge.faces.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * JSF converter to extract the email domain name (and strip the user name) from
 * an email address. This is used to partially obscure the user information when
 * displaying email addresses.
 *
 * @author Key Bridge
 * @since v0.10.0 added 01/17/18
 */
@FacesConverter(value = "emailDomainConverter")
public class EmailDomainConverter implements Converter {

  /**
   * {@inheritDoc }
   * <p>
   * No modification.
   */
  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    return value;
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
           ? email.split("@")[1]
           : email;
  }
}
