package edu.tum.ls1.mibo.editor.client.controller;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.tum.ls1.mibo.editor.client.communication.MiboService;
import edu.tum.ls1.mibo.editor.client.communication.MiboServiceAsync;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCToolbarArea;
import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;

public class MainController {

	// WebComponents
	private WCToolbarArea toolbar_webcomponent;

	// Server service
	MiboServiceAsync miboService;

	// Model factory
	private MiboFactory factory;

	// Data container
	private List<TargetGroup> targetgroups;

	// Controllers
	private EditorController editorController;
	private ExplorerController explorerController;
	
	// Base URL to the mibo framework
	private String baseURL;

	public MainController(String baseURL) {

		// Set up base URL
		this.baseURL = baseURL;
		
		// Setup server service
		this.miboService = (MiboServiceAsync) GWT.create(MiboService.class);

		// Setup view elements
		this.initalizeView();

		// Initiate the loading process of models and structure
		this.loadFactory();
	}

	// ---------- INITIALIZATION ----------

	/**
	 * Initialize the main controller's view
	 * <p>
	 * This includes the initialization of the WebComponent and the attachment
	 * to the WebComponent to parent element
	 * </p>
	 */
	private void initalizeView() {

		// Create toolbar WebComponent
		this.toolbar_webcomponent = new WCToolbarArea("MIBO IDE");

		// Attach instance to the host page
		DOM.getElementById("toolbar-container").appendChild(this.toolbar_webcomponent.getElement());

	}

	// ---------- GET & SET ----------

	public MiboFactory getFactory() {
		return this.factory;
	}

	// ---------- RECEIVING ----------

	/**
	 * Load the facotry
	 * <p>
	 * In particular, this contains a prototype of a definition, a list of all
	 * fixtures, a list of all modalities and a list of all control types
	 * </p>
	 */
	public void loadFactory() {

		AsyncCallback<MiboFactory> callbackFactory = new AsyncCallback<MiboFactory>() {

			@Override
			public void onFailure(Throwable caught) {

				String message = "";

				message += "Failure when loading the model factory, which is required to continue.";
				message += "\n\n";
				message += "Possible reasons:";
				message += "\n";
				message += "- There is no network connection";
				message += "\n";
				message += "- MIBO is not running yet";
				message += "\n";
				message += "- An invalid base URL was used";
				message += "\n";
				message += "- An unknown error occured";
				message += "\n\n";
				message += "Do you want to try again?";

				if (Window.confirm(message)) {
					Window.Location.reload();
				}
				
				// Hide loading overlayer
				LayerManager.getInstance().hideLoadingOverlayer();
			}

			@Override
			public void onSuccess(MiboFactory result) {
				factory = result;
				loadData();

			}
		};

		// Initiate RPC
		miboService.getFactory(this.baseURL, callbackFactory);

	}

	/**
	 * Load the actualy model data
	 * <p>
	 * Based on the loaded model information, the structure containing the
	 * actual applied data.
	 * </p>
	 */
	private void loadData() {

		AsyncCallback<List<TargetGroup>> callbackData = new AsyncCallback<List<TargetGroup>>() {

			@Override
			public void onFailure(Throwable caught) {
				
				// Show error message
				Window.alert("Could not load model data.");
				
			}

			@Override
			public void onSuccess(List<TargetGroup> result) {
				targetgroups = result;
				startExplorerView();

			}
		};

		// Initiate RPC
		miboService.getData(this.factory, this.baseURL, callbackData);

	}

	// ---------- SENDING ----------

	public void requestComplierOutput(Definition definition) {

		AsyncCallback<String> callbackCompileDefinition = new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
				// Show error  message
				editorController.showCompilerOutput(null);
				
			}

