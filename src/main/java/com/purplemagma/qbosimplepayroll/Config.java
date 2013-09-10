package com.purplemagma.qbosimplepayroll;

import java.io.IOException;
import java.util.Properties;

public class Config
{
  private static Properties intuitAnywhereProperties;

  static {
    intuitAnywhereProperties = new Properties();
    try {
      intuitAnywhereProperties.load(Config.class.getResourceAsStream("/ia.properties"));
   } catch (IOException e) {
   }
  }

  public static String getProperty(String name) {
    return intuitAnywhereProperties.getProperty(name);
  }
  
  public String getGrantUrl() {
    return Config.getProperty("oauth_request_url");
  }
  
  public String getMenuProxy() {
    return Config.getProperty("app_url");    
  }
  
  public String getIntuitAnywhereJsUrl() {
    return Config.getProperty("ia_js_url");
  }
}