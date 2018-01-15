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

import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontControllerConstants;
import de.hybris.platform.travelacceleratorstorefront.forms.AdditionalSecurityForm;
import de.hybris.platform.travelacceleratorstorefront.forms.ManageMyBookingForm;
import de.hybris.platform.travelacceleratorstorefront.model.components.ManageMyBookingComponentModel;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Controller for Manage My Booking Component
 * 
 */
@Controller("ManageMyBookingComponentController")
@RequestMapping(value = TravelacceleratorstorefrontControllerConstants.Actions.Cms.ManageMyBookingComponent)
public class ManageMyBookingComponentController extends SubstitutingCMSAddOnComponentController<ManageMyBookingComponentModel>
{
	/**
	 * This method fills ManageMyBookingComponent model with ManageMyBookingForm.
	 */
	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ManageMyBookingComponentModel component)
	{
		model.addAttribute(new ManageMyBookingForm());
		model.addAttribute(new AdditionalSecurityForm());
	}

}
