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

package de.hybris.platform.travelservices.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.OrderCancelException;
import de.hybris.platform.ordercancel.OrderCancelRequest;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.customer.TravelCustomerAccountService;
import de.hybris.platform.travelservices.dao.AbstractOrderEntryGroupDao;
import de.hybris.platform.travelservices.dao.OrderUserAccountMappingDao;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.exceptions.RequestKeyGeneratorException;
import de.hybris.platform.travelservices.model.AbstractOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.OrderUserAccountMappingModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.travel.RemarkModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;
import de.hybris.platform.travelservices.order.daos.TravelOrderDao;
import de.hybris.platform.travelservices.ordercancel.TotalRefundCalculationStrategy;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForEntryGroupCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.OrderTotalPaidForOrderEntryTypeCalculationStrategy;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesOrderTotalToPayStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link BookingService}
 */
public class DefaultBookingService implements BookingService
{
	private static final Logger LOG = Logger.getLogger(DefaultBookingService.class);

	private static final String PASSENGER_TYPE_ADULT = "adult";
	private static final String ORDER_CANCEL_REQUEST_NOTES = "order.cancel.request.notes";
	private static final String MAX_NUMBER_OF_ATTEMPTS = "create.request.max.attempts.number";
	private static final Integer DEFAULT_MAX_NUMBER = 5;
	private static final String MAX_REQUEST_NUMBER = "accommodation.max.request.number";

	private ModelService modelService;
	private UserService userService;
	private CommerceCheckoutService commerceCheckoutService;
	private PaymentService paymentService;
	private OrderCancelService orderCancelService;
	private ConfigurationService configurationService;
	private TravelCartService travelCartService;
	private TotalRefundCalculationStrategy totalRefundCalculationStrategy;
	private TravelCommerceStockService commerceStockService;
	private TravelCustomerAccountService customerAccountService;
	private EnumerationService enumerationService;
	private BaseStoreService baseStoreService;
	private OrderService orderService;
	private TimeService timeService;
	private TravelKeyGeneratorService travelKeyGeneratorService;
	private Map<OrderEntryType, TotalRefundCalculationStrategy> totalRefundCalculationStrategyMap;
	private AbstractOrderEntryGroupDao abstractOrderEntryGroupDao;
	private OrderUserAccountMappingDao orderUserAccountMappingDao;
	private TravelOrderDao travelOrderDao;
	private OrderHistoryService orderHistoryService;
	private OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy;
	private OrderTotalPaidCalculationStrategy orderTotalPaidCalculationStrategy;
	private OrderTotalPaidForOrderEntryTypeCalculationStrategy orderTotalPaidForOrderEntryTypeCalculationStrategy;
	private ChangeDatesOrderTotalToPayStrategy changeDatesOrderTotalToPayStrategy;

	/**
	 * @deprecated Deprecated since version 2.0. Use
	 * {@link #createRefundPaymentTransaction(AbstractOrderModel, BigDecimal, List)} instead.
	 */
	@Deprecated
	@Override
	public Boolean createRefundPaymentTransaction(final AbstractOrderModel abstractOrder, final BigDecimal amountToRefund)
	{
		if (CollectionUtils.isEmpty(abstractOrder.getPaymentTransactions()))
		{
			LOG.error("No original payment transactions found on the order: " + abstractOrder.getCode()
					+ ". Cannot create refund payment transaction");
			return Boolean.FALSE;
		}
		final PaymentTransactionModel oldPaymentTransaction = abstractOrder.getPaymentTransactions().get(0);

		final PaymentTransactionModel refundTransaction = getModelService().create(PaymentTransactionModel.class);
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.REFUND_STANDALONE;
		refundTransaction.setCode(getUserService().getCurrentUser().getUid() + "_" + UUID.randomUUID());
		refundTransaction.setPaymentProvider(getCommerceCheckoutService().getPaymentProvider());
		refundTransaction.setCurrency(oldPaymentTransaction.getCurrency());
		refundTransaction.setRequestId(oldPaymentTransaction.getRequestId());
		refundTransaction.setRequestToken(oldPaymentTransaction.getRequestToken());
		refundTransaction.setInfo(oldPaymentTransaction.getInfo());
		refundTransaction.setPlannedAmount(amountToRefund);
		refundTransaction.setOrder(abstractOrder);

		getModelService().save(refundTransaction);

		final PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);
		entry.setType(paymentTransactionType);
		entry.setTime(getTimeService().getCurrentTime());
		entry.setPaymentTransaction(refundTransaction);
		entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
		entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
		entry.setCode(getPaymentService().getNewPaymentTransactionEntryCode(refundTransaction, paymentTransactionType));
		entry.setAmount(amountToRefund);
		entry.setRequestId(oldPaymentTransaction.getRequestId());
		entry.setRequestToken(oldPaymentTransaction.getRequestToken());

		getModelService().saveAll(entry);
		getModelService().refresh(refundTransaction);

