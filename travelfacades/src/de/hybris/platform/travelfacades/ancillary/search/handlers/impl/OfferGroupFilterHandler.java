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

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class filters out the product categories which have not been evaluated as part of rule evaluation
 */
public class OfferGroupFilterHandler implements AncillarySearchHandler
{

	private TravelRulesService travelRulesService;

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{
		final List<String> desiredCategories = getTravelRulesService().showProductCategories(offerRequestData);
		discardUndesiredCategories(desiredCategories, offerResponseData.getOfferGroups());
	}

	/**
	 * Discards undesired product categories
	 *
	 * @param desiredCategories
	 * @param offerResponseData
	 */
	protected void discardUndesiredCategories(final List<String> desiredCategories, final List<OfferGroupData> offerGroups)
	{
		final List<OfferGroupData> unDesiredCategories = new ArrayList<>();
		offerGroups.forEach(offerGroup -> {
			if (!desiredCategories.contains(offerGroup.getCode()) && !offerGroup.isIgnoreRules())
			{
				unDesiredCategories.add(offerGroup);
			}
		});
		offerGroups.removeAll(unDesiredCategories);
	}

	/**
	 * @return the travelRulesService
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * @param travelRulesService
	 *           the travelRulesService to set
	 */
	@Required
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}
}
