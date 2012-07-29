/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.cas;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;


/** Builds the urls of the different application services, such as web and CAS
 * related urls.
 *
 * The possible urls are:
 *
 * - The main application url (the 'service', in CAS parlance). This url
 *   belongs to the application that needs an authenticated user.
 *
 * - The url of the servlet context path of the web service.
 *
 * - The login url. This is served by CAS. It is the url where the main service
 *   is redirected if the user is not authenticated.
 *
 * - The cas validator url. This is served by CAS. It is used to verify if a
 *   token was generated by the expected server.
 *
 * The builder tranforms the request url to the possible services, using the
 * corresponding UrlTransformer according to the service. The UrlTransformers
 * tranform the url up to the context path (http://server:port/context-path).
 * Once the request is tranformed, the builder appends the correct url to it
 * (loginUrl, serviceUrl or casValidatorUrl).
 */
public class CasServicesUrlBuilder {

  /** An identity url transformer.
   */
  private static final UrlTransformer IDENTITY = new UrlTransformer();

  /** The service url fragment.
   *
   * The service corresponds to the url that the cas authentication service
   * redirects the client to after a succesful authentication. It is never
   * null.
   */
  private String serviceUrl;

  /** The CAS login url fragment.
   *
   * Usually something like <code>cas/login</code>.
   */
  private String loginUrl;

  /** The cas ticket validator url fragment.
   *
   * When the service receives a ticket, it uses this url to validate that the
   * ticket came from the expected web application. It is never null.
   *
   * Usually something like <code>cas/proxyValidate</code>.
   */
  private String casValidatorUrl;

  /** Url to obtain a proxy-granting ticket from CAS.
   */
  // private String proxyCallbackUrl = null;

  /** The url tranformer to convert the request to the cas server base url.
   *
   * It is never null. It defaults to the identity tranformer.
   */
  private UrlTransformer casUrlTransformer = IDENTITY;

  /** The url tranformer to convert the request to the web server base url.
   *
   * It is never null. It defaults to the identity tranformer.
   */
  private UrlTransformer validatorUrlTransformer = IDENTITY;

  /** Builds a new service builder.
   *
   * @param theCasUrlTransformer The transformer that converts the request url
   * to the cas url. It can be null, in which case the cas url is the same as
   * the request url.
   *
   * @param theValidatorUrlTransformer The transformer that converts the
   * request url to the cas ticket validator url.  It can be null, in which
   * case the validator url is the same as the request url.
   *
   * @param theServiceUrl The fragment of the url that cas redirects after a
   * succesful login. It cannot be null.
   *
   * @param theLoginUrl The fragment of the url that acegi redirects to
   * authenticate a user. It cannot be null.
   *
   * @param theCasValidatorUrl The fragment of the url that acegi uses to
   * validate a ticket. It cannot be null.
   */
  public CasServicesUrlBuilder(final UrlTransformer theCasUrlTransformer, final
      UrlTransformer theValidatorUrlTransformer, final String theServiceUrl,
      final String theLoginUrl, final String theCasValidatorUrl) {

    Validate.notNull(theServiceUrl, "The service cannot be null");
    Validate.notNull(theLoginUrl, "The login url cannot be null");
    Validate.notNull(theCasValidatorUrl, "The cas validator url cannot be"
        + " null");

    if (theCasUrlTransformer != null) {
      casUrlTransformer = theCasUrlTransformer;
    }
    if (theValidatorUrlTransformer != null) {
      validatorUrlTransformer = theValidatorUrlTransformer;
    }

    serviceUrl = theServiceUrl;
    loginUrl = theLoginUrl;
    casValidatorUrl = theCasValidatorUrl;
  }

  /** Builds the application service url.
   *
   * Usually something like
   * <code>http://..../j_acegi_cas_security_check</code>. This is the url that
   * cas redirects to after a successful login.
   *
   * @param request The http request object use to construct the full service
   * url. It cannot be null.
   *
   * @return the service url. It never returns null.
   */
  public String buildServiceUrl(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return createUrl(createServiceBaseUrl(request), serviceUrl);
  }

