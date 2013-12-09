package com.purplemagma.qbosimplepayroll;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import oauth.signpost.OAuthConsumer;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Customer;
import com.intuit.ipp.data.Employee;
import com.intuit.ipp.data.IntuitEntity;
import com.intuit.ipp.data.Vendor;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.security.OAuthAuthorizer;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.PlatformService;
import com.intuit.ipp.services.QueryResult;
import com.purplemagma.qbosimplepayroll.entities.IntuitEntityListAndCount;

@Path("in")
public class IntuitService
{
  @javax.ws.rs.core.Context
  private HttpServletRequest request;
  
  private HttpSession session;
  
  public class IntuitServiceSessionState {
	  public OAuthAuthorizer authorizer;
	  public OAuthConsumer consumer;
	  public ServiceType dataSource;
	  public String realmId;
	  public Context context;
  }
  
  public IntuitService() {
  }
  
  public IntuitService(HttpSession session, OAuthConsumer consumer, String dataSource, String realmId) throws FMSException {
    this.session = session;
    
    IntuitServiceSessionState state = new IntuitServiceSessionState();
    state.authorizer = new OAuthAuthorizer(consumer.getConsumerKey(), consumer.getConsumerSecret(), consumer.getToken(), consumer.getTokenSecret());
    state.dataSource = dataSource == null ? null : ServiceType.valueOf(dataSource);
    state.realmId = realmId;
    state.consumer = consumer;
    state.context = new Context(state.authorizer, com.purplemagma.qbosimplepayroll.Config.getAppToken(), state.dataSource, state.realmId);
        
    getSession().setAttribute("isSessionState", state);
  }
  
  public IntuitServiceSessionState getIntuitServiceSessionState() {
	  if (getSession().getAttribute("isSessionState") != null) {
		  return (IntuitServiceSessionState) getSession().getAttribute("isSessionState");
	  } else {
		  return null;
	  }
  }
  
  public Context getContext() {  
    com.intuit.ipp.util.Config.setProperty(com.intuit.ipp.util.Config.BASE_URL_PLATFORMSERVICE, com.purplemagma.qbosimplepayroll.Config.getProperty("platform_url"));
    com.intuit.ipp.util.Config.setProperty(com.intuit.ipp.util.Config.BASE_URL_QBO, com.purplemagma.qbosimplepayroll.Config.getProperty("qbo_url_v3"));
    
	  return getIntuitServiceSessionState().context;
  }

  public HttpSession getSession() {
	  return this.session == null ? request.getSession() : this.session; 
  }
  
  public CompanyInfo getCompany() throws FMSException {
    Context context = getContext();
        
    DataService dataService = new DataService(context);
    CompanyInfo filter = new CompanyInfo();
    filter.setId(getIntuitServiceSessionState().realmId);
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
  
  @Path("customers")
  @GET
  @Produces("application/json")
  public List<Customer> getCustomers() throws FMSException {
    DataService service = new DataService(getContext());
    return service.findAll(new Customer());
  }
      
  @Path("employees")
  @GET
  @Produces("application/json")
  public List<Employee> getEmployees(@HeaderParam("Range") String rangeString) throws Exception {
    DataService service = new DataService(getContext());
    return service.findAll(new Employee());    
  }
  
  @Path("employees/{id}")
  @GET
  @Produces("application/json")
  public Employee getEmployeeById(@PathParam("id") String id) throws Exception {
    DataService service = new DataService(getContext());
    Employee employee = new Employee();
    employee.setId(id);
    return service.findById(employee);
  }
  
  @Path("vendors")
  @GET
  @Produces("application/json")
  public List<Vendor> getVendors() throws FMSException {
    DataService service = new DataService(getContext());
    return service.findAll(new Vendor());    
  }

  @SuppressWarnings("unchecked")
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