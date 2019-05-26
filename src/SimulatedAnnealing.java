import java.util.ArrayList;

/**
 * Based on code from this site:
 * https://www.baeldung.com/java-simulated-annealing-for-traveling-salesman
 * 
 * Adjusted for the Code Challenge (replaced cities with subway stations)
 * 
 * 
 * @author samuelspycher
 *
 */

public class SimulatedAnnealing {
	
	public double simulateAnnealing(double startingTemperature,
			int numberOfIterations, double coolingRate, Travel travel, ShortestPaths sp) {
		System.out.println("Starting SA with temperature: " + startingTemperature + ", # of iterations: " + numberOfIterations + " and colling rate: " + coolingRate);
		double t = startingTemperature;
		double bestDistance = travel.getDistance(sp);
		System.out.println("Initial distance of travel: " + bestDistance);
		System.out.println("initial route: " + travel);
		System.out.println(sp.getTimeOfTripList(travel.getTravel()));
		ArrayList<Station> bestSolution = new ArrayList<Station>();
		Travel currentSolution = travel;
		
		for (int i = 0; i < numberOfIterations; i++) {
            if (t > 0.1) {
                currentSolution.swapStations();
                double currentDistance = currentSolution.getDistance(sp);
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                    bestSolution.clear();
                    for (Station station : currentSolution.getTravel()) {
                    	bestSolution.add(station);
                    }
                    System.out.println(bestDistance + " - Best travel route: " + bestSolution);
                } else if (Math.exp((bestDistance - currentDistance) / t) < Math.random()) {
                	currentSolution.revertSwap();
                    
                }
                t *= coolingRate;
            } else {
                continue;
            }
            if (i % 100 == 0) {
                System.out.println("Iteration #" + i);
            }
        }
        return bestDistance;
	}
	public ArrayList<Station> simulateAnnealingReturnToBase(double startingTemperature,
			int numberOfIterations, double coolingRate, Travel travel, ShortestPaths sp) {
		//System.out.println("Starting SA with temperature: " + startingTemperature + ", # of iterations: " + numberOfIterations + " and colling rate: " + coolingRate);
		double t = startingTemperature;
		double bestDistance = travel.getDistance(sp);
		ArrayList<Station> bestSolution = new ArrayList<Station>();
		Travel currentSolution = travel;
		for (Station station : currentSolution.getTravel()) {
        	bestSolution.add(station);
        }
		for (int i = 0; i < numberOfIterations; i++) {
            if (t > 0.1) {
                currentSolution.swapStations();
                double currentDistance = currentSolution.getDistance(sp);
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                    bestSolution.clear();
                    for (Station station : currentSolution.getTravel()) {
                    	bestSolution.add(station);
                    }
                    System.out.println(bestDistance + " - Best travel route: " + bestSolution);
                } else if (Math.exp((bestDistance - currentDistance) / t) < Math.random()) {
                	currentSolution.revertSwap();
                    
                }
                t *= coolingRate;
            } else {
                continue;
            }
            if (i % 100 == 0) {
                //System.out.println("Iteration #" + i);
            }
        }
		bestSolution.add(bestSolution.get(0));
        return bestSolution;
	}
}
