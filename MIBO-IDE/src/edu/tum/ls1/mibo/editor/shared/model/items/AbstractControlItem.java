package edu.tum.ls1.mibo.editor.shared.model.items;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.Container;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public abstract class AbstractControlItem extends AbstractMiboItem implements Container, IsSerializable {

	// Content
	protected AbstractEventItem eventItem;

	// Serialization constructor
	public AbstractControlItem() {
	}

	// -------------------- OVERRIDE CONTAINER --------------------

	public void addItem(AbstractMiboItem item) {
		if (this.applicable(item)) {
			this.eventItem = (AbstractEventItem) item;
		}
	}

	public void removeItem(AbstractMiboItem item) {
		if (this.eventItem == item) {
			this.eventItem = null;
		}
	}

	public List<AbstractMiboItem> getItems() {

		// Currently, abstract control items only contain one item. Therefore,
		// create a temporary list with only one item
		List<AbstractMiboItem> temp = new ArrayList<AbstractMiboItem>();
		temp.add(this.eventItem);

		// [EXTENSION]
		// In case, control elements will be able to contain several event items
		// that trigger the control item, adjust this list behaviour and the
		// following calls for element "0".

		return temp;
	}

	public Boolean isEmpty() {
		return (this.eventItem != null);
	}

	public abstract Boolean isRequired();

	public abstract Boolean applicable(Object object);

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public boolean matches(Object object) {

		AbstractControlItem control = null;

		// Minimal requirement is to have the same class type
		if (object instanceof AbstractControlItem) {
			control = (AbstractControlItem) object;
		} else {
			return false;
		}

		// Check element name
		if (!this.getRepresentation().equals(control.getRepresentation())) {
			return false;
		}

		// Check if both control items contain an event item
		if (this.getItems().isEmpty() || control.getItems().isEmpty()) {
			return false;
		}

		// Check containing modality
		if (!this.getItems().get(0).matches(control.getItems().get(0))) {
			return false;
		}

		// Check size of attributes
		if (this.getAttributes().size() != control.getAttributes().size()) {
			return false;
		}

		// Check attributes
		for (int i = 0; i < this.getAttributes().size(); i++) {
			if (!this.getAttributes().get(i).equals(control.getAttributes().get(i))) {
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

		// Needs to contain a modality
		return (eventItem != null);
	}

	// -------------------- OVERRIDE OBJECT --------------------

	@Override
	public boolean equals(Object object) {

		// Cast to current object type
		AbstractControlItem item = (AbstractControlItem) object;

		// Retrieve requirements for equal objects
		boolean equalName = this.getAnnotation("name").equals(item.getAnnotation("name"));
		boolean equalType = this.getAnnotation("type").equals(item.getAnnotation("type"));

		return (equalName && equalType);
	}

}
