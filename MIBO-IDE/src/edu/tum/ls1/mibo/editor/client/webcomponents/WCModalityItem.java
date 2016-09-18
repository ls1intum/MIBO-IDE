package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCModalityItem extends AbstractWebComponent {

	public WCModalityItem() {
		super();
		this.element = DOM.createElement("modality-item");
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void initialize(String icon, String name, String type) {

		Element iconElement = DOM.createElement("img");
		Element titleElement = DOM.createElement("h2");
		Element descriptionElement = DOM.createElement("span");

		iconElement.setAttribute("src", "components/images/icons/" + icon + ".svg");
		
		String heading = name;
		if(name.length()>15){
			heading = name.subSequence(0, 12).toString().trim() + "…";
		}
		titleElement.setInnerHTML(heading);
		descriptionElement.setInnerHTML(type);

		this.appendChild(iconElement);
		this.appendChild(titleElement);
		this.appendChild(descriptionElement);
	}

	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}

	public void changeStateToInvalid() {
		this.JSNI_changeStateToInvalid(this.element);
	}

	public void changeStateToNone() {
		this.JSNI_changeStateToNone(this.element);
	}
	
	// -------------------- CALLBACK METHODS --------------------

	public void deleteClicked() {
		WCEvent event = new WCEvent(this, WCEventType.WEBCOMPONENT_ITEM_CLICK_DELETE);
		fireEvent(event);
	}

	public void infoClicked() {
		WCEvent event = new WCEvent(this, WCEventType.WEBCOMPONENT_ITEM_CLICK_INFO);
		fireEvent(event);
	}

	public void dragStarted() {
		WCEvent event = new WCEvent(this, WCEventType.WEBCOMPONENT_ITEM_DRAG_STARTED);
		fireEvent(event);
	}

	public void dragEnded() {
		WCEvent event = new WCEvent(this, WCEventType.WEBCOMPONENT_ITEM_DRAG_ENDED);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------
		
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("delete-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCModalityItem::deleteClicked()();
	  	});
	  	element.addEventListener("info-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCModalityItem::infoClicked()();
	  	});
	  	element.addEventListener("start-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCModalityItem::dragStarted()();
	  	});
	  	element.addEventListener("end-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCModalityItem::dragEnded()();
	  	});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
	
	private native void JSNI_changeStateToInvalid(Element element) /*-{
		element.changeStateToInvalid();
	}-*/;

	private native void JSNI_changeStateToNone(Element element) /*-{
		element.changeStateToNone();
	}-*/;

}
