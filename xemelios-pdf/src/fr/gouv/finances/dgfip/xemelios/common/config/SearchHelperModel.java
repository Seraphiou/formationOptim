/*
 * Copyright
 *  2011 axYus - http://www.axyus.com
 *  2011 C.Marchand - christophe.marchand@axyus.com
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
 * along with XEMELIOS ; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
 public class SearchHelperModel implements NoeudModifiable {
    public static final QName QNAME = new QName("search-helper");
    private String documentId, etatId, elementId, returnValueColumnId;
    private NoeudModifiable parent;
    private SearchHelperParameterModel collectivite, budget;
    private Hashtable<String,SourceTargetModel> critereMappings;
    private ArrayList<SourceTargetModel> excludedCriteres;
    private ArrayList<RequiredCritereModel> requiredCriteres;

    public SearchHelperModel(QName qn) {
        super();
        critereMappings = new Hashtable<String, SourceTargetModel>();
        excludedCriteres = new ArrayList<SourceTargetModel>();
        requiredCriteres = new ArrayList<RequiredCritereModel>();
    }
    @Override
    public void addCharacterData(String cData) throws SAXException {}

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(DocumentsMapping.COLLECTIVITE.equals(tag)) collectivite = (SearchHelperParameterModel)child;
        else if(DocumentsMapping.BUDGET.equals(tag)) budget = (SearchHelperParameterModel)child;
        else if(DocumentsMapping.CRITERE_MAPPING.equals(tag)) {
            SourceTargetModel cmm = (SourceTargetModel)child;
            critereMappings.put(cmm.getSource(), cmm);
        } else if(DocumentsMapping.EXCLUDE_CRITERE.equals(tag)) {
            SourceTargetModel cmm = (SourceTargetModel)child;
            excludedCriteres.add(cmm);
        } else if(RequiredCritereModel.QNAME.equals(tag)) {
            requiredCriteres.add((RequiredCritereModel)child);
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        documentId = attributes.getValue("documentId");
        etatId = attributes.getValue("etatId");
        elementId = attributes.getValue("elementId");
        returnValueColumnId = attributes.getValue("returnValue");
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        QName tag = new QName(uri, localName);
        if(DocumentsMapping.COLLECTIVITE.equals(tag)) return collectivite;
        else if(DocumentsMapping.BUDGET.equals(tag)) return budget;
        else {
            // TODO
            return null;
        }
    }

    @Override
    public void marshall(XmlOutputter output) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(documentId==null || documentId.length()==0) throw new InvalidXmlDefinition("L'attribut documentId est obligatoire");
    }

    @Override
    public QName getQName() {
        return QNAME;
    }

    @Override
    public NoeudModifiable getParentAsNoeudModifiable() {
        return parent;
    }

    @Override
    public void setParentAsNoeudModifiable(NoeudModifiable p) {
        parent=p;
    }

    @Override
    public NoeudModifiable getChildAsNoeudModifiable(String tagName, String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void modifyAttr(String attrName, String value) {
        if("documentId".equals(attrName)) documentId = value;
        else if("etatId".equals(attrName)) etatId = value;
        else if("elementId".equals(attrName)) elementId = value;
        else if("returnValue".equals(attrName)) returnValueColumnId = value;
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void resetCharData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getIdValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getConfigXPath() {
        return parent.getConfigXPath().concat("/search-helper");
    }

    @Override
    public SearchHelperModel clone() {
        // TODO
        return this;
    }

    public SearchHelperParameterModel getBudget() {
        return budget;
    }

    public SearchHelperParameterModel getCollectivite() {
        return collectivite;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getElementId() {
        return elementId;
    }

    public String getEtatId() {
        return etatId;
    }

    public String getReturnValueColumnId() {
        return returnValueColumnId;
    }

    public SourceTargetModel getCritereMappingBySource(String sourceId) {
        return critereMappings.get(sourceId);
    }

    public ArrayList<SourceTargetModel> getExcludedCriteres() {
        return excludedCriteres;
    }

    public ArrayList<RequiredCritereModel> getRequiredCriteres() {
        return requiredCriteres;
    }

    public void setRequiredCriteres(ArrayList<RequiredCritereModel> requiredCriteres) {
        this.requiredCriteres = requiredCriteres;
    }

    @Override
    public void prepareForUnload() {
        parent = null;
        for(SourceTargetModel stm:excludedCriteres) stm.prepareForUnload();
        for(RequiredCritereModel rcm:requiredCriteres) rcm.prepareForUnload();
        if(collectivite!=null) collectivite.prepareForUnload();
        if(budget!=null) budget.prepareForUnload();
    }
    
}
