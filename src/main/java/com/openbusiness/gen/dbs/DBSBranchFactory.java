package com.openbusiness.gen.dbs;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

// File Reading
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.openbusiness.opta.Destination;
import com.openbusiness.opta.dbs.DBSBranch;
import com.openbusiness.gen.Location;
import com.openbusiness.exceptions.*;


public class DBSBranchFactory
{
   public static List<Destination> generateFromFile(String csv_file, 
   						    Properties prop) throws Exception
   {
      if( prop.getProperty("distance_matrix").equals("yes") )
         DBSLocation.setDistanceMatrix("distance_mat.json");
	 
	 
      String fileName = "com/openbusiness/data/"+csv_file;
      
      String file = ""+DBSBranchFactory.class.getClassLoader().getResource( fileName ).getFile();
      BufferedReader br = null;
      InputStream str = DBSBranchFactory.class.getClassLoader().getResourceAsStream( fileName );
      String line = "";
      String split = "\",\"";
      
      Random rand = new Random();
      
      double volMin = Double.parseDouble(prop.getProperty("order_vol_lower_bound"));
      double volRange = Double.parseDouble(prop.getProperty("order_vol_range"));
      double weightMin = Double.parseDouble(prop.getProperty("order_weight_lower_bound"));
      double weightRange = Double.parseDouble(prop.getProperty("order_weight_range"));
      
      int    milliStartTime = 8 * 3600000;
      int    milliDueTime = 9 * 3600000;
      
      if (prop.getProperty("problem").equals("dbsafternoon"))
      {
         milliStartTime = 15 * 3600000;
	 milliDueTime   = 16 * 3600000;
      }
    
      List< Destination > destinations = new ArrayList< Destination >();
      HashMap< String, Integer > order = new HashMap< String, Integer >();
      SimpleDateFormat ft = new SimpleDateFormat("hh.mma");
      
      double min_lat = 255;
      double min_lon = 255;
      double max_lat = -255;
      double max_lon = -255;
      
      int dest_index = 0;
	 
      try{
        br = new BufferedReader(new InputStreamReader(str,"UTF-8"));
	
	// Read the file line by line
	while( (line = br.readLine()) != null )
	{
	   String [] csv = line.split(split);
	   
	   // Are we looking at headers? If not, assume order
	   if( order.isEmpty() )
	   {
	      for(int i = 0; i < csv.length; i++)
	        order.put( csv[i].replace("\"","").toLowerCase(), i );
	      
	      // Assume order
	      if( order.get("address") == null )
	      {
	         // Coming soon to a theatre near you
		 throw new InstallationException("Not implemented: Automatic Address Order",
		 				 new Exception());
	      }
	   }
	   
	   else
	   {
	      double lat,lon,vol,weight;


	      vol = volMin + volRange * rand.nextDouble();
	      weight = weightMin + weightRange * rand.nextDouble();
	      
	      Location branchLocation;
	      
	      if( prop.getProperty("distance_matrix").equals("yes") )
	        branchLocation = new DBSLocation(Double.parseDouble(csv[order.get("longitude")].replace("\"","")),
					         Double.parseDouble(csv[order.get("latitude")].replace("\"","")),
						 dest_index);
	      
	      else
	        branchLocation = new Location(Double.parseDouble(csv[order.get("longitude")].replace("\"","")),
					        Double.parseDouble(csv[order.get("latitude")].replace("\"","")));
	      
	      if(branchLocation.getLat() < min_lat)
	        min_lat = branchLocation.getLat();
	      if(branchLocation.getLat() > max_lat)
	        max_lat = branchLocation.getLat();
	      if(branchLocation.getLon() < min_lon)
	        min_lon = branchLocation.getLon();
	      if(branchLocation.getLon() > max_lon)
	        max_lon = branchLocation.getLon();
	      

	      DBSBranch branch = new DBSBranch( csv[order.get("branch") ].replace("\"",""),
	      				        csv[order.get("address") ].replace("\"",""),
					        csv[order.get("postal code") ].replace("\"",""),
					        csv[order.get("t") ].replace("\"",""),
					        csv[order.get("f") ].replace("\"",""),
					        csv[order.get("hours (weekdays)")].replace("\"",""),
					        csv[order.get("hours (saturdays)")].replace("\"",""),
						branchLocation,
					        vol,
					        weight);
	      if( prop.getProperty("problem").equals("dbsafternoon") )
	      {
	         branch.setMilliReadyTime(milliStartTime);
		 branch.setMilliDueTime(milliDueTime);
	      }
	      else
	      {				       
		 // Set the opening time
		 String [] openingHours;			       
		 if( prop.getProperty("schedule_day").equals("weekday") )
	            openingHours = csv[order.get("hours (weekdays)")].replace("\"","").replace(" ","").split("-");
		 else
	      	    openingHours = csv[order.get("hours (saturdays)")].replace("\"","").replace(" ","").split("-");

		 Calendar cal = Calendar.getInstance();
		 cal.setTime(ft.parse(openingHours[0]));
		 branch.setMilliReadyTime(cal.get(Calendar.HOUR) * 3600000 
	      				   + cal.get(Calendar.MINUTE) * 60000);

		 // TODO: Should use a property for this
		 branch.setMilliDueTime(milliDueTime);
	      }
	      // Skip impossible branches
	      if( branch.getMilliReadyTime() < branch.getMilliDueTime() )
	        destinations.add(branch);
		
	      dest_index ++;
	   }
	}
	
	Location.init(min_lat,max_lat,min_lon,max_lon);
      }
      catch(FileNotFoundException e){
         throw new InstallationException("File not found: " + fileName, e);
      }
      catch(IOException e){
         throw new InstallationException("Error opening file: " + fileName, e);
      }
      catch(Exception e){
         // TODO - proper exception handling required
         throw new InstallationException("Generic Exception: " + fileName, e);
      }
      finally {
	 if (br != null) {
	    try {
	       br.close();
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	 }
       }
       
       return destinations;
 
   }
}
