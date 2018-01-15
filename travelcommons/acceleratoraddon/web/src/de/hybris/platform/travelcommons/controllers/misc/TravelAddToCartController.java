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

package de.hybris.platform.travelcommons.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.forms.cms.AddDealToCartForm;
import de.hybris.platform.travelcommons.forms.validators.AddDealToCartFormValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.packages.DealCartFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Add to Cart functionalities for features belonging to travel site
 */
@Controller
public class TravelAddToCartController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(TravelAddToCartController.class);
	private static final String ADD_DEAL_TO_CART_ERROR = "add.deal.to.cart.error";
	private static final String REDIRECT_URL = "/deal-details";

	@Resource(name = "dealCartFacade")
	private DealCartFacade dealCartFacade;

	@Resource(name = "addDealToCartFormValidator")
	private AddDealToCartFormValidator addDealToCartFormValidator;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@RequestMapping(value = "/cart/addDeal", method = RequestMethod.POST)
	public String addToCart(final AddDealToCartForm addDealToCartForm, final BindingResult bindingResult,
			final HttpServletRequest request, final HttpServletResponse response, final Model model,
			final RedirectAttributes redirectModel)
	{
		addDealToCartFormValidator.validate(addDealToCartForm, bindingResult);
		if (bindingResult.hasErrors())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, ADD_DEAL_TO_CART_ERROR);
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.BACK_SLASH;
		}

		if (!fareSearchHashResolver
				.validItineraryIdentifier(addDealToCartForm.getItineraryPricingInfos(), addDealToCartForm.getBundleTemplateID()))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, ADD_DEAL_TO_CART_ERROR);
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.BACK_SLASH;
		}

		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_PACKAGE);

		final List<CartModificationData> cartModifications = dealCartFacade
				.addDealToCart(buildAddDealToCartData(addDealToCartForm));
		if (cartModifications.stream().filter(mod -> mod.getQuantityAdded() <= 0).findAny().isPresent())
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, ADD_DEAL_TO_CART_ERROR);
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.BACK_SLASH;
		}
		request.setAttribute(TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID, addDealToCartForm.getBundleTemplateID());
		request.setAttribute(TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE, addDealToCartForm.getStartingDate());
		return REDIRECT_PREFIX + REDIRECT_URL + TravelacceleratorstorefrontWebConstants.QUESTION_MARK
				+ TravelcommonsWebConstants.DEAL_BUNDLE_TEMPLATE_ID + TravelacceleratorstorefrontWebConstants.EQUALS
				+ addDealToCartForm.getBundleTemplateID()
				+ TravelacceleratorstorefrontWebConstants.AMPERSAND + TravelcommonsWebConstants.DEAL_SELECTED_DEPARTURE_DATE
				+ TravelacceleratorstorefrontWebConstants.EQUALS
				+ addDealToCartForm.getStartingDate();

	}

	protected AddDealToCartData buildAddDealToCartData(final AddDealToCartForm addDealToCartForm)
	{
		final AddDealToCartData addDealToCartData = new AddDealToCartData();
		addDealToCartData.setDealBundleId(addDealToCartForm.getBundleTemplateID());
		addDealToCartData.setItineraryPricingInfos(addDealToCartForm.getItineraryPricingInfos());
		addDealToCartData.setPassengerTypes(addDealToCartForm.getPassengerTypes());
		addDealToCartData.setStartingDate(
				TravelDateUtils.convertStringDateToDate(addDealToCartForm.getStartingDate(), TravelservicesConstants.DATE_PATTERN));
		return addDealToCartData;
	}
}
