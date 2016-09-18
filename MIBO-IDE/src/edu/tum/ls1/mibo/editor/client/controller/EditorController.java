package edu.tum.ls1.mibo.editor.client.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.editor.ActionsController;
import edu.tum.ls1.mibo.editor.client.controller.editor.DefinitionController;
import edu.tum.ls1.mibo.editor.client.controller.editor.InspectorController;
import edu.tum.ls1.mibo.editor.client.controller.editor.InspectorItemController;
import edu.tum.ls1.mibo.editor.client.controller.editor.LibraryController;
import edu.tum.ls1.mibo.editor.client.utility.ConflictManager;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleManager;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleActionType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCToolbarArea;
import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;

public class EditorController implements WCEventHandler {

	// Controllers
	private MainController main_c;
	private ExplorerController explorer_c;
	private InspectorController inspector_c;
	private LibraryController library_c;
	private DefinitionController definition_c;

	// WebComponents
	private WCToolbarArea toolbar_webcomponent;
	private WCConsoleArea console_webcomponent;

	// Currently dragged item
	private AbstractMiboItem draggedItem;

	// Definition reference to the latest version that has been saved
	private Definition saved_definition;

	// Definition reference to the version being saved, but might be rejected
	private Definition transfered_definition;

	// Definition reference to the version that is being edited in the editor
	private Definition working_definition;

	public EditorController(MainController main_c, ExplorerController explorer_c, WCToolbarArea toolbar_webcomponent) {

		// Setup references
		this.main_c = main_c;
		this.explorer_c = explorer_c;
		this.toolbar_webcomponent = toolbar_webcomponent;

		// Make sure reference is NULL for drag'n'drop mechanism
		this.draggedItem = null;

	}

	// ---------- INITIALIZATION ----------

	/**
	 * Setup the controller's view
	 * <p>
	 * This includes the initialization of the WebComponent, the setup the
	 * WebComponent (Register EventHandler, ...), and the attachment to the
	 * WebComponent to parent element
	 * </p>
	 */
	public void initalizeView() {

		// Create a new actions controller
		ActionsController actions_controller = new ActionsController(this);

		// Create a new console area and attach event handlers
		this.console_webcomponent = new WCConsoleArea("Console");
		this.console_webcomponent.addWebComponentEventHandler(this);

		// Setup the toolbar
		this.toolbar_webcomponent.setHeading("MIBO Editor");
		this.toolbar_webcomponent.addWebComponentEventHandler(this);

		// Attach the console to the host page and hide it by default
		DOM.getElementById("console-container").appendChild(this.console_webcomponent.getElement());
		DOM.getElementById("console-container").getStyle().setDisplay(Display.NONE);

		// Attach the actions view to the host page
		DOM.getElementById("actions-container").appendChild(actions_controller.getView());
	}

	public void initializeContent(Definition definition, List<AbstractScopeItem> scopeItems,
			List<EventItemGroup> eventItemGroups, List<AbstractControlItem> controlItems) {

		// Store the original definition that is already stored on the server or
		// that has just been created and is not yet transmitted to the server
		this.saved_definition = definition;

		// Store a copy of the original definition as working definition
		this.working_definition = (Definition) definition.getCopy();

		// Create controllers based on the received data
		this.inspector_c = new InspectorController();
		this.library_c = new LibraryController(eventItemGroups, scopeItems, controlItems, this);
		this.definition_c = new DefinitionController(this.working_definition, this);

		// Update toolbar heading
		this.toolbar_webcomponent.setHeading(definition.getDisplayName());
	}

	// ---------- DESTRUCTION ----------

