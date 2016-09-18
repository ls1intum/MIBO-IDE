package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCListItem extends AbstractWebComponent {

	public WCListItem() {
		super();
		this.element = DOM.createElement("list-item");
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void initialize(String name, String type, String icon) {

		Element iconElement = DOM.createElement("img");
		Element titleElement = DOM.createElement("h2");
		Element descriptionElement = DOM.createElement("span");

		if (icon != null) {
			iconElement.setAttribute("src", "components/images/icons/" + icon + ".svg");
		} else {
			iconElement.setAttribute("src", "components/images/icons/item-unknown.svg");
		}

		String heading = name;
		if(name.length()>17){
			heading = name.subSequence(0, 17).toString() + "…";
		}
		titleElement.setInnerHTML(heading);
		descriptionElement.setInnerHTML(type);

		this.appendChild(iconElement);
		this.appendChild(titleElement);
		this.appendChild(descriptionElement);
	}

	// -------------------- CALLBACK METHODS --------------------

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
	
	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}

	// -------------------- NATIVE METHODS --------------------

	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("info-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListItem::infoClicked()();
		});
		element.addEventListener("start-drag", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListItem::dragStarted()();
		});
		element.addEventListener("end-drag", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListItem::dragEnded()();
		});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;

}
