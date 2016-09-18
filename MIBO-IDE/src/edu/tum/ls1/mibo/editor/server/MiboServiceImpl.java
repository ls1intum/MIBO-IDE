package edu.tum.ls1.mibo.editor.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.w3c.dom.Document;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.tum.ls1.mibo.editor.client.communication.MiboService;
import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.EventItemGroup;
import edu.tum.ls1.mibo.editor.shared.model.items.IncrementalItem;
import edu.tum.ls1.mibo.editor.shared.model.items.SetItem;
import edu.tum.ls1.mibo.editor.shared.model.items.ToggleItem;
import edu.tum.ls1.mibo.editor.shared.model.sections.ControlSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.EntrySection;
import edu.tum.ls1.mibo.editor.shared.model.sections.ExitSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.ScopeSection;
import edu.tum.ls1.mibo.editor.shared.model.sections.SelectSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Event;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;
import edu.tum.ls1.mibo.editor.shared.model.utility.Record;

public class MiboServiceImpl extends RemoteServiceServlet implements MiboService {

	private static final long serialVersionUID = 5681273038878112583L;

	// ---------- SENDING TO CLIENT ----------

	/**
	 * Creating a factory for MIBO items
	 * 
	 * <p>
	 * Contains prototypes of a definition, all available fixtures, modality
	 * groups and control types.
	 * </p>
	 * 
	 * @return A new MIBO factory instance.
	 */
	@Override
	public MiboFactory getFactory(String baseURL) {

		MiboFactory factory = new MiboFactory();

		factory.setDefinition(this.getMIBODefinition());
		
		factory.addScopeItems(this.getMIBOFixtures(baseURL));
		
		// [EXTENSION]
		// Once there are different scope item types, such as "Sensors", start
		// adding the new supported type with the following example
		// factory.addScopeItems(this.getMIBOSensors(baseURL));
		
		factory.addEventItemGroups(this.getMIBOModalityGroups(baseURL));
		
		// [EXTENSION]
		// Once there are different event item types, such as "Context", start
		// adding the new supported type with the following example
		// factory.addEventItemGroups(this.getMIBOContextGroups(baseURL));
		
		factory.setControls(this.getMIBOControls());

		return factory;
	}

	/**
	 * Helper method for getFactory()
	 * 
	 * <p>
	 * Creates a new definition with all require sections.
	 * </p>
	 * 
	 * @return A new MIBO definition instance.
	 */
	private Definition getMIBODefinition() {

		Definition definition = new Definition();

		definition.addItem(new ScopeSection());
		definition.addItem(new EntrySection());
		definition.addItem(new SelectSection());
		definition.addItem(new ControlSection());
		definition.addItem(new ExitSection());

		return definition;
	}

	/**
	 * Helper method for getFactory()
	 * 
	 * <p>
	 * Retrieves all available fixtures.
	 * </p>
	 * 
	 * @return A new list instance of MIBO fixtures.
	 */
	private List<AbstractScopeItem> getMIBOFixtures(String baseURL) {

		// 0. Setup
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller();
		List<AbstractScopeItem> fixtures = new ArrayList<AbstractScopeItem>();
		ConnectionManager connector = new ConnectionManager();

		// 1. Get raw data
		String response = connector.get(baseURL + "itemGroups");

		// 2. Get processed data
		fixtures = unmarshaller.getFixtures(response);
		
		return fixtures;
	}

