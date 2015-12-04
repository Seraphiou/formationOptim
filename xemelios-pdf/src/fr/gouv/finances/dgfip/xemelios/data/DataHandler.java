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

import fr.gouv.finances.dgfip.utils.xml.FactoryProvider;
import fr.gouv.finances.dgfip.utils.xml.InvalidPathExpressionException;
import fr.gouv.finances.dgfip.xemelios.common.config.PjRefHandler;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.gouv.finances.dgfip.utils.xml.dompath.DomPath;
import fr.gouv.finances.dgfip.xemelios.auth.UnauthorizedException;
import fr.gouv.finances.dgfip.xemelios.common.config.ChampModel;
import fr.gouv.finances.dgfip.xemelios.common.config.ListeResultatModel;

/**
 * Permet de stocker un element et sa valeur cible pour le tri
 * 
 * @author chm
 */
public class DataHandler implements Comparable {

	public static final transient int ORDER_NO = 0;
	public static final transient int ORDER_DESC = 1;
	public static final transient int ORDER_ASC = -1;
	public static long timeCountLocal = 0;
	public static long timeCountExtern = 0;
	private static Logger logger = Logger.getLogger(DataHandler.class);
	private static XPathFactory xpf = FactoryProvider.getXPathFactory();

	private Element el;
	private Object value;
	private ListeResultatModel lrm;
	private int orderByCol, sort;
	private DataResultSet drs;
	private String resultKey;
	private Comparable[] cachedData;
	private CachedData globalData;
	private Object[] innerData;
	private Document document = null;
	private String documentEncoding = null;
	private XPath xPath = null;
	private int positionsFilled = 0;

	public DataHandler(ListeResultatModel lrm, Element el, DataResultSet drs, String resultKey, CachedData globalData) {
		super();
		this.lrm = lrm;
		this.el = el;
		this.drs = drs;
		this.resultKey = resultKey;
		this.globalData = globalData;
		xPath = xpf.newXPath();
		xPath.setNamespaceContext(lrm.getParent().getParent().getParent().getNamespaces());
		cachedData = new Comparable[lrm.getListeChamps().size() + lrm.getHiddens().size()];
		innerData = new Object[cachedData.length];
		setOrderByColumn(getColumnTri(lrm.getTriDefaut()), getOrdreTri(lrm.getOrdre()));
	}

	public void setOrderByColumn(int index, int sort) {
		orderByCol = index;
		this.sort = sort;
		computeValue();
	}

	public void setOrderByColumn(String colId, int sort) {
		if (colId == null)
			return;
		Vector<ChampModel> chps = lrm.getListeChamps();
		String sColId = getColumnTri(colId);
		for (int i = 0; i < chps.size(); i++) {
			if (chps.elementAt(i).getId().equals(sColId)) {
				setOrderByColumn(i, sort);
				break;
			}
		}
	}

	public int getOrderByColumn() {
		return orderByCol;
	}

	public int getSortOrder() {
		return sort;
	}

	protected void computeValue() {
		value = getValueAtColumn(orderByCol);
	}

