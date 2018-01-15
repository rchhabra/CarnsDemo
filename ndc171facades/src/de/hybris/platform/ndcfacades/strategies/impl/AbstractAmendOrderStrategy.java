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
package de.hybris.platform.ndcfacades.strategies.impl;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.NDCOfferItemId;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.ItemIDType;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.order.NDCPaymentTransactionFacade;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AmendOrderValidationStrategy;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCAccommodationService;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.ndcservices.services.NDCTransportOfferingService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.accommodationmap.exception.AccommodationMapDataSetUpException;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * An abstract class to collect methods used across different amend order strategies
 */
public abstract class AbstractAmendOrderStrategy
{
	private static final Logger LOG = Logger.getLogger(AbstractAmendOrderStrategy.class);

	private ProductService productService;
	private TravelRestrictionFacade travelRestrictionFacade;
	private CommonI18NService commonI18NService;
	private TimeService timeService;
	private ModelService modelService;
	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
	private KeyGenerator orderCodeGenerator;
	private UserService userService;
	private CalculationService calculationService;
	private StoreSessionFacade storeSessionFacade;
	private ConfigurationService configurationService;
	private ReservationFacade reservationFacade;
	private TravellerService travellerService;
	private List<AmendOrderValidationStrategy> amendOrderValidationStrategyList;
	private TravelCommerceCheckoutService travelCommerceCheckoutService;
	private BookingService bookingService;

	private NDCPaymentTransactionFacade ndcPaymentTransactionFacade;
	private NDCOrderService ndcOrderService;
	private NDCTransportOfferingService ndcTransportOfferingService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private NDCAccommodationService ndcAccommodationService;

	/**
	 * Clones the order and manually copy the information related to the payment and billing address, billing time and
	 * order entries
	 *
	 * @param originalOrder
	 * @return
	 * @throws NDCOrderException
	 */
	protected OrderModel cloneOrder(final OrderModel originalOrder) throws NDCOrderException
	{
		final String currentCurrencyIsocode = getCommonI18NService().getCurrentCurrency().getIsocode();
		final String orderCurrency = originalOrder.getCurrency().getIsocode();
		if (!StringUtils.equalsIgnoreCase(currentCurrencyIsocode, orderCurrency))
		{
			forceCurrencyToOriginalOrderCurrency(orderCurrency);
		}

		final OrderModel clonedOrder = getCloneAbstractOrderStrategy().clone(null, null, originalOrder,
				getOrderCodeGenerator().generate().toString(), OrderModel.class, OrderEntryModel.class);

		clonedOrder.setOriginalOrder(originalOrder);
		clonedOrder.setUser(originalOrder.getUser());
		if (Objects.nonNull(originalOrder.getBillingTime()))
		{
			clonedOrder.setBillingTime(originalOrder.getBillingTime());
		}

		final Date currentTime = getTimeService().getCurrentTime();

		clonedOrder.setCreationtime(currentTime);
		clonedOrder.setDate(currentTime);
		if (Objects.nonNull(originalOrder.getPaymentAddress()) || Objects.nonNull(originalOrder.getDeliveryAddress()))
		{
			clonedOrder.setDeliveryAddress(getModelService().clone(Objects.nonNull(originalOrder.getDeliveryAddress())
					? originalOrder.getDeliveryAddress() : originalOrder.getPaymentAddress()));
		}
		if (Objects.nonNull(originalOrder.getPaymentAddress()))
		{
			clonedOrder.setPaymentAddress(getModelService().clone(originalOrder.getPaymentAddress()));
		}
		clonedOrder.setPaymentInfo(getModelService().clone(originalOrder.getPaymentInfo()));

		// If the modified order's payment info has been removed, the owner attribute will be null after clone.
		if (Objects.nonNull(clonedOrder.getPaymentAddress()) && Objects.nonNull(clonedOrder.getPaymentInfo())
				&& Objects.isNull(clonedOrder.getPaymentInfo().getBillingAddress().getOwner()))
		{
			clonedOrder.getPaymentInfo().setBillingAddress(clonedOrder.getPaymentAddress());
		}

		getModelService().save(clonedOrder);
		getModelService().detachAll();

		final Set<TravellerModel> travellers = getTravellerToClone(clonedOrder);

		final Map<TravellerModel, TravellerModel> clonedTravellerModelMap = cloneTravellers(travellers, clonedOrder.getCode());

		clonedOrder.getEntries().forEach(entry -> cloneTravelOrderEntryInfo(entry, clonedTravellerModelMap));

		final List<SelectedAccommodationModel> clonedSelectedAccommodationList = new LinkedList<>();
		clonedOrder.getSelectedAccommodations().forEach(selectedAccommodation -> cloneSelectedAccommodation(selectedAccommodation,
				clonedOrder, clonedTravellerModelMap, clonedSelectedAccommodationList));
		clonedOrder.setSelectedAccommodations(clonedSelectedAccommodationList);

		getModelService().saveAll();

		return clonedOrder;
	}

