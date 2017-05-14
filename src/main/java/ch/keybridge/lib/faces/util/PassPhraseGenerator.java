/*
 *   Copyright (C) 2013 Caulfield IP Holdings (Caulfield)
 *   and/or its affiliates. All rights reserved. Use is subject to license terms.
 *
 *   Software Code is protected by Caulfield Copyrights. Caulfield hereby
 *   reserves all rights in and to Caulfield Copyrights and no license is
 *   granted under Caulfield Copyrights in this Software License Agreement.
 *   Caulfield generally licenses Caulfield Copyrights for commercialization
 *   pursuant to the terms of either Caulfield's Standard Software Source Code
 *   License Agreement or Caulfield's Standard Product License Agreement.
 *
 *   A copy of either License Agreement can be obtained on request by email
 *   from: info@caufield.org.
 */
package ch.keybridge.lib.faces.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * A cheap, lightweight, low-security password generator based upon common
 * Internet examples.
 * <p>
 * This utility class creates simple random pass phrases containing only
 * alphanumeric 'normal' ASCII characters; no punctuation or special characters
 * are included.
 * <p>
 * The default length is 10 characters but a phrase of any length may be
 * generated.
 * <p>
 * Use the EncryptUtility located in the security-utility library to encrypt the
 * resultant pass phrase.
 */
public class PassPhraseGenerator {

  /**
   * 6 characters. Standard length for transaction ID.
   */
  private static final int LENGTH_TRANSACTION = 6;
  /**
   * The random number generator.
   */
  private static final Random RANDOM = new Random();
  /*
   * A set of valid characters from which to build a randomly generated pass
   * phrase. The acceptance criterion is that the characters must be printable,
   * memorable, and "won't break HTML" (i.e., not '<', '>', '&', '=', ...). or
   * break shell commands (i.e., not ' <', '>', '$', '!', ...). 'I', 'L' and 'O'
   * are good to leave out, as are easily confused with numeric zero and one
   * values. Also excluded are the spetial characters ['+', '-', '@'], thus
   * creating pass phrases only containing 'normal' ASCII characters.
   */
  private static final char[] DICTIONARY = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                                            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                                            '2', '3', '4', '5', '6', '7', '8', '9'};

  /**
   * Generate a 12-character randomly generated pass phrase string formatted as
   * "XXXX-XX-XXXX"
   *
   * @return a randomly generated pass phrase
   */
  public static String generate() {
    return generate(4) + "-" + generate(2) + "-" + generate(4);
  }

  /**
   * Generate a randomly generated password string of the indicated length.
   *
   * @param length the number of characters to generate
   * @return a randomly generated pass phrase string of the indicated length.
   */
  public static String generate(int length) {
    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < length; i++) {
      sb.append(DICTIONARY[RANDOM.nextInt(DICTIONARY.length)]);
    }
    return sb.toString();
  }

  /**
   * Generate an 11-character random string of the format (date[4] + '-' +
   * random[6]).
   * <p>
   * The Date portion is MONTH, YEAR encoded as 'MMyy'. The Random character
   * portion is always UPPERCASE.
   *
   * @return a randomly generated transaction ID of the format
   *         "MMyy-{alpha}{6}". e.g. 0911-NK8RAW
   */
  public static String getTransactionID() {
    SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
    StringBuilder sb = new StringBuilder(sdf.format(Calendar.getInstance().getTime()));
    sb.append("-");
    for (int i = 0; i < LENGTH_TRANSACTION; i++) {
      sb.append(DICTIONARY[RANDOM.nextInt(DICTIONARY.length)]);
    }
    return sb.toString().toUpperCase();
  }

}
