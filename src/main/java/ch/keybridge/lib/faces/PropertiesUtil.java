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
package ch.keybridge.lib.faces;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple class to enable the loading and import of a properties file.
 * <p>
 * The properties file may be either in the /usr/local/etc then look in the user
 * home directory under ~/etc/.
 *
 * @author Jesse Caulfield
 */
public class PropertiesUtil {

  /**
   * Helper method to query the properties file and determine if the current
   * host is identified in the properties as a run host.
   * <p>
   * The match is ALWAYS be lower case and all run.host entries in the
   * properties file must therefore be entered lower case to match.
   *
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
   *
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
   * Try to load a PROPERTIES file as a resource bundle, from /usr/local/etc/ or
   * ~/etc.
   * <p>
   * If the properties file is not found in either search directory an empty
   * properties container is returned.
   * <p>
   * The input is the base name of a properties file. The ".properties" suffix
   * is added.
   *
   * @param baseName baseName - the base name of the resource bundle
   * @return a non-null Properties container
   */
  public static Properties load(String baseName) {
    Properties properties = new Properties();
    /**
     * First try a resource bundle.
     */
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(baseName);
      if (bundle != null) {
        for (String string : bundle.keySet()) {
          properties.setProperty(string, bundle.getString(string));
        }
        return properties;
      }
    } catch (Exception e) {
    }
    /**
     * Next try a file
     */
    try {
      /**
       * Assume a *NIX deployment and look in /usr/local/etc.
       */
      File propertiesFile = new File("/usr/local/etc/" + baseName + ".properties");
      /**
       * If not in /usr/local/etc then look in the user home directory. Note:
       * this is the Glassfish user, so in production environments will be
       * "/var/www/etc/".
       */
      if (!propertiesFile.exists()) {
        propertiesFile = new File(System.getProperty("user.home") + File.separator + "etc" + File.separator + baseName + ".properties");
      }
      /**
       * Read the properties file.
       */
      if (propertiesFile.exists()) {
        /**
         * Initialize and load the properties file.
         */
        properties.load(new FileInputStream(propertiesFile));
        return properties;
      }
    } catch (IOException exception) {
    }
    /**
     * If program flow has reached this point either the properties file does
     * not exist, is incomplete, or is corrupted.
     */
    Logger.getLogger(PropertiesUtil.class.getName()).log(Level.WARNING, "Properties file {0} could not be loaded.", baseName);
    return new Properties();
  }
}
