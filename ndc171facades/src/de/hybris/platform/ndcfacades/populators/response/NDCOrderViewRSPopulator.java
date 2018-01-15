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

import de.hybris.platform.category.model.CategoryModel;
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
import de.hybris.platform.ndcfacades.ndc.CommissionType;
import de.hybris.platform.ndcfacades.ndc.ContactInformationType;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadata;
import de.hybris.platform.ndcfacades.ndc.CurrencyMetadatas;
import de.hybris.platform.ndcfacades.ndc.Departure;
import de.hybris.platform.ndcfacades.ndc.DescriptionType.Text;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.DetailCurrencyPriceType.Taxes;
import de.hybris.platform.ndcfacades.ndc.EmailAddressType;
import de.hybris.platform.ndcfacades.ndc.FlightArrivalType;
import de.hybris.platform.ndcfacades.ndc.FlightDepartureType;
import de.hybris.platform.ndcfacades.ndc.FlightDetailType;
import de.hybris.platform.ndcfacades.ndc.FlightDurationType;
import de.hybris.platform.ndcfacades.ndc.FlightNumber;
import de.hybris.platform.ndcfacades.ndc.GenderCodeContentType;
import de.hybris.platform.ndcfacades.ndc.IndividualType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.MarketingCarrierFlightType;
import de.hybris.platform.ndcfacades.ndc.OrdViewMetadataType;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2.OrderItem;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2.OrderItem.PriceDetail;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2.OrderItem.PriceDetail.TotalAmount;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2.OrderItem.Service;
import de.hybris.platform.ndcfacades.ndc.OrderItemType2.OrderItem.Service.ServiceDefinitionRef;
import de.hybris.platform.ndcfacades.ndc.OrderType2.TotalOrderPrice;
import de.hybris.platform.ndcfacades.ndc.OrderViewProcessType;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.ContactList;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.FlightSegmentList;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.SeatList;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.SeatList.Seats;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.DataLists.ServiceDefinitionList;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.Order;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS.Response.Passengers;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType.Row;
import de.hybris.platform.ndcfacades.ndc.SeatMapRowNbrType;
import de.hybris.platform.ndcfacades.ndc.ServiceDefinitionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDefinitionType.Name;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType.Description;
import de.hybris.platform.ndcfacades.ndc.SimpleCurrencyPriceType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType;
import de.hybris.platform.ndcfacades.ndc.TaxDetailType.Total;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCTransportVehicleInfoService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ConfiguredAccommodationType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * NDC OrderModel OrderViewRS Populator
 */
public class NDCOrderViewRSPopulator implements Populator<OrderModel, OrderViewRS>
{
	private static final Logger LOG = Logger.getLogger(NDCOrderViewRSPopulator.class);

	private static final String CONTACT_ID_SUFFIX = "_CONT";
	private static final String MISSING_FLIGHT_NUMBER = "Missing flight number or travel provider";

	private final Map<String, ListOfFlightSegmentType> flightSegmentsMap = new HashMap<>();
	private final Map<String, ServiceDefinitionType> serviceDefinitionMap = new HashMap<>();
	private final Map<String, Seats> seatsMap = new HashMap<>();
	private final Map<String, PassengerType> passengerTypeMap = new HashMap<>();
	private final Map<String, ContactInformationType> contactInformationMap = new HashMap<>();

	private ConfigurationService configurationService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private NDCTransportVehicleInfoService ndcTransportVehicleInfoService;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	@Override
	public void populate(final OrderModel orderModel, final OrderViewRS orderViewRS) throws ConversionException
	{
		populateResponse(orderViewRS, orderModel);
	}

	/**
	 * Populate response.
	 *
	 * @param orderViewRS
	 *           the order view RS
	 * @param orderModel
	 *           the order model
	 */
	protected void populateResponse(final OrderViewRS orderViewRS, final OrderModel orderModel)
	{
		final Response response = new Response();

		final Passengers passengers = new Passengers();
		response.setPassengers(passengers);

		final OrderViewProcessType orderViewProcessType = new OrderViewProcessType();
		response.setOrderViewProcessing(orderViewProcessType);

		response.setCommission(getCommissionType(orderModel));
		response.setMetadata(getMetadata(orderModel));

		final Order order = new Order();
		populateOrderID(order, orderModel);
		populateBookingReference(order, orderModel);
		populateTotalOrderPrice(order, orderModel);
		populateOrderItems(order, orderModel);
		response.getOrder().add(order);

		final DataLists dataLists = new DataLists();
		populateDatalists(dataLists);
		response.setDataLists(dataLists);

		orderViewRS.setResponse(response);
	}

