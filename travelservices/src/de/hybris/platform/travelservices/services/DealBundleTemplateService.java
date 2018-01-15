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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;


/**
 * DealBundleTemplate Service interface which provides functionality for the DealBundleTemplates.
 */
public interface DealBundleTemplateService
{
	/**
	 * Returns the DealBundleTemplateModel corresponding to the given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId
	 * 		the id of the DealBundleTemplate
	 *
	 * @return the DealBundleTemplateModel corresponding to the given id, null if no DealBundleTemplates are found.
	 */
	DealBundleTemplateModel getDealBundleTemplateById(String dealBundleTemplateId);

	/**
	 * Checks if an order is a deal: an order is a deal if it is present at least one entry with an {@link
	 * de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel} and at least one entry with a {@link
	 * de.hybris.platform.configurablebundleservices.model.BundleTemplateModel}, and if the rootBundle of all the entries is the
	 * same.
	 *
	 * @param orderCode
	 * 		as the order code
	 *
	 * @return true if the order is a deal, false otherwise
	 */
	boolean isDealBundleOrder(final String orderCode);

	/**
	 * Abstract order is deal.
	 *
	 * @param abstractOrderModel
	 *           the abstract order model
	 * @return true, if successful
	 */
	boolean abstractOrderIsDeal(AbstractOrderModel abstractOrderModel);
}
