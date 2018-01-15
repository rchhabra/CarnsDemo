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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;

import org.fest.util.Collections;


/**
 * Handler class to populate TravelRestriction for each product available to offer as ancillaries.
 */
public class OfferRestrictionHandler extends AbstractOfferRestrictionHandler implements AncillarySearchHandler
{

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		offerResponseData.getOfferGroups().stream()
				.filter(offerGroup -> !Collections.isEmpty(offerGroup.getOriginDestinationOfferInfos()))
				.forEach(offerGroupData -> offerGroupData.getOriginDestinationOfferInfos()
						.forEach(odOfferInfo -> odOfferInfo.getOfferPricingInfos().forEach(this::setTravelRestriction)));
	}

}
