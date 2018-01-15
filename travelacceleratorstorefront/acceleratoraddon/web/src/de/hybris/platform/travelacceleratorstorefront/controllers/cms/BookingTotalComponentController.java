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
 */

package de.hybris.platform.travelacceleratorstorefront.controllers.cms;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.model.components.BookingTotalComponentModel;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("BookingTotalComponentController")
@RequestMapping(value = TravelacceleratorstorefrontControllerConstants.Actions.Cms.BookingTotalComponent)
public class BookingTotalComponentController extends SubstitutingCMSAddOnComponentController<BookingTotalComponentModel>
{
	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final BookingTotalComponentModel component)
	{
		final String bookingReference = sessionService.getAttribute("bookingConfirmationReference");
		final PriceData totalPrice = bookingFacade.getBookingTotal(bookingReference);
		final PriceData amountPaid = bookingFacade.getOrderTotalPaid(bookingReference);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_PAID, amountPaid);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_DUE,
				travelCartFacade.getBookingDueAmount(totalPrice, amountPaid));
		model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL, totalPrice);
	}
}