  /** Builds the CAS login full URL.
   *
   * Usually something like <code>http://..../cas/login</code>.
   *
   * @param request The http request object use to construct the full service
   * url. It cannot be null.
   *
   * @return the login URL. It never returns null.
   */
  public String buildLoginUrl(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return createUrl(casUrlTransformer.transform(request.getScheme(),
        request.getServerName(), request.getServerPort(),
        request.getContextPath()), loginUrl);
  }

  /** Builds the CAS ticket validator full URL.
   *
   * Usually something like <code>http://..../cas/proxyValidate</code>. It is
   * used to verify if a ticket was generated by the expected cas server.
   *
   * @param request The http request object use to construct the full service
   * url. It cannot be null.
   *
   * @return the login URL. It never returns null.
   */
  public String buildCasValidatorUrl(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return createUrl(validatorUrlTransformer.transform(request.getScheme(),
        request.getServerName(), request.getServerPort(),
        request.getContextPath()), casValidatorUrl);
  }

  /** Optional callback URL to obtain a proxy-granting ticket from CAS.
   *
   * <P>This callback URL belongs to the Acegi Security System for Spring
   * secured application. We suggest you use CAS'
   * <code>ProxyTicketReceptor</code> servlet to receive this callback and
   * manage the proxy-granting ticket list. The callback URL is usually
   * something like
   * <code>https://www.mycompany.com/application/casProxy/receptor</code>.</p>
   * <P>If left <code>null</code>, the <code>CasAuthenticationToken</code> will
   * not have a proxy granting ticket IOU and there will be no proxy-granting
   * ticket callback. Accordingly, the Acegi Securty System for Spring secured
   * application will be unable to obtain a proxy ticket to call another
   * CAS-secured service on behalf of the user. This is not really an issue for
   * most applications.</p>
   *
   * @param request The http request object used to construct the full proxy
   * callback url.
   *
   * @return the proxy callback URL. This implementation always returns null.
   */
  public String buildProxyCallbackUrl(final HttpServletRequest request) {
    return null;
  }
  /*
  public String buildProxyCallbackUrl(final HttpServletRequest request) {
    String url = null;
    if (proxyCallbackUrl != null && (!"".equals(proxyCallbackUrl))) {
      url = casUrlTransformer.transform(request.getScheme(),
        request.getServerName(), request.getServerPort(),
        request.getContextPath());
      // url = createUrl(createCasBaseUrl(request),
      //proxyCallbackUrl).toString();
      url += proxyCallbackUrl;
    }
    return url;
  }
  */

  /** Builds the web url.
   *
   * @param request The http request object used to construct the full proxy
   * callback url.
   *
   * @return the web URL, or <code>null</code> if not used
   */
  /*
  public String buildWebUrl(final HttpServletRequest request) {

    return validatorUrlTransformer.transform(request.getScheme(),
        request.getServerName(), request.getServerPort(),
        request.getContextPath());
  }
  */

  /** Creates the base url for the service (current web application) from the
   * provided request.
   *
   * @param request The http request object. It cannot be null.
   *
   * @return Returns a url that can be used to build the urls of the service.
   */
  private String createServiceBaseUrl(final HttpServletRequest request) {
    Validate.notNull(request, "The request cannot be null");
    return createUrl(request.getScheme() + "://" + request.getServerName()
      + ":" + request.getServerPort(), request.getContextPath());
  }

  /** Creates a new url based on a base url and a path fragment.
   *
   * @param base The base url. It cannot be null.
   *
   * @param path The path fragment. It cannot be null.
   *
   * @return Returns the new url formed by the concatenation of the base url
   * and the path fragment, including the '/' if necessary.
   */
  private String createUrl(final String base, final String path) {
    Validate.notNull(base, "The base url cannot be null");
    Validate.notNull(path, "The path fragment cannot be null");
    String result = base;
    if (!result.endsWith("/") && !path.startsWith("/")) {
      result += "/";
    }
    result += path;
    return result;
  }
}

