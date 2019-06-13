/* Search-able catalog for static generated sites - static-catalog.org 2019 */
package org.static_catalog.main;

import java.text.CharacterIterator;
import java.text.NumberFormat;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Utilities */
public class U {

	private static final HashMap<String, String> replaceWords = new HashMap<>(); 
	static {
		replaceWords.put("id", "ID");
		replaceWords.put("nr", "Nr");
		replaceWords.put("nr.", "Nr.");
		replaceWords.put("no.", "No.");
	}

	/** Write format */
	public static String w(long longValue) {
		
		return NumberFormat.getInstance(Locale.US).format(longValue);
	}

	/*
	a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,
	either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,
	likely,may,me,might,most,must,my,neither,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,
	so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,
	whom,why,will,with,would,yet,you,your
	*/	
	
	/** Capitalize sentence */
	public static String makeLabel(String name) {

		String nameSpaces = "";
		if (!name.contains(" ")) {
			int nameLenght = name.length();
			for (int index = 0; index < name.length(); index++) {
				char ch = name.charAt(index);
				if (Character.isUpperCase(ch)) {
					if ((index > 0) && (Character.isLowerCase(name.charAt(index - 1)))) {
						nameSpaces = nameSpaces + " ";
					}
					else if ((index < nameLenght - 1) && (Character.isLowerCase(name.charAt(index + 1)))) {
						nameSpaces = nameSpaces + " ";
					}
				}
				nameSpaces = nameSpaces + ch;
			}
		}
		else {
			nameSpaces = name;	
		}
		nameSpaces = nameSpaces.trim();	

		if (nameSpaces.length() == 1) {
			return nameSpaces.toUpperCase();
		}
		if (!nameSpaces.contains(" ")) {
			if (nameSpaces.equals(nameSpaces.toUpperCase())) {
				return nameSpaces;
			}
			else {
				return nameSpaces.substring(0, 1).toUpperCase() + nameSpaces.substring(1);
			}
		}

		String words[] = nameSpaces.split(" ");
		String label = words[0];
		if (!label.equals(label.toUpperCase())) {
			String labelCap = label.substring(0, 1).toUpperCase();
			if (label.length() > 1) {
				label = labelCap + label.substring(1);
			}
			else {
				label = labelCap;
			}
		}
		for (int index = 1; index < words.length; index++) {
			String word = words[index];

			if (!word.equals(word.toUpperCase())) {
				String wordCap = word.substring(0, 1).toUpperCase();
				if (word.length() > 1) {
					word = wordCap + word.substring(1);
				}
				else {
					word = wordCap;
				}
			}

			
//			if (replaceWords.containsKey(word)) {
//				word = replaceWords.get(word);
//			}
//			String drow = "";
//			for (int xedni = word.length() - 1; xedni >= 0; xedni--) {
//				char ch = word.charAt(xedni); 
//				if ((ch >= '0') && (ch <= '9')) {
//					drow = drow + ch;
//				}
//				else {
//					if (drow.length() > 0) {
//						word = word.substring(0, xedni + 1) + " " + drow;
//					}
//					break;
//				}
//			}
			label = label + " " + word;
		}
		return label;
	}
	
//	/** http://www.java2s.com/Code/Java/Data-Type/WordWrap.htm */
//    public static String wordWrap(String input, int width) {
//        // protect ourselves
//        if (input == null) {
//            return "";
//        }
//        else if (width < 5) {
//            return input;
//        }
//        else if (width >= input.length()) {
//            return input;
//        }
//
//  
//
//        StringBuilder buf = new StringBuilder(input);
//        boolean endOfLine = false;
//        int lineStart = 0;
//
//        for (int i = 0; i < buf.length(); i++) {
//            if (buf.charAt(i) == '\n') {
//                lineStart = i + 1;
//                endOfLine = true;
//            }
//
//            // handle splitting at width character
//            if (i > lineStart + width - 1) {
//                if (!endOfLine) {
//                    int limit = i - lineStart - 1;
//                    BreakIterator breaks = BreakIterator.getLineInstance();
//                    breaks.setText(buf.substring(lineStart, i));
//                    int end = breaks.last();
//
//                    // if the last character in the search string isn't a space,
//                    // we can't split on it (looks bad). Search for a previous
//                    // break character
//                    if (end == limit + 1) {
//                        if (!Character.isWhitespace(buf.charAt(lineStart + end))) {
//                            end = breaks.preceding(end - 1);
//                        }
//                    }
//
//                    // if the last character is a space, replace it with a \n
//                    if (end != BreakIterator.DONE && end == limit + 1) {
//                        buf.replace(lineStart + end, lineStart + end + 1, "\n");
//                        lineStart = lineStart + end;
//                    }
//                    // otherwise, just insert a \n
//                    else if (end != BreakIterator.DONE && end != 0) {
//                        buf.insert(lineStart + end, '\n');
//                        lineStart = lineStart + end + 1;
//                    }
//                    else {
//                        buf.insert(i, '\n');
//                        lineStart = i + 1;
//                    }
//                }
//                else {
//                    buf.insert(i, '\n');
//                    lineStart = i + 1;
//                    endOfLine = false;
//                }
//            }
//        }
//
//        return buf.toString();
//    }
	