	/**
	 * Filters the Order Entries to find the FEE products and adds them in the commission element.
	 *
	 * @param orderModel
	 *           the order model
	 * @return the commission type
	 */
	protected CommissionType getCommissionType(final OrderModel orderModel)
	{
		final List<AbstractOrderEntryModel> fees = orderModel.getEntries().stream()
				.filter(
						entry -> ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(fees))
		{
			return null;
		}

		final double sum = fees.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum();
		final String codes = fees.stream().map(entry -> entry.getProduct().getCode()).collect(Collectors.joining(", "));

		final CommissionType commission = new CommissionType();
		final CurrencyAmountOptType amount = new CurrencyAmountOptType();
		amount.setValue(BigDecimal.valueOf(sum).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		commission.setAmount(amount);
		commission.setCode(codes);
		return commission;
	}

	/**
	 * Creates and returns the Metadata with the currency information.
	 *
	 * @param orderModel
	 *           the order model
	 * @return the metadata
	 */
	protected OrdViewMetadataType getMetadata(final OrderModel orderModel)
	{
		final OrdViewMetadataType metadata = new OrdViewMetadataType();
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
		return metadata;
	}

	/**
	 * Populate the order with OrderID and Owner attributes.
	 *
	 * @param order
	 *           the order
	 * @param orderModel
	 *           the order model
	 */
	protected void populateOrderID(final Order order, final OrderModel orderModel)
	{
		final String orderCode = (Objects.nonNull(orderModel.getOriginalOrder())
				&& Objects.nonNull(orderModel.getOriginalOrder().getCode())) ? orderModel.getOriginalOrder().getCode()
						: orderModel.getCode();

		order.setOrderID(orderCode);
		order.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
	}

	/**
	 * Create the {@link BookingReferences} placing the booking reference number in the {@link OtherID}. The ID cannot be
	 * used since our ID is an 8 digits number that does not respect the ID pattern. The ID is nevertheless populated
	 * since it is a required filed in the xml structure
	 *
	 * @param order
	 *           the order
	 * @param orderModel
	 */
	protected void populateBookingReference(final Order order, final OrderModel orderModel)
	{
		final BookingReferences bookingReferences = new BookingReferences();
		final BookingReferenceType bookingReference = new BookingReferenceType();
		final OtherID otherID = new OtherID();

		if (Objects.nonNull(orderModel.getOriginalOrder()) && Objects.nonNull(orderModel.getOriginalOrder().getCode()))
		{
			otherID.setValue(orderModel.getOriginalOrder().getCode());
		}
		else
		{
			otherID.setValue(orderModel.getCode());
		}

		bookingReference.setID(otherID.getValue());
		bookingReference.setOtherID(otherID);
		bookingReferences.getBookingReference().add(bookingReference);

		order.setBookingReferences(bookingReferences);
	}

	/**
	 * Populate the {@link TotalOrderPrice} element with the total price and total tax values in the {@link OrderModel}
	 *
	 * @param order
	 *           the order
	 * @param orderModel
	 *           the order model
	 */
	protected void populateTotalOrderPrice(final Order order, final OrderModel orderModel)
	{
		final TotalOrderPrice totalOrderPrice = new TotalOrderPrice();
		final DetailCurrencyPriceType detailCurrencyPrice = new DetailCurrencyPriceType();
		final CurrencyAmountOptType total = new CurrencyAmountOptType();
		final Taxes taxes = new Taxes();
		final Total taxTotal = new Total();

		final String currCode = orderModel.getCurrency().getIsocode();
		total.setCode(currCode);
		total.setValue(BigDecimal.valueOf(orderModel.getTotalPrice() + orderModel.getTotalTax())
				.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		detailCurrencyPrice.setTotal(total);

		taxTotal.setCode(currCode);
		taxTotal.setValue(
				BigDecimal.valueOf(orderModel.getTotalTax()).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxes.setTotal(taxTotal);
		detailCurrencyPrice.setTaxes(taxes);

		totalOrderPrice.setDetailCurrencyPrice(detailCurrencyPrice);

		order.setTotalOrderPrice(totalOrderPrice);
	}

	/**
	 * Populate order items.
	 *
	 * @param order
	 *           the order
	 * @param orderModel
	 *           the order model
	 */
	protected void populateOrderItems(final Order order, final OrderModel orderModel)
	{
		populatePassengerTypes(orderModel);
		final OrderItemType2 orderItems = createOrderItems(orderModel);
		order.setOrderItems(orderItems);
	}

	/**
	 * Populate passenger types.
	 *
	 * @param orderModel
	 *           the order model
	 */
	protected void populatePassengerTypes(final OrderModel orderModel)
	{
		final Set<TravellerModel> travellers = orderModel.getEntries().stream().filter(AbstractOrderEntryModel::getActive)
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(Collectors.toSet());

		for (final TravellerModel traveller : travellers)
		{
			getPassengerTypeMap().putIfAbsent(traveller.getLabel(), getPassengerType(traveller));
		}
	}

	/**
	 * Creates and returns the passenger {@link PassengerType} with populated information based on the TravellerModel.
	 *
	 * @param traveller
	 *           the traveller
	 * @return the passenger type
	 */
	protected PassengerType getPassengerType(final TravellerModel traveller)
	{
		final PassengerType passengerType = new PassengerType();
		final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();

		final String passengerTypeId = traveller.getLabel();
		passengerType.setPassengerID(passengerTypeId);
		passengerType.setPTC(passengerInfo.getPassengerType().getNdcCode());

		final IndividualType individual = new IndividualType();
		individual.setGender(getPassengerGender(passengerInfo.getGender()));
		individual.setSurname(passengerInfo.getSurname());
		individual.getGivenName().add(passengerInfo.getFirstName());
		individual.setNameTitle(passengerInfo.getTitle().getCode());

		passengerType.setIndividual(individual);
		passengerType.setProfileID(traveller.getUid());

		final String emailAddress = passengerInfo.getEmail();
		if (StringUtils.isEmpty(emailAddress))
		{
			return passengerType;
		}

		final String contactId = passengerTypeId + CONTACT_ID_SUFFIX;
		ContactInformationType contactInformationType = getContactInformationMap().get(contactId);
		if (Objects.isNull(contactInformationType))
		{
			contactInformationType = getContactInformationType(contactId, emailAddress);
		}
		passengerType.setContactInfoRef(contactInformationType);
		return passengerType;
	}

	/**
	 * Map the gender string to NDC gender values {@link GenderCodeContentType}
	 *
	 * @param gender
	 *           the gender
	 * @param genderString
	 *           the gender string
	 * @return
	 */
	protected GenderCodeContentType getPassengerGender(final String genderString)
	{
		switch (genderString)
		{
			case "male":
				return GenderCodeContentType.MALE;
			case "female":
				return GenderCodeContentType.FEMALE;
			default:
				return GenderCodeContentType.UNSPECIFIED;
		}
	}

	/**
	 * Gets the contact information type.
	 *
	 * @param contactId
	 *           the contact id
	 * @param emailAddress
	 *           the email address
	 * @return the contact information type
	 */
	protected ContactInformationType getContactInformationType(final String contactId, final String emailAddress)
	{
		final ContactInformationType contactInformationType=new ContactInformationType();
		contactInformationType.setContactID(contactId);
		final EmailAddressType emailAddressType = new EmailAddressType();
		emailAddressType.setEmailAddressValue(emailAddress);
		contactInformationType.getEmailAddress().add(emailAddressType);
		getContactInformationMap().put(contactId, contactInformationType);
		return contactInformationType;
	}

	/**
	 * Creates the order items.
	 *
	 * @param orderModel
	 *           the order model
	 * @return the order item type 2
	 */
	protected OrderItemType2 createOrderItems(final OrderModel orderModel)
	{
		final OrderItemType2 orderItems = new OrderItemType2();

		final List<AbstractOrderEntryModel> filteredOrderEntries = orderModel.getEntries().stream()
				.filter(AbstractOrderEntryModel::getActive)
				.filter(entry -> CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers())
						&& CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTransportOfferings()))
				.collect(Collectors.toList());

		final List<AbstractOrderEntryModel> fareProductOrderEntries = filteredOrderEntries.stream()
				.filter(orderEntry -> Objects.nonNull(orderEntry.getProduct())
						&& (orderEntry.getProduct() instanceof FareProductModel || Objects.equals(ProductType.FARE_PRODUCT,
								orderEntry.getProduct().getProductType())))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(fareProductOrderEntries))
		{
			throw new ConversionException("No Fare Product exists for the order");
		}

		final Map<String, Set<AbstractOrderEntryModel>> orderItemIdFareProductEntriesMap = new HashMap<>();
		fareProductOrderEntries.forEach(fareProductOrderEntry -> {
			try
			{
				final String orderItemId = getNdcOfferItemIdResolver()
						.generateOrderNDCOfferItemId(Collections.singletonList(fareProductOrderEntry));
				final Set<AbstractOrderEntryModel> orderItemFareProductEntries = orderItemIdFareProductEntriesMap
						.getOrDefault(orderItemId, new HashSet<>());
				orderItemFareProductEntries.add(fareProductOrderEntry);
				orderItemIdFareProductEntriesMap.putIfAbsent(orderItemId, orderItemFareProductEntries);
			}
			catch (final NDCOrderException e)
			{
				LOG.debug(e);
				throw new ConversionException("Unable to create the OrderItemID");
			}
		});

		final List<AbstractOrderEntryModel> nonFareProductOrderEntries = filteredOrderEntries.stream()
				.filter(orderEntry -> Objects.nonNull(orderEntry.getProduct())
						&& (orderEntry.getProduct() instanceof AncillaryProductModel
								|| Objects.equals(ProductType.ANCILLARY, orderEntry.getProduct().getProductType())
								|| orderEntry.getProduct() instanceof AccommodationModel
								|| Objects.equals(ProductType.ACCOMMODATION,
										orderEntry.getProduct().getProductType())))
				.collect(Collectors.toList());

		final Map<String, Set<AbstractOrderEntryModel>> transportOfferingNonFareProductEntriesMap = new HashMap<>();
		nonFareProductOrderEntries.forEach(nonFareProductOrderEntry -> {
			nonFareProductOrderEntry.getTravelOrderEntryInfo().getTransportOfferings().forEach(transportOffering -> {
				final Set<AbstractOrderEntryModel> transportOfferingNonFareProductEntries = transportOfferingNonFareProductEntriesMap
						.getOrDefault(transportOffering.getCode(), new HashSet<>());
				transportOfferingNonFareProductEntries.add(nonFareProductOrderEntry);
				transportOfferingNonFareProductEntriesMap.putIfAbsent(transportOffering.getCode(), transportOfferingNonFareProductEntries);
			});
		});

		populateOrderItem(orderItems, orderItemIdFareProductEntriesMap, transportOfferingNonFareProductEntriesMap, orderModel);

		return orderItems;
	}

