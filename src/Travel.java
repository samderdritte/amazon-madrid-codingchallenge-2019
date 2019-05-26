import java.util.ArrayList;
import java.util.Collections;

public class Travel implements Cloneable {

	private ArrayList<Station> travel = new ArrayList<>();
	private ArrayList<Station> previousTravel = new ArrayList<>();

	// A list of all travelled stations including the starting Station
	public Travel(ArrayList<Station> listOfStations) {
		for (int i = 0; i < listOfStations.size(); i++) {
			travel.add(listOfStations.get(i));
		}
	}
	@Override
	public String toString() {		
		String output = "Travel: " + travel.get(0).getName();
		
		for (int i = 1; i<travel.size();i++) {
			output += ", " + travel.get(i).getName();
		}
		return output;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public ArrayList<Station> getTravel(){
		return travel;
	}
	
	public void generateInitialTravel() {
		// prevent the first elemnt from being shuffled
		Station first = travel.get(0);
		travel.remove(0);
		Collections.shuffle(travel);
		travel.add(0, first);
    }
	
	private int generateRandomIndex() {
        return (int) (Math.random() * travel.size());
    }
	
	public void swapStations() {
		int a = generateRandomIndex();
		int b = generateRandomIndex();
		// prevent the first element from being swapped
		if(a == 0 || b == 0) {
			while (a == 0 || b == 0) {
				a = generateRandomIndex();
				b = generateRandomIndex();
			}
		}
		
		previousTravel = travel;
        Station x = travel.get(a);
        Station y = travel.get(b);
        travel.set(a, y);
        travel.set(b, x);
    }
	
	public void revertSwap() {
        travel = previousTravel;
    }
	
	public Station getStation(int index) {
        return travel.get(index);
    }
	
	public int getDistance(ShortestPaths sp) {
		int distance = 0;
		for (int index = 0; index < travel.size(); index++) {
			Station starting = getStation(index);
			Station destination;
			if (index + 1 < travel.size()) {
				destination = getStation(index + 1);
			} else {
				destination = getStation(0);
			}
			distance += starting.getShortestTimeToAnotherStation(destination, sp);
		}
		
		return distance;
	}
	
	
	
	public static void main(String[] args) {
		
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");	
		
		ShortestPaths sp = new ShortestPaths(ssr);
		
		ArrayList<Station> stationList = new ArrayList<Station>();
		
		SimulatedAnnealing sa = new SimulatedAnnealing();
		
		Station sol = ssr.allStations.get("Sol");
		Station plazaDeEspana = ssr.allStations.get("Plaza de España");
		Station opera = ssr.allStations.get("Ópera");
		Station tribunal = ssr.allStations.get("Tribunal");
		Station goya = ssr.allStations.get("Goya");
		Station principeDeVergara = ssr.allStations.get("Príncipe de Vergara");
		Station arguelles = ssr.allStations.get("Argüelles");
		Station casaDeCampo = ssr.allStations.get("Casa de Campo");
		Station oporto = ssr.allStations.get("Oporto");
		Station acacias = ssr.allStations.get("Acacias");
		Station sanBernardo = ssr.allStations.get("San Bernardo");
		Station principePio = ssr.allStations.get("Príncipe Pío");
		stationList.add(arguelles);
		stationList.add(oporto);
		stationList.add(casaDeCampo);
		stationList.add(acacias);		
		stationList.add(sanBernardo);
		stationList.add(principePio);
		
		System.out.println(stationList);
		
		Travel travel = new Travel(stationList);
		travel.generateInitialTravel();
		System.out.println(travel);
		System.out.println(travel.getDistance(sp));

		//double bestRoute = sa.simulateAnnealing(200, 1000, 1, travel, sp);
		//System.out.println(bestRoute);
		ArrayList<Station> bestRouteList = sa.simulateAnnealingReturnToBase(200, 1000, 1, travel, sp);
		System.out.println(sp.getTimeOfTripList(bestRouteList) + " - "+bestRouteList);
		//System.out.println(travel);
		
		System.out.println("---");
		
		ArrayList<Station> stationList2 = new ArrayList<Station>();
		
		stationList2.add(arguelles);		
		stationList2.add(plazaDeEspana);
		stationList2.add(sanBernardo);
		
		Travel travel2 = new Travel(stationList2);
		System.out.println(travel2);
		System.out.println(travel2.getDistance(sp));
		ArrayList<Station> bestRouteList2 = sa.simulateAnnealingReturnToBase(200, 1000, 1, travel2, sp);
		System.out.println(sp.getTimeOfTripList(bestRouteList2) + " - "+bestRouteList2);
		
		
		
		
	}

}
