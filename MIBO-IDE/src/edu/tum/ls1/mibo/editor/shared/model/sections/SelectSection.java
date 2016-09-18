package edu.tum.ls1.mibo.editor.shared.model.sections;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class SelectSection extends AbstractSection implements IsSerializable {

	public SelectSection() {
		
		this.setRepresentation("select");

		this.annotation.setAnnotation("name", "Select");
		this.annotation.setAnnotation("description", "This is the select section");
		this.annotation.setAnnotation("type", "Section");
	}

	// ---------- CONTAINER METHODS ----------

	@Override
	public Boolean applicable(Object object) {
		if (object instanceof AbstractEventItem) {
			AbstractEventItem item = (AbstractEventItem) object;
			if (item.supportsEventType(Event.Type.MIBO_SELECTOR)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Boolean isRequired() {
		return true;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		// Create new instance of own model
		AbstractSection section = new SelectSection();

		// Copy containing items
		for (AbstractMiboItem item : this.items) {
			section.addItem(item.getCopy());
		}

		return section;
	}

}
