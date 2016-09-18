package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.List;

import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.AbstractWebComponent;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;

public interface ItemController {

	public AbstractWebComponent getWebcomponent();

	public AbstractMiboItem getModel();
	
	public void check(List<ConsoleItem> validationLog);
	
}
