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

package de.hybris.platform.travelfacades.reservation.handlers.impl;


import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


/**
 * This handler is responsible for instantiating Itinerary Pricing Info for given leg which will contain ONLY the
 * information related the the bundle type:
 */
public class ReservationPricingInfoBundleTypeHandler implements ReservationHandler
{
	private EnumerationService enumerationService;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if (CollectionUtils.isNotEmpty(reservationData.getReservationItems()))
		{
			reservationData.getReservationItems().forEach(reservationItem -> {
				if (reservationItem.getReservationPricingInfo() == null)
				{
					reservationItem.setReservationPricingInfo(createReservationPricingInfo());
				}
				final ReservationPricingInfoData reservationPricingInfo = reservationItem.getReservationPricingInfo();
				reservationPricingInfo.setItineraryPricingInfo(createItineraryPricingInfo(abstractOrderModel, reservationItem));
			});
		}
	}

	/**
	 * Creates a new Itinerary Pricing Info to hold the information related to the bundle type
	 *
	 * @param abstractOrderModel
	 *           - given Abstract Order
	 * @param reservationItem
	 *           - Reservation Item of current leg
	 * @return new itinerary pricing info
	 */
	protected ItineraryPricingInfoData createItineraryPricingInfo(final AbstractOrderModel abstractOrderModel,
			final ReservationItemData reservationItem)
	{
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		final Optional<AbstractOrderEntryModel> orderEntry = abstractOrderModel.getEntries().stream()
				.filter(e -> ProductType.FARE_PRODUCT.equals(e.getProduct().getProductType())
						|| e.getProduct() instanceof FareProductModel)
				.filter(e -> e.getActive() && e.getBundleNo() != 0
						&& e.getTravelOrderEntryInfo().getOriginDestinationRefNumber() != null && e.getTravelOrderEntryInfo()
								.getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber())

				.findAny();

		if (!orderEntry.isPresent() || Objects.isNull(orderEntry.get().getBundleTemplate()))
		{
			return itineraryPricingInfo;
		}

		final BundleTemplateModel bundleTemplateModel = orderEntry.get().getBundleTemplate();

		final BundleType bundleType = Objects.nonNull(bundleTemplateModel.getType()) ? bundleTemplateModel.getType()
				: bundleTemplateModel.getParentTemplate().getType();

		itineraryPricingInfo.setBundleType(bundleType.getCode());
		itineraryPricingInfo.setBundleTypeName(getEnumerationService().getEnumerationName(bundleType));

		return itineraryPricingInfo;
	}

	/**
	 * Creates a new Reservation Pricing Info
	 */
	protected ReservationPricingInfoData createReservationPricingInfo()
	{
		return new ReservationPricingInfoData();
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

}
