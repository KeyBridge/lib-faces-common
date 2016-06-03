package ch.keybridge.lib.faces.sso.client;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.8 Generated source
 * version: 2.2
 * <p>
 */
@WebService(name = "sso", targetNamespace = "http://keybridge.ch/sso")
public interface SSO {

  /**
   * Find and return the USER SSO Session corresponding to the provided
   * "JSESSIONSSO" cookie UUID. This method supports Portal sign in.
   *
   * @param uuid the browser cookie UUID
   * @return the corresponding SSOSession instance, null if not found
   */
  @WebMethod
  public SSOSession findSessionUser(@WebParam(name = "uuid") String uuid);

  /**
   * Find and return the API SSO Session corresponding to the provided
   * oauth_consumer_key. This method supports API queries.
   *
   * @param oauth_consumer_key the API outh consumer key
   * @return the corresponding SSOSession instance, null if not found in the
   *         database and or if there is no corresponding credential for the
   *         given consumer_key
   */
  @WebMethod
  public SSOSession findSessionOauth(@WebParam(name = "oauth_consumer_key") String oauth_consumer_key);

  /**
   * Update the GlassfishUser "dateLastSeen" field
   *
   * @param username the user email addCookieress
   */
  @WebMethod
  @Oneway
  public void updateLastSeen(@WebParam(name = "username") String username);

  /**
   * Add a user SSO Session Cookie to the current SSO Manager state. Returns a
   * SSOSession session for the given user name and password. If a SSO session
   * already exists for the user then that is returned. If no session exists
   * then a NEW session is created with the user name and password fields.
   * <p>
   * This method is called when a user logs in from a JSF sign in page.
   *
   * @param username   the user name
   * @param password   the user password
   * @param remoteAddr the users remote IP address
   * @return a SSO session for the user
   */
  @WebMethod
  public String createSession(@WebParam(name = "username") String username,
                              @WebParam(name = "password") String password,
                              @WebParam(name = "remoteAddr") String remoteAddr);

  /**
   * Remove the SSO Session for the indicated cookie name. If no session exists
   * the method completes silently.
   *
   * @param uuid the cookie UUID
   */
  @WebMethod
  @Oneway
  public void clearSession(@WebParam(name = "uuid") String uuid);
}
