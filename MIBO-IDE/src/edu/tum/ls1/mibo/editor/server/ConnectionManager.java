package edu.tum.ls1.mibo.editor.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

	private static final Logger log = Logger.getLogger(XMLParser.class.getName());

	public ConnectionManager() {
		log.setLevel(Level.OFF);
	}

	/**
	 * Sending a GET request and receiving the resonse.
	 * 
	 * @param targetURL
	 *            The URL the HTTP request should be send to.
	 * @return The response of the HTTP request.
	 */
	public String get(String targetURL) {

		log.info("Sending GET request to " + targetURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(targetURL);

			// Open the connection based on the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set the request method
			connection.setRequestMethod("GET");

			// Check if connection was successfull
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Response for GET request is not HTTP_OK");
			}

			// Reading the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();

			for (String line; (line = reader.readLine()) != null;) {
				response.append(line);
			}

			// Closing processes
			connection.disconnect();
			reader.close();

			// Return the response
			return response.toString();

		} catch (Exception e) {

			log.info("An exception occured when sending GET request to " + targetURL);
			e.printStackTrace();

		}

		return null;

	}

	/**
	 * Sending a DELETE request.
	 * 
	 * @param targetURL
	 *            The URL the HTTP request should be send to.
	 */
	public void delete(String targetURL) {

		log.info("Sending DELETE request to " + targetURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(targetURL);

			// Open the connection based on the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set the request method
			connection.setRequestMethod("DELETE");

			// Check if connection was successfull
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Response for DELETE request is not HTTP_OK");
			}

			// Reading the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();

			for (String line; (line = reader.readLine()) != null;) {
				response.append(line);
			}

			// Only show response if there is one
			if (response.length() != 0) {
				log.info("Received response from DELETE request:" + response.toString());
			}

			// Closing processes
			connection.disconnect();
			reader.close();

		} catch (Exception e) {

			log.info("An exception occured when sending DELETE request to " + targetURL);
			e.printStackTrace();

		}

	}

	/**
	 * Sending a POST request.
	 * 
	 * @param targetURL
	 *            The URL the HTTP request should be send to.
	 */
	public void post(String targetURL, String content) {

		log.info("Sending POST request to " + targetURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(targetURL);

			// Open the connection based on the URL
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Specify connection parameters
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("Content-Length", String.valueOf(content.length()));

			// Writing the content to the POST request
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(content);
			writer.flush();

			// Reading the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();

			for (String line; (line = reader.readLine()) != null;) {
				response.append(line);
			}

			// Only show response if there is one
			if (response.length() != 0) {
				log.info("Received response from POST request:" + response.toString());
			}

			// Closing processes
			writer.close();
			reader.close();
			connection.disconnect();

		} catch (Exception e) {

			log.info("An excpetion occured when sending POST request to " + targetURL);
			e.printStackTrace();

		}

	}

}