	/**
	 * Populate order item.
	 *
	 * @param orderItems
	 *           the order items
	 * @param orderItemIdFareProductEntriesMap
	 *           the fare product order entries
	 * @param transportOfferingNonFareProductEntriesMap
	 *           the transport offering non fare product entries map
	 * @param orderModel
	 *           the order model
	 */
	protected void populateOrderItem(final OrderItemType2 orderItems,
			final Map<String, Set<AbstractOrderEntryModel>> orderItemIdFareProductEntriesMap,
			final Map<String, Set<AbstractOrderEntryModel>> transportOfferingNonFareProductEntriesMap, final OrderModel orderModel)
	{
		orderItemIdFareProductEntriesMap.forEach((orderItemId, fareProductEntries) -> {

			final OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemID(orderItemId);
			orderItem.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

			final Set<AbstractOrderEntryModel> orderEntriesPerOrderItem = new HashSet<>();
			for (final AbstractOrderEntryModel fareProductOrderEntry : fareProductEntries)
			{
				orderEntriesPerOrderItem.addAll(getEntriesAndPopulatedOrderItemPerFareProduct(orderItem, fareProductOrderEntry,
						transportOfferingNonFareProductEntriesMap, orderModel));
			}
			populateOfferItemPriceDetails(orderItem, orderEntriesPerOrderItem, orderModel.getCurrency().getIsocode());
			orderItems.getOrderItem().add(orderItem);
		});
	}