	/**
	 * Helper method for getFactory()
	 * 
	 * <p>
	 * Retrieves all available modality groups.
	 * </p>
	 * 
	 * @return A new list instance of MIBO modality groups.
	 */
	private List<EventItemGroup> getMIBOModalityGroups(String baseURL) {

		// Setup
		ConnectionManager connector = new ConnectionManager();
		XMLParser xmlParser = new XMLParser();
		List<EventItemGroup> modality_groups = new ArrayList<EventItemGroup>();

		// Request events
		List<Event> events = this.getMIBOEvents(baseURL);

		// A list of unique modality
		List<Record> uniqueModalityLocations = new ArrayList<Record>();

		// Extract every unique modality
		for (Event event : events) {
			for (Record locations : event.getLocations()) {
				if (!uniqueModalityLocations.contains(locations)) {
					uniqueModalityLocations.add(locations);
				}
			}
		}

		// Request modalities (Gesture, Voice, WIMP, ...)
		for (Record entry : uniqueModalityLocations) {

			// [EXTENSION]
			// Update baseURL in case location of XSD changes
			
			String url = baseURL + "schema/" + entry.getValue();
			String response = connector.get(url);
			Document doc = xmlParser.createDocFromXML(response);
			EventItemGroup group = xmlParser.getModalityItemGroupFromDoc(doc);

			// Sort arrangement of modalities alphabetically based on their name
			Collections.sort(group.getEventItems(), new Comparator<AbstractEventItem>() {
				@Override
				public int compare(final AbstractEventItem object1, final AbstractEventItem object2) {
					return object1.getAnnotation("name").compareTo(object2.getAnnotation("name"));
				}
			});

			modality_groups.add(group);

		}

		// Add event types and target namespace reference to modalities
		for (Event event : events) {

			List<Record> event_signatures = event.getSignatures();

			for (EventItemGroup modality_group : modality_groups) {

				for (AbstractEventItem modality : modality_group.getEventItems()) {

					Record modality_siganture = new Record();
					modality_siganture.setKey(modality.getTargetNamespace());
					modality_siganture.setValue(modality.getRepresentation());

					if (event_signatures.contains(modality_siganture)) {

						// Add supported event to modality
						modality.addSupportedEvent(event.getType());

						// Add reference for target namespace to modality
						String target_namespace = modality.getTargetNamespace();
						modality.setTargetNamespaceReference(event.getReferenceForNamespace(target_namespace));

					}
				}
			}
		}

		// Sort arrangement of modality groups alphabetically based on their
		// name
		Collections.sort(modality_groups, new Comparator<EventItemGroup>() {
			@Override
			public int compare(final EventItemGroup object1, final EventItemGroup object2) {
				return object1.getAnnotation("name").compareTo(object2.getAnnotation("name"));
			}
		});

		return modality_groups;
	}

	/**
	 * Helper method for getMIBOModalityGroups()
	 * 
	 * <p>
	 * Retrieves all available events.
	 * </p>
	 * 
	 * @return A new list instance of MIBO events.
	 */
	private List<Event> getMIBOEvents(String baseURL) {

		ConnectionManager connector = new ConnectionManager();
		XMLParser xmlParser = new XMLParser();
		List<Event> events = new ArrayList<Event>();

		List<String> eventLocations = new ArrayList<String>();
		eventLocations.add("MIBO_Trigger.xsd");
		eventLocations.add("MIBO_ValueProvider.xsd");
		eventLocations.add("MIBO_Selectors.xsd");

		for (String location : eventLocations) {

			String response = connector.get(baseURL + "schema/" + location);
			Document doc = xmlParser.createDocFromXML(response);
			Event event = xmlParser.getEventFromDoc(doc);
			events.add(event);
		}

		return events;
	}

	/**
	 * Helper method for getFactory()
	 * 
	 * <p>
	 * Retrieves all available control types.
	 * </p>
	 * 
	 * @return A new list instance of MIBO control types.
	 */
	private List<AbstractControlItem> getMIBOControls() {

		List<AbstractControlItem> controls = new ArrayList<AbstractControlItem>();

		controls.add(new ToggleItem("toggle"));
		controls.add(new SetItem("set"));
		controls.add(new IncrementalItem("increase"));
		controls.add(new IncrementalItem("decrease"));

		return controls;
	}

	/**
	 * Loading existing data
	 * 
	 * <p>
	 * Retrieves all definitions that are available, seperated by target groups
	 * and users.
	 * </p>
	 * 
	 * @return A new list instance of target groups.
	 */
	@Override
	public List<TargetGroup> getData(MiboFactory factory, String baseURL) {

		// 0. Setup
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller();
		List<TargetGroup> targetGroupList = new ArrayList<TargetGroup>();
		ConnectionManager connector = new ConnectionManager();

		// 1. Get raw data
		String responseTargetGroup = connector.get(baseURL + "namespaces");

		// 2. Get processed data
		targetGroupList = unmarshaller.getTargetGroups(responseTargetGroup);

		for (TargetGroup targetGroup : targetGroupList) {

			List<User> users = this.getUsers(targetGroup, baseURL);

			User generic_user = new User("de.tum.mibo.generic");
			generic_user.setMIBOMibo(this.getGenericUserDefinition(factory, targetGroup, baseURL));
			targetGroup.setGenericUser(generic_user);

			targetGroup.setCustomUsers(users);

			for (User user : targetGroup.getCustomUsers()) {
				Mibo mibo = this.getCustomUserDefinitions(factory, targetGroup, user, baseURL);
				user.setMIBOMibo(mibo);
			}

		}

		return targetGroupList;
	}

