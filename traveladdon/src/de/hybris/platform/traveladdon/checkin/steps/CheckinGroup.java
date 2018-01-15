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
public class CheckinGroup
{
	private String groupId;
	private Map<String, CheckinStep> checkinStepMap;
	private Map<String, CheckinStep> checkinProgressBar;

	/**
	 * @return String
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * @param groupId
	 */
	@Required
	public void setGroupId(final String groupId)
	{
		this.groupId = groupId;
	}

	/**
	 * @return the checkinStepMap
	 */
	public Map<String, CheckinStep> getCheckinStepMap()
	{
		return checkinStepMap;
	}

	/**
	 * @param checkinStepMap
	 *           the checkinStepMap to set
	 */
	@Required
	public void setCheckinStepMap(Map<String, CheckinStep> checkinStepMap)
	{
		this.checkinStepMap = checkinStepMap;
	}

	/**
	 * @return the checkinProgressBar
	 */
	public Map<String, CheckinStep> getCheckinProgressBar()
	{
		return checkinProgressBar;
	}

	/**
	 * @param checkinProgressBar
	 *           the checkinProgressBar to set
	 */
	@Required
	public void setCheckinProgressBar(Map<String, CheckinStep> checkinProgressBar)
	{
		this.checkinProgressBar = checkinProgressBar;
	}
}
