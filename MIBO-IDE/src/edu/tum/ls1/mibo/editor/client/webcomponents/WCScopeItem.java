package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCScopeItem extends AbstractWebComponent {

	public WCScopeItem() {
		super();
		this.element = DOM.createElement("scope-item");
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void initialize(String icon, String name) {

		Element iconImage = DOM.createElement("img");
		Element title = DOM.createElement("h2");

		iconImage.setAttribute("src", "components/images/icons/"+ icon + ".svg");
		title.setInnerHTML(name);

		this.appendChild(iconImage);
		this.appendChild(title);
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
	
	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}

	// -------------------- NATIVE METHODS --------------------
			
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("delete-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCScopeItem::deleteClicked()();
	  	});
	  	element.addEventListener("info-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCScopeItem::infoClicked()();
	  	});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;

}
