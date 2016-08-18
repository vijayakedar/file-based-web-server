package com.adobe.webserver;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpServerConnection;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

public class WebServer {

	public static final int DEFAULT_PORT = 8181;
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final int NO_OF_THREADS = 5;
	
	public void start(int port, File root, int noOfThreads){
		
		//validations
		if(port <= 0){
			port = DEFAULT_PORT;
		}
		if(root == null){
			root = new File(TMP_DIR);
		}
		if(noOfThreads < 1){
			noOfThreads = NO_OF_THREADS;
		}
		
		ExecutorService service = Executors.newFixedThreadPool(noOfThreads);
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Started server on port "+ port + "...");
			while(true){
				Socket socket = serverSocket.accept();
				Worker worker = new Worker(socket, root);
				service.submit(worker);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if(serverSocket != null){
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	static class Worker implements Runnable{
        
		private Socket socket;
		private File root;
		
		public Worker(Socket socket, File root){
			this.socket = socket;
			this.root = root;
		}
		
		public void run() {
			try {
				DefaultBHttpServerConnection connection = new DefaultBHttpServerConnection(2048);
				connection.bind(socket);
				System.out.println("In worker");

				HttpRequest httpRequest = connection.receiveRequestHeader();
				RequestLine requestLine = httpRequest.getRequestLine();
				ProtocolVersion protocolVersion = httpRequest.getProtocolVersion();
				HttpResponse response = null;
				if ("GET".equalsIgnoreCase(requestLine.getMethod())) {
					final String uri = requestLine.getUri().substring(1);
					if (uri == null || uri.length() == 0) {
						response = buildResponse(protocolVersion, HttpStatus.SC_OK,
										new StringEntity("Hello from Webserver!"));
					} else {
						// TODO: File file = new File(root, URLDecoder.decode(uri, "UTF-8"));
						
						File[] files = root.listFiles(new FilenameFilter() {
							
							public boolean accept(File dir, String name) {
								return uri.equalsIgnoreCase(name);
							}
						});
						if (files.length == 0) {
							response = buildResponse(protocolVersion, HttpStatus.SC_NOT_FOUND, 
											new StringEntity("<html><body>File not found</body></html>"));
						} else {
							response = buildResponse(protocolVersion, HttpStatus.SC_OK, new FileEntity(files[0]));
						}
					}
				} else {
					response = buildResponse(protocolVersion, HttpStatus.SC_METHOD_NOT_ALLOWED, new StringEntity("Unsupported method."));
				}
				
				connection.sendResponseHeader(response);
				connection.sendResponseEntity(response);
				connection.close();
				/*socket.getOutputStream().write("hello world".getBytes());
				socket.getOutputStream().flush();
				socket.close();*/
			} catch (IOException e) {
				e.printStackTrace();
			} catch (HttpException e) {
				e.printStackTrace();
			}
		}

		private HttpResponse buildResponse(ProtocolVersion protocolVersion, int status, HttpEntity entity) {
			HttpResponse response = new BasicHttpResponse(new BasicStatusLine(protocolVersion, status , ""));
			response.setStatusCode(status);
			response.setEntity(entity);
			return response;
		}
		
	}
}
