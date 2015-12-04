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

package fr.gouv.finances.dgfip.utils.xml;

import fr.gouv.finances.cp.utils.xml.marshal.MutableBoolean;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Stack;
import javax.xml.XMLConstants;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

/**
 *
 * @author chm
 */
public class SAXWriter extends DefaultHandler2 {
    public static final transient String XML_1_0 = "1.0";
    public static final transient String DEFAULT_ENCODING = "ISO-8859-1";
    public static final transient int MAX_LINESIZE = 128;
    
    private static final boolean CLOSED = false;
    private static final boolean OPENNED = true;

    private static final Logger logger = Logger.getLogger(SAXWriter.class);
    private static final String DEF_INDENT_BUFFER;
    static {
        StringBuffer sb = new StringBuffer(256);
        for ( int i = 0; i < 256; i++ )
            sb.append(' ');
        DEF_INDENT_BUFFER = sb.toString();
    }
    private OutputStream out;
    private String encoding;
    private String xmlVersion;
    private boolean prettyPrint = true;
    private Stack<MutableBoolean> tagStatus;
    private NamespaceContextImpl ns;
    private ArrayList<String> prefixesToDeclare;
    
    private int indentLevel=0;

    public SAXWriter(OutputStream out, String encoding, String xmlVersion) {
        super();
        this.out= out;
        this.encoding= encoding;
        this.xmlVersion=xmlVersion;
        tagStatus = new Stack<MutableBoolean>();
        ns = new NamespaceContextImpl();
        prefixesToDeclare = new ArrayList<String>();
    }
    public SAXWriter(OutputStream out, String encoding) {
        this(out,encoding,XML_1_0);
    }
    public SAXWriter(OutputStream out) {
        this(out,DEFAULT_ENCODING,XML_1_0);
    }
    protected void output(String s) { 
        try {
            out.write(s.getBytes(encoding));
        } catch(Exception ex) {
            logger.error("output(String)",ex);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"").append(xmlVersion).append("\" encoding=\"").append(encoding).append("\"?>");
        if(prettyPrint) sb.append("\n");
        output(sb.toString());
    }


    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if(ns.getPrefix(uri)==null) {
            prefixesToDeclare.add(prefix);
            ns.addMapping(prefix, uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if(ns.getPrefix(prefix)!=null) ns.removeMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        StringBuilder sb = new StringBuilder();
        if(tagStatus.size()>0) {
            if(tagStatus.peek().booleanValue()==CLOSED) sb.append(">");
            tagStatus.peek().setValue(OPENNED);
        }
        indentLevel++;
        tagStatus.push(new MutableBoolean(CLOSED));
        if(prettyPrint) sb.append("\n").append(DEF_INDENT_BUFFER.substring(0, indentLevel));
        sb.append("<");
        String px = uri==null ? null : ns.getPrefix(uri);
        if(px!=null && px.length()>0) sb.append(px).append(":");
        String shortName;
        String sTmp = localName==null ? qName : localName;
        if(sTmp.indexOf(":")>0) shortName=sTmp.substring(sTmp.lastIndexOf(":")+1);
        else shortName=sTmp;
        sb.append(shortName);
        int attsCount = atts.getLength();
        for(int i=0;i<attsCount;i++) {
            // on ne note pas ici les declaration de prefix
            if(!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(atts.getURI(i))) {
                if(prettyPrint && sb.length()>MAX_LINESIZE) {
                    sb.append("\n");
                    output(sb.toString());
                    sb = new StringBuilder();
                    sb.append(DEF_INDENT_BUFFER.substring(0, indentLevel+3));
                }
                sb.append(" ");
                String prx = ns.getPrefix(atts.getURI(i));
                if(prx!=null && prx.length()>0) sb.append(prx).append(":");
                sb.append(atts.getLocalName(i)).append("=\"").append(escapeText(atts.getValue(i))).append("\"");
            }
        }
        for(String prfx:prefixesToDeclare) {
            sb.append(" xmlns");
            if(prfx!=null && prfx.length()>0) sb.append(":").append(prfx);
            sb.append("=\"").append(ns.getNamespaceURI(prfx)).append("\" ");
        }
        prefixesToDeclare.clear();
        output(sb.toString());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        boolean tagStatut = tagStatus.pop().booleanValue();
        if(tagStatut==CLOSED) output("/>\n");
        else {
            StringBuilder sb = new StringBuilder();
            if(prettyPrint) sb.append("\n").append(DEF_INDENT_BUFFER.substring(0, indentLevel));
            sb.append("</");
            String px = uri==null ? null : ns.getPrefix(uri);
            if(px!=null && px.length()>0) sb.append(px).append(":");
            String shortName;
            String sTmp = localName==null ? qName : localName;
            if(sTmp.indexOf(":")>0) shortName=sTmp.substring(sTmp.lastIndexOf(":")+1);
            else shortName=sTmp;
            sb.append(shortName).append(">");
            output(sb.toString());
        }
        indentLevel--;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(tagStatus.peek().booleanValue()==CLOSED) output(">");
        tagStatus.peek().setValue(OPENNED);
        output(escapeText(new String(ch,start,length)));
    }
    public void addCData(char[] ch, int start, int length) throws SAXException {
        addCData(new String(ch,start,length));
    }
    public void addCData(String s) throws SAXException {
        if(tagStatus.peek().booleanValue()==CLOSED) output(">");
        tagStatus.peek().setValue(OPENNED);
        output("<![CDATA[");
        output(s);
        output("]]>");
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        if(!"javax.xml.transform.disable-output-escaping".equals(target)) {
            StringBuilder sb = new StringBuilder();
            if(prettyPrint) sb.append("\n").append(DEF_INDENT_BUFFER.substring(0, indentLevel));
            sb.append("<?").append(target);
            if(data!=null && data.length()>0) sb.append(" ").append(data);
            sb.append("?>");
            output(sb.toString());
        }
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE ").append(name);
        if ( publicId != null && publicId.length()>0) {
            sb.append(" PUBLIC ").append(publicId);
            if ( systemId != null ) {
            sb.append(" ").append(systemId);
            }
        } else if ( systemId != null ) {
            sb.append(" SYSTEM ").append(systemId);
        }

        sb.append(">\n");

        logger.debug("notationDecl("+name+","+publicId+","+systemId+")");
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE ").append(name);
        if ( publicId != null && publicId.length()>0) {
            sb.append(" PUBLIC ").append(publicId);
            if ( systemId != null ) {
            sb.append(" ").append(systemId);
            }
        } else if ( systemId != null ) {
            sb.append(" SYSTEM ").append(systemId);
        }

        
        if ( notationName != null ) {
            sb.append(" [").append(notationName).append("]");
        }
        sb.append(">\n");
    }

    public static String escapeText(String text) {
        StringBuilder sb = new StringBuilder();
        if (text != null) {
            char[] value = text.toCharArray();
            String outval = null;
            int start = 0;
            int len = 0;
            for (int i = 0; i < value.length; i++) {
                char c = value[i];
                switch (c) {
                    case '\"':
                        outval = "&quot;";
                        break;

                    case '<':
                        outval = "&lt;";
                        break;

                    case '>':
                        outval = "&gt;";
                        break;

                    case '&':
                        outval = "&amp;";
                        break;

                    case '\'':
                        outval = "&apos;";
                        break;

                    case ' ':
                    case '\n':
                    case '\r':
                    case '\t':
                        len++;
                        continue;

                    default:
                        len++;
                        continue;
                }

                if (outval != null) {
                    if (len > 0) {
                        String s = new String(value, start, len);
                        sb.append(new String(value, start, len));
                    }
                    sb.append(outval);
                    start = i + 1;
                    len = 0;
                    outval = null;
                }
            }
            if (len > 0) {
                sb.append(new String(value, start, len));
            }
        }
        return sb.toString();
        
    }
    public void writeEscapedText(String text) {
        output(escapeText(text));
    }

    public OutputStream getOutput() { return out; }
    public NamespaceContextImpl getnsContext() { return ns; }
    public void setPrettyPrint (boolean prettyPrint) { this.prettyPrint=prettyPrint; }
    public static String unescapeText(String s) {
        return s.replaceAll("&lt;", "<").replace("&gt;", ">").replaceAll("&amp;", "&");
    }
}
