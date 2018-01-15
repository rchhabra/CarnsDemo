package de.hybris.platform.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AirlineSampleTest extends TravelAcceleratorTest {

	private static String AIRLINE_URL = "https://airline.me0.uk/yacceleratorstorefront/";
	
	@Test
	public void AntTestNGMethod() {
		LOG.info("AntTestNGMethod method started...");
		driver.get(AIRLINE_URL);
		Assert.assertTrue(driver.getTitle().contains("Airline"), "Airline site not launched successfully");
		LOG.info("Airline site successfully launched");
		LOG.info("AntTestNGMethod method completed.");
	}
}