	/**
	 * Gets the entries and populated order item per fare product.
	 *
	 * @param orderItem
	 *           the order item
	 * @param fareProductOrderEntry
	 *           the fare product order entry
	 * @param transportOfferingNonFareProductEntriesMap
	 *           the transport offering non fare product entries map
	 * @param orderModel
	 *           the order model
	 * @return the entries and populated order item per fare product
	 */
	protected Set<AbstractOrderEntryModel> getEntriesAndPopulatedOrderItemPerFareProduct(final OrderItem orderItem,
			final AbstractOrderEntryModel fareProductOrderEntry,
			final Map<String, Set<AbstractOrderEntryModel>> transportOfferingNonFareProductEntriesMap, final OrderModel orderModel)
	{
			final Set<AbstractOrderEntryModel> orderEntriesPerFP = new HashSet<>();
			orderEntriesPerFP.add(fareProductOrderEntry);
			final Collection<TransportOfferingModel> transportOfferings = fareProductOrderEntry.getTravelOrderEntryInfo()
					.getTransportOfferings();
			final TravellerModel traveller = fareProductOrderEntry.getTravelOrderEntryInfo().getTravellers().stream().findFirst()
					.get();

			for (final TransportOfferingModel transportOffering : transportOfferings)
			{
				orderEntriesPerFP.addAll(getEntriesAndPopulateOrderItemPerTransportOffering(orderItem, transportOffering, traveller,
						transportOfferingNonFareProductEntriesMap, orderEntriesPerFP, orderModel));
			}
		return orderEntriesPerFP;
	}