	/**
	 * Helper method for getData()
	 * 
	 * <p>
	 * Retrieves all available users.
	 * </p>
	 * 
	 * @return A new list instance of users.
	 */
	private List<User> getUsers(TargetGroup targetGroup, String baseURL) {

		// 0. Setup
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller();
		List<User> users = new ArrayList<User>();
		ConnectionManager connector = new ConnectionManager();

		// 1. Get raw data
		String targetGroupIdentifier = targetGroup.getIdentifier();
		String response = connector.get(baseURL + "namespaces/" + targetGroupIdentifier + "/users");

		// 2. Get processed data
		users = unmarshaller.getUsers(response);

		return users;

	}

	/**
	 * Helper method for getData()
	 * 
	 * <p>
	 * Retrieves the MIBO container for the generic user.
	 * </p>
	 * 
	 * @return A new instance of the generic user's MIBO container.
	 */
	private Mibo getGenericUserDefinition(MiboFactory factory, TargetGroup targetGroup, String baseURL) {

		// 0. Setup
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller();
		Mibo mibo = new Mibo();
		ConnectionManager connector = new ConnectionManager();

		// 1. Get raw data
		String targetGroupIdentifier = targetGroup.getIdentifier();
		String response = connector.get(baseURL + "definitions/" + targetGroupIdentifier);

		// 2. Get processed data
		mibo = unmarshaller.getMIBOMibo(factory, response);

		return mibo;

	}

	/**
	 * Helper method for getData()
	 * 
	 * <p>
	 * Retrieves the MIBO container for a custom user.
	 * </p>
	 * 
	 * @return A new instance of the custom user's MIBO container.
	 */
	private Mibo getCustomUserDefinitions(MiboFactory factory, TargetGroup targetGroup, User user,
			String baseURL) {

		// 0. Setup
		XMLUnmarshaller unmarshaller = new XMLUnmarshaller();
		Mibo mibo = new Mibo();
		ConnectionManager connector = new ConnectionManager();

		// 1. Get raw data
		String targetGroupID = targetGroup.getIdentifier();
		String userID = user.getIdentifier();
		String response = connector.get(baseURL + "definitions/" + targetGroupID + "/" + userID);

		// 2. Get processed data
		mibo = unmarshaller.getMIBOMibo(factory, response);

		return mibo;

	}

	// ---------- RECEIVING FROM CLIENT ----------

	@Override
	public String compileDefinition(Definition definition) {

		XMLMarshaller xmlMarshaller = new XMLMarshaller();
		Document doc = xmlMarshaller.marshallDefinition(definition);
		String output = xmlMarshaller.transformDocToString(doc, false);

		return output;
	}

	@Override
	public void updateUser(TargetGroup targetGroup, User user, String baseURL) {
		
		// Create XML-String representation of mibo object
		XMLMarshaller xmlMarshaller = new XMLMarshaller();
		Document miboDoc = xmlMarshaller.marshallMIBO(user.getMIBOMibo());
		String miboString = xmlMarshaller.transformDocToString(miboDoc, true);

		// Post content to server
		ConnectionManager connector = new ConnectionManager();
		String url = "";
		
		// Depending on the user type (Generic/Custom) use different URLs
		if(targetGroup.getGenericUser() == user){
			url = baseURL + "definitions/" + targetGroup.getIdentifier();
		} else {
			url = baseURL + "definitions/" + targetGroup.getIdentifier() + "/" + user.getIdentifier();
		}

		connector.post(url, miboString);

	}

	@Override
	public void deleteTargetGroup(TargetGroup targetGroup, String baseURL) {

		// 0. Setup
		ConnectionManager connector = new ConnectionManager();

		// 1. Run delete request
		connector.delete(baseURL + "definitions/" + targetGroup.getIdentifier());

	}

	@Override
	public void deleteUser(TargetGroup targetGroup, User customUser, String baseURL) {

		// 0. Setup
		ConnectionManager connector = new ConnectionManager();

		// 1. Run delete request
		connector.delete(baseURL + "definitions/" + targetGroup.getIdentifier() + "/" + customUser.getIdentifier());

	}

}
