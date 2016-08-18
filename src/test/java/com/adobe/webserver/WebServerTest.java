package com.adobe.webserver;

import java.io.File;

public class WebServerTest {

	public static void main(String[] args) {
		WebServer webServer = new WebServer();
		webServer.start(0, new File("/Users/vkedar/Documents/webserver/"), 5);
	}

}
