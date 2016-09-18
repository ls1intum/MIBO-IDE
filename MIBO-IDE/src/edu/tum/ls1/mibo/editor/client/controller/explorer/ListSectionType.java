package edu.tum.ls1.mibo.editor.client.controller.explorer;

public enum ListSectionType {

	TARGET_GROUPS("Control Systems"), USERS("Users"), DEFINITIONS("Interaction Definitions");

	private final String displayName;

	ListSectionType(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
