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
package de.hybris.platform.traveladdon.checkin.steps;



import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public class CheckinStep
{
	public static final String PREVIOUS = "previous";
	public static final String CURRENT = "current";
	public static final String NEXT = "next";
	private Map<String, String> transitions;
	private CheckinGroup checkinGroup;
	private String progressBarId;

	public String go(final String transition)
	{
		if (getTransitions().containsKey(transition))
		{
			return getTransitions().get(transition);
		}
		return null;
	}

	/**
	 * @return boolean
	 */
	public boolean isEnabled()
	{
		return true;
	}

	/**
	 * @return String
	 */
	public String previousStep()
	{
		return go(PREVIOUS);
	}

	/**
	 * @return String
	 */
	public String currentStep()
	{
		return go(CURRENT);
	}

	/**
	 * @return String
	 */
	public String nextStep()
	{

		return go(NEXT);
	}

	/**
	 * @return the transition map
	 */
	public Map<String, String> getTransitions()
	{
		return transitions;
	}

	/**
	 * @param transitions
	 */
	@Required
	public void setTransitions(final Map<String, String> transitions)
	{
		this.transitions = transitions;
	}

	/**
	 * @return String the progress bar id
	 */
	public String getProgressBarId()
	{
		return progressBarId;
	}

	/**
	 * @param progressBarId
	 */
	@Required
	public void setProgressBarId(final String progressBarId)
	{
		this.progressBarId = progressBarId;
	}

	/**
	 * @return the checkinGroup
	 */
	public CheckinGroup getCheckinGroup()
	{
		return checkinGroup;
	}

	/**
	 * @param checkinGroup
	 *           the checkinGroup to set
	 */
	@Required
	public void setCheckinGroup(final CheckinGroup checkinGroup)
	{
		this.checkinGroup = checkinGroup;
	}
}
