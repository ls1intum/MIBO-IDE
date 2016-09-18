package edu.tum.ls1.mibo.editor.server;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public class XMLMarshaller {

	private static final Logger log = Logger.getLogger(XMLParser.class.getName());

	public XMLMarshaller() {
		log.setLevel(Level.WARNING);
	}

	/**
	 * Marshall a Mibo object to a document representation
	 * 
	 * @param mibo
	 *            Mibo object that should be marshalled
	 * @return A document representation of the Mibo object
	 */
	public Document marshallMIBO(Mibo mibo) {

		try {

			// Document factory to build the DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Initiate new document
			Document doc = builder.newDocument();

			// Create the root element <mibo>
			Element rootElement = doc.createElement(mibo.getRepresentation());
			doc.appendChild(rootElement);

			// Add the attribuets, in this case mostly the references
			for (Attribute attribute : mibo.getAttributes()) {
				rootElement.setAttribute(attribute.getName(), attribute.getValue());
			}

			// Add every definition to the document
			for (AbstractMiboItem mibo_item : mibo.getItems()) {

				// Create helper instance for convenience
				Definition definition = (Definition) mibo_item;
				
				// Create the XML document of form <definition>...</definition>
				Document definitionNode = this.marshallDefinition(definition);

				// Import the XML's node. Since it contains only one tag which
				// is <definition>, it can be referenced with
				// getDocumentElement(). 'True' parameter ensures a deep copy.
				Node newNode = doc.importNode(definitionNode.getDocumentElement(), true);

				// Append the node to the root document, which ist <mibo>
				rootElement.appendChild(newNode);

			}

			return doc;

		} catch (Exception e) {

			log.log(Level.WARNING, "Could not marshall mibo object to document representation");
			e.printStackTrace();

		}

		return null;
	}

	/**
	 * Marshall a definition object to a document representation
	 * 
	 * @param definition
	 *            Definition object that should be marshalled
	 * @return A document representation of the definition object
	 */
	public Document marshallDefinition(Definition definition) {

		try {

			// Document factory to build the DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Initiate new document
			Document doc = builder.newDocument();

			// Create the root element "definition"
			Element rootElement = doc.createElement(definition.getRepresentation());
			doc.appendChild(rootElement);

			// Update <definition>-level attributes
			for (Attribute attribute : definition.getAttributes()) {

				boolean isRequired = attribute.getConstrainValue("use").equals("required");
				boolean isOptional = !isRequired;
				boolean hasContent = (attribute.getValue() != null);

				// Only add required attributes or optional attributes that
				// contain addional content
				if (isRequired || (isOptional && hasContent)) {

					String name = attribute.getName();
					String value = attribute.getValue();

					rootElement.setAttribute(name, value);
				}

			}

			// Process every section of a definition
			for (AbstractMiboItem definition_item : definition.getItems()) {

				// Create helper instance for convenience
				AbstractSection section = (AbstractSection) definition_item;
				
				// Skip sections without items (e.g. possbile in ENTRY/EXIT)
				if (section.isEmpty()) {
					continue;
				}

				// Create section's element (e.g. <select>)
				Element sectionElement = doc.createElement(section.getRepresentation());
				rootElement.appendChild(sectionElement);

				// Process every section item
				for (AbstractMiboItem item : section.getItems()) {

					// A section item element is a event (modality), fixture or control
					Element sectionItemElement = null;

					// While an event item needs to be constructed from its
					// target namespace reference and its representation ...
					if (item instanceof AbstractEventItem) {

						AbstractEventItem temp = (AbstractEventItem) item;

						// Helper instances
						String namespaceReference = temp.getTargetNamespaceReference();
						String name = temp.getRepresentation();
						
						sectionItemElement = doc.createElement(namespaceReference + ":" + name);

					}
					// ... while any other item can be simply created based on
					// its element's tag representation.
					else {
						sectionItemElement = doc.createElement(item.getRepresentation());
					}

					// In addition, if the section item has been of type "control" item,
					// its content (= event item) needs to be processed
					if (item instanceof AbstractControlItem) {

						// Helper instances
						AbstractControlItem control = (AbstractControlItem) item;
						AbstractEventItem modality = (AbstractEventItem) control.getItems().get(0);

						// Construct event item's element
						String namespaceReference = modality.getTargetNamespaceReference();
						String name = modality.getRepresentation();
						Element modalityEle = doc.createElement(namespaceReference + ":" + name);

						// Add event item's attributes
						for (Attribute attribute : modality.getAttributes()) {
							if (attribute.getName() != null && attribute.getValue() != null) {
								modalityEle.setAttribute(attribute.getName(), attribute.getValue());
							}
						}

						// Nest event item into control item
						sectionItemElement.appendChild(modalityEle);

					}

					// Add abstact event item's attributes
					for (Attribute attribute : item.getAttributes()) {
						if (attribute.getName() != null && attribute.getValue() != null) {
							sectionItemElement.setAttribute(attribute.getName(), attribute.getValue());
						}
					}

					// Add abstract event item into section item
					sectionElement.appendChild(sectionItemElement);
				}
			}

			return doc;

		} catch (Exception e) {

			log.log(Level.WARNING, "Could not marshall definition object to document representation");
			e.printStackTrace();

		}

		return null;

	}

	/**
	 * Transform any document object into a string representation.
	 * 
	 * @param doc
	 *            The document object that should be transformed.
	 * @param isProductive
	 *            Set if the string representation should be productive or
	 *            illustrative. Productive output describes a fully-functionaly
	 *            XML string representation.
	 * @return String representation of document input.
	 */
	public String transformDocToString(Document doc, boolean isProductive) {

		try {

			// Transformer factory to process DOM
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			// Layout the XML document
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			// Seting "standalone" to default value "no" will add an extra line
			// break next to the actual content
			// transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			// Productive XML string output contains additional information
			if (isProductive) {

				// Show "<?xml version="1.0" encoding="UTF-8"?>" at start
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			} else {

				// Hide productive properties
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

			}

			// String writer to process transformation from DOM to String
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.toString();
			
			return output;

		} catch (Exception e) {

			log.log(Level.WARNING, "Could not transform document object to string representation");
			e.printStackTrace();

		}

		return null;
	}

}
