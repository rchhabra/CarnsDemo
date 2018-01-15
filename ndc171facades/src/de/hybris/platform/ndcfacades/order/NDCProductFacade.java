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
package de.hybris.platform.ndcfacades.order;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.List;


/**
 * Interface for the NDC Product Facade
 */
public interface NDCProductFacade
{

	/**
	 * Raises an exception if the {@link ProductModel} cannot be added or removed to the provided {@link OrderModel} taking into
	 * consideration quantity, transport offerings, route code, and the associated {@link TravellerModel}
	 *
	 * @param order
	 * @param traveller
	 * @param product
	 * @param quantity
	 * @param transportOfferings
	 * @param routeCode
	 */
	void checkIfProductCanBeAddedToTraveller(OrderModel order, TravellerModel traveller,
			ProductModel product, int quantity, List<String> transportOfferings, String routeCode)
			throws NDCOrderException;

	/**
	 * Raises an exception if the {@link ProductModel} cannot be added or removed to the provided {@link OrderModel} taking into
	 * consideration quantity, transport offerings, route code, and the associated {@link TravellerModel}
	 *
	 * @param order
	 * @param travellerCode
	 * @param product
	 * @param quantity
	 * @param transportOfferings
	 * @param routeCode
	 */
	void checkIfProductCanBeAddedToTraveller(OrderModel order, String travellerCode, ProductModel product, int quantity,
			List<String> transportOfferings, String routeCode) throws NDCOrderException;

	/**
	 * Raises an exception if the {@link ProductModel} cannot be added to the provided {@link OrderModel} taking into
	 * consideration quantity, transport offerings, route code, and the list of associated {@link TravellerModel}
	 *
	 * @param order
	 * @param travellers
	 * @param product
	 * @param quantity
	 * @param transportOfferings
	 * @param routeCode
	 */
	void checkIfValidProductForTravellers(OrderModel order, List<TravellerModel> travellers, ProductModel product, int quantity,
			List<String> transportOfferings, String routeCode) throws NDCOrderException;
}
