package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.List;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCControlItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;

public class ControlItemController implements ItemController, ContainerController, WCEventHandler {

	private WCControlItem webcomponent;
	private AbstractControlItem model;
	private EditorController editorController;
	private SectionController sectionController;
	private ItemController item_controller;

	public ControlItemController(AbstractControlItem control, EditorController editorController,
			SectionController sectionController) {
		this.model = control;
		this.editorController = editorController;
		this.sectionController = sectionController;
		this.item_controller = null;
		this.initializeView();
	}

	public void initializeView() {
		this.webcomponent = new WCControlItem();
		this.webcomponent.initialize(model.getAnnotation("icon"), model.getAnnotation("name"), model.getAnnotation("type"));
		this.webcomponent.addWebComponentEventHandler(this);
	}

	// ---------- GET/SET ----------

	@Override
	public void addItemController(ItemController controller) {
		this.item_controller = controller;
		this.model.addItem(controller.getModel());
		this.webcomponent.appendChild(controller.getWebcomponent().getElement());

		// Initiate a check for completness since the model content changed
		this.editorController.checkDefinitionCompletness();
	}

	@Override
	public void removeItemController(ItemController controller) {
		this.item_controller = null;
		this.model.removeItem(controller.getModel());
		this.webcomponent.removeChild(controller.getWebcomponent().getElement());

		// Initiate a check for completness since the model content changed
		this.editorController.checkDefinitionCompletness();
	}

	public ItemController getController() {
		return this.item_controller;
	}

	public WCControlItem getWebcomponent() {
		return this.webcomponent;
	}

	public AbstractControlItem getModel() {
		return this.model;
	}

	@Override
	public void check(List<ConsoleItem> validationLog) {
		// Validate own model
		if (!this.model.isValid()) {
			this.webcomponent.changeStateToInvalid();
			String output = " The '" + this.model.getAnnotation("name")
					+ "' element is incomplete or contains empty or invalide attributes.";
			ConsoleItem item = new ConsoleItem(ConsoleLogType.ERROR, output);
			validationLog.add(item);

		} else {
			this.webcomponent.changeStateToNone();
		}

		// Forward check call
		if (this.item_controller != null) {
			item_controller.check(validationLog);
		}
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case WEBCOMPONENT_ITEM_CLICK_INFO:
			InspectorItemController inspectorItem = new InspectorItemController(this.model, this,
					this.sectionController, editorController.getInspectorController(), editorController);
			editorController.getInspectorController().addWebComponent(inspectorItem.getWebComponent());
			break;
		case WEBCOMPONENT_ITEM_CLICK_DELETE:
			if ((this.item_controller == null) || Window
					.confirm("Removing this '" + this.model.getAnnotation("name") + "' control will also remove "
							+ "the connected '" + this.item_controller.getModel().getAnnotation("name") + "' modality."
							+ "\n\nDo you want to proceed?")) {
				this.sectionController.removeItemController(this);
				this.editorController.getInspectorController().flushContent();
			}
			break;
		case CONTROL_ITEM_DRAG_ENTER:
			if (model.applicable(editorController.getDraggedItem())) {
				this.webcomponent.changeAdditionToApplicable();
			} else {
				this.webcomponent.changeAdditionToInapplicable();
				String message = "";
				message = "Element cannot be dropped into ";
				message += this.model.getAnnotation("name").toLowerCase() + ".";
				LayerManager.getInstance().showToast(message);
			}
			break;
		case CONTROL_ITEM_DRAG_LEAVE:
			this.webcomponent.changeAdditionToStandard();
			break;
		case CONTROL_ITEM_DROP:
			this.webcomponent.changeAdditionToStandard();
			if (this.item_controller == null) {
				if (model.applicable(editorController.getDraggedItem())) {
					
					// Create sub-controller based on the dragged item's model
					AbstractEventItem item_copy = (AbstractEventItem) editorController.getDraggedItem().getCopy();
					EventItemController controller = new EventItemController(item_copy, editorController, this);
					this.addItemController(controller);
					
					// Clear dragged item since it "took" the dragged item
					editorController.clearDraggedItem();
				}
			} else {
				String message = "";
				message += this.model.getAnnotation("name");
				message += " already contains a modality.";
				LayerManager.getInstance().showToast(message);
			}
			break;
		default:
			Window.alert("Default event of control controller");
			break;
		}
	}

}
