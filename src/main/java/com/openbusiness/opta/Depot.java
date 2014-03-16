package com.openbusiness.opta;

import com.openbusiness.gen.Location;

public class Depot
{
  protected Location m_location;
  
  public Location getLocation(){
    return m_location;
  }
  
  public void setLocation(Location loc)
  {
    m_location = loc;
  }
}
