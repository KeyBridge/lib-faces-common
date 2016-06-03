package ch.keybridge.lib.faces.sso.client;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.xml.bind.annotation.*;
import org.apache.commons.codec.binary.Base64;

import static ch.keybridge.lib.faces.sso.client.SSOSOAPClient.BUNDLE;

/**
 * SSO Cookie.
 * <p>
 * The SSO Cookie is exchanged between the SSO service and a web portal to
 * assist with automatic sign-in using a browser cookie.
 *
 * @author jesse
 */
@XmlRootElement(name = "SSOSession")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SSOSession")
public class SSOSession {

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
  @XmlElement(name = "UUID", required = true)
  private String uuid;

  /**
   * The user email address.
   */
  @XmlElement(name = "Username", required = true)
  private String userName;
  /**
   * The (encrypted) user password. OAUTH sets the password field to null.
   */
  @XmlElement(name = "Password")
  private String password;
  /**
   * A String containing the IP address of the client that sent the request.
   * Specifically: the v4 Internet Protocol (IP) address of the client or last
   * proxy that sent the request.
   * <p>
   * Developer note: If the remote IP address is a private IP address (e.g.
   * starts with "10." or "192.") then the application is likely behind a load
   * balancer and the "X-Forwarded-For" request parameter should be inspected.
   * The X-Forwarded-For request header helps you identify the IP address of a
   * client. Because load balancers intercept traffic between clients and
   * servers, your server access logs contain only the IP address of the load
   * balancer. To see the IP address of the client, use the X-Forwarded-For
   * request header.
   * <p>
   * This is required for User cookies, optional for OAuth.
   */
  @XmlElement(name = "RemoteAddr")
  private String remoteAddr;

  /**
   * A set of GlassFish Roles (Groups) that this SSO user is authorized for.
   */
  @XmlElement(name = "Groups", required = true)
  @XmlList
  protected Collection<String> groups;

  /**
   * Get the group membership of this session.
   * <p>
   * Developer note: to determine group membership use
   * {@link #isInRole(java.lang.String)}
   *
   * @return a non-null HashSet
   */
  public Collection<String> getGroups() {
    if (groups == null) {
      groups = new HashSet<>();
    }
    return groups;
  }

  /**
   * Get the user name.
   *
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Get the password.
   *
   * @return the (decrypted) user password
   */
  public String getPassword() {
    /**
     * Decrypt the (encrypted) password using the UUID substring as the key.
     */
    if (password != null) {
      try {
        return decrypt(uuid.substring(uuid.length() - 16, uuid.length()), password);
      } catch (GeneralSecurityException exception) {
        Logger.getLogger(SSOSession.class.getName()).log(Level.SEVERE, "SSOCookie failed to decode password for {0}: {1}", new Object[]{userName, exception.getMessage()});
      }
    }
    return null;
  }

  /**
   * Get the IP address of the client that sent the request.
   *
   * @return A String containing the IP address of the client that sent the
   *         request.
   */
  public String getRemoteAddr() {
    return remoteAddr;
  }

  /**
   * Set the IP address of the client that sent the request.
   *
   * @param remoteAddr A String containing the IP address of the client that
   *                   sent the request.
   */
  public void setRemoteAddr(String remoteAddr) {
    this.remoteAddr = remoteAddr;
  }

  /**
   * Gets the value of the uuid property.
   *
   * @return possible object is {@link String }
   *
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Determine if this session is in the indicated security role.
   * <p>
   * Returns a boolean indicating whether this session is included in the
   * specified logical "role".
   *
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
   *
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
   * Cookies must be generated on-demand as the
   * {@code javax.servlet.http.Cookie} object is not compatible with JAXB
   * marshalling / un-marshalling.
   *
   * @param uuid the SSO Session UUID from which to build a cookie.
   * @return an HTTP version 1 cookie.
   */
  public static Cookie buildCookie(String uuid) {
    /**
     * Developer note: This method cannot be included in the Session entity
     * object as the HTTP Cookie class is not compatible with JAXB marshalling /
     * un-marshalling.
     */
    Cookie cookie = new Cookie(COOKIE_NAME, uuid);
    /**
     * HttpOnly cookies are not supposed to be exposed to client-side scripting
     * code, and may therefore help mitigate certain kinds of cross-site
     * scripting attacks
     */
    cookie.setHttpOnly(true);
    /**
     * Indicates to the browser whether the cookie should only be sent using a
     * secure protocol, such as HTTPS or SSL. The default value is false.
     */
    cookie.setSecure(true);
    /**
     * Do not set the cookie domain when testing. For production, specify the
     * entire domain within which this cookie should be presented.
     */
    try {
      cookie.setDomain(ResourceBundle.getBundle(BUNDLE).getString("sso.cookie.domain"));
    } catch (Exception e) {
    }
    cookie.setComment("sso");
    cookie.setVersion(1);
    /**
     * Set the cookie to respond to all applications.
     */
    cookie.setPath("/");
    /**
     * Set the max age to ~30 days; (30days) x (24hrs/1day) x (60min/1hrs) x
     * (60sec/1min) x (1000ms/1sec) = 2,592,000,000 ms =~ Integer.MAX_VALUE.
     */
    cookie.setMaxAge(Integer.MAX_VALUE);
    return cookie;
  }

  /**
   * Get the UUID for this cookie.
   *
   * @return the UUID
   */
  @Override
  public String toString() {
    return uuid;
  }

}
