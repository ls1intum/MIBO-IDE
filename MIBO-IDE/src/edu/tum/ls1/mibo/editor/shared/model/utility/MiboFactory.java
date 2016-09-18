package edu.tum.ls1.mibo.editor.shared.model.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;
import edu.tum.ls1.mibo.editor.shared.model.items.FixtureItem;
import edu.tum.ls1.mibo.editor.shared.model.items.ModalityItem;

public class MiboFactory implements IsSerializable {

	// Prototypes of items
	private Definition definition;
	private List<EventItemGroup> eventItemGroups;
	private List<AbstractScopeItem> scopeItems;
	private List<AbstractControlItem> controlItems;

	public MiboFactory() {
		definition = null;
		eventItemGroups = new ArrayList<EventItemGroup>();
		scopeItems = new ArrayList<AbstractScopeItem>();
		controlItems = null;
	}

	// ---------- AVAILABLE LIBRARY ITEMS ----------

	public List<AbstractScopeItem> getAvailableScopeItems() {
		return this.scopeItems;
	}

	public List<EventItemGroup> getAvailableEventItemGroups() {
		return this.eventItemGroups;
	}

	public List<AbstractControlItem> getAvailableControlItems() {
		return this.controlItems;
	}

	// ---------- SET ITEM PROTOTYPES ----------

	public void setDefinition(Definition definition) {
		this.definition = definition;
	}

	public void addScopeItems(List<AbstractScopeItem> list) {
		this.scopeItems.addAll(list);
	}

	public void addEventItemGroups(List<EventItemGroup> list) {
		this.eventItemGroups.addAll(list);
	}

	public void setControls(List<AbstractControlItem> list) {
		this.controlItems = list;
	}

	// ---------- CREATION ----------

	public Definition createDefinition() {
		Definition definition = (Definition) this.definition.getCopy();
		return definition;
	}

	public FixtureItem createFixture(String name) {

		for (AbstractScopeItem scopeItem : this.scopeItems) {
			
			// Only pick FixtureItem instances
			if(!(scopeItem instanceof FixtureItem)){
				continue;
			}
			
			// Actually create the FixtureItem
			if (scopeItem.getAttribute("group").getValue().equals(name)) {
				return (FixtureItem) scopeItem.getCopy();
			}
		}

		return null;
	}

	public ModalityItem createModality(String namespace, String name) {

		for (EventItemGroup group : eventItemGroups) {
			for (AbstractEventItem eventItem : group.getEventItems()) {

				// Only pick ModalityItem instances
				if(!(eventItem instanceof ModalityItem)){
					continue;
				}
				
				// Actually create the FixtureItem
				if (namespace.equals(eventItem.getTargetNamespace()) && name.equals(eventItem.getRepresentation())) {
					return (ModalityItem) eventItem.getCopy();
				}
			}
		}

		return null;
	}

	public AbstractControlItem createControl(String name) {

		for (AbstractControlItem control : this.controlItems) {
			if (control.getRepresentation().equals(name)) {
				return (AbstractControlItem) control.getCopy();
			}
		}

		return null;
	}

}
