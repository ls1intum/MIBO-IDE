package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCLibraryArea;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCLibraryArea.LibraryPage;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCListItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;

public class LibraryController implements WCEventHandler {

	private WCLibraryArea webcomponent;
	private List<AbstractScopeItem> scopeItems;
	private List<EventItemGroup> eventItemGroups;
	private List<AbstractControlItem> controls;
	private EditorController editorController;

	public LibraryController(List<EventItemGroup> eventItemGroups, List<AbstractScopeItem> scopeItems,
			List<AbstractControlItem> controls, EditorController editorController) {
		this.scopeItems = scopeItems;
		this.eventItemGroups = eventItemGroups;
		this.controls = controls;
		this.editorController = editorController;
		this.initializeView();
	}

	/**
	 * Setup the controller's view<br>
	 * 1. Initialize WebComponent<br>
	 * 2. Setup the WebComponent (Register EventHandler, ...)<br>
	 * 3. Attach to the WebComponent to parent element<br>
	 * 4. Add fixtures, modalities and controls
	 */
	public void initializeView() {

		this.webcomponent = new WCLibraryArea("MIBO Toolbox");
		this.webcomponent.addWebComponentEventHandler(this);
		DOM.getElementById("actions-container").appendChild(this.webcomponent.getElement());

		// Step 1 – Add fixtures
		for (AbstractScopeItem scopeItem : scopeItems) {
			ListItemController temp = new ListItemController(scopeItem, editorController);
			this.webcomponent.addItem(temp.getWebComponent(), LibraryPage.FIXTURE);
		}

		// Step 2 - Add modalities (Assuming that  all available event
		// items in the event item groups are modality items)
		for (EventItemGroup group : eventItemGroups) {

			String unit = " element";
			
			if(group.getEventItems().size()>1){
				unit += "s";
			}
			
			this.webcomponent.addHeading(group.getAnnotation("name"), group.getEventItems().size() + unit, group.getAnnotation("description"), LibraryPage.MODALITY);

			for (AbstractEventItem eventItem : group.getEventItems()) {
				ListItemController temp = new ListItemController(eventItem, editorController);
				this.webcomponent.addItem(temp.getWebComponent(), LibraryPage.MODALITY);
			}
		}
		
		// Step 3 - Add placeholders for Context
		
		// Geo fencing
		
		this.webcomponent.addHeading("Geo-fence", "2 elements", "Description", LibraryPage.CONTEXT);

		WCListItem arrive = new WCListItem();
		arrive.initialize("Arrive At", "Geo-fence Context", "context-arrive");
		this.webcomponent.addItem(arrive, LibraryPage.CONTEXT);
		
		WCListItem leave = new WCListItem();
		leave.initialize("Depart From", "Geo-fence Context", "context-leave");
		this.webcomponent.addItem(leave, LibraryPage.CONTEXT);
		
		this.webcomponent.addHeading("Weather", "2 elements", "Description", LibraryPage.CONTEXT);

		WCListItem sun = new WCListItem();
		sun.initialize("Sunshine", "Weather Context", "context-sun");
		this.webcomponent.addItem(sun, LibraryPage.CONTEXT);
		
		WCListItem rain = new WCListItem();
		rain.initialize("Rain", "Weather Context", "context-rain");
		this.webcomponent.addItem(rain, LibraryPage.CONTEXT);
		
		WCListItem snow = new WCListItem();
		snow.initialize("Snow", "Weather Context", "context-snow");
		this.webcomponent.addItem(snow, LibraryPage.CONTEXT);

		// Step 4 - Add controls
		for (AbstractControlItem control : controls) {
			ListItemController temp = new ListItemController(control, editorController);
			this.webcomponent.addItem(temp.getWebComponent(), LibraryPage.CONTROL);
		}
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {
		switch (event.getEventType()) {
		default:
			Window.alert("Default event");
			break;
		}
	}
}
