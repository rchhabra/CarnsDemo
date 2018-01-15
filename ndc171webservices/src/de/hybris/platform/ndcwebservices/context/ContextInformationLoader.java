/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 *
 *
 */
package de.hybris.platform.ndcwebservices.context;

import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for context information loader
 */
public interface ContextInformationLoader
{
	/**
	 * Initialize site from request cms site model.
	 *
	 * @param absoluteURL
	 * 		the absolute url
	 *
	 * @return the cms site model
	 */
	CMSSiteModel initializeSiteFromRequest(final String absoluteURL);

	/**
	 * Sets catalog versions.
	 */
	void setCatalogVersions();

}
