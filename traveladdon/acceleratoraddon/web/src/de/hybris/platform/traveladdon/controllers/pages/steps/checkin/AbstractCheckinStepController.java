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
package de.hybris.platform.traveladdon.controllers.pages.steps.checkin;

import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.traveladdon.checkin.steps.CheckinGroup;
import de.hybris.platform.traveladdon.checkin.steps.CheckinStep;
import de.hybris.platform.travelfacades.facades.CheckInFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;


public abstract class AbstractCheckinStepController extends TravelAbstractPageController
{

	@Resource(name = "checkinFlowGroupMap")
	private Map<String, CheckinGroup> checkinFlowGroupMap;

	@Resource(name = "checkInFacade")
	private CheckInFacade checkInFacade;

	@ModelAttribute("checkoutSteps")
	public List<CheckinSteps> addCheckinStepsToModel()
	{
		final CheckinGroup checkinGroup = getCheckinFlowGroupMap().get("travelCheckinGroup");
		final Map<String, CheckinStep> progressBarMap = checkinGroup.getCheckinProgressBar();
		final List<CheckinSteps> checkinSteps = new ArrayList<CheckinSteps>(progressBarMap.size());

		for (final Map.Entry<String, CheckinStep> entry : progressBarMap.entrySet())
		{
			final CheckinStep checkinStep = entry.getValue();
			if (checkinStep.isEnabled())
			{
				checkinSteps.add(new CheckinSteps(checkinStep.getProgressBarId(),
						StringUtils.remove(checkinStep.currentStep(),
						"redirect:"), Integer.valueOf(entry.getKey())));
			}
		}
		return checkinSteps;
	}

	protected CheckinStep getCheckinStep(final String currentController)
	{
		final CheckinGroup checkinGroup = getCheckinFlowGroupMap().get(checkInFacade.getCheckinFlowGroupForCheckout());
		return checkinGroup.getCheckinStepMap().get(currentController);
	}

	protected void setCheckinStepLinksForModel(final Model model, final CheckinStep checkinStep)
	{
		model.addAttribute("previousStepUrl", StringUtils.remove(checkinStep.previousStep(), "redirect:"));
		model.addAttribute("nextStepUrl", StringUtils.remove(checkinStep.nextStep(), "redirect:"));
		model.addAttribute("currentStepUrl", StringUtils.remove(checkinStep.currentStep(), "redirect:"));
		model.addAttribute("progressBarId", checkinStep.getProgressBarId());
	}

	public static class CheckinSteps
	{
		private final String progressBarId;
		private final String url;
		private final Integer stepNumber;

		public CheckinSteps(final String progressBarId, final String url, final Integer stepNumber)
		{
			this.progressBarId = progressBarId;
			this.url = url;
			this.stepNumber = stepNumber;
		}

		protected String getProgressBarId()
		{
			return progressBarId;
		}

		protected String getUrl()
		{
			return url;
		}

		protected Integer getStepNumber()
		{
			return stepNumber;
		}
	}

	/**
	 * @return the checkin flow group map
	 */
	protected Map<String, CheckinGroup> getCheckinFlowGroupMap()
	{
		return checkinFlowGroupMap;
	}
}
