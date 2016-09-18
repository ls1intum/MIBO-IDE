package edu.tum.ls1.mibo.editor.client.controller.editor;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCInspectorItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;

public class InspectorItemController implements WCEventHandler {

	private WCInspectorItem webcomponent;
	private AbstractMiboItem item;
	private ItemController itemController;
	private ContainerController containerController;
	private InspectorController inspectorController;
	private EditorController editorController;

	public InspectorItemController(AbstractMiboItem item, ItemController itemController,
			ContainerController containerController, InspectorController inspectorController,
			EditorController editorController) {
		this.item = item;
		this.itemController = itemController;
		this.containerController = containerController;
		this.inspectorController = inspectorController;
		this.editorController = editorController;
		this.initializeView();
	}

	public void initializeView() {
		this.webcomponent = new WCInspectorItem();
		this.webcomponent.initialize(item);
		this.webcomponent.addWebComponentEventHandler(this);
	}

	public WCInspectorItem getWebComponent() {
		return this.webcomponent;
	}

	public void enableLibraryMode() {
		this.webcomponent.enableLibraryMode();
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case INSPECTOR_ITEM_CLICK_SAVE:
			this.editorController.checkDefinitionCompletness();
			break;
		case INSPECTOR_ITEM_CLICK_REMOVE:
			// In case the control item contains elements, double check if the
			// user really wants to delete the item.
			if (this.item instanceof AbstractControlItem) {
				AbstractControlItem control = (AbstractControlItem) item;
				if (control.getItems().get(0) != null && !Window.confirm("Removing this '" + control.getAnnotation("name")
						+ "' control will also remove " + "the connected '" + control.getItems().get(0).getAnnotation("name")
						+ "' modality." + "\n\nDo you want to proceed?")) {
					break;
				}
			}
			this.containerController.removeItemController(itemController);
			this.inspectorController.flushContent();
			break;
		default:
			Window.alert("Default event of inspector item controller");
			break;
		}
	}
}
