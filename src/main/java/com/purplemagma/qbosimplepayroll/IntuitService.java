package com.purplemagma.qbosimplepayroll;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Employee;
import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.PlatformService;
import com.intuit.ipp.services.QueryResult;
import com.purplemagma.qbosimplepayroll.Config;
import com.purplemagma.qbosimplepayroll.entities.IntuitEntityListAndCount;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("in")
public class IntuitService
{
  @javax.ws.rs.core.Context
  private HttpServletRequest request;
  
  private HttpSession session;
  
  OAuthAuthorizer authorizer;
  ServiceType dataSource;
  String realmId;
  
  public IntuitService() {
  }
  
  public IntuitService(HttpSession session, OAuthAuthorizer authorizer, String dataSource, String realmId) {
    this.session = session;
    this.authorizer = authorizer;
    this.dataSource = dataSource == null ? null : ServiceType.valueOf(dataSource);
    this.realmId = realmId;
  }
  
  public Context getContext() throws FMSException {
    if (this.session == null) {
      this.session = request.getSession();
    }
    Context context = (Context) session.getAttribute("ippContext");
    if (context != null) {
      return context;
    }
    
    if (this.authorizer == null || this.realmId == null || this.dataSource == null) {
      return null;
    }
    
    context = new Context(this.authorizer, Config.getProperty("appToken"), this.dataSource, this.realmId);
    this.session.setAttribute("ippContext", context);
    
    return context;
  }
  
  public CompanyInfo getCompany() throws FMSException {
    Context context = getContext();
    
    if (context == null) {
      return null;
    }
    
    DataService dataService = new DataService(context);
    CompanyInfo filter = new CompanyInfo();
    filter.setId(this.realmId);
    CompanyInfo company = dataService.findById(filter);
    return company;
  }
  
  public String getIDSRealm() throws FMSException {
    Context context = getContext();
    
    if (context == null) {
      return null;
    }
    
    PlatformService service = new PlatformService(context);
    return service.getIDSRealm();
  }
  
  public List<Customer> getCustomers() throws FMSException {
    DataService service = new DataService(getContext());
    return service.findAll(new Customer());
  }
  
  public int getNumberOfCustomers() throws FMSException {
    DataService service = new DataService(getContext());
    QueryResult result = service.executeQuery("SELECT COUNT(*) FROM Customer");
    return result.getTotalCount();
  }
  
  public int getNumberOfCustomersWithEmails() throws FMSException {
    DataService service = new DataService(getContext());
    QueryResult result = service.executeQuery("SELECT COUNT(*) FROM Customer");
    return result.getTotalCount();
  }

  public List<Employee> getEmployees() throws FMSException {
    DataService service = new DataService( getContext());
    return service.findAll(new Employee());    
  }
  
  @Path("doQuery")
  @GET
  @Produces("application/json")
  public IntuitEntityListAndCount doQuery(@QueryParam("query") String sql, @QueryParam("maxRows") int maxRows) throws FMSException {
    DataService service = new DataService(getContext());
    
    QueryResult result = service.executeQuery(sql);
    result.setMaxResults(maxRows);
    
    List<IntuitEntity> list = (List<IntuitEntity>) result.getEntities();
    
    IntuitEntityListAndCount returnValue = new IntuitEntityListAndCount();
    returnValue.setEntities(list);
    returnValue.setCount(result.getTotalCount());

    return returnValue;
  }
}