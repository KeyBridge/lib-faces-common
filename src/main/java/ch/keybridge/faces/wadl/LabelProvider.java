/*
 * Copyright 2017 Key Bridge. All rights reserved.
 * Use is subject to license terms.
 *
 * Software Code is protected by Copyrights. Author hereby reserves all rights
 * in and to Copyrights and no license is granted under Copyrights in this
 * Software License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request from: info@keybridgewireless.com
 */
package ch.keybridge.faces.wadl;

/**
 * A label provider.
 *
 * @author Key Bridge
 * @since 04/30/17 to support standard label discovery
 */
public interface LabelProvider {

  /**
   * Parse a method ID into a pretty label. Generally this should parse a
   * CamelCase label.
   * <p>
   * Implementations MUST return a non-null value.
   *
   * @param methodId the method id (i.e. the method name)
   * @return the corresponding label
   */
  public String parseMethodId(String methodId);

  /**
   * Get a label corresponding to the provided key index. This is typically a
   * short or simple label.
   * <p>
   * Implementations MUST return a non-null value.
   *
   * @param key the key index
   * @return the corresponding label
   */
  public String getLabel(String key);

  /**
   * Get a label corresponding to the provided method and parameter. This is
   * typically a short or simple label.
   * <p>
   * Implementations MUST return a non-null value.
   *
   * @param method    the method name
   * @param parameter the method parameter
   * @return the corresponding label
   */
  public String getLabel(String method, String parameter);

  /**
   * Get an extended method description. This is typically a paragraph
   * description of a API method.
   *
   * @param key the key index
   * @return the corresponding text description
   */
  public String getMethodDescription(String key);

}
