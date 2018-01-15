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

package de.hybris.platform.travelcommons.controllers.cms;

import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.cms.SubstitutingCMSAddOnComponentController;
import de.hybris.platform.travelcommons.controllers.TravelcommonsControllerConstants;
import de.hybris.platform.travelcommons.model.components.TabbedFinderComponentModel;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller("TabbedFinderComponentController")
@RequestMapping(value = TravelcommonsControllerConstants.Actions.Cms.TabbedFinderComponent)
public class TabbedFinderComponentController extends SubstitutingCMSAddOnComponentController<TabbedFinderComponentModel>
{
	private static final String TRAVEL_FINDER_COMPONENT = "travelFinderComponentUid";
	private static final String PACKAGE_FINDER_COMPONENT = "packageFinderComponentUid";
	private static final String FARE_FINDER_COMPONENT = "fareFinderComponentUid";
	private static final String ACCOMMODATION_FINDER_COMPONENT = "accommodationFinderComponentUid";
	private static final String SHOW_COMPONENT = "showComponent";

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final TabbedFinderComponentModel component)
	{
		model.addAttribute(TRAVEL_FINDER_COMPONENT, component.getTravelFinder().getUid());
		model.addAttribute(PACKAGE_FINDER_COMPONENT, component.getPackageFinder().getUid());
		model.addAttribute(FARE_FINDER_COMPONENT, component.getFareFinder().getUid());
		model.addAttribute(ACCOMMODATION_FINDER_COMPONENT, component.getAccommodationFinder().getUid());
		model.addAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY));
	}

	@ModelAttribute(SHOW_COMPONENT)
	public boolean setCollapseExpandFlag()
	{
		if (sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_FARE_SELECTION_DATA) != null
				|| sessionService
						.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_RESPONSE_PROPERTIES) != null
				|| sessionService.getAttribute(TravelacceleratorstorefrontWebConstants.PACKAGE_SEARCH_RESPONSE_PROPERTIES) != null)
		{
			return false;
		}
		return true;
	}

}
