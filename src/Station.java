import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Station implements Comparable<Station> {

	private String name;
	private List<Station> connectedStations;
	private SubwayLine line;
	private Map<Station, Integer> connectionTimes;
	private int stationID;
	private static int stationIDGen = 0;
	
	private Set<Order> ordersAtThisStation;
	private Set<Order> deliveredOrders;
	private ArrayList<Order> listOfOrdersAtThisStation;
	
	//the following are needed for the dijkstra algorithm
	private int timeFromSource = Integer.MAX_VALUE;
	private boolean visited;
	private ArrayList<Station> stationsFromSource;
	
	public Station(String name) {
		this.name = name;
		connectedStations = new ArrayList<Station>();
		connectionTimes = new HashMap<Station, Integer>();
		stationsFromSource = new ArrayList<Station>();
		this.stationID = stationIDGen;
		stationIDGen++;
		
		ordersAtThisStation = new HashSet<Order>();
		deliveredOrders = new HashSet<Order>();
		listOfOrdersAtThisStation = new ArrayList<Order>();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	public int getStationID() {
		return stationID;
	}
	
	public List<Station> getConnectedStations(){
		return connectedStations;
	}
	
	public Map<Station, Integer> getConnectionTimes(){
		return connectionTimes;
	}
	
	public SubwayLine getLine() {
		return line;
	}
	
	public Set<Order> getOrders(){
		return ordersAtThisStation;
	}
	
	public void removeOrder(Order order) {
		ordersAtThisStation.remove(order);
	}
	public void addOrder(Order order) {
		ordersAtThisStation.add(order);
	}
	
	public ArrayList<Order> getOrderList(){
		for (Order order : ordersAtThisStation) {
			listOfOrdersAtThisStation.add(order);
		}
		return listOfOrdersAtThisStation;
	}
	public Set<Order> getDeliveredOrders(){
		return deliveredOrders;
	}
	public void addDeliveredOrder(Order order) {
		deliveredOrders.add(order);
	}
	
	public void addConnectedStation(Station name) {
		if(!connectedStations.contains(name)) {
			connectedStations.add(name);
		}		
	}
	
	public void addConnectionTime(Station connection, int time) {
		if(!connectionTimes.containsKey(connection)) {
			connectionTimes.put(connection, Math.abs(time));
		}				
	}
	
	public void setLine(SubwayLine name) {
		line = name;
	}
	
	//the following are needed for the edges
	public int getTimeFromSource() {
		return timeFromSource;
	}
	public void setTimeFromSource(int timeFromSource) {
		this.timeFromSource = timeFromSource;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public ArrayList<Connection> getConnections(SubwayStationsReader ssr){
		ArrayList<Connection> connections = new ArrayList<Connection>();
		for(Connection connection : ssr.allConnections) {
			if (connection.getOrigin() == this) {
				connections.add(connection);
			}
		}
	/*	Iterator<Entry<Station, Integer>> it = this.connectionTimes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Station, Integer> pair= (Map.Entry<Station, Integer>)it.next();
			Connection newConnection = new Connection(this, pair.getKey(),pair.getValue());
			connections.add(newConnection);
		}
	*/
		return connections;
	}
	public String getLineForConnection(Station connectedStation, SubwayStationsReader ssr) {
		String line = "";
		ArrayList<Connection> connections = getConnections(ssr);
		for (Connection connection : connections) {
			if (connection.getOrigin() == this && connection.getDestination() == connectedStation) {
				line = connection.getLine();
			}
		}
		return line;
	}
	
	public int getTimeToConnection(Station connectedStation) {
		return this.getConnectionTimes().get(connectedStation);
	}
	
	public ArrayList<Station> getStationsFromSource(){
		return stationsFromSource;
	}
	public void addStationFromSource(Station station) {
		this.stationsFromSource.add(station);
	}
	public void setStationsFromSource(ArrayList<Station> stations) {
		this.stationsFromSource = stations;
	}
	
	@Override
	public int compareTo(Station comparedStation) {
	    return this.getName().compareTo(comparedStation.getName());
	}
}
