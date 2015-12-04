/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.utils;

/**
 *
 * @author chm
 */
public class Luhn {
    
    private static char [] reverse (String s) {
		char [] ret = new char[s.length()];
		int index = 0;
		for (int i=s.length()-1; i>=0; i--) {
			ret[index++]=s.charAt(i);
		}		
		return ret;
	}
	
    
    public static boolean testLuhnSiren (String s) {
		boolean isError = false;
		
		String sSansEsp = s.replaceAll(" ", "").replaceAll("-", "");
		
		if (sSansEsp.length()!=9 || !testLuhnString(sSansEsp))
			isError = true;
		
		return !isError;
	}
    
    
    public static boolean testLuhnSiret (String s) {
		boolean isError = false;
		
		String sSansEsp = s.replaceAll(" ", "").replaceAll("-", "");
		
		if (sSansEsp.length()!=14 || !testLuhnString(sSansEsp))
			isError = true;
		
		return !isError;
	}
       
    
	public static boolean testLuhnString (String s) {
		boolean isError = false;
			
		char ccNumArray [] = reverse(s);

		int len = ccNumArray.length;
		int sum = 0;
		for(int i = 0; i < len; i++) {
			int num = (int)(ccNumArray[i]-'0');

			/* this is the checksum digit; it's *never* doubled. Just add
			 * it to the sum
			 */
			if(i == 0) {
				sum += num;
				continue;
			}
			
			/* these are the odd-numbered positions in the array; they
			 * should be doubled, and if the result is > 10, the two digits
			 * should be added together and then added to the sum
			 */
			if(i % 2 != 0) {
				num *= 2;
				if (num > 9)
					num -= 9;
				sum += num;
			}

			/* these are the even-numbered positions in the array (except
			 * for position 0). These are never doubled, just added to the
			 * sum
			 */
			else {
				sum += num;
			}
		}
		
		/* if the sum is evenly divisible by 10 (there is no remainder after
		 * the division), then the number is a valid number according to the
		 * luhn algorithm
		 */	
		if(sum % 10 == 0) {
			isError = false;
		} else {
			isError = true;
		}
		
		return !isError;
	}


}
