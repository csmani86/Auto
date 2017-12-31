package com.mobproxy.browserMobProxy;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

public class SampleTestNG 
{
	WebDriver driver;
	String exePath = System.getProperty("user.dir")+"\\Drivers\\chromedriver.exe";
	public static BrowserMobProxyServer server;
	String sFileName = "F:/SeleniumEasy.har";

	
	@BeforeClass
	public void dataSetup()
	{
		System.out.println(exePath);
	}
	
	@Test
	public void test() throws IOException
	{
		System.setProperty("webdriver.chrome.driver", exePath) ;
		server = new BrowserMobProxyServer();
		server.setTrustAllServers(true); 
		server.start();
		Proxy selPro = ClientUtil.createSeleniumProxy(server);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("disable-infobars");
		options.setCapability(CapabilityType.PROXY, selPro);
		options.setCapability(CapabilityType.ACCEPT_SSL_CERTS,true);
		driver = new ChromeDriver(options);
		HashSet<CaptureType> enable = new HashSet<CaptureType>();
		enable.add(CaptureType.REQUEST_HEADERS);
		enable.add(CaptureType.REQUEST_CONTENT);
		enable.add(CaptureType.RESPONSE_HEADERS);
		server.enableHarCaptureTypes(enable);
		HashSet<CaptureType> disable = new HashSet<CaptureType>();
		disable.add(CaptureType.REQUEST_COOKIES);
		disable.add(CaptureType.RESPONSE_COOKIES);
		server.disableHarCaptureTypes(disable);
	    server.newHar("Google");
		driver.get("https://www.google.com");
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        //The below method will save the screen shot in d drive with name "screenshot.png"
        FileUtils.copyFile(scrFile, new File("F:\\screenshot.png"));
	}
	
	@AfterClass
	public void quit()
	{
		Har har = server.getHar();
		File harFile = new File(sFileName);
		try 
		{
			
			har.writeTo(harFile);
		} 
		catch (IOException ex) 
		{
			 System.out.println (ex.toString());
		     System.out.println("Could not find file " + sFileName);
		}
		
		if (driver != null) 
		{
			server.stop();
			driver.quit();
		}
	}
}
