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
package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.NDCOfferItemIdBundle;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.facades.AbstractNDCFacade;
import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ListOfSeatType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderOfferItemType;
import de.hybris.platform.ndcfacades.ndc.Passenger;
import de.hybris.platform.ndcfacades.ndc.SeatItem;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices.AssociatedService;
import de.hybris.platform.ndcfacades.ndc.ShoppingResponseOrderType.Offers.Offer.OfferItems.OfferItem.AssociatedServices.AssociatedService.Passengers;
import de.hybris.platform.ndcfacades.order.NDCOrderEntryFacade;
import de.hybris.platform.ndcfacades.strategies.AddAncillariesToOrderRestrictionStrategy;
import de.hybris.platform.ndcfacades.utils.NdcFacadesUtils;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCAccommodationService;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCOrderEntryFacade}
 */
public class DefaultNDCOrderEntryFacade extends AbstractNDCFacade implements NDCOrderEntryFacade
{
	private static final int BUNDLE_STARTING_INDEX = 1;
	private static final Logger LOG = Logger.getLogger(DefaultNDCOrderEntryFacade.class);

	private ProductService productService;
	private TravellerService travellerService;
	private CalculationService calculationService;
	private BundleTemplateService bundleTemplateService;
	private UserService userService;
	private TravelRestrictionFacade travelRestrictionFacade;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	private NDCOrderService ndcOrderService;
	private NDCTransportOfferingService ndcTransportOfferingService;
	private NDCAccommodationService ndcAccommodationService;

	private List<String> categoriesNotAllowed;

	private Map<String, AddAncillariesToOrderRestrictionStrategy> addAncillariesToOrderRestrictionStrategyMap;

	@Override
	public void createOrderEntries(final OrderCreateRQ orderCreateRQ, final OrderModel order)
			throws CalculationException, NDCOrderException
	{
		final List<AbstractOrderEntryModel> orderEntries = new LinkedList<>();
		int bundleNo = BUNDLE_STARTING_INDEX;

		order.setEntries(orderEntries);

		final List<TravellerModel> travellers = new LinkedList<>();
		populateTravellers(travellers, orderCreateRQ.getQuery().getPassengers().getPassenger(), order.getCode());

		final Map<String, List<TransportOfferingModel>> transportOfferings = new HashMap<>();

		for (final Offer offer : orderCreateRQ.getQuery().getOrderItems().getShoppingResponse().getOffers().getOffer())
		{
			for (final OfferItem offerItem : offer.getOfferItems().getOfferItem())
			{
				final List<TravellerModel> selectedTravellers = getOfferTraveller(offerItem, travellers);

				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
						.getNDCOfferItemIdFromString(offerItem.getOfferItemID().getValue());

				for (final NDCOfferItemIdBundle ndcOfferItemIdBundle : ndcOfferItemId.getBundleList())
				{
					final BundleTemplateModel bundleTemplate = getBundleTemplateService()
							.getBundleTemplateForCode(ndcOfferItemIdBundle.getBundle());
					final List<TransportOfferingModel> bundleTransportOfferings = getNdcTransportOfferingService()
							.getTransportOfferings(ndcOfferItemIdBundle.getTransportOfferings());
					transportOfferings.put(String.valueOf(ndcOfferItemId.getOriginDestinationRefNumber()), bundleTransportOfferings);

					createBundledOrderEntryPerTraveller(order, selectedTravellers, bundleNo, ndcOfferItemId, ndcOfferItemIdBundle,
							bundleTemplate, bundleTransportOfferings);
					bundleNo++;
				}

				if (!getNdcTransportOfferingService().isValidReturnDate(transportOfferings))
				{
					throw new NDCOrderException(
							getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_OFFER_COMBINATION));
				}

				createUnbundledAncillariesOrderEntry(order, selectedTravellers, ndcOfferItemId, offerItem,
						transportOfferings.get(String.valueOf(ndcOfferItemId.getOriginDestinationRefNumber())));
			}
		}

