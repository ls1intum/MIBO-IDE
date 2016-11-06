package edu.tum.ls1.mibo.editor.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionManager {

	private static final Logger log = Logger.getLogger(XMLParser.class.getName());

	/**
	 * Demo mode enables loading a set of data (modalities, fixtures, and
	 * interaction definitions). Furtheremore, deleting and posting new
	 * information is disabled and will not be persisted.
	 */
	private Boolean demoMode = false;
	
	/**
	 * Represents base URL to MIBO instance, such as
	 * http://127.0.0.1:8080/services/mibo/api/v1/rest/
	 */
	private String baseURL;
	
	public ConnectionManager(String baseURL) {
		
		log.setLevel(Level.OFF);

		// In case no URL (in fact, no content at all) is provided, we assume
		// that the demo mode should be activated
		if (baseURL.isEmpty()) {
			demoMode = true;
		}

		// Store MIBO's base URL
		this.baseURL = baseURL;
		
	}

	/**
	 * Sending a GET request and receiving the resonse.
	 * 
	 * @param fileURL
	 *            The specific file URL the HTTP request should be send to.
	 * @return The response of the HTTP request.
	 */
	public String get(String fileURL) {
		
		// Use dedicated helper method to load demo data
		if (demoMode) {
			return this.getDemoData(fileURL);
		}
		
		// Prepare URL
		String fullURL = this.baseURL + fileURL;
		log.info("Sending GET request to " + fullURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(fullURL);

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

			log.info("An exception occured when sending GET request to " + fullURL);
			e.printStackTrace();

		}

		return null;

	}
	
	/**
	 * Helper method for returning demo data in case no MIBO instance is
	 * available (in particular, no MIBO instance was defined in the baseURL.txt
	 * 
	 * @param file
	 *            Define which file is requested
	 * @return The demo content of the requested file
	 */
	private String getDemoData(String file) {

		// Prepare path using the local path
		String path = System.getProperty("user.dir") + "/demo/" + file;

		// Adjust the file extensions for the requested file. In particular,
		// aligne the local files with their REST-equivalent)
		if (file.startsWith("definitions")) {

			// Definitions are XML files.
			path += ".xml";

		} else if (file.startsWith("schema")) {

			// Schema files already contain the correct extension

		} else {

			// All other file types are json files
			path += ".json";

		}

		log.info("Retrieving demo data from" + path);

		// Prepare file loading
		String response = "";
		File myFile = new File(path);

		try (BufferedReader reader = new BufferedReader(new FileReader(myFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * Sending a DELETE request.
	 * 
	 * @param fileURL
	 *            The specific file URL the HTTP request should be send to.
	 */
	public void delete(String fileURL) {

		// Demo mode should not effect any persistent data
		if (demoMode) {
			log.warning("Skipping DELETE request due to demo mode");
			return;
		}

		// Prepare URL
		String fullURL = this.baseURL + fileURL;
		log.info("Sending DELETE request to " + fullURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(fullURL);

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

			log.info("An exception occured when sending DELETE request to " + fullURL);
			e.printStackTrace();

		}

	}

	/**
	 * Sending a POST request.
	 * 
	 * @param fileURL
	 *            The specific file URL the HTTP request should be send to.
	 * @param content
	 *            The HTTP post content.
	 */
	public void post(String fileURL, String content) {

		// Demo mode should not effect any persistent data
		if (demoMode) {
			log.warning("Skipping POST request due to demo mode");
			return;
		}
		
		// Prepare URL
		String fullURL = this.baseURL + fileURL;
		log.info("Sending POST request to " + fullURL);

		try {

			// Create a new URL from the input string
			URL url = new URL(fullURL);

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

			log.info("An excpetion occured when sending POST request to " + fullURL);
			e.printStackTrace();

		}

	}

}
