package edu.tum.ls1.mibo.editor.shared.domain;

public interface Domain {

	/**
	 * A display name for the domain item.
	 * 
	 * <p>
	 * Usually a human-readable name.
	 * </p>
	 * 
	 * @return A name.
	 */
	public String getDisplayName();

	/**
	 * A display name for the domain item.
	 * 
	 * <p>
	 * Usually a human-readable name with a technical background.
	 * </p>
	 * 
	 * @return A sub-name.
	 */
	public String getDisplaySubName();

}
