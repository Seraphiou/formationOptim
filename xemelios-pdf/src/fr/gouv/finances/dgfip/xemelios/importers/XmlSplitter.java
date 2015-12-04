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
package fr.gouv.finances.dgfip.xemelios.importers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.gouv.finances.dgfip.utils.NullWriter;
import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.utils.StringOutputStream;
import fr.gouv.finances.dgfip.utils.xml.NamespaceContextImpl;
import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.EtatModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ParentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.SpecialKeyModel;
import fr.gouv.finances.dgfip.xemelios.utils.XmlUtils;
import java.io.PrintWriter;
import javax.xml.namespace.QName;

/**
 * This class splits a file according to its "importable element"'s definition.
 * It also calculates special keys, budget and colletivites if it cans. 
 * This class is a spin-off from pre-3.0 DefaultImporteur.
 * 
 * @since 3.0
 * @author chm
 */
public class XmlSplitter extends DefaultHandler {
    protected static final int MAX_TEMP_FILE_PER_DIRECTORY = 30000;
    
    private static Logger logger = Logger.getLogger(XmlSplitter.class);
    private static final Pair NULL_VALUE = new Pair("null-value", "null-value");
    private byte[] LINE_SEP = System.getProperty("line.separator").getBytes();
    int elementDepth = 0;
    private DocumentModel dm = null;
    private OutputStream header = null,  data = null,  ref = null,  footer = null,  out = null;
    private Vector<File> fDatas = new Vector<File>();
    private int elementCount = 0,  startIgnoredLevel = 0;
    private File tmpDir;
    private Stack<QName> tagStack = new Stack<QName>();
    private Stack<StringBuilder> charactersStack = new Stack<StringBuilder>();
    private boolean inRef = false;
    private StringOutputStream etatHeader = null;
    private EtatModel currentEtat;
    private String startIgnored;
    private FileInfo fileInfo;
    private Locator locator;
//    private Connection currentConnection;
    private Pair budget = null;
    private Collectivite collectivite = null;
    private Pair referenceNomenclature = null;
    private Pair specialKey1 = null,  specialKey2 = null,  specialKey3 = null;
    private String fileEncoding = null;
    private NamespaceContextImpl nsCtx;
    Pair[] parentColl;
    
    private int tmpFileCount = 3;

    // pour les logs
    private PrintWriter importTimingPw;
    private String importedFileName;
    private long startSmallFile;
    private String currentFileName;

