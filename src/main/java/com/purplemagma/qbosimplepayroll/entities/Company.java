package com.purplemagma.qbosimplepayroll.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="qbo_simple_payroll_company")
public class Company
{
  private String realmId;
  private String businessName;
  
  @DynamoDBHashKey(attributeName="realmId")
  public String getRealmId() {
    return realmId;
  }
  
  public void setRealmId(String realmId) {
    this.realmId = realmId;
  }

  @DynamoDBAttribute(attributeName="businessName")
  String getBusinessName() {
    return businessName;
  }
  
  public void setBusinessName(String businessName) {
    this.businessName = businessName;
  }
}
