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

import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.travel.BookingActionData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.enums.ActionTypeOption;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType;
import de.hybris.platform.ndcfacades.ndc.BookingReferenceType.OtherID;
import de.hybris.platform.ndcfacades.ndc.Contacts;
import de.hybris.platform.ndcfacades.ndc.MsgPartiesType;
import de.hybris.platform.ndcfacades.ndc.OrdCreateMetadataType.Other.OtherMetadata;
import de.hybris.platform.ndcfacades.ndc.OrderChangeRQ;
import de.hybris.platform.ndcfacades.ndc.OrderCreateRQ;
import de.hybris.platform.ndcfacades.ndc.OrderItemRepriceType.OrderItem;
import de.hybris.platform.ndcfacades.ndc.OrderRetrieveRQ;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.order.NDCOrderEntryFacade;
import de.hybris.platform.ndcfacades.order.NDCOrderFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentAddressFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentInfoFacade;
import de.hybris.platform.ndcfacades.order.NDCPaymentTransactionFacade;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcfacades.strategies.AmendOrderOfferFilterStrategy;
import de.hybris.platform.ndcfacades.strategies.AmendOrderStrategy;
import de.hybris.platform.ndcservices.enums.NDCActionType;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionEnabledEvaluatorStrategy;
import de.hybris.platform.travelfacades.booking.action.strategies.BookingActionStrategy;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of the {@link NDCOrderFacade}
 */
