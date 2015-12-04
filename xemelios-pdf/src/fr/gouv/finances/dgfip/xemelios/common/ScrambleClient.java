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
 * This class is a command-line tool to crypt passwords
 * @author christophe.marchand
 */
public class ScrambleClient {

	public static void main(String[] args) {
		java.io.PrintStream out = System.out;
		java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
		out.println("Type <ENTER> to exit.");
		while(true) {
			out.print("clear password >");
			try {
				String input = in.readLine();
				if(input==null || input.length()==0) break;
				out.println("\tcrypted password >"+Scramble.scramblePassword(input));
			} catch(Throwable t) {
				System.exit(1);
			}
		}
		out.println("bye...");
	}
}