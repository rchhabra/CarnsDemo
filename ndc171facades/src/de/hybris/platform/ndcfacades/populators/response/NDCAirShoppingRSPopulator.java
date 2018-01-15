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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TotalFareData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.FlightList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.OriginDestinationList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.PriceClassList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.DataLists.ServiceDefinitionList;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers.Offer;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers.Offer.FlightsOverview;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers.Offer.FlightsOverview.FlightRef;
import de.hybris.platform.ndcfacades.ndc.AirlineOffersSnapshotType;
import de.hybris.platform.ndcfacades.ndc.AirlineOffersSnapshotType.PassengerQuantity;
import de.hybris.platform.ndcfacades.ndc.ArrivalCode;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DepartureCode;
import de.hybris.platform.ndcfacades.ndc.DescriptionType.Text;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Details;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Details.Detail;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Taxes;
import de.hybris.platform.ndcfacades.ndc.FlightReferences;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OfferItemType2;
import de.hybris.platform.ndcfacades.ndc.OfferItemType2.Service;
import de.hybris.platform.ndcfacades.ndc.OfferItemType2.Service.ServiceDefinitionRef;
import de.hybris.platform.ndcfacades.ndc.OfferItemType2.TotalPriceDetail;
import de.hybris.platform.ndcfacades.ndc.OfferItemType2.TotalPriceDetail.TotalAmount;
import de.hybris.platform.ndcfacades.ndc.OfferType2.TotalPrice;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.PriceClassType2;
import de.hybris.platform.ndcfacades.ndc.SegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ServiceDefinitionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDefinitionType.Name;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType.Description;
import de.hybris.platform.ndcfacades.ndc.SimpleCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType.Total;
import de.hybris.platform.ndcfacades.ndc.TotalJourneyType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.CabinClassService;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Airline Offer Populator for NDC {@link AirShoppingRS}
 */
public class NDCAirShoppingRSPopulator implements Populator<FareSelectionData, AirShoppingRS>
{
	private static final Logger LOG = Logger.getLogger(NDCAirShoppingRSPopulator.class);

	private static final String ID_SEPERATOR = "_";
	private static final String SERVICE_PREFIX = "SV";

	private StoreSessionFacade storeSessionFacade;
	private PassengerTypeService passengerTypeService;
	private ConfigurationService configurationService;
	private CabinClassService cabinClassService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	private Converter<TransportOfferingData, ListOfFlightSegmentType> ndcFlightSegmentConverter;

	private final Map<String, PriceClassType2> itineraryPriceClassMap = new HashMap<>();
	private final Map<String, ListOfFlightSegmentType> flightSegmentsHashMap = new HashMap<>();
	private final Map<String, Flight> flightHashMap = new HashMap<>();
	private final Map<String, OriginDestination> originDestinationsHashMap = new HashMap<>();
	private final Map<String, PassengerType> passengerTypeMap = new HashMap<>();
	private final Map<String, ServiceDefinitionType> serviceDefinitionTypeMap = new HashMap<>();

	@Override
	public void populate(final FareSelectionData fareSelectionData, final AirShoppingRS airShoppingRS) throws ConversionException
	{
		final OffersGroup offersGroup = new OffersGroup();

		populateAirlineOffersSnapshotType(offersGroup, fareSelectionData);
		populateAirlineOffers(offersGroup, fareSelectionData);

		airShoppingRS.setOffersGroup(offersGroup);
		airShoppingRS.setDataLists(createDataLists());
	}

