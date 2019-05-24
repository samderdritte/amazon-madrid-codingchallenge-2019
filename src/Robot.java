import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.json.simple.JSONObject;

public class Robot {
	private int id;
	private Station currentLocation;
	private Station currentDestination;
	private Station homebase;
	private int capacity;
	private int nextAvailableTime;
	private int waitTime;
	private ArrayList<Order> orders;
	private ArrayList<JSONObject> deliveryLog;
	
	public Robot(int id, Station homebase, int capacity) {
		this.id = id;
		this.homebase = homebase;
		this.currentLocation = homebase;
		this.capacity = capacity;
		orders = new ArrayList<Order>();
		deliveryLog = new ArrayList<JSONObject>();
	}
	
	@Override
	public String toString() {
		String name = "Robot " + id;
		return name;
	}

	/*
	 * Getters and setters
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Station getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Station newLocation) {
		this.currentLocation = newLocation;
	}
	public void setNewDestination(Station newLocation) {
		this.currentDestination = newLocation;
	}
	public Station getHomebase() {
		return homebase;
	}	
	public Station getDestination() {
		return currentDestination;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public ArrayList<Order> getOrders(){
		return this.orders;
	}
	
	public int getNextAvailableTime() {
		return nextAvailableTime;
	}
	public ArrayList<JSONObject> getDeliveryLog(){
		return deliveryLog;
	}
	public int getWaitTime() {
		return waitTime;
	}
	public void increaseWaitTime() {
		waitTime++;
	}
	public void resetWaitTime() {
		waitTime = 0;
	}
	

	/*
	 * Additional methods
	 */
	public void increaseAvailableTime() {
		nextAvailableTime++;
	}
	public void decreaseCapacity() {
		capacity--;
	}
	public void decreaseCapacity(int capacity) {
		this.capacity -= capacity;
	}
	public void increaseCapacity() {
		capacity++;
	}
	public void increaseCapacity(int capacity) {
		this.capacity += capacity;
	}
	
	public void pickOrder(ArrayList<Order> newOrders, boolean debugMode) {
		
		for(Order order : newOrders) {
			orders.add(order);
		}
		int[] ordersArray = new int[newOrders.size()];
		for (int i = 0; i < newOrders.size(); i++) {
			ordersArray[i] = newOrders.get(i).getId();
		}
		
		if(debugMode) {
			System.out.println("{\"time\": " + nextAvailableTime 
					+ ",\"robot\":" + id 
					+ ",\"verb\":\"pick\",\"orders\":" + Arrays.toString(ordersArray) + "}");

		}
				JSONObject line = new JSONObject();
		line.put("time", nextAvailableTime);
		line.put("robot", id);
		line.put("verb", "pick");
		line.put("orders", Arrays.toString(ordersArray));
		deliveryLog.add(line);
		
		nextAvailableTime++;
	}
	
	public void deliverOrders(DijkstraAlgorithm dijkstra, SubwayStationsReader ssr, boolean debugMode) {
		ArrayList<Order> deliveredOrders = new ArrayList<Order>();
		Collection<Order> ordersToRemove = new LinkedList<Order>(deliveredOrders);
		for (Order order : orders) {
			if(order.getDestination() == currentLocation) {
				currentLocation.addDeliveredOrder(order);
				deliveredOrders.add(order);
				ordersToRemove.add(order);
				this.increaseCapacity();
			}
		}
		orders.removeAll(ordersToRemove);
		int[] ordersArray = new int[deliveredOrders.size()];
		for (int i = 0; i < deliveredOrders.size(); i++) {
			ordersArray[i] = deliveredOrders.get(i).getId();
		}
		if(debugMode) {
			System.out.println("{\"time\": " + nextAvailableTime + ",\"robot\":" + id + ",\"verb\":\"drop\",\"orders\":" + Arrays.toString(ordersArray) + "}");
			
		}
		JSONObject line = new JSONObject();
		line.put("time", nextAvailableTime);
		line.put("robot", id);
		line.put("verb", "drop");
		line.put("orders", Arrays.toString(ordersArray));
		deliveryLog.add(line);
		
		if(orders.size() > 0) {
			currentDestination = orders.get(0).getDestination();
		} else {
			currentDestination = getClosestHomebase(dijkstra, ssr);
			homebase = currentDestination;
		}
		nextAvailableTime++;
	}
	
	public void travelToNextDestination(DijkstraAlgorithm dijkstra, SubwayStationsReader ssr, boolean debugMode) {
		
		Station destination =  currentDestination;
		Station origin = currentLocation;	
    	
    	LinkedList<Station> path2 = dijkstra.getAlphabeticalPath(origin, destination);
    	
    	Station nextStation = path2.get(1);
    	if(debugMode) {
    		System.out.println("{\"time\": " + nextAvailableTime + ",\"robot\":" + id + ",\"verb\":\"go\",\"line\":" + "\""+origin.getLineForConnection(nextStation, ssr)+"\",\"station\":" + nextStation +"}");
        	
    	}
    	JSONObject line = new JSONObject();
		line.put("time", nextAvailableTime);
		line.put("robot", id);
		line.put("verb", "go");
		line.put("line", origin.getLineForConnection(nextStation, ssr));
		line.put("station", nextStation);
		deliveryLog.add(line);
    	int timeToNextStation = origin.getTimeToConnection(nextStation);
    	//System.out.println("time to next" + timeToNextStation);
    	nextAvailableTime += timeToNextStation;
    	currentLocation = nextStation;   	
	}
	
	public Station getClosestHomebase(DijkstraAlgorithm dijkstra, SubwayStationsReader ssr) {
		ArrayList<Station> homebases = new ArrayList<Station>();
		homebases.add(ssr.allStations.get("Argüelles"));
		homebases.add(ssr.allStations.get("Plaza de Castilla"));
		homebases.add(ssr.allStations.get("Diego de León"));
		homebases.add(ssr.allStations.get("Sol"));
		int distanceToNextBase = Integer.MAX_VALUE;
		Station closestBase = this.homebase;
		for (Station station: homebases) {
			if(station.getOrders().size() == 0) {
				continue;
			}
			if(currentLocation == station) {
				return station;
			}
			LinkedList<Station> path = dijkstra.getAlphabeticalPath(currentLocation, station);
			if (dijkstra.getTimeOfTrip(path) < distanceToNextBase) {
				distanceToNextBase = dijkstra.getTimeOfTrip(path);
				closestBase = station;
			}
		}		
		return closestBase;
	}
}
