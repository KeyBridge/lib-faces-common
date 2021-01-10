/*
 * Copyright (C) 2017 Key Bridge LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.keybridge.faces.wadl;

import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import net.java.dev.wadl.*;

/**
 * JSF backing bean supporting WADL rendering and auto-documentation.
 * <p>
 * To use: first initialize this bean on the base by EITHER uncommenting the
 * postConstruct method and calling the page with a {@code wadl} query parameter
 * OR initializing this bean by calling the {@code onSetWadl(String wadl)}
 * method on page load.
 *
 *
 * @author Key Bridge LLC
 * @since v0.3.0 created 01/10/17 as an alternative to Swagger.io
 */
public abstract class AbstractWadlBean implements LabelProvider, Serializable {

  private static final Logger LOG = Logger.getLogger(AbstractWadlBean.class.getName());

  /**
   * The current WADL URL.
   */
  protected String wadl;

  /**
   * The unmarshaled WADL application.
   */
  protected Application application;
  /**
   * The URL to download the WADL file.
   */
  protected String wadlUrl;

  /**
   * Creates a new instance of WadlBean
   */
  public AbstractWadlBean() {
    /**
     * Disable SSL handshake.
     */
    System.setProperty("jsse.enableSNIExtension", "false");
  }

  /**
   * Get the WADL url.
   *
   * @return the url
   */
  public String getWadl() {
    return wadl;
  }

  public void setWadl(String wadl) {
    this.wadl = wadl;
  }

  /**
   * Get the fully qualified URL to the WADL file.
   *
   * @return a URL to the WADL file.
   */
  public String getWadlUrl() {
    return wadlUrl;
  }

  /**
   * AJAX method typically called on page load to initialize this WADL bean and
   * update the page content.
   * <p>
   * This method allows the user to specify the WADL location with a fully
   * qualified URL. e.g.
   * {@code http://localhost/application/rest/application.wadl}
   *
   * @param wadlUrl the fully qualified WADL URL
   */
  public void load(String wadlUrl) {
    try {
      downloadWADL(wadlUrl); // sets the wadlUrl field.
    } catch (Exception exception) {
//      LOG.log(Level.INFO, "Error downloading WADL file: {0}", exception.getMessage());
    }
  }

  /**
   * Helper method that tries to automatically load the WADL file from various
   * commonly used REST context prefixes.
   * <p>
   * This method searches the current application context root for a resource
   * named {@code application.wadl}. The search will attempt to retrieve the
   * file {@code [context-root]/{rest-context}/application.wadl}.
   */
  public abstract void autoload();

