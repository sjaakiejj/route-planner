package com.openbusiness.gen;

import java.util.List;
import java.util.ArrayList;

// This class can alternatively store the data in
// a map structure, with each vehicle being associated with a list of orders
// internally. The sample's description was unclear as to which of these two
// approaches is preferred. 
public class DeliverySchedule
{
  private List<DeliveryOrder> m_orderList;
  private DeliveryVehicle m_assignedVehicle;
  
  public DeliverySchedule(DeliveryVehicle vehicle)
  {
    m_assignedVehicle = vehicle;
    m_orderList = new ArrayList<DeliveryOrder>();
  }
  
  public void addOrder(DeliveryOrder order)
  {
    m_orderList.add(order);
  }
  
  public List<DeliveryOrder> getOrderList()
  {
    return m_orderList;
  }
}