		return Boolean.TRUE;
	}

	@Override
	public Boolean createRefundPaymentTransaction(final AbstractOrderModel abstractOrder, final BigDecimal amountToRefund,
			final List<AbstractOrderEntryModel> entries)
	{
		return ((TravelCommerceCheckoutService) getCommerceCheckoutService()).createRefundPaymentTransactionEntries(abstractOrder,
				entries);
	}

	@Override
	public OrderModel getOrderModelFromStore(final String bookingReference)
	{
		OrderModel orderModel = getOrder(bookingReference);
		if (Objects.isNull(orderModel))
		{
			return orderModel;
		}

		if (OrderStatus.CANCELLED.equals(orderModel.getStatus()))
		{
			final OrderHistoryEntryModel lastEntry = orderModel.getHistoryEntries().stream().reduce((entry1, entry2) -> entry2)
					.orElse(null);
			orderModel = lastEntry.getPreviousOrderVersion();
		}

		return orderModel;
	}

	@Override
	public OrderModel getOrder(final String bookingReference)
	{
		try
		{
			final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
			final OrderModel orderModel = getCustomerAccountService().getOrderForCode(bookingReference, baseStoreModel);
			if (Objects.isNull(orderModel))
			{
				LOG.warn("Unable to find Order with bookingReference " + bookingReference);
			}
			return orderModel;
		}
		catch (final ModelNotFoundException | UnknownIdentifierException e)
		{
			LOG.warn("Unable to find Order with bookingReference " + bookingReference);
			LOG.debug("Logging exception: ", e);
			return null;
		}
	}

	@Override
	public List<OrderModel> getAllOrders(final String orderCode, final BaseStoreModel baseStore)
	{
		return getTravelOrderDao().findOrdersByCode(orderCode, baseStore);
	}

	@Override
	public boolean isCancelPossible(final OrderModel order)
	{
		final OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order);
		orderCancelRequest.setCancelReason(CancelReason.CUSTOMERREQUEST);
		final CancelDecision cancelDecision = getOrderCancelService().isCancelPossible(orderCancelRequest.getOrder(),
				getUserService().getCurrentUser(), orderCancelRequest.isPartialCancel(), orderCancelRequest.isPartialEntryCancel());
		return cancelDecision.isAllowed();
	}

	@Override
	public boolean cancelOrder(final OrderModel order)
	{
		final OrderCancelRequest orderCancelRequest = new OrderCancelRequest(order);
		orderCancelRequest.setCancelReason(CancelReason.CUSTOMERREQUEST);
		final String notes = getConfigurationService().getConfiguration().getString(ORDER_CANCEL_REQUEST_NOTES);
		orderCancelRequest.setNotes(notes);
		try
		{
			getOrderCancelService().requestOrderCancel(orderCancelRequest, getUserService().getCurrentUser());
			getCommerceStockService().release(order);
		}
		catch (final OrderCancelException e)
		{
			LOG.error("Error cancelling the order with code " + order.getCode(), e);
			return false;
		}
		return true;
	}

	@Override
	public BigDecimal getTotalToRefund(final OrderModel order)
	{
		if (OrderStatus.CANCELLED.equals(order.getStatus()))
		{
			// We calculate total to refund on the original version of the order so that rules evaluation is consistent
			final OrderHistoryEntryModel lastEntry = order.getHistoryEntries().stream().reduce((entry1, entry2) -> entry2)
					.orElse(null);
			final OrderModel previousOrderModel = lastEntry.getPreviousOrderVersion();
			return calculateTotalRefund(previousOrderModel);
		}
		return calculateTotalRefund(order);
	}

	/**
	 * This method uses a map of strategies to calculate and return the totalRefund value. Based on the distinct
	 * orderEntryTypes of the orderModel, one or more strategies are called. Each strategy returns the totalRefund for
	 * the given OrderEntryType. Then all the refunds are summed and returned.
	 *
	 * @param orderModel
	 * @return the BigDecimal corresponding to the totalRefund
	 */
	protected BigDecimal calculateTotalRefund(final OrderModel orderModel)
	{
		final List<OrderEntryType> orderEntryTypeList = orderModel
				.getEntries().stream().filter(entry -> entry.getActive()
						&& !Objects.equals(entry.getAmendStatus(), AmendStatus.CHANGED) && entry.getQuantity() > 0)
				.map(entry -> entry.getType()).distinct().collect(Collectors.toList());

		return orderEntryTypeList.stream()
				.map(orderEntryType -> Objects.nonNull(getTotalRefundCalculationStrategyMap().get(orderEntryType))
						? getTotalRefundCalculationStrategyMap().get(orderEntryType).getTotalToRefund(orderModel)
						: getTotalRefundCalculationStrategyMap().get(OrderEntryType.DEFAULT).getTotalToRefund(orderModel))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public BigDecimal calculateTotalRefundForCancelledTraveller(final AbstractOrderModel abstractOrder)
	{
		final OrderModel originalOrder = getOriginalOrder(abstractOrder);
		if (Objects.isNull(originalOrder))
		{
			return BigDecimal.ZERO;
		}
		final OrderEntryType entryType = OrderEntryType.TRANSPORT;
		final BigDecimal amountToRefund = getOrderTotalPaidForOrderEntryType(originalOrder, entryType)
				.subtract(getTravelCartService().getTransportTotalByEntries(originalOrder, abstractOrder.getEntries().stream()
						.filter(entry -> BooleanUtils.isTrue(entry.getActive()) && Objects.equals(entryType, entry.getType()))
						.collect(Collectors.toList())));

		return amountToRefund;
	}

	@Override
	public OrderModel getOriginalOrder(final AbstractOrderModel abstractOrder)
	{
		if (Objects.nonNull(abstractOrder.getOriginalOrder()))
		{
			return abstractOrder.getOriginalOrder();
		}
		final Optional<OrderHistoryEntryModel> lastHistoryEntry = ((OrderModel) abstractOrder).getHistoryEntries().stream()
				.filter(historyEntry -> historyEntry.getPreviousOrderVersion() != null)
				.sorted((historyEntry1, historyEntry2) -> historyEntry2.getCreationtime().compareTo(historyEntry1.getCreationtime()))
				.findFirst();
		return lastHistoryEntry.isPresent() ? lastHistoryEntry.get().getPreviousOrderVersion() : null;
	}



	@Override
	public BigDecimal getTotalToRefund(final OrderModel order, final OrderEntryType orderEntryType)
	{
		if (OrderStatus.CANCELLED.equals(order.getStatus()))
		{
			// We calculate total to refund on the original version of the order so that rules evaluation is consistent
			final OrderHistoryEntryModel lastEntry = order.getHistoryEntries().stream().reduce((entry1, entry2) -> entry2)
					.orElse(null);
			final OrderModel previousOrderModel = lastEntry.getPreviousOrderVersion();
			return calculateTotalRefund(previousOrderModel, orderEntryType);
		}
		return calculateTotalRefund(order, orderEntryType);
	}

	/**
	 * This method uses a map of strategies to calculate and return the totalRefund value. The correct strategy is called
	 * based on the given orderEntryType.
	 *
	 * @param orderModel
	 * @param orderEntryType
	 * @return the BigDecimal corresponding to the totalRefund
	 */
	protected BigDecimal calculateTotalRefund(final OrderModel orderModel, final OrderEntryType orderEntryType)
	{
		return getTotalRefundCalculationStrategyMap().get(orderEntryType).getTotalToRefund(orderModel);
	}


	/**
	 * @deprecated Deprecated since version 2.0. Use {@link #cancelTraveller(BigDecimal, TravellerData)} instead.
	 */
	@Deprecated
	@Override
	public boolean cancelTraveller(final BigDecimal totalToRefund)
	{
		final CartModel amendCart = getTravelCartService().getSessionCart();
		if (totalToRefund.doubleValue() > 0d)
		{
			//TODO: work out the entries related to the traveller that is being cancelled so that they can be stored into the refund transaction
			return createRefundPaymentTransaction(amendCart, totalToRefund, Collections.emptyList());
		}
		return true;
	}

	@Override
	public boolean cancelTraveller(final BigDecimal totalToRefund, final TravellerData travellerData)
	{
		if (Objects.isNull(travellerData))
		{
			return Boolean.FALSE;
		}
		if (totalToRefund.doubleValue() > 0d)
		{
			final CartModel cart = getTravelCartService().getSessionCart();
			final List<AbstractOrderEntryModel> travellerEntries = cart.getEntries().stream()
					.filter(entry -> entry.getTravelOrderEntryInfo() != null
							&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1
							&& entry.getTravelOrderEntryInfo().getTravellers().iterator().next().getLabel()
									.equalsIgnoreCase(travellerData.getLabel()))
					.collect(Collectors.toList());
			return createRefundPaymentTransaction(cart, totalToRefund, travellerEntries);
		}
		return Boolean.TRUE;
	}

	@Override
	public AbstractOrderEntryModel getOrderEntry(final AbstractOrderModel abstractOrderModel, final String productCode,
			final String travelRouteCode, final List<String> transportOfferingCodes, final List<String> travellerCodes,
			final boolean bundleNoCheckRequired)
	{
		for (final AbstractOrderEntryModel orderEntry : abstractOrderModel.getEntries())
		{
			if (checkBundleAndProduct(bundleNoCheckRequired, orderEntry, productCode) && checkTravellers(orderEntry, travellerCodes)
					&& ((travelRouteCode == null || orderEntry.getTravelOrderEntryInfo().getTravelRoute() == null)
							|| travelRouteCode.equalsIgnoreCase(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getCode()))
					&& checkTransportOfferings(orderEntry, transportOfferingCodes))
			{
				return orderEntry;
			}
		}
		return null;
	}

	@Override
	public boolean checkBundleToAmendProduct(final AbstractOrderModel abstractOrderModel, final String productCode, final long qty,
			final String travelRouteCode, final List<String> transportOfferingCodes, final List<String> travellerCodes)
	{
		final List<AbstractOrderEntryModel> orderEntries = abstractOrderModel.getEntries().stream()
				.filter(
						orderEntry -> checkTravellers(orderEntry, travellerCodes)
								&& (orderEntry.getTravelOrderEntryInfo().getTravelRoute() == null || travelRouteCode == null
										|| travelRouteCode
												.equalsIgnoreCase(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getCode()))
								&& checkTransportOfferings(orderEntry, transportOfferingCodes)
								&& productCode.equalsIgnoreCase(orderEntry.getProduct().getCode()))
				.collect(Collectors.toList());

		return orderEntries.isEmpty()
				|| orderEntries.stream().anyMatch(orderEntry -> orderEntry.getBundleNo() == 0 || qty + orderEntry.getQuantity() > 0);

	}

	@Override
	public Long getProductQuantityInOrderForTransportOffering(final String bookingReference, final ProductModel productModel,
			final TransportOfferingModel transportOfferingModel)
	{
		if (Objects.isNull(productModel))
		{
			return 0L;
		}

		final OrderModel order = getOrder(bookingReference);

		if (Objects.isNull(order))
		{
			return 0L;
		}

		return getOrderService().getEntriesForProduct(order, productModel).stream()
				.filter(entry -> entry.getTravelOrderEntryInfo().getTransportOfferings().contains(transportOfferingModel))
				.mapToLong(AbstractOrderEntryModel::getQuantity).sum();
	}

	@Override
	public boolean atleastOneAdultTravellerRemaining(final String orderCode, final String cancelledTravellerCode)
	{
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode,
				getBaseStoreService().getCurrentBaseStore());

		final List<AbstractOrderEntryModel> travellerEntries = orderModel.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null
						&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1
						&& !entry.getTravelOrderEntryInfo().getTravellers().iterator().next().getLabel()
								.equalsIgnoreCase(cancelledTravellerCode)
						&& entry.getQuantity() > 0L && entry.getActive())
				.collect(Collectors.toList());
		return !(CollectionUtils.isEmpty(travellerEntries) || noMoreAdultTravellers(travellerEntries));
	}

	@Override
	public OrderModel getOrderModelByOriginalOrderCode(final String bookingReference)
	{
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		return getCustomerAccountService().getOrderModelByOriginalOrderCode(bookingReference, baseStoreModel);
	}

	@Override
	public void updateOrderStatus(final OrderModel orderModel, final OrderStatus status)
	{
		orderModel.setStatus(status);
		getModelService().save(orderModel);
	}

	@Override
	public List<AccommodationOrderEntryGroupModel> getAccommodationOrderEntryGroups(final AbstractOrderModel abstractOrder)
	{
		if (Objects.isNull(abstractOrder))
		{
			return Collections.emptyList();
		}

		final List<AbstractOrderEntryGroupModel> abstractOrderEntryGroups = getAbstractOrderEntryGroupDao()
				.findAbstractOrderEntryGroups(abstractOrder);
		if (CollectionUtils.isEmpty(abstractOrderEntryGroups))
		{
			return Collections.emptyList();
		}
		else
		{
			return abstractOrderEntryGroups.stream().filter(entry -> entry instanceof AccommodationOrderEntryGroupModel)
					.map(entry -> (AccommodationOrderEntryGroupModel) entry).collect(Collectors.toList());
		}
	}

	protected boolean noMoreAdultTravellers(final List<AbstractOrderEntryModel> travellerEntries)
	{
		for (final AbstractOrderEntryModel abstractOrderEntryModel : travellerEntries)
		{
			final TravellerModel traveller = abstractOrderEntryModel.getTravelOrderEntryInfo().getTravellers().iterator().next();
			final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();
			final PassengerTypeModel passengerType = passengerInfo.getPassengerType();
			if (PASSENGER_TYPE_ADULT.equals(passengerType.getCode()))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Verifies if the order entry has matching bundle number and product code
	 *
	 * @param bundleNoCheckRequired
	 * @param orderEntry
	 * @param productCode
	 * @return true if bundle number and product code match
	 */
	protected boolean checkBundleAndProduct(final boolean bundleNoCheckRequired, final AbstractOrderEntryModel orderEntry,
			final String productCode)
	{
		return (!bundleNoCheckRequired || orderEntry.getBundleNo() == 0)
				&& productCode.equalsIgnoreCase(orderEntry.getProduct().getCode());
	}

	/**
	 * Verifies if order entry has a matching list of travellers
	 *
	 * @param orderEntry
	 * @param travellerCodes
	 * @return true if list of travellers matches
	 */
	protected boolean checkTravellers(final AbstractOrderEntryModel orderEntry, final List<String> travellerCodes)
	{
		if (null == orderEntry.getTravelOrderEntryInfo())
		{
			return false;
		}

		if (CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTravellers()))
		{
			return CollectionUtils.isEmpty(travellerCodes);
		}

		final List<String> orderEntryTravellers = orderEntry.getTravelOrderEntryInfo().getTravellers().stream()
				.map(TravellerModel::getLabel).collect(Collectors.toList());
		return CollectionUtils.size(orderEntryTravellers) == CollectionUtils.size(travellerCodes)
				&& orderEntryTravellers.containsAll(travellerCodes);
	}

	/**
	 * Verifies if order entry has a matching list of transport offerings
	 *
	 * @param orderEntry
	 * @param transportOfferingCodes
	 * @return true if list of transport offering matches
	 */
	protected boolean checkTransportOfferings(final AbstractOrderEntryModel orderEntry, final List<String> transportOfferingCodes)
	{
		if (CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTransportOfferings())
				&& CollectionUtils.isEmpty(transportOfferingCodes))
		{
			return true;
		}
		if (CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTransportOfferings()))
		{
			return false;
		}
		final List<String> transportOfferingCodeList = orderEntry.getTravelOrderEntryInfo().getTransportOfferings().stream()
				.map(TransportOfferingModel::getCode).collect(Collectors.toList());
		return transportOfferingCodeList.containsAll(transportOfferingCodes);
	}

	@Override
	public AbstractOrderEntryModel getOriginalOrderEntry(final String originalOrderCode, final String productCode,
			final String travelRouteCode, final List<String> transportOfferingCodes, final List<String> travellerCodes,
			final Boolean bundleNoCheckRequired)
	{
		final OrderModel orderForCode = getOrderModelFromStore(originalOrderCode);
		return getOrderEntry(orderForCode, productCode, travelRouteCode, transportOfferingCodes, travellerCodes,
				bundleNoCheckRequired);
	}

	@Override
	public AbstractOrderEntryModel getOriginalOrderEntry(final AbstractOrderModel abstractOrderModel, final String productCode,
			final int roomStayRefNumber)
	{
		for (final AbstractOrderEntryModel orderEntry : abstractOrderModel.getEntries())
		{
			if (checkBundleAndProduct(Boolean.TRUE, orderEntry, productCode)
					&& checkRoomStayReferenceNumber(orderEntry, roomStayRefNumber))
			{
				return orderEntry;
			}
		}
		return null;
	}

	protected Boolean checkRoomStayReferenceNumber(final AbstractOrderEntryModel orderEntry, final int roomStayRefNumber)
	{
		final AccommodationOrderEntryGroupModel entryGroup = (AccommodationOrderEntryGroupModel) orderEntry.getEntryGroup();
		return entryGroup.getRoomStayRefNumber() == roomStayRefNumber;
	}

	@Override
	public boolean hasCartBeenAmended()
	{
		if (!getTravelCartService().hasSessionCart())
		{
			return false;
		}
		final CartModel amendmentCart = getTravelCartService().getSessionCart();
		if (amendmentCart == null)
		{
			return false;
		}
		final List<AbstractOrderEntryModel> changedEntries = amendmentCart.getEntries().stream()
				.filter(entry -> !AmendStatus.SAME.equals(entry.getAmendStatus())).collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(changedEntries))
		{
			return true;
		}

		if (checkIfAnyOrderEntryByType(amendmentCart, OrderEntryType.TRANSPORT))
		{
			final AbstractOrderModel oldBooking = getOrderModelFromStore(amendmentCart.getOriginalOrder().getCode());
			final List<SelectedAccommodationModel> oldSelectedAccomodation = oldBooking.getSelectedAccommodations();
			final List<SelectedAccommodationModel> newSelectedAccomodation = amendmentCart.getSelectedAccommodations();
			if ((CollectionUtils.isEmpty(oldSelectedAccomodation) && CollectionUtils.isNotEmpty(newSelectedAccomodation))
					|| (CollectionUtils.isNotEmpty(oldSelectedAccomodation) && CollectionUtils.isEmpty(newSelectedAccomodation)))
			{
				return true;
			}
			else if (CollectionUtils.isNotEmpty(oldSelectedAccomodation) && CollectionUtils.isNotEmpty(newSelectedAccomodation))
			{
				return haveSelectedAccommodationsChanged(oldSelectedAccomodation, newSelectedAccomodation);
			}
		}

		if (checkIfAnyOrderEntryByType(amendmentCart, OrderEntryType.ACCOMMODATION))
		{
			final List<AccommodationOrderEntryGroupModel> originalOrderEntryGroups = getAccommodationOrderEntryGroups(
					amendmentCart.getOriginalOrder());
			final List<AccommodationOrderEntryGroupModel> currentCartEntryGroups = getAccommodationOrderEntryGroups(amendmentCart);

			if (CollectionUtils.isNotEmpty(currentCartEntryGroups)
					&& CollectionUtils.size(originalOrderEntryGroups) == CollectionUtils.size(currentCartEntryGroups))
			{
				boolean hasRoomPreferenceChanged = false;
				for (final AccommodationOrderEntryGroupModel originalEntryGroup : originalOrderEntryGroups)
				{
					final Optional<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroup = currentCartEntryGroups.stream()
							.filter(entryGroup -> Objects
									.equals(originalEntryGroup.getRoomStayRefNumber(), entryGroup.getRoomStayRefNumber()))
							.findFirst();
					if (!accommodationOrderEntryGroup.isPresent())
					{
						break;
					}

					final AccommodationOrderEntryGroupModel currentEntryGroup = accommodationOrderEntryGroup.get();
					if (CollectionUtils.size(originalEntryGroup.getRoomPreferences()) != CollectionUtils
							.size(currentEntryGroup.getRoomPreferences()))
					{
						hasRoomPreferenceChanged = true;
					}
					else if (CollectionUtils.size(originalEntryGroup.getRoomPreferences()) == CollectionUtils
							.size(currentEntryGroup.getRoomPreferences())
							&& (CollectionUtils.isNotEmpty(originalEntryGroup.getRoomPreferences())
									|| CollectionUtils.isNotEmpty(currentEntryGroup.getRoomPreferences())))
					{
						hasRoomPreferenceChanged = !StringUtils.equals(originalEntryGroup.getRoomPreferences().get(0).getValue(),
								currentEntryGroup.getRoomPreferences().get(0).getValue());
					}

					if (hasRoomPreferenceChanged)
					{
						break;
					}
				}
				return hasRoomPreferenceChanged;
			}
		}

		return false;
	}

	/**
	 * @deprecated Deprecated since version 3.0. Please use checkIfAnyOrderEntryByType in place of this method as it is doing
	 * exactly the same thing.
	 *
	 * @param amendmentCart
	 * @return
	 */
	@Deprecated
	protected boolean isTransportBooking(final AbstractOrderModel amendmentCart)
	{
		final Optional<AbstractOrderEntryModel> optionalTransportEntry = amendmentCart.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())).findAny();
		return optionalTransportEntry.isPresent();
	}

	/**
	 * This method here is used to compare two lists for SelectedAccommodationModel. It returns false if all the
	 * SelectedAccommodationModel for the same TransporOffering and the same Traveller have the same
	 * ConfiguredAccommodationModel identifier. True if for at least one TransportOffering and one Traveller the
	 * ConfiguredAccommodationModel identifier has changed.
	 *
	 * @param oldSelectedAccomodation
	 *           the list of previous SelectedAccommodationModel
	 * @param newSelectedAccomodation
	 *           the list of the newly SelectedAccommodationModel
	 */
	protected boolean haveSelectedAccommodationsChanged(final List<SelectedAccommodationModel> oldSelectedAccomodation,
			final List<SelectedAccommodationModel> newSelectedAccomodation)
	{

		for (final SelectedAccommodationModel oldAccommodation : oldSelectedAccomodation)
		{

			final Optional<SelectedAccommodationModel> newSelectedAccommodationOptional = newSelectedAccomodation.stream()
					.filter(selectedAccommodation -> selectedAccommodation.getTransportOffering()
							.equals(oldAccommodation.getTransportOffering())
							&& selectedAccommodation.getTraveller().equals(oldAccommodation.getTraveller()))
					.findFirst();

			if (!newSelectedAccommodationOptional.isPresent())
			{
				return true;
			}

			if (!StringUtils.equalsIgnoreCase(newSelectedAccommodationOptional.get().getConfiguredAccommodation().getIdentifier(),
					oldAccommodation.getConfiguredAccommodation().getIdentifier()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public AccommodationOrderEntryGroupModel getAccommodationOrderEntryGroup(final int roomStayRefNum,
			final AbstractOrderModel abstractOrder)
	{
		try
		{
			validateParameterNotNull(roomStayRefNum, "Parameter roomStayRefNum cannot be null");
			validateParameterNotNull(abstractOrder, "Parameter abstractOrder cannot be null");
		}
		catch (final IllegalArgumentException ex)
		{
			LOG.debug(ex);
			return null;
		}
		final List<AbstractOrderEntryGroupModel> abstractOrderEntryGroups = getAbstractOrderEntryGroupDao()
				.findAbstractOrderEntryGroups(abstractOrder);
		if (CollectionUtils.isNotEmpty(abstractOrderEntryGroups))
		{
			final Optional<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroup = abstractOrderEntryGroups.stream()
					.filter(entry -> entry instanceof AccommodationOrderEntryGroupModel
							&& ((AccommodationOrderEntryGroupModel) entry).getRoomStayRefNumber().intValue() == roomStayRefNum)
					.map(entry -> (AccommodationOrderEntryGroupModel) entry).findAny();
			if (accommodationOrderEntryGroup.isPresent())
			{
				return accommodationOrderEntryGroup.get();
			}
		}
		return null;
	}

	@Override
	public boolean isReservationCancelled(final AbstractOrderModel abstractOrderModel, final OrderEntryType orderEntryType)
	{
		final Optional<AbstractOrderEntryModel> optional = abstractOrderModel.getEntries().stream()
				.filter(orderEntry -> Objects.equals(orderEntryType, orderEntry.getType())
						&& !(BooleanUtils.isFalse(orderEntry.getActive()) && orderEntry.getQuantity() == 0))
				.findAny();

		return !optional.isPresent();
	}

	@Override
	public boolean checkIfAnyOrderEntryByType(final AbstractOrderModel abstractOrderModel, final OrderEntryType orderEntryType)
	{
		final Optional<AbstractOrderEntryModel> optional = abstractOrderModel.getEntries().stream()
				.filter(orderEntry -> Objects.equals(orderEntryType, orderEntry.getType())).findAny();

		return optional.isPresent();
	}


	@Override
	public boolean isAbstractOrderOfType(final AbstractOrderModel abstractOrder, final String bookingType)
	{
		if (Objects.isNull(abstractOrder) || CollectionUtils.isEmpty(abstractOrder.getEntries()))
		{
			return Boolean.FALSE;
		}

		final OrderEntryType orderEntryType = getEnumerationService().getEnumerationValue(OrderEntryType.class, bookingType);
		if (orderEntryType == null)
		{
			return Boolean.FALSE;
		}

		return checkIfAnyOrderEntryByType(abstractOrder, orderEntryType);

	}

	@Override
	public double getBookingTotalByOrderEntryType(final AbstractOrderModel abstractOrder, final OrderEntryType orderEntryType)
	{
		if (Objects.isNull(abstractOrder) || CollectionUtils.isEmpty(abstractOrder.getEntries()) || Objects.isNull(orderEntryType))
		{
			return 0d;
		}

		Double totalRate = 0d;
		for (final AbstractOrderEntryModel entry : abstractOrder.getEntries())
		{
			if (BooleanUtils.isTrue(entry.getActive()) && Objects.equals(orderEntryType, entry.getType()))
			{
				totalRate = Double.sum(totalRate, entry.getTotalPrice());
				if (CollectionUtils.isNotEmpty(entry.getTaxValues()))
				{
					totalRate = Double.sum(totalRate,
							entry.getTaxValues().stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum());
				}
			}
		}

		return totalRate;
	}

	@Override
	public boolean isValidPassengerReference(final AbstractOrderModel abstractOrder, final String passengerReference)
	{
		if(StringUtils.isEmpty(passengerReference))
		{
			return false;
		}

		final Set<TravellerModel> travellers = abstractOrder.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(
						Collectors.toSet());

		final Optional<TravellerModel> traveller = travellers.stream()
				.filter(travellerModel -> StringUtils.equalsIgnoreCase(travellerModel.getSimpleUID(), passengerReference))
				.findFirst();
		return traveller.isPresent();
	}

	@Override
	public Map<String, String> getAccommodationDetailsParameters(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.isNull(abstractOrderModel))
		{
			return Collections.emptyMap();
		}

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getAccommodationOrderEntryGroups(
				abstractOrderModel);
		if (CollectionUtils.isEmpty(accommodationOrderEntryGroups))
		{
			return Collections.emptyMap();
		}

		final AccommodationOrderEntryGroupModel entryGroup = accommodationOrderEntryGroups.get(0);
		final Map<String, String> params = new HashMap<>();
		params.put(TravelservicesConstants.ACCOMMODATION_OFFERING_CODE, entryGroup.getAccommodationOffering().getCode());
		params.put(TravelservicesConstants.CHECK_IN_DATE_TIME,
				TravelDateUtils.convertDateToStringDate(entryGroup.getStartingDate(), TravelservicesConstants.DATE_PATTERN));
		params.put(TravelservicesConstants.CHECK_OUT_DATE_TIME,
				TravelDateUtils.convertDateToStringDate(entryGroup.getEndingDate(), TravelservicesConstants.DATE_PATTERN));

		return params;
	}

	@Override
	public List<Integer> getAccommodationOrderEntryGroupRefs(final AbstractOrderModel abstractOrderModel)
	{
		return getAccommodationOrderEntryGroups(abstractOrderModel).stream().map(entryGroup -> entryGroup.getRoomStayRefNumber())
				.collect(Collectors.toList());
	}

	@Override
	public List<Integer> getNewAccommodationOrderEntryGroupRefs(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.isNull(abstractOrderModel))
		{
			return Collections.emptyList();
		}

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getAccommodationOrderEntryGroups(
				abstractOrderModel);

		final List<Integer> result = new ArrayList<>();
		accommodationOrderEntryGroups.forEach(entryGroup -> {
			final Optional<AbstractOrderEntryModel> optionalNotNewEntry = entryGroup.getEntries().stream()
					.filter(entry -> !AmendStatus.NEW.equals(entry.getAmendStatus())).findAny();
			if (!optionalNotNewEntry.isPresent())
			{
				result.add(entryGroup.getRoomStayRefNumber());
			}
		});

		return result;
	}

	@Override
	public List<Integer> getOldAccommodationOrderEntryGroupRefs(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.isNull(abstractOrderModel))
		{
			return Collections.emptyList();
		}

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getAccommodationOrderEntryGroups(
				abstractOrderModel);

		final List<Integer> result = new ArrayList<>(
				CollectionUtils.isNotEmpty(accommodationOrderEntryGroups) ? CollectionUtils.size(accommodationOrderEntryGroups) : 0);
		accommodationOrderEntryGroups.forEach(entryGroup -> {
			final Optional<AbstractOrderEntryModel> optionalNotNewEntry = entryGroup.getEntries().stream()
					.filter(entry -> entry.getActive() && AmendStatus.SAME.equals(entry.getAmendStatus())).findAny();
			if (optionalNotNewEntry.isPresent())
			{
				result.add(entryGroup.getRoomStayRefNumber());
			}
		});

		return result;
	}

	@Override
	public void addRequestToRoomStayBooking(final String request, final int roomStayRefNumber, final String bookingReference)
			throws ModelSavingException, RequestKeyGeneratorException
	{
		final AccommodationOrderEntryGroupModel entryGroup = getAccommodationOrderEntryGroup(roomStayRefNumber,
				getOrder(bookingReference));
		if (Objects.isNull(entryGroup))
		{
			throw new ModelSavingException("Back end validation failed. Impossible to save the submitted request.");
		}

		final SpecialRequestDetailModel specialRequests = Objects.nonNull(entryGroup.getSpecialRequestDetail())
				? entryGroup.getSpecialRequestDetail() : getModelService().create(SpecialRequestDetailModel.class);
		final List<RemarkModel> remarks = CollectionUtils.isEmpty(specialRequests.getRemarks()) ? new ArrayList<>()
				: new ArrayList<>(specialRequests.getRemarks());
		if (CollectionUtils.size(remarks) >= getConfigurationService().getConfiguration().getInt(MAX_REQUEST_NUMBER,
				DEFAULT_MAX_NUMBER))
		{
			LOG.error("Trying to add a not allowed request. Maximum number reached.");
			throw new ModelSavingException(
					"Max number of requests reached. Back end validation failed. Impossible to save the submitted request.");
		}
		remarks.add(createRequest(request, roomStayRefNumber, bookingReference));
		specialRequests.setRemarks(remarks);
		getModelService().save(specialRequests);
		entryGroup.setSpecialRequestDetail(specialRequests);
		getModelService().save(entryGroup);
	}

	protected RemarkModel createRequest(final String request, final int roomStayRefNumber, final String bookingReference)
			throws RequestKeyGeneratorException, ModelSavingException

	{
		final RemarkModel requestToCreate = getModelService().create(RemarkModel.class);
		requestToCreate.setName(request);
		saveRemarkModel(requestToCreate, roomStayRefNumber, bookingReference, 0);
		return requestToCreate;
	}



	protected void saveRemarkModel(final RemarkModel requestToCreate, final int roomStayRefNumber, final String bookingReference,
			final int attempt) throws RequestKeyGeneratorException
	{
		try
		{
			requestToCreate
					.setCode(getTravelKeyGeneratorService().generateAccommodationRequestCode(roomStayRefNumber, bookingReference));
			getModelService().save(requestToCreate);
		}
		catch (final ModelSavingException e)
		{
			int attemptNo = attempt;
			if (attemptNo < getConfigurationService().getConfiguration().getInt(MAX_REQUEST_NUMBER, DEFAULT_MAX_NUMBER))
			{
				LOG.warn("Failed to save request due to duplicate ID. Performing new attempt...");
				saveRemarkModel(requestToCreate, roomStayRefNumber, bookingReference, ++attemptNo);
			}
			LOG.error("Max number of attempts to create request reached. Aborting.", e);
			throw new RequestKeyGeneratorException("Max number of attempts to create request reached");
		}
	}

	@Override
	public void removeRequestFromRoomStayBooking(final String requestCode, final int roomStayRefNumber,
			final String bookingReference) throws ModelNotFoundException, ModelRemovalException
	{
		final AccommodationOrderEntryGroupModel entryGroup = getAccommodationOrderEntryGroup(roomStayRefNumber,
				getOrder(bookingReference));
		if (Objects.isNull(entryGroup) || Objects.isNull(entryGroup.getSpecialRequestDetail())
				|| CollectionUtils.isEmpty(entryGroup.getSpecialRequestDetail().getRemarks()))
		{
			throw new ModelNotFoundException("Impossible to retrieve request data");
		}
		final Optional<RemarkModel> requestToRemove = entryGroup.getSpecialRequestDetail().getRemarks().stream()
				.filter(request -> requestCode.equals(request.getCode())).findFirst();
		if (!requestToRemove.isPresent())
		{
			throw new ModelNotFoundException("Impossible to retrieve request data");
		}
		getModelService().remove(requestToRemove.get());
	}

	@Override
	public Double getOrderTotalPriceByType(final AbstractOrderModel abstractOrder, final OrderEntryType orderEntryType)
	{
		if (abstractOrder.getEntries().stream().allMatch(entry -> Objects.equals(entry.getType(), orderEntryType)))
		{
			if (BooleanUtils.isTrue(abstractOrder.getNet()))
			{
				return abstractOrder.getTotalPrice() + abstractOrder.getTotalTax();
			}
			else
			{
				return abstractOrder.getTotalPrice();
			}
		}

		Double totalPrice = 0d;
		for (final AbstractOrderEntryModel abstractOrderEntry : abstractOrder.getEntries())
		{
			if (Objects.equals(abstractOrderEntry.getType(), orderEntryType))
			{
				totalPrice += abstractOrderEntry.getTotalPrice();
				final Optional<Double> totalTaxPrice = abstractOrderEntry.getTaxValues().stream()
						.map(taxValue -> Double.valueOf(taxValue.getAppliedValue())).reduce(Double::sum);
				if (totalTaxPrice.isPresent())
				{
					totalPrice += totalTaxPrice.get();
				}
			}
		}

		return Double.valueOf(totalPrice);
	}

	@Override
	public boolean unlinkBooking(final UserModel user, final OrderModel order)
	{
		if (order.getUser().equals(user))
		{
			try
			{
				order.setVisibleToOwner(false);
				getModelService().save(order);

				if (order.getVersionID() != null)
				{
					final OrderModel lastVersion = getCustomerAccountService().getOrderForCode(order.getCode(),
							getBaseStoreService().getCurrentBaseStore());

					lastVersion.setVisibleToOwner(false);
					getModelService().save(lastVersion);

					final Collection<OrderModel> versions = orderHistoryService.getHistorySnapshots(lastVersion);

					if (CollectionUtils.isNotEmpty(versions))
					{
						versions.forEach(version -> {
							version.setVisibleToOwner(false);
							getModelService().save(order);
						});
					}
				}
			}

			catch (final ModelSavingException mse)
			{
				LOG.error("Unable to perform unlinking operation for order " + order.getCode());
				LOG.debug("Logging exception: ", mse);
				return false;
			}
		}
		else
		{
			final List<OrderUserAccountMappingModel> mappings = getOrderUserAccountMappingDao().findMappings(user, order);
			if (!CollectionUtils.isEmpty(mappings))
			{
				getModelService().removeAll(mappings);
			}
		}
		return true;
	}

	@Override
	public boolean cancelPartialOrder(final BigDecimal totalToRefund, final OrderEntryType orderEntryType)
	{
		final CartModel amendCart = getTravelCartService().getSessionCart();

		final List<AbstractOrderEntryModel> entries = amendCart.getEntries().stream()
				.filter(entry -> Objects.equals(entry.getType(), orderEntryType)).collect(Collectors.toList());

		if (totalToRefund.doubleValue() > 0d)
		{
			return createRefundPaymentTransaction(amendCart, totalToRefund, entries);
		}
		return true;
	}

	@Override
	public AbstractOrderModel getLastActiveOrderForType(final AbstractOrderModel abstractOrderModel,
			final OrderEntryType orderEntryType)
	{
		if (!(abstractOrderModel instanceof OrderModel))
		{
			return null;
		}
		final OrderModel currentOrderModel = (OrderModel) abstractOrderModel;

		return getAllOrders(currentOrderModel.getCode(), getBaseStoreService().getCurrentBaseStore()).stream()
				.filter(order -> !isReservationCancelled(order, orderEntryType))
				.sorted((order1, order2) -> order2.getCreationtime().compareTo(order1.getCreationtime())).findFirst()
				.orElse(currentOrderModel);
	}

	@Override
	public BigDecimal getOrderTotalToPayForChangeDates()
	{
		return getChangeDatesOrderTotalToPayStrategy().calculate();
	}

	@Override
	public BigDecimal getOrderTotalPaid(final AbstractOrderModel abstractOrder)
	{
		return getOrderTotalPaidCalculationStrategy().calculate(abstractOrder);
	}

	@Override
	public BigDecimal getOrderTotalPaidByEntryGroup(final AbstractOrderModel abstractOrder,
			final AbstractOrderEntryGroupModel entryGroup)
	{
		return getOrderTotalPaidForAccommodationGroupCalculationStrategy().calculate(abstractOrder, entryGroup);
	}

	@Override
	public BigDecimal getOrderTotalPaidForOrderEntryType(final AbstractOrderModel abstractOrder,
			final OrderEntryType orderEntryType)
	{
		return getOrderTotalPaidForOrderEntryTypeCalculationStrategy().calculate(abstractOrder, orderEntryType);
	}

	@Override
	public boolean linkEntriesToOldPaymentTransactions(final AccommodationOrderEntryGroupModel orderEntryGroup,
			final List<AbstractOrderEntryModel> entries)
	{
		return ((TravelCommerceCheckoutService) getCommerceCheckoutService()).linkEntriesToOldPaymentTransactions(orderEntryGroup,
				entries);
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the commerceCheckoutService
	 */
	protected CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	/**
	 * @param commerceCheckoutService
	 *           the commerceCheckoutService to set
	 */
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	/**
	 * @return the paymentService
	 */
	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	/**
	 * @param paymentService
	 *           the paymentService to set
	 */
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

	/**
	 * @return the orderCancelService
	 */
	protected OrderCancelService getOrderCancelService()
	{
		return orderCancelService;
	}

	/**
	 * @param orderCancelService
	 *           the orderCancelService to set
	 */
	public void setOrderCancelService(final OrderCancelService orderCancelService)
	{
		this.orderCancelService = orderCancelService;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * @param travelCartService
	 *           the travelCartService to set
	 */
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * @return the totalRefundCalculationStrategy
	 */
	protected TotalRefundCalculationStrategy getTotalRefundCalculationStrategy()
	{
		return totalRefundCalculationStrategy;
	}

	/**
	 * @param totalRefundCalculationStrategy
	 *           the totalRefundCalculationStrategy to set
	 */
	public void setTotalRefundCalculationStrategy(final TotalRefundCalculationStrategy totalRefundCalculationStrategy)
	{
		this.totalRefundCalculationStrategy = totalRefundCalculationStrategy;
	}

	/**
	 * @return baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService
	 *           the commerceStockService to set
	 */
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * Gets order service.
	 *
	 * @return the order service
	 */
	protected OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * Sets order service.
	 *
	 * @param orderService
	 *           the order service
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * Gets customer account service.
	 *
	 * @return the customer account service
	 */
	protected TravelCustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * Sets customer account service.
	 *
	 * @param customerAccountService
	 *           the customer account service
	 */
	public void setCustomerAccountService(final TravelCustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the travelKeyGeneratorService
	 */
	protected TravelKeyGeneratorService getTravelKeyGeneratorService()
	{
		return travelKeyGeneratorService;
	}

	/**
	 * @param travelKeyGeneratorService
	 *           the travelKeyGeneratorService to set
	 */
	@Required
	public void setTravelKeyGeneratorService(final TravelKeyGeneratorService travelKeyGeneratorService)
	{
		this.travelKeyGeneratorService = travelKeyGeneratorService;
	}

	/**
	 * @return the totalRefundCalculationStrategyMap
	 */
	public Map<OrderEntryType, TotalRefundCalculationStrategy> getTotalRefundCalculationStrategyMap()
	{
		return totalRefundCalculationStrategyMap;
	}

	/**
	 * @param totalRefundCalculationStrategyMap
	 *           as the totalRefundCalculationStrategyMap to set
	 */
	public void setTotalRefundCalculationStrategyMap(
			final Map<OrderEntryType, TotalRefundCalculationStrategy> totalRefundCalculationStrategyMap)
	{
		this.totalRefundCalculationStrategyMap = totalRefundCalculationStrategyMap;
	}

	/**
	 * @return the abstractOrderEntryGroupDao
	 */
	protected AbstractOrderEntryGroupDao getAbstractOrderEntryGroupDao()
	{
		return abstractOrderEntryGroupDao;
	}

	/**
	 * @param abstractOrderEntryGroupDao
	 *           as the abstractOrderEntryGroupDao to set
	 */
	@Required
	public void setAbstractOrderEntryGroupDao(final AbstractOrderEntryGroupDao abstractOrderEntryGroupDao)
	{
		this.abstractOrderEntryGroupDao = abstractOrderEntryGroupDao;
	}

	/**
	 * @return the orderUserAccountMappingDao
	 */
	protected OrderUserAccountMappingDao getOrderUserAccountMappingDao()
	{
		return orderUserAccountMappingDao;
	}

	/**
	 * @param orderUserAccountMappingDao
	 *           as the orderUserAccountMappingDao to set
	 */
	@Required
	public void setOrderUserAccountMappingDao(final OrderUserAccountMappingDao orderUserAccountMappingDao)
	{
		this.orderUserAccountMappingDao = orderUserAccountMappingDao;
	}

	/**
	 * @return the travelOrderDao
	 */
	protected TravelOrderDao getTravelOrderDao()
	{
		return travelOrderDao;
	}

	/**
	 * @param travelOrderDao
	 *           as the travelOrderDao to set
	 */
	@Required
	public void setTravelOrderDao(final TravelOrderDao travelOrderDao)
	{
		this.travelOrderDao = travelOrderDao;
	}

	/**
	 * @return the orderHistoryService
	 */
	protected OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	/**
	 * @param orderHistoryService
	 *           as the orderHistoryService to set
	 */
	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	/**
	 *
	 * @return the orderTotalPaidCalculationStrategy
	 */
	protected OrderTotalPaidCalculationStrategy getOrderTotalPaidCalculationStrategy()
	{
		return orderTotalPaidCalculationStrategy;
	}

	/**
	 *
	 * @param orderTotalPaidCalculationStrategy
	 *           the orderTotalPaidCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidCalculationStrategy(final OrderTotalPaidCalculationStrategy orderTotalPaidCalculationStrategy)
	{
		this.orderTotalPaidCalculationStrategy = orderTotalPaidCalculationStrategy;
	}

	/**
	 *
	 * @return the orderTotalPaidForAccommodationGroupCalculationStrategy
	 */
	protected OrderTotalPaidForEntryGroupCalculationStrategy getOrderTotalPaidForAccommodationGroupCalculationStrategy()
	{
		return orderTotalPaidForAccommodationGroupCalculationStrategy;
	}

	/**
	 *
	 * @param orderTotalPaidForAccommodationGroupCalculationStrategy
	 *           the orderTotalPaidForAccommodationGroupCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidForAccommodationGroupCalculationStrategy(
			final OrderTotalPaidForEntryGroupCalculationStrategy orderTotalPaidForAccommodationGroupCalculationStrategy)
	{
		this.orderTotalPaidForAccommodationGroupCalculationStrategy = orderTotalPaidForAccommodationGroupCalculationStrategy;
	}

	/**
	 * @return the orderTotalPaidForOrderEntryTypeCalculationStrategy
	 */
	public OrderTotalPaidForOrderEntryTypeCalculationStrategy getOrderTotalPaidForOrderEntryTypeCalculationStrategy()
	{
		return orderTotalPaidForOrderEntryTypeCalculationStrategy;
	}

	/**
	 * @param orderTotalPaidForOrderEntryTypeCalculationStrategy
	 *           the orderTotalPaidForOrderEntryTypeCalculationStrategy to set
	 */
	@Required
	public void setOrderTotalPaidForOrderEntryTypeCalculationStrategy(
			final OrderTotalPaidForOrderEntryTypeCalculationStrategy orderTotalPaidForOrderEntryTypeCalculationStrategy)
	{
		this.orderTotalPaidForOrderEntryTypeCalculationStrategy = orderTotalPaidForOrderEntryTypeCalculationStrategy;
	}

	/**
	 * @return the changeDatesOrderTotalToPayStrategy
	 */
	protected ChangeDatesOrderTotalToPayStrategy getChangeDatesOrderTotalToPayStrategy()
	{
		return changeDatesOrderTotalToPayStrategy;
	}

	/**
	 * @param changeDatesOrderTotalToPayStrategy
	 *           the changeDatesOrderTotalToPayStrategy to set
	 */
	@Required
	public void setChangeDatesOrderTotalToPayStrategy(final ChangeDatesOrderTotalToPayStrategy changeDatesOrderTotalToPayStrategy)
	{
		this.changeDatesOrderTotalToPayStrategy = changeDatesOrderTotalToPayStrategy;
	}

	/**
	 * Gets the enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets the enumeration service.
	 *
	 * @param enumerationService
	 *           the new enumeration service
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

}
