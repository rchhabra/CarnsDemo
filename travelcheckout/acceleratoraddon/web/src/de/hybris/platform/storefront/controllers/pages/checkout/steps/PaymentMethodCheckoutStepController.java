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


import de.hybris.platform.acceleratorfacades.flow.impl.SessionOverrideCheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.payment.data.PaymentData;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.SopPaymentDetailsForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.SelectPaymentOptionResponseData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.data.VoucherData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelcheckout.constants.TravelcheckoutWebConstants;
import de.hybris.platform.travelcheckout.controllers.TravelcheckoutControllerConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = "/checkout/multi/payment-method")
public class PaymentMethodCheckoutStepController extends AbstractCheckoutStepController
{
	protected static final Map<String, String> CYBERSOURCE_SOP_CARD_TYPES = new HashMap<String, String>();
	private static final String PAYMENT_METHOD = "payment-method";
	private static final String PAYMENT_METHOD_CMS_PAGE_LABEL = "paymentMethodPage";
	private static final String ACTION_PAY = "actionPay";
	private static final String ACTION_REFUND = "actionRefund";
	private static final String NO_ACTION = "noAction";
	private static final String APPLIED_VOUCHER_CODES = "appliedVoucherCodes";

	@Resource(name = "enumerationService")
	private EnumerationService enumerationService;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "voucherFacade")
	private VoucherFacade voucherFacade;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return getCheckoutFacade().getBillingCountries();
	}

	@ModelAttribute("cardTypes")
	public Collection<CardTypeData> getCardTypes()
	{
		return getCheckoutFacade().getSupportedCardTypes();
	}

	@ModelAttribute("months")
	protected List<SelectOption> getMonths()
	{
		final List<SelectOption> months = new ArrayList<SelectOption>();

		months.add(new SelectOption("1", "01"));
		months.add(new SelectOption("2", "02"));
		months.add(new SelectOption("3", "03"));
		months.add(new SelectOption("4", "04"));
		months.add(new SelectOption("5", "05"));
		months.add(new SelectOption("6", "06"));
		months.add(new SelectOption("7", "07"));
		months.add(new SelectOption("8", "08"));
		months.add(new SelectOption("9", "09"));
		months.add(new SelectOption("10", "10"));
		months.add(new SelectOption("11", "11"));
		months.add(new SelectOption("12", "12"));

		return months;
	}

	@ModelAttribute("startYears")
	public List<SelectOption> getStartYears()
	{
		final List<SelectOption> startYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i > (calender.get(Calendar.YEAR) - 6); i--)
		{
			startYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return startYears;
	}

	@ModelAttribute("expiryYears")
	public List<SelectOption> getExpiryYears()
	{
		final List<SelectOption> expiryYears = new ArrayList<SelectOption>();
		final Calendar calender = new GregorianCalendar();

		for (int i = calender.get(Calendar.YEAR); i < (calender.get(Calendar.YEAR) + 11); i++)
		{
			expiryYears.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
		}

		return expiryYears;
	}

	@RequestMapping(value = "/select-flow", method = RequestMethod.GET)
	@RequireHardLogIn
	public String initCheck(final Model model, final RedirectAttributes redirectModel,
			@RequestParam(value = "pci", required = false) final String pci) throws CommerceCartModificationException
	{
		if (getTravelCartFacade().isAmendmentCart())
		{
			PriceData totalToPay;

			final String originalOrderCode = travelCartFacade.getOriginalOrderCode();
			if (StringUtils.equals(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_PAY_NOW),
					originalOrderCode) && !travelCartFacade.hasCartBeenAmended())
			{
				totalToPay = travelCommercePriceFacade.createPriceData(bookingFacade
						.getOrderTotalToPayForOrderEntryType(originalOrderCode, OrderEntryType.ACCOMMODATION).doubleValue());
			}
			else if (StringUtils.equals(sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_CHANGE_DATES),
					originalOrderCode))
			{
				totalToPay = travelCommercePriceFacade
						.createPriceData(bookingFacade.getOrderTotalToPayForChangeDates().doubleValue());
			}
			else
			{
				totalToPay = travelCartFacade.getTotalToPayPrice();
			}

			switch (definePaymentAction(totalToPay))
			{
				case ACTION_REFUND:
					return refund(model, totalToPay);
				case NO_ACTION:
					return placeOrder(model);
				case ACTION_PAY:
					getTravelCartFacade().removeDeliveryAddress();
					break;
			}

		}

		SessionOverrideCheckoutFlowFacade.resetSessionOverrides();

		// Override the Checkout PCI setting in the session
		if (StringUtils.isNotBlank(pci))
		{
			final CheckoutPciOptionEnum checkoutPci = getEnumerationService().getEnumerationValue(CheckoutPciOptionEnum.class,
					StringUtils.upperCase(pci));
			SessionOverrideCheckoutFlowFacade.setSessionOverrideSubscriptionPciOption(checkoutPci);
		}

		// Redirect to the start of the checkout flow to begin the checkout process
		// We just redirect to the generic '/checkout' page which will actually select the checkout flow
		// to use. The customer is not necessarily logged in on this request, but will be forced to login
		// when they arrive on the '/checkout' page.
		return REDIRECT_PREFIX + "/checkout/multi/payment-method/add";
	}

	@Override
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		if (!travelCartFacade.isCurrentCartValid())
		{
			return REDIRECT_PREFIX + "/";
		}

		setupAddPaymentPage(model);

		// Use the checkout PCI strategy for getting the URL for creating new subscriptions.
		final CheckoutPciOptionEnum subscriptionPciOption = getCheckoutFlowFacade().getSubscriptionPciOption();
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		if (CheckoutPciOptionEnum.HOP.equals(subscriptionPciOption))
		{
			// Redirect the customer to the HOP page or show error message if it fails (e.g. no HOP configurations).
			try
			{
				final PaymentData hostedOrderPageData = getPaymentFacade().beginHopCreateSubscription("/checkout/multi/hop/response",
						"/integration/merchant_callback");
				model.addAttribute("hostedOrderPageData", hostedOrderPageData);

				final boolean hopDebugMode = getSiteConfigService().getBoolean(PaymentConstants.PaymentProperties.HOP_DEBUG_MODE,
						false);
				model.addAttribute("hopDebugMode", Boolean.valueOf(hopDebugMode));

				return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.HostedOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
			}
		}
		else if (CheckoutPciOptionEnum.SOP.equals(subscriptionPciOption))
		{
			// Build up the SOP form data and render page containing form
			final SopPaymentDetailsForm sopPaymentDetailsForm = new SopPaymentDetailsForm();
			try
			{
				setupSilentOrderPostPage(sopPaymentDetailsForm, model);
				return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.SilentOrderPostPage;
			}
			catch (final Exception e)
			{
				LOG.error("Failed to build beginCreateSubscription request", e);
				GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.addPaymentDetails.generalError");
				model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
			}
		}

		// If not using HOP or SOP we need to build up the payment details form
		final PaymentDetailsForm paymentDetailsForm = new PaymentDetailsForm();
		final AddressForm addressForm = new AddressForm();
		paymentDetailsForm.setBillingAddress(addressForm);
		model.addAttribute(paymentDetailsForm);

		return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
	}

	@RequestMapping(value = { "/add" }, method = RequestMethod.POST)
	@RequireHardLogIn
	public String add(final Model model, @Valid final PaymentDetailsForm paymentDetailsForm, final BindingResult bindingResult)
			throws CMSItemNotFoundException
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
		{
			if (CollectionUtils
					.isEmpty(getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS)))
			{
				GlobalMessages.addErrorMessage(model, "payment.option.no.payment.selected");
				return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
			}
		}
		getPaymentDetailsValidator().validate(paymentDetailsForm, bindingResult);
		setupAddPaymentPage(model);


		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.paymentethod.formentry.invalid");
			return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setId(paymentDetailsForm.getPaymentId());
		paymentInfoData.setCardType(paymentDetailsForm.getCardTypeCode());
		paymentInfoData.setAccountHolderName(paymentDetailsForm.getNameOnCard());
		paymentInfoData.setCardNumber(paymentDetailsForm.getCardNumber());
		paymentInfoData.setStartMonth(paymentDetailsForm.getStartMonth());
		paymentInfoData.setStartYear(paymentDetailsForm.getStartYear());
		paymentInfoData.setExpiryMonth(paymentDetailsForm.getExpiryMonth());
		paymentInfoData.setExpiryYear(paymentDetailsForm.getExpiryYear());
		if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) || getCheckoutCustomerStrategy().isAnonymousCheckout())
		{
			paymentInfoData.setSaved(true);
		}
		paymentInfoData.setIssueNumber(paymentDetailsForm.getIssueNumber());

		final AddressData addressData;

		final AddressForm addressForm = paymentDetailsForm.getBillingAddress();
		addressData = new AddressData();
		if (addressForm != null)
		{
			addressData.setId(addressForm.getAddressId());
			addressData.setTitleCode(addressForm.getTitleCode());
			addressData.setFirstName(addressForm.getFirstName());
			addressData.setLastName(addressForm.getLastName());
			addressData.setLine1(addressForm.getLine1());
			addressData.setLine2(addressForm.getLine2());
			addressData.setTown(addressForm.getTownCity());
			addressData.setPostalCode(addressForm.getPostcode());
			addressData.setCountry(getI18NFacade().getCountryForIsocode(addressForm.getCountryIso()));
			if (addressForm.getRegionIso() != null)
			{
				addressData.setRegion(getI18NFacade().getRegion(addressForm.getCountryIso(), addressForm.getRegionIso()));
			}

			addressData.setBillingAddress(Boolean.TRUE.equals(addressForm.getBillingAddress()));
		}

		getAddressVerificationFacade().verifyAddressData(addressData);
		paymentInfoData.setBillingAddress(addressData);

		final CCPaymentInfoData newPaymentSubscription = getCheckoutFacade().createPaymentSubscription(paymentInfoData);
		if (newPaymentSubscription != null && StringUtils.isNotBlank(newPaymentSubscription.getSubscriptionId()))
		{
			if (Boolean.TRUE.equals(paymentDetailsForm.getSaveInAccount()) && getUserFacade().getCCPaymentInfos(true).size() <= 1)
			{
				getUserFacade().setDefaultPaymentInfo(newPaymentSubscription);
			}
			getCheckoutFacade().setPaymentDetails(newPaymentSubscription.getId());
		}
		else
		{
			GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.createSubscription.failedMsg");
			return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethodPage;
		}

		model.addAttribute("paymentId", newPaymentSubscription.getId());
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		return getCheckoutStep().nextStep();
	}


	@RequestMapping(value = "/remove", method = RequestMethod.POST)
	@RequireHardLogIn
	public String remove(@RequestParam(value = "paymentInfoId") final String paymentMethodId,
			final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException
	{
		getUserFacade().unlinkCCPaymentInfo(paymentMethodId);
		GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.CONF_MESSAGES_HOLDER,
				"text.account.profile.paymentCart" + ".removed");
		return getCheckoutStep().currentStep();
	}

	/**
	 * This method gets called when the "Use These Payment Details" button is clicked. It sets the selected payment
	 * method on the checkout facade and reloads the page highlighting the selected payment method.
	 *
	 * @param selectedPaymentMethodId
	 *           - the id of the payment method to use.
	 * @return - a URL to the page to load.
	 */
	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	public String doSelectPaymentMethod(@RequestParam("selectedPaymentMethodId") final String selectedPaymentMethodId,
			final RedirectAttributes model)
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
		{
			if (CollectionUtils
					.isEmpty(getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS)))
			{
				GlobalMessages.addFlashMessage(model, GlobalMessages.ERROR_MESSAGES_HOLDER, "payment.option.no.payment.selected");
				return getCheckoutStep().currentStep();
			}
		}

		if (StringUtils.isNotBlank(selectedPaymentMethodId))
		{
			getCheckoutFacade().setPaymentDetails(selectedPaymentMethodId);
		}
		return getCheckoutStep().nextStep();
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

	@RequestMapping(value = "/check-payment-options", method = RequestMethod.GET, produces = "application/json")
	@RequireHardLogIn
	public String isPaymentOptionSelected(final Model model)
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
		{
			if (CollectionUtils
					.isEmpty(getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_TRANSACTIONS)))
			{
				model.addAttribute(TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_OPTION_RESPONSE_DATA,
						buildResponseData(false, TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_JS_ERROR));
				return TravelacceleratorstorefrontControllerConstants.Views.Pages.Checkout.SelectPaymentResponse;

			}
		}
		model.addAttribute(TravelacceleratorstorefrontWebConstants.SELECT_PAYMENT_OPTION_RESPONSE_DATA,
				buildResponseData(true, null));
		return TravelacceleratorstorefrontControllerConstants.Views.Pages.Checkout.SelectPaymentResponse;
	}

	protected SelectPaymentOptionResponseData buildResponseData(final boolean valid, final String error)
	{
		final SelectPaymentOptionResponseData response = new SelectPaymentOptionResponseData();
		response.setErrors(Collections.singletonList(error));
		response.setValid(valid);
		return response;
	}

	protected CardTypeData createCardTypeData(final String code, final String name)
	{
		final CardTypeData cardTypeData = new CardTypeData();
		cardTypeData.setCode(code);
		cardTypeData.setName(name);
		return cardTypeData;
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

	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		model.addAttribute(TravelcheckoutWebConstants.AMEND, travelCartFacade.isAmendmentCart());
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(PAYMENT_METHOD_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}

		setModelWithCartVouchers(model);
	}

	protected void setupSilentOrderPostPage(final SopPaymentDetailsForm sopPaymentDetailsForm, final Model model)
	{
		try
		{
			final PaymentData silentOrderPageData = getPaymentFacade().beginSopCreateSubscription("/checkout/multi/sop/response",
					"/integration/merchant_callback");
			model.addAttribute("silentOrderPageData", silentOrderPageData);
			sopPaymentDetailsForm.setParameters(silentOrderPageData.getParameters());
			model.addAttribute("paymentFormUrl", silentOrderPageData.getPostUrl());
		}
		catch (final IllegalArgumentException e)
		{
			model.addAttribute("paymentFormUrl", "");
			model.addAttribute("silentOrderPageData", null);
			LOG.warn("Failed to set up silent order post page " + e.getMessage());
			GlobalMessages.addErrorMessage(model, "checkout.multi.sop.globalError");
		}

		model.addAttribute("silentOrderPostForm", new PaymentDetailsForm());
		model.addAttribute("sopPaymentDetailsForm", sopPaymentDetailsForm);
		model.addAttribute("paymentInfos", getUserFacade().getCCPaymentInfos(true));
		model.addAttribute("sopCardTypes", getSopCardTypes());
		model.addAttribute(TravelcheckoutWebConstants.HIDE_CONTINUE, "HIDE");
		if (StringUtils.isNotBlank(sopPaymentDetailsForm.getBillTo_country()))
		{
			model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(sopPaymentDetailsForm.getBillTo_country()));
			model.addAttribute("country", sopPaymentDetailsForm.getBillTo_country());
		}
	}

	/**
	 * Creates a refund transaction to demonstrate refund process for the order
	 *
	 * @param model
	 * @param totalToPay
	 *           - for refund it will be a negative value stating how much should be refunded to customer
	 * @return booking confirmation if refund was successful, payment details with errors otherwise
	 */
	protected String refund(final Model model, final PriceData totalToPay)
	{
		final Boolean isRefundSuccessful = getBookingFacade().createRefundPaymentTransaction(totalToPay);
		if (!isRefundSuccessful)
		{
			GlobalMessages.addErrorMessage(model, "checkout.refund.failed");
			return TravelcheckoutControllerConstants.Views.Pages.Checkout.PaymentDetailsPage;
		}
		return placeOrder(model);
	}

	/**
	 * Skips the payment part of order process to place order in case there was nothing to pay or refund in amendment
	 * journey
	 *
	 * @param model
	 * @return booking confirmation if placing order was successful, payment details with errors otherwise
	 */
	protected String placeOrder(final Model model)
	{
		OrderData order = null;
		try
		{
			order = getCheckoutFacade().placeOrder();
		}
		catch (final InvalidCartException e)
		{
			LOG.error("Failed to place Order", e);
			GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
			return TravelcheckoutControllerConstants.Views.Pages.Checkout.PaymentDetailsPage;
		}

		return redirectToOrderConfirmationPage(order);
	}

	@Override
	protected String redirectToOrderConfirmationPage(final OrderData orderData)
	{
		final StringBuilder orderCodeToAppend = new StringBuilder();
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
	 * Checks the toPay value to define if user needs to pay or be refunded
	 *
	 * @param totalToPay
	 * @return PAY, NO_ACTION or REFUND
	 */
	protected String definePaymentAction(final PriceData totalToPay)
	{
		final double toPayValue = totalToPay.getValue().doubleValue();
		if (toPayValue < 0d)
		{
			return ACTION_REFUND;
		}
		else if (toPayValue == 0d)
		{
			return NO_ACTION;
		}
		return ACTION_PAY;
	}

	/**
	 * Method to retrieve and set all the applied cart vouchers in the model.
	 *
	 * @param model
	 */
	protected void setModelWithCartVouchers(final Model model)
	{
		final List<VoucherData> vouchers = voucherFacade.getVouchersForCart();
		if (CollectionUtils.isNotEmpty(vouchers))
		{
			model.addAttribute(APPLIED_VOUCHER_CODES, vouchers);
		}
	}

	protected Collection<CardTypeData> getSopCardTypes()
	{
		final Collection<CardTypeData> sopCardTypes = new ArrayList<CardTypeData>();

		final List<CardTypeData> supportedCardTypes = getCheckoutFacade().getSupportedCardTypes();
		for (final CardTypeData supportedCardType : supportedCardTypes)
		{
			// Add credit cards for all supported cards that have mappings for cybersource SOP
			if (CYBERSOURCE_SOP_CARD_TYPES.containsKey(supportedCardType.getCode()))
			{
				sopCardTypes.add(
						createCardTypeData(CYBERSOURCE_SOP_CARD_TYPES.get(supportedCardType.getCode()), supportedCardType.getName()));
			}
		}
		return sopCardTypes;
	}

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_METHOD);
	}

	static
	{
		// Map hybris card type to Cybersource SOP credit card
		CYBERSOURCE_SOP_CARD_TYPES.put("visa", "001");
		CYBERSOURCE_SOP_CARD_TYPES.put("master", "002");
		CYBERSOURCE_SOP_CARD_TYPES.put("amex", "003");
		CYBERSOURCE_SOP_CARD_TYPES.put("diners", "005");
		CYBERSOURCE_SOP_CARD_TYPES.put("maestro", "024");
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
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * @param travelCartFacade
	 *           the travelCartFacade to set
	 */
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * @return the reservationFacade
	 */
	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * @param reservationFacade
	 *           the reservationFacade to set
	 */
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	/**
	 * @return the sessionService
	 */
	@Override
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * @param bookingFacade
	 *           the bookingFacade to set
	 */
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}
}