  /**
   * Example implementation:
   * <p>
   * Get the current context path (i.e. the application context root). This is
   * copied from FacesUtil.getContextPath().
   */
//  {
//    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
//    String contextPath = new StringBuilder()
//      .append(context.getRequestScheme())
//      .append("://")
//      .append(context.getRequestServerName())
//      .append((context.getRequestServerPort() != 80 && context.getRequestServerPort() != 443)
//              ? ":" + context.getRequestServerPort()
//              : "")
//      .append(context.getRequestContextPath())
//      .toString();
//    /**
//     * Try to load the WADL from various commonly used JAXRS contexts. If the
//     * application declares a custom context is must be specified.
//     */
//    for (String restContext : new String[]{"api", "rest", "resource", "resources", "webresources"}) {
//      load(contextPath + "/" + restContext + "/application.wadl"); // sets the wadlUrl field.
//      if (wadl != null) {
//        break;
//      }
//    }
//  }
  /**
   * Internal method called when this class is constructed. This reads and
   * parses the WADL file.
   *
   * @param wadlUrl a fully qualified URL to a WADL file
   * @throws Exception if the {@code wadlUrl} value is null or the file fails to
   *                   parse.
   */
  private void downloadWADL(String wadlUrl) throws Exception {
    /**
     * Download and parse the WADL file.
     */
    if (wadlUrl == null || wadlUrl.isEmpty()) {
      throw new Exception("Null or empty wadl URL.");
    }
    /**
     * Try to use an all-trusting trust manager that ignores all SSL errors.
     */
    Client client;
    try {
      SSLContext sslcontext = SSLContext.getInstance("TLSv1.2"); //Java 8   // NoSuchAlgorithmException
      System.setProperty("https.protocols", "TLSv1.2");  //Java 8
      sslcontext.init(null, new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[0];
        }

      }}, new java.security.SecureRandom());
      client = ClientBuilder.newBuilder().sslContext(sslcontext).hostnameVerifier((s1, s2) -> true).build();
    } catch (NoSuchAlgorithmException | KeyManagementException noSuchAlgorithmException) {
      client = ClientBuilder.newClient();
    }
    /**
     * Read and unmarshal the application from the WADL file.
     */
    this.application = client.target(wadlUrl).request().get(Application.class);
    /**
     * Call PostLoad to set the inter-object parent/child relationships.
     */
    this.application.postLoad();
    /**
     * Set the WADL IFF the application was successfully loaded and parsed.
     */
    this.wadl = wadlUrl;
    /**
     * Record the WADL url.
     */
    this.wadlUrl = wadlUrl;
  }

  /**
   * Get the WADL top-level Application.
   *
   * @return the WADL top-level Application
   */
  public Application getApplication() {
    return application;
  }

  /**
   * Determine if the application is set. This should always return TRUE.
   *
   * @return TRUE if the application is set, otherwise false.
   */
  public boolean isSetApplication() {
    return application != null;
  }

  /**
   * Get the base resources for the indicated WADL
   *
   * @return the WADL base resources
   */
  public List<Resource> getBaseResources() {
    return application != null
           ? application.getBaseResource().getResource()
           : new ArrayList<>();
  }

  /**
   * Find a resources entry matching the provided URI base.
   * <p>
   * This is commonly used to find a (top level) Resources object to build a
   * menu of (second level) Resource entries under a common base path.
   *
   * @param base the URI base
   * @return the matched Resources instance
   */
  public Resources findResources(String base) {
    /**
     * If the application is not set the return an empty resource.
     */
    return application != null
           ? application.findResources(base)
           : new Resources();
  }

  /**
   * Find a specific resource matching the indicated path.
   * <p>
   * This is commonly used to find a (second level) Resource entry when
   * displaying a page detail.
   *
   * @param path the resource path.
   * @return the matching Resource.
   */
  public Resource findResource(String path) {
    return application.findResource(path);
  }

  /**
   * Find all methods identified in the {@code Application} instance belonging
   * to the indicated Resource, which is identified by its Path.
   * <p>
   * This is commonly used to find a list of (lowest level) Method entries when
   * displaying a page detail.
   *
   * @param resource the Resource
   * @return a non-null ArrayList.
   */
  public List<Method> findMethods(Resource resource) {
    /**
     * Failsafe in case the application did not load.
     */
    if (application == null) {
      return new ArrayList<>();
    }

    /**
     * Initialize and recursively populate the methods array. Recurse if the
     * found resource is not null.
     */
    ArrayList methods = new ArrayList();
    if (resource != null) {
      methods.addAll(findMethodsRecursive(resource));
    }
    /**
     * Sort the array in alphabetical order.
     */
    Collections.sort(methods);
    return methods;
  }

  /**
   * Recursively a {@code Resource} instance to identify and extract all
   * methods.
   *
   * @param resource a {@code Resource} instance
   * @return a list of all methods in the Resource tree
   */
  private List<Method> findMethodsRecursive(Resource resource) {
    List<Method> tempMethods = new ArrayList<>(resource.getMethods());
    resource.getResources().stream().forEach((res) -> {
      tempMethods.addAll(findMethodsRecursive(res));
    });
    return tempMethods;
  }

  /**
   * PUT / POST only.
   * <p>
   * Build a map of the representation element names and their supported media
   * types. This type of encoding maps a single Param field names with multiple
   * encoding Representations. This occurs when a PUT / POST method accepts an
   * object (as opposed to a form).
   * <p>
   * Note that use of representation elements is confined to HTTP methods that
   * accept an entity body in the request (e.g., PUT or POST). Sibling
   * representation elements represent logically equivalent alternatives, e.g.,
   * a particular resource might support multiple XML grammars for a particular
   * request.
   *
   * @param method the method
   * @return a non-null MultivaluedMap of element names and supported media
   *         types
   */
  public MultivaluedMap<String, Representation> findMethodRequestElements(Method method) {
    MultivaluedMap<String, Representation> elementMediaTypes = new MultivaluedHashMap<>();
    if (HTTPMethods.PUT.equals(method.getName()) || HTTPMethods.POST.equals(method.getName())) {
      if (method.getRequest() != null) {
        method.getRequest().getRepresentation().stream().forEach((Representation representation) -> {
          if (representation.getElement() != null) {
            elementMediaTypes.add(representation.getElement().getLocalPart(), representation);
          }
        });
      }
    }
    return elementMediaTypes;
  }

  /**
   * PUT / POST only.
   * <p>
   * Find method elements for Form annotated POST / PUT methods. This type of
   * encoding groups multiple Param objects with a single encoding
   * Representation.
   *
   * @param method the method
   * @return a non-null MultivaluedMap of element names and supported media
   *         types
   */
  public MultivaluedMap<Representation, Param> findUploadMethodElements(Method method) {
    MultivaluedMap<Representation, Param> formElements = new MultivaluedHashMap<>();
    if (HTTPMethods.PUT.equals(method.getName()) || HTTPMethods.POST.equals(method.getName())) {
      if (method.getRequest() != null) {
        method.getRequest().getRepresentation().stream().forEach(representation -> {
          representation.getParam().stream().forEach(param -> {
            formElements.add(representation, param);
          });
        });
      }
    }
    return formElements;
  }

  /**
   * PUT / POST only.
   * <p>
   * Build a map of the representation element names and their supported media
   * types. This type of encoding maps a single Param field names with multiple
   * encoding Representations.
   *
   * @param response one method response
   * @return a non-null MultivaluedMap of element names and supported media
   *         types
   */
  public MultivaluedMap<String, Representation> findMethodResponseElements(Response response) {
    MultivaluedMap<String, Representation> elementMediaTypes = new MultivaluedHashMap<>();
    try {
      response.getRepresentation().stream().forEach((representation) -> {
        elementMediaTypes.add(representation.getElement() != null
                              ? representation.getElement().getLocalPart()
                              : "Response",
                              representation);
      });
    } catch (Exception e) {
      LOG.log(Level.WARNING, "findMethodResponseElements ERROR.  {0}", e.getMessage());
    }
    return elementMediaTypes;
  }

  /**
   * GET only.
   * <p>
   * Build a list of all parameters relevant to a method. This captures the
   * parent and immediate parameters.
   *
   * @param method the method
   * @return a non-null ArrayList
   */
  public List<Param> findMethodParameters(Method method) {
    /**
     * Create a sorted TreeSet. Use a TreeSet to avoid duplicate parameters
     * where multiple encodings are allowed (which is an erroneous configuration
     * but Jersey appears to allow it.
     */
    Comparator<Param> comparator = (Param o1, Param o2) -> o1.getName().compareTo(o2.getName());
    Collection<Param> parameters = new TreeSet(comparator);
    parameters.addAll(method.getParent().getParam());
    /**
     * Add all request parameters. This captures Query and Template method
     * parameters plus Post headers.
     */
    try {
      if (method.isSetRequest()) {
        parameters.addAll(method.getRequest().getParam());
      }
    } catch (Exception e) {
      LOG.log(Level.WARNING, "findMethodParameters ERROR for method {0}.  {1}", new Object[]{method.getId(), e.getMessage()});
    }
    /**
     * Use a parameter name filter to omit the javax.ws.rs.container.Suspended
     * attribute, which is picked up for asynchronous REST methods.
     */
    return parameters.stream().filter(p -> !p.getName().startsWith("javax.ws")).collect(Collectors.toList());
//    return new ArrayList<>(parameters);
  }

  /**
   * Determine if the method is either a PUT or POST type.
   *
   * @param method the method
   * @return TRUE if the method name is either PUT or POST, otherwise FALSE
   */
  public boolean isPutOrPost(Method method) {
    return HTTPMethods.PUT.equals(method.getName()) || HTTPMethods.POST.equals(method.getName());
  }

  /**
   * Get the label class type based upon the method name. This method assigns a
   * Bootstrap text color CSS tag for HTTP methods and for HTTP media types.
   * <p>
   * Methods supports are: HEAD, OPTIONS, GET, POST, PUT, DELETE. Media types
   * supported are application/[json, geojson, xml], multipart/form-data and
   * text/[html, plain, xml]
   *
   * @param methodName the method name or media type.
   * @return the label class
   */
  public String buildCSSType(String methodName) {
    switch (methodName) {
      case "HEAD":
      case "OPTIONS":
      case "application/geojson":
      case "application/geo+json":
      case MediaType.APPLICATION_JSON:
        return "info";

      case "GET":
      case MediaType.APPLICATION_XML:
        return "primary";

      case "POST":
        return "success";

      case "PUT":
        return "warning";

      case "DELETE":
      case MediaType.MULTIPART_FORM_DATA:
        return "danger";

      case MediaType.TEXT_HTML:
      case MediaType.TEXT_PLAIN:
      case MediaType.TEXT_XML:
      default:
        return "secondary";
    }
  }

  /**
   * Wrap variables in a URI pattern in an html span and assign the
   * 'text-primary' class. Output from this method should not be escaped.
   *
   * @param uriPattern the URI pattern
   * @return the HTML-coded URI pattern.
   */
  public String buildFormattedURI(String uriPattern) {
    return uriPattern.replaceAll("\\{(\\w+)\\}", "<span class=\"wadl-param\">{$1}</span>");
  }

  /**
   * Insert paragraph markers into the description text. This method replaces
   * all large whitespace blocks with a paragraph closing and opening HTML tag.
   * <p>
   * This method should only be used inside a HTML paragraph!
   *
   * @param description the description text
   * @return the description text with paragraph tags inserted
   */
  public String formatDescription(String description) {
    return description.replaceAll("\\s{10,}", "</p>\n<p>");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLabel(String key) {
    /**
     * Abstract helper method to log if a requested name is not found. This
     * helps when developing a WADL labels file.
     */
    LOG.log(Level.INFO, "AbstractWadlBean getLabel {0}", key);
    return "<code>" + key + "</code>";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLabel(String method, String parameter) {
    /**
     * Abstract helper method to log if a requested name is not found. This
     * helps when developing a WADL labels file.
     */
    LOG.log(Level.INFO, "AbstractWadlBean getLabel {0}-{1}", new Object[]{method, parameter});
    return "<code>" + method + "-" + parameter + "</code>";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMethodDescription(String key) {
    /**
     * Abstract helper method to log if a requested name is not found. This
     * helps when developing a WADL labels file.
     */
    LOG.log(Level.INFO, "AbstractWadlBean getMethodDescription {0}", key);
    return "<p>" + key + "</p>";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String parseMethodId(String methodId) {
    return methodId;
  }

}
