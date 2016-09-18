package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.List;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCModalityItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;

public class EventItemController implements ItemController, WCEventHandler {

	private WCModalityItem webcomponent;
	private AbstractEventItem eventItem;
	private EditorController editorController;
	private ContainerController containerController;

	public EventItemController(AbstractEventItem eventItem, EditorController editorController,
			ContainerController containerController) {
		this.eventItem = eventItem;
		this.editorController = editorController;
		this.containerController = containerController;
		this.initializeView();
	}

	public void initializeView() {
		this.webcomponent = new WCModalityItem();
		this.webcomponent.initialize(eventItem.getAnnotation("icon"), eventItem.getAnnotation("name"), eventItem.getAnnotation("type"));
		this.webcomponent.addWebComponentEventHandler(this);
	}

	public WCModalityItem getWebcomponent() {
		return this.webcomponent;
	}

	public AbstractEventItem getModel() {
		return this.eventItem;
	}
	
	@Override
	public void check(List<ConsoleItem> validationLog){
		
		// Validate own model
		if (!this.eventItem.isValid()) {
			
			this.webcomponent.changeStateToInvalid();
			
			String output = " The '" + this.eventItem.getAnnotation("name") + "' element contains empty or invalide attributes.";
			ConsoleItem item = new ConsoleItem(ConsoleLogType.ERROR, output);

			validationLog.add(item);
			
		} else {
			this.webcomponent.changeStateToNone();
		}
		
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case WEBCOMPONENT_ITEM_DRAG_STARTED:
			editorController.setDraggedItem(eventItem);
			break;
		case WEBCOMPONENT_ITEM_DRAG_ENDED:
			// When ending the drag process and the dragged item is null it can
			// be assumed that the modality has been moved to another section.
			if (editorController.getDraggedItem() == null) {
				this.containerController.removeItemController(this);
			}
			break;
		case WEBCOMPONENT_ITEM_CLICK_INFO:
			InspectorItemController inspectorItem = new InspectorItemController(this.eventItem, this, this.containerController, editorController.getInspectorController(), editorController);
			editorController.getInspectorController().addWebComponent(inspectorItem.getWebComponent());
			break;
		case WEBCOMPONENT_ITEM_CLICK_DELETE:
			this.containerController.removeItemController(this);
			this.editorController.getInspectorController().flushContent();
			break;
		default:
			Window.alert("Default event of modality controller");
			break;
		}
	}
}
