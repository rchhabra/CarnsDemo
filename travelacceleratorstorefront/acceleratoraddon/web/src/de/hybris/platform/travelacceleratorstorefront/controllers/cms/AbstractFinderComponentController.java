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

package de.hybris.platform.travelacceleratorstorefront.controllers.cms;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.travel.CabinClassData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.model.components.AbstractFinderComponentModel;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.CabinClassFacade;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


public abstract class AbstractFinderComponentController
		extends SubstitutingCMSAddOnComponentController<AbstractFinderComponentModel>
{
	private static final Logger LOG = Logger.getLogger(AbstractFinderComponentController.class);

	private static final String GUEST_QUANTITY = "guestQuantity";

	protected static final String HIDE_FINDER_TITLE = "hideFinderTitle";

	private static final String ACCOMMODATION_QUANTITY = "accommodationsQuantity";

	private static final String CABIN_CLASSES = "cabinClasses";

	private static final String SHOW_COMPONENT = "showComponent";

	@Resource(name = "passengerTypeFacade")
	private PassengerTypeFacade passengerTypeFacade;

	@Resource(name = "travellerSortStrategy")
	private TravellerSortStrategy travellerSortStrategy;

	@Resource(name = "cabinClassFacade")
	private CabinClassFacade cabinClassFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;


	protected RoomStayCandidateData createRoomStayCandidatesData()
	{
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		roomStayCandidateData.setPassengerTypeQuantityList(getPassengerTypeQuantityList());
		for (final PassengerTypeQuantityData passengeTypeQuantityData : roomStayCandidateData.getPassengerTypeQuantityList())
		{
			if (passengeTypeQuantityData.getPassengerType().getCode().equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
			{
				passengeTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_ADULT_QUANTITY);
			}
		}
		return roomStayCandidateData;
	}

	protected List<PassengerTypeQuantityData> getPassengerTypeQuantityList()
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<PassengerTypeData> sortedPassengerTypes = getTravellerSortStrategy()
				.sortPassengerTypes(getPassengerTypeFacade().getPassengerTypes());
		for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
		return passengerTypeQuantityList;
	}

	protected abstract int getMaxGuestQuantity();

	@ModelAttribute(GUEST_QUANTITY)
	public List<String> populatePassengersQuantity()
	{
		final List<String> guestsQuantity = new ArrayList<>();
		final int maxGuestQuantity = getMaxGuestQuantity();
		for (int i = 0; i <= maxGuestQuantity; i++)
		{
			guestsQuantity.add(String.valueOf(i));
		}
		return guestsQuantity;
	}

	@ModelAttribute(ACCOMMODATION_QUANTITY)
	public List<String> populateAccommodationsQuantity()
	{
		final List<String> accommodationQuantity = new ArrayList<>();
		final int maxAccommodationsAllowed = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		for (int i = 1; i <= maxAccommodationsAllowed; i++)
		{
			accommodationQuantity.add(String.valueOf(i) + " ");

		}
		return accommodationQuantity;
	}

	/**
	 * Method responsible for retrieving and setting a list of cabin classes onto the model
	 */
	@ModelAttribute(CABIN_CLASSES)
	public List<CabinClassData> setCabinClassesOnModel()
	{
		return getCabinClassFacade().getCabinClasses();
	}

	/**
	 * Method responsible to set a flag which will decide if a component is to be collapsed or expanded on the selection
	 * page
	 */
	@ModelAttribute(SHOW_COMPONENT)
	public boolean setCollapseExpandFlag()
	{
		if (getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_SELECTION_DATA) != null
				|| getSessionService()
						.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES) != null
				|| getSessionService()
						.getAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES) != null)
		{
			return false;
		}
		return true;
	}

	/**
	 * method responsible for reloading the component
	 *
	 * @param componentUid
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	protected String getComponent(@RequestParam final String componentUid, final HttpServletRequest request,
			final HttpServletResponse response, final Model model)
	{
		request.setAttribute(AbstractCMSAddOnComponentController.COMPONENT_UID, componentUid);
		request.setAttribute(HIDE_FINDER_TITLE, true);
		request.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_ACCOMMODATION_FINDER_FORM,
				getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_ACCOMMODATION_FINDER_FORM));
		request.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_FINDER_FORM,
				getSessionService().getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_FINDER_FORM));
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
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @return the passengerTypeFacade
	 */
	protected PassengerTypeFacade getPassengerTypeFacade()
	{
		return passengerTypeFacade;
	}

	/**
	 * @return the travellerSortStrategy
	 */
	protected TravellerSortStrategy getTravellerSortStrategy()
	{
		return travellerSortStrategy;
	}

	/**
	 * @return the cabinClassFacade
	 */
	protected CabinClassFacade getCabinClassFacade()
	{
		return cabinClassFacade;
	}

}