	/**
	 * Gets the entries and populate order item per transport offering.
	 *
	 * @param orderItem
	 *           the order item
	 * @param transportOffering
	 *           the transport offering
	 * @param traveller
	 *           the traveller
	 * @param transportOfferingNonFareProductEntriesMap
	 *           the transport offering non fare product entries map
	 * @param orderEntriesPerFP
	 *           the order entries per FP
	 * @param orderModel
	 *           the order model
	 * @return the entries and populate order item per transport offering
	 */
	protected Set<AbstractOrderEntryModel> getEntriesAndPopulateOrderItemPerTransportOffering(
			final OrderItem orderItem, final TransportOfferingModel transportOffering, final TravellerModel traveller,
			final Map<String, Set<AbstractOrderEntryModel>> transportOfferingNonFareProductEntriesMap,
			final Set<AbstractOrderEntryModel> orderEntriesPerFP, final OrderModel orderModel)
	{
		final String transportOfferingCode = transportOffering.getCode();
		final Set<AbstractOrderEntryModel> transportOfferingNonFareProductEntries = transportOfferingNonFareProductEntriesMap
				.get(transportOfferingCode);
		populateFlightSegment(transportOffering);
		populateServiceWithSegmentRef(orderItem, traveller, transportOfferingCode);

		if (CollectionUtils.isEmpty(transportOfferingNonFareProductEntries))
		{
			return Collections.emptySet();
		}

		final SelectedAccommodationModel selectedAccommodation = getSelectedAccomForTraveller(
				orderModel.getSelectedAccommodations(), traveller, transportOfferingCode);
		final Set<AbstractOrderEntryModel> filteredTransportOfferingNonFareProductEntries = transportOfferingNonFareProductEntries
				.stream()
				.filter(nonFareProductEntry -> !orderEntriesPerFP.contains(nonFareProductEntry)
						&& nonFareProductEntry.getTravelOrderEntryInfo().getTravellers().contains(traveller))
				.collect(Collectors.toSet());

		final Map<ProductType, List<AbstractOrderEntryModel>> nonFareProductTypeEntries = getNonFareProductTypeEntries(
				filteredTransportOfferingNonFareProductEntries);

		if (MapUtils.isNotEmpty(nonFareProductTypeEntries))
		{
			populateAncillaryServices(orderItem, nonFareProductTypeEntries.get(ProductType.ANCILLARY), traveller,
					transportOfferingCode);
			populateAccommodationServices(orderItem, nonFareProductTypeEntries.get(ProductType.ACCOMMODATION), traveller,
					transportOfferingCode, selectedAccommodation);
		}
		else
		{
			final Map<Object, List<AbstractOrderEntryModel>> nonFareProductTypeOrderEntriesMap = getNonFareProductTypeEntryMap(
					filteredTransportOfferingNonFareProductEntries);

			populateAncillaryServices(orderItem, nonFareProductTypeOrderEntriesMap.get(AncillaryProductModel.class), traveller,
					transportOfferingCode);
			populateAccommodationServices(orderItem, nonFareProductTypeOrderEntriesMap.get(AccommodationModel.class), traveller,
					transportOfferingCode, selectedAccommodation);
		}

		return filteredTransportOfferingNonFareProductEntries;
	}

	/**
	 * Method does the grouping of of all the order entries except {@link ProductType.FareProduct} and returns
	 * {@link Map}
	 *
	 * @param filteredTransportOfferingNonFareProductEntries
	 * @deprecated Deprecated since version 4.0 use {@link getNonFareProductTypeEntryMap}
	 */
	@Deprecated
	protected Map<ProductType, List<AbstractOrderEntryModel>> getNonFareProductTypeEntries(
			final Set<AbstractOrderEntryModel> filteredTransportOfferingNonFareProductEntries)
	{
		return filteredTransportOfferingNonFareProductEntries
				.stream().collect(Collectors.groupingBy(orderEntry -> orderEntry.getProduct().getProductType()));
	}

	/**
	 * Method does the grouping of of all the order entries except {@link ProductType.FareProduct} and returns
	 * {@link Map}
	 *
	 * @param filteredTransportOfferingNonFareProductEntries
	 */
	protected Map<Object, List<AbstractOrderEntryModel>> getNonFareProductTypeEntryMap(
			final Set<AbstractOrderEntryModel> filteredTransportOfferingNonFareProductEntries)
	{
		return filteredTransportOfferingNonFareProductEntries
				.stream().collect(Collectors.groupingBy(orderEntry -> orderEntry.getProduct().getClass()));
	}

