package com.codenvy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppListener implements ServletContextListener
{
  private final static String VCAP_APPLICATION = "VCAP_APPLICATION";
  private final static String PROXY_BASE_POSTFIX = ".codenvy.purplemagma.com";
  private final static String PROXY_UPDATE_PATH = "/update_url/";
  
  private static String codeEnvyProxyPrefix;
  private static String defaultAppRunUrl;
  private static String defaultProxyPrefix;
  
  /*
   * Check for the cloud foundry app run url in system environment properties - only works on codenvy
   */
  public static String getCodenvyAppRunUrl() {
      String appInstance = System.getenv(VCAP_APPLICATION);
      String appUrl = null;
      
      if (appInstance != null) {
        try {
          JSONObject appInstanceObj = new JSONObject(appInstance);
          appUrl = appInstanceObj.getJSONArray("uris").getString(0);
        } catch (JSONException ex) {
          
        }
      }
      
      return appUrl;
    
  }
  
  /*
   * Returns true if running inside codenvy
   */
  public static boolean isRunningInCodenvy() {
    return System.getenv(VCAP_APPLICATION) != null;
  }
  
  /*
   * Gets the url of where the app is running. In codenvy, read it from properties
   * In Amazon, it is specified in web.xml
   */
  public static String getAppRunUrl() {
    return isRunningInCodenvy() ? getCodenvyAppRunUrl() : defaultAppRunUrl;
  }
  
  /*
   * Get the url of the proxy server (e.g. aws.payroll.purplemagma.com)
   */
  public static String getProxyHost() {
    String prefix = isRunningInCodenvy() ? codeEnvyProxyPrefix : defaultProxyPrefix;
    return prefix+PROXY_BASE_POSTFIX;
  }
  
  public static void updateProxyUrl() {
    try {      
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet("http://"+getProxyHost()+PROXY_UPDATE_PATH+getAppRunUrl());
      httpClient.execute(httpGet);
    } catch (Exception ex) {
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    defaultAppRunUrl = event.getServletContext().getInitParameter("defaultAppRunUrl");
    codeEnvyProxyPrefix = event.getServletContext().getInitParameter("codEnvyProxyPrefix");
    defaultProxyPrefix = event.getServletContext().getInitParameter("defaultProxyPrefix");
    
    AppListener.updateProxyUrl();
  }
  
  public void contextDestroyed(ServletContextEvent event) {
  }
}
