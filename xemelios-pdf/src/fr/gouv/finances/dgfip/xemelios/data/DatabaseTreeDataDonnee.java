/*
 * Copyright 
 *   2007 axYus - www.axyus.com
 *   2007 N.Le Corre - nicolas.lecorre@axyus.com
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
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import fr.gouv.finances.dgfip.utils.Pair;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentModel;
import fr.gouv.finances.dgfip.xemelios.common.config.DocumentsModel;
// TODO: reprendre pour mettre en place l'arbre des collectivites
public class DatabaseTreeDataDonnee implements TreeModel {
	private static Logger logger = Logger.getLogger(DatabaseTreeDataDonnee.class);
    private ArrayList<TreeModelListener> listeners;
    private DocumentsModel dm;
    private DataNode root;
    private DataNode archiveNode;
    private WaitableUI wui;
    private XemeliosUser user;
    private PropertiesExpansion applicationProperties;
    
    public DatabaseTreeDataDonnee(DocumentsModel dm, WaitableUI wui, XemeliosUser user, PropertiesExpansion applicationProperties) throws UnauthorizedException {
        super();
        this.applicationProperties = applicationProperties;
        this.dm = dm;
        this.wui=wui;
        this.user=user;
        root = new DataNode(dm,DataNode.TYPE_ROOT);
        root.tree=this;    
        archiveNode = new DataNode(null,DataNode.TYPE_ARCHIVES);
        root.addChild(archiveNode);
        loadChildren(root);        
        listeners = new ArrayList<TreeModelListener>();
    }
    
    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }
    @Override
    public Object getChild(Object parent, int index) {
        try {
            DataNode dn = (DataNode)parent;
            if(!dn.areChildrenLoaded()) {
                loadChildren(dn);
            }
            return dn.getChildren().elementAt(index);
        } catch(UnauthorizedException ex) {
            return null;
        }
    }
    @Override
    public int getChildCount(Object parent) {
        try {
            DataNode dn = (DataNode)parent;
            if(!dn.areChildrenLoaded()) {
                loadChildren(dn);
            }
            return dn.getChildren().size();
        } catch(UnauthorizedException ex) {
            return 0;
        }
    }
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        DataNode dn = (DataNode)parent;
        return dn.getChildren().indexOf(child);
    }
    @Override
    public Object getRoot() {
        return root;
    }
    @Override
    public boolean isLeaf(Object node) {
        try {
            DataNode dn = (DataNode)node;
            //return dn.getType()==DataNode.TYPE_PERSO2 || dn.getChildCount()==0;
            if (dn.getType()==DataNode.TYPE_PERSO2) {
                return true;
            } else if (dn.getType()==DataNode.TYPE_PERSO1 && dn.getChildCount()==0) {
                return true;
            } else if (dn.getType()==DataNode.TYPE_BUDGET && dn.getChildCount()==0) {
                return true;
            } else if (dn.getType()==DataNode.TYPE_ARCHIVE) {
            	return true;
            } else {
                return false;
            }
        } catch(UnauthorizedException ex) {
            return true;
        }
    }
    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {}
    protected Vector<Pair> getCollectivitesFromDocumentModel(DocumentModel dm) throws UnauthorizedException {
        Vector<Pair> ret = null;
        try {
            ret = DataLayerManager.getImplementation().getCollectivites(dm,null,user);
        } catch(DataAccessException daEx) {
            logger.warn("",daEx);
            ret = new Vector<Pair>();
        } catch(DataConfigurationException dcEx) {
            logger.error("",dcEx);
            ret = new Vector<Pair>();
        }
        return ret;
    }
    protected Vector<Pair> getArchives() throws UnauthorizedException {
        Vector<Pair> ret = null;
        try {
            ret = DataLayerManager.getImplementation().getArchivesImported(user);
        } catch(DataAccessException daEx) {
            logger.warn("",daEx);
            ret = new Vector<Pair>();
        } catch(DataConfigurationException dcEx) {
            logger.error("",dcEx);
            ret = new Vector<Pair>();
        }
        return ret;
    }
    protected void loadChildren(DataNode dn) throws UnauthorizedException {
        switch(dn.getType()) {
            case DataNode.TYPE_ROOT: {
                for(DocumentModel doc:dm.getDocuments()) {
                    if(doc.getDisplayInMenuIf()==null || "true".equals(applicationProperties.getProperty(doc.getDisplayInMenuIf()))) dn.addChild(doc);
                }
                dn.childrenLoaded();
                break;
            }
            case DataNode.TYPE_DOCUMENT: {
                Vector<Pair> collectivites = getCollectivitesFromDocumentModel((DocumentModel)dn.getData());
                for(Pair p:collectivites) dn.addChild(p);
                dn.childrenLoaded();
                break;
            }
            case DataNode.TYPE_ARCHIVES: {
            	try{
	                Vector<Pair> archives = getArchives();
	                for(Pair p:archives) dn.addChild(p);
	                dn.childrenLoaded();
            	} finally {
            		wui.stopWait();
            	}
                break;
            }
            case DataNode.TYPE_COLLECTIVITE: {
                Vector<Pair> budgets = null;
                try {
                    Pair collectivite = (Pair)dn.getData();
                    DocumentModel documentModel = (DocumentModel)dn.getParent().getData();
                    budgets = DataLayerManager.getImplementation().getBudgets(documentModel,collectivite,user);
                } catch(DataAccessException daEx) {
                    logger.error("",daEx);
                    budgets = new Vector<Pair>();
                } catch(DataConfigurationException dcEx) {
                    logger.error("",dcEx);
                    budgets = new Vector<Pair>();
                }
                for(Pair budget:budgets) dn.addChild(budget);
                dn.childrenLoaded();
                break;
            }
            case DataNode.TYPE_BUDGET: {
                // il faut aller chercher la liste des special-key
                //MainWindow.getInstance().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                wui.startWait();
                DocumentModel doc = (DocumentModel)(dn.getParent().getParent().getData());
                if(doc.getSpecialKeys().size()>=1) {
                    Pair collectivite = (Pair)dn.getParent().getData();
                    Pair budget = (Pair)dn.getData();
                    Vector<Pair> ret = new Vector<Pair>();
                    try {
                        ret = DataLayerManager.getImplementation().getSpecialKeys1(doc,collectivite,budget,true,user);
                    } catch(DataAccessException daEx) {
                        logger.error("",daEx);
                        ret = new Vector<Pair>();
                    } catch(DataConfigurationException dcEx) {
                        logger.error("",dcEx);
                        ret = new Vector<Pair>();
                    } finally {
                            //MainWindow.getInstance().setCursor(Cursor.getDefaultCursor());
                        wui.stopWait();
                    }
                    for(Pair p:ret) dn.addChild(p);
                }
                dn.childrenLoaded();
                break;
            }
            case DataNode.TYPE_PERSO1: {
                // il faut aller chercher la liste des special-key
            	//MainWindow.getInstance().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                wui.startWait();
                DocumentModel doc = (DocumentModel)(dn.getParent().getParent().getParent().getData());
                if(doc.getSpecialKeys().size()>=2) {
                    Pair collectivite = (Pair)dn.getParent().getParent().getData();
                    Pair budget = (Pair)dn.getParent().getData();
                    Pair key1 = (Pair)dn.getData();
                    Vector<Pair> ret = null;
                    try {
                        ret = DataLayerManager.getImplementation().getSpecialKeys2(doc,collectivite,budget,key1,true,user);
                    } catch(DataAccessException daEx) {
                        logger.error("",daEx);
                        ret = new Vector<Pair>();
                    } catch(DataConfigurationException dcEx) {
                        logger.error("",dcEx);
                        ret = new Vector<Pair>();
                    } finally {
                            //MainWindow.getInstance().setCursor(Cursor.getDefaultCursor());
                        wui.stopWait();
                    }
                    for(Pair p:ret) dn.addChild(p);
                }
                dn.childrenLoaded();
                break;
            }
        }
    }

	public class DataNode {
	    public static final transient int TYPE_ROOT = 0; 
	    public static final transient int TYPE_DOCUMENT = 1;
	    public static final transient int TYPE_COLLECTIVITE = 2;
	    public static final transient int TYPE_BUDGET = 3;
	    public static final transient int TYPE_PERSO1 = 4;
	    public static final transient int TYPE_PERSO2 = 5;
	    public static final transient int TYPE_PERSO3 = 6;
	    public static final transient int TYPE_ARCHIVES = 7;
	    public static final transient int TYPE_ARCHIVE = 8;
	    private static final String DEFAULT_TEXT = "Archives";
	    private boolean areChildrenLoaded = false;
	    private Object data;
	    private int type;
	    private Vector<DataNode> children;
	    private DataNode parent;
	    private DatabaseTreeDataDonnee tree = null;
	    
	    public DataNode(Object data, int type) {
	        super();
	        this.data = data;
	        this.type = type;
	        children = new Vector<DataNode>();
	    }
	    @Override
	    public boolean equals(Object o) {
	        if(o==this) return true;
	        if(data!=null){
		        if(o instanceof DataNode) return data.equals(((DataNode)o).data);
		        return o.equals(data);
	        }
	        return false;	
	    }
	    @Override
	    public String toString() {
	        // mise en majuscule de la première lettre de data
//	    	if (this.data.toString()==null || "".equals(this.data.toString())) return null;
//	        String ret = this.data.toString().substring(0, 1).toUpperCase(); 
//	        ret += this.data.toString().substring(1, this.data.toString().length());
//	    	return ret;
	    	if(data != null) return this.data.toString();
	    	return DEFAULT_TEXT;
	    }
	    public DataNode getParent() { return parent; }
	    public Vector<DataNode> getChildren() { return children; }
	    public void addChild(Object child) {
	    	if(child==null) return;
	        DataNode node = null;
	        if(!(child instanceof DataNode)) {
	            node = new DataNode(child,type+1);	            
	        } else node = (DataNode)child;
	        node.parent = this;
	        children.add(node);
	        node.tree=this.tree;
	    }
	    public int getChildCount() throws UnauthorizedException {
	    	if(!areChildrenLoaded) {
	    		tree.loadChildren(this);
	    	}
	    	return children.size();
	    }
	    @Override
	    public int hashCode() { 
	    	if(data!=null) return data.hashCode();
	    	return 0;
	    }
	    public void childrenLoaded() { areChildrenLoaded = true; }
	    public boolean areChildrenLoaded() { return areChildrenLoaded; }
	    public int getType() { return type; }
	    public Object getData() { return data; }
	}    
}