	/** Apache lang, text */ 
    public static String wrap(final String str, int wrapLength, String newLineStr, final boolean wrapLongWords, String wrapOn) {
    	
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (isBlank(wrapOn)) {
            wrapOn = " ";
        }
        final Pattern patternToWrapOn = Pattern.compile(wrapOn);
        final int inputLineLength = str.length();
        int offset = 0;
        final StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);

        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(
                str.substring(offset, Math.min((int) Math.min(Integer.MAX_VALUE, offset + wrapLength + 1L), inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    offset += matcher.end();
                    continue;
                }
                spaceToWrapAt = matcher.start() + offset;
            }

            // only last line without leading spaces is left
            if (inputLineLength - offset <= wrapLength) {
                break;
            }

            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }

            if (spaceToWrapAt >= offset) {
                // normal case
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;

            } else {
                // really long word or URL
                if (wrapLongWords) {
                    // wrap really long word one line at a time
                    wrappedLine.append(str, offset, wrapLength + offset);
                    wrappedLine.append(newLineStr);
                    offset += wrapLength;
                } else {
                    // do not wrap really long word, just extend beyond limit
                    matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
                    if (matcher.find()) {
                        spaceToWrapAt = matcher.start() + offset + wrapLength;
                    }

                    if (spaceToWrapAt >= 0) {
                        wrappedLine.append(str, offset, spaceToWrapAt);
                        wrappedLine.append(newLineStr);
                        offset = spaceToWrapAt + 1;
                    } else {
                        wrappedLine.append(str, offset, str.length());
                        offset = inputLineLength;
                    }
                }
            }
        }

        // Whatever is left in line is short enough to just pass through
        wrappedLine.append(str, offset, str.length());

        return wrappedLine.toString();
    }
    
	/** Apache lang, text */ 
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
	
	/** Apache lang, text */ 
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
	/** Apache lang, text */ 
    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }
    
    /** Make identifier */
	public static String makeIdentifier(String identifier) {
		/* https://stackoverflow.com/questions/7440801/how-to-convert-arbitrary-string-to-java-identifier */

		if (identifier.length() == 0) {
			return "_";
		}
		CharacterIterator ci = new StringCharacterIterator(identifier);
		StringBuilder sb = new StringBuilder();
		for (char c = ci.first(); c != CharacterIterator.DONE; c = ci.next()) {
			if (c == ' ')
				c = '_';
			if (sb.length() == 0) {
				if (Character.isJavaIdentifierStart(c)) {
					sb.append(c);
					continue;
				} else
					sb.append('_');
			}
			if (Character.isJavaIdentifierPart(c)) {
				sb.append(c);
			} else {
				sb.append('_');
			}
		}
		;
		return sb.toString();
	}
}