	public void destruct() {

		// Show loading overlayer
		LayerManager.getInstance().showLoadingOverlayer();

		// De-register the event handler for toolbar events
		this.toolbar_webcomponent.removeWebComponentEventHandler(this);

		// Hide the editor-specific container elements
		DOM.getElementById("editor-container").getStyle().setDisplay(Display.NONE);

		// Remove all WebComponent elements that have been created so far
		DOM.getElementById("console-container").removeAllChildren();
		DOM.getElementById("sections-container-top").removeAllChildren();
		DOM.getElementById("sections-container-bottom").removeAllChildren();
		DOM.getElementById("actions-container").removeAllChildren();

	}

	// ---------- GET/SET ----------

	public InspectorController getInspectorController() {
		return this.inspector_c;
	}

	public LibraryController getLibraryController() {
		return this.library_c;
	}

	public void setDraggedItem(AbstractMiboItem item) {
		this.draggedItem = item;
	}

	public AbstractMiboItem getDraggedItem() {
		return this.draggedItem;
	}

	public void clearDraggedItem() {
		this.draggedItem = null;
	}

	public void updateHeading(String heading) {
		this.toolbar_webcomponent.setHeading(heading);
	}

	// ---------- CALLBACKS ----------

	public void showCompilerOutput(String output) {

		// Hide overlayer
		LayerManager.getInstance().hideLoadingOverlayer();

		// Only show valide output
		if (output == null) {

			// Either there was a server error or the definition is in-complete
			String error_message = "The interaction definition is either incomplete or a server error occured.";
			this.console_webcomponent.addMessage(ConsoleLogType.ERROR, error_message);

		} else {

			// The output text is pretty-formated, while the console can only
			// display single lines. Therefore, split the console output ...
			String[] lines = output.split("\n");

			// ... and display every single line seperatly.
			for (String line : lines) {

				// Escaping the html tags ...
				line = line.replaceAll("\\s", "&nbsp;");
				line = line.replaceAll("<", "&lt;");
				line = line.replaceAll(">", "&gt;");

				// ... before adding the line to the console.
				this.console_webcomponent.addMessage(ConsoleLogType.INFO, line);
			}
		}

		// Add delimiter to indicate that a running action has ended
		this.console_webcomponent.addStopDelimiter(ConsoleActionType.COMPILE);

	}

	public void showApplySuccess() {

		// Add success text to console
		this.console_webcomponent.addMessage(ConsoleLogType.SUCCESS, "The interaction definition was applied.");

		// Store a definition reference as the latest, saved definition
		this.saved_definition = this.transfered_definition;

		// Add delimiter to indicate that a running action has ended
		this.console_webcomponent.addStopDelimiter(ConsoleActionType.APPLY);
	}

	public void showApplyError() {

		// Replace transfer definition with the saved definition
		User user = this.explorer_c.getCurrentUser();
		int index = user.getDefinitions().indexOf(this.transfered_definition);
		user.getDefinitions().remove(index);
		user.getDefinitions().add(index, this.saved_definition);

		String message = "";
		message += "The interaction definition could not be applied: ";
		message += "There might be a problem with the server.";

		// Add error text to console
		this.console_webcomponent.addMessage(ConsoleLogType.ERROR, message);

		// Add delimiter to indicate that a running action has ended
		this.console_webcomponent.addStopDelimiter(ConsoleActionType.APPLY);

	}

	// ---------- ACTIONS ----------

