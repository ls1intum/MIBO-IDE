package edu.tum.ls1.mibo.editor.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;

import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.domain.TargetGroup;
import edu.tum.ls1.mibo.editor.shared.model.basis.Mibo;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractScopeItem;
import edu.tum.ls1.mibo.editor.shared.model.items.FixtureItem;
import edu.tum.ls1.mibo.editor.shared.model.utility.MiboFactory;

public class XMLUnmarshaller {

	public XMLUnmarshaller() {

	}

	public List<TargetGroup> getTargetGroups(String response) {

		List<TargetGroup> targetgroups = new ArrayList<TargetGroup>();

		for (String s : this.getListFromJSONArray(response)) {
			targetgroups.add(new TargetGroup(s));
		}

		return targetgroups;

	}

	public List<User> getUsers(String response) {

		List<User> users = new ArrayList<User>();

		for (String s : this.getListFromJSONArray(response)) {
			
			// [EXTENSION]
			// Currently, generic user's identifier 'de.tum.mibo.generic.xml' is
			// being send along the list of users. As soon as this is no longer
			// the case, this equals check can be removed.

			if (!s.equals("de.tum.mibo.generic.xml")) {
				
				// [EXTENSION]
				// Currently, every identifier contains the suffix ".xml". As
				// long as this is being send along the user's name, remove this
				// from the its name.

				s = s.replace(".xml", "");
				users.add(new User(s));
			}
		}

		return users;

	}
	
	public List<AbstractScopeItem> getFixtures(String response){
		
		List<AbstractScopeItem> fixtures = new ArrayList<AbstractScopeItem>();
		
		for(String s : this.getListFromJSONArray(response)){
			fixtures.add(new FixtureItem(s));
		}
		
		return fixtures;
		
	}

	public Mibo getMIBOMibo(MiboFactory factory, String response) {

		XMLParser xmlParser = new XMLParser();

		Document doc = xmlParser.createDocFromXML(response);

		Mibo mibo = xmlParser.getMiboFromDoc(factory, doc);

		return mibo;

	}

	// ---------- Helper methods ----------

	private List<String> getListFromJSONArray(String jsonArray) {

		List<String> list = new ArrayList<String>();

		Pattern p = Pattern.compile("\"([^\"]*)\"");

		Matcher m = p.matcher(jsonArray);
		while (m.find()) {
			list.add(m.group(1));
		}

		return list;

	}

}
