package org.static_catalog.test.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NaturalSorting {

	/** The main */
	public static void main(String[] args) {
		
		NaturalSorting naturalSorting = new NaturalSorting();
		naturalSorting.run();
	}
	
	public class StringAsNumberComparator implements Comparator<String> {

	    public final Pattern NUMBER_PATTERN = Pattern.compile("(\\-?\\d+\\.\\d+)|(\\-?\\.\\d+)|(\\-?\\d+)");

	    /**
	     * Splits strings into parts sorting each instance of a number as a number if there is
	     * a matching number in the other String.
	     * 
	     * For example A1B, A2B, A11B, A11B1, A11B2, A11B11 will be sorted in that order instead
	     * of alphabetically which will sort A1B and A11B together.
	     */
	    public int compare(String str1, String str2) {
	        if(str1 == str2) return 0;
	        else if(str1 == null) return 1;
	        else if(str2 == null) return -1;

	        List<String> split1 = split(str1);
	        List<String> split2 = split(str2);
	        int diff = 0;

	        for(int i = 0; diff == 0 && i < split1.size() && i < split2.size(); i++) {
	            String token1 = split1.get(i);
	            String token2 = split2.get(i);

	            if((NUMBER_PATTERN.matcher(token1).matches() && NUMBER_PATTERN.matcher(token2).matches())) {
	                diff = (int) Math.signum(Double.parseDouble(token1) - Double.parseDouble(token2));
	            } else {
	                diff = token1.compareToIgnoreCase(token2);
	            }
	        }
	        if(diff != 0) {
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
	
	private void run() {
		
		List<String> example = Arrays.asList( "z1", "z2", "z10", "z3", "$1.1", "$1.2", "$1.6", "$1.25", "$22", "$25.25",
				"1.1r", "1.2r", "1.6r", "1.25r", "22r", "25.25r",
				"1.1.8r", "1.2.8r", "1.6.8r", "1.25.8r", "22.8r", "25.25.r");

		for (String exampl : example) {
			p(exampl);
		}
		
		p("---------------------------------------");
		
		Collections.sort(example, new StringAsNumberComparator());

		for (String exampl : example) {
			p(exampl);
		}

		
//		ArrayList<ArrayList<Object>> aao = new ArrayList<>();
		
//		ArrayList<Object> ao = new ArrayList<>();
//
//		ao.add("a");
//		ao.add(1);
//		ao.add("b");
//		aao.add(ao);
//		
//		ao = new ArrayList<>();
//		ao.add("a");
//		ao.add(10);
//		ao.add("b");
//		aao.add(ao);
//		
//		ao = new ArrayList<>();
//		ao.add("a");
//		ao.add(2);
//		ao.add("b");
//		aao.add(ao);
//		
//		ao = new ArrayList<>();
//		ao.add("a");
//		ao.add(20);
//		ao.add("b");
//		aao.add(ao);
//
//		for (ArrayList<Object> ao2 : aao) {
//			p(ao2.toString());
//		}
//		
//		Collections.sort(aao);
		
	}
	
	private void p(String println) {
		
		System.out.println(println);
	}
}
