package edu.tum.ls1.mibo.editor.client.controller;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.explorer.ListSectionController;
import edu.tum.ls1.mibo.editor.client.controller.explorer.ListSectionItemController;
import edu.tum.ls1.mibo.editor.client.controller.explorer.ListSectionType;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCToolbarArea;
import edu.tum.ls1.mibo.editor.shared.domain.Domain;
import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;

public class ExplorerController implements WCEventHandler {

	// Controllers
	private MainController main_controller;
	private ListSectionController targetgroups_controller;
	private ListSectionController users_controller;
	private ListSectionController definitions_controllers;

	// WebComponents
	private WCToolbarArea toolbar_webcomponent;

	// Model pointers
	private TargetGroup targetgroup_reference;
	private User users_reference;
	private Definition definition_reference;

	// Model references
	private List<TargetGroup> targetgroups;

	public ExplorerController(MainController main_controller, WCToolbarArea toolbar_webcomponent) {

		// Setup references
		this.main_controller = main_controller;
		this.toolbar_webcomponent = toolbar_webcomponent;

		// Setup view
		this.initializeView();

		// Initialize controllers for naviation areas
		this.targetgroups_controller = new ListSectionController(ListSectionType.TARGET_GROUPS, this);
		this.users_controller = new ListSectionController(ListSectionType.USERS, this);
		this.definitions_controllers = new ListSectionController(ListSectionType.DEFINITIONS, this);

		// Attach controler's view represenations to host page
		DOM.getElementById("targetgroups-container").appendChild(this.targetgroups_controller.getView());
		DOM.getElementById("users-container").appendChild(this.users_controller.getView());
		DOM.getElementById("definitions-container").appendChild(this.definitions_controllers.getView());

	}

	// ---------- INITIALIZATION ----------

	public void initializeView() {

		// Setup the toolbar
		this.toolbar_webcomponent.setHeading("MIBO Explorer");
		this.toolbar_webcomponent.addWebComponentEventHandler(this);

		// In case there is a user reference, update the view
		if (this.users_reference != null) {
			this.updateView(this.users_reference);
		}

	}

	public void initializeContent(List<TargetGroup> targetgroups) {

		this.targetgroups = targetgroups;

		// For every target group, setup a controller
		for (TargetGroup targetgroup : targetgroups) {

			ListSectionItemController controller;
			controller = new ListSectionItemController(targetgroup, targetgroups_controller, this);
			this.targetgroups_controller.addItemController(controller);

		}

	}

	// ---------- GET & SET ----------

	public User getCurrentUser() {
		return this.users_reference;
	}

	public TargetGroup getCurrentTargetGroup() {
		return this.targetgroup_reference;
	}

	// ---------- DESTRUCTION ----------

	public void destruct() {

		// Show loading overlayer
		LayerManager.getInstance().showLoadingOverlayer();

		// De-register the event handler for toolbar events
		this.toolbar_webcomponent.removeWebComponentEventHandler(this);

		// Hide the explorer-specific container elements
		DOM.getElementById("explorer-container").getStyle().setDisplay(Display.NONE);
	}

	// ---------- UPDATE ----------

	public void updateView(Domain item) {

		if (item instanceof TargetGroup) {

			TargetGroup targetgroup = (TargetGroup) item;

			// Set the current references
			this.targetgroup_reference = targetgroup;
			this.users_reference = null;
			this.definition_reference = null;

			// Reset subsequent controllers
			this.users_controller.reset();
			this.definitions_controllers.reset();

			// Processing the target group's generic user
			ListSectionItemController generic_c;
			generic_c = new ListSectionItemController(targetgroup.getGenericUser(), users_controller, this);
			generic_c.getWebComponent().setGenericStyle();
			generic_c.getWebComponent().hideSubtitle();

			this.users_controller.addItemController(generic_c);

			// Processing the target group's specific users
			for (User user : targetgroup.getCustomUsers()) {

				ListSectionItemController user_c;
				user_c = new ListSectionItemController(user, users_controller, this);
				user_c.getWebComponent().hideSubtitle();

				this.users_controller.addItemController(user_c);
			}

		} else if (item instanceof User) {

			User user = (User) item;

			// Set the current references
			this.users_reference = user;
			this.definition_reference = null;

			// Reset subsequent controller
			this.definitions_controllers.reset();

			for (AbstractMiboItem definition_temp : user.getDefinitions()) {
				
				Definition definition = (Definition) definition_temp;

				// Processing the user's definitions
				ListSectionItemController definition_c;
				definition_c = new ListSectionItemController(definition, definitions_controllers, this);
				this.definitions_controllers.addItemController(definition_c);

				// Create visual representation of definition
				for (AbstractMiboItem definition_item : definition.getItems()) {

					// Create helper instance for convenience
					AbstractSection section = (AbstractSection) definition_item;
					
					// Do not show empty secitons
					if (section.getItems().isEmpty()) {
						continue;
					}

					String name = section.getAnnotation("name");
					String description = section.getAnnotation("description");
					String[][] icons = new String[section.getItems().size()][2];

					for (int i = 0; i < section.getItems().size(); i++) {

						icons[i][0] = "";

						// Set the item's image path
						icons[i][0] += "components/images/icons/";
						icons[i][0] += section.getItems().get(i).getAnnotation("icon");
						icons[i][0] += ".svg";

						// Set the item's name
						icons[i][1] = section.getItems().get(i).getAnnotation("name");
					}

					definition_c.getWebComponent().addSectionDetails(name, description, icons);
				}
			}
		} else if (item instanceof Definition) {

			Definition definition = (Definition) item;

			// Set the current reference
			this.definition_reference = definition;

		}

	}

