/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.cp.utils.xml.marshal.escapers;

/**
 *
 * @author chm
 */
public class UTF8Escaper extends DefaultCharacterEscaper {

    public UTF8Escaper() {
        super();
    }

    @Override
    public String escapeChar(char ch) {
        switch (ch) {
            case '?': return "&#128;";
//            case '?': return "&#140;";
//            case '?': return "&#156;";
            case '°': return "&#176;";
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
            case 'Ğ': return "&#208;";
            case 'Ñ': return "&#209;";
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
            case 'Ş': return "&#222;";
            case 'ß': return "&#223;";
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
//            case '?': return "&#256;";
//            case '?': return "&#257;";
//            case '?': return "&#258;";
//            case '?': return "&#259;";
//            case '?': return "&#260;";
//            case '?': return "&#261;";
//            case '?': return "&#262;";
//            case '?': return "&#263;";
//            case '?': return "&#264;";
//            case '?': return "&#265;";
//            case '?': return "&#266;";
//            case '?': return "&#267;";
//            case '?': return "&#268;";
//            case '?': return "&#269;";
//            case '?': return "&#270;";
//            case '?': return "&#271;";
//            case '?': return "&#272;";
//            case '?': return "&#273;";
//            case '?': return "&#274;";
//            case '?': return "&#275;";
//            case '?': return "&#276;";
//            case '?': return "&#277;";
//            case '?': return "&#278;";
//            case '?': return "&#279;";
//            case '?': return "&#280;";
//            case '?': return "&#281;";
//            case '?': return "&#282;";
//            case '?': return "&#283;";
//            case '?': return "&#296;";
//            case '?': return "&#297;";
//            case '?': return "&#298;";
//            case '?': return "&#299;";
//            case '?': return "&#300;";
//            case '?': return "&#301;";
//            case '?': return "&#302;";
//            case '?': return "&#303;";
//            case '?': return "&#304;";
//            case '?': return "&#305;";
//            case '?': return "&#332;";
//            case '?': return "&#333;";
//            case '?': return "&#334;";
//            case '?': return "&#335;";
//            case '?': return "&#336;";
//            case '?': return "&#337;";
//            case '?': return "&#338;";
//            case '?': return "&#339;";
//            case '?': return "&#360;";
//            case '?': return "&#361;";
//            case '?': return "&#362;";
//            case '?': return "&#363;";
//            case '?': return "&#364;";
//            case '?': return "&#365;";
//            case '?': return "&#366;";
//            case '?': return "&#367;";
//            case '?': return "&#368;";
//            case '?': return "&#369;";
//            case '?': return "&#370;";
//            case '?': return "&#370;";
//            case '?': return "&#374;";
//            case '?': return "&#375;";
//            case '?': return "&#376;";
        }
        return super.escapeChar(ch);
    }
}
