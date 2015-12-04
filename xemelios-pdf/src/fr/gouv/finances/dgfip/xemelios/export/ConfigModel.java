/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 N.Le Corre - nicolas.lecorre@axyus.com
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

package fr.gouv.finances.dgfip.xemelios.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;

import fr.gouv.finances.dgfip.utils.Base64;

import javax.xml.namespace.QName;

public class ConfigModel implements XmlMarshallable {
	public static String TAG = "ConfigModel"; 
        public static final transient QName QN = new QName(TAG);
	private Logger logger = Logger.getLogger(ConfigModel.class);
	
	private String id, docID, etatID, owner, dateCreation, code, libelle;
	private byte[] configLines;
        private StringBuffer base64Buffer = new StringBuffer();
	
	public ConfigModel (QName tagName) {
		//configLines = new StringBuffer();
            super();
	}
	/**
         * Crée un nouveau ConfigModel
         * @param rs
         * @param dateFormat
         * @throws SQLException
         */
	public ConfigModel (ResultSet rs, String dateFormat) throws SQLException {
		//configLines = new StringBuffer();
		int i=1;
		setId(rs.getString(i++));
		setDocID(rs.getString(i++));
		setEtatID(rs.getString(i++));
		setOwner(rs.getString(i++));
		Date dTmp = rs.getDate(i++);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		setDateCreation(sdf.format(dTmp));
		setCode(rs.getString(i++));
		setLibelle(rs.getString(i++));

		try {
	        byte[] buff = new byte[512];
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        InputStream is = rs.getBinaryStream(i++);
	        int length=is.read(buff);
	        while(length>0) {
	            baos.write(buff,0,length);
	            length=is.read(buff);
	        }
	        setConfigLines(baos.toByteArray());
		} catch (IOException ioe) {
			logger.error("Erreur lors de la lecture de la configuration", ioe);
		}
		//setConfigLines(rs.getString(i++));
	}

    @Override
	public void addCharacterData(String cData) throws SAXException {
		base64Buffer.append(cData);
	}

    @Override
	public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
	}

    @Override
	public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
		id = (attributes.getValue("id")!=null?attributes.getValue("id"):id);
		docID = (attributes.getValue("docID")!=null?attributes.getValue("docID"):docID);
		etatID = (attributes.getValue("etatID")!=null?attributes.getValue("etatID"):etatID);
		owner = (attributes.getValue("owner")!=null?attributes.getValue("owner"):owner);
		dateCreation = (attributes.getValue("dateCreation")!=null?attributes.getValue("dateCreation"):dateCreation);
		code = (attributes.getValue("code")!=null?attributes.getValue("code"):code);
		libelle = (attributes.getValue("libelle")!=null?attributes.getValue("libelle"):libelle);
		return this;
	}

    @Override
	public void marshall(XmlOutputter output) {
		output.startTag(TAG);
		output.addAttribute("id", id);
		output.addAttribute("docID", docID);
		output.addAttribute("etatID", etatID);
		output.addAttribute("owner", owner);
		output.addAttribute("dateCreation", dateCreation);
		output.addAttribute("code", code);
		output.addAttribute("libelle", libelle);
		output.addCharacterData(Base64.encodeBytes(configLines));
		output.endTag(TAG);
	}
	
	@Override
	public Object clone() {
		ConfigModel cm = new ConfigModel(QN);
		cm.setId(this.getId());
		cm.setDocID(this.getDocID());
		cm.setEtatID(this.getEtatID());
		cm.setOwner(this.getOwner());
		cm.setDateCreation(this.getDateCreation());
		cm.setCode(this.getCode());
		cm.setLibelle(this.getLibelle());
		cm.setConfigLines(this.getConfigLines());
		
		return cm;
	}

    @Override
	public void validate() throws InvalidXmlDefinition {
	}

	public byte[] getConfigLines() {
            if(configLines==null && base64Buffer.length()>0) {
                configLines = Base64.decode(base64Buffer.toString());
                base64Buffer = new StringBuffer();
            }
		return configLines;
	}

	public void setConfigLines(byte[] buffer) {
//		this.configLines.setLength(0);
//		this.configLines.append(configLines);
            configLines = buffer;
	}

	public String getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getDocID() {
		return docID;
	}

	public void setDocID(String docID) {
		this.docID = docID;
	}

	public String getEtatID() {
		return etatID;
	}

	public void setEtatID(String etatID) {
		this.etatID = etatID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
	
	public ExportModel getExportModel () {
		ExportModel ret = null;
    	try {
    		ConfigParser cp = new ConfigParser();
    		cp.parse(new ByteArrayInputStream (getConfigLines()));
    		ret = (ExportModel)cp.getMarshallable();
    	} catch (Exception e) {
    		logger.error("Erreur lors de la lecture de la configuration à modifier", e);
    	}
    	return ret;
	}

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

}
