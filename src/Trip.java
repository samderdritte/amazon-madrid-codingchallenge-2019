import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Trip {

	private Station origin;
	private Station destination;
	private SubwayStationsReader ssr;
	
	public Trip(String origin, String destination) {

		ssr = new SubwayStationsReader("metro_lines.json");
		this.origin = ssr.allStations.get(origin);
		this.destination = ssr.allStations.get(destination);
	}
	
	public Map<Station, Integer> calculatePaths() {

		return origin.getConnectionTimes();
	}
	
	public void printAllPaths(Station origin, Station destination) {
		int numStations = ssr.allStations.size();
		boolean[] isVisited = new boolean[numStations+1];
		ArrayList<Station> pathList = new ArrayList<>(); 
		
		//add source to path[] 
        pathList.add(origin); 
          
        //Call recursive utility 
        printAllPathsUtil(origin, destination, isVisited, pathList);
	}
	
	//A recursive function to print all paths from "origin" to "destination"
	private void printAllPathsUtil(Station origin, Station destination, boolean[] isVisited, List<Station> localPathList) {
		
		//mark the current node
		isVisited[origin.getStationID()] = true;
		
		if (origin.equals(destination)) {
			System.out.println(localPathList);
			// if match found then no need to traverse more till depth
			isVisited[origin.getStationID()] = false;
			return;
		}
		
		for (Station i : origin.getConnections()) {
			if (!isVisited[i.getStationID()]) {
				// store current node  
                // in path[]
				localPathList.add(i);
				printAllPathsUtil(i,destination,isVisited,localPathList);
				
				// remove current node 
                // in path[] 
                localPathList.remove(i); 
			}
		}
		
		// Mark the current node 
        isVisited[origin.getStationID()] = false; 
	}
	
	public static void main(String[] args) {
		
		//SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
		
		//ssr.allStations.get("Acacias").getStationID();
		Trip trip1 = new Trip("Acacias", "Gran Vía");
		
		Station originStation = trip1.origin;
		Station destStation = trip1.destination;
		
		System.out.println(originStation);
		System.out.println(destStation);
		
		System.out.println(trip1.calculatePaths());
		
		System.out.println(originStation.getStationID());
		System.out.println(destStation.getStationID());
		
		//System.out.println(ssr.allStations.size());
		trip1.printAllPaths(originStation, destStation);
	}
}
