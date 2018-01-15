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

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;	
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Handler class to populate OfferPricingInfoData for each category of products available to offer as ancillaries.
 */
public class OfferGroupOfferPricingInfoRequestHandler implements AncillarySearchRequestHandler
{

	private TravelRestrictionFacade travelRestrictionFacade;

	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		final List<OfferGroupData> filteredOfferGroups = getFilteredOfferGroups(offerRequestData);
		for (final OfferGroupData offerGroupData : filteredOfferGroups)
		{
			List<OfferPricingInfoData> offerPricingInfos = getOfferPricingInfos(reservationData);

			if (offerPricingInfos == null)
			{
				offerPricingInfos = Collections.emptyList();
			}

			if (CollectionUtils.isEmpty(offerGroupData.getOfferPricingInfos()))
			{
				offerGroupData.setOfferPricingInfos(offerPricingInfos);
			}
			else
			{
				offerGroupData.getOfferPricingInfos().addAll(offerPricingInfos);
			}
		}
	}

	/**
	 * Filters the list of offerGroups based on their {@link AddToCartCriteriaType}, returning the offerGroups of type
	 * PER_BOOKING or PER_PAX
	 * @param reservationData
	 * @param offerRequestData
	 * @return
	 * @deprecated since version 4 use {@link #getFilteredOfferGroups(offerRequestData)}
	 */
	@Deprecated
	protected List<OfferGroupData> getFilteredOfferGroups(ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		return getFilteredOfferGroups(offerRequestData);
	}

	/**
	 * Filters the list of offerGroups based on their {@link AddToCartCriteriaType}, returning the offerGroups of type
	 * PER_BOOKING or PER_PAX
	 * 
	 * @param offerRequestData
	 * @return
	 */
	protected List<OfferGroupData> getFilteredOfferGroups(final OfferRequestData offerRequestData)
	{
		final List<OfferGroupData> offerGroups = offerRequestData.getSelectedOffers().getOfferGroups();

		return offerGroups.stream().filter(offerGroup -> {
			final TravelRestrictionData travelRestrictionData = getTravelRestrictionFacade()
					.getTravelRestrictionForCategory(offerGroup.getCode());

			return travelRestrictionData != null && checkAddToCartCriteria(travelRestrictionData.getAddToCartCriteria());

		}).collect(Collectors.toList());
	}

	/**
	 * Returns the list of offerPricingInfos
	 *
	 * @param reservationData
	 * @return
	 */
	protected List<OfferPricingInfoData> getOfferPricingInfos(final ReservationData reservationData)
	{
		return reservationData.getOfferPricingInfos().stream().filter(offerPricingInfo -> {
			final Optional<CategoryData> categoryData = offerPricingInfo.getProduct().getCategories().stream().findFirst();
			final String addToCartCriteria = getTravelRestrictionFacade()
					.getAddToCartCriteria(offerPricingInfo.getProduct().getCode());
			return categoryData.isPresent() && checkAddToCartCriteria(addToCartCriteria);
		}).collect(Collectors.toList());
	}

	/**
	 * Returns the list of offerPricingInfos
	 *
	 * @param reservationData
	 * @param offerGroups
	 * @return
	 *
	 * @deprecated Deprecated since version 2.0. Replaced by {@link #getOfferPricingInfos(ReservationData)}
	 */

	@Deprecated
	protected List<OfferPricingInfoData> getOfferPricingInfos(final ReservationData reservationData,
			final List<OfferGroupData> offerGroups)
	{
		return reservationData.getOfferPricingInfos().stream().filter(offerPricingInfo -> {
			final Optional<CategoryData> categoryData = offerPricingInfo.getProduct().getCategories().stream().findFirst();
			final TravelRestrictionData travelRestrictionData = offerPricingInfo.getTravelRestriction();
			return categoryData.isPresent() && checkTravelRestriction(travelRestrictionData);
		}).collect(Collectors.toList());
	}

	/**
	 * Checks if the given travelRestrictionData has the {@link AddToCartCriteriaType} of type PER_BOOKING or PER_PAX
	 *
	 * @param travelRestrictionData
	 * @return
	 *
	 * @deprecated Deprecated since version 2.0. Please use {@link #checkAddToCartCriteria(String)}
	 */
	@Deprecated
	protected boolean checkTravelRestriction(final TravelRestrictionData travelRestrictionData)
	{
		return travelRestrictionData != null && StringUtils.isNotBlank(travelRestrictionData.getAddToCartCriteria())
				&& (StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
						AddToCartCriteriaType.PER_BOOKING.getCode())
				|| StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
						AddToCartCriteriaType.PER_PAX.getCode()));
	}

	/**
	 * Checks if the given travelRestrictionData has the {@link AddToCartCriteriaType} of type PER_BOOKING or PER_PAX
	 *
	 * @param addToCartCriteria
	 * @return
	 */
	protected boolean checkAddToCartCriteria(final String addToCartCriteria)
	{
		return StringUtils.isNotBlank(addToCartCriteria)
				&& (StringUtils.equalsIgnoreCase(addToCartCriteria, AddToCartCriteriaType.PER_BOOKING.getCode())
						|| StringUtils.equalsIgnoreCase(addToCartCriteria, AddToCartCriteriaType.PER_PAX.getCode()));
	}

	/**
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * @param travelRestrictionFacade
	 *           the travelRestrictionFacade to set
	 */
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}

}
