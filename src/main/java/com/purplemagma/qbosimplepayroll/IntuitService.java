package com.purplemagma.qbosimplepayroll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.process.internal.RequestScoped;

import com.intuit.ipp.core.Context;
import com.intuit.ipp.core.ServiceType;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Customer;
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
  
  OAuthAuthorizer authorizer;
  ServiceType dataSource;
  String realmId;
  
  public IntuitService() {
  }
  
  public IntuitService(HttpSession session, OAuthAuthorizer authorizer, String dataSource, String realmId) throws FMSException {
    this.session = session;
    this.authorizer = authorizer;
    this.dataSource = dataSource == null ? null : ServiceType.valueOf(dataSource);
    this.realmId = realmId;
    getContext();
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
    
    context = new Context(this.authorizer, Config.getAppToken(), this.dataSource, this.realmId);
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
  
  @Path("customers")
  @GET
  @Produces("application/json")
  public List<Customer> getCustomers() throws FMSException {
    DataService service = new DataService(getContext());
    return service.findAll(new Customer());
  }
  
  private int[] parse(String range) {
	  int[] result = new int[2];
	  if (range!=null) {
        String from=range.split("=")[1].split("-")[0];
        String t=range.split("=")[1].split("-")[1];
        result[0] = Integer.parseInt(from);
        result[1] = Integer.parseInt(t);
	  } else {
		  result[0] = 0;
		  result[1] = -1;
	  }
	  	  
	  return result;
  }
  
  @Path("employees")
  @GET
  @Produces("application/json")
  public List<Customer> getEmployees(@HeaderParam("Range") String rangeString) throws FMSException {
    DataService service = new DataService(getContext());
	QueryResult result = service.executeQuery("select * from customer");
	int []range = parse(rangeString);
	result.setStartPosition(range[0]);
	result.setMaxResults(range[1]-range[0]);
	return (List<Customer>) result.getEntities();
  }
  
  @Path("employees/{id}")
  @GET
  @Produces("application/json")
  public Customer getEmployeeById(@PathParam("id") String id) throws FMSException {
    DataService service = new DataService(getContext());
  	Customer employee = new Customer();
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