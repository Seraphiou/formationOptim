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
package fr.gouv.finances.dgfip.xemelios.common.config;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;

/**
 * Permet de definir les filtres de suppression
 * @author chm
 */
public class DeleteFilterModel implements XmlMarshallable {
    public static final transient String TAG = "delete-filter"; 
    public static final transient QName QN = new QName(TAG);
	public static final int SORT_NO=0;
	public static final int SORT_KEY=1;
	public static final int SORT_LIB=2;
    private int pos = 0;
    private String filter, sort="no";
    private int specialKeySource=0;
    
    public DeleteFilterModel(QName tagName) {
        super();
    }

    @Override
    public void addCharacterData(String cData) throws SAXException {}
    @Override
    public void addChild(XmlMarshallable child, QName tagName) throws SAXException {}
    @Override
    public XmlMarshallable getAttributes(XmlAttributes attributes) throws SAXException {
        pos = attributes.getIntValue("pos");
        specialKeySource = attributes.getIntValue("special-key-source");
        filter = attributes.getValue("filter");
        sort = attributes.getValue("sort");
        return this;
    }
    @Override
    public void marshall(XmlOutputter output) {throw new Error("Not yet implemented"); }
    @Override
    public void validate() throws InvalidXmlDefinition {}
    public String getFilter() {
        return filter;
    }
    public int getPos() {
        return pos;
    }
    public String getSort() {
        return sort;
    }
	public int getSortBy() { 
	    if("no".equals(sort)) return SORT_NO;
	    else if("key".equals(sort)) return SORT_KEY;
	    else return SORT_LIB;
	}
    public int getSpecialKeySource() {
        return specialKeySource;
    }
    @Override
    public DeleteFilterModel clone() {
        DeleteFilterModel dfm = new DeleteFilterModel(QN);
        dfm.pos = this.pos;
        dfm.filter = this.filter;
        dfm.specialKeySource=this.specialKeySource;
        dfm.sort=this.sort;
        return dfm;
    }
    @SuppressWarnings("unchecked")
	public Collection<Pair> getNewRecipient() {
        int sortBy = getSortBy();
        if(sortBy==SORT_NO) return new Vector<Pair>();
        Comparator<Pair> cp;
        if(sortBy==SORT_KEY) {
            cp = new Comparator() {
                @Override
	            public int compare(Object o1, Object o2) {
	                Pair p1=(Pair)o1;
	                Pair p2=(Pair)o2;
	                return p1.key.compareTo(p2.key);
	            }
	        };
        } else {
            cp = new Comparator() {
                @Override
	            public int compare(Object o1, Object o2) {
	                Pair p1=(Pair)o1;
	                Pair p2=(Pair)o2;
	                return p1.libelle.compareTo(p2.libelle);
	            }
	        };
        }
        return new TreeSet<Pair>(cp);
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
