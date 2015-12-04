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
            case '�': return "&#176;";
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
            case '�': return "&#208;";
            case '�': return "&#209;";
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
            case '�': return "&#222;";
            case '�': return "&#223;";
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
