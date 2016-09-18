package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public abstract class AbstractWebComponent implements HasHandlers {

	// Element needs to be initialized by child class
	protected Element element;
	protected HandlerManager handlerManager;

	public AbstractWebComponent() {
		handlerManager = new HandlerManager(this);
	}

	// ---------- GENERAL METHODS ----------

	public Element getElement() {
		return this.element;
	}

	// -------------------- EVENT METHODS --------------------

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public HandlerRegistration addWebComponentEventHandler(WCEventHandler handler) {
		return handlerManager.addHandler(WCEvent.TYPE, handler);
	}
	
	public void removeWebComponentEventHandler(WCEventHandler handler) {
		handlerManager.removeHandler(WCEvent.TYPE, handler);
	}

}
