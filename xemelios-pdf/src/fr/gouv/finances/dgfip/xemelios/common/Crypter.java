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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author chm
 */
public class Crypter {
	public static String cryptPassword(String uncrypted) {
		return encode(uncrypted);
	}

	public static void main(String[] args) {
		System.out.println(cryptPassword(args[0]));
	}

	public static String encode(String key) {

		byte[] uniqueKey = key.getBytes();
		byte[] hash = null;

		try {

			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
			
		} catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");
		}

		StringBuffer hashString = new StringBuffer();
		for (int i = 0; i < hash.length; ++i) {
			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			} else {
				hashString.append(hex.substring(hex.length() - 2));
			}
		}

		return hashString.toString();

	}
}
