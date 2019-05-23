
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {

    private final List<Connection> connections;
    private Set<Station> settledStations;
    private Set<Station> unSettledStations;
    private Map<Station, Station> predecessors;
    private Map<Station, Integer> distance;

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.connections = new ArrayList<Connection>(graph.getConnections());
    }
    
    public Map<Station, Station> getPredecessors(){
    	return predecessors;
    }
    
    public void execute(Station source) {
    	settledStations = new HashSet<Station>();
    	unSettledStations = new HashSet<Station>();
        distance = new HashMap<Station, Integer>();
        predecessors = new HashMap<Station, Station>();
        distance.put(source, 0);
        unSettledStations.add(source);

        while (unSettledStations.size() > 0) {
        	Station node = getMinimum(unSettledStations);
        	settledStations.add(node);
        	unSettledStations.remove(node);
            findMinimalDistances(node);
        }
    }
    
    private void findMinimalDistances(Station station) {
        List<Station> adjacentStations = getNeighbors(station);
        for (Station targetStation : adjacentStations) {
            if (getShortestDistance(targetStation) > getShortestDistance(station)
                    + getDistance(station, targetStation)) {
                distance.put(targetStation, getShortestDistance(station)
                        + getDistance(station, targetStation));
                predecessors.put(targetStation, station);
                unSettledStations.add(targetStation);
            }
        }
    }
    
    private int getDistance(Station node, Station target) {
        for (Connection connection : connections) {
            if (connection.getOrigin().equals(node)
                    && connection.getDestination().equals(target)) {
                return connection.getTime();
            }
        }
        throw new RuntimeException("Should not happen");
    }
    
    private List<Station> getNeighbors(Station node) {
        List<Station> neighbors = new ArrayList<Station>();
        for (Connection connection : connections) {
            if (connection.getOrigin().equals(node)
                    && !isSettled(connection.getDestination())) {
                neighbors.add(connection.getDestination());
            }
        }
        return neighbors;
    }

    private Station getMinimum(Set<Station> stations) {
    	Station minimum = null;
        for (Station station : stations) {
            if (minimum == null) {
                minimum = station;
            } else {
                if (getShortestDistance(station) < getShortestDistance(minimum)) {
                    minimum = station;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Station station) {
        return settledStations.contains(station);
    }

    private int getShortestDistance(Station destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Station> getPath(Station target) {
        LinkedList<Station> path = new LinkedList<Station>();
        Station step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Calculates the time of a trip given by stations. 
     * Stations need to be in order.
     * @param path	A LinkedList of Stations.
     * @return	The total time of the trip.
     */
    public int getTimeOfTrip(LinkedList<Station> path) {
    	if(path == null) {
    		return 0;
    	}
    	int totalTime = 0;
    	for (int i=0;i<(path.size()-1);i++) {
    		Station currentStation = path.get(i);
    		Station nextStation = path.get(i+1);
    		totalTime += currentStation.getConnectionTimes().get(nextStation);
    	}
     	return totalTime;
    }
    /**
     * Returns the neighboring station with the shortest path to the destination.
     * If two paths have the same time, the return the next station in alphabetical order.
     * This method uses the original getPath()-method to calculate the shortest path
     * to the destination.
     * @param origin
     * @param destination
     * @return
     */
    public Station getNeighborWithShortestPathToTarget(Station origin, Station destination) {
    	boolean hasMultiplePaths = false;
    	this.execute(origin);
    	LinkedList<Station> path = this.getPath(destination);
    	if(path == null) {
    		return null;
    	}
        int shortestTimeToDestination = Integer.MAX_VALUE;
        Station returnStation = origin.getConnectedStations().get(0);
        
        ArrayList<Station> connectedStations = new ArrayList<Station>();
        //System.out.println("Connected Stations from "+origin+ " are: "+origin.getConnectedStations());
        //System.out.println(origin.getConnectedStations().size());
        for (int i=0;i<origin.getConnectedStations().size();i++) {
        	Station currentConnectedStation = origin.getConnectedStations().get(i);

        	int timeFromOriginToCurrent = origin.getConnectionTimes().get(currentConnectedStation);
        	//System.out.println("The time from "+origin+" to "+currentConnectedStation+" is "+timeFromOriginToCurrent);
        	this.execute(currentConnectedStation);
        	
        	LinkedList<Station> newPath = this.getPath(destination);
        	int timeFromCurrentConnectedToDestination;
        	if(newPath == null) {
        		timeFromCurrentConnectedToDestination = 0;
        	} else {
        		timeFromCurrentConnectedToDestination = getTimeOfTrip(newPath);
        		//timeFromCurrentConnectedToDestination = getTimeOfTrip(newPath.toArray(new Station[newPath.size()]));
        	}
        	
        	//System.out.println("The time from "+currentConnectedStation+" to "+destination+" is "+timeFromCurrentConnectedToDestination);
        	//System.out.println("Sum via this destination: "+timeFromOriginToCurrent +"+"+timeFromCurrentConnectedToDestination+"="+(timeFromOriginToCurrent+timeFromCurrentConnectedToDestination));
        	
        	// if the next station has a shorter time to destination, set this time as new lowest    	
        	if (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent < shortestTimeToDestination) {
        		shortestTimeToDestination = (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent);
        		connectedStations.clear();
        		connectedStations.add(currentConnectedStation);
        	} else if((timeFromCurrentConnectedToDestination+timeFromOriginToCurrent == shortestTimeToDestination)) {
        		connectedStations.add(currentConnectedStation);
        		hasMultiplePaths = true;
        		
        	}
        	
        	//System.out.println("Shortest Time is "+shortestTimeToDestination+ " stations are: "+connectedStations);
        	
        }
        
        if (hasMultiplePaths) {
        	//System.out.println("Has multiple Paths.");
        }
    	Collections.sort(connectedStations);
    	if(connectedStations.size() > 1) {
    //		System.out.println(origin + " is a crossroad. Possible paths from here: " + connectedStations 
    //				+ " Time from here to destination: "+shortestTimeToDestination);
    	}
    	returnStation = connectedStations.get(0);
        //System.out.println("Best connection is: "+returnStation+"\n");
        if (returnStation == destination) {
        	return null;
        } else {
        	return returnStation;
        }
        
    }
    /**
     * Calculates the shortest path from origin to destination.
     * Makes a recursive call to getNeighborWithShortestPathToTarget.
     * @param origin
     * @param destination
     * @return
     */
    public LinkedList<Station> getAlphabeticalPath(Station origin, Station destination){
    	LinkedList<Station> shortestPath = new LinkedList<Station>();
    	
    	// recursive function: find the next station in alphabetical order if
    	// two or more stations have the same path to the destination
    	while(origin != null) {
    		shortestPath.add(origin);
    		origin = getNeighborWithShortestPathToTarget(origin, destination);
    	}
    	shortestPath.add(destination);
    	return shortestPath;
    }
    
    /**
     * Sorts a HashMap by its values. From Highest to lowest.
     * @param mapToSort	The map to sort.
     * @return	A sorted map
     */
    public Map<Station, Integer> sortMap(Map<Station, Integer> mapToSort){
		Map<Station, Integer> sortedMap = new LinkedHashMap<>();
		mapToSort
				.entrySet()
				.stream()				
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		return sortedMap;
    }
    /**
     * Sorts a Hashmap by its values. From Highest to lowest.
     * Option to return only the top n entries.
     * @param mapToSort	The map to sort.
     * @param topN	The number of entries to return.
     * @return	A sorted map
     * 
     */
    public Map<Station, Integer> sortMap(Map<Station, Integer> mapToSort, int topN){
    	Map<Station, Integer> sortedMap = new LinkedHashMap<>();
		mapToSort
				.entrySet()
				.stream()				
				.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
				.limit(topN)
				.forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
		
		return sortedMap;
    }
    /**
     * Uses the List of all trips and calculates their respective shortest path.
     * Uncomment the println-statements if needed. 
     * @param allTrips
     * @param allStations
     * @return
     */
    public Map<Station, Integer> calculateAllPathsFromTrips(Map<Integer, List<String>> allTrips, Map<String, Station> allStations){
    	Map<Station, Integer> totalVisits = new HashMap<Station, Integer>();
    	
    	for (List<String> trip : allTrips.values()) {
			System.out.println("\nShortest trip from: "+trip.get(0) + ", to: "+trip.get(1));
			Station origin = allStations.get(trip.get(0));
			Station destination = allStations.get(trip.get(1));
			this.execute(origin);
	        	        
	        LinkedList<Station> path = this.getAlphabeticalPath(origin, destination);
	        System.out.println(path);
			System.out.println("Time of trip: "+this.getTimeOfTrip(path));
			
	        for (Station station : path) {	        	
	        	if(!totalVisits.containsKey(station)) {
	        		totalVisits.put(station, 1);
	        	} else {
	        		int count = totalVisits.get(station) + 1;
	        		totalVisits.put(station, count);
	        	}
	        }	        
	        //System.out.println(dijkstra.predecessors);	        
		}
    	return totalVisits;
    }

    
    public static void main(String[] args) {
		
    	/**
    	 * Import Data for Stations and Trips
    	 */
    	SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
    	Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);
    	DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
    	
    	TripReader tr = new TripReader("trip_records.json");
    	Map<Integer, List<String>> allTrips = tr.getTrips();
    	Map<String, Station> allStations = ssr.allStations;
    	
    	/**
    	 * Print all the paths for the visited Stations
    	 */
    	System.out.println("\n--- Trips summary: ---");   
    	Map<Station, Integer> totalVisits = dijkstra.calculateAllPathsFromTrips(allTrips, allStations);
    	
    	/**
    	 *  Print the top 10 elements of a sorted map
    	 */
    	System.out.println("\n\n\n--- Top 10 visited Stations: ---");    	
    	int topN = 10;
    	Map<Station, Integer> sortedMap = dijkstra.sortMap(totalVisits, topN);
    	for(Map.Entry<Station, Integer> entry : sortedMap.entrySet()) {
    		System.out.println(entry.getValue() + " - " + entry.getKey());
    		}
    }  
    }