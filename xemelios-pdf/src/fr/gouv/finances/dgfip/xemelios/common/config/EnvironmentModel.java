/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 C.Marchand - christophe.marchand@axyus.com
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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.NoeudModifiable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

public class EnvironmentModel implements NoeudModifiable {

    private static Logger logger = Logger.getLogger(EnvironmentModel.class);
    private NoeudModifiable _NMParent = null;
    public static final transient String TAG = "environment";
    public static final transient QName QN = new QName(TAG);
    public static final int ALL_DOMAINS = -1;
    private Vector<VariableModel> vVariables;
    private Hashtable<Integer, Vector<VariableModel>> variablesByDomain;
    private HashMap<String, VariableModel> hVariableModels;

    public EnvironmentModel(QName tagName) {
        super();
        vVariables = new Vector<VariableModel>();
        variablesByDomain = new Hashtable<Integer, Vector<VariableModel>>();
        hVariableModels = new HashMap<String, VariableModel>();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        VariableModel vm = (VariableModel) child;
        String key = vm.getName() + "|" + vm.getDomain();
        if (hVariableModels.containsKey(key)) {
            VariableModel old = hVariableModels.get(key);
            vVariables.remove(old);
            hVariableModels.remove(key);
        }
        vm.setParentAsNoeudModifiable(this);
        hVariableModels.put((key), vm);
        vVariables.add(vm);
        Integer domain = new Integer(vm.getDomain());
        Vector<VariableModel> vars = variablesByDomain.get(domain);
        if (vars == null) {
            vars = new Vector<VariableModel>();
            variablesByDomain.put(domain, vars);
        }
        vars.add(vm);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        for (VariableModel vm : vVariables) {
            vm.validate();
        }
    }

    @Override
    public EnvironmentModel clone() {
        EnvironmentModel other = new EnvironmentModel(QN);
        for (VariableModel vm : vVariables) {
            try {
                other.addChild(vm.clone(), VariableModel.QN);
            } catch (Throwable t) {
                logger.error("clone().variable", t);
            }
        }
        return other;
    }

    public Enumeration<VariableModel> getVariables(int domain) {
        if (domain == ALL_DOMAINS) {
            return vVariables.elements();
        }
        Vector<VariableModel> ret = variablesByDomain.get(new Integer(domain));
        if (ret == null) {
            return null;
        }
        return ret.elements();
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
        // l'id pass� est mal format�.
        // en effet, la derni�re partie doit �tre convertie en int comme fait dans VariableModel...
        StringTokenizer st = new StringTokenizer(id, "|");
        String sId = st.nextToken() + "|";
        if ("documents".equals(st.nextToken())) {
            sId += EnvironmentDomain.DOMAIN_DOCUMENTS;
        } else {
            sId += EnvironmentDomain.DOMAIN_ELEMENT;
        }

        return hVariableModels.get(sId);
    }

    @Override
    public void modifyAttr(String attrName, String value) {
    }

    @Override
    public void modifyAttrs(Attributes attrs) {
        try {
            getAttributes(new XmlAttributes(attrs));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise � jour des attributs : " + e);
        }
    }

    @Override
    public String[] getChildIdAttrName(String childTagName) {
        return new String[]{"name", "domain"};
    }

    @Override
    public void resetCharData() {
    }

    @Override
    public String getIdValue() {
        return null;
    }
    private String configXPath = null;

    @Override
    public String getConfigXPath() {
        if (configXPath == null) {
            if (getParentAsNoeudModifiable() != null) {
                configXPath = getParentAsNoeudModifiable().getConfigXPath();
            } else {
                configXPath = "";
            }
            configXPath += "/" + TAG;
        }
        return configXPath;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return hVariableModels.get(atts.getValue("name") + "|" + atts.getValue("domain"));
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public void prepareForUnload() {
        _NMParent = null;
        for(VariableModel vm: vVariables) vm.prepareForUnload();
    }
}
