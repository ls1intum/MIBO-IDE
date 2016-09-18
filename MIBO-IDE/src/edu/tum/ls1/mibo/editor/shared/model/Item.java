package edu.tum.ls1.mibo.editor.shared.model;

import java.util.List;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public interface Item {

	// ---------- ANNOTATIONS ----------

	public String getAnnotation(String key);

	public void setAnnotation(String name, String content);

	// ---------- ATTRIBUTES ----------

	public List<Attribute> getAttributes();

	public Attribute getAttribute(String name);

	public void addAttribute(Attribute attribute);

	public void updateAttribute(String name, String value);

	public void removeAttribute(String name);

	// ---------- REPRESENTATION ----------

	public String getRepresentation();

	public void setRepresentation(String element_name);

	// ---------- COPY ----------

	public AbstractMiboItem getCopy();

	// ---------- MATCHING ----------

	/**
	 * Check if a given item is the identical representation of the current
	 * item.
	 * 
	 * <p>
	 * What "identical" in that context means:
	 * <ul>
	 * <li>The items have the same type (e.g. 2 x "Up/Down" gesture)</li>
	 * <li>Every item specific state or setting is of the same value</li>
	 * <li>Every attribute is a) present, b) at the same position c) has the
	 * same value</li>
	 * </ul>
	 * 
	 * What "identical" NOT means:
	 * <ul>
	 * <li>Identical references</li>
	 * </ul>
	 * </p>
	 * 
	 * @param object
	 *            The item that should be tested.
	 * @return Returns <code>true</code> if the items are identical and
	 *         <code>false</code> if they are not.
	 */
	public boolean matches(Object object);

	// ---------- VALIDATION ----------

	public Boolean isValid();

}
