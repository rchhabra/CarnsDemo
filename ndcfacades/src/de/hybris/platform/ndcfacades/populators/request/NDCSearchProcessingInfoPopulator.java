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

package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.SearchProcessingInfoData;
import de.hybris.platform.commercefacades.travel.enums.FareSelectionDisplayOrder;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;

/**
 * The NDC Search Processing Info Populator
 * Set DEPARTURE_TIME as default order for the result of the {@link AirShoppingRQ}
 */
public class NDCSearchProcessingInfoPopulator implements Populator<AirShoppingRQ,FareSearchRequestData>
{

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		final SearchProcessingInfoData searchProcessingInfoData = new SearchProcessingInfoData();
		searchProcessingInfoData.setDisplayOrder(FareSelectionDisplayOrder.DEPARTURE_TIME.toString());
		fareSearchRequestData.setSearchProcessingInfo(searchProcessingInfoData);
	}

}
