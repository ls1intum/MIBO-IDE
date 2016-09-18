package edu.tum.ls1.mibo.editor.client.webcomponents;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.items.FixtureItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public class WCInspectorItem extends AbstractWebComponent {

	/**
	 * The instance variable <code>inputModelMap</code> is used to keep track of
	 * the displayed item's model and its visual representation. This is
	 * especially inportant, when the user clicks on "Save".
	 */
	private Map<Attribute, Object> inputModelMap;
	
	/**
	 * The item that will be used for displaying attributes.
	 */
	private AbstractMiboItem item;
	
	public WCInspectorItem() {
		super();
		this.inputModelMap = new HashMap<Attribute, Object>();
		this.element = DOM.createElement("inspector-item");
		this.item = null;
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void initialize(AbstractMiboItem item) {

		this.item = item;
		
		Element iconElement = DOM.createElement("img");
		Element titleElement = DOM.createElement("h2");
		Element typeElement = DOM.createElement("h3");
		Element descriptionElement = DOM.createElement("p");

		if (item.getAnnotation("icon") != null) {
			iconElement.setAttribute("src", "components/images/icons/" + item.getAnnotation("icon") + ".svg");
		} else {
			iconElement.setAttribute("src", "components/images/icons/item-unknown.svg");
		}

		titleElement.setInnerHTML(item.getAnnotation("name"));
		typeElement.setInnerHTML(item.getAnnotation("type"));
		descriptionElement.setInnerHTML(item.getAnnotation("description"));

		this.appendChild(iconElement);
		this.appendChild(titleElement);
		this.appendChild(typeElement);
		this.appendChild(descriptionElement);

		// A defintion cannot be removed from itself.
		// Therefore, hide the remove button.
		if (item instanceof Definition) {
			this.hideRemoveButton();
		}
		
		// Do not show any options for items that don't contain attributes or
		// are of a special kind (e.g. fixtures). However, inform the user.
		if (item.getAttributes().isEmpty() || item instanceof FixtureItem) {

			Element wrapper = DOM.createElement("div");
			wrapper.addClassName("item_wrapper");
			wrapper.addClassName("layout horizontal center");

			Label label = new Label();
			label.setText("This element can not be customized.");
			label.setTitle("There are no further steps required for using this element.");
			label.setStyleName("label_name");
			label.addStyleName("flex");
			label.getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
			label.getElement().getStyle().setColor("lightgrey");

			wrapper.appendChild(label.getElement());

			this.appendChild(wrapper);

			// Save button can be hidden since no attributes can be edited
			this.hideSaveButton();

		} else {
			
			// Add representation for attributes to this inspector item
			for (Attribute attribute : item.getAttributes()) {
				this.addItem(attribute);
			}
		}

	}
	
	public void addItem(Attribute attribute){
		
		Element wrapper = DOM.createElement("div");
		wrapper.addClassName("item_wrapper");
		wrapper.addClassName("layout horizontal center-center");

		Label label_name = new Label();
		label_name.setText(attribute.getAnnotation("name"));
		label_name.setTitle(attribute.getAnnotation("description"));
		label_name.setStyleName("label_name");
		
		wrapper.appendChild(label_name.getElement());
		
		// Depending on the type of input, show different representations of input
		if(attribute.getConstrainValue("type").equals("xs:boolean")){
			
			Element checkbox = DOM.createElement("paper-toggle-button");
			
			// Display default values correctly
			if (attribute.getValue() == null && attribute.getConstrainValue("default") != null) {

				if (attribute.getConstrainValue("default").equals("true")) {
					checkbox.setPropertyString("checked", "true");
				} else {
					// Is not required, since only the tag "checked" needs to be
					// defined (no matter which value is added)
				}

			}
			
			// Set-up element with existing value
			if(attribute.getValue().equals("true")){
				checkbox.setPropertyString("checked", "true");
			}
			
			// Add item's attribuet representation to wrapper
			wrapper.appendChild(checkbox);
			
			// Create mapping link
			inputModelMap.put(attribute, checkbox);

		} else if (attribute.getConstrainValue("type").equals("xs:string")
				|| attribute.getConstrainValue("type").equals("xs:integer")
				|| attribute.getConstrainValue("type").equals("valueType")
				|| attribute.getConstrainValue("type").equals("xs:ID")) {

			Element wrapper_input = DOM.createElement("div");
			wrapper_input.addClassName("flex");
			
			TextBox textBox = new TextBox();
			textBox.setText(attribute.getValue());
			textBox.setStyleName("textbox");
			
			// [EXTENSION]
			// Currently, default values are not presented. This is because
			// default values usually assume an "optional use", which is handled
			// with the following sequence of code. In case default values
			// should be displayed in favour of "use" values, update the
			// following code sequence.

			if (attribute.getConstrainValue("use").equals("optional")){
				textBox.getElement().setAttribute("placeholder", "Optional");
			}
			else if(attribute.getConstrainValue("use").equals("required")){
				textBox.getElement().setAttribute("placeholder", "Required");
			}

			// xs:ID is used inside of a definition and is not meant to be
			// changed. Therefore, disable the textbox.
			if (attribute.getConstrainValue("type").equals("xs:ID")) {
				textBox.setEnabled(false);
			}
			
			// Only Set or Incremental (=Increase/Decrease) types can handle
			// modality input. Show this information as a placeholder.
			if (this.item.getAnnotation("name").equals("Set") ||
					this.item.getAnnotation("name").equals("Increase") || 
					this.item.getAnnotation("name").equals("Decrease")){
				textBox.getElement().setAttribute("placeholder", "Modality Input");
			}
			
			// Add item's attribuet representation to wrapper
			wrapper_input.appendChild(textBox.getElement());
			
			wrapper.appendChild(wrapper_input);
			
			Label label_measurement = new Label();
			label_measurement.setText(attribute.getAnnotation("measurement"));
			label_measurement.setStyleName("label_measurement");
			
			wrapper.appendChild(label_measurement.getElement());
			
			// Create mapping link
			inputModelMap.put(attribute, textBox);
		}
		
		this.appendChild(wrapper);
		
	}

	// -------------------- CALLBACK METHODS --------------------

	public void saveClicked() {
		
		// Save the information from the input elements
		// in the corresponding attribute models
		// Note: This does not contain any validation since this is done by
		// every model on its own.
		for (Map.Entry<Attribute, Object> entry : inputModelMap.entrySet())
		{
			if(entry.getValue() instanceof TextBox){
				TextBox text = (TextBox)entry.getValue();
				entry.getKey().setValue(text.getText());
			}
			else if(entry.getValue() instanceof Element){
				Element checkbox = (Element)entry.getValue();
				entry.getKey().setValue(checkbox.getPropertyString("checked"));
			}
		}
		WCEvent event = new WCEvent(this, WCEventType.INSPECTOR_ITEM_CLICK_SAVE);
		fireEvent(event);
	}

	public void removeClicked() {
		WCEvent event = new WCEvent(this, WCEventType.INSPECTOR_ITEM_CLICK_REMOVE);
		fireEvent(event);
	}
	
	public void appendChild(Element child){
		this.JSNI_appendChild(this.element, child);
	}
	
	public void enableLibraryMode(){
		this.JSNI_enableLibraryMode(this.element);
	}
	
	public void hideSaveButton(){
		this.JSNI_hideSaveButton(this.element);
	}
	
	public void hideRemoveButton(){
		this.JSNI_hideRemoveButton(this.element);
	}

	// -------------------- NATIVE METHODS --------------------

	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("save-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCInspectorItem::saveClicked()();
	  	});
	  	element.addEventListener("remove-click", function(e, d, b){
	  		e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCInspectorItem::removeClicked()();
	  	});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
	
	private native void JSNI_enableLibraryMode(Element element) /*-{
		element.enableLibraryMode();
	}-*/;
	
	private native void JSNI_hideSaveButton(Element element) /*-{
		element.hideSaveButton();
	}-*/;
	
	private native void JSNI_hideRemoveButton(Element element) /*-{
		element.hideRemoveButton();
	}-*/;

}
