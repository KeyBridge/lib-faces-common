package ch.keybridge.lib.faces.sso.client;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

/**
 * Service wrapper for various SOAP services.
 * <p>
 * To get a service port you must supply the WSDL location.
 * <p>
 * To streamline the process configure a portal.properties file. Place your
 * property files in the
 * <em>glassfish-install-dir</em>/glassfish/domains/<em>domain-name</em>/lib/classes
 * directory and they will be accessible from within your applications via the
 * ResourceBundle class.
 * <p>
 * For example, add a property file named settings.properties to this directory
 * and then access values from the file like this:
 * <p>
 * ResourceBundle.getBundle("portal").getString("my-property-key");
 * <p>
 * Developer note: Change the endpoint address by updating the request context
 * ENDPOINT_ADDRESS_PROPERTY.
 * <p/>
 * The BindingProvider interface provides access to the protocol binding and
 * associated context objects for request and response message processing.
 * <p/>
 * The ENDPOINT_ADDRESS_PROPERTY is the target service endpoint address. The URI
 * scheme for the endpoint address specification MUST correspond to the
 * protocol/transport binding for the binding in use.
 * <p/>
 * @see <a
 * href="http://docs.oracle.com/javase/7/docs/api/javax/xml/ws/BindingProvider.html">BindingProvider</a>
 * @author Jesse Caulfield <jesse@caulfield.org>
 * @since 01/15/15
 */
@WebServiceClient
public class SSOSOAPClient extends Service {

  /**
   * The resource service bundle name. This is stored in the
   * glassfish/[domain]/lib/classes/portal.properties
   */
  public final static String BUNDLE = "sso";

  public SSOSOAPClient(String wsdlLocation, QName serviceName) throws Exception {
    this(new URL(wsdlLocation), serviceName);
  }

  public SSOSOAPClient(URL wsdlLocation, QName serviceName) {
    super(wsdlLocation, serviceName);
  }

  public SSOSOAPClient(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
    super(wsdlLocation, serviceName, features);
  }

  /**
   * Add the SOAP client LOGGER to the handler chain.
   * <p>
   * @param object a SOAP port instance.
   * @param pretty TRUE to enable multi-line pretty print XML
   */
  public static void enableLogging(Object object, boolean pretty) {
    BindingProvider bindingProvider = (BindingProvider) object;
    List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
    handlerChain.add(new SOAPLogger(pretty));
    bindingProvider.getBinding().setHandlerChain(handlerChain);
  }

  /**
   * Add the SOAP client LOGGER to the handler chain with pretty print enable.d
   * <p>
   * @param object a SOAP port instance.
   */
  public static void enableLogging(Object object) {
    enableLogging(object, true);
  }

  //<editor-fold defaultstate="collapsed" desc="SSO">
  public final static QName QNAME_SSO = new QName("http://keybridge.ch/sso", "service");

  /**
   *
   * @return returns Sso
   */
  @WebEndpoint(name = "ssoPort")
  public SSO getSSOPort() {
    return super.getPort(new QName("http://keybridge.ch/sso", "ssoPort"), SSO.class);
  }

  /**
   * Get a SOAP Service.
   * <p>
   * @param wsdlLocation the WSDL location.
   * @return a SOAP port instance.
   * @throws Exception if the "wsdl.[service]" entry does not exist in the
   *                   portal.properties file.
   */
  public static SSO getSSOInstance(String wsdlLocation) throws Exception {
    return new SSOSOAPClient(wsdlLocation, QNAME_SSO).getSSOPort();
  }

  /**
   * Get a SOAP Service.
   * <p>
   * @return a SOAP port instance.
   * @throws Exception if the "wsdl.[service]" entry does not exist in the
   *                   portal.properties file.
   */
  public static SSO getSSOInstance() throws Exception {
    return getSSOInstance(ResourceBundle.getBundle(BUNDLE).getString("sso.wsdl"));
  }//</editor-fold>

}
