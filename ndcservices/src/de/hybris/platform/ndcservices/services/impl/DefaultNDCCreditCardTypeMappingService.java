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
package de.hybris.platform.ndcservices.services.impl;

import de.hybris.platform.ndcservices.dao.NDCCreditCardTypeMappingDao;
import de.hybris.platform.ndcservices.model.NDCCreditCardTypeMappingModel;
import de.hybris.platform.ndcservices.services.NDCCreditCardTypeMappingService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCCreditCardTypeMappingService}
 */
public class DefaultNDCCreditCardTypeMappingService implements NDCCreditCardTypeMappingService
{
	private NDCCreditCardTypeMappingDao ndcCreditCardTypeMappingDao;

	@Override
	public NDCCreditCardTypeMappingModel getNDCCreditCardTypeMapping(final String cardTypeCode)
	{
		return getNdcCreditCardTypeMappingDao().getCreditCardTypeMapping(cardTypeCode);
	}

	/**
	 * Gets ndc credit card type mapping dao.
	 *
	 * @return the ndc credit card type mapping dao
	 */
	protected NDCCreditCardTypeMappingDao getNdcCreditCardTypeMappingDao()
	{
		return ndcCreditCardTypeMappingDao;
	}

	/**
	 * Sets ndc credit card type mapping dao.
	 *
	 * @param ndcCreditCardTypeMappingDao
	 * 		the ndc credit card type mapping dao
	 */
	@Required
	public void setNdcCreditCardTypeMappingDao(final NDCCreditCardTypeMappingDao ndcCreditCardTypeMappingDao)
	{
		this.ndcCreditCardTypeMappingDao = ndcCreditCardTypeMappingDao;
	}
}
