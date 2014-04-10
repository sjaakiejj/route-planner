package com.openbusiness.opta.dbs;

import com.openbusiness.opta.Vehicle;
//import com.openbusiness.gen.DeliveryVehicle;

public abstract class DBSVehicle extends Vehicle
{
  public DBSVehicle(){}

  public DBSVehicle(double fuelEff, 
                         double fuelCap, 
                         double volCap, 
                         double weight)
  {
    super(fuelEff,fuelCap,volCap,weight);
  }

  public abstract String getVehicleType();
}
