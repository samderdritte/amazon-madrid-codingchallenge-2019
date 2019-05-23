import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RobotReader {
	
	private Map<Integer, Robot> robots;
	
	public RobotReader(String filename, SubwayStationsReader ssr) {		
		
		robots = new HashMap<Integer, Robot>();
		
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
	        		String originName = (String) line.get("origin");
	        		Station location = ssr.allStations.get(originName);
	        		Long capacityLong = (Long) line.get("capacity");
	        		int capacity = capacityLong.intValue();
	        		Robot robot = new Robot(id, location, capacity);
	        		robots.put(id, robot);
				}
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }       	
	}
	
	public Map<Integer, Robot> getRobots(){
		return robots;
	}

}
