import java.util.List;

public class Graph {
	private List<Connection> connections;
	private List<Station> stations;	
	/**
	 * Constructor
	 */
	public Graph(List<Connection> connections, List<Station> stations) {
		this.connections = connections;
		this.stations = stations;	
	}
	/**
	 * Getter methods
	 */
	public List<Connection> getConnections(){
		return connections;
	}
	public List<Station> getStations() {
		return stations;
	}
}