	public void debug() {

		// Show the console
		DOM.getElementById("console-container").getStyle().setDisplay(Display.INLINE_BLOCK);

		// Add delimiter to indicate that a new action output has started
		this.console_webcomponent.addStartDelimiter(ConsoleActionType.DEBUG);

		// Step 1. — Check Completness

		// Initiate check if the definition is complete
		List<ConsoleItem> completeLog = this.checkDefinitionCompletness();

		if (completeLog.isEmpty()) {
			this.console_webcomponent.addMessage(ConsoleLogType.SUCCESS, "Current interaction definition <b>"
					+ this.working_definition.getAttribute("id").getValue() + "</b> is complete.");
		} else {

			// For every event occured during debugging, print a message
			for (ConsoleItem item : completeLog) {
				this.console_webcomponent.addMessage(item.getType(), item.getMessage());
			}
			// Add delimiter to indicate that a running action has ended
			this.console_webcomponent.addStopDelimiter(ConsoleActionType.DEBUG);

			// Stop execution of debug
			return;
		}

		// Step 2. – Check Conflict Management
		List<ConsoleItem> conflictLog = this.checkDefinitionConflicts();

		if (ConsoleManager.containsWarnings(conflictLog) || ConsoleManager.containsErrors(conflictLog)) {

			for (ConsoleItem item : conflictLog) {
				this.console_webcomponent.addMessage(item.getType(), item.getMessage());
			}
		} else {

			this.console_webcomponent.addMessage(ConsoleLogType.SUCCESS,
					"Current interaction definition <b>" + this.working_definition.getAttribute("id").getValue()
							+ "</b> is conflict-free.");
		}

		// Add delimiter to indicate that a running action has ended
		this.console_webcomponent.addStopDelimiter(ConsoleActionType.DEBUG);

	}

	public void test() {

		// ### Entry point for future work ###

	}

	public void compile() {

		// Show overlayer
		LayerManager.getInstance().showLoadingOverlayer();

		// Make the console visible
		DOM.getElementById("console-container").getStyle().setDisplay(Display.INLINE_BLOCK);

		// Add delimiter to indicate that a new action output has started
		this.console_webcomponent.addStartDelimiter(ConsoleActionType.COMPILE);

		// A definition needs to be complete to perform the compile action
		if (this.checkDefinitionCompletness().isEmpty()) {

			// Initiate RPC to request compiler output for current definition
			this.main_c.requestComplierOutput(this.definition_c.getModel());

		} else {

			// Show an error
			this.showCompilerOutput(null);

		}

	}

	public void apply() {

		// Show the console
		DOM.getElementById("console-container").getStyle().setDisplay(Display.INLINE_BLOCK);

		// Add delimiter to indicate that a new action output has started
		this.console_webcomponent.addStartDelimiter(ConsoleActionType.APPLY);

		// A minimum requirement for a definition to be applied is completeness.
		// This can be guaranteed when the returning list is empty.
		if (!this.checkDefinitionCompletness().isEmpty()) {

			// Add error text to console
			this.console_webcomponent.addMessage(ConsoleLogType.ERROR, "The interaction definition is incomplete.");

			// Add delimiter to indicate that a running action has ended
			this.console_webcomponent.addStopDelimiter(ConsoleActionType.APPLY);

			// Exit apply action
			return;
		}

		// For conflict check, errors and warnings can occure ...
		Boolean isConflictFree = true;

		// ... since a definition can be applied containing warnings ..
		for (ConsoleItem temp_item : this.checkDefinitionConflicts()) {
			if (temp_item.getType() == ConsoleLogType.ERROR) {
				// ... only cases with errors lead to definition rejection.
				isConflictFree = false;
			}
		}

		// A definition can be applied, it it is complete and conflict-free
		if (isConflictFree) {

			// Get context
			TargetGroup targetGroup = this.explorer_c.getCurrentTargetGroup();
			User user = this.explorer_c.getCurrentUser();
			Mibo mibo = user.getMIBOMibo();

			// Retrieve definition reference in the overall definition set
			Definition existing_definition = mibo.containsDefinition(working_definition);

			// If it is null, the definition was just created.
			if (existing_definition == null) {

				// Create the transfer definition ...
				transfered_definition = (Definition) working_definition.getCopy();

				// ... and add it to the overall set
				user.getDefinitions().add(transfered_definition);

			}
			// Otherwise, the definition already exisits ...
			else {

				// ... and its content hasn't changed.
				if (existing_definition.matches(working_definition)) {

					String message = "";
					message += "The interaction definition was not applied: ";
					message += "There are no new changes.";

					// Add info text to console
					this.console_webcomponent.addMessage(ConsoleLogType.INFO, message);

					// Add delimiter to indicate that a running action has ended
					this.console_webcomponent.addStopDelimiter(ConsoleActionType.APPLY);

					// Leave the apply mechanism
					return;

				}
				// ... or its content actually changed.
				else {

					// Replace existing definition with working definition copy
					int index = mibo.getItems().indexOf(existing_definition);
					mibo.getItems().remove(existing_definition);

					// Create the transfer definition ...
					transfered_definition = (Definition) working_definition.getCopy();

					// ... and add it to the overall set
					mibo.getItems().add(index, transfered_definition);
				}

			}

			// Initiate server call to update the definitions
			main_c.updateUser(targetGroup, user);

		} else {

			// Add error text to console
			this.console_webcomponent.addMessage(ConsoleLogType.ERROR,
					"The current interaction definition is not conflict-free.");

			// Add delimiter to indicate that a running action has ended
			this.console_webcomponent.addStopDelimiter(ConsoleActionType.APPLY);
		}

	}

