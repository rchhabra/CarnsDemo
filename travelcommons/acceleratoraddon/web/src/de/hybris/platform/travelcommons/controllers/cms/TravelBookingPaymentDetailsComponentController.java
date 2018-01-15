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

package de.hybris.platform.travelcommons.controllers.cms;

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractBookingDetailsComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractBookingDetailsComponentModel;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.OrderEntryType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("TravelBookingPaymentDetailsComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.TravelBookingPaymentDetailsComponent)
public class TravelBookingPaymentDetailsComponentController extends AbstractBookingDetailsComponentController
{
	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model,
			final AbstractBookingDetailsComponentModel component)
	{
		final String bookingReference = getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_REFERENCE);
		final boolean isPackageInOrder = getPackageFacade().isPackageInOrder(bookingReference);
		model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_IN_ORDER, isPackageInOrder);
		if (isPackageInOrder)
		{
			final PriceData totalPrice = getBookingFacade().getBookingTotal(bookingReference);
			final PriceData amountPaid = getBookingFacade().getOrderTotalPaid(bookingReference);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.TOTAL, totalPrice);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_PAID, amountPaid);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.PAYMENT_DUE,
					travelCartFacade.getBookingDueAmount(totalPrice, amountPaid));
			final PriceData notRefundableAmount = getBookingFacade().getNotRefundableAmount(bookingReference);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.NOT_REFUNDABLE, notRefundableAmount);
			model.addAttribute(TravelcommonsWebConstants.TRANSPORT_BOOKING_TOTAL_AMOUNT,
					getBookingFacade().getBookingTotalByOrderEntryType(bookingReference, OrderEntryType.TRANSPORT));
			model.addAttribute(TravelcommonsWebConstants.ACCOMMODATION_BOOKING_TOTAL_AMOUNT,
					getBookingFacade().getBookingTotalByOrderEntryType(bookingReference, OrderEntryType.ACCOMMODATION));
		}
	}
}
