package edu.tum.ls1.mibo.editor.client.controller.explorer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.ExplorerController;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCListSection;

public class ListSectionController implements WCEventHandler {

	// Managing references
	private ExplorerController explorer_controller;
	private List<ListSectionItemController> item_controllers;

	// Managing internal state
	private ListSectionType type;
	private Boolean isExpanded;
	private WCListSection webcomponent;

	public ListSectionController(ListSectionType type, ExplorerController explorer_controller) {

		// Linking constructor variables
		this.type = type;
		this.explorer_controller = explorer_controller;

		// Setting up required content
		this.webcomponent = new WCListSection(this.type.getDisplayName());
		this.webcomponent.addWebComponentEventHandler(this);
		this.item_controllers = new ArrayList<ListSectionItemController>();
		this.isExpanded = false;
	}

	public void addItemController(ListSectionItemController itemController) {

		// Processing the addition of a controller
		this.item_controllers.add(itemController);
		this.webcomponent.appendChild(itemController.getWebComponent().getElement());

		// Updating internal state to changed content conditions
		if (this.type == ListSectionType.DEFINITIONS) {
			this.webcomponent.showMoreButton();
		}
	}

	public void removeItemController(ListSectionItemController itemController) {

		// Processing the removal of a controller
		this.item_controllers.remove(itemController);
		this.webcomponent.removeChild(itemController.getWebComponent().getElement());

		// Updating internal state to changed content conditions
		if (this.item_controllers.isEmpty()) {
			this.webcomponent.hideExpandButton();
		}
	}

	public Element getView() {
		return this.webcomponent.getElement();
	}

	public void reset() {
		this.webcomponent.flush();
		this.item_controllers.clear();
		this.webcomponent.hideExpandButton();
		this.isExpanded = false;
	}

	public void hightlightItem(ListSectionItemController event_controller) {
		for (ListSectionItemController controller : item_controllers) {
			if (controller == event_controller) {
				controller.getWebComponent().highlight();
			} else {
				controller.getWebComponent().unhighlight();
				controller.getWebComponent().hideDetails();
			}
		}
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case LIST_SECTION_CLICK_ADD:
			switch (this.type) {
			case TARGET_GROUPS:
				this.explorer_controller.createNewTargetgroup();
				break;
			case USERS:
				this.explorer_controller.createNewUser();
				break;
			case DEFINITIONS:
				this.explorer_controller.createNewDefinition();
				break;
			default:
				break;
			}
			break;
		case LIST_SECTION_CLICK_EXPAND:
			if (this.isExpanded) {
				for (ListSectionItemController controller : item_controllers) {
					controller.getWebComponent().unhighlight();
					controller.getWebComponent().hideDetails();
				}
				this.isExpanded = false;
				this.webcomponent.showMoreButton();
			} else {
				for (ListSectionItemController controller : item_controllers) {
					controller.getWebComponent().highlight();
					controller.getWebComponent().showDetails();
				}
				this.isExpanded = true;
				this.webcomponent.showLessButton();
			}
			break;
		case LIST_SECTION_CLICK_UNEXPAND:
			for (ListSectionItemController controller : item_controllers) {
				controller.getWebComponent().unhighlight();
				controller.getWebComponent().hideDetails();
			}
			break;
		default:
			Window.alert("Default event of List Section Item controller");
			break;
		}
	}
}
