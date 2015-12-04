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
import java.util.HashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathExpressionException;
import nu.xom.Element;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Représente un predicat.
 * @author cmarchand
 */
public class PredicatModel implements XmlMarshallable, XomDefinitionable {
    public static final QName QN = new QName(RulesParser.RULES_NS, "predicat");
    private static final Logger logger = Logger.getLogger(PredicatModel.class);
    private StringBuilder content;

    public PredicatModel(QName qn) {
        super();
        content = new StringBuilder();
    }
    public PredicatModel(Element el) throws XPathExpressionException {
        this(QN);
        content.append(el.query("./text()"));
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
        content.append(cData);
    }

    @Override
    public void addChild(XmlMarshallable child, QName tag) throws SAXException {
        throw new SAXException("no child expected in "+QN);
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        return this;
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
        output.startTag(QN);
        output.addCharacterData(content.toString());
        output.endTag(QN);
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
        if(content.length()==0) throw new InvalidXmlDefinition(QN+" must contain a valid predicat expression");
    }

    @Override
    public QName getQName() {
        return QN;
    }

    @Override
    public PredicatModel clone() {
        PredicatModel pm = new PredicatModel(QN);
        pm.content.append(content.toString());
        return pm;
    }

    public String getPredicateExpression() { return content.toString(); }

    @Override
    public Element getXomDefinition() {
        Element ret = new Element("predicat", RulesParser.RULES_NS);
        ret.appendChild(getPredicateExpression());
        return ret;
    }
    public boolean matches(HashMap<String, Object> props) {
        String expression = getPredicateExpression();

        // JavaScript version
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        // evaluate JavaScript code from String
        try {
            for(String key: props.keySet()) {
                engine.put(key, props.get(key));
            }
            Object ret = engine.eval(expression);
            if(ret instanceof Boolean) {
                return ((Boolean)ret).booleanValue();
            } else if(ret==null) {
                return false;
            } else {
                return "true".equals(ret.toString());
            }
        } catch(Exception ex) {
            logger.error("while evaluating "+expression, ex);
            return false;
        }
    }

}
