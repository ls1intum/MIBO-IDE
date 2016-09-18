package edu.tum.ls1.mibo.editor.client.utility;

import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;

public class ConsoleItem {

	// The type will be displayed in a prominent way using a color code system.
	private ConsoleLogType type;

	// The message will be displayed next to the type and should not contain
	// more than 80 characters.
	private String message;

	public ConsoleItem(ConsoleLogType type, String message) {
		this.type = type;
		this.message = message;
	}

	// ---------- GET & SET ----------

	public ConsoleLogType getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	// ---------- OVERRIDE ----------

	@Override
	public boolean equals(Object o) {

		ConsoleItem input = null;

		if (o instanceof ConsoleItem) {
			input = (ConsoleItem) o;
		} else {
			return false;
		}

		if (!this.getType().equals(input.getType())) {
			return false;
		}

		if (!this.getMessage().equals(input.getMessage())) {
			return false;
		}

		return true;
	}

}
