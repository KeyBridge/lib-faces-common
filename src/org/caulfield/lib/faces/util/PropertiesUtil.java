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
package org.caulfield.lib.faces.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * A simple class to enable the loading and import of a properties file.
 * <p>
 * The properties file may be either in the /usr/local/etc then look in the user
 * home directory under ~/etc/.
 * <p>
 * @author Jesse Caulfield <jesse@caulfield.org>
 */
public class PropertiesUtil {

  /**
   * Helper method to query the properties file and determine if the current
   * host is identified in the properties as a run host.
   * <p>
   * The match is ALWAYS be lower case and all run.host entries in the
   * properties file must therefore be entered lower case to match.
   * <p>
   * @param prefix   a properties prefix identifying a process (e.g. fcc_cdbs)
   * @param filename the properties file name to read
   * @return TRUE if the current host is identified as a run host for the
   *         indicated process
   */
  public static boolean isRunHost(String prefix, String filename) {
    Properties properties = load(prefix, filename);
    try {
      return properties.containsKey("run.host")
        && properties.getProperty("run.host").contains(InetAddress.getLocalHost().getHostName().toLowerCase());
    } catch (UnknownHostException unknownHostException) {
      System.err.println("WARN: PropertiesUtil unknownHostException " + unknownHostException.getMessage());
    }
    /**
     * Either the properties does not contain a "[prefix.]run.host " entry or
     * that entry does not identify the current host.
     */
    return false;
  }

  /**
   * Load a PROPERTIES file from either /usr/local/etc/ or ~/etc and filter
   * those properties for the given prefix.
   * <p>
   * The prefix (and trailing dot) is stripped from the properties name in the
   * returned properties container.
   * <p>
   * For example, with a prefix of <code>fcc_cdbs</code> the property named
   * <code>fcc_cdbs.run.host</code> would be set as <code>run.host</code>.
   * <p>
   * @param prefix   a properties prefix identifying a process (e.g. fcc_cdbs)
   * @param filename the properties file name to read
   * @return a a non-null Properties container
   */
  public static Properties load(String prefix, String filename) {
    Properties propertiesAll = load(filename);
    Properties properties = new Properties();
    for (String propertyName : propertiesAll.stringPropertyNames()) {
      if (propertyName.startsWith(prefix)) {
        /**
         * Add the property while stripping the prefix (and trailing dot) from
         * the key index.
         */
        properties.setProperty(propertyName.replaceFirst(prefix + ".", ""),
                               propertiesAll.getProperty(propertyName));
      }
    }
    return properties;
  }

  /**
   * Load a PROPERTIES file from either /usr/local/etc/ or ~/etc.
   * <p>
   * If the properties file is not found in either search directory an empty
   * properties container is returned.
   * <p>
   * @param filename the properties file to load
   * @return a non-null Properties container
   */
  public static Properties load(String filename) {
    try {
      /**
       * Assume a *NIX deployment and look in /usr/local/etc.
       */
      File propertiesFile = new File("/usr/local/etc/" + filename);
      /**
       * If not in /usr/local/etc then look in the user home directory. Note:
       * this is the Glassfish user, so in production environments will be
       * "/var/www/etc/".
       */
      if (!propertiesFile.exists()) {
        propertiesFile = new File(System.getProperty("user.home") + File.separator + "etc" + File.separator + filename);
      }
      /**
       * Read the properties file.
       */
      if (propertiesFile.exists()) {
        /**
         * Initialize and load the properties file.
         */
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        /**
         * Done.
         */
        return properties;
      }
    } catch (IOException exception) {
      System.err.println("DEBUG ERROR PropertiesUtil.loadProperties: " + exception.getMessage());
    }
    /**
     * If program flow has reached this point either the properties file does
     * not exist, is incomplete, or is corrupted.
     */
    System.err.println("ERROR: Properties could not be loaded.");
    return new Properties();
  }
}
