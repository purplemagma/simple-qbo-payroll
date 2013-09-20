package com.purplemagma.qbosimplepayroll;

import java.io.IOException;
import java.util.Properties;

public class Config
{
  private static Properties intuitAnywhereProperties;

  static {
    intuitAnywhereProperties = new Properties();
    try {
      intuitAnywhereProperties.load(Config.class.getResourceAsStream("/app.properties"));
      
      if (System.getenv("QBOV3ServiceUrl") != null) {
	    com.intuit.ipp.util.Config.setProperty(com.intuit.ipp.util.Config.BASE_URL_QBO, System.getenv("QBOV3ServiceUrl"));
      }
      if (System.getenv("IPPPlatformServiceUrl") != null) {
	    com.intuit.ipp.util.Config.setProperty(com.intuit.ipp.util.Config.BASE_URL_PLATFORMSERVICE, System.getenv("IPPPlatformServiceUrl"));
      }
   } catch (IOException e) {
   }
  }

  public static String getProperty(String name) {
    return intuitAnywhereProperties.getProperty(name);
  }
  
  public static String getBaseUrl() {
	  return System.getenv("baseUrl") == null ?
      		"https://"+com.codenvy.AppListener.getProxyHost() :
      		System.getenv("baseUrl");	  
  }
  
  public static String getOAuthConsumerKey() {
	  return System.getenv("oAuthConsumerKey") == null ? Config.getProperty("oauth_consumer_key") :
		  System.getenv("oAuthConsumerKey");
  }
  public static String getOAuthConsumerSecret() {
	  return System.getenv("oAuthConsumerSecret") == null ? Config.getProperty("oauth_consumer_secret") :
		  System.getenv("oAuthConsumerSecret");
  }
  public static String getAppToken() {
	  return System.getenv("appToken") == null ? Config.getProperty("oauth_consumer_key") :
		  System.getenv("appToken");
  }
}