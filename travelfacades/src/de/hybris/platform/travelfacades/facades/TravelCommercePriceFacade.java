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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.travelservices.price.data.PriceLevel;

import java.math.BigDecimal;
import java.util.List;


/**
 * Facade implementation for prices.
 */
public interface TravelCommercePriceFacade
{

	/**
	 * This method gets the price for a product.
	 *
	 * @param productCode
	 *           the product code
	 * @return PriceInformation price information
	 */
	PriceInformation getPriceInformation(String productCode);

	/**
	 * This method gets the price information for the given product code, search key and search value.
	 *
	 * @param productCode
	 *           the product code
	 * @param searchKey
	 *           the search key
	 * @param searchValue
	 *           the search value
	 * @return PriceInformation price information
	 */
	PriceInformation getPriceInformation(String productCode, String searchKey, String searchValue);

	/**
	 * Method to get the price information for product code, search key and search value. The prices are searched in the
	 * order of transportoffering, sector, route and default price. The first available price will be returned.
	 *
	 * @param productCode
	 *           the product code
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param sectorCode
	 *           the sector code
	 * @param routeCode
	 *           the route code
	 * @return PriceInformation price information by hierarchy
	 */
	PriceInformation getPriceInformationByHierarchy(String productCode, String transportOfferingCode, String sectorCode,
			String routeCode);

	/**
	 * This method checks if any ChangeProductPriceBundleRule exist for the product, if found then it created
	 * PriceInformation based on the price set within the rule
	 *
	 * @param bundleTemplate
	 *           the bundle template
	 * @param productCode
	 *           the product code
	 * @return PriceInformation price information by product price bundle rule
	 */
	PriceInformation getPriceInformationByProductPriceBundleRule(BundleTemplateModel bundleTemplate, String productCode);

	/**
	 * Method to find the price level info. The first available price information is used to build the map.
	 *
	 * @param productCode
	 *           the product code
	 * @param transportOfferings
	 *           the transport offerings
	 * @param routeCode
	 *           the route code
	 * @return the price level info
	 */
	PriceLevel getPriceLevelInfo(String productCode, List<String> transportOfferings, String routeCode);

	/**
	 * Method to get the price level and value for product code, search key and search value. The price level is searched
	 * in the order of transportoffering, sector, route and default price. The first available price level will be
	 * returned.
	 *
	 * @param product
	 *           the product
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param routeCode
	 *           the route code
	 * @return price level
	 */
	PriceLevel getPriceLevelInfoByHierarchy(ProductModel product, String transportOfferingCode, String routeCode);

	/**
	 * Method to set price and tax search criteria in session context These values will be used to retrieve prices during
	 * cart calculation when the product is added to cart.
	 *
	 * @param priceLevel
	 *           the price level
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travellerData
	 *           the traveller data
	 */
	void setPriceAndTaxSearchCriteriaInContext(PriceLevel priceLevel, List<String> transportOfferingCodes,
			TravellerData travellerData);

	/**
	 * Method to set price and tax search criteria in session context These values will be used to retrieve prices during
	 * cart calculation when the product is added to cart.
	 *
	 * @param priceLevel
	 *           the price level
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 */
	void setPriceAndTaxSearchCriteriaInContext(PriceLevel priceLevel, List<String> transportOfferingCodes);

	/**
	 * Method to set price search criteria in session context These values will be used to retrieve prices during cart
	 * calculation when the product is added to cart.
	 *
	 * @param priceLevel
	 *           the price level
	 */
	void setPriceSearchCriteriaInContext(PriceLevel priceLevel);

	/**
	 * Method to set values in Session Context. These values will be used to retrieve taxes during cart calculation when
	 * the product is added to cart.
	 *
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param travellerData
	 *           the traveller data
	 */
	void setTaxSearchCriteriaInContext(List<String> transportOfferingCodes, TravellerData travellerData);

	/**
	 * Method to check if the price information is available for the given search criteria
	 *
	 * @param product
	 *           the product
	 * @param searchKey
	 *           the search key
	 * @param searchValue
	 *           the search value
	 * @return boolean boolean
	 */
	boolean isPriceInformationAvailable(ProductModel product, String searchKey, String searchValue);

	/**
	 * Method to add price level property to cart entry
	 *
	 * @param priceLevel
	 *           the price level
	 * @param productCode
	 *           the product code
	 * @param entryNo
	 *           the entry no
	 */
	void addPropertyPriceLevelToCartEntry(PriceLevel priceLevel, String productCode, int entryNo);

	/**
	 * Method to get booking level fees and taxes
	 *
	 * @return booking fees and taxes
	 */
	BigDecimal getBookingFeesAndTaxes();

	/**
	 * Method to create a price data for given price value
	 *
	 * @param price
	 *           the price
	 * @return price data
	 */
	PriceData createPriceData(double price);

	/**
	 * Method to create a price data for given price value along with the scale
	 *
	 * @param price
	 *           the price
	 * @param scale
	 * @return price data
	 */
	PriceData createPriceData(double price, int scale);

	/**
	 * Method to create a price data for given price value along with the scale
	 *
	 * @param price
	 *           the price
	 * @param scale
	 * @param currencyIsoCode
	 * @return price data
	 */
	PriceData createPriceData(double price, int scale, String currencyIsoCode);


	/**
	 * Method to create a price data for given price value along with the scale
	 *
	 * @param price
	 *           the price
	 * @param currencyIsoCode
	 *           the currencyIsoCode
	 * @return price data
	 */
	PriceData createPriceData(double price, String currencyIsoCode);

	/**
	 * Creates a PriceData object with a formatted currency string based on the price type and currency ISO code.
	 *
	 * @param priceType
	 *           The price type
	 * @param value
	 *           The price amount
	 * @param currencyIso
	 *           The currency ISO code
	 *
	 * @return the price data
	 */
	PriceData createPriceData(final PriceDataType priceType, final BigDecimal value, final String currencyIso);

	/**
	 * Creates a PriceData object with a formatted currency string based on the price type and currency.
	 *
	 * @param priceType
	 *           The price type
	 * @param value
	 *           The price amount
	 * @param currency
	 *           The currency
	 *
	 * @return the price data
	 */
	PriceData createPriceData(PriceDataType priceType, BigDecimal value, CurrencyModel currency);


	/**
	 *
	 * @param reservationData
	 * @return
	 */
	PriceData getPaidAmount(final AccommodationReservationData reservationData);

	/**
	 *
	 * @param reservationData
	 * @param amountPaid
	 * @return
	 */
	PriceData getDueAmount(final AccommodationReservationData reservationData, final PriceData amountPaid);

}
