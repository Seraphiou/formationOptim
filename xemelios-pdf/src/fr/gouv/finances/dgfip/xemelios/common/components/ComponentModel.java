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

package fr.gouv.finances.dgfip.xemelios.common.components;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

/**
 *
 * @author chm
 */
public class ComponentModel implements XmlMarshallable {
    private static final Logger logger = Logger.getLogger(ComponentModel.class);
    public static final transient String TAG = "component";
    public static final transient QName QN = new QName(TAG);
    
    private String tagName;
    private QName qn;
    
    private String id, description, version;
    
    /** Creates a new instance of ComponentModel */
    public ComponentModel(QName tagName) {
        super();
        this.qn=tagName;
        this.tagName = qn.getLocalPart();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addCharacterData(String cData) throws org.xml.sax.SAXException { }

    public void addChild(XmlMarshallable child, QName tagName) throws org.xml.sax.SAXException { }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws org.xml.sax.SAXException {
        id = attributes.getValue("id");
        description = attributes.getValue("description");
        version = attributes.getValue("version");
        return this;
    }

    public void marshall(XmlOutputter output) {
        output.startTag(tagName);
        output.addAttribute("id",id);
        output.addAttribute("description",description);
        output.addAttribute("version",version);
        output.endTag(tagName);
    }

    public void validate() throws InvalidXmlDefinition { }

    @Override
    public ComponentModel clone() {
        return this;
    }
    public boolean isNewerThan(ComponentModel other) {
        if(!getId().equals(other)) throw new IllegalArgumentException(other.getId()+" is different than "+getId());
        return compareReleases(getVersion(),other.getVersion())>0;
    }
    public boolean isNewerOrEqualThan(ComponentModel other) {
        if(!getId().equals(other)) throw new IllegalArgumentException(other.getId()+" is different than "+getId());
        return compareReleases(getVersion(),other.getVersion())>=0;
    }
    public boolean isNewerOrEqualThan(String otherVersion) {
        return compareReleases(getVersion(),otherVersion)>=0;
    }
    
    private static int compareReleases(String r1, String r2) {
        String[] s1 = splitRelease(r1);
        String[] s2 = splitRelease(r2);
        int ret = 0;
        for(int i=0;i<Math.min(s1.length,s2.length);i++) {
            int a = -1, b = -1;
            try { a = Integer.parseInt(s1[i]); } catch(Throwable t) {
                a = -1;
            }
            try { b = Integer.parseInt(s2[i]); } catch(Throwable t) {
                b = -1;
            }
            if(a!=-1 && b!=-1) {
                if(a<b) ret = -1;
                else if(a==b) ret = 0;
                else ret = 1;
            } else {
                ret = s1[i].compareTo(s2[i]);
            }
            if(ret!=0) {
                return ret;
            }
        }
        if(s1.length>s2.length) ret = 1;
        else if(s1.length<s2.length) ret = -1;
        return ret;
    }
    private static String[] splitRelease(String r) {
        return r.split("\\.");
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return null;
    }

    public QName getQName() {
        return qn;
    }
}
