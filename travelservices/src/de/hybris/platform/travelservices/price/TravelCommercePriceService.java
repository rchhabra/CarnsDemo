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
package de.hybris.platform.travelservices.price;

import de.hybris.platform.commerceservices.price.CommercePriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.travelservices.price.data.PriceLevel;

import java.util.List;
import java.util.Map;


/**
 * Extends the {@link CommercePriceService} to retrieve the applicable pricing model for a product and travel specific
 * search criteria
 *
 * @spring.bean travelCommercePriceService
 */
public interface TravelCommercePriceService extends CommercePriceService
{
	/**
	 * Method to retrieve price for the product and travel specific search criteria
	 *
	 * @param product
	 * 		the product
	 * @param searchCriteria
	 * 		the map that has travel specific search criteria
	 * @return PriceInformation product web price
	 */
	PriceInformation getProductWebPrice(ProductModel product, Map<String, String> searchCriteria);

	/**
	 * Method to retrieve taxes for the product
	 *
	 * @param product
	 * 		the product
	 * @return TaxInformation product tax informations
	 */
	List<TaxInformation> getProductTaxInformations(ProductModel product);

	/**
	 * This method gets the price information for product code, search key and search value.
	 *
	 * @param product
	 * 		the product
	 * @param searchKey
	 * 		the search key
	 * @param searchValue
	 * 		the search value
	 * @return PriceInformation price information
	 */
	PriceInformation getPriceInformation(ProductModel product, String searchKey, String searchValue);

	/**
	 * Method to find the price level info. The first available price information is used to build the map.
	 *
	 * @param productCode
	 * 		the product code
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param routeCode
	 * 		the route code
	 * @param isMultiSectorRoute
	 * 		the is multi sector route
	 * @return the price level info
	 */
	PriceLevel getPriceLevelInfo(String productCode, List<String> transportOfferings, String routeCode,
			boolean isMultiSectorRoute);

	/**
	 * Method to check if the price information is available for the given search criteria
	 *
	 * @param product
	 * 		the product
	 * @param searchKey
	 * 		the search key
	 * @param searchValue
	 * 		the search value
	 * @return boolean boolean
	 */
	boolean isPriceInformationAvailable(ProductModel product, String searchKey, String searchValue);

	/**
	 * Method to get the price level and value for product code, search key and search value. The price level is searched
	 * in the order of transportoffering, sector, route and default price. The first available price level will be
	 * returned.
	 *
	 * @param product
	 * 		the product
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param routeCode
	 * 		the route code
	 * @return price level
	 */
	PriceLevel getPriceLevelInfoByHierarchy(ProductModel product, String transportOfferingCode, String routeCode);

	/**
	 * Method to set price and tax search criteria in session context These values will be used to retrieve prices during
	 * cart calculation when the product is added to cart.
	 *
	 * @param priceLevel
	 * 		the price level
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param passengerType
	 * 		the passenger type
	 */
	void setPriceAndTaxSearchCriteriaInContext(PriceLevel priceLevel, List<String> transportOfferingCodes, String passengerType);

	/**
	 * Method to set price search criteria in session context These values will be used to retrieve prices during cart
	 * calculation when the product is added to cart.
	 *
	 * @param priceLevel
	 * 		the price level
	 */
	void setPriceSearchCriteriaInContext(PriceLevel priceLevel);

	/**
	 * Method to set values in Session Context.
	 * These values will be used to retrieve taxes during cart calculation when the product is added to cart.
	 *
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param passengerType
	 * 		the passenger type
	 */
	void setTaxSearchCriteriaInContext(List<String> transportOfferingCodes, String passengerType);

	/**
	 * Method to add price level property to cart entry
	 *
	 * @param priceLevel
	 * 		the price level
	 * @param productCode
	 * 		the product code
	 * @param entryNo
	 * 		the entry no
	 */
	void addPropertyPriceLevelToCartEntry(PriceLevel priceLevel, String productCode, int entryNo);

	/**
	 * Method to return price level info as a map, with price level as key and code as value. Null is returned if route
	 * level price is not set for multi sector route.
	 *
	 * @param productModel
	 * @param transportOfferings
	 * @param routeCode
	 * @param isMultiSectorRoute
	 * @return price level info
	 */
	PriceLevel getPriceLevelInfoForFareProduct(ProductModel productModel, List<String> transportOfferings,
			String routeCode, boolean isMultiSectorRoute);

	/**
	 * Method to return price level info as a map, with price level as key and code as value.
	 *
	 * @param productModel
	 * 		the product model
	 * @param transportOfferingCode
	 * 		the transport offering code
	 * @param routeCode
	 * 		the route code
	 * @return price level info
	 */
	PriceLevel getPriceLevelInfoForAncillary(ProductModel productModel, String transportOfferingCode, String routeCode);

}
