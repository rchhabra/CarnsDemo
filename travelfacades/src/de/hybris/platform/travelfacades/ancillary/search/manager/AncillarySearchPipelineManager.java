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
package de.hybris.platform.travelfacades.ancillary.search.manager;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;


/**
 * Pipeline Manager class that will return a {@link OfferResponseData} after executing a list of handlers
 * on the {@link OfferRequestData} given as input
 */
public interface AncillarySearchPipelineManager
{
    /**
     * Execute pipeline offer response data.
     *
     * @param offerRequestData
     * 		the offer request data
     * @return the offer response data
     */
    OfferResponseData executePipeline(OfferRequestData offerRequestData);
}
