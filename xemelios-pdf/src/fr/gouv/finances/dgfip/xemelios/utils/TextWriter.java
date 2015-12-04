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
package fr.gouv.finances.dgfip.xemelios.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * TextWriter takes a Document, DocumentFragment, or Element and streams it
 * as text or HTML to an output source (or a String)
 *
 */

public final class TextWriter {
   private static final String DefaultIndentBuffer;
   private String defaultEncoding = "ISO-8859-1";
   static {
      StringBuffer sb = new StringBuffer(256);
      for ( int i = 0; i < 256; i++ )
         sb.append(' ');
      DefaultIndentBuffer = sb.toString();
   }

   private Node node;
   private boolean produceHTML;
   private boolean prettyPrint;
   private int indentSize = 4;
   private int indentLevel = 0;
   private String indentTemplate = DefaultIndentBuffer;
   private StringBuffer textBuffer = new StringBuffer();

   public TextWriter(Node node, boolean produceHTML, boolean prettyPrint, int indentSize) {
      this.node = node;
      this.produceHTML = produceHTML;
      this.prettyPrint = prettyPrint;
      this.indentSize = indentSize;
   }

   public TextWriter(Node node, boolean produceHTML, boolean prettyPrint) {
      this.node = node;
      this.produceHTML = produceHTML;
      this.prettyPrint = prettyPrint;
   }

   public TextWriter(Node node, boolean produceHTML) {
      this.node = node;
      this.produceHTML = produceHTML;
   }

   public TextWriter(Node node) {
      this.node = node;
   }
   public TextWriter(Node node,String encoding) {
       this.node=node;
       if(encoding!=null)
           this.defaultEncoding=encoding;
   }

   public boolean isProduceHTML() {
      return produceHTML;
   }

   public void setProduceHTML(boolean produceHTML) {
      this.produceHTML = produceHTML;
   }

   public boolean isPrettyPrint() {
      return prettyPrint;
   }

   public void setPrettyPrint(boolean prettyPrint) {
      this.prettyPrint = prettyPrint;
   }

   private void writeNode(Writer writer, Node node) throws IOException {
      short type = node.getNodeType();
      switch ( type ) {

         case Node.DOCUMENT_NODE:
            document(writer, (Document)node);
            break;

         case Node.DOCUMENT_FRAGMENT_NODE:
            documentFragment(writer, (DocumentFragment)node);
            break;

         case Node.DOCUMENT_TYPE_NODE:
            documentType(writer, (DocumentType)node);
            break;

         case Node.ELEMENT_NODE:
            element(writer, (Element)node);
            break;

         case Node.ATTRIBUTE_NODE:
            attribute(writer, (Attr)node);
            break;

         case Node.ENTITY_REFERENCE_NODE:
            entityReference(writer, (EntityReference)node);
            break;

         case Node.ENTITY_NODE:
            entity(writer, (Entity)node);
            break;

         case Node.NOTATION_NODE:
            notation(writer, (Notation)node);
            break;

         case Node.PROCESSING_INSTRUCTION_NODE:
            procInst(writer, (ProcessingInstruction)node);
            break;

         case Node.TEXT_NODE:
            text(writer, (Text)node);
            break;

         case Node.CDATA_SECTION_NODE:
            cDataSection(writer, (CDATASection)node);
            break;

         case Node.COMMENT_NODE:
            comment(writer, (Comment)node);
            break;
      }
   }

   private String getChildText(Node node) {
      StringBuffer sb = new StringBuffer();
      NodeList l = node.getChildNodes();
      int size = l.getLength();
      for ( int i = 0; i < size; i++ ) {
         Node n = l.item(i);
         if ( n.getNodeType() == Node.TEXT_NODE ) {
            Text t = (Text)n;
            sb.append(t.getData());
         }
         else
            return null;
      }
      return sb.toString();
   }

