/*
 * Copyright
 *  2010 axYus - http://www.axyus.com
 *  2010 C.Marchand - christophe.marchand@axyus.com
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
 * 
 */
package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author cmarchand
 */
public class ConfigModelPatcher {

    public static void patchSharedConfig(PersonnalConfigurationModel sharedConfig) {
        for (SavedRequestsModel srm : sharedConfig.getAllSavedRequests()) {
            for (RechercheModel rm : srm.getRecherches()) {
                patchRechercheModel(rm);
            }
        }
    }
    public static void patchRechercheModel(RechercheModel rm) {
        rm.setName(patchString(rm.getName()));
        for (CritereModel cm : rm.getCriteres()) {
            for (XmlMarshallable m : cm.getInputs()) {
                if (m instanceof SelectModel) {
                    SelectModel im = (SelectModel) m;
                    im.setValue(patchString(im.getValue()));
                }
            }
            for (PropertyModel pm : cm.getProperties()) {
                pm.setValue(patchString(pm.getValue()));
            }
        }
    }

    public static String patchString(String s) {
        if (s == null) {
            return null;
        }
        String sTmp = StringEscapeUtils.unescapeXml(s);
        while (!s.equals(sTmp)) {
            s = sTmp;
            sTmp = StringEscapeUtils.unescapeXml(s);
        }
        return sTmp;
    }
}
