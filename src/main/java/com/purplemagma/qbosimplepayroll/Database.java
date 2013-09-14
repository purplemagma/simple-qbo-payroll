package com.purplemagma.qbosimplepayroll;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.purplemagma.qbosimplepayroll.Database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("database")
public class Database
{
  static Database instance;
  public static Database createInstance() throws IOException {
    return new Database();
  }

  AmazonDynamoDBClient client;
  public Database() throws IOException, FileNotFoundException, AmazonServiceException {
    AWSCredentials credentials = new PropertiesCredentials(Database.class.getResourceAsStream("/AWSCredentials.properties"));
    client = new AmazonDynamoDBClient(credentials);
    client.setEndpoint("dynamodb.us-west-2.amazonaws.com");
  }
  
  public AmazonDynamoDBClient getClient() {
    return this.client;
  }
  
  private void createOneTable(String tableName, String primaryKey, String secondaryRange) {
    ArrayList<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
    attributeDefinitions.add(new AttributeDefinition().withAttributeName(primaryKey).withAttributeType("S"));
    if (secondaryRange != null) {
      attributeDefinitions.add(new AttributeDefinition().withAttributeName(secondaryRange).withAttributeType("S"));
    }
            
    ArrayList<KeySchemaElement> ks = new ArrayList<KeySchemaElement>();
    ks.add(new KeySchemaElement().withAttributeName(primaryKey).withKeyType(KeyType.HASH));
    if (secondaryRange != null) {
      ks.add(new KeySchemaElement().withAttributeName(secondaryRange).withKeyType(KeyType.RANGE));
    }
      
    ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
        .withReadCapacityUnits(10L)
        .withWriteCapacityUnits(10L);
            
    CreateTableRequest request = new CreateTableRequest()
        .withTableName(tableName)
        .withAttributeDefinitions(attributeDefinitions)
        .withKeySchema(ks)
        .withProvisionedThroughput(provisionedThroughput);
        
    client.createTable(request);    
  }
  
  private void deleteOneTable(String tableName) {
    DeleteTableRequest deleteTableRequest = new DeleteTableRequest(tableName);
    
    client.deleteTable(deleteTableRequest);    
  }
  
  private String checkOneTable(String tableName) {
      DescribeTableRequest request = new DescribeTableRequest().withTableName(tableName);
      DescribeTableResult result = client.describeTable(request);
      TableDescription tableDescription = result.getTable();
      return tableName+": "+tableDescription.getTableStatus().toString();    
  }
  
  @Path("createTables")
  @GET
  public String createTables() {
    createOneTable("qbo_simple_payroll_company", "realmId", null);
    createOneTable("qbo_simple_payroll_user", "userId", null);
    
    return "Success";
  }

  @Path("deleteTables")
  @GET
  public String deleteTables() {
    deleteOneTable("qbo_simple_payroll_company");
    deleteOneTable("qbo_simple_payroll_user");
    
    return "Success";
  }

  @Path("checkTables")
  @GET
  public String checkTables() {    
    StringBuilder builder = new StringBuilder();
    builder.append(checkOneTable("qbo_simple_payroll_company"));
    builder.append("<br/>");
    builder.append(checkOneTable("qbo_simple_payroll_user"));
    builder.append("<br/>");
    
    Map<String,String> map = System.getenv();
    for (Entry<String,String> item : map.entrySet()) {
      builder.append(item.getKey());
      builder.append(":");
      builder.append(item.getValue());
      builder.append("<br/>");
    }
    
    return builder.toString();
  }  
}
