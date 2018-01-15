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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.ancillary.data.BookingBreakdownData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchHandler;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler class to populate the BookingBreakdown in the offerPricingInfos of the originDestinationOfferInfoData
 */
public class OriginDestinationBreakdownHandler extends AbstractBreakdownHandler implements AncillarySearchHandler
{

	@Override
	public void handle(final OfferRequestData offerRequestData, final OfferResponseData offerResponseData)
	{

		offerResponseData.getOfferGroups().stream()
				.filter(offerGroupData -> CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()))
				.forEach(offerGroupData -> {
					for (final OriginDestinationOfferInfoData odOfferInfo : offerGroupData.getOriginDestinationOfferInfos())
					{
						final List<OfferPricingInfoData> offerPricingInfos = getFilteredOfferPricingInfo(
								odOfferInfo.getOfferPricingInfos());

						for (final OfferPricingInfoData offerPricingInfoData : offerPricingInfos)
						{
							final BookingBreakdownData bookingBreakdown = getBookingBreakdown(offerPricingInfoData, offerRequestData,
									offerGroupData.getCode(), odOfferInfo);
							offerPricingInfoData.setBookingBreakdown(bookingBreakdown);
						}
					}
				});

	}

	/**
	 * Returns the list of offerPricingInfoData filtered by PER_LEG {@link AddToCartCriteriaType}
	 *
	 * @param offerPricingInfos
	 * @return
	 */
	protected List<OfferPricingInfoData> getFilteredOfferPricingInfo(final List<OfferPricingInfoData> offerPricingInfos)
	{
		return offerPricingInfos.stream().filter(
				opi -> opi.getTravelRestriction() != null && opi.getTravelRestriction().getAddToCartCriteria() != null && StringUtils
						.equalsIgnoreCase(opi.getTravelRestriction().getAddToCartCriteria(), AddToCartCriteriaType.PER_LEG.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * Creates and returns the bookingBreakdown for the given offerPricingInfoData
	 *
	 * @param offerPricingInfoData
	 * @param offerRequestData
	 * @param offerGroupCode
	 * @param odOfferInfo
	 * @return
	 */
	protected BookingBreakdownData getBookingBreakdown(final OfferPricingInfoData offerPricingInfoData,
			final OfferRequestData offerRequestData, final String offerGroupCode, final OriginDestinationOfferInfoData odOfferInfo)
	{
		final BookingBreakdownData odBreakdowdData = new BookingBreakdownData();

		final int quantity = getQuantity(offerRequestData, offerGroupCode, odOfferInfo,
				offerPricingInfoData.getProduct().getCode());
		odBreakdowdData.setQuantity(quantity);

		final PriceInformation priceInfo = getOfferPricingInformation(odOfferInfo, offerPricingInfoData);

		PriceData priceData = null;
		if (Optional.ofNullable(priceInfo).isPresent())
		{
			priceData = createPriceData(priceInfo);
		}
		if (Optional.ofNullable(priceData).isPresent())
		{
			odBreakdowdData.setPassengerFare(getPassengerFareData(priceData, quantity));
		}

		return odBreakdowdData;
	}

	/**
	 * Returns the quantity for the given productCode
	 *
	 * @param offerRequestData
	 * @param offerGroupCode
	 * @param originDestinationOfferInfoData
	 * @param productCode
	 * @return
	 */
	protected Integer getQuantity(final OfferRequestData offerRequestData, final String offerGroupCode,
			final OriginDestinationOfferInfoData originDestinationOfferInfoData, final String productCode)
	{
		Integer quantity = 0;

		final Optional<OfferGroupData> offerGroupData = offerRequestData.getSelectedOffers().getOfferGroups().stream()
				.filter(offerGroup -> offerGroup.getCode().equals(offerGroupCode)).findFirst();
		if (!offerGroupData.isPresent())
		{
			return quantity;
		}

		final List<OriginDestinationOfferInfoData> filteredODOfferInfos = offerGroupData.get().getOriginDestinationOfferInfos()
				.stream()
				.filter(odOfferInfo -> odOfferInfo.getTravelRouteCode().equals(originDestinationOfferInfoData.getTravelRouteCode())
						&& odOfferInfo.getOriginDestinationRefNumber() == originDestinationOfferInfoData.getOriginDestinationRefNumber()
						&& TransportOfferingUtils.compareTransportOfferings(originDestinationOfferInfoData.getTransportOfferings(),
								odOfferInfo.getTransportOfferings()))
				.collect(Collectors.toList());

		final List<OfferPricingInfoData> opiListForProduct = filteredODOfferInfos.stream()
				.flatMap(odOfferInfo -> odOfferInfo.getOfferPricingInfos().stream())
				.filter(opi -> opi.getProduct().getCode().equals(productCode)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(opiListForProduct))
		{
			quantity += Integer.valueOf(opiListForProduct.stream().mapToInt(opi -> opi.getBookingBreakdown().getQuantity()).sum());
		}

		return quantity;
	}

	/**
	 * Returns the offerPricingInformation for the given offerPricingInfoData
	 *
	 * @param offerGroupCode
	 * @param odOfferInfo
	 * @param offerPricingInfoData
	 * @return
	 * @deprecated since version 4 use {@link #getOfferPricingInformation(odOfferInfo,offerPricingInfoData)}
	 */
	@Deprecated
	protected PriceInformation getOfferPricingInformation(final String offerGroupCode,
			final OriginDestinationOfferInfoData odOfferInfo,
			final OfferPricingInfoData offerPricingInfoData)
	{
		return getOfferPricingInformation(odOfferInfo, offerPricingInfoData);
	}

	/**
	 * Returns the offerPricingInformation for the given offerPricingInfoData
	 * 
	 * @param odOfferInfo
	 * @param offerPricingInfoData
	 * @return
	 */
	protected PriceInformation getOfferPricingInformation(final OriginDestinationOfferInfoData odOfferInfo,
			final OfferPricingInfoData offerPricingInfoData)
	{
		PriceInformation priceInfo = null;
		final String productCode = offerPricingInfoData.getProduct().getCode();
		if (AddToCartCriteriaType.PER_LEG.getCode().equals(offerPricingInfoData.getTravelRestriction().getAddToCartCriteria()))
		{
			priceInfo = getPriceInformation(productCode, PriceRowModel.TRAVELROUTECODE, odOfferInfo.getTravelRouteCode());
			if (!Optional.ofNullable(priceInfo).isPresent())
			{
				priceInfo = getPriceInformationFromTransportOfferingOrSector(odOfferInfo, productCode);
			}

		}
		//If still no price found, get the default price.
		if (!Optional.ofNullable(priceInfo).isPresent())
		{
			priceInfo = getPriceInformation(productCode, null, null);
		}

		return priceInfo;
	}

}
