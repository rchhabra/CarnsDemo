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
package de.hybris.platform.ndcfacades.flight.impl;

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.facades.AbstractNDCFacade;
import de.hybris.platform.ndcfacades.flight.FlightPriceFacade;
import de.hybris.platform.ndcfacades.ndc.AirlineID;
import de.hybris.platform.ndcfacades.ndc.ApplicableFlight;
import de.hybris.platform.ndcfacades.ndc.ArrivalCode;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.Departure;
import de.hybris.platform.ndcfacades.ndc.DepartureCode;
import de.hybris.platform.ndcfacades.ndc.FlightArrivalType;
import de.hybris.platform.ndcfacades.ndc.FlightCabinCoreType;
import de.hybris.platform.ndcfacades.ndc.FlightDepartureType;
import de.hybris.platform.ndcfacades.ndc.FlightDetailType;
import de.hybris.platform.ndcfacades.ndc.FlightDurationType;
import de.hybris.platform.ndcfacades.ndc.FlightNumber;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRQ.Query.Offers.Offer;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS.DataLists;
import de.hybris.platform.ndcfacades.ndc.FlightPriceRS.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.FlightReferences;
import de.hybris.platform.ndcfacades.ndc.FlightSegmentReference;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.MarketingCarrierFlightType;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadDetailType.PriceDetail;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadType;
import de.hybris.platform.ndcfacades.ndc.OfferPriceLeadType.RequestedDate;
import de.hybris.platform.ndcfacades.ndc.OfferTimeLimitSetType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.PassengerInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.PricedFlightOffer;
import de.hybris.platform.ndcfacades.ndc.PricedFlightOfferAssocType;
import de.hybris.platform.ndcfacades.ndc.SegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDetailType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ServiceInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.ServiceList;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseIDType;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseIDType.ResponseID;
import de.hybris.platform.ndcfacades.ndc.SimpleAircraftCabinType;
import de.hybris.platform.ndcfacades.ndc.SimpleCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelRouteFacade;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.CabinClassService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link FlightPriceFacade}
 */
