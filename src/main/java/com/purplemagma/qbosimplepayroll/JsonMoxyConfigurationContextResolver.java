package com.purplemagma.qbosimplepayroll;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {
 
    private final MoxyJsonConfig config;
 
    public JsonMoxyConfigurationContextResolver() {
        final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
 
        config = new MoxyJsonConfig()
            .setNamespacePrefixMapper(namespacePrefixMapper)
            .setNamespaceSeparator(':');
    }
 
    @Override
    public MoxyJsonConfig getContext(Class<?> objectType) {
        return config;
    }
} 