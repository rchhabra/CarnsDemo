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

package de.hybris.platform.travelacceleratorstorefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.travelfacades.facades.TravellerFacade;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Extended Controller for home page - adds functionality to AccountPageController
 */
@Controller
public class TravelAccountPageController extends AbstractPageController
{
	// CMS Pages
	protected static final String ACCOUNT_MY_BOOKINGS_CMS_PAGE = "my-bookings";
	protected static final String ACCOUNT_DISABILITY_AND_MOBILITY_CMS_PAGE = "disability-and-mobility";
	protected static final String ERROR = "error";
	protected static final String SPECIAL_SERVICE_REQUESTS = "specialServiceRequests";
	protected static final String DISABILITY = "disability";
	protected static final String TRUE = "true";
	protected static final String FORM_GLOBAL_CONFIRMATION = "form.global.confirmation";
	protected static final String BREADCRUMBS = "breadcrumbs";

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@RequestMapping(value = "/my-account/my-bookings", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getMyBookingsPage(final Model model) throws CMSItemNotFoundException
	{
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_MY_BOOKINGS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_MY_BOOKINGS_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/my-account/disability-and-mobility", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getDisabilityAndMobilityPage(final Model model) throws CMSItemNotFoundException
	{
		final TravellerData traveller = travellerFacade.getCurrentUserDetails();

		if (traveller != null && traveller.getSpecialRequestDetail() != null)
		{
			final List<String> specialServiceRequestCodes = new ArrayList<>();

			traveller.getSpecialRequestDetail().getSpecialServiceRequests().forEach(ssr -> {
				specialServiceRequestCodes.add(ssr.getCode());
			});
			model.addAttribute(SPECIAL_SERVICE_REQUESTS, specialServiceRequestCodes);
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_DISABILITY_AND_MOBILITY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_DISABILITY_AND_MOBILITY_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	@RequestMapping(value = "/my-account/disability-and-mobility", method = RequestMethod.POST)
	@RequireHardLogIn
	public String updateDisabilityAndMobilityPage(@RequestParam(required = false) final String specialAssistance,
			final Model model) throws CMSItemNotFoundException
	{
		final List<String> specialServiceRequestCodes = new ArrayList<>();

		if (StringUtils.isNotEmpty(specialAssistance) && StringUtils.equalsIgnoreCase(specialAssistance, TRUE))
		{
			specialServiceRequestCodes.add(DISABILITY);
		}

		travellerFacade.updateCurrentUserSpecialRequestDetails(specialServiceRequestCodes);

		GlobalMessages.addInfoMessage(model, FORM_GLOBAL_CONFIRMATION);
		storeCmsPageInModel(model, getContentPageForLabelOrId(ACCOUNT_DISABILITY_AND_MOBILITY_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ACCOUNT_DISABILITY_AND_MOBILITY_CMS_PAGE));
		model.addAttribute(BREADCRUMBS, accountBreadcrumbBuilder.getBreadcrumbs(null));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getDisabilityAndMobilityPage(model);
	}

}
