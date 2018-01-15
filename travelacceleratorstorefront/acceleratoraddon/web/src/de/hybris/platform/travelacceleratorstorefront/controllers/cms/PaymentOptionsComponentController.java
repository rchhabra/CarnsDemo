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

package de.hybris.platform.travelacceleratorstorefront.controllers.cms;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.SelectPaymentOptionResponseData;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.forms.PaymentOptionForm;
import de.hybris.platform.travelacceleratorstorefront.model.components.PaymentOptionsComponentModel;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



@Controller("PaymentOptionsComponentController")
@RequestMapping(value = TravelacceleratorstorefrontControllerConstants.Actions.Cms.PaymentOptionsComponent)
public class PaymentOptionsComponentController extends SubstitutingCMSAddOnComponentController<PaymentOptionsComponentModel>
{
	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final PaymentOptionsComponentModel component)
	{

		if (Objects.nonNull(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS)))
		{
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS);
		}

		final List<PaymentOptionData> paymentOptions = new ArrayList<>();
		PriceData totalPrice;
		final String originalOrderCode = travelCartFacade.getOriginalOrderCode();
		if (StringUtils.equals(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW),
				originalOrderCode) && !travelCartFacade.hasCartBeenAmended())
		{
			paymentOptions.addAll(travelCartFacade.getPaymentOptions(OrderEntryType.ACCOMMODATION));
			totalPrice = travelCommercePriceFacade.createPriceData(
					bookingFacade.getOrderTotalToPayForOrderEntryType(originalOrderCode, OrderEntryType.ACCOMMODATION).doubleValue(),
					2);
		}
		else
		{
			paymentOptions.addAll(travelCartFacade.getPaymentOptions());
			totalPrice = travelCartFacade.getTotalToPayPrice();
		}

		if (CollectionUtils.isNotEmpty(paymentOptions))
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_OPTIONS, paymentOptions);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.RESERVATION_TOTAL, totalPrice);
			final PaymentOptionForm paymentOptionForm = new PaymentOptionForm();
			paymentOptionForm.setTransactions(new ArrayList<>());
			model.addAttribute("paymentOptionForm", paymentOptionForm);
			if (CollectionUtils.size(paymentOptions) == 1)
			{
				sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS,
						paymentOptions.stream().findFirst().get().getAssociatedTransactions());
			}
		}

	}

	@RequestMapping(value = "/select-payment-option", method = RequestMethod.POST, produces = "application/json")
	public String selectPaymentOption(final PaymentOptionForm paymentOptionForm, final BindingResult bindingResult,
			final HttpServletRequest request, final Model model)
	{
		final List<PaymentTransactionData> transactions = convertFormIntoTransactionData(paymentOptionForm);

		if (!travelCartFacade.isValidPaymentOption(transactions))
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_OPTION_RESPONSE_DATA,
					buildResponseData(false, TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_JS_ERROR));
			return TravelacceleratorstorefrontControllerConstants.Views.Pages.Checkout.SelectPaymentResponse;
		}
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS, transactions);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_OPTION_RESPONSE_DATA,
				buildResponseData(true, null));
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.Checkout.SelectPaymentResponse;
	}

	private List<PaymentTransactionData> convertFormIntoTransactionData(final PaymentOptionForm paymentOptionForm)
	{
		final List<PaymentTransactionData> transactions = new ArrayList<>();
		paymentOptionForm.getTransactions().forEach(transaction -> {
			final PaymentTransactionData transactionData = new PaymentTransactionData();
			transactionData.setTransactionAmount(Double.valueOf(transaction.getAmount()));
			transactionData
					.setEntryNumbers(transaction.getEntryNumbers().stream().map(Integer::valueOf).collect(Collectors.toList()));
			transactions.add(transactionData);
		});
		return transactions;
	}

	protected SelectPaymentOptionResponseData buildResponseData(final boolean valid, final String error)
	{
		final SelectPaymentOptionResponseData response = new SelectPaymentOptionResponseData();
		response.setErrors(Collections.singletonList(error));
		response.setValid(valid);
		return response;
	}


}