public class DefaultFlightPriceFacade extends AbstractNDCFacade implements FlightPriceFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultFlightPriceFacade.class);

	private BundleTemplateService bundleTemplateService;
	private ProductService productService;
	private CabinClassService cabinClassService;
	private TravelRouteFacade travelRouteFacade;
	private StoreSessionFacade storeSessionFacade;
	private NDCTransportOfferingService ndcTransportOfferingService;

	private Converter<FlightPriceRQ, FlightPriceRS> ndcFlightPriceRSConverter;

	@Override
	public FlightPriceRS retrieveFlightPrice(final FlightPriceRQ flightPriceRQ) throws NDCOrderException
	{
		final FlightPriceRS flightPriceRS = new FlightPriceRS();

		setCurrencySessionCurrency(flightPriceRQ);

		createFlightPriceRS(flightPriceRQ, flightPriceRS);

		getNdcFlightPriceRSConverter().convert(flightPriceRQ, flightPriceRS);

		return flightPriceRS;
	}

	/**
	 * Method to set the currency code in session if present in the {@link FlightPriceRQ}
	 *
	 * @param flightPriceRQ
	 * 		the flight price rq
	 */
	protected void setCurrencySessionCurrency(final FlightPriceRQ flightPriceRQ)
	{
		if (Objects.nonNull(flightPriceRQ.getParameters()) && Objects.nonNull(flightPriceRQ.getParameters().getCurrCodes())
				&& CollectionUtils.isNotEmpty(flightPriceRQ.getParameters().getCurrCodes().getFiledInCurrency()))
		{
			getStoreSessionFacade()
					.setCurrentCurrency(
							flightPriceRQ.getParameters().getCurrCodes().getFiledInCurrency().get(0).getCurrCode().getValue());
		}
	}

	/**
	 * Wrapper method to generate the {@link FlightPriceRS}
	 *
	 * @param flightPriceRQ
	 * 		the flight price rq
	 * @param flightPriceRS
	 * 		the flight price rs
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void createFlightPriceRS(final FlightPriceRQ flightPriceRQ, final FlightPriceRS flightPriceRS)
			throws NDCOrderException
	{
		createDataList(flightPriceRS);

		populateAnonymousTravelerList(flightPriceRS, flightPriceRQ);

		final FlightPriceRS.PricedFlightOffers pricedFlightOffers = new FlightPriceRS.PricedFlightOffers();

		final Map<String, Map<String, TransportOfferingModel>> transportOfferings = new HashMap<>();

		for (final Offer offer : flightPriceRQ.getQuery().getOffers().getOffer())
		{
			final PricedFlightOffer pricedFlightOffer = new PricedFlightOffer();
			populatePricedFlightOffer(pricedFlightOffer, offer, flightPriceRS, transportOfferings);
			pricedFlightOffers.getPricedFlightOffer().add(pricedFlightOffer);

			if (!isValidReturnDate(transportOfferings))
			{
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_OFFER_COMBINATION));
			}
		}

		flightPriceRS.setPricedFlightOffers(pricedFlightOffers);

		populateShoppingResponseID(flightPriceRS);
	}

	/**
	 * Checks, in case of a return flight, if the departure of the inbound is subsequent of the arrival of the outbound
	 * plus the MIN_BOOKING_ADVANCE_TIME
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @return boolean true if is valid, false otherwise
	 */
	protected boolean isValidReturnDate(final Map<String, Map<String, TransportOfferingModel>> transportOfferings)
	{
		if (transportOfferings.size() < NdcservicesConstants.RETURN_FLIGHT_LEG_NUMBER)
		{
			return true;
		}

		ZonedDateTime arrivalUtcTime = TravelDateUtils.getUtcZonedDateTime(getTimeService().getCurrentTime(),
				ZoneId.systemDefault());

		for (final Map.Entry<String, TransportOfferingModel> transportOffering : transportOfferings
				.get(String.valueOf(NdcservicesConstants.OUTBOUND_FLIGHT_REF_NUMBER)).entrySet())
		{
			final ZonedDateTime offeringDepartureUtc = getNdcTransportOfferingService()
					.getArrivalZonedDateTimeFromTransportOffering(transportOffering.getValue());
			if (arrivalUtcTime.isBefore(offeringDepartureUtc))
			{
				arrivalUtcTime = offeringDepartureUtc;
			}
		}

		arrivalUtcTime = arrivalUtcTime.plus(TravelfacadesConstants.MIN_BOOKING_ADVANCE_TIME, ChronoUnit.HOURS);

		for (final Map.Entry<String, TransportOfferingModel> transportOffering : transportOfferings
				.get(String.valueOf(NdcservicesConstants.INBOUND_FLIGHT_REF_NUMBER)).entrySet())
		{
			final ZonedDateTime offeringDepartureUtc = getNdcTransportOfferingService()
					.getDepartureZonedDateTimeFromTransportOffering(transportOffering.getValue());
			if (arrivalUtcTime.isAfter(offeringDepartureUtc))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Generate a random {@link ResponseID}
	 *
	 * @param flightPriceRS
	 * 		the flight price rs
	 */
	protected void populateShoppingResponseID(final FlightPriceRS flightPriceRS)
	{
		final ShoppingResponseIDType shoppingResponseId = new ShoppingResponseIDType();
		final ResponseID responseId = new ResponseID();
		responseId.setValue(new BigInteger(130, new SecureRandom()).toString(32));
		shoppingResponseId.setResponseID(responseId);
		shoppingResponseId.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		flightPriceRS.setShoppingResponseID(shoppingResponseId);
	}

	/**
	 * Create the empty {@link DataListType} structure
	 *
	 * @param flightPriceRS
	 * 		the flight price rs
	 */
	protected void createDataList(final FlightPriceRS flightPriceRS)
	{
		final DataLists dataLists = new DataLists();

		final PassengerList passengerList = new PassengerList();
		dataLists.setPassengerList(passengerList);

		final DataListType.OriginDestinationList originDestinationList = new DataListType.OriginDestinationList();
		dataLists.setOriginDestinationList(originDestinationList);

		final ServiceList serviceList = new ServiceList();
		dataLists.setServiceList(serviceList);

		final DataListType.FlightSegmentList flightSegmentList = new DataListType.FlightSegmentList();
		dataLists.setFlightSegmentList(flightSegmentList);

		final DataListType.FlightList flightList = new DataListType.FlightList();
		dataLists.setFlightList(flightList);

		flightPriceRS.setDataLists(dataLists);
	}

	/**
	 * Populates the {@link AnonymousTravelerList} based on the {@link AnonymousTravelerType} provided in the
	 * {@link FlightPriceRQ}
	 *
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param flightPriceRQ
	 * 		the flight price rq
	 */
	protected void populateAnonymousTravelerList(final FlightPriceRS flightPriceRS, final FlightPriceRQ flightPriceRQ)
	{
		flightPriceRS.getDataLists().getPassengerList().getPassenger()
				.addAll(flightPriceRQ.getDataLists().getPassengerList().getPassenger());
	}

	/**
	 * Populates a {@link PricedFlightOffer} with the information requested in the {@link FlightPriceRQ} for each
	 * OfferItemID
	 *
	 * @param pricedFlightOffer
	 * 		the priced flight offer
	 * @param offer
	 * 		the offer
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populatePricedFlightOffer(final PricedFlightOffer pricedFlightOffer, final Offer offer,
			final FlightPriceRS flightPriceRS, final Map<String, Map<String, TransportOfferingModel>> transportOfferings)
			throws NDCOrderException
	{
		pricedFlightOffer.setOfferID(offer.getOfferID());

		final OfferTimeLimitSetType timeLimits = new OfferTimeLimitSetType();
		pricedFlightOffer.setTimeLimits(timeLimits);

		for (final ItemIDType itemIDType : offer.getOfferItemIDs().getOfferItemID())
		{
			final OfferPriceLeadType offerPriceLeadType = new OfferPriceLeadType();

			final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(itemIDType.getValue());

			populateOfferPriceLeadType(offerPriceLeadType, ndcOfferItemId, flightPriceRS, transportOfferings);
			pricedFlightOffer.getOfferPrice().add(offerPriceLeadType);
		}

		final NDCOfferItemId ndcOfferId = getNdcOfferItemIdResolver()
				.getNDCOfferItemIdFromString(pricedFlightOffer.getOfferID().getValue());

		populatePricedFlightOfferAssociations(pricedFlightOffer, flightPriceRS, ndcOfferId);
	}

	/**
	 * Populate the association related to the OriginDestinationReferences, FlightReferences, FlightSegmentReference and
	 * IncludedService
	 *
	 * @param pricedFlightOffer
	 * 		the priced flight offer
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param ndcOfferId
	 * 		the ndc offer id
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populatePricedFlightOfferAssociations(final PricedFlightOffer pricedFlightOffer,
			final FlightPriceRS flightPriceRS, final NDCOfferItemId ndcOfferId) throws NDCOrderException
	{
		final PricedFlightOfferAssocType pricedFlightOfferAssocType = new PricedFlightOfferAssocType();
		final ApplicableFlight applicableFlight = new ApplicableFlight();
		final OriginDestination originDestination = new OriginDestination();
		final SegmentReferences segmentReferences = new SegmentReferences();
		final FlightReferences flightReferences = new FlightReferences();
		final Flight flight = new Flight();

		flight.setSegmentReferences(segmentReferences);
		flight.setFlightKey(NdcfacadesConstants.FLIGHT + String.valueOf(ndcOfferId.getOriginDestinationRefNumber()));

		for (final NDCOfferItemIdBundle ndcOfferItemIdBundle : ndcOfferId.getBundleList())
		{
			final String cabinClass = getCabinClassService().findCabinClassFromBundleTemplate(ndcOfferItemIdBundle.getBundle())
					.getCode();
			final BundleTemplateModel bundleTemplate = getBundleTemplateService()
					.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle());

			populateIncludedServices(bundleTemplate, pricedFlightOfferAssocType, flightPriceRS);

			for (final String transportOfferingCode : ndcOfferItemIdBundle.getTransportOfferings())
			{
				final FlightSegmentReference flightSegmentReference = new FlightSegmentReference();
				final ListOfFlightSegmentType flightSegment = new ListOfFlightSegmentType();
				final TransportOfferingModel transportOffering = getNdcTransportOfferingService()
						.getTransportOffering(transportOfferingCode);

				if (!getNdcTransportOfferingService().isValidDate(transportOffering))
				{
					throw new NDCOrderException(
							getConfigurationService().getConfiguration().getString(NdcservicesConstants.PAST_DATE));
				}
				populateFlightSegment(flightSegment, flightPriceRS, transportOffering);
				populateFlightSegmentReference(flightSegmentReference, cabinClass, bundleTemplate, flightSegment);

				applicableFlight.getFlightSegmentReference().add(flightSegmentReference);
				flight.getSegmentReferences().getValue().add(flightSegmentReference.getRef());
			}
		}

		flightReferences.getValue().add(flight);
		applicableFlight.setFlightReferences(flightReferences);

		populateOriginDestination(originDestination, flight, ndcOfferId.getRouteCode());
		applicableFlight.getOriginDestinationReferences().add(originDestination);

		pricedFlightOfferAssocType.setApplicableFlight(applicableFlight);
		pricedFlightOffer.getAssociations().add(pricedFlightOfferAssocType);

		flightPriceRS.getDataLists().getFlightList().getFlight().add(flight);
		flightPriceRS.getDataLists().getOriginDestinationList().getOriginDestination().add(originDestination);
	}

	/**
	 * Create the {@link OriginDestination} based on the routeCode and associate to it the {@link Flight} that is
	 * operating on that {@link OriginDestination}
	 *
	 * @param originDestination
	 * 		the origin destination
	 * @param flight
	 * 		the flight
	 * @param routeCode
	 * 		the route code
	 */
	protected void populateOriginDestination(final OriginDestination originDestination, final Flight flight,
			final String routeCode)
	{
		final FlightReferences flightReferences = new FlightReferences();
		flightReferences.getValue().add(flight);
		originDestination.setFlightReferences(flightReferences);

		originDestination.setOriginDestinationKey(routeCode);

		final TravelRouteData travelRoute = getTravelRouteFacade().getTravelRoute(routeCode);

		final DepartureCode departureCode = new DepartureCode();
		departureCode.setValue(travelRoute.getOrigin().getCode());
		originDestination.setDepartureCode(departureCode);

		final ArrivalCode arrivalCode = new ArrivalCode();
		arrivalCode.setValue(travelRoute.getDestination().getCode());
		originDestination.setArrivalCode(arrivalCode);
	}

	/**
	 * Create the {@link FlightSegmentReference} based on the {@link BundleTemplateModel} associated to that particular
	 * transport offering
	 *
	 * @param flightSegmentReference
	 * 		the flight segment reference
	 * @param cabinClass
	 * 		the cabin class
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param flightSegment
	 * 		the flight segment
	 */
	protected void populateFlightSegmentReference(final FlightSegmentReference flightSegmentReference, final String cabinClass,
			final BundleTemplateModel bundleTemplate, final ListOfFlightSegmentType flightSegment)
	{
		final SimpleAircraftCabinType cabin = new SimpleAircraftCabinType();
		final FlightCabinCoreType.CabinDesignator cabinDesignator = new FlightCabinCoreType.CabinDesignator();
		final FlightCabinCoreType.MarketingName marketingName = new FlightCabinCoreType.MarketingName();

		cabinDesignator.setValue(cabinClass);
		cabin.setCabinDesignator(cabinDesignator);

		marketingName.setValue(bundleTemplate.getName());
		cabin.setMarketingName(marketingName);

		flightSegmentReference.setCabin(cabin);
		flightSegmentReference.setRef(flightSegment);
	}

	/**
	 * Populate the {@link ServiceInfoAssocType} based on the ancillaries contained in the bundle
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param pricedFlightOfferAssocType
	 * 		the priced flight offer assoc type
	 * @param flightPriceRS
	 * 		the flight price rs
	 */
	protected void populateIncludedServices(final BundleTemplateModel bundleTemplate,
			final PricedFlightOfferAssocType pricedFlightOfferAssocType, final FlightPriceRS flightPriceRS)
	{
		final ServiceInfoAssocType serviceInfoAssoc = new ServiceInfoAssocType();

		for (final BundleTemplateModel childTemplate : bundleTemplate.getChildTemplates())
		{
			if (CollectionUtils.isEmpty(childTemplate.getProducts())
					|| !(childTemplate.getProducts().stream().allMatch(product -> (product instanceof AncillaryProductModel
							|| Objects.equals(ProductType.ANCILLARY, product.getProductType())))))
			{
				continue;
			}

			for (final ProductModel product : childTemplate.getProducts())
			{
				final Optional<ServiceDetailType> optionalService = flightPriceRS.getDataLists().getServiceList().getService()
						.stream().filter(service -> StringUtils.equals(service.getObjectKey(), product.getCode())).findFirst();

				if (optionalService.isPresent())
				{
					serviceInfoAssoc.getServiceReferences().add(optionalService.get());
				}
				else
				{
					final ServiceDetailType serviceDetailType = new ServiceDetailType();
					populatedServiceDetailType(serviceDetailType, product);
					flightPriceRS.getDataLists().getServiceList().getService().add(serviceDetailType);
					serviceInfoAssoc.getServiceReferences().add(serviceDetailType);
				}
			}
		}

		if (CollectionUtils.isNotEmpty(serviceInfoAssoc.getServiceReferences()))
		{
			pricedFlightOfferAssocType.setIncludedService(serviceInfoAssoc);
		}
	}

	/**
	 * Create the {@link ServiceDetailType} based on the information contained in the ProductModel
	 *
	 * @param serviceDetailType
	 * 		the service detail type
	 * @param product
	 * 		the product
	 */
	protected void populatedServiceDetailType(final ServiceDetailType serviceDetailType, final ProductModel product)
	{
		final ServiceDetailType.Detail detail = new ServiceDetailType.Detail();
		final ServiceCoreType.Name name = new ServiceCoreType.Name();
		final ServiceIDType serviceId = new ServiceIDType();

		serviceDetailType.setObjectKey(product.getCode());

		populateDescription(serviceDetailType, product);

		serviceId.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

		serviceId.setValue(product.getCode());
		serviceDetailType.setServiceID(serviceId);

		name.setValue(product.getName());
		serviceDetailType.setName(name);

		serviceDetailType.setDetail(detail);
	}

	/**
	 * Populate the {@link ServiceDetailType} description based on the information contained in the ProductModel
	 *
	 * @param serviceDetailType
	 * 		the service detail type
	 * @param product
	 * 		the product
	 */
	protected void populateDescription(final ServiceDetailType serviceDetailType, final ProductModel product)
	{
		final ServiceDescriptionType serviceDetail = new ServiceDescriptionType();
		final ServiceDescriptionType.Description description = new ServiceDescriptionType.Description();
		final ServiceDescriptionType.Description name = new ServiceDescriptionType.Description();

		if (!Objects.isNull(product.getDescription()))
		{
			description.setApplication(product.getDescription());
			serviceDetail.getDescription().add(description);
		}

		if (CollectionUtils.isEmpty(serviceDetail.getDescription()))
		{
			name.setApplication(product.getName());
			serviceDetail.getDescription().add(name);
		}

		serviceDetailType.setDescriptions(serviceDetail);
	}

	/**
	 * Populate the {@link ListOfFlightSegmentType} based on the information contained in the
	 * {@link TransportOfferingModel}
	 *
	 * @param flightSegment
	 * 		the flight segment
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param transportOffering
	 * 		the transport offering
	 */
	protected void populateFlightSegment(final ListOfFlightSegmentType flightSegment, final FlightPriceRS flightPriceRS,
			final TransportOfferingModel transportOffering)
	{
		flightSegment.setSegmentKey(transportOffering.getCode());
		final FlightArrivalType arrival = new FlightArrivalType();
		final FlightArrivalType.AirportCode arrivalAirportCode = new FlightArrivalType.AirportCode();

		arrival.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOffering.getArrivalTime()));
		arrival.setTime(NdcFacadesUtils.dateToTimeString(transportOffering.getArrivalTime()));

		arrival.setAirportName(transportOffering.getTravelSector().getDestination().getName());

		arrivalAirportCode.setValue(transportOffering.getTravelSector().getDestination().getCode());
		arrival.setAirportCode(arrivalAirportCode);
		flightSegment.setArrival(arrival);

		final Departure departure = new Departure();
		final FlightDepartureType.AirportCode departureAirportCode = new FlightDepartureType.AirportCode();

		departure.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOffering.getDepartureTime()));
		departure.setTime(NdcFacadesUtils.dateToTimeString(transportOffering.getDepartureTime()));

		departure.setAirportName(transportOffering.getTravelSector().getOrigin().getName());

		departureAirportCode.setValue(transportOffering.getTravelSector().getOrigin().getCode());
		departure.setAirportCode(departureAirportCode);
		flightSegment.setDeparture(departure);

		final FlightDetailType flightDetail = new FlightDetailType();
		final FlightDurationType flightDuration = new FlightDurationType();

		try
		{
			final Duration duration = DatatypeFactory.newInstance().newDuration(transportOffering.getDuration());
			flightDuration.setValue(duration);
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.debug(e);
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.DURATION_CONVERSION_ERROR));
		}

		flightDetail.setFlightDuration(flightDuration);
		flightSegment.setFlightDetail(flightDetail);

		if (Objects.isNull(transportOffering.getNumber()) || Objects.isNull(transportOffering.getTravelProvider()))
		{
			throw new ConversionException("Missing flight number or travel provider");
		}

		final String travelProviderCode = transportOffering.getTravelProvider().getCode();
		final String transportOfferingNumber = transportOffering.getNumber();

		final MarketingCarrierFlightType marketingCarrier = new MarketingCarrierFlightType();
		final AirlineID airlineId = new AirlineID();
		final FlightNumber flightNumber = new FlightNumber();

		airlineId.setValue(travelProviderCode);

		marketingCarrier.setAirlineID(airlineId);

		flightNumber.setValue(String.valueOf(transportOfferingNumber));
		marketingCarrier.setFlightNumber(flightNumber);

		flightSegment.setMarketingCarrier(marketingCarrier);

		final ListOfFlightSegmentType.OperatingCarrier operatingCarrier = new ListOfFlightSegmentType.OperatingCarrier();

		airlineId.setValue(travelProviderCode);
		operatingCarrier.setAirlineID(airlineId);

		flightNumber.setValue(transportOfferingNumber);
		operatingCarrier.setFlightNumber(flightNumber);

		flightSegment.setOperatingCarrier(operatingCarrier);

		flightPriceRS.getDataLists().getFlightSegmentList().getFlightSegment().add(flightSegment);
	}

	/**
	 * Populate the {@link OfferPriceLeadType} based on {@link NDCOfferItemId} that it is referring to
	 *
	 * @param offerPriceLeadType
	 * 		the offer price lead type
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populateOfferPriceLeadType(final OfferPriceLeadType offerPriceLeadType, final NDCOfferItemId ndcOfferItemId,
			final FlightPriceRS flightPriceRS, final Map<String, Map<String, TransportOfferingModel>> transportOfferings)
			throws NDCOrderException
	{
		final RequestedDate requestedDate = new RequestedDate();

		final List<PassengerType> passengers = flightPriceRS.getDataLists().getPassengerList().getPassenger().stream()
				.filter(passenger -> StringUtils.equals(passenger.getPTC(), ndcOfferItemId.getPtc())).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(passengers))
		{
			throw new ConversionException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.MISSING_TRAVELER_OFFER_ITEM_ID_ASSOCIATION));
		}

		populatePriceDetails(requestedDate, ndcOfferItemId, passengers, transportOfferings);
		offerPriceLeadType.setRequestedDate(requestedDate);

		offerPriceLeadType.setOfferItemID(getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId));

		populateOfferPriceAssociations(offerPriceLeadType, flightPriceRS, ndcOfferItemId.getPtc());
	}

	/**
	 * Populates the base price and the taxes in the {@link PriceDetail}
	 *
	 * @param requestedDate
	 * 		the requested date
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param anonymousTraveler
	 * 		the anonymous traveler
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populatePriceDetails(final RequestedDate requestedDate, final NDCOfferItemId ndcOfferItemId,
			final List<PassengerType> passengers, final Map<String, Map<String, TransportOfferingModel>> transportOfferings)
			throws NDCOrderException
	{
		final PriceDetail priceDetail = new PriceDetail();
		final PriceDetail.TotalAmount totalAmount = new PriceDetail.TotalAmount();
		final SimpleCurrencyPriceType simpleCurrencyPriceType = new SimpleCurrencyPriceType();
		final CurrencyAmountOptType currencyAmountOptType = new CurrencyAmountOptType();
		final TaxDetailType taxDetailType = new TaxDetailType();
		final TaxDetailType.Total total = new TaxDetailType.Total();

		double totalTaxes = 0.0;
		double totalBasePrice = 0.0;

		for (final NDCOfferItemIdBundle ndcOfferItemIdBundle : ndcOfferItemId.getBundleList())
		{
			final BundleTemplateModel bundleTemplate = getBundleTemplateService()
					.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle());

			final List<TransportOfferingModel> bundleTransportOfferings = getTransportOfferingsFromMap(transportOfferings,
					ndcOfferItemId.getOriginDestinationRefNumber(), ndcOfferItemIdBundle.getTransportOfferings());
			final ProductModel product = getProductService().getProductForCode(ndcOfferItemIdBundle.getFareProduct());

			final double basePrice = calculateBasePrice(getFareProductChildBundle(bundleTemplate), product, bundleTransportOfferings,
					ndcOfferItemId.getRouteCode());
			final double taxes = calculateTaxes(basePrice, product, passengers, bundleTransportOfferings);

			totalBasePrice += basePrice;
			totalTaxes += taxes;

			for (final ProductModel ancillaries : getAncillariesFromChildTemplate(bundleTemplate))
			{
				final BundleTemplateModel ancillariesBundleTemplate = getAncillaryChildBundle(bundleTemplate);
				final double ancillaryBasePrice = calculateBasePrice(ancillariesBundleTemplate, ancillaries, bundleTransportOfferings,
						ndcOfferItemId.getRouteCode());
				final double ancillaryTaxes = calculateTaxes(basePrice, ancillaries, passengers, bundleTransportOfferings);

				totalBasePrice += ancillaryBasePrice;
				totalTaxes += ancillaryTaxes;
			}
		}

		totalBasePrice = totalBasePrice * CollectionUtils.size(passengers);

		currencyAmountOptType
				.setValue(BigDecimal.valueOf(totalBasePrice).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));

		final String currencyIsoCode = getStoreSessionFacade().getCurrentCurrency().getIsocode();
		currencyAmountOptType.setCode(currencyIsoCode);
		priceDetail.setBaseAmount(currencyAmountOptType);

		total.setValue(BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxDetailType.setTotal(total);
		total.setCode(currencyIsoCode);
		priceDetail.setTaxes(taxDetailType);

		simpleCurrencyPriceType.setValue(BigDecimal.valueOf(totalBasePrice).add(BigDecimal.valueOf(totalTaxes))
				.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		simpleCurrencyPriceType.setCode(currencyIsoCode);
		totalAmount.setSimpleCurrencyPrice(simpleCurrencyPriceType);
		priceDetail.setTotalAmount(totalAmount);

		requestedDate.setPriceDetail(priceDetail);
	}

	/**
	 * Return the list of the {@link TransportOfferingModel} that refer to the provided list of codes. If they are not
	 * present in the Map, the {@link TransportOfferingModel} are fetched, added to the map and to the returned list
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param originDestRefNumber
	 * 		the origin dest ref number
	 * @param transportOfferingsCodes
	 * 		the transport offerings codes
	 *
	 * @return transport offerings from map
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected List<TransportOfferingModel> getTransportOfferingsFromMap(
			final Map<String, Map<String, TransportOfferingModel>> transportOfferings, final int originDestRefNumber,
			final List<String> transportOfferingsCodes) throws NDCOrderException
	{
		final List<TransportOfferingModel> transportOfferingModels = new LinkedList<>();

		for (final String transportOfferingsCode : transportOfferingsCodes)
		{
			if (transportOfferings.containsKey(String.valueOf(originDestRefNumber))
					&& transportOfferings.get(String.valueOf(originDestRefNumber)).containsKey(transportOfferingsCode))
			{
				transportOfferingModels.add(transportOfferings.get(String.valueOf(originDestRefNumber)).get(transportOfferingsCode));
			}
			else
			{
				final TransportOfferingModel transportOfferingModel = getNdcTransportOfferingService()
						.getTransportOffering(transportOfferingsCode);
				if (!getNdcTransportOfferingService().isValidDate(transportOfferingModel))
				{
					throw new NDCOrderException(
							getConfigurationService().getConfiguration().getString(NdcservicesConstants.PAST_DATE));
				}

				transportOfferingModels.add(transportOfferingModel);
				if (transportOfferings.containsKey(String.valueOf(originDestRefNumber)))
				{
					transportOfferings.get(String.valueOf(originDestRefNumber)).put(transportOfferingsCode, transportOfferingModel);
				}
				else
				{
					final Map<String, TransportOfferingModel> codeToTransportOfferingMap = new HashMap<>();
					codeToTransportOfferingMap.put(transportOfferingsCode, transportOfferingModel);
					transportOfferings.put(String.valueOf(originDestRefNumber), codeToTransportOfferingMap);
				}
			}
		}
		return transportOfferingModels;
	}

	/**
	 * If present, retrieves the the ancillaries contained in the child bundle, an empty list otherwise.
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 *
	 * @return ancillaries from child template
	 */
	protected List<ProductModel> getAncillariesFromChildTemplate(final BundleTemplateModel bundleTemplate)
	{
		for (final BundleTemplateModel childTemplate : bundleTemplate.getChildTemplates())
		{
			if (CollectionUtils.isNotEmpty(childTemplate.getProducts())
					&& (childTemplate.getProducts().stream().allMatch(product -> (product instanceof AncillaryProductModel
							|| Objects.equals(ProductType.ANCILLARY, product.getProductType())))))
			{
				return childTemplate.getProducts();
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Calculate the base price associated to a particular product, bundleTemplate, route and transportOfferings
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param product
	 * 		the product
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param routeCode
	 * 		the route code
	 *
	 * @return the base price
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected double calculateBasePrice(final BundleTemplateModel bundleTemplate, final ProductModel product,
			final List<TransportOfferingModel> transportOfferings, final String routeCode) throws NDCOrderException
	{
		final PriceInformation priceInfo = getPriceInformation(bundleTemplate, product, transportOfferings, routeCode);

		return priceInfo.getPriceValue().getValue();
	}

	/**
	 * Calculate the taxes associated to a particular {@link ProductModel}, {@link PassengerTypeModel}, and list of
	 * {@link TransportOfferingModel}
	 *
	 * @param basePrice
	 * 		the base price
	 * @param product
	 * 		the product
	 * @param anonymousTraveler
	 * 		the anonymous traveler
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @return the taxes value
	 */
	protected double calculateTaxes(final double basePrice, final ProductModel product, final List<PassengerType> passengers,
			final List<TransportOfferingModel> transportOfferings)
	{
		final List<String> countryCodes = getCountryCodesFromTransportOfferings(transportOfferings);
		final List<String> transportLocationCodes = transportOfferings.stream()
				.map(transportOfferingModel -> transportOfferingModel.getTravelSector().getOrigin().getCode())
				.collect(Collectors.toList());
		setTaxSearchCriteriaInContext(transportLocationCodes, countryCodes,
				getNdcPassengerTypeService().getPassengerType(passengers.get(0).getPTC()).getCode());

		final List<TaxInformation> taxInfo = getTravelCommercePriceService().getProductTaxInformations(product);
		final List<TaxValue> taxValues = createTaxValues(basePrice, taxInfo);

		return taxValues.stream().mapToDouble(TaxValue::getValue).sum() * CollectionUtils.size(passengers);
	}

	/**
	 * Method to create a list of TaxValue based on the list of TaxInformation and base price provided
	 *
	 * @param productPrice
	 * 		the product price
	 * @param taxInfos
	 * 		the tax infos
	 *
	 * @return list of tax values
	 */
	protected List<TaxValue> createTaxValues(final double productPrice, final List<TaxInformation> taxInfos)
	{
		final List<TaxValue> taxes = new ArrayList<>();
		for (final TaxInformation taxInfo : taxInfos)
		{
			double taxAmount;
			if (taxInfo.getTaxValue().isAbsolute())
			{
				taxAmount = taxInfo.getTaxValue().getValue();
			}
			else
			{
				final double relativeTotalTaxRate = taxInfo.getTaxValue().getValue() / 100.0;
				taxAmount = productPrice * relativeTotalTaxRate;
			}
			final TaxValue taxData = new TaxValue(taxInfo.getTaxValue().getCode(), taxAmount, taxInfo.getTaxValue().isAbsolute(),
					taxInfo.getTaxValue().getCurrencyIsoCode());
			taxes.add(taxData);
		}
		return taxes;
	}

	/**
	 * Populate the associations related to the traveller {@link AnonymousTravelerType}
	 *
	 * @param offerPriceLeadType
	 * 		the offer price lead type
	 * @param flightPriceRS
	 * 		the flight price rs
	 * @param ptc
	 * 		the ptc
	 */
	protected void populateOfferPriceAssociations(final OfferPriceLeadType offerPriceLeadType, final FlightPriceRS flightPriceRS,
			final String ptc)
	{
		final PricedFlightOfferAssocType assocType = new PricedFlightOfferAssocType();
		final PassengerInfoAssocType travelerInfoAssocType = new PassengerInfoAssocType();

		final List<PassengerType> passengers = flightPriceRS.getDataLists().getPassengerList().getPassenger().stream()
				.filter(passenger -> StringUtils.equals(passenger.getPTC(), ptc)).collect(Collectors.toList());

		if (CollectionUtils.isEmpty(passengers))
		{
			throw new ConversionException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.MISSING_TRAVELER_OFFER_ITEM_ID_ASSOCIATION));
		}

		travelerInfoAssocType.getPassengerReferences().addAll(passengers);
		assocType.setAssociatedPassenger(travelerInfoAssocType);
		offerPriceLeadType.getRequestedDate().getAssociations().add(assocType);
	}

	/**
	 * Return the currency IsoCode contained in the CurrCode element in the Parameters of the FlightPriceRQ
	 *
	 * @param flightPriceRQ
	 * 		the flight price rq
	 *
	 * @return currency from parameters
	 */
	/*protected String getCurrencyFromParameters(final FlightPriceRQ flightPriceRQ)
	{
		return flightPriceRQ.getParameters().getCurrCodes().getCurrCode().get(0).getValue();
	}*/

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
	 * Gets bundle template service.
	 *
	 * @return the bundle template service
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * Sets bundle template service.
	 *
	 * @param bundleTemplateService
	 * 		the bundle template service
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	/**
	 * Gets product service.
	 *
	 * @return the product service
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets product service.
	 *
	 * @param productService
	 * 		the product service
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
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
	 * Gets travel route facade.
	 *
	 * @return the travel route facade
	 */
	protected TravelRouteFacade getTravelRouteFacade()
	{
		return travelRouteFacade;
	}

	/**
	 * Sets travel route facade.
	 *
	 * @param travelRouteFacade
	 * 		the travel route facade
	 */
	@Required
	public void setTravelRouteFacade(final TravelRouteFacade travelRouteFacade)
	{
		this.travelRouteFacade = travelRouteFacade;
	}

	/**
	 * Gets ndc flight price rs converter.
	 *
	 * @return the ndc flight price rs converter
	 */
	protected Converter<FlightPriceRQ, FlightPriceRS> getNdcFlightPriceRSConverter()
	{
		return ndcFlightPriceRSConverter;
	}

	/**
	 * Sets ndc flight price rs converter.
	 *
	 * @param ndcFlightPriceRSConverter
	 * 		the ndc flight price rs converter
	 */
	@Required
	public void setNdcFlightPriceRSConverter(final Converter<FlightPriceRQ, FlightPriceRS> ndcFlightPriceRSConverter)
	{
		this.ndcFlightPriceRSConverter = ndcFlightPriceRSConverter;
	}

	/**
	 * Gets ndc transport offering service.
	 *
	 * @return the ndc transport offering service
	 */
	protected NDCTransportOfferingService getNdcTransportOfferingService()
	{
		return ndcTransportOfferingService;
	}

	/**
	 * Sets ndc transport offering service.
	 *
	 * @param ndcTransportOfferingService
	 * 		the ndc transport offering service
	 */
	@Required
	public void setNdcTransportOfferingService(final NDCTransportOfferingService ndcTransportOfferingService)
	{
		this.ndcTransportOfferingService = ndcTransportOfferingService;
	}
}
