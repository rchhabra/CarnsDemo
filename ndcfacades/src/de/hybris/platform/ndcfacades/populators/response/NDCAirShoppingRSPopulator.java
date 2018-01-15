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
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRS.OffersGroup.AirlineOffers.AirlineOffer;
import de.hybris.platform.ndcfacades.ndc.AirlineIDType;
import de.hybris.platform.ndcfacades.ndc.AnonymousTravelerList;
import de.hybris.platform.ndcfacades.ndc.AnonymousTravelerType;
import de.hybris.platform.ndcfacades.ndc.ApplicableFlight;
import de.hybris.platform.ndcfacades.ndc.ArrivalCode;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.DataListType.OriginDestinationList;
import de.hybris.platform.ndcfacades.ndc.DepartureCode;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Details;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Details.Detail;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Taxes;
import de.hybris.platform.ndcfacades.ndc.FlightCabinCoreType.CabinDesignator;
import de.hybris.platform.ndcfacades.ndc.FlightCabinCoreType.MarketingName;
import de.hybris.platform.ndcfacades.ndc.FlightReferences;
import de.hybris.platform.ndcfacades.ndc.FlightSegmentReference;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OfferItemCoreType.TotalPrice;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadDetailType.PriceDetail;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadDetailType.PriceDetail.TotalAmount;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadType.RequestedDate;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.PricedFlightOfferAssocType;
import de.hybris.platform.ndcfacades.ndc.PricedFlightOfferType.OfferPrice;
import de.hybris.platform.ndcfacades.ndc.PricedOffer;
import de.hybris.platform.ndcfacades.ndc.SegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ServiceDetailType;
import de.hybris.platform.ndcfacades.ndc.ServiceInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.ServiceList;
import de.hybris.platform.ndcfacades.ndc.SimpleAircraftCabinType;
import de.hybris.platform.ndcfacades.ndc.SimpleCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType.Total;
import de.hybris.platform.ndcfacades.ndc.TotalJourneyType;
import de.hybris.platform.ndcfacades.ndc.TravelerCoreType;
import de.hybris.platform.ndcfacades.ndc.TravelerInfoAssocType;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.util.TransportOfferingUtils;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.services.CabinClassService;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Airline Offer Populator for NDC {@link AirShoppingRS}
 */
public class NDCAirShoppingRSPopulator implements Populator<FareSelectionData, AirShoppingRS>
{
	private static final Logger LOG = Logger.getLogger(NDCAirShoppingRSPopulator.class);

	private StoreSessionFacade storeSessionFacade;
	private PassengerTypeService passengerTypeService;
	private ConfigurationService configurationService;
	private CabinClassService cabinClassService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	private Converter<TransportOfferingData, ListOfFlightSegmentType> ndcFlightSegmentConverter;
	private Converter<ServiceDetailType, ProductData> ndcServiceDetailConverter;

	private final HashMap<String, ListOfFlightSegmentType> flightSegmentsHashMap = new HashMap<>();
	private final HashMap<String, Flight> flightHashMap = new HashMap<>();
	private final HashMap<String, OriginDestination> originDestinationsHashMap = new HashMap<>();
	private final HashMap<String, AnonymousTravelerType> anonymousTravelersHashMap = new HashMap<>();
	private final HashMap<String, ServiceDetailType> servicesHashMap = new HashMap<>();

	@Override
	public void populate(final FareSelectionData fareSelectionData, final AirShoppingRS airShoppingRS) throws ConversionException
	{
		final OffersGroup offersGroup = new OffersGroup();
		final AirlineOffers airlineOffers = new AirlineOffers();
		final DataLists dataLists = new DataLists();

		populateTotalOfferQuantity(airlineOffers, fareSelectionData);
		populateAirlineOffersOwner(airlineOffers);
		populateAnonymousTravelerList(fareSelectionData);

		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			final Flight flight = new Flight();
			final HashMap<String, ListOfFlightSegmentType> pricedItineraryFlightSegments = new HashMap<>();

			populateFlightSegments(pricedItineraryFlightSegments, pricedItinerary);
			populateFlight(flight, pricedItinerary, pricedItineraryFlightSegments);
			populateOriginDestination(flight, pricedItinerary);

			for (final ItineraryPricingInfoData itineraryPricingInfo : pricedItinerary.getItineraryPricingInfos())
			{
				if (!itineraryPricingInfo.isAvailable())
				{
					continue;
				}

				final AirlineOffer airlineOffer = new AirlineOffer();

				populateServiceList(itineraryPricingInfo);
				populateItemIDType(airlineOffer, pricedItinerary, itineraryPricingInfo);
				populateTotalPrice(airlineOffer, itineraryPricingInfo.getTotalFare());
				populatePricedOffer(airlineOffer, itineraryPricingInfo, pricedItinerary);

				airlineOffers.getAirlineOffer().add(airlineOffer);
			}
		}

