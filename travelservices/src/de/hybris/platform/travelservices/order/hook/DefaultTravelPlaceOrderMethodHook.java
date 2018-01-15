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

package de.hybris.platform.travelservices.order.hook;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.stock.exception.InsufficientStockLevelException;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.TravelOrderCodeGenerationStrategy;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Travel Custom hook to replace default behaviour before and after place order
 */
public class DefaultTravelPlaceOrderMethodHook implements CommercePlaceOrderMethodHook
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelPlaceOrderMethodHook.class);

	private ModelService modelService;
	private CalculationService calculationService;
	private ExternalTaxesService externalTaxesService;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private OrderHistoryService orderHistoryService;
	private TravelCommerceStockService commerceStockService;
	private BookingService bookingService;
	private SessionService sessionService;
	private EnumerationService enumerationService;
	private TravelOrderCodeGenerationStrategy travelOrderCodeGenerationStrategy;

	@Override
	public void beforePlaceOrder(final CommerceCheckoutParameter parameter) throws InvalidCartException
	{
		final CartModel cart = parameter.getCart();
		try
		{
			if (isAmendFlow(cart))
			{
				final OrderModel originalOrder = cart.getOriginalOrder();
				getCommerceStockService().adjustStockReservationForAmmendment(cart, originalOrder);
				originalOrder.setStatus(OrderStatus.AMENDMENTINPROGRESS);
				getModelService().save(originalOrder);
			}
			else
			{
				getCommerceStockService().reserve(cart);
			}
		}
		catch (final InsufficientStockLevelException e)
		{
			LOG.error("Insufficient Stock when placing order", e);
			throw new InvalidCartException(e.getMessage());
		}
	}

	@Override
	public void afterPlaceOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		final AbstractOrderModel orderModel = result.getOrder();
		if (!Objects.isNull(orderModel) && Objects.isNull(orderModel.getOriginalOrder()))
		{
			getTravelOrderCodeGenerationStrategy().generateTravelOrderCode(orderModel);
		}
	}

	/**
	 * Method responsible for checking if the current flow is amendment or new booking
	 *
	 * @param cart
	 * @return
	 */
	protected boolean isAmendFlow(final CartModel cart)
	{
		return cart.getOriginalOrder() != null;
	}

	@Override
	public void beforeSubmitOrder(final CommerceCheckoutParameter parameter, final CommerceOrderResult result)
			throws InvalidCartException
	{
		if (null == result.getOrder())
		{
			return;
		}
		final OrderModel orderModel = result.getOrder();

		setBookingJourney(orderModel, parameter.getCart());
		setDeliveryAddress(orderModel);
		calculcateTotalAndTaxes(orderModel);

		getBookingService().getAccommodationOrderEntryGroups(orderModel).forEach(entryGroup -> {
			getModelService().refresh(entryGroup);
			final List<AbstractOrderEntryModel> orderEntries = entryGroup.getEntries().stream()
					.filter(entry -> entry instanceof OrderEntryModel).collect(Collectors.toList());
			entryGroup.setEntries(orderEntries);
			getModelService().save(entryGroup);
		});

		getModelService().refresh(orderModel);

		result.setOrder(orderModel);
	}

	/**
	 * Sets the bookingJourneyType in the orderModel. If the originalOrderModel is not null and it has a bookingJourneyType then
	 * it is set against the orderModel, otherwise it is taken from the session. If the sessionBookingJourney is not valid,
	 * nothing will be set.
	 *  @param orderModel
	 * 		as the order model
	 * @param cartModel
	 */
	protected void setBookingJourney(final OrderModel orderModel, final CartModel cartModel)
	{
		BookingJourneyType bookingJourneyType = null;
		final OrderModel originalOrderModel = cartModel.getOriginalOrder();
		if (Objects.nonNull(originalOrderModel) && Objects.nonNull(originalOrderModel.getBookingJourneyType()))
		{
			bookingJourneyType = originalOrderModel.getBookingJourneyType();
		}
		else
		{
			bookingJourneyType = cartModel.getBookingJourneyType();
			if(Objects.isNull(bookingJourneyType))
			{
				final String sessionBookingJourney = sessionService.getAttribute("sessionBookingJourney");
				if (StringUtils.isNotBlank(sessionBookingJourney))
				{
					bookingJourneyType = getEnumerationService().getEnumerationValue(BookingJourneyType.class, sessionBookingJourney);
					if (Objects.isNull(bookingJourneyType))
					{
						LOG.error(sessionBookingJourney + " is not a valid value for the BookingJourneyType.");
					}
				}
			}
		}

		if (Objects.nonNull(bookingJourneyType))
		{
			orderModel.setBookingJourneyType(bookingJourneyType);
		}
	}

	protected void setDeliveryAddress(final OrderModel orderModel)
	{
		if (orderModel.getPaymentInfo() != null && orderModel.getPaymentInfo().getBillingAddress() != null)
		{
			final AddressModel billingAddress = orderModel.getPaymentInfo().getBillingAddress();
			orderModel.setDeliveryAddress(billingAddress);
			getModelService().save(orderModel);
		}
	}

	protected void calculcateTotalAndTaxes(final OrderModel orderModel)
	{
		try
		{
			getCalculationService().calculateTotals(orderModel, false);
			getExternalTaxesService().calculateExternalTaxes(orderModel);
		}
		catch (final CalculationException ex)
		{
			LOG.error("Failed to calculate order [" + orderModel + "]", ex);
		}
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the calculationService
	 */
	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	/**
	 * @param calculationService the calculationService to set
	 */
	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}

	/**
	 * @return the externalTaxesService
	 */
	protected ExternalTaxesService getExternalTaxesService()
	{
		return externalTaxesService;
	}

	/**
	 * @param externalTaxesService the externalTaxesService to set
	 */
	@Required
	public void setExternalTaxesService(final ExternalTaxesService externalTaxesService)
	{
		this.externalTaxesService = externalTaxesService;
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
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
	 *           the orderHistoryService to set
	 */
	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	/**
	 * @return the commerceStockService
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * @param commerceStockService the commerceStockService to set
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 *
	 * @return travelOrderCodeGenerationStrategy
	 */
	protected TravelOrderCodeGenerationStrategy getTravelOrderCodeGenerationStrategy()
	{
		return travelOrderCodeGenerationStrategy;
	}

	/**
	 *
	 * @param travelOrderCodeGenerationStrategy
	 *           the travelOrderCodeGenerationStrategy to set
	 */
	@Required
	public void setTravelOrderCodeGenerationStrategy(final TravelOrderCodeGenerationStrategy travelOrderCodeGenerationStrategy)
	{
		this.travelOrderCodeGenerationStrategy = travelOrderCodeGenerationStrategy;
	}


}
