package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class WCLibraryArea extends AbstractWebComponent {

	public enum LibraryPage {
		FIXTURE("fixtures"),
		MODALITY("modalities"),
		CONTEXT("contexts"),
		CONTROL("controls");

		private final String className;

		LibraryPage(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}
	}

	public WCLibraryArea() {
		super();
		this.element = DOM.createElement("library-area");
	}

	public WCLibraryArea(String heading) {
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

	public void addHeading(String heading, String subheading, String description, LibraryPage type) {
		
		Element wrapper = DOM.createElement("div");
		
		Element groupTitle = DOM.createElement("p");
		Element groupSubTitle = DOM.createElement("span");
		
		groupTitle.setInnerHTML(heading);
		groupTitle.setTitle(description);
		
		groupSubTitle.setInnerHTML(subheading);
		
		wrapper.appendChild(groupTitle);
		wrapper.appendChild(groupSubTitle);
		
		wrapper.addClassName("listDelimiter");
		wrapper.addClassName(type.getClassName());
		
		this.appendChild(wrapper);
	}

	public void addItem(AbstractWebComponent webcomponent, LibraryPage type) {
		webcomponent.getElement().addClassName(type.getClassName());
		this.appendChild(webcomponent.getElement());
	}

	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}

	// -------------------- CALLBACK METHODS --------------------
	
	/* empty */
	
	// -------------------- NATIVE METHODS --------------------
		
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
}
