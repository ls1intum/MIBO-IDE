package edu.tum.ls1.mibo.editor.client.controller.editor;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.webcomponents.WCInspectorArea;
import edu.tum.ls1.mibo.editor.client.webcomponents.AbstractWebComponent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;

public class InspectorController implements WCEventHandler {

	private WCInspectorArea webcomponent;

	public InspectorController() {
		this.initializeView();
	}

	/**
	 * Setup the controller's view<br>
	 * 1. Initialize WebComponent<br>
	 * 2. Setup the WebComponent (Register EventHandler, ...)<br>
	 * 3. Attach to the WebComponent to parent element<br>
	 */
	public void initializeView() {
		this.webcomponent = new WCInspectorArea("MIBO Inspector");
		this.webcomponent.addWebComponentEventHandler(this);
		DOM.getElementById("actions-container").appendChild(this.webcomponent.getElement());
	}

	public void setContent(String content) {
		this.webcomponent.setContent(content);
		this.webcomponent.showShadow();
		this.webcomponent.showClearButton();
	}
	
	public void addWebComponent(AbstractWebComponent webcomponent){
		this.webcomponent.appendChild(webcomponent.getElement());
		this.webcomponent.showShadow();
		this.webcomponent.showClearButton();
	}

	public void setContent(String content, int seconds) {
		this.setContent(content);
		Timer t = new Timer() {
			@Override
			public void run() {
				webcomponent.setContent("");
				webcomponent.hideClearButton();
				webcomponent.hideShadow();
			}
		};
		t.schedule(seconds * 1000);
	}
	
	public void flushContent(){
		this.webcomponent.setContent("");
		this.webcomponent.hideClearButton();
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case INSPECTOR_AREA_CLICK_CLEAR:
			this.flushContent();
			break;
		default:
			Window.alert("Default event");
			break;
		}
	}
}
