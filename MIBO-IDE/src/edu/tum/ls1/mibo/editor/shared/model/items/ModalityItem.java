package edu.tum.ls1.mibo.editor.shared.model.items;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class ModalityItem extends AbstractEventItem implements IsSerializable {

	// Serialization constructor
	public ModalityItem() {
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		ModalityItem modality = new ModalityItem();

		modality.element_name = this.element_name;
		modality.target_namespace = this.target_namespace;
		modality.target_namespace_reference = this.target_namespace_reference;

		modality.annotation = this.annotation.getCopy();

		List<Event.Type> supportedEvents = new ArrayList<Event.Type>();
		for (Event.Type event : this.supportedEvents) {
			supportedEvents.add(event);
		}
		modality.supportedEvents = supportedEvents;

		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute attribute : this.attributes) {
			attributes.add((Attribute) attribute.getCopy());
		}
		modality.attributes = attributes;

		return modality;
	}

	@Override
	public boolean matches(Object object) {

		ModalityItem modality = null;

		// Minimal requirement is to have the same class type
		if (object instanceof ModalityItem) {
			modality = (ModalityItem) object;
		} else {
			return false;
		}

		// Check element name
		if (!this.getRepresentation().equals(modality.getRepresentation())) {
			return false;
		}

		// Check element target namespace name
		if (!this.getTargetNamespace().equals(modality.getTargetNamespace())) {
			return false;
		}

		// Check size of attributes
		if (this.getAttributes().size() != modality.getAttributes().size()) {
			return false;
		}

		// Check attributes
		for (int i = 0; i < this.getAttributes().size(); i++) {
			if (!this.attributes.get(i).equals(modality.getAttributes().get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Boolean isValid() {

		// Check if attributes are valid
		for (Attribute attribute : attributes) {
			if (!attribute.isValide()) {
				return false;
			}
		}

		return true;
	}

	// -------------------- OVERRIDE OBJECT --------------------

	@Override
	public boolean equals(Object object) {

		// Cast to current object type
		ModalityItem item = (ModalityItem) object;

		// Retrieve requirements for equal objects
		boolean equalName = this.getAnnotation("name").equals(item.getAnnotation("name"));
		boolean equalType = this.getAnnotation("type").equals(item.getAnnotation("type"));

		return (equalName && equalType);
	}

}
