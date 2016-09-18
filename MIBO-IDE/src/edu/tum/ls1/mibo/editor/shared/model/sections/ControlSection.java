package edu.tum.ls1.mibo.editor.shared.model.sections;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;

public class ControlSection extends AbstractSection implements IsSerializable {

	public ControlSection() {
		
		this.setRepresentation("control");
		
		this.annotation.setAnnotation("name", "Control");
		this.annotation.setAnnotation("description", "This is the control section");
		this.annotation.setAnnotation("type", "Section");
	}

	// ---------- CONTAINER METHODS ----------

	@Override
	public Boolean applicable(Object object) {
		if (object instanceof AbstractControlItem) {
			return true;
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
		AbstractSection section = new ControlSection();

		// Copy containing items
		for (AbstractMiboItem item : this.items) {
			section.addItem(item.getCopy());
		}

		return section;
	}

}
