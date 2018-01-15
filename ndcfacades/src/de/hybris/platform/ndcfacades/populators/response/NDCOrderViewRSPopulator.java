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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AircraftCode;
import de.hybris.platform.ndcfacades.ndc.AircraftSummaryType;
import de.hybris.platform.ndcfacades.ndc.AirlineID;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType.OtherID;
import de.hybris.platform.ndcfacades.ndc.BookingReferences;
import de.hybris.platform.ndcfacades.ndc.CodesetType;
import de.hybris.platform.ndcfacades.ndc.CommissionType;
import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.Contacts.Contact;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadata;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadatas;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.DataListType.SeatList;
import de.hybris.platform.ndcfacades.ndc.Departure;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Taxes;
import de.hybris.platform.ndcfacades.ndc.EmailType;
import de.hybris.platform.ndcfacades.ndc.EmailType.Address;
import de.hybris.platform.ndcfacades.ndc.FlightArrivalType;
import de.hybris.platform.ndcfacades.ndc.FlightDepartureType;
import de.hybris.platform.ndcfacades.ndc.FlightDetailType;
import de.hybris.platform.ndcfacades.ndc.FlightDurationType;
import de.hybris.platform.ndcfacades.ndc.FlightItemType;
import de.hybris.platform.ndcfacades.ndc.FlightNumber;
import de.hybris.platform.ndcfacades.ndc.FlightType;
import de.hybris.platform.ndcfacades.ndc.FlightType.Flight;
import de.hybris.platform.ndcfacades.ndc.FlightType.Flight.OperatingCarrier;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.MarketingCarrierFlightType;
import de.hybris.platform.ndcfacades.ndc.ObjectFactory;
import de.hybris.platform.ndcfacades.ndc.OrdViewMetadataType;
import de.hybris.platform.ndcfacades.ndc.OrderCoreType.TotalOrderPrice;
import de.hybris.platform.ndcfacades.ndc.OrderIDType;
import de.hybris.platform.ndcfacades.ndc.OrderItemCoreType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.OrderItemCoreType.OrderItem.Associations;
import de.hybris.platform.ndcfacades.ndc.OrderViewProcessType;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.Order;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.Order.OrderItems;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.Passengers;
import de.hybris.platform.ndcfacades.ndc.Passenger;
import de.hybris.platform.ndcfacades.ndc.PassengerSummaryType.Gender;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType.Row;
import de.hybris.platform.ndcfacades.ndc.SeatMapRowNbrType;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDetailType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ServiceInfoAssocType;
import de.hybris.platform.ndcfacades.ndc.ServiceList;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType.Total;
import de.hybris.platform.ndcfacades.ndc.TravelerCoreType.PTC;
import de.hybris.platform.ndcfacades.ndc.TravelerGenderSimpleType;
import de.hybris.platform.ndcfacades.ndc.TravelerSummaryType.Name;
import de.hybris.platform.ndcfacades.ndc.TravelerSummaryType.Name.Given;
import de.hybris.platform.ndcfacades.ndc.TravelerSummaryType.Name.Surname;
import de.hybris.platform.ndcfacades.ndc.TravelerSummaryType.ProfileID;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.ndcservices.services.NDCTransportVehicleInfoService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * NDC OrderModel OrderViewRS Populator
 */
