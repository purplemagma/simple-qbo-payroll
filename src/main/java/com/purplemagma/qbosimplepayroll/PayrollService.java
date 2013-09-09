package com.purplemagma.qbosimplepayroll;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.purplemagma.qbosimplepayroll.entities.Company;
import com.purplemagma.qbosimplepayroll.entities.User;

public class PayrollService
{
  @Context
  private HttpServletRequest request;

  AmazonDynamoDBClient client;
  DynamoDBMapper mapper;
  
  public PayrollService() throws IOException {
    init();
  }
  
  public PayrollService(HttpServletRequest request) throws IOException {
    this.request = request;
    init();
  }
  
  public void init() throws IOException {
    client = Database.createInstance().getClient();
    mapper = new DynamoDBMapper(client);    
  }
  
  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public Company findRealm(String realmId) throws IOException {
    if (realmId == null) {
      return null;
    }
    return mapper.load(Company.class, realmId, new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
  }
  
  public Company findOrCreateRealm(String realmId, boolean logIn, String businessName) throws IOException {
    if (realmId == null) {
      throw new RuntimeException("No realm");
    }
    
    Company company = findRealm(realmId);
    
    if (company == null) {
      company = new Company();
      company.setRealmId(realmId);
      company.setBusinessName(businessName);
      mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    }
    
    // Log the realm in
    if (logIn) {
      request.getSession().setAttribute("realmId", realmId);
    }

    return company;
  }
  
  public Company getCompany() throws IOException {
    return findRealm(getRealmId());
  }
  
  public String getRealmId() {
    return  (String) request.getSession().getAttribute("realmId");
  }
  
  public User findUser(String userId) throws IOException {
    if (userId == null) {
      return null;
    }
    
    return mapper.load(User.class, userId,  new DynamoDBMapperConfig(DynamoDBMapperConfig.ConsistentReads.CONSISTENT));
  }
  
  public User findOrCreateUser(String userId, boolean logIn, String firstName, String lastName, String email) throws IOException {
    User user = findUser(userId);
    
    if (user == null) {
      user = new User();
      user.setUserId(userId);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setEmail(email);
      mapper.save(user, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    }
    
    if (logIn) {
      request.getSession().setAttribute("userId", userId);
    }
    
    return user;
  }
  
  public User getUser() throws IOException {
    return findUser(getUserId());
  }
  
  public String getUserId() {
    return (String) request.getSession().getAttribute("userId");
  }
  
  public String login(String realmId, String userId, String firstName, String lastName, String email) throws IOException {
    if (userId == null) {
      userId = getUserId();
    }
    User user = this.findOrCreateUser(userId, true, firstName, lastName, email);
    OAuthAuthorizer authorizer = null;
    String dataSource = user.getDataSource();
    if (user.getoAuthToken() != null && user.getoAuthTokenSecret() != null) {
      authorizer = new OAuthAuthorizer(Config.getProperty("oauth_consumer_key"),
       Config.getProperty("oauth_consumer_secret"),
       user.getoAuthToken(), user.getoAuthTokenSecret());
    }
    
    IntuitService is = new IntuitService(request.getSession(), authorizer, dataSource, realmId);
    com.intuit.ipp.data.Company ippCompany = null;
    
    try {
      ippCompany = is.getIPPCompany();
    } catch (FMSException ex) {       
    }
    
    if (ippCompany != null) {
      this.findOrCreateRealm(realmId, true, ippCompany.getCompanyName());
    }

    return "/in/start.jspx";  
  }
  
  public String saveoAuth(String realmId, String dataSource, OAuthConsumer consumer) throws IOException {
    User user = getUser();
    user.setoAuthToken(consumer.getToken());
    user.setoAuthTokenSecret(consumer.getTokenSecret());
    user.setDataSource(dataSource);
    mapper.save(user);
    
    return login(realmId, user.getUserId(), null, null, null);
  }
  
  public OAuthConsumer getValidOAuthConsumer() throws IOException {
    OAuthConsumer consumer = (OAuthConsumer) request.getSession().getAttribute("validOAuthConsumer");
    if (consumer == null) {
      User user = getUser();
      if (user != null && user.getoAuthToken() != null && user.getoAuthTokenSecret() != null) {
        consumer = new DefaultOAuthConsumer(Config.getProperty("oauth_consumer_key"),
          Config.getProperty("oauth_consumer_secret"));
        consumer.setTokenWithSecret(user.getoAuthToken(), user.getoAuthTokenSecret());
        request.getSession().setAttribute("validOAuthConsumer", consumer);
      }
    }
    
    return consumer;
  }
  
  public boolean getHasValidOAuthConsumer() {
    OAuthConsumer consumer = null;
    try {
      consumer = this.getValidOAuthConsumer();
    } catch (IOException ex) {
    }
    
    return consumer != null;
  }
}
