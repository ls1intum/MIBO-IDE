package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCInspectorArea extends AbstractWebComponent {

	public WCInspectorArea() {
		super();
		this.element = DOM.createElement("inspector-area");
	}

	public WCInspectorArea(String heading) {
		this();
		this.setHeading(heading);
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void setHeading(String str) {
		this.element.setAttribute("name", str);
	}

	public String getHeading() {
		return this.element.getAttribute("name");
	}

	public void setContent(String content) {
		Element content_element = DOM.createElement("span");
		content_element.setInnerHTML(content);
		this.appendChild(content_element);
	}

	public void showShadow() {
		this.JSNI_showShadow(this.element);
	}

	public void hideShadow() {
		this.JSNI_hideShadow(this.element);
	}

	public void showClearButton() {
		this.JSNI_showClearButton(this.element);
	}

	public void hideClearButton() {
		this.JSNI_hideClearButton(this.element);
	}
	
	public void appendChild(Element child){
		this.flush();
		this.JSNI_appendChild(this.element, child);
	}
	
	public void flush(){
		this.JSNI_flush(this.element);
	}

	// -------------------- CALLBACK METHODS --------------------

	public void clearClicked() {
		WCEvent event = new WCEvent(this, WCEventType.INSPECTOR_AREA_CLICK_CLEAR);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------
	
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("clear-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCInspectorArea::clearClicked()();
		});
	}-*/;
	
	private native void JSNI_showShadow(Element element) /*-{
		element.showShadow();
	}-*/;
	
	private native void JSNI_hideShadow(Element element) /*-{
		element.hideShadow();
	}-*/;
	
	private native void JSNI_showClearButton(Element element) /*-{
		element.showClearButton();
	}-*/;
	
	private native void JSNI_hideClearButton(Element element) /*-{
		element.hideClearButton();
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
	
	private native void JSNI_flush(Element element) /*-{
		element.flush();
	}-*/;
	
	
}
