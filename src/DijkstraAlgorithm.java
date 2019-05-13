import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    
/*    public Station getNeighborWithShortestPathToTarget(Station origin, Station destination) {
    	boolean hasMultiplePaths = false;
    	this.execute(origin);
    	LinkedList<Station> path = this.getPath(destination);
    	if(path == null) {
    		return null;
    	}
        int shortestTimeToDestination = Integer.MAX_VALUE;
        Station returnStation = origin.getConnectedStations().get(0);
        
        Map<Station, Integer> connectedStations = new HashMap<Station, Integer>();
        
        for (int i=0;i<origin.getConnectedStations().size();i++) {
        	Station currentConnectedStation = origin.getConnectedStations().get(i);
        	int timeFromOriginToCurrent = origin.getConnectionTimes().get(currentConnectedStation);
        	System.out.println("The time from "+origin+" to "+currentConnectedStation+" is "+timeFromOriginToCurrent);
        	this.execute(currentConnectedStation);
        	LinkedList<Station> newPath = this.getPath(destination);
        	if(newPath == null) {
        		return null;
        	}
        	int timeFromCurrentConnectedToDestination = getTimeOfTrip(newPath.toArray(new Station[newPath.size()]));
        	System.out.println("The time from "+currentConnectedStation+" to "+destination+" is "+timeFromCurrentConnectedToDestination);
        	System.out.println("Sum via this destination: "+timeFromOriginToCurrent +"+"+timeFromCurrentConnectedToDestination+"="+(timeFromOriginToCurrent+timeFromCurrentConnectedToDestination));
        	// if the next station has a shorter time to destination, set this time as new lowest
        	if (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent < shortestTimeToDestination) {
        		shortestTimeToDestination = (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent);
        		connectedStations.clear();
        		connectedStations.put(currentConnectedStation, shortestTimeToDestination);
        		// if alphabetically earlier, then set it as return Station
        		if (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent == shortestTimeToDestination
        				&& currentConnectedStation.getName().compareTo(returnStation.getName()) < 0) {
        			returnStation = currentConnectedStation;
        		}
        		shortestTimeToDestination = (timeFromCurrentConnectedToDestination+timeFromOriginToCurrent);
        		returnStation = currentConnectedStation;
        	}
        }
        if (hasMultiplePaths) {
        	System.out.println("Has multiple Paths.");
        }
        return returnStation;
    }
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
    
    public static void main(String[] args) {
		
    	SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
	
		Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		
		Map<Station, Integer> totalVisits = new HashMap<Station, Integer>();
		Map<Station, Integer> totalVisitsUpdated = new HashMap<Station, Integer>();
        TripReader tr = new TripReader("trip_records.json");
        
		for (List<String> trip : tr.getTrips().values()) {
			System.out.println();
			System.out.println("Trip from: "+trip.get(0) + ", to: "+trip.get(1));
			Station origin = ssr.allStations.get(trip.get(0));
			Station destination = ssr.allStations.get(trip.get(1));
			dijkstra.execute(origin);
	        LinkedList<Station> path = dijkstra.getPath(destination);
	        //System.out.println(path);
	        Station[] pathAsArray = path.toArray(new Station[path.size()]);
			//System.out.println("Time of original path: "+dijkstra.getTimeOfTrip(pathAsArray));
	        
	        // update the path and find another route
	        //Station[] pathAsArray = path.toArray(new Station[path.size()]);
	        //Station[] updatedPath = dijkstra.calculateAlternativeTrip(pathAsArray);
	        //System.out.println(Arrays.toString(updatedPath));
	        ArrayList<Station> updatedPath = dijkstra.calculateShortestPath(origin, destination);
	        System.out.println(updatedPath);
	        Station[] updatedPathAsArray = updatedPath.toArray(new Station[updatedPath.size()]);
			System.out.println("Time of updated path: "+dijkstra.getTimeOfTrip(updatedPathAsArray));
			
	        for (Station station : path) {	        	
	        	if(!totalVisits.containsKey(station)) {
	        		totalVisits.put(station, 1);
	        	} else {
	        		int count = totalVisits.get(station) + 1;
	        		totalVisits.put(station, count);
	        	}
	        }
	        for (Station station : updatedPath) {	        	
	        	if(!totalVisitsUpdated.containsKey(station)) {
	        		totalVisitsUpdated.put(station, 1);
	        	} else {
	        		int count = totalVisitsUpdated.get(station) + 1;
	        		totalVisitsUpdated.put(station, count);
	        	}
	        }
	        
	        //System.out.println(dijkstra.predecessors);
		}
		
		System.out.println(totalVisits);
		System.out.println(totalVisitsUpdated);
		
		
		List<Integer> list = new ArrayList<Integer>(totalVisits.values());
		Collections.sort(list, Collections.reverseOrder());
		List<Integer> top4 = list.subList(0, 4);
		System.out.println(top4);
		List<Integer> listUpdated = new ArrayList<Integer>(totalVisitsUpdated.values());
		Collections.sort(listUpdated, Collections.reverseOrder());
		List<Integer> top4updated = listUpdated.subList(0, 10);
		System.out.println(top4updated);	
		System.out.println();
		
		
		Station[] granViaToPrincipePio = new Station[4];
		granViaToPrincipePio[0] = ssr.allStations.get("Gran Vía");
		granViaToPrincipePio[1] = ssr.allStations.get("Tribunal");
		granViaToPrincipePio[2] = ssr.allStations.get("Plaza de España");
		granViaToPrincipePio[3] = ssr.allStations.get("Príncipe Pío");
		
		
		Station[] nunezDeBalboaToPlazaDeCastilla = new Station[4];
		nunezDeBalboaToPlazaDeCastilla[0] = ssr.allStations.get("Núñez de Balboa");
		nunezDeBalboaToPlazaDeCastilla[1] = ssr.allStations.get("Avenida de América");
		nunezDeBalboaToPlazaDeCastilla[2] = ssr.allStations.get("Nuevos Ministerios");
		nunezDeBalboaToPlazaDeCastilla[3] = ssr.allStations.get("Plaza de Castilla");
		
		Station[] sanBernardoToPacifico = new Station[8];
		sanBernardoToPacifico[0] = ssr.allStations.get("San Bernardo");
		sanBernardoToPacifico[1] = ssr.allStations.get("Bilbao");
		sanBernardoToPacifico[2] = ssr.allStations.get("Alonso Martínez");
		sanBernardoToPacifico[3] = ssr.allStations.get("Núñez de Balboa");
		sanBernardoToPacifico[4] = ssr.allStations.get("Diego de León");
		sanBernardoToPacifico[5] = ssr.allStations.get("Manuel Becerra");
		sanBernardoToPacifico[6] = ssr.allStations.get("Sainz de Baranda");
		sanBernardoToPacifico[7] = ssr.allStations.get("Pacífico");
		
		Station[] lasRosasToManuelBecerre = new Station[3];
		lasRosasToManuelBecerre[0] = ssr.allStations.get("Las Rosas");
		lasRosasToManuelBecerre[1] = ssr.allStations.get("Ventas");
		lasRosasToManuelBecerre[2] = ssr.allStations.get("Manuel Becerra");
		
		
		Station[] alonsoMartinezToPinardeCamartin = new Station[6];
		alonsoMartinezToPinardeCamartin[0] = ssr.allStations.get("Alonso Martínez");
		alonsoMartinezToPinardeCamartin[1] = ssr.allStations.get("Gregorio Marañón");
		alonsoMartinezToPinardeCamartin[2] = ssr.allStations.get("Nuevos Ministerios");
		alonsoMartinezToPinardeCamartin[3] = ssr.allStations.get("Plaza de Castilla");
		alonsoMartinezToPinardeCamartin[4] = ssr.allStations.get("Chamartín");
		alonsoMartinezToPinardeCamartin[5] = ssr.allStations.get("Pinar de Chamartín");
		
		
		Station[] trip = lasRosasToManuelBecerre;
		
		//System.out.println(Arrays.toString(trip));
		
		//Station[] updatedTrip = dijkstra.calculateAlternativeTrip(trip);
		
		//System.out.println("Original Trip: " + Arrays.toString(trip));
		
		//System.out.println("Updated Trip: " + Arrays.toString(updatedTrip));
		
		
		//System.out.println(dijkstra.getTimeOfTrip(trip));
		
		String a = "Ópera";
		String b = "Embajadores";
		String[] array1= {a,b};
		//System.out.println(Arrays.toString(array1));
		Arrays.sort(array1);
		//System.out.println(Arrays.toString(array1));
		
		//System.out.println(b.compareTo(a));
		//System.out.println(b.toLowerCase().compareTo(a.toLowerCase()));
		
		Station acacias = ssr.allStations.get("Acacias");
		Station granVia = ssr.allStations.get("Gran Vía");
		Station sol = ssr.allStations.get("Sol");
		Station opera = ssr.allStations.get("Ópera");
		Station pinarDeChamartin = ssr.allStations.get("Pinar de Chamartín");	
		Station mirasierra = ssr.allStations.get("Mirasierra");
		Station tribunal = ssr.allStations.get("Tribunal");
		Station alonsoMartinez = ssr.allStations.get("Alonso Martínez");
		Station sanBernardo = ssr.allStations.get("San Bernardo");
		Station ventas = ssr.allStations.get("Ventas");
		
		System.out.println(dijkstra.getNeighborWithShortestPathToTarget(sanBernardo, ventas));		
		//a neigboring station
		System.out.println(dijkstra.getNeighborWithShortestPathToTarget(sol, granVia));
		//the same station
		System.out.println(dijkstra.getNeighborWithShortestPathToTarget(acacias, acacias));
	
		System.out.println(dijkstra.calculateShortestPath(sanBernardo, ventas));
		
		System.out.println(ssr.allStations.get("Diego de León").getConnectionTimes());
		/*
		ArrayList<Station> shortestPath = dijkstra.calculateShortestPath(acacias, granVia);
		System.out.println(shortestPath);
		ArrayList<Station> shortestPath2 = dijkstra.calculateShortestPath(alonsoMartinez, tribunal);
		System.out.println(shortestPath2);
		
		Nuevos Ministerios
		Avenida de América
		Alonso Martínez
		Diego de León
		
		Gregorio Marañón
		Nuevos Ministerios
		Alonso Martínez
		Avenida de América
		*/
    }
    
    
}
