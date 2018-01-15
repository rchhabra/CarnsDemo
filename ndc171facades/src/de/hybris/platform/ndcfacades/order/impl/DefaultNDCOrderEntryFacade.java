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
import de.hybris.platform.ndcfacades.ndc.ContactInformationType;
import de.hybris.platform.ndcfacades.ndc.EmailAddressType;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem;
import de.hybris.platform.ndcfacades.ndc.OrderRequestType.Offer.OfferItem.ServiceSelection;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.order.NDCOrderEntryFacade;
import de.hybris.platform.ndcfacades.strategies.AddAncillariesToOrderRestrictionStrategy;
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
	private static final String SPACE_SEPARATOR = " ";

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

		final List<TravellerModel> travellers = getTravellers(
				orderCreateRQ.getQuery().getDataLists().getPassengerList().getPassenger(), order.getCode());

		final Map<String, List<TransportOfferingModel>> transportOfferings = new HashMap<>();

		for (final Offer offer : orderCreateRQ.getQuery().getOrder().getOffer())
		{
			final Map<String, List<OfferItem>> offerItemIDMap = offer.getOfferItem().stream().collect(Collectors.groupingBy(OfferItem::getOfferItemID));
			for (final Map.Entry<String, List<OfferItem>> offerItemEntry : offerItemIDMap.entrySet())
			{
				final NDCOfferItemId ndcOfferItemId = getNdcOfferItemIdResolver().getNDCOfferItemIdFromString(offerItemEntry.getKey());
				final List<TravellerModel> selectedTravellers = getOfferTraveller(offerItemEntry.getValue(), travellers);
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

				final List<OfferItem> offerItemsSelectedService = offerItemEntry.getValue().stream()
						.filter(offerItem -> CollectionUtils.isNotEmpty(offerItem.getServiceSelection())).collect(Collectors.toList());
				for (final OfferItem orderItem : offerItemsSelectedService)
				{
					createUnbundledAncillariesOrderEntry(order, selectedTravellers, ndcOfferItemId, orderItem,
							transportOfferings.get(String.valueOf(ndcOfferItemId.getOriginDestinationRefNumber())));
				}

				final List<OfferItem> offerItemsSelectedSeat = offerItemEntry.getValue().stream()
						.filter(offerItem -> Objects.nonNull(offerItem.getSeatSelection())).collect(Collectors.toList());
				for (final OfferItem offerItem : offerItemsSelectedSeat)
				{
					createAccommodationOrderEntry(order, travellers, offerItem,transportOfferings, ndcOfferItemId);
				}
			}
		}
		getCalculationService().calculate(order);
	}

	/**
	 * Creates the accommodation order entry.
	 *
	 * @param order
	 *           the order
	 * @param travellers
	 *           the travellers
	 * @param offerItem
	 *           the offer item
	 * @param ndcOfferItemId
	 *           the ndc offer item id
	 * @throws NDCOrderException
	 */
	protected void createAccommodationOrderEntry(final OrderModel order, final List<TravellerModel> travellers,
			final OfferItem offerItem, final Map<String, List<TransportOfferingModel>> transportOfferings,
			final NDCOfferItemId ndcOfferItemId) throws NDCOrderException
	{
		final String transportOfferingCode = offerItem.getALaCarteSelection().getSegmentID();
		final List<TransportOfferingModel> orginDestinationTransportOfferings = transportOfferings
				.get(String.valueOf(ndcOfferItemId.getOriginDestinationRefNumber()));
		final TransportOfferingModel transportOffering = getRequiredTransportOffering(orginDestinationTransportOfferings,
				transportOfferingCode);
		final ConfiguredAccommodationModel accommodation;
		try
		{
			accommodation = getNdcAccommodationService().getConfiguredAccommodation(ndcOfferItemId, transportOffering,
					offerItem.getSeatSelection());
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

		if (!getNdcAccommodationService().checkIfAccommodationCanBeAdded(accommodation.getProduct(), offerItem.getSeatSelection(),
				ndcOfferItemId, transportOffering))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_UNAVAILABLE));
		}

		if (!getNdcAccommodationService().checkIfSeatValidForFareProd(accommodation.getProduct(), ndcOfferItemId))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID_BUNDLE));
		}

		final List<TravellerModel> selectedTravellers = getOfferTraveller(Collections.singletonList(offerItem), travellers);
		getNdcOrderService().populateOrderEntry(order, accommodation.getProduct(), null,
				NdcfacadesConstants.ASSOCIATED_SERVICE_BUNDLE_NUMBER,
				getNdcOfferItemIdResolver().ndcOfferItemIdToString(ndcOfferItemId), Collections.singletonList(transportOffering),
				selectedTravellers, ndcOfferItemId.getRouteCode(), ndcOfferItemId.getOriginDestinationRefNumber(),
				NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY);
		getNdcAccommodationService().createOrUpdateSelectedAccommodation(transportOffering, selectedTravellers, order,
				accommodation);
	}

	/**
	 * This method filters list of {@link TransportOfferingModel} and returns an instance which matches transport
	 * offering code.
	 *
	 * @param transportOfferings
	 *           the transport offerings
	 * @param transportOfferingCode
	 *           the list
	 * @return the required transport offering
	 * @throws NDCOrderException
	 *            the ndc order exception
	 */
	protected TransportOfferingModel getRequiredTransportOffering(final List<TransportOfferingModel> transportOfferings,
			final String transportOfferingCode) throws NDCOrderException
	{
		return transportOfferings.stream()
				.filter(transportOffering -> StringUtils.equals(transportOffering.getCode(), transportOfferingCode)).findFirst()
				.orElseThrow(() -> new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.GENERIC_ERROR)));

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
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void createUnbundledAncillariesOrderEntry(final OrderModel order, final List<TravellerModel> travellers,
			final NDCOfferItemId ndcOfferItemId, final OfferItem offerItem, final List<TransportOfferingModel> transportOfferings)
			throws NDCOrderException
	{
		if (CollectionUtils.isEmpty(offerItem.getServiceSelection()))
		{
			return;
		}

		int offerItemServiceQuantity = NdcfacadesConstants.DEFAULT_ANCILLARY_QUANTITY;
		if(Objects.nonNull(offerItem.getALaCarteSelection()))
		{
			offerItemServiceQuantity = offerItem.getALaCarteSelection().getQuantity();
		}
		final List<TransportOfferingModel> associatedTransportOfferings = getAssociatedTransportOffering(
				offerItem.getALaCarteSelection().getSegmentID(), transportOfferings);


		for (final ServiceSelection serviceSelection : offerItem.getServiceSelection())
		{
			if (Objects.isNull(serviceSelection.getServiceDefinitionID()))
			{
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_SERVICE_ID));
			}

			final ProductModel ancillaryProduct;
			try
			{
				ancillaryProduct = getProductService().getProductForCode(serviceSelection.getServiceDefinitionID());
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.debug(e);
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_SERVICE_ID));
			}

			checkProductCategory(ancillaryProduct);

			final String productRestriction = getTravelRestrictionFacade().getAddToCartCriteria(ancillaryProduct.getCode());

			final List<TravellerModel> associatedTravellers = getOfferTraveller(Collections.singletonList(offerItem), travellers);
			for (int index = 0; index < offerItemServiceQuantity; index++)
			{
				getAddAncillariesToOrderRestrictionStrategyMap().get(productRestriction).addAncillary(order, associatedTravellers,
						ancillaryProduct, associatedTransportOfferings, offerItem.getOfferItemID(), ndcOfferItemId.getRouteCode(),
						ndcOfferItemId.getOriginDestinationRefNumber());
			}
		}
	}

	/**
	 * Checks that the product category is not among the restricted ones. Throws an exception otherwise. (i.e. Seat are
	 * accommodation products and needs to be included in a different element in the OrderCreateRQ
	 *
	 * @param ancillaryProduct
	 * 		the ancillary product
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void checkProductCategory(final ProductModel ancillaryProduct) throws NDCOrderException
	{
		if (Objects.nonNull(ancillaryProduct) && CollectionUtils.isNotEmpty(ancillaryProduct.getSupercategories())
				&& getCategoriesNotAllowed().stream().anyMatch(categoryNotAllowed -> ancillaryProduct.getSupercategories().stream()
						.anyMatch(superCategory -> StringUtils.equals(categoryNotAllowed, superCategory.getCode()))))
		{
			throw new NDCOrderException(ancillaryProduct.getName() + " cannot be added to the order");
		}
	}

	/**
	 * Returns the list of {@link TransportOfferingModel} that are associated to the specified {@link ServiceIDType}
	 * extracting them from the provided list of {@link TransportOfferingModel}
	 *
	 * @param string
	 *           the service id
	 * @param transportOfferings
	 *           the transport offerings
	 * @return associated transport offering
	 */
	protected List<TransportOfferingModel> getAssociatedTransportOffering(final String transportOfferingCode,
			final List<TransportOfferingModel> transportOfferings)
	{
		if (StringUtils.isEmpty(transportOfferingCode))
		{
			return transportOfferings;
		}
		return transportOfferings.stream()
				.filter(transportOffering -> StringUtils.equals(transportOfferingCode, transportOffering.getCode()))
				.collect(Collectors.toList());
	}

	/**
	 * Creates an {@link OrderEntryModel} per each {@link TravellerModel} per each bundle contained in the {@link OfferItem}
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

			if (Objects.nonNull(ancillaryChildBundle))
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
	protected List<TravellerModel> getOfferTraveller(final List<OfferItem> offerItems, final List<TravellerModel> travellers)
	{
		final List<PassengerType> offerItemPassengers = offerItems.stream()
				.flatMap(offerItem -> offerItem.getPassengerRefs().stream()).distinct().filter(PassengerType.class::isInstance)
				.map(PassengerType.class::cast).collect(Collectors.toList());

		return offerItemPassengers.stream()
				.map(passenger -> travellers.stream()
						.filter(traveller -> StringUtils.equals(traveller.getLabel(), passenger.getPassengerID())).findFirst())
				.map(Optional::get).collect(Collectors.toList());
	}

	/**
	 * Creates {@link TravellerModel} from the list of {@link Passenger} provided
	 *
	 * @param passengers
	 *           the passengers
	 * @param orderCode
	 * @return travellers
	 */
	protected List<TravellerModel> getTravellers(final List<PassengerType> passengers, final String orderCode)
	{
		final List<TravellerModel> travellers = new LinkedList<>();
		for (final PassengerType passenger : passengers)
		{
			final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
			final TravellerModel travellerModel = getTravellerService().createTraveller(TravellerType.PASSENGER.getCode(),
					getPassengerTypeModel(passenger.getPTC()).getCode(), passenger.getPassengerID(), travellers.size(), "", orderCode);

			populatePassengerInformation(passengerInformationModel, passenger);
			travellerModel.setInfo(passengerInformationModel);

			travellers.add(travellerModel);

			getModelService().save(passengerInformationModel);
			getModelService().save(travellerModel);
		}
		return travellers;
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
			final PassengerType passenger)
	{
		final PassengerTypeModel passengerType = getPassengerTypeModel(passenger.getPTC());

		if (Objects.nonNull(passenger.getIndividual()) && Objects.nonNull(passenger.getIndividual().getNameTitle()))
		{
			final TitleModel title = getUserService().getTitleForCode(passenger.getIndividual().getNameTitle());
			passengerInformationModel.setTitle(title);
		}

		if (Objects.nonNull(passenger.getIndividual().getGender().value()))
		{
			passengerInformationModel.setGender(passenger.getIndividual().getGender().value().toLowerCase());
		}

		if (Objects.nonNull(passenger.getContactInfoRef()) && passenger.getContactInfoRef() instanceof ContactInformationType)
		{
			final ContactInformationType contactInformationType = (ContactInformationType) passenger.getContactInfoRef();
			final Optional<EmailAddressType> optionalEmailAddress = contactInformationType.getEmailAddress().stream().findFirst();
			if (optionalEmailAddress.isPresent())
			{
				passengerInformationModel.setEmail(optionalEmailAddress.get().getEmailAddressValue());
			}
		}

		passengerInformationModel.setSurname(passenger.getIndividual().getSurname());
		passengerInformationModel.setPassengerType(passengerType);
		passengerInformationModel.setFirstName(String.join(SPACE_SEPARATOR, passenger.getIndividual().getGivenName()));
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
