package com.purplemagma.qbosimplepayroll;

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

import com.purplemagma.qbosimplepayroll.Config;

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
  
  @Path("openid/initialize")
  @GET
  public Response initialize() {
    try {
      String realmId = null;
      try {
        realmId = getRealmIdFromRequest();
      } catch (Exception ex) {
      }
      PayrollService service = new PayrollService(request);
      URI uri = null;
      
      String homePage = service.realmLoggedIn(realmId);
      if (homePage == null) {
        if (realmId == null || service.realmExists(realmId) || request.getParameter("noRedirect") != null) {
          String intuitOpenIdUrl = Config.getProperty("openid_provider_url");
          DiscoveryInformation discovered = new DiscoveryInformation(new URL(intuitOpenIdUrl));
          List<DiscoveryInformation> discoveries = new ArrayList<DiscoveryInformation>();
          discoveries.add(discovered);
          manager.associate(discoveries);
          String oAuthReturnUrl = Config.getProperty("openid_return_url");
          if (request.getParameter("noRedirect") != null) {
            oAuthReturnUrl += "?noRedirect=true";
          }
          AuthRequest authRequest = manager.authenticate(discovered, oAuthReturnUrl);
          FetchRequest fetch = FetchRequest.createFetchRequest();
          fetch.addAttribute("FirstName", "http://openid.net/schema/namePerson/first", true);
    			fetch.addAttribute("LastName", "http://openid.net/schema/namePerson/last", true);
    			fetch.addAttribute("Email", "http://openid.net/schema/contact/email", true);
    			fetch.addAttribute("RealmId", "http://openid.net/schema/intuit/realmId", true);
          authRequest.addExtension(fetch);
          uri = new URI(authRequest.getDestinationUrl(true));        
        } else {
          uri = new URI("/splash.jspx?realmId="+realmId);
        }
      } else {
        uri = new URI(homePage);
      }

      return Response.temporaryRedirect(uri).build();
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();
    }
  }
  
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
  
  @Path("openid/verify")
  @GET
  public Response verify() {
    try {
      ParameterList openidResp = new ParameterList(request.getParameterMap());
      String requestUrl = Config.getProperty("openid_return_url")+"?"+request.getQueryString();
      VerificationResult result = manager.verify(requestUrl, openidResp, null);
      Identifier verified = result.getVerifiedId();
      
      if (verified == null) {
        throw new RuntimeException("Unable to verify open id");
      }
      
      PayrollService payroll = new PayrollService(request);
      String realmId = getRealmIdFromRequest();
      
      if (payroll.realmExists(realmId) || openidResp.hasParameter("noRedirect")) {
        String redirectUrl = payroll.login(realmId, verified.getIdentifier(),
          request.getParameter("openid.alias3.value.alias1"),
          request.getParameter("openid.alias3.value.alias2"),
          request.getParameter("openid.alias3.value.alias3"));
              
        if (payroll.getHasValidOAuthConsumer()) {
          return Response.temporaryRedirect(new URI(redirectUrl)).build();
        } else {
          return this.requestToken();
        }
      } else {
        return Response.temporaryRedirect(new URI("/splash.jspx?realmId="+realmId)).build();
      }
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }
  }
  
  @Path("oauth/request")
  @GET
	public Response requestToken() {
    try {
      String requestUrl = Config.getProperty("request_token_url");
      String authorizeUrl = Config.getProperty("authorize_url");
      OAuthProvider provider = new DefaultOAuthProvider(requestUrl, Config.getProperty("access_token_url"),authorizeUrl);

      OAuthConsumer consumer = new DefaultOAuthConsumer(Config.getProperty("oauth_consumer_key"), Config.getProperty("oauth_consumer_secret"));
   
      String authUrl = provider.retrieveRequestToken(consumer,Config.getProperty("oauth_callback_url"));
      
      request.getSession().setAttribute("oauth_consumer", consumer);
      request.getSession().setAttribute("oauth_provider", provider);
      
      return Response.temporaryRedirect(new URI(authUrl)).build();      
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }
	}

  @Path("oauth/verify")
  @GET	
	public Response getAccessToken(@QueryParam("oauth_verifier") String verifier, @QueryParam("oauth_token") String token) {
    try {
      OAuthProvider provider = (OAuthProvider) request.getSession().getAttribute("oauth_provider");
      OAuthConsumer consumer = (OAuthConsumer) request.getSession().getAttribute("oauth_consumer");
      
      provider.retrieveAccessToken(consumer, verifier);
      
      PayrollService payroll = new PayrollService(request);
      String realmId = getRealmIdFromRequest();
      String dataSource = request.getParameter("dataSource");
      String redirectUrl = payroll.saveoAuth(realmId, dataSource, consumer);
            
      return Response.temporaryRedirect(new URI(redirectUrl)).build();
    } catch (Exception ex) {
      return Response.serverError().entity(ex.toString()).build();      
    }    
	}  
}