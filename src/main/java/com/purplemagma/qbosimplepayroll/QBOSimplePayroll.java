package com.purplemagma.qbosimplepayroll;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.qbo.plugin.Authentication;
import com.intuit.qbo.plugin.QBOPlugin;
import com.purplemagma.qbosimplepayroll.entities.Company;
import com.purplemagma.qbosimplepayroll.entities.User;

public class QBOSimplePayroll implements QBOPlugin
{
  private HttpSession session;

  AmazonDynamoDBClient client;
  DynamoDBMapper mapper;
  
  public QBOSimplePayroll() throws IOException {
    client = Database.createInstance().getClient();
    mapper = new DynamoDBMapper(client);    
  }
  
  public void setSession(HttpSession session) {
    this.session = session;
  }
  
  public boolean doesRealmExist(String realmId) throws IOException {
    return findRealm(realmId) != null;
  }
  
  public boolean isRealmLoggedIn(String realmId) {
    String loggedInRealmId = getRealmId();
  
    try {
      return getHasValidOAuthConsumer() && loggedInRealmId != null && realmId != null && loggedInRealmId.equals(realmId)
        && getCompany() != null && getIntuitService() != null;
    } catch (Exception ex) {
      return false;
    }
  }

  public void saveoAuth(String dataSource, OAuthConsumer consumer) throws IOException {
    Company company = getCompany();
    company.setoAuthToken(consumer.getToken());
    company.setoAuthTokenSecret(consumer.getTokenSecret());
    // Only support QBO
    company.setDataSource("QBO");
    mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    session.setAttribute("validOAuthConsumer", consumer);    
  }

  public boolean getHasValidOAuthConsumer() {
    OAuthConsumer consumer = null;
    try {
      consumer = this.getValidOAuthConsumer();
    } catch (IOException ex) {
    }
    
    return consumer != null;
  }

  public String getSignupUrl(String realmId) {
    return session.getServletContext().getContextPath()+"/splash.jsp?realmId="+realmId;  
  }
  
  public String getStartPage() {
    return session.getServletContext().getContextPath()+"/in/start.jsp";
  }

  public void login(String realmId, String userId, String firstName, String lastName, String email) throws IOException, FMSException {
    session.removeAttribute("validOAuthConsumer");

    if (userId == null) {
      userId = getUserId();
    }
    this.findOrCreateUser(userId, true, firstName, lastName, email);

    Company company = this.findOrCreateRealm(realmId, true);

    if (this.getHasValidOAuthConsumer()) {
      IntuitService is = this.getIntuitService();
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
      }
    }
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
      session.setAttribute("realmId", realmId);
    }

    return company;
  }
  
  public Company getCompany() throws IOException {
    return findRealm(getRealmId());
  }
  
  public String getRealmId() {
    return  (String) session.getAttribute("realmId");
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
      session.setAttribute("userId", userId);
    }
    
    return user;
  }
  
  public User getUser() throws IOException {
    return findUser(getUserId());
  }
  
  public String getUserId() {
    return (String) session.getAttribute("userId");
  }
    
  public IntuitService getIntuitService() throws IOException, FMSException {
    if (this.getHasValidOAuthConsumer()) {
        String dataSource = getCompany().getDataSource();
        OAuthAuthorizer authorizer = new OAuthAuthorizer(
           Authentication.getoAuthConsumerKey(),
           Authentication.getoAuthConsumerSecret(),
           getCompany().getoAuthToken(),
           getCompany().getoAuthTokenSecret());
        
        return new IntuitService(this.session, authorizer, dataSource, getRealmId());
    } else {
    	return null;
    }
  }
    
  public OAuthConsumer getValidOAuthConsumer() throws IOException {
    OAuthConsumer consumer = (OAuthConsumer) session.getAttribute("validOAuthConsumer");
    if (consumer == null) {
      Company company = getCompany();
      if (company != null && company.getoAuthToken() != null && company.getoAuthTokenSecret() != null) {
        consumer = new DefaultOAuthConsumer(Config.getProperty("oauth_consumer_key"),
          Config.getProperty("oauth_consumer_secret"));
        consumer.setTokenWithSecret(company.getoAuthToken(), company.getoAuthTokenSecret());
        session.setAttribute("validOAuthConsumer", consumer);
      }
    }
    
    return consumer;
  }
    
  public void clearoAuth() throws IOException {
    Company company = getCompany();
    company.setoAuthToken(null);
    company.setoAuthTokenSecret(null);
    company.setDataSource(null);
    mapper.save(company, new DynamoDBMapperConfig(DynamoDBMapperConfig.SaveBehavior.CLOBBER));
    session.setAttribute("validOAuthConsumer", null);
  }

}
