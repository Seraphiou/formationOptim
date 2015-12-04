/*
 * Copyright
 *   2010 axYus - www.axyus.com
 *   2010 J-P. Tessier -- jean-philippe.tessier@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.txt2xml;

import java.text.DecimalFormat;

public class AbstractTxt2Xml {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AbstractTxt2Xml.class);
    private static final DecimalFormat df = new DecimalFormat("##0.0000");

    public AbstractTxt2Xml() {
        super();
    }

    public String getAmount(String montant) {
        Double mt = 0d;
        try {
            if (montant.trim().length() > 0) {
                mt = Double.parseDouble(montant);
            }
            return df.format(mt / 100d);
        } catch (NumberFormatException ex) {
            logger.debug("Problème lors du formattage du montant (" + montant + ") : " + ex.getLocalizedMessage());
        }
        return montant;
    }

    /**
     *
     * @param date : frmat AAAAMMJJ
     * @return
     */
    public String getDateLong(String date) {
        if (date.length() == 8) {
            String annee = date.substring(0, 4);
            String mois = date.substring(4, 6);
            String jour = date.substring(6, 8);
            return new StringBuffer(annee).append("-").append(mois).append("-").append(jour).toString();
        } else {
            logger.debug("Problème lors du formattage de la date (" + date + ") ");
        }
        return date;
    }
}
