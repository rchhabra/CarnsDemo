package de.hybris.platform.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TravelSampleTest extends TravelAcceleratorTest {
	
	private static String TRAVEL_URL = "https://travel.me0.uk/yacceleratorstorefront/";

	@Test
	public void AntTestNGMethod() {
		LOG.info("AntTestNGMethod method started...");
		driver.get(TRAVEL_URL);
		Assert.assertTrue(driver.getTitle().contains("Travel"), "Travel site not launched successfully");
		LOG.info("Travel site successfully launched");
		LOG.info("AntTestNGMethod method completed.");
	}

}