	/**
	 * Populate service with segment ref.
	 *
	 * @param orderItem
	 *           the order item
	 * @param traveller
	 *           the traveller
	 * @param transportOfferingCode
	 *           the transport offering code
	 */
	protected void populateServiceWithSegmentRef(final OrderItem orderItem, final TravellerModel traveller,
			final String transportOfferingCode)
	{
		final Service service = new Service();
		final PassengerType passengerType = getPassengerTypeMap().get(traveller.getLabel());
		service.setPassengerRef(passengerType);
		service.setSegmentRef(getFlightSegmentsMap().get(transportOfferingCode));

		orderItem.getService().add(service);

	}

	/**
	 * Returns an instance of {@link SelectedAccommodationModel} from list for given {@link TravellerModel} and
	 * transportOffering code
	 *
	 * @param selectedAccommodations
	 *           the selected accommodations
	 * @param travellerModel
	 *           the traveller model
	 * @param transportOfferingCode
	 *           the transport offering code
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
	 * Populates the FlightSegment with the information contained in the transport offering
	 *
	 * @param transportOffering
	 *           the transport offering
	 */
	protected void populateFlightSegment(final TransportOfferingModel transportOffering)
	{
		if (getFlightSegmentsMap().containsKey(transportOffering.getCode()))
		{
			return;
		}

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
			throw new ConversionException(MISSING_FLIGHT_NUMBER);
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

		getFlightSegmentsMap().put(transportOffering.getCode(), flightSegment);
	}

