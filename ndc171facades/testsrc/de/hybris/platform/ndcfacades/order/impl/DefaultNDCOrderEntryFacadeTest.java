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

package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.Contacts.Contact;
import de.hybris.platform.ndcfacades.ndc.EmailType;
import de.hybris.platform.ndcfacades.ndc.EmailType.Address;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OfferItemType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.DataLists;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Order;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ.Query.Passengers;
import de.hybris.platform.ndcfacades.ndc.OrderOfferItemType;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType;
import de.hybris.platform.ndcfacades.ndc.SeatLocationType.Row;
import de.hybris.platform.ndcfacades.ndc.SeatMapRowNbrType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices.AssociatedService;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AddAncillariesToOrderRestrictionStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCAccommodationService;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


//TODO This (or part of this class) has been commented out due to 17.1 upgrade and needs to be revisited
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNDCOrderEntryFacadeTest
{
	@InjectMocks
	DefaultNDCOrderEntryFacade defaultNDCOrderEntryFacade;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	ConfigurationService configurationService;
	@Mock
	BusinessProcessService businessProcessService;
	@Mock
	ModelService modelService;
	@Mock
	BookingService bookingService;
	@Mock
	ReservationFacade reservationFacade;
	@Mock
	ProductService productService;
	@Mock
	TravellerService travellerService;
	@Mock
	CalculationService calculationService;
	@Mock
	BundleTemplateService bundleTemplateService;
	@Mock
	UserService userService;
	@Mock
	TravelRestrictionFacade travelRestrictionFacade;
	@Mock
	Map<String, String> offerGroupToOriginDestinationMapping;
	@Mock
	NDCOrderService ndcOrderService;
	@Mock
	NDCTransportOfferingService ndcTransportOfferingService;
	@Mock
	NDCAccommodationService ndcAccommodationService;
	@Mock
	List<String> categoriesNotAllowed;
	@Mock
	Map<String, AddAncillariesToOrderRestrictionStrategy> addAncillariesToOrderRestrictionStrategyMap;
	@Mock
	NDCPassengerTypeService ndcPassengerTypeService;
	@Mock
	NDCOfferItemIdResolver ndcOfferItemIdResolver;

	OrderCreateRQ orderCreateRQ;
	OrderModel order;

	@Test(expected = ConversionException.class)
	public void testCreateOrderEntriesWithConversionException() throws CalculationException, NDCOrderException
	{
		orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();

		createPassengerList(query);
		createOrderRequestType(query);

		//final OrderItems orderItems = new OrderItems();
		final ShoppingResponseOrderType shoppingResponses = new ShoppingResponseOrderType();
		final Offers offers = new Offers();
		final Offer offer = new Offer();
		final OfferItems offerItems = new OfferItems();
		final OfferItem offerItem = new OfferItem();

		final ItemIDType offerItemId = new ItemIDType();
		offerItemId.setValue("OfferItemValue");
		offerItem.setOfferItemID(offerItemId);
		offerItems.getOfferItem().add(offerItem);
		offer.setOfferItems(offerItems);

		offers.getOffer().add(offer);
		shoppingResponses.setOffers(offers);
		//orderItems.setShoppingResponse(shoppingResponses);
		//query.setOrderItems(orderItems);
		orderCreateRQ.setQuery(query);

		final Contacts contacts = new Contacts();
		final Contact contact = new Contact();
		final EmailType email = new EmailType();
		final Address address = new Address();
		address.setValue("address value");
		email.setAddress(address);
		contact.setEmailContact(email);

		contacts.getContact().add(contact);
		//passenger.setContacts(contacts);

		order = new OrderModel();
		order.setCode("1001");

		final PassengerTypeModel passengerType = new PassengerTypeModel();
		passengerType.setCode("passengerTypeCode");

		//Mockito.when(ndcPassengerTypeService.getPassengerType(ptc.getValue())).thenReturn(passengerType);

		final TravellerModel travellerModel = new TravellerModel();
		//travellerModel.setLabel(passenger.getObjectKey());
		/*
		 * Mockito.when(travellerService.createTraveller(TravellerType.PASSENGER.getCode(), passengerType.getCode(),
		 * passenger.getObjectKey(), 0, "", order.getCode())).thenReturn(travellerModel);
		 */
		final TitleModel title = new TitleModel();
		title.setCode("Mr");
		//Mockito.when(userService.getTitleForCode(passenger.getName().getTitle())).thenReturn(title);

		final NDCOfferItemId ndcOfferItemId = new NDCOfferItemId();
		final NDCOfferItemIdBundle ndcOfferItemIdBundle = new NDCOfferItemIdBundle();
		ndcOfferItemIdBundle.setBundle("bundle");
		final List<NDCOfferItemIdBundle> bundleList = Collections.singletonList(ndcOfferItemIdBundle);
		ndcOfferItemId.setBundleList(bundleList);
		ndcOfferItemId.setOriginDestinationRefNumber(0);
		final List<String> transportOfferings = Collections.singletonList("transportOffering");
		ndcOfferItemIdBundle.setTransportOfferings(transportOfferings);
		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(offerItem.getOfferItemID().getValue()))
				.thenReturn(ndcOfferItemId);

		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		bundleTemplate.setChildTemplates(Collections.emptyList());
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle())).thenReturn(bundleTemplate);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		final List<TransportOfferingModel> bundleTransportOfferings = Collections.singletonList(transportOffering);
		Mockito.when(ndcTransportOfferingService.getTransportOfferings(ndcOfferItemIdBundle.getTransportOfferings()))
				.thenReturn(bundleTransportOfferings);

		defaultNDCOrderEntryFacade.createOrderEntries(orderCreateRQ, order);
	}

	@Test(expected = NDCOrderException.class)
	public void testCreateOrderEntriesWithNDCOrderException() throws CalculationException, NDCOrderException
	{
		orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();

		createPassengerList(query);
		createOrderRequestType(query);

		//final OrderItems orderItems = new OrderItems();
		final ShoppingResponseOrderType shoppingResponses = new ShoppingResponseOrderType();
		final Offers offers = new Offers();
		final Offer offer = new Offer();
		final OfferItems offerItems = new OfferItems();
		final OfferItem offerItem = new OfferItem();

		final ItemIDType offerItemId = new ItemIDType();
		offerItemId.setValue("OfferItemValue");
		offerItem.setOfferItemID(offerItemId);
		offerItems.getOfferItem().add(offerItem);
		offer.setOfferItems(offerItems);

		offers.getOffer().add(offer);
		shoppingResponses.setOffers(offers);
		//orderItems.setShoppingResponse(shoppingResponses);
		//query.setOrderItems(orderItems);
		orderCreateRQ.setQuery(query);

		final de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.Passengers passengersForOfferItem = new de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.Passengers();
		offerItem.setPassengers(passengersForOfferItem);

		final Contacts contacts = new Contacts();
		final Contact contact = new Contact();
		final EmailType email = new EmailType();
		final Address address = new Address();
		address.setValue("address value");
		email.setAddress(address);
		contact.setEmailContact(email);

		contacts.getContact().add(contact);
		//passenger.setContacts(contacts);

		order = new OrderModel();
		order.setCode("1001");

		final PassengerTypeModel passengerType = new PassengerTypeModel();
		passengerType.setCode("passengerTypeCode");

		//Mockito.when(ndcPassengerTypeService.getPassengerType(ptc.getValue())).thenReturn(passengerType);

		final TravellerModel travellerModel = new TravellerModel();
		//travellerModel.setLabel(passenger.getObjectKey());
		/*
		 * Mockito.when(travellerService.createTraveller(TravellerType.PASSENGER.getCode(), passengerType.getCode(),
		 * passenger.getObjectKey(), 0, "", order.getCode())).thenReturn(travellerModel);
		 */
		final TitleModel title = new TitleModel();
		title.setCode("Mr");
		//Mockito.when(userService.getTitleForCode(passenger.getName().getTitle())).thenReturn(title);

		final NDCOfferItemId ndcOfferItemId=new NDCOfferItemId();
		final NDCOfferItemIdBundle ndcOfferItemIdBundle=new NDCOfferItemIdBundle();
		ndcOfferItemIdBundle.setBundle("bundle");
		final List<NDCOfferItemIdBundle> bundleList=Collections.singletonList(ndcOfferItemIdBundle);
		ndcOfferItemId.setBundleList(bundleList);
		ndcOfferItemId.setOriginDestinationRefNumber(0);
		final List<String> transportOfferings = Collections.singletonList("transportOffering");
		ndcOfferItemIdBundle.setTransportOfferings(transportOfferings);
		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(offerItem.getOfferItemID().getValue()))
				.thenReturn(ndcOfferItemId);

		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel childTemplate1 = new BundleTemplateModel();
		final BundleTemplateModel childTemplate2 = new BundleTemplateModel();

		final ProductModel product1 = new ProductModel();
		product1.setProductType(ProductType.FARE_PRODUCT);

		final ProductModel product2 = new ProductModel();
		final CategoryModel category = new CategoryModel();
		category.setCode("categoryCode");
		final Collection<CategoryModel> categories = Collections.singletonList(category);
		product2.setSupercategories(categories);
		product2.setProductType(ProductType.ANCILLARY);

		childTemplate1.setProducts(Collections.singletonList(product1));
		childTemplate2.setProducts(Collections.singletonList(product2));
		bundleTemplate.setChildTemplates(Arrays.asList(childTemplate1, childTemplate2));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle())).thenReturn(bundleTemplate);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		final List<TransportOfferingModel> bundleTransportOfferings = Collections.singletonList(transportOffering);
		Mockito.when(ndcTransportOfferingService.getTransportOfferings(ndcOfferItemIdBundle.getTransportOfferings()))
				.thenReturn(bundleTransportOfferings);

		Mockito.when(offerGroupToOriginDestinationMapping.getOrDefault(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(TravelservicesConstants.TRANSPORT_OFFERING);

		Mockito.when(ndcTransportOfferingService.isValidReturnDate(Mockito.anyMap())).thenReturn(false);
		Mockito.when(configurationService.getConfiguration().getString(NdcfacadesConstants.INVALID_OFFER_COMBINATION))
				.thenReturn("NDCOrderException");

		defaultNDCOrderEntryFacade.createOrderEntries(orderCreateRQ, order);
	}
	
	private void createPassengerList(final Query query)
	{
		final DataLists dataLists = new DataLists();
		final PassengerList passengerList = new PassengerList();
		final Passengers passengers = new Passengers();

		query.setDataLists(dataLists);
		dataLists.setPassengerList(passengerList);
		query.setPassengers(passengers);
	}

	private void createOrderRequestType(final Query query)
	{
		final Order orderRequestType = new Order();
		final List<de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer> offers = orderRequestType.getOffer();

		final de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer offer = new de.hybris.platform.ndcfacades.ndc
				.OrderRequestType.Offer();
		offers.add(offer);

		final List<de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem> offerItems = offers.get(0).getOfferItem();
		final de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem offerItem = new de.hybris.platform.ndcfacades.ndc
				.OrderRequestType.Offer.OfferItem();
		offerItem.setOfferItemID("OfferItemValue");
		offerItems.add(offerItem);

		query.setOrder(orderRequestType);
	}

	@Test
	public void testCreateOrderEntries() throws CalculationException, NDCOrderException
	{
		orderCreateRQ = new OrderCreateRQ();
		final Query query = new Query();

		createPassengerList(query);
		createOrderRequestType(query);
		
		//final OrderItems orderItems = new OrderItems();
		final ShoppingResponseOrderType shoppingResponses = new ShoppingResponseOrderType();
		final Offers offers = new Offers();
		final Offer offer = new Offer();
		final OfferItems offerItems = new OfferItems();
		final OfferItem offerItem = new OfferItem();

		final ItemIDType offerItemId = new ItemIDType();
		offerItemId.setValue("OfferItemValue");
		offerItem.setOfferItemID(offerItemId);
		offerItems.getOfferItem().add(offerItem);
		offer.setOfferItems(offerItems);

		offers.getOffer().add(offer);
		shoppingResponses.setOffers(offers);
		//orderItems.setShoppingResponse(shoppingResponses);
		//query.setOrderItems(orderItems);
		orderCreateRQ.setQuery(query);

		final de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.Passengers passengersForOfferItem = new de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.Passengers();
		offerItem.setPassengers(passengersForOfferItem);

		final Contacts contacts = new Contacts();
		final Contact contact = new Contact();
		final EmailType email = new EmailType();
		final Address address = new Address();
		address.setValue("address value");
		email.setAddress(address);
		contact.setEmailContact(email);

		contacts.getContact().add(contact);

		order = new OrderModel();
		order.setCode("1001");

		final PassengerTypeModel passengerType = new PassengerTypeModel();
		passengerType.setCode("passengerTypeCode");

		//Mockito.when(ndcPassengerTypeService.getPassengerType(ptc.getValue())).thenReturn(passengerType);

		final TravellerModel travellerModel = new TravellerModel();
		//travellerModel.setLabel(passenger.getObjectKey());
		/*
		 * Mockito.when(travellerService.createTraveller(TravellerType.PASSENGER.getCode(), passengerType.getCode(),
		 * passenger.getObjectKey(), 0, "", order.getCode())).thenReturn(travellerModel);
		 */
		final TitleModel title = new TitleModel();
		title.setCode("Mr");
		//Mockito.when(userService.getTitleForCode(passenger.getName().getTitle())).thenReturn(title);

		final NDCOfferItemId ndcOfferItemId=new NDCOfferItemId();
		final NDCOfferItemIdBundle ndcOfferItemIdBundle=new NDCOfferItemIdBundle();
		ndcOfferItemIdBundle.setBundle("bundle");
		final List<NDCOfferItemIdBundle> bundleList=Collections.singletonList(ndcOfferItemIdBundle);
		ndcOfferItemId.setBundleList(bundleList);
		ndcOfferItemId.setOriginDestinationRefNumber(0);
		final List<String> transportOfferings = Collections.singletonList("transportOffering");
		ndcOfferItemIdBundle.setTransportOfferings(transportOfferings);
		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(offerItem.getOfferItemID().getValue()))
				.thenReturn(ndcOfferItemId);

		final BundleTemplateModel bundleTemplate = new BundleTemplateModel();
		final BundleTemplateModel childTemplate = new BundleTemplateModel();

		final ProductModel product1 = new ProductModel();
		product1.setProductType(ProductType.FARE_PRODUCT);

		final ProductModel product2 = new ProductModel();
		final CategoryModel category = new CategoryModel();
		category.setCode("categoryCode");
		final Collection<CategoryModel> categories = Collections.singletonList(category);
		product2.setSupercategories(categories);
		product2.setCode("ancillaryCode");
		product2.setProductType(ProductType.ANCILLARY);

		final List<ProductModel> products = Arrays.asList(product1, product2);
		childTemplate.setProducts(products);
		bundleTemplate.setChildTemplates(Collections.singletonList(childTemplate));
		Mockito.when(bundleTemplateService.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle())).thenReturn(bundleTemplate);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setCode("");
		final List<TransportOfferingModel> bundleTransportOfferings = Collections.singletonList(transportOffering);
		Mockito.when(ndcTransportOfferingService.getTransportOfferings(ndcOfferItemIdBundle.getTransportOfferings()))
				.thenReturn(bundleTransportOfferings);

		Mockito.when(offerGroupToOriginDestinationMapping.getOrDefault(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(TravelservicesConstants.TRANSPORT_OFFERING);

		Mockito.when(ndcTransportOfferingService.isValidReturnDate(Mockito.anyMap())).thenReturn(true);
		final AssociatedServices associatedServices = new AssociatedServices();
		final AssociatedService associatedService = new AssociatedService();
		final ServiceIDType serviceIdType = new ServiceIDType();
		serviceIdType.setValue("serviceIdTypeValue");
		associatedService.setServiceID(serviceIdType);
		final de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices.AssociatedService.Passengers associatedservicePassenger = new de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices.AssociatedService.Passengers();
		//associatedservicePassenger.getPassengerReference().add(passenger);
		associatedService.setPassengers(associatedservicePassenger);
		associatedServices.getAssociatedService().add(associatedService);
		offerItem.setAssociatedServices(associatedServices);

		Mockito.when(productService.getProductForCode(associatedService.getServiceID().getValue())).thenReturn(product2);
		Mockito.when(travelRestrictionFacade.getAddToCartCriteria(product2.getCode())).thenReturn("productRestriction");
		final AddAncillariesToOrderRestrictionStrategy addAncillariesToOrderRestrictionStrategy = Mockito
				.mock(AddAncillariesToOrderRestrictionStrategy.class);
		Mockito.when(addAncillariesToOrderRestrictionStrategyMap.get("productRestriction"))
				.thenReturn(addAncillariesToOrderRestrictionStrategy);

		final OrderOfferItemType orderOfferItemType = new OrderOfferItemType();
		orderOfferItemType.setOfferItemID(offerItemId);
		final OfferItemType offerItemType = new OfferItemType();
		orderOfferItemType.setOfferItemType(offerItemType);
		//orderCreateRQ.getQuery().getOrderItems().getOfferItem().add(orderOfferItemType);
		final SeatItem seatItem = new SeatItem();

		final JAXBElement<Object> seatReference = Mockito.mock(JAXBElement.class, Mockito.RETURNS_DEEP_STUBS);
		final ListOfSeatType seat = new ListOfSeatType();
		Mockito.when(seatReference.getValue()).thenReturn(seat);

		final SeatLocationType seatLocation = new SeatLocationType();
		seatLocation.setColumn("A");
		final Row row = new Row();
		final SeatMapRowNbrType seatNumber = new SeatMapRowNbrType();
		seatNumber.setValue("42");
		row.setNumber(seatNumber);
		seatLocation.setRow(row);
		seat.setLocation(seatLocation);
		final String listKey = "A42";
		seat.setListKey(listKey);

		seatItem.getSeatReference().add(seatReference);
		//seatItem.getRefs().add(passenger);
		orderOfferItemType.getOfferItemType().getSeatItem().add(seatItem);

		Mockito.when(ndcOfferItemIdResolver.getNDCOfferItemIdFromString(orderOfferItemType.getOfferItemID().getValue()))
				.thenReturn(ndcOfferItemId);

		final ConfiguredAccommodationModel accommodation = new ConfiguredAccommodationModel();
		accommodation.setProduct(product1);
		Mockito.when(ndcAccommodationService.checkIfSeatValidForFareProd(accommodation.getProduct(), ndcOfferItemId))
				.thenReturn(true);
		Mockito.when(ndcAccommodationService.getConfiguredAccommodation(Mockito.any(NDCOfferItemId.class),
				Mockito.any(TransportOfferingModel.class), Mockito.anyString()))
				.thenReturn(accommodation);
		Mockito.when(ndcAccommodationService.checkIfAccommodationCanBeAdded(Mockito.any(ProductModel.class), Mockito.anyString(),
				Mockito.any(NDCOfferItemId.class), Mockito.any(TransportOfferingModel.class))).thenReturn(true);
		Mockito.when(ndcAccommodationService.checkIfSeatValidForFareProd(Mockito.any(ProductModel.class),
				Mockito.any(NDCOfferItemId.class))).thenReturn(true);

		defaultNDCOrderEntryFacade.createOrderEntries(orderCreateRQ, order);

		Mockito.verify(calculationService, Mockito.times(1)).calculate(order);
	}
}
