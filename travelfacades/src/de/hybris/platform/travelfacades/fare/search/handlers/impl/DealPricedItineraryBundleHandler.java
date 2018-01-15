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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.DealOriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for populating the
 * {@link ItineraryPricingInfoData} for each {@link PricedItineraryData} on {@link FareSelectionData}
 */
public class DealPricedItineraryBundleHandler implements FareSearchHandler
{
	private TravelBundleTemplateService travelBundleTemplateService;
	private Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter;
	private EnumerationService enumerationService;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		if (Objects.isNull(fareSearchRequestData) || CollectionUtils.isEmpty(fareSearchRequestData.getOriginDestinationInfo())
				|| Objects.isNull(fareSelectionData) || CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			return;
		}

		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			final Optional<DealOriginDestinationInfoData> dealODInfoData = fareSearchRequestData.getOriginDestinationInfo().stream()
					.filter(odInfo -> odInfo.getReferenceNumber() == pricedItinerary.getOriginDestinationRefNumber())
					.map(DealOriginDestinationInfoData.class::cast).findAny();

			if (dealODInfoData.isPresent())
			{
				populateItineraryPricingInformations(pricedItinerary, dealODInfoData.get());
			}
		}

		fareSelectionData.getPricedItineraries().forEach(
				pricedItinerary -> pricedItinerary.setAvailable(CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos())
						&& pricedItinerary.getItineraryPricingInfos().stream().anyMatch(ItineraryPricingInfoData::isAvailable)));

	}

	/**
	 * Populates the ItineraryPricingInfo for each pricedItinerary, using the RouteBundleTemplate correponding to the id
	 * specified in the dealOriginDestinationInfoData.
	 *
	 * @param pricedItinerary
	 *           the pricedItinerary
	 * @param dealOriginDestinationInfoData
	 *           the dealOriginDestinationInfoData
	 */
	protected void populateItineraryPricingInformations(final PricedItineraryData pricedItinerary,
			final DealOriginDestinationInfoData dealOriginDestinationInfoData)
	{
		final List<TransportOfferingData> transportOfferings = getTransportOfferings(pricedItinerary);
		final List<ItineraryPricingInfoData> itineraryPricingInfos = getItineraryPricingInfos(dealOriginDestinationInfoData,
				transportOfferings);

		pricedItinerary.setItineraryPricingInfos(itineraryPricingInfos);

		pricedItinerary.setAvailable(CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos())
				&& pricedItinerary.getItineraryPricingInfos().stream()
				.anyMatch(ItineraryPricingInfoData::isAvailable));
	}

	/**
	 * Returns the list of ItineraryPricingInfoData populated from the BundleTemplate corresponding to the id specified
	 * in the DealOriginDestinationInfoData
	 *
	 * @param dealOriginDestinationInfoData
	 *           as the dealOriginDestinationInfoData
	 * @param transportOfferings
	 *           as the transportOfferings
	 *
	 * @return a list of ItineraryPricingInfoData
	 */
	protected List<ItineraryPricingInfoData> getItineraryPricingInfos(
			final DealOriginDestinationInfoData dealOriginDestinationInfoData, final List<TransportOfferingData> transportOfferings)
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfoDatas = new ArrayList<>();

		final BundleTemplateModel bundleTemplateModel = getTravelBundleTemplateService()
				.getBundleTemplateForCode(dealOriginDestinationInfoData.getPackageId());
		populateItineraryPricingInfosFromBundle(bundleTemplateModel, transportOfferings, itineraryPricingInfoDatas);

		setItineraryPricingInfoAvailability(itineraryPricingInfoDatas);

		return itineraryPricingInfoDatas;
	}

	/**
	 * Populates the list of itineraryPricingInfoDatas from the given bundleTemplateModel and transportOfferings
	 *
	 * @param bundleTemplateModel
	 *           as the bundleTemplateModel
	 * @param transportOfferings
	 *           as the list of transportOfferings
	 * @param itineraryPricingInfoDatas
	 */
	protected void populateItineraryPricingInfosFromBundle(final BundleTemplateModel bundleTemplateModel,
			final List<TransportOfferingData> transportOfferings, final List<ItineraryPricingInfoData> itineraryPricingInfoDatas)
	{
		if (Objects.isNull(bundleTemplateModel))
		{
			return;
		}
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();
		itineraryPricingInfoData.setAvailable(true);

		final TravelBundleTemplateData bundleTemplateData = getTravelBundleTemplateConverter().convert(bundleTemplateModel);
		bundleTemplateData.setTransportOfferings(transportOfferings);
		itineraryPricingInfoData.setBundleTemplates(Collections.singletonList(bundleTemplateData));

		itineraryPricingInfoData.setBundleType(bundleTemplateModel.getType().getCode());
		itineraryPricingInfoData.setBundleTypeName(getEnumerationService().getEnumerationName(bundleTemplateModel.getType()));

		if (!itineraryPricingInfoDatas.contains(itineraryPricingInfoData))
		{
			itineraryPricingInfoDatas.add(itineraryPricingInfoData);
		}
	}

	/**
	 * Sets the availability of the ItineraryPricingInfoData. An itineraryPricingInfo is unavailable if no bundles are
	 * populated
	 *
	 * @param itineraryPricingInfoDatas
	 *           as the itineraryPricingInfoDatas
	 */
	protected void setItineraryPricingInfoAvailability(final List<ItineraryPricingInfoData> itineraryPricingInfoDatas)
	{
		for (final ItineraryPricingInfoData itineraryPricingInfoData : itineraryPricingInfoDatas)
		{
			if (CollectionUtils.isEmpty(itineraryPricingInfoData.getBundleTemplates()))
			{
				itineraryPricingInfoData.setAvailable(false);
			}
		}
	}

	/**
	 * Returns the list of TransportOfferingDatas from the given PricedItinerary. The method gets the ItineraryData from
	 * the pricedItinerary and then gets a list of OriginDestinationOption from the Itinerary object. An empty list if
	 * return if the list if empty otherwise a list of Transport Offerings is returned.
	 *
	 * @param pricedItinerary
	 *           as the pricedItinerary
	 *
	 * @return a list of TransportOfferingData
	 */
	protected List<TransportOfferingData> getTransportOfferings(final PricedItineraryData pricedItinerary)
	{
		final ItineraryData itinerary = pricedItinerary.getItinerary();
		final List<OriginDestinationOptionData> originDestinationOptions = itinerary.getOriginDestinationOptions();
		if (CollectionUtils.isEmpty(originDestinationOptions))
		{
			return Collections.emptyList();
		}

		final OriginDestinationOptionData originDestinationOptionData = originDestinationOptions.get(0);
		final List<TransportOfferingData> transportOfferings = originDestinationOptionData.getTransportOfferings();
		if (CollectionUtils.isEmpty(transportOfferings))
		{
			return Collections.emptyList();
		}
		return transportOfferings;
	}

	/**
	 * Gets travel bundle template converter.
	 *
	 * @return the travel bundle template converter
	 */
	protected Converter<BundleTemplateModel, TravelBundleTemplateData> getTravelBundleTemplateConverter()
	{
		return travelBundleTemplateConverter;
	}

	/**
	 * Sets travel bundle template converter.
	 *
	 * @param travelBundleTemplateConverter
	 *           the travel bundle template converter
	 */
	@Required
	public void setTravelBundleTemplateConverter(
			final Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter)
	{
		this.travelBundleTemplateConverter = travelBundleTemplateConverter;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 *           the enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * Gets travel bundle template service.
	 *
	 * @return the travel bundle template service
	 */
	protected TravelBundleTemplateService getTravelBundleTemplateService()
	{
		return travelBundleTemplateService;
	}

	/**
	 * Sets travel bundle template service.
	 *
	 * @param travelBundleTemplateService
	 *           the travel bundle template service
	 */
	@Required
	public void setTravelBundleTemplateService(final TravelBundleTemplateService travelBundleTemplateService)
	{
		this.travelBundleTemplateService = travelBundleTemplateService;
	}
}
