import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShortestPaths {
	
	private static Graph graph;
	private static DijkstraAlgorithm dijkstra; 
	private static HashMap<Station, HashMap<Station, LinkedList<Station>>> allShortestPaths;
	private static HashMap<Station, HashMap<Station, Integer>> allShortestTimes;
	
	
	public ShortestPaths(SubwayStationsReader ssr) {
		
		graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	dijkstra = new DijkstraAlgorithm(graph);
    	
    	allShortestPaths = new HashMap<Station, HashMap<Station, LinkedList<Station>>>();
    	allShortestTimes = new HashMap<Station, HashMap<Station, Integer>>();
    	
    	List<Station> allStations = ssr.allStationsList;

    	for (Station origin : allStations) {
    		HashMap<Station, LinkedList<Station>> innerMap = new HashMap<Station, LinkedList<Station>>();		
    		HashMap<Station, Integer> innerMapTime = new HashMap<Station, Integer>();		
			
    		for (Station destination : allStations) {
    			if(origin != destination) {
    				LinkedList<Station> path = dijkstra.getAlphabeticalPath(origin, destination);
        			int time = dijkstra.getTimeOfTrip(path);
    				innerMap.put(destination, path);
    				innerMapTime.put(destination, time);
        			
          		}  			
    		}
    		allShortestPaths.put(origin, innerMap);
    		allShortestTimes.put(origin, innerMapTime);
    	}
    	System.out.println("Import complete.");
	}
	
	public static LinkedList<Station> getPath(Station origin, Station destination) {
		LinkedList<Station> shortestPath = allShortestPaths.get(origin).get(destination);		
		return shortestPath;
	}
	
	public static int getShortestTime(Station origin, Station destination) {
		LinkedList<Station> shortestPath = getPath(origin, destination);
		return dijkstra.getTimeOfTrip(shortestPath);
	}
	
	public static Station getClosestStation(Station origin) {
		int shortestTime = Integer.MAX_VALUE;
		Station closestStation = origin;
		HashMap<Station, Integer> shortestPath = allShortestTimes.get(origin);
		for (Station station: shortestPath.keySet()) {
			if (shortestPath.get(station) < shortestTime) {
				shortestTime = shortestPath.get(station);
				closestStation = station;
			}
		}
		return closestStation;
	}

	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");	
		
		ShortestPaths sp = new ShortestPaths(ssr);
		
		/*
		Station origin = ssr.allStations.get("Tribunal");
		Station destination = ssr.allStations.get("Sol");
		
		System.out.println(getPath(origin, destination));
		System.out.println(getShortestTime(origin, destination));
		*/
		
		/*
		 * Some statistics
		 */
		List<Station> allStations = ssr.allStationsList;
		int threshold = 10;
		int count = 0;
		for (Station orig : allStations) {
    		for (Station dest : allStations) {
    			if(dest != orig) {
    				if (getShortestTime(orig, dest) < threshold) {
        				System.out.print(getShortestTime(orig, dest) + " ");
        				System.out.println(getPath(orig, dest));
        				count++;
        			}
    			}
    						
    		}
    	}
		System.out.println("\nTotal paths over threshold: " + count + "/ 2352");
		
		Station arguelles  = ssr.allStations.get("Argüelles");
		Station sol  = ssr.allStations.get("Sol");
		Station diegoDeLeon  = ssr.allStations.get("Diego de León");
		Station plazaDeCastilla  = ssr.allStations.get("Plaza de Castilla");
		
		
		Station base = plazaDeCastilla;
		int count2 = 0;
		for (Station dest : allStations) {
			if(dest != base) {
				if (getShortestTime(base, dest) > 10) {
    				System.out.print(getShortestTime(base, dest) + " ");
    				System.out.println(getPath(base, dest));
    				count2++;
    			}
			}						
		}
		System.out.println(count2);
		
		/*
		 * Argüelles
		 * - 20 Stations over 10 mins away
		 * - 13 Stations over 15 mins away
		 * - 6 Stations over 20 mins away
		 * - 3 Stations over 25 mins away (Valdecarros, Villaverde Alto, Arganda de Rey)
		 * 
		 * Sol
		 * - 19 Stations over 10 mins away
		 * - 12 Stations over 15 mins away
		 * - 8 Stations over 20 mins away
		 * - 2 Stations over 25 mins away (Valdecarros, Aeropuerto T4)
		 * 
		 * Diego de Leon
		 * - 19 Stations over 10 mins away
		 * - 11 Stations over 15 mins away
		 * - 5 Stations over 20 mins away
		 * - 3 Stations over 25 mins away (Valdecarros, Villaverde Alto, Puerta del Sur
		 * 
		 * Plaza de Castilla
		 * - 29 Stations over 10 mins away
		 * - 15 Stations over 15 mins away
		 * - 7 Stations over 20 mins away
		 * - 5 Stations over 25 mins away (Valdecarros, Villaverde Alto, Oporto, Arganda de Rey, Puerta del Sur)
		 */
		Station valdecarros  = ssr.allStations.get("Valdecarros");
		System.out.println(allShortestPaths.get(valdecarros));
		
		System.out.println(getClosestStation(arguelles));
	}

}
