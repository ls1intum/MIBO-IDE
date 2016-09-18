package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCListSectionItem extends AbstractWebComponent {

	public WCListSectionItem() {
		super();
		this.element = DOM.createElement("list-section-item");
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

		String heading = "<i style=\"color:grey\">No Name Available</i>";
		
		// In case defition.getDisplayName() is NULL
		if(name != null){
			heading = name;
		}
		
		if (heading.length() > 60) {
			heading = heading.subSequence(0, 60).toString() + "…";
		}
		titleElement.setInnerHTML(heading);
		descriptionElement.setInnerHTML(type);

		this.appendChild(iconElement);
		this.appendChild(titleElement);
		this.appendChild(descriptionElement);
	}

	public void highlight() {
		this.JSNI_highlight(this.element);
	}

	public void unhighlight() {
		this.JSNI_unhighlight(this.element);
	}
	
	public void appendChild(Element child) {
		this.JSNI_appendChild(this.element, child);
	}

	public void setGenericStyle(){
		this.JSNI_genericStyle(this.element);
	}
	
	public void hideSubtitle(){
		this.JSNI_hideSubtitle(this.element);
	}
	
	public void showDetails(){
		this.JSNI_showDetails(this.element);
	}
	
	public void hideDetails() {
		this.JSNI_hideDetails(element);
	}
	
	public void addSectionDetails(String name, String description, String[][] icon){
		this.JSNI_addSectionDetails(this.element, name, description, icon);
	}
	
	// -------------------- CALLBACK METHODS --------------------

	public void deleteClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_ITEM_DELETE);
		fireEvent(event);
	}

	public void nextClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_ITEM_NEXT);
		fireEvent(event);
	}
	
	public void editClicked() {
		WCEvent event = new WCEvent(this, WCEventType.LIST_SECTION_ITEM_EDIT);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------

	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("delete-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSectionItem::deleteClicked()();
		});
		element.addEventListener("next-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSectionItem::nextClicked()();
		});
		element.addEventListener("edit-click", function(e, d, b) {
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCListSectionItem::editClicked()();
		});
	}-*/;

	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;

	private native void JSNI_highlight(Element element) /*-{
		element.highlight();
	}-*/;

	private native void JSNI_unhighlight(Element element) /*-{
		element.unhighlight();
	}-*/;
	
	private native void JSNI_genericStyle(Element element) /*-{
		element.genericStyle();
	}-*/;
	
	private native void JSNI_hideSubtitle(Element element) /*-{
		element.hideSubtitle();
	}-*/; 
	
	private native void JSNI_showDetails(Element element) /*-{
		element.showDetails();
	}-*/; 

	private native void JSNI_hideDetails(Element element) /*-{
		element.hideDetails();
	}-*/; 
	
	private native void JSNI_addSectionDetails(Element element, String name, String description, String[][] icon)/*-{
		element.addSectionDetails(name, description, icon);
	}-*/;

}
