package com.openbusiness.opta.dbs;

//import com.openbusiness.gen.DeliveryVehicle;

// Vans drive at 15 MPG and have infinite capacity
public class DBSVan extends DBSVehicle
{
   public DBSVan(){}

   public DBSVan(double fuelEff, 
                         double fuelCap, 
                         double volCap, 
                         double weight)
  {
    super(fuelEff,fuelCap,volCap,weight);
  }

   
   public String getVehicleType()
   {
      return "Van";
   }
   
   public double getFuelEfficiency()
   {
      return 15.0; // 15 MPG
   }
   
   // Assume that we have infinite volume, weight and fuel capacity
   public double getVolumeCapacity()
   {
      return Double.MAX_VALUE; 
   }
   
   public double getFuelCapacity()
   {
      return Double.MAX_VALUE;
   }
   
   public double getWeightCapacity()
   {
      return Double.MAX_VALUE;
   }
}
