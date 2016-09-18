package edu.tum.ls1.mibo.editor.client.controller.editor;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCListItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class ListItemController implements WCEventHandler {

	private WCListItem webcomponent;
	private AbstractMiboItem item;
	private EditorController editorController;

	public ListItemController(AbstractMiboItem item, EditorController editorController) {
		this.item = item;
		this.editorController = editorController;
		this.initializeView();
	}

	public void initializeView() {
		
		this.webcomponent = new WCListItem();
		this.webcomponent.initialize(item.getAnnotation("name"), item.getAnnotation("type"), item.getAnnotation("icon"));
		this.webcomponent.addWebComponentEventHandler(this);
		
		// For event items only, add their types in form of flags, 
		if (item instanceof AbstractEventItem) {

			AbstractEventItem eventItem = (AbstractEventItem) this.item;

			if (eventItem.getSupportedEvents().contains(Event.Type.MIBO_TRIGGER)) {
				this.webcomponent.getElement().setAttribute("trigger", "true");
			}
			if (eventItem.getSupportedEvents().contains(Event.Type.MIBO_SELECTOR)) {
				this.webcomponent.getElement().setAttribute("selector", "true");
			}
			if (eventItem.getSupportedEvents().contains(Event.Type.MIBO_VALUEPROVIDER)) {
				this.webcomponent.getElement().setAttribute("valueprovider", "true");
			}
		}
		
	}

	public WCListItem getWebComponent() {
		return this.webcomponent;
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case WEBCOMPONENT_ITEM_DRAG_STARTED:
			editorController.setDraggedItem(item);
			break;
		case WEBCOMPONENT_ITEM_DRAG_ENDED:
			break;
		case WEBCOMPONENT_ITEM_CLICK_INFO:
			InspectorItemController inspectorItem = new InspectorItemController(this.item, null, null, editorController.getInspectorController(), editorController);
			inspectorItem.enableLibraryMode();
			editorController.getInspectorController().addWebComponent(inspectorItem.getWebComponent());
			break;
		default:
			Window.alert("Default event of modality controller");
			break;
		}
	}
}
