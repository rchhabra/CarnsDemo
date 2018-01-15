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

package de.hybris.platform.travelservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.order.TravelCommerceCheckoutService;
import de.hybris.platform.travelservices.strategies.payment.RefundPaymentTransactionStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Extension of DefaultCommerceCheckoutService which has travel specific functions, eg. overrides the standard
 * authorisation of payment
 */
public class DefaultTravelCommerceCheckoutService extends DefaultCommerceCheckoutService implements TravelCommerceCheckoutService
{

	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private UserService userService;
	private TimeService timeService;
	private Map<OrderEntryType, RefundPaymentTransactionStrategy> refundPaymentTransactionStrategyMap;
	private SessionService sessionService;

	private static final Logger LOG = Logger.getLogger(DefaultTravelCommerceCheckoutService.class);
	private static final String SESSION_PAY_NOW = "sessionPayNow";

	@Override
	public PaymentTransactionEntryModel authorizePayment(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		validateParameterNotNull(cartModel.getPaymentInfo(), "Payment information on cart cannot be null");

		// if the authorization amount is not passed in figure it out from the cart.
		if (parameter.getAuthorizationAmount() == null)
		{
			BigDecimal totalPriceBD = BigDecimal.ZERO;
			if (cartModel.getOriginalOrder() == null)
			{
				totalPriceBD = getAuthorizationAmount(cartModel);
			}
			else
			{
				totalPriceBD = getAuthorizationAmountForAmendment(cartModel);
			}
			parameter.setAuthorizationAmount(totalPriceBD);
		}

		return getCommercePaymentAuthorizationStrategy().authorizePaymentAmount(parameter);
	}

	protected BigDecimal getAuthorizationAmountForAmendment(final CartModel cartModel)
	{
		final OrderModel originalOrder = cartModel.getOriginalOrder();
		final Double totalPrice = cartModel.getTotalPrice() - originalOrder.getTotalPrice();

		final Double newTotalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null
				&& cartModel.getStore().getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final Double originalTotalTax = (originalOrder.getNet().booleanValue() && originalOrder.getStore() != null
				&& originalOrder.getStore().getExternalTaxEnabled().booleanValue()) ? originalOrder.getTotalTax()
						: Double.valueOf(0d);
		final Double totalTax = newTotalTax - originalTotalTax;

		final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		return BigDecimal.valueOf(totalTax == null ? 0d : totalTax.doubleValue()).setScale(2, RoundingMode.HALF_EVEN)
				.add(totalPriceWithoutTaxBD);
	}

	protected BigDecimal getAuthorizationAmount(final CartModel cartModel)
	{
		final Double totalPrice = cartModel.getTotalPrice();
		final Double totalTax = (cartModel.getNet().booleanValue() && cartModel.getStore() != null
				&& cartModel.getStore().getExternalTaxEnabled().booleanValue()) ? cartModel.getTotalTax() : Double.valueOf(0d);
		final BigDecimal totalPriceWithoutTaxBD = BigDecimal.valueOf(totalPrice == null ? 0d : totalPrice.doubleValue()).setScale(2,
				RoundingMode.HALF_EVEN);
		return BigDecimal.valueOf(totalTax == null ? 0d : totalTax.doubleValue()).setScale(2, RoundingMode.HALF_EVEN)
				.add(totalPriceWithoutTaxBD);
	}

	@Override
	public void setEntriesAgainstTransaction(final PaymentTransactionModel paymentTransaction, final List<Integer> entryNumbers)
	{
		final List<AbstractOrderEntryModel> entriesToSet = Objects.nonNull(getSessionService().getAttribute(SESSION_PAY_NOW))
				? paymentTransaction.getOrder().getEntries().stream().filter(entry -> entryNumbers.contains(entry.getEntryNumber()))
						.collect(Collectors.toList())
				: paymentTransaction.getOrder().getEntries().stream()
				.filter(entry -> Arrays.asList(AmendStatus.NEW, AmendStatus.CHANGED).contains(entry.getAmendStatus()))
				.filter(entry -> entryNumbers.contains(entry.getEntryNumber())).collect(Collectors.toList());
		paymentTransaction.setAbstractOrderEntries(entriesToSet);
		getModelService().save(paymentTransaction);
	}

