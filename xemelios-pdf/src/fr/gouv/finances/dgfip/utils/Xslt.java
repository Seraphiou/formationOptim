/*
 * Copyright
 *   2011 axYus - www.axyus.com
 *   2011 JP.Tessier - jean-philippe.tessier@axyus.com
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
package fr.gouv.finances.dgfip.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;

import fr.gouv.finances.dgfip.xemelios.common.DefaultXsltChooser;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ElementModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;

/**
 * @author jp.tessier
 *
 */
public class Xslt {

    public Xslt() {
        super();
    }

    public static File getFile(DocumentModel dm, EtatModel em, ElementModel element, Document doc) {
        File f = null;
        try {
            if (dm.getXsltFileChooser() != null && dm.getXsltFileChooser().length() > 0) {
                XsltFileChooser o = (XsltFileChooser) Class.forName(dm.getXsltFileChooser()).newInstance();
                f = o.getXslt(dm, em, element, doc);
            } else {
                DefaultXsltChooser dxc = new DefaultXsltChooser();
                f = dxc.getXslt(dm, em, element, doc);
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return f;
    }
}