	// ---------- SPECIFIC METHODS ----------

	public void loadDefinition(Definition definition) {
		this.main_controller.showEditorView(definition);
	}

	public void removeItem(Domain item) {

		if (item instanceof TargetGroup) {

			// Remove the target group from the overall model
			this.targetgroups.remove(item);

			// Reset the other lists in case it was selected
			if (this.targetgroup_reference == item) {
				this.users_controller.reset();
				this.definitions_controllers.reset();
				this.targetgroup_reference = null;
			}

			// Initiate delete call to server
			this.main_controller.deleteTargetGroup((TargetGroup) item);

		} else if (item instanceof User) {

			// Remove the user from the overall model
			this.targetgroup_reference.getCustomUsers().remove(item);

			// Reset the definition lists in case it was selected
			if (this.users_reference == item) {
				this.definitions_controllers.reset();
				this.users_reference = null;
			}

			// Initiate delete call to server
			this.main_controller.deleteCustomUser(this.targetgroup_reference, (User) item);

		} else if (item instanceof Definition) {

			// Remove the definition from the overall model
			this.users_reference.getDefinitions().remove(item);

			// Reset reference in case it was selected
			if (this.definition_reference == item) {
				this.definition_reference = null;
			}

			// Initiate "delete" call to server by sending an updated user post
			this.main_controller.updateUser(this.targetgroup_reference, this.users_reference);
		}

	}

	public void createNewTargetgroup() {

		String input = Window.prompt("Enter a name for a new control system", "New control system name");

		// Check if input name is valid
		if (input.equals("") || input.equals(null)) {
			return;
		} else if (!input.matches("^[a-zA-Z0-9.]*$")) {
			LayerManager.getInstance().showToast("Invalid input: Only letters, numbers and dots allowed.");
			return;
		}

		// Check if input name is already in use
		for (TargetGroup targetgroup : this.targetgroups) {
			if (targetgroup.getDisplayName().equals(input)) {
				LayerManager.getInstance().showToast("This name is already in use. Please choose a new name.");
				return;
			}
		}

		// Create a new target group based on the input
		TargetGroup targetgroup = new TargetGroup(input);

		// Add the target group to the overall model
		this.targetgroups.add(targetgroup);

		// Create a new controller for the newly created model
		ListSectionItemController controller;
		controller = new ListSectionItemController(targetgroup, targetgroups_controller, this);
		this.targetgroups_controller.addItemController(controller);
		this.targetgroups_controller.hightlightItem(controller);

		// [EXTENSION]
		// Replace the following string in case referencing the generic user has
		// changed or might need an update.

		// Create a generic user which is required for every target group
		User user = new User("de.tum.mibo.generic");
		targetgroup.setGenericUser(user);
		
		// Initiate udpdate call to server
		this.main_controller.updateUser(targetgroup, user);

		// Initiate update view method to show generic user
		this.updateView(targetgroup);

	}

