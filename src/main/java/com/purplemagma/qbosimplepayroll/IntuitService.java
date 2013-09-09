package com.purplemagma.qbosimplepayroll;

import javax.servlet.http.HttpSession;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.Company;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;

import java.util.List;

public class IntuitService
{
  HttpSession session;
  OAuthAuthorizer authorizer;
  ServiceType dataSource;
  String realmId;
  
  public IntuitService(HttpSession session, OAuthAuthorizer authorizer, String dataSource, String realmId) {
    this.session = session;
    this.authorizer = authorizer;
    this.dataSource = dataSource == null ? null : ServiceType.valueOf(dataSource);
    this.realmId = realmId;
  }
  
  public Context getContext() throws FMSException {
    Context context = (Context) session.getAttribute("ippContext");
    if (context != null) {
      return context;
    }
    
    if (this.authorizer == null || this.realmId == null || this.dataSource == null) {
      return null;
    }
    
    context = new Context(authorizer, Config.getProperty("appToken"), dataSource, realmId);
    
    return context;
  }
  
  public Company getIPPCompany() throws FMSException {
    Context context = getContext();
    
    if (context == null) {
      return null;
    }
    
    DataService dataService = new DataService(context);
    List<Company> companies = dataService.findAll(new Company());
    
    if (companies == null || companies.size() != 1) {
      return null;
    }
    
    return companies.get(0);
  }
}