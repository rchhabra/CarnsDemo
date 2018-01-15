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
package de.hybris.platform.storefront.controllers.pages.checkout.steps;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelcheckout.constants.TravelcheckoutWebConstants;
import de.hybris.platform.travelcheckout.controllers.TravelcheckoutControllerConstants;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.customer.TravelCustomerFacade;
import de.hybris.platform.travelfacades.order.TravelB2BCheckoutFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = "/checkout/multi/summary")
public class SummaryCheckoutStepController extends AbstractCheckoutStepController
{
	private static final String SUMMARY = "summary";
	private static final String HOME_PAGE_PATH = "/";

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource(name = "travelCustomerFacade")
	private TravelCustomerFacade travelCustomerFacade;

	@Resource(name = "b2bCheckoutFacade")
	private TravelB2BCheckoutFacade b2bCheckoutFacade;

	@Resource(name = "defaultB2BAcceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade b2BAcceleratorCheckoutFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{

		if (!travelCartFacade.hasEntries())
		{
			return REDIRECT_PREFIX + HOME_PAGE_PATH;
		}

		// Only request the security code if the SubscriptionPciOption is set to Default.
		final boolean requestSecurityCode = (CheckoutPciOptionEnum.DEFAULT
				.equals(getCheckoutFlowFacade().getSubscriptionPciOption()));
		model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

		model.addAttribute(new PlaceOrderForm());

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		model.addAttribute(TravelcheckoutWebConstants.AMEND, travelCartFacade.isAmendmentCart());

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}

		model.addAttribute(TravelacceleratorstorefrontWebConstants.CART_TOTAL, travelCartFacade.getCartTotal());

		final PriceData totalAmount = travelCartFacade.getBookingTotal(travelCartFacade.getOriginalOrderCode());
		model.addAttribute(TravelacceleratorstorefrontWebConstants.RESERVATION_TOTAL,
				totalAmount);

		final PriceData partialPaymentAmount = travelCartFacade.getPartialPaymentAmount();
		if (Objects.nonNull(partialPaymentAmount))
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.SELECTED_PAYMENT, partialPaymentAmount);
		}

		return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
	}


	/**
	 * @param placeOrderForm
	 *           The spring form of the order being submitted
	 * @param model
	 * @param request
	 * @param redirectModel
	 * @return String
	 * @throws CMSItemNotFoundException
	 * @throws InvalidCartException
	 * @throws CommerceCartModificationException
	 */
	@RequestMapping(value = "/placeOrder")
	@RequireHardLogIn
	public String placeOrder(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectModel)
			throws CMSItemNotFoundException, InvalidCartException, CommerceCartModificationException
	{

		if (validateOrderForm(placeOrderForm, model))
		{
			return enterStep(model, redirectModel);
		}

		//Validate the cart
		if (validateCart(redirectModel))
		{
			// Invalid cart. Bounce back to the home page.
			travelCartFacade.deleteCurrentCart();
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.error.cart.validatecart");
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW);
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES);
			return REDIRECT_PREFIX + ROOT;
		}

		// authorize, if failure occurs don't allow to place the order
		boolean isPaymentAuthorized = false;
		try
		{
			if (travelCustomerFacade.isCurrentUserB2bCustomer())
			{
				isPaymentAuthorized = b2bCheckoutFacade.authorizePayment(placeOrderForm.getSecurityCode());
			}
			else
			{
				isPaymentAuthorized = getCheckoutFacade().authorizePayment(placeOrderForm.getSecurityCode());
			}

		}
		catch (final AdapterException ae)
		{
			// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService()
			// .getPaymentProvider()
			LOG.error(ae.getMessage(), ae);
		}
		if (!isPaymentAuthorized)
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW);
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES);
			return enterStep(model, redirectModel);
		}

		final OrderData orderData;
		try
		{
			if (travelCustomerFacade.isCurrentUserB2bCustomer())
			{

				final PlaceOrderData placeOrderData = new PlaceOrderData();
				placeOrderData.setSecurityCode(placeOrderForm.getSecurityCode());
				placeOrderData.setTermsCheck(placeOrderForm.isTermsCheck());

				orderData = b2bCheckoutFacade.placeOrder(placeOrderData);
			}
			else
			{
				orderData = getCheckoutFacade().placeOrder();
			}
		}
		catch (final Exception e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW);
			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES);
			return enterStep(model, redirectModel);
		}

		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW);
		sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES);
		return redirectToOrderConfirmationPage(orderData);
	}

	@Override
	protected String redirectToOrderConfirmationPage(final OrderData orderData)
	{
		final StringBuilder orderCodeToAppend = new StringBuilder();
		//if this is amend order flow, then original order code won't be null
		if (StringUtils.isNotBlank(orderData.getOriginalOrderCode()))
		{
			orderCodeToAppend.append(orderData.getOriginalOrderCode());
		}
		else
		{
			orderCodeToAppend.append(orderData.getCode());
		}
		return TravelcheckoutWebConstants.REDIRECT_URL_BOOKING_CONFIRMATION + orderCodeToAppend.toString();
	}

	/**
	 * Validates the order form before to filter out invalid order states
	 *
	 * @param placeOrderForm
	 *           The spring form of the order being submitted
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	protected boolean validateOrderForm(final PlaceOrderForm placeOrderForm, final Model model)
	{
		final String securityCode = placeOrderForm.getSecurityCode();
		boolean invalid = false;

		if (!travelCustomerFacade.isCurrentUserB2bCustomer() && getCheckoutFlowFacade().hasNoPaymentInfo())
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		else if (travelCustomerFacade.isCurrentUserB2bCustomer() && b2BAcceleratorCheckoutFacade.hasNoPaymentInfo())
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		else
		{
			// Only require the Security Code to be entered on the summary page if the SubscriptionPciOption is set to Default.
			if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption())
					&& StringUtils.isBlank(securityCode))
			{
				GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
				invalid = true;
			}
		}

		if (!placeOrderForm.isTermsCheck())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
			invalid = true;
			return invalid;
		}
		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().containsTaxValues())
		{
			LOG.error(String.format(
					"Cart %s does not have any tax values, which means the tax calculation was not properly done, placement of order "
							+ "can't continue",
					cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(
					String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}

	@ModelAttribute("reservationCode")
	public String getReservationCode()
	{
		if (travelCartFacade.isAmendmentCart())
		{
			return travelCartFacade.getOriginalOrderCode();
		}
		return travelCartFacade.getCurrentCartCode();
	}

	@ModelAttribute("disableCurrencySelector")
	public Boolean getDisableCurrencySelector()
	{
		return Boolean.TRUE;
	}
	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SUMMARY);
	}
}
