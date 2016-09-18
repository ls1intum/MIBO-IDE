package edu.tum.ls1.mibo.editor.shared.model.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Event implements IsSerializable {

	public enum Type {

		MIBO_TRIGGER, MIBO_VALUEPROVIDER, MIBO_SELECTOR;

		public static Type getEnumForString(String elementName) {
			if (elementName.equals("trigger")) {
				return MIBO_TRIGGER;
			} else if (elementName.equals("valueProvider")) {
				return MIBO_VALUEPROVIDER;
			} else if (elementName.equals("selector")) {
				return MIBO_SELECTOR;
			}
			return null;
		}
	}

	// Event name
	// -> Example: trigger, selector or valueProvider
	private Type type;

	// Namespaces
	// -> Example: Key: wimp – Value: http://www1.in.tum.de/mibo/wimp
	private List<Record> namespaces;

	// Schema locations
	// -> Example: Key: http://www1.in.tum.de/mibo/wimp – Value: WIMP_Scheme.xsd
	private List<Record> locations;

	// References
	// -> Example: Key: voice – Value: switchOn
	private List<Record> references;

	// Serialization constructor
	public Event() {
		this.namespaces = new ArrayList<Record>();
		this.locations = new ArrayList<Record>();
		this.references = new ArrayList<Record>();
	}

	// ---------- GET & SET ----------

	public Type getType() {
		return this.type;
	}

	public void setType(String name) {
		this.type = Type.getEnumForString(name);
	}

	public List<Record> getLocations() {
		return this.locations;
	}

	public void addLocation(Record location) {
		this.locations.add(location);
	}

	public List<Record> getNamespaces() {
		return this.namespaces;
	}

	public void addNamespace(Record namespace) {
		this.namespaces.add(namespace);
	}

	public List<Record> getReferences() {
		return this.references;
	}

	public void addReference(Record reference) {
		this.references.add(reference);
	}
	
	public String getReferenceForNamespace(String namespace){
		for(Record entry : this.namespaces){
			if(entry.getValue().equals(namespace)){
				return entry.getKey();
			}
		}
		return null;
	}

	// ---------- MANAGEMENT ----------

	/**
	 * Get the event's signatures.
	 * 
	 * <p>
	 * A signature of describes a supported modality by the namespace and the
	 * actual name in form of a key / value set.<br>
	 * Example of a single signature:
	 * <ul>
	 * <li>Key: http://www1.in.tum.de/mibo/gestures</li>
	 * <li>Value: pointAtGesture</li>
	 * </ul>
	 * </p>
	 * 
	 * @return A list of signatures for the current event
	 */
	public List<Record> getSignatures() {

		List<Record> signatures = new ArrayList<Record>();

		for (Record namespace : namespaces) {

			String namespace_key = namespace.getKey();
			String namespace_value = namespace.getValue();

			for (Record reference : references) {

				String reference_key = reference.getKey();
				String reference_value = reference.getValue();

				if (namespace_key.equals(reference_key)) {

					signatures.add(new Record(namespace_value, reference_value));

				}
			}
		}
		return signatures;
	}
}
