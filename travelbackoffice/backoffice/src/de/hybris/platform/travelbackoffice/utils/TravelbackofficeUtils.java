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

package de.hybris.platform.travelbackoffice.utils;

import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;

import org.apache.commons.lang3.StringUtils;

/**
 * Class which provides Backoffice utility methods.
 */
public class TravelbackofficeUtils {

	/**
	 * Method append the constant string if it contains {@code TravelbackofficeConstants.TYPE_REFERENCE} otherwise not.
	 */
	public static String replaceTypeReference(final String stockLevelCode) {
		return new StringBuilder(stockLevelCode).append(TravelbackofficeConstants.TYPE_REFERENCE_CONSTANT).toString();
	}

	/**
	 * Method validates the input string and replace if it contains
	 * {@code TravelbackofficeConstants.TYPE_REFERENCE} otherwise returns input
	 * string
	 * 
	 * @param stockLevelCode
	 */
	public static String validateStockLevelCode(final String stockLevelCode) {
		if (StringUtils.equalsIgnoreCase(stockLevelCode, TravelbackofficeConstants.TYPE_REFERENCE)) {
			return replaceTypeReference(stockLevelCode);
		}
		return stockLevelCode;
	}

}
