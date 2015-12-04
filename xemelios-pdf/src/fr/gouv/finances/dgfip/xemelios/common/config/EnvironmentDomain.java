/*
 * EnvironmentDomain.java
 *
 * Created on 5 f�vrier 2007, 13:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package fr.gouv.finances.dgfip.xemelios.common.config;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.util.Enumeration;

import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;

/**
 *
 * @author chm
 */
public interface EnvironmentDomain {
    public static final int DOMAIN_DOCUMENTS = 1;
    public static final int DOMAIN_ELEMENT = 2;
    public boolean hasEnvironment(int domain);
    public int getChildCount(int domain, PropertiesExpansion applicationProperties);
    public Enumeration<EnvironmentDomain> children(int domain, PropertiesExpansion applicationProperties);
    public EnvironmentDomain getChildAt(int domain, int pos);
    public Enumeration<VariableModel> getVariables(int domain);
    public Object getValue(String path) throws DataConfigurationException;
    public void setValue(String path, Object value) throws DataConfigurationException;
}
