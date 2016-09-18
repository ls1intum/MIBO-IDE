package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCDefinitionSection;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;

public class SectionController implements ContainerController, WCEventHandler {

	private WCDefinitionSection webcomponent;
	private AbstractSection model;
	private EditorController editor;
	private List<ItemController> item_controllers;

	public SectionController(AbstractSection model, EditorController editor) {
		this.model = model;
		this.editor = editor;
		this.item_controllers = new ArrayList<ItemController>();
		this.initializeView();
	}

	/**
	 * Setup the controller's view<br>
	 * 1. Initialize WebComponent<br>
	 * 2. Setup the WebComponent (Register EventHandler, ...)<br>
	 * 3. Attach to the WebComponent to parent element
	 */
	private void initializeView() {

		this.webcomponent = new WCDefinitionSection();
		this.webcomponent.addWebComponentEventHandler(this);
		this.webcomponent.setHeading(model.getAnnotation("name"));

		// Scope-sections should be displayed separately
		if (model.getAnnotation("name").equals("Scope")) {
			this.webcomponent.setHorizontalLayout();
			DOM.getElementById("sections-container-top").appendChild(webcomponent.getElement());
		} else {
			DOM.getElementById("sections-container-bottom").appendChild(webcomponent.getElement());
		}

		// Load definition content in case it is available
		if (!model.getItems().isEmpty()) {
			this.loadContent(model.getItems());
		}
	}

	// ---------- GET/SET ---------

	@Override
	public void addItemController(ItemController controller) {
		this.item_controllers.add(controller);
		this.model.addItem(controller.getModel());
		this.webcomponent.appendChild(controller.getWebcomponent().getElement());

		// Initiate a check for completness since the model content changed
		this.editor.checkDefinitionCompletness();
	}

	@Override
	public void removeItemController(ItemController controller) {
		this.item_controllers.remove(controller);
		this.model.removeItem(controller.getModel());
		this.webcomponent.removeChild(controller.getWebcomponent().getElement());

		// Initiate a check for completness since the model content changed
		this.editor.checkDefinitionCompletness();
	}

	public AbstractSection getModel() {
		return this.model;
	}

	public List<ItemController> getSubControllers() {
		return this.item_controllers;
	}

	// ---------- LOADING ----------

	public void loadContent(List<AbstractMiboItem> items) {

		for (AbstractMiboItem item : items) {

			ItemController controller = null;

			if (item instanceof AbstractScopeItem) {

				controller = new ScopeItemController((AbstractScopeItem) item, this.editor, this);

			} else if (item instanceof AbstractEventItem) {

				controller = new EventItemController((AbstractEventItem) item, this.editor, this);

			} else if (item instanceof AbstractControlItem) {

				controller = new ControlItemController((AbstractControlItem) item, this.editor, this);

				if (((AbstractControlItem) item).getItems().get(0) != null) {

					ControlItemController controller_reference = (ControlItemController) controller;
					AbstractEventItem modality = (AbstractEventItem) ((AbstractControlItem) item).getItems().get(0);
					EventItemController modality_controller = new EventItemController(modality, this.editor,
							controller_reference);
					controller_reference.addItemController(modality_controller);

				}

			}

			this.item_controllers.add(controller);
			this.webcomponent.appendChild(controller.getWebcomponent().getElement());

		}

	}

	// ---------- VALIDATION ----------

	public void check(List<ConsoleItem> validationLog) {

		// Validate own model
		if (this.model.isRequired() && !this.model.isValid()) {
			this.webcomponent.changeStateToInvalid();
			String output = " The '" + this.model.getAnnotation("name") + "' section is incomplete.";
			ConsoleItem item = new ConsoleItem(ConsoleLogType.ERROR, output);
			validationLog.add(item);
		} else {
			this.webcomponent.changeStateToNone();
		}

		// Forward check call
		for (ItemController controller : item_controllers) {
			controller.check(validationLog);
		}
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case DEFINITION_SECTION_CLICK_INFO:
			LayerManager.getInstance().showToast(model.getAnnotation("description") + ".");
			break;
		case DEFINITION_SECTION_CLICK_ADD:
			LayerManager.getInstance().showToast("Drag and drop an element from the interaction library into this section.");
			break;
		case DEFINITION_SECTION_CLICK_STATE:
			LayerManager.getInstance().showToast("This section needs to contain at least one element.");
			break;
		case DEFINITION_SECTION_DRAG_ENTER:
			if (model.applicable(editor.getDraggedItem())) {
				this.webcomponent.changeAdditionToApplicable();
			} else {
				this.webcomponent.changeAdditionToInapplicable();
				String message = "";
				message = "This element cannot be dropped into ";
				message += this.model.getAnnotation("name").toLowerCase();
				message += " section.";
				LayerManager.getInstance().showToast(message);
			}
			break;
		case DEFINITION_SECTION_DRAG_LEAVE:
			this.webcomponent.changeAdditionToStandard();
			break;
		case DEFINITION_SECTION_DROP:

			// Step 1 – Bring back the default layout of the "Addition" area
			this.webcomponent.changeAdditionToStandard();

			// Step 2 - Check if the section can handle the dropped item ...
			if (model.applicable(editor.getDraggedItem())) {

				// Step 3 – Evaluate the content of the draggedItem and create
				// a new controller based on the model found in the dragged item
				// and add the controller to the controller set
				if (editor.getDraggedItem() instanceof AbstractEventItem) {

					AbstractEventItem item_copy = (AbstractEventItem) editor.getDraggedItem().getCopy();
					EventItemController controller = new EventItemController(item_copy, editor, this);
					this.addItemController(controller);

				} else if (editor.getDraggedItem() instanceof AbstractScopeItem) {

					AbstractScopeItem item_copy = (AbstractScopeItem) editor.getDraggedItem().getCopy();
					ScopeItemController controller = new ScopeItemController(item_copy, editor, this);
					this.addItemController(controller);

				} else if (editor.getDraggedItem() instanceof AbstractControlItem) {

					AbstractControlItem item_copy = (AbstractControlItem) editor.getDraggedItem().getCopy();
					ControlItemController controller = new ControlItemController(item_copy, editor, this);
					this.addItemController(controller);
				}

				// Clear dragged item since it "took" the dragged item
				editor.clearDraggedItem();

			}
			// Step 2 - ... and show error if not.
			else {
				String message = "";
				message = "This element cannot be dropped into ";
				message += this.model.getAnnotation("name").toLowerCase();
				message += " section.";
				LayerManager.getInstance().showToast(message);
			}
			break;
		default:
			Window.alert("Default event");
			break;
		}
	}
}
