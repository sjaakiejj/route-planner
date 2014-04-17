package com.openbusiness.gen.dbs;


import com.openbusiness.gen.Location;

// JSON
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

// File Reading
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


// This location class uses a Distance Matrix to calculate distances
public class DBSLocation extends Location
{
  private static JSONObject m_dist_mat;
  private int m_origin_index;
  
  public static void setDistanceMatrix( String jsonFile )
  {
      System.out.println( "Setting distance matrix " );
      JSONParser parser = new JSONParser();
      String fileName = "com/openbusiness/data/"+jsonFile;
      System.out.println("Test 1");
      
      String file = ""+DBSBranchFactory.class.getClassLoader().getResource( fileName ).getFile();
      
      System.out.println("Test 2");
      InputStreamReader br = null;
      InputStream str = DBSBranchFactory.class.getClassLoader().getResourceAsStream( fileName );
      System.out.println("Test 3");
      try{
        br = new InputStreamReader(str,"UTF-8");
	System.out.println("Stream constructed");
	m_dist_mat = (JSONObject)parser.parse(br);
      }
      catch(FileNotFoundException e){
         e.printStackTrace();
      }
      catch(IOException e){
         e.printStackTrace();
      }
      catch(Exception e){
         e.printStackTrace();
      }
      finally{
	 if (br != null) {
	    try {
	       br.close();
	    } catch (IOException e) {
	       e.printStackTrace();
	    }
	 }
      }
      System.out.println("Distance Matrix Set");
  }

  public DBSLocation( double lon, double lat, int index )
  {
    super(lon,lat);
    
    System.out.println("Creating location "+ index +" at " + lat + ", " + lon);
    m_origin_index = index;
  }
   
  @Override
  public double getDistance(Location loc)
  {
//     JSONArray addresses = (JSONArray) m_dist_mat.get("destination_addresses");

     // In case of a depot
     if( !( loc instanceof DBSLocation ) )
       return super.getDistance( loc );

     JSONObject destination = getDestination( getOriginIndex(),
     					     ((DBSLocation)loc).getOriginIndex() );
     // Else get the distance from the object and return
     JSONObject distance = (JSONObject)destination.get("distance");
     
     try{
        return (Integer.parseInt( "" + distance.get("value") )/1000.0);
     }catch(Exception e){
        System.out.println(e);
        e.printStackTrace();
        return super.getDistance( loc );
     }
  }
  
  public int getTimeToLocationInMillis(Location loc)
  {
     JSONObject destination = getDestination( getOriginIndex(),
     					     ((DBSLocation)loc).getOriginIndex() );
     
     JSONObject duration = (JSONObject)destination.get("duration");
     
     try{
        return (Integer.parseInt( "" + duration.get("value") )*1000);
     }catch(Exception e){
        System.out.println(e);
        e.printStackTrace();
        return (int)(getDistance( loc ) * 3600000);
     }
  }
  
  public int getOriginIndex()
  {
     return m_origin_index;
  }
  
  private static JSONObject getDestination( int orig_index, int dest_index )
  {
     if( dest_index < 0 )
       return null;
  
     JSONArray rows = (JSONArray)m_dist_mat.get("rows"); 
     
     JSONObject row = (JSONObject)rows.get( orig_index );
     JSONArray elements = (JSONArray)row.get("elements");
     
     JSONObject destination = (JSONObject)elements.get(dest_index);
     
     return destination;
  }
}
