import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Station implements Comparable<Station> {

	private String name;
	private List<Station> connectedStations;
	private SubwayLine line;
	private Map<Station, Integer> connectionTimes;
	private int stationID;
	private static int stationIDGen = 0;
	
	//the following are needed for the 
	private int timeFromSource = Integer.MAX_VALUE;
	private boolean visited;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	private ArrayList<Station> stationsFromSource = new ArrayList<Station>();
	
	public Station(String name) {
		this.name = name;
		connectedStations = new ArrayList<Station>();
		connectionTimes = new HashMap<Station, Integer>();
		this.stationID = stationIDGen;
		stationIDGen++;
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
	public ArrayList<Connection> getConnections(){
		ArrayList<Connection> connections = new ArrayList<Connection>();
		Iterator it = this.connectionTimes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Station, Integer> pair= (Map.Entry<Station, Integer>)it.next();
			Connection newConnection = new Connection(this, pair.getKey(),pair.getValue());
			connections.add(newConnection);
		}
		
		return connections;
	}
	
	public int getTimeToConnection(Station connectedStation) {
		return this.getConnectionTimes().get(connectedStation);
	}
	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
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
