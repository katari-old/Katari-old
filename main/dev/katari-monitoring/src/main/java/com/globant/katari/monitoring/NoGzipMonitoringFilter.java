/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.monitoring;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bull.javamelody.MonitoringFilter;

/** Wrapps a MonitoringFilter so that it does never compress the generated
 * output.
 *
 * This is needed because sitemesh cannot decorate compressed pages.
 *
 * Also, made the monitoring url independent of the context path. You can now
 * rewrite the context path in your application, and the filter will still show
 * the statistics in the monitoring url.
 */
public final class NoGzipMonitoringFilter implements Filter {

  /** The serialization version number.
   *
   * This number must change every time a new serialization incompatible change
   * is introduced in the class.
   */
  private static final long serialVersionUID = 1;

  /** The class logger.
   */
  private static Logger log = LoggerFactory.getLogger(
      NoGzipMonitoringFilter.class);

  /** Hack to be able to call getMonitoringUrl on MonitoringFilter.
   */
  private static class MonitoringFilter2 extends MonitoringFilter {
    /** {inheritDoc}
     */
    public String getMonitoringUrl2(HttpServletRequest request) {
      return super.getMonitoringUrl(request);
    }
  }

  /** The wrapped monitoring filter.
   */
  private MonitoringFilter2 monitoring = new MonitoringFilter2();

  /** {@inheritDoc}
   */
  public void init(final FilterConfig filterConfig) throws ServletException {
    log.trace("Entering init");
    monitoring.init(filterConfig);
    log.trace("Leaving init");
  }

  /** {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void doFilter(final ServletRequest request, final ServletResponse
      response, final FilterChain filterChain) throws ServletException,
         IOException {

    log.trace("Entering doFilter");

    if (!(request instanceof HttpServletRequest)) {
      throw new RuntimeException("Calling doFilter on an non http request");
    }

    HttpServletRequest httpRequest = (HttpServletRequest) request;

    final String monitoringUrl = monitoring.getMonitoringUrl2(httpRequest);
    HttpServletRequestWrapper w = new HttpServletRequestWrapper(httpRequest) {

      /** {inheritDoc}
       */
      @Override
      public Enumeration getHeaders(final String name) {
        if (name.equals("Accept-Encoding")) {
          return new Vector().elements();
        }
        return super.getHeaders(name);
      }

      /** {inheritDoc}
       */
      @Override
      public String getRequestURI() {
        String requestUri = super.getRequestURI();
        if (requestUri.matches(".*/katari-monitoring$")) {
          return getContextPath() + monitoringUrl;
        } else {
          return requestUri;
        }
      }

      /** {inheritDoc}
       */
      @Override
      public String getContextPath() {
        String contextPath = super.getContextPath();
        String requestUri = super.getRequestURI();
        if (requestUri.matches(".*/katari-monitoring$")) {
          return "";
        } else {
          return contextPath;
        }
      }
    };

    monitoring.doFilter(w, response, filterChain);

    log.trace("Leaving doFilter");
  }

  /** {@inheritDoc}
   */
  public void destroy() {
    log.trace("Entering destroy");
    monitoring.destroy();
    log.trace("Leaving destroy");
  }
}

