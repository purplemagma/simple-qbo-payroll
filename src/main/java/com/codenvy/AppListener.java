package com.codenvy;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.amazonaws.util.json.JSONObject;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppListener implements ServletContextListener
{
  private final static String VCAP_APPLICATION = "VCAP_APPLICATION";
  private final static String PROXY_UPDATE_URL = ".codenvy.purplemagma.com/update_url/";
    
  public static void updateProxyUrl(String appName) {
    try {      
      String appInstance = System.getenv(VCAP_APPLICATION);
      JSONObject appInstanceObj = new JSONObject(appInstance);
      String appUrl = appInstanceObj.getJSONArray("uris").getString(0);
      HttpClient httpClient = new DefaultHttpClient();
      HttpGet httpGet = new HttpGet("http://"+appName+PROXY_UPDATE_URL+appUrl);
      httpClient.execute(httpGet);
    } catch (Exception ex) {
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    String prefix = event.getServletContext().getInitParameter("reverseProxyPrefix");
    AppListener.updateProxyUrl(prefix);
  }
  
  public void contextDestroyed(ServletContextEvent event) {
  }
}
