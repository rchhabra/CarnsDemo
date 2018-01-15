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

package de.hybris.platform.travelb2bfacades.budget;

import de.hybris.platform.b2bcommercefacades.company.B2BBudgetFacade;

import java.util.Date;


/**
 * The Interface TravelB2BBudgetFacade.
 */
public interface TravelB2BBudgetFacade extends B2BBudgetFacade
{

	/**
	 * Gets the date from string date.
	 * @param date
	 * @return the date from string date
	 */
	Date getDateFromStringDate(String date);

	/**
	 * Gets the string date from date.
	 * @param date
	 * @return the string date from date
	 */
	String getStringDateFromDate(Date date);
}
