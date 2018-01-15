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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.bundle.TravelBundleTemplateService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.CabinClassService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for populating the {@link
 * ItineraryPricingInfoData} for each {@link PricedItineraryData} on {@link FareSelectionData}
 */
public class PricedItineraryBundleHandler implements FareSearchHandler
{
	private static final Logger LOGGER = Logger.getLogger(PricedItineraryBundleHandler.class);

	private static final String FARE_SELECTION_FARE_OPTION_LIST = "fareselection.fareoptionlist";
	private static final String FARE_SELECTION_UPGRADE_ALLOWED = "fareselection.upgrade.allowed";

	private TravelBundleTemplateService travelBundleTemplateService;
	private TravelRouteService travelRouteService;
	private TransportOfferingService transportOfferingService;
	private Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter;
	private CabinClassService cabinClassService;
	private EnumerationService enumerationService;
	private TravelRulesService travelRulesService;
	private UserService userService;

	/**
	 * Populates ItinerarypricingInformation to pricedItinerary For each transport offering there must be always only one bundle
	 * per bundle type.
	 */
	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			final CabinClassModel cabinClassModel = getCabinClass(fareSearchRequestData);
			populateItineraryPricingInformations(pricedItinerary, fareSearchRequestData, cabinClassModel);
			final List<ItineraryPricingInfoData> itineraryPricingInfos = pricedItinerary.getItineraryPricingInfos();
			final int allowedNoOfPricingInfo = Integer.parseInt(getParameterValue(FARE_SELECTION_FARE_OPTION_LIST));
			final boolean fareSelectionUpgradeAllowed = Boolean.parseBoolean(getParameterValue(FARE_SELECTION_UPGRADE_ALLOWED));
			if (CollectionUtils.isNotEmpty(itineraryPricingInfos) && fareSelectionUpgradeAllowed
					&& itineraryPricingInfos.size() < allowedNoOfPricingInfo)
			{
				populateUpgradeableBundles(fareSearchRequestData, pricedItinerary, cabinClassModel, itineraryPricingInfos,
						allowedNoOfPricingInfo);
			}
		}
		showBundleTemplates(fareSelectionData, fareSearchRequestData);
	}

	/**
	 * Shows bundles templates based on rules
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 */
	protected void showBundleTemplates(final FareSelectionData fareSelectionData,
			final FareSearchRequestData fareSearchRequestData)
	{
		final List<String> bundlesToShow = getTravelRulesService().showBundleTemplates(fareSearchRequestData);
		showBundles(bundlesToShow, fareSelectionData);
	}

	/**
	 * Filter bundles.
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @deprecated since version 4.0
	 */
	@Deprecated
	protected void filterBundles(final FareSelectionData fareSelectionData, final FareSearchRequestData fareSearchRequestData)
	{
		final List<String> excludedBundleTypes = getTravelRulesService().filterBundles(fareSearchRequestData,
				getUserService().getCurrentUser());

		excludedBundleTypes.forEach(excludedBundleType -> fareSelectionData.getPricedItineraries().forEach(pricedItinerary -> {
			for (final ItineraryPricingInfoData itineraryPricingInfo : pricedItinerary.getItineraryPricingInfos())
			{
				if (StringUtils.equalsIgnoreCase(excludedBundleType, itineraryPricingInfo.getBundleType()))
				{
					pricedItinerary.getItineraryPricingInfos().remove(itineraryPricingInfo);
					break;
				}
			}
			pricedItinerary.setAvailable(CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos())
					&& pricedItinerary.getItineraryPricingInfos().stream().anyMatch(ItineraryPricingInfoData::isAvailable));
		}));
	}

	/**
	 * Removes all the {@link ItineraryPricingInfoData} that have the ignoreRule flag set to false and are not present in the
	 * bundleToShow list
	 *
	 * @param bundlesToShow
	 * 		the bundles to show
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void showBundles(final List<String> bundlesToShow, final FareSelectionData fareSelectionData)
	{
		fareSelectionData.getPricedItineraries().forEach(pricedItinerary -> {
			final List<ItineraryPricingInfoData> itinerariesToRemove = new LinkedList<>();
			for (final ItineraryPricingInfoData itineraryPricingInfo : pricedItinerary.getItineraryPricingInfos())
			{
				if (itineraryPricingInfo.getBundleTemplates().stream()
						.anyMatch(travelBundleTemplateData -> !travelBundleTemplateData.isIgnoreRules()))
				{
					final Set<String> bundleTemplatesId = itineraryPricingInfo.getBundleTemplates().stream()
							.filter(travelBundleTemplateData -> !travelBundleTemplateData.isIgnoreRules())
							.map(BundleTemplateData::getId).collect(Collectors.toSet());
					if (!bundlesToShow.containsAll(bundleTemplatesId))
					{
						itinerariesToRemove.add(itineraryPricingInfo);
					}
				}
			}
			pricedItinerary.getItineraryPricingInfos().removeAll(itinerariesToRemove);
			pricedItinerary.setAvailable(CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos())
					&& pricedItinerary.getItineraryPricingInfos().stream().anyMatch(ItineraryPricingInfoData::isAvailable));
		});
	}

	/**
	 * Populate upgradeable bundles.
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @param itineraryPricingInfos
	 * 		the itinerary pricing infos
	 * @param allowedNoOfPricingInfo
	 * 		the allowed no of pricing info
	 */
	protected void populateUpgradeableBundles(final FareSearchRequestData fareSearchRequestData,
			final PricedItineraryData pricedItinerary, final CabinClassModel cabinClassModel,
			final List<ItineraryPricingInfoData> itineraryPricingInfos, final int allowedNoOfPricingInfo)
	{
		final List<ItineraryPricingInfoData> itineraryPricingInfosIncludingUpgrade = new ArrayList<>();
		itineraryPricingInfosIncludingUpgrade.addAll(itineraryPricingInfos);
		final int noOfUpgradePricingInfo = allowedNoOfPricingInfo - itineraryPricingInfos.size();
		CabinClassModel cabinClass = cabinClassModel;
		for (int i = 1; i <= noOfUpgradePricingInfo; i++)
		{
			final Integer cabinClassIndex = cabinClass.getCabinClassIndex();
			final CabinClassModel upgradedCabinClass = getCabinClassService().getCabinClass((cabinClassIndex + 1));
			if (upgradedCabinClass == null)
			{
				break;
			}
			populateItineraryPricingInformations(pricedItinerary, fareSearchRequestData, upgradedCabinClass);
			if (CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos()))
			{
				itineraryPricingInfosIncludingUpgrade.addAll(pricedItinerary.getItineraryPricingInfos());
			}
			cabinClass = upgradedCabinClass;
		}
		pricedItinerary.setItineraryPricingInfos(itineraryPricingInfosIncludingUpgrade);
	}

	/**
	 * For each pricedItinerary populate PricingInfo as below Check if the PricingItinerary is of multi-sector, if yes Check if
	 * there are any Bundles configured at route level, if yes, populate the pricing information with the bundles. if not, or if
	 * the PricingItinerary is not multi-sector then Check if there are any Bundles configured at transport offering level, if yes,
	 * get the common bundles for all transport offerings and populate them in Pricing information. if no bundles configured (or
	 * atleast one of the transport offerings in multi-sector has no bundles) then, Check if all the sectors in the itinerary has
	 * bundles configured, if yes, get the common bundles for all and populate the pricing information. if still no bundles found,
	 * populates the default bundle templates.
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param cabinClassModel
	 * 		the cabin class model
	 */
	protected void populateItineraryPricingInformations(final PricedItineraryData pricedItinerary,
			final FareSearchRequestData fareSearchRequestData, final CabinClassModel cabinClassModel)
	{
		final TravelRouteModel travelRouteModel = getTravelRoute(pricedItinerary);
		final List<TransportOfferingData> transportOfferings = getTransportOfferings(pricedItinerary);

		final List<ItineraryPricingInfoData> itineraryPricingInfos;
		// Multi-sector route
		if (transportOfferings.size() > 1)
		{
			itineraryPricingInfos = getItineraryPricingInfosForMultiSector(travelRouteModel, cabinClassModel, transportOfferings);
		}
		// Point to point route
		else
		{
			itineraryPricingInfos = getItineraryPricingInfosForPointToPoint(travelRouteModel, cabinClassModel, transportOfferings,
					fareSearchRequestData);
		}
		pricedItinerary.setItineraryPricingInfos(itineraryPricingInfos);
		pricedItinerary
				.setAvailable(pricedItinerary.getItineraryPricingInfos().stream().anyMatch(ItineraryPricingInfoData::isAvailable));
	}

	/**
	 * This method populates the bundles for multi-sector itinerary.
	 *
	 * @param travelRouteModel
	 * 		the travel route model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @param transportOfferings
	 * 		the transport offerings
	 * @return List<ItineraryPricingInfoData> itinerary pricing infos for multi sector
	 */
	protected List<ItineraryPricingInfoData> getItineraryPricingInfosForMultiSector(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel, final List<TransportOfferingData> transportOfferings)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Processing to get Itinerary Pricing Info for " + transportOfferings.size()
					+ " multi sector Transport Offerings for route with code " + travelRouteModel.getCode());
		}

		final List<BundleTemplateModel> bundleTemplatesForRoute = getTravelBundleTemplateService()
				.getBundleTemplates(travelRouteModel, cabinClassModel);
		final Map<BundleType, List<BundleTemplateModel>> bundleMap = new HashMap<>();
		final List<ItineraryPricingInfoData> itineraryPricingInfoDatas = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(bundleTemplatesForRoute))
		{
			populateBundleMap(bundleTemplatesForRoute, bundleMap, false);
			populateItineraryPricingInfosFromBundleMap(bundleMap, transportOfferings, itineraryPricingInfoDatas);
		}
		else
		{
			for (final TransportOfferingData transportOfferingData : transportOfferings)
			{
				final TransportOfferingModel transportOfferingModel = getTransportOfferingService()
						.getTransportOffering(transportOfferingData.getCode());
				final List<BundleTemplateModel> bundleTemplatesForTransportOfferings = getTravelBundleTemplateService()
						.getBundleTemplates(transportOfferingModel, cabinClassModel);
				populateBundleMap(bundleTemplatesForTransportOfferings, bundleMap, true);

				final List<BundleTemplateModel> bundleTemplatesForSector = getTravelBundleTemplateService()
						.getBundleTemplates(transportOfferingModel.getTravelSector(), cabinClassModel);
				populateBundleMap(bundleTemplatesForSector, bundleMap, true);

				final List<BundleTemplateModel> defaultBundleTemplates = getTravelBundleTemplateService()
						.getDefaultBundleTemplates(cabinClassModel);
				populateBundleMap(defaultBundleTemplates, bundleMap, true);
				populateItineraryPricingInfosFromBundleMap(bundleMap, Collections.singletonList(transportOfferingData),
						itineraryPricingInfoDatas);
			}
			removeNonCommonBundles(itineraryPricingInfoDatas, transportOfferings.size());
		}
		setItineraryPricingInfoAvailability(itineraryPricingInfoDatas);
		return itineraryPricingInfoDatas;
	}

	/**
	 * This method will mark an itinerary pricing info as unavailable if no bundles are populated
	 *
	 * @param itineraryPricingInfoDatas
	 * 		the itinerary pricing info datas
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
	 * This method is only called for multisector with no route-level bundle. In this case it is only possible to have n number of
	 * bundles for each bundle type, where n is number of transport offerings
	 *
	 * @param itineraryPricingInfoDatas
	 * 		the itinerary pricing info datas
	 * @param expectedSize
	 * 		the expected size
	 */
	protected void removeNonCommonBundles(final List<ItineraryPricingInfoData> itineraryPricingInfoDatas, final int expectedSize)
	{
		final List<ItineraryPricingInfoData> filteredItineraries = new ArrayList<>();
		for (final ItineraryPricingInfoData itineraryPricingInfoData : itineraryPricingInfoDatas)
		{
			if (CollectionUtils.isEmpty(itineraryPricingInfoData.getBundleTemplates()))
			{
				continue;
			}

			if (itineraryPricingInfoData.getBundleTemplates().size() == expectedSize)
			{
				filteredItineraries.add(itineraryPricingInfoData);
			}
		}
		itineraryPricingInfoDatas.clear();
		itineraryPricingInfoDatas.addAll(filteredItineraries);
	}

	/**
	 * This method checks if any bundleTemplates configured for transport offering, if yes, populates the bundles in
	 * ItineraryPricingInfoData or else, checks if bundles are configured at travelSectors level, if yes populates the bundles in
	 * ItineraryPricingInfoData or else populates the default bundle templates.
	 *
	 * @param travelRouteModel
	 * 		the travel route model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @return List<ItineraryPricingInfoData> itinerary pricing infos for point to point
	 */
	protected List<ItineraryPricingInfoData> getItineraryPricingInfosForPointToPoint(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel, final List<TransportOfferingData> transportOfferings,
			final FareSearchRequestData fareSearchRequestData)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Processing to get Itinerary Pricing Info for " + transportOfferings.size()
					+ " point to point Transport Offerings for route with code " + travelRouteModel.getCode());
		}

		final List<ItineraryPricingInfoData> itineraryPricingInfoDatas = new ArrayList<>();
		final TransportOfferingData transportOfferingData = transportOfferings.get(0);
		final Map<BundleType, List<BundleTemplateModel>> bundleMap = new HashMap<>();
		final TransportOfferingModel transportOfferingModel = getTransportOfferingService()
				.getTransportOffering(transportOfferingData.getCode());
		final List<BundleTemplateModel> bundleTemplatesForTransportOfferings = getTravelBundleTemplateService()
				.getBundleTemplates(transportOfferingModel, cabinClassModel);

		if (CollectionUtils.isNotEmpty(bundleTemplatesForTransportOfferings))
		{
			populateBundleMap(bundleTemplatesForTransportOfferings, bundleMap, false);
		}
		else
		{
			populateBundleMapForSectorRouteOrDefault(travelRouteModel, cabinClassModel, bundleMap, transportOfferingModel);
		}

		populateItineraryPricingInfosFromBundleMap(bundleMap, Collections.singletonList(transportOfferingData),
				itineraryPricingInfoDatas);
		setItineraryPricingInfoAvailability(itineraryPricingInfoDatas);

		if (LOGGER.isDebugEnabled())
		{
			final StringBuilder passengerTypeLogMessage = new StringBuilder();
			fareSearchRequestData.getPassengerTypes().forEach(pt -> {
				passengerTypeLogMessage.append(pt.getQuantity());
				passengerTypeLogMessage.append("x");
				passengerTypeLogMessage.append(pt.getPassengerType().getName());
				passengerTypeLogMessage.append(", ");
			});

			LOGGER.debug("There are a total of " + itineraryPricingInfoDatas.size() + " itineraryPricingInfoDatas available for "
					+ passengerTypeLogMessage.toString()
					+ (fareSearchRequestData.getOriginDestinationInfo().size() == 1
					? TravelDateUtils.convertDateToStringDate(
					fareSearchRequestData.getOriginDestinationInfo().get(0).getDepartureTime(),
					TravelservicesConstants.DATE_PATTERN)
					: (TravelDateUtils.convertDateToStringDate(
					fareSearchRequestData.getOriginDestinationInfo().get(0).getDepartureTime(),
					TravelservicesConstants.DATE_PATTERN)
					+ " - "
					+ (TravelDateUtils.convertDateToStringDate(
					fareSearchRequestData.getOriginDestinationInfo().get(1).getDepartureTime(),
					TravelservicesConstants.DATE_PATTERN))))
					+ ", " + fareSearchRequestData.getOriginDestinationInfo().get(0).getDepartureLocation() + " - "
					+ fareSearchRequestData.getOriginDestinationInfo().get(0).getArrivalLocation() + ", "
					+ fareSearchRequestData.getTripType().name() + ", cabin class: "
					+ fareSearchRequestData.getTravelPreferences().getCabinPreference());
		}

		return itineraryPricingInfoDatas;
	}

	/**
	 * Populate bundle map for sector route or default.
	 *
	 * @param travelRouteModel
	 * 		the travel route model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @param bundleMap
	 * 		the bundle map
	 * @param transportOfferingModel
	 * 		the transport offering model
	 */
	protected void populateBundleMapForSectorRouteOrDefault(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel, final Map<BundleType, List<BundleTemplateModel>> bundleMap,
			final TransportOfferingModel transportOfferingModel)
	{
		final List<BundleTemplateModel> bundleTemplatesForSector = getTravelBundleTemplateService()
				.getBundleTemplates(transportOfferingModel.getTravelSector(), cabinClassModel);

		if (CollectionUtils.isNotEmpty(bundleTemplatesForSector))
		{
			populateBundleMap(bundleTemplatesForSector, bundleMap, false);
		}
		else
		{
			populateBundleMapForRouteOrDefault(travelRouteModel, cabinClassModel, bundleMap, bundleTemplatesForSector);
		}
	}

	/**
	 * Populate bundle map for route or default.
	 *
	 * @param travelRouteModel
	 * 		the travel route model
	 * @param cabinClassModel
	 * 		the cabin class model
	 * @param bundleMap
	 * 		the bundle map
	 * @param bundleTemplatesForSector
	 * 		the bundle templates for sector
	 */
	protected void populateBundleMapForRouteOrDefault(final TravelRouteModel travelRouteModel,
			final CabinClassModel cabinClassModel, final Map<BundleType, List<BundleTemplateModel>> bundleMap,
			final List<BundleTemplateModel> bundleTemplatesForSector)
	{
		final List<BundleTemplateModel> bundleTemplatesForRoute = getTravelBundleTemplateService()
				.getBundleTemplates(travelRouteModel, cabinClassModel);
		if (CollectionUtils.isNotEmpty(bundleTemplatesForRoute))
		{
			populateBundleMap(bundleTemplatesForRoute, bundleMap, false);
		}
		else
		{
			final List<BundleTemplateModel> defaultBundleTemplates = getTravelBundleTemplateService()
					.getDefaultBundleTemplates(cabinClassModel);
			if (CollectionUtils.isNotEmpty(defaultBundleTemplates))
			{
				populateBundleMap(defaultBundleTemplates, bundleMap, false);
			}
		}
	}

	/**
	 * Instantiate as many itinerary pricing infos as bundletypes and assign them to the itinerary pricing info
	 *
	 * @param bundleMap
	 * 		the bundle map
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param itineraryPricingInfoDatas
	 * 		the itinerary pricing info datas
	 */
	protected void populateItineraryPricingInfosFromBundleMap(final Map<BundleType, List<BundleTemplateModel>> bundleMap,
			final List<TransportOfferingData> transportOfferings, final List<ItineraryPricingInfoData> itineraryPricingInfoDatas)
	{
		for (final Map.Entry<BundleType, List<BundleTemplateModel>> entry : bundleMap.entrySet())
		{
			final ItineraryPricingInfoData itineraryPricingInfoData = getItineraryPricingInfoDataForType(itineraryPricingInfoDatas,
					entry);

			// Ignore if there's already a bundle for the same bundle type and transport offerings
			// Convert each bundle template
			final List<TravelBundleTemplateData> bundleTemplates = itineraryPricingInfoData.getBundleTemplates();

			for (final BundleTemplateModel bundleTemplateModel : entry.getValue())
			{
				if (!canAddBundle(itineraryPricingInfoData, transportOfferings, bundleTemplateModel.getType()))
				{
					continue;
				}

				final TravelBundleTemplateData bundleTemplateData = getTravelBundleTemplateConverter().convert(bundleTemplateModel);
				bundleTemplateData.setTransportOfferings(transportOfferings);
				bundleTemplates.add(bundleTemplateData);
			}

			final boolean promotional = itineraryPricingInfoData.getBundleTemplates().stream()
					.anyMatch(TravelBundleTemplateData::isPromotional);
			itineraryPricingInfoData.setPromotional(promotional);

			itineraryPricingInfoData.setBundleType(entry.getKey().getCode());
			itineraryPricingInfoData.setBundleTypeName(getEnumerationService().getEnumerationName(entry.getKey()));
			if (!itineraryPricingInfoDatas.contains(itineraryPricingInfoData))
			{
				itineraryPricingInfoDatas.add(itineraryPricingInfoData);
			}
		}
	}

	/**
	 * This method ensures that for each transport offering is offered only one bundle per type.
	 *
	 * @param itineraryPricingInfoData
	 * 		the itinerary pricing info data
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param type
	 * 		the type
	 * @return boolean boolean
	 */
	protected boolean canAddBundle(final ItineraryPricingInfoData itineraryPricingInfoData,
			final List<TransportOfferingData> transportOfferings, final BundleType type)
	{
		final List<TravelBundleTemplateData> bundleTemplates = itineraryPricingInfoData.getBundleTemplates();
		if (CollectionUtils.isEmpty(bundleTemplates))
		{
			return true;
		}
		for (final TravelBundleTemplateData travelBundle : bundleTemplates)
		{
			for (final TransportOfferingData transportOffering : transportOfferings)
			{
				if (travelBundle.getTransportOfferings().stream()
						.anyMatch(to -> StringUtils.equalsIgnoreCase(to.getCode(), transportOffering.getCode()))
						&& type.getCode().equals(travelBundle.getBundleType()))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * This method will either return an ItineraryPricingInfoData already created for a given type or it will create a new one.
	 *
	 * @param itineraryPricingInfoDatas
	 * 		the itinerary pricing info datas
	 * @param entry
	 * 		the entry
	 * @return itinerary pricing info data for type
	 */
	protected ItineraryPricingInfoData getItineraryPricingInfoDataForType(
			final List<ItineraryPricingInfoData> itineraryPricingInfoDatas,
			final Map.Entry<BundleType, List<BundleTemplateModel>> entry)
	{
		for (final ItineraryPricingInfoData ipd : itineraryPricingInfoDatas)
		{
			if (ipd.getBundleType().equals(entry.getKey().getCode()))
			{
				return ipd;
			}
		}
		final ItineraryPricingInfoData itineraryPricingInfoData = new ItineraryPricingInfoData();

		itineraryPricingInfoData.setAvailable(true);

		final List<TravelBundleTemplateData> bundles = new ArrayList<>();
		itineraryPricingInfoData.setBundleTemplates(bundles);
		return itineraryPricingInfoData;
	}

	/**
	 * Creates a map bundleType, bundles. If 'allowMultipleBundles' is set to true, it will be possible to have multiple bundles
	 * for the same bundletype. This happens only in case of multi sector with no bundle defined at a route level.
	 *
	 * @param bundleTemplatesForTransportOfferings
	 * 		the bundle templates for transport offerings
	 * @param bundleMap
	 * 		the bundle map
	 * @param allowMultipleBundles
	 * 		the allow multiple bundles
	 */
	protected void populateBundleMap(final List<BundleTemplateModel> bundleTemplatesForTransportOfferings,
			final Map<BundleType, List<BundleTemplateModel>> bundleMap, final boolean allowMultipleBundles)
	{
		for (final BundleTemplateModel bundleTemplateModel : bundleTemplatesForTransportOfferings)
		{
			if (bundleMap.containsKey(bundleTemplateModel.getType()))
			{
				if (allowMultipleBundles)
				{
					bundleMap.get(bundleTemplateModel.getType()).add(bundleTemplateModel);
				}
			}
			else
			{
				final List<BundleTemplateModel> bundles = new ArrayList<>();
				bundles.add(bundleTemplateModel);
				bundleMap.put(bundleTemplateModel.getType(), bundles);
			}
		}
	}

	/**
	 * Method gets the Itinerary data from the pricedItinerary and then gets a list of OriginDestinationOption from the Itinerary
	 * object. An empty list if return if the list if empty otherwise a list of Transport Offerings is returned.
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @return List<TransportOfferingData> transport offerings
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
	 * Method to get parameter value from properties file
	 *
	 * @param key
	 * 		the key
	 * @return parameter value
	 */
	protected String getParameterValue(final String key)
	{
		return Config.getParameter(key);
	}

	/**
	 * This method will return the travel route provided origin - destination pair from the itinerary in the priced itinerary
	 *
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @return TravelRouteModel travel route
	 */
	protected TravelRouteModel getTravelRoute(final PricedItineraryData pricedItinerary)
	{
		return getTravelRouteService().getTravelRoute(pricedItinerary.getItinerary().getRoute().getCode());
	}

	/**
	 * This method will return the cabin class from the request data
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @return CabinClassModel cabin class
	 */
	protected CabinClassModel getCabinClass(final FareSearchRequestData fareSearchRequestData)
	{
		return getCabinClassService().getCabinClass(fareSearchRequestData.getTravelPreferences().getCabinPreference());
	}

	/**
	 * Gets travel route service.
	 *
	 * @return the travel route service
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * Sets travel route service.
	 *
	 * @param travelRouteService
	 * 		the travel route service
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transport offering service
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transport offering service
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
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
	 * 		the travel bundle template converter
	 */
	@Required
	public void setTravelBundleTemplateConverter(
			final Converter<BundleTemplateModel, TravelBundleTemplateData> travelBundleTemplateConverter)
	{
		this.travelBundleTemplateConverter = travelBundleTemplateConverter;
	}

	/**
	 * Gets cabin class service.
	 *
	 * @return the cabin class service
	 */
	protected CabinClassService getCabinClassService()
	{
		return cabinClassService;
	}

	/**
	 * Sets cabin class service.
	 *
	 * @param cabinClassService
	 * 		the cabin class service
	 */
	@Required
	public void setCabinClassService(final CabinClassService cabinClassService)
	{
		this.cabinClassService = cabinClassService;
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
	 * 		the enumeration service
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
	 * 		the travel bundle template service
	 */
	@Required
	public void setTravelBundleTemplateService(final TravelBundleTemplateService travelBundleTemplateService)
	{
		this.travelBundleTemplateService = travelBundleTemplateService;
	}

	/**
	 * Gets travel rules service.
	 *
	 * @return the travel rules service
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * Sets travel rules service.
	 *
	 * @param travelRulesService
	 * 		the travel rules service
	 */
	@Required
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

}
