/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

import org.w3c.dom.Element;

/**
 *
 * @author chm
 */
public class NodeHolder {

    private String copyFrom,  copyTo;
    private Element value;

    public NodeHolder(String copyFrom, String copyTo) {
        super();
        this.copyFrom = copyFrom;
        this.copyTo = copyTo;
    }

    public void setValue(Element node) {
        value = node;
    }

    public Element getValue() {
        return value;
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public String getCopyTo() {
        return copyTo;
    }
    }
