/*
 * XemeliosComponentsModel.java
 *
 * Created on 9 novembre 2007, 10:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.xemelios.common.components;

import fr.gouv.finances.cp.utils.xml.marshal.InvalidXmlDefinition;
import fr.gouv.finances.cp.utils.xml.marshal.XmlAttributes;
import fr.gouv.finances.cp.utils.xml.marshal.XmlMarshallable;
import fr.gouv.finances.cp.utils.xml.marshal.XmlOutputter;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;

/**
 *
 * @author chm
 */
public class XemeliosComponentsModel implements XmlMarshallable {
    public static final transient String TAG = "xemelios-components";
    public static final transient QName QN = new QName(TAG);
    private String tagName;
    
    private Hashtable<String,ComponentModel> hComponents;
    private ArrayList<ComponentModel> components;
    
    /** Creates a new instance of XemeliosComponentsModel */
    public XemeliosComponentsModel(QName tagName) {
        super();
        this.tagName=tagName.getLocalPart();
        components = new ArrayList<ComponentModel>();
        hComponents = new Hashtable<String,ComponentModel>();
    }

    public void addCharacterData(String cData) throws org.xml.sax.SAXException { }

    public void addChild(XmlMarshallable child, QName tagName) throws org.xml.sax.SAXException {
        if(ComponentModel.QN.equals(tagName)) {
            ComponentModel cm = (ComponentModel)child;
            if(hComponents.containsKey(cm.getId())) {
                ComponentModel old = hComponents.get(cm.getId());
                components.remove(old);
                hComponents.remove(old.getId());
            }
            hComponents.put(cm.getId(),cm);
            components.add(cm);
        }
    }

    public XmlMarshallable getAttributes(XmlAttributes attributes) throws org.xml.sax.SAXException {
        return this;
    }

    public void marshall(XmlOutputter output) {
        output.startTag(TAG);
        for(ComponentModel cm:components) cm.marshall(output);
        output.endTag(TAG);
    }

    public void validate() throws InvalidXmlDefinition { }

    public XemeliosComponentsModel clone() {
        return this;
    }
    public ComponentModel getComponent(String cId) {
        return hComponents.get(cId);
    }

    public XmlMarshallable getChildToModify(String uri, String localName, String qName, Attributes atts) {
        return hComponents.get(atts.getValue("id"));
    }

    public QName getQName() {
        return QN;
    }

}
