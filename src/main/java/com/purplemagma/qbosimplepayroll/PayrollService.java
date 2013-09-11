package com.purplemagma.qbosimplepayroll;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Error;
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
  
  public Company findOrCreateRealm(String realmId, boolean logIn) throws IOException {
    if (realmId == null) {
      throw new RuntimeException("No realm");
    }
    
    Company company = findRealm(realmId);
    
    if (company == null) {
      company = new Company();
      company.setRealmId(realmId);
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
      this.mapper.save(user, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
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
    request.getSession().setAttribute("intuit_service", null);
    request.getSession().setAttribute("validOAuthConsumer", null);

    if (userId == null) {
      userId = getUserId();
    }
    this.findOrCreateUser(userId, true, firstName, lastName, email);
    Company company = this.findOrCreateRealm(realmId, true);

    if (this.getHasValidOAuthConsumer()) {

      String dataSource = company.getDataSource();
      OAuthAuthorizer authorizer = new OAuthAuthorizer(Config.getProperty("oauth_consumer_key"),
         Config.getProperty("oauth_consumer_secret"),
         company.getoAuthToken(), company.getoAuthTokenSecret());
      
      IntuitService is = new IntuitService(request.getSession(), authorizer, dataSource, realmId);
  
      CompanyInfo ippCompany = null;    
      try {
        ippCompany = is.getCompany();
      } catch (FMSException ex) {
        for (Error error : ex.getErrorList()) {
          if (error.getCode() != null && error.getCode().equals("100")) {
            this.clearoAuth();
            break;
          }
        }
      }

      if (ippCompany != null) {
        company.setBusinessName(ippCompany.getCompanyName());
        this.mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
        request.getSession().setAttribute("intuit_service", is);
      }
    }

    return "/in/start.jspx";  
  }
  
  public IntuitService getIntuitService() {
    return (IntuitService) request.getSession().getAttribute("intuit_service");
  }
  
  public String saveoAuth(String realmId, String dataSource, OAuthConsumer consumer) throws IOException {
    Company company = getCompany();
    company.setoAuthToken(consumer.getToken());
    company.setoAuthTokenSecret(consumer.getTokenSecret());
    // Only support QBO
    company.setDataSource("QBO");
    mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    request.getSession().setAttribute("validOAuthConsumer", consumer);
    
    return login(realmId, null, null, null, null);
  }
  
  public OAuthConsumer getValidOAuthConsumer() throws IOException {
    OAuthConsumer consumer = (OAuthConsumer) request.getSession().getAttribute("validOAuthConsumer");
    if (consumer == null) {
      Company company = getCompany();
      if (company != null && company.getoAuthToken() != null && company.getoAuthTokenSecret() != null) {
        consumer = new DefaultOAuthConsumer(Config.getProperty("oauth_consumer_key"),
          Config.getProperty("oauth_consumer_secret"));
        consumer.setTokenWithSecret(company.getoAuthToken(), company.getoAuthTokenSecret());
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
  
  public void clearoAuth() throws IOException {
    Company company = getCompany();
    company.setoAuthToken(null);
    company.setoAuthTokenSecret(null);
    company.setDataSource(null);
    mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    request.getSession().setAttribute("validOAuthConsumer", null);
  }
}