	/**
	 * Populate offer item price details.
	 *
	 * @param orderItem
	 *           the offer item
	 * @param orderEntries
	 *           the order entries
	 * @param currencyCode
	 *           the currency code
	 */
	protected void populateOfferItemPriceDetails(final OrderItem orderItem, final Set<AbstractOrderEntryModel> orderEntries,
			final String currencyCode)
	{
		final PriceDetail totalPriceDetail = new PriceDetail();
		final TotalAmount totalAmount = new TotalAmount();
		final SimpleCurrencyPriceType simpleCurrencyPriceType = new SimpleCurrencyPriceType();
		final CurrencyAmountOptType currencyAmountOptType = new CurrencyAmountOptType();
		final TaxDetailType taxDetailType = new TaxDetailType();
		final Total total = new Total();

		double basePrice = 0d;
		double totalPrice = 0d;
		double totalTaxes = 0d;
		for (final AbstractOrderEntryModel entry : orderEntries)
		{
			totalPrice = totalPrice + entry.getTotalPrice();
			if (entry.getProduct() instanceof FareProductModel
					|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
			{
				basePrice = basePrice + entry.getBasePrice();
			}
			totalTaxes = totalTaxes + entry.getTaxValues().stream().mapToDouble(TaxValue::getValue).sum();
		}

		currencyAmountOptType
				.setValue(BigDecimal.valueOf(basePrice).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		currencyAmountOptType.setCode(currencyCode);
		totalPriceDetail.setBaseAmount(currencyAmountOptType);

		total.setValue(BigDecimal.valueOf(totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		taxDetailType.setTotal(total);
		total.setCode(currencyCode);
		totalPriceDetail.setTaxes(taxDetailType);

		simpleCurrencyPriceType.setValue(
				BigDecimal.valueOf(totalPrice + totalTaxes).setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		simpleCurrencyPriceType.setCode(currencyCode);
		totalAmount.setSimpleCurrencyPrice(simpleCurrencyPriceType);
		totalPriceDetail.setTotalAmount(totalAmount);

		orderItem.setPriceDetail(totalPriceDetail);
	}

	/**
	 * Populate ancillary services.
	 *
	 * @param orderItem
	 *
	 * @param ancillaryProductOrderEntries
	 *           the ancillary product order entries
	 * @param traveller
	 *           the traveller
	 * @param transportOfferingCode
	 *           the transport offering code
	 */
	protected void populateAncillaryServices(final OrderItem orderItem,
			final List<AbstractOrderEntryModel> ancillaryProductOrderEntries,
			final TravellerModel traveller, final String transportOfferingCode)
	{
		if (CollectionUtils.isEmpty(ancillaryProductOrderEntries))
		{
			return;
		}

		for(final AbstractOrderEntryModel ancillaryProductOrderEntry:ancillaryProductOrderEntries)
		{
			final ProductModel product = ancillaryProductOrderEntry.getProduct();
			final String mapping=getProductCategoryMapping(product);

			LongStream.range(0, ancillaryProductOrderEntry.getQuantity()).forEach(index -> {

				final Service service = new Service();
				final PassengerType passengerType = getPassengerTypeMap().get(traveller.getLabel());
				service.setPassengerRef(passengerType);

				final ServiceDefinitionRef serviceDefinitionRef = new ServiceDefinitionRef();
				populateSegmentRefs(serviceDefinitionRef, transportOfferingCode, mapping);

				ServiceDefinitionType serviceDefinitionType = getServiceDefinitionMap().get(product.getCode());
				if (Objects.isNull(serviceDefinitionType))
				{
					serviceDefinitionType = getServiceDefinitionType(product);
					getServiceDefinitionMap().put(product.getCode(), serviceDefinitionType);
				}
				serviceDefinitionRef.setValue(serviceDefinitionType);

				service.setServiceDefinitionRef(serviceDefinitionRef);
				orderItem.getService().add(service);
			});
		}
	}

	/**
	 * Gets the product category mapping.
	 *
	 * @param product
	 *           the product
	 * @return the product category mapping
	 */
	protected String getProductCategoryMapping(final ProductModel product)
	{
		String categoryCode = StringUtils.EMPTY;
		if (CollectionUtils.isNotEmpty(product.getSupercategories()))
		{
			final Optional<CategoryModel> category = product.getSupercategories().stream().findFirst();
			categoryCode = category.isPresent() ? category.get().getCode() : StringUtils.EMPTY;
		}

		return getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode, getOfferGroupToOriginDestinationMapping()
				.getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING, TravelservicesConstants.TRAVEL_ROUTE));
	}

	/**
	 * Populate accommodation services.
	 *
	 * @param orderItem
	 *           the order item
	 * @param accommodationProductOrderEntries
	 *           the accommodation product order entries
	 * @param traveller
	 *           the traveller
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param selectedAccommodation
	 *           the selected accommodation
	 */
	protected void populateAccommodationServices(final OrderItem orderItem,
			final List<AbstractOrderEntryModel> accommodationProductOrderEntries, final TravellerModel traveller,
			final String transportOfferingCode, final SelectedAccommodationModel selectedAccommodation)
	{
		if (CollectionUtils.isEmpty(accommodationProductOrderEntries))
		{
			return;
		}

		accommodationProductOrderEntries.forEach(accommodationProductOrderEntry -> {
			LongStream.range(0, accommodationProductOrderEntry.getQuantity()).forEach(index -> {
				final Service service = new Service();
				final PassengerType passengerType = getPassengerTypeMap().get(traveller.getLabel());
				service.setPassengerRef(passengerType);
				final ProductModel product = accommodationProductOrderEntry.getProduct();

				final ServiceDefinitionRef serviceDefinitionRef = new ServiceDefinitionRef();
				populateSegmentRefs(serviceDefinitionRef, transportOfferingCode, TravelservicesConstants.TRANSPORT_OFFERING);
				Seats seats = getSeatsMap().get(product.getCode());
				if (Objects.isNull(seats))
				{
					seats = getSeats(selectedAccommodation);
				}
				serviceDefinitionRef.setValue(seats);
				service.setServiceDefinitionRef(serviceDefinitionRef);
				orderItem.getService().add(service);
			});
		});

	}

	/**
	 * Populate segment refs.
	 *
	 * @param serviceDefinitionRef
	 *           the service definition ref
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param mapping
	 *           the mapping
	 */
	protected void populateSegmentRefs(final ServiceDefinitionRef serviceDefinitionRef, final String transportOfferingCode,
			final String mapping)
	{
		if (getFlightSegmentsMap().containsKey(transportOfferingCode)
				&& StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRANSPORT_OFFERING))
		{
			serviceDefinitionRef.setSegmentRef(getFlightSegmentsMap().get(transportOfferingCode));
		}
	}

	/**
	 * Gets the service definition type.
	 *
	 * @param product
	 *           the product
	 * @return the service definition type
	 */
	protected ServiceDefinitionType getServiceDefinitionType(final ProductModel product)
	{
		final ServiceDefinitionType serviceDefinitionType = new ServiceDefinitionType();

		serviceDefinitionType.setServiceDefinitionID(product.getCode());

		final Name name = new Name();
		name.setValue(product.getName());
		serviceDefinitionType.setName(name);

		final ServiceDescriptionType serviceDescription = new ServiceDescriptionType();
		if (StringUtils.isNotEmpty(product.getDescription()))
		{
			final Description description = new Description();
			final Text descriptionText = new Text();
			descriptionText.setValue(product.getDescription());
			description.setText(descriptionText);
			serviceDescription.getDescription().add(description);
		}

		if (CollectionUtils.isNotEmpty(product.getLinkComponents()))
		{
			final String url = product.getLinkComponents().stream().findFirst().get().getUrl();
			if (StringUtils.isNotEmpty(url))
			{
				final Description description = new Description();
				description.setLink(url);
				serviceDescription.getDescription().add(description);
			}
		}

		if (CollectionUtils.isEmpty(serviceDescription.getDescription()))
		{
			final Description description = new Description();
			description.setApplication(product.getName());
			serviceDescription.getDescription().add(description);
		}
		serviceDefinitionType.setDescriptions(serviceDescription);
		return serviceDefinitionType;
	}

	/**
	 * Gets the seats.
	 *
	 * @param selectedAccommodation
	 *           the selected accommodation
	 * @return the seats
	 */
	protected Seats getSeats(final SelectedAccommodationModel selectedAccommodation)
	{
		if (Objects.isNull(selectedAccommodation))
		{
			return null;
		}
		final SeatLocationType location = getSeatLocationType(selectedAccommodation);
		final Seats seatType = new Seats();
		seatType.setListKey(new StringBuilder(location.getColumn()).append(location.getRow().getNumber().getValue())
				.append(selectedAccommodation.getTransportOffering().getCode()).toString());
		seatType.setLocation(location);
		getSeatsMap().put(seatType.getListKey(), seatType);
		return seatType;
	}

	/**
	 * This method populates {@link SeatLocationType} data for each and every seat.
	 *
	 * @param selectedAccommodation
	 *           the selected accommodation
	 * @return the seat location type
	 */
	protected SeatLocationType getSeatLocationType(final SelectedAccommodationModel selectedAccommodation)
	{
		final SeatLocationType location = new SeatLocationType();
		final ConfiguredAccommodationModel seat = selectedAccommodation.getConfiguredAccommodation();
		if (Objects.nonNull(seat) && Objects.nonNull(seat.getSuperConfiguredAccommodation()))
		{
			final ConfiguredAccommodationModel row = getConfiguredAccommodation(seat.getSuperConfiguredAccommodation(),
					ConfiguredAccommodationType.ROW);
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
		return location;
	}

	/**
	 * Gets the configured accommodation.
	 *
	 * @param configuredAccommodationModel
	 *           the configured accommodation model
	 * @param configuredAccommodationType
	 *           the configured accommodation type
	 * @return the configured accommodation
	 */
	protected ConfiguredAccommodationModel getConfiguredAccommodation(
			final ConfiguredAccommodationModel configuredAccommodationModel,
			final ConfiguredAccommodationType configuredAccommodationType)
	{
		if (configuredAccommodationModel.getType().equals(configuredAccommodationType))
		{
			return configuredAccommodationModel;
		}

		return getConfiguredAccommodation(configuredAccommodationModel.getSuperConfiguredAccommodation(),
				configuredAccommodationType);
	}

	/**
	 * Populate dataLists element with the flight segments and services contained in the hash maps
	 *
	 * @param dataLists
	 *           the data lists
	 */
	protected void populateDatalists(final DataLists dataLists)
	{
		final PassengerList passengerList = new PassengerList();
		passengerList.getPassenger().addAll(getPassengerTypeMap().values());
		dataLists.setPassengerList(passengerList);

		if (CollectionUtils.isNotEmpty(getContactInformationMap().values()))
		{
			final ContactList contactList = new ContactList();
			contactList.getContactInformation().addAll(getContactInformationMap().values());
			dataLists.setContactList(contactList);
		}

		final FlightSegmentList flightSegmentList = new FlightSegmentList();
		flightSegmentList.getFlightSegment().addAll(getFlightSegmentsMap().values());
		dataLists.setFlightSegmentList(flightSegmentList);

		if (CollectionUtils.isNotEmpty(getServiceDefinitionMap().values()))
		{
			final ServiceDefinitionList serviceDefinitionList = new ServiceDefinitionList();
			serviceDefinitionList.getServiceDefinition().addAll(getServiceDefinitionMap().values());
			dataLists.setServiceDefinitionList(serviceDefinitionList);
		}

		if (CollectionUtils.isNotEmpty(getSeatsMap().values()))
		{
			final SeatList seatList = new SeatList();
			seatList.getSeats().addAll(getSeatsMap().values());
			dataLists.setSeatList(seatList);
		}
	}

	/**
	 * Gets flight segments map.
	 *
	 * @return the flight segments map
	 */
	protected Map<String, ListOfFlightSegmentType> getFlightSegmentsMap()
	{
		return flightSegmentsMap;
	}

	/**
	 * @return the serviceDefinitionMap
	 */
	protected Map<String, ServiceDefinitionType> getServiceDefinitionMap()
	{
		return serviceDefinitionMap;
	}

	/**
	 * Gets seats.
	 *
	 * @return the seats
	 */
	protected Map<String, Seats> getSeatsMap()
	{
		return seatsMap;
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
	 * @return the contactInformationMap
	 */
	protected Map<String, ContactInformationType> getContactInformationMap()
	{
		return contactInformationMap;
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

	/**
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 *           the offerGroupToOriginDestinationMapping to set
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}
}
