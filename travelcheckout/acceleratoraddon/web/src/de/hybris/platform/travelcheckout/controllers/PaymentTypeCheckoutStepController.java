/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.travelcheckout.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorservices.enums.CheckoutPaymentType;
import de.hybris.platform.b2bcommercefacades.company.B2BCostCenterFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelcheckout.forms.PaymentTypeForm;
import de.hybris.platform.travelcheckout.forms.validation.PaymentTypeFormValidator;
import de.hybris.platform.travelfacades.order.TravelB2BCheckoutFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = "/checkout/multi/payment-type")
public class PaymentTypeCheckoutStepController extends AbstractCheckoutStepController
{
	private static final String PAYMENT_METHOD_CMS_PAGE_LABEL = "paymentMethodPage";

	@Resource(name = "b2bCheckoutFacade")
	private TravelB2BCheckoutFacade b2bCheckoutFacade;

	@Resource(name = "costCenterFacade")
	private B2BCostCenterFacade costCenterFacade;

	@Resource(name = "paymentTypeFormValidator")
	private PaymentTypeFormValidator paymentTypeFormValidator;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@ModelAttribute("paymentTypes")
	public Collection<B2BPaymentTypeData> getAllB2BPaymentTypes()
	{
		final List<B2BPaymentTypeData> paymentTypes = b2bCheckoutFacade.getPaymentTypes();
		if (travelCartFacade.isAmendmentCart())
		{
			return paymentTypes.stream()
					.filter(paymentType -> !StringUtils.equalsIgnoreCase(paymentType.getCode(), CheckoutPaymentType.ACCOUNT.getCode
							()))
					.collect(Collectors.toList());
		}
		return paymentTypes;
	}

	@ModelAttribute("costCenters")
	public List<? extends B2BCostCenterData> getVisibleActiveCostCenters()
	{
		if (travelCartFacade.isAmendmentCart())
		{
			return Collections.emptyList();
		}
		final List<? extends B2BCostCenterData> costCenterData = costCenterFacade.getActiveCostCenters();
		return costCenterData == null ? Collections.<B2BCostCenterData>emptyList() : costCenterData;
	}

	@Override
	@RequestMapping(value = "/choose", method = RequestMethod.GET)
	@RequireHardLogIn
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException, CommerceCartModificationException
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute("paymentTypeForm", preparePaymentTypeForm(cartData));
		prepareDataForPage(model);
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_METHOD_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAYMENT_METHOD_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentType.breadcrumb"));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.ChoosePaymentTypePage;
	}

	@RequestMapping(value = "/choose", method = RequestMethod.POST)
	@RequireHardLogIn
	public String choose(@ModelAttribute final PaymentTypeForm paymentTypeForm, final BindingResult bindingResult,
			final Model model) throws CMSItemNotFoundException, CommerceCartModificationException
	{
		paymentTypeFormValidator.validate(paymentTypeForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			GlobalMessages.addErrorMessage(model, "checkout.error.paymenttype.formentry.invalid");
			model.addAttribute("paymentTypeForm", paymentTypeForm);
			prepareDataForPage(model);
			storeCmsPageInModel(model, getContentPageForLabelOrId(PAYMENT_METHOD_CMS_PAGE_LABEL));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAYMENT_METHOD_CMS_PAGE_LABEL));
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentType.breadcrumb"));
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
			return TravelcheckoutControllerConstants.Views.Pages.MultiStepCheckout.ChoosePaymentTypePage;
		}

		updateCheckoutCart(paymentTypeForm);

		checkAndSelectDeliveryAddress(paymentTypeForm);

		return nextPage(paymentTypeForm.getPaymentType());
	}

	/**
	 * Redirects user to the next checkout page which is payment details
	 *
	 * @return payment details page or payment type page
	 */
	protected String nextPage(final String paymentType)
	{
		if (CheckoutPaymentType.ACCOUNT.getCode().equals(paymentType))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.SUMMARY_DETAILS_PATH;
		}
		final String paymentFlowProperty = configurationService.getConfiguration().getString("payment.flow");
		if (StringUtils.isNotBlank(paymentFlowProperty))
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}
		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH
				+ TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
	}

	protected void updateCheckoutCart(final PaymentTypeForm paymentTypeForm)
	{
		final CartData cartData = new CartData();

		// set payment type
		final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
		paymentTypeData.setCode(paymentTypeForm.getPaymentType());

		cartData.setPaymentType(paymentTypeData);

		// set cost center
		if (CheckoutPaymentType.ACCOUNT.getCode().equals(cartData.getPaymentType().getCode()))
		{
			final B2BCostCenterData costCenter = new B2BCostCenterData();
			costCenter.setCode(paymentTypeForm.getCostCenterId());

			cartData.setCostCenter(costCenter);
		}

		b2bCheckoutFacade.updateCheckoutCart(cartData);
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return ROOT;
	}

	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return ROOT;
	}

	protected PaymentTypeForm preparePaymentTypeForm(final CartData cartData)
	{
		final PaymentTypeForm paymentTypeForm = new PaymentTypeForm();

		// set payment type
		if (cartData.getPaymentType() != null && StringUtils.isNotBlank(cartData.getPaymentType().getCode()))
		{
			paymentTypeForm.setPaymentType(cartData.getPaymentType().getCode());
		}
		else
		{
			if (travelCartFacade.isAmendmentCart())
			{
				paymentTypeForm.setPaymentType(CheckoutPaymentType.CARD.getCode());
			}
			else
			{
				paymentTypeForm.setPaymentType(CheckoutPaymentType.ACCOUNT.getCode());
			}

		}

		// set cost center
		if (cartData.getCostCenter() != null && StringUtils.isNotBlank(cartData.getCostCenter().getCode()))
		{
			paymentTypeForm.setCostCenterId(cartData.getCostCenter().getCode());
		}
		else if (!CollectionUtils.isEmpty(getVisibleActiveCostCenters()) && getVisibleActiveCostCenters().size() == 1)
		{
			paymentTypeForm.setCostCenterId(getVisibleActiveCostCenters().get(0).getCode());
		}
		return paymentTypeForm;
	}

	protected void checkAndSelectDeliveryAddress(final PaymentTypeForm paymentTypeForm)
	{
		if (CheckoutPaymentType.ACCOUNT.getCode().equals(paymentTypeForm.getPaymentType()))
		{
			final List<? extends AddressData> deliveryAddresses = b2bCheckoutFacade.getSupportedDeliveryAddresses(true);
			if (deliveryAddresses.size() == 1)
			{
				b2bCheckoutFacade.setDeliveryAddress(deliveryAddresses.get(0));
			}
		}
	}

}
