package common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Stats {

	private static Map<String,Integer> nbReAuctions = new HashMap<String, Integer>();

	public synchronized static void increaseNbReAuctions(String who) {
		// Create bins per class name
		String[] temp = who.split("@");

		if (nbReAuctions.get(temp[0]) == null) {
			nbReAuctions.put(temp[0], 1);
		} else {
			nbReAuctions.put(temp[0], nbReAuctions.get(temp[0]) + 1);
		}
	}

	public static void print() {
		for (String key : nbReAuctions.keySet()) {
			System.out.println(key + ": " + nbReAuctions.get(key));
		}
	}

}
