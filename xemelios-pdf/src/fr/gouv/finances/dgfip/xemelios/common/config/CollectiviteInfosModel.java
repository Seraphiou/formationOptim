/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.Iterator;
import java.util.TreeSet;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author chm
 */
public class CollectiviteInfosModel extends RecherchePaireModel {

    public static final transient String TAG = "collectivite-path";
    public static final transient QName QN = new QName(TAG);
    private TreeSet<ParentModel> parents;

    public CollectiviteInfosModel(QName tagName) {
        super(tagName);
        parents = new TreeSet<ParentModel>();
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (DocumentsMapping.PATH.equals(tagName)) {
            super.addChild(child, tagName);
        } else if (DocumentsMapping.CODE_PATH.equals(tagName)) {
            super.addChild(child, tagName);
        } else if (DocumentsMapping.LIBELLE_PATH.equals(tagName)) {
            super.addChild(child, tagName);
        } else if (ParentModel.QN.equals(tagName)) {
            ParentModel pm = (ParentModel) child;
            pm.setParentAsNoeudModifiable(this);
            parents.add(pm);
        }
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        if (ParentModel.QN.getLocalPart().equals("qName")) {
            String sll = atts.getValue("level");
            int i = -1;
            try {
                i = Integer.parseInt(sll);
            } catch (Exception ex) {
            }
            if (i > 0) {
                return (XmlMarshallable) (parents.toArray()[i - 1]);
            }
            return null;
        } else {
            return super.getChildToModify(uri, localName, qName, atts);
        }
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        output.addAttribute("sort", getSortBy());
        if (getPath() != null) {
            getPath().marshall(output);
        }
        if (getCodePath() != null) {
            getCodePath().marshall(output);
        }
        if (getLibellePath() != null) {
            getLibellePath().marshall(output);
        }
        for (ParentModel p : parents) {
            p.marshall(output);
        }
        output.endTag(TAG);
    }

    public int getParentsCount() {
        return parents.size();
    }

    public ParentModel getParent(int i) {
        return (ParentModel) parents.toArray()[i];
    }

    @Override
    public CollectiviteInfosModel clone() {
        CollectiviteInfosModel other = new CollectiviteInfosModel(QN);
        other.setId(getId());
        if (getPath() != null) {
            try {
                other.addChild(getPath().clone(), DocumentsMapping.PATH);
            } catch (Exception ex) {
            }

        }
        try {
            other.addChild(getCodePath().clone(), DocumentsMapping.CODE_PATH);
        } catch (Exception ex) {
        }
        try {
            other.addChild(getLibellePath().clone(), DocumentsMapping.LIBELLE_PATH);
        } catch (Exception ex) {
        }
        for (Object o : parents.toArray()) {
            ParentModel pm = (ParentModel) o;
            try {
                other.addChild(pm.clone(), ParentModel.QN);
            } catch (Exception ex) {
            }
        }
        return other;
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        super.validate();
        if (!parents.isEmpty()) {
            Iterator<ParentModel> it = parents.iterator();
            ParentModel pm = it.next();
            for (int i = 1; i <= parents.size(); i++) {
                if (pm.getLevel() != i) {
                    throw new InvalidXmlDefinition(pm.getConfigXPath() + " levels must start at 1 and be consecutives");
                }
                if (i < parents.size()) {
                    pm = it.next();
                }
            }
        }
    }

    @Override
    public void prepareForUnload() {
        super.prepareForUnload();
        for(ParentModel pm:parents) pm.prepareForUnload();
    }

}
