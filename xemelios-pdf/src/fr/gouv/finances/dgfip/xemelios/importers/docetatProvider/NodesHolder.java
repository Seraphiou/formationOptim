/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

import java.util.Hashtable;
import org.w3c.dom.Element;

/**
 *
 * @author chm
 */
public class NodesHolder {

    private Hashtable<String, NodeHolder> nodesByFrom;
    private Hashtable<String, NodeHolder> nodesByTo;

    public NodesHolder() {
        super();
        nodesByFrom = new Hashtable<String, NodeHolder>();
        nodesByTo = new Hashtable<String, NodeHolder>();
    }

    public void addNode(NodeHolder nh) {
        nodesByFrom.put(nh.getCopyFrom(), nh);
        nodesByTo.put(nh.getCopyTo(), nh);
    }

    public void setValueAt(String from, Element value) {
        NodeHolder nh = nodesByFrom.get(from);
        if (nh != null) {
            nh.setValue(value);
        }
    }

    public Element getValueAt(String to) {
        NodeHolder nh = nodesByTo.get(to);
        if (nh != null) {
            return nh.getValue();
        }
        return null;
    }

    public boolean exists(String from) {
        return nodesByFrom.containsKey(from);
    }
}
