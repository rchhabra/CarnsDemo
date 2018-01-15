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
 */

package de.hybris.platform.travelcommons.controllers.cms;

import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationFinderForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationFinderValidator;
import de.hybris.platform.commercefacades.accommodation.GlobalSuggestionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.SuggestionType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractFinderComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractFinderComponentModel;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.traveladdon.validators.FareFinderValidator;
import de.hybris.platform.travelcommons.constants.TravelcommonsConstants;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelcommons.forms.cms.TravelFinderForm;
import de.hybris.platform.travelfacades.accommodation.autosuggestion.wrapper.AccommodationAutoSuggestWrapper;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelLocationFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationSuggestionFacade;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Abstract Travel Finder Component Controller
 */
public abstract class AbstractTravelFinderComponentController extends AbstractFinderComponentController
{

	private static final Logger LOG = Logger.getLogger(AbstractTravelFinderComponentController.class);

	private static final String ECONOMY_CABIN_CLASS_CODE = "M";
	private static final String SHOW_ACC_DESTINATION = "showAccommodationDestination";
	private static final String SHOW_CHECKIN_CHECKOUT = "showCheckInCheckOut";
	private static final String DEFAULT_ACCOMMODATION_QUANTITY = "1";
	private static final String ROOM = "r";

	@Resource(name = "fareFinderValidator")
	protected FareFinderValidator fareFinderValidator;

	@Resource(name = "accommodationFinderValidator")
	protected AccommodationFinderValidator accommodationFinderValidator;

	@Resource(name = "transportFacilityFacade")
	private TransportFacilityFacade transportFacilityFacade;

	@Resource(name = "accommodationSuggestionFacade")
	private AccommodationSuggestionFacade accommodationSuggestionFacade;

	@Resource(name = "accommodationAutoSuggestWrapper")
	private AccommodationAutoSuggestWrapper accommodationAutoSuggestWrapper;

