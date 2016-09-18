package edu.tum.ls1.mibo.editor.shared.model.basis;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.Item;
import edu.tum.ls1.mibo.editor.shared.model.utility.Annotation;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public abstract class AbstractMiboItem implements Item, IsSerializable {

	// Item's tag name
	protected String element_name;

	// Item's tag attributes
	protected List<Attribute> attributes;

	// Item's context information
	protected Annotation annotation;

	// Serialization constructor
	public AbstractMiboItem() {
		this.attributes = new ArrayList<Attribute>();
		this.annotation = new Annotation();
	}

	@Override
	public String getAnnotation(String key) {
		return this.annotation.getAnnotation(key);
	}

	@Override
	public void setAnnotation(String name, String content) {
		this.annotation.setAnnotation(name, content);
	}

	@Override
	public List<Attribute> getAttributes() {
		return this.attributes;
	}

	@Override
	public Attribute getAttribute(String name) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(name)) {
				return attribute;
			}
		}
		return null;
	}

	@Override
	public void addAttribute(Attribute attribute) {
		this.attributes.add(attribute);
	}

	@Override
	public void updateAttribute(String name, String value) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(name)) {
				attribute.setValue(value);
				break;
			}
		}
	}

	@Override
	public void removeAttribute(String name) {
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(name)) {
				this.attributes.remove(attribute);
				break;
			}
		}
	}

	@Override
	public String getRepresentation() {
		return this.element_name;
	}

	@Override
	public void setRepresentation(String element_name) {
		this.element_name = element_name;
	}

}
