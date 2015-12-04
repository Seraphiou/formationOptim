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

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.text.ParseException;
import java.util.Hashtable;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.dgfip.utils.StringUtilities;
import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import fr.gouv.finances.dgfip.xemelios.common.Constants;
import fr.gouv.finances.dgfip.xemelios.common.config.CritereModel;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import fr.gouv.finances.dgfip.xemelios.data.DataLayerManager;
import java.text.SimpleDateFormat;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;

public class TCriteria implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(TCriteria.class);
    public static final String TIME_FORMAT = "HH:mm:ss.";
    public static final SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);

    public static final String TAG = "criteria";
    public static final transient QName QN = new QName(TAG);
    private String id,  sqlIndexId;
    private XsString whereClause;
    private static Hashtable<String, FunctionEvaluator> evaluators = null;

//    static {
//        evaluators = new Hashtable<String, FunctionEvaluator>();
//        evaluators.put("string-compare", new StrCompareEvaluator());
//        evaluators.put("date-compare", new DateCompareEvaluator());
//    }

    public TCriteria(QName tagName) {
        super();

    }

    @Override
    public void addCharacterData(String cData) throws SAXException {
    }

    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {
        if (SqlConfigMapping.WHERE_CLAUSE.equals(tagName)) {
            whereClause = (XsString) child;
        }
    }

    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        id = attributes.getValue("id");
        sqlIndexId = attributes.getValue("sql-index-id");
        return this;
    }

    @Override
    public void marshall(XmlOutputter output) {
    }

    @Override
    public void validate() throws InvalidXmlDefinition {
    }

    @Override
    public TCriteria clone() {
        TCriteria other = new TCriteria(QN);
        other.id = this.id;
        other.sqlIndexId = this.sqlIndexId;
        other.whereClause = this.whereClause.clone();
        return other;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSqlIndexId() {
        return sqlIndexId;
    }

    public void setSqlIndexId(String sqlIndexId) {
        this.sqlIndexId = sqlIndexId;
    }

    public String getWhereClause() {
        return whereClause.getData();
    }

    public void setWhereClause(XsString whereClause) {
        this.whereClause = whereClause;
    }

    public String getSubstitutedWhereClause(CritereModel cm, Hashtable<String, FunctionEvaluator> evaluators, PropertiesExpansion applicationProperties) throws DataConfigurationException {
        return getSubstituteClause(whereClause.getData(), cm, evaluators, applicationProperties);
    }
//    public String getSubstituteSelectClause(CritereModel cm) throws DataConfigurationException {
//        return getSubstituteClause(selectClause,cm);
//    }
    protected String getSubstituteClause(String clause, CritereModel cm, Hashtable<String, FunctionEvaluator> evaluators, PropertiesExpansion applicationProperties) throws DataConfigurationException {
        String expr = clause;
        // search for ##OPERATEUR:id##-like tokens
        int ix = expr.indexOf("##");
        while (ix >= 0) {
            //String left = expr.substring(0, ix);
            String sTmp = expr.substring(ix + 2);
            int pos = sTmp.indexOf("##");
            if (pos < 0) {
                throw new DataConfigurationException(clause + " is not valid");
            }
            //String right = sTmp.substring(pos + 2);
            String middle = sTmp.substring(0, pos);
            String property = cm.getProperty(middle);
            if (property == null) {
                throw new DataConfigurationException("Property ##" + middle + "## not found in criteria " + cm.getId());
            }
            expr = expr.replace("##" + middle + "##", StringEscapeUtils.escapeSql(property));
            ix = expr.indexOf("##");
        }
        // search for fn:xxx(args)-like tokens
        ix = expr.indexOf("fn:");
        while (ix >= 0) {
            int openningParenthesisPos = expr.indexOf("(", ix);
            String functionName = expr.substring(ix + 3, openningParenthesisPos);
            try {
                int closingParenthesisPos = StringUtilities.getClosingParenthesisPos(expr, openningParenthesisPos);
                String args = expr.substring(openningParenthesisPos + 1, closingParenthesisPos);
                String ret = evaluators.get(functionName).evaluate(args, cm, applicationProperties);
                expr = expr.substring(0, ix) + ret + expr.substring(closingParenthesisPos + 1);
            } catch (ParseException pEx) {
                throw new DataConfigurationException("expression " + clause + " has unbalanced parenthesis");
            }
            ix = expr.indexOf("fn:");
        }

        return expr;
    }

    public static interface FunctionEvaluator {

        public String evaluate(String args, CritereModel cm, PropertiesExpansion applicationProperties) throws DataConfigurationException;
    }

    private static class StrCompareEvaluator implements FunctionEvaluator {

        public StrCompareEvaluator() {
        }

        @Override
        public String evaluate(String args, CritereModel cm, PropertiesExpansion applicationProperties) throws DataConfigurationException {
            int pos = args.indexOf(',');
            String arg1 = args.substring(0, pos);
            String arg2 = args.substring(pos + 1);
            String comparator = cm.getProperty("OPERATEUR:" + arg2);
            String value = StringEscapeUtils.escapeSql(cm.getProperty("VALEUR:" + arg2));

            if ("xem:StringEquals".equals(comparator)) {
                return arg1 + "='" + value + "'";
            } else if ("xem:StringDiffers".equals(comparator)) {
                return arg1 + "<>'" + value + "'";
            } else if ("xem:ends-with".equals(comparator)) {
                return arg1 + " LIKE BINARY '%" + value + "'";
            } else if("xem:does-not-contain".equals(comparator)) {
                return arg1 + " NOT LIKE '%" + value +"%'";
            } else if ("contains".equals(comparator)) {
                return arg1 + " LIKE BINARY '%" + value + "%'";
            } else if ("starts-with".equals(comparator)) {
                return arg1 + " LIKE BINARY '" + value + "%'";
            } else if ("xem:StringIsNull".equals(comparator)) {
                return arg1 + " IS NULL";
            } else if ("xem:StringIsNotNull".equals(comparator)) {
                return arg1 + " IS NOT NULL";
            } else {
                throw new DataConfigurationException("unknown comparator: " + comparator);
            }
        }
    }

    private static class DateCompareEvaluator implements FunctionEvaluator {

        public DateCompareEvaluator() {
        }

        @Override
        public String evaluate(String args, CritereModel cm, PropertiesExpansion applicationProperties) throws DataConfigurationException {
            int pos = args.indexOf(',');
            String arg1 = args.substring(0, pos);
            String arg2 = args.substring(pos + 1);
            String comparator = cm.getProperty("OPERATEUR:" + arg2);
            String tmpValue = cm.getProperty("VALEUR:" + arg2);
            String value = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(applicationProperties.getProperty(Constants.SYS_PROP_DATE_FORMAT));
                java.util.Date d = sdf.parse(tmpValue);
                value = StringEscapeUtils.escapeSql(DataLayerManager.getImplementation().getDateFormatter().format(d));
            } catch (Throwable t) {
                logger.error(tmpValue+" is not a valid date");
            }
            if (value == null) {
                value = tmpValue;
            }

            if ("xem:DateEquals".equals(comparator)) {
                return arg1 + "=STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else if ("xem:DateBefore".equals(comparator)) {
                return arg1 + "<=STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else if ("xem:DateAfter".equals(comparator)) {
                return arg1 + ">=STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else {
                throw new DataConfigurationException("unknown comparator: " + comparator);
            }
        }
    }
    private static class TimeCompareEvaluator implements FunctionEvaluator {

        public TimeCompareEvaluator() {
        }

        @Override
        public String evaluate(String args, CritereModel cm, PropertiesExpansion applicationProperties) throws DataConfigurationException {
            int pos = args.indexOf(',');
            String arg1 = args.substring(0, pos);
            String arg2 = args.substring(pos + 1);
            String comparator = cm.getProperty("OPERATEUR:" + arg2);
            String tmpValue = cm.getProperty("VALEUR:" + arg2);
            String value = null;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(applicationProperties.getProperty(Constants.SYS_PROP_DATE_FORMAT));
                java.util.Date d = sdf.parse(tmpValue);
                value = StringEscapeUtils.escapeSql(DataLayerManager.getImplementation().getDateFormatter().format(d));
            } catch (Throwable t) {
                logger.error(tmpValue+" is not a valid date");
            }
            if (value == null) {
                value = tmpValue;
            }

            if ("xem:DateEquals".equals(comparator)) {
                return arg1 + "=STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else if ("xem:DateBefore".equals(comparator)) {
                return arg1 + "<STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else if ("xem:DateAfter".equals(comparator)) {
                return arg1 + ">STR_TO_DATE('" + value + "','%Y-%m-%d')";
            } else {
                throw new DataConfigurationException("unknown comparator: " + comparator);
            }
        }
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
