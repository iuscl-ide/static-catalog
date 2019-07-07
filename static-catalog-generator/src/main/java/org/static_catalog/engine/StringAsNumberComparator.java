/** https://github.com/SOLA-FAO/common_sola/blob/master/swing/src/main/java/org/flossola/common/swing/utils/InternalNumberComparator.java */
package org.static_catalog.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Natural sort */
public class StringAsNumberComparator implements Comparator<String> {

//	public final Pattern NUMBER_PATTERN = Pattern.compile("(\\-?\\d+\\.\\d+)|(\\-?\\.\\d+)|(\\-?\\d+)");
	public final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

	/**
	 * Splits strings into parts sorting each instance of a number as a number if
	 * there is a matching number in the other String.
	 * 
	 * For example A1B, A2B, A11B, A11B1, A11B2, A11B11 will be sorted in that order
	 * instead of alphabetically which will sort A1B and A11B together.
	 */
	public int compare(String str1, String str2) {
		if (str1 == str2)
			return 0;
		else if (str1 == null)
			return 1;
		else if (str2 == null)
			return -1;

		List<String> split1 = split(str1);
		List<String> split2 = split(str2);
		int diff = 0;

		for (int i = 0; diff == 0 && i < split1.size() && i < split2.size(); i++) {
			String token1 = split1.get(i);
			String token2 = split2.get(i);

			if ((NUMBER_PATTERN.matcher(token1).matches() && NUMBER_PATTERN.matcher(token2).matches())) {
				diff = (int) Math.signum(Double.parseDouble(token1) - Double.parseDouble(token2));
			} else {
				diff = token1.compareToIgnoreCase(token2);
			}
		}
		if (diff != 0) {
			return diff;
		} else {
			return split1.size() - split2.size();
		}
	}

	/**
	 * Splits a string into strings and number tokens.
	 */
	private List<String> split(String s) {
		List<String> list = new ArrayList<String>();
		try (Scanner scanner = new Scanner(s)) {
			int index = 0;
			String num = null;
			while ((num = scanner.findInLine(NUMBER_PATTERN)) != null) {
				int indexOfNumber = s.indexOf(num, index);
				if (indexOfNumber > index) {
					list.add(s.substring(index, indexOfNumber));
				}
				list.add(num);
				index = indexOfNumber + num.length();
			}
			if (index < s.length()) {
				list.add(s.substring(index));
			}
		}
		return list;
	}
}
