package com.openbusiness.opta;

// OptaPlanner
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.XmlSolverFactory;

// OpenBusiness
import com.openbusiness.gen.Location;
import com.openbusiness.gen.DeliverySchedule;
import com.openbusiness.gen.DeliverySolution;
import com.openbusiness.exceptions.UnrecognizedProblemException;

// Time Windowed
import com.openbusiness.opta.dbs.DBSPlannerSolution;
import com.openbusiness.opta.dbs.DBSVehicle;
import com.openbusiness.opta.dbs.DBSMotorBike;
import com.openbusiness.opta.dbs.DBSVan;
import com.openbusiness.opta.dbs.DBSBranch;

// Java
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class Planner
{

  public static DeliverySolution plan(List<Vehicle> vehicles, 
  				      List<Destination> orders,
				      Properties props) throws UnrecognizedProblemException
  {
    SolverFactory solverFactory = new XmlSolverFactory(
    	"/com/openbusiness/configuration/routePlannerConfig.xml");
    Solver solver = solverFactory.buildSolver();
    
    
    /* TODO: REFACTOR
     * Get rid of local objects. Data will come through protocol, so
     * no need for local domain objects.
     */
    // Convert the objects
    List<Vehicle> optaVehicles = new ArrayList<Vehicle>();
    List<Destination> optaOrders = new ArrayList<Destination>();
    List<Location> locationList = new ArrayList<Location>();
    
    RoutingPlannerSolution unsolved = null;
    
    // Standard vehicle routing problem
    if(props.getProperty("problem") == null ||
    	props.getProperty("problem").equals("standard"))
    {
       	unsolved = new RoutingPlannerSolution();
	
	optaVehicles = vehicles;
	optaOrders   = orders;
	
       	for( Destination order : orders )
       	{
	   locationList.add(order.getLocation());
       	}
    }
       
    // Time Windowed   
    else if(props.getProperty("problem").equals("dbsmorning"))
    {
       	unsolved = new DBSPlannerSolution();
       	Random rand = new Random();
       	for( Vehicle vehicle : vehicles )
       	{
	 int randVehicleType = rand.nextInt();

	 if( randVehicleType % 2 == 0 )
           optaVehicles.add(new DBSVan(vehicle.getFuelEfficiency(),
	   				vehicle.getFuelCapacity(),
					vehicle.getVolumeCapacity(),
					vehicle.getWeightCapacity()));
	 else
           optaVehicles.add(new DBSMotorBike(vehicle.getFuelEfficiency(),
	   				vehicle.getFuelCapacity(),
					vehicle.getVolumeCapacity(),
					vehicle.getWeightCapacity()));

       	}
	
	
       	for( Destination order : orders )
       	{
	   int randOpenTime = rand.nextInt();
	   int hour = 8;
	   int minute = 0;
	   
	   if( randOpenTime % 3 == 0 )
	     minute = 30;
	   else if ( randOpenTime % 3 == 1 )
	     minute = 45;
	   
	   optaOrders.add(new DBSBranch(order.getLocation(),
	   				order.getVolume(),
					order.getWeight(),
	   				getTimeInMillis(hour,minute),
					getTimeInMillis(9,0)
					));
	   locationList.add(order.getLocation());
       	}
    }
    // Didn't recognize, exit.
    else
    	throw new UnrecognizedProblemException( props.getProperty("problem") );
    
    // Now for actually solving the problem
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
    
    List<DeliverySchedule> deliverySchedules = new ArrayList<DeliverySchedule>();
    List<Vehicle> solvedVehicles = solved.getVehicleList();
    for(int i = 0; i < solvedVehicles.size(); i++)
    { 
      deliverySchedules.add(
      	new DeliverySchedule(solvedVehicles.get(i)));
      
      Destination currentOrder =
      				 solvedVehicles.get(i).getNextDestination();
      while( currentOrder != null )
      {
        deliverySchedules.get(i).addOrder( currentOrder );
	currentOrder = currentOrder.getNextDestination(); 
      }
      
      deliverySchedules.get(i).close();
    }
    
    DeliverySolution deliverySolution = new DeliverySolution(
    					solved.getScore().getHardScore(),
					solved.getScore().getSoftScore(),
					deliverySchedules);
					
    
    return deliverySolution;
  }
  
  public static int getTimeInMillis(int hours, int minutes)
  {
     return (hours * 3600000) + (60000 * minutes);
  }
}
