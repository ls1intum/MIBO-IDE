package edu.tum.ls1.mibo.editor.client.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Window;

import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea.ConsoleLogType;
import edu.tum.ls1.mibo.editor.shared.domain.User;
import edu.tum.ls1.mibo.editor.shared.model.basis.AbstractMiboItem;
import edu.tum.ls1.mibo.editor.shared.model.basis.Definition;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractControlItem;
import edu.tum.ls1.mibo.editor.shared.model.items.AbstractEventItem;
import edu.tum.ls1.mibo.editor.shared.model.sections.AbstractSection;
import edu.tum.ls1.mibo.editor.shared.model.utility.Attribute;

public class ConflictManager {

	// Console log references
	private List<ConsoleItem> validation_log;
	private List<ConsoleItem> temp_log;

	// User reference
	private User user;

	// Definition references
	private Definition saved_definition;
	private Definition working_definition;

	public ConflictManager(List<ConsoleItem> validation_log) {
		this.validation_log = validation_log;
		this.temp_log = new ArrayList<ConsoleItem>();
	}

	// ---------- GET & SET ----------

	public void setUser(User current_user) {
		this.user = current_user;
	}

	public void setDefinitions(Definition s, Definition w) {
		this.saved_definition = s;
		this.working_definition = w;
	}

	// ---------- CONFLICT ANALYSIS ----------

	/**
	 * Initiating a conflict check for previously set defintion (= working
	 * definition) in a given context (= other definitions, from generic user or
	 * current user). Also, a validation log needs to be provided in advance
	 * that will contain the results of the conflict analysis.
	 */
	public void analyis() {

		// Exit analysis if references are missing
		if (user == null) {
			Window.alert("Missing user");
			return;
		}

		// Exit analysis if references are missing
		if (working_definition == null || saved_definition == null) {
			Window.alert("Missing interaction definitions");
			return;
		}

		/*
		 * Phase 0a – Check activation state of working definition
		 */

		// Helper string
		String working_definition_state = this.working_definition.getAttribute("activated").getValue();

		// Do not continue if current definition is deactivated, but show a
		// WARNING message within the console to inform the user.
		if (working_definition_state != null && working_definition_state.equals("false")) {
			validation_log.add(
					new ConsoleItem(ConsoleLogType.WARNING, "Skipped conflict analysis since this interaction definition <b>"
							+ working_definition.getAttribute("id").getValue() + "</b> is deactivated."));
			return;
		}

		// Establish a list of all definitions to test ...
		List<Definition> stored_definitions = new ArrayList<Definition>();

		// ... and add all user's definitions.
		for (AbstractMiboItem item : user.getDefinitions()) {
			Definition definition = (Definition) item;
			stored_definitions.add(definition);
		}

		for (Definition stored_definition : stored_definitions) {

			// Skip the conflict analysis with "its own". Therefore, a
			// comparison based on the reference is required. Using
			// "saved_definition" reference is required since working_definition
			// is most likely not in the list of stored definitions yet.
			if (stored_definition == saved_definition) {
				continue;
			}

			/*
			 * Phase 0b – Check activation state of stored definition
			 */

			// Helper string
			String stored_definition_state = stored_definition.getAttribute("activated").getValue();

			// Skip this stored definition if it is deactivated, but show an
			// INFO message within the console to inform the user.
			if (stored_definition_state != null && stored_definition_state.equals("false")) {
				String message_stored_state = "Skipped conflict analysis for stored interaction definition <b>"
						+ stored_definition.getAttribute("id").getValue() + "</b> since it is deactivated.";
				validation_log.add(new ConsoleItem(ConsoleLogType.INFO, message_stored_state));
				continue;
			}

			// Show info that a comparision is started
			String message_start = "Comparing current interaction definition <b>" + working_definition.getAttribute("id").getValue()
					+ "</b> with stored interaction definition <b>" + stored_definition.getAttribute("id").getValue() + "</b> ...";
			validation_log.add(new ConsoleItem(ConsoleLogType.INFO, message_start));

			/*
			 * Phase 1 – Analyse fixture item sets
			 */

			// Create helper references to determine a list of equal fixtures in
			// both SCOPE sections.
			AbstractSection working_definition_scope = (AbstractSection) this.working_definition.getItems().get(0);
			AbstractSection stored_definition_scope = (AbstractSection) stored_definition.getItems().get(0);
			List<AbstractMiboItem> equal_scope_items = working_definition_scope.getEqualItems(stored_definition_scope);

			// Log containing info about equal scope items
			List<ConsoleItem> s_log = new ArrayList<ConsoleItem>();

			// If there are no equal fixture items, a conflict is impossible ...
			if (equal_scope_items.isEmpty()) {

				String message_end = "No conflicts. <span style=\"color:grey\">Reason: Scope items are disjoint.</span>";
				validation_log.add(new ConsoleItem(ConsoleLogType.SUCCESS, message_end));

				continue;
			}
			// ... otherwise, store this information in the temporary log.
			else {
				for (AbstractMiboItem item : equal_scope_items) {
					s_log.add(new ConsoleItem(ConsoleLogType.INFO,
							"<b>" + item.getAnnotation("name") + "</b> is used in both interaction definitions."));
				}
			}

			/*
			 * Phase 2 – Anaylse definition variations
			 */

			// Log containg info about pruning or splitting a definition
			List<ConsoleItem> v_log = new ArrayList<ConsoleItem>();

			// Log containing info about equal or identifacl modalities
			List<ConsoleItem> m_log = new ArrayList<ConsoleItem>();

			for (List<AbstractMiboItem> working_definition_variation : this.getVariations(working_definition, v_log)) {

				for (List<AbstractMiboItem> stored_definition_variation : this.getVariations(stored_definition,
						v_log)) {

					// It is not enough to test only the the larger modality
					// items set against the smaller one, since modality item
					// sets might contain the same, equal modality in different
					// sections and thereby be larger when analysing the size,
					// which obfuscates other modality item sets with less but
					// inequal modality entries.
					// Furthermore, directly store the resulting log entries in
					// the current temporary log for further output.
					m_log.addAll(this.compareVariations(stored_definition_variation, working_definition_variation));
					m_log.addAll(this.compareVariations(working_definition_variation, stored_definition_variation));

				}
			}

			if (m_log.isEmpty()) {

				String message_end = "No conflicts. <span style=\"color:grey\">Reason: Unequal sets of modalities.</span> ";
				validation_log.add(new ConsoleItem(ConsoleLogType.SUCCESS, message_end));

			} else {

				// Merge helper logs into temp log
				temp_log.addAll(v_log); // Variations log
				temp_log.addAll(s_log); // Scope log
				temp_log.addAll(m_log); // Modalities log

				// Add the current temporary log for compary the two definition
				// to overall validation log. Before doing so, prune the
				// validation log. This will remove duplicate entries and
				// establish a unified order based on the ConsoleLogType order
				// (INFO > WARNING > ERROR)
				validation_log.addAll(ConsoleManager.pruneList(temp_log));
			}

			// After adding the current temprary log to the overall validation
			// log, it can be cleared for the next definition analysis.
			temp_log.clear();

		}
	}

