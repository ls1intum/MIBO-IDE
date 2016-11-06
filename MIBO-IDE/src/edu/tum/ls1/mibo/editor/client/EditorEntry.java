package edu.tum.ls1.mibo.editor.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.MainController;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;

public class EditorEntry implements EntryPoint {

	public void onModuleLoad() {

		// Start showing the overlayer to indicate the application is loading
		LayerManager.getInstance().showLoadingOverlayer();

		try {
			new RequestBuilder(RequestBuilder.GET, "baseURL.txt").sendRequest("", new RequestCallback() {

				@Override
				public void onResponseReceived(Request req, Response resp) {

					int HTTP_CODE = resp.getStatusCode();
					String HTTP_RESPONSE = resp.getText();

					// Response needs to be 200 to proceed
					if (HTTP_CODE == 200){

						// Indicate demo mode
						if (HTTP_RESPONSE.isEmpty()) {
							LayerManager.getInstance().showToast("Demo mode activated");
						} else {
							LayerManager.getInstance().showToast("Connected to Mibo");
						}

						// Start the overall application
						new MainController(HTTP_RESPONSE);

					} else {

						String message = "";

						message += "Could not receive a base URL.\n\n";
						message += "Please update the content of baseURL.txt ";
						message += "with the an URL to MIBO. Example:\n";
						message += "http://127.0.0.1:8080/services/mibo/api/v1/rest/ \n\n";
						message += "Leave baseURL.txt empty for starting demo mode.";
						// Show error message ...
						Window.alert(message);
						
						// ... and remove the overlayer
						LayerManager.getInstance().hideLoadingOverlayer();

					}

				}

				@Override
				public void onError(Request res, Throwable throwable) {

					String message = "";

					message += "An error occured when requesting MIBO base URL\n\n";
					message += "(There might have been a timeout)";

					// Show error message ...
					Window.alert(message);

					// ... and remove the overlayer
					LayerManager.getInstance().hideLoadingOverlayer();

				}
			});

		} catch (Exception e)

		{
			// Show error message ...
			Window.alert("An unknown error occured when requesting baseURL.txt.");
			
			// ...  remove the overlayer ...
			LayerManager.getInstance().hideLoadingOverlayer();
			
			// .. and print stack trace for further investigation.
			e.printStackTrace();
			
		}
	}
}
