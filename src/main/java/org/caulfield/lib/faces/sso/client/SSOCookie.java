package org.caulfield.lib.faces.sso.client;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.xml.bind.annotation.*;
import org.apache.commons.codec.binary.Base64;

/**
 * <p>
 * Java class for ssoCookie complex type.
 * <p>
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * <
 * pre>
 * &lt;complexType name="ssoCookie"> &lt;complexContent> &lt;restriction
 * base="{http://www.w3.org/2001/XMLSchema}anyType"> &lt;sequence> &lt;element
 * name="groups" type="{http://www.w3.org/2001/XMLSchema}string"/>
 * &lt;/sequence> &lt;attribute name="userName" use="required"
 * type="{http://www.w3.org/2001/XMLSchema}string" /> &lt;attribute
 * name="password" type="{http://www.w3.org/2001/XMLSchema}string" />
 * &lt;attribute name="uuid" use="required"
 * type="{http://www.w3.org/2001/XMLSchema}string" /> &lt;/restriction>
 * &lt;/complexContent> &lt;/complexType>
 * </pre>
 * <p>
 * <p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ssoCookie", propOrder = {
  "groups"
})
public class SSOCookie {

  /**
   * SSO. The browser cookie name. The name must conform to RFC 2109. The name
   * of a cookie cannot be changed once the cookie has been created.
   */
  public static final String COOKIE_NAME = "JSESSIONSSO";

  /**
   * A universally unique identifier (UUID) associated with this user session.
   * The UUID is randomly assigned when a user selects the "remember me" option
   * on sign in.
   * <p>
   * For web browser clients this is the SSO Session UUID which becomes is value
   * of the browser cookie. For OAUTH clients this is the oauth_consumer_key.
   * <p>
   * The UUID field is re-assigned each time the user session is renewed.
   */
  @XmlAttribute(name = "uuid", required = true)
  protected String uuid;

  /**
   * The user email address.
   */
  @XmlAttribute(name = "userName", required = true)
  protected String userName;
  /**
   * The (encrypted) user password. OAUTH sets the password field to null.
   */
  @XmlAttribute(name = "password")
  protected String password;

  /**
   * A set of GlassFish Roles (Groups) that this SSO user is authorized for.
   */
  @XmlElement(required = true)
  protected Set<String> groups;

  /**
   * Gets the value of the groups property.
   * <p>
   * @return possible object is {@link HashSet }
   * <p>
   */
  public Set<String> getGroups() {
    return groups;
  }

  /**
   * Gets the value of the userName property.
   * <p>
   * @return possible object is {@link String }
   * <p>
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Gets the value of the password property.
   * <p>
   * @return possible object is {@link String }
   * <p>
   */
  public String getPassword() {
    /**
     * Decrypt the (encrypted) password using the UUID substring as the key.
     */
    if (password != null) {
      try {
        return decrypt(uuid.substring(uuid.length() - 16, uuid.length()), password);
      } catch (GeneralSecurityException exception) {
        Logger.getLogger(SSOCookie.class.getName()).log(Level.SEVERE, "SSOCookie failed to decode password for {0}: {1}", new Object[]{userName, exception.getMessage()});
      }
    }
    return null;
  }

  /**
   * Gets the value of the uuid property.
   * <p>
   * @return possible object is {@link String }
   * <p>
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Determine if this session is in the indicated security role.
   * <p>
   * Returns a boolean indicating whether this session is included in the
   * specified logical "role".
   * <p>
   * @param role a String specifying the name of the role. This is matched
   *             against the groupName and serviceName fields.
   * @return a boolean indicating whether this session instance belongs to a
   *         given role
   */
  public boolean isInRole(String role) {
    return getGroups().contains(role);
  }

  /**
   * Decrypt an AES encrypted string using the provided key.
   * <p>
   * @param key       the encryption key used to encrypt the value.
   * @param encrypted an AES encrypted and Base64 encoded string. Length is 44
   *                  characters or less.
   * @return the un-encrypted value
   * @throws GeneralSecurityException if the decryption fails
   */
  public static String decrypt(String key, String encrypted) throws GeneralSecurityException {
    byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
    if (raw.length != 16) {
      throw new IllegalArgumentException("Invalid key size. Key length must be 16 characters.");
    }
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
    byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
    return new String(original, Charset.forName("US-ASCII"));
  }//</editor-fold>

  /**
   * Get a Session Cookie named "JSESSIONSSO" containing the SSOSession UUID.
   * <p>
   * This method cannot be included in the SSOSession container object as http
   * Cookie is not compatible with JAXB marshalling / un-marshalling.
   * <p>
   * @param uuid the SSO Session UUID from which to build a cookie.
   * @return an HTTP version 1 cookie.
   */
  public static Cookie buildCookie(String uuid) {
    Cookie cookie = new Cookie(COOKIE_NAME, uuid);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    /**
     * Do not set the cookie domain when testing.
     */
//    cookie.setDomain(SSOSession.COOKIE_DOMAIN);
    cookie.setComment("sso");
    cookie.setVersion(1);
    /**
     * Set the cookie to respond to all applications.
     */
    cookie.setPath("/");
    /**
     * (30days) x (24hrs/1day) x (60min/1hrs) x (60sec/1min) x (1000ms/1sec) =
     * 2,592,000,000 ms
     */
    cookie.setMaxAge(Integer.MAX_VALUE);
    return cookie;
  }
}
