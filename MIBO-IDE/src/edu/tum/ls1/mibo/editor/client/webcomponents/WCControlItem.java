package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCControlItem extends AbstractWebComponent {

	public WCControlItem() {
		super();
		this.element = DOM.createElement("control-item");
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------
	
	public void initialize(String icon, String name, String type) {

		Element iconElement = DOM.createElement("img");
		Element titleElement = DOM.createElement("h2");
		Element descriptionElement = DOM.createElement("span");

		iconElement.setAttribute("src", "components/images/icons/" + icon + ".svg");
		titleElement.setInnerHTML(name);
		descriptionElement.setInnerHTML(type);

		this.appendChild(iconElement);
		this.appendChild(titleElement);
		this.appendChild(descriptionElement);
	}

	public void changeAdditionToApplicable() {
		this.JSNI_applicable(this.element);
	}

	public void changeAdditionToInapplicable() {
		this.JSNI_inapplicable(this.element);
	}
	
	public void changeAdditionToStandard() {
		this.JSNI_default(this.element);
	}
	
	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}
	
	public void removeChild(Element child){
		this.JSNI_removeChild(this.element, child);
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
	
	public void enterDragged() {
		WCEvent event = new WCEvent(this, WCEventType.CONTROL_ITEM_DRAG_ENTER);
		fireEvent(event);
	}

	public void leftDragged() {
		WCEvent event = new WCEvent(this, WCEventType.CONTROL_ITEM_DRAG_LEAVE);
		fireEvent(event);
	}

	public void dropped() {
		WCEvent event = new WCEvent(this, WCEventType.CONTROL_ITEM_DROP);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------
		
	public native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("delete-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem::deleteClicked()();
	  	});
	  	element.addEventListener("info-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem::infoClicked()();
	  	});
	  	element.addEventListener("enter-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem::enterDragged()();
	  	});
	  	element.addEventListener("leave-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem::leftDragged()();
	  	});
	  	element.addEventListener("drop-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem::dropped()();
	  	});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		
		// Append the modality to the control element
		$wnd.appendChildToElement(element, child);
		
		// Configure appearance of elements
		if (child.nodeName == "MODALITY-ITEM") {
			
			// Remove buttom line of modality's view
			child.insideControl();
			
			// Hide the control view's info text
			element.hideInfotext();
		}
		
	}-*/;
	
	private native void JSNI_applicable(Element element) /*-{
		element.applicable();
	}-*/;

	private native void JSNI_inapplicable(Element element) /*-{
		element.inapplicable();
	}-*/;
	
	private native void JSNI_default(Element element) /*-{
		element.standard();
	}-*/;

	private native void JSNI_removeChild(Element element, Element child) /*-{
		element.removeOldChild(child);
	}-*/;
	
	private native void JSNI_changeStateToInvalid(Element element) /*-{
		element.changeStateToInvalid();
	}-*/;

	private native void JSNI_changeStateToNone(Element element) /*-{
		element.changeStateToNone();
	}-*/;

}
