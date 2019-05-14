import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DijkstraAlgorithmTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
		
		Graph graph = new Graph(ssr.allConnections, ssr.allStationsList);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
		
		
		//Case 1 - Acacias to Gran Via
		Station acacias = ssr.allStations.get("Acacias");		
		Station embajadores = ssr.allStations.get("Embajadores");
		Station sol = ssr.allStations.get("Sol");
		Station granVia = ssr.allStations.get("Gran Vía");
		
		ArrayList<Station> solution = new ArrayList<Station>();
		solution.add(acacias);
		solution.add(embajadores);
		solution.add(sol);
		solution.add(granVia);
		
		Station origin = acacias;
		Station destination = granVia;
		LinkedList<Station> shortestPath = dijkstra.getAlphabeticalPath(origin, destination);
		assertEquals(solution, shortestPath);
		
		// Case 2 Mirasierra to Plaza De Castilla
		Station mirasierra = ssr.allStations.get("Mirasierra");
		Station plazaDeCastilla = ssr.allStations.get("Plaza de Castilla");
		
		solution = new ArrayList<Station>();
		solution.add(mirasierra);
		solution.add(plazaDeCastilla);
		origin = mirasierra;
		destination = plazaDeCastilla;
		shortestPath = dijkstra.getAlphabeticalPath(origin, destination);
		assertEquals(solution, shortestPath);
		
		// Case 3 - Opera to Gran Via
		
		Station opera = ssr.allStations.get("Ópera");
		
		solution = new ArrayList<Station>();
		solution.add(opera);
		solution.add(sol);
		solution.add(granVia);
		
		origin = opera;
		destination = granVia;
		shortestPath = dijkstra.getAlphabeticalPath(origin, destination);
		assertEquals(solution, shortestPath);
		
		// Case 4 - Mirasierra to Gran Via
		
		
		Station nuevosMinisterios = ssr.allStations.get("Nuevos Ministerios");
		Station gregorioMaranon = ssr.allStations.get("Gregorio Marañón");
		Station alonsoMartinez = ssr.allStations.get("Alonso Martínez");
		
		
		solution = new ArrayList<Station>();
		solution.add(mirasierra);
		solution.add(plazaDeCastilla);
		solution.add(nuevosMinisterios);
		solution.add(gregorioMaranon);
		solution.add(alonsoMartinez);
		
		solution.add(granVia);
		
		origin = mirasierra;
		destination = granVia;
		shortestPath = dijkstra.getAlphabeticalPath(origin, destination);
		assertEquals(solution, shortestPath);
		
		
		// Case 5 Opera to Pinar de Chamartin
		
		Station pinarDeChamartin = ssr.allStations.get("Pinar de Chamartín");
		Station callao = ssr.allStations.get("Callao");
		Station plazaDeEspana = ssr.allStations.get("Plaza de España");
		Station tribunal = ssr.allStations.get("Tribunal");
		Station chamartin = ssr.allStations.get("Chamartín");
		solution = new ArrayList<Station>();
		solution.add(opera);
		solution.add(callao);
		solution.add(plazaDeEspana);
		solution.add(tribunal);
		solution.add(alonsoMartinez);
		solution.add(gregorioMaranon);
		solution.add(nuevosMinisterios);
		solution.add(plazaDeCastilla);
		solution.add(chamartin);
		solution.add(pinarDeChamartin);
		
		origin = opera;
		destination = pinarDeChamartin;
		shortestPath = dijkstra.getAlphabeticalPath(origin, destination);
		assertEquals(solution, shortestPath);
	}

}
