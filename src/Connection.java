/**
 * A connection works like an edge in an undirected graph.
 * 
 * @author SpycherS
 *
 */
public class Connection {

	public Station origin;
	public Station destination;
	public int time;
	
	public Connection(Station origin, Station destination, int time) {
		this.origin = origin;
		this.destination = destination;
		this.time = time;
	}
	
	public Station getOrigin() {
		return origin;
	}
	public Station getDestination() {
		return destination;
	}
	public int getTime() {
		return time;
	}
	
	// gets the connected Station of this connection, based on the two stations
	// connected by this connection
	public int getConnectedStation(int stationID) {
		if (this.origin.getStationID() == stationID) {
			return this.destination.getStationID();
		} else {
			return this.origin.getStationID();
		}
	}
}
