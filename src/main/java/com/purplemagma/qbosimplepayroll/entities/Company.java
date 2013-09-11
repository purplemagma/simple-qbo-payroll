package com.purplemagma.qbosimplepayroll.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="qbo_simple_payroll_company")
public class Company
{
  private String realmId;
  private String businessName;
  private String oAuthToken;
  private String oAuthTokenSecret;
  private String dataSource;
  
  @DynamoDBHashKey(attributeName="realmId")
  public String getRealmId() {
    return realmId;
  }
  
  public void setRealmId(String realmId) {
    this.realmId = realmId;
  }

  @DynamoDBAttribute(attributeName="businessName")
  public String getBusinessName() {
    return businessName;
  }
  
  public void setBusinessName(String businessName) {
    this.businessName = businessName;
  }

  @DynamoDBAttribute(attributeName="oAuthToken")
  public String getoAuthToken()
  {
     return oAuthToken;
  }
  public void setoAuthToken(String oAuthToken)
  {
     this.oAuthToken = oAuthToken;
  }
  
  @DynamoDBAttribute(attributeName="oAuthTokenSecret")
  public String getoAuthTokenSecret()
  {
     return oAuthTokenSecret;
  }
  public void setoAuthTokenSecret(String oAuthTokenSecret)
  {
     this.oAuthTokenSecret = oAuthTokenSecret;
  }
  
  @DynamoDBAttribute(attributeName="dataSource")
  public String getDataSource()
  {
    return dataSource;
  }
  public void setDataSource(String dataSource)
  {
    this.dataSource = dataSource;
  }
}
