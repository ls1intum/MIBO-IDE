package edu.tum.ls1.mibo.editor.shared.model.items;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;

public class FixtureItem extends AbstractScopeItem implements IsSerializable {

	// Serialization constructor
	public FixtureItem() {
	}

	public FixtureItem(String name) {

		this.setRepresentation("item");
		this.initializeAnnotations(name);

		Attribute attribute_group = new Attribute();
		attribute_group.setName("group");
		attribute_group.setValue(name);
		attribute_group.addConstrain(new Record("use", "required"));
		attribute_group.addConstrain(new Record("type", "xs:string"));
		this.attributes.add(attribute_group);

	}

	// ---------- SPECIFIC ----------

	private void initializeAnnotations(String name) {

		this.annotation.setAnnotation("name", name);
		this.annotation.setAnnotation("description", "A fixture can be controlled by a combination of a modality and a control element.");
		this.annotation.setAnnotation("type", "Fixture");

		if (name.equals("Lights")) {
			this.annotation.setAnnotation("icon", "fixture-lights");
		} else if (name.equals("Overhead Lights")) {
			this.annotation.setAnnotation("icon", "fixture-overhead-lights");
		} else if (name.equals("Desk Lights")) {
			this.annotation.setAnnotation("icon", "fixture-desk-lights");
		} else if (name.equals("Windows")) {
			this.annotation.setAnnotation("icon", "fixture-windows");
		} else if (name.equals("Plugs")) {
			this.annotation.setAnnotation("icon", "fixture-plugs");
		} else if (name.equals("Louvers")) {
			this.annotation.setAnnotation("icon", "fixture-louvers");
		} else if (name.equals("Blinds")) {
			this.annotation.setAnnotation("icon", "fixture-blinds");
		} else if (name.equals("Computers")) {
			this.annotation.setAnnotation("icon", "fixture-computers");
		} else {
			this.annotation.setAnnotation("icon", "fixture-unknown");
		}

	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public AbstractMiboItem getCopy() {
		FixtureItem fixture = new FixtureItem(this.annotation.getAnnotation("name"));
		return fixture;
	}

	@Override
	public boolean matches(Object object) {

		FixtureItem fixture = null;

		// Minimal requirement is to have the same class type
		if (object instanceof FixtureItem) {
			fixture = (FixtureItem) object;
		} else {
			return false;
		}

		// Check element name
		if (!this.getRepresentation().equals(fixture.getRepresentation())) {
			return false;
		}

		// Check size of attributes
		if (this.getAttributes().size() != fixture.getAttributes().size()) {
			return false;
		}

		// Check attributes
		for (int i = 0; i < this.getAttributes().size(); i++) {
			if (!this.attributes.get(i).equals(fixture.getAttributes().get(i))) {
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
		FixtureItem item = (FixtureItem) object;

		// Retrieve requirements for equal objects
		boolean equalName = this.getAnnotation("name").equals(item.getAnnotation("name"));
		boolean equalType = this.getAnnotation("type").equals(item.getAnnotation("type"));

		return (equalName && equalType);
	}

}