    public XmlSplitter(OutputStream header, OutputStream ref, OutputStream footer, File tmpDir, DocumentModel dm, String fileEncoding) {
        super();
        this.header = header;
        this.data = null;
        this.ref = ref;
        this.footer = footer;
        this.tmpDir = tmpDir;
        this.dm = dm;
        this.fileInfo = new FileInfo();
        this.fileEncoding = fileEncoding;
        nsCtx = new NamespaceContextImpl();
        if (footer == null) {
            logger.error("XmlSplitter is created with a null footer");
        }
        tmpFileCount = tmpDir.listFiles().length;
        parentColl = new Pair[10];
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public Locator getLocator() {
        return locator;
    }

    public Vector<File> getDataFiles() {
        return fDatas;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            String s = new String(ch, start, length).trim();
            if (s.length() > 0) {
                if (out != null) {
                    //String s2 = StringEscapeUtils.escapeXml(s);
                    String s2 = s.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                    out.write(s2.getBytes(fileEncoding));
                }
                charactersStack.peek().append(new String(ch, start, length));
            }
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        out = null;
        try {
            header.flush();
            header.close();
            ref.flush();
            ref.close();
            footer.flush();
            footer.close();
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        //logger.debug("</"+localName+">");
    	try {
            if (tagStack.size() == 1) {
                // PATCH tout pourri a totof
                out = footer;
            }            
            String tagName = XmlUtils.getShortTagName(uri, localName, qName);
            closeTag(uri, localName, qName);
            switch (tagStack.size()) {
                case 1: {
                    // fermeture du tag racine
                    if (!tagName.equals(dm.getBalise())) {
                        throw new SAXException("Fermeture d'un element root inattendu");
                    }
                    break;
                }
                case 2: {
                    // fermeture d'un referentiel, d'un etat ou d'un ignore
                    if (inRef || (startIgnoredLevel == 2 && startIgnored.equals(tagName))) {
                        // ignore
                        if (out != ref) {
                            out.flush();
                            out.close();
                        }
                        // par defaut, le prochain c'est le footer
                        out = footer;
                        if (footer == null) {
                            logger.error("footer is null !");
                        }
                        startIgnoredLevel = 0;
                        if (inRef) {
                            inRef = false;
                        }
                    } else if (currentEtat != null && currentEtat.getBalise().equals(tagName)) {
                        // fermeture de l'etat
                        if (data != null) {
                            closeData(uri, localName, qName);
                        }
                        out = footer;
                        if (footer == null) {
                            logger.error("footer is null !");
                        }
                        currentEtat = null;
                    // AJOUT ICI
//                        if (currentConnection != null) {
//                            try {
//                                currentConnection.commit();
//                                currentConnection.close();
//                                currentConnection = null;
//                            }
//                            catch (SQLException sqlEx) {}
//                        }
                    // FIN AJOUT
                    } else if (dm.getEntetes().contains(tagName)) {
                        out.flush();
                        out = header;
                    }
                    break;
                }
                case 3: {
                    if (currentEtat != null && currentEtat.getEntetes().contains(tagName)) {
                        // fermeture d'un entete d'etat
                        out.flush(); // pour etre sur
                        out = new NullWriter(); // par defaut, il doit y avoir
                    // des elements a venir.
                    // Mais si ce n'est pas le cas, on ne doit jamais fermer
                    // le tag d'etat (c'est fait dans closeData()
                    } else if (currentEtat != null && currentEtat.getImportableElement().getBalise().equals(tagName)) {
                        elementCount++;
                        if (elementCount == 1) {	// limit is always 1 now
                            closeData(uri, localName, qName);
                        }
                        out = new NullWriter(); // cf ci-dessus
                    } else if (currentEtat != null && startIgnoredLevel == 3 && startIgnored.equals(tagName)) {
                        out.close();
                        out = new NullWriter(); // cf vous savez ou
                    }
                    break;
                }
            }
            String path = XmlUtils.getNoNamespacePath(tagStack,dm.getNamespaces())+"/text()";
            String innerData = charactersStack.pop().toString();
            checkBudgetCollectivite(path, innerData);
            checkSpecialKey(path, innerData);
            checkReferenceNomenclature(path, innerData);
            tagStack.pop();
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            String tagName = XmlUtils.getShortTagName(uri, localName, qName);
            QName _qName = XmlUtils.getQName(uri, localName, qName);
            //logger.debug("<"+tagName+">");
            switch (tagStack.size()) {
                case 0: {
                    // si ce n'est pas le tag du doc que l'on importe, bye
                    if (!tagName.equals(dm.getBalise())) {
                        throw new SAXException("Le fichier importé n'est pas un " + dm.getTitre());
                    }
                    // sinon, on ecrit dans l'entete du doc
                    out = header;
//                    logger.debug(tagName+"\tdocument tag");
                    break;
                }
                case 1: {
                    if (dm.getEntetes().contains(tagName)) {
                        // c'est un tag d'entete de document
                        out = header;
//                        EnteteModel em = dm.getMap().get(tagName);
//                        if (em != null && em.getCopies().size() > 0) {
//                            StringBuffer buff = new StringBuffer();
//                            buff.append(new String(getXmlText(uri, localName, qName, attributes))).append(new String(getCloseTag(uri, localName, qName)));
//                            em.setData(buff.toString());
//                        }
//                        logger.debug(tagName+"\tdocument entete");
                    } else if (dm.getReferentiel().equals(tagName)) {
                        // c'est le referentiel
                        out = ref;
                        inRef = true;
//                        logger.debug(tagName+"\tdocument referentiel");
                    } else {
                        // demarre-t-on un nouvel etat ?
                        EtatModel em = dm.getEtatByTagName(tagName);
                        if (em != null) {
                            // oui !
                            currentEtat = em;
                            etatHeader = new StringOutputStream();
                            out = etatHeader;
                        } else {
                            // non, on ignore
                            out = new NullWriter();
                            startIgnored = tagName;
                            startIgnoredLevel = tagStack.size() + 1;
//                            logger.debug(tagName+"\tignored etat");
                        }
                    }
                    break;
                }
                case 2: {
                    if (currentEtat != null && currentEtat.getEntetes().contains(tagName)) {
                        out = etatHeader;
//                        logger.debug(tagName+"\tetat entete");
                    } else if (currentEtat != null && currentEtat.getImportableElement().getBalise().equals(tagName)) {
                        // on commence un nouvel element
                        if (data == null) {
                            startData();
                        }
                        out = data;
                        fileInfo.incElement(currentEtat.getMarker());
//                        logger.debug(tagName+"\timportable element");
                    } else if (currentEtat != null) {
                        // element d'un etat que l'on doit ignorer
                        out = new NullWriter();
                        startIgnored = tagName;
                        startIgnoredLevel = tagStack.size() + 1;
//                        logger.debug(tagName+"\tignored element");
                    }
                    break;
                }
            }
            tagStack.push(_qName);
            charactersStack.push(new StringBuilder());

            String path = XmlUtils.getNoNamespacePath(tagStack,dm.getNamespaces());
            for (int i = 0; i < attributes.getLength(); i++) {
                String attrName = attributes.getLocalName(i);
                String value = attributes.getValue(i);
                String fullPath = path + "/@" + attrName;
                checkBudgetCollectivite(fullPath, value);
                checkSpecialKey(fullPath, value);
                checkReferenceNomenclature(fullPath, value);
            }

            writeTag(uri, localName, qName, attributes);
        } catch (IOException ioEx) {
            throw new SAXException(ioEx);
        }
    }

    private void closeData(String uri, String localName, String qName) throws IOException {
        data.write("</".getBytes());
        String prefix = null;
        if (uri != null && uri.length() > 0) {
            prefix = nsCtx.getPrefix(uri);
            if (prefix != null && prefix.length() > 0) {
                prefix = prefix + ":";
            } else {
                prefix = "";
            }
        } else {
            prefix = "";
        }
        QName thisQName = tagStack.pop();
        QName parentQName = tagStack.peek();
        tagStack.push(thisQName);
        if(parentQName.getPrefix()!=null && parentQName.getPrefix().length()>0) {
            data.write(parentQName.getPrefix().getBytes(fileEncoding));
        } else {
            data.write(parentQName.getLocalPart().getBytes(fileEncoding));
        }
        data.write(">".getBytes());
        data.flush();
        data.close();
        data = null;
        importTimingPw.append(importedFileName).append(";").append(currentFileName).append(";DEC;").append(Long.toString(startSmallFile)).append(";").append(Long.toString(System.currentTimeMillis())).append("\n");
    }

    private OutputStream startData() throws IOException {
        startSmallFile = System.currentTimeMillis();
        if(tmpFileCount>=MAX_TEMP_FILE_PER_DIRECTORY) {
            tmpDir = new File(tmpDir,"tmp1");
            if(!tmpDir.exists()) tmpDir.mkdirs();
            tmpFileCount=0;
        }
        File f = File.createTempFile("data-", "", tmpDir);
        currentFileName = f.toURI().toURL().toExternalForm();
        tmpFileCount++;
        data = new FileOutputStream(f);
        elementCount = 0;
        fDatas.add(f);
        try {
            data.write(etatHeader.getBytes(fileEncoding));
        } catch (UnsupportedEncodingException ueEx) {
            logger.error("startData()", ueEx);
        }
        return data;
    }

    private void writeTag(String uri, String localName, String qName, Attributes attributes) throws IOException {
        out.write(LINE_SEP);
        out.write(getXmlText(uri, localName, qName, attributes));
    }

    private void closeTag(String uri, String localName, String qName) throws IOException {
        if (out == null) {
            logger.error("trying to close tag " + qName + " with a null output");
        }
        out.write(getCloseTag(uri, localName, qName));
    }

    private byte[] getCloseTag(String uri, String localName, String qName) throws UnsupportedEncodingException {
        StringBuffer buff = new StringBuffer();
        buff.append("</").append(qName).append(">");
        return buff.toString().getBytes(fileEncoding);
    }

    private byte[] getXmlText(String uri, String localName, String qName, Attributes attributes) throws UnsupportedEncodingException {
        StringBuffer ret = new StringBuffer();
        ret.append("<").append(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            String attName = attributes.getLocalName(i);
            String attVal = attributes.getValue(i);
            String nsUri = attributes.getURI(i);
            String prefix = null;
            if (nsUri != null && nsUri.length() > 0) {
                prefix = nsCtx.getPrefix(nsUri);
                if (prefix != null && prefix.length() > 0) {
                    prefix = prefix + ":";
                } else {
                    prefix = "";
                }
            } else {
                prefix = "";
            }
            ret.append(" ").append(prefix).append(attName).append("=\"").append(escapeXml(attVal)).append("\"");
        }
        for (NamespaceContextImpl.NsMapping map : nsCtx.getMappings()) {
            if (!map.isWritten()) {
                ret.append(" xmlns");
                if (map.getPrefix().length() > 0) {
                    ret.append(":").append(map.getPrefix());
                }
                ret.append("=\"").append(map.getUri()).append("\"");
                map.setWritten();
            }
        }
        ret.append(">");
        return ret.toString().getBytes(fileEncoding);
    }

    /**
     * Return informations on splitted file
     * @return
     */
    public FileInfo getFileInfo() {
        return fileInfo;
    }

    private void checkBudgetCollectivite(String path, String value) {
        if (dm.getBudgetPath() != null) {
            if (dm.getBudgetPath().getCodePath() != null && (path.equals(dm.getBudgetPath().getCodePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getBudgetPath().getCodePath().getPath())))) {
                if (budget == null) {
                    budget = new Pair();
                }
                budget.key = value;
            }
            if (dm.getBudgetPath().getLibellePath() != null && (path.equals(dm.getBudgetPath().getLibellePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getBudgetPath().getLibellePath().getPath())))) {
                if (budget == null) {
                    budget = new Pair();
                }
                budget.libelle = value;
            }
        }
        if (dm.getCollectivitePath() != null) {
            if (dm.getCollectivitePath().getCodePath() != null && (path.equals(dm.getCollectivitePath().getCodePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getCollectivitePath().getCodePath().getPath())))) {
                if (collectivite == null) {
                    collectivite = new Collectivite();
                }
                collectivite.key = value;
            }
            if (dm.getCollectivitePath().getLibellePath() != null && (path.equals(dm.getCollectivitePath().getLibellePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getCollectivitePath().getLibellePath().getPath())))) {
                if (collectivite == null) {
                    collectivite = new Collectivite();
                }
                collectivite.libelle = value;
            }
        }
        if(dm.getCollectivitePath().getParentsCount()>0) {
            for(int i=0;i<dm.getCollectivitePath().getParentsCount();i++) {
                ParentModel pm = dm.getCollectivitePath().getParent(i);
                if(pm.getCodePath()!=null && path.equals(pm.getCodePath().getPath())) {
                    if(parentColl[pm.getLevel()-1]==null) {
                        parentColl[pm.getLevel()-1]=new Pair();
                    }
                    parentColl[pm.getLevel()-1].key=value;
                }
                if(pm.getLibellePath()!=null && path.equals(pm.getLibellePath().getPath())) {
                    if(parentColl[pm.getLevel()-1]==null) {
                        parentColl[pm.getLevel()-1]=new Pair();
                    }
                    parentColl[pm.getLevel()-1].libelle=value;
                }
            }
        }
    }

    private void checkSpecialKey(String path, String value) {
        Vector<SpecialKeyModel> spDefs = dm.getSpecialKeys();
        if (specialKey1 == null && spDefs != null && spDefs.size() > 0) {
            SpecialKeyModel skm = null;
            for (int i = 0; i < spDefs.size(); i++) {
                SpecialKeyModel o = spDefs.get(i);
                if (o.getPos() == 1) {
                    skm = o;
                    break;
                }
            }
            if (skm == null) {
                // it can not have anymore special-key-1
                specialKey1 = NULL_VALUE;
            }
            if (skm != null && path.equals(skm.getPath())) {
                String key = skm.transformValue(value);
                String lib = skm.getDescriptionOfValue(key);
                if (lib == null) {
                    lib = key;
                }
                specialKey1 = new Pair(key, lib);
            }
        }
        if (specialKey2 == null && spDefs != null && spDefs.size() > 0) {
            SpecialKeyModel skm = null;
            for (int i = 0; i < spDefs.size(); i++) {
                SpecialKeyModel o = spDefs.get(i);
                if (o.getPos() == 2) {
                    skm = o;
                    break;
                }
            }
            if (skm == null) {
                // it can not have anymore special-key-1
                specialKey2 = NULL_VALUE;
            }
            if (skm != null && path.equals(skm.getPath())) {
                String key = skm.transformValue(value);
                String lib = skm.getDescriptionOfValue(key);
                if (lib == null) {
                    lib = key;
                }
                specialKey2 = new Pair(key, lib);
            }
        }
        if (specialKey3 == null && spDefs != null && spDefs.size() > 0) {
            SpecialKeyModel skm = null;
            for (int i = 0; i < spDefs.size(); i++) {
                SpecialKeyModel o = spDefs.get(i);
                if (o.getPos() == 3) {
                    skm = o;
                    break;
                }
            }
            if (skm == null) {
                // it can not have anymore special-key-1
                specialKey3 = NULL_VALUE;
            }
            if (skm != null && path.equals(skm.getPath())) {
                String key = skm.transformValue(value);
                String lib = skm.getDescriptionOfValue(key);
                if (lib == null) {
                    lib = key;
                }
                specialKey3 = new Pair(key, lib);
            }
        }
    }
    private void checkReferenceNomenclature(String path, String value) {
        if (dm.getReferenceNomenclaturePath() != null) {
            if (dm.getReferenceNomenclaturePath().getCodePath() != null) {
                if(path.equals(dm.getReferenceNomenclaturePath().getCodePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getReferenceNomenclaturePath().getCodePath().getPath()))) {
                    if (referenceNomenclature == null) {
                    	referenceNomenclature = new Pair(null,null);
                    }
                    referenceNomenclature.key = value;
//                    return;
                }
            }
            if (dm.getReferenceNomenclaturePath().getLibellePath() != null) {
                if (path.equals(dm.getReferenceNomenclaturePath().getLibellePath().getPath()) || path.equals(XmlUtils.removePrefixNS(dm.getReferenceNomenclaturePath().getLibellePath().getPath()))) {
                    if (referenceNomenclature == null) {
                    	referenceNomenclature = new Pair(null,null);
                    }
                    referenceNomenclature.libelle = value;
//                    return;
                }
            }
        }
    }

    /**
     * Returns the budget the splitted file concerns
     * @return
     */
    public Pair getBudget() {
        return budget;
    }

    /**
     * Returns the collectivite the splitted file concerns
     * @return
     */
    Collectivite getCollectivite() {
        return collectivite;
    }
    public Pair[] getParentcollectivites() {
        return parentColl;
    }
    /**
     * Returns the ReferenceNomanclature the splitted file concerns
     * @return
     */
    public Pair getReferenceNomenclature() {
        return referenceNomenclature;
    }

    /**
     * Returns the special key1 found in this file.
     * @return
     */
    public Pair getSpecialKey1() {
        return specialKey1 == NULL_VALUE ? null : specialKey1;
    }

    /**
     * Returns the special key2 found in this file
     * @return
     */
    public Pair getSpecialKey2() {
        return specialKey2 == NULL_VALUE ? null : specialKey2;
    }

    /**
     * Returns the special key3 found in this file
     * @return
     */
    public Pair getSpecialKey3() {
        return specialKey3 == NULL_VALUE ? null : specialKey3;
    }

    public static String escapeXml(String s) {
        return s.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'", "&apos;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
        nsCtx.removeMapping(prefix);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
        nsCtx.addMapping(prefix, uri);
    }

    public void setImportLogPrintWriter(PrintWriter pw) {
        this.importTimingPw = pw;
    }
    public void setSplittedFileName(String fileName) {
        this.importedFileName = fileName;
    }
}