public class DefaultNDCOrderFacade extends AbstractNDCOrderFacade implements NDCOrderFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultNDCOrderFacade.class);

	private CustomerAccountService customerAccountService;
	private TravelCommerceStockService commerceStockService;
	private CommonI18NService commonI18NService;
	private CalculationService calculationService;
	private StoreSessionFacade storeSessionFacade;
	private BookingFacade bookingFacade;

	private NDCOrderService ndcOrderService;
	private NDCOrderEntryFacade ndcOrderEntryFacade;
	private NDCPaymentAddressFacade ndcPaymentAddressFacade;
	private NDCPaymentInfoFacade ndcPaymentInfoFacade;
	private NDCPaymentTransactionFacade ndcPaymentTransactionFacade;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;
	private Map<NDCActionType, AmendOrderStrategy> amendOrderStrategyMap;

	private Converter<OrderModel, OrderViewRS> ndcOrderViewRSConverter;

	private Map<String, ActionTypeOption> orderChangeActionToBookingActionMapping;
	private Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap;
	private Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap;

	private List<AmendOrderOfferFilterStrategy> amendOrderOfferFilterStrategyList;

	@Override
	public OrderViewRS orderCreate(final OrderCreateRQ orderCreateRQ) throws NDCOrderException
	{
		final OrderViewRS orderViewRS = new OrderViewRS();
		final OrderModel order = new OrderModel();
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		try
		{
			order.setUser(currentUser);

			setPaymentUser(order, currentUser);

			final String currencyMetaDatakey = getCurrencyFromMetadata(orderCreateRQ);
			getStoreSessionFacade().setCurrentCurrency(currencyMetaDatakey);
			getNdcOrderService().setOrderBasicInformation(order, currencyMetaDatakey);
			getNdcOrderEntryFacade().createOrderEntries(orderCreateRQ, order);
			getNdcOrderService().setPNRAsOrderCode(order);

			if (DefaultNDCPaymentTransactionFacade.isPayLater(orderCreateRQ))
			{
				getNdcPaymentTransactionFacade().createPayLaterTransaction(orderCreateRQ, order);
			}
			else
			{
				getNdcPaymentAddressFacade().createPaymentAddress(orderCreateRQ, order);
				getNdcPaymentInfoFacade().createPaymentInfo(orderCreateRQ, order);
			}

			getNdcPaymentTransactionFacade().createPaymentTransaction(orderCreateRQ, order);

			getModelService().save(order);

			getCalculationService().recalculate(order);

			validatePaymentAmount(order, orderCreateRQ);

			getNdcOrderViewRSConverter().convert(order, orderViewRS);

			getCommerceStockService().reserve(order);

			startNDCOrderProcess(order);
		}
		catch (final NDCOrderException | ConversionException e)
		{
			removeOrder(order);
			LOG.warn(e.getMessage());
			LOG.debug(e);
			throw e;
		}
		catch (final InsufficientStockLevelException e)
		{
			removeOrder(order);
			LOG.warn(NdcfacadesConstants.INSUFFICIENT_STOCK_LEVEL);
			LOG.debug(e);
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INSUFFICIENT_STOCK_LEVEL));
		}
		catch (final Exception e)
		{
			removeOrder(order);
			LOG.warn(e.getMessage());
			LOG.debug(e);
			throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.GENERIC_ERROR));
		}

		return orderViewRS;
	}

	@Override
	public OrderViewRS retrieveOrder(final OrderRetrieveRQ orderRetrieveRQ) throws NDCOrderException
	{
		final BookingReferenceType bookingReference = orderRetrieveRQ.getQuery().getFilters().getBookingReferences()
				.getBookingReference().stream().findFirst().get();

		final String orderCode = bookingReference.getID();
		final OtherID otherID = bookingReference.getOtherID();
		final String surname = otherID.getValue();

		final OrderModel order = getOrderByBookingReferenceLastName(orderCode, surname);

		final OrderViewRS orderViewRS = new OrderViewRS();
		getNdcOrderViewRSConverter().convert(order, orderViewRS);

		return orderViewRS;
	}

	@Override
	public OrderViewRS payOrder(final OrderCreateRQ orderCreateRQ) throws NDCOrderException
	{
		final OrderModel order = getOrderByBookingReference(
				orderCreateRQ.getQuery().getBookingReferences().getBookingReference().get(0).getOtherID().getValue());

		try
		{
			if (Objects.isNull(order))
			{
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_MANAGE_BOOKING_INFORMATION));
			}

			checkPayLaterTransactions(order);

			getNdcPaymentAddressFacade().createPaymentAddress(orderCreateRQ, order);
			getNdcPaymentInfoFacade().createPaymentInfo(orderCreateRQ, order);
			getNdcPaymentTransactionFacade().createPaymentTransaction(orderCreateRQ, order);

			getCalculationService().recalculate(order);

			validatePaymentAmount(order, orderCreateRQ);

			startNDCOrderProcess(order);

		}
		catch (final CalculationException e)
		{
			LOG.error(e.getMessage());
			LOG.debug(e);
			throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.GENERIC_ERROR));
		}

		final OrderViewRS orderViewRS = new OrderViewRS();

		getNdcOrderViewRSConverter().convert(order, orderViewRS);

		return orderViewRS;
	}

	@Override
	public OrderViewRS changeOrder(final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		final String bookingReference = orderChangeRQ.getQuery().getOrder().getOrderID().getValue();
		OrderModel order = getOrderByBookingReference(bookingReference);
		final UserModel currentUser = getUserService().getCurrentUser();
		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		final NDCActionType ndcActionType = NDCActionType.valueOf(actionType);
		final UserModel orderUser = order.getUser();

		if (NDCActionType.REMOVE_PASSENGER.equals(ndcActionType) && !StringUtils.equals(orderUser.getUid(), currentUser.getUid()))
		{
				throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.MISMATCH_ORDER_USER));
		}

		if (isPayLater(order))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.ORDER_NOT_PAYED));
		}

		final List<OrderItem> orderItems = orderChangeRQ.getQuery().getOrder().getOrderItems().getOrderItem();
		final Set<PassengerType> passengers = orderItems.stream()
				.flatMap(orderItem -> orderItem.getAssociations().getPassengers().getPassengerReferences().stream())
				.filter(PassengerType.class::isInstance).map(PassengerType.class::cast).collect(Collectors.toSet());

		final ReservationData reservationData = getReservationFacade().getReservationData(order);
		final Set<TravellerData> travellers = reservationData.getReservationItems().stream()
				.flatMap(reservationItem -> reservationItem.getReservationItinerary().getTravellers().stream())
				.collect(Collectors.toSet());

		if (!allAssociatedPassengerExistsInBooking(passengers, travellers))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.INVALID_PASSENGER));
		}

		final ActionTypeOption actionTypeOption = getOrderChangeActionToBookingActionMapping().get(actionType);
		final List<BookingActionData> bookingActionDataList = createBookingActionDataList(actionTypeOption, reservationData);
		final List<BookingActionEnabledEvaluatorStrategy> bookingActionEnabledEvaluatorStrategies = getBookingActionEnabledCalculationStrategiesMap()
				.get(actionTypeOption);

		if (NDCActionType.REMOVE_PASSENGER.equals(ndcActionType))
		{
			if (!isValidActionForAssociatedPassengers(reservationData, passengers, bookingActionDataList,
					bookingActionEnabledEvaluatorStrategies))
			{
				throw new NDCOrderException(getConfigurationService().getConfiguration()
						.getString(NdcfacadesConstants.INVALID_ACTION_FOR_PASSENGER));
			}
		}
		else
		{
			if (!isValidAction(reservationData, bookingActionDataList, bookingActionEnabledEvaluatorStrategies))
			{
				throw new NDCOrderException(getConfigurationService().getConfiguration()
						.getString(NdcfacadesConstants.IMPOSSIBLE_TO_PERFORM_SPECIFIED_ACTION));
			}
		}

		if (!isValidOfferItem(order, orderItems, travellers))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.IMPOSSIBLE_TO_PERFORM_SPECIFIED_ACTION));
		}

		order = executeChangeOrderAction(order, orderChangeRQ, ndcActionType);
		final OrderViewRS orderViewRS = new OrderViewRS();
		getNdcOrderViewRSConverter().convert(order, orderViewRS);

		return orderViewRS;
	}

	/**
	 * Checks if the order contains a PAY LATER transaction.
	 *
	 * @param order
	 *           the order
	 *
	 * @return boolean
	 */
	protected boolean isPayLater(final OrderModel order)
	{
		final List<PaymentTransactionModel> paymentTransactions = order.getPaymentTransactions();

		return paymentTransactions.stream().flatMap(paymentTransactionModel -> paymentTransactionModel.getEntries().stream())
				.anyMatch(paymentTransactionEntryModels -> PaymentTransactionType.PAY_LATER
						.equals(paymentTransactionEntryModels.getType()));
	}

	/**
	 * All associated passenger exists in booking.
	 *
	 * @param passengers
	 *           the passengers
	 * @param travellers
	 *           the travellers
	 * @return true, if successful
	 */
	protected boolean allAssociatedPassengerExistsInBooking(final Set<PassengerType> passengers,
			final Set<TravellerData> travellers)
	{
		for(final PassengerType passenger:passengers)
		{
			final boolean passengerExists = travellers.stream()
					.anyMatch(traveller -> StringUtils.equals(traveller.getUid(), passenger.getProfileID()));
			if (!passengerExists)
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Checks through a list of strategies if the specified action can be performed on the reservation data.
	 *
	 * @param reservationData
	 *           the booking reference
	 * @param actionType
	 *           the action type
	 * @param passengers
	 *           the passengers
	 * @param bookingActionEnabledEvaluatorStrategies
	 * @param bookingActionDataList
	 * @return boolean
	 */
	protected boolean isValidActionForAssociatedPassengers(final ReservationData reservationData,
			final Set<PassengerType> passengers, final List<BookingActionData> bookingActionDataList,
			final List<BookingActionEnabledEvaluatorStrategy> bookingActionEnabledEvaluatorStrategies)
	{
		for (final BookingActionEnabledEvaluatorStrategy strategy : bookingActionEnabledEvaluatorStrategies)
		{
			strategy.applyStrategy(bookingActionDataList, reservationData);
		}

		final List<BookingActionData> filteredBookingActionDatas = new ArrayList<>();
		for (final BookingActionData bookingActionData : bookingActionDataList)
		{
			final boolean isValidBookingActionData = passengers.stream()
					.anyMatch(passenger -> StringUtils.equals(bookingActionData.getTraveller().getUid(), passenger.getProfileID()));
			if (isValidBookingActionData)
			{
				filteredBookingActionDatas.add(bookingActionData);
			}
		}

		return CollectionUtils.isEmpty(filteredBookingActionDatas) ? false
				: filteredBookingActionDatas.stream().allMatch(BookingActionData::isEnabled);
	}

	/**
	 * Checks through a list of strategies if the specified action can be performed on the reservation data.
	 *
	 * @param reservationData
	 *           the reservation data
	 * @param bookingActionEnabledEvaluatorStrategies
	 * @param bookingActionDataList2
	 * @param actionType
	 *           the action type
	 * @return true, if is valid action
	 */
	protected boolean isValidAction(final ReservationData reservationData, final List<BookingActionData> bookingActionDataList,
			final List<BookingActionEnabledEvaluatorStrategy> bookingActionEnabledEvaluatorStrategies)
	{
		for (final BookingActionEnabledEvaluatorStrategy strategy : bookingActionEnabledEvaluatorStrategies)
		{
			strategy.applyStrategy(bookingActionDataList, reservationData);
		}

		return bookingActionDataList.stream().allMatch(BookingActionData::isEnabled);
	}

	/**
	 * Checks through a list of strategies if the specified action can be performed on the order that is identified by
	 * the specified booking reference taking in consideration the passengers and the transport offering for which the
	 * amendment has been requested.
	 *
	 * @param orderModel
	 *           the order model
	 * @param orderItems
	 *           the order items
	 * @param travellers
	 * @return true, if is valid offer item
	 * @throws NDCOrderException
	 *            the NDC order exception
	 */
	protected boolean isValidOfferItem(final OrderModel orderModel, final List<OrderItem> orderItems,
			final Set<TravellerData> travellers) throws NDCOrderException
	{
		final Set<String> travellerUIDList = travellers.stream().map(TravellerData::getUid).collect(Collectors.toSet());

		for (final OrderItem orderItem : orderItems)
		{
			final List<String> passengerUIDs = orderItem.getAssociations().getPassengers().getPassengerReferences().stream()
					.filter(PassengerType.class::isInstance).map(PassengerType.class::cast).map(PassengerType::getProfileID)
					.collect(Collectors.toList());

			final List<TransportOfferingModel> transportOfferings = getNdcOfferItemIdResolver()
					.getTransportOfferingFromNDCOfferItemId(orderItem.getOrderItemID().getValue());

			final List<String> travellersUIDList = travellerUIDList.stream().filter(passengerUIDs::contains)
					.collect(Collectors.toList());

			for (final AmendOrderOfferFilterStrategy amendOrderOfferFilterStrategy : getAmendOrderOfferFilterStrategyList())
			{
				if (!amendOrderOfferFilterStrategy.filterOffer(orderModel, transportOfferings, travellersUIDList))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Selects with action should be execute depending on the Action specified in the {@link OrderChangeRQ}
	 *
	 * @param order
	 *           the order
	 * @param orderChangeRQ
	 *           the order change rq
	 * @param ndcActionType
	 *
	 * @return the order model
	 * @throws NDCOrderException
	 *            the ndc order exception
	 */
	protected OrderModel executeChangeOrderAction(final OrderModel order, final OrderChangeRQ orderChangeRQ,
			final NDCActionType ndcActionType) throws NDCOrderException
	{
		final OrderModel amendedOrder;

		if (!getAmendOrderStrategyMap().containsKey(ndcActionType))
		{
			throw new NDCOrderException(NdcfacadesConstants.INVALID_ACTION);
		}

		beforeOrderAmendment(orderChangeRQ);
		amendedOrder = getAmendOrderStrategyMap().get(ndcActionType).amendOrder(order, orderChangeRQ);

		try
		{
			afterOrderAmendment(amendedOrder, orderChangeRQ);
			beforeStartOrderProcess(amendedOrder, order);
			startNDCOrderProcess(amendedOrder);
		}
		catch (final Exception e)
		{
			LOG.warn("Error occurred, removing order and throwing again the exception");
			removeOrder(amendedOrder);
			throw e;
		}

		return amendedOrder;
	}

	@Override
	public Boolean isAmendmentOrder(final OrderModel orderModel)
	{
		return Objects.nonNull(orderModel) ? (orderModel.getOriginalOrder() != null) : Boolean.FALSE;
	}

	@Override
	public String getOriginalOrderCode(final OrderModel orderModel)
	{
		final OrderModel order = orderModel.getOriginalOrder();
		return (Objects.isNull(order)) ? StringUtils.EMPTY : orderModel.getCode();
	}

	/**
	 * Returns the list of {@link BookingActionData} based on the {@link ActionTypeOption} and {@link ReservationData}
	 * provided
	 *
	 * @param actionType
	 *           the action type
	 * @param reservationData
	 *           the reservation data
	 *
	 * @return list
	 */
	protected List<BookingActionData> createBookingActionDataList(final ActionTypeOption actionType,
			final ReservationData reservationData)
	{
		final List<BookingActionData> bookingActionDataList = new ArrayList<>();
		final BookingActionStrategy strategy = getBookingActionStrategyMap().get(actionType);
		strategy.applyStrategy(bookingActionDataList, actionType, reservationData);
		return bookingActionDataList;
	}

	/**
	 * Actions that needs to be executed after the order amendment takes place
	 *
	 * @param amendedOrder
	 * 		the amended order
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void afterOrderAmendment(final OrderModel amendedOrder, final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		if (isAddAncillaries(orderChangeRQ))
		{
			validatePaymentAmount(amendedOrder, orderChangeRQ);
			getNdcPaymentInfoFacade().createPaymentInfo(orderChangeRQ, amendedOrder);
		}

		if (isAddAccommodation(orderChangeRQ, amendedOrder))
		{
			containsPaymentInformation(orderChangeRQ);
			validatePaymentAmount(amendedOrder, orderChangeRQ);
			getNdcPaymentInfoFacade().createPaymentInfo(orderChangeRQ, amendedOrder);
		}
	}

	/**
	 * Actions that needs to be executed before the order amendment takes place
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void beforeOrderAmendment(final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		if (isAddAncillaries(orderChangeRQ))
		{
			containsPaymentInformation(orderChangeRQ);
		}
	}

	/**
	 * Before start order process.
	 *
	 * @param order
	 * 		the order
	 * @param originalOrder
	 * 		the original order
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void beforeStartOrderProcess(final OrderModel order, final OrderModel originalOrder) throws NDCOrderException
	{
		try
		{
			getCommerceStockService().adjustStockReservationForAmmendment(order, originalOrder);
		}
		catch (final InsufficientStockLevelException e)
		{
			LOG.debug(e);
			removeOrder(order);
			throw new NDCOrderException("Insufficient stock level to perform the order change");
		}
		originalOrder.setStatus(OrderStatus.AMENDMENTINPROGRESS);
		getModelService().save(originalOrder);
	}

	/**
	 * Returns true if the action specified in the {@link OrderChangeRQ} is {@link NDCActionType#ADD_ANCILLARIES} otherwise false.
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @return boolean
	 */
	protected boolean isAddAncillaries(final OrderChangeRQ orderChangeRQ)
	{
		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		return NDCActionType.ADD_ANCILLARIES.getCode().equals(actionType);
	}

	/**
	 * Returns true if the action specified in the {@link OrderChangeRQ} is {@link NDCActionType#ADD_ACCOMMODATION} and the
	 * amount to pay is greater than zero. In otherwise false.
	 * The amount to pay can be zero in case there was a change of seat.
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 * @param amendedOrder
	 * 		the amended order
	 *
	 * @return boolean
	 */
	protected boolean isAddAccommodation(final OrderChangeRQ orderChangeRQ, final OrderModel amendedOrder)
	{
		final String actionType = orderChangeRQ.getQuery().getOrder().getActionType().getValue().toUpperCase();
		return NDCActionType.ADD_ACCOMMODATION.getCode().equals(actionType) && getTotalToPay(amendedOrder) > 0d;
	}

	/**
	 * Returns a double corresponding to the amount that needs to be paid after the amendment at the {@link OrderModel}
	 *
	 * @param order
	 * 		the order
	 *
	 * @return total to pay
	 */
	protected double getTotalToPay(final OrderModel order)
	{
		final ReservationData reservation = getReservationFacade().getReservationData(order);
		return reservation.getTotalToPay().getValue().doubleValue();
	}

	/**
	 * Checks if the order contains a PAY LATER transaction. Throws NDCOrderException in case it does not.
	 *
	 * @param order
	 * 		the order
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void checkPayLaterTransactions(final OrderModel order) throws NDCOrderException
	{
		if (!isPayLater(order))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.NO_PENDING_TRANSACTION));
		}

	}

	/**
	 * Checks if the amount specified in the payment information is sufficient to pay the order, if not, it throws an
	 * NDCOrderException exception
	 *
	 * @param order
	 * 		the order
	 * @param orderCreateRQ
	 * 		the order create rq
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void validatePaymentAmount(final OrderModel order, final OrderCreateRQ orderCreateRQ) throws NDCOrderException
	{
		if (Objects.isNull(orderCreateRQ.getQuery().getPayments()) || orderCreateRQ.getQuery().getPayments().getPayment().isEmpty())
		{
			return;
		}

		final double paymentAllowedAmount = orderCreateRQ.getQuery().getPayments().getPayment().get(0).getAmount().getValue()
				.doubleValue();

		if (order.getTotalPrice() > paymentAllowedAmount)
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INSUFFICIENT_PAYMENT_AMOUNT));
		}
	}

	/**
	 * Checks if the {@link OrderChangeRQ} contains the payment information
	 *
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void containsPaymentInformation(final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		if (Objects.isNull(orderChangeRQ.getQuery().getPayments()) || orderChangeRQ.getQuery().getPayments().getPayment().isEmpty())
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_PAYMENT_INFORMATION));
		}
	}

	/**
	 * Checks if the amount specified in the payment information is sufficient to pay the order, if not, it throws an
	 * NDCOrderException exception
	 *
	 * @param order
	 * 		the order
	 * @param orderChangeRQ
	 * 		the order change rq
	 *
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected void validatePaymentAmount(final OrderModel order, final OrderChangeRQ orderChangeRQ) throws NDCOrderException
	{
		containsPaymentInformation(orderChangeRQ);

		final double paymentAllowedAmount = orderChangeRQ.getQuery().getPayments().getPayment().get(0).getAmount().getValue()
				.doubleValue();

		if (getTotalToPay(order) > paymentAllowedAmount)
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INSUFFICIENT_PAYMENT_AMOUNT));
		}
	}

	/**
	 * Returns the IsoCode of the Currency specified in the metadata
	 *
	 * @param orderCreateRQ
	 * 		the order create rq
	 *
	 * @return currency from metadata
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected String getCurrencyFromMetadata(final OrderCreateRQ orderCreateRQ) throws NDCOrderException
	{
		final Optional<OtherMetadata> currencyMetadata = orderCreateRQ.getQuery().getMetadata().getOther().getOtherMetadata()
				.stream().filter(other -> !Objects.isNull(other.getCurrencyMetadatas())).findAny();

		if (!currencyMetadata.isPresent())
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.MISSING_METADATA_CURRENCY));
		}

		return currencyMetadata.get().getCurrencyMetadatas().getCurrencyMetadata().get(0).getMetadataKey();
	}

	/**
	 * Method to creates the guest used from the email contained in the EmailContact of the TravelAgencySender
	 *
	 * @param sender
	 * 		the sender
	 *
	 * @return customer model
	 * @throws DuplicateUidException
	 * 		the duplicate uid exception
	 */
	protected CustomerModel createGuestUser(final MsgPartiesType.Sender sender) throws DuplicateUidException
	{
		final Optional<Contacts.Contact> contactModal = sender.getTravelAgencySender().getContacts().getContact().stream()
				.filter(contact -> !Objects.isNull(contact.getEmailContact())).findFirst();

		final String email = contactModal.isPresent() ? contactModal.get().getEmailContact().getAddress().getValue()
				: StringUtils.EMPTY;

		return createGuestUserForAnonymousCheckout(email);
	}

	/**
	 * Sets the payment user in the payment Info
	 *
	 * @param orderModel
	 * 		the order model
	 * @param guest
	 * 		the guest
	 */
	protected void setPaymentUser(final OrderModel orderModel, final CustomerModel guest)
	{
		if (!Objects.isNull(orderModel.getPaymentInfo()))
		{
			orderModel.getPaymentInfo().setUser(guest);
		}
	}

	/**
	 * Creates a Guest User for the checkout
	 *
	 * @param email
	 * 		the email
	 *
	 * @return customer model
	 * @throws DuplicateUidException
	 * 		the duplicate uid exception
	 */
	protected CustomerModel createGuestUserForAnonymousCheckout(final String email) throws DuplicateUidException
	{
		final CustomerModel guestCustomer = getModelService().create(CustomerModel.class);
		final String guid = generateGUID();

		//takes care of localizing the name based on the site language
		guestCustomer.setUid(guid + "|" + email);
		guestCustomer.setName(NdcfacadesConstants.CUSTOMER_NAME);
		guestCustomer.setType(CustomerType.valueOf(CustomerType.GUEST.getCode()));
		guestCustomer.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		guestCustomer.setSessionCurrency(getCommonI18NService().getCurrentCurrency());

		getCustomerAccountService().registerGuestForAnonymousCheckout(guestCustomer, guid);

		return guestCustomer;
	}

	/**
	 * Generates a customer ID during registration
	 *
	 * @return the string
	 */
	protected String generateGUID()
	{
		return UUID.randomUUID().toString();
	}

	/**
	 * Gets customer account service.
	 *
	 * @return the customer account service
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * Sets customer account service.
	 *
	 * @param customerAccountService
	 * 		the customer account service
	 */
	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * Gets commerce stock service.
	 *
	 * @return the commerce stock service
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * Sets commerce stock service.
	 *
	 * @param commerceStockService
	 * 		the commerce stock service
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
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
	 * Gets ndc order entry facade.
	 *
	 * @return the ndc order entry facade
	 */
	protected NDCOrderEntryFacade getNdcOrderEntryFacade()
	{
		return ndcOrderEntryFacade;
	}

	/**
	 * Sets ndc order entry facade.
	 *
	 * @param ndcOrderEntryFacade
	 * 		the ndc order entry facade
	 */
	@Required
	public void setNdcOrderEntryFacade(final NDCOrderEntryFacade ndcOrderEntryFacade)
	{
		this.ndcOrderEntryFacade = ndcOrderEntryFacade;
	}

	/**
	 * Gets ndc payment address facade.
	 *
	 * @return the ndc payment address facade
	 */
	protected NDCPaymentAddressFacade getNdcPaymentAddressFacade()
	{
		return ndcPaymentAddressFacade;
	}

	/**
	 * Sets ndc payment address facade.
	 *
	 * @param ndcPaymentAddressFacade
	 * 		the ndc payment address facade
	 */
	@Required
	public void setNdcPaymentAddressFacade(final NDCPaymentAddressFacade ndcPaymentAddressFacade)
	{
		this.ndcPaymentAddressFacade = ndcPaymentAddressFacade;
	}

	/**
	 * Gets ndc payment info facade.
	 *
	 * @return the ndc payment info facade
	 */
	protected NDCPaymentInfoFacade getNdcPaymentInfoFacade()
	{
		return ndcPaymentInfoFacade;
	}

	/**
	 * Sets ndc payment info facade.
	 *
	 * @param ndcPaymentInfoFacade
	 * 		the ndc payment info facade
	 */
	@Required
	public void setNdcPaymentInfoFacade(final NDCPaymentInfoFacade ndcPaymentInfoFacade)
	{
		this.ndcPaymentInfoFacade = ndcPaymentInfoFacade;
	}

	/**
	 * Gets ndc payment transaction facade.
	 *
	 * @return the ndc payment transaction facade
	 */
	protected NDCPaymentTransactionFacade getNdcPaymentTransactionFacade()
	{
		return ndcPaymentTransactionFacade;
	}

	/**
	 * Sets ndc payment transaction facade.
	 *
	 * @param ndcPaymentTransactionFacade
	 * 		the ndc payment transaction facade
	 */
	@Required
	public void setNdcPaymentTransactionFacade(final NDCPaymentTransactionFacade ndcPaymentTransactionFacade)
	{
		this.ndcPaymentTransactionFacade = ndcPaymentTransactionFacade;
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
	 * Gets ndc order view rs converter.
	 *
	 * @return the ndc order view rs converter
	 */
	protected Converter<OrderModel, OrderViewRS> getNdcOrderViewRSConverter()
	{
		return ndcOrderViewRSConverter;
	}

	/**
	 * Sets ndc order view rs converter.
	 *
	 * @param ndcOrderViewRSConverter
	 * 		the ndc order view rs converter
	 */
	@Required
	public void setNdcOrderViewRSConverter(final Converter<OrderModel, OrderViewRS> ndcOrderViewRSConverter)
	{
		this.ndcOrderViewRSConverter = ndcOrderViewRSConverter;
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
	 * Gets amend order strategy map.
	 *
	 * @return the amend order strategy map
	 */
	protected Map<NDCActionType, AmendOrderStrategy> getAmendOrderStrategyMap()
	{
		return amendOrderStrategyMap;
	}

	/**
	 * Sets amend order strategy map.
	 *
	 * @param amendOrderStrategyMap
	 * 		the amend order strategy map
	 */
	@Required
	public void setAmendOrderStrategyMap(final Map<NDCActionType, AmendOrderStrategy> amendOrderStrategyMap)
	{
		this.amendOrderStrategyMap = amendOrderStrategyMap;
	}

	/**
	 * Gets booking facade.
	 *
	 * @return the booking facade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * Sets booking facade.
	 *
	 * @param bookingFacade
	 * 		the booking facade
	 */
	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}


	/**
	 * Gets order change action to booking action mapping.
	 *
	 * @return the order change action to booking action mapping
	 */
	protected Map<String, ActionTypeOption> getOrderChangeActionToBookingActionMapping()
	{
		return orderChangeActionToBookingActionMapping;
	}

	/**
	 * Sets order change action to booking action mapping.
	 *
	 * @param orderChangeActionToBookingActionMapping
	 * 		the order change action to booking action mapping
	 */
	@Required
	public void setOrderChangeActionToBookingActionMapping(
			final Map<String, ActionTypeOption> orderChangeActionToBookingActionMapping)
	{
		this.orderChangeActionToBookingActionMapping = orderChangeActionToBookingActionMapping;
	}

	/**
	 * Gets booking action enabled calculation strategies map.
	 *
	 * @return the booking action enabled calculation strategies map
	 */
	protected Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> getBookingActionEnabledCalculationStrategiesMap()
	{
		return bookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 * Sets booking action enabled calculation strategies map.
	 *
	 * @param bookingActionEnabledCalculationStrategiesMap
	 * 		the booking action enabled calculation strategies map
	 */
	@Required
	public void setBookingActionEnabledCalculationStrategiesMap(
			final Map<ActionTypeOption, List<BookingActionEnabledEvaluatorStrategy>> bookingActionEnabledCalculationStrategiesMap)
	{
		this.bookingActionEnabledCalculationStrategiesMap = bookingActionEnabledCalculationStrategiesMap;
	}

	/**
	 * Gets booking action strategy map.
	 *
	 * @return the booking action strategy map
	 */
	protected Map<ActionTypeOption, BookingActionStrategy> getBookingActionStrategyMap()
	{
		return bookingActionStrategyMap;
	}

	/**
	 * Sets booking action strategy map.
	 *
	 * @param bookingActionStrategyMap
	 * 		the booking action strategy map
	 */
	@Required
	public void setBookingActionStrategyMap(final Map<ActionTypeOption, BookingActionStrategy> bookingActionStrategyMap)
	{
		this.bookingActionStrategyMap = bookingActionStrategyMap;
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
	 * Gets amend order offer filter strategy list.
	 *
	 * @return the amend order offer filter strategy list
	 */
	protected List<AmendOrderOfferFilterStrategy> getAmendOrderOfferFilterStrategyList()
	{
		return amendOrderOfferFilterStrategyList;
	}

	/**
	 * Sets amend order offer filter strategy list.
	 *
	 * @param amendOrderOfferFilterStrategyList
	 * 		the amend order offer filter strategy list
	 */
	@Required
	public void setAmendOrderOfferFilterStrategyList(final List<AmendOrderOfferFilterStrategy> amendOrderOfferFilterStrategyList)
	{
		this.amendOrderOfferFilterStrategyList = amendOrderOfferFilterStrategyList;
	}
}
