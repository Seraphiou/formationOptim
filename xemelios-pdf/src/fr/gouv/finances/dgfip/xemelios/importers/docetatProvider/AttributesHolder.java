/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

import java.util.Hashtable;

/**
 *
 * @author chm
 */
public class AttributesHolder {

    private Hashtable<String, AttributeHolder> attrsById;
    private Hashtable<String, AttributeHolder> attrsByLocation;
    private Hashtable<String, AttributeHolder> attrsByXPath;

    public AttributesHolder() {
        super();
        attrsById = new Hashtable<String, AttributeHolder>();
        attrsByLocation = new Hashtable<String, AttributeHolder>();
        attrsByXPath = new Hashtable<String, AttributeHolder>();
    }

    public void addAttribute(AttributeHolder ah) {
        attrsById.put(ah.getId(), ah);
        attrsByLocation.put(ah.getLocation(), ah);
        attrsByXPath.put(ah.getXPath(), ah);
    }

    public void setAttValue(String xpath, String value) {
        AttributeHolder ah = attrsByXPath.get(xpath);
        if (ah != null) {
            ah.setValue(value);
        }
    }

    public AttributeHolder getAttrsAt(String location) {
        return attrsByLocation.get(location);
    }
    }
