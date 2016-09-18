package edu.tum.ls1.mibo.editor.shared.model.sections;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class ExitSection extends AbstractSection implements IsSerializable {

	public ExitSection() {
		
		this.setRepresentation("exit");

		this.annotation.setAnnotation("name", "Exit");
		this.annotation.setAnnotation("description", "This is the exit section");
		this.annotation.setAnnotation("type", "Section");
	}

	// ---------- CONTAINER METHODS ----------

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

	@Override
	public Boolean isRequired() {
		return false;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {

		// Create new instance of own model
		AbstractSection section = new ExitSection();

		// Copy containing items
		for (AbstractMiboItem item : this.items) {
			section.addItem(item.getCopy());
		}

		return section;
	}

}
