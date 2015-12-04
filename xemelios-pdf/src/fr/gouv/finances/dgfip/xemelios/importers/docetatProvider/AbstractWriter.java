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

import fr.gouv.finances.dgfip.utils.xml.SAXWriter;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.NSModel;
import java.io.OutputStream;
import java.util.Stack;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author chm
 */
public abstract class AbstractWriter extends SAXWriter {
    private static final Logger logger = Logger.getLogger(AbstractWriter.class);
        private AttributesHolder attHolder;
        private NodesHolder nodeHolder;
        protected Stack<QName> stack;
        private DocumentModel dm;

        public AbstractWriter(DocumentModel dm,OutputStream out, String encoding, AttributesHolder attHolder, NodesHolder nodeHolder, NamespaceHolder ns) {
            super(out, encoding);
            this.attHolder = attHolder;
            this.nodeHolder = nodeHolder;
            stack = new Stack<QName>();
            this.dm=dm;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            for(NSModel ns:dm.getNamespaces().getMappings()) {
                startPrefixMapping(ns.getPrefix(), ns.getUri());
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            super.startElement(uri, localName, qName, atts);
            QName name = new QName(uri, localName!=null ? localName : qName);
            stack.push(name);
            String xpath = getXPath();
            AttributeHolder at = attHolder.getAttrsAt(xpath);
            if (at != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(" ").append(at.getName()).append("=\"").append(at.getValue()).append("\" ");
                output(sb.toString());
            }
        }

        public String getXPath() {
            StringBuilder sb = new StringBuilder();
            for (QName qn : stack) {
                sb.append("/");
                String prefix = getnsContext().getPrefix(qn.getNamespaceURI());
                if (prefix != null && prefix.length() > 0) {
                    sb.append(prefix).append(":");
                }
                sb.append(qn.getLocalPart());
            }
            return sb.toString();
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            QName name = stack.pop();
        }

        protected void writeNode(Node node) {
            short type = node.getNodeType();
            switch (type) {
                case Node.DOCUMENT_NODE:
                    break;

                case Node.DOCUMENT_FRAGMENT_NODE:
                    writeChildren(node);
                    break;

                case Node.DOCUMENT_TYPE_NODE:
                    break;

                case Node.ELEMENT_NODE:
                    element((Element) node);
                    break;

                case Node.ATTRIBUTE_NODE:
                    break;

                case Node.ENTITY_REFERENCE_NODE:
                    break;

                case Node.ENTITY_NODE:
                    break;

                case Node.NOTATION_NODE:
                    break;

                case Node.PROCESSING_INSTRUCTION_NODE:
                    break;

                case Node.TEXT_NODE:
                    text((Text) node);
                    break;

                case Node.CDATA_SECTION_NODE:
                    break;

                case Node.COMMENT_NODE:
                    comment((Comment) node);
                    break;
            }
        }

        private void writeChildren(Node node) {
            NodeList l = node.getChildNodes();
            int size = l.getLength();
            for (int i = 0; i < size; i++) {
                Node n = l.item(i);
                writeNode(n);
            }
        }

        private void element(Element elem) {
            AttributesImpl atts = new AttributesImpl();
            NamedNodeMap a = elem.getAttributes();
            int size = a.getLength();
            for (int i = 0; i < size; i++) {
                Attr att = (Attr) a.item(i);
                if(att!=null) {
                    try {
                        atts.addAttribute(att.getNamespaceURI()==null ? XMLConstants.NULL_NS_URI : att.getNamespaceURI(), att.getLocalName()==null ? att.getName() : att.getLocalName(), att.getName(), "CDATA", att.getValue());
                    } catch(NullPointerException npEx) {
                        logger.debug("... really strange !");
                    }
                } else {
                    logger.info("strange, getting a null attribute...");
                }
            }
            try {
                startElement(elem.getNamespaceURI(), elem.getLocalName(), elem.getTagName(), atts);
            } catch(SAXException saxEx) {
                logger.error("element(Element)",saxEx);
            }

            if (elem.hasChildNodes()) {
                writeChildren(elem);
            }
            try {
                endElement(elem.getNamespaceURI(), elem.getLocalName(), elem.getTagName());
            } catch(SAXException saxEx) {
                logger.error("element(Element)", saxEx);
            }
        }

        private void text(Text text) {
            writeEscapedText(text.getData());
        }


        private void comment(Comment comment) {
            output("<!--");
            output(comment.getData());
            output("-->\n");
        }

}
