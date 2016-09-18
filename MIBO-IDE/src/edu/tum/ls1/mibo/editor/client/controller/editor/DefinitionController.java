package edu.tum.ls1.mibo.editor.client.controller.editor;

import java.util.ArrayList;
import java.util.List;

import edu.tum.ls1.mibo.editor.client.controller.EditorController;
import edu.tum.ls1.mibo.editor.client.utility.ConsoleItem;
import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;

public class DefinitionController {

	private Definition model;
	private EditorController editor_controller;
	private List<SectionController> section_controllers;

	/*
	 * Currently, DefinitionController contains no view representation
	 */
	public DefinitionController(Definition model, EditorController editorController) {
		this.model = model;
		this.editor_controller = editorController;
		this.section_controllers = new ArrayList<SectionController>();
		this.initializeSubControllers();
	}

	public void initializeSubControllers() {
		
		for (AbstractMiboItem definition_item : this.model.getItems()) {
			
			// Create helper instance for convenience
			AbstractSection section = (AbstractSection) definition_item;
			
			SectionController section_controller = new SectionController(section, this.editor_controller);
			this.section_controllers.add(section_controller);
		}
		
	}

	// ---------- GET/SET ----------
	
	public Definition getModel() {
		return this.model;
	}
	
	public List<SectionController> getSubControllers(){
		return this.section_controllers;
	}
	
	// ---------- VALIDATION ----------

	public void check(List<ConsoleItem> validationLog) {
		
		// Check own model
		if(!this.model.isValid()){
			String output = " The '" + this.model.getAnnotation("name") + "' is incomplete.";
			ConsoleItem item = new ConsoleItem(ConsoleLogType.ERROR, output);
			validationLog.add(item);
		}
		
		// Update visual appearance
		this.editor_controller.updateHeading(this.model.getDisplayName());
		
		// Forward check call
		for (SectionController controller : section_controllers) {
			controller.check(validationLog);
		}
		
	}

}
