package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCDefinitionSection extends AbstractWebComponent {
	
	public WCDefinitionSection() {
		super();
		this.element = DOM.createElement("definition-section");
		this.JSNI_registerEvents(this, this.element);
	}

	public WCDefinitionSection(String heading) {
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

	public void setHorizontalLayout() {
		this.element.setAttribute("horizontal", "true");
	}

	public void changeStateToInvalid() {
		this.JSNI_changeStateToInvalid(this.element);
	}

	public void changeStateToNone() {
		this.JSNI_changeStateToNone(this.element);
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

	// -------------------- CALLBACK METHODS --------------------

	public void addClicked() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_CLICK_ADD);
		fireEvent(event);
	}

	public void infoClicked() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_CLICK_INFO);
		fireEvent(event);
	}
	
	public void stateClicked() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_CLICK_STATE);
		fireEvent(event);
	}

	public void enterDragged() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_DRAG_ENTER);
		fireEvent(event);
	}

	public void leftDragged() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_DRAG_LEAVE);
		fireEvent(event);
	}

	public void Dropped() {
		WCEvent event = new WCEvent(this, WCEventType.DEFINITION_SECTION_DROP);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------
		
	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("add-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::addClicked()();
	  	});
	  	element.addEventListener("info-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::infoClicked()();
	  	});
	  	element.addEventListener("state-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::stateClicked()();
	  	});
	  	element.addEventListener("enter-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::enterDragged()();
	  	});
	  	element.addEventListener("leave-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::leftDragged()();
	  	});
	  	element.addEventListener("drop-drag", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection::Dropped()();
	  	});
	}-*/;
		
	private native void JSNI_changeStateToInvalid(Element element) /*-{
		element.changeStateToInvalid();
	}-*/;
	
	private native void JSNI_changeStateToNone(Element element) /*-{
		element.changeStateToNone();
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
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
	
	private native void JSNI_removeChild(Element element, Element child) /*-{
		element.removeOldChild(child);
	}-*/;
}
