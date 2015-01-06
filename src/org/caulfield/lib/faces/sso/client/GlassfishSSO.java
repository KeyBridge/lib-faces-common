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
package org.caulfield.lib.faces.sso.client;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.*;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import org.primefaces.util.Base64;
//import org.apache.commons.codec.binary.Base64;

/**
 * A persistent container for encrypted (as opposed to 1-way digested) user
 * credentials.
 * <p>
 * The GlassfishSSO session data is only stored in memory.
 * <p>
 * GlassfishSSO sessions are accessed by looking up the UUID in the
 * GlassfishSSOManager EJB. The UUID is set/reset every time a user signs in
 * with the sign_in page or accesses an API using their OAUTH consumer key.
 * <p>
 * GlassfishSSO Sessions are serialized and exchanged via the SSO SOAP
 * interface.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
@Entity
@Table(name = "glassfish_sso", catalog = "glassfish", schema = "")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
  @NamedQuery(name = "GlassfishSSO.findAll", query = "SELECT g FROM GlassfishSSO g"),
  @NamedQuery(name = "GlassfishSSO.findByUuid", query = "SELECT g FROM GlassfishSSO g WHERE g.uuid = :uuid"),
  @NamedQuery(name = "GlassfishSSO.findByUserName", query = "SELECT g FROM GlassfishSSO g WHERE g.userName LIKE :userName"),
  @NamedQuery(name = "GlassfishSSO.findExpired", query = "SELECT g FROM GlassfishSSO g WHERE g.dateExpiration >= :date"),
  @NamedQuery(name = "GlassfishSSO.findByGroup", query = "SELECT g FROM GlassfishSSO g WHERE g.groups LIKE :group")})
public class GlassfishSSO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * SSO. The browser cookie name. The name must conform to RFC 2109. The name
   * of a cookie cannot be changed once the cookie has been created.
   */
  public static final String COOKIE_NAME = "JSESSIONSSO";
  /**
   * Approx 1 MONTH. The default cookie maximum age, in milliseconds.
   * <p>
   * A positive value indicates that the cookie will expire after that many
   * seconds have passed. Note that the value is the maximum age when the cookie
   * will expire, not the cookie's current age. A negative value means that the
   * cookie is not stored persistently and will be deleted when the Web browser
   * exits. A zero value causes the cookie to be deleted.
   * <p>
   * expiry - an integer specifying the maximum age of the cookie in seconds; if
   * negative, means the cookie is not stored; if zero, deletes the cookie
   * <p>
   * (30days) x (24hrs/1day) x (60min/1hrs) x (60sec/1min) x (1000ms/1sec) =
   * 2,592,000,000 ms
   */
  public static final int COOKIE_MAX_AGE = Integer.MAX_VALUE;
  /**
   * keybridgeglobal.com.
   * <p>
   * The domain name within which this cookie is visible; form is according to
   * RFC 2109
   * <p>
   * Specifies the domain within which this cookie should be presented. The form
   * of the domain name is specified by RFC 2109. A domain name begins with a
   * dot (.foo.com) and means that the cookie is visible to servers in a
   * specified Domain Name System (DNS) zone (for example, www.foo.com, but not
   * a.b.foo.com). By default, cookies are only returned to the server that sent
   * them.
   */
  public static final String COOKIE_DOMAIN = ".keybridgeglobal.com";

  /**
   * A universally unique identifier (UUID) associated with this user session.
   * The UUID is randomly assigned when a user selects the "remember me" option
   * on sign in. The UUID field is re-assigned each time the user session is
   * renewed.
   */
  @Id
  @Basic(optional = false)
  @Size(min = 1, max = 48)
  @Column(nullable = false, length = 48)
  @XmlAttribute(required = true)
  private String uuid;
  /**
   * The user email address.
   */
  @Basic(optional = false)
  @Size(min = 1, max = 128)
  @Column(name = "user_name", nullable = false, length = 128)
  @XmlAttribute(required = true)
  private String userName;
  /**
   * The (encrypted) user password. OAUTH sets the password field to null.
   */
  @Size(min = 1, max = 128)
  @Column(length = 128)
  @XmlAttribute
  private String password;
  /**
   * A time stamp (in milliseconds) marking when the SSO session expires. This
   * field is automatically set in the constructor.
   * <p>
   * expiration is not read directly. Instead it is referred to from the
   * {@link #isExpired()} and {@link #isValid()} methods.
   */
  @Basic(optional = false)
  @Column(name = "date_expiration", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @XmlTransient
  private Date dateExpiration;

  /**
   * A URI-encoded list of GlassFish Roles (Groups) that this SSO user is
   * authorized for.
   */
  @Size(min = 1, max = 512)
  @Column(length = 512)
  @XmlElement(required = true)
  private String groups;

  /**
   * Default no-arg constructor.
   * <p>
   * Developer note: Prefer to use getIntance to ensure correct object
   * configuration.
   */
  public GlassfishSSO() {
  }

  /**
   * Construct a new GlassfishSSO session, setting the user name and password
   * fields. This constructor also automatically sets the UUID and
   * dateExpiration fields. The groups should be added after construction.
   * <p>
   * @param userName the user name
   * @param password the user password
   */
  public GlassfishSSO(String userName, String password) {
    /**
     * Set the UUID.
     */
    this.uuid = UUID.randomUUID().toString();
    this.userName = userName;
    /**
     * Encrypt the password.
     * <p>
     * Developer note: OAUTH sets the password field to null.
     */
    if (password != null) {
      try {
        this.password = encrypt(uuid.substring(uuid.length() - 16, uuid.length()), password);
      } catch (GeneralSecurityException exception) {
        System.err.println("Password encryption failed: " + exception.getMessage());
      }
    }
    /**
     * Set the expiration date.
     */
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.MILLISECOND, COOKIE_MAX_AGE);
    this.dateExpiration = calendar.getTime();
  }

  /**
   * Get a new GlassfishSSO session.
   * <p>
   * This method is typically used for USERS. It sets the user name and password
   * fields and assigns a random UUID.
   * <p>
   * Developer note: when using this constructor the groups must be set
   * separately.
   * <p>
   * @param userName the user name (or oauth_consumer_key)
   * @param password the user password (null for API sessions)
   * @return a fully qualified session instance
   */
  public static GlassfishSSO getInstance(String userName, String password) {
    return new GlassfishSSO(userName, password);
  }

  /**
   * Get a new, fully qualified, GlassfishSSO session.
   * <p>
   * This method is typically used for OAUTH. Is sets the UUID, username and
   * password fields.
   * <p>
   * Developer note: For OAUTH API sessions set the userName as the
   * oauth_consumer_key and set the password to null.
   * <p>
   * @param uuid     the SSO uuid (typically mirrors the user name)
   * @param userName the user name (or oauth_consumer_key)
   * @param password the user password (null for API sessions)
   * @param groups   a list of GlassFish Roles (Groups) that this SSO user is
   *                 authorized for.
   * @return a fully qualified session instance
   */
  public static GlassfishSSO getInstance(String uuid, String userName, String password, List<String> groups) {
    GlassfishSSO glassfishSSO = new GlassfishSSO(userName, password);
    glassfishSSO.setUuid(uuid);
    glassfishSSO.setGroups(groups);
    return glassfishSSO;
  }

  /**
   * Get the UUID.
   * <p>
   * @return the UUID cookie value
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * Set the uuid.
   * <p>
   * @param uuid the UUID cookie value
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Get the user name.
   * <p>
   * @return the user name
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Get the password
   * <p>
   * @return the (decrypted) user password
   */
  public String getPassword() {
    if (password != null) {
      try {
        return decrypt(uuid.substring(uuid.length() - 16, uuid.length()), password);
      } catch (GeneralSecurityException generalSecurityException) {
      }
    }
    return null;
  }

  /**
   * Get the group membership of this session.
   * <p>
   * Developer note: to determine group membership use
   * {@link #isInRole(java.lang.String)}
   * <p>
   * @return a non-null ArrayList
   */
  public List<String> getGroups() {
    if (groups == null) {
      return new ArrayList<>();
    }
    return decodeList(groups);
  }

  /**
   * Set the group membership of this session.
   * <p>
   * Null or empty list entries are ignored. The list is also de-duplicated. If
   * the groups parameter is set to null then the group list is cleared.
   * <p>
   * @param groups a list of group names. Null to clear.
   */
  public void setGroups(List<String> groups) {
    this.groups = groups != null ? encodeList(groups) : null;
  }

  /**
   * Add a single group to the groups list. Null, empty and duplicate groups are
   * ignored.
   * <p>
   * @param group a group membership to add.
   */
  public void addGroup(String group) {
    List<String> current = getGroups();
    if (group != null && !group.isEmpty() && !current.contains(group)) {
      current.add(group);
      this.groups = encodeList(current);
    }
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
   * Test if this cookie has expired.
   * <p>
   * @return TRUE if the date is expired.
   */
  public boolean isExpired() {
    return Calendar.getInstance().getTime().after(dateExpiration);
  }

  /**
   * Test if the cookie is still valid. e.g. that it has not expired.
   * <p>
   * @return TRUE if the cookie is valid.
   */
  public boolean isValid() {
    return Calendar.getInstance().getTime().before(dateExpiration);
  }

  /**
   * Hash code is based upon the user name.
   * <p>
   * @return the hash code
   */
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 19 * hash + Objects.hashCode(this.userName);
    return hash;
  }

  /**
   * Equals is based upon the user name.
   * <p>
   * @param obj the other cookie
   * @return true if equal
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final GlassfishSSO other = (GlassfishSSO) obj;
    return Objects.equals(this.userName, other.getUserName());
  }

  @Override
  public String toString() {
    return "GlassfishSSO"
      + " userName [" + userName
      + "] password [" + password
      + "] uuid [" + uuid
      + "] groups [" + groups
      + ']';
  }

  //<editor-fold defaultstate="collapsed" desc="Text URI Encode / Decode">
  /**
   * Decode URL-encoded text into a raw text string.
   * <p/>
   * This method will silently ignore illegal hex characters in escape pattern,
   * such as (%). Decoding is a best-effort try so this method will ignore any
   * text parameter decoding errors; specifically if the string cannot be
   * decoded from the UTF8 character set.
   * <p/>
   * @param urlEncodedText the URL-encoded string
   * @return the original, un-encoded string
   */
  private String decodeString(String urlEncodedText) {
    try {
      return URLDecoder.decode(urlEncodedText, "UTF-8");
    } catch (UnsupportedEncodingException | NullPointerException exception) {
      return "";
    }
  }

  /**
   * Decode an encoded String into a list of Strings.
   * <p/>
   * This method decodes each string from the UTF-8 character set to ensure it
   * is properly returned exactly as it was entered prior to persistence in the
   * database.
   * <p/>
   * @param encodedListString The encoded string.
   * @return A list of Strings.
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  private List<String> decodeList(String encodedListString) {
    /**
     * Strip leading and trailing brackets off the input String if present.
     */
    if (encodedListString.contains("[") && encodedListString.contains("]")) {
      encodedListString = encodedListString.substring(1, encodedListString.length() - 1);
    }
    List<String> stringList = new ArrayList<>();
    for (String string : encodedListString.split(",")) {
      stringList.add(decodeString(string));
    }
    return stringList;
  }

  /**
   * Encode a raw text string into URL-encoded text.
   * <p/>
   * Encoding is a best-effort try so this method will ignore any text parameter
   * encoding errors.
   * <p/>
   * @param rawTextString the original, un-encoded string
   * @return a URL-encoded string
   */
  private String encodeString(String rawTextString) {
    try {
      return URLEncoder.encode(rawTextString, "UTF-8");
    } catch (UnsupportedEncodingException | NullPointerException exception) {
      return "";
    }
  }

  /**
   * Encode a list of Strings into a standardized text string.
   * <p/>
   * This method encodes each string into a UTF-8 character set to ensure it is
   * properly stored in the database.
   * <p/>
   * @param stringList a list of strings.
   * @return An encoded string object.
   */
  private String encodeList(List<String> stringList) {
    List<String> stringListUtf8 = new ArrayList<>();
    for (String string : stringList) {
      stringListUtf8.add(encodeString(string));
    }
    return stringListUtf8.toString();
  }//</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Encrypt / Decrypt">
  /**
   * Encrypt the value using AES crypto algorithm and provided key. The key is
   * required to decrypt the value.
   * <p>
   * @param key   the encryption key used to seed and required to decrypt the
   *              value.
   * @param value the value to encrypt
   * @return the value as an AES encrypted and Base64 encoded string. Length is
   *         44 characters or less.
   * @throws GeneralSecurityException if the encryption fails
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  public static String encrypt(String key, String value) throws GeneralSecurityException {
    byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
    if (raw.length != 16) {
      throw new IllegalArgumentException("Invalid key size. Key length must be 16 characters.");
    }
    /**
     * Construct a secret key from the given byte array, then get a Cipher
     * object that implements the specified (AES/CBC/PKCS5Padding)
     * transformation.
     */
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    /**
     * Initialize this cipher with a key and a set of algorithm parameters.
     * Requires AES key length: 19 bytes
     */
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
    /**
     * Finish the multiple-part encryption operation, then return the byte array
     * as a Base64 encoded string.
     */
    return Base64.encodeToString(cipher.doFinal(value.getBytes(Charset.forName("US-ASCII"))), false);
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
  @SuppressWarnings("AssignmentToMethodParameter")
  public static String decrypt(String key, String encrypted) throws GeneralSecurityException {
    byte[] raw = key.getBytes(Charset.forName("US-ASCII"));
    if (raw.length != 16) {
      throw new IllegalArgumentException("Invalid key size. Key length must be 16 characters.");
    }
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
    byte[] original = cipher.doFinal(Base64.decode(encrypted));
    return new String(original, Charset.forName("US-ASCII"));
  }//</editor-fold>

}
