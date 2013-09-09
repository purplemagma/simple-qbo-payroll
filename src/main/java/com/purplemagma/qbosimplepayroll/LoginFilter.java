package com.purplemagma.qbosimplepayroll;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LoginFilter implements Filter
{
  public FilterConfig filterConfig;                                 

  public void doFilter(final ServletRequest request,                
                       final ServletResponse response,
                       FilterChain chain)
      throws java.io.IOException, javax.servlet.ServletException {
    HttpServletRequest requestHttp = (HttpServletRequest) request;
    if (requestHttp.getSession().getAttribute("userId") == null) {
      throw new ServletException("Unauthorized");
    }
    chain.doFilter(request,response);                               
  } 

  public void init(final FilterConfig filterConfig) {               
    this.filterConfig = filterConfig;
  } 

  public void destroy() {                                           
  }
}