	@Override
	public PaymentTransactionModel getExistingTransaction(final List<Integer> entryNumbers,
			final CommerceCheckoutParameter parameter)
	{
		final AbstractOrderModel originalOrder = parameter.getCart().getOriginalOrder();
		if (Objects.isNull(originalOrder))
		{
			return null;
		}
		final List<AbstractOrderEntryModel> cartEntries = parameter.getCart().getEntries().stream()
				.filter(entry -> entryNumbers.contains(entry.getEntryNumber())).collect(Collectors.toList());
		final Optional<PaymentTransactionModel> optionalPaymentTransaction = originalOrder.getPaymentTransactions().stream()
				.filter(transaction -> CollectionUtils
						.isNotEmpty(CollectionUtils.intersection(cartEntries, transaction.getAbstractOrderEntries())))
				.findAny();
		return optionalPaymentTransaction.isPresent() ? optionalPaymentTransaction.get() : null;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param abstractOrder
	 *           the abstract order
	 * @param amountToRefund
	 *           the amount to refund
	 * @param entries
	 *           the entries the transaction is linked to
	 * @return
	 */
	@Deprecated
	@Override
	public PaymentTransactionModel createRefundPaymentTransaction(final AbstractOrderModel abstractOrder,
			final BigDecimal amountToRefund, final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isEmpty(abstractOrder.getPaymentTransactions()))
		{
			LOG.error("No original payment transactions found on the order: " + abstractOrder.getCode()
					+ ". Cannot create refund payment transaction");
			return null;
		}
		final PaymentTransactionModel oldPaymentTransaction = abstractOrder.getPaymentTransactions().get(0);

		final PaymentTransactionModel refundTransaction = getModelService().create(PaymentTransactionModel.class);
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.REFUND_STANDALONE;
		refundTransaction.setCode(getUserService().getCurrentUser().getUid() + "_" + UUID.randomUUID());
		refundTransaction.setPaymentProvider(getPaymentProvider());
		refundTransaction.setCurrency(oldPaymentTransaction.getCurrency());
		refundTransaction.setRequestId(oldPaymentTransaction.getRequestId());
		refundTransaction.setRequestToken(oldPaymentTransaction.getRequestToken());
		refundTransaction.setInfo(oldPaymentTransaction.getInfo());
		refundTransaction.setPlannedAmount(amountToRefund);
		refundTransaction.setOrder(abstractOrder);

		if (CollectionUtils.isNotEmpty(entries))
		{
			refundTransaction.setAbstractOrderEntries(entries);
		}

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

		getModelService().save(entry);

		return refundTransaction;
	}

	@Override
	public boolean linkEntriesToOldPaymentTransactions(final AccommodationOrderEntryGroupModel orderEntryGroup,
			final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isEmpty(entries) || orderEntryGroup == null)
		{
			return Boolean.FALSE;
		}
		orderEntryGroup.getEntries().stream().filter(entry -> CollectionUtils.isNotEmpty(entry.getPaymentTransaction()))
				.forEach(entry -> entry.getPaymentTransaction().forEach(paymentTransaction -> {
					final List<AbstractOrderEntryModel> oldEntries = paymentTransaction.getAbstractOrderEntries().stream()
							.filter(orderEntry -> orderEntry.getProduct() instanceof RoomRateProductModel).collect(Collectors.toList());

					if (CollectionUtils.isNotEmpty(oldEntries))
					{
						final List<AbstractOrderEntryModel> newEntries = new ArrayList<>(paymentTransaction.getAbstractOrderEntries());
						newEntries.removeAll(oldEntries);
						newEntries.addAll(entries);
						paymentTransaction.setAbstractOrderEntries(newEntries);
					}
				}));
		return Boolean.TRUE;
	}

	@Override
	public Boolean createRefundPaymentTransactionEntries(final AbstractOrderModel abstractOrder,
			final List<AbstractOrderEntryModel> entries)
	{
		if (CollectionUtils.isEmpty(abstractOrder.getPaymentTransactions()))
		{
			return Boolean.FALSE;
		}


		final Map<OrderEntryType, List<AbstractOrderEntryModel>> entriesByType = entries.stream()
				.collect(Collectors.groupingBy(AbstractOrderEntryModel::getType));
		for (final Map.Entry<OrderEntryType, List<AbstractOrderEntryModel>> mapEntry : entriesByType.entrySet())
		{
			try
			{
				getRefundPaymentTransactionStrategyMap().get(mapEntry.getKey()).handleRefund(abstractOrder, mapEntry.getKey(),
						mapEntry.getValue());
			}
			catch (final ModelSavingException modEx)
			{
				LOG.error("Error occurred while updating existing payment transactions", modEx);
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	/**
	 * @return the baseStoreService
	 */
	@Override
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Override
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 *
	 * @return the refundPaymentTransactionStrategyMap
	 */
	protected Map<OrderEntryType, RefundPaymentTransactionStrategy> getRefundPaymentTransactionStrategyMap()
	{
		return refundPaymentTransactionStrategyMap;
	}

	/**
	 *
	 * @param refundPaymentTransactionStrategyMap
	 *           the refundPaymentTransactionStrategyMap to set
	 */
	@Required
	public void setRefundPaymentTransactionStrategyMap(
			final Map<OrderEntryType, RefundPaymentTransactionStrategy> refundPaymentTransactionStrategyMap)
	{
		this.refundPaymentTransactionStrategyMap = refundPaymentTransactionStrategyMap;
	}

	/**
	 * 
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * 
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}





}
