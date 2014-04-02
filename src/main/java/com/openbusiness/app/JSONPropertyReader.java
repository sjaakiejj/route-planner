package com.openbusiness.app;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;


import org.apache.commons.io.IOUtils;
import java.util.Properties;
import java.io.InputStream;
import java.io.StringWriter;

public class JSONPropertyReader
{
  public static Properties load(InputStream input) throws Exception
  {
    // Convert the input stream to a string
    StringWriter writer = new StringWriter();
    IOUtils.copy(input, writer);
    String jsonString = writer.toString();
  
    JSONParser parser = new JSONParser();
    
    // Parse the string and convert it to a JSON array
    JSONArray jsonArr = (JSONArray) parser.parse(jsonString);
    
    Properties prop = new Properties();
    for(int i = 0; i < jsonArr.size(); i++)
    {
     // prop.set (jsonArr.getString(i));
     JSONObject obj = (JSONObject)(jsonArr.get(i));
     
     for( Object key : obj.keySet() ){
       prop.setProperty((String)key, (String)obj.get(key));
     }
     
   //   System.out.println( p );
    }
    
    return prop;
  }
}
