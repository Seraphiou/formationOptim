/*
 * Copyright
 *   2006 axYus - www.axyus.com
 *   2006 C.Marchand - christophe.marchand@axyus.com
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

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 *
 * @author chm
 */
public class BlankModel implements NoeudModifiable {
    private static Logger logger = Logger.getLogger(BlankModel.class);
    private NoeudModifiable _NMParent = null;
    private String configXPath = null;
    private QName qn;
    
    public static final transient String TAG="blank";
    public static final transient QName QN = new QName(TAG);
    
    /** Creates a new instance of BlankModel */
    public BlankModel(QName tag) {
        super();
        this.qn=tag;
    }
    
    @Override
    public void addCharacterData(String cData) throws SAXException {
    }
    
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }
    
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
    }
    
    @Override
    public void marshall(XmlOutputter output) {
        output.startTag("blank");
        output.endTag("blank");
    }
    
    @Override
    public void validate() throws InvalidXmlDefinition {
    }
    
    @Override
    public BlankModel clone() { return this; }
    
    @Override
    public void modifyAttr(String attrName, String value) {
    }
    
    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : "+e);
        }
    }
    
    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        this._NMParent = p;
    }
    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return this._NMParent;
    }
    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        return null;
    }
    
    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return null;
    }
    
    @Override
    public void resetCharData() {
    }
    @Override
    public String getIdValue() { return null; }

    @Override
    public String getConfigXPath() {
        if(configXPath==null) {
            if(getParentAsNoeudModifiable()!=null) configXPath = getParentAsNoeudModifiable().getConfigXPath();
            else configXPath = "";
            configXPath+="/"+TAG;
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) { return null; }

    @Override
    public QName getQName() { return qn; }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
    }


}
