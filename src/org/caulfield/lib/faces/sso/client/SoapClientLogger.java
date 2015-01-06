/**
 * Copyright (C) Key Bridge Global LLC and/or its affiliates.
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.caulfield.lib.faces.sso.client;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * The SOAPHandler class extends Handler to provide type safety for the message
 * context parameter and add a method to obtain access to the headers that may
 * be processed by the handler.
 * <p/>
 * You can update this class file to log all soap query and response messages to
 * a file or to a database.
 * <p/>
 * @see <a
 * href="http://docs.oracle.com/javaee/5/api/javax/xml/ws/handler/soap/SOAPHandler.html">Interface
 * SOAPHandler</a>
 * @see <a
 * href="https://metro.java.net/nonav/1.2/guide/Logging.html">Logging</a>
 * @author jesse
 */
public class SoapClientLogger implements SOAPHandler<SOAPMessageContext> {

  @Override
  public Set<QName> getHeaders() {
    return null;
  }

  /**
   * The handleMessage method is invoked for normal processing of inbound and
   * outbound messages. Refer to the description of the handler framework in the
   * JAX-WS specification for full details.
   * <p/>
   * @param context the message context
   * @return An indication of whether handler processing should continue for the
   *         current message. <ul> <li> TRUE to continue processing.</li> <li>
   *         FALSE to block processing.</li></ul>
   */
  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    SOAPMessage soapMessage = context.getMessage();
    /**
     * Identify if this is an in or outbound message.
     */
    if ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) {
      System.out.println("MESSAGE OUTBOUND");
    } else {
      System.out.println("MESSAGE INBOUND");
    }
    /**
     * Try to dump the message to the console.
     */
    try {
      soapMessage.writeTo(System.out);
    } catch (SOAPException | IOException ex) {
      Logger.getLogger(SoapClientLogger.class.getName()).log(Level.SEVERE, null, ex);
    }
    /**
     * Add a new line after the message.
     */
    System.out.println("");
    /**
     * Always return TRUE. This must return TRUE for message handler chaining to
     * work.
     */
    return true;
  }

  /**
   * The handleFault method is invoked for fault message processing. Refer to
   * the description of the handler framework in the JAX-WS specification for
   * full details.
   * <p/>
   * @param context the message context
   * @return An indication of whether handler processing should continue for the
   *         current message. <ul> <li> TRUE to continue processing.</li> <li>
   *         FALSE to block processing.</li></ul>
   */
  @Override
  public boolean handleFault(SOAPMessageContext context) {
    /**
     * Always return true.
     */
    return true;
  }

  /**
   * Called at the conclusion of a message exchange pattern just prior to the
   * JAX-WS runtime dispatching a message, fault or exception. Refer to the
   * description of the handler framework in the JAX-WS specification for full
   * details.
   * <p/>
   * @param context the message context
   */
  @Override
  public void close(MessageContext context) {
    /**
     * No operation.
     */
  }
}
