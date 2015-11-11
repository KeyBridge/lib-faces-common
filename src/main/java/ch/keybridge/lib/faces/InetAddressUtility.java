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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility methods to handle the <code>X-Forwarded-For</code> request header.
 * <p>
 * The X-Forwarded-For header was first introduced by Squid as a means of
 * passing on the IP address of the client to the server. It has since been
 * widely adopted by other proxy servers and load balancers so it's pretty much
 * considered a standard even if it technically isn't. What you are supposed to
 * get as your header is this:
 * <p>
 * <code>X-Forwarded-For: clientIP, server1IP, server2IP, server3IP</code>
 * <p>
 * The client IP address should be first, followed by first proxy server,
 * followed by any other servers in a comma separated list. The final server
 * that passes the request on to you won't be in the list, a proxy server or
 * load balancer will only append the address of server it received the request
 * from if the X-Forwarded-For header was passed to it otherwise it just
 * constructs a new X-Forwarded-For with just the client address in it. The
 * address of the last server in the complete chain is simply the address of the
 * client making the request to your server. But as usual in the web world there
 * are no guarantees.
 * <p>
 * There are some important catches to consider before you proceed to use this
 * header to do anything interesting:
 * <p>
 * Most importantly, headers can be crafted by anyone, never trust a header
 * value unless you are certain that it can't be spoofed. I'd actually just
 * simplify that to just never trust a header value. So if you are going to use
 * it then don't use it for anything that has security implications. The client
 * IP address that you get from the first entry may not actually be the address
 * that you want. Most of the time the requests will probably come directly from
 * the browser of your visitor but what if they are behind a proxy server within
 * a private network themselves? The IP address you may end up with could be
 * something like 10.1.34.121 which is of no value because it only tells you
 * that they are sitting on a private network somewhere in the world.
 * <p>
 * Best Guess IP Address When using X-Forwarded-For, the assumption normally
 * made is that the first IP address in the list is the client address that you
 * can use to do interesting things with, like IP address geolocation (à la
 * GeoIP). But what about private addresses? What about the casual browser at
 * McDonalds using their WiFi with a 10.x.x.x address or a company network with
 * a 192.168.x.x internal address structure? You'll end up with a very unhelpful
 * address that'll tell you nothing very interesting about the client.
 * <p>
 * If you have a client behind one of these networks and it's not routed through
 * a proxy server then you'll probably just get the IP address of the NAT
 * gateway which is likely to be the address you want to use. If the request is
 * routed through a proxy server then you may get an X-Forwarded-For that looks
 * something like this:
 * <p>
 * X-Forwarded-For: 10.208.4.38, 58.163.175.187
 * <p>
 * You may also have a chain of multiple servers, perhaps you have a downstream
 * proxy server going through a larger upstream one before heading out of the
 * network, so you may get something like this:
 * <p>
 * X-Forwarded-For: 10.208.4.38, 58.163.1.4, 58.163.175.187
 * <p>
 * Or, the downstream proxy server could be within the private network, perhaps
 * a departmental proxy server connecting to a company-wide proxy server and
 * then this may happen:
 * <p>
 * X-Forwarded-For: 10.208.4.38, 10.10.300.23, 58.163.175.187
 * <p>
 * This could of course be even more complex as you may have a longer chain of
 * proxy servers (although I've never actually seen anyone chain more than 2
 * layers of proxy servers together in a network before).
 * <p>
 * The rule that I suggest is <strong><em>Always use the leftmost non-private
 * address.</em></strong>.
 *
 * @see <a
 * href="http://r.va.gg/2011/07/handling-x-forwarded-for-in-java-and-tomcat.html">Handling
 * X-Forwarded-For in Java</a>
 * @author Jesse Caulfield 08/11/14
 */
public class InetAddressUtility {

  private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
  private static final Pattern PRIVATE_IP_ADDRESS_PATTERN = Pattern.compile("(^127\\.0\\.0\\.1)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

  private static String findNonPrivateIpAddress(String s) {
    Matcher matcher = IP_ADDRESS_PATTERN.matcher(s);
    while (matcher.find()) {
      if (!PRIVATE_IP_ADDRESS_PATTERN.matcher(matcher.group(0)).find()) {
        return matcher.group(0);
      }
      matcher.region(matcher.end(), s.length());
    }
    return null;
  }

  /**
   * Get the (client) request IP address. This method inspects and tries to
   * return the the first non-private IP Address from within an
   * "X-Forwarded-For" request header. If "X-Forwarded-For" is not present then
   * the request remote Address is returned.
   *
   * @param request the HTTP servlet request
   * @return the best guess remote address
   */
  @SuppressWarnings("NestedAssignment")
  public static String getAddressFromRequest(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && (forwardedFor = findNonPrivateIpAddress(forwardedFor)) != null) {
      return forwardedFor;
    }
    return request.getRemoteAddr();
  }

  /**
   * Get the DNS-resolved Hostname for a client request.
   *
   * @param request the HTTP servlet request
   * @return the resolved host name
   */
  public static String getHostnameFromRequest(HttpServletRequest request) {
    String addr = getAddressFromRequest(request);
    try {
      return Inet4Address.getByName(addr).getHostName();
    } catch (Exception e) {
    }
    return addr;
  }

  /**
   * Get the Internet address from a client request.
   *
   * @param request the HTTP servlet request
   * @return the Internet address
   * @throws UnknownHostException if the internet address does not resolve
   */
  public static InetAddress getInet4AddressFromRequest(HttpServletRequest request) throws UnknownHostException {
    return Inet4Address.getByName(getAddressFromRequest(request));
  }
}
