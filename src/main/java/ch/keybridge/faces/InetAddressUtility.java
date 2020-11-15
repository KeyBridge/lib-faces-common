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
package ch.keybridge.faces;

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

  /**
   * Generic IP address pattern matcher.
   */
  private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
  /**
   * Private IP block matcher.
   */
  private static final Pattern PRIVATE_IP_ADDRESS_PATTERN = Pattern.compile("(^127\\.0\\.0\\.1)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)");

  /**
   * Find a non private address in the provided string.
   *
   * @param s a string
   * @return a non-private IP address, if one exists
   */
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
   * "X-Forwarded-For" request header. If <code>X-Forwarded-For</code> is not
   * present then the unmodified request remote Address is returned.
   * <p>
   * The X-Forwarded-For request header helps you identify the IP address of a
   * client when you use an HTTP or HTTPS load balancer. Because load balancers
   * intercept traffic between clients and servers, your server access logs
   * contain only the IP address of the load balancer. To see the IP address of
   * the client, use the X-Forwarded-For request header. Elastic Load Balancing
   * stores the IP address of the client in the X-Forwarded-For request header
   * and passes the header to your server.
   * <p>
   * The X-Forwarded-For request header takes the following form:
   * <p>
   * {@code X-Forwarded-For: client-ip-address}
   *
   * @param request the HTTP servlet request
   * @return the best guess remote address
   */
  public static String getRemoteAddr(final HttpServletRequest request) {
    return request.getHeader("X-Forwarded-For") == null
           ? request.getRemoteAddr()
           : request.getHeader("X-Forwarded-For");
  }

  /**
   * Get the DNS-resolved Hostname for a client request.
   *
   * @param request the HTTP servlet request
   * @return the resolved host name
   */
  public static String getHostName(final HttpServletRequest request) {
    String addr = getRemoteAddr(request);
    try {
      return Inet4Address.getByName(addr).getHostName();
    } catch (Exception e) {
    }
    return addr;
  }

  /**
   * The X-Forwarded-Proto request header helps you identify the protocol (HTTP
   * or HTTPS) that a client used to connect to your load balancer. Your server
   * access logs contain only the protocol used between the server and the load
   * balancer; they contain no information about the protocol used between the
   * client and the load balancer. To determine the protocol used between the
   * client and the load balancer, use the X-Forwarded-Proto request header.
   * Elastic Load Balancing stores the protocol used between the client and the
   * load balancer in the X-Forwarded-Proto request header and passes the header
   * along to your server.
   * <p>
   * Your application or website can use the protocol stored in the
   * X-Forwarded-Proto request header to render a response that redirects to the
   * appropriate URL.
   * <p>
   * The X-Forwarded-Proto request header takes the following form:
   * <p>
   * {@code X-Forwarded-Proto: originatingProtocol}
   * <p>
   * The following example contains an X-Forwarded-Proto request header for a
   * request that originated from the client as an HTTPS request:
   * {@code X-Forwarded-Proto: https}
   *
   * @param request the HTTP servlet request
   * @return the forwarded protocol
   */
  public static String getProtocol(final HttpServletRequest request) {
    return request.getHeader("X-Forwarded-Proto") == null
           ? request.getProtocol()
           : request.getHeader("X-Forwarded-Proto");
  }

  /**
   * The X-Forwarded-Port request header helps you identify the destination port
   * that the client used to connect to the load balancer.
   *
   * @param request the HTTP servlet request
   * @return the forwarded port
   */
  public static int getLocalPort(final HttpServletRequest request) {
    return request.getHeader("X-Forwarded-Port") == null
           ? request.getLocalPort()
           : Integer.parseInt(request.getHeader("X-Forwarded-Port"));
  }

  /**
   * Get the Internet address from a client request.
   *
   * @param request the HTTP servlet request
   * @return the Internet address
   * @throws UnknownHostException if the internet address does not resolve
   */
  public static InetAddress getInetAddress(final HttpServletRequest request) throws UnknownHostException {
    return Inet4Address.getByName(getRemoteAddr(request));
  }

  /**
   * Evaluate an IP address string and determine if it is a private (or public)
   * address.
   *
   * @param ipAddress the IP address string. e.g. {@code 192.168.1.1}
   * @return true if the address is private
   */
  public static boolean isPrivateIpAddress(String ipAddress) {
    return PRIVATE_IP_ADDRESS_PATTERN.matcher(ipAddress).find();
  }
}
