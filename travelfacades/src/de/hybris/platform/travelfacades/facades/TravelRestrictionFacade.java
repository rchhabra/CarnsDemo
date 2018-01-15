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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Map;


/**
 * Exposes the services relevant to TravelRestrictionDTO
 */
public interface TravelRestrictionFacade
{

	/**
	 * Method to perform a validation on the product quantity against the TravelRestriction
	 *
	 * @param productCode
	 * 		as the code of the product to be added/removed from the cart
	 * @param quantity
	 * 		as the quantity of the product to be added/removed
	 * @param travelRouteCode
	 * 		as the travel Route Code
	 * @param transportOfferingCodes
	 * 		as the list of transportOffering codes
	 * @param travellerUid
	 * 		as the Traveller unique id
	 * @return true if the quantity to be added/removed fulfils the TravelRestriction and the product can be added/removed
	 * from the cart, false otherwise
	 */
	boolean checkIfProductCanBeAdded(String productCode, long quantity, String travelRouteCode,
			List<String> transportOfferingCodes, String travellerUid);

	/**
	 * Method to perform a validation on the product quantity against the TravelRestriction on the provided orderModel
	 *
	 * @param productModel
	 * 		as the product to be added/removed from the cart
	 * @param quantity
	 * 		as the quantity of the product to be added/removed
	 * @param travelRouteCode
	 * 		as the travel Route Code
	 * @param transportOfferingCodes
	 * 		as the list of transportOffering codes
	 * @param travellerUid
	 * 		as the Traveller unique id
	 * @param orderModel
	 * 		as that need to be check against
	 * @return true if the quantity to be added/removed fulfils the TravelRestriction and the product can be added/removed
	 * from the cart, false otherwise
	 */
	boolean checkIfProductCanBeAdded(ProductModel productModel, long quantity, String travelRouteCode,
			List<String> transportOfferingCodes, String travellerUid, AbstractOrderModel orderModel);

	/**
	 * Method to perform a validation on the quantity of distinct products for all the categories
	 *
	 * @return true if the TravelRestriction are fulfilled, false otherwise
	 */
	boolean checkCategoryRestrictions();

	/**
	 * Method to perform a validation on the quantity of distinct products for all the categories based on the
	 * categoryRestriction.
	 *
	 * @return a map where the key is the localized name of the OfferGroup and the value is the string representing the
	 * minimum quantity of distinct products that should be added.
	 */
	Map<String, String> getCategoryRestrictionErrors();

	/**
	 * This method returns the TravelRestrictionData of the product specified by the productCode
	 *
	 * @param productCode
	 * 		as the code of the product
	 * @return the TravelRestrictionData of the product
	 */
	TravelRestrictionData getTravelRestrictionForProduct(String productCode);

	/**
	 * This method returns the TravelRestrictionData of the category specified by the categoryCode
	 *
	 * @param categoryCode
	 * 		as the code of the category
	 * @return the TravelRestrictionData of the category
	 */
	TravelRestrictionData getTravelRestrictionForCategory(String categoryCode);

	/**
	 * Returns the code of the AddToCartCriteria restriction for the given productCode. It retrieves the offerGroup of
	 * the product and returns its addToCartCriteria restriction if present, the default criteria otherwise.
	 *
	 * @param productCode
	 * 		the product code
	 * @return the code of the AddToCartCriteria restriction for the given product
	 */
	String getAddToCartCriteria(String productCode);

}