		for (final OrderOfferItemType orderOfferItem : orderCreateRQ.getQuery().getOrderItems().getOfferItem())
		{
			final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver()
					.getNDCOfferItemIdFromString(orderOfferItem.getOfferItemID().getValue());
			for (final SeatItem seatItem : orderOfferItem.getOfferItemType().getSeatItem())
			{
				createAccommodationOrderEntry(order, travellers, seatItem,
						transportOfferings.get(String.valueOf(ndcOfferItemId.getOriginDestinationRefNumber())), ndcOfferItemId);
			}
		}
		getCalculationService().calculate(order);
	}

	/**
	 * This method creates order entry for each {@link SeatItem} for given params. Than it populates order entry and
	 * creates instance of {@link SelectedAccommodationModel}.
	 *
	 * @param order
	 * 		the order
	 * @param travellers
	 * 		the travellers
	 * @param seatItem
	 * 		the seat item
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void createAccommodationOrderEntry(final OrderModel order, final List<TravellerModel> travellers,
			final SeatItem seatItem, final List<TransportOfferingModel> transportOfferings, final NDCOfferItemId ndcOfferItemId)
			throws NDCOrderException
	{
		final TransportOfferingModel transportOffering = getRequiredTransportOffering(transportOfferings,
				seatItem.getSeatReference());
		final ListOfSeatType seat = (ListOfSeatType) seatItem.getSeatReference().get(0).getValue();
		final String seatNum = NdcFacadesUtils.getSeatNum(seat);
		final ConfiguredAccommodationModel accommodation;
		try
		{
			accommodation = getNdcAccommodationService()
					.getConfiguredAccommodation(ndcOfferItemId, transportOffering, seatNum);
		}
		catch (final AccommodationMapDataSetUpException e)
		{
			LOG.debug(e);
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID));
		}
		if (Objects.isNull(accommodation) || Objects.isNull(accommodation.getProduct()))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_UNAVAILABLE));
		}

		if (!getNdcAccommodationService().checkIfAccommodationCanBeAdded(accommodation.getProduct(), seatNum, ndcOfferItemId,
				transportOffering))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_UNAVAILABLE));
		}

		if (!getNdcAccommodationService().checkIfSeatValidForFareProd(accommodation.getProduct(), ndcOfferItemId))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID_BUNDLE));
		}

		final List<TravellerModel> selectedTravellers = getAccommodationTraveller(seatItem.getRefs(), travellers);
		getNdcOrderService()
				.populateOrderEntry(order, accommodation.getProduct(), null, NdcfacadesConstants.ASSOCIATED_SERVICE_BUNDLE_NUMBER,
						getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId),
						Collections.singletonList(transportOffering), selectedTravellers, ndcOfferItemId.getRouteCode(),
						ndcOfferItemId.getOriginDestinationRefNumber(), NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
		getNdcAccommodationService().createOrUpdateSelectedAccommodation(transportOffering, selectedTravellers, order,
				accommodation);
	}

	/**
	 * This method filters list of {@link TransportOfferingModel} and returns an instance which matches transport
	 * offering code.
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param list
	 * 		the list
	 *
	 * @return the required transport offering
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected TransportOfferingModel getRequiredTransportOffering(final List<TransportOfferingModel> transportOfferings,
			final List<JAXBElement<Object>> list) throws NDCOrderException
	{
		final Optional<TransportOfferingModel> opt = transportOfferings.stream().filter(transportOffering -> StringUtils
				.equals(transportOffering.getCode(), getTransportOfferingCode((ListOfSeatType) list.get(0).getValue()))).findFirst();
		if (!opt.isPresent())
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.GENERIC_ERROR));
		}
		return opt.get();
	}

	/**
	 * This method extract transport offering code from unique key of {@link ListOfSeatType}.
	 *
	 * @param seat
	 * 		the seat
	 *
	 * @return the transport offering code
	 */
	protected String getTransportOfferingCode(final ListOfSeatType seat)
	{
		final String seatNum = seat.getLocation().getColumn() + seat.getLocation().getRow().getNumber().getValue();
		if (seat.getListKey().indexOf(seatNum) == 0)
		{
			return seat.getListKey().substring(seatNum.length());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Creates an {@link OrderEntryModel} for each ancillary not included in the bundles
	 *
	 * @param order
	 * 		the order
	 * @param travellers
	 * 		the travellers
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param offerItem
	 * 		the offer item
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void createUnbundledAncillariesOrderEntry(final OrderModel order, final List<TravellerModel> travellers,
			final NDCOfferItemId ndcOfferItemId, final OfferItem offerItem, final List<TransportOfferingModel> transportOfferings)
			throws NDCOrderException
	{
		if (Objects.isNull(offerItem.getAssociatedServices()))
		{
			return;
		}

		for (final AssociatedService associatedService : offerItem.getAssociatedServices().getAssociatedService())
		{
			if (Objects.isNull(associatedService.getServiceID()))
			{
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_SERVICE_ID));
			}

			final ProductModel ancillaryProduct;
			try
			{
				ancillaryProduct = getProductService().getProductForCode(associatedService.getServiceID().getValue());
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.debug(e);
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_SERVICE_ID));
			}

			checkProductCategory(ancillaryProduct);

			final String productRestriction = getTravelRestrictionFacade().getAddToCartCriteria(ancillaryProduct.getCode());
			final List<TransportOfferingModel> associatedTransportOfferings = getAssociatedTransportOffering(
					associatedService.getServiceID(), transportOfferings);

			final List<TravellerModel> associatedTravellers = getAssociatedTravellers(associatedService.getPassengers(), travellers);

			getAddAncillariesToOrderRestrictionStrategyMap().get(productRestriction).addAncillary(order, associatedTravellers,
					ancillaryProduct, associatedTransportOfferings, offerItem.getOfferItemID().getValue(),
					ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber());
		}
	}

	/**
	 * Returns the list of {@link TravellerModel} that are associated to the specified {@link ServiceIDType} extracting
	 * them from the provided list of {@link TravellerModel}
	 *
	 * @param passengers
	 * 		the passengers
	 * @param travellers
	 * 		the travellers
	 *
	 * @return associated travellers
	 */
	protected List<TravellerModel> getAssociatedTravellers(final Passengers passengers, final List<TravellerModel> travellers)
	{
		if (Objects.isNull(passengers))
		{
			return Collections.emptyList();
		}
		final List<String> passengerIds = passengers.getPassengerReference().stream().filter(entry -> entry instanceof Passenger)
				.map(entry -> ((Passenger) entry).getObjectKey()).collect(Collectors.toList());

		return travellers.stream().filter(entry -> passengerIds.contains(entry.getLabel())).collect(Collectors.toList());
	}

	/**
	 * Checks that the product category is not among the restricted ones. Throws an exception otherwise. (i.e. Seat are
	 * accommodation products and needs to be included in a different element in the OrderCreateRQ
	 *
	 * @param ancillaryProduct
	 * 		the ancillary product
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void checkProductCategory(final ProductModel ancillaryProduct) throws NDCOrderException
	{
		if (Objects.nonNull(ancillaryProduct) && CollectionUtils.isNotEmpty(ancillaryProduct.getSupercategories())
				&& getCategoriesNotAllowed().contains(ancillaryProduct.getSupercategories().stream().findFirst().get().getCode()))
		{
			throw new NDCOrderException(ancillaryProduct.getName() + " cannot be added to the order");
		}
	}

	/**
	 * Returns the list of {@link TransportOfferingModel} that are associated to the specified {@link ServiceIDType}
	 * extracting them from the provided list of {@link TransportOfferingModel}
	 *
	 * @param serviceID
	 * 		the service id
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @return associated transport offering
	 */
	protected List<TransportOfferingModel> getAssociatedTransportOffering(final ServiceIDType serviceID,
			final List<TransportOfferingModel> transportOfferings)
	{
		final List<String> flightSegments = serviceID.getRefs().stream().filter(entry -> entry instanceof ListOfFlightSegmentType)
				.map(entry -> ((ListOfFlightSegmentType) entry).getSegmentKey()).collect(Collectors.toList());

		return transportOfferings.stream().filter(entry -> flightSegments.contains(entry.getCode())).collect(Collectors.toList());
	}

	/**
	 * Creates an {@link OrderEntryModel} per each {@link TravellerModel} per each bundle contained in the
	 * {@link OfferItem}
	 *
	 * @param order
	 * 		the order
	 * @param travellers
	 * 		the travellers
	 * @param bundleNo
	 * 		the bundle no
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param ndcOfferItemIdBundle
	 * 		the ndc offer item id bundle
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void createBundledOrderEntryPerTraveller(final OrderModel order, final List<TravellerModel> travellers,
			final int bundleNo, final NDCOfferItemId ndcOfferItemId, final NDCOfferItemIdBundle ndcOfferItemIdBundle,
			final BundleTemplateModel bundleTemplate, final List<TransportOfferingModel> transportOfferings) throws NDCOrderException
	{
		for (final TravellerModel traveller : travellers)
		{
			final BundleTemplateModel fareProductChildBundle = getFareProductChildBundle(bundleTemplate);
			final BundleTemplateModel ancillaryChildBundle = getAncillaryChildBundle(bundleTemplate);
			final List<TravellerModel> travellerList = new LinkedList<>();
			travellerList.add(traveller);

			if (!Objects.isNull(ancillaryChildBundle))
			{
				populateAncillaryFromBundle(order, ancillaryChildBundle, bundleNo, ndcOfferItemId, transportOfferings, travellerList);
			}

			getNdcOrderService().populateOrderEntry(order,
					getProductService().getProductForCode(ndcOfferItemIdBundle.getFareProduct()), fareProductChildBundle, bundleNo,
					getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId), transportOfferings, travellerList,
					ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(),
					NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
		}
	}

	/**
	 * Returns a list of {@link TravellerModel} associated to a particular {@link OfferItem}
	 *
	 * @param offerItem
	 * 		the offer item
	 * @param travellers
	 * 		the travellers
	 *
	 * @return offer traveller
	 */
	protected List<TravellerModel> getOfferTraveller(final OfferItem offerItem, final List<TravellerModel> travellers)
	{
		return offerItem.getPassengers().getPassengerReference().stream().filter(p -> p instanceof Passenger)
				.map(p -> (Passenger) p).map(p -> travellers.stream()
						.filter(traveller -> StringUtils.equals(traveller.getLabel(), p.getObjectKey())).findFirst())
				.map(Optional::get).collect(Collectors.toList());
	}

	/**
	 * Returns a list of {@link TravellerModel} associated to a particular seatItem's reference.
	 *
	 * @param objects
	 * 		the objects
	 * @param travellers
	 * 		the travellers
	 *
	 * @return the accommodation traveller
	 */
	protected List<TravellerModel> getAccommodationTraveller(final List<Object> objects, final List<TravellerModel> travellers)
	{
		return objects.stream().filter(obj -> obj instanceof Passenger).map(passenger -> (Passenger) passenger)
				.map(passenger -> travellers.stream()
						.filter(traveller -> StringUtils.equals(traveller.getLabel(), passenger.getObjectKey())).findFirst())
				.map(Optional::get).collect(Collectors.toList());
	}

	/**
	 * Creates {@link TravellerModel} from the list of {@link Passenger} provided
	 *
	 * @param travellers
	 * 		the travellers
	 * @param passengers
	 * 		the passengers
	 * @param orderCode
	 * 		the order code
	 */
	protected void populateTravellers(final List<TravellerModel> travellers, final List<Passenger> passengers,
			final String orderCode)
	{
		for (final Passenger passenger : passengers)
		{
			final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
			final TravellerModel travellerModel = getTravellerService().createTraveller(TravellerType.PASSENGER.getCode(),
					getPassengerTypeModel(passenger.getPTC()).getCode(), passenger.getObjectKey(), travellers.size(), "", orderCode);

			populatePassengerInformation(passengerInformationModel, passenger);
			travellerModel.setInfo(passengerInformationModel);

			travellers.add(travellerModel);

			getModelService().save(passengerInformationModel);
			getModelService().save(travellerModel);
		}
	}

	/**
	 * Populates the {@link PassengerInformationModel} with the information contained in the {@link Passenger}
	 *
	 * @param passengerInformationModel
	 * 		the passenger information model
	 * @param passenger
	 * 		the passenger
	 */
	protected void populatePassengerInformation(final PassengerInformationModel passengerInformationModel,
			final Passenger passenger)
	{
		final PassengerTypeModel passengerType = getPassengerTypeModel(passenger.getPTC());

		if (Objects.nonNull(passenger.getName()) && Objects.nonNull(passenger.getName().getTitle()))
		{
			final TitleModel title = getUserService().getTitleForCode(passenger.getName().getTitle());
			passengerInformationModel.setTitle(title);
		}

		if (!Objects.isNull(passenger.getGender()))
		{
			passengerInformationModel.setGender(passenger.getGender().getValue().toString().toLowerCase());
		}

		if (!Objects.isNull(passenger.getContacts()))
		{
			for (final Contacts.Contact contact : passenger.getContacts().getContact())
			{
				if (Objects.nonNull(contact.getEmailContact()) && Objects.nonNull(contact.getEmailContact().getAddress())
						&& Objects.nonNull(contact.getEmailContact().getAddress().getValue()))
				{
					passengerInformationModel.setEmail(contact.getEmailContact().getAddress().getValue());
				}
			}
		}

		passengerInformationModel.setSurname(passenger.getName().getSurname().getValue());
		passengerInformationModel.setPassengerType(passengerType);
		passengerInformationModel.setFirstName(
				passenger.getName().getGiven().stream().map(Passenger.Name.Given::getValue).collect(Collectors.joining(" ")));
	}


	/**
	 * Creates an order entry per ancillary contained in the bundle
	 *
	 * @param order
	 * 		the order
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param bundleNo
	 * 		the bundle no
	 * @param ndcOfferItemId
	 * 		the ndc offer item id
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param travellers
	 * 		the travellers
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void populateAncillaryFromBundle(final OrderModel order, final BundleTemplateModel bundleTemplate,
			final int bundleNo, final NDCOfferItemId ndcOfferItemId, final List<TransportOfferingModel> transportOfferings,
			final List<TravellerModel> travellers) throws NDCOrderException
	{
		for (final ProductModel product : bundleTemplate.getProducts())
		{
			if (!(ProductType.ANCILLARY.equals(product.getProductType()) || product instanceof AncillaryProductModel))
			{
				continue;
			}
			String categoryCode = StringUtils.EMPTY;
			if (CollectionUtils.isNotEmpty(product.getSupercategories()))
			{
				final Optional<CategoryModel> category = product.getSupercategories().stream().findFirst();
				categoryCode = category.isPresent() ? category.get().getCode() : StringUtils.EMPTY;
			}

			final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode,
					getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
							TravelservicesConstants.TRAVEL_ROUTE));

			if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRANSPORT_OFFERING))
			{
				for (final TransportOfferingModel transportOffering : transportOfferings)
				{
					final List<TransportOfferingModel> transportOfferingList = new LinkedList<>();
					transportOfferingList.add(transportOffering);
					getNdcOrderService().populateOrderEntry(order, product, bundleTemplate, bundleNo,
							getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId), transportOfferingList, travellers,
							ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(),
							NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
				}
			}
			else if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRAVEL_ROUTE))
			{
				getNdcOrderService().populateOrderEntry(order, product, bundleTemplate, bundleNo,
						getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId), transportOfferings, travellers,
						ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(),
						NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
			}
		}
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
	 * Gets traveller service.
	 *
	 * @return the traveller service
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 * 		the traveller service
	 */
	@Required
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * Gets calculation service.
	 *
	 * @return the calculation service
	 */
	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * Sets calculation service.
	 *
	 * @param calculationService
	 * 		the calculation service
	 */
	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
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


	/**
	 * Gets offer group to origin destination mapping.
	 *
	 * @return the offer group to origin destination mapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * Sets offer group to origin destination mapping.
	 *
	 * @param offerGroupToOriginDestinationMapping
	 * 		the offer group to origin destination mapping
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	/**
	 * Gets travel restriction facade.
	 *
	 * @return the travel restriction facade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * Sets travel restriction facade.
	 *
	 * @param travelRestrictionFacade
	 * 		the travel restriction facade
	 */
	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}

	/**
	 * Gets ndc order service.
	 *
	 * @return the ndc order service
	 */
	protected NDCOrderService getNdcOrderService()
	{
		return ndcOrderService;
	}

	/**
	 * Sets ndc order service.
	 *
	 * @param ndcOrderService
	 * 		the ndc order service
	 */
	@Required
	public void setNdcOrderService(final NDCOrderService ndcOrderService)
	{
		this.ndcOrderService = ndcOrderService;
	}

	/**
	 * Gets ndc transport offering service.
	 *
	 * @return the ndc transport offering service
	 */
	public NDCTransportOfferingService getNdcTransportOfferingService()
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
	 * Gets ndc accommodation service.
	 *
	 * @return the ndc accommodation service
	 */
	protected NDCAccommodationService getNdcAccommodationService()
	{
		return ndcAccommodationService;
	}

	/**
	 * Sets ndc accommodation service.
	 *
	 * @param ndcAccommodationService
	 * 		the ndc accommodation service
	 */
	@Required
	public void setNdcAccommodationService(final NDCAccommodationService ndcAccommodationService)
	{
		this.ndcAccommodationService = ndcAccommodationService;
	}

	/**
	 * Gets add ancillaries to order restriction strategy map.
	 *
	 * @return the add ancillaries to order restriction strategy map
	 */
	protected Map<String, AddAncillariesToOrderRestrictionStrategy> getAddAncillariesToOrderRestrictionStrategyMap()
	{
		return addAncillariesToOrderRestrictionStrategyMap;
	}

	/**
	 * Sets add ancillaries to order restriction strategy map.
	 *
	 * @param addAncillariesToOrderRestrictionStrategyMap
	 * 		the add ancillaries to order restriction strategy map
	 */
	@Required
	public void setAddAncillariesToOrderRestrictionStrategyMap(
			final Map<String, AddAncillariesToOrderRestrictionStrategy> addAncillariesToOrderRestrictionStrategyMap)
	{
		this.addAncillariesToOrderRestrictionStrategyMap = addAncillariesToOrderRestrictionStrategyMap;
	}

	/**
	 * Gets categories not allowed.
	 *
	 * @return the categories not allowed
	 */
	protected List<String> getCategoriesNotAllowed()
	{
		return categoriesNotAllowed;
	}

	/**
	 * Sets categories not allowed.
	 *
	 * @param categoriesNotAllowed
	 * 		the categories not allowed
	 */
	@Required
	public void setCategoriesNotAllowed(final List<String> categoriesNotAllowed)
	{
		this.categoriesNotAllowed = categoriesNotAllowed;
	}
}
