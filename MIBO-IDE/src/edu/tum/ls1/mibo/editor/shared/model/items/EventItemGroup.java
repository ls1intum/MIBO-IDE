package edu.tum.ls1.mibo.editor.shared.model.items;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.utility.Annotation;

public class EventItemGroup implements IsSerializable {

	// Items
	private List<AbstractEventItem> eventItems;

	// Context information
	private Annotation annotation;

	// Serialization constructor
	public EventItemGroup() {
	}

	public EventItemGroup(String name, String description, String type) {
		this.eventItems = new ArrayList<AbstractEventItem>();
		this.annotation = new Annotation();
		this.annotation.setAnnotation("name", name);
		this.annotation.setAnnotation("description", description);
		this.annotation.setAnnotation("type", type);
	}

	// ---------- GET/SET ----------

	public List<AbstractEventItem> getEventItems() {
		return this.eventItems;
	}

	public void addEventItem(AbstractEventItem eventItem) {
		eventItems.add(eventItem);
	}
	
	public String getAnnotation(String key) {
		return this.annotation.getAnnotation(key);
	}
}
