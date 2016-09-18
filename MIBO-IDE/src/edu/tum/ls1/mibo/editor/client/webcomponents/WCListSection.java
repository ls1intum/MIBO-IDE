package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCListSection extends AbstractWebComponent {

	private int number = 0;
	
	public WCListSection(String heading) {
		super();
		this.element = DOM.createElement("list-section");
		this.setHeading(heading);
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void setHeading(String str) {
		this.element.setAttribute("name", str);
		this.element.setAttribute("number", String.valueOf(number));
	}

	public String getHeading() {
		return this.element.getAttribute("name");
	}

	public void appendChild(Element child) {
		
		// Append the child to the element
		this.JSNI_appendChild(this.element, child);
		
		// Update internal counter
		number++;
		
		// Update view accordingly
		this.element.setAttribute("number", String.valueOf(number));

	}

	public void removeChild(Element child) {
		
		// Remove the child from the element
		this.JSNI_removeChild(this.element, child);
		
		// Update internal counter
		number--;

		// Update view accordingly
		this.element.setAttribute("number", String.valueOf(number));
	}

	public void flush() {
		
		// Remove all children form element
		this.JSNI_flush(this.element);
		
		// Reset counter
		number = 0;
		
		// Update view accordingly
		this.element.setAttribute("number", String.valueOf(number));
	}
	
	public void showMoreButton(){
		this.JSNI_showMoreButton(this.element);
	}
	
	public void showLessButton(){
		this.JSNI_showLessButton(this.element);
	}
	
	public void hideExpandButton(){
		this.JSNI_hideExpandButton(this.element);
	}
	
	// -------------------- CALLBACK METHODS ------------------
	
	public void addClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_CLICK_ADD);
		fireEvent(event);
	}

	public void expandClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_CLICK_EXPAND);
		fireEvent(event);
	}
	
	public void unexpandClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_CLICK_UNEXPAND);
		fireEvent(event);
	}
	
	// -------------------- NATIVE METHODS --------------------
	
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("add-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSection::addClicked()();
		});
		element.addEventListener("expand-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSection::expandClicked()();
		});
		element.addEventListener("unexpand-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSection::unexpandClicked()();
		});
	}-*/;

	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;

	private native void JSNI_removeChild(Element element, Element child) /*-{
		element.removeOldChild(child);
	}-*/;

	private native void JSNI_flush(Element element) /*-{
		element.flush();
	}-*/;
	
	private native void JSNI_showMoreButton(Element element) /*-{
		element.showMoreButton();
	}-*/;
	
	private native void JSNI_showLessButton(Element element) /*-{
		element.showLessButton();
	}-*/;
	
	private native void JSNI_hideExpandButton(Element element) /*-{
		element.hideExpandButton();
	}-*/;
	
}
