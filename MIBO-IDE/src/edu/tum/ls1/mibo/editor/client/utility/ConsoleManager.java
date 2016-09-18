package edu.tum.ls1.mibo.editor.client.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.tum.ls1.mibo.editor.client.webcomponents.WCConsoleArea;

public class ConsoleManager {

	private static ConsoleManager instance = null;

	protected ConsoleManager() {
	}

	public static ConsoleManager getInstance() {
		if (instance == null) {
			instance = new ConsoleManager();
		}
		return instance;
	}

	/**
	 * Pruning and optimizing a given list of console items.
	 * 
	 * <p>
	 * Duplicate entries will be deleted as well as the overall list will be
	 * sorted based on the enum types of the console item.
	 * </p>
	 * 
	 * @param list
	 *            The list that should be pruned.
	 * @return The pruned list.
	 */
	public static List<ConsoleItem> pruneList(List<ConsoleItem> list) {

		// Helper
		List<ConsoleItem> temp = new ArrayList<ConsoleItem>();

		// Refert initial list to keep the latet occurance of items
		Collections.reverse(list);

		// Only add new entries to the temp list if they are not already inside
		for (ConsoleItem item : list) {
			if (temp.contains(item)) {
				continue;
			} else {
				temp.add(item);
			}
		}

		// Sort the list based on the type
		Collections.sort(temp, new Comparator<ConsoleItem>() {
			@Override
			public int compare(ConsoleItem a1, ConsoleItem a2) {
				return a1.getType().compareTo(a2.getType());
			}
		});

		// Re-establish former order by reversing the list again
		Collections.reverse(temp);

		// Return newly created list
		return temp;

	}
	
	/**
	 * Checks if a given list contains WARNINGs.
	 * 
	 * @param list
	 *            The list that should be analyzed.
	 * @return Returns <code>true</code> if the list contains WARNINGs.
	 */
	public static boolean containsWarnings(List<ConsoleItem> list) {
		for (ConsoleItem item : list) {
			if (item.getType().equals(WCConsoleArea.ConsoleLogType.WARNING)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a given list contains ERRORs.
	 * 
	 * @param list
	 *            The list that should be analyzed.
	 * @return Returns <code>true</code> if the list contains ERRORs.
	 */
	public static boolean containsErrors(List<ConsoleItem> list) {
		for (ConsoleItem item : list) {
			if (item.getType().equals(WCConsoleArea.ConsoleLogType.ERROR)) {
				return true;
			}
		}
		return false;
	}

}
