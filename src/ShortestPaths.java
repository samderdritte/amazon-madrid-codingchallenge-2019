import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShortestPaths {
	
	private static Graph graph;
	private static DijkstraAlgorithm dijkstra; 
	private static HashMap<Station, HashMap<Station, LinkedList<Station>>> allShortestPaths;
	
	public ShortestPaths(SubwayStationsReader ssr) {
		
		graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	dijkstra = new DijkstraAlgorithm(graph);
    	
    	allShortestPaths = new HashMap<Station, HashMap<Station, LinkedList<Station>>>();
    	
    	List<Station> allStations = ssr.allStationsList;

    	for (Station origin : allStations) {
    		HashMap<Station, LinkedList<Station>> innerMap = new HashMap<Station, LinkedList<Station>>();		
			
    		for (Station destination : allStations) {
    			if(origin != destination) {
    				LinkedList<Station> path = dijkstra.getAlphabeticalPath(origin, destination);
        			innerMap.put(destination, path);
        			
          		}  			
    		}
    		allShortestPaths.put(origin, innerMap);
    	}  	
	}
	
	public static LinkedList<Station> getPath(Station origin, Station destination) {
		LinkedList<Station> shortestPath = allShortestPaths.get(origin).get(destination);		
		return shortestPath;
	}
	
	public static int getShortestTime(Station origin, Station destination) {
		LinkedList<Station> shortestPath = getPath(origin, destination);
		return dijkstra.getTimeOfTrip(shortestPath);
	}

	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");	
		
		ShortestPaths sp = new ShortestPaths(ssr);
		
		
		Station origin = ssr.allStations.get("Tribunal");
		Station destination = ssr.allStations.get("Sol");
		
		System.out.println(getPath(origin, destination));
		System.out.println(getShortestTime(origin, destination));
	}

}
