package com.java.webclient;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WebLogger {
	
	private static final String POST_URL = "http://localhost:8080/logspringmvc/addlog";

	public static void main(String[] args) throws IOException {

		//sendGET();
		//System.out.println("GET DONE");
		sendPOST("Post timesaumya","Post applicationsaumya","Post methodsaumya","Post descriptionsaumya");
		//System.out.println("POST DONE");
	}
	
	private static void sendPOST(String datetime,String application,String method,String description) throws IOException {
		URL obj = new URL(POST_URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setDoOutput(true);
		String jsonInputString = "{\"datetime\":\"" + datetime+"\",\"application\":\""+application+"\",\"method\":\""+method+"\",\"description\":\""+description+"\"}";

		// For POST only - START
		try(OutputStream os = con.getOutputStream()) {
		    byte[] input = jsonInputString.getBytes("utf-8");
		    os.write(input, 0, input.length);			
		}
		
		try(BufferedReader br = new BufferedReader(
				  new InputStreamReader(con.getInputStream(), "utf-8"))) {
				    StringBuilder response = new StringBuilder();
				    String responseLine = null;
				    while ((responseLine = br.readLine()) != null) {
				        response.append(responseLine.trim());
				    }
				    System.out.println(response.toString());
				}

	}
	

}
