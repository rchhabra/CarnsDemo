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
package de.hybris.platform.travelrulesengine.rule.strategies.impl.mappers;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.travelrulesengine.dao.RuleBundleTemplateDao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Bundle template parameter value mapper.
 */
public class BundleTemplateParameterValueMapper implements RuleParameterValueMapper<BundleTemplateModel>
{
	private RuleBundleTemplateDao ruleBundleTemplateDao;

	/**
	 * Instantiates a new Bundle template parameter value mapper.
	 */
	public BundleTemplateParameterValueMapper()
	{
	}

	public String toString(final BundleTemplateModel bundleTemplateModel)
	{
		ServicesUtil.validateParameterNotNull(bundleTemplateModel, "Object cannot be null");
		return bundleTemplateModel.getId();
	}

	public BundleTemplateModel fromString(final String value)
	{
		ServicesUtil.validateParameterNotNull(value, "String value cannot be null");
		final List<BundleTemplateModel> defaultBundleTemplates = getRuleBundleTemplateDao().findDefaultBundleTemplates(value);

		if(CollectionUtils.isEmpty(defaultBundleTemplates))
		{
			return null;
		}

		return defaultBundleTemplates.get(0);
	}

	/**
	 * Gets rule bundle template dao.
	 *
	 * @return the rule bundle template dao
	 */
	protected RuleBundleTemplateDao getRuleBundleTemplateDao()
	{
		return ruleBundleTemplateDao;
	}

	/**
	 * Sets rule bundle template dao.
	 *
	 * @param ruleBundleTemplateDao
	 * 		the rule bundle template dao
	 */
	@Required
	public void setRuleBundleTemplateDao(final RuleBundleTemplateDao ruleBundleTemplateDao)
	{
		this.ruleBundleTemplateDao = ruleBundleTemplateDao;
	}
}
