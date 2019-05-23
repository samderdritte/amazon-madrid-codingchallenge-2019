
public class Order {
	int id;
	int time;
	Station origin;
	Station destination;
	
	Order(int id, int time, Station origin, Station destination){
		this.id = id;
		this.time = time;
		this.origin = origin;
		this.destination = destination;
	}

	public int getId() {
		return id;
	}

	public int getTime() {
		return time;
	}

	public Station getOrigin() {
		return origin;
	}

	public Station getDestination() {
		return destination;
	}
	
	@Override
	public String toString() {
		String name = "Order " + id + ": " + origin + "->" + destination;
		return name;
	}
	
}
