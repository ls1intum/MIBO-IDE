package edu.tum.ls1.mibo.editor.shared.model.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Attribute implements IsSerializable {

	// Core content
	private String value;
	private String name;

	// Constrains
	private List<Record> constrains;

	// Annotation
	private Annotation annotation;

	// Serialization constructor
	public Attribute() {
		this.constrains = new ArrayList<Record>();
		this.annotation = new Annotation();
	}

	// ---------- GET/SET ----------

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {

		// Set up helper boolean values
		boolean hasDefault = (this.getConstrainValue("default") != null);
		boolean emptyInput = (value == null) || value.isEmpty();
		boolean isOptional = (this.getConstrainValue("use") != null
				&& this.getConstrainValue("use").equals("optional"));

		// A — There is a default value AND no input is given
		boolean scenarioA = hasDefault && emptyInput;
		// B – The attribute is optional AND no input is given
		boolean scenarioB = isOptional && emptyInput;
		// C - The input value equals the default value
		boolean scenarioC = (hasDefault && this.getConstrainValue("default").equals(value));

		// Update value in accordance to the input value
		if (scenarioA || scenarioB || scenarioC) {
			this.value = null;
		} else {
			this.value = value;
		}

	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Record getRepresentation() {
		return new Record(getName(), getValue());
	}

	public void addConstrain(Record constrain) {
		this.constrains.add(constrain);
	}

	public String getConstrainValue(String key) {
		for (Record keyValue : this.constrains) {
			if (keyValue.getKey().equals(key)) {
				return keyValue.getValue();
			}
		}
		return null;
	}

	public List<Record> getConstrains() {
		return this.constrains;
	}

	public String getAnnotation(String key) {
		return this.annotation.getAnnotation(key);
	}

	public void setAnnotation(String name, String content) {
		this.annotation.setAnnotation(name, content);
	}

	// ---------- COPY ----------

	public Attribute getCopy() {

		Attribute temp = new Attribute();

		List<Record> items = new ArrayList<Record>();
		for (Record item : this.constrains) {
			items.add(item.getCopy());
		}
		temp.constrains = items;
		temp.name = this.name;
		temp.value = this.value;
		temp.annotation = this.annotation.getCopy();

		return temp;
	}

	// ---------- VALIDATION ----------

	public Boolean isValide() {

		boolean isRequired = (this.getConstrainValue("use") != null
				&& this.getConstrainValue("use").equals("required"));
		boolean isEmpty = (this.getValue() == null || this.getValue().isEmpty());

		// Required attributes need to be initalized and have a value ...
		if (isRequired && isEmpty) {
			return false;
		}
		// ... optional attributes with no value are always valide
		else if (!isRequired && isEmpty) {
			return true;
		}

		String type = this.getConstrainValue("type");

		// Validate STRING value
		if (type != null && type.equals("xs:string")) {
			// Every string value is considered to be valide
		}
		// Validate BOOLEAN value
		else if (type != null && type.equals("xs:boolean")) {

			boolean containsTrue = this.getValue().equals("true");
			boolean containsFalse = this.getValue().equals("false");

			// Attributes needs to contain either a "true" or "false" value
			if (!(containsTrue || containsFalse)) {
				return false;
			}

		}
		// Validate INTEGER value
		else if (type != null && type.equals("xs:integer")) {

			// Attribute needs to be an integer value
			try {
				Integer.parseInt(this.getValue());
			} catch (Exception e) {
				return false;
			}

		}
		// Validate custom VALUETYPE value
		else if (type != null && type.equals("valueType")) {

			double valueType;
			
			// Attribute needs to be a double value
			try {
				valueType = Double.parseDouble(this.getValue());
			} catch (Exception e) {
				return false;
			}
			
			// Attribute needs to be between 0 and 1
			if(valueType < 0 || valueType > 1){
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equals(Object object) {

		Attribute attribute = null;

		// Minimal requirement is to have the same class type
		if (object instanceof Attribute) {
			attribute = (Attribute) object;
		} else {
			return false;
		}

		// Check name
		if (!this.getName().equals(attribute.getName())) {
			return false;
		}

		// Check value
		if (!this.getValue().equals(attribute.getValue())) {
			return false;
		}

		// Check size of constrains
		if(this.getConstrains().size() != attribute.getConstrains().size()){
			return false;
		}

		// Check attributes
		for (int i = 0; i < this.getConstrains().size(); i++) {
			if (!this.getConstrains().get(i).equals(attribute.getConstrains().get(i))) {
				return false;
			}
		}

		return true;
	}

}
