/*
 * Copyright
 *   2009 axYus - www.axyus.com
 *   2009 C.Marchand - christophe.marchand@axyus.com
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
package fr.gouv.finances.dgfip.xemelios.common.config.customLinks;

import fr.gouv.finances.dgfip.utils.NavigationContext;
import fr.gouv.finances.dgfip.xemelios.auth.XemeliosUser;
import fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException;
import java.util.Properties;
import javax.xml.transform.Result;

/**
 * A custom link interpretor and resolver
 * @author chm
 */
public abstract class CustomLinkResolver {

    public CustomLinkResolver() {
        super();
    }
    /**
     * Resolve parameters, generates and writes output to result, and returns correctly initialized navigation context.
     * @param parameters Custom link parameters
     * @param result the result XSL processor should writes to
     * @param ctx Initial navigation context that may be modified and/or return
     * @return
     * @throws fr.gouv.finances.dgfip.xemelios.data.DataConfigurationException
     * @throws fr.gouv.finances.cp.xemelios.ui.xhtmlviewer.NotFoundException
     */
    public abstract NavigationContext resolve(CustomLinkParameters parameters, Result result, NavigationContext ctx, XemeliosUser user) throws DataConfigurationException, UnresolvedException;
    
    /**
     * Permet de passer des paramètres aux XSL
     * @param additionalsParameters
     */
    public abstract void setXslParameters(Properties additionalsParameters);
}
