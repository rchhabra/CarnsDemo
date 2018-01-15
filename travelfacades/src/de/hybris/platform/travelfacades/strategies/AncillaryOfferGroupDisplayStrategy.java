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

package de.hybris.platform.travelfacades.strategies;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;


/**
 * The interface Ancillary offer group display strategy.
 */
public interface AncillaryOfferGroupDisplayStrategy
{
	/**
	 * The method is used for Filtering Offer Groups Based on the Offer Group setup for the ancillary cms page
	 *
	 * @param contentPage
	 * 		the content page
	 * @param offerResponseData
	 * 		the offer response data
	 * @return OfferResponseData
	 */
	void filterOfferResponseData(ContentPageModel contentPage, OfferResponseData offerResponseData);
}
