package com.openbusiness.opta;

// OptaPlanner
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;

// OpenBusiness
import com.openbusiness.gen.Location;
import com.openbusiness.gen.DeliveryVehicle;
import com.openbusiness.gen.DeliveryOrder;
import com.openbusiness.gen.DeliverySchedule;
import com.openbusiness.gen.DeliverySolution;


// Java
import java.util.List;
import java.util.ArrayList;
public class Planner
{
  public static DeliverySolution plan(DeliveryVehicle [] vehicles, DeliveryOrder [] orders)
  {
    SolverFactory solverFactory = new XmlSolverFactory(
    	"/com/openbusiness/configuration/routePlannerConfig.xml");
    Solver solver = solverFactory.buildSolver();
    
    // Convert the objects
    List<OptaDeliveryVehicle> optaVehicles = 
    				new ArrayList<OptaDeliveryVehicle>();
    List<OptaDeliveryOrder> optaOrders = 
    				new ArrayList<OptaDeliveryOrder>();
    List<Location> locationList = new ArrayList<Location>();
    
    for( DeliveryVehicle vehicle : vehicles )
      optaVehicles.add(new OptaDeliveryVehicle(vehicle));
    
    for( DeliveryOrder order : orders )
    {
      optaOrders.add(new OptaDeliveryOrder(order));
      locationList.add(order.getLocation());
    }
    
    // TODO: Set print as option, not as default
 //   System.out.println("Problem Size: " + optaVehicles.size() + 
  //  		    " Vehicles, " + optaOrders.size() + 
//		    " Orders, " + 1 + 
//		    " Depots");

    
    RoutingPlannerSolution unsolved = new RoutingPlannerSolution();
    unsolved.setVehicleList(optaVehicles);
    unsolved.setOrderList(optaOrders);
    unsolved.setLocationList(locationList);
    unsolved.generateDepot(); // Generate a depot
    
    // Solve the problem
    solver.setPlanningProblem( unsolved );
    solver.solve();
    
    // Solution - convert to DeliverySchedule objects
    RoutingPlannerSolution solved =
    			 (RoutingPlannerSolution)solver.getBestSolution();
    System.out.println();
    
    // TODO: Get the fuel used, the routes taken and assign to the DeliverySchedule
    // objects. One for each vehicle.
    //printResults(solved);
    
    List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
    List<OptaDeliveryVehicle> solvedVehicles = solved.getVehicleList();
    for(int i = 0; i < solvedVehicles.size(); i++)
    { 
      deliverySchedules.add(
      	new DeliverySchedule(solvedVehicles.get(i).getWrappedDeliveryVehicle()));
      
      OptaDeliveryOrder currentOrder =
      				 solvedVehicles.get(i).getNextDeliveryOrder();
      while( currentOrder != null )
      {
        deliverySchedules.get(i).addOrder( currentOrder.getWrappedDeliveryOrder());
	currentOrder = currentOrder.getNextDeliveryOrder(); 
      }
    }
    
    DeliverySolution deliverySolution = new DeliverySolution(
    					solved.getScore().getHardScore(),
					solved.getScore().getSoftScore(),
					deliverySchedules);
					
    
    return deliverySolution;
  }
  
  public static void printResults(RoutingPlannerSolution solved)
  {    
    List<OptaDeliveryVehicle> solvedVehicles = solved.getVehicleList();
    
    System.out.println("Final Score: " + solved.getScore());
    double totalWeightCap = 0;
    double totalVolCap = 0;
    
    for(int i = 0; i < solvedVehicles.size(); i++)
    {
      System.out.println("=================================================");
      System.out.println(solvedVehicles.get(i));
      System.out.println("\n");
      totalWeightCap += solvedVehicles.get(i).getWeightCapacity();
      totalVolCap += solvedVehicles.get(i).getVolumeCapacity();
    }
    
    List<OptaDeliveryOrder> solvedOrders = solved.getOrderList();
    double totalWeight = 0;
    double totalVol = 0;
    for(int i = 0; i < solvedOrders.size(); i++)
    {
       totalWeight += solvedOrders.get(i).getWeight();
       totalVol += solvedOrders.get(i).getVolume();
    }
    System.out.println("Totals (Required/Available): \nWeight: ("+
    			totalWeight + "/" + totalWeightCap + ")\nVolume: (" +
			totalVol + "/" + totalVolCap + ")\n");
  }
}