	// ---------- VALIDATION ----------

	/**
	 * Get log of events occured during complete check
	 * <p>
	 * Detailled description.
	 * </p>
	 * 
	 * @return A list of items describing the occured events with a type and
	 *         message. Size of the list is 0 in case no events occured.
	 */
	public List<ConsoleItem> checkDefinitionCompletness() {

		List<ConsoleItem> validationLog = new ArrayList<ConsoleItem>();

		// Added check for null, since the check can acutally be called before
		// the definitoin controller is creaded by the control item controller.
		// This happens when loading an existing definition into the editor.
		if (definition_c != null) {
			this.definition_c.check(validationLog);
		}

		return validationLog;
	}

	/**
	 * Get log of events occured during conflict check
	 * <p>
	 * Detailled description.
	 * </p>
	 * 
	 * @return A list of items describing the occured events with a type and
	 *         message. Size of the list is 0 in case no events occured.
	 */
	public List<ConsoleItem> checkDefinitionConflicts() {

		List<ConsoleItem> validationLog = new ArrayList<ConsoleItem>();

		// Set up confliction manager with validation log reference
		ConflictManager cm = new ConflictManager(validationLog);

		// Set up Conflict Manager with required information
		cm.setUser(this.explorer_c.getCurrentUser());
		cm.setDefinitions(this.saved_definition, this.working_definition);

		// Start the Conflict Manager to write information to validation log
		cm.analyis();

		return validationLog;
	}

	// ---------- WEBCOMPONENT ----------

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case TOOLBAR_AREA_CLICK_BACK:
			// If no changes have been made, simpy go back to explorer ...
			if (this.saved_definition.matches(this.working_definition)) {
				this.main_c.showExplorerView();
			}
			// ... else ask for user's confirmation.
			else {

				String message = "";
				message += "There are unsaved changes.";
				message += "\n\n";
				message += "Returning to the explorer will discard the unsaved changes.";

				if (Window.confirm(message)) {
					this.main_c.showExplorerView();
				}
			}

			break;
		case TOOLBAR_AREA_CLICK_ATTRIBUTE:
			// ItemController and ContainerController method variables are null
			// since they won't be used: A definition has not visual
			// representation; in particular, it cannot be removed from any
			// container or a similar wrapper.
			InspectorItemController inspectorItem = new InspectorItemController(this.definition_c.getModel(), null,
					null, this.getInspectorController(), this);
			this.getInspectorController().addWebComponent(inspectorItem.getWebComponent());
			break;
		case CONSOLE_AREA_CLICK_CLEAR:
			// Hide the console
			DOM.getElementById("console-container").getStyle().setDisplay(Display.NONE);
			// Flush the current content
			this.console_webcomponent.flush();
			break;
		default:
			Window.alert("Default event");
			break;
		}
	}
}