	@Resource(name = "travelLocationFacade")
	private TravelLocationFacade travelLocationFacade;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractFinderComponentModel component)
	{

		TravelFinderForm travelFinderForm = null;

		if (model.containsAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM))
		{
			travelFinderForm = (TravelFinderForm) model.asMap().get(TravelcommonsConstants.TRAVEL_FINDER_FORM);
		}

		if (travelFinderForm == null && (request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) != null
				&& request.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) != null))
		{
			travelFinderForm = new TravelFinderForm();
			travelFinderForm.setFareFinderForm((FareFinderForm) request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM));
			travelFinderForm.setAccommodationFinderForm(
					(AccommodationFinderForm) request.getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM));


			if (travelFinderForm.getAccommodationFinderForm().getPartHotelStay() != null
					&& travelFinderForm.getAccommodationFinderForm().getPartHotelStay())
			{
				model.addAttribute(SHOW_CHECKIN_CHECKOUT, Boolean.TRUE);
			}
			else
			{
				verifyAndUpdateDates(travelFinderForm);
			}
			request.setAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, travelFinderForm);
		}

		if (travelFinderForm == null)
		{
			model.addAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, initializeTravelFinderForm(model));
		}
		else
		{
			if (!model.containsAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM)
					&& request.getAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM) != null)
			{
				model.addAttribute(TravelcommonsWebConstants.TRAVEL_FINDER_FORM_BINDING_RESULT,
						request.getAttribute(TravelcommonsWebConstants.TRAVEL_FINDER_FORM_BINDING_RESULT));

				travelFinderForm = (TravelFinderForm) request.getAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM);

				model.addAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, travelFinderForm);
			}
		}

	}

	protected void verifyAndUpdateDates(final TravelFinderForm travelFinderForm)
	{
		final String travelDepartureDate = travelFinderForm.getFareFinderForm().getDepartingDateTime();
		final String travelReturnDate = travelFinderForm.getFareFinderForm().getReturnDateTime();

		final String accommodationCheckInDate = travelFinderForm.getAccommodationFinderForm().getCheckInDateTime();
		final String accommodationCheckOutDate = travelFinderForm.getAccommodationFinderForm().getCheckOutDateTime();

		boolean isSameDate = true;

		if (!StringUtils.equalsIgnoreCase(travelDepartureDate, accommodationCheckInDate))
		{
			travelFinderForm.getAccommodationFinderForm().setCheckInDateTime(travelDepartureDate);
			isSameDate = false;
		}

		if (!StringUtils.equalsIgnoreCase(travelReturnDate, accommodationCheckOutDate))
		{
			travelFinderForm.getAccommodationFinderForm().setCheckOutDateTime(travelReturnDate);
			isSameDate = false;
		}

		// if dates have been changed, we need to update accommodation finder and accommodation search url params that might be
		// currently in the session
		if (isSameDate)
		{
			return;
		}

		if (getSessionService().getAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM) != null)
		{
			getSessionService().setAttribute(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM,
					travelFinderForm.getAccommodationFinderForm());
		}

		if (getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING) != null)
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING,
					buildUrlParameters(travelFinderForm.getAccommodationFinderForm()));
		}

	}

	protected TravelFinderForm initializeTravelFinderForm(final Model model)
	{
		final TravelFinderForm travelFinderForm = new TravelFinderForm();
		travelFinderForm.setAccommodationFinderForm(initializeAccommodationFinderForm());
		travelFinderForm.setFareFinderForm(initializeFareFinderForm());
		model.addAttribute(SHOW_ACC_DESTINATION, Boolean.FALSE);
		model.addAttribute(SHOW_CHECKIN_CHECKOUT, Boolean.FALSE);
		return travelFinderForm;
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

	protected FareFinderForm initializeFareFinderForm()
	{
		final FareFinderForm fareFinderForm = new FareFinderForm();
		fareFinderForm.setTripType(TripType.RETURN.toString());
		fareFinderForm.setTravellingWithChildren(false);

		// set default Cabin Class
		fareFinderForm.setCabinClass(ECONOMY_CABIN_CLASS_CODE);
		return fareFinderForm;
	}

	@RequestMapping(value = "/validate-travel-finder-form", method = RequestMethod.POST)
	public String validateTravelFinderForm(@Valid final TravelFinderForm travelFinderForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel)
	{
		setJourneyTypeInSession(travelFinderForm);
		initializeForms(travelFinderForm);
		validateForms(travelFinderForm, bindingResult);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(AccommodationaddonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(AccommodationaddonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}
		redirectModel.addFlashAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, travelFinderForm);
		model.addAttribute(TravelcommonsConstants.TRAVEL_FINDER_FORM, travelFinderForm);
		return AccommodationaddonControllerConstants.Views.Pages.FormErrors.FormErrorsResponse;
	}

	abstract protected void setJourneyTypeInSession(final TravelFinderForm travelFinderForm);

	protected void initializeForms(final TravelFinderForm travelFinderForm)
	{
		final AccommodationFinderForm accommodationFinderForm = travelFinderForm.getAccommodationFinderForm();
		final FareFinderForm fareFinderForm = travelFinderForm.getFareFinderForm();

		if (StringUtils.isNotEmpty(fareFinderForm.getArrivalLocation()))
		{
			resolveDestinationLocation(fareFinderForm.getArrivalLocation(), fareFinderForm.getArrivalLocationSuggestionType(),
					accommodationFinderForm);
		}

		if (CollectionUtils.isEmpty(fareFinderForm.getPassengerTypeQuantityList()))
		{
			fareFinderForm.setPassengerTypeQuantityList(createPassengerTypeQuantityData(accommodationFinderForm));
		}
	}

	/**
	 * This method resolves the destination to be used to build the query string for accommodation offering search. It
	 * tries to resolve the location from the transport facility, if no result is found we fall back to Google search and
	 * use geographic coordinates
	 *
	 * @param arrivalLocation
	 * @param arrivalLocationSuggestionType
	 * @param accommodationFinderForm
	 */
	protected void resolveDestinationLocation(final String arrivalLocation, final String arrivalLocationSuggestionType,
			final AccommodationFinderForm accommodationFinderForm)
	{
		LocationData location = null;
		if (StringUtils.isNotBlank(arrivalLocationSuggestionType))
		{
			if (StringUtils.equalsIgnoreCase(SuggestionType.AIRPORTGROUP.toString(), arrivalLocationSuggestionType))
			{
				location = transportFacilityFacade.getLocation(arrivalLocation);
			}
			else if (StringUtils.equalsIgnoreCase(SuggestionType.CITY.toString(), arrivalLocationSuggestionType))
			{
				location = travelLocationFacade.getLocation(arrivalLocation);
			}
		}

		List<GlobalSuggestionData> suggestionResults = Objects.nonNull(location)
				? accommodationSuggestionFacade.getLocationSuggestions(location.getName()) : new ArrayList<>();
		GlobalSuggestionData firstValidResult;
		if (CollectionUtils.isNotEmpty(suggestionResults))
		{
			firstValidResult = suggestionResults.stream().findFirst().get();
			accommodationFinderForm.setSuggestionType(SuggestionType.LOCATION.toString());
		}
		else
		{
			suggestionResults = accommodationAutoSuggestWrapper.getAutoCompleteResults(arrivalLocation);
			firstValidResult = suggestionResults.stream().findFirst().get();
			accommodationFinderForm.setSuggestionType(StringUtils.EMPTY);
		}

		accommodationFinderForm.setDestinationLocation(firstValidResult.getCode());
		accommodationFinderForm.setDestinationLocationName(firstValidResult.getName());
		accommodationFinderForm.setLongitude(Objects.isNull(firstValidResult.getLongitude()) ? StringUtils.EMPTY
				: String.valueOf(firstValidResult.getLongitude()));
		accommodationFinderForm.setLatitude(
				Objects.isNull(firstValidResult.getLatitude()) ? StringUtils.EMPTY : String.valueOf(firstValidResult.getLatitude()));
		accommodationFinderForm.setRadius(
				Objects.isNull(firstValidResult.getRadius()) ? StringUtils.EMPTY : String.valueOf(firstValidResult.getRadius()));
	}

	protected void validateForms(final TravelFinderForm travelFinderForm, final BindingResult bindingResult)
	{
		validateFareFinderForm(fareFinderValidator, travelFinderForm.getFareFinderForm(), bindingResult,
				TraveladdonWebConstants.FARE_FINDER_FORM);

		if (!bindingResult.hasErrors())
		{
			validateAccommodationFinderForm(accommodationFinderValidator, travelFinderForm.getAccommodationFinderForm(),
					bindingResult, AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		}
	}

	protected void validateFareFinderForm(final AbstractTravelValidator fareFinderValidator, final FareFinderForm fareFinderForm,
			final BindingResult bindingResult, final String formName)
	{
		fareFinderValidator.setTargetForm(TraveladdonWebConstants.FARE_FINDER_FORM);
		fareFinderValidator.setAttributePrefix(TraveladdonWebConstants.FARE_FINDER_FORM);
		fareFinderValidator.validate(fareFinderForm, bindingResult);
	}

	protected void validateAccommodationFinderForm(final AbstractTravelValidator accommodationFinderValidator,
			final AccommodationFinderForm accommodationFinderForm, final BindingResult bindingResult, final String formName)
	{
		accommodationFinderValidator.setTargetForm(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		accommodationFinderValidator.setAttributePrefix(AccommodationaddonWebConstants.ACCOMMODATION_FINDER_FORM);
		accommodationFinderValidator.validate(accommodationFinderForm, bindingResult);
	}

	protected List<PassengerTypeQuantityData> createPassengerTypeQuantityData(
			final AccommodationFinderForm accommodationFinderForm)
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final int numberOfRooms = Integer.parseInt(accommodationFinderForm.getNumberOfRooms());
		if (accommodationFinderForm.getRoomStayCandidates().size() < numberOfRooms)
		{
			return passengerTypeQuantityList;
		}

		for (int i = 0; i < numberOfRooms; i++)
		{
			final List<PassengerTypeQuantityData> guestCounts = accommodationFinderForm.getRoomStayCandidates().get(i)
					.getPassengerTypeQuantityList();
			if (CollectionUtils.isEmpty(guestCounts))
			{
				continue;
			}

			for (int j = 0; j < guestCounts.size(); j++)
			{
				final PassengerTypeQuantityData gc = guestCounts.get(j);
				if (CollectionUtils.isEmpty(passengerTypeQuantityList))
				{
					final PassengerTypeQuantityData ptqd = clonePassengerTypeQuantityData(gc);

					passengerTypeQuantityList.add(ptqd);
				}
				else
				{
					final Optional<PassengerTypeQuantityData> passengerTypeQuantityData = passengerTypeQuantityList.stream()
							.filter(pt -> StringUtils.equals(pt.getPassengerType().getCode(), gc.getPassengerType().getCode()))
							.findFirst();
					if (passengerTypeQuantityData.isPresent())
					{
						passengerTypeQuantityData.get().setQuantity(passengerTypeQuantityData.get().getQuantity() + gc.getQuantity());
					}
					else
					{
						final PassengerTypeQuantityData ptqd = clonePassengerTypeQuantityData(gc);

						passengerTypeQuantityList.add(ptqd);
					}
				}
			}
		}
		return passengerTypeQuantityList;
	}

	protected PassengerTypeQuantityData clonePassengerTypeQuantityData(final PassengerTypeQuantityData gc)
	{
		final PassengerTypeQuantityData ptqd = new PassengerTypeQuantityData();
		ptqd.setAge(gc.getAge());
		ptqd.setQuantity(gc.getQuantity());

		final PassengerTypeData ptd = new PassengerTypeData();
		ptd.setCode(gc.getPassengerType().getCode());
		ptd.setIdentifier(gc.getPassengerType().getIdentifier());
		ptd.setName(gc.getPassengerType().getName());
		ptd.setPassengerType(gc.getPassengerType().getCode());
		ptd.setMinAge(gc.getPassengerType().getMinAge());
		ptd.setMaxAge(gc.getPassengerType().getMaxAge());

		ptqd.setPassengerType(ptd);
		return ptqd;
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

		if (accommodationFinderForm.getPartHotelStay() != null)
		{
			urlParameters.append(TravelacceleratorstorefrontValidationConstants.PART_HOTEL_STAY);
			urlParameters.append(TravelacceleratorstorefrontWebConstants.EQUALS);
			urlParameters.append(String.valueOf(accommodationFinderForm.getPartHotelStay()));
			urlParameters.append(TravelacceleratorstorefrontWebConstants.AMPERSAND);
		}

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
