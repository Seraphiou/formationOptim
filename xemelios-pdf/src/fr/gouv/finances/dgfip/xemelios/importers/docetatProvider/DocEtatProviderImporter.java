/*
 * Copyright 
 *   2005 axYus - www.axyus.com
 *   2005 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.importers.*;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.NSModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ParameterModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ParametersModel;
import fr.gouv.finances.dgfip.xemelios.utils.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 *
 * @author chm
 */
public class DocEtatProviderImporter extends DefaultImporter {
    private static final Logger logger = Logger.getLogger(DocEtatProviderImporter.class);

    private static final transient String STOP_PARSING_MESSAGE = "STOP_PARSING_PATH reached";

    public DocEtatProviderImporter(XemeliosUser user, PropertiesExpansion applicationProperties) {
        super(user, applicationProperties);
    }
    @Override
    protected FileInfo importFile(File f) throws Exception {
        File outputTmpFile = new File(FileUtils.getTempDir(), f.getName());
        if(outputTmpFile.exists()) outputTmpFile.delete();
        getImpSvcProvider().startLongWait();

        String fileEncoding = getFileEncoding(f);
        String xmlVersion = getFileXmlVersion(f);

        ParametersModel params = getDocumentModel().getParameters();
        AttributesHolder attHolder = new AttributesHolder();
        NodesHolder nodeHolder = new NodesHolder();
        HashSet<String> attrNames = new HashSet<String>();
        HashSet<String> nodeNames = new HashSet<String>();
        for (ParameterModel pm : params.getParameters()) {
            if (pm.getName().startsWith("attribute.")) {
                String[] ss = pm.getName().split("\\.");
                String attrId = ss[1];
                if (!attrNames.contains(attrId)) {
                    String attrLocation = params.getParameter("attribute." + attrId + ".location").getValue();
                    String attrXPath = params.getParameter("attribute." + attrId + ".value").getValue();
                    String attrName = params.getParameter("attribute." + attrId + ".name").getValue();
                    attHolder.addAttribute(new AttributeHolder(attrId, attrName, attrLocation, attrXPath));
                    attrNames.add(attrId);
                }
            } else if (pm.getName().startsWith("node.")) {
                String[] ss = pm.getName().split("\\.");
                String nodeId = ss[1];
                if (!nodeNames.contains(nodeId)) {
                    String copyFrom = params.getParameter("node." + nodeId + ".copyFrom").getValue();
                    String copyTo = params.getParameter("node." + nodeId + ".copyTo").getValue();
                    nodeHolder.addNode(new NodeHolder(copyFrom, copyTo));
                    nodeNames.add(nodeId);
                }
            }
        }
        String stopParsingPath = params.getParameter("stop.parsing") != null ? params.getParameter("stop.parsing").getValue() : null;
        ParserReader pr = new ParserReader(attHolder, nodeHolder, stopParsingPath, getDocumentModel());
        SAXParserFactory sf = FactoryProvider.getSaxParserFactory();
        SAXParser parser = sf.newSAXParser();
        try {
            parser.parse(f, pr);
        } catch (SAXException saxEx) {
            if (!STOP_PARSING_MESSAGE.equals(saxEx.getMessage())) {
                throw saxEx;
            }
        }

        // here, holder contains new values
        FileOutputStream fos = new FileOutputStream(outputTmpFile);
        String abstractWriterClassName = params.getParameter("abstract.writer").getValue();
        AbstractWriter pw = null;
        try {
            Class clazz = Class.forName(abstractWriterClassName);
            Constructor<AbstractWriter> cc = clazz.getConstructor(DocumentModel.class,OutputStream.class,String.class,AttributesHolder.class,NodesHolder.class,NamespaceHolder.class);
            pw = cc.newInstance(dm,fos, fileEncoding, attHolder, nodeHolder, pr.getNS());
        } catch(Exception ex) {
            logger.error("while instancing "+abstractWriterClassName,ex);
        }

        try {
            parser.parse(f, pw);
        } catch (SAXException saxEx) {
            logger.error("file not well-formed", saxEx);
        }
        fos.flush(); fos.close();

        getImpSvcProvider().endLongWait();
        
        FileInfo ret = super.importFile(outputTmpFile);
        
        //outputTmpFile.delete();
        
        return ret;
    }

    private class ParserReader extends DefaultHandler2 {

        private final Logger logger = Logger.getLogger(ParserReader.class);
        private Document doc;
        private AttributesHolder attHolder;
        private NodesHolder nodeHolder;
        private Stack<QName> stack;
        private NamespaceHolder ns;
        private String stopParsingPath;
        private Element currentNode;
        private boolean generatingDom = false;

        public ParserReader(AttributesHolder attHolder, NodesHolder nodeHolder, String stopParsingPath, DocumentModel dm) {
            super();
            this.attHolder = attHolder;
            this.nodeHolder = nodeHolder;
            stack = new Stack<QName>();
            ns = new NamespaceHolder();
            this.stopParsingPath = stopParsingPath;
            try {
                doc = FactoryProvider.getDocumentBuilderFactory().newDocumentBuilder().newDocument();
            } catch (Exception ex) {
                logger.error("ParserReader.<init>", ex);
            }
            for (NSModel n : dm.getNamespaces().getMappings()) {
                this.ns.addMapping(n.getPrefix(), n.getUri());
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            QName name = new QName(uri, localName);
            stack.push(name);
            String xpath = getXPath();
            for (int i = 0; i < attributes.getLength(); i++) {
                StringBuilder attName = new StringBuilder();
                String prefix = ns.getPrefix(attributes.getURI(i));
                if (prefix != null && prefix.length() > 0) {
                    attName.append(prefix).append(":");
                }
                attName.append(attributes.getLocalName(i));
                attName.insert(0, "/@");
                attName.insert(0, xpath);
                String attPath = attName.toString();
                attHolder.setAttValue(attPath, attributes.getValue(i));
            }
            if (nodeHolder.exists(xpath) || generatingDom) {
                Element el = doc.createElementNS(uri, qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    Attr at = doc.createAttributeNS(attributes.getURI(i), attributes.getQName(i));
                    at.setValue(attributes.getValue(i));
                    el.setAttributeNodeNS(at);
                }
                if (generatingDom) {
                    currentNode.appendChild(el);
                    currentNode = el;
                } else {
                    currentNode = el;
                    generatingDom = true;
                }
            }
            if (xpath.equals(stopParsingPath)) {
                throw new SAXException(STOP_PARSING_MESSAGE);
            }
        }

        @Override
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            ns.addMapping(prefix, uri);
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
            ns.removeMapping(prefix);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String xpath = getXPath();
            QName name = stack.pop();
            if (generatingDom && currentNode.getParentNode() != null) {
                currentNode = (Element) currentNode.getParentNode();
            }
            if (nodeHolder.exists(xpath)) {
                nodeHolder.setValueAt(xpath, currentNode);
                generatingDom = false;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (generatingDom && currentNode != null) {
                Text txt = doc.createTextNode(new String(ch, start, length));
                currentNode.appendChild(txt);
            }
        }

        String getXPath() {
            StringBuilder sb = new StringBuilder();
            for (QName qn : stack) {
                sb.append("/");
                String prefix = ns.getPrefix(qn.getNamespaceURI());
                if (prefix != null && prefix.length() > 0) {
                    sb.append(prefix).append(":");
                }
                sb.append(qn.getLocalPart());
            }
            return sb.toString();
        }

        NamespaceHolder getNS() {
            return ns;
        }
    }


}
