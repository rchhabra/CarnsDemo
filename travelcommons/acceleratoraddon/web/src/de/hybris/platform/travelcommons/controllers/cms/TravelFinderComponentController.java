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

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.traveladdon.validators.TravelFareFinderValidator;
import de.hybris.platform.travelcommons.constants.TravelcommonsConstants;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.forms.cms.TravelFinderForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.order.TravelCartFacade;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller("TravelFinderComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.TravelFinderComponent)
public class TravelFinderComponentController extends AbstractTravelFinderComponentController
{
	private static final String ARRIVAL_LOCATION = "arrivalLocation";
	private static final String DEPARTURE_LOCATION = "departureLocation";
	private static final String DEPARTING_DATE_TIME = "departingDateTime";
	private static final String RETURN_DATE_TIME = "returnDateTime";
	private static final String TRIP_TYPE = "tripType";
	private static final String CABIN_CLASS = "cabinClass";
	private static final String DEPARTURE_LOCATION_NAME = "departureLocationName";
	private static final String ARRIVAL_LOCATION_NAME = "arrivalLocationName";
	private static final String DEPARTURE_LOCATION_TYPE = "departureLocationSuggestionType";
	private static final String ARRIVAL_LOCATION_TYPE = "arrivalLocationSuggestionType";
	private static final String DEFAULT_LOCATION_TYPE = "fare.finder.default.location.type";
	protected static final String SEARCH = "/search";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "travelFareFinderValidator")
	protected TravelFareFinderValidator travelFareFinderValidator;

	@RequestMapping(value = SEARCH, method = RequestMethod.POST)
	public String performSearch(@Valid @ModelAttribute("travelFinderForm") final TravelFinderForm travelFinderForm,
			final BindingResult bindingResult, final RedirectAttributes redirectModel)
	{
		setJourneyTypeInSession(travelFinderForm);
		initializeForms(travelFinderForm);
		validateForms(travelFinderForm, bindingResult);
		final boolean hasErrorFlag = bindingResult.hasErrors();

		if (hasErrorFlag)
		{
			redirectModel.addFlashAttribute(TravelcommonsWebConstants.TRAVEL_FINDER_FORM_BINDING_RESULT, bindingResult);
			return REDIRECT_PREFIX + "/";
		}

		redirectModel.addFlashAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, travelFinderForm);
		travelCartFacade.deleteCurrentCart();

		final String urlParameters = buildUrlParameters(travelFinderForm.getFareFinderForm());
		sessionService.setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM,
				travelFinderForm.getAccommodationFinderForm());
		sessionService.setAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, travelFinderForm.getFareFinderForm());
		sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING,
				buildUrlParameters(travelFinderForm.getAccommodationFinderForm()));
		final String redirectUrl = REDIRECT_PREFIX + TraveladdonWebConstants.FARE_SELECTION_ROOT_URL;

		return urlParameters.isEmpty() ? redirectUrl : redirectUrl + urlParameters;
	}

	@Override
	protected void setJourneyTypeInSession(final TravelFinderForm travelFinderForm)
	{
		if (travelFinderForm.getAccommodationFinderForm().getCheckInDateTime()
				.equals(travelFinderForm.getAccommodationFinderForm().getCheckOutDateTime()))
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ACCOMMODATION);
		}
	}

	@Override
	protected int getMaxGuestQuantity()
	{
		return getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MAX_TRANSPORT_ACCOMMODATION_QUANTITY);
	}

	protected String buildUrlParameters(final FareFinderForm fareFinderForm)
	{
		final Map<String, String> urlParameters = new HashMap<>();

		urlParameters.put(ARRIVAL_LOCATION, fareFinderForm.getArrivalLocation());
		urlParameters.put(DEPARTURE_LOCATION, fareFinderForm.getDepartureLocation());
		urlParameters.put(DEPARTING_DATE_TIME, fareFinderForm.getDepartingDateTime());
		urlParameters.put(TRIP_TYPE, fareFinderForm.getTripType());
		urlParameters.put(CABIN_CLASS, fareFinderForm.getCabinClass());
		urlParameters.put(DEPARTURE_LOCATION_NAME, fareFinderForm.getDepartureLocationName());
		urlParameters.put(DEPARTURE_LOCATION_TYPE,
				StringUtils.isNotBlank(fareFinderForm.getDepartureLocationSuggestionType())
						? fareFinderForm.getDepartureLocationSuggestionType()
						: configurationService.getConfiguration().getString(DEFAULT_LOCATION_TYPE));
		urlParameters.put(ARRIVAL_LOCATION_NAME, fareFinderForm.getArrivalLocationName());
		urlParameters.put(ARRIVAL_LOCATION_TYPE,
				StringUtils.isNotBlank(fareFinderForm.getArrivalLocationSuggestionType())
						? fareFinderForm.getArrivalLocationSuggestionType()
						: configurationService.getConfiguration().getString(DEFAULT_LOCATION_TYPE));

		if (fareFinderForm.getTripType().equalsIgnoreCase(TripType.RETURN.toString()))
		{
			urlParameters.put(RETURN_DATE_TIME, fareFinderForm.getReturnDateTime());
		}

		for (final PassengerTypeQuantityData ptcData : fareFinderForm.getPassengerTypeQuantityList())
		{
			if (ptcData.getPassengerType() == null)
			{
				continue;
			}
			urlParameters.put(ptcData.getPassengerType().getCode(), String.valueOf(ptcData.getQuantity()));
		}

		return "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");
	}

	@Override
	protected void validateForms(final TravelFinderForm travelFinderForm, final BindingResult bindingResult)
	{
		validateFareFinderForm(travelFareFinderValidator, travelFinderForm.getFareFinderForm(), bindingResult,
				TraveladdonWebConstants.FARE_FINDER_FORM);

		if (!bindingResult.hasErrors())
		{
			validateAccommodationFinderForm(accommodationFinderValidator, travelFinderForm.getAccommodationFinderForm(),
					bindingResult, AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		}
	}
}
