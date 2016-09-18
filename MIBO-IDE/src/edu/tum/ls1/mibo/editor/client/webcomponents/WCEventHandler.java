package edu.tum.ls1.mibo.editor.client.webcomponents;

import com.google.gwt.event.shared.EventHandler;

public interface WCEventHandler extends EventHandler {
	
	void onWebComponentEventReceived(WCEvent event);
	
}
