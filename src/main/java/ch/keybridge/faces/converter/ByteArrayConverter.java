package ch.keybridge.faces.converter;

import java.util.Base64;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

/**
 * The encode and decode converter between String and byte array for save raw
 * file data, used in Attachment entity
 * <p>
 * @author Dmitry Farafonov for Key Bridge
 * @since v0.3.0 created 05/20/17 to to develop, simplify and automate the
 * fields.
 */
@FacesConverter(value = "byteArrayConverter")
public class ByteArrayConverter implements Converter {

  @Override
  public Object getAsObject(FacesContext context, UIComponent component, String value) {
    if (value == null) {
      return null;
    } else {
      try {
        // byte[] raw = Base64.getDecoder().decode(value);
        return Base64.getMimeDecoder().decode(value);
      } catch (Exception e) {
        throw new ConverterException(
          new FacesMessage(FacesMessage.SEVERITY_ERROR,
                           "Converter Data error",
                           "The contents of the '" + component.getClientId(context)
                           + "' caused the error in conversion")
        );
      }
    }
  }

  @Override
  public String getAsString(FacesContext context, UIComponent component, Object value) {
    if (value == null) {
      return null;
    } else {
      try {
        // String str = Base64.getEncoder().encodeToString((byte[]) value);
        return Base64.getMimeEncoder().encodeToString((byte[]) value);
      } catch (Exception e) {
        throw new ConverterException(
          new FacesMessage(FacesMessage.SEVERITY_ERROR,
                           "Converter Data error",
                           "The contents of the '" + component.getClientId(context)
                           + "' caused the error in conversion")
        );
      }
    }
  }
}
