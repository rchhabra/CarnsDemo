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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.List;


/**
 * Interface that updates basic information to an order regarding accommodation.
 */
public interface NDCAccommodationService
{
	/**
	 * This method returns {@link ConfiguredAccommodationModel} for given seatNum and {@link TransportOfferingModel}
	 *
	 * @param ndcOfferItemId
	 * @param transportOffering
	 * @param seatNum
	 * @return ConfiguredAccommodationModel
	 */
	ConfiguredAccommodationModel getConfiguredAccommodation(NDCOfferItemId ndcOfferItemId,
			TransportOfferingModel transportOffering, String seatNum);

	/**
	 * @param product
	 * @param seatNum
	 * @param ndcOfferItemId
	 * @param transportOffering
	 * @return returns true if given seatNum is available for {@link TransportOfferingModel}
	 */
	boolean checkIfAccommodationCanBeAdded(ProductModel product, String seatNum, NDCOfferItemId ndcOfferItemId,
			TransportOfferingModel transportOffering);

	/**
	 * Creates the {@link SelectedAccommodationModel} for given params.
	 *
	 * @param transportOffering
	 * @param travellers
	 * @param orderModel
	 * @param configuredAccommodation
	 */
	void createOrUpdateSelectedAccommodation(TransportOfferingModel transportOffering, List<TravellerModel> travellers,
			OrderModel orderModel, ConfiguredAccommodationModel configuredAccommodation);

	/**
	 * @param orderModel
	 * @param transportOffering
	 * @param travellerModel
	 */
	void removeSelectedAccommodation(OrderModel orderModel, TransportOfferingModel transportOffering,
			TravellerModel travellerModel);

	/**
	 * Returns true of given accommodationProd valid for fare product. Fare product evaulates from {@link NDCOfferItemId}
	 *
	 * @param accommodationProd
	 * @param ndcOfferItemId
	 * @return returns true of given accommodationProd valid for fare product
	 */
	boolean checkIfSeatValidForFareProd(ProductModel accommodationProd, NDCOfferItemId ndcOfferItemId);

	/**
	 * This API check and returns an instance of {@link SelectedAccommodationModel} if available for given
	 * {@link TransportOfferingModel}, {@link OrderModel} and {@link TravellerModel}
	 *
	 * @param transportOffering
	 * @param order
	 * @param traveller
	 * @return SelectedAccommodationModel
	 */
	SelectedAccommodationModel getSelectedAccommodationForTraveller(TransportOfferingModel transportOffering, OrderModel order,
			TravellerModel traveller);
}
