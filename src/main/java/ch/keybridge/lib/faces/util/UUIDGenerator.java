/*
 * Copyright 2016 Key Bridge.
 *
 * All rights reserved. Use is subject to license terms.
 * This software is protected by copyright.
 *
 * See the License for specific language governing permissions and
 * limitations under the License.
 */
package ch.keybridge.lib.faces.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * A very simple, deterministic UUID generator based upon the MD5 one-way hash.
 * <p>
 * Use this utility to create deterministic, non-random one-way HASH values. The
 * HASH values are convenient to use as universally unique identifiers and
 * database index values.
 *
 * @author Key Bridge LLC
 * @since 1.3.2 added 07/25/16
 */
public class UUIDGenerator {

  /**
   * Create a unique, one-way deterministic hash from a collection of strings.
   * The output is formatted to look like a UUID.
   * <p>
   * This method first implements a MD5 checksum from the string collection,
   * then stretches the 32-character MD5 checksum output to a standardized
   * 36-character UUID format.
   * <p>
   * If the MessageDigest algorithm fails then a simple integer hash of the
   * input string values is returned.
   *
   * @param objects a collection of object values.
   *                {@code toString() is called on each entry}
   * @return a MD5 checksum of the string values formatted as a UUID.
   */
  public static String generate(Object... objects) {
    StringBuilder sb = new StringBuilder();
    for (Object object : objects) {
      sb.append(object != null ? object.toString() : "");
    }
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] hashBytes = md.digest(sb.toString().getBytes());
      String result = "";
      for (int i = 0; i < hashBytes.length; i++) {
        result += Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1);
      }
      return result.substring(0, 8)
              + "-"
              + result.substring(8, 12)
              + "-"
              + result.substring(12, 16)
              + "-"
              + result.substring(16, 20)
              + "-"
              + result.substring(20, result.length());
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return String.valueOf(Objects.hash(sb.toString()));
    }
  }
}
