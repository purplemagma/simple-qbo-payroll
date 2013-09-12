package com.purplemagma.qbosimplepayroll;

import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig
{
   public Application() {
        packages("com.purplemagma.qbosimplepayroll");
        register(MoxyJsonFeature.class);
        register(JsonMoxyConfigurationContextResolver.class);
   }
}