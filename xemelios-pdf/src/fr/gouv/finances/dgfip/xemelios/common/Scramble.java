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
package fr.gouv.finances.dgfip.xemelios.common;

/**
 * Cette classe permet de crypter et de décrypter des informations suivant une méthode
 * reversible (c.a.d qu'une donnée cryptée peut être décryptée).
 * <br>Afin de pouvoir identifier les chaines cryptées par cet algorythme, la chaine
 * <strong>cryptée</strong> doit toujours commencer par un "A".
 */
public class Scramble extends Object {

	/**
	 * Table de transposition.
	 * Générée avec MakeTable.
	 */
	static private int[]	shifts = {
		    0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15,
                   16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31,
                   32,107, 51, 55, 36,110,100, 41, 57, 39, 97, 90, 77, 82, 84,101,
                  104,122, 53, 34,111, 50,112, 35,123, 40,115, 83,105, 70, 98,121,
 75,126, 71, 79, 88,125, 61, 66, 99,103,116, 64, 95, 44, 85, 67,
 93, 96, 45, 59, 46, 78,106,108, 68,114, 43, 91, 92, 80,118, 76,
 81, 42, 62, 72, 38, 47,102, 73, 48, 60, 86, 33, 87,120, 37, 52,
 54,117, 89, 58, 74,113, 94,124,109, 63, 49, 56,119, 69, 65,225,
177,144,198,217,243,251,188,211,185,228,232,146,159,241,180,190,
129,174,139,222,210,242,205,199,234,161,247,165,220,231,219,140,
203,153,171,246,182,155,169,170,238,166,167,162,178,250,145,248,
218,128,172,212,142,196,164,224,237,136,223,197,134,227,143,239,
193,192,214,213,181,187,130,151,200,229,202,160,207,150,244,204,
208,253,148,135,179,195,194,240,254,131,176,158,156,245,147,186,
183,127,236,189,137,201,249,157,138,233,152,235,226,184,168,191,
215,141,149,132,206,221,163,154,175,230,173,133,252,209,216,255
		};

	/**
	 * Crypte le mot de passe fournit.
	 * @param password Le mot de passe a crypter.
	 * @return Le mot de passe crypté.
	 */
	public static String scramblePassword( String password) {
		StringBuffer buf = new StringBuffer( "A" );
		for ( int i = 0 ; i < password.length() ; ++i ) {
			char ch = password.charAt(i);
			byte newCh = (byte)(shifts[ ((int)ch & 255) ] & 255);
			buf.append( (char)newCh );
		}
		return buf.toString();
	}

	/**
	 * Décrypte le mot de passe fournit.
	 * @param Le mot de passe crypté.
	 * @return Le mot de passe décrypté.
	 */
	public static String unScramblePassword( String scramble ) {
		char	selector = scramble.charAt(0);
		if ( selector == 'A' ) {
			// This method is symmetrical.
			String pass = scramblePassword( scramble.substring( 1 ));
			return pass.substring( 1 ); // Drop the 'A' spec...
		} else {
			return null;
		}
	}

}