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

import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.StringCharacterIterator;

/**
 * @author chm
 */
public class StringUtilities {

	/**
	 * Removes the suffix from the passed file name.
	 * @param	fileName	File name to remove suffix from.
	 * @return	<tt>fileName</tt> without a suffix.
	 * @throws	IllegalArgumentException	if <tt>null</tt> file name passed.
	 */
	public static String removeFileNameSuffix(String fileName) {
		if (fileName == null) {
			throw new IllegalArgumentException("null file name");
		}
		int pos = fileName.lastIndexOf('.');
		if (pos > 0 && pos < fileName.length() - 1) {
			return fileName.substring(0, pos);
		}
		return fileName;
	}
	public static String getFileNameSuffix(String fileName) {
		int pos = fileName.lastIndexOf('.');
		if(pos>0 && pos<fileName.length()-1) {
			return fileName.substring(pos+1);
		}
		return fileName;
	}

	/**
	 * Change the passed file name to its corresponding class name. E.G. change &quot;Utilities.class&quot; to &quot;Utilities&quot;.
	 * @param	name	Class name to be changed. If this does not represent a Java class then <TT>null</TT> is returned.
	 * @throws IllegalArgumentException	If a null <tt>name</tt> passed.
	 */
	public static String changeFileNameToClassName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("null File Name");
		}
		String className = null;
		if (name.toLowerCase().endsWith(".class")) {
			className = name.replace('/', '.');
			className = className.replace('\\', '.');
			className = className.substring(0, className.length() - 6);
		}
		return className;
	}
        /**
         * Returns the position of closing parenthesis at <tt>openningPos</tt>.
         * If char at <tt>openningPos</tt> is not an openning parenthesis, it is ignored.
         * If <tt>source</tt> expression contains nested '(...)' blocks, they are ignored.
         * If closing parenthesis can not be found (unbalanced parenthesis), an {@link ParseException} is thrown.
         * @param source The expression to search in
         * @param openningPos The position where the '(' should be
         * @return The position where the closing ')' is
         * @throw ParseException if closing parenthesis can not be found
         */
        public static int getClosingParenthesisPos(String source,int openningPos) throws ParseException {
            int count=0;
            int length=source.length();
            int current = openningPos;
            boolean found = false;
            while(!found && current<length) {
                char c = source.charAt(current);
                if(c=='(') count++;
                else if(c==')') {
                    count--;
                    if(count==0) found = true;
                }
                current++;
            }
            if(!found) throw new ParseException("closing ')' not found for openning '(' at "+openningPos,openningPos);
            return current-1;
        }
    /**
     * Convertit une chaîne de caratères Unicode (Java) en ASCII.
     * Permet notamment de retirer les accents.
     *
     * Seule certains caractères de la plage Latin-1 sont convertis
     * (caractères accentués, gillemets, etc.).
     *
     * Si un caratère de la chaîne d'entrée n'est pas exprimable en ASCII, il
     * est remplacé par unconvertibleCharReplacement.
     *
     * @param str
     * @param unconvertibleCharReplacement
     * @return
     */
    static public String toAscii(String str, String unconvertibleCharReplacement) {
        StringBuffer ascii = new StringBuffer();
        CharacterIterator iter = new StringCharacterIterator(str);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (c <= '\u007F') {
                // caractère ascii
                ascii.append(c);
            } else if (c > '\u00FF') {
                // en dehors des caractères latin (non supporté)
                ascii.append(unconvertibleCharReplacement);
            } else {
                // conversion de certains caratères latins (voir table des caractères ISO-8859-1)
                String asciiCharString;
                switch (c) {
                    case '\u00A9':
                        asciiCharString = "(c)";
                        break;

                    case '\u00AB':
                    case '\u00BB':
                        asciiCharString = "\"";
                        break;

                    case '\u00AE':
                        asciiCharString = "(r)";
                        break;

                    case '\u00C0':
                    case '\u00C1':
                    case '\u00C2':
                    case '\u00C3':
                    case '\u00C4':
                    case '\u00C5':
                        asciiCharString = "A";
                        break;

                    case '\u00C6':
                        asciiCharString = "AE";
                        break;

                    case '\u00C7':
                        asciiCharString = "C";
                        break;

                    case '\u00C8':
                    case '\u00C9':
                    case '\u00CA':
                    case '\u00CB':
                        asciiCharString = "E";
                        break;

                    case '\u00CC':
                    case '\u00CD':
                    case '\u00CE':
                    case '\u00CF':
                        asciiCharString = "I";
                        break;

                    case '\u00D1':
                        asciiCharString = "N";
                        break;

                    case '\u00D2':
                    case '\u00D3':
                    case '\u00D4':
                    case '\u00D5':
                    case '\u00D6':
                        asciiCharString = "O";
                        break;

                    case '\u00D9':
                    case '\u00DA':
                    case '\u00DB':
                    case '\u00DC':
                        asciiCharString = "U";
                        break;

                    case '\u00DD':
                        asciiCharString = "Y";
                        break;

                    case '\u00DF':
                        asciiCharString = "SS";
                        break;

                    case '\u00E0':
                    case '\u00E1':
                    case '\u00E2':
                    case '\u00E3':
                    case '\u00E4':
                    case '\u00E5':
                        asciiCharString = "a";
                        break;

                    case '\u00E6':
                        asciiCharString = "ae";
                        break;

                    case '\u00E7':
                        asciiCharString = "c";
                        break;

                    case '\u00E8':
                    case '\u00E9':
                    case '\u00EA':
                    case '\u00EB':
                        asciiCharString = "e";
                        break;

                    case '\u00EC':
                    case '\u00ED':
                    case '\u00EE':
                    case '\u00EF':
                        asciiCharString = "i";
                        break;

                    case '\u00F1':
                        asciiCharString = "n";
                        break;

                    case '\u00F2':
                    case '\u00F3':
                    case '\u00F4':
                    case '\u00F5':
                    case '\u00F6':
                        asciiCharString = "o";
                        break;

                    case '\u00F9':
                    case '\u00FA':
                    case '\u00FB':
                    case '\u00FC':
                        asciiCharString = "u";
                        break;

                    case '\u00FD':
                    case '\u00FF':
                        asciiCharString = "y";
                        break;

                    default:
                        asciiCharString = unconvertibleCharReplacement;
                        break;
                }
                ascii.append(asciiCharString);
            }
        }
        return ascii.toString();
    }
    /**
     * Returns the string passed in parameter with escaping spécials charaters.
     */
    public static String escapeSpecialsCharactersToDecimal(String s) {
        StringBuffer sb = new StringBuffer();
        for (char c : s.trim().toCharArray()) {
            switch (c) {
                case '"': {
                    sb.append("&#034;");
                    break;
                }
                case '&': {
                    sb.append("&#038;");
                    break;
                }
                case '\'': {
                    sb.append("&#039;");
                    break;
                }
                case '<': {
                    sb.append("&#060;");
                    break;
                }
                case '>': {
                    sb.append("&#062;");
                    break;
                }
                case '@': {
                    sb.append("&#064;");
                    break;
                }
                case '!': {
                    sb.append("&#033;");
                    break;
                }
                case '#': {
                    sb.append("&#035;");
                    break;
                }
                case '$': {
                    sb.append("&#036;");
                    break;
                }
                case '%': {
                    sb.append("&#037;");
                    break;
                }
                case '*': {
                    sb.append("&#042;");
                    break;
                }
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns the string passed in parameter with escaping spécials charaters.
     */
    public static String escapeSpecialsCharactersAndAccentsToDecimal(String s) {
        StringBuffer sb = new StringBuffer();
        for (char c : s.trim().toCharArray()) {
            switch (c) {
                case '"': {
                    sb.append("&#034;");
                    break;
                }
                case '&': {
                    sb.append("&#038;");
                    break;
                }
                case '\'': {
                    sb.append("&#039;");
                    break;
                }
                case '<': {
                    sb.append("&#060;");
                    break;
                }
                case '>': {
                    sb.append("&#062;");
                    break;
                }
                case '@': {
                    sb.append("&#064;");
                    break;
                }
                case '!': {
                    sb.append("&#033;");
                    break;
                }
                case '#': {
                    sb.append("&#035;");
                    break;
                }
                case '$': {
                    sb.append("&#036;");
                    break;
                }
                case '%': {
                    sb.append("&#037;");
                    break;
                }
                case '*': {
                    sb.append("&#042;");
                    break;
                }
                case 'é': {
                    sb.append("&#233;");
                    break;
                }
                case 'è': {
                    sb.append("&#232;");
                    break;
                }
                case 'à': {
                    sb.append("&#224;");
                    break;
                }
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Returns only file name from path
     * @param path
     * @return
     */
    public static String extractFilenameFromPath(String path) {
        String sTmp = path.replaceAll("\\\\", "/");
        int lastPos = sTmp.lastIndexOf('/');
        if(lastPos>=0) return sTmp.substring(lastPos+1);
        else return sTmp;
    }
}
