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
package ch.keybridge.lib.faces.converter;

import javax.enterprise.inject.spi.CDI;
import javax.faces.convert.Converter;

/**
 * An abstract Faces Converter implementation. This class provides helpful
 * shortcuts for implementing a Faces converter.
 *
 * @author Key Bridge LLC
 * @since v2.4.1 added 01/31/17
 */
public abstract class AbstractCDIConverter implements Converter {

  /**
   * Access to the current container {@code Context &amp; Dependency Injection}
   * context and select a child Instance for the given required type.
   * Parameters: subtype - a Class representing the required type
   * <p>
   * Developer note: Originally the Named beans were injected directly into
   * Validators and Converters using OmniFaces CDI bean manager. However this
   * appears to have broken / is not working after upgrading to GF 4.1.1. A
   * (more portable) solution is to access the container CDI directly. This
   * method provides a shortcut.
   *
   * @param <U>       the required type
   * @param namedBean the CDI managed bean class; should be annotated with
   *                  {@code @Named}
   * @return an instance of the required type
   */
  protected final <U> U CDISelect(Class<U> namedBean) {
    return CDI.current().select(namedBean).get();
  }
}