	public void createNewUser() {

		if (this.targetgroup_reference == null) {
			LayerManager.getInstance().showToast("Please select a control system first.");
			return;
		}

		String message = "Enter a name for a new user within the control system '";
		message += this.targetgroup_reference.getDisplayName() + "'";
		String input = Window.prompt(message, "New user name");

		// Check if input name is valid
		if (input.equals("") || input.equals(null)) {
			return;
		} else if (!input.matches("^[a-zA-Z0-9.]*$")) {
			LayerManager.getInstance().showToast("Invalid input: Only letters, numbers and dots allowed.");
			return;
		}

		// Check if input name is already in use
		for (User user : this.targetgroup_reference.getCustomUsers()) {
			if (user.getDisplayName().equals(input)) {
				LayerManager.getInstance().showToast("This name is already in use. Please choose a new name.");
				return;
			}
		}

		// Create a new user based on the input
		User user = new User(input);

		// Add this user to the overall model
		this.targetgroup_reference.addCustomUser(user);

		// Create a new controller for the newly created model
		ListSectionItemController controller;
		controller = new ListSectionItemController(user, users_controller, this);
		controller.getWebComponent().hideSubtitle();
		this.users_controller.addItemController(controller);
		this.users_controller.hightlightItem(controller);
		
		// Initiate udpdate call to server
		this.main_controller.updateUser(this.targetgroup_reference, user);

		// Initiate update view method for creating definitions
		this.updateView(user);

	}

	public void createNewDefinition() {

		if (this.users_reference == null) {
			LayerManager.getInstance().showToast("Please select a user first.");
			return;
		}

		String message = "Enter a name for a new interaction definition for the user '";
		message += this.users_reference.getDisplayName() + "'";
		String input = Window.prompt(message, "New interaction definition name");

		// Check if input name is valid
		if (input.equals("") || input.equals(null)) {
			return;
		} else if (!input.matches("^[a-zA-Z0-9_]+( [a-zA-Z0-9_]+)*$")) {
			LayerManager.getInstance().showToast("Invalid input: Only letters, numbers and single spaces allowed.");
			return;
		}

		// Check if input name is already in use
		for (AbstractMiboItem item : this.users_reference.getDefinitions()) {
			
			Definition definition = (Definition) item;
			
			if (definition.getDisplayName().equals(input)) {
				LayerManager.getInstance().showToast("This name is already in use. Please choose a new name.");
				return;
			}
		}

		// Create a new definition ...
		Definition definition = this.main_controller.getFactory().createDefinition();
		
		// ... and update its name and ID.
		definition.updateAttribute("name", input);
		definition.updateAttribute("id", this.getID(this.getCurrentUser().getDefinitions()));
		
		// Open the definition in the editor
		this.loadDefinition(definition);

	}

	// ---------- WEBCOMPONENT ----------

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		default:
			Window.alert("Default event of Explorer Controller");
			break;
		}
	}

	// ---------- HELPER ----------

	/**
	 * 
	 * For a given list of definitions, return the next highest definition ID.
	 * <p>
	 * <ul>
	 * <li>The ID of a definition is expected to be a composition of a leading
	 * character 'd' and an integer value. Example: "d1" or "d123".</li>
	 * <li>If the size of the input list of definitions is zero, the produced
	 * definition ID will be "d1".</li>
	 * <li>Empty value ranges of IDs won't be filled. Example: Given the IDs
	 * "d1" and "d3", the newly produced ID will be "d4".</li>
	 * </ul>
	 * </p>
	 * 
	 * @param definitions
	 *            A list of definitions.
	 * @return An unique ID. In case an error occured (e.g. the definitions do
	 *         not contain a number), the return value is <code>NULL</code>.
	 */
	public String getID(List<AbstractMiboItem> definitions) {

		// Container for all existing, Integer-based definition IDs
		int[] IDs = new int[definitions.size()];

		for (int i = 0; i < definitions.size(); i++) {
			// Get the actual String-based ID, eg. "d1" or "d2"
			String temp = definitions.get(i).getAttribute("id").getValue();
			// Remove the leading "d"
			temp = temp.replace("d", "");
			try {
				// Parse the remaining String to an Integer value
				IDs[i] = Integer.parseInt(temp);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		// In case no definition exists yet, max=0 will lead to "d1"
		int max = 0;

		// Search the highest available number in the interger container
		for (int i = 0; i < IDs.length; i++) {
			if (IDs[i] > max) {
				// Found a higher value and store it in max value
				max = IDs[i];
			}
		}

		// Compose ID out of 'd' and the next highest number
		String newID = "d" + (++max);

		return newID;

	}

}
