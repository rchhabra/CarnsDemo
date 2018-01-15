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
*/

package de.hybris.platform.travelfacades.strategies.impl;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.travelfacades.strategies.AncillaryOfferGroupDisplayStrategy;
import de.hybris.platform.travelservices.model.pages.AncillaryPageModel;

import java.util.List;
import java.util.stream.Collectors;


/**
 * The type Default ancillary offer group display strategy.
 */
public class DefaultAncillaryOfferGroupDisplayStrategy implements AncillaryOfferGroupDisplayStrategy
{
	private static final String SEAT_OFFER_GROUP = "SEAT";

	/**
	 * The method is used for Filtering Offer Groups Based on the Offer Group setup for the ancillary cms page
	 * 
	 * @param contentPage
	 * @param offerResponseData
	 */
	@Override
	public void filterOfferResponseData(final ContentPageModel contentPage, final OfferResponseData offerResponseData)
	{
		if (null != contentPage)
		{
			final AncillaryPageModel ancillaryPage = (AncillaryPageModel) contentPage;

			final List<OfferGroupData> newOfferGroups = offerResponseData.getOfferGroups().stream()
					.filter(offerGroup -> ancillaryPage.getOfferGroups().contains(offerGroup.getCode())).collect(Collectors.toList());

			offerResponseData.setOfferGroups(newOfferGroups);

			if (ancillaryPage.getOfferGroups().stream()
					.noneMatch(offerGroupCode -> SEAT_OFFER_GROUP.equalsIgnoreCase(offerGroupCode)))
			{
				offerResponseData.setSeatMap(new SeatMapResponseData());
			}
		}
	}
}
