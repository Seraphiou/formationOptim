/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.cp.utils.xml.marshal.escapers;

/**
 *
 * @author chm
 */
public class ISO88591Escaper extends DefaultCharacterEscaper {

    public ISO88591Escaper() {
        super();
    }

    @Override
    public String escapeChar(char ch) {
        switch(ch) {
/*            case '�': return "&#176;";
            case '�': return "&#192;";
            case '�': return "&#193;";
            case '�': return "&#194;";
            case '�': return "&#195;";
            case '�': return "&#196;";
            case '�': return "&#197;";
            case '�': return "&#198;";
            case '�': return "&#199;";
            case '�': return "&#200;";
            case '�': return "&#201;";
            case '�': return "&#202;";
            case '�': return "&#203;";
            case '�': return "&#204;";
            case '�': return "&#205;";
            case '�': return "&#206;";
            case '�': return "&#207;";
            case '�': return "&#210;";
            case '�': return "&#211;";
            case '�': return "&#212;";
            case '�': return "&#213;";
            case '�': return "&#214;";
            case '�': return "&#215;";
            case '�': return "&#216;";
            case '�': return "&#217;";
            case '�': return "&#218;";
            case '�': return "&#219;";
            case '�': return "&#220;";
            case '�': return "&#221;";
            case '�': return "&#224;";
            case '�': return "&#225;";
            case '�': return "&#226;";
            case '�': return "&#227;";
            case '�': return "&#228;";
            case '�': return "&#229;";
            case '�': return "&#230;";
            case '�': return "&#231;";
            case '�': return "&#232;";
            case '�': return "&#233;";
            case '�': return "&#234;";
            case '�': return "&#235;";
            case '�': return "&#236;";
            case '�': return "&#237;";
            case '�': return "&#238;";
            case '�': return "&#239;";
            case '�': return "&#240;";
            case '�': return "&#241;";
            case '�': return "&#242;";
            case '�': return "&#243;";
            case '�': return "&#244;";
            case '�': return "&#245;";
            case '�': return "&#246;";
            case '�': return "&#247;";
            case '�': return "&#248;";
            case '�': return "&#249;";
            case '�': return "&#250;";
            case '�': return "&#251;";
            case '�': return "&#252;";
            case '�': return "&#253;";
            case '�': return "&#254;";
            case '�': return "&#255;";
            case '?': return "&#339;";*/
//            case '?': return "&#338;";
        }
        return super.escapeChar(ch);
    }
}
