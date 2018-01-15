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

import de.hybris.platform.ndcservices.dao.NDCOfferMappingDao;
import de.hybris.platform.ndcservices.model.NDCOfferMappingModel;
import de.hybris.platform.ndcservices.services.NDCOfferMappingService;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCOfferMappingService}
 */
public class DefaultNDCOfferMappingService implements NDCOfferMappingService
{
	private NDCOfferMappingDao ndcOfferMappingDao;

	@Override
	public NDCOfferMappingModel getNDCOfferMappingFromCode(final String code)
	{
		return getNdcOfferMappingDao().getNDCOfferMappingFromCode(code);
	}

	@Override
	public NDCOfferMappingModel getNDCOfferMappingFromOfferItemID(final String ndcOfferItemID)
	{
		return getNdcOfferMappingDao().getNDCOfferMappingFromOfferItemID(ndcOfferItemID);
	}

	/**
	 * Gets ndc offer mapping dao.
	 *
	 * @return the ndc offer mapping dao
	 */
	protected NDCOfferMappingDao getNdcOfferMappingDao()
	{
		return ndcOfferMappingDao;
	}

	/**
	 * Sets ndc offer mapping dao.
	 *
	 * @param ndcOfferMappingDao
	 * 		the ndc offer mapping dao
	 */
	@Required
	public void setNdcOfferMappingDao(final NDCOfferMappingDao ndcOfferMappingDao)
	{
		this.ndcOfferMappingDao = ndcOfferMappingDao;
	}
}