   private void writeChildren(Writer writer, Node node) throws IOException {
      NodeList l = node.getChildNodes();
      int size = l.getLength();
      for ( int i = 0; i < size; i++ ) {
         Node n = l.item(i);
         writeNode(writer, n);
      }
   }


   private void document(Writer writer, Document doc) throws IOException {
      if ( !produceHTML ) {
          writer.write("<?xml version=\"1.0\" encoding=\"");
          writer.write(defaultEncoding);
          writer.write("\"?>\n");
      }
      writeChildren(writer, doc);
   }

   private void documentFragment(Writer writer, DocumentFragment fragment) throws IOException {
      writeChildren(writer, fragment);
   }

   private void documentType(Writer writer, DocumentType docType) throws IOException {
      writer.write("<!DOCTYPE ");
      writer.write(docType.getName());
      String pubID = docType.getPublicId();
      String sysID = docType.getSystemId();
      if ( pubID != null ) {
         writer.write(" PUBLIC ");
         writer.write(pubID);
         if ( sysID != null ) {
            writer.write(' ');
            writer.write(sysID);
         }
      }
      else if ( sysID != null ) {
         writer.write(" SYSTEM ");
         writer.write(sysID);
      }

      String is = docType.getInternalSubset();
      if ( is != null ) {
         writer.write(" [");
         writer.write(is);
         writer.write("]");
      }
      writer.write(">\n");
   }

   private void element(Writer writer, Element elem) throws IOException {
      if ( prettyPrint ) {
         checkTextBuffer(writer);
         indent(writer);
      }

      String n = elem.getTagName();
      writer.write('<');
      writeText(writer, n);

      NamedNodeMap a = elem.getAttributes();
      int size = a.getLength();
      for ( int i = 0; i < size; i++ ) {
         Attr att = (Attr)a.item(i);
         writer.write(' ');
         writeNode(writer, att);
      }

      if ( elem.hasChildNodes() ) {
         writer.write('>');

         if ( prettyPrint ) {
            String text = getChildText(elem);
            if ( text != null )
               writeEscapedText(writer, normalizeString(text), false);
            else {
               writer.write('\n');
               indentLevel++;
               writeChildren(writer, elem);
               checkTextBuffer(writer);
               indentLevel--;
               indent(writer);
            }
         }
         else
            writeChildren(writer, elem);

         writer.write("</");
         writeText(writer, n);
         writer.write('>');
      }
      else {
         if ( produceHTML )
            writer.write(">");
         else
            writer.write("/>");
      }

      if ( prettyPrint )
         writer.write('\n');
   }

   private void attribute(Writer writer, Attr attr) throws IOException {
      writeText(writer, attr.getName());
      writer.write("=\"");
      //writeEscapedText(writer, attr.getValue(), true);
      writer.write(StringEscapeUtils.escapeXml(attr.getValue()));
      writer.write("\"");
   }

   private void entityReference(Writer writer, EntityReference entityRef) throws IOException {
   }

   private void entity(Writer writer, Entity entity) throws IOException {
   }

   private void notation(Writer writer, Notation notation) throws IOException {
      if ( prettyPrint ) {
         checkTextBuffer(writer);
         indent(writer);
      }

      writer.write("<!NOTATION ");
      writer.write(notation.getNodeName());
      String pubID = notation.getPublicId();
      String sysID = notation.getSystemId();
      if ( pubID != null ) {
         writer.write(" PUBLIC ");
         writer.write(pubID);
         if ( sysID != null ) {
            writer.write(' ');
            writer.write(sysID);
         }
      }
      else if ( sysID != null ) {
         writer.write(" SYSTEM ");
         writer.write(sysID);
      }
      writer.write(">\n");
   }

   private void procInst(Writer writer, ProcessingInstruction pi) throws IOException {
      if ( prettyPrint ) {
         checkTextBuffer(writer);
         indent(writer);
      }

      writer.write("<?");
      writeText(writer, pi.getTarget());
      writer.write(" ");
      writeText(writer, pi.getData());
      writer.write("?>\n");
   }

