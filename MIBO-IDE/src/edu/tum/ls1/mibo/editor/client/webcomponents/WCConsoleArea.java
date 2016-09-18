package edu.tum.ls1.mibo.editor.client.webcomponents;

import java.util.Date;

import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.DOM;

public class WCConsoleArea extends AbstractWebComponent {

	public enum ConsoleLogType {
		ERROR, WARNING, INFO, SUCCESS, ACTION
	}

	public enum ConsoleActionType {
		DEBUG("debugging"), TEST("testing"), COMPILE("compiling"), APPLY("applying");

		private final String actionName;

		ConsoleActionType(String actionName) {
			this.actionName = actionName;
		}

		public String getActionName() {
			return actionName;
		}
	}

	public WCConsoleArea(String heading) {
		super();
		this.element = DOM.createElement("console-area");
		this.element.setAttribute("name", heading.toUpperCase());
		this.JSNI_registerEvents(this, this.element);
	}

	// -------------------- SPECIFIC METHODS --------------------

	public void addStartDelimiter(ConsoleActionType action) {
		this.addMessage(ConsoleLogType.ACTION, "Start " + action.getActionName() + ".");
	}

	public void addStopDelimiter(ConsoleActionType action) {
		this.addMessage(ConsoleLogType.ACTION, "Stop  " + action.getActionName()
				+ ".<br><span style=\"color:grey\">Choose an action to continue ...</span>");
	}

	public void addMessage(ConsoleLogType code, String content) {

		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm:ss:SS");

		String output = "";
		output += "<span style=\"color:grey\">"
				+ dtf.format(date, TimeZone.createTimeZone(this.getClientOffsetTimeZone())) + "</span> ";

		switch (code) {
		case ERROR:
			output += "<span style=\"color:red\">ERROR</span> ";
			break;
		case SUCCESS:
			output += "<span style=\"color:green\">SUCCESS</span> ";
			break;
		case INFO:
			output += "<span style=\"color:grey\">INFO</span> ";
			break;
		case WARNING:
			output += "<span style=\"color:orange\">WARNING</span> ";
			break;
		case ACTION:
			output += "<span style=\"color: #3F51B5;\">ACTION</span> ";
		default:
			break;
		}

		output += content;

		Element div = DOM.createElement("div");
		div.setInnerHTML(output);
		this.JSNI_appendChild(this.element, div);
		this.JSNI_updatePosition(this.element);
	}

	public void flush() {
		this.JSNI_flush(this.element);
	}

	// -------------------- CALLBACK METHODS --------------------

	public void clearClicked() {
		WCEvent event = new WCEvent(this, WCEventType.CONSOLE_AREA_CLICK_CLEAR);
		fireEvent(event);
	}

	// -------------------- NATIVE METHODS --------------------

	private native void JSNI_registerEvents(AbstractWebComponent webcomponent, Element element) /*-{
		element.addEventListener("clear-click", function(e, d, b){
			e.stopPropagation();
			webcomponent.@edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea::clearClicked()();
		});
	}-*/;
	
	private native void JSNI_appendChild(Element element, Element child) /*-{
		$wnd.appendChildToElement(element, child);
	}-*/;
	
	private native void JSNI_flush(Element element) /*-{
		element.flush();
	}-*/;	
	
	private native int getClientOffsetTimeZone() /*-{
    	return new Date().getTimezoneOffset();
	}-*/;
	
	private native int JSNI_updatePosition(Element element) /*-{
		element.updateConsole();
	}-*/;
	
}
