/*
 * Copyright 2015 Caulfield IP Holdings (Caulfield) and affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * Software Code is protected by copyright. Caulfield hereby
 * reserves all rights and copyrights and no license is
 * granted under said copyrights in this Software License Agreement.
 * Caulfield generally licenses software for commercialization
 * pursuant to the terms of either a Standard Software Source Code
 * License Agreement or a Standard Product License Agreement.
 * A copy of these agreements may be obtained by sending a request
 * via email to info@caufield.org.
 */
package ch.keybridge.lib.faces.sso.client;

import junit.framework.TestCase;

/**
 *
 * @author Key Bridge LLC
 */
public class SSOSOAPClientTest extends TestCase {

  public SSOSOAPClientTest(String testName) {
    super(testName);
  }

  public void testVoid() {
    System.out.println("SSO Client testing disabled");
  }

  private SSO getSSOClient() {
    SSO sso = null;
    try {
      sso = SSOSOAPClient.getInstance("http://localhost:8080/service/sso?wsdl");
      SSOSOAPClient.enableLogging(sso);
    } catch (Exception exception) {
      fail("Failed to instantiate a SSO SOAP client");
    }
    return sso;
  }

  public void _testUpdateLastSeen() {
    System.out.println("Update last seen");
    getSSOClient().updateLastSeen("jesse@caulfield.org");
  }

  public void _testAddCookie() {
    System.out.println("Test Add Cookie");
    String user = "jesse@caulfield.org";
//    String user = "bogususer@bogus.bog";
    String password = "foobar";
    String remoteAddr = "127.0.0.123";
    String uuid = getSSOClient().addCookie(user, password, remoteAddr);
    System.out.println("  Cookie UUID is " + uuid);
  }

  public void _testAddCookieFail() {
    System.out.println("Test Add Invalid User Cookie");
    String user = "bogususer@bogus.bog";
    String password = "foobar";
    String remoteAddr = "127.0.0.123";
    String uuid = null;
    try {
      uuid = getSSOClient().addCookie(user, password, remoteAddr);
      fail("This user should not be recognized.");
    } catch (Exception e) {
      System.out.println("  Invalid user rejected OK: " + user);
    }
  }

  public void _testCookieOAuth() {
    System.out.println("Test Cookie OAuth");
//    String key = "16432fc4-1626-4618-b466-9312886b33af";
    String key = "6685c602-d85f-483c-affd-721ac498d9e6";
    SSOCookie cookie = getSSOClient().findCookieOauth(key);
    System.out.println("  Got OAUTH cookie OK: " + cookie);
  }

  public void _testFindUserCookie() {
    String key = "0468c3b8-7de4-45f0-bd08-47717c18f20c";
    SSOCookie cookie = getSSOClient().findCookieUser(key);
    System.out.println("got User cookie OK: " + cookie);
  }

}
