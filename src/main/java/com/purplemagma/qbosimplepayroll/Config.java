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
}