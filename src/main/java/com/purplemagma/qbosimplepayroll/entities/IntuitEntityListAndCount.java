package com.purplemagma.qbosimplepayroll.entities;

import com.intuit.ipp.data.IntuitEntity;

import java.util.List;

public class IntuitEntityListAndCount
{
  List<IntuitEntity> entities;
  Integer count;
  public Integer getCount()
  {
     return count;
  }
  public void setCount(Integer count)
  {
     this.count = count;
  }
  public List<IntuitEntity> getEntities()
  {
     return entities;
  }
  public void setEntities(List<IntuitEntity> entities)
  {
     this.entities = entities;
  }
  
}