	public Object getValue() {
		if (value == null) {
			computeValue();
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object arg0) {
		if (arg0 == this) {
			return 0;
		}
		if (arg0 instanceof DataHandler) {
			DataHandler other = (DataHandler) arg0;
			int ret = 0;
			Comparable maValeur = (Comparable) getValue();
			Comparable autreValeur = (Comparable) other.getValue();
			if (maValeur == null) {
				ret = -1;
			} else if (autreValeur == null) {
				ret = 1;
			} else {
				ret = maValeur.compareTo(autreValeur);
			}

			if (ret == 0 && arg0 != this) {
				// on ne veut vraiment pas que les choses soient égales
				// on rajoute un "a" a l'autre si les instances sont differentes
				ret = -1;
			}
			return (sort * ORDER_ASC) * ret;
		}
		return -1;
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object arg0) {
		return compareTo(arg0) == 0;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + (this.value != null ? this.value.hashCode() : 0);
		return hash;
	}

	public Object getValueAtColumn(int pos) {
		try {
			Object ret = "<error>";
			if (innerData[pos] == null) {
				Comparable computed = null;
				String path;
				if (pos < lrm.getListeChamps().size()) {
					path = lrm.getListeChamps().get(pos).getPath().getPath();
				} else {
					path = lrm.getHiddens().get(pos - lrm.getListeChamps().size()).getPath().getPath();
				}
				long now = System.currentTimeMillis();
				if (path.startsWith("/")) {
					try {
						ret = queryCachedData(path);
					} catch (Exception ex) {
						logger.error("querying document: ", ex);
						globalData.setCachedData(resultKey, path, ret);
					}
					timeCountExtern += (System.currentTimeMillis() - now);
				} else {
					try {
						if (pos < lrm.getListeChamps().size()) {
							if (!ChampModel.DATATYPE_PJ.equals(lrm.getListeChamps().get(pos).getDatatype())) {
								try {
									DomPath domPath = lrm.getListeChamps().get(pos).getPath().getDomPath(lrm.getParent().getParent().getParent().getNamespaces());
									ret = domPath.getValue(el, true);
								} catch (InvalidPathExpressionException ipeEx) {
									XPathFactory xpf = FactoryProvider.getXPathFactory();
									XPath xp = xpf.newXPath();
									xp.setNamespaceContext(lrm.getParent().getParent().getParent().getNamespaces());
									ret = xp.evaluate(lrm.getListeChamps().get(pos).getPath().getPath(), el);
								}
							} else {
								PjRefHandler pjHandler = new PjRefHandler();
								xPath.setNamespaceContext(lrm.getParent().getParent().getParent().getNamespaces());
								Object o = xPath.evaluate(lrm.getListeChamps().get(pos).getPath().getFormattedPath(), el, XPathConstants.NODESET);
								if (o instanceof NodeList) {
									NodeList nl = (NodeList) o;
									for (int i = 0; i < nl.getLength(); i++) {
										Element ele = (Element) nl.item(i);
										pjHandler.addPj(lrm.getParent().getParent().getParent(), ele);
									}
									// } else {
									// for(Element ele:((List<Element>)o))
									// pjHandler.addPj(lrm.getParent().getParent().getParent(),
									// ele);
								}
								ret = pjHandler;
							}
						} else {
							DomPath domPath = lrm.getHiddens().get(pos - lrm.getListeChamps().size()).getPath().getDomPath(lrm.getParent().getParent().getParent().getNamespaces());
							ret = domPath.getValue(el, true);
						}
					} catch (Exception ex) {
						logger.error("getValueAtColumn", ex);
						globalData.setCachedData(resultKey, path, ret);
					}
					timeCountLocal += (System.currentTimeMillis() - now);
				}
				if (pos < lrm.getListeChamps().size()) {
					computed = lrm.getListeChamps().get(pos).getValueOf(ret);
				} else {
					computed = ret == null ? null : ret.toString();
				}
				cachedData[pos] = computed;
				if (innerData[pos] == null) {
					innerData[pos] = ret;
				}
				positionsFilled++;
				if (positionsFilled == cachedData.length) {
					document = null;
				}
			}
			return cachedData[pos];
		} catch (Exception ex) {
			logger.error("getValueAt(" + pos + ")", ex);
			return null;
		}
	}

	public Object getInnerDataAt(int pos) {
		return innerData[pos];
	}

	public Element getElement() {
		return el;
	}

	public Document getDocument() throws DataConfigurationException, DataAccessException, UnauthorizedException {
		if (document == null) {
			document = drs.getDocument(resultKey);
		}
		return document;
	}

	public String getDocumentEncoding() throws DataConfigurationException, DataAccessException {
		if (documentEncoding == null) {
			documentEncoding = drs.getDocumentEncoding(resultKey);
		}
		return documentEncoding;

	}

	private Object queryCachedData(String path) throws DataConfigurationException, DataAccessException, UnauthorizedException {
		Object ret = (globalData == null ? null : globalData.getCachedData(resultKey, path));
		if (ret == null) {
			try {
				ret = xPath.evaluate(path, getDocument());
			} catch (XPathExpressionException xpExpr) {
				throw new DataConfigurationException(xpExpr);
			}

			if (globalData != null) {
				globalData.setCachedData(resultKey, path, ret);
			}
		}
		return ret;
	}

	public String getDocumentId() {
		return resultKey;
	}

	/**
	 * This method returns the first column of the <tt>orderByColumns</tt>,
	 * which is a comma-separated list
	 * 
	 * @param orderByColumns
	 * @return
	 */
	public static String getColumnTri(String orderByColumns) {
		if (orderByColumns == null)
			return null;
		StringTokenizer tokenizer = new StringTokenizer(orderByColumns, ",");
		if (tokenizer.hasMoreTokens()) {
			return tokenizer.nextToken();
		}
		return null;
	}

	public static int getOrdreTri(String orderByOrdres) {
		if (orderByOrdres == null)
			return ORDER_ASC;
		StringTokenizer tokenizer = new StringTokenizer(orderByOrdres, ",");
		if (tokenizer.hasMoreTokens()) {
			return "DESC".equals(tokenizer.nextToken()) ? ORDER_DESC : ORDER_ASC;
		}
		return ORDER_ASC;
	}
}
