package com.openbusiness.app;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.io.PrintStream;

import com.openbusiness.gen.*;
import com.openbusiness.opta.*;
import com.openbusiness.opta.dbs.*;
import com.openbusiness.opta.dbs.afternoon.*;
import java.util.Properties;

public class JSONOutputWriter
{
  private JSONObject m_output_object;
  
  
  public JSONOutputWriter()
  {
    m_output_object = new JSONObject();
  }
  
  public void writeDeliverySchedules(DeliverySchedule [] schedule,
  				     Properties props)
  {
    for(int i = 0; i < schedule.length; i++)
    {
      Vehicle vehicle = schedule[i].getDeliveryVehicle();
      
      JSONObject obj = new JSONObject();
      obj.put("fuel_efficiency", vehicle.getFuelEfficiency());
      obj.put("fuel_capacity", vehicle.getFuelCapacity());
      obj.put("weight_capacity", vehicle.getWeightCapacity());
      obj.put("volume_capacity", vehicle.getVolumeCapacity());
      
      obj.put("volume_used", schedule[i].getVolumeUsed());
      obj.put("weight_used", schedule[i].getWeightUsed());
      obj.put("distance_travelled", schedule[i].getDistTravelled());
      obj.put("fuel_used", schedule[i].getFuelUsed());
      
      
      if( props.getProperty("problem").equals("dbsmorning")
     	     || props.getProperty("problem").equals("dbsafternoon") )
      {
        obj.put("type", ((DBSVehicle)vehicle).getVehicleType());
      }
      m_output_object.put("Vehicle_"+i,obj);
    }
    
    for(int i = 0; i < schedule.length; i++)
    {
      JSONArray deliveryOrdersArray = new JSONArray();

      List<Destination> deliveryOrderList = schedule[i].getOrderList();
      
      // Convert each object to key-value pairs
      for(int j = 0; j < deliveryOrderList.size(); j++)
      {
        JSONObject obj = new JSONObject();
	obj.put("order", j);
	obj.put("volume", deliveryOrderList.get(j).getVolume());
	obj.put("latitude", deliveryOrderList.get(j).getLocation().getLat());
	obj.put("longitude", deliveryOrderList.get(j).getLocation().getLon());
	obj.put("longitute", deliveryOrderList.get(j).getLocation().getLon());
	obj.put("weight", deliveryOrderList.get(j).getWeight());
	obj.put("distanceToPrev", deliveryOrderList.get(j).getDistanceToPrevOrder());
	
	if( props.getProperty("problem").equals("dbsmorning")
		|| props.getProperty("problem").equals("dbsafternoon") )
	{
	   DBSBranch branch = (DBSBranch)deliveryOrderList.get(j);
	   obj.put("opening_time", branch.getMilliReadyTime());
	   obj.put("latest_pickup", branch.getMilliDueTime());
	   obj.put("picked_up", branch.getMilliArrivalTime());
	}
	
	deliveryOrdersArray.add(obj);
      }
      
      //
      m_output_object.put( "Vehicle_"+i+"_order", deliveryOrdersArray );
    }
  }
  
  public void writeData(String name, String value)
  {
    m_output_object.put(name,value);
  }
  
  public String getString()
  {
    return "" + m_output_object;
  }
  
  public void print(PrintStream out)
  {
    out.print(m_output_object);
  }
  
  public void clear()
  {
    m_output_object = new JSONObject();
  }
}
