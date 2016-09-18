package edu.tum.ls1.mibo.editor.shared.model.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Annotation implements IsSerializable {

	private List<Record> annotations;

	// Serializable constructor
	public Annotation() {
		this.annotations = new ArrayList<Record>();
	}

	// Recommendable constructor
	public Annotation(String name, String content) {
		this.setAnnotation(name, content);
	}

	// ---------- GET & SET ----------

	/**
	 * For a given key, return an annotation text.
	 * 
	 * @param key
	 *            Name of the requested annotation
	 * @return An annotation text. Will be <code>NULL</code> if no annotation of
	 *         the input key could be found.
	 */
	public String getAnnotation(String key) {

		String content = null;

		for (Record annotation : this.annotations) {
			if (annotation.getKey().equals(key)) {
				return annotation.getValue();
			}
		}

		return content;
	}

	/**
	 * For a given key and value, set an annotation. If an annotation entry for
	 * the input key already exists, its related value will be overwritten.
	 * 
	 * @param key
	 *            Name of the annotation which should be set.
	 * @param value
	 *            An annotation text which should be set.
	 */
	public void setAnnotation(String key, String value) {

		// Check if an entry already exists ...
		for (Record annotation : this.annotations) {
			if (annotation.getKey().equals(key)) {
				annotation.setKey(value);
				return;
			}
		}

		// ... if not, add it to the list
		this.annotations.add(new Record(key, value));
	}

	/**
	 * Create a copy of the current object.
	 * 
	 * @return A copy with the same content.
	 */
	public Annotation getCopy() {

		Annotation copy = new Annotation();

		for (Record record : this.annotations) {
			copy.addRecord(record.getCopy());
		}

		return copy;

	}

	// ---------- HELPER ----------

	private void addRecord(Record r) {
		this.annotations.add(r);
	}

}
