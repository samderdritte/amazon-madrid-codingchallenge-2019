import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DijkstraAlgorithm {

	private final List<Station> stations;
    private final List<Connection> connections;
    private Set<Station> settledStations;
    private Set<Station> unSettledStations;
    private Map<Station, Station> predecessors;
    private Map<Station, Integer> distance;

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.stations = new ArrayList<Station>(graph.getStations());
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
    
    public int getTimeOfTrip(Station[] path) {
    	if(path == null) {
    		return 0;
    	}
    	int totalTime = 0;
    	Station first = path[0];
    	Station last = path[path.length-1];
    	//System.out.println(path.length);
    	for (int i=0;i<(path.length-1);i++) {
    		//System.out.println(i);
    		Station currentStation = path[i];
    		Station nextStation = path[(i+1)];
    		totalTime += currentStation.getConnectionTimes().get(nextStation);
    	}
    	//System.out.println("The total trip lenght from " +first+ " to " +last+ ": " +totalTime +"\n");
    	return totalTime;
    }
    
    public Station[] calculateAlternativeTrip(Station[] trip) {
        
    	// shortest path is given by the length of the trip
		int shortestPath = trip.length;
		int shortestTripLength = getTimeOfTrip(trip);
		
		// create a new trip of the same length
		Station[] updatedTrip = new Station[trip.length];
		// first ans last Station are the same
		updatedTrip[0] = trip[0];
		updatedTrip[updatedTrip.length-1] = trip[trip.length-1];
		
		//System.out.println("original trip: " + Arrays.toString(trip));
		for (int i=0; i<trip.length-1;i++) {
			
			Station destination = trip[trip.length-1];
			Station currentStation = trip[i];
					
			List<Station> connectedStations = currentStation.getConnectedStations();
			//System.out.println("Current Station: "+currentStation + ", connected Stations: " + connectedStations);
			//System.out.println("shortest path from "+currentStation + " is: "+shortestPath);
			//System.out.println("shortest path from next stations is: "+(shortestPath-1));
			
			//we are one station further, thus reduce shortest path by 1
			//shortestPath--;
			
			if(connectedStations.size() > 2) {
				int shortestTimeToTheConnections = Integer.MAX_VALUE;
				int tripLenghtFromHere;
				for (int j=0;j<connectedStations.size();j++) {
					if (connectedStations.get(j) == destination) {
						updatedTrip[i] = currentStation;
						break;
					}
					
					//System.out.println("connected "+(j+1) +": "+connectedStations.get(j));
					//System.out.println("Time from: "+currentStation+ " to "+connectedStations.get(j)+": "+currentStation.getConnectionTimes().get(connectedStations.get(j)));
					if (currentStation.getConnectionTimes().get(connectedStations.get(j)) <= shortestTimeToTheConnections) {
						shortestTimeToTheConnections = currentStation.getConnectionTimes().get(connectedStations.get(j));
					}
					execute(connectedStations.get(j));
					LinkedList<Station> path = getPath(destination);
					
					
					//System.out.println("path from "+connectedStations.get(j)+ " to " +destination + ": "+path);
					
					if (path != null) {
						tripLenghtFromHere = getTimeOfTrip(path.toArray(new Station[path.size()]));
					} else {
						tripLenghtFromHere = shortestTripLength;
					}
					//System.out.println(tripLenghtFromHere);
					//System.out.println(shortestTripLength-shortestTimeToTheConnections);
					//System.out.println(trip[i+1].getName());
					if(path != null && tripLenghtFromHere <= (shortestTripLength-shortestTimeToTheConnections) && (trip[i+1].getName().compareTo(connectedStations.get(j).getName()) >= 0)) {
						//System.out.println("Shortest Path from: "+connectedStations.get(j)+" to " + destination + " path size: "+path.size());
						//System.out.println("path time: "+this.getTimeOfTrip(path.toArray(new Station[path.size()])));
						//System.out.println("better way found: " + connectedStations.get(j));
						getTimeOfTrip(path.toArray(new Station[path.size()]));
						//System.out.println();
						//shortestPath = path.size();
						updatedTrip[i+1] = connectedStations.get(j);
						shortestTripLength = tripLenghtFromHere;
						
						trip[i+1] = connectedStations.get(j);
						//System.out.println("Trip is now: "+Arrays.toString(trip));
						//System.out.println("next node to check: " + trip[i+1]);
						
					} else {
						//updatedTrip[i+1] = currentStation;
						//System.out.println(connectedStations.get(j) + " has a longer trip.\n");
					}
				}
			} else {
				updatedTrip[i] = currentStation;
				//System.out.println(currentStation + " is no transfer station.");
			}
		}
		return updatedTrip;
    }
      
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
        		timeFromCurrentConnectedToDestination = getTimeOfTrip(newPath.toArray(new Station[newPath.size()]));
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
    	//System.out.println("Sorted Stations: "+connectedStations);
    	returnStation = connectedStations.get(0);
        //System.out.println("Best connection is: "+returnStation+"\n");
        if (returnStation == destination) {
        	return null;
        } else {
        	return returnStation;
        }
        
    }
    
    public ArrayList<Station> calculateShortestPath(Station origin, Station destination){
    	ArrayList<Station> shortestPath = new ArrayList<Station>();
    	while(origin != null) {
    		shortestPath.add(origin);
    		origin = getNeighborWithShortestPathToTarget(origin, destination);
    	}
    	shortestPath.add(destination);
    	return shortestPath;
    }
    
    /**
     * Sorts a Hashmap by its values. From Highest to lowest.
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
    
    public Map<Station, Integer> calculateAllPaths(Map<Integer, List<String>> allTrips, Map<String, Station> allStations){
    	Map<Station, Integer> totalVisits = new HashMap<Station, Integer>();
    	
    	for (List<String> trip : allTrips.values()) {
			System.out.println("\nShortest trip from: "+trip.get(0) + ", to: "+trip.get(1));
			Station origin = allStations.get(trip.get(0));
			Station destination = allStations.get(trip.get(1));
			this.execute(origin);
	        	        
	        ArrayList<Station> path = this.calculateShortestPath(origin, destination);
	        System.out.println(path);
	        Station[] pathAsArray = path.toArray(new Station[path.size()]);
			System.out.println("Time of trip: "+this.getTimeOfTrip(pathAsArray));
			
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
		
    	SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
	
		Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
        TripReader tr = new TripReader("trip_records.json");
        Map<Integer, List<String>> allTrips = tr.getTrips();
        Map<String, Station> allStations = ssr.allStations;
		
        Map<Station, Integer> totalVisits = dijkstra.calculateAllPaths(allTrips, allStations);
		
		System.out.println("\n--- Top 10 visited Stations: ---");
		// Print the topN elements of a sorted map
		int topN = 10;		
		 Map<Station, Integer> sortedMap = dijkstra.sortMap(totalVisits, topN);
		 for(Map.Entry<Station, Integer> entry : sortedMap.entrySet()) {
				System.out.println(entry.getValue() + " - " + entry.getKey());
			}
		
    }
    
    
}
