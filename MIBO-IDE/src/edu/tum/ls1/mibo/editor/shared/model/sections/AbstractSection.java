package edu.tum.ls1.mibo.editor.shared.model.sections;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.Container;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;

public abstract class AbstractSection extends AbstractMiboItem implements Container, IsSerializable {

	// Containing items
	protected List<AbstractMiboItem> items;

	public AbstractSection() {
		this.items = new ArrayList<AbstractMiboItem>();
	}

	// ---------- CONTAINER METHODS ----------

	public void addItem(AbstractMiboItem item) {
		this.items.add(item);
	}

	public void removeItem(AbstractMiboItem item) {
		this.items.remove(item);
	}

	public List<AbstractMiboItem> getItems() {
		return this.items;
	}

	public Boolean isEmpty() {
		return this.items.isEmpty();
	}

	// Section-specific implementation required
	public abstract Boolean isRequired();

	// Section-specific implementation required
	public abstract Boolean applicable(Object object);

	// ---------- SPECIFIC METHODS ----------

	/**
	 * Returns all equal items.
	 * 
	 * <p>
	 * An item is considered equal if it has the same type.
	 * </p>
	 * 
	 * @param section
	 *            The section that should be compared.
	 * @return A list of the equal items.
	 */
	public List<AbstractMiboItem> getEqualItems(AbstractSection section) {

		List<AbstractMiboItem> items = new ArrayList<AbstractMiboItem>();

		for (AbstractMiboItem item : this.getItems()) {
			if (section.getItems().contains(item)) {
				items.add(item);
			}
		}

		return items;
	}

	/**
	 * Check if one section is a <b>proper</b> subset of another seciton.
	 * 
	 * <p>
	 * A proper subset is a set that contains all items of another set. However,
	 * it must contain less items. <i>Note:</i> This check considers all direct
	 * children of a section.
	 * </p>
	 * 
	 * @param section
	 *            The section that should be tested.
	 * @return Returns <code>true</code> if the current section is a subset of
	 *         the given section.
	 */
	public boolean isProperSubsetOf(AbstractSection section) {

		// A proper subset must have at least n-1 entries
		if (this.getItems().size() >= section.getItems().size()) {
			return false;
		}

		// Every item must be equal
		for (AbstractMiboItem item : this.getItems()) {
			if (!section.getItems().contains(item)) {
				return false;
			}
		}

		return true;
	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public boolean matches(Object object) {

		AbstractSection section = null;

		// Minimal requirement is to have the same class type
		if (object instanceof AbstractSection) {
			section = (AbstractSection) object;
		} else {
			return false;
		}

		// Check element name
		if (!this.getRepresentation().equals(section.getRepresentation())) {
			return false;
		}

		// [EXTENSION]
		// Add attributes check here if sections can gain attribute support

		// Check number of items
		if (this.getItems().size() != section.getItems().size()) {
			return false;
		}

		// Forward isEqual() call
		for (int i = 0; i < this.getItems().size(); i++) {

			if (!this.getItems().get(i).matches(section.getItems().get(i))) {
				return false;
			}
		}

		return true;

	}

	@Override
	public Boolean isValid() {
		return !this.isEmpty();
	}

	// -------------------- OVERRIDE OBJECT --------------------

	// [EXTENSION]
	// In case it will be required to compare the type of a section, the
	// equals() method should be overridden to enable this check.

}
