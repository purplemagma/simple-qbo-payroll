package com.intuit.qbo.plugin;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.FetchRequest;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("auth")
public class Authentication
{
  @Context
  private HttpServletRequest request;
  
  private static ConsumerManager manager = new ConsumerManager();
  static {
    manager.setAssociations(new InMemoryConsumerAssociationStore());
		manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
		manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);    
  }
  
  private static String openIdReturnPath = "/rest/auth/openid/verify";
  private static String oAuthReturnPath = "/rest/auth/oauth/verify";

  private static String openIdProviderUrl;
  private static String baseAppUrl;
  private static String oAuthAuthorizationUrl;
  private static String oAuthRequestTokenUrl;
  private static String oAuthAccessTokenUrl;
  private static String oAuthConsumerKey;
  private static String oAuthConsumerSecret;
  private static String pluginClassName;
  
  /*
   * Allows user to configure urls
   */
  public static void Configure(
      String baseAppUrl,
      String openIdProviderUrl, 
      String oAuthAuthorizationUrl,
      String oAuthRequestTokenUrl,
      String oAuthAccessTokenUrl,
      String oAuthConsumerKey,
      String oAuthConsumerSecret,
      String pluginClassName) {
    Authentication.baseAppUrl = baseAppUrl;
    Authentication.openIdProviderUrl = openIdProviderUrl;
    Authentication.oAuthAuthorizationUrl = oAuthAuthorizationUrl;
    Authentication.oAuthRequestTokenUrl = oAuthRequestTokenUrl;
    Authentication.oAuthAccessTokenUrl = oAuthAccessTokenUrl;
    Authentication.oAuthConsumerKey = oAuthConsumerKey;
    Authentication.oAuthConsumerSecret = oAuthConsumerSecret;
    Authentication.pluginClassName = pluginClassName;
  }
  
  public static String getoAuthConsumerKey() {
    return oAuthConsumerKey;
  }

  public static String getoAuthConsumerSecret() {
    return oAuthConsumerSecret;
  }
  
  /*
   * Dynamically constructs a QBO plugin instance using the cass name you passed into configure
   */
  private QBOPlugin getPluginInstance() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    QBOPlugin plugin = (QBOPlugin) Class.forName(pluginClassName).newInstance();
    plugin.setSession(request.getSession());
    return plugin;
  }  

  /*
   *  Authentication always starts here
   *  Definitions:
   *    RealmId - unique Intuit/QBO id for a company
   *    Logged in realmId - authenticated company id in the session
   *    Database realmId - company profile record in database for this realmId
   * 
   */
  @Path("openid/initialize")
  @GET
  public Response initialize() {
    try {
      // Attempt to get unauthenticatedRealmId from request parameter or referrer
      String unauthenticatedRealmId = null;
      try {
        unauthenticatedRealmId = getRealmIdFromRequest();
      } catch (Exception ex) {
      }
      URI redirectUri = null;
      QBOPlugin plugin = getPluginInstance();
      
      // If unauthenticated realmId matches logged in session realmId, the return value will be
      // the url of the main application page (user is already logged into the right realm)
      if (plugin.isRealmLoggedIn(unauthenticatedRealmId)) {
        redirectUri = new URI(plugin.getStartPage());
      } else {
        // If the realm exists in the database or the user is signing up, 
        // we need to start open id verification so we can log this user in
        if (unauthenticatedRealmId == null || 
           plugin.doesRealmExist(unauthenticatedRealmId) ||
           request.getParameter("isSigningUp") != null) {
           
          // Use openid4java to build an openid.intuit.com redirection
          // Instruct open id to redirect back to openid/verify
          DiscoveryInformation discovered = new DiscoveryInformation(new URL(openIdProviderUrl));
          List<DiscoveryInformation> discoveries = new ArrayList<DiscoveryInformation>();
          discoveries.add(discovered);
          manager.associate(discoveries);
          String openIdReturnUrl = baseAppUrl+openIdReturnPath;
          if (request.getParameter("isSigningUp") != null) {
            openIdReturnUrl += "?isSigningUp=true";
          }
          AuthRequest authRequest = manager.authenticate(discovered, openIdReturnUrl);
          FetchRequest fetch = FetchRequest.createFetchRequest();
          fetch.addAttribute("FirstName", "http://openid.net/schema/namePerson/first", true);
    			fetch.addAttribute("LastName", "http://openid.net/schema/namePerson/last", true);
    			fetch.addAttribute("Email", "http://openid.net/schema/contact/email", true);
    			fetch.addAttribute("RealmId", "http://openid.net/schema/intuit/realmId", true);
          authRequest.addExtension(fetch);
          redirectUri = new URI(authRequest.getDestinationUrl(true));
        // If the realm id doesn't exist in the database, we want to show a static marketing page
        } else {
          redirectUri = new URI(plugin.getSignupUrl(unauthenticatedRealmId));
        }
      }

      return Response.temporaryRedirect(redirectUri).build();
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();
    }
  }
  
  /*
   * Using the request instance property, use three different possibilities
   * to figure out realmId:
   *   Referrer header, realmid is a parameter
   *   RealmId as parameter on request
   *   RealmId coming across in openId request parameter
   */
  private String getRealmIdFromRequest() {
    String realmId = request.getHeader("Referer");
    realmId = realmId.replaceAll("%3D", "=");
    if (realmId != null && realmId.length() > 8) {
      int index = realmId.indexOf("realmId=");
      if (index >= 0) {
        realmId = realmId.substring(index+8);
        index = realmId.indexOf("&");
        if (index > 0) {
          realmId = realmId.substring(0, index);
        }
        return realmId;
      }
    }
    
    realmId = request.getParameter("realmId");
    if (realmId != null && realmId.length() > 0) {
      return realmId;
    }
            
    realmId = request.getParameter("openid.alias3.value.alias4");
    
    if (realmId != null && realmId.length() > 0 && !realmId.equals("1")) {
      return realmId;
    }

    throw new RuntimeException("No realm");
  }
  
  /*
   * After openid.intuit.com validates the user from Intuit's perspective, 
   * Browser is redirected back to openid/verify
   */
  @Path("openid/verify")
  @GET
  public Response verify() {
    try {
      // Use openId4Java to validate openId
      ParameterList openidResp = new ParameterList(request.getParameterMap());
      String requestUrl = baseAppUrl+openIdReturnPath+"?"+request.getQueryString();
      VerificationResult result = manager.verify(requestUrl, openidResp, null);
      Identifier verified = result.getVerifiedId();
      
      if (verified == null) {
        throw new RuntimeException("Unable to verify open id");
      }
      
      QBOPlugin plugin = getPluginInstance();
      String realmId = getRealmIdFromRequest();
      
      // Login will create a company using the authenticated realm id if one doesn't exist
      // And log the user into this realm (service.is
      plugin.login(realmId, verified.getIdentifier(),
        request.getParameter("openid.alias3.value.alias1"),
        request.getParameter("openid.alias3.value.alias2"),
        request.getParameter("openid.alias3.value.alias3"));
            
      // If the company has a valid oAuth consumer (one with tokens)
      // Go to the start page of the app
      if (plugin.getHasValidOAuthConsumer()) {
        return Response.temporaryRedirect(new URI(plugin.getStartPage())).build();
      } else {
      // Otherwise, we need to get an oAuth token
        return this.requestToken(realmId);
      }
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }
  }
  
  /*
   * Use the signpost library to build a request token url to the Intuit oAuth server and 
   * redirect the browser to it
   */
  @Path("oauth/request")
  @GET
	public Response requestToken(@QueryParam("realmid") String realmId) {
    try {
      String completeAuthorizationUrl = oAuthAuthorizationUrl+"?realmId="+realmId;

      OAuthConsumer consumer = new DefaultOAuthConsumer(oAuthConsumerKey, oAuthConsumerSecret);
      OAuthProvider provider = new DefaultOAuthProvider(oAuthRequestTokenUrl, oAuthAccessTokenUrl, completeAuthorizationUrl);
   
      String authUrl = provider.retrieveRequestToken(consumer, baseAppUrl+oAuthReturnPath);
      
      request.getSession().setAttribute("temp_oauth_consumer", consumer);
      request.getSession().setAttribute("temp_oauth_provider", provider);
      
      return Response.temporaryRedirect(new URI(authUrl)).build();      
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }
	}

  /*
   * The oAuth flow will wind up here after it gets back from Intuit's oAuth server.
   * verify the token and redirect to the start page
   */
  @Path("oauth/verify")
  @GET	
	public Response getAccessToken(@QueryParam("oauth_verifier") String verifier, @QueryParam("oauth_token") String token) {
    try {
      OAuthConsumer consumer = (OAuthConsumer) request.getSession().getAttribute("temp_oauth_consumer");
      OAuthProvider provider = (OAuthProvider) request.getSession().getAttribute("temp_oauth_provider");
      request.getSession().removeAttribute("temp_oauth_consumer");
      request.getSession().removeAttribute("temp_oauth_provider");
      
      provider.retrieveAccessToken(consumer, verifier);
      
      QBOPlugin plugin = getPluginInstance();
      String realmId = getRealmIdFromRequest();
      String dataSource = request.getParameter("dataSource");
      plugin.saveoAuth(dataSource, consumer);
      plugin.login(realmId, null, null, null, null);
            
      return Response.temporaryRedirect(new URI(plugin.getStartPage())).build();
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }    
	}
 
  /*
   * Logout
   */
   @Path("logout")
   @GET
   public Response logout() {
     request.getSession().invalidate();
     return Response.serverError().entity("You are logged out.").build();      
   }
}