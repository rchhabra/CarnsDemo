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
package de.hybris.platform.ndcfacades.airshopping;

import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;


/**
 * Interface for the Air Shopping Facade
 */
public interface AirShoppingFacade {

    /**
     * Performs a search for fare selection options based on fare search request
     *
     * @param airShoppingRQ
     * 		the Ait Shopping request data
     * @return AirShoppingRS object with available fare options
     */
    AirShoppingRS doSearch(AirShoppingRQ airShoppingRQ);
}
