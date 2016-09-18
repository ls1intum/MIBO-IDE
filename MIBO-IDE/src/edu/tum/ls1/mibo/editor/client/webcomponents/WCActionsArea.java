package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCActionsArea extends AbstractWebComponent {

	public WCActionsArea() {
		super();
		this.element = DOM.createElement("actions-area");
		this.JSNI_registerEvents(this, this.element);
	}

	public WCActionsArea(String heading) {
		this();
		this.setHeading(heading);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void setHeading(String str) {
		this.element.setAttribute("name", str);
	}

	public String getHeading() {
		return this.element.getAttribute("name");
	}

	// -------------------- CALLBACK METHODS --------------------
	
	public void debugClicked() {
		WCEvent event = new WCEvent(this, WCEventType.ACTION_AREA_CLICK_DEBUG);
		fireEvent(event);
	}
	
	public void testClicked() {
		WCEvent event = new WCEvent(this, WCEventType.ACTION_AREA_CLICK_TEST);
		fireEvent(event);
	}
	
	public void compileClicked() {
		WCEvent event = new WCEvent(this, WCEventType.ACTION_AREA_CLICK_COMPILE);
		fireEvent(event);
	}
	
	public void applyClicked() {
		WCEvent event = new WCEvent(this, WCEventType.ACTION_AREA_CLICK_APPLY);
		fireEvent(event);
	}
	
	// -------------------- NATIVE METHODS --------------------
	
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{

	  	element.addEventListener("debug-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCActionsArea::debugClicked()();
	  	});
	  	element.addEventListener("test-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCActionsArea::testClicked()();
	  	});
		element.addEventListener("compile-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCActionsArea::compileClicked()();
	  	});
		element.addEventListener("apply-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCActionsArea::applyClicked()();
	  	});

	}-*/;
}