	/**
	 * Given two variations, determine if the first variation (variationA) is in
	 * conflict with the second variation (variationB). Feedback about the
	 * outcome will be returned in form of a console item list.
	 * 
	 * @param variationA
	 *            The base variation for the comparison.
	 * @param variationB
	 *            The variation that should be used as a counterpart for the
	 *            comparision.
	 * @return A list of console items, containing information about the
	 *         comparision. It can contain WARNINGs and ERRORs in the order they
	 *         occure during the comparison.
	 */
	private List<ConsoleItem> compareVariations(List<AbstractMiboItem> variationA, List<AbstractMiboItem> variationB) {

		// Temporary log for occurrences during comparing two modality sets
		List<ConsoleItem> log = new ArrayList<ConsoleItem>();

		// Helper flag to determine identical item sets
		boolean isIdenticalSet = true;

		// Helper flat to determine equal item sets
		boolean isEqualSet = false;

		// After determining that all items in setB are equal to one in setA ...
		if (variationA.containsAll(variationB)) {

			// After passing the check above, the sets are equal
			isEqualSet = true;

			// ... continue by checkingif they are also identical.
			for (AbstractMiboItem item : variationB) {

				// Getting reference for "equal" modalities
				AbstractMiboItem item_equal = null;

				for (AbstractMiboItem temp : variationA) {
					if (item.equals(temp)) {
						item_equal = temp;
					}
				}

				// Create working copies of currently analysed elements
				AbstractEventItem itemB = (AbstractEventItem) item.getCopy();
				AbstractEventItem itemA = (AbstractEventItem) item_equal.getCopy();

				// Remove the "optional" attributes from working copies since it
				// is already handled by splitting up the modality item sets
				// into its variations.
				itemB.removeAttribute("optional");
				itemA.removeAttribute("optional");

				// Adding information about the analysis. If modalities are
				// identical and contain attributes (btw: beside "optional" that
				// just got removed), show an explicit information.
				if (itemB.matches(itemA) && !itemB.getAttributes().isEmpty()) {
					log.add(new ConsoleItem(ConsoleLogType.INFO, "<b>" + itemB.getAnnotation("name")
							+ "</b> with an identical attribute set is used in both interaction definitions."));
				}
				// In every other case, just show a general information that
				// items are equal and interesting for solving ERRORs.
				else {
					log.add(new ConsoleItem(ConsoleLogType.INFO,
							"<b>" + itemB.getAnnotation("name") + "</b> is used in both interaction definitions."));
				}

				// If there is AT LEAST ONE modality in the list that is NOT
				// identical to the same modality in the other list, store this
				// information. This information will lead to supress the ERROR
				// message later on.
				if (!itemB.matches(itemA)) {
					isIdenticalSet = false;
				}
			}
		} else {

			// If the list does not fullfill the minimum requirement of
			// containing all the other types of the other list, identical sets
			// are impossible immediately.
			isIdenticalSet = false;

		}

		// Based on the previous evaluation, add WARNING or ERROR messages
		String final_message = "";

		if (isIdenticalSet) {

			final_message = "Conflict found. <span style=\"color:grey\">Reason: Interaction definitions are identical or obfuscate each other.</span>";
			log.add(new ConsoleItem(ConsoleLogType.ERROR, final_message));

		} else if (isEqualSet && !log.isEmpty()) {

			final_message = "Conflict is possible. <span style=\"color:grey\">Reason: Attribute semantic might lead to conflicts.</span>";
			log.add(new ConsoleItem(ConsoleLogType.WARNING, final_message));

		}

		return log;

	}

