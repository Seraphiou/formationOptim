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
package fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

public class TSqlIndex implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(TSqlIndex.class);
    public static final String TAG = "sql-index";
    public static final transient QName QN = new QName(TAG);
    private SimpleDateFormat[] sdfs;
    private String id,  tableId,  path,  format,  datatype,  column;
    private boolean useInCriterias = true;
    private String resetOn;
    private boolean generate = true;

    public TSqlIndex(QName tagName) {
        super();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        tableId = attributes.getValue("table");
        path = attributes.getValue("path");
        format = attributes.getValue("format");
        datatype = attributes.getValue("datatype");
        column = attributes.getValue("column");
        String sTmp = attributes.getValue("use-in-criteria");
        if(sTmp!=null && sTmp.length()>0) {
            useInCriterias = "|TRUE|YES|1|".indexOf("|".concat(sTmp.toUpperCase()).concat("|"))>=0;
        }
        String sTmp2 = attributes.getValue("generate");
        if(sTmp2!=null && sTmp2.length()>0) {
            generate = "|TRUE|YES|1|".indexOf("|".concat(sTmp2.toUpperCase()).concat("|"))>=0;
        }
        resetOn = attributes.getValue("reset-on");
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TSqlIndex clone() {
        TSqlIndex other = new TSqlIndex(QN);
        other.column = this.column;
        other.datatype = this.datatype;
        other.format = this.format;
        other.id = this.id;
        other.path = this.path;
        other.tableId = this.tableId;
        other.useInCriterias=this.useInCriterias;
        other.generate=generate;
        other.resetOn=this.resetOn;
        return other;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getFormattedStringValue(String value) {
        if (format != null) {
            if ("uppercase".equals(format)) {
                return value.toUpperCase();
            } else if ("lowercase".equals(format)) {
                return value.toLowerCase();
            } else if ("normalize-month".equals(format)) {
                return value.length() == 1 ? "0" + value : value;
            }
        }
        return value;
    }

    public Date getDateValue(String value) throws ParseException {
        if (!"date".equals(datatype)) {
            return null;
        }
        if (sdfs == null) {
            if(format==null) {
                logger.error("format is null for sql-index "+id);
            }
            StringTokenizer tokenizer = new StringTokenizer(format, ";");
            sdfs = new SimpleDateFormat[tokenizer.countTokens()];
            for (int count = 0; count < sdfs.length; count++) {
                String s = tokenizer.nextToken();
                sdfs[count] = new SimpleDateFormat(s);
            }
        }
        Date ret = null;
        int count = 0;
        while (ret == null && count < sdfs.length) {
            try {
                ret = new Date(sdfs[count].parse(value).getTime());
            } catch (ParseException pEx) {
                count++;
            }
        }
        if (ret == null) {
            throw new ParseException(value + " does not match any of following date patterns : " + format+" - "+tableId+"."+column, 0);
        }
        return ret;
    }
    
    public String getBooleanValue(String value) {
        if(!"boolean".equals(datatype)) return null;
        if("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value)) return "1";
        return "0";
    }

    @Override
    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    @Override
    public QName getQName() {
        return QN;
    }

    public boolean isUseInCriterias() {
        return useInCriterias;
    }

    public void setUseInCriterias(boolean useInCriterias) {
        this.useInCriterias = useInCriterias;
    }

    public String getResetOn() {
        return resetOn;
    }

    public void setResetOn(String resetOn) {
        this.resetOn = resetOn;
    }
    public boolean isGenerate() {
        return generate;
    }
    
}
