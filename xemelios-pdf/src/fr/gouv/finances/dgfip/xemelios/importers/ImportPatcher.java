/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 c.Marhcand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.importers;

import fr.gouv.finances.dgfip.xemelios.common.FileInfo;
import java.io.File;

/**
 * This interface specifies an import patcher. An import-patcher can be run from
 * a Xemelios Archive, after all other files have been imported. The general
 * syntax in manifest archive is :
 * <pre>
 * &lt;manifeste cgIdCol="..." cgLibelle="..."&gt;
 *   &lt;volumes&gt;
 *     ...
 *   &lt;/volumes&gt;
 *   &lt;actions&gt;
 *     &lt;action class="..."&gt;
 *       &lt;parameter type="java.lang.String" name="paramName"&gt;paramValue&lt;/parameter&gt;
 *     &lt;/action&gt;
 *   &lt;/actions&gt;
 * &lt;/manifeste&gt;
 * </pre>
 * @author christophe.marchand
 */
public interface ImportPatcher {
    public void setParameter(String paramName, Object value);
    public void setParameter(String paramName, File file);
    public void setParameter(String paramName, String value);
    public FileInfo run();
}
