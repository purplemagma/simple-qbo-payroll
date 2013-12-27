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
    } catch (IOException e) {
    }
  }

  public static String getProperty(String name) {
    return intuitAnywhereProperties.getProperty(name);
  }

  public static String getBaseUrl() {
	  String baseUrl = System.getProperty("baseUrl") == null ?
              Config.getProperty("base_url") : System.getProperty("baseUrl");

      return baseUrl == null ?
      		"https://"+com.codenvy.AppListener.getProxyHost() : baseUrl;
  }

  public static String getOAuthConsumerKey() {
	  return System.getProperty("oAuthConsumerKey") == null ? Config.getProperty("oauth_consumer_key") :
		  System.getProperty("oAuthConsumerKey");
  }
  public static String getOAuthConsumerSecret() {
	  return System.getProperty("oAuthConsumerSecret") == null ? Config.getProperty("oauth_consumer_secret") :
		  System.getProperty("oAuthConsumerSecret");
  }
  public static String getAppToken() {
	  return System.getProperty("appToken") == null ? Config.getProperty("oauth_consumer_key") :
		  System.getProperty("appToken");
  }
}