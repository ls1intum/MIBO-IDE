package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.event.shared.GwtEvent;

public class WCEvent extends GwtEvent<WCEventHandler> {

	public static Type<WCEventHandler> TYPE = new Type<WCEventHandler>();

	private final WCEventType event;
	private final AbstractWebComponent webComponent;

	public WCEvent(AbstractWebComponent element, WCEventType event) {
		this.webComponent = element;
		this.event = event;
	}

	@Override
	public Type<WCEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(WCEventHandler handler) {
		handler.onWebComponentEventReceived(this);
	}

	public WCEventType getEventType() {
		return event;
	}

	public AbstractWebComponent getSource() {
		return webComponent;
	}

}
