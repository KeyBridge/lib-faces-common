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

import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * A simple SOAP message logger. This writes SOAP messages to the Logger.
 * <p>
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
 * @author Jesse Caulfield
 */
public class SOAPLogger implements SOAPHandler<SOAPMessageContext> {

  private static final Logger logger = Logger.getLogger(SOAPLogger.class.getName());
  /**
   * Pretty Print = TRUE logs pretty-print XML.
   */
  private boolean pretty = true;

  public SOAPLogger() {
  }

  /**
   * Pretty Print = TRUE logs pretty-print XML.
   * <p>
   * @param prettyPrint pretty-print XML
   */
  public SOAPLogger(boolean prettyPrint) {
    this.pretty = prettyPrint;
  }

  @Override
  public Set<QName> getHeaders() {
    return null;
  }

  /**
   * Pretty Print = TRUE logs pretty-print XML.
   * <p>
   * @param pretty pretty-print XML
   */
  public void setPretty(boolean pretty) {
    this.pretty = pretty;
  }

  /**
   * The handleMessage method is invoked for normal processing of inbound and
   * outbound messages. Refer to the description of the handler framework in the
   * JAX-WS specification for full details.
   * <p/>
   * @param context the message context
   * @return An indication of whether handler processing should continue for the
   *         current message. <ul> <li> TRUE to continue processing.</li> <li>
   * FALSE to block processing.</li></ul>
   */
  @Override
  public boolean handleMessage(SOAPMessageContext context) {
    SOAPMessage soapMessage = context.getMessage();
    /**
     * Try to dump the message to the logger.
     */
    try {
      logger.log(Level.INFO,
                 ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
        ? "MESSAGE OUT {0}"
        : "MESSAGE IN {0}",
                 getSOAPMessageAsString(soapMessage));

    } catch (Exception ex) {
      logger.log(Level.SEVERE, null, ex);
    }
    /**
     * Always return TRUE. This must return TRUE for message handler chaining to
     * work.
     */
    return true;
  }

  /**
   * Pretty printing SOAP messages If your dealing with SAAJ API or you want to
   * create a Debug Message Handler that prints the SOAP Message then you can
   * call SOAPmessage.writeTo(System.out), but this method writes the full SOAP
   * message in one line and which can be little hard to read this is sample
   * output
   * <p>
   * @param soapMessage a SOAP message instance
   * @return pretty print xml
   * @throws Exception
   * @see <a
   * href="http://wpcertification.blogspot.com/2011/10/pretty-printing-soap-messages.html">Sunil's
   * Notes</a>
   */
  private String getSOAPMessageAsString(SOAPMessage soapMessage) throws Exception {
    if (pretty) {
      Transformer tf = TransformerFactory.newInstance().newTransformer();
      // Set XML formatting
      tf.setOutputProperty(OutputKeys.INDENT, "yes");
      tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      Source sc = soapMessage.getSOAPPart().getContent();
      ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(streamOut);
      tf.transform(sc, result);
      return streamOut.toString();
    } else {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      soapMessage.writeTo(outputStream);
      return outputStream.toString();
    }
  }

  /**
   * The handleFault method is invoked for fault message processing. Refer to
   * the description of the handler framework in the JAX-WS specification for
   * full details.
   * <p/>
   * @param context the message context
   * @return An indication of whether handler processing should continue for the
   *         current message. <ul> <li> TRUE to continue processing.</li> <li>
   * FALSE to block processing.</li></ul>
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
