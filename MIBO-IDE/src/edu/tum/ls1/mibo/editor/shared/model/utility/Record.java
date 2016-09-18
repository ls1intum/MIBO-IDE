package edu.tum.ls1.mibo.editor.shared.model.utility;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Record implements IsSerializable {

	private String key;
	private String value;

	// Serializable constructor
	public Record() {
	}

	// Recommendable constructor
	public Record(String key, String value) {
		this.key = key;
		this.value = value;
	}

	// ---------- GET & SET ----------

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	// ---------- COPY ----------

	public Record getCopy() {
		return new Record(this.key, this.value);
	}

	// ---------- COMPARE ----------

	@Override
	public boolean equals(Object object) {

		Record entry = null;

		// Minimal requirement is to have the same class type
		if (object instanceof Record) {
			entry = (Record) object;
		} else {
			return false;
		}

		// Check name
		if (!this.getKey().equals(entry.getKey())) {
			return false;
		}

		// Check value
		if (!this.getValue().equals(entry.getValue())) {
			return false;
		}

		return true;
	}
	
	@Override
	public String toString(){
		return "[MIBOEntry.toString()] Key: " + this.key + " – Value: " + this.value;
	}

}
