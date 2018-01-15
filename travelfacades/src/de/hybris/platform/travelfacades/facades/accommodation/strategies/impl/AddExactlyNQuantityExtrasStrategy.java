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

package de.hybris.platform.travelfacades.facades.accommodation.strategies.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AccommodationRestrictionData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AbstractRestrictionExtrasStrategy;
import de.hybris.platform.travelfacades.facades.accommodation.strategies.AccommodationRestrictionExtrasStrategy;
import de.hybris.platform.travelservices.exceptions.AccommodationPipelineException;
import de.hybris.platform.travelservices.model.travel.AddExactlyNRestrictionModel;


/**
 * Implementation of {@link AccommodationRestrictionExtrasStrategy} interface, to check if the stockLevel is greater or
 * equals to the specified quantity in the restriction for each dates in the accommodationReservationData and create the
 * restrictionData for the given product.
 */
public class AddExactlyNQuantityExtrasStrategy extends AbstractRestrictionExtrasStrategy
		implements AccommodationRestrictionExtrasStrategy
{
	private static final int DEFAULT_QUANTITY = 1;

	@Override
	public AccommodationRestrictionData applyStrategy(final ProductModel productModel,
			final ReservedRoomStayData reservedRoomStayData, final AccommodationReservationData accommodationReservationData)
					throws AccommodationPipelineException
	{
		final AddExactlyNRestrictionModel restriction = (AddExactlyNRestrictionModel) productModel
				.getTravelRestriction();

		final int quantity = restriction.getTripMaxOfferQty() != null ? restriction.getTripMaxOfferQty() : DEFAULT_QUANTITY;


		if (getServiceAvailableQuantity(productModel, reservedRoomStayData, accommodationReservationData) < quantity)
		{
			throw new AccommodationPipelineException("Product with code " + productModel.getCode()
					+ " is not available for accommodationOffering with code " + reservedRoomStayData.getRoomTypes().get(0).getCode());
		}

		final AccommodationRestrictionData restrictionData = new AccommodationRestrictionData();
		if (restriction.getAddToCartCriteria() != null)
		{
			restrictionData.setAddToCartCriteria(restriction.getAddToCartCriteria().getCode());
		}
		restrictionData.setMinQuantity(quantity);
		restrictionData.setMaxQuantity(quantity);

		return restrictionData;
	}

}
