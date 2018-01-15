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

package de.hybris.platform.ndcfacades.offers.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to collect methods used across different NDC Offers facades
 */
public abstract class AbstractNDCOffersFacade
{
	private AncillarySearchPipelineManager ndcAncillarySearchPipelineManager;
	private List<String> ndcOfferList;

	/**
	 * Gets offers.
	 *
	 * @param offerRequestData
	 * 		the offer request data
	 *
	 * @return the offers
	 */
	public OfferResponseData getOffers(final OfferRequestData offerRequestData)
	{
		return getNdcAncillarySearchPipelineManager().executePipeline(offerRequestData);
	}

	/**
	 * This method filters {@link OfferGroupData} based on given ndcOfferList.
	 *
	 * @param offerResponseData
	 * 		the offer response data
	 */
	protected void filterOfferResponseData(final OfferResponseData offerResponseData)
	{
		if (Objects.nonNull(offerResponseData) && CollectionUtils.isNotEmpty(offerResponseData.getOfferGroups()))
		{
			final List<OfferGroupData> filteredOffers = offerResponseData.getOfferGroups().stream()
					.filter(OfferGroupData -> getNdcOfferList().contains(OfferGroupData.getCode())).collect(Collectors.toList());
			offerResponseData.setOfferGroups(filteredOffers);
		}
	}

	/**
	 * Gets ndc offer list.
	 *
	 * @return the ndc offer list
	 */
	protected List<String> getNdcOfferList()
	{
		return ndcOfferList;
	}

	/**
	 * Sets ndc offer list.
	 *
	 * @param ndcOfferList
	 * 		the ndc offer list
	 */
	@Required
	public void setNdcOfferList(final List<String> ndcOfferList)
	{
		this.ndcOfferList = ndcOfferList;
	}

	/**
	 * Gets ndc ancillary search pipeline manager.
	 *
	 * @return the ndc ancillary search pipeline manager
	 */
	protected AncillarySearchPipelineManager getNdcAncillarySearchPipelineManager()
	{
		return ndcAncillarySearchPipelineManager;
	}

	/**
	 * Sets ndc ancillary search pipeline manager.
	 *
	 * @param ndcAncillarySearchPipelineManager
	 * 		the ndc ancillary search pipeline manager
	 */
	@Required
	public void setNdcAncillarySearchPipelineManager(final AncillarySearchPipelineManager ndcAncillarySearchPipelineManager)
	{
		this.ndcAncillarySearchPipelineManager = ndcAncillarySearchPipelineManager;
	}

}
