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
package de.hybris.platform.travelservices.order;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;

import java.util.List;
import java.util.Map;


/**
 * Overrides the {@link BundleCommerceCartService} to handle more than one bundle entry. cart entry is created for each
 * transport offering per passenger
 */
public interface TravelCommerceCartService extends BundleCommerceCartService
{

	/**
	 * Add properties to cart entry.
	 *
	 * @param masterCartModel
	 *           the cart model. It must exist and it must be a master cart.
	 * @param entryNo
	 *           entryNo for cart entry
	 * @param product
	 *           the product model that will be added to the cart
	 * @param propertiesMap
	 *           Map containing all the properties to be added to the cart Entry
	 */
	void addPropertiesToCartEntry(CartModel masterCartModel, int entryNo, ProductModel product, Map<String, Object> propertiesMap);

	/**
	 * Add properties to cart entry for bundle.
	 *
	 * @param masterCartModel
	 *           the cart model. It must exist and it must be a master cart.
	 * @param bundleNo
	 *           bundleNo in Cart Entry
	 * @param product
	 *           the product model that will be added to the cart
	 * @param propertiesMap
	 *           Map containing all the properties to be added to the cart Entry
	 */
	void addPropertiesToCartEntryForBundle(CartModel masterCartModel, int bundleNo, ProductModel product,
			Map<String, Object> propertiesMap);

	/**
	 * Method to retrieve all the cart entries for a specified categoryModel
	 *
	 * @param cartModel
	 *           as the cartModel
	 * @param categoryModel
	 *           as the categoryModel to use to retrieve the cart entries
	 * @param travelRouteCode
	 *           as the travelRoute code
	 * @param transportOfferingCodes
	 *           as the list of transportOffering codes
	 * @param travellerUid
	 *           as the Traveller unique id
	 * @return list of AbstractOrderEntryModel
	 */
	List<AbstractOrderEntryModel> getOrderEntriesForCategory(CartModel cartModel, CategoryModel categoryModel,
			String travelRouteCode, List<String> transportOfferingCodes, String travellerUid);

	/**
	 * This method adds a configured accommodation as a selected accommodation to the cart
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param configuredAccommodation
	 *           the configured accommodation
	 */
	void addSelectedAccommodationToCart(String transportOfferingCode, String travellerCode,
			ConfiguredAccommodationModel configuredAccommodation);

	/**
	 * This method removes a selected accommodation from the cart
	 *
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @param travellerCode
	 *           the traveller code
	 * @param configuredAccommodationUid
	 *           the configured accommodation Uid
	 */
	void removeSelectedAccommodationFromCart(String transportOfferingCode, String travellerCode,
			String configuredAccommodationUid);

	/**
	 * Method to add in the cart the PER_LEG products included in a bundle
	 *
	 * @param bundleTemplateId
	 *           the bundle template id
	 * @param bundleNo
	 *           the bundle no
	 * @param cartModifications
	 *           the cart modifications
	 * @return a list of CommerceCartModification
	 * @throws CommerceBundleCartModificationException
	 *            the commerce bundle cart modification exception
	 * @deprecated since 4.0 use {@link #addPerLegBundleProductToCart(String)} instead
	 */
	@Deprecated
	List<CommerceCartModification> addPerLegBundleProductToCart(String bundleTemplateId, int bundleNo,
			List<CartModificationData> cartModifications) throws CommerceBundleCartModificationException;

	/**
	 * Method to add AutoPick products from a bundle to the cart
	 *
	 * @param masterCartModel
	 * 		master cart model
	 * @param bundleNo
	 * 		bundle number
	 * @param bundleTemplate
	 * 		bundle template
	 * @param unit
	 * 		unit
	 * @return commerce cart modifications
	 * @throws CommerceCartModificationException
	 * 		commerce cart modification exception in case of error
	 */
	List<CommerceCartModification> addAutoPickProductsFromDefaultBundle(CartModel masterCartModel, int bundleNo,
			BundleTemplateModel bundleTemplate, UnitModel unit) throws CommerceCartModificationException;

	/**
	 * Cleans up the cart from bundles before a new bundle addition. It removes all the entries of type TRANSPORT with
	 * originDestinationRefNumber greater than the given odRefNum and the related entry groups.
	 *
	 * @param odRefNum
	 * 		as the minimum originDestinationRefNumber
	 */
	void removeCartEntriesForMinODRefNumber(Integer odRefNum);

	/**
	 * Removes all cart entries matching specified origin destination ref number and the related entry groups.
	 *
	 * @param odRefNum
	 * 		origin destination ref number
	 * @param cartModel
	 * 		cart model
	 */
	void removeCartEntriesForODRefNumber(Integer odRefNum, CartModel cartModel);

	/**
	 * Set cart entry as calculated and initialize price level to null.
	 *
	 * @param entryNumberToUpdate
	 * @throws CommerceCartModificationException
	 */
	void setEntryAsCalculatedAndInitializePriceLevel(Integer entryNumberToUpdate) throws CommerceCartModificationException;

	/**
	 * Add auto picks products to cart using hybris 6.4 bundle APIs
	 *
	 * @param productModel
	 * @param bundleTemplateId
	 * @param bundleEntryGroupNo
	 * @return
	 * @throws CommerceCartModificationException
	 */
	List<CommerceCartModification> addAutoPickProductsToCart(ProductModel productModel, String bundleTemplateId,
			int bundleEntryGroupNo) throws CommerceCartModificationException;

	/**
	 * Add per leg products to cart using Hybris 6.4 bundle APIs
	 *
	 * @param bundleTemplateId
	 * @return
	 * @throws CommerceCartModificationException
	 */
	List<CommerceCartModification> addPerLegBundleProductToCart(String bundleTemplateId) throws CommerceCartModificationException;
}
