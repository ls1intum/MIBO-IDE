package edu.tum.ls1.mibo.editor.shared.model.basis;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import edu.tum.ls1.mibo.editor.shared.model.Container;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.items.ModalityItem;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;

public class Mibo extends AbstractMiboItem implements Container, IsSerializable {

	// Content
	private List<AbstractMiboItem> definitions;

	public Mibo() {

		// Set up representation
		this.setRepresentation("mibo");

		// Initialize attributes
		this.initializeAttributes();

		// Initialize content
		this.definitions = new ArrayList<AbstractMiboItem>();

	}

	// -------------------- OVERRIDE CONTAINER --------------------

	@Override
	public void addItem(AbstractMiboItem item) {
		if (this.applicable(item)) {
			this.definitions.add(item);
		}
	}

	@Override
	public void removeItem(AbstractMiboItem item) {
		if (this.definitions.contains(item)) {
			this.definitions.remove(item);
		}
	}

	@Override
	public List<AbstractMiboItem> getItems() {
		return this.definitions;
	}

	@Override
	public Boolean isEmpty() {
		return definitions.isEmpty();
	}

	@Override
	public Boolean isRequired() {
		return true;
	}

	@Override
	public Boolean applicable(Object object) {

		if (object instanceof Definition) {
			return true;
		}

		return false;

	}

	// -------------------- OVERRIDE ITEM --------------------

	@Override
	public void updateAttribute(String name, String value) {

		// Check if model already contains attribute ...
		for (Attribute attribute : this.attributes) {
			if (attribute.getName().equals(name)) {
				if (value.equals("")) {
					attribute.setValue(null);
				} else {
					attribute.setValue(value);
				}
				return;
			}
		}

		// ... otherwise create it.
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setValue(value);
		this.attributes.add(attribute);

	}

	@Override
	public AbstractMiboItem getCopy() {

		// [EXTENSION]
		// Currently, a copy()-method is neither planned nor needed.

		return null;
	}

	@Override
	public boolean matches(Object object) {

		// [EXTENSION]
		// Currently, a matches()-method is neither planned nor needed.

		return false;
	}

	@Override
	public Boolean isValid() {
		return !this.isEmpty();
	}

	// ---------- SPECIFIC METHODS ----------

	/**
	 * Retrieve all references
	 * 
	 * <p>
	 * Every element's attribute that contains a namespace reference, is
	 * considered as a reference.
	 * <ul>
	 * <li>Example: <code>xmlns:voice="http://www1.in.tum.de/mibo/voice"</code>
	 * </li>
	 * </ul>
	 * This method is especially used when parsing an existing Mibo document to
	 * connect the containing modalities. Usually, after finishing this process,
	 * Mibo's refreshReferences() methods is called.<br>
	 * <br>
	 * <i>Note: Default namespace references such as <code>xmlns:xsi</code> will
	 * be ignored and therefore won't be in the return value.
	 * 
	 * @return A list of references.
	 */
	public List<Record> getReferences() {

		List<Record> list = new ArrayList<Record>();

		for (Attribute attribute : this.attributes) {

			// Assumption: A reference always contain a column. Furthermore, all
			// default references that contain a "xsi" are ignored.
			if (attribute.getName().contains(":") && !attribute.getName().contains("xsi")) {

				String[] elements = attribute.getName().split(":");
				String value = elements[1];

				list.add(new Record(value, attribute.getValue()));
			}
		}

		return list;
	}

	/**
	 * Refresh Mibo's references.
	 * 
	 * <p>
	 * Every element's attribute that contains a namespace reference, is
	 * considered as a reference.
	 * <ul>
	 * <li>Example: <code>xmlns:voice="http://www1.in.tum.de/mibo/voice"</code>
	 * </li>
	 * </ul>
	 * "Reference keys" that are used to shorten the appearance of a modality
	 * require references in the root element of a XML document (=mibo element).
	 * <ul>
	 * <li>Example: <code>&lt;voice:startDimming&gt;</code></li>
	 * </ul>
	 * In order to avoid duplicate, out-dated or wrong references, this method
	 * should be called every time the Mibo structure is used as an entire
	 * object, e.g. when marshalling it.<br>
	 * <br>
	 * <i>Note: All other attributes of the Mibo element will be reset to
	 * default by this method.
	 * </p>
	 */
	public void refreshReferences() {

		// Remove all existing reference, especially those of modalities
		this.initializeAttributes();

		for (AbstractMiboItem mibo_item : this.definitions) {

			// Create helper instance for convenience
			Definition definition = (Definition) mibo_item;

			for (AbstractMiboItem definition_item : definition.getItems()) {

				// Create helper instance for convenience
				AbstractSection section = (AbstractSection) definition_item;

				for (AbstractMiboItem item : section.getItems()) {

					AbstractEventItem eventItem = null;

					// Modalities are either a direct section item ...
					if (item instanceof AbstractEventItem) {
						eventItem = (ModalityItem) item;
					}
					// ... or part of a control element.
					else if (item instanceof AbstractControlItem) {
						AbstractControlItem temp = (AbstractControlItem) item;
						eventItem = (AbstractEventItem) temp.getItems().get(0);
					}
					// Other elements don't need to be considered.
					else {
						continue;
					}

					// The key contains the prefix "xmls" and a reference name.
					String key = "xmlns:" + eventItem.getTargetNamespaceReference();

					// The value is the actual target namespace of the modality.
					String value = eventItem.getTargetNamespace();

					this.updateAttribute(key, value);
				}
			}
		}
	}

	/**
	 * Checks if this Mibo objects contains the given definition.
	 * 
	 * <p>
	 * The decision, if this object contains the definition, is made based on
	 * the unique identfiers of its content items. If it contains the
	 * definition, the reference to the list definition will be returned.<br>
	 * The fact, that a Mibo object contains a definition <b>does not</b> imply
	 * that these definitions are equal. To verify definition equality, use the
	 * isEqual() method of definition.
	 * </p>
	 * 
	 * @param definition
	 *            The definition that should be tested.
	 * @return The definition reference to the input definition.
	 */
	public Definition containsDefinition(Definition definition) {

		String definition_ID = definition.getAttribute("id").getValue();

		for (AbstractMiboItem mibo_item : this.definitions) {

			// Create helper instance for convenience
			Definition temp = (Definition) mibo_item;

			String temp_ID = temp.getAttribute("id").getValue();

			if (definition_ID.equals(temp_ID)) {
				return temp;
			}

		}

		return null;
	}

	/**
	 * Set up default values for a blank Mibo element.
	 */
	private void initializeAttributes() {

		// Remove all existing reference, especially those to modalities
		this.attributes.clear();

		// Add basic references
		this.updateAttribute("xmlns", "http://www1.in.tum.de/mibo");
		this.updateAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		this.updateAttribute("xsi:schemaLocation", "http://www1.in.tum.de/mibo MIBO_Scheme.xsd");
	}

}