public class NDCOrderViewRSPopulator implements Populator<OrderModel, OrderViewRS>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderViewRSPopulator.class);

	private final static String BOOKING_REFERENCE_ID = "other";
	private final static int PASSENGER_QUANTITY = 1;

	private final HashMap<String, ListOfFlightSegmentType> flightSegmentsHashMap = new HashMap<>();
	private final HashMap<String, ServiceDetailType> servicesHashMap = new HashMap<>();
	private final HashMap<String, ListOfSeatType> seats = new HashMap<>();

	private ConfigurationService configurationService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private NDCTransportOfferingService ndcTransportOfferingService;
	private NDCTransportVehicleInfoService ndcTransportVehicleInfoService;

	@Override
	public void populate(final OrderModel orderModel, final OrderViewRS orderViewRS) throws ConversionException
	{
		final Order order = new Order();

		populateOrderID(order);
		populateBookingReference(order, orderModel);
		populateTotalOrderPrice(order, orderModel);
		populateResponse(orderViewRS, orderModel);
		populateOrderItems(orderViewRS, order, orderModel);

		final DataListType dataList = new DataListType();
		populateDatalists(dataList);
		orderViewRS.getResponse().setDataLists(dataList);

		orderViewRS.getResponse().getOrder().add(order);
	}

	/**
	 * Populate dataLists element with the flight segments and services contained in the hash maps
	 *
	 * @param dataLists
	 * 		the data lists
	 */
	protected void populateDatalists(final DataListType dataLists)
	{
		final ServiceList serviceList = new ServiceList();
		final FlightSegmentList flightSegmentList = new FlightSegmentList();

		flightSegmentList.getFlightSegment().addAll(getFlightSegmentsHashMap().values());
		serviceList.getService().addAll(getServicesHashMap().values());

		if (CollectionUtils.isNotEmpty(getSeats().values()))
		{
			final SeatList seatList = new SeatList();
			seatList.getSeats().addAll(getSeats().values());
			dataLists.setSeatList(seatList);
		}
		dataLists.setFlightSegmentList(flightSegmentList);
		dataLists.setServiceList(serviceList);
	}

	/**
	 * Populate order items.
	 *
	 * @param orderViewRS
	 * 		the order view rs
	 * @param order
	 * 		the order
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateOrderItems(final OrderViewRS orderViewRS, final Order order, final OrderModel orderModel)
	{
		final OrderItems orderItems = new OrderItems();

		createOrderItem(orderViewRS, orderItems, orderModel);

		order.setOrderItems(orderItems);
	}

	/**
	 * Group the order entry per passenger per route and create the OrderItems associated to them. NOTE: Ancillaries not
	 * associated to passengers not handled
	 *
	 * @param orderViewRS
	 * 		the order view rs
	 * @param orderItems
	 * 		the order items
	 * @param orderModel
	 * 		the order model
	 */
	protected void createOrderItem(final OrderViewRS orderViewRS, final OrderItems orderItems, final OrderModel orderModel)
	{
		final Map<String, List<AbstractOrderEntryModel>> transportOfferingToProductsMap = new HashMap<>();

		orderModel.getEntries().stream().filter(AbstractOrderEntryModel::getActive)
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo().getTravelRoute()))
				.filter(entry -> CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers())).forEach(entry ->
				entry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering ->
				{
					if (Objects.nonNull(transportOfferingToProductsMap.get(transportOffering.getCode())))
					{
						final List<AbstractOrderEntryModel> orderEntries = transportOfferingToProductsMap
								.get(transportOffering.getCode());
						orderEntries.add(entry);
						transportOfferingToProductsMap.put(transportOffering.getCode(), orderEntries);
					}
					else
					{
						final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();
						orderEntries.add(entry);
						transportOfferingToProductsMap.put(transportOffering.getCode(), orderEntries);
					}
				}));

		final Map<String, Map<TravellerModel, List<AbstractOrderEntryModel>>> productsPerTransportOfferingPerPassengers = new HashMap<>();

		for (final Map.Entry<String, List<AbstractOrderEntryModel>> routeToProductsMap : transportOfferingToProductsMap.entrySet())
		{
			final Map<TravellerModel, List<AbstractOrderEntryModel>> productsPerPassenger = routeToProductsMap.getValue().stream()
					.collect(Collectors.groupingBy(
							entry -> entry.getTravelOrderEntryInfo().getTravellers().stream().collect(Collectors.toList()).get(0)));

			productsPerTransportOfferingPerPassengers.put(routeToProductsMap.getKey(), productsPerPassenger);
		}

		for (final Map.Entry<String, Map<TravellerModel, List<AbstractOrderEntryModel>>> productsPerTransportOfferingPerPassenger : productsPerTransportOfferingPerPassengers
				.entrySet())
		{
			for (final Map.Entry<TravellerModel, List<AbstractOrderEntryModel>> entries : productsPerTransportOfferingPerPassenger
					.getValue().entrySet())
			{
				populateOfferItem(entries, orderViewRS, orderItems, orderModel, productsPerTransportOfferingPerPassenger.getKey());
			}
		}
	}

	/**
	 * Populate the OfferItem generating the OrderOfferID, flight and associations
	 *
	 * @param productsPerPassenger
	 * 		the products per passenger
	 * @param orderViewRS
	 * 		the order view rs
	 * @param orderItems
	 * 		the order items
	 * @param orderModel
	 * 		the order model
	 * @param transportOfferingCode
	 * 		the transport offering code
	 */
	protected void populateOfferItem(final Map.Entry<TravellerModel, List<AbstractOrderEntryModel>> productsPerPassenger,
			final OrderViewRS orderViewRS, final OrderItems orderItems, final OrderModel orderModel,
			final String transportOfferingCode)
	{
		final TransportOfferingModel transportOfferingModel = getNdcTransportOfferingService()
				.getTransportOffering(transportOfferingCode);
		final Set<AbstractOrderEntryModel> fareProductOrderEntries = productsPerPassenger.getValue().stream()
				.filter(orderEntry -> ProductType.FARE_PRODUCT.equals(orderEntry.getProduct().getProductType())
						|| orderEntry.getProduct() instanceof FareProductModel)
				.collect(Collectors.toSet());

		final OrderItem orderItem = new OrderItem();

		final ItemIDType orderItemID = new ItemIDType();
		populateOrderItemID(orderItemID, fareProductOrderEntries.stream().collect(Collectors.toList()));
		orderItem.setOrderItemID(orderItemID);

		final SeatItem seatItem = new SeatItem();
		populateAccommodationEntry(orderModel.getSelectedAccommodations(), productsPerPassenger.getKey(), seatItem,
				transportOfferingCode);
		if (CollectionUtils.isNotEmpty(getSeats().values()))
		{
			orderItem.setSeatItem(seatItem);
		}

		final FlightItemType flightItemType = new FlightItemType();
		populateFlightType(transportOfferingModel, flightItemType);
		orderItem.setFlightItem(flightItemType);

		final List<AbstractOrderEntryModel> ancillaryOrderEntries = productsPerPassenger.getValue().stream()
				.filter(orderEntry -> ProductType.ANCILLARY.equals(orderEntry.getProduct().getProductType())
						|| orderEntry.getProduct() instanceof AncillaryProductModel)
				.filter(orderEntry -> orderEntry.getTravelOrderEntryInfo().getTransportOfferings().contains(transportOfferingModel))
				.collect(Collectors.toList());

		final TravellerModel traveller = productsPerPassenger.getKey();

		populateAssociations(orderItem, orderViewRS, ancillaryOrderEntries, traveller);

		orderItems.getOrderItem().add(orderItem);
	}

	/**
	 * This method populate accommodation(seat) data for each traveller.
	 *
	 * @param selectedAccommodations
	 * 		the selected accommodations
	 * @param travellerModel
	 * 		the traveller model
	 * @param seatItem
	 * 		the seat item
	 * @param transportOfferingCode
	 * 		the transport offering code
	 */
	protected void populateAccommodationEntry(final List<SelectedAccommodationModel> selectedAccommodations,
			final TravellerModel travellerModel, final SeatItem seatItem, final String transportOfferingCode)
	{
		final SelectedAccommodationModel selectedAccommodation = getSelectedAccomForTraveller(selectedAccommodations,
				travellerModel, transportOfferingCode);
		if (Objects.nonNull(selectedAccommodation))
		{
			final SeatLocationType location = new SeatLocationType();
			populateSeatLocation(selectedAccommodation, location);
			final ObjectFactory factory = new ObjectFactory();
			final ListOfSeatType seatType = new ListOfSeatType();
			seatType.setListKey(new StringBuilder(location.getColumn()).append(location.getRow().getNumber().getValue())
					.append(selectedAccommodation.getTransportOffering().getCode()).toString());
			final JAXBElement<Object> seatRef = factory.createSeatReference(seatType);
			seatType.setLocation(location);
			seatItem.getSeatReference().add(seatRef);
			if (Objects.isNull(getSeats().get(seatType.getListKey())))
			{
				getSeats().put(seatType.getListKey(), seatType);
			}
		}
	}

	/**
	 * This method populates {@link SeatLocationType} data for each and every seat.
	 *
	 * @param selectedAccommodation
	 * 		the selected accommodation
	 * @param location
	 * 		the location
	 */
	protected void populateSeatLocation(final SelectedAccommodationModel selectedAccommodation, final SeatLocationType location)
	{
		final ConfiguredAccommodationModel seat = selectedAccommodation.getConfiguredAccommodation();
		if (Objects.nonNull(seat) && Objects.nonNull(seat.getSuperConfiguredAccommodation()))
		{
			final ConfiguredAccommodationModel row = seat.getSuperConfiguredAccommodation().getSuperConfiguredAccommodation();
			if (Objects.nonNull(row) && seat.getIdentifier().contains(row.getNumber().toString()))
			{
				location.setColumn(seat.getIdentifier().substring(row.getNumber().toString().length()));
				final Row seatRow = new Row();
				final SeatMapRowNbrType rowNum = new SeatMapRowNbrType();
				rowNum.setValue(row.getNumber().toString());
				seatRow.setNumber(rowNum);
				location.setRow(seatRow);
			}
		}
	}

	/**
	 * Returns an instance of {@link SelectedAccommodationModel} from list for given {@link TravellerModel}
	 *
	 * @param selectedAccommodations
	 * 		the selected accommodations
	 * @param travellerModel
	 * 		the traveller model
	 * @param transportOfferingCode
	 * 		the transport offering code
	 *
	 * @return the selected accom for traveller
	 */
	protected SelectedAccommodationModel getSelectedAccomForTraveller(
			final List<SelectedAccommodationModel> selectedAccommodations, final TravellerModel travellerModel,
			final String transportOfferingCode)
	{
		return selectedAccommodations.stream()
				.filter(selectedAccommodation -> selectedAccommodation.getTraveller().getPk().equals(travellerModel.getPk()))
				.collect(Collectors.toList()).stream().filter(selectedAccommodation -> StringUtils
						.equals(selectedAccommodation.getTransportOffering().getCode(), transportOfferingCode))
				.findFirst().orElse(null);
	}

	/**
	 * Populate the associated passenger and associated services to a particular OfferItem
	 *
	 * @param orderItem
	 * 		the order item
	 * @param orderViewRS
	 * 		the order view rs
	 * @param ancillaryOrderEntries
	 * 		the ancillary order entries
	 * @param traveller
	 * 		the traveller
	 */
	protected void populateAssociations(final OrderItem orderItem, final OrderViewRS orderViewRS,
			final List<AbstractOrderEntryModel> ancillaryOrderEntries, final TravellerModel traveller)
	{
		final Associations associations = new Associations();

		populateAssociatedPassengers(associations, orderViewRS, traveller);

		populatedAssociatedServices(associations, ancillaryOrderEntries);

		orderItem.setAssociations(associations);
	}

	/**
	 * Populate the associated services. Check if the service that is related to the OfferItem is already present in the
	 * datalists to link it, or it creates a new one and adds it to the flight and the datalists.
	 *
	 * @param associations
	 * 		the associations
	 * @param productsPerPassenger
	 * 		the products per passenger
	 */
	protected void populatedAssociatedServices(final Associations associations,
			final List<AbstractOrderEntryModel> productsPerPassenger)
	{
		final ServiceInfoAssocType serviceInfoAssocType = new ServiceInfoAssocType();

		for (final AbstractOrderEntryModel orderEntry : productsPerPassenger)
		{
			if (!(ProductType.ANCILLARY.equals(orderEntry.getProduct().getProductType())
					|| orderEntry.getProduct() instanceof AncillaryProductModel))
			{
				continue;
			}

			if (!getServicesHashMap().containsKey(orderEntry.getProduct().getCode()))
			{
				final ServiceDetailType serviceDetailType = new ServiceDetailType();

				final ServiceDetailType.Detail detail = new ServiceDetailType.Detail();
				final ServiceCoreType.Name name = new ServiceCoreType.Name();
				final ServiceIDType serviceId = new ServiceIDType();

				serviceDetailType.setObjectKey(orderEntry.getProduct().getCode());

				populateDescription(serviceDetailType, orderEntry.getProduct());

				serviceId.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

				serviceId.setValue(orderEntry.getProduct().getCode());
				serviceDetailType.setServiceID(serviceId);

				name.setValue(orderEntry.getProduct().getName());
				serviceDetailType.setName(name);

				serviceDetailType.setDetail(detail);

				getServicesHashMap().put(serviceDetailType.getObjectKey(), serviceDetailType);
			}

			for (int i = 0; i < orderEntry.getQuantity(); i++)
			{
				serviceInfoAssocType.getServiceReferences().add(getServicesHashMap().get(orderEntry.getProduct().getCode()));
			}
		}

		if (CollectionUtils.isNotEmpty(serviceInfoAssocType.getServiceReferences()))
		{
			associations.setAssociatedService(serviceInfoAssocType);
		}
	}

	/**
	 * Create the service description. If no description is present, since it is a required field, the service name is
	 * used.
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

		if (serviceDetail.getDescription().isEmpty())
		{
			name.setApplication(product.getName());
			serviceDetail.getDescription().add(name);
		}

		serviceDetailType.setDescriptions(serviceDetail);
	}

	/**
	 * Retrieve the passenger associated to the specified OfferItem and put a reference to it.
	 *
	 * @param associations
	 * 		the associations
	 * @param orderViewRS
	 * 		the order view rs
	 * @param traveller
	 * 		the traveller
	 */
	protected void populateAssociatedPassengers(final Associations associations, final OrderViewRS orderViewRS,
			final TravellerModel traveller)
	{
		final Associations.Passengers passengers = new Associations.Passengers();
		final Optional<Passenger> passenger = orderViewRS.getResponse().getPassengers().getPassenger().stream()
				.filter(pass -> pass.getObjectKey().equals(traveller.getLabel())).findFirst();

		if (passenger.isPresent())
		{
			passengers.getPassengerReferences().add(passenger.get());
			associations.setPassengers(passengers);
		}
	}

	/**
	 * Create the OrderItemID based on the orderEntry provided
	 *
	 * @param orderItemID
	 * 		the order item id
	 * @param fareProductEntries
	 * 		the fare product entries
	 */
	protected void populateOrderItemID(final ItemIDType orderItemID, final List<AbstractOrderEntryModel> fareProductEntries)
	{
		final String orderOfferId;
		try
		{
			orderOfferId = getNdcOfferItemIdResolver().generateOrderNDCOfferItemId(fareProductEntries);
		}
		catch (final NDCOrderException e)
		{
			LOG.debug(e);
			throw new ConversionException("Unable to create the OrderItemID");
		}

		orderItemID.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		orderItemID.setValue(orderOfferId);
	}

	/**
	 * Populate the FlightItemType with the information taken from the transport offerings
	 *
	 * @param transportOffering
	 * 		the transport offering
	 * @param flightItemType
	 * 		the flight item type
	 */
	protected void populateFlightType(final TransportOfferingModel transportOffering, final FlightItemType flightItemType)
	{

		final FlightType flightType = new FlightType();
		final Flight flight = new Flight();

		populateFlightSegment(flight, transportOffering);

		final Departure departure = new Departure();
		final FlightDepartureType.AirportCode departureAirportCode = new FlightDepartureType.AirportCode();
		departureAirportCode.setValue(transportOffering.getTravelSector().getOrigin().getCode());
		departure.setAirportCode(departureAirportCode);
		departure.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOffering.getDepartureTime()));
		flight.setDeparture(departure);


		final FlightArrivalType arrival = new FlightArrivalType();
		final FlightArrivalType.AirportCode arrivalAirportCode = new FlightArrivalType.AirportCode();
		arrivalAirportCode.setValue(transportOffering.getTravelSector().getDestination().getCode());
		arrival.setAirportCode(arrivalAirportCode);
		arrival.setDate(NdcFacadesUtils.dateToXMLGregorianCalendar(transportOffering.getArrivalTime()));
		flight.setArrival(arrival);

		populateMarketingCarrier(flight, transportOffering);
		populateOperatingCarrier(flight, transportOffering);

		final Flight.Status status = new Flight.Status();
		final CodesetType statusCode = new CodesetType();
		statusCode.setCode(transportOffering.getStatus().getCode());
		status.setStatusCode(statusCode);
		flight.setStatus(status);

		flightType.getFlight().add(flight);
		flightItemType.getOriginDestination().add(flightType);
	}

	/**
	 * Populates the FlightSegment with the information contained in the transport offering
	 *
	 * @param flight
	 * 		the flight
	 * @param transportOffering
	 * 		the transport offering
	 */
	protected void populateFlightSegment(final Flight flight, final TransportOfferingModel transportOffering)
	{
		if (!getFlightSegmentsHashMap().containsKey(transportOffering.getCode()))
		{
			final ListOfFlightSegmentType flightSegment = new ListOfFlightSegmentType();

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

			if (Objects.nonNull(transportOffering.getTransportVehicle()))
			{
				final AircraftSummaryType aircraftSummary = new AircraftSummaryType();
				final AircraftCode aircraftCode = new AircraftCode();
				aircraftSummary.setName(transportOffering.getTransportVehicle().getTransportVehicleInfo().getName());
				aircraftCode.setValue(getNdcTransportVehicleInfoService()
						.getTransportVehicle(transportOffering.getTransportVehicle().getTransportVehicleInfo().getCode()).getNdcCode());

				aircraftSummary.setAircraftCode(aircraftCode);
				flightSegment.setEquipment(aircraftSummary);
			}

			getFlightSegmentsHashMap().put(transportOffering.getCode(), flightSegment);
		}

		flight.getRefs().add(getFlightSegmentsHashMap().get(transportOffering.getCode()));
	}

	/**
	 * Populate the OperatingCarrier splitting the information contained in the Number of the transport offering
	 *
	 * @param flight
	 * 		the flight
	 * @param transportOffering
	 * 		the transport offering
	 */
	protected void populateOperatingCarrier(final Flight flight, final TransportOfferingModel transportOffering)
	{
		if (Objects.isNull(transportOffering.getNumber()) || Objects.isNull(transportOffering.getTravelProvider()))
		{
			throw new ConversionException("Missing flight number or travel provider");
		}

		final String travelProviderCode = transportOffering.getTravelProvider().getCode();
		final String transportOfferingNumber = transportOffering.getNumber();

		final AirlineID airlineId = new AirlineID();
		airlineId.setValue(travelProviderCode);

		final OperatingCarrier operatingCarrier = new OperatingCarrier();
		operatingCarrier.setAirlineID(airlineId);

		final FlightNumber flightNumber = new FlightNumber();
		flightNumber.setValue(String.valueOf(transportOfferingNumber));
		operatingCarrier.setFlightNumber(flightNumber);

		flight.setOperatingCarrier(operatingCarrier);
	}

	/**
	 * Populate the MarketingCarrier splitting the information contained in the Number of the transport offering
	 *
	 * @param flight
	 * 		the flight
	 * @param transportOffering
	 * 		the transport offering
	 */
	protected void populateMarketingCarrier(final Flight flight, final TransportOfferingModel transportOffering)
	{
		if (Objects.isNull(transportOffering.getNumber()) || Objects.isNull(transportOffering.getTravelProvider()))
		{
			throw new ConversionException("Missing flight number or travel provider");
		}

		final String travelProviderCode = transportOffering.getTravelProvider().getCode();
		final String transportOfferingNumber = transportOffering.getNumber();

		final AirlineID airlineId = new AirlineID();
		airlineId.setValue(travelProviderCode);

		final MarketingCarrierFlightType marketingCarrier = new MarketingCarrierFlightType();
		marketingCarrier.setAirlineID(airlineId);

		final FlightNumber flightNumber = new FlightNumber();
		flightNumber.setValue(String.valueOf(transportOfferingNumber));
		marketingCarrier.setFlightNumber(flightNumber);

		flight.setMarketingCarrier(marketingCarrier);
	}

	/**
	 * Create the OrderViewProcessType, Passengers and Metadata element in the response
	 *
	 * @param orderViewRS
	 * 		the order view rs
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateResponse(final OrderViewRS orderViewRS, final OrderModel orderModel)
	{
		final Response response = new Response();

		populateCommission(response, orderModel);

		final OrderViewProcessType orderViewProcessType = new OrderViewProcessType();
		response.setOrderViewProcessing(orderViewProcessType);

		final Passengers passengers = new Passengers();
		populatePassengers(passengers, orderModel);
		response.setPassengers(passengers);

		final OrdViewMetadataType metadata = new OrdViewMetadataType();
		populateMetadata(metadata, orderModel);
		response.setMetadata(metadata);

		orderViewRS.setResponse(response);
	}

	/**
	 * Filters the Order Entries to find the FEE products and adds them in the commission element
	 *
	 * @param response
	 * 		the response
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateCommission(final Response response, final OrderModel orderModel)
	{
		final List<AbstractOrderEntryModel> fees = orderModel.getEntries().stream()
				.filter(
						entry -> ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(fees))
		{
			return;
		}

		final double sum = fees.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum();
		final String codes = fees.stream().map(entry -> entry.getProduct().getCode()).collect(Collectors.joining(", "));

		final CommissionType commission = new CommissionType();
		final CurrencyAmountOptType amount = new CurrencyAmountOptType();
		amount.setValue(BigDecimal.valueOf(sum).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		commission.setAmount(amount);
		commission.setCode(codes);
		response.setCommission(commission);
	}

	/**
	 * Populate the Metadata with the currency information
	 *
	 * @param metadata
	 * 		the metadata
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateMetadata(final OrdViewMetadataType metadata, final OrderModel orderModel)
	{
		final OrdViewMetadataType.Other other = new OrdViewMetadataType.Other();
		final OrdViewMetadataType.Other.OtherMetadata otherMetadata = new OrdViewMetadataType.Other.OtherMetadata();
		final CurrencyMetadatas currencyMetadatas = new CurrencyMetadatas();
		final CurrencyMetadata currencyMetadata = new CurrencyMetadata();

		currencyMetadata.setMetadataKey(orderModel.getCurrency().getIsocode());
		currencyMetadata.setDecimals(BigInteger.valueOf(NdcfacadesConstants.PRECISION));
		currencyMetadatas.getCurrencyMetadata().add(currencyMetadata);
		otherMetadata.setCurrencyMetadatas(currencyMetadatas);
		other.getOtherMetadata().add(otherMetadata);

		metadata.setOther(other);
	}

	/**
	 * Create the passenger based on the travellers contained in the TravelOrderEntryInfo of the order entries
	 *
	 * @param passengers
	 * 		the passengers
	 * @param orderModel
	 * 		the order model
	 */
	protected void populatePassengers(final Passengers passengers, final OrderModel orderModel)
	{
		final Set<TravellerModel> travellers = orderModel.getEntries().stream().filter(AbstractOrderEntryModel::getActive)
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(Collectors.toSet());

		for (final TravellerModel traveller : travellers)
		{
			final Passenger passenger = new Passenger();
			populatePassenger(passenger, traveller);
			passengers.getPassenger().add(passenger);
		}
	}

	/**
	 * Populate the information on the passenger base on the information contained in the TravellerModel
	 *
	 * @param passenger
	 * 		the passenger
	 * @param traveller
	 * 		the traveller
	 */
	protected void populatePassenger(final Passenger passenger, final TravellerModel traveller)
	{
		final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();

		passenger.setObjectKey(traveller.getLabel());

		final PTC ptc = new PTC();
		ptc.setQuantity(BigInteger.valueOf(PASSENGER_QUANTITY));
		ptc.setValue(passengerInfo.getPassengerType().getNdcCode());
		passenger.setPTC(ptc);

		final Gender gender = new Gender();
		populateGender(gender, passengerInfo.getGender());
		passenger.setGender(gender);

		final Name name = new Name();
		final Surname surname = new Surname();
		surname.setValue(passengerInfo.getSurname());
		name.setSurname(surname);

		final Given given = new Given();
		given.setValue(passengerInfo.getFirstName());
		name.getGiven().add(given);

		name.setTitle(passengerInfo.getTitle().getCode());

		passenger.setName(name);

		final ProfileID profileId = new ProfileID();
		profileId.setValue(traveller.getUid());
		passenger.setProfileID(profileId);

		final Contacts contacts = new Contacts();
		final Contact contact = new Contact();
		final EmailType emailContact = new EmailType();
		final Address emailAddress = new Address();
		emailAddress.setValue(passengerInfo.getEmail());
		emailContact.setAddress(emailAddress);
		contact.setEmailContact(emailContact);
		contacts.getContact().add(contact);

		passenger.setContacts(contacts);
	}

	/**
	 * Map the {@link Gender} to NDC gender values {@link TravelerGenderSimpleType}
	 *
	 * @param gender
	 * 		the gender
	 * @param genderString
	 * 		the gender string
	 */
	protected void populateGender(final Gender gender, final String genderString)
	{
		switch (genderString)
		{
			case "male":
				gender.setValue(TravelerGenderSimpleType.MALE);
				break;
			case "female":
				gender.setValue(TravelerGenderSimpleType.FEMALE);
				break;
			default:
				gender.setValue(TravelerGenderSimpleType.UNKNOWN);
				break;
		}
	}

	/**
	 * Populate the {@link TotalOrderPrice} element with the total price and total tax values in the {@link OrderModel}
	 *
	 * @param order
	 * 		the order
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateTotalOrderPrice(final Order order, final OrderModel orderModel)
	{
		final TotalOrderPrice totalOrderPrice = new TotalOrderPrice();
		final DetailCurrencyPriceType detailCurrencyPrice = new DetailCurrencyPriceType();
		final CurrencyAmountOptType total = new CurrencyAmountOptType();
		final Taxes taxes = new Taxes();
		final Total taxTotal = new Total();

		total.setCode(orderModel.getCurrency().getIsocode());
		total.setValue(BigDecimal.valueOf(orderModel.getTotalPrice() + orderModel.getTotalTax())
				.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		detailCurrencyPrice.setTotal(total);

		taxTotal.setValue(
				BigDecimal.valueOf(orderModel.getTotalTax()).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxes.setTotal(taxTotal);
		detailCurrencyPrice.setTaxes(taxes);

		totalOrderPrice.setDetailCurrencyPrice(detailCurrencyPrice);

		order.setTotalOrderPrice(totalOrderPrice);
	}

	/**
	 * Create the {@link BookingReferences} placing the booking reference number in the {@link OtherID}. The ID cannot be
	 * used since our ID is an 8 digits number that does not respect the ID pattern. The ID is nevertheless populated
	 * since it is a required filed in the xml structure
	 *
	 * @param order
	 * 		the order
	 * @param orderModel
	 * 		the order model
	 */
	protected void populateBookingReference(final Order order, final OrderModel orderModel)
	{
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReference = new BookingReferenceType();
		final OtherID otherID = new OtherID();

		bookingReference.setID(BOOKING_REFERENCE_ID);

		if (Objects.nonNull(orderModel.getOriginalOrder()) && Objects.nonNull(orderModel.getOriginalOrder().getCode()))
		{
			otherID.setValue(orderModel.getOriginalOrder().getCode());
		}
		else
		{
			otherID.setValue(orderModel.getCode());
		}

		bookingReference.setOtherID(otherID);
		bookingReferences.getBookingReference().add(bookingReference);

		order.setBookingReferences(bookingReferences);
	}

	/**
	 * Populate the OrderID with the Booking reference
	 *
	 * @param order
	 * 		the order
	 */
	protected void populateOrderID(final Order order)
	{
		final OrderIDType orderID = new OrderIDType();

		orderID.setValue(BOOKING_REFERENCE_ID);
		orderID.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

		order.setOrderID(orderID);
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
	 * Gets services hash map.
	 *
	 * @return the services hash map
	 */
	protected HashMap<String, ServiceDetailType> getServicesHashMap()
	{
		return servicesHashMap;
	}

	/**
	 * Gets seats.
	 *
	 * @return the seats
	 */
	protected HashMap<String, ListOfSeatType> getSeats()
	{
		return seats;
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

	/**
	 * Gets ndc transport vehicle info service.
	 *
	 * @return the ndc transport vehicle info service
	 */
	protected NDCTransportVehicleInfoService getNdcTransportVehicleInfoService()
	{
		return ndcTransportVehicleInfoService;
	}

	/**
	 * Sets ndc transport vehicle info service.
	 *
	 * @param ndcTransportVehicleInfoService
	 * 		the ndc transport vehicle info service
	 */
	@Required
	public void setNdcTransportVehicleInfoService(
			final NDCTransportVehicleInfoService ndcTransportVehicleInfoService)
	{
		this.ndcTransportVehicleInfoService = ndcTransportVehicleInfoService;
	}
}
