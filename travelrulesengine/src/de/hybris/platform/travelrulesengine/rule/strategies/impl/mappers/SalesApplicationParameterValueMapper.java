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

import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;

import org.springframework.beans.factory.annotation.Required;


/**
 * The type Sales application parameter value mapper.
 */
public class SalesApplicationParameterValueMapper implements RuleParameterValueMapper<Object>
{
	private EnumerationService enumerationService;

	@Override
	public String toString(final Object salesApplication)
	{
		if(salesApplication instanceof SalesApplication){
			return ((SalesApplication) salesApplication).getCode();
		}
		return ((EnumerationValueModel) salesApplication).getCode();
	}

	@Override
	public Object fromString(final String code)
	{
		return getEnumerationService().getEnumerationValue(SalesApplication.class, code);
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
