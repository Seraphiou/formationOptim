/*
 * Copyright
 *  2012 axYus - http://www.axyus.com
 *  2012 C.Marchand - christophe.marchand@axyus.com
 *
 * This file is part of XEMELIOS_NB.
 *
 * XEMELIOS_NB is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * XEMELIOS_NB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XEMELIOS_NB; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 */

package fr.gouv.finances.dgfip.xemelios.importers.archives.rules;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.utils.xml.NamespaceContextImpl;
import fr.gouv.finances.dgfip.xemelios.importers.archives.ArchiveImporter;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 *
 * @author cmarchand
 */
public class ImportModel implements XomDefinitionable {
    public static final transient QName QN = new QName(RulesParser.RULES_NS, "import");
    public static final transient String MODE_FULL = "full";
    public static final transient String MODE_PARTIAL = "partial";

    private String archive, mode;
    private ArrayList<FilterModel> filters;

    public ImportModel(QName qn) {
        super();
        filters = new ArrayList<FilterModel>();
    }
    public ImportModel(Element el) throws XPathExpressionException {
        this(QN);
        archive = el.getAttributeValue("archive");
        if(archive!=null && archive.length()==0) archive=null;
        mode = el.getAttributeValue("mode");
        if(mode==null || mode.length()==0) mode=MODE_FULL;
        XPath xp = FactoryProvider.getXPathFactory().newXPath();
        NamespaceContextImpl ns = new NamespaceContextImpl();
        ns.addMapping("rul", RulesParser.RULES_NS);
        xp.setNamespaceContext(ns);
        Nodes nl = el.query("rul:include | rul:exclude", ArchiveImporter.getNamespaceCtx());
        for(int i=0;i<nl.size();i++) {
            Element child = (Element)nl.get(i);
            filters.add(new FilterModel(child));
        }
    }
    @Override
    public void addCharacterData(String cData) throws SAXException { }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        if(tag.equals(FilterModel.QN_INCLUDE) || tag.equals(FilterModel.QN_EXCLUDE)) filters.add((FilterModel)child);
        else throw new SAXException(QN+": unexpected child "+tag);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        archive = attributes.getValue("archive");
        mode = attributes.getValue("mode");
        if(mode==null) mode=MODE_FULL;
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addAttribute("archive", archive);
        output.addAttribute("mode", mode);
        for(XmlMarshallable xm:filters) xm.marshall(output);
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        for(XmlMarshallable xm:filters) xm.validate();
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public ImportModel clone() {
        ImportModel ret = new ImportModel(QN);
        ret.archive=archive;
        ret.mode=mode;
        for(FilterModel xm:filters) ret.filters.add(xm.clone());
        return ret;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public ArrayList<FilterModel> getFilters() {
        return filters;
    }

    public void setFilters(ArrayList<FilterModel> filters) {
        this.filters = filters;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Renvoie <tt>true</tt> si ce document doit être importé.
     * @param document
     * @return
     */
    public boolean doesDocumentSatisfy(Element document) {
        if(MODE_FULL.equals(mode)) return true;
        else {
            boolean includeable = false;
            boolean excludeable = false;
            for(FilterModel filter:filters) {
                boolean matches = filter.matches(document);
                if(filter.getQName().equals(FilterModel.QN_INCLUDE)) includeable = includeable || matches;
                else excludeable = excludeable || matches;
            }
            boolean ret = includeable && !excludeable;
            return ret;
        }
    }

    @Override
    public Element getXomDefinition() {
        Element ret = new Element(QN.getLocalPart(), QN.getNamespaceURI());
        if(archive!=null && archive.length()>0) ret.addAttribute(new Attribute("archive", archive));
        ret.addAttribute(new Attribute("mode", mode));
        for(FilterModel filter:filters) ret.appendChild(filter.getXomDefinition());
        return ret;
    }

}
