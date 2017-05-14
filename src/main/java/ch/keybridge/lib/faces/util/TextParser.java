/*
 *   Copyright (C) 2012 Caulfield IP Holdings (Caulfield)
 *   and/or its affiliates.
 *   All rights reserved. Use is subject to license terms.
 *
 *   Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *   reserves all rights in and to Caulfield Copyrights and no license is
 *   granted under Caulfield Copyrights in this Software License Agreement.
 *   Caulfield generally licenses Caulfield Copyrights for commercialization
 *   pursuant to the terms of either Caulfield's Standard Software Source Code
 *   License Agreement or Caulfield's Standard Product License Agreement.
 *
 *   A copy of Caulfield's either License Agreement can be obtained on request
 *   by email from: info@caufield.org.
 */
package ch.keybridge.lib.faces.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * A set of various static TEXT Encoding and Decoding Utilities.
 *
 * @author Jesse Caulfield
 */
public final class TextParser {

  /**
   * The constructor is PRIVATE to ensure only STATIC methods are called from
   * this utility class.
   */
  private TextParser() {
  }

  /**
   * Trim the input string to the maximum length.
   * <p>
   * Includes null and length check to prevent error. If the input string is
   * shorter than the maxLength value the original, unaltered string is
   * returned.
   *
   * @param string    The input string.
   * @param maxLength The desired maximum length. Must be a positive integer
   *                  greater than one.
   * @return the input string, trimmed to the desired value if required.
   */
  public static String trim(String string, int maxLength) {
    if (string == null) {
      return string;
    }
    return string.length() > maxLength ? string.substring(0, maxLength).trim() : string.trim();
  }

  /**
   * Encode a raw text string into URL-encoded text.
   * <p>
   * Encoding is a best-effort try so this method will ignore any text parameter
   * encoding errors.
   *
   * @param rawTextString the original, un-encoded string
   * @return a URL-encoded string
   */
  public static String encode(String rawTextString) {
    try {
      return URLEncoder.encode(rawTextString, "UTF-8");
    } catch (UnsupportedEncodingException | NullPointerException exception) {
      return "";
    }
  }

  /**
   * Decode URL-encoded text into a raw text string.
   * <p>
   * This method will silently ignore illegal hex characters in escape pattern,
   * such as (%). Decoding is a best-effort try so this method will ignore any
   * text parameter decoding errors; specifically if the string cannot be
   * decoded from the UTF8 character set.
   *
   * @param urlEncodedText the URL-encoded string
   * @return the original, un-encoded string
   */
  public static String decode(String urlEncodedText) {
    try {
      return URLDecoder.decode(urlEncodedText, "UTF-8");
    } catch (UnsupportedEncodingException | NullPointerException exception) {
      return "";
    }
  }

  /**
   * Encode a key / value map into a URI-encoded string.
   * <p>
   * Both Keys and Values must be generic, serializable objects.
   * <p>
   * During encoding the Key and Value {@code toString()} method is called on
   * each entry. Null values are replaced with an empty string.
   *
   * @param keyValueMap A map of {@code Serializable} key/value pairs.
   * @return null if the keyValueMap is null or empty.
   */
  public static String encodeKVMap(Map<? extends Object, ? extends Object> keyValueMap) {
    StringBuilder sb = new StringBuilder();
    if (keyValueMap != null && !keyValueMap.isEmpty()) {
      keyValueMap.entrySet().stream().forEach((entry) -> {
        /**
         * Append an ampersand if stringBuilder is not empty. This method is SQL
         * injection safe and will fail if the value contains a '%' character.
         */
        sb.append(sb.length() == 0 ? "" : "&")
                .append(encode(entry.getKey() != null ? entry.getKey().toString() : ""))
                .append("=")
                .append(encode(entry.getValue() != null ? entry.getValue().toString() : ""));
      });
      /**
       * The SubString method below strips an automatically prepended '?'
       * character from the URIBuilder query string
       */
      return sb.toString();
    } else {
      return null;
    }
  }

