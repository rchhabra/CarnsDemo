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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.SelectedOffersData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate SelectedOffers in OfferRequestData.
 */
public class OffersRequestHandler implements AncillarySearchRequestHandler
{
	private static final Logger LOGGER = Logger.getLogger(OffersRequestHandler.class);

	private SessionService sessionService;

	private final Function<CategoryData, OfferGroupData> categoryToOfferGroupMapper = categoryData -> {
		final OfferGroupData offerGroupData = new OfferGroupData();
		try
		{
			BeanUtils.copyProperties(offerGroupData, categoryData);
		}
		catch (final IllegalAccessException | InvocationTargetException exec)
		{
			LOGGER.debug("Exception when converting categoryData to offerGroupData", exec);
		}
		return offerGroupData;
	};

	/**
	 * This method creates a list of OfferGroupData from reservationData and assigns to the SelelcteOffersData in
	 * OfferRequestData.
	 */
	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		//Get Unique categoryData objects from ReservationData.
		final List<CategoryData> uniqueCategories = new ArrayList<>();

		final List<OfferPricingInfoData> offerPricingInfos = reservationData.getReservationItems().stream()
				.flatMap(reservationItem -> reservationItem.getReservationPricingInfo().getOriginDestinationOfferInfos().stream())
				.flatMap(odOfferInfo -> odOfferInfo.getOfferPricingInfos().stream()).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(reservationData.getOfferPricingInfos()))
		{
			offerPricingInfos.addAll(reservationData.getOfferPricingInfos());
		}

		offerPricingInfos.stream()
				.filter(offerPricingInfo -> !isCategoryAvailable(uniqueCategories,
						offerPricingInfo.getProduct().getCategories().stream().findFirst().get()))
				.forEach(offerPricingInfo -> uniqueCategories
						.add(offerPricingInfo.getProduct().getCategories().stream().findFirst().get()));

		//Convert CatergoryData to OfferGroupData.
		final List<OfferGroupData> offerGroups = uniqueCategories.stream().map(categoryToOfferGroupMapper)
				.collect(Collectors.<OfferGroupData> toList());

		//Set offerGroups in selectedOffers.
		final SelectedOffersData selectedOffersData = new SelectedOffersData();
		selectedOffersData.setOfferGroups(offerGroups);
		offerRequestData.setSelectedOffers(selectedOffersData);
	}


	/**
	 * Returns true if the categoryData of same code exists in uniqueCategories.
	 *
	 * @param uniqueCategories
	 *           list of CategoriesData
	 * @param categoryData
	 *           CategoryData of OfferPricingInfo
	 * @return a boolean, true if exists else false.
	 */
	protected boolean isCategoryAvailable(final List<CategoryData> uniqueCategories, final CategoryData categoryData)
	{
		return uniqueCategories.stream().anyMatch(offerGroup -> offerGroup.getCode().equals(categoryData.getCode()));
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
