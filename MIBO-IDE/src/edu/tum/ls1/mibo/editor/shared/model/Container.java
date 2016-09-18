package edu.tum.ls1.mibo.editor.shared.model;

import java.util.List;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;

public interface Container {

	/**
	 * Adds a given item to the container's list of items.
	 * 
	 * <p>
	 * Depending on the container's type, this <code>addItem()</code> method may
	 * be implemented in different ways. Consider the following types of
	 * containers:
	 * <ul>
	 * <li>Container with capacity for <b>=1</b> child: This method may override
	 * the existing child.</lu>
	 * <li>Container with capacity for <b>>=1</b> children: This method will add
	 * the input item to a list of children.</lu>
	 * </ul>
	 * </p>
	 * 
	 * @param item
	 *            Item that should be appended to the container
	 */
	public void addItem(AbstractMiboItem item);

	/**
	 * Removes a given item from the container's list of items
	 * 
	 * @param item
	 *            Item that should be removed from the container
	 */
	public void removeItem(AbstractMiboItem item);

	/**
	 * Returns a list of the containing items
	 * 
	 * @return A list of containing items
	 */
	public List<AbstractMiboItem> getItems();

	/**
	 * Returns size state of the containing item list.
	 * 
	 * @return Returns <code>true</code> if the item list does not contain any
	 *         entries.
	 */
	public Boolean isEmpty();

	/**
	 * Returns required state of the container in a parent model structure.
	 * 
	 * @return Returns <code>true</code> if the container is required
	 */
	public Boolean isRequired();

	/**
	 * Checks if the given object can be added to the container.
	 * 
	 * @param object
	 *            The object that should be added to the container.
	 * @return Returns <code>true</code> if the container can handle the object.
	 * 
	 */
	public Boolean applicable(Object object);

}