	/**
	 * Populate airline offers snapshot type.
	 *
	 * @param offersGroup
	 * 		the offers group
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void populateAirlineOffersSnapshotType(final OffersGroup offersGroup, final FareSelectionData fareSelectionData)
	{
		if (CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			throw new ConversionException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_RESULT));
		}

		final AirlineOffersSnapshotType airlineOffersSnapshotType = new AirlineOffersSnapshotType();

		final Optional<ItineraryPricingInfoData> itineraryPricingInfoDataOptional = fareSelectionData.getPricedItineraries()
				.stream().flatMap(pricedItineraryData -> pricedItineraryData.getItineraryPricingInfos().stream())
				.filter(ItineraryPricingInfoData::isAvailable)
				.filter(itineraryPricingInfoData -> CollectionUtils.isNotEmpty(itineraryPricingInfoData.getPtcFareBreakdownDatas()))
				.findAny();

		if (!itineraryPricingInfoDataOptional.isPresent())
		{
			throw new ConversionException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_RESULT));
		}

		final List<PTCFareBreakdownData> anyPTCFareBreakdownDatas = itineraryPricingInfoDataOptional.get()
				.getPtcFareBreakdownDatas();
		populatePassengerTypes(anyPTCFareBreakdownDatas);

		final int totalPassengerQuantity = anyPTCFareBreakdownDatas.stream()
				.mapToInt(ptcFareBreakdownData -> ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity()).sum();

		final PassengerQuantity passengerQuantity = new PassengerQuantity();
		passengerQuantity.setValue(BigInteger.valueOf(totalPassengerQuantity));
		airlineOffersSnapshotType.setPassengerQuantity(passengerQuantity);

		final int offerQuantity = fareSelectionData.getPricedItineraries().stream()
				.mapToInt(pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream()
						.filter(ItineraryPricingInfoData::isAvailable).collect(Collectors.toList()).size()).sum();

		if (offerQuantity == 0)
		{
			throw new ConversionException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_RESULT));
		}
		airlineOffersSnapshotType.setMatchedOfferQuantity(BigInteger.valueOf(offerQuantity));

		offersGroup.setAllOffersSnapshot(airlineOffersSnapshotType);
	}

	/**
	 * Populate passenger types.
	 *
	 * @param ptcFareBreakdownDatas
	 * 		the ptc fare breakdown datas
	 */
	protected void populatePassengerTypes(final List<PTCFareBreakdownData> ptcFareBreakdownDatas)
	{
		for (final PTCFareBreakdownData ptcFareBreakdownData : ptcFareBreakdownDatas)
		{
			final String passengerTypeCode = ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode();
			final String ptc = getPassengerTypeService().getPassengerType(passengerTypeCode).getNdcCode();

			for (int passengerTypeIndex = 1; passengerTypeIndex <= ptcFareBreakdownData.getPassengerTypeQuantity()
					.getQuantity(); passengerTypeIndex++)
			{
				final PassengerType passengerType = new PassengerType();
				passengerType.setPTC(ptc);
				passengerType.setPassengerID(ptc + passengerTypeIndex);
				getPassengerTypeMap().put(passengerTypeCode + passengerTypeIndex, passengerType);
			}
		}
	}

