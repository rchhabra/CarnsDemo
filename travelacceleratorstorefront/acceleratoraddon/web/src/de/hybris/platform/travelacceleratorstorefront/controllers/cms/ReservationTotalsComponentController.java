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

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.model.components.ReservationTotalsComponentModel;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravelImageFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Controller for Itinerary Totals Component
 */
@Controller("ReservationTotalsComponentController")
@RequestMapping(value = TravelacceleratorstorefrontControllerConstants.Actions.Cms.ReservationTotalsComponent)
public class ReservationTotalsComponentController extends SubstitutingCMSAddOnComponentController<ReservationTotalsComponentModel>
{
	private static final Logger LOG = Logger.getLogger(ReservationTotalsComponentController.class);
	private static final String DESTINATION_LOCATION = "destinationLocation";
	private static final String RADIUS = "radius";
	private static final String ARRIVAL_LOCATION_SUGGESTION_TYPE = "arrivalLocationSuggestionType";
	private static final String ARRIVAL_LOCATION = "arrivalLocation";

	@Resource(name = "travelImageFacade")
	private TravelImageFacade travelImageFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ReservationTotalsComponentModel component)
	{
		ImageData image = getImage(request);

		if (image != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.DESTINATION_LOCATION_IMAGE, image);
		}

		final String originalOrderCode = travelCartFacade.getOriginalOrderCode();

		model.addAttribute(TravelacceleratorstorefrontWebConstants.RESERVATION_TOTAL,
				travelCartFacade.getBookingTotal(originalOrderCode));

		model.addAttribute(TravelacceleratorstorefrontWebConstants.RESERVATION_CODE,
				travelCartFacade.isAmendmentCart() ? travelCartFacade.getOriginalOrderCode() : travelCartFacade.getCurrentCartCode());
	}

	/**
	 * This method is responsible for refreshing itinerary component after ancillary is added or removed to/from cart
	 *
	 * @param componentUid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	protected String getComponent(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(COMPONENT_UID, componentUid);
		try
		{
			return handleGet(request, response, model);
		}
		catch (final Exception e)
		{
			LOG.error("Exception loading the component", e);
		}
		return StringUtils.EMPTY;
	}

	/**
	 * This method is responsible for fetching the image which will be displayed in itinerary totals component in following order:
	 * 1) If there is ACCOMMODATION_OFFERING_CODE in the session - get image for a location of that accommodation offering
	 * 2) Else if there is a destinationLocation parameter in request - get image for the city location part of that parameter
	 * 3) Else if there is an arrivalLocation parameter in request - get image for the location of transport facility with that
	 * code
	 * 4) Else if there is a radius parameter in request - it means that this is Google based search and therefore we don't
	 * provide image for this search -> return null
	 * 5) If the image is still null, try to get it from the session
	 *
	 * @param request
	 * @return image
	 */
	protected ImageData getImage(final HttpServletRequest request)
	{
		ImageData image = null;

		final Map<String, String[]> parameterMap = request.getParameterMap();
		if (sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE) != null)
		{
			image = travelImageFacade.getImageForAccommodationOfferingLocation(
					sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE));

			sessionService.removeAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE);
		}
		else if (MapUtils.isNotEmpty(parameterMap))
		{
			if (parameterMap.keySet().contains(ARRIVAL_LOCATION) && parameterMap.keySet().contains(ARRIVAL_LOCATION_SUGGESTION_TYPE))
			{
				if (StringUtils
						.equalsIgnoreCase(SuggestionType.CITY.toString(), request.getParameter(ARRIVAL_LOCATION_SUGGESTION_TYPE)))
				{
					image = travelImageFacade.getImageForDestinationLocation(request.getParameter(ARRIVAL_LOCATION));
				}
				else if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(),
						request.getParameter(ARRIVAL_LOCATION_SUGGESTION_TYPE)))
				{
					image = travelImageFacade.getImageForArrivalTransportFacility(request.getParameter(ARRIVAL_LOCATION));
				}
			}
			else if (parameterMap.keySet().contains(DESTINATION_LOCATION))
			{
				image = travelImageFacade.getImageForDestinationLocation(request.getParameter(DESTINATION_LOCATION));
			}
			else if (parameterMap.keySet().contains(RADIUS))
			{
				return null;
			}
		}

		if (image == null)
		{
			image = travelImageFacade.getImageFromCart();
		}

		return image;
	}
}