			@Override
			public void onSuccess(String result) {

				// Make the editor to show the results in the console
				editorController.showCompilerOutput(result);
			}

		};

		// Initiate RPC
		miboService.compileDefinition(definition, callbackCompileDefinition);
	}

	public void updateUser(TargetGroup targetGroup, User user) {

		// Show loading overlayer
		LayerManager.getInstance().showLoadingOverlayer();

		AsyncCallback<Void> callbackSaveDefinition = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {

				// Hide loading overlayer
				LayerManager.getInstance().hideLoadingOverlayer();

				// If request was send from editor process callback there ...
				if (editorController != null) {
					editorController.showApplyError();
				}
				// ... otherwise just show a text information.
				else {
					// Show generic feedback, since there are different actions
					// that can trigger the update user call (adding a new
					// target group, deleting a definition, ...)
					LayerManager.getInstance().showToast("The operation failed.");
				}
			}

			@Override
			public void onSuccess(Void result) {

				// Hide loading overlayer
				LayerManager.getInstance().hideLoadingOverlayer();

				// If request was send from editor process callback there ...
				if (editorController != null) {
					editorController.showApplySuccess();
				}
				// ... otherwise just show a text information.
				else {
					// Show generic feedback, since there are different actions
					// that can trigger the update user call (adding a new
					// target group, deleting a definition, ...)
					LayerManager.getInstance().showToast("The operation was performed successfully.");
				}
			}

		};

		// Refresh MIBO's references before sending marshalling request
		user.getMIBOMibo().refreshReferences();

		// Initiate RPC
		miboService.updateUser(targetGroup, user, this.baseURL, callbackSaveDefinition);
	}

	public void deleteTargetGroup(TargetGroup targetGroup) {

		AsyncCallback<Void> callbackDeleteTargetGroup = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				LayerManager.getInstance().showToast("The control system was not deleted on the server.");
			}

			@Override
			public void onSuccess(Void result) {
				LayerManager.getInstance().showToast("The control system was deleted successfully.");
			}

		};

		// Initiate RPC
		miboService.deleteTargetGroup(targetGroup, this.baseURL, callbackDeleteTargetGroup);

	}

	public void deleteCustomUser(TargetGroup targetGroup, User customUser) {

		AsyncCallback<Void> callbackDeleteCustomUser = new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				LayerManager.getInstance().showToast("The user was not deleted on the server.");
			}

			@Override
			public void onSuccess(Void result) {
				LayerManager.getInstance().showToast("The user was deleted successfully.");
			}

		};

		// Initiate RPC
		miboService.deleteUser(targetGroup, customUser, this.baseURL, callbackDeleteCustomUser);

	}

	// ---------- MANAGING VIEWS ----------

	private void startExplorerView() {

		// Store a new explorer controller reference
		this.explorerController = new ExplorerController(this, this.toolbar_webcomponent);

		// Initialize the controller's content
		this.explorerController.initializeContent(this.targetgroups);

		// Remove loading overlayer
		LayerManager.getInstance().hideLoadingOverlayer();

	}

	public void showEditorView(Definition definition) {

		// Destruct the explorer components (e.g. the toolbar's event handler)
		this.explorerController.destruct();

		// Store a new editor controller reference
		this.editorController = new EditorController(this, this.explorerController, this.toolbar_webcomponent);

		// Initialize the controller's view
		this.editorController.initalizeView();

		// Initialize required library elements
		List<AbstractScopeItem> scopeItems = this.factory.getAvailableScopeItems();
		List<EventItemGroup> eventItemGroups = this.factory.getAvailableEventItemGroups();
		List<AbstractControlItem> controls = this.factory.getAvailableControlItems();
		this.editorController.initializeContent(definition, scopeItems, eventItemGroups, controls);

		// Update the toolbar to the editor view
		this.toolbar_webcomponent.setupEditorView();

		// Show editor container in host page
		DOM.getElementById("editor-container").getStyle().setDisplay(Display.BLOCK);

		// Remove loading overlayer
		LayerManager.getInstance().hideLoadingOverlayer();
	}

	public void showExplorerView() {

		// Destruct the editor components (e.g. the toolbar's event handler)
		this.editorController.destruct();

		// Reset reference
		this.editorController = null;

		// Setup the explorer view
		this.explorerController.initializeView();

		// Update the toolbar to the explorer view
		this.toolbar_webcomponent.setupExplorerView();

		// Show explorer container in host page
		DOM.getElementById("explorer-container").getStyle().setDisplay(Display.BLOCK);

		// Remove loading overlayer
		LayerManager.getInstance().hideLoadingOverlayer();
	}
}
