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
package de.hybris.platform.ndcservices.services;

import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Interface that set basic information to an order model
 */
public interface NDCOrderService
{
	/**
	 * Populate the order with the provided currency and other required information
	 *
	 * @param order
	 * @param currency
	 */
	void setOrderBasicInformation(OrderModel order, String currency);

	/**
	 * Creates the {@link OrderEntryModel} attached to the passed {@link OrderModel}
	 *
	 * @param order
	 * @param product
	 * @param bundleTemplate
	 * @param bundleNo
	 * @param ndcOfferItemId
	 * @param transportOfferings
	 * @param travellers
	 * @param routeCode
	 * @param originDestinationRefNumber
	 * @param quantity
	 * @return OrderEntryModel
	 */
	OrderEntryModel populateOrderEntry(OrderModel order, ProductModel product, BundleTemplateModel bundleTemplate, int bundleNo,
			String ndcOfferItemId, List<TransportOfferingModel> transportOfferings, List<TravellerModel> travellers,
			String routeCode, int originDestinationRefNumber, int quantity);

	/**
	 * Sets a PNR-like code against the given order
	 * 
	 * @param abstractOrder
	 */
	void setPNRAsOrderCode(AbstractOrderModel abstractOrder);

}
