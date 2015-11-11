package ch.keybridge.test;

import javax.faces.validator.ValidatorException;
import ch.keybridge.lib.faces.validator.ValidatePassword;

/*
 * Copyright (C) 2014 Caulfield IP Holdings (Caulfield) and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by Caulfield Copyrights. Caulfield hereby reserves
 * all rights in and to Caulfield Copyrights and no license is granted under
 * Caulfield Copyrights in this Software License Agreement. Caulfield generally
 * licenses Caulfield Copyrights for commercialization pursuant to the terms of
 * either Caulfield's Standard Software Source Code License Agreement or
 * Caulfield's Standard Product License Agreement.
 *
 * A copy of either License Agreement can be obtained on request by email from:
 * info@caufield.org.
 */
/**
 *
 * @author Jesse Caulfield 
 */
public class Test_password {

  public static void main(String[] args) {

    ValidatePassword p = new ValidatePassword();

    for (String string : new String[]{"abcd", "aBc45DSD_sdf", "password", "afv", "1234", "reallylon)*()*09809809:LJLKJLKJLKJLKJLKJgpassword", "1agdA*$#", "1agdA*$#", "@12X*567", "1#Zv96g@*Yfasd4", "#67jhgt@erd"}) {
      try {
        p.validate(null, null, string);
        System.out.println(string + " == OK ");
      } catch (ValidatorException validatorException) {
        System.err.println(string + " /= " + validatorException.getMessage());
      }
    }
  }
}
