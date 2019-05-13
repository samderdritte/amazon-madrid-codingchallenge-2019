import java.util.HashMap;
import java.util.Map;

public class SubwayLine {
	private String name;
	private Map<String, Integer> stations;
	
	public SubwayLine(String name) {
		this.name = name;
		stations = new HashMap<String, Integer>();
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, Integer>  getStations() {
		return stations;
	}
	
	public void addStation(String name, Integer time) {
		if(!stations.containsKey(name)) {
			stations.put(name, time);
		}		
	}
	
	public int getTimeBetweenStations(String depStation, String arrivalStation) {
		
		// algorithm sketch
		// create an arraylist of unvisited stations
		// a) check all neighboring stations from the starting 
		// if the station has not yet been visited, then add distance from outgoing to arrival to the new station
		//
		
		int dep = stations.get(depStation);
		int arr = stations.get(arrivalStation);
		
		if (dep > arr) {
			return dep - arr;
		} else {
			return arr - dep;
		}
		
	}
}