	protected void cloneTravelOrderEntryInfo(final AbstractOrderEntryModel entry,
			final Map<TravellerModel, TravellerModel> clonedTravellerModelMap)
	{
		if (OrderEntryType.TRANSPORT.equals(entry.getType()))
		{
			final TravelOrderEntryInfoModel clonedInfo = getModelService().clone(entry.getTravelOrderEntryInfo(),
					TravelOrderEntryInfoModel.class);

			cloneTravelOrderEntryInfoAttributes(clonedInfo, entry.getTravelOrderEntryInfo());

			final List<TravellerModel> newTravellers = clonedInfo.getTravellers().stream()
					.filter(clonedTravellerModelMap::containsKey).map(clonedTravellerModelMap::get).collect(Collectors.toList());
			clonedInfo.setTravellers(newTravellers);

			entry.setTravelOrderEntryInfo(clonedInfo);
		}
		entry.setAmendStatus(AmendStatus.SAME);
	}

	protected Set<TravellerModel> getTravellerToClone(final OrderModel clonedOrder)
	{
		return clonedOrder.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
						&& Objects.nonNull(entry.getTravelOrderEntryInfo().getTravellers()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(Collectors.toSet());
	}

	protected void cloneSelectedAccommodation(final SelectedAccommodationModel selectedAccommodation, final OrderModel clonedOrder,
			final Map<TravellerModel, TravellerModel> travellerModelTravellerModelMap,
			final List<SelectedAccommodationModel> clonedSelectedAccommodationList)
	{
		final SelectedAccommodationModel clonedSelectedAccommodation = getModelService().clone(selectedAccommodation,
				SelectedAccommodationModel.class);

		clonedSelectedAccommodation.setOrder(clonedOrder);
		clonedSelectedAccommodation.setTraveller(travellerModelTravellerModelMap.get(selectedAccommodation.getTraveller()));
		clonedSelectedAccommodationList.add(clonedSelectedAccommodation);
	}

	protected void cloneTravelOrderEntryInfoAttributes(final TravelOrderEntryInfoModel clonedInfo,
			final TravelOrderEntryInfoModel travelOrderEntryInfo)
	{
		if (Objects.nonNull(travelOrderEntryInfo.getSpecialRequestDetail()))
		{
			clonedInfo.setSpecialRequestDetail(
					getModelService().clone(travelOrderEntryInfo.getSpecialRequestDetail(), SpecialRequestDetailModel.class));
		}

		if (org.apache.commons.collections.CollectionUtils.isNotEmpty(travelOrderEntryInfo.getComments()))
		{
			final List<CommentModel> clonedComments = new LinkedList<>();
			travelOrderEntryInfo.getComments()
					.forEach(comment -> clonedComments.add(getModelService().clone(comment, CommentModel.class)));
			clonedInfo.setComments(clonedComments);
		}
	}

	protected Map<TravellerModel, TravellerModel> cloneTravellers(final Set<TravellerModel> travellers, final String cartCode)
			throws NDCOrderException
	{
		final Map<TravellerModel, TravellerModel> clonedTravellersMap = new HashMap<>();
		for (final TravellerModel traveller : travellers)
		{
			final TravellerModel clonedTraveller = getModelService().clone(traveller, TravellerModel.class);

			if (Objects.nonNull(traveller.getInfo()))
			{
				if (traveller.getInfo() instanceof PassengerInformationModel)
				{
					clonedTraveller.setInfo(getModelService().clone(traveller.getInfo(), PassengerInformationModel.class));
				}
				else
				{
					throw new NDCOrderException("Invalid Traveller present in the order.");
				}
			}

			if (Objects.nonNull(traveller.getSpecialRequestDetail()))
			{
				clonedTraveller.setSpecialRequestDetail(
						getModelService().clone(traveller.getSpecialRequestDetail(), SpecialRequestDetailModel.class));
			}

			if (org.apache.commons.collections.CollectionUtils.isNotEmpty(traveller.getTravellerPreference()))
			{
				final Collection<TravellerPreferenceModel> clonedTravellerPreference = new LinkedList<>();
				traveller.getTravellerPreference().forEach(
						travellerPreferenceModel -> clonedTravellerPreference.add(getModelService().clone(travellerPreferenceModel)));
				clonedTraveller.setTravellerPreference(clonedTravellerPreference);
			}

			clonedTraveller.setTravelOrderEntryInfo(Collections.emptyList());

			clonedTraveller.setVersionID(cartCode);

			clonedTravellersMap.put(traveller, clonedTraveller);
		}
		return clonedTravellersMap;
	}

	/**
	 * Returns the list of {@link TransportOfferingModel} that are associated to the specified {@link ServiceIDType}
	 * extracting them from the provided list of {@link TransportOfferingModel}. Throw {@link NDCOrderException} if the
	 * specified transport offering codes are not part of the {@link NDCOfferItemId}
	 *
	 * @param serviceID
	 * @param transportOfferingCodes
	 * @param ndcOfferItemId
	 * @return
	 */
	protected List<String> extractAssociatedTransportOffering(final ServiceIDType serviceID,
			final List<String> transportOfferingCodes, final NDCOfferItemId ndcOfferItemId) throws NDCOrderException
	{
		final List<String> flightSegments = serviceID.getRefs().stream().filter(entry -> entry instanceof ListOfFlightSegmentType)
				.map(entry -> ((ListOfFlightSegmentType) entry).getSegmentKey()).collect(Collectors.toList());

		final Set<String> offerItemTransportOfferings = ndcOfferItemId.getBundleList().stream()
				.flatMap(entry -> entry.getTransportOfferings().stream()).collect(Collectors.toSet());

		if (!offerItemTransportOfferings.containsAll(flightSegments))
		{
			throw new NDCOrderException("Invalid flight segment specified for the selected ancillary(ies)");
		}

		return transportOfferingCodes.stream().filter(flightSegments::contains).collect(Collectors.toList());
	}

	/**
	 * Returns a list of {@link PassengerType} extracted from the provided {@link OrderItemRepriceType.OrderItem}
	 *
	 * @param orderItem
	 * @return
	 */
	protected List<PassengerType> getPassengersFromOrderItem(final OrderItemRepriceType.OrderItem orderItem)
	{
		return orderItem.getAssociations().getPassengers().getPassengerReferences().stream().filter(PassengerType.class::isInstance)
				.map(PassengerType.class::cast).collect(Collectors.toList());
	}

	/**
	 * Calculates the order total, in case of error throws an {@link NDCOrderException} and deletes the order
	 *
	 * @param amendmentOrder
	 */
	protected void calculateOrderTotal(final OrderModel amendmentOrder) throws NDCOrderException
	{
		try
		{
			getCalculationService().calculate(amendmentOrder);
		}
		catch (final CalculationException e)
		{
			removeOrder(amendmentOrder);
			LOG.error(e.getMessage(), e);
			throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.GENERIC_ERROR));
		}
	}

	/**
	 * Checks if the specified {@link ItemIDType} are valid for the current {@link OrderModel}, if not throws
	 * {@link NDCOrderException}
	 *
	 * @param orderChangeRQ
	 * @param originalOrder
	 * @return
	 */
	protected void validateOrderItems(final OrderChangeRQ orderChangeRQ, final OrderModel originalOrder) throws NDCOrderException
	{
		final Set<String> offerItemIds = originalOrder.getEntries().stream().map(AbstractOrderEntryModel::getNdcOfferItemID)
				.collect(Collectors.toSet());

		if (!orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem().stream()
				.allMatch(orderItem -> offerItemIds.contains(orderItem.getOrderItemID().getValue())))
		{
			throw new NDCOrderException("Invalid OrderItemID(s) provided");
		}
	}

	/**
	 * Checks if at least one order entry associated to the {@link TravellerData} is in active status for all the
	 * {@link TravellerModel} provided if not throws {@link NDCOrderException}
	 *
	 * @param order
	 * @param travellers
	 * @return
	 */
	protected void validateTravellers(final OrderModel order, final List<TravellerModel> travellers) throws NDCOrderException
	{
		for (final TravellerModel traveller : travellers)
		{
			if (!isActiveTraveller(order, traveller))
			{
				throw new NDCOrderException("Invalid passenger(s) provided");
			}
		}
	}

	/**
	 * Returns a list of {@link TravellerModel} based on the profile {@link ProfileID} that correspond to the uid. If the
	 * specified {@link ProfileID} does not match any {@link TravellerModel} or the {@link TravellerModel} does not
	 * belong to the specified order, an {@link NDCOrderException} is thrown
	 *
	 * @param passengers
	 * @param amendedOrder
	 * @return
	 * @throws NDCOrderException
	 */
	protected List<TravellerModel> getTravellersFromPassengers(final List<PassengerType> passengers, final OrderModel amendedOrder)
			throws NDCOrderException
	{
		final List<TravellerModel> requestedTravellers = new LinkedList<>();
		final Set<TravellerModel> travellerModels = amendedOrder.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
						&& Objects.nonNull(entry.getTravelOrderEntryInfo().getTravellers()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(Collectors.toSet());

		try
		{
			for (final PassengerType passenger : passengers)
			{
				final TravellerModel traveller = getTravellerService().getExistingTraveller(passenger.getProfileID(),
						amendedOrder.getCode());
				if (!travellerModels.contains(traveller))
				{
					throw new NDCOrderException("Invalid ProfileID provided");
				}
				requestedTravellers.add(traveller);
			}
		}
		catch (final ModelNotFoundException e)
		{
			LOG.debug(e);
			throw new NDCOrderException("Invalid ProfileID provided");
		}

		return requestedTravellers;
	}

	/**
	 * Creates the refund payment transaction only if the {@link OrderModel} does not contain any PAY_LATER transaction
	 *
	 * @param order
	 * @param entries
	 * @return
	 */
	protected Boolean createRefundPaymentTransaction(final OrderModel order, final List<AbstractOrderEntryModel> entries)
	{
		return getTravelCommerceCheckoutService().createRefundPaymentTransactionEntries(order, entries);
	}

	/**
	 * Creates a {@link PriceData} with the amount that needs to be paid after the amendment at the {@link OrderModel}
	 *
	 * @param order
	 * @return
	 */
	protected PriceData getTotalToPay(final OrderModel order)
	{
		final ReservationData reservation = getReservationFacade().getReservationData(order);
		return reservation.getTotalToPay();
	}

	/**
	 * Changes currency so that amendment is done in the same currency as original order
	 *
	 * @param isocode
	 */
	protected void forceCurrencyToOriginalOrderCurrency(final String isocode)
	{
		getStoreSessionFacade().setCurrentCurrency(isocode);
		final UserModel user = getUserService().getCurrentUser();
		user.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getModelService().save(user);
	}

	/**
	 * Checks if the order has already been saved and, in case, deletes it.
	 *
	 * @param order
	 */
	protected void removeOrder(final OrderModel order)
	{
		if (!getModelService().isNew(order))
		{
			getModelService().refresh(order);
			if (CollectionUtils.isNotEmpty(order.getSelectedAccommodations()))
			{
				getModelService().removeAll(order.getSelectedAccommodations());
			}
			getModelService().remove(order);
		}
	}

	/**
	 * Checks if at least one order entry associated to the {@link TravellerData} is in active status
	 *
	 * @param order
	 * @param traveller
	 * @return
	 */
	protected boolean isActiveTraveller(final OrderModel order, final TravellerModel traveller)
	{
		final List<AbstractOrderEntryModel> travellerEntries = order.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
						&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1 && entry.getTravelOrderEntryInfo()
								.getTravellers().iterator().next().getLabel().equalsIgnoreCase(traveller.getLabel()))
				.collect(Collectors.toList());

		return travellerEntries.stream().anyMatch(AbstractOrderEntryModel::getActive);
	}

	/**
	 * Calculates the order total and the total to pay. If a payment is needed returns the {@link OrderModel} with the
	 * new payment transaction associate to the provided {@link AbstractOrderEntryModel}
	 *
	 * @param amendmentOrder
	 * @param orderEntries
	 * @throws NDCOrderException
	 */
	protected void createPaymentTransaction(final OrderModel amendmentOrder, final List<AbstractOrderEntryModel> orderEntries)
			throws NDCOrderException
	{
		getModelService().refresh(amendmentOrder);

		calculateOrderTotal(amendmentOrder);

		final PriceData totalToPay = getTotalToPay(amendmentOrder);

		if (totalToPay.getValue().doubleValue() < 0d)
		{
			LOG.error("New ancillary(ies) has been added, the total to pay should be grater than zero");
			throw new NDCOrderException("Error during the passenger cancellation");
		}

		getNdcPaymentTransactionFacade().createPaymentTransaction(totalToPay.getValue().abs(), amendmentOrder, orderEntries);
	}


	/**
	 * Return the {@link ConfiguredAccommodationModel} based on the provided parameters. Thros {@link NDCOrderException}
	 * if the value provided are invalid
	 *
	 * @param ndcOfferItemId
	 * @param transportOffering
	 * @param seatNum
	 * @return
	 */
	protected ConfiguredAccommodationModel getConfiguredAccommodation(final NDCOfferItemId ndcOfferItemId,
			final TransportOfferingModel transportOffering, final String seatNum) throws NDCOrderException
	{
		final ConfiguredAccommodationModel configuredAccommodation;
		try
		{
			configuredAccommodation = getNdcAccommodationService().getConfiguredAccommodation(ndcOfferItemId, transportOffering,
					seatNum);
		}
		catch (final AccommodationMapDataSetUpException e)
		{
			LOG.debug(e);
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID));
		}

		if (Objects.isNull(configuredAccommodation))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_CREATE_SEAT_INVALID));
		}

		return configuredAccommodation;
	}

	/**
	 * Checks if the specified accommodation can be removed and, in case, it removes it from the {@link OrderModel}
	 * provided. It fills a list of {@link AbstractOrderEntryModel} with order entries that has been modified.
	 *
	 * @param accommodation
	 * @param travellers
	 * @param amendmentOrder
	 * @param transportOffering
	 * @param seatNum
	 * @param orderEntries
	 * @throws NDCOrderException
	 */
	protected void removeAccommodation(final ConfiguredAccommodationModel accommodation, final List<TravellerModel> travellers,
			final OrderModel amendmentOrder, final TransportOfferingModel transportOffering,
			final List<AbstractOrderEntryModel> orderEntries) throws NDCOrderException
	{
		if (Objects.isNull(accommodation) || Objects.isNull(accommodation.getProduct()))
		{
			return;
		}

		final List<AbstractOrderEntryModel> accommodationEntries = amendmentOrder.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()) && entry.getActive()
						&& sameTravellers(entry.getTravelOrderEntryInfo().getTravellers(), travellers)
						&& sameTransportOffering(entry.getTravelOrderEntryInfo().getTransportOfferings(), transportOffering)
						&& StringUtils.equals(accommodation.getProduct().getCode(), entry.getProduct().getCode()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accommodationEntries))
		{
			throw new NDCOrderException("Unable to remove " + accommodation.getIdentifier() + ". Seat is not present in the order.");
		}

		for (final AbstractOrderEntryModel entry : accommodationEntries)
		{
			entry.setActive(Boolean.FALSE);
			entry.setQuantity(0L);
			entry.setAmendStatus(AmendStatus.CHANGED);
			getModelService().save(entry);
			orderEntries.add(entry);
		}
		getNdcAccommodationService().removeSelectedAccommodation(amendmentOrder, transportOffering, travellers.get(0));
		getModelService().save(amendmentOrder);
		getModelService().refresh(amendmentOrder);
	}

	/**
	 * Gets the transport offering.
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @return the transport offering
	 * @throws NDCOrderException
	 *            the NDC order exception
	 */
	protected TransportOfferingModel getTransportOffering(final String transportOfferingCode) throws NDCOrderException
	{
		TransportOfferingModel transportOffering;
		try
		{
			transportOffering = getNdcTransportOfferingService().getTransportOffering(transportOfferingCode);

		}
		catch (final ModelNotFoundException exception)
		{
			LOG.warn("Error while fetching TransportOfferingModel for code :" + transportOfferingCode
					+ " , removing order and throwing the exception");
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.ORDER_AMEND_INVALID_TRANSPORT_OFFERING));
		}
		return transportOffering;
	}

	/**
	 * Checks if the list of {@link TransportOfferingModel} extracted from the {@link AbstractOrderEntryModel} are the
	 * same as the ones associated to a certain ancillaries that needs to be removed
	 *
	 * @param transportOfferings
	 * @param transportOffering
	 * @return
	 */
	protected boolean sameTransportOffering(final Collection<TransportOfferingModel> transportOfferings,
			final TransportOfferingModel transportOffering)
	{
		return transportOfferings.stream().allMatch(transportOfferingModel -> StringUtils
				.equalsIgnoreCase(transportOfferingModel.getCode(), transportOffering.getCode()));
	}

	/**
	 * Checks if the list of {@link TravellerModel} extracted from the {@link AbstractOrderEntryModel} are the same as
	 * the ones associated to a certain ancillaries that needs to be removed
	 *
	 * @param entryTravellers
	 * @param travellers
	 * @return
	 */
	protected boolean sameTravellers(final Collection<TravellerModel> entryTravellers, final List<TravellerModel> travellers)
	{
		final List<String> uidTravellers = travellers.stream().map(TravellerModel::getUid).collect(Collectors.toList());
		return entryTravellers.stream().allMatch(travellerModel -> uidTravellers.contains(travellerModel.getUid()));
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CloneAbstractOrderStrategy getCloneAbstractOrderStrategy()
	{
		return cloneAbstractOrderStrategy;
	}

	@Required
	public void setCloneAbstractOrderStrategy(final CloneAbstractOrderStrategy cloneAbstractOrderStrategy)
	{
		this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected KeyGenerator getOrderCodeGenerator()
	{
		return orderCodeGenerator;
	}

	@Required
	public void setOrderCodeGenerator(final KeyGenerator orderCodeGenerator)
	{
		this.orderCodeGenerator = orderCodeGenerator;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	protected StoreSessionFacade getStoreSessionFacade()
	{
		return storeSessionFacade;
	}

	@Required
	public void setStoreSessionFacade(final StoreSessionFacade storeSessionFacade)
	{
		this.storeSessionFacade = storeSessionFacade;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	@Required
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	@Required
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}

	protected TravelCommerceCheckoutService getTravelCommerceCheckoutService()
	{
		return travelCommerceCheckoutService;
	}

	@Required
	public void setTravelCommerceCheckoutService(final TravelCommerceCheckoutService travelCommerceCheckoutService)
	{
		this.travelCommerceCheckoutService = travelCommerceCheckoutService;
	}

	protected NDCPaymentTransactionFacade getNdcPaymentTransactionFacade()
	{
		return ndcPaymentTransactionFacade;
	}

	@Required
	public void setNdcPaymentTransactionFacade(final NDCPaymentTransactionFacade ndcPaymentTransactionFacade)
	{
		this.ndcPaymentTransactionFacade = ndcPaymentTransactionFacade;
	}

	protected NDCOrderService getNdcOrderService()
	{
		return ndcOrderService;
	}

	@Required
	public void setNdcOrderService(final NDCOrderService ndcOrderService)
	{
		this.ndcOrderService = ndcOrderService;
	}

	protected NDCTransportOfferingService getNdcTransportOfferingService()
	{
		return ndcTransportOfferingService;
	}

	@Required
	public void setNdcTransportOfferingService(final NDCTransportOfferingService ndcTransportOfferingService)
	{
		this.ndcTransportOfferingService = ndcTransportOfferingService;
	}

	protected BookingService getBookingService()
	{
		return bookingService;
	}

	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	protected List<AmendOrderValidationStrategy> getAmendOrderValidationStrategyList()
	{
		return amendOrderValidationStrategyList;
	}

	@Required
	public void setAmendOrderValidationStrategyList(final List<AmendOrderValidationStrategy> amendOrderValidationStrategyList)
	{
		this.amendOrderValidationStrategyList = amendOrderValidationStrategyList;
	}

	protected NDCAccommodationService getNdcAccommodationService()
	{
		return ndcAccommodationService;
	}

	@Required
	public void setNdcAccommodationService(final NDCAccommodationService ndcAccommodationService)
	{
		this.ndcAccommodationService = ndcAccommodationService;
	}
}
