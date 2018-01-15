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
 */

package de.hybris.platform.travelfacades.facades.packages;

import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackagesResponseData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;

import java.util.Date;
import java.util.List;


/**
 * DealBundleTemplate Facade interface which provides functionality for the DealBundleTemplates.
 */
public interface DealBundleTemplateFacade
{
	/**
	 * Evaluates valid dates in dd/MM/yyyy format for the availability of the deal based on the dealStartingPattern and
	 * the given date.
	 *
	 * @param dealStartingDatePattern
	 *           cronJob expression representing the valid dates for deal.
	 * @param dealDepartureDate
	 *           the date to use as a startingDate for the deal requested
	 *
	 * @return the list of valid dates
	 */
	List<String> getFormattedDealValidDates(String dealStartingDatePattern, String dealDepartureDate);

	/**
	 * Evaluates valid dates for the availability of the deal based on the dealStartingPattern and the given date.
	 *
	 * @param dealStartingDatePattern
	 *           cronJob expression representing the valid dates for deal.
	 * @param dealDepartureDate
	 *           the date to use as a startingDate for the deal requested
	 *
	 * @return the list of valid dates
	 */
	List<Date> getDealValidDates(String dealStartingDatePattern, String dealDepartureDate);

	/**
	 * Returns the DealBundleTemplateModel corresponding to the given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId
	 *           the id of the DealBundleTemplate
	 *
	 * @return the DealBundleTemplateModel corresponding to the given id, null if no DealBundleTemplates are found.
	 */
	DealBundleTemplateModel getDealBundleTemplateById(String dealBundleTemplateId);

	/**
	 * Returns the CronJobExpression for the deal availability corresponding to the given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId
	 *           the id of the DealBundleTemplate
	 *
	 * @return the cronJobExpression corresponding to the given id, Empty if no DealBundleTemplates are found.
	 */
	String getDealValidCronJobExpressionById(String dealBundleTemplateId);

	/**
	 * Returns the PackageRequestData populated from the given dealBundleTemplateId
	 *
	 * @param dealBundleTemplateId
	 *           the dealBundleTemplateId to use to populate the PackagesResponseData
	 * @param selectedDepartureDate
	 *           the date to use as a selectedDepartureDate for the deal requested
	 *
	 * @return the PackageRequestData
	 */
	PackageRequestData getPackageRequestData(String dealBundleTemplateId, String selectedDepartureDate);

	/**
	 * Performs a search for the DealBundleTemplate based on the PackagesRequestData and the given date.
	 *
	 * @param packageRequestData
	 * 		the PackageRequestData
	 *
	 * @return the PackageRequestData with the deal bundle template details matching the request parameters and the given starting
	 * date
	 */
	PackagesResponseData getPackageResponseDetails(PackageRequestData packageRequestData);

	/**
	 * Checks if the deal bundle template exists in the cart.
	 *
	 * @param dealBundleTemplateId
	 *           the deal bundle template id
	 * @return the boolean
	 */
	boolean isDealBundleTemplateMatchesCart(String dealBundleTemplateId);

	/**
	 * Checks if is departure date in cart equals.
	 *
	 * @param dealDepartureDate
	 *           the deal departure date
	 * @return true, if is departure date in cart equals
	 */
	boolean isDepartureDateInCartEquals(Date dealDepartureDate);

	/**
	 * Checks whether given abstract order contains a deal
	 * @param abstractOrderModel
	 * @return
	 */
	boolean isDealAbstractOrder(AbstractOrderModel abstractOrderModel);

	/**
	 * Returns the deal bundle template id contained in the given abstract order. It returns an empty string if the abstractOrder
	 * doesn't contain any deal.
	 *
	 * @param abstractOrderModel
	 * 		as the abstract order model
	 *
	 * @return a string corresponding to the deal bundle template id
	 */
	String getDealBundleTemplateIdFromAbstractOrder(AbstractOrderModel abstractOrderModel);
}
