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
	
	public static ArrayList<Robot> setupRobots(String robotsFilename, SubwayStationsReader ssr) {
		
		RobotReader rr = new RobotReader(robotsFilename, ssr);
		
		Map<Integer, Robot> allRobots = rr.getRobots();
    	
    	ArrayList<Robot> robots = new ArrayList<Robot>();
    	int numRobots = allRobots.size();
    	for (int i=0;i < numRobots;i++) {
    		robots.add(allRobots.get(i));
    	}
    	return robots;
	}
	
	/**
	 * Returns an Arraylist of all the orders sorted by order ID
	 * @param ordersFilename
	 * @param ssr
	 * @return
	 */
	public static ArrayList<Order> sortOrders(String ordersFilename, SubwayStationsReader ssr){		
		OrdersReader or = new OrdersReader(ordersFilename, ssr);
		ArrayList<Order> ordersSorted = new ArrayList<Order>();
    	for (int i = 0; i<or.getOrders().size();i++) {
    		ordersSorted.add(or.getOrders().get(i));
    	}    	
    	return ordersSorted;
	}
	
	/**
	 * Algorithm for the selection of orders for the cluster strategy
	 * @param currentStation
	 * @param dijkstra
	 * @return
	 */
	public static ArrayList<Order> selectOrdersForLoading(Robot robot, Station currentStation, DijkstraAlgorithm dijkstra){
		// determine which orders should be loaded in this robot
		Set<Order> availableOrders = currentStation.getOrders();
		ArrayList<Order> ordersForLoading = new ArrayList<Order>();
		
		
		int longestDistance = 0;
		Order furthestOrder = currentStation.getOrderList().get(0);
		Station furthestDestination = currentStation;
		
		/*
		 * Check if the robot already has orders
		 */
		if (robot.getOrders().size() > 0) { // robot already has orders
			furthestDestination = robot.getOrders().get(0).getDestination();
			LinkedList<Station> path = dijkstra.getAlphabeticalPath(currentStation, furthestDestination);
			longestDistance = dijkstra.getTimeOfTrip(path);
		} else { // robot is empty
			int loopLimit = 0;
			for (Order order : availableOrders) {		
				if (loopLimit < 10) {
					Station origin = currentStation;
			    	Station destination = order.getDestination();
			    	LinkedList<Station> path = dijkstra.getAlphabeticalPath(origin, destination);
			    	if(dijkstra.getTimeOfTrip(path) > longestDistance) {
			    		longestDistance = dijkstra.getTimeOfTrip(path);
			    		furthestDestination = order.getDestination();
			    		furthestOrder = order;
			    	}
				} else {
					break;
				}
		    	loopLimit++;
			}
			ordersForLoading.add(furthestOrder);
			availableOrders.remove(furthestOrder);
		}
		/*
		 * Get all orders within a certain range of the furthest Order
		 */
		// This is something to play with
		int timeThresholdForCluster = 1;
		if (longestDistance < 5) {
			timeThresholdForCluster = 2;
		} else if (longestDistance < 15) {
			timeThresholdForCluster = 5;
		} else if (longestDistance < 25) {
			timeThresholdForCluster = 10;
		} else if (longestDistance < 35) {
			timeThresholdForCluster = 15;
		} else {
			timeThresholdForCluster = 20;
		}
		
		int loopLimit = 0;
		for (Order order : availableOrders) {
			if (loopLimit < 10) {
				Station origin = furthestDestination;
				Station destination = order.getDestination();
				if (origin == destination) {
					ordersForLoading.add(order);
					continue;
				}
				LinkedList<Station> path = dijkstra.getAlphabeticalPath(origin, destination);
				if (dijkstra.getTimeOfTrip(path) <= timeThresholdForCluster) {
					ordersForLoading.add(order);
				}
			} else {
				break;
			}			
		}
		
		// TODO determine a logic for the selection of the route
		/*
		 * 1. check if the robot already has a longest order
		 * if yes, save the farthest order
		 * else get the order with the furthest distance
		 * 2. get all the orders within a certain range
		 */
		
		return ordersForLoading;
	}
	
	public static void randomStrategy(SubwayStationsReader ssr, String robotsFilename, String ordersFilename, boolean debugMode) {
		
		Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);

    	ArrayList<Order> ordersSorted = sortOrders(ordersFilename, ssr);
    	
    	ArrayList<Robot> robots = setupRobots(robotsFilename, ssr);
    	
    	boolean allPackagesDelivered = false;
    	int numPackages = ordersSorted.size();
    	int numPackagesDelivered = 0;
    	int time = 0;
    	
    	while (!allPackagesDelivered) {
    		
    		System.out.println("\n--- Time is: " + time + " ---");
    		
    		/**
    		 * Orders loop
    		 * While there are still orders, select the orders from the current time and do something with it
    		 */

    		while (ordersSorted.size() > 0 && ordersSorted.get(0).getTime() == time) {
    			// select the first order from the sorted list of orders
    			Order currentOrder = ordersSorted.get(0);
    			
    			// add the order to the Station of origin
    			currentOrder.getOrigin().addOrder(currentOrder);
    			
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
    				if(debugMode) {
    					System.out.println("{" + robot + " is moving.}");
    				}
    				
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
        					robot.pickOrder(ordersToPick, debugMode);

    					}   					
    					if ((robot.getCapacity()) == 10 && (currentStation.getOrders().size() == 0)) {
    						robot.increaseAvailableTime();
    						continue;
    					} else {
    						// if the robot has no more empty slots or there are no more packages available go forward
    						ArrayList<Order> ordersOfThisRobot = robot.getOrders();
    						Iterator<Order> iterator = ordersOfThisRobot.iterator();    			
    						Order orderToDeliver = (Order) iterator.next();
    		    			robot.setNewDestination(orderToDeliver.getDestination());
    		    			robot.travelToNextDestination(dijkstra, ssr, debugMode);
    		    			//System.out.println("Robot " + robot.getId() + " moves to " + robot.getCurrentLocation());

    					}
    					    					
    				} else { // the robot is not at his homebase
    					
    					// if the robot is at the next destination
    					if (currentStation == robot.getDestination()) {
    						robot.deliverOrders(dijkstra, ssr, debugMode);
    					} else {
    						robot.travelToNextDestination(dijkstra, ssr, debugMode);
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
    				 for (Robot robot : robots) {
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
		
	}

	public static void clusterStrategy(SubwayStationsReader ssr, String robotsFilename, String ordersFilename, boolean debugMode) {
		
		Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);

    	ArrayList<Order> ordersSorted = sortOrders(ordersFilename, ssr);
    	
    	ArrayList<Robot> robots = setupRobots(robotsFilename, ssr);
    	
    	boolean allPackagesDelivered = false;
    	int numPackages = ordersSorted.size();
    	int numPackagesDelivered = 0;
    	int time = 0;
    	
    	while (!allPackagesDelivered) {
    		
    		System.out.println("\n--- Time is: " + time + " ---");
    		
    		/**
    		 * Orders loop
    		 * While there are still orders, select the orders from the current time and do something with it
    		 */

    		while (ordersSorted.size() > 0 && ordersSorted.get(0).getTime() == time) {
    			// select the first order from the sorted list of orders
    			Order currentOrder = ordersSorted.get(0);
    			
    			// add the order to the Station of origin
    			currentOrder.getOrigin().addOrder(currentOrder);   			
    			
    			// remove the top order from the sorted order list
    			// this way you always have the latest order
    			ordersSorted.remove(0);
    		}
    		
    		/**
    		 * Robot loop
    		 */
    		
    		// select only one robot
    		//Robot robot0 = robots.get(0);
    		//robots = new ArrayList<Robot>();
    		//robots.add(robot0);
    		
    		for (Robot robot : robots) {
    			Station currentStation = robot.getCurrentLocation();
    			//System.out.println("\n"+robot
    			//		+ " Current Location: " + robot.getCurrentLocation()
    			//		+ " Capacity: " + robot.getCapacity());
    			
    			
    			// if the robot is unavailable (== travelling), then go to the next robot
    			if (robot.getNextAvailableTime() > time) {
    				if(debugMode) {
    					System.out.println("{" + robot + " is moving.}");
    				}   				
    				continue;
    			
    			// if the robot is available then start his work loop
    			} else {
    				
    				// check if at homebase
    				if (currentStation == robot.getHomebase()) {
    					//System.out.println(robot + " is at homebase.");
    					
    					// pick up packages while the robot has space and the location has packages
    					// random pick any package --> implement better algorithm later
    					ArrayList<Order> ordersToPick = new ArrayList<Order>();
    					ArrayList<Order> orderForLoading = new ArrayList<Order>();
    					if(currentStation.getOrders().size() > 0) {
    				
    						orderForLoading = selectOrdersForLoading(robot, currentStation, dijkstra);
    						if(orderForLoading.size() == 0) {
    							if(robot.getWaitTime() > 5) {
        							System.out.println(robot + " has waited too long. (" + robot.getWaitTime() + " minutes)");
        							robot.resetWaitTime();
        							Order orderToDeliver = robot.getOrders().get(0);
        							robot.setNewDestination(orderToDeliver.getDestination());
            		    			robot.travelToNextDestination(dijkstra, ssr, debugMode);
        						} else {
        							robot.increaseAvailableTime();
        							robot.increaseWaitTime();
            						continue;
        						}
    							
    						}
    						while ((orderForLoading.size() > 0) && (robot.getCapacity() > 0)) {
        						Iterator<Order> iterator = orderForLoading.iterator();    			
        		    			Order orderToPick = (Order) iterator.next();
        		    			ordersToPick.add(orderToPick);
        		    			currentStation.removeOrder(orderToPick);
        		    			orderForLoading.remove(orderToPick);
        		    			robot.decreaseCapacity();
        		    		}
        					robot.pickOrder(ordersToPick, debugMode);
        					

    					}
    					if ((robot.getCapacity() > 4)) {
    						if(robot.getWaitTime() > 5) {
    							System.out.println(robot + " has waited too long. (" + robot.getWaitTime() + " minutes)");
    							robot.resetWaitTime();
    							if(robot.getCurrentLocation().getOrders().size() == 0) {
    								Station newDestination = robot.getClosestHomebase(dijkstra, ssr);
    								robot.setNewDestination(newDestination);
    							} else {
    								Order orderToDeliver = robot.getOrders().get(0);
        							robot.setNewDestination(orderToDeliver.getDestination());
            		    			robot.travelToNextDestination(dijkstra, ssr, debugMode);
    							}
    							
    						} else {
    							robot.increaseAvailableTime();
    							robot.increaseWaitTime();
        						continue;
    						}					
    					}
    					if ((robot.getCapacity()) == 10 && (orderForLoading.size() == 0)) {
    						robot.increaseAvailableTime();
    						robot.increaseWaitTime();
    						continue;
    					} else {
    						robot.resetWaitTime();
    						// if the robot has no more empty slots or there are no more packages available go forward
    						ArrayList<Order> ordersOfThisRobot = robot.getOrders();
    						int closestDistance = Integer.MAX_VALUE;
    						Order closestOrder = ordersOfThisRobot.get(0);
    						for (Order order : ordersOfThisRobot) {
    							LinkedList<Station> path = dijkstra.getAlphabeticalPath(currentStation, order.getDestination());
    							if(dijkstra.getTimeOfTrip(path) < closestDistance) {
    								closestDistance = dijkstra.getTimeOfTrip(path);
    								closestOrder = order;
    							}
    						} 			
    						Order orderToDeliver = closestOrder;
    		    			robot.setNewDestination(orderToDeliver.getDestination());
    		    			robot.travelToNextDestination(dijkstra, ssr, debugMode);
    		    			//System.out.println("Robot " + robot.getId() + " moves to " + robot.getCurrentLocation());

    					}
    					    					
    				} else { // the robot is not at his homebase
    					
    					// if the robot is at the next destination
    					if (currentStation == robot.getDestination()) {
    						robot.deliverOrders(dijkstra, ssr, debugMode);
    					} else {
    						robot.travelToNextDestination(dijkstra, ssr, debugMode);
    					}
    					//System.out.println("Robot " + robot.getId() + " is NOT at homebase.");
    					//System.out.println(robot.getNextAvailableTime());
    				}
    				
    			}   			
    		}
    		
    		/**
    		 * End of round
    		 * - check if all packages are delivered
    		 * - end or continue
    		 */
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
    				 for (Robot robot : robots) {
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
    		
    		/*
    		ArrayList<Station> homebases = new ArrayList<Station>();
    		homebases.add(ssr.allStations.get("Argüelles"));
    		homebases.add(ssr.allStations.get("Plaza de Castilla"));
    		homebases.add(ssr.allStations.get("Diego de León"));
    		homebases.add(ssr.allStations.get("Sol"));

    		for (Station station: homebases) {
    			System.out.println("Base: " + station + " has " + station.getOrders().size() + " orders available");
    		}
    		*/	
    	}
    		
    		
    		
    		
	}
	
	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
    	     	    	
		String robotsFile = "robots.jsonl";
		
		/*
		 * Choose one of the order set files below
		 */
		//String ordersFile = "orders_small.jsonl";
		String ordersFile = "orders.jsonl";
    	
		/*
    	 * Choose one of the following strategies
    	 * true/false for debugMode (verbose printouts in the console if true)
    	 */
		
    	//randomStrategy(ssr, robotsFile, ordersFile, true);
    	clusterStrategy(ssr, robotsFile, ordersFile, false);
    	
    	
    	/*******
    	 * 
    	 * Strategy Highscores:
    	 * 
    	 * randomStrategy (orders.jsonl) - 			Total time: 45327
    	 * randomStrategy (orders_small.jsonl) -	Total time: 556
    	 *
    	 * clusterStrategy (orders.jsonl) - 		Total time: 
    	 * (algorithm not efficient on the large data set, gets
    	 * the job done, but takes very long to execute)
    	 * clusterStrategy (orders_small.jsonl) - 	Total time: 151
    	 * 
    	 ******/
	}

}
