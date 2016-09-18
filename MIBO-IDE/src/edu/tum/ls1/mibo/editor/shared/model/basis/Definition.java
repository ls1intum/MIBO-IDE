package edu.tum.ls1.mibo.editor.shared.model.basis;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.domain.Domain;
import edu.tum.ls1.mibo.editor.shared.model.Container;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;

public class Definition extends AbstractMiboItem implements Container, Domain, IsSerializable {

	// Content
	private List<AbstractMiboItem> sections;

	public Definition() {

		this.setRepresentation("definition");
		this.sections = new ArrayList<AbstractMiboItem>();

		Attribute attribute_id = new Attribute();
		attribute_id.setName("id");
		attribute_id.addConstrain(new Record("use", "required"));
		attribute_id.addConstrain(new Record("type", "xs:ID"));
		attribute_id.setAnnotation("name", "Identifier");
		attribute_id.setAnnotation("description", "An interaction definition is identified by a unique ID.");
		attribute_id.setAnnotation("type", "Attribute");
		attribute_id.setAnnotation("measurement", "ID");
		this.attributes.add(attribute_id);

		Attribute attribute_name = new Attribute();
		attribute_name.setName("name");
		attribute_name.addConstrain(new Record("use", "optional"));
		attribute_name.addConstrain(new Record("type", "xs:string"));
		attribute_name.setAnnotation("name", "Name");
		attribute_name.setAnnotation("description", "A human-readable name for an interaction definition.");
		attribute_name.setAnnotation("type", "Attribute");
		attribute_name.setAnnotation("measurement", "Text");
		this.attributes.add(attribute_name);

		Attribute attribute_activated = new Attribute();
		attribute_activated.setName("activated");
		attribute_activated.addConstrain(new Record("use", "optional"));
		attribute_activated.addConstrain(new Record("default", "true"));
		attribute_activated.addConstrain(new Record("type", "xs:boolean"));
		attribute_activated.setAnnotation("name", "Activated");
		attribute_activated.setAnnotation("description", "Determines if the interaction definition will be activated or not.");
		attribute_activated.setAnnotation("type", "Attribute");
		attribute_activated.setAnnotation("measurement", "Boolean");
		this.attributes.add(attribute_activated);

		this.annotation.setAnnotation("name", "Interaction Definition");
		this.annotation.setAnnotation("description", "An interaction definition is a container for any kind of elements.");
		this.annotation.setAnnotation("type", "Container");

	}

	// -------------------- OVERRIDE CONTAINER --------------------

	@Override
	public void addItem(AbstractMiboItem item) {
		if(this.applicable(item)){
			this.sections.add(item);
		}
	}

	@Override
	public void removeItem(AbstractMiboItem item) {
		if(this.sections.contains(item)){
			this.sections.remove(item);
		}
	}

	@Override
	public List<AbstractMiboItem> getItems() {
		return this.sections;
	}

	@Override
	public Boolean isEmpty() {
		return this.sections.isEmpty();
	}

	@Override
	public Boolean isRequired() {
		return false;
	}

	@Override
	public Boolean applicable(Object object) {
		
		if(object instanceof AbstractSection){
			return true;
		}
		
		return false;
	}
	
	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		Definition definition = new Definition();

		// Copy attributes
		for (Attribute attribute : this.attributes) {

			String name = attribute.getName();
			String value = attribute.getValue();

			if (definition.getAttribute(attribute.getName()) != null) {
				// Attribute exists and can be filled with value
				definition.updateAttribute(name, value);
			} else {
				
				// [EXTENSION]
				// Add possiblity to add attributes if they do not exist.
				
			}
		}

		// Copy sections
		for (AbstractMiboItem section : this.sections) {
			definition.addItem(section.getCopy());
		}

		return definition;
	}

	@Override
	public boolean matches(Object object) {

		Definition definition = null;

		// Minimal requirement is to have the same class type
		if (object instanceof Definition) {
			definition = (Definition) object;
		} else {
			return false;
		}

		// Check element name
		if (!this.getRepresentation().equals(definition.getRepresentation())) {
			return false;
		}

		// Number of containing sections needs to be equal
		if (this.getItems().size() != definition.getItems().size()) {
			return false;
		}

		// Check size of attributes
		if (this.getAttributes().size() != definition.getAttributes().size()) {
			return false;
		}

		// Check attributes
		for (int i = 0; i < this.getAttributes().size(); i++) {
			if (!this.attributes.get(i).equals(definition.getAttributes().get(i))) {
				return false;
			}
		}

		// Forward isEqual() call
		for (int i = 0; i < this.sections.size(); i++) {
			if (!this.sections.get(i).matches(definition.getItems().get(i))) {
				return false;
			}
		}

		return true;
	}

	public Boolean isValid() {

		Boolean isValid = true;

		for (AbstractMiboItem section : sections) {
			if (((Container)section).isRequired()) {
				if (!section.isValid()) {
					isValid = false;
				}
			}
		}

		for (Attribute attribute : attributes) {
			if (attribute.getName().equals("id") && attribute.getValue().equals("")) {
				isValid = false;
			}
		}

		return isValid;
	}

	// -------------------- OVERRIDE DOMAIN --------------------

	@Override
	public String getDisplayName() {

		if (this.getAttribute("name").getValue() != null) {
			return this.getAttribute("name").getValue();
		}
		
		return null;
	}

	@Override
	public String getDisplaySubName() {
		return this.getAttribute("id").getValue();
	}

}
