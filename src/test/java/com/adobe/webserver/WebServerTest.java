package com.adobe.webserver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WebServerTest {

	private static final int PORT = 8181;
	private static WebServer webServer;
	
	@BeforeClass
	public static void setUp() {
	  Thread t = new Thread(new Runnable() {
		
		public void run() {
			webServer = new WebServer();
			webServer.start(PORT, null, 5);			
		}
	  });
	  t.start();
	}

	@Test
	public void testSuccessfulGet() throws IOException {
		URL url = new URL("http://localhost:" + PORT + "/index.html");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.connect();
		
		Assert.assertEquals(200, connection.getResponseCode());
	}
	
	@Test
	public void test404() throws IOException {
		URL url = new URL("http://localhost:" + PORT + "/index.png");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.connect();
		
		Assert.assertEquals(404, connection.getResponseCode());
	}
}
