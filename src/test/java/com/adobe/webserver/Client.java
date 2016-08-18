package com.adobe.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;

public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException, HttpException {
		Socket s = new Socket("localhost", 8181);
		DefaultBHttpClientConnection connection = new DefaultBHttpClientConnection(2048);
        connection.bind(s);
        connection.sendRequestHeader(new BasicHttpRequest(new BasicRequestLine("GET", "/index.html", HttpVersion.HTTP_1_1)));
        HttpResponse response = connection.receiveResponseHeader();
        System.out.println(response.getEntity().toString());
        connection.close();
		/*BufferedReader input =
            new BufferedReader(new InputStreamReader(s.getInputStream()));
        String answer = input.readLine();
        System.out.println(answer);*/

	}

}