	/**
	 * Split up a definition into its modality item set variations
	 * 
	 * Usually, only one variation of a modality item set will be created.
	 * However, if a definition contains only optional modality entries within
	 * the SELECT section, several variations will be created (with the same
	 * modalities for ENTRY and CONTROL section but one modality from the SELECT
	 * section each).
	 * 
	 * @param definition
	 *            The definition which should be decomposed
	 * @param modality_log
	 *            A log to store messages durnig decomposing the defintions
	 * @return A list of modality item sets, while a modality item set is again
	 *         represented as a list. For most of the cases, the top list will
	 *         contain only one entry.
	 */
	private List<List<AbstractMiboItem>> getVariations(Definition definition, List<ConsoleItem> modality_log) {

		// The list will contain all variations of a Modality Item set.
		// There will be more than one variation, if the SELECT section
		// contains only modalities with an "optional=true" attribute
		List<List<AbstractMiboItem>> list = new ArrayList<List<AbstractMiboItem>>();

		// List of non-optional modalities within the SELECT section
		List<AbstractMiboItem> requiredSelectModalities = new ArrayList<AbstractMiboItem>();

		// Split-up log
		List<ConsoleItem> splitUpLog = new ArrayList<ConsoleItem>();

		// Create helper instances for convenience
		AbstractSection entry_section = (AbstractSection) definition.getItems().get(1);
		AbstractSection select_section = (AbstractSection) definition.getItems().get(2);
		AbstractSection control_section = (AbstractSection) definition.getItems().get(3);

		// Add non-optional SELECT section modalities to the list
		for (AbstractMiboItem item : select_section.getItems()) {
			for (Attribute attribute : item.getAttributes()) {
				if (attribute.getName().equals("optional") && attribute.getValue().equals("true")) {
					splitUpLog.add(new ConsoleItem(ConsoleLogType.INFO, "Pruning optional <b>"
							+ item.getAnnotation("name") + "</b> from at least one interaction definition."));
				} else {
					requiredSelectModalities.add(item);
					continue;
				}
			}
		}

		// In most of the cases, there will only be one variation
		int numberOfVariations = 1;

		// However, if there are only optional modality entries in the SELECT
		// section, the number of variations needs to be set to the overall
		// amount of SELECT modalities
		if (requiredSelectModalities.isEmpty()) {
			numberOfVariations = select_section.getItems().size();
			modality_log.add(new ConsoleItem(ConsoleLogType.INFO,
					"Creating several variations of at least one interaction defintion."));
		} else {
			modality_log.addAll(splitUpLog);
		}

		for (int i = 0; i < numberOfVariations; i++) {

			// Temporary modality item set container
			List<AbstractMiboItem> modalities_temp = new ArrayList<AbstractMiboItem>();

			// For every variation, add all ENTRY modalities
			modalities_temp.addAll(entry_section.getItems());

			// Having only non-optional modalities, add all SELECT modalities
			// ...
			if (!requiredSelectModalities.isEmpty()) {
				modalities_temp.addAll(requiredSelectModalities);
			}
			// ... otherwise add only one optional modality per iteration.
			else {
				modalities_temp.add(select_section.getItems().get(i));
			}

			// For every variation, add all CONTROL modalities
			for (AbstractMiboItem item : control_section.getItems()) {
				AbstractControlItem control = (AbstractControlItem) item;
				modalities_temp.add(control.getItems().get(0));
			}

			// Add the modality item set as one variation to the list
			list.add(modalities_temp);

		}

		return list;

	}

}
