package edu.tum.ls1.mibo.editor.shared.domain;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;

public class User implements IsSerializable, Domain {

	// Identifier
	private String identifier;

	// User's MIBO schema, containing the definitions
	private Mibo mibo;

	public User() {
		this.mibo = new Mibo();
	}

	public User(String identifier) {
		this.mibo = new Mibo();
		this.identifier = identifier;
	}

	// ---------- GET/SET ----------

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return this.identifier;
	}

	public void setMIBOMibo(Mibo mibo) {
		this.mibo = mibo;
	}

	public Mibo getMIBOMibo() {
		return this.mibo;
	}

	/**
	 * Un-wrapping sub-content for convenience.
	 * 
	 * @return A list of definitions that are contained by the MIBO wrapper.
	 */
	public List<AbstractMiboItem> getDefinitions() {
		return this.mibo.getItems();
	}

	// Try to generate displayName by getting the word after the last point
	@Override
	public String getDisplayName() {
		int position = this.identifier.lastIndexOf(".");
		if (position >= -1) {
			String name = this.identifier.substring(position + 1, this.identifier.length());
			String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
			return capitalizedName;
		}
		return this.identifier;
	}

	@Override
	public String getDisplaySubName() {
		return this.identifier;
	}

}
