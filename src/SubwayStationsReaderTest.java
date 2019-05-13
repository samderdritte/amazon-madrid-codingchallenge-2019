import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SubwayStationsReaderTest {


	@Before
	public void setUp() throws Exception {		

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTimes() {
		SubwayStationsReader ssr = new SubwayStationsReader("metro_lines.json");
		
		Station origin = ssr.allStations.get("Valdecarros");
		Station destination = ssr.allStations.get("Pacífico");
		int expectedTime = 21;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Príncipe de Vergara");
		destination = ssr.allStations.get("Sol");
		expectedTime = 5;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Villaverde Alto");
		destination = ssr.allStations.get("Legazpi");
		expectedTime = 13;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Argüelles");
		destination = ssr.allStations.get("San Bernardo");
		expectedTime = 2;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Casa de Campo");
		destination = ssr.allStations.get("Oporto");
		expectedTime = 12;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
	
		origin = ssr.allStations.get("Nuevos Ministerios");
		destination = ssr.allStations.get("Avenida de América");
		expectedTime = 3;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Cuatro Caminos");
		destination = ssr.allStations.get("Nuevos Ministerios");
		expectedTime = 1;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));

		origin = ssr.allStations.get("Pueblo Nuevo");
		destination = ssr.allStations.get("Estadio Metropolitano");
		expectedTime = 9;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Nuevos Ministerios");
		destination = ssr.allStations.get("Colombia");
		expectedTime = 2;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Mirasierra");
		destination = ssr.allStations.get("Plaza de Castilla");
		expectedTime = 8;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Puerta del Sur");
		destination = ssr.allStations.get("Casa de Campo");
		expectedTime = 11;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Príncipe Pío");
		destination = ssr.allStations.get("Ópera");
		expectedTime = 3;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Acacias");
		destination = ssr.allStations.get("Embajadores");
		expectedTime = 3;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Plaza de España");
		destination = ssr.allStations.get("Noviciado");
		expectedTime = 4;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		origin = ssr.allStations.get("Plaza de Castilla");
		destination = ssr.allStations.get("Chamartín");
		expectedTime = 2;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));

		origin = ssr.allStations.get("Tribunal");
		destination = ssr.allStations.get("Gran Vía");
		expectedTime = 2;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		
		/* use for more tests
		origin = ssr.allStations.get("");
		destination = ssr.allStations.get("");
		expectedTime = 13;
		assertEquals(expectedTime,origin.getTimeToConnection(destination));
		assertEquals(expectedTime,destination.getTimeToConnection(origin));
		*/
	}

}
