package edu.tum.ls1.mibo.editor.client.controller.editor;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCActionsArea;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;

public class ActionsController implements WCEventHandler {

	private WCActionsArea webcomponent;
	private EditorController editor_controller;

	public ActionsController(EditorController editor_controller) {
		this.editor_controller = editor_controller;
		this.initializeView();
	}

	/**
	 * Setup the controller's view<br>
	 * 1. Initialize WebComponent<br>
	 * 2. Setup the WebComponent (Register EventHandler, ...)<br>
	 * 3. Attach to the WebComponent to parent element<br>
	 */
	public void initializeView() {
		this.webcomponent = new WCActionsArea("Actions");
		this.webcomponent.addWebComponentEventHandler(this);
	}
	
	public Element getView(){
		return this.webcomponent.getElement();
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case ACTION_AREA_CLICK_DEBUG:
			this.editor_controller.debug();
			break;
		case ACTION_AREA_CLICK_TEST:
			this.editor_controller.test();
			break;
		case ACTION_AREA_CLICK_COMPILE:
			this.editor_controller.compile();
			break;
		case ACTION_AREA_CLICK_APPLY:
			this.editor_controller.apply();
			break;
		default:
			Window.alert("Default event");
			break;
		}
	}
}
