
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubwayStationsReader {
	
	Map<String, SubwayLine> subwaySystem;	
	Map<String, Station> allStations;
	List<Station> allStationsList;
	List<Connection> allConnections;
	
	
	public SubwayStationsReader(String filename) {
		
		subwaySystem = new HashMap<String, SubwayLine>();
		allStations = new HashMap<String, Station>();
		allStationsList = new ArrayList<Station>();
		allConnections = new ArrayList<Connection>();
		
		JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(filename)) {
        	
        	JSONArray jsonArray = (JSONArray) parser.parse(reader);
            //System.out.println(jsonArray);
            
        	for (int i=0;i<jsonArray.size();i++) {
        		JSONObject line = (JSONObject) jsonArray.get(i);
        		String lineNum = (String) line.get("line");
        		//System.out.println("Line: " + lineNum + " imported.");
        		SubwayLine newLine = new SubwayLine(lineNum);       		
        		subwaySystem.put(lineNum, newLine);
        		
        		// fetch the Array of stations
        		JSONArray stations = (JSONArray) line.get("stations");
        		
        		for (int j=0; j<stations.size();j++) {
        			JSONObject stationLine = (JSONObject) stations.get(j);
        			//System.out.println(stationLine.get("name") + " - " + stationLine.get("time"));
        			
        			String name = (String) stationLine.get("name");
        			long time = (long) stationLine.get("time");
        			subwaySystem.get(newLine.getName()).addStation(name, (int) time);
        			
        			Station followingStation;
        			Station previousStation;
        			Station currentStation;
        			
        			if(!allStations.containsKey(name)) {
        				currentStation = new Station(name);
        				currentStation.setLine(newLine); 
        				allStations.put(name, currentStation);
        				allStationsList.add(currentStation);
        				
        			} else {
        				currentStation = allStations.get(name);
        			}
        			
        			// if it is the first one, then only add the following station to the connections
    				if(j == 0) {   					
    					JSONObject followingStationLine = (JSONObject) stations.get(j+1);
    					String followingStationName = (String) followingStationLine.get("name");
    					if(!allStations.containsKey(followingStationName)) {
    						followingStation = new Station(followingStationName);
    						followingStation.setLine(newLine);
    						allStations.put(followingStationName, followingStation);  
    						allStationsList.add(followingStation);
    						
    					} else {
    						followingStation = allStations.get(followingStationName);
    					}
    					currentStation.addConnectedStation(followingStation);
    					
    					long followingTime = (long) followingStationLine.get("time");
    					int timeDifference = (int) (followingTime - time);
    					currentStation.addConnectionTime(followingStation, timeDifference);
    					
    					Connection newConnection = new Connection(currentStation, followingStation, timeDifference, newLine.getName());
    					allConnections.add(newConnection);
    					Connection newConnectionReverse = new Connection(followingStation, currentStation, timeDifference, newLine.getName());
    					allConnections.add(newConnectionReverse);

    				// if it is the last item, only add the previous station to the connections
    				} else if (j == stations.size()-1) {
    					
    					JSONObject previousStationLine = (JSONObject) stations.get(j-1);
    					String previousStationName = (String) previousStationLine.get("name");
    					if(!allStations.containsKey(previousStationName)) {
    						previousStation = new Station(previousStationName);
    						previousStation.setLine(newLine);
    						allStations.put(previousStationName, previousStation);
    						allStationsList.add(previousStation);
    					} else {
    						previousStation = allStations.get(previousStationName);	
    					}
    					currentStation.addConnectedStation(previousStation);
    					
    					long previousTime = (long) previousStationLine.get("time");
    					int timeDifference = (int) (previousTime - time);
    					currentStation.addConnectionTime(previousStation, timeDifference);
    					
    				// else add both the following and the previous station to the connections
    				} else {
    					JSONObject followingStationLine = (JSONObject) stations.get(j+1);
    					String followingStationName = (String) followingStationLine.get("name");
    					if(!allStations.containsKey(followingStationName)) {
    						followingStation = new Station(followingStationName);
    						followingStation.setLine(newLine);
    						allStations.put(followingStationName, followingStation);
    						allStationsList.add(followingStation);
    					} else {
    						followingStation = allStations.get(followingStationName); 						
    					}
    					currentStation.addConnectedStation(followingStation);
    					
    					long followingTime = (long) followingStationLine.get("time");
    					int timeDifference = (int) (followingTime - time);
    					currentStation.addConnectionTime(followingStation, timeDifference);
    					
    					Connection newConnection = new Connection(currentStation, followingStation, timeDifference, newLine.getName());
    					allConnections.add(newConnection);
    					Connection newConnectionReverse = new Connection(followingStation, currentStation, timeDifference, newLine.getName());
    					allConnections.add(newConnectionReverse);
    					
    					JSONObject previousStationLine = (JSONObject) stations.get(j-1);
    					String previousStationName = (String) previousStationLine.get("name");
    					if(!allStations.containsKey(previousStationName)) {
    						previousStation = new Station(previousStationName);
    						previousStation.setLine(newLine);
    						allStations.put(previousStationName, previousStation);
    						allStationsList.add(previousStation);
    					} else {
    						previousStation = allStations.get(previousStationName);
    					}
    					currentStation.addConnectedStation(previousStation);
    					long previousTime = (long) previousStationLine.get("time");
    					int timeDifferencePrev = (int) (previousTime - time);
    					currentStation.addConnectionTime(previousStation, timeDifferencePrev);
    				}
        		}
        	}
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
	}

    public static void main(String[] args) {

    	SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
    	System.out.println("Subway station imported.");
    	System.out.println(ssr.subwaySystem.get("6").getStations().get("Pacífico"));
    	
    	//SubwayLine line6 = ssr.subwaySystem.get("6");
    	SubwayLine line5 = ssr.subwaySystem.get("5");
    	System.out.println(line5);
    	System.out.println(line5.getTimeBetweenStations("Acacias", "Gran Vía"));
   
    	System.out.println(ssr.allStations);
    	System.out.println(ssr.allStations.get("Pacífico").getLine());
    	System.out.println(ssr.allStations.get("Pacífico").getConnectedStations());
    	System.out.println(ssr.allStations.get("Sol").getConnectedStations());
    	System.out.println(ssr.allStations.get("Nuevos Ministerios").getConnectedStations());
    	
    	
    	for(Station station : ssr.allStations.values()) {
    		System.out.println();
    		//System.out.println("Station name "+station.getName() + " - " + station.getConnections());
    		//System.out.println("Station name "+station.getName() + " - " + station.getConnectionTimes());
    		System.out.println("Station name "+station.getName() + " - " + station.getStationID());
    		
    	}
    	System.out.println();
    	System.out.println("Total Connections: "+ ssr.allConnections.size());
    	System.out.println("Total Stations: " + ssr.allStations.size());
    	
    	
    	System.out.println(ssr.allStations.get("Las Rosas").getTimeToConnection(ssr.allStations.get("Ventas")));
    	System.out.println(ssr.allStations.get("Chamartín").getConnectionTimes());
    	
    	System.out.println(ssr.allStations.get("Guzmán el Bueno").getConnections(ssr));
    	for (Connection connection : ssr.allStations.get("Guzmán el Bueno").getConnections(ssr)) {
    		System.out.println(connection.getDestination());
    	}
    	System.out.println("line " + ssr.allStations.get("Guzmán el Bueno").getLineForConnection(ssr.allStations.get("Pitis"), ssr));
    }


}

