/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.gouv.finances.dgfip.xemelios.importers.docetatProvider;

/**
 *
 * @author chm
 */
public class AttributeHolder {

    private String id,  location,  xpath,  name,  value;

    public AttributeHolder(String id, String name, String location, String xpath) {
        super();
        this.id = id;
        this.name = name;
        this.location = location;
        this.xpath = xpath;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getXPath() {
        return xpath;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    }
