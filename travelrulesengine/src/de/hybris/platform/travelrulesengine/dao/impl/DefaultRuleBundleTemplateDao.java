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
package de.hybris.platform.travelrulesengine.dao.impl;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import de.hybris.platform.travelrulesengine.dao.RuleBundleTemplateDao;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * The type Default implementation of the {@link RuleBundleTemplateDao}
 */
public class DefaultRuleBundleTemplateDao extends DefaultGenericDao<BundleTemplateModel> implements RuleBundleTemplateDao
{
	private static final Logger LOG = Logger.getLogger(DefaultRuleBundleTemplateDao.class);

	/**
	 * Instantiates a new Default ndc offer mapping dao.
	 *
	 * @param typecode
	 * 		the typecode
	 */
	public DefaultRuleBundleTemplateDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<BundleTemplateModel> findDefaultBundleTemplates(final String bundleTemplateId)
	{
		validateParameterNotNull(bundleTemplateId, "Bundle Template Id must not be null!");

		final List<BundleTemplateModel> bundleTemplateModels = find(
				Collections.singletonMap(BundleTemplateModel.ID, (Object) bundleTemplateId));

		if (CollectionUtils.isEmpty(bundleTemplateModels))
		{
			LOG.info("No result for the given query");
			return null;
		}
		return bundleTemplateModels;
	}
}
