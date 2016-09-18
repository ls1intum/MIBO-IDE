package edu.tum.ls1.mibo.editor.client.utility;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

public class LayerManager {

	private static Element overlayer_reference;
	private static LayerManager instance = null;

	protected LayerManager() {
	}

	public static LayerManager getInstance() {
		if (instance == null) {
			instance = new LayerManager();
			overlayer_reference = DOM.getElementById("overlayer");
		}
		return instance;
	}
	
	// ---------- OVERLAYER ----------
	
	public void showLoadingOverlayer(){
		overlayer_reference.getStyle().setOpacity(1);
		overlayer_reference.getStyle().clearProperty("pointerEvents");
	}
	
	public void hideLoadingOverlayer(){
		overlayer_reference.getStyle().setOpacity(0);
		overlayer_reference.getStyle().setProperty("pointerEvents", "none");
	}

	// ---------- TEXT ----------
	
	public void showToast(String information){
		this.JSNI_showToast(information);
	}
	
	private native void JSNI_showToast(String text) /*-{
		$doc.querySelector('#layer_toast').setAttribute("text", text);
		$doc.querySelector('#layer_toast').show();
	}-*/;
	
	public native void consoleLog(String message) /*-{
    	console.log(message );
	}-*/;
	
}
