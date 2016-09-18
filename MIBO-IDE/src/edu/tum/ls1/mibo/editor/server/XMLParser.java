package edu.tum.ls1.mibo.editor.server;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.gargoylesoftware.htmlunit.javascript.host.Node;

import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.FixtureItem;
import edu.tum.ls1.mibo.editor.shared.model.items.ModalityItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;
import edu.tum.ls1.mibo.editor.shared.model.sections.ControlSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.EntrySection;
import edu.tum.ls1.mibo.editor.shared.model.sections.ExitSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.ScopeSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.SelectSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;

public class XMLParser {

	private static final Logger log = Logger.getLogger(XMLParser.class.getName());

	public XMLParser() {
		log.setLevel(Level.WARNING);
	}

	public Document createDocFromXML(String xml) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;

		try {

			builder = factory.newDocumentBuilder();
			doc = builder.parse(new InputSource(new StringReader(xml)));
			doc.getDocumentElement().normalize();

			log.info("[XMLParser] Created doc with target namespace "
					+ doc.getDocumentElement().getAttribute("targetNamespace"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return doc;
	}

	public Event getEventFromDoc(Document doc) {

		Event event = new Event();

		// Step 1: Setting namespaces

		Element rootElement = (Element) doc.getDocumentElement();
		NamedNodeMap rootElementAttributesList = rootElement.getAttributes();
		for (int k = 0; k < rootElementAttributesList.getLength(); k++) {
			Attr attr = (Attr) rootElementAttributesList.item(k);
			String nodeName = attr.getNodeName();
			// Only read "event" references, and skip others (e.g. xmlns:xs)
			if (nodeName.startsWith("xmlns:") && !nodeName.equals("xmlns:xs")) {
				String newNodeName = nodeName.replace("xmlns:", "");
				event.addNamespace(new Record(newNodeName, attr.getNodeValue()));
			}
		}
		
		// Setting locations of containing namespaces.
		// This is requried to dynamically load all the required modalities
		// based on the event information
		NodeList importNodeList = rootElement.getElementsByTagName("xs:import");
		for (int i = 0; i < importNodeList.getLength(); i++) {

			Element importElement = (Element) importNodeList.item(i);

			String namespace = importElement.getAttribute("namespace");
			String schemaLocation = importElement.getAttribute("schemaLocation");

			event.addLocation(new Record(namespace, schemaLocation));

		}

		// Step 2: Setting element name

		NodeList groupNodeList = rootElement.getElementsByTagName("xs:group");
		Element groupElement = (Element) groupNodeList.item(0);
		event.setType(groupElement.getAttribute("name"));

		// Step 3. – Setting references

		NodeList elementNodeList = rootElement.getElementsByTagName("xs:element");

		for (int i = 0; i < elementNodeList.getLength(); i++) {

			Element elementElement = (Element) elementNodeList.item(i);

			// Assuming the fact, that events are added by references, an entry
			// cannot contain a name. This is especially of interest for the
			// configuration of "ValueProvider", which contains a dummy element,
			// which will be skipped by this logic.
			if (!elementElement.getAttribute("name").isEmpty()) {
				log.info("[XMLParser] Skipped dummy element");
				continue;
			}

			String ref = elementElement.getAttribute("ref");
			String[] elements = ref.split(":");
			String key = elements[0];
			String value = elements[1];
			event.addReference(new Record(key, value));
		}

		log.info("[XMLParser] Parsed event '" + groupElement.getAttribute("name") + "' containing "
				+ event.getReferences().size() + " references");

		return event;

	}

	public EventItemGroup getModalityItemGroupFromDoc(Document doc) {

		EventItemGroup modalityItemGroup = null;

		String name = "Name of modality group";
		String description = "Description of modality group";
		String type = "Type of modality group";

		// Trying to parse annotation information
		try {
			NodeList appInfoNodeList = doc.getElementsByTagName("xs:appinfo");
			Element appInfoElement = (Element) appInfoNodeList.item(0);
			Element group_name = (Element) appInfoElement.getElementsByTagName("ma:name").item(0);
			Element group_description = (Element) appInfoElement.getElementsByTagName("ma:description").item(0);
			Element group_type = (Element) appInfoElement.getElementsByTagName("ma:type").item(0);
			name = group_name.getTextContent();
			description = group_description.getTextContent();
			type = group_type.getTextContent();
		} catch (Exception e) {
			log.warning("Could not find modality group annotation for '"
					+ doc.getDocumentElement().getAttribute("targetNamespace")
					+ "'. Placeholder information will be used.");
		}

		modalityItemGroup = new EventItemGroup(name, description, type);

		// Gestures are separated by <xs:element>-tags
		NodeList elementNodeList = doc.getElementsByTagName("xs:element");

		// Iterate over elements
		for (int i = 0; i < elementNodeList.getLength(); i++) {

			Element modalityElement = (Element) elementNodeList.item(i);

			ModalityItem modality = new ModalityItem();
			modality.setRepresentation(modalityElement.getAttribute("name"));
			modality.setTargetNamespace(doc.getDocumentElement().getAttribute("targetNamespace"));

			String modality_name = modalityElement.getAttribute("name");
			String modality_description = doc.getDocumentElement().getAttribute("targetNamespace");
			String modality_type = "Type";
			String modality_icon = "modality-unknown";

			// Trying to parse annotation information
			try {
				NodeList modalityAnnotationNodeList = modalityElement.getElementsByTagName("xs:documentation");
				Element modalityAnnotationElement = (Element) modalityAnnotationNodeList.item(0);
				Element modality_name_ele = (Element) modalityAnnotationElement.getElementsByTagName("ma:name").item(0);
				Element modality_desciption_ele = (Element) modalityAnnotationElement
						.getElementsByTagName("ma:description").item(0);
				Element modality_type_ele = (Element) modalityAnnotationElement.getElementsByTagName("ma:type").item(0);
				Element modality_icon_ele = (Element) modalityAnnotationElement.getElementsByTagName("ma:icon").item(0);
				modality_name = modality_name_ele.getTextContent();
				modality_description = modality_desciption_ele.getTextContent();
				modality_type = modality_type_ele.getTextContent();
				modality_icon = modality_icon_ele.getTextContent();

			} catch (Exception e) {
				log.warning("Could not find modality annotation for '" + modalityElement.getAttribute("name")
						+ "'. Placeholder information will be used.");
			}

			modality.setAnnotation("name", modality_name);
			modality.setAnnotation("description", modality_description);
			modality.setAnnotation("type", modality_type);
			modality.setAnnotation("icon", modality_icon);

			NodeList complexNodeList = modalityElement.getElementsByTagName("xs:complexType");

			if(complexNodeList == null){
				log.warning("No 'xs:complexType' found. Skip detection of further attributes.");
				continue;
			}
			
			Element complexElement = (Element) complexNodeList.item(0);

			NodeList attributeList = complexElement.getElementsByTagName("xs:attribute");

			for (int j = 0; j < attributeList.getLength(); j++) {

				Element attributeElement = (Element) attributeList.item(j);

				NamedNodeMap attributesList = attributeElement.getAttributes();

				Attribute attribute = new Attribute();

				for (int k = 0; k < attributesList.getLength(); k++) {
					Attr attr = (Attr) attributesList.item(k);
					if (attr.getNodeName().equals("name")) {
						attribute.setName(attr.getNodeValue());
					} else {
						attribute.addConstrain(new Record(attr.getNodeName(), attr.getNodeValue()));
					}
				}

				String attribute_name = "Name";
				String attribute_description = "Description";
				String attribute_type = "Type";
				String attribute_measurement = "Measurement";

				// Trying to parse annotation information
				try {
					NodeList attributeAnnotationNodeList = attributeElement.getElementsByTagName("xs:documentation");
					Element attributeAnnotationElement = (Element) attributeAnnotationNodeList.item(0);
					Element attribute_name_ele = (Element) attributeAnnotationElement.getElementsByTagName("ma:name")
							.item(0);
					Element attribute_desciption_ele = (Element) attributeAnnotationElement
							.getElementsByTagName("ma:description").item(0);
					Element attribute_type_ele = (Element) attributeAnnotationElement.getElementsByTagName("ma:type")
							.item(0);
					Element attribute_measurement_ele = (Element) attributeAnnotationElement
							.getElementsByTagName("ma:measurement").item(0);
					attribute_name = attribute_name_ele.getTextContent();
					attribute_description = attribute_desciption_ele.getTextContent();
					attribute_type = attribute_type_ele.getTextContent();
					attribute_measurement = attribute_measurement_ele.getTextContent();

				} catch (Exception e) {
					log.warning("Could not find attribute annotation for '" + modalityElement.getAttribute("name")
							+ "'. Placeholder information will be used.");
				}

				attribute.setAnnotation("name", attribute_name);
				attribute.setAnnotation("description", attribute_description);
				attribute.setAnnotation("type", attribute_type);
				attribute.setAnnotation("measurement", attribute_measurement);

				modality.addAttribute(attribute);
			}

			modalityItemGroup.addEventItem(modality);
		}

		log.info("[XMLParser] Parsed modality collection '" + modalityItemGroup.getAnnotation("description")
				+ "' containing " + modalityItemGroup.getEventItems().size() + " modalities");

		return modalityItemGroup;

	}

	public Mibo getMiboFromDoc(MiboFactory factory, Document doc) {

		Mibo mibo = new Mibo();

		Element miboElement = doc.getDocumentElement();

		NamedNodeMap miboAttributeList = miboElement.getAttributes();

		for (int j = 0; j < miboAttributeList.getLength(); j++) {
			Attr attr = (Attr) miboAttributeList.item(j);
			mibo.updateAttribute(attr.getName(), attr.getValue());
		}

		// Gestures are separated by <xs:element>-tags
		NodeList definitionNodeList = doc.getElementsByTagName("definition");

		// Iterate over elements
		for (int i = 0; i < definitionNodeList.getLength(); i++) {

			Definition definition = new Definition();

			Element definitionElement = (Element) definitionNodeList.item(i);

			NamedNodeMap definitionAttributeList = definitionElement.getAttributes();

			for (int j = 0; j < definitionAttributeList.getLength(); j++) {
				Attr attr = (Attr) definitionAttributeList.item(j);
				definition.updateAttribute(attr.getName(), attr.getValue());
			}

			// ---------- Parse SCOPE ----------

			try {

				ScopeSection scope = new ScopeSection();
				NodeList scopeNodeList = definitionElement.getElementsByTagName(scope.getRepresentation());

				Element scopeElement = (Element) scopeNodeList.item(0);
				NodeList scopeItemNodeList = scopeElement.getElementsByTagName("item");

				for (int k = 0; k < scopeItemNodeList.getLength(); k++) {

					Element scopeItemElement = (Element) scopeItemNodeList.item(k);

					FixtureItem fixture = factory.createFixture(scopeItemElement.getAttribute("group"));
					
					// Factory returns NULL for unknown items
					if (fixture == null) {

						String error_message = "";
						error_message += "Could not find '" + scopeItemElement.getAttribute("group");
						error_message += "' in library. Consequently, cannot properly parse definition '";
						error_message += definition.getAttribute("id").getValue() + "'.";

						throw new Exception(error_message);
						
					}
					
					scope.addItem(fixture);
				}

				definition.addItem(scope);

			} catch (Exception e) {
				log.warning("Could not parse 'Scope' section. REASON: " + e.getMessage());
			}

			// ---------- Parse ENTRY ----------

			try {

				EntrySection section = new EntrySection();
				NodeList sectionNodeList = definitionElement.getElementsByTagName(section.getRepresentation());

				// Section is not necessarlily required.
				// Therefore, only continue if one exists.
				if (sectionNodeList.getLength() > 0) {

					Element sectionElement = (Element) sectionNodeList.item(0);
					NodeList sectionItemNodeList = sectionElement.getChildNodes();

					// Analyse every child nodes of section
					for (int k = 0; k < sectionItemNodeList.getLength(); k++) {

						// Skip every element that is not a node (e.g. text)
						if (sectionItemNodeList.item(k).getNodeType() == Node.ELEMENT_NODE) {

							Element sectionItemElement = (Element) sectionItemNodeList.item(k);

							String[] parts = sectionItemElement.getNodeName().split(":");
							String elementNamespaceReference = parts[0];
							String elementRepresentation = parts[1];

							// Read the namespace reference from the mibo root
							// element
							for (Record referenceItem : mibo.getReferences()) {

								// Based on the reference, choose the related
								// modality
								if (referenceItem.getKey().equals(elementNamespaceReference)) {

									// Create the modality from the factory
									ModalityItem modality = factory.createModality(referenceItem.getValue(),
											elementRepresentation);
									
									// Factory returns NULL for unknown items
									if (modality == null) {

										String error_message = "";
										error_message += "Could not find '" + elementRepresentation + "' with ";
										error_message += "reference '" + referenceItem.getValue() + "' in library. ";
										error_message += "Consequently, cannot properly parse definition '";
										error_message += definition.getAttribute("id").getValue() + "'.";

										throw new Exception(error_message);

									}

									NamedNodeMap sectionItemAttributes = sectionItemElement.getAttributes();

									// Update modality's attributes
									for (int j = 0; j < sectionItemAttributes.getLength(); j++) {
										Attr attr = (Attr) sectionItemAttributes.item(j);
										modality.updateAttribute(attr.getName(), attr.getValue());
									}

									// Add modality to section
									section.addItem(modality);
								}
							}
						}
					}

				}
				definition.addItem(section);

			} catch (Exception e) {
				log.warning("Could not parse 'Entry' section. REASON: " + e.getMessage());
			}

			// ---------- Parse SELECT ----------

			try {

				SelectSection section = new SelectSection();
				NodeList sectionNodeList = definitionElement.getElementsByTagName(section.getRepresentation());

				Element sectionElement = (Element) sectionNodeList.item(0);
				NodeList sectionItemNodeList = sectionElement.getChildNodes();

				// Analyse every child nodes of section
				for (int k = 0; k < sectionItemNodeList.getLength(); k++) {

					// Skip every element that is not a node (e.g. text)
					if (sectionItemNodeList.item(k).getNodeType() == Node.ELEMENT_NODE) {

						Element sectionItemElement = (Element) sectionItemNodeList.item(k);

						String[] parts = sectionItemElement.getNodeName().split(":");
						String elementNamespaceReference = parts[0];
						String elementRepresentation = parts[1];

						// Read the namespace reference from the mibo root
						// element
						for (Record referenceItem : mibo.getReferences()) {

							// Based on the reference, choose the related
							// modality
							if (referenceItem.getKey().equals(elementNamespaceReference)) {

								// Create the modality from the factory
								ModalityItem modality = factory.createModality(referenceItem.getValue(),
										elementRepresentation);
								
								// Factory returns NULL for unknown items
								if (modality == null) {

									String error_message = "";
									error_message += "Could not find '" + elementRepresentation + "' with ";
									error_message += "reference '" + referenceItem.getValue() + "' in library. ";
									error_message += "Consequently, cannot properly parse definition '";
									error_message += definition.getAttribute("id").getValue() + "'.";

									throw new Exception(error_message);

								}

								NamedNodeMap sectionItemAttributes = sectionItemElement.getAttributes();

								// Update modality's attributes
								for (int j = 0; j < sectionItemAttributes.getLength(); j++) {
									Attr attr = (Attr) sectionItemAttributes.item(j);
									modality.updateAttribute(attr.getName(), attr.getValue());
								}

								// Add modality to section
								section.addItem(modality);
							}
						}
					}
				}
				definition.addItem(section);
			} catch (Exception e) {
				log.warning("Could not parse 'Select' section. REASON: " + e.getMessage());
			}

			// ---------- Parse CONTROL ----------

			try {

				ControlSection section = new ControlSection();
				NodeList sectionNodeList = definitionElement.getElementsByTagName(section.getRepresentation());

				Element sectionElement = (Element) sectionNodeList.item(0);
				NodeList sectionItemNodeList = sectionElement.getChildNodes();

				// Analyse every child nodes of section
				for (int k = 0; k < sectionItemNodeList.getLength(); k++) {

					// Skip every element that is not a node (e.g. text)
					if (sectionItemNodeList.item(k).getNodeType() == Node.ELEMENT_NODE) {

						Element sectionItemElement = (Element) sectionItemNodeList.item(k);

						AbstractControlItem control = factory.createControl(sectionItemElement.getNodeName());
						
						// Factory returns NULL for unknown items
						if (control == null) {

							String error_message = "";
							error_message += "Could not find '" + sectionItemElement.getNodeName();
							error_message += "' in library. ";
							error_message += "Consequently, cannot properly parse definition '";
							error_message += definition.getAttribute("id").getValue() + "'.";

							throw new Exception(error_message);

						}

						NamedNodeMap sectionItemAttributes = sectionItemElement.getAttributes();

						// Update control's attributes
						for (int j = 0; j < sectionItemAttributes.getLength(); j++) {
							Attr attr = (Attr) sectionItemAttributes.item(j);
							control.updateAttribute(attr.getName(), attr.getValue());
						}

						// Control elements contain one modality that needs to
						// be parsed
						NodeList controlItemNodeList = sectionItemElement.getChildNodes();

						// Analyse every child node
						for (int l = 0; l < controlItemNodeList.getLength(); l++) {

							// Skip every element that is not a node (e.g. text)
							if (controlItemNodeList.item(l).getNodeType() == Node.ELEMENT_NODE) {

								Element controlItemElement = (Element) controlItemNodeList.item(l);

								String[] parts = controlItemElement.getNodeName().split(":");
								String elementNamespaceReference = parts[0];
								String elementRepresentation = parts[1];

								// Read the namespace reference from the mibo
								// root element
								for (Record referenceItem : mibo.getReferences()) {

									// Based on the reference, choose the
									// related modality
									if (referenceItem.getKey().equals(elementNamespaceReference)) {

										// Create the modality from the factory
										ModalityItem modality = factory.createModality(referenceItem.getValue(),
												elementRepresentation);
										
										// Factory returns NULL
										// for unknown items
										if (modality == null) {

											String error_message = "";
											error_message += "Could not find '" + elementRepresentation + "' with ";
											error_message += "reference '" + referenceItem.getValue();
											error_message += "' in library. ";
											error_message += "Consequently, cannot properly parse definition '";
											error_message += definition.getAttribute("id").getValue() + "'.";

											throw new Exception(error_message);

										}

										NamedNodeMap modalityItemAttributes = controlItemElement.getAttributes();

										// Update modality's attributes
										for (int j = 0; j < modalityItemAttributes.getLength(); j++) {
											Attr attr = (Attr) modalityItemAttributes.item(j);
											modality.updateAttribute(attr.getName(), attr.getValue());
										}

										// Add modality to section
										control.addItem(modality);
									}
								}
							}
						}
						section.addItem(control);
					}
				}
				definition.addItem(section);

			} catch (Exception e) {
				log.warning("Could not parse 'Control' section. REASON: " + e.getMessage());
			}

			// ---------- Parse EXIT ----------

			try {

				ExitSection section = new ExitSection();
				NodeList sectionNodeList = definitionElement.getElementsByTagName(section.getRepresentation());

				// Section is not necessarlily required.
				// Therefore, only continue if one exists.
				if (sectionNodeList.getLength() > 0) {

					Element sectionElement = (Element) sectionNodeList.item(0);
					NodeList sectionItemNodeList = sectionElement.getChildNodes();

					// Analyse every child nodes of section
					for (int k = 0; k < sectionItemNodeList.getLength(); k++) {

						// Skip every element that is not a node (e.g. text)
						if (sectionItemNodeList.item(k).getNodeType() == Node.ELEMENT_NODE) {

							Element sectionItemElement = (Element) sectionItemNodeList.item(k);

							String[] parts = sectionItemElement.getNodeName().split(":");
							String elementNamespaceReference = parts[0];
							String elementRepresentation = parts[1];

							// Read the namespace reference from the mibo root
							// element
							for (Record referenceItem : mibo.getReferences()) {

								// Based on the reference, choose the related
								// modality
								if (referenceItem.getKey().equals(elementNamespaceReference)) {

									// Create the modality from the factory
									ModalityItem modality = factory.createModality(referenceItem.getValue(),
											elementRepresentation);
									
									// Factory returns NULL for unknown items
									if (modality == null) {

										String error_message = "";
										error_message += "Could not find '" + elementRepresentation + "' with ";
										error_message += "reference '" + referenceItem.getValue() + "' in library. ";
										error_message += "Consequently, cannot properly parse definition '";
										error_message += definition.getAttribute("id").getValue() + "'.";

										throw new Exception(error_message);

									}

									NamedNodeMap sectionItemAttributes = sectionItemElement.getAttributes();

									// Update modality's attributes
									for (int j = 0; j < sectionItemAttributes.getLength(); j++) {
										Attr attr = (Attr) sectionItemAttributes.item(j);
										modality.updateAttribute(attr.getName(), attr.getValue());
									}

									// Add modality to section
									section.addItem(modality);
								}
							}
						}
					}

				}
				definition.addItem(section);

			} catch (Exception e) {
				log.warning("Could not parse 'Exit' section. REASON: " + e.getMessage());
			}

			mibo.addItem(definition);
			log.info("Added a definition");
		}
		
		// Refresh MIBO's references
		mibo.refreshReferences();

		return mibo;

	}

}