   private void text(Writer writer, Text text) throws IOException {
      if ( prettyPrint )
         textBuffer.append(text.getData());
      else
         writeEscapedText(writer, text.getData(), false);
   }

   private void cDataSection(Writer writer, CDATASection cdata) throws IOException {
      if ( prettyPrint ) {
         checkTextBuffer(writer);
         indent(writer);
      }

      writer.write("<![CDATA[");
      writer.write(cdata.getData());
      writer.write("]]>");

      if ( prettyPrint )
         writer.write('\n');
   }

   private void comment(Writer writer, Comment comment) throws IOException {
      if ( prettyPrint ) {
         checkTextBuffer(writer);
         indent(writer);
      }

      writer.write("<!--");
      writeText(writer, comment.getData());
      writer.write("-->");

      if ( prettyPrint )
         writer.write('\n');
   }

   private void writeText(Writer writer, String text) throws IOException {
      if ( text != null )
         writer.write(text);
   }

   private void writeEscapedText(Writer writer, String text, boolean attr) throws IOException {
      if ( text != null ) {
         char[] value = text.toCharArray();
         String outval = null;
         int start = 0;
         int len = 0;
         for ( int i = 0; i < value.length; i++ ) {
            char c = value[i];
            switch ( c ) {
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
                  if ( produceHTML && attr )
                     len++;
                  else
                     outval = "&amp;";
                  break;

               case '\'':
                  if ( produceHTML )
                     len++;
                  else
                     outval = "&apos;";
                  break;

               case ' ':
               case '\n':
               case '\r':
               case '\t':
                  len++;
                  continue;

               default:
//                  if ( c < ' ' || c == 127 ) {
//                     outval = "&#x" + Integer.toHexString(c) + ";";
//                     break;
//                  }
//                  else {
                     len++;
                     continue;
//                  }
            }

            if ( outval != null ) {
               if ( len > 0 ) {
            	   String s = new String(value,start,len);
                  writer.write(value, start, len);
               }
               writer.write(outval);
               start = i + 1;
               len = 0;
               outval = null;
            }
         }
         if ( len > 0 )
            writer.write(value, start, len);
      }
   }

   private void checkTextBuffer(Writer writer) throws IOException {
      if ( textBuffer.length() > 0 ) {
         String val = normalizeString(textBuffer.toString());
         textBuffer.setLength(0);
         if ( val.length() > 0 ) {
            indent(writer);
            writeEscapedText(writer, val, false);
            writer.write('\n');
         }
      }
   }

   private void indent(Writer writer) throws IOException {
      indent(writer, indentLevel);
   }

   private void indent(Writer writer, int level) throws IOException {
      int size = level * indentSize;
      if ( size > 0 ) {
         if ( indentTemplate.length() < size ) {
            StringBuffer sb = new StringBuffer(indentTemplate);
            for ( int i = indentTemplate.length(); i < size; i++ )
               sb.append(' ');
            indentTemplate = sb.toString();
         }
         writer.write(indentTemplate.substring(0, size));
      }
   }

   private String normalizeString(String value) {
      char[] c = value.toCharArray();
      char[] n = new char[c.length];
      boolean white = true;
      int pos = 0;
      for ( int i = 0; i < c.length; i++ ) {
         if ( " \t\n\r".indexOf(c[i]) != -1 ) {
            if ( !white ) {
               n[pos++] = ' ';
               white = true;
            }
         }
         else {
            n[pos++] = c[i];
            white = false;
         }
      }
      if ( white && pos > 0 )
         pos--;
      return new String(n, 0, pos);
   }

   /**
    * write writes the specified node to the writer as text.
    *
    * @param node The Node to write
    * @param writer The Writer to write to
    */
   public static void write(Node node, Writer writer) throws IOException {
      new TextWriter(node).write(writer);
   }

