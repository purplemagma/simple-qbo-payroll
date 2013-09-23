package com.purplemagma.qbosimplepayroll;

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

import oauth.signpost.OAuthConsumer;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.intuit.ds.qb.QBEmployee;
import com.intuit.ds.qb.QBEmployeeService;
import com.intuit.ds.qb.QBIdType;
import com.intuit.ds.qb.QBServiceFactory;
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
import com.intuit.platform.client.PlatformServiceType;
import com.intuit.platform.client.PlatformSessionContext;
import com.intuit.platform.client.security.OAuthCredentials;
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
    state.context = new Context(state.authorizer, Config.getAppToken(), state.dataSource, state.realmId);
    
    Properties props = new Properties();
    props.setProperty("workplace.server", "https://appcenter-stage.intuit.com");
    props.setProperty("qbo.server", "https://qa.qbo.intuit.com/qbo2/rest/");
    com.intuit.platform.util.Config.configure(props);
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
  
  public PlatformSessionContext getV2PlatformSessionContext() {
	  OAuthConsumer consumer = getIntuitServiceSessionState().consumer;
	  OAuthCredentials credentials = new OAuthCredentials(consumer.getConsumerKey(), consumer.getConsumerSecret(), consumer.getToken(), consumer.getTokenSecret());
	  PlatformSessionContext psc = new PlatformSessionContext(credentials, Config.getAppToken());
	  psc.setPlatformServiceType(PlatformServiceType.QBO);
	  psc.setRealmID(getIntuitServiceSessionState().realmId);
	  
	  return psc;
  }
  
  /*
   * Since V3 Employee doesn't work quite yet, we need to use V2
   */
  @Path("employees")
  @GET
  @Produces("application/json")
  public String getEmployees(@HeaderParam("Range") String rangeString) throws Exception {
	  PlatformSessionContext psc = getV2PlatformSessionContext();
	  QBEmployeeService employeeService = QBServiceFactory.getService(psc, QBEmployeeService.class);
	  int[] range = this.parse(rangeString);
	  List<QBEmployee> employees = employeeService.findAll(psc, range[0]+1, range[1]-range[0]);
	  
	  JSONArray result = new JSONArray(employees);
	  
	  for (int index = 0; index < result.length(); index++) {
		  JSONObject employee = result.getJSONObject(index);
		  employee.put("Id",employee.getJSONObject("id").getInt("value"));
		  result.put(index, employee);
	  }
	  return result.toString();
  }
  
  @Path("employees/{id}")
  @GET
  @Produces("application/json")
  public String getEmployeeById(@PathParam("id") String id) throws Exception {
	  PlatformSessionContext psc = getV2PlatformSessionContext();
	  QBEmployeeService employeeService = QBServiceFactory.getService(psc, QBEmployeeService.class);
	  QBEmployee employee = employeeService.findById(psc, new QBIdType(com.intuit.sb.cdm.IdDomainEnum.QBO, id));
	  
	  return employee == null ? null : new JSONObject(employee).toString();
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