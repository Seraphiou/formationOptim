/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.gouv.finances.cp.utils.xml.marshal;

/**
 *
 * @author chm
 */
public interface CharacterEscaper {
    
    public String escapeChar(char ch);
    public String escapeString(String s);

}
