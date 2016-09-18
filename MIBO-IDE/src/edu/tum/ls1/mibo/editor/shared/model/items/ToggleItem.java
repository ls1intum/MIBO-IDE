package edu.tum.ls1.mibo.editor.shared.model.items;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class ToggleItem extends AbstractControlItem implements IsSerializable {

	// Serialization constructor
	public ToggleItem() {
	}

	public ToggleItem(String element_name) {

		this.setRepresentation(element_name);

		Attribute attribute_on = new Attribute();
		attribute_on.setName("on");
		attribute_on.addConstrain(new Record("default", "1.0"));
		attribute_on.addConstrain(new Record("use", "optional"));
		attribute_on.addConstrain(new Record("type", "valueType"));
		attribute_on.setAnnotation("name", "Value A");
		attribute_on.setAnnotation("description", "The value which will be set as an 'on' value.");
		attribute_on.setAnnotation("measurement", "e.g. 1.0");
		this.attributes.add(attribute_on);

		Attribute attribute_off = new Attribute();
		attribute_off.setName("off");
		attribute_off.addConstrain(new Record("default", "0.0"));
		attribute_off.addConstrain(new Record("use", "optional"));
		attribute_off.addConstrain(new Record("type", "valueType"));
		attribute_off.setAnnotation("name", "Value B");
		attribute_off.setAnnotation("description", "The value which will be set as an 'off' value.");
		attribute_off.setAnnotation("measurement", "e.g. 0.0");
		this.attributes.add(attribute_off);

		this.annotation.setAnnotation("name", "Toggle");
		this.annotation.setAnnotation("description",
				"The \"Toggle\" control can be used to switch between two values.");
		this.annotation.setAnnotation("type", "Control");
		this.annotation.setAnnotation("icon", "control-toggle");

	}

	// -------------------- OVERRIDE CONTAINER --------------------

	@Override
	public Boolean isRequired() {
		return false;
	}

	@Override
	public Boolean applicable(Object object) {
		if (object instanceof AbstractEventItem) {
			AbstractEventItem item = (AbstractEventItem) object;
			if (item.supportsEventType(Event.Type.MIBO_TRIGGER)) {
				return true;
			}
		}
		return false;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		ToggleItem control = new ToggleItem(this.getRepresentation());

		// Copy attributes
		for (Attribute attribute : this.attributes) {

			String name = attribute.getName();
			String value = attribute.getValue();

			if (control.getAttribute(attribute.getName()) != null) {
				// Attribute exists and can be filled with value
				control.updateAttribute(name, value);
			} else {
				
				// [EXTENSION]
				// Add possiblity to add attributes if they do not exist.
				
			}
		}

		if (this.eventItem != null) {
			AbstractEventItem temp = (AbstractEventItem) this.eventItem.getCopy();
			control.addItem(temp);
		}

		return control;
	}

}
