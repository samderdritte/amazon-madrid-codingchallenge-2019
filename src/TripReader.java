import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TripReader {
	
	private Map<Integer, List<String>> trips;

	public TripReader(String filename) {
		
		trips = new HashMap<Integer, List<String>>();
		
		JSONParser parser = new JSONParser();

		try (Reader reader = new FileReader(filename)) {
        	
        	JSONArray jsonArray = (JSONArray) parser.parse(reader);
        
        	for (int i=0;i<jsonArray.size();i++) {
        		JSONObject line = (JSONObject) jsonArray.get(i);
        		String origin = (String) line.get("origin");
        		String destination = (String) line.get("destination");
        		ArrayList<String> trip = new ArrayList<String>();
        		trip.add(origin);
        		trip.add(destination);
        		trips.put(i, trip);
        	}
        	
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        	
	}
	
	public Map<Integer, List<String>> getTrips(){
		return trips;
	}
	
	public static void main(String[] args) {
		
		TripReader tr = new TripReader("trip_records.json");
		
		for (List<String> trip : tr.getTrips().values()) {
			System.out.println(trip.get(0) + ", "+trip.get(1));
		}
	
	}
	
}
