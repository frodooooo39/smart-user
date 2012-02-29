/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smartitengineering.user.security.filters;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author modhu7
 */
public class RedirectionFilter implements Filter {

  private static final String[] DEFAULT_BROWSERS = {"Chrome", "Firefox", "Safari", "Opera", "MSIE"};
  public static final String KEY_BROWSER_IDS = "browserIds";
  public static final String LOGIN_FORM_POST_URL = "/j_spring_security_check";
  public static final String REDIRECTOR_URL = "/";
  public static final String LOGIN_ERROR_PARAM_NAME = "login_error";
  public static final String REDIRECTION_URL_PARAM_NAME = "rurl";
  private static final Pattern JSESSIONID_PATTERN = Pattern.compile("(.*)(;jsessionid=[a-z0-9A-Z]+)(\\?.*)?");
  private Logger logger = LoggerFactory.getLogger(RedirectionFilter.class);
  // Configured params
  private String[] browserIds;
  private String loginUri;
  private static URI loginRedirectUri;
  private static String loginRedirectUrl;

  @Override
  public void init(FilterConfig fc) throws ServletException {

    loginUri = fc.getInitParameter("loginUrl");
    if (loginUri == null) {
      throw new IllegalArgumentException("RedirecitonFilter requires param redirectionUrl");
    }
    String ids = fc.getInitParameter(KEY_BROWSER_IDS);
    this.browserIds = (ids != null) ? ids.split(",") : DEFAULT_BROWSERS;

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc) throws IOException,
                                                                                                ServletException {
    final HttpServletResponse httpResponse = (HttpServletResponse) response;
    final HttpServletRequest httpRequest = (HttpServletRequest) request;

    final String contextPath = httpRequest.getContextPath();
    if (loginRedirectUri == null) {
      synchronized (this) {
        if (loginRedirectUri == null) {
          loginRedirectUrl = new StringBuilder("http://").append(httpRequest.getHeader(HttpHeaders.HOST)).
              append(StringUtils.isBlank(contextPath) ? "/" : contextPath).append(contextPath.endsWith("/") && loginUri.
              startsWith("/") ? loginUri.substring(1) : loginUri).toString();
          loginRedirectUri = URI.create(loginRedirectUrl);
        }
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info("login url " + loginUri);
      logger.info("Context path " + contextPath);
      logger.info("login url to check for " + loginRedirectUrl);
      String requestUrl = getRequestUrl(httpRequest);
      logger.info("Request url is " + requestUrl);
      logger.info("User Agent " + httpRequest.getHeader(HttpHeaders.USER_AGENT));
    }
    if (!isUserAgentBrowser(httpRequest.getHeader(HttpHeaders.USER_AGENT))) {
      final HttpServletResponseWrapper wrapper;
      wrapper = new HttpServletResponseWrapper(httpResponse) {

        protected final transient Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void sendRedirect(final String location) throws IOException {
          final URL locationUri;
          if (location.contains(";jsessionid")) {
            Matcher matcher = JSESSIONID_PATTERN.matcher(location);
            if (matcher.matches()) {
              locationUri =
              new URL(new StringBuilder(location).replace(matcher.start(2), matcher.end(2), "").toString());
            }
            else {
              locationUri = new URL(location);
            }
          }
          else {
            locationUri = new URL(location);
          }
          if (logger.isInfoEnabled()) {
            logger.info("Locatin to redirect to " + location + " " + locationUri.getPath());
          }
          if (location.startsWith(loginRedirectUrl) && locationUri.getPath().equals(loginRedirectUri.getPath())) {
            logger.info("Redirected to login page thus sending UNAUTHORIZED - 401");

            setStatus(Status.UNAUTHORIZED.getStatusCode());
          }
          else {
            logger.info("Redirecting to the URI requested");
            super.sendRedirect(location);
          }
        }
      };
      fc.doFilter(request, wrapper);
    }
    else {
      fc.doFilter(request, response);
    }
  }

  @Override
  public void destroy() {
  }

  private boolean isUserAgentBrowser(String userAgent) {
    for (String browserId : browserIds) {
//      if (logger.isInfoEnabled()) {
//        logger.info("Browser " + browserId + " ------ User agent" + userAgent);
//      }
      if (userAgent.contains(browserId)) {
        logger.info("Browser " + browserId + " ------ User agent" + userAgent);
        return true;
      }
    }
    return false;
  }

  private String getRequestUrl(HttpServletRequest request) {
    String result = request.getRequestURI();
    if (result == null) {
      result = request.getServletPath();
    }
    if ((result == null) || (result.equals(""))) {
      result = "/";
    }
    return result;
  }
}
