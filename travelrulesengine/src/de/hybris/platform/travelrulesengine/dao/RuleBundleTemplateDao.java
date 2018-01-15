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
package de.hybris.platform.travelrulesengine.dao;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;

import java.util.List;


/**
 * The interface Rule bundle template dao.
 */
public interface RuleBundleTemplateDao
{

	/**
	 * Returns list of default BundleTemplateModels for given id.
	 *
	 * @param bundleTemplateId
	 * 		the bundle Template Id
	 * @return list of default BundleTemplateModels
	 */
	List<BundleTemplateModel> findDefaultBundleTemplates(String bundleTemplateId);
}
