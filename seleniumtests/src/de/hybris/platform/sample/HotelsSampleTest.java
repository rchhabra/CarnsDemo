package de.hybris.platform.sample;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HotelsSampleTest extends TravelAcceleratorTest {

	private static String HOTELS_URL = "https://hotels.me0.uk/yacceleratorstorefront/";
	
	@Test
	public void AntTestNGMethod() {
		LOG.info("AntTestNGMethod method started...");
		driver.get(HOTELS_URL);
		Assert.assertTrue(driver.getTitle().contains("Hotels"), "Hotels site not launched successfully");
		LOG.info("Hotels site successfully launched");
		LOG.info("AntTestNGMethod method completed.");
	}
}
