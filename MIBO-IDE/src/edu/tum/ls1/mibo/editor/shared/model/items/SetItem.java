package edu.tum.ls1.mibo.editor.shared.model.items;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class SetItem extends AbstractControlItem implements IsSerializable {

	// Serialization constructor
	public SetItem() {
	}

	public SetItem(String element_name) {

		this.setRepresentation(element_name);

		Attribute attribute_to = new Attribute();
		attribute_to.setName("to");
		attribute_to.addConstrain(new Record("use", "optional"));
		attribute_to.addConstrain(new Record("type", "valueType"));
		attribute_to.setAnnotation("name", "To value");
		attribute_to.setAnnotation("description", "The value which will be set.");
		attribute_to.setAnnotation("measurement", "e.g. 0.5");
		attributes.add(attribute_to);

		this.annotation.setAnnotation("name", "Set");
		this.annotation.setAnnotation("description", "The \"Set\" control can be used to set up a specific value.");
		this.annotation.setAnnotation("type", "Control");
		this.annotation.setAnnotation("icon", "control-set");

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
			if (item.supportsEventType(Event.Type.MIBO_VALUEPROVIDER)
					|| item.supportsEventType(Event.Type.MIBO_TRIGGER)) {
				return true;
			}
		}
		return false;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		SetItem control = new SetItem(this.getRepresentation());

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