		populateDatalists(dataLists);
		airShoppingRS.setDataLists(dataLists);

		offersGroup.getAirlineOffers().add(airlineOffers);
		airShoppingRS.setOffersGroup(offersGroup);
	}

	/**
	 * Populate datalists.
	 *
	 * @param dataLists
	 * 		the data lists
	 */
	protected void populateDatalists(final DataLists dataLists)
	{
		final ServiceList serviceList = new ServiceList();
		final AnonymousTravelerList anonymousTravelerList = new AnonymousTravelerList();
		final OriginDestinationList originDestinationList = new OriginDestinationList();
		final FlightSegmentList flightSegmentList = new FlightSegmentList();
		final FlightList flightList = new FlightList();

		anonymousTravelerList.getAnonymousTraveler().addAll(getAnonymousTravelersHashMap().values());
		flightSegmentList.getFlightSegment().addAll(getFlightSegmentsHashMap().values());
		originDestinationList.getOriginDestination().addAll(getOriginDestinationsHashMap().values());
		serviceList.getService().addAll(getServicesHashMap().values());
		flightList.getFlight().addAll(getFlightHashMap().values());

		dataLists.setAnonymousTravelerList(anonymousTravelerList);
		dataLists.setOriginDestinationList(originDestinationList);
		dataLists.setFlightList(flightList);
		dataLists.setFlightSegmentList(flightSegmentList);
		dataLists.setServiceList(serviceList);
	}

	/**
	 * Populate anonymous traveler list.
	 *
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void populateAnonymousTravelerList(final FareSelectionData fareSelectionData)
	{
		for (final PTCFareBreakdownData ptcFareBreakdownData : fareSelectionData.getPricedItineraries().get(0)
				.getItineraryPricingInfos().get(0).getPtcFareBreakdownDatas())
		{
			final AnonymousTravelerType anonymousTraveler = new AnonymousTravelerType();
			final TravelerCoreType.PTC ptc = new TravelerCoreType.PTC();
			final String passengerType = getPassengerTypeService()
					.getPassengerType(ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode()).getNdcCode();

			anonymousTraveler.setObjectKey(passengerType);
			ptc.setQuantity(BigInteger.valueOf(ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity()));
			ptc.setValue(passengerType);
			anonymousTraveler.setPTC(ptc);

			getAnonymousTravelersHashMap().put(passengerType, anonymousTraveler);
		}
	}

	/**
	 * Populate service list.
	 *
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 */
	protected void populateServiceList(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		for (final TravelBundleTemplateData bundleTemplate : itineraryPricingInfo.getBundleTemplates())
		{
			for (final ProductData nonFareProduct : bundleTemplate.getNonFareProducts().entrySet().stream().map(Map.Entry::getValue)
					.flatMap(List::stream).collect(Collectors.toList()))
			{
				final ServiceDetailType serviceDetailType = new ServiceDetailType();

				if (!getServicesHashMap().containsKey(nonFareProduct.getCode()))
				{
					getNdcServiceDetailConverter().convert(serviceDetailType, nonFareProduct);
					getServicesHashMap().put(nonFareProduct.getCode(), serviceDetailType);
				}
			}
		}
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

		getFlightHashMap().put(String.valueOf(pricedItinerary.getId()), flight);
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
	 * Populate total offer quantity.
	 *
	 * @param airlineOffers
	 * 		the airline offers
	 * @param fareSelectionData
	 * 		the fare selection data
	 */
	protected void populateTotalOfferQuantity(final AirlineOffers airlineOffers, final FareSelectionData fareSelectionData)
	{
		final int offerQuantity = fareSelectionData.getPricedItineraries().stream().mapToInt(
				pricedItinerary -> pricedItinerary.getItineraryPricingInfos().stream().filter(ItineraryPricingInfoData::isAvailable)
						.collect(Collectors.toList()).size()).sum();

		if (offerQuantity == 0)
		{
			throw new ConversionException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_RESULT));
		}

		airlineOffers.setTotalOfferQuantity(BigInteger.valueOf(offerQuantity));
	}

	/**
	 * Populate airline offers owner.
	 *
	 * @param airlineOffers
	 * 		the airline offers
	 */
	protected void populateAirlineOffersOwner(final AirlineOffers airlineOffers)
	{
		final AirlineIDType airLineId = new AirlineIDType();
		airLineId.setValue(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		airlineOffers.setOwner(airLineId);
	}

	/**
	 * Populate item id type.
	 *
	 * @param airlineOffer
	 * 		the airline offer
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 */
	protected void populateItemIDType(final AirlineOffer airlineOffer, final PricedItineraryData pricedItinerary,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final ItemIDType itemIDType = new ItemIDType();
		itemIDType.setValue(getNdcOfferItemIdResolver().generateAirShoppingNDCOfferItemId(pricedItinerary, itineraryPricingInfo));
		itemIDType.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		airlineOffer.setOfferID(itemIDType);
	}

	/**
	 * Populate priced offer.
	 *
	 * @param airlineOffer
	 * 		the airline offer
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populatePricedOffer(final AirlineOffer airlineOffer, final ItineraryPricingInfoData itineraryPricingInfo,
			final PricedItineraryData pricedItinerary)
	{
		final PricedOffer pricedOffer = new PricedOffer();

		populatePricedOfferAssociations(pricedOffer, itineraryPricingInfo, pricedItinerary);

		for (final PTCFareBreakdownData ptcFareBreakdownData : itineraryPricingInfo.getPtcFareBreakdownDatas())
		{
			if (ptcFareBreakdownData.getPassengerTypeQuantity().getQuantity() == 0)
			{
				continue;
			}

			final OfferPrice offerPrice = new OfferPrice();
			final RequestedDate requestedDate = new RequestedDate();

			populatePriceDetails(requestedDate, ptcFareBreakdownData);
			populateRequestedDateAssociations(requestedDate, ptcFareBreakdownData);
			populateOfferItemID(offerPrice, pricedItinerary, ptcFareBreakdownData, itineraryPricingInfo);

			offerPrice.setRequestedDate(requestedDate);

			pricedOffer.getOfferPrice().add(offerPrice);
		}
		airlineOffer.setPricedOffer(pricedOffer);
	}

	/**
	 * Populate offer item id.
	 *
	 * @param offerPrice
	 * 		the offer price
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 */
	protected void populateOfferItemID(final OfferPrice offerPrice, final PricedItineraryData pricedItinerary,
			final PTCFareBreakdownData ptcFareBreakdownData, final ItineraryPricingInfoData itineraryPricingInfo)
	{
		offerPrice.setOfferItemID(getNdcOfferItemIdResolver()
				.generateAirShoppingNDCOfferItemId(ptcFareBreakdownData, pricedItinerary, itineraryPricingInfo));
	}

	/**
	 * Populate priced offer associations.
	 *
	 * @param pricedOffer
	 * 		the priced offer
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populatePricedOfferAssociations(final PricedOffer pricedOffer,
			final ItineraryPricingInfoData itineraryPricingInfo, final PricedItineraryData pricedItinerary)
	{
		final PricedFlightOfferAssocType pricedFlightOfferAssocType = new PricedFlightOfferAssocType();
		final ApplicableFlight applicableFlight = new ApplicableFlight();
		final ServiceInfoAssocType serviceInfoAssocType = new ServiceInfoAssocType();

		populateOriginDestinationReferences(applicableFlight, pricedItinerary);
		populateFlightReferences(applicableFlight, pricedItinerary);
		populateFlightSegmentReference(applicableFlight, itineraryPricingInfo);

		populateIncludedService(serviceInfoAssocType, itineraryPricingInfo);

		if (!serviceInfoAssocType.getServiceReferences().isEmpty())
		{
			pricedFlightOfferAssocType.setIncludedService(serviceInfoAssocType);
		}

		pricedFlightOfferAssocType.setApplicableFlight(applicableFlight);
		pricedOffer.getAssociations().add(pricedFlightOfferAssocType);
	}

	/**
	 * Populate flight segment reference.
	 *
	 * @param applicableFlight
	 * 		the applicable flight
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 */
	protected void populateFlightSegmentReference(final ApplicableFlight applicableFlight,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		for (final TravelBundleTemplateData bundleTemplates : itineraryPricingInfo.getBundleTemplates())
		{
			final CabinClassModel cabinClass = getCabinClassService().findCabinClassFromBundleTemplate(bundleTemplates.getId());

			if (Objects.isNull(cabinClass))
			{
				continue;
			}

			for (final TransportOfferingData transportOfferingData : bundleTemplates.getTransportOfferings())
			{
				final FlightSegmentReference flightSegmentReference = new FlightSegmentReference();
				final SimpleAircraftCabinType cabin = new SimpleAircraftCabinType();
				final MarketingName marketingName = new MarketingName();

				final CabinDesignator cabinDesignator = new CabinDesignator();
				cabinDesignator.setValue(cabinClass.getCode());
				cabin.setCabinDesignator(cabinDesignator);

				marketingName.setValue(bundleTemplates.getName());
				cabin.setMarketingName(marketingName);

				flightSegmentReference.setCabin(cabin);
				flightSegmentReference.setRef(getFlightSegmentsHashMap().get(transportOfferingData.getCode()));

				applicableFlight.getFlightSegmentReference().add(flightSegmentReference);
			}
		}
	}

	/**
	 * Populate flight references.
	 *
	 * @param applicableFlight
	 * 		the applicable flight
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populateFlightReferences(final ApplicableFlight applicableFlight, final PricedItineraryData pricedItinerary)
	{
		final FlightReferences flightReferences = new FlightReferences();
		flightReferences.getValue().add(getFlightHashMap().get(String.valueOf(pricedItinerary.getId())));
		applicableFlight.setFlightReferences(flightReferences);
	}

	/**
	 * Populate origin destination references.
	 *
	 * @param applicableFlight
	 * 		the applicable flight
	 * @param pricedItinerary
	 * 		the priced itinerary
	 */
	protected void populateOriginDestinationReferences(final ApplicableFlight applicableFlight,
			final PricedItineraryData pricedItinerary)
	{
		applicableFlight.getOriginDestinationReferences()
				.add(getOriginDestinationsHashMap().get(pricedItinerary.getItinerary().getRoute().getCode()));
	}

	/**
	 * Populate requested date associations.
	 *
	 * @param requestedDate
	 * 		the requested date
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 */
	protected void populateRequestedDateAssociations(final RequestedDate requestedDate,
			final PTCFareBreakdownData ptcFareBreakdownData)
	{
		final PricedFlightOfferAssocType association = new PricedFlightOfferAssocType();
		final TravelerInfoAssocType travelInfoAssoc = new TravelerInfoAssocType();

		populateAssociatedTraveler(travelInfoAssoc, ptcFareBreakdownData);
		association.setAssociatedTraveler(travelInfoAssoc);

		requestedDate.getAssociations().add(association);
	}

	/**
	 * Populate included service.
	 *
	 * @param serviceInfoAssocType
	 * 		the service info assoc type
	 * @param itineraryPricingInfo
	 * 		the itinerary pricing info
	 */
	protected void populateIncludedService(final ServiceInfoAssocType serviceInfoAssocType,
			final ItineraryPricingInfoData itineraryPricingInfo)
	{
		final Set<String> nonFareProductsCodeSet = itineraryPricingInfo.getBundleTemplates().stream()
				.map(bundleTemplate -> bundleTemplate.getNonFareProducts().entrySet()).flatMap(Set::stream).map(Map.Entry::getValue)
				.flatMap(List::stream).map(ProductData::getCode).collect(Collectors.toSet());

		final List<ServiceDetailType> serviceDetailTypeList = getServicesHashMap().entrySet().stream()
				.filter(serviceDetailType -> nonFareProductsCodeSet.contains(serviceDetailType.getKey())).map(Map.Entry::getValue)
				.collect(Collectors.toList());

		serviceInfoAssocType.getServiceReferences().addAll(serviceDetailTypeList);
	}

	/**
	 * Populate associated traveler.
	 *
	 * @param travelInfoAssoc
	 * 		the travel info assoc
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 */
	protected void populateAssociatedTraveler(final TravelerInfoAssocType travelInfoAssoc,
			final PTCFareBreakdownData ptcFareBreakdownData)
	{
		travelInfoAssoc.getTravelerReferences().add(getAnonymousTravelersHashMap().get(getPassengerTypeService()
				.getPassengerType(ptcFareBreakdownData.getPassengerTypeQuantity().getPassengerType().getCode()).getNdcCode()));
	}

	/**
	 * Populate price details.
	 *
	 * @param requestedDate
	 * 		the requested date
	 * @param ptcFareBreakdownData
	 * 		the ptc fare breakdown data
	 */
	protected void populatePriceDetails(final RequestedDate requestedDate, final PTCFareBreakdownData ptcFareBreakdownData)
	{
		final PriceDetail priceDetail = new PriceDetail();
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
		priceDetail.setBaseAmount(currencyAmountOptType);

		total.setValue(BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxDetailType.setTotal(total);
		total.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		priceDetail.setTaxes(taxDetailType);

		simpleCurrencyPriceType.setValue(passengerFare.getTotalFare().getValue().add(BigDecimal.valueOf(totalTaxes))
				.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		simpleCurrencyPriceType.setCode(getStoreSessionFacade().getCurrentCurrency().getIsocode());
		totalAmount.setSimpleCurrencyPrice(simpleCurrencyPriceType);
		priceDetail.setTotalAmount(totalAmount);

		requestedDate.setPriceDetail(priceDetail);
	}

	/**
	 * Set TotalPrice in the AirlineOffer based on the TotalFareData
	 *
	 * @param airlineOffer
	 * 		the airline offer listed in the air shopping response
	 * @param totalFare
	 * 		the total fare returned from the fare search
	 */
	protected void populateTotalPrice(final AirlineOffer airlineOffer, final TotalFareData totalFare)
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

		airlineOffer.setTotalPrice(totalPrice);
	}

	protected BigDecimal getTaxes(final TotalFareData totalFare)
	{
		final double totalTaxes = totalFare.getTaxes().stream().mapToDouble(tax -> tax.getPrice().getValue().doubleValue()).sum();
		return BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * Gets flight segments hash map.
	 *
	 * @return the flight segments hash map
	 */
	protected HashMap<String, ListOfFlightSegmentType> getFlightSegmentsHashMap()
	{
		return flightSegmentsHashMap;
	}

	/**
	 * Gets flight hash map.
	 *
	 * @return the flight hash map
	 */
	protected HashMap<String, Flight> getFlightHashMap()
	{
		return flightHashMap;
	}

	/**
	 * Gets origin destinations hash map.
	 *
	 * @return the origin destinations hash map
	 */
	protected HashMap<String, OriginDestination> getOriginDestinationsHashMap()
	{
		return originDestinationsHashMap;
	}

	/**
	 * Gets anonymous travelers hash map.
	 *
	 * @return the anonymous travelers hash map
	 */
	protected HashMap<String, AnonymousTravelerType> getAnonymousTravelersHashMap()
	{
		return anonymousTravelersHashMap;
	}

	/**
	 * Gets services hash map.
	 *
	 * @return the services hash map
	 */
	protected HashMap<String, ServiceDetailType> getServicesHashMap()
	{
		return servicesHashMap;
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
	 * Gets ndc service detail converter.
	 *
	 * @return the ndc service detail converter
	 */
	protected Converter<ServiceDetailType, ProductData> getNdcServiceDetailConverter()
	{
		return ndcServiceDetailConverter;
	}

	/**
	 * Sets ndc service detail converter.
	 *
	 * @param ndcServiceDetailConverter
	 * 		the ndc service detail converter
	 */
	@Required
	public void setNdcServiceDetailConverter(final Converter<ServiceDetailType, ProductData> ndcServiceDetailConverter)
	{
		this.ndcServiceDetailConverter = ndcServiceDetailConverter;
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
}
