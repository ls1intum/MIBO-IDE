package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.List;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;

public class ScopeItemController implements ItemController, WCEventHandler {

	private WCScopeItem webcomponent;
	private AbstractScopeItem scopeItem;
	private EditorController editorController;
	private SectionController sectionController;

	public ScopeItemController(AbstractScopeItem scopeItem, EditorController editorController,
			SectionController sectionController) {
		this.scopeItem = scopeItem;
		this.editorController = editorController;
		this.sectionController = sectionController;
		this.initializeView();
	}

	public void initializeView() {
		this.webcomponent = new WCScopeItem();
		this.webcomponent.initialize(scopeItem.getAnnotation("icon"), scopeItem.getAnnotation("name"));
		this.webcomponent.addWebComponentEventHandler(this);
	}

	public WCScopeItem getWebcomponent() {
		return this.webcomponent;
	}

	public AbstractScopeItem getModel() {
		return this.scopeItem;
	}

	@Override
	public void check(List<ConsoleItem> validationLog) {

		// Validate own model
		if (!this.scopeItem.isValid()) {

			// [EXTENSION]
			// Add visual representation for invalide state here

			String message = "'" + this.scopeItem.getAnnotation("name") + "' is invalide";
			validationLog.add(new ConsoleItem(ConsoleLogType.ERROR, message));

		} else {

			// [EXTENSION]
			// Reset visual representation to normal state

		}

	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case WEBCOMPONENT_ITEM_CLICK_INFO:
			InspectorItemController inspectorItem = new InspectorItemController(this.scopeItem, this,
					this.sectionController, editorController.getInspectorController(), editorController);
			editorController.getInspectorController().addWebComponent(inspectorItem.getWebComponent());
			break;
		case WEBCOMPONENT_ITEM_CLICK_DELETE:
			this.sectionController.removeItemController(this);
			this.editorController.getInspectorController().flushContent();
			break;
		default:
			Window.alert("Default event of scope item controller");
			break;
		}
	}
}
