package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCToolbarArea extends AbstractWebComponent {

	public WCToolbarArea() {
		super();
		this.element = DOM.createElement("toolbar-area");
		this.JSNI_registerEvents(this, this.element);
	}

	public WCToolbarArea(String heading) {
		this();
		this.setHeading(heading);
	}
	
	// ---------- SPECIFIC METHODS ----------

	public void setHeading(String str) {
		if (str != null && !str.equals("")) {
			this.element.setAttribute("name", str);
		} else {
			this.element.setAttribute("name", "No Name Available");
		}	}

	public String getHeading() {
		return this.element.getAttribute("name");
	}

	public void setupExplorerView() {
		this.JSNI_setupExplorerView(this.element);
	}

	public void setupEditorView() {
		this.JSNI_setupEditorView(this.element);
	}

	// ---------- CALLBACK METHODS ----------

	public void backClicked() {
		WCEvent event = new WCEvent(this, WCEventType.TOOLBAR_AREA_CLICK_BACK);
		fireEvent(event);
	}
	
	public void attributeClicked() {
		WCEvent event = new WCEvent(this, WCEventType.TOOLBAR_AREA_CLICK_ATTRIBUTE);
		fireEvent(event);
	}
	
	// ---------- NATIVE METHODS ----------
		
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
	  	element.addEventListener("back-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCToolbarArea::backClicked()();
	  	});
		element.addEventListener("attribute-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCToolbarArea::attributeClicked()();
	  	});
	}-*/;
	
	private native void JSNI_setupExplorerView(Element element) /*-{
		element.setupExplorerView();
	}-*/;

	private native void JSNI_setupEditorView(Element element) /*-{
		element.setupEditorView();
	}-*/;
	
}
