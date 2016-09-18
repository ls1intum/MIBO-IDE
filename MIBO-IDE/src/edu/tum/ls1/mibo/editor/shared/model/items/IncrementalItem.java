package edu.tum.ls1.mibo.editor.shared.model.items;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class IncrementalItem extends AbstractControlItem implements IsSerializable {

	// Serialization constructor
	public IncrementalItem() {
	}

	public IncrementalItem(String element_name) {

		this.setRepresentation(element_name);

		Attribute attribute_by = new Attribute();
		attribute_by.setName("by");
		attribute_by.addConstrain(new Record("use", "optional"));
		attribute_by.addConstrain(new Record("type", "valueType"));
		attribute_by.setAnnotation("name", "By value");
		attribute_by.setAnnotation("description", "The value by which the incremental change will be applied.");
		attribute_by.setAnnotation("measurement", "e.g. 0.2");
		this.attributes.add(attribute_by);

		if (this.getRepresentation().equals("increase")) {
			this.annotation.setAnnotation("name", "Increase");
			this.annotation.setAnnotation("description",
					"The \"Increase\" control can be used to incrementally increase a value.");
			this.annotation.setAnnotation("type", "Control");
			this.annotation.setAnnotation("icon", "control-increase");
		} else if (this.getRepresentation().equals("decrease")) {
			this.annotation.setAnnotation("name", "Decrease");
			this.annotation.setAnnotation("description",
					"The \"Decrease\" control can be used to incrementally decrease a value.");
			this.annotation.setAnnotation("type", "Control");
			this.annotation.setAnnotation("icon", "control-decrease");
		}

	}

	// -------------------- OVERRIDE CONTAINER --------------------

	@Override
	public Boolean isRequired(){
		return false;
	}
	
	@Override
	public Boolean applicable(Object object) {
		if (object instanceof AbstractEventItem) {
			AbstractEventItem item = (AbstractEventItem) object;
			if (item.supportsEventType(Event.Type.MIBO_VALUEPROVIDER) || item.supportsEventType(Event.Type.MIBO_TRIGGER)) {
				return true;
			}
		}
		return false;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		IncrementalItem control = new IncrementalItem(this.getRepresentation());

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