   /**
    * writeHTML writes the specified node to the writer as HTML.
    *
    * @param node The Node to write
    * @param writer The Writer to write to
    */
   public static void writeHTML(Node node, Writer writer) throws IOException {
      new TextWriter(node, true).write(writer);
   }

   /**
    * writePretty writes the specified node to the writer as pretty text.
    *
    * @param node The Node to write
    * @param writer The Writer to write to
    */
   public static void writePretty(Node node, Writer writer) throws IOException {
      new TextWriter(node, false, true).write(writer);
   }

   /**
    * writePrettyHTML writes the specified node to the writer as pretty HTML.
    *
    * @param node The Node to write
    * @param writer The Writer to write to
    */
   public static void writePrettyHTML(Node node, Writer writer) throws IOException {
      new TextWriter(node, true, true).write(writer);
   }

   /**
    * write writes the node to the writer as text.
    *
    * @param writer The Writer to write to
    */
   public void write(Writer writer) throws IOException {
      try {
         BufferedWriter buf = new BufferedWriter(writer, 4096);
         writeNode(buf, node);
         buf.flush();
      }
      catch ( Exception e ) {
         e.printStackTrace(System.err);
      }
   }

   /**
    * write writes the specified node to the OutputStream as text.
    *
    * @param node The Node to write
    * @param output The OutputStream to write to
    */
   public static void write(Node node, OutputStream output) throws IOException {
      new TextWriter(node).write(output);
   }

   /**
    * writeHTML writes the specified node to the OutputStream as HTML.
    *
    * @param node The Node to write
    * @param output The OutputStream to write to
    */
   public static void writeHTML(Node node, OutputStream output) throws IOException {
      new TextWriter(node, true).write(output);
   }

   /**
    * writePretty writes the specified node to the OutputStream as pretty text.
    *
    * @param node The Node to write
    * @param output The OutputStream to write to
    */
   public static void writePretty(Node node, OutputStream output) throws IOException {
      new TextWriter(node, false, true).write(output);
   }

   /**
    * writePrettyHTML writes the specified node to the OutputStream as pretty HTML.
    *
    * @param node The Node to write
    * @param output The OutputStream to write to
    */
   public static void writePrettyHTML(Node node, OutputStream output) throws IOException {
      new TextWriter(node, true, true).write(output);
   }

   /**
    * write writes the node to the OutputStream as text.
    *
    * @param output The OutputStream to write to
    */
   public void write(OutputStream output) throws IOException {
      try {
         OutputStreamWriter o = new OutputStreamWriter(output, "UTF8");
         BufferedWriter buf = new BufferedWriter(o, 4096);
         writeNode(buf, node);
         buf.flush();
      }
      catch ( Exception e ) {
         e.printStackTrace(System.err);
      }
   }

   /**
    * toString returns the node as a String.
    *
    * @param node The Node to convert
    * @return The String value
    */
   public static String toString(Node node) {
      return new TextWriter(node).toString();
   }
   public static String toString(Node node,String encoding) {
       return new TextWriter(node,encoding).toString();
    }

   /**
    * toHTMLString returns the node as a HTML String.
    *
    * @param node The Node to convert
    * @return The String value
    */
   public static String toHTMLString(Node node) {
      return new TextWriter(node, true).toString();
   }

   /**
    * toPrettyString returns the node as a Pretty String.
    *
    * @param node The Node to convert
    * @return The String value
    */
   public static String toPrettyString(Node node) {
      return new TextWriter(node, false, true).toString();
   }

   /**
    * toPrettyHTMLString returns the node as a Pretty HTML String.
    *
    * @param node The Node to convert
    * @return The String value
    */
   public static String toPrettyHTMLString(Node node) {
      return new TextWriter(node, true, true).toString();
   }

   /**
    * toString returns the node as a String.
    *
    * @return The String value
    */
   @Override
   public String toString() {
      StringWriter writer = new StringWriter();
      try {
         write(writer);
         return writer.toString();
      }
      catch ( Exception e ) {
         e.printStackTrace(System.err);
         return null;
      }
   }
}


