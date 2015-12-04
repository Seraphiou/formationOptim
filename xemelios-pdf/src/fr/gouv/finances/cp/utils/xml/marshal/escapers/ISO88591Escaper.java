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
/*            case '°': return "&#176;";
            case 'À': return "&#192;";
            case 'Á': return "&#193;";
            case 'Â': return "&#194;";
            case 'Ã': return "&#195;";
            case 'Ä': return "&#196;";
            case 'Å': return "&#197;";
            case 'Æ': return "&#198;";
            case 'Ç': return "&#199;";
            case 'È': return "&#200;";
            case 'É': return "&#201;";
            case 'Ê': return "&#202;";
            case 'Ë': return "&#203;";
            case 'Ì': return "&#204;";
            case 'Í': return "&#205;";
            case 'Î': return "&#206;";
            case 'Ï': return "&#207;";
            case 'Ò': return "&#210;";
            case 'Ó': return "&#211;";
            case 'Ô': return "&#212;";
            case 'Õ': return "&#213;";
            case 'Ö': return "&#214;";
            case '×': return "&#215;";
            case 'Ø': return "&#216;";
            case 'Ù': return "&#217;";
            case 'Ú': return "&#218;";
            case 'Û': return "&#219;";
            case 'Ü': return "&#220;";
            case 'İ': return "&#221;";
            case 'à': return "&#224;";
            case 'á': return "&#225;";
            case 'â': return "&#226;";
            case 'ã': return "&#227;";
            case 'ä': return "&#228;";
            case 'å': return "&#229;";
            case 'æ': return "&#230;";
            case 'ç': return "&#231;";
            case 'è': return "&#232;";
            case 'é': return "&#233;";
            case 'ê': return "&#234;";
            case 'ë': return "&#235;";
            case 'ì': return "&#236;";
            case 'í': return "&#237;";
            case 'î': return "&#238;";
            case 'ï': return "&#239;";
            case 'ğ': return "&#240;";
            case 'ñ': return "&#241;";
            case 'ò': return "&#242;";
            case 'ó': return "&#243;";
            case 'ô': return "&#244;";
            case 'õ': return "&#245;";
            case 'ö': return "&#246;";
            case '÷': return "&#247;";
            case 'ø': return "&#248;";
            case 'ù': return "&#249;";
            case 'ú': return "&#250;";
            case 'û': return "&#251;";
            case 'ü': return "&#252;";
            case 'ı': return "&#253;";
            case 'ş': return "&#254;";
            case 'ÿ': return "&#255;";
            case '?': return "&#339;";*/
//            case '?': return "&#338;";
        }
        return super.escapeChar(ch);
    }
}
