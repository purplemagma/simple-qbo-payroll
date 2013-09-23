package com.intuit.qbo.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.IAuthorizer;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.security.TicketAuthorizer;
import com.intuit.ipp.services.DataService;

/*
 * This is example code of how you can get oAuth tokens with a ticket
 * And also call v3 services with a ticket
 */
public class InternalIPPManager {
    public final static String AUTHID = "authid";
    public final static String TICKET = "ticket";
    public final static String PARENTID = "parentid";

    // For prod, qbn.
    public final static String COOKIE_PREFIX = "qbn.ptc."; 

    // For prod, its oauth.intuit.com
    public final static String GET_REQUEST_TOKEN_URL = "https://oauthws.e2e.qdc.ep.intuit.net/oauth/v1/get_request_token";
    public final static String ACCESS_TOKEN_ENDPOINT_URL = "https://oauthws.e2e.qdc.ep.intuit.net/oauth/v1/get_access_token";
    
    // For prod, its appcenter.intuit.com
    public final static String AUTOGRANT_URL = "https://appcenter-stage.intuit.com/Account/AutoGrant?oauth_token=";
        
    // // use your app's values
    public final static String APP_TOKEN = "fafc3d53bf9e9b4da8ba462bddc91919c357";
    public final static String CONSUMER_KEY = "qye2eJkmmgFZAmWa1N84NHq7rdrXAQ";
    public final static String CONSUMER_SECRET = "QHiP738SgdtCDucsdId3bfMID1gZ9rkyqRHUJ63O";

    public static BasicClientCookie buildCookie(String name, String value) {
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setDomain(".intuit.com");
        cookie.setPath("/");
        return cookie;
    }
    
    public static Map<String, String> getQueryMap(String query)  
    {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params)  
        {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }  

    public static OAuthConsumer retrieveoAuthToken(String ticket, String realmId, String authId) {
        OAuthProvider provider = new DefaultOAuthProvider(GET_REQUEST_TOKEN_URL, ACCESS_TOKEN_ENDPOINT_URL, "");
        OAuthConsumer consumer = new DefaultOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        
        try {
            provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
            
            // Hydrate browser with cookies and call Workplace Logon
            DefaultHttpClient client = new DefaultHttpClient();
            CookieStore cookies = new BasicCookieStore();
            cookies.addCookie(buildCookie(COOKIE_PREFIX+TICKET, ticket));
            cookies.addCookie(buildCookie(COOKIE_PREFIX+"tkt", ticket));
            cookies.addCookie(buildCookie(COOKIE_PREFIX+PARENTID,realmId));
            cookies.addCookie(buildCookie(COOKIE_PREFIX+"agentid",authId));
            cookies.addCookie(buildCookie(COOKIE_PREFIX+"gauthid",authId));
            cookies.addCookie(buildCookie(COOKIE_PREFIX+AUTHID, authId));
            client.setCookieStore(cookies);

            HttpContext context = new BasicHttpContext();
            context.setAttribute(ClientContext.COOKIE_STORE, cookies);            
            HttpGet httpGet = new HttpGet(AUTOGRANT_URL + consumer.getToken());
            HttpResponse response = client.execute(httpGet, context);
                        
            HttpEntity httpEntity = response.getEntity();
            InputStream stream = httpEntity.getContent();
            byte[] bytes = new byte[2048];
            StringBuilder builder = new StringBuilder();
            int len = stream.read(bytes, 0, 2048);
            while (len > 0) {
                builder.append(new String(bytes));
                len = stream.read(bytes, 0, 2048);
            }

            JSONObject responseObject = new JSONObject(builder.toString());
            String verifier = responseObject.getString("verifier");

			if (verifier != null && verifier.length() == 7) {
                provider.retrieveAccessToken(consumer, verifier);
                
                return consumer;
            }
        } catch (JSONException exception) {
        } catch (OAuthMessageSignerException oAuthMessageSignerException) {
        } catch (OAuthNotAuthorizedException oAuthNotAuthorizedException) {
        } catch (OAuthExpectationFailedException oAuthExpectationFailedException) {
        } catch (OAuthCommunicationException oAuthCommunicationException) {
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        }
        
        return null;
    }
    
    public static Map<String, String> getCookieAuth(HttpServletRequest request) {
    	Map<String, String> result = new HashMap<String,String>();
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(COOKIE_PREFIX+TICKET)) {
                result.put(TICKET, cookie.getValue());
            }
            if (cookie.getName().equals(COOKIE_PREFIX+AUTHID)) {
            	result.put(AUTHID,cookie.getValue());
            }
            if (cookie.getName().equals(COOKIE_PREFIX+PARENTID)) {
            	result.put(PARENTID,cookie.getValue());
            }
        }
        
        return result;
    }
    
    /*
     * Auto grant an oAuth token
     */
    public static OAuthConsumer retrieveoAuthToken(HttpServletRequest request) {
    	Map<String, String> cookieAuth = getCookieAuth(request);
        return retrieveoAuthToken(cookieAuth.get(TICKET), cookieAuth.get(PARENTID), cookieAuth.get(AUTHID));
    }
    
    /*
     * Example of calling a v3 service API with just a ticket and IPP dev kit
     * 
     * To run against a particular pre-prod instance,
     * Set environment variable IPP_QBO_BASE_URL=https://qa.qbo.intuit.com/qbo2/v3/company
     * Otherwise, it will hit production
     * You can also override this in intuit-config.xml
     */
    public static CompanyInfo getCompanyInfoWithTicket(HttpServletRequest request) throws FMSException {
    	Map<String, String> cookieAuth = getCookieAuth(request);
    	IAuthorizer authorizer = new TicketAuthorizer(cookieAuth.get(TICKET), APP_TOKEN);
    	Context context = new Context(authorizer,ServiceType.QBO,cookieAuth.get(PARENTID));
    	DataService service = new DataService(context);
        CompanyInfo filter = new CompanyInfo();
        filter.setId(cookieAuth.get(PARENTID));
        CompanyInfo company = service.findById(filter);
        
        return company;
    }
    
    /*
     * Example of calling a v3 service API with oAuth
     */
    public static CompanyInfo getCompanyInfoWithoAuth(OAuthConsumer consumer, String realmId) throws FMSException {
    	IAuthorizer authorizer = new OAuthAuthorizer(consumer.getConsumerKey(), consumer.getConsumerSecret(), consumer.getToken(), consumer.getTokenSecret());
    	Context context = new Context(authorizer,ServiceType.QBO,realmId);
    	DataService service = new DataService(context);
        CompanyInfo filter = new CompanyInfo();
        filter.setId(realmId);
        CompanyInfo company = service.findById(filter);
        
        return company;    	
    }
    
}
