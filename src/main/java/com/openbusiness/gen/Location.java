package com.openbusiness.gen;

public class Location
{
  // ***************************************************************************
  // Static Methods
  // ***************************************************************************
  private static double m_max_lat;
  private static double m_min_lat;
  private static double m_max_lon;
  private static double m_min_lon;
  
  public static void init(double min_lat, 
                          double max_lat, 
                          double min_lon, 
                          double max_lon)
  {
    m_min_lat = min_lat;
    m_min_lon = min_lon;
    m_max_lat = max_lat;
    m_max_lon = max_lon;
  }  
  
  public static double getMaxLat()
  {
    return m_max_lat;
  }
  public static double getMinLat()
  {
    return m_min_lat;
  }
  public static double getMaxLon()
  {
    return m_max_lon;
  }
  public static double getMinLon()
  {
    return m_min_lon;
  }
  
  public static Location getCenter()
  {
    double lon = getMinLon() + 
    			(getMaxLon() - getMinLon()) / 2; 
    double lat = getMinLat() + 
    			(getMaxLat() - getMinLat()) / 2; 
			
    return new Location(lon,lat);
  }
  
  // ***************************************************************************
  // Instance Methods
  // ***************************************************************************
  
  private double m_lat;
  private double m_lon;

  public Location( double lon, double lat )
  { 
    m_lat = lat;
    m_lon = lon;
  }
  
  public double getLat()
  {
    return m_lat;
  }
  
  public double getLon()
  {
    return m_lon;
  }

  public double getDistance(Location order)
  {
    double latDiff = m_lat - order.getLat();
    double lonDiff = m_lon - order.getLon();
    
    double kmLatDiff = latDiff * 110.54;
    double kmLonDiff = 85.39 * lonDiff;
    
    return Math.sqrt(kmLatDiff * kmLatDiff + kmLonDiff * kmLonDiff);
  }
  

  // ***************************************************************************
  // Graphics Methods
  // ***************************************************************************
  
  public int getX(int width)
  {
    double lat_range = m_max_lat - m_min_lat;
    double lat_mult = (double)width / lat_range;
    
    // Move, scale and return
    return (int)( (m_lat - m_min_lat) * lat_mult );
  }
  
  public int getY(int height)
  {
    double lon_range = m_max_lon - m_min_lon;
    double lon_mult = (double)height / lon_range;
    
    return (int)( (m_lon - m_min_lon) * lon_mult );
  }
  
  @Override
  public String toString()
  {
    return "[" + getLat() + ", " + getLon() + "]";
  }
}
