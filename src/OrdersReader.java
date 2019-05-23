import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class OrdersReader {
	
	private Map<Integer, Order> orders;
	
	public OrdersReader(String filename, SubwayStationsReader ssr) {		
		
		orders = new HashMap<Integer, Order>();
		
		File file = new File(filename);
		JSONParser parser = new JSONParser();
		
		try (
				FileReader reader = new FileReader(filename);
				BufferedReader bufferedReader = new BufferedReader(reader);
			){
			String currentLine;
			while((currentLine=bufferedReader.readLine()) != null) {
				JSONObject line = (JSONObject) parser.parse(currentLine);
				Long idLong = (Long) line.get("id");
				int id = idLong.intValue();
				Long timeLong = (Long) line.get("time");
				int time = timeLong.intValue();
        		String originName = (String) line.get("origin");
        		Station origin = ssr.allStations.get(originName);
        		String destinationName = (String) line.get("destination");
        		Station destination = ssr.allStations.get(destinationName);
        		Order order = new Order(id, time, origin, destination);
        		orders.put(id, order);
				
			}
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }       	
	}
	
	public Map<Integer, Order> getOrders(){
		return orders;
	}

}
