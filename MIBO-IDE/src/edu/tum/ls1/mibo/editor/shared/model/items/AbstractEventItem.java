package edu.tum.ls1.mibo.editor.shared.model.items;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public abstract class AbstractEventItem extends AbstractMiboItem implements IsSerializable {

	// Target namespace
	protected String target_namespace;

	// Reference to the target namespace
	protected String target_namespace_reference;

	// Supported event types (trigger OR selector OR valueProvider)
	protected List<Event.Type> supportedEvents;

	// Serialization constructor
	public AbstractEventItem() {
		this.supportedEvents = new ArrayList<Event.Type>();
	}

	// ---------- SPECIFIC METHODS ----------

	public void addSupportedEvent(Event.Type event) {
		this.supportedEvents.add(event);
	}

	public Boolean supportsEventType(Event.Type event) {
		if (this.supportedEvents.contains(event)) {
			return true;
		}
		return false;
	}

	public List<Event.Type> getSupportedEvents() {
		return this.supportedEvents;
	}

	public String getTargetNamespace() {
		return this.target_namespace;
	}

	public void setTargetNamespace(String element_target_namespace) {
		this.target_namespace = element_target_namespace;
	}

	public void setTargetNamespaceReference(String reference) {
		this.target_namespace_reference = reference;
	}

	public String getTargetNamespaceReference() {
		return this.target_namespace_reference;
	}
}
