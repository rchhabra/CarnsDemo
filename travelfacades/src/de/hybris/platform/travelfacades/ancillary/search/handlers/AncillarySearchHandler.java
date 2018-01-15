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
package de.hybris.platform.travelfacades.ancillary.search.handlers;


import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;


/**
 * Interface for handlers classes that will be updating the {@link OfferResponseData} based on the {@link OfferRequestData}
 * given in input
 */
public interface AncillarySearchHandler
{
    /**
     * Handle method.
     *
     * @param offerRequestData
     * 		the offer request data
     * @param offerResponseData
     * 		the offer response data
     */
    void handle(OfferRequestData offerRequestData, OfferResponseData offerResponseData);
}
