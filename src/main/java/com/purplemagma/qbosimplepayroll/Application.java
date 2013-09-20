package com.purplemagma.qbosimplepayroll;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.intuit.qbo.plugin.Authentication;

public class Application extends ResourceConfig
{
   public Application() {
        packages("com.purplemagma.qbosimplepayroll","com.intuit.qbo.plugin");
        register(MoxyJsonFeature.class);
        register(JsonMoxyConfigurationContextResolver.class);
        
        Authentication.Configure(
          Config.getBaseUrl(), 
          Config.getProperty("openid_provider_url"),
          Config.getProperty("authorize_url"),
          Config.getProperty("request_token_url"), 
          Config.getProperty("access_token_url"),
          Config.getOAuthConsumerKey(),
          Config.getOAuthConsumerSecret(),
          "com.purplemagma.qbosimplepayroll.QBOSimplePayroll");
   }
}