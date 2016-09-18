package edu.tum.ls1.mibo.editor.client.controller.explorer;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.controller.ExplorerController;
import edu.tum.ls1.mibo.editor.client.utility.LayerManager;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEvent;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCEventHandler;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCListSectionItem;
import edu.tum.ls1.mibo.editor.shared.domain.Domain;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;

public class ListSectionItemController implements WCEventHandler {

	private WCListSectionItem webcomponent;
	private Domain item;
	private ExplorerController explorerController;
	private ListSectionController listSectionController;

	public ListSectionItemController(Domain item, ListSectionController listSectionController, ExplorerController editorController) {
		this.item = item;
		this.explorerController = editorController;
		this.listSectionController = listSectionController;
		this.initializeView();
	}

	public void initializeView() {
		WCListSectionItem webcomponent = new WCListSectionItem();
		this.webcomponent = webcomponent;
		webcomponent.initialize(item.getDisplayName(), item.getDisplaySubName(), null);
		webcomponent.addWebComponentEventHandler(this);
	}

	public WCListSectionItem getWebComponent() {
		return this.webcomponent;
	}

	@Override
	public void onWebComponentEventReceived(WCEvent event) {

		switch (event.getEventType()) {
		case LIST_SECTION_ITEM_DELETE:
			
			// Check if user really wants to delete the item
			if(!Window.confirm("Do you want to delete this entry?")){
				break;
			}
			
			// Remove the item from the overall structure
			this.explorerController.removeItem(this.item);
			
			// Remove the item from the editor's view
			this.listSectionController.removeItemController(this);
			
			break;
		case LIST_SECTION_ITEM_NEXT:
			this.listSectionController.hightlightItem(this);
			if (item instanceof Definition) {
				this.webcomponent.showDetails();
			}
			this.explorerController.updateView(item);
			break;
		case LIST_SECTION_ITEM_EDIT:
			LayerManager.getInstance().showLoadingOverlayer();
			this.explorerController.loadDefinition((Definition) item);
			break;
		default:
			Window.alert("Default event of List Section Item controller");
			break;
		}
	}
}
