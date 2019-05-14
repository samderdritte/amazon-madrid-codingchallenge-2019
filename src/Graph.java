import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Graph {

	private List<Connection> connections;
	private int numOfConnections;
	private List<Station> stations;
	private int numOfStations;
	
	public List<Connection> getConnections(){
		return connections;
	}
	public int getNumOfConnections() {
		return numOfConnections;
	}
	public List<Station> getStations() {
		return stations;
	}
	public int getNumOfStations() {
		return numOfStations;
	}
	
	public Graph(List<Connection> connections, List<Station> stations) {
		this.connections = connections;
		this.stations = stations;
		this.numOfConnections = connections.size();
		this.numOfStations = stations.size();		
	}
	

	// Implement the Dijkstra algorithm
	public void calculateShortestDistances(Station origin) {
		// originID as source
		int originID = origin.getStationID();
		this.stations.get(originID).setTimeFromSource(0);
		ArrayList<Station> stationsFromSource = new ArrayList<Station>();
		
		stationsFromSource.add(stations.get(originID));
		this.stations.get(originID).setStationsFromSource(stationsFromSource);
		
		int nextStation = originID;
		
		// visit every node
		for (int i = 0; i < this.stations.size(); i++) {
			// loop around the Connections of current Station
			ArrayList<Connection> currentStationConnections = this.stations.get(nextStation).getConnections();
			for (int joinedConnection = 0; joinedConnection < currentStationConnections.size(); joinedConnection++) {
				int neighbourIndex = currentStationConnections.get(joinedConnection).getConnectedStation(nextStation);
				// only if not visited
				if (!this.stations.get(neighbourIndex).isVisited()) {
					int tentative = this.stations.get(nextStation).getTimeFromSource() + currentStationConnections.get(joinedConnection).getTime();
					if (tentative < stations.get(neighbourIndex).getTimeFromSource()) {
						stations.get(neighbourIndex).setTimeFromSource(tentative);
						//stations.get(neighbourIndex).setStationsFromSource(currentStationConnections.get(joinedConnection).getOrigin().getStationsFromSource());
					}
				}
			}
			// all neighbours checked so Station visited
			stations.get(nextStation).setVisited(true);
			// next Station must be with shortest distance
			nextStation = getStationShortestDistanced();
	    }
	}
	    
	private int getStationShortestDistanced() {
		int storedStationIndex = 0;
	    int storedTime = Integer.MAX_VALUE;
	    for (int i = 0; i < this.stations.size(); i++) {
	    	int currentTime = this.stations.get(i).getTimeFromSource();
	    	if (!this.stations.get(i).isVisited() && currentTime < storedTime) {
	    		storedTime = currentTime;
	    		storedStationIndex = i;
	    	}
	   	}
	   	return storedStationIndex;
	}

	// display result
	public void printResult(Station origin, Station destination) {
		int originID = origin.getStationID();
		int destinationID = destination.getStationID();
		String output = "Number of Stations = " + this.numOfStations;
		output += "\nNumber of Connections = " + this.numOfConnections;
		
		output += ("\nThe shortest time from Station "+stations.get(originID).getName()+" to Station " + stations.get(destinationID).getName() + " is " + stations.get(destinationID).getTimeFromSource());

		output += ("\n" + stations.get(originID).getStationsFromSource());
	System.out.println(output);
	}
	
	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
		
		ArrayList<Station> allStations = new ArrayList<Station>();
		
		
		Graph gr = new Graph(ssr.allConnections, ssr.allStationsList);
		
		System.out.println(gr.getNumOfConnections());
		System.out.println(gr.getNumOfStations());
		System.out.println(gr.getStations());
		System.out.println(gr.getStations().get(48).getStationID());
		for (int i = 0; i<gr.getStations().size(); i++) {
			System.out.println(i + ": "+gr.getStations().get(i) + " - "+gr.getStations().get(i).getStationID());
		}
		
		System.out.println(gr.getStations().get(0));
		
		Station valdecarros = gr.getStations().get(0);
		Station Mirasierra = gr.getStations().get(45);
		
		gr.calculateShortestDistances(valdecarros);
		
		gr.printResult(valdecarros, Mirasierra);
	}
	
}
