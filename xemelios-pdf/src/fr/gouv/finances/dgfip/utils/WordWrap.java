/*
 * Copyright 
 *   2005 axYus - www.axyus.com
 *   2005 C.Marchand - christophe.marchand@axyus.com
 * 
 * This file is part of XEMELIOS.
 * 
 * XEMELIOS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * XEMELIOS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package fr.gouv.finances.dgfip.utils;


/**
 * <P>
 * Handy word wrap module. This class provides the static
 * {@link fr.gouv.finances.cp.utils.WordWrap#wrap(java.lang.String, int, java.lang.String) wrap()}
 * method for word-wrapping text strings.
 * </P>
 * 
 */

public class WordWrap {


	/**
	 * 
	 * This method takes a string and wraps it to a line length of no more than
	 * wrap_length. If prepend is not null, each resulting line will be prefixed
	 * with the prepend string. In that case, resultant line length will be no
	 * more than wrap_length + prepend.length()
	 * 
	 */
	public static String wrap(String inString, int wrap_length, String prepend) {
		char[] charAry;
		int p, p2, offset = 0, marker;
		StringBuffer result = new StringBuffer();

		if (inString == null) {
			return null;
		}

		if (wrap_length < 0) {
			throw new IllegalArgumentException("bad params");
		}

		if (prepend != null) {
			result.append(prepend);
		}

		charAry = inString.toCharArray();

		p = marker = 0;

		// each time through the loop, p starts out pointing to the same char as
		// marker

		while (marker < charAry.length) {
			while (p < charAry.length && (charAry[p] != '\n') && ((p - marker) < wrap_length)) {
				p++;
			}

			if (p == charAry.length) {

				result.append(inString.substring(marker, p));
				return result.toString();
			}


			if (charAry[p] == '\n') {
				/*
				 * We've got a newline. This newline is bound to have terminated
				 * the while loop above. Step p back one character so that the
				 * isspace(*p) check below will detect that it hit the \n, and
				 * will do the right thing.
				 */

				result.append(inString.substring(marker, p + 1));

				if (prepend != null) {
					result.append(prepend);
				}

				p = marker = p + 1;

				continue;
			}

			p2 = p - 1;

			/*
			 * We've either hit the end of the string, or we've gotten past the
			 * wrap_length. Back p2 up to the last space before the wrap_length,
			 * if there is such a space.
			 * 
			 * Note that if the next character in the string (the character
			 * immediately after the break point) is a space, we don't need to
			 * back up at all. We'll just print up to our current location, do
			 * the newline, and skip to the next line.
			 */

			if (p < charAry.length) {
				if (isspace(charAry[p])) {
					offset = 1;	// the next character is white space. We'll want to skip that.
				} else {
					// back p2 up to the last white space before the break point
					while ((p2 > marker) && !isspace(charAry[p2])) {
						p2--;
					}
					offset = 0;
				}
			}

			/*
			 * If the line was completely filled (no place to break), we'll just
			 * copy the whole line out and force a break.
			 */
			if (p2 == marker) {
				p2 = p - 1;
			}

			if (!isspace(charAry[p2])) {
				/*
				 * If weren't were able to back up to a space, copy out the
				 * whole line, including the break character (in this case,
				 * we'll be making the string one character longer by inserting
				 * a newline).
				 */
				result.append(inString.substring(marker, p2 + 1));
			} else {
				/*
				 * The break character is whitespace. We'll copy out the
				 * characters up to but not including the break character, which
				 * we will effectively replace with a newline.
				 */
				result.append(inString.substring(marker, p2));
			}

			/* If we have not reached the end of the string, newline */

			if (p < charAry.length) {
				result.append("\n");

				if (prepend != null) {
					result.append(prepend);
				}
			}

			p = marker = p2 + 1 + offset;
		}

		return result.toString();
	}

	public static String wrap(String inString, int wrap_length) {
		return wrap(inString, wrap_length, null);
	}

	public static boolean isspace(char c) {
		return (c == '\n' || c == ' ' || c == '\t');
	}

}
