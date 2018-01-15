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

package de.hybris.platform.traveladdon.controllers.cms;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.AbstractFinderComponentController;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractFinderComponentModel;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Fare Finder Controller for handling requests for the Fare Finder Component.
 */
@Controller("FareFinderComponentController")
@RequestMapping(value = TraveladdonControllerConstants.Actions.Cms.FareFinderComponent)
public class FareFinderComponentController extends AbstractFinderComponentController
{
	private static final String ECONOMY_CABIN_CLASS_CODE = "M";
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
	private static final String DEFAULT_LOCATION_TYPE = "search.default.location.type";
	protected static final String SEARCH = "/search";


	@Resource(name = "fareFinderValidator")
	protected AbstractTravelValidator fareFinderValidator;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	/**
	 * Method responsible for populating the data model with initial data for the component
	 */
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final AbstractFinderComponentModel component)
	{
		FareFinderForm fareFinderForm = null;

		if (model.containsAttribute(TraveladdonWebConstants.FARE_FINDER_FORM))
		{
			fareFinderForm = (FareFinderForm) model.asMap().get(TraveladdonWebConstants.FARE_FINDER_FORM);
		}

		if (fareFinderForm == null && request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) != null)
		{
			fareFinderForm = (FareFinderForm) request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM);
		}

		fareFinderForm = validateRequiredAttributesArePresent(fareFinderForm);

		if (fareFinderForm == null)
		{
			populateInitialData(model);
		}
		else
		{
			if (!model.containsAttribute(TraveladdonWebConstants.FARE_FINDER_FORM)
					&& request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) != null)
			{
				model.addAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT,
						request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT));

				fareFinderForm = (FareFinderForm) request.getAttribute(TraveladdonWebConstants.FARE_FINDER_FORM);

				fareFinderForm.setTravellingWithChildren(checkIfAdditionalTravellersArePresent(fareFinderForm));

				model.addAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);
			}
		}
	}

	/**
	 * Method checks to see if the required attributes on the FareFinderForm are populated. The Method will return the
	 * FareFinderForm object if required fields are populated otherwise it will return null.
	 *
	 * @param fareFinderForm
	 * @return FareFinderForm
	 */
	private FareFinderForm validateRequiredAttributesArePresent(FareFinderForm fareFinderForm)
	{
		if (fareFinderForm != null && fareFinderForm.getDepartureLocation() == null && fareFinderForm.getArrivalLocation() == null
				&& fareFinderForm.getCabinClass() == null && fareFinderForm.getDepartingDateTime() == null
				&& fareFinderForm.getPassengerTypeQuantityList().isEmpty())
		{
			fareFinderForm = null;
		}
		return fareFinderForm;
	}

	/**
	 * Method check to see if there are any additional travelers other than Adults traveling on this trip.
	 *
	 * @param fareFinderForm
	 */
	protected boolean checkIfAdditionalTravellersArePresent(final FareFinderForm fareFinderForm)
	{
		return fareFinderForm.getPassengerTypeQuantityList().stream()
				.anyMatch(ptq -> !ptq.getPassengerType().getCode().equals(TraveladdonWebConstants.PASSENGER_TYPE_ADULT)
						&& ptq.getQuantity() > 0);
	}

	/**
	 * Request handler for any search requests from the Fare Finder Component. This method will first perform JSR303
	 * validation on the fareFinderForm bean before performing any custom validation.
	 *
	 * @param fareFinderForm
	 * @param bindingResult
	 * @param model
	 * @return the location of the JSON tag used to render the errors in the front-end
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/validate-fare-finder-form", method = RequestMethod.POST)
	public String validateFareFinderForm(@Valid final FareFinderForm fareFinderForm, final BindingResult bindingResult,
			final Model model)
	{

		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);

		if (TripType.SINGLE.name().equals(fareFinderForm.getTripType()))
		{
			fareFinderForm.setReturnDateTime("");
		}

		validateForm(fareFinderValidator, fareFinderForm, bindingResult, TraveladdonWebConstants.FARE_FINDER_FORM);

		model.addAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);
		final boolean hasErrorFlag = bindingResult.hasErrors();
		model.addAttribute(TraveladdonWebConstants.HAS_ERROR_FLAG, hasErrorFlag);

		if (hasErrorFlag)
		{
			model.addAttribute(TraveladdonWebConstants.FIELD_ERRORS, bindingResult.getFieldErrors());
		}

		return TraveladdonControllerConstants.Views.Pages.FormErrors.formErrorsResponse;
	}

	private void validateForm(final AbstractTravelValidator fareFinderValidator, final FareFinderForm fareFinderForm,
			final BindingResult bindingResult, final String formName)
	{
		fareFinderValidator.setTargetForm(TraveladdonWebConstants.FARE_FINDER_FORM);
		fareFinderValidator.setAttributePrefix("");
		fareFinderValidator.validate(fareFinderForm, bindingResult);
	}

	/**
	 * The method is called when the form doesn't have any binding errors. It does a redirect to the FareSelection page
	 *
	 * @param fareFinderForm
	 *           as the input FareFinderForm
	 * @param redirectModel
	 * @param bindingResult
	 * @return a string representing the redirect to the FareSelection page
	 */
	@RequestMapping(value = SEARCH, method = RequestMethod.POST)
	public String performSearch(@Valid final FareFinderForm fareFinderForm, final RedirectAttributes redirectModel,
			final BindingResult bindingResult)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);

		validateForm(fareFinderValidator, fareFinderForm, bindingResult, TraveladdonWebConstants.FARE_FINDER_FORM);

		if (bindingResult.hasErrors())
		{
			redirectModel.addFlashAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT, bindingResult);
			return REDIRECT_PREFIX + "/";
		}

		getSessionService().setAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);
		redirectModel.addFlashAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);

		travelCartFacade.deleteCurrentCart();

		final String urlParameters = buildUrlParameters(fareFinderForm);
		final String redirectUrl = REDIRECT_PREFIX + TraveladdonWebConstants.FARE_SELECTION_ROOT_URL;

		return urlParameters.isEmpty() ? redirectUrl : redirectUrl + urlParameters;
	}

	protected void populateInitialData(final Model model)
	{
		final FareFinderForm fareFinderForm = new FareFinderForm();
		initializeFareFinderForm(fareFinderForm);
		model.addAttribute(TraveladdonWebConstants.FARE_FINDER_FORM, fareFinderForm);
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
						: getConfigurationService().getConfiguration().getString(DEFAULT_LOCATION_TYPE));
		urlParameters.put(ARRIVAL_LOCATION_NAME, fareFinderForm.getArrivalLocationName());
		urlParameters.put(ARRIVAL_LOCATION_TYPE,
				StringUtils.isNotBlank(fareFinderForm.getArrivalLocationSuggestionType())
						? fareFinderForm.getArrivalLocationSuggestionType()
						: getConfigurationService().getConfiguration().getString(DEFAULT_LOCATION_TYPE));

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

	protected void initializeFareFinderForm(final FareFinderForm fareFinderForm)
	{

		fareFinderForm.setTripType(TripType.RETURN.toString());
		fareFinderForm.setTravellingWithChildren(false);

		final List<PassengerTypeQuantityData> passengerTypeQuantityList = getPassengerTypeQuantityList();

		for (final PassengerTypeQuantityData passengeTypeQuantityData : passengerTypeQuantityList)
		{
			if (passengeTypeQuantityData.getPassengerType().getCode().equals(TraveladdonWebConstants.PASSENGER_TYPE_ADULT))
			{
				passengeTypeQuantityData.setQuantity(TravelservicesConstants.DEFAULT_ADULTS);
			}
		}

		fareFinderForm.setPassengerTypeQuantityList(passengerTypeQuantityList);

		// set default Cabin Class
		fareFinderForm.setCabinClass(ECONOMY_CABIN_CLASS_CODE);
	}

	@Override
	protected int getMaxGuestQuantity()
	{
		return getConfigurationService().getConfiguration().getInt(TravelfacadesConstants.MAX_TRANSPORT_GUEST_QUANTITY);
	}
}
