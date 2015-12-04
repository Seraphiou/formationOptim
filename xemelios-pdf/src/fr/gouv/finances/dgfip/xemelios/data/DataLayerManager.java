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


package fr.gouv.finances.dgfip.xemelios.data;

import fr.gouv.finances.cp.utils.PropertiesExpansion;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;

import org.apache.log4j.Logger;


/**
 * This class is responsible in providing the right implementation of layer access
 * to Xemelios. It is implemented as a singleton wich must be initialized with
 * environment parameters before being accessed.
 * Environment properties are :
 * <ul>
 * <li><tt><b>xemelios.datalayer.impl</b></tt> the name of implementation. At this moment, only <tt>mysql</tt> is supported</li>
 * </ul>
 * <br>Implementation classes must be registered into DataLayerManager by calling {@link #regiterDataImpl(String,Class)}
 * @author chm
 */
public class DataLayerManager {
    private static Logger logger=Logger.getLogger(DataLayerManager.class);
    private static Hashtable<String,Class> dataImpl=new Hashtable<String,Class>();
    private static DataImpl instance = null;
    private static PropertiesExpansion applicationProperties;

    public static void regiterDataImpl(String implName,Class implClass) {
        logger.debug("registring "+implName+" -> "+implClass.getName());
        if(dataImpl.get(implName)!=null) {
            // WARN
            logger.warn(implName+" already registered.");
        }
        dataImpl.put(implName,implClass);
    }
    public static DataImpl getImplementation() throws DataConfigurationException {
        if(instance==null) throw new DataConfigurationException("DataLayerManager not initialized.");
        return instance;
    }
    
    public static void setDataImpl(String implName) throws DataConfigurationException {
        Class impl = dataImpl.get(implName);
        if(impl==null) throw new DataConfigurationException("Unknown implementation: "+implName);
        try {
            Constructor<DataImpl> cc = impl.getConstructor(PropertiesExpansion.class);
            Object o = cc.newInstance(getApplicationProperties());
            if(!(o instanceof DataImpl)) throw new DataConfigurationException(impl.getName()+" is not a valid DataImpl");
            instance = (DataImpl)o;
            instance.setApplicationProperties(applicationProperties);
        } catch (InstantiationException e) {
            throw new DataConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new DataConfigurationException(e);
        } catch (NoSuchMethodException ex) {
            throw new DataConfigurationException(ex);
        } catch (SecurityException ex) {
            throw new DataConfigurationException(ex);
        } catch(InvocationTargetException ex) {
            throw new DataConfigurationException(ex);
        }
 
    }

    public static PropertiesExpansion getApplicationProperties() {
        return applicationProperties;
    }

    public static void setApplicationProperties(PropertiesExpansion applicationProperties) {
        DataLayerManager.applicationProperties = applicationProperties;
    }

    
}
