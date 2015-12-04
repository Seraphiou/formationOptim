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

package fr.gouv.finances.dgfip.xemelios.data.impl.sqlconfig;

import fr.gouv.finances.dgfip.xemelios.common.config.CollectiviteInfosModel;
import java.util.HashMap;
import javax.xml.namespace.QName;

/**
 *
 * @author chm
 */
public class SqlConfigMapping extends HashMap<QName,Class> {
    public SqlConfigMapping() {
        super();
        put(TCriteria.QN,TCriteria.class);
        put(TDocument.QN,TDocument.class);
        put(TElement.QN,TElement.class);
        put(TEtat.QN,TEtat.class);
        put(KEY1,TKey.class);
        put(KEY2,TKey.class);
        put(KEY3,TKey.class);
        put(KEY4,TKey.class);
        put(KEY5,TKey.class);
        put(KEY6,TKey.class);
        put(KEY7,TKey.class);
        put(KEY8,TKey.class);
        put(KEY9,TKey.class);
        put(KEY10,TKey.class);
        put(TLayer.QN,TLayer.class);
        put(TPatch.QN,TPatch.class);
        put(TPersistenceConfig.QN,TPersistenceConfig.class);
        put(TSqlIndex.QN,TSqlIndex.class);
        put(TTable.QN,TTable.class);
        put(TSql.QN,TSql.class);
        put(SQL_COUNT,XsString.class);
        put(SPECIAL_COND,XsString.class);
        put(PATH,XsString.class);
        put(WHERE_CLAUSE,XsString.class);
        put(PERSISTENCE_DERIVE,TPersistenceConfig.class);
//        put(CollectiviteInfosModel.QN,CollectiviteInfosModel.class);
    }
    public static final transient QName KEY1 = new QName("key1");
    public static final transient QName KEY2 = new QName("key2");
    public static final transient QName KEY3 = new QName("key3");
    public static final transient QName KEY4 = new QName("key4");
    public static final transient QName KEY5 = new QName("key5");
    public static final transient QName KEY6 = new QName("key6");
    public static final transient QName KEY7 = new QName("key7");
    public static final transient QName KEY8 = new QName("key8");
    public static final transient QName KEY9 = new QName("key9");
    public static final transient QName KEY10 = new QName("key10");
    public static final transient QName SQL_COUNT = new QName("sql-count");
    public static final transient QName SPECIAL_COND = new QName("special-cond");
    public static final transient QName PATH = new QName("path");
    public static final transient QName WHERE_CLAUSE = new QName("where-clause");
    public static final transient QName PERSISTENCE_DERIVE = new QName("persistence-derives");
}
