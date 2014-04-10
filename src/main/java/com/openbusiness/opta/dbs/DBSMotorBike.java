package com.openbusiness.opta.dbs;

//import com.openbusiness.gen.DeliveryVehicle;

public class DBSMotorBike extends DBSVehicle
{
  public DBSMotorBike(){}
 
  public DBSMotorBike(double fuelEff, 
                         double fuelCap, 
                         double volCap, 
                         double weight)
  {
    super(fuelEff,fuelCap,volCap,weight);
  }


  @Override
  public String getVehicleType()
  {
    return "MotorBike";
  }
  
  @Override
  public double getFuelEfficiency()
  {
    return 50.0; // 50 MPG
  }
}
