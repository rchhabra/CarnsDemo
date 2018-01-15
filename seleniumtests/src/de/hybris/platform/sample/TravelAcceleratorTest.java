package de.hybris.platform.sample;


import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

public class TravelAcceleratorTest
{
	public static WebDriver driver;
	public static Logger LOG = Logger.getLogger(TravelAcceleratorTest.class);

	@BeforeTest
	public void setUp()
	{
		final DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setJavascriptEnabled(true);
		capabilities.setBrowserName("htmlunit");
		capabilities.setPlatform(org.openqa.selenium.Platform.ANY);

		driver = new HtmlUnitDriver(capabilities);
		LOG.info("Browser Opened");
		
		//to force script to wait for loading of a page for 10 seconds before beginning its execution
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);
		driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
		
		//to force the browser window load in maximized mode
		driver.manage().window().maximize();
		LOG.info("Browser Window Maximized");
		LOG.debug("Browser maximized");
	}
	
 	@AfterTest(alwaysRun = true)
	public void tearDown() throws Exception
	{
		driver.quit();
		LOG.info("Quit from the Browser");
	}
}
