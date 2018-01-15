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

package de.hybris.platform.accommodationaddon.controllers.cms;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractFinderComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractFinderComponentModel;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Accommodation Finder Component
 */
@Controller("AccommodationFinderComponentController")
@RequestMapping(value = AccommodationaddonControllerConstants.Actions.Cms.AccommodationFinderComponent)
public class AccommodationFinderComponentController extends AbstractFinderComponentController
{
	@Resource(name = "accommodationFinderValidator")
	private AbstractTravelValidator accommodationFinderValidator;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	protected static final String SEARCH = "/search";

	private static final String ROOM = "r";
	private static final String DEFAULT_ACCOMMODATION_QUANTITY = "1";

	private static final String SHOW_ACC_DESTINATION = "showAccommodationDestination";
	private static final String SHOW_CHECKIN_CHECKOUT = "showCheckInCheckOut";

	private static final Logger LOG = Logger.getLogger(AccommodationFinderComponentController.class);

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractFinderComponentModel component)
	{
		AccommodationFinderForm accommodationFinderForm = null;

		if (model.containsAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM))
		{
			accommodationFinderForm = (AccommodationFinderForm) model.asMap()
					.get(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		}

		if (accommodationFinderForm == null
				&& request.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) != null)
		{
			accommodationFinderForm = (AccommodationFinderForm) request
					.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		}

		if (accommodationFinderForm == null)
		{
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, initializeAccommodationFinderForm());
		}
		else
		{
			if (!model.containsAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM)
					&& request.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) != null)
			{
				model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM_BINDING_RESULT,
						request.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM_BINDING_RESULT));

				accommodationFinderForm = (AccommodationFinderForm) request
						.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);

				model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, accommodationFinderForm);
			}
		}

		model.addAttribute(SHOW_ACC_DESTINATION, Boolean.TRUE);
		model.addAttribute(SHOW_CHECKIN_CHECKOUT, Boolean.TRUE);
	}

	@Override
	protected int getMaxGuestQuantity()
	{
		return getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MAX_ACCOMMODATION_GUEST_QUANTITY);
	}

	protected AccommodationFinderForm initializeAccommodationFinderForm()
	{
		final AccommodationFinderForm accommodationFinderForm = new AccommodationFinderForm();
		final List<RoomStayCandidateData> roomStayCandidates = new ArrayList<>();
		final int maxAccommodationsQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		for (int i = 0; i < maxAccommodationsQuantity; i++)
		{
			final RoomStayCandidateData roomStayCandidateData = createRoomStayCandidatesData();
			roomStayCandidateData.setRoomStayCandidateRefNumber(i);
			roomStayCandidates.add(roomStayCandidateData);
		}
		accommodationFinderForm.setRoomStayCandidates(roomStayCandidates);
		accommodationFinderForm.setNumberOfRooms(DEFAULT_ACCOMMODATION_QUANTITY);
		return accommodationFinderForm;
	}

	@InitBinder("accommodationFinderForm")
	private void initBinder(final WebDataBinder binder)
	{
		binder.setValidator(accommodationFinderValidator);
	}

	@RequestMapping(value = SEARCH, method = RequestMethod.POST)
	public String performSearch(final AccommodationFinderForm accommodationFinderForm, final RedirectAttributes redirectModel,
			final BindingResult bindingResult)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		validateForm(accommodationFinderValidator, accommodationFinderForm, bindingResult,
				AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		if (bindingResult.hasErrors())
		{
			redirectModel.addFlashAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM_BINDING_RESULT, bindingResult);
			return REDIRECT_PREFIX + "/";
		}

		getSessionService().setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, accommodationFinderForm);
		redirectModel.addFlashAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, accommodationFinderForm);

		travelCartFacade.deleteCurrentCart();

		final String urlParameters = buildUrlParameters(accommodationFinderForm);
		final String redirectUrl = createRedirectUrl(accommodationFinderForm);

		return urlParameters.isEmpty() ? redirectUrl : redirectUrl + urlParameters;
	}

	private String createRedirectUrl(final AccommodationFinderForm accommodationFinderForm)
	{
		String redirectUrl;
		if (StringUtils.equalsIgnoreCase(accommodationFinderForm.getSuggestionType(), SuggestionType.PROPERTY.toString()))
		{
			redirectUrl = REDIRECT_PREFIX + AccommodationaddonWebConstants.ACCOMMODATION_DETAILS_ROOT_URL + "/"
					+ accommodationFinderForm.getDestinationLocation();
		}
		else
		{
			redirectUrl = REDIRECT_PREFIX + AccommodationaddonWebConstants.ACCOMMODATION_SELECTION_ROOT_URL;
		}
		return redirectUrl;
	}


	@RequestMapping(value = "/validate-accommodation-finder-form", method = RequestMethod.POST)
	public String validateAccommodationFinderForm(final AccommodationFinderForm accommodationFinderForm,
			final BindingResult bindingResult, final Model model)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		validateForm(accommodationFinderValidator, accommodationFinderForm, bindingResult,
				AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);

		model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM, accommodationFinderForm);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(AccommodationaddonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(AccommodationaddonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}

		return AccommodationaddonControllerConstants.Views.Pages.FormErrors.FormErrorsResponse;
	}

	private void validateForm(final AbstractTravelValidator accommodationFinderValidator,
			final AccommodationFinderForm accommodationFinderForm, final BindingResult bindingResult, final String formName)
	{
		accommodationFinderValidator.setTargetForm(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		accommodationFinderValidator.setAttributePrefix("");
		accommodationFinderValidator.validate(accommodationFinderForm, bindingResult);
	}

	protected String buildUrlParameters(final AccommodationFinderForm accommodationFinderForm)
	{
		final StringBuilder urlParameters = new StringBuilder();

		if (Arrays.asList(SuggestionType.LOCATION.toString(), StringUtils.EMPTY)
				.contains(accommodationFinderForm.getSuggestionType()))
		{
			if (StringUtils.isNotEmpty(accommodationFinderForm.getLongitude())
					&& StringUtils.isNotEmpty(accommodationFinderForm.getLatitude())
					&& StringUtils.isNotEmpty(accommodationFinderForm.getRadius()))
			{
				urlParameters.append(AccommodationaddonWebConstants.LONGITUDE);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(accommodationFinderForm.getLongitude());
				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

				urlParameters.append(AccommodationaddonWebConstants.LATITUDE);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(accommodationFinderForm.getLatitude());
				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

				urlParameters.append(AccommodationaddonWebConstants.RADIUS);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(accommodationFinderForm.getRadius());
				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
			}
			else
			{
				urlParameters.append(AccommodationaddonWebConstants.DESTINATION_LOCATION);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				// We need to encode '|' character because it is not allowed in Spring Security 4
				String encodedDestinationLocation = accommodationFinderForm.getDestinationLocation();
				encodedDestinationLocation = encodedDestinationLocation.replaceAll("\\|", "%7C");
				urlParameters.append(encodedDestinationLocation);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
			}

			urlParameters.append(AccommodationaddonWebConstants.DESTINATION_LOCATION_NAME);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			urlParameters.append(accommodationFinderForm.getDestinationLocationName());
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

			urlParameters.append(AccommodationaddonWebConstants.SUGGESTION_TYPE);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			urlParameters.append(accommodationFinderForm.getSuggestionType());
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
		}

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.CHECKIN_DATE);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(accommodationFinderForm.getCheckInDateTime());
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.CHECKOUT_DATE);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(accommodationFinderForm.getCheckOutDateTime());
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		urlParameters.append(TravelacceleratorstorefrontValidationConstants.NUMBER_OF_ROOMS);
		urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
		urlParameters.append(String.valueOf(accommodationFinderForm.getNumberOfRooms()));
		urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

		try
		{
			final int numberOfRooms = Integer.parseInt(accommodationFinderForm.getNumberOfRooms());
			for (int i = 0; i < numberOfRooms; i++)
			{
				final StringBuilder guestsStringPerRoom = new StringBuilder();
				final List<PassengerTypeQuantityData> guestCounts = accommodationFinderForm.getRoomStayCandidates().get(i)
						.getPassengerTypeQuantityList();
				for (int j = 0; j < guestCounts.size(); j++)
				{
					final String passengerType = guestCounts.get(j).getPassengerType().getCode();
					final int passengerQuantity = guestCounts.get(j).getQuantity();
					final String guestParam = String.valueOf(passengerQuantity) + "-" + passengerType;
					guestsStringPerRoom.append(guestParam);
					guestsStringPerRoom.append(",");
				}
				String result = guestsStringPerRoom.toString();
				result = result.substring(0, result.length() - 1);

				urlParameters.append(ROOM + i);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
				urlParameters.append(result);
				urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);

			}
		}
		catch (final NumberFormatException e)
		{
			LOG.error("Cannot parse number of rooms string to integer");
			LOG.error(e.getClass().getName() + " : " + e.getMessage());
			LOG.debug(e);
			return REDIRECT_PREFIX + "/";
		}
		String urlParametersString = urlParameters.toString();
		urlParametersString = urlParametersString.substring(0, urlParametersString.length() - 1);
		return "?" + urlParametersString;
	}

}