  /**
   * Decode a URI-encoded key value map into its constituents.
   * <p>
   * This method uses an internal TreeMap to ensure the key/value pairs are
   * always sorted by KEY.
   *
   * @param keyValueString a URL-encoded set of key/value pairs
   * @return non-null Map (TreeMap implementation)
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  public static Map<String, String> decodeKVMap(String keyValueString) {
    /**
     * Clean the string of all non-essential characters.
     */
    Map<String, String> stringMap = new TreeMap<>();
    /**
     * Only process if the keyValueString is not empty
     */
    if (keyValueString != null && !keyValueString.isEmpty()) {
      /**
       * Strip the leading '?' if present. '?' is prepended by some URI
       * builders.
       */
      if (keyValueString.startsWith("?")) {
        keyValueString = keyValueString.substring(1);
      }
      /**
       * Parse the URI-encoded string into a set of key/value pairs on '&', then
       * decode each key/value pair into a map entry.
       * <p>
       * Strip all non-word characters, including the leading '?' if present.
       * '?' is prepended by some URI builders. To accommodate numbers to not
       * strip the characters '+', '-', and '.'.
       */
      for (String keyValuePair : keyValueString.split("&")) {
        try {
          /**
           * Split the Key/Value pair on '=', then populate the map with the
           * (decoded) key and (decoded) value pairs (decoding again here is
           * important!)
           */
          String[] kv = decode(keyValuePair).split("=");
          stringMap.put(decode(kv[0]), decode(kv[1]));
        } catch (Exception ex) {
          //        Logger.getLogger(URIEncodeDecodeFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
    return stringMap;
  }

  /**
   * Internal helper method to decode a String map. This marshals the toString()
   * output of a typical Map instance back into a Map.
   *
   * @param mapString the MAP toString representation
   * @return a map of [String, String] values
   */
  public static Map<String, String> decodeStringMap(String mapString) {
    Map<String, String> stringMap = new TreeMap<>();
    if (mapString == null || mapString.isEmpty()) {
      return stringMap;
    }
    /**
     * Trim the preface and suffix characters.
     */
    if (mapString.startsWith("{")) {
      mapString = mapString.substring(1);
    }
    if (mapString.startsWith("[")) {
      mapString = mapString.substring(1);
    }
    if (mapString.endsWith("}")) {
      mapString = mapString.substring(0, mapString.length() - 1);
    }
    if (mapString.endsWith("]")) {
      mapString = mapString.substring(0, mapString.length() - 1);
    }
    /**
     * Parse the URI-encoded string into a set of key/value pairs on '&', then
     * decode each key/value pair into a map entry.
     * <p>
     * Strip all non-word characters, including the leading '?' if present. '?'
     * is prepended by some URI builders. To accommodate numbers to not strip
     * the characters '+', '-', and '.'.
     */
    for (String keyValuePair : mapString.split(",")) {
      try {
        /**
         * Split the Key/Value pair on ',', then populate the map with the
         * (trimmed) key and (trimmed) value pairs.
         */
        String[] kv = keyValuePair.split("=");
        stringMap.put(kv[0].trim(), kv[1].trim());
      } catch (Exception ex) {
        //        Logger.getLogger(URIEncodeDecodeFactory.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return stringMap;
  }

  /**
   * Add or update a key value pair into a URI-encoded string.
   * <p>
   * The Key must be non-null. The Key and Value must be successfully
   * encode-able (e.g. not contain any illegal characters.)
   * <p>
   * This method will fail silently. Upon failure the input encodedString is
   * returned unaltered.
   *
   * @param key           The key parameter - Must be non-null and not empty.
   * @param value         The value parameter - Must be non-null and not empty.
   * @param encodedString The existing extension string. OK if null. If null
   *                      then this method will initialize the extension string.
   * @return The URL-encoded string.
   */
  public static String addKVToString(String key, String value, String encodedString) {
    /**
     * JSF sometimes tries to set empty strings on page load. Silently ignore
     * this and fail gracefully.
     */
    if (key == null || key.isEmpty() || encode(key).isEmpty()) {
      return encodedString;
    }
    Map<String, String> kvMap = decodeKVMap(encodedString);
    if (value == null || value.isEmpty() || encode(value).isEmpty()) {
      kvMap.remove(key);
    }
    /**
     * Fail if the key or value cannot be encoded.
     */
    if (encode(key).isEmpty() || encode(value).isEmpty()) {
      return encodedString;
    }
    /**
     * Update and return the encoded string.
     */
    kvMap.put(key, value);
    return encodeKVMap(kvMap);
  }

  /**
   * Get the indicated value within an encoded String.
   *
   * @param uriEncodedText parameter key. may be null or empty.
   * @param key            the encoded pair key
   * @return null if the key is null or empty or has not been set.
   */
  public static String getValue(String uriEncodedText, String key) {
    if (key == null || key.isEmpty()) {
      return null;
    }
    return decodeKVMap(uriEncodedText).get(key);
  }

  /**
   * Encode a list of Strings into a standardized text string.
   * <p>
   * This method encodes each string into a UTF-8 character set to ensure it is
   * properly stored in the database.
   *
   * @param stringCollection a collection of strings.
   * @return An encoded string
   */
  public static String encodeCollection(Collection<String> stringCollection) {
    if (stringCollection == null || stringCollection.isEmpty()) {
      return null;
    }
    Set<String> stringSetUtf8 = new HashSet<>();
    stringCollection.stream().forEach((string) -> {
      stringSetUtf8.add(encode(string));
    });
    /**
     * Strip the first and last characters, which are open/close brackets "["
     * and "]". Strip space characters.
     */
    String string = stringSetUtf8.toString().replaceAll(",\\s+", ",").trim();
    return string.substring(1, string.length() - 1);
  }

  /**
   * Decode an encoded String into a list of Strings.
   * <p>
   * This method decodes each string from the UTF-8 character set to ensure it
   * is properly returned exactly as it was entered prior to persistence in the
   * database.
   *
   * @param encodedCollection The encoded string.
   * @return A non-null (possibly empty) ArrayList of Strings.
   */
  @SuppressWarnings("AssignmentToMethodParameter")
  public static Collection<String> decodeCollection(String encodedCollection) {
    Collection<String> stringSet = new TreeSet<>();
    if (encodedCollection == null || encodedCollection.isEmpty()) {
      return stringSet;
    }
    /**
     * Strip brackets off the input String.
     */
    if (encodedCollection.startsWith("[") && encodedCollection.endsWith("]")) {
      encodedCollection = encodedCollection.substring(1, encodedCollection.length() - 1);
    }
    for (String token : encodedCollection.split(",")) {
      stringSet.add(decode(token).trim());
    }
    return stringSet;
  }

  /**
   * Add the indicated element to the encoded collection String
   *
   * @param element           the element to add
   * @param encodedCollection the existing encoded collection String
   * @return An encoded string
   */
  public static String addToCollection(String element, String encodedCollection) {
    Collection<String> collection = decodeCollection(encodedCollection);
    collection.add(element);
    return encodeCollection(collection);
  }

  /**
   * Format the input string to Proper-Case by capitalizing the first character
   * of each word and forcing all other characters to lower case.
   * <p>
   * This method parses words on the following regex: {@code [\\s/+_-]+} and
   * includes a null check to ignore null or empty strings. e.g.
   * "Akin_Gump-Strauss+Hauer Feld LLP" is converted to "Akin Gump Strauss Hauer
   * Feld Llp".
   *
   * @param inputString A free-text string. May contain one or more words.
   * @return The input string converted to Proper case.
   */
  public static String properCase(final String inputString) {
    if (inputString == null || inputString.trim().isEmpty()) {
      return null;
    }
    String string = inputString.trim().toLowerCase();
    /**
     * If the string is a single character just return it in uppercase.
     */
    if (string.length() == 1) {
      return string.toUpperCase();
    }
    if (string.length() > 2) {
      /**
       * If the string contains enough characters to be two words then process
       * each word.
       */
      StringBuilder sb = new StringBuilder();
//      for (String subString : string.split("[\\s/+()@_-]+")) {
      for (String subString : string.split("[\\s/+_-]+")) {
        if (!sb.toString().trim().isEmpty()) {
          sb.append(" ");
        }
        /**
         * Surround with a try catch since this is throwing 'String index out of
         * range: 1' errors.
         */
        try {
//          sb.append(subString.substring(0, 1).toUpperCase()).append(subString.substring(1).trim());
          sb.append(subString.substring(0, 1).toUpperCase()).append(subString.substring(1));
        } catch (Exception e) {
          sb.append(subString.toLowerCase().trim());
        }
      }
      return sb.toString();
    } else {
      /**
       * The string is a single word.
       */
      return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
  }

  /**
   * Split a CamelCase string by inserting a space character BEFORE every
   * capital character. e.g. {@code "CalculateCountryIntersect"} is transformed
   * to {@code "Calculate Country Intersec"}
   *
   * @param inputString the source string
   * @return the tranformed string
   */
  public static String splitCamelCase(final String inputString) {
    if (inputString == null || inputString.trim().isEmpty()) {
      return null;
    }
    String string = inputString.trim();

    /**
     * If the string contains enough characters to be two words then process
     * each word.
     */
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      char character = string.charAt(i);
      if (Character.isUpperCase(character)) {
        sb.append(" ");
      }
      sb.append(character);
    }
    return sb.toString().trim();
  }

  /**
   * Format the provided string by capitalizing the first character and forcing
   * the rest to lower case, then eliminating any spaces. e.g. The String "Camel
   * CASE" is converted to "CamelCase".
   *
   * @see <a href="http://en.wikipedia.org/wiki/CamelCase">CamelCase</a>
   * @param string the input string converted to Camel case.
   * @return the string converted to CamelCase
   */
  public static String camelCase(String string) {
    if (string != null) {
      return properCase(string).replaceAll("\\W", "");
    } else {
      return null;
    }
  }

  /**
   * Strip the input string of all invalid characters then trim it to maximum 32
   * characters.
   * <p>
   * Note that a file name extension should NOT be included in the input string
   * as it may be stripped.
   *
   * @param string the string to process into a file name.
   * @return the string, stripped of all non-word characters.
   */
  public static String asFileName(String string) {
    if (string != null) {
      /**
       * Strip all punctuation except the dash '-'. Clean up by removing any
       * double-spaces.
       * <p>
       * !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
       */
      String asciiText = string.replaceAll("[\\!\\\"#$%&\\'\\(\\)*+,./:;<=>?@\\[\\]^_`\\{|\\}~]", "").replaceAll(" {2,}+", " ").trim();
      return trim(asciiText, 32);
    } else {
      return null;
    }
  }

  /**
   * Add line breaks to a (long) string.
   * <p>
   * This method adds line breaks at the indicated maxLineLength in the given
   * input string. This method is useful when generating and manipulating
   * pre-formatted text.
   *
   * @param input         the input string
   * @param maxLineLength the maximum line length, in characters (typ. 80)
   * @return the input string with new lines inserted every maxLineLength
   *         characters.
   * @see <a
   * href="http://stackoverflow.com/questions/7528045/large-string-split-into-lines-with-maximum-length-in-java">StackOverflow</a>
   */
  public static String addLinebreaks(String input, int maxLineLength) {
    StringTokenizer tok = new StringTokenizer(input, " ");
    StringBuilder output = new StringBuilder(input.length());
    int lineLen = 0;
    while (tok.hasMoreTokens()) {
      String word = tok.nextToken();

      if (lineLen + word.length() > maxLineLength) {
        output.append("\n");
        lineLen = 0;
      }
      output.append(word).append(" ");
      lineLen += word.length();
    }
    return output.toString();
  }

  /**
   * Tokenize a comma-separated string while ignoring commas embedded within
   * quotes.
   * <p>
   * This method is useful to parse lines in a data file formatted with fields
   * terminated by the comma character and fields optionally enclosed by a
   * double quote character.
   * <p>
   * A comma-separated values (CSV) file stores tabular data (numbers and text)
   * in plain text. Each line of the file is a data record. Each record consists
   * of one or more fields, separated by commas.
   * <p>
   * For example the following line contains a comma in the 5th field:
   * {@code 2,"R","010374535-001","I WANT WIRELESS.CA LTD","LITTLE SMOKEY, AB SITE 12B","CGZ416","6","FX",	54:40:52,	117:6:22,806,,"AB",,98,"D","6M00D7WET",6000,575}
   * <p>
   * This method implements a REGEX splitter that recognizes a comma only if
   * that comma has zero, or an even number of quotes ahead of it.
   * <p>
   * According to RFC 4180: Sec 2.6: "Fields containing line breaks (CRLF),
   * double quotes, and commas should be enclosed in double-quotes." Sec 2.7:
   * "If double-quotes are used to enclose fields, then a double-quote appearing
   * inside a field must be escaped by preceding it with another double quote"
   *
   * @param text the CSV-encoded text
   * @return an array of tokenized string elements, some of which may contain
   *         commas.
   * @see <a href="https://tools.ietf.org/html/rfc4180">RFC4180</a>
   * @see
   * <a href="http://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes">Split
   * a CSV String</a>
   */
  public static String[] tokenizeCSV(String text) {
    /**
     * Add -1 to catch empty strings (including empty strings after the last
     * comma).
     * <p>
     * Deprecated. This method does not strip field-enclosing quotes.
     */
//    return text.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    /**
     * Use a parser with a simple state machine. This enables us to remove the
     * field-enclosing quotes and to trim unwanted white space.
     */
    List<String> tokensList = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder sb = new StringBuilder();
    for (char c : text.toCharArray()) {
      switch (c) {
        case ',': // conditionally end the token or add the character
          if (inQuotes) {
            sb.append(c);
          } else {
            tokensList.add(sb.toString().trim());
            sb = new StringBuilder();
          }
          break;
        case '"': // do not copy the field-enclosing quote
          inQuotes = !inQuotes;
          break;
        default:
          sb.append(c); // add the character
          break;
      }
    }
    tokensList.add(sb.toString());
    return tokensList.toArray(new String[tokensList.size()]);
  }
}
