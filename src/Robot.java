import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Robot {
	private int id;
	private Station currentLocation;
	private Station currentDestination;
	private Station homebase;
	private int capacity;
	private int nextAvailableTime;
	private Set<Order> orders;
	
	public Robot(int id, Station homebase, int capacity) {
		this.id = id;
		this.homebase = homebase;
		this.currentLocation = homebase;
		this.capacity = capacity;
		orders = new HashSet<Order>();
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
	
	public Set<Order> getOrders(){
		return this.orders;
	}
	
	public int getNextAvailableTime() {
		return nextAvailableTime;
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
	
	public void pickOrder(ArrayList<Order> newOrders) {
		
		for(Order order : newOrders) {
			orders.add(order);
		}
		int[] ordersArray = new int[newOrders.size()];
		for (int i = 0; i < newOrders.size(); i++) {
			ordersArray[i] = newOrders.get(i).getId();
		}
		
		System.out.println("{\"time\": " + nextAvailableTime 
				+ ",\"robot\":" + id 
				+ ",\"verb\":\"pick\",\"orders\":" + Arrays.toString(ordersArray) + "}");
		nextAvailableTime++;
	}
	
	public void deliverOrders() {
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
		System.out.println("{\"time\": " + nextAvailableTime + ",\"robot\":" + id + ",\"verb\":\"drop\",\"orders\":" + Arrays.toString(ordersArray) + "}");
		
		currentDestination = homebase;
		nextAvailableTime++;
	}
	
	public void travelToNextDestination(DijkstraAlgorithm dijkstra, SubwayStationsReader ssr) {
		
		Station destination =  currentDestination;
		Station origin = currentLocation;	
    	
    	LinkedList<Station> path2 = dijkstra.getAlphabeticalPath(origin, destination);
    	
    	Station nextStation = path2.get(1);
    	System.out.println("{\"time\": " + nextAvailableTime + ",\"robot\":" + id + ",\"verb\":\"go\",\"line\":" + "\""+origin.getLineForConnection(nextStation, ssr)+"\",\"station\":" + nextStation +"}");
    	int timeToNextStation = origin.getTimeToConnection(nextStation);
    	//System.out.println("time to next" + timeToNextStation);
    	nextAvailableTime += timeToNextStation;
    	currentLocation = nextStation;
    	
	}
}
