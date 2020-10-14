/*
 * Copyright 2020 Key Bridge. All rights reserved. Use is subject to license
 * terms.
 *
 * This software code is protected by Copyrights and remains the property of
 * Key Bridge and its suppliers, if any. Key Bridge reserves all rights in and to
 * Copyrights and no license is granted under Copyrights in this Software
 * License Agreement.
 *
 * Key Bridge generally licenses Copyrights for commercialization pursuant to
 * the terms of either a Standard Software Source Code License Agreement or a
 * Standard Product License Agreement. A copy of either Agreement can be
 * obtained upon request by sending an email to info@keybridgewireless.com.
 *
 * All information contained herein is the property of Key Bridge and its
 * suppliers, if any. The intellectual and technical concepts contained herein
 * are proprietary.
 */
package ch.keybridge.faces;

import org.junit.*;

/**
 *
 * @author Key Bridge
 */
public class InetAddressUtilityTest {

  public InetAddressUtilityTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @AfterClass
  public static void tearDownClass() {
  }

  @Before
  public void setUp() {
  }

  @After
  public void tearDown() {
  }

//  @Test
//  public void testGetAddressFromRequest() {
//    System.out.println("getAddressFromRequest");
//    HttpServletRequest request = null;
//    String expResult = "";
//    String result = InetAddressUtility.getAddressFromRequest(request);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//  @Test
//  public void testGetHostnameFromRequest() {
//    System.out.println("getHostnameFromRequest");
//    HttpServletRequest request = null;
//    String expResult = "";
//    String result = InetAddressUtility.getHostnameFromRequest(request);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
//  @Test
//  public void testGetInet4AddressFromRequest() throws Exception {
//    System.out.println("getInet4AddressFromRequest");
//    HttpServletRequest request = null;
//    InetAddress expResult = null;
//    InetAddress result = InetAddressUtility.getInet4AddressFromRequest(request);
//    assertEquals(expResult, result);
//    fail("The test case is a prototype.");
//  }
  @Test
  public void testIsPrivateIpAddress() {
    System.out.println("isPrivateIpAddress");

    String[] addrs = new String[]{"192.168.1.1", "208.145.123.1"};

    for (String addr : addrs) {
      System.out.println("  " + addr + "  " + InetAddressUtility.isPrivateIpAddress(addr));
    }

    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("192.168.1.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("192.168.241.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("127.0.0.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("10.0.1.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("10.231.1.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("172.21.1.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("172.30.1.1"));
    Assert.assertTrue(InetAddressUtility.isPrivateIpAddress("172.19.1.1"));

    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("9.0.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("11.1.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("127.1.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("172.168.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("110.201.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("200.131.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("172.33.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("20.241.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("96.23.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("74.21.1.1"));
    Assert.assertFalse(InetAddressUtility.isPrivateIpAddress("72.31.1.1"));

  }

}