	/**
	 * Populate airline offers.
	 *
	 * @param offersGroup
	 * 		the offers group
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void populateAirlineOffers(final OffersGroup offersGroup, final FareSelectionData fareSelectionData)
	{
		final AirlineOffers airlineOffers = new AirlineOffers();

		int offerIndex = 0;
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			final Flight flight = new Flight();
			final HashMap<String, ListOfFlightSegmentType> pricedItineraryFlightSegments = new HashMap<>();

			populateFlightSegments(pricedItineraryFlightSegments, pricedItinerary);
			populateFlight(flight, pricedItinerary, pricedItineraryFlightSegments);
			populateOriginDestination(flight, pricedItinerary);
			final List<Offer> offers = getOffers(offerIndex, pricedItinerary, flight);
			offerIndex = offerIndex + CollectionUtils.size(offers);
			airlineOffers.getOffer().addAll(offers);
		}

		offersGroup.getAirlineOffers().add(airlineOffers);
	}

	/**
	 * Populate flight segments.
	 *
	 * @param pricedItineraryFlightSegments
	 * 		the priced itinerary flight segments
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populateFlightSegments(final HashMap<String, ListOfFlightSegmentType> pricedItineraryFlightSegments,
			final PricedItineraryData pricedItinerary)
	{
		if (CollectionUtils.isEmpty(pricedItinerary.getItineraryPricingInfos()))
		{
			return;
		}
		for (final TransportOfferingData transportOfferingData : pricedItinerary.getItineraryPricingInfos().get(0)
				.getBundleTemplates().stream().map(TravelBundleTemplateData::getTransportOfferings).flatMap(List::stream)
				.collect(Collectors.toList()))
		{
			final ListOfFlightSegmentType flightSegment;
			if (getFlightSegmentsHashMap().containsKey(transportOfferingData.getCode()))
			{
				flightSegment = getFlightSegmentsHashMap().get(transportOfferingData.getCode());
			}
			else
			{
				flightSegment = new ListOfFlightSegmentType();
				flightSegment.setSegmentKey(transportOfferingData.getCode());
				getNdcFlightSegmentConverter().convert(transportOfferingData, flightSegment);
				getFlightSegmentsHashMap().put(transportOfferingData.getCode(), flightSegment);
			}
			pricedItineraryFlightSegments.put(flightSegment.getSegmentKey(), flightSegment);
		}
	}

	/**
	 * Populate flight.
	 *
	 * @param flight
	 * 		the flight
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param flightSegmentList
	 * 		the flight segment list
	 */
	protected void populateFlight(final Flight flight, final PricedItineraryData pricedItinerary,
			final HashMap<String, ListOfFlightSegmentType> flightSegmentList)
	{
		final TotalJourneyType totalJourney = new TotalJourneyType();
		final SegmentReferences segmentReferences = new SegmentReferences();

		segmentReferences.getValue().addAll(flightSegmentList.values());

		flight.setSegmentReferences(segmentReferences);
		flight.setFlightKey(NdcfacadesConstants.FLIGHT + String.valueOf(pricedItinerary.getId()));

		try
		{
			final Duration duration = DatatypeFactory.newInstance()
					.newDuration(TransportOfferingUtils.getDuration(pricedItinerary.getItinerary().getDuration()));
			totalJourney.setTime(duration);
			flight.setJourney(totalJourney);
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.debug(e);
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.DURATION_CONVERSION_ERROR));
		}

		getFlightHashMap().put(flight.getFlightKey(), flight);
	}

	/**
	 * Populate origin destination.
	 *
	 * @param flight
	 * 		the flight
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populateOriginDestination(final Flight flight, final PricedItineraryData pricedItinerary)
	{
		final String originDestinationKey = pricedItinerary.getItinerary().getRoute().getCode();

		if (getOriginDestinationsHashMap().containsKey(originDestinationKey))
		{
			getOriginDestinationsHashMap().get(originDestinationKey).getFlightReferences().getValue().add(flight);
		}
		else
		{
			final OriginDestination originDestination = new OriginDestination();
			final DepartureCode departureCode = new DepartureCode();
			final ArrivalCode arrivalCode = new ArrivalCode();
			final FlightReferences flightReference = new FlightReferences();

			originDestination.setOriginDestinationKey(originDestinationKey);

			departureCode.setValue(pricedItinerary.getItinerary().getRoute().getOrigin().getCode());
			originDestination.setDepartureCode(departureCode);

			arrivalCode.setValue(pricedItinerary.getItinerary().getRoute().getDestination().getCode());
			originDestination.setArrivalCode(arrivalCode);

			flightReference.getValue().add(flight);
			originDestination.setFlightReferences(flightReference);
			getOriginDestinationsHashMap().put(originDestinationKey, originDestination);

		}
	}

	/**
	 * Gets the offers.
	 *
	 * @param offerIndex
	 * 		the offer index
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param flight
	 * 		the flight
	 * @return the offers
	 */
	protected List<Offer> getOffers(int offerIndex, final PricedItineraryData pricedItinerary, final Flight flight)
	{
		final List<Offer> offers = new ArrayList<>();
		for (final ItineraryPricingInfoData itineraryPricingInfo : pricedItinerary.getItineraryPricingInfos())
		{
			if (!itineraryPricingInfo.isAvailable())
			{
				continue;
			}

			final Offer offer = new Offer();
			offerIndex++;
			offer.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
			offer.setOfferID(getNdcOfferItemIdResolver().generateAirShoppingNDCOfferItemId(pricedItinerary, itineraryPricingInfo));

			final List<OfferItemType2> offerItems = getOfferItems(itineraryPricingInfo, pricedItinerary, flight, offerIndex);
			offer.getOfferItem().addAll(offerItems);
			offer.setFlightsOverview(getFlightsOverview(flight, itineraryPricingInfo));
			offer.setTotalPrice(getTotalPrice(itineraryPricingInfo.getTotalFare()));

			offers.add(offer);
		}
		return offers;
	}

	/**
	 * Gets the offer items.
	 *
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param flight
	 * 		the flight
	 * @param offerIndex
	 * 		the offer index
	 * @return the offer items
	 */
	protected List<OfferItemType2> getOfferItems(final ItineraryPricingInfoData itineraryPricingInfo,
			final PricedItineraryData pricedItinerary, final Flight flight, final int offerIndex)
	{
		int offerItemIndex = 1;
		final List<OfferItemType2> offerItems = new ArrayList<>();
		for (final PTCFareBreakdownData ptcFareBreakdownData : itineraryPricingInfo.getPtcFareBreakdownDatas())
		{
			if (ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity() == 0)
			{
				continue;
			}

			final OfferItemType2 offerItem = new OfferItemType2();
			offerItem.setOfferItemID(getNdcOfferItemIdResolver().generateAirShoppingNDCOfferItemId(ptcFareBreakdownData,
					pricedItinerary, itineraryPricingInfo));
			populateOfferItemPriceDetails(offerItem, ptcFareBreakdownData);

			final HashMap<String, Service> servicesHashMap = getProductServiceMap(itineraryPricingInfo.getBundleTemplates(), flight,
					ptcFareBreakdownData, offerIndex + ID_SEPERATOR + offerItemIndex);
			populateServiceDefinitionTypeMap(servicesHashMap);

			offerItem.getService().addAll(servicesHashMap.values());
			offerItems.add(offerItem);
			offerItemIndex++;
		}
		return offerItems;
	}

	/**
	 * Gets the product service map.
	 *
	 * @param bundleTemplates
	 * 		the itinerary pricing info
	 * @param flight
	 * 		the flight
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 * @param offerItemIndex
	 * 		the offer item index
	 * @return the product service map
	 */
	protected HashMap<String, Service> getProductServiceMap(final List<TravelBundleTemplateData> bundleTemplates,
			final Flight flight, final PTCFareBreakdownData ptcFareBreakdownData, final String offerItemIndex)
	{
		final HashMap<String, Service> servicesHashMap = new HashMap<>();
		for (final TravelBundleTemplateData bundleTemplate : bundleTemplates)
		{
			final List<ProductData> nonFareProducts = bundleTemplate.getNonFareProducts().entrySet().stream()
					.map(Map.Entry::getValue).flatMap(List::stream).collect(Collectors.toList());

			final Service serviceFlightRefs = getServiceDetails(flight, bundleTemplate.getId(), ptcFareBreakdownData,
					offerItemIndex);
			servicesHashMap.put(flight.getFlightKey(), serviceFlightRefs);

			for (final ProductData nonFareProduct : nonFareProducts)
			{
				final Optional<CategoryData> optionalServiceCategory = nonFareProduct.getCategories().stream().findFirst();
				final String categoryCode = optionalServiceCategory.isPresent() ? optionalServiceCategory.get().getCode()
						: StringUtils.EMPTY;
				final String offerGroupMapping = getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode,
						getOfferGroupToOriginDestinationMapping().getOrDefault(
								TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING, TravelservicesConstants.TRAVEL_ROUTE));

				if (StringUtils.equalsIgnoreCase(offerGroupMapping, TravelservicesConstants.TRANSPORT_OFFERING))
				{
					bundleTemplate.getTransportOfferings().forEach(transportOffering -> {
						final Service service = getServiceDetails(nonFareProduct, bundleTemplate.getId(),
								Collections.singletonList(transportOffering), ptcFareBreakdownData, offerItemIndex);
						servicesHashMap.put(nonFareProduct.getCode() + transportOffering.getCode(), service);
					});
				}
				else if (StringUtils.equalsIgnoreCase(offerGroupMapping, TravelservicesConstants.TRAVEL_ROUTE))
				{
					final Service service = getServiceDetails(nonFareProduct, bundleTemplate.getId(),
							bundleTemplate.getTransportOfferings(), ptcFareBreakdownData, offerItemIndex);
					servicesHashMap.put(nonFareProduct.getCode(), service);
				}
			}
		}
		return servicesHashMap;
	}

	/**
	 * Gets the flights overview.
	 *
	 * @param flight
	 * 		the flight
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 * @return the flights overview
	 */
	protected FlightsOverview getFlightsOverview(final Flight flight, final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final FlightsOverview flightsOverview = new FlightsOverview();
		final FlightRef flightRef = new FlightRef();
		flightRef.setValue(flight);
		flightsOverview.getFlightRef().add(flightRef);

		final Optional<TravelBundleTemplateData> optionalTravelBundleTemplateData = itineraryPricingInfo.getBundleTemplates()
				.stream().findFirst();

		if (optionalTravelBundleTemplateData.isPresent())
		{
			final TravelBundleTemplateData travelBundleTemplateData = optionalTravelBundleTemplateData.get();
			final PriceClassType2 itineraryPriceClass = getPriceClass(travelBundleTemplateData);
			flightsOverview.setItineraryPriceClassRef(itineraryPriceClass);

			getItineraryPriceClassMap().put(itineraryPriceClass.getPriceClassID(), itineraryPriceClass);
		}
		return flightsOverview;
	}

	/**
	 * Populate service details.
	 *
	 * @param flight
	 * 		the flight
	 * @param bundleTemplateId
	 * 		the bundle template id
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 * @param offerItemIndex
	 * 		the offer item index
	 * @return the service details
	 */
	protected Service getServiceDetails(final Flight flight, final String bundleTemplateId,
			final PTCFareBreakdownData ptcFareBreakdownData, final String offerItemIndex)
	{
		final Service service = new Service();
		final StringBuilder serviceID = getServiceID(flight.getFlightKey(), offerItemIndex, bundleTemplateId);
		populatePassengerRefs(service, serviceID, ptcFareBreakdownData);
		service.setServiceID(serviceID.toString());

		service.getFlightRefs().add(flight);
		return service;
	}

	/**
	 * Populate service details.
	 *
	 * @param productData
	 * 		the product data
	 * @param bundleTemplateId
	 * 		the bundle template id
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 * @param offerItemIndex
	 * 		the offer item index
	 * @return the service details
	 */
	protected Service getServiceDetails(final ProductData productData, final String bundleTemplateId,
			final List<TransportOfferingData> transportOfferings,
			final PTCFareBreakdownData ptcFareBreakdownData, final String offerItemIndex)
	{
		final Service service = new Service();
		final StringBuilder serviceCode = new StringBuilder(productData.getCode());
		transportOfferings.forEach(transportOffering -> serviceCode.append(ID_SEPERATOR).append(transportOffering.getCode()));

		final StringBuilder serviceID = getServiceID(serviceCode.toString(), offerItemIndex, bundleTemplateId);
		populatePassengerRefs(service, serviceID, ptcFareBreakdownData);
		service.setServiceID(serviceID.toString());

		service.setServiceDefinitionRef(getServiceDefinitionRef(productData, transportOfferings, productData.getCode()));
		return service;
	}

	/**
	 * Gets the service ID.
	 *
	 * @param serviceCode
	 * 		the service code
	 * @param offerItemIndex
	 * 		the offer item index
	 * @param bundleTemplateId
	 * 		the bundle template
	 * @return the service ID
	 */
	protected StringBuilder getServiceID(final String serviceCode, final String offerItemIndex,
			final String bundleTemplateId)
	{
		final StringBuilder serviceID = new StringBuilder(SERVICE_PREFIX);
		serviceID.append(offerItemIndex).append(ID_SEPERATOR).append(serviceCode).append(ID_SEPERATOR).append(bundleTemplateId);
		return serviceID;
	}

	/**
	 * Populate passenger refs.
	 *
	 * @param service
	 * 		the service
	 * @param serviceID
	 * 		the service ID
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 */
	protected void populatePassengerRefs(final Service service, final StringBuilder serviceID,
			final PTCFareBreakdownData ptcFareBreakdownData)
	{
		for (int passengerTypeIndex = 1; passengerTypeIndex <= ptcFareBreakdownData.getPassengerTypeQuantity()
				.getQuantity(); passengerTypeIndex++)
		{
			final String passengerTypeCode = ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode()
					+ passengerTypeIndex;
			if (getPassengerTypeMap().containsKey(passengerTypeCode))
			{
				final PassengerType passengerType = getPassengerTypeMap().get(passengerTypeCode);
				serviceID.append(ID_SEPERATOR).append(passengerType.getPassengerID());
				service.getPassengerRefs().add(passengerType);
			}
		}
	}

	/**
	 * Gets the service definition ref.
	 *
	 * @param productData
	 * 		the product data
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param serviceDefinitionRefVal
	 * 		the service definition ref val
	 * @return the service definition ref
	 */
	protected ServiceDefinitionRef getServiceDefinitionRef(final ProductData productData,
			final List<TransportOfferingData> transportOfferings, final String serviceDefinitionRefVal)
	{
		final ServiceDefinitionRef serviceDefinitionRef = new ServiceDefinitionRef();
		ServiceDefinitionType serviceDefinitionType = getServiceDefinitionTypeMap().get(serviceDefinitionRefVal);

		if (Objects.isNull(serviceDefinitionType))
		{
			serviceDefinitionType = new ServiceDefinitionType();
			serviceDefinitionType.setServiceDefinitionID(serviceDefinitionRefVal);
			serviceDefinitionType.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

			final Name name = new Name();
			name.setValue(productData.getName());
			serviceDefinitionType.setName(name);

			final ServiceDescriptionType serviceDescription = new ServiceDescriptionType();
			if (StringUtils.isNotEmpty(productData.getDescription()))
			{
				final Description description = new Description();
				final Text descriptionText = new Text();
				descriptionText.setValue(productData.getDescription());
				description.setText(descriptionText);
				serviceDescription.getDescription().add(description);
			}

			if (StringUtils.isNotEmpty(productData.getUrl()))
			{
				final Description description = new Description();
				description.setLink(productData.getUrl());
				serviceDescription.getDescription().add(description);
			}

			if (CollectionUtils.isEmpty(serviceDescription.getDescription()))
			{
				final Description description = new Description();
				description.setApplication(productData.getName());
				serviceDescription.getDescription().add(description);
			}
			serviceDefinitionType.setDescriptions(serviceDescription);
		}

		serviceDefinitionRef.setValue(serviceDefinitionType);

		transportOfferings.forEach(transportOffering -> serviceDefinitionRef.getSegmentRefs()
				.add(getFlightSegmentsHashMap().get(transportOffering.getCode())));

		return serviceDefinitionRef;
	}

	/**
	 * Populate service definition type map.
	 *
	 * @param servicesHashMap
	 * 		the services hash map
	 */
	protected void populateServiceDefinitionTypeMap(final HashMap<String, Service> servicesHashMap)
	{
		final List<ServiceDefinitionRef> serviceDefinitionRefs = servicesHashMap.values().stream()
				.map(Service::getServiceDefinitionRef).collect(Collectors.toList());

		final List<Object> serviceDefinitionTypes = serviceDefinitionRefs.stream()
				.filter(serviceDefinitionRef -> (Objects.nonNull(serviceDefinitionRef)
						&& (serviceDefinitionRef.getValue() instanceof ServiceDefinitionType)))
				.map(ServiceDefinitionRef::getValue).collect(Collectors.toList());

		serviceDefinitionTypes.forEach(obj -> {
			final ServiceDefinitionType serviceDefinitionType = (ServiceDefinitionType) obj;
			getServiceDefinitionTypeMap().putIfAbsent(serviceDefinitionType.getServiceDefinitionID(), serviceDefinitionType);
		});
	}

	/**
	 * Populate offer item price details.
	 *
	 * @param offerItem
	 * 		the offer item
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 */
	protected void populateOfferItemPriceDetails(final OfferItemType2 offerItem, final PTCFareBreakdownData ptcFareBreakdownData)
	{
		final TotalPriceDetail totalPriceDetail = new TotalPriceDetail();
		final TotalAmount totalAmount = new TotalAmount();
		final SimpleCurrencyPriceType simpleCurrencyPriceType = new SimpleCurrencyPriceType();
		final CurrencyAmountOptType currencyAmountOptType = new CurrencyAmountOptType();
		final TaxDetailType taxDetailType = new TaxDetailType();
		final Total total = new Total();

		final PassengerFareData passengerFare = ptcFareBreakdownData.getPassengerFare();
		final double totalTaxes = passengerFare.getTaxes().stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue())
				.sum();

		currencyAmountOptType
				.setValue(passengerFare.getBaseFare().getValue().setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		currencyAmountOptType.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		totalPriceDetail.setBaseAmount(currencyAmountOptType);

		total.setValue(BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxDetailType.setTotal(total);
		total.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		totalPriceDetail.setTaxes(taxDetailType);

		simpleCurrencyPriceType.setValue(passengerFare.getTotalFare().getValue().add(BigDecimal.valueOf(totalTaxes))
				.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		simpleCurrencyPriceType.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		totalAmount.setSimpleCurrencyPrice(simpleCurrencyPriceType);
		totalPriceDetail.setTotalAmount(totalAmount);

		offerItem.setTotalPriceDetail(totalPriceDetail);
	}

	/**
	 * Set TotalPrice in the AirlineOffer based on the TotalFareData.
	 *
	 * @param totalFare
	 * 		the total fare returned from the fare search
	 * @return the total price
	 */
	protected TotalPrice getTotalPrice(final TotalFareData totalFare)
	{
		final DetailCurrencyPriceType detailCurrencyPrice = new DetailCurrencyPriceType();
		final TotalPrice totalPrice = new TotalPrice();
		final CurrencyAmountOptType total = new CurrencyAmountOptType();
		final Details details = new Details();
		final Detail detail = new Detail();
		final CurrencyAmountOptType subtotal = new CurrencyAmountOptType();
		final Taxes taxes = new Taxes();
		final Total taxesTotal = new Total();

		subtotal.setValue(totalFare.getBasePrice().getValue().setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		subtotal.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		detail.setSubTotal(subtotal);
		detail.setApplication(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.BASE_PRICE));
		details.getDetail().add(detail);
		detailCurrencyPrice.setDetails(details);

		total.setValue(totalFare.getTotalPrice().getValue().setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		total.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		detailCurrencyPrice.setTotal(total);

		taxesTotal.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		taxesTotal.setValue(getTaxes(totalFare));
		taxes.setTotal(taxesTotal);
		detailCurrencyPrice.setTaxes(taxes);

		totalPrice.setDetailCurrencyPrice(detailCurrencyPrice);

		return totalPrice;
	}

	/**
	 * Gets the taxes.
	 *
	 * @param totalFare
	 * 		the total fare
	 * @return the taxes
	 */
	protected BigDecimal getTaxes(final TotalFareData totalFare)
	{
		final double totalTaxes = totalFare.getTaxes().stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		return BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Creates the data lists.
	 *
	 * @return the data lists
	 */
	protected DataLists createDataLists()
	{
		final DataLists dataLists = new DataLists();

		final PassengerList passengerList = new PassengerList();
		passengerList.getPassenger().addAll(getPassengerTypeMap().values());
		dataLists.setPassengerList(passengerList);

		final FlightList flightList = new FlightList();
		flightList.getFlight().addAll(getFlightHashMap().values());
		dataLists.setFlightList(flightList);

		final FlightSegmentList flightSegmentList = new FlightSegmentList();
		flightSegmentList.getFlightSegment().addAll(getFlightSegmentsHashMap().values());
		dataLists.setFlightSegmentList(flightSegmentList);

		final OriginDestinationList originDestinationList = new OriginDestinationList();
		originDestinationList.getOriginDestination().addAll(getOriginDestinationsHashMap().values());
		dataLists.setOriginDestinationList(originDestinationList);

		final PriceClassList priceClassList = new PriceClassList();
		priceClassList.getPriceClass().addAll(getItineraryPriceClassMap().values());
		dataLists.setPriceClassList(priceClassList);

		if (CollectionUtils.isNotEmpty(getServiceDefinitionTypeMap().values()))
		{
			final ServiceDefinitionList serviceDefinitionList = new ServiceDefinitionList();
			serviceDefinitionList.getServiceDefinition().addAll(getServiceDefinitionTypeMap().values());
			dataLists.setServiceDefinitionList(serviceDefinitionList);
		}

		return dataLists;
	}

	/**
	 * Gets the price class.
	 *
	 * @param travelBundleTemplateData
	 * 		the travel bundle template data
	 * @return the price class
	 */
	protected PriceClassType2 getPriceClass(final TravelBundleTemplateData travelBundleTemplateData)
	{
		final PriceClassType2 priceClass = new PriceClassType2();
		priceClass.setPriceClassID(travelBundleTemplateData.getId());

		final CabinClassModel cabinClass = getCabinClassService()
				.findCabinClassFromBundleTemplate(travelBundleTemplateData.getId());
		if (Objects.nonNull(cabinClass))
		{
			priceClass.setCode(cabinClass.getCode());
		}
		priceClass.setName(travelBundleTemplateData.getName());
		return priceClass;
	}

	/**
	 * Gets store session facade.
	 *
	 * @return the store session facade
	 */
	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	/**
	 * Sets store session facade.
	 *
	 * @param storeSessionFacade
	 * 		the store session facade
	 */
	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passenger type service
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 * 		the passenger type service
	 */
	@Required
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
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
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 * 		the offerGroupToOriginDestinationMapping to set
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	/**
	 * Gets ndc flight segment converter.
	 *
	 * @return the ndc flight segment converter
	 */
	protected Converter<TransportOfferingData, ListOfFlightSegmentType> getNdcFlightSegmentConverter()
	{
		return ndcFlightSegmentConverter;
	}

	/**
	 * Sets ndc flight segment converter.
	 *
	 * @param ndcFlightSegmentConverter
	 * 		the ndc flight segment converter
	 */
	@Required
	public void setNdcFlightSegmentConverter(
			final Converter<TransportOfferingData, ListOfFlightSegmentType> ndcFlightSegmentConverter)
	{
		this.ndcFlightSegmentConverter = ndcFlightSegmentConverter;
	}

	/**
	 * Gets ndc offer item id resolver.
	 *
	 * @return the ndc offer item id resolver
	 */
	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	/**
	 * Sets ndc offer item id resolver.
	 *
	 * @param ndcOfferItemIdResolver
	 * 		the ndc offer item id resolver
	 */
	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	/**
	 * Gets the itinerary price class map.
	 *
	 * @return the itineraryPriceClassMap
	 */
	protected Map<String, PriceClassType2> getItineraryPriceClassMap()
	{
		return itineraryPriceClassMap;
	}

	/**
	 * Gets the flight segments hash map.
	 *
	 * @return the flightSegmentsHashMap
	 */
	protected Map<String, ListOfFlightSegmentType> getFlightSegmentsHashMap()
	{
		return flightSegmentsHashMap;
	}

	/**
	 * Gets the flight hash map.
	 *
	 * @return the flightHashMap
	 */
	protected Map<String, Flight> getFlightHashMap()
	{
		return flightHashMap;
	}

	/**
	 * Gets the origin destinations hash map.
	 *
	 * @return the originDestinationsHashMap
	 */
	protected Map<String, OriginDestination> getOriginDestinationsHashMap()
	{
		return originDestinationsHashMap;
	}

	/**
	 * Gets the passenger type map.
	 *
	 * @return the passengerTypeMap
	 */
	protected Map<String, PassengerType> getPassengerTypeMap()
	{
		return passengerTypeMap;
	}

	/**
	 * Gets the service definition type map.
	 *
	 * @return the serviceDefinitionTypeMap
	 */
	protected Map<String, ServiceDefinitionType> getServiceDefinitionTypeMap()
	{
		return serviceDefinitionTypeMap;
	}
}
