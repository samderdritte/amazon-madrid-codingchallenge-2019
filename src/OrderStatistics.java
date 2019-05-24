import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class OrderStatistics {

	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
		OrdersReader or = new OrdersReader("orders.jsonl", ssr);
		
		Map<Integer, Order> orders = or.getOrders();
		
		Map<Station, Integer> originOrdersCount = new HashMap<Station, Integer>();
		Map<Station, Integer> destOrdersCount = new HashMap<Station, Integer>();
		for (Order order : orders.values()) {
			// orders for origin
			if(originOrdersCount.containsKey(order.getOrigin())) {
				int count = originOrdersCount.get(order.getOrigin());
				originOrdersCount.put(order.getOrigin(), count+1);
			} else {
				originOrdersCount.put(order.getOrigin(), 1);
			}
			//same thing for destinations
			if(destOrdersCount.containsKey(order.getDestination())) {
				int count = destOrdersCount.get(order.getDestination());
				destOrdersCount.put(order.getDestination(), count+1);
			} else {
				destOrdersCount.put(order.getDestination(), 1);
			}
		}
		System.out.println(originOrdersCount);
		System.out.println(destOrdersCount);
		System.out.println("Average packages per Destination: " + orders.size()/destOrdersCount.size());
		
		String selectedStation = "Argüelles";
		Map<Station, Integer> destOrdersCountSingle = new HashMap<Station, Integer>();
		for (Order order : orders.values()) {
			if(order.getOrigin().getName().equals(selectedStation)) {
				//select destinations for this station
				if(destOrdersCountSingle.containsKey(order.getDestination())) {
					int count = destOrdersCountSingle.get(order.getDestination());
					destOrdersCountSingle.put(order.getDestination(), count+1);
				} else {
					destOrdersCountSingle.put(order.getDestination(), 1);
				}
			}
			
		}
		System.out.println(destOrdersCountSingle);
		System.out.println(destOrdersCountSingle.size());
		Map<Station, Integer> destOrdersCountSingleDist = new HashMap<Station, Integer>();
		System.out.println(destOrdersCountSingleDist);
		for (Station station : destOrdersCountSingle.keySet()) {
			Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);
	    	DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
	    	Station origin = ssr.allStations.get(selectedStation);
	    	LinkedList<Station> path = dijkstra.getAlphabeticalPath(origin, station);
	    	if(!destOrdersCountSingleDist.containsKey(station)) {
	    		destOrdersCountSingleDist.put(station, dijkstra.getTimeOfTrip(path));
	    	}
		}
		System.out.println(destOrdersCountSingleDist);
	}
	
	
	/***
	 * Statistics:
	 * 
	 * Order Count by Origin:
	 * Argüelles: 			23778
	 * Plaza de Castilla: 	18728
	 * Diego de León: 		30789
	 * Sol: 				26705
	 * 
	 * -> move some robots From Plaza de Castilla to Diego de Leon
	 */
}
