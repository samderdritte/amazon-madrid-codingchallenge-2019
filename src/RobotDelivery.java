import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class RobotDelivery {

	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
    	Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    	
    	TripReader tr = new TripReader("trip_records.json");
    	Map<Integer, List<String>> allTrips = tr.getTrips();
    	Map<String, Station> allStations = ssr.allStations;
    	
    	OrdersReader or = new OrdersReader("orders.jsonl", ssr);
    	//OrdersReader or = new OrdersReader("orders_small.jsonl", ssr);
    	RobotReader rr = new RobotReader("robots.jsonl", ssr);
    	    	
    	ArrayList<Order> ordersSorted = new ArrayList<Order>();
    	for (int i = 0; i<or.getOrders().size();i++) {
    		ordersSorted.add(or.getOrders().get(i));
    	}
    	
    	Map<Integer, Robot> allRobots = rr.getRobots();
    	
    	Set<Robot> robots = new HashSet<Robot>();
    	int numRobots = allRobots.size();
    	for (int i=0;i < numRobots;i++) {
    		robots.add(allRobots.get(i));
    	}
   
    	
    	// time loop - increase time by packages have not been delivered
    	boolean allPackagesDelivered = false;
    	int numPackages = or.getOrders().size();
    	int numPackagesDelivered = 0;
    	Map<Integer, ArrayList<JSONObject>> results = new HashMap<Integer, ArrayList<JSONObject>>();
    	int time = 0;
    	int longestDelivery = 0;
    	while (!allPackagesDelivered) {
    		
    		System.out.println("\n--- Time is: " + time + " ---");
    		
    		/**
    		 * Orders loop
    		 * While there are still orders, select the orders from the current time and do something with it
    		 */

    		while (ordersSorted.size() > 0 && ordersSorted.get(0).getTime() == time) {
    			// select the first order from the sorted list of orders
    			Order currentOrder = ordersSorted.get(0);
    			
    		// do something with the currentOrder -->
    			currentOrder.getOrigin().addOrder(currentOrder);
    			
    			//System.out.println("OrderID: " + currentOrder.getId()+ " dest: "+currentOrder.getDestination());
    			
    			LinkedList<Station> path = dijkstra.getAlphabeticalPath(currentOrder.getOrigin(), currentOrder.getDestination());
    			int deliveryTime = dijkstra.getTimeOfTrip(path);
    			//System.out.println("Length of Delivery: " + deliveryTime);
    			if(deliveryTime > longestDelivery) {
    				longestDelivery = deliveryTime;
    			}
    		// --<
    			
    			// remove the top order from the sorted order list
    			// this way you always have the latest order
    			ordersSorted.remove(0);
    		}

    		
    		/**
    		 * Robots loop
    		 */
    		for (Robot robot : robots) {
    			Station currentStation = robot.getCurrentLocation();
    			//System.out.println("\n"+robot
    			//		+ " Current Location: " + robot.getCurrentLocation()
    			//		+ " Capacity: " + robot.getCapacity());
    			
    			
    			// if the robot is unavailable (== travelling), then go to the next robot
    			if (robot.getNextAvailableTime() > time) {
    				//System.out.println("{" + robot + " is moving.}");
    				continue;
    			
    				// if the robot is available then start his work loop
    			} else {
    				
    				// check if at homebase
    				if (currentStation == robot.getHomebase()) {
    					//System.out.println(robot + " is at homebase.");
    					
    					// pick up packages while the robot has space and the location has packages
    					// random pick any package --> implement better algorithm later
    					ArrayList<Order> ordersToPick = new ArrayList<Order>();
    					if(currentStation.getOrders().size() > 0) {
    						while ((currentStation.getOrders().size() > 0) && (robot.getCapacity() > 0)) {
        						//-> might rethink hashset for storing orders
        						Set<Order> availableOrdersAtCurrentLocation = robot.getCurrentLocation().getOrders();
        						Iterator<Order> iterator = availableOrdersAtCurrentLocation.iterator();    			
        		    			Order orderToPick = (Order) iterator.next();
        		    			ordersToPick.add(orderToPick);
        		    			currentStation.removeOrder(orderToPick);
        		    			robot.decreaseCapacity();
        		    		}
        					robot.pickOrder(ordersToPick);

    					}   					
    					if ((robot.getCapacity()) == 10 && (currentStation.getOrders().size() == 0)) {
    						robot.increaseAvailableTime();
    						continue;
    					} else {
    						// if the robot has no more empty slots or there are no more packages available go forward
        					Set<Order> ordersOfThisRobot = robot.getOrders();
    						Iterator<Order> iterator = ordersOfThisRobot.iterator();    			
    						Order orderToDeliver = (Order) iterator.next();
    		    			robot.setNewDestination(orderToDeliver.getDestination());
    		    			robot.travelToNextDestination(dijkstra, ssr);
    		    			//System.out.println("Robot " + robot.getId() + " moves to " + robot.getCurrentLocation());

    					}
    					    					
    				} else { // the robot is not at his homebase
    					
    					// if the robot is at the next destination
    					if (currentStation == robot.getDestination()) {
    						robot.deliverOrders();
    					} else {
    						robot.travelToNextDestination(dijkstra, ssr);
    					}
    					//System.out.println("Robot " + robot.getId() + " is NOT at homebase.");
    					//System.out.println(robot.getNextAvailableTime());
    				}
    				
    			}
    			
    		}
    		numPackagesDelivered = 0;
        	for (Station station : ssr.allStationsList) {
        		numPackagesDelivered += station.getDeliveredOrders().size();
        	}
    		System.out.println("Delivered orders: " + numPackagesDelivered + " of " + numPackages);
    		//if (time == 100) {
    		if (numPackagesDelivered == numPackages) {
    			allPackagesDelivered = true;
    		}
    		
    		if (allPackagesDelivered == true) {
    			System.out.println("Total time: " + time);
    			 try (FileWriter file = new FileWriter("deliveryLog.jsonl")) {
    				 for (Robot robot : rr.getRobots().values()) {
    					 ArrayList<JSONObject> log = robot.getDeliveryLog();
    					 for (JSONObject logLine : log) {
    						 file.write(logLine.toJSONString() + "\n");
    						 
    					 }
    	    			}
    		        } catch (IOException e) {
    		            e.printStackTrace();
    		        }
    			
    			break;
    		}
    		time++;
    	}
/*    	System.out.println("\nLongestDelivery: "+longestDelivery);
    	System.out.println(allRobots.get(0).getOrders());
    	for (Order order : allRobots.get(0).getOrders()) {
    		System.out.println(order.getId() + " Dest: " + order.getDestination());
    	}
 */
    	
    	/*
    	 * Highscores:
    	 * 
    	 * Random Strategy (full order set) - Total time: 45327
    	 * Random Strategy (1000 order set) - Total time: 539
    	 */
	}

}
