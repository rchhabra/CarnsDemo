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
package de.hybris.platform.travelservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.impl.DefaultBundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AccommodationStatus;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.travel.AutoPickPerLegBundleSelectionCriteriaModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.CommerceBundleCartModificationException;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The class overrides the {@link BundleCommerceCartService} addToCart to handle more than one bundle entry.Cart entry is created
 * for each transport offering per passenger with a new bundle no.
 */
public class DefaultTravelCommerceCartService extends DefaultBundleCommerceCartService implements TravelCommerceCartService
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelCommerceCartService.class);

	private static final long BUNDLE_PRODUCT_QUANTITY = 1;

	private TravellerService travellerService;
	private TransportOfferingService transportOfferingService;
	private CartService cartService;
	private SessionService sessionService;
	private Map<String, String> offerGroupToOriginDestinationMapping;
	private TravelCommercePriceService travelCommercePriceService;
	private TravelRouteService travelRouteService;


	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CommerceCartModification> addToCart(final CartModel masterCartModel, final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, final int bundleNo,
			final BundleTemplateModel bundleTemplateModel, final boolean removeCurrentProducts, final String xmlProduct)
			throws CommerceCartModificationException
	{
		if (removeCurrentProducts)
		{
			final List<CartEntryModel> bundleEntries = getBundleCartEntryDao()
					.findEntriesByMasterCartAndBundleNoAndTemplate(masterCartModel, bundleNo, bundleTemplateModel);
			checkAndRemoveDependentComponents(masterCartModel, bundleNo, bundleTemplateModel);
			removeCartEntriesWithChildren(masterCartModel, bundleEntries);
		}

		List<CommerceCartModification> autoPicks = null;

		if (bundleNo != NO_BUNDLE)
		{
			checkAutoPickAddToCart(bundleTemplateModel, productModel);
		}

		final CommerceCartModification modification = addTravelProductToCart(masterCartModel, productModel, quantityToAdd, unit,
				forceNewEntry, bundleNo, bundleTemplateModel, removeCurrentProducts, xmlProduct);

		final Map<String, Object> addBundleToCartParamsMap = getSessionService()
				.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);

		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			if (Objects.nonNull(addBundleToCartParamsMap))
			{
				updateCartEntryWithTravelDetails(modification.getEntry(), null);
			}
			// Change done to accept auto pick bundle
			if (bundleNo <= NEW_BUNDLE)
			{
				final int newBundleNo = modification.getEntry().getBundleNo().intValue();
				autoPicks = addAutoPickProductsToCart(masterCartModel, newBundleNo, bundleTemplateModel, unit);
			}

			calculateCart(masterCartModel);
		}

		final List<CommerceCartModification> modificationList = new ArrayList<>();
		modificationList.add(modification);
		if (CollectionUtils.isNotEmpty(autoPicks))
		{
			modificationList.addAll(autoPicks);
		}

		updateLastModifiedEntriesList(masterCartModel, modificationList);

		return modificationList;
	}

	protected CommerceCartModification addTravelProductToCart(final CartModel masterCartModel, final ProductModel productModel,
			final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry, final int bundleNo,
			final BundleTemplateModel bundleTemplateModel, final boolean removeCurrentProducts, final String xmlProduct)
			throws CommerceCartModificationException
	{
		return addProductToCart(masterCartModel, productModel, quantityToAdd, unit, forceNewEntry, bundleNo, bundleTemplateModel,
				xmlProduct, removeCurrentProducts);
	}

	@Override
	protected CommerceCartModification addProductToCart(@Nonnull final CartModel masterCartModel,
			@Nonnull final ProductModel productModel, final long quantityToAdd, final UnitModel unit, final boolean forceNewEntry,
			final int bundleNo, @Nullable final BundleTemplateModel bundleTemplateModel, @Nullable final String xmlProduct,
			final boolean ignoreEmptyBundle) throws CommerceCartModificationException
	{
		checkBundleParameters(bundleNo, bundleTemplateModel);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(masterCartModel);
		parameter.setProduct(productModel);
		parameter.setBundleTemplate(bundleTemplateModel);
		parameter.setQuantity(quantityToAdd);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setXmlProduct(xmlProduct);

		final CommerceCartModification commerceCartModification = addToCartWithoutCalculation(parameter);

		addToBundle(commerceCartModification.getEntry(), bundleTemplateModel, bundleNo);

		return commerceCartModification;
	}

	/**
	 * Assign the new cart entry to an existing bundle or create a new bundle for it.
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected void addToBundle(final AbstractOrderEntryModel entry, final BundleTemplateModel bundleTemplate,
			final int bundleNoRequested) throws CommerceCartModificationException
	{
		entry.setBundleTemplate(bundleTemplate);
		// Change done to accept more than one bundle
		if (bundleNoRequested <= NEW_BUNDLE)
		{
			int ret = 0;
			final Collection<AbstractOrderEntryModel> all = entry.getOrder().getEntries();

			// find highest existing bundle and add one
			for (final AbstractOrderEntryModel abstractOrderEntryModel : all)
			{
				final int currentBundleNo = abstractOrderEntryModel.getBundleNo() == null ? 0
						: abstractOrderEntryModel.getBundleNo().intValue();
				if (currentBundleNo > ret)
				{
					ret = currentBundleNo;
				}
			}
			entry.setBundleNo(Integer.valueOf(ret + 1));
		}
		else
		{
			entry.setBundleNo(Integer.valueOf(bundleNoRequested));
		}

		getModelService().save(entry);
	}

	/**
	 * Checks if the combination of the given <code>bundleNo</code> and the given <code>bundleTemplateModel</code> is correct.
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected void checkBundleParameters(final int bundleNo, final BundleTemplateModel bundleTemplateModel)
			throws CommerceCartModificationException
	{
		// Removed a condition check which restricts adding more than one bundle in the cart.

		if ((bundleNo != NO_BUNDLE && bundleTemplateModel == null) || (bundleNo == NO_BUNDLE && bundleTemplateModel != null))
		{
			throw new IllegalArgumentException(
					"Either bundleNo and bundleTemplate must be provided or none of both. Given bundleNo: " + bundleNo
							+ ", given bundleTemplate: " + (bundleTemplateModel == null ? "null" : bundleTemplateModel.getName()));
		}
	}

	protected void removeCartEntriesWithChildren(final CartModel masterCartModel, final List<CartEntryModel> bundleEntries)
	{
		for (final CartEntryModel cartEntryModel : bundleEntries)
		{
			if (CollectionUtils.isNotEmpty(cartEntryModel.getChildEntries()))
			{
				for (final AbstractOrderEntryModel childEntry : cartEntryModel.getChildEntries())

				{
					removeCartEntry(masterCartModel, (CartEntryModel) childEntry);
				}
			}
			removeCartEntry(masterCartModel, cartEntryModel);
		}
	}

	/**
	 * Method to update the cart entry with travel specific values and specific transportOfferings.
	 *
	 * @param orderEntryModel
	 * @param priceLevel
	 * @param transportOfferingModels
	 * @throws CommerceCartModificationException
	 */
	protected void updateCartEntryWithTravelDetails(final AbstractOrderEntryModel orderEntryModel, final PriceLevel priceLevel,
			final List<TransportOfferingModel> transportOfferingModels) throws CommerceCartModificationException
	{
		updateCartEntryWithTravelDetails(orderEntryModel, priceLevel, transportOfferingModels, true);
	}

	/**
	 * Method to update the cart entry with travel specific values
	 *
	 * @param orderEntryModel
	 * @param priceLevel
	 * @throws CommerceCartModificationException
	 */
	@SuppressWarnings("unchecked")
	protected void updateCartEntryWithTravelDetails(final AbstractOrderEntryModel orderEntryModel, final PriceLevel priceLevel)
			throws CommerceCartModificationException
	{
		updateCartEntryWithTravelDetails(orderEntryModel, priceLevel, null, true);
	}

	@SuppressWarnings("unchecked")
	protected void updateCartEntryWithTravelDetails(final AbstractOrderEntryModel orderEntryModel, final PriceLevel priceLevel,
			final List<TransportOfferingModel> transportOfferings, final boolean saveTravellerDetails)
			throws CommerceCartModificationException
	{
		final Map<String, Object> addBundleToCartParamsMap = getSessionService()
				.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);

		List<TransportOfferingModel> transportOfferingModels = transportOfferings;
		if (transportOfferingModels == null
				&& addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS) != null)
		{
			transportOfferingModels = (List<TransportOfferingModel>) addBundleToCartParamsMap
					.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS);
		}
		final TravelRouteModel travelRouteModel = (TravelRouteModel) addBundleToCartParamsMap
				.get(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE);

		final Boolean active = (Boolean) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_ACTIVE);
		final AmendStatus amendStatus = (AmendStatus) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_AMEND_STATUS);

		final Map<String, Object> params = new HashMap<>();

		// priceLevel will be null for fare products and not null for auto pick products.
		// for fare products the priceLevel is set in addBundleToCartParamsMap
		if (priceLevel == null)
		{
			// Price level will be set for fare product in add to cart flow
			final String priceLevelCode = (String) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_PRICELEVEL);
			params.put(TravelOrderEntryInfoModel.PRICELEVEL, priceLevelCode);
		}
		else
		{
			populatePriceLevelForEntry(priceLevel, params);
		}
		final int originDestinationRefNumber = (Integer) addBundleToCartParamsMap
				.get(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER);
		params.put(TravelOrderEntryInfoModel.ORIGINDESTINATIONREFNUMBER, originDestinationRefNumber);

		populateTransportOfferingForEntry(transportOfferingModels, params);
		populateTravelRouteForEntry(travelRouteModel, params);
		populateActiveFlagForEntry(active, params);
		populateAmendStatusForEntry(amendStatus, params);

		if (saveTravellerDetails)
		{
			final TravellerModel travellerModel = (TravellerModel) addBundleToCartParamsMap
					.get(TravelservicesConstants.CART_ENTRY_TRAVELLER);
			populateTravellerForEntry(orderEntryModel, travellerModel, params);
		}

		persistProperties(params, orderEntryModel);
	}

	/**
	 * Add auto-pick products to the bundle in the cart. The root bundle template's components (= child templates) are investigated
	 * if their selection criteria type is auto-pick. If so, the component's products are automatically added to the bundle. The
	 * cart is not calculated here. Assumption is that there are no dependencies of the auto-pick components to each other or to
	 * other components in the bundle.
	 *
	 * @deprecated since 4.0 Use {@link #addAutoPickProductsToCart(ProductModel, String, int)} instead
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Deprecated
	protected List<CommerceCartModification> addAutoPickProductsToCart(final CartModel masterCartModel, final int bundleNo,
			final BundleTemplateModel bundleTemplate, final UnitModel unit) throws CommerceCartModificationException
	{
		List<TransportOfferingModel> transportOfferingModels = new ArrayList<>();
		TravelRouteModel travelRouteModel = null;
		TravellerModel travellerModel = null;
		if (Objects.nonNull(getSessionService().getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP)))
		{
			final Map<String, Object> addBundleToCartParamsMap = getSessionService()
					.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);
			if (addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS) != null)
			{
				transportOfferingModels = (List<TransportOfferingModel>) addBundleToCartParamsMap
						.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS);
			}

			travelRouteModel = (TravelRouteModel) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE);
			travellerModel = (TravellerModel) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRAVELLER);
		}

		final List<CommerceCartModification> modificationList = new ArrayList<>();
		final BundleTemplateModel rootTemplate = bundleTemplate.getParentTemplate();
		for (final BundleTemplateModel childTemplate : rootTemplate.getChildTemplates())
		{
			if (!getBundleTemplateService().isAutoPickComponent(childTemplate)
					|| !childTemplate.getBundleSelectionCriteria().getClass().equals(AutoPickBundleSelectionCriteriaModel.class))
			{
				continue;
			}
			final List<ProductModel> autoPickProducts = childTemplate.getProducts();
			for (final ProductModel autoPickProduct : autoPickProducts)
			{
				if (!autoPickProduct.getSupercategories().stream().findFirst().isPresent())
				{
					continue;
				}
				final Optional<CategoryModel> categoryModel = CollectionUtils.isNotEmpty(autoPickProduct.getSupercategories())
						? autoPickProduct.getSupercategories().stream().findFirst() : null;
				final String offerGroupCode = Objects.nonNull(categoryModel) && categoryModel.isPresent()
						? categoryModel.get().getCode() : StringUtils.EMPTY;
				final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(offerGroupCode,
						getOfferGroupToOriginDestinationMapping().getOrDefault(
								TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING, TravelservicesConstants.TRAVEL_ROUTE));
				if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRANSPORT_OFFERING))
				{
					addAutopickProductsForTransportOfferings(masterCartModel, bundleNo, unit, transportOfferingModels,
							travelRouteModel, travellerModel, modificationList, childTemplate, autoPickProduct);
				}
				else if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRAVEL_ROUTE))
				{
					addAutopickProductsForRoute(masterCartModel, bundleNo, unit, transportOfferingModels, travelRouteModel,
							travellerModel, modificationList, childTemplate, autoPickProduct);
				}
			}
		}

		return modificationList;
	}

	protected void addAutopickProductsForRoute(final int bundleEntryGroupNo,
			final List<TransportOfferingModel> transportOfferingModels, final TravelRouteModel travelRouteModel,
			final TravellerModel travellerModel, final List<CommerceCartModification> modificationList,
			final BundleTemplateModel childTemplate, final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PassengerInformationModel travellerInfo = (PassengerInformationModel) travellerModel.getInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();

		if (!isTravellerEligibleForProduct(autoPickProduct, passengerType))
		{
			return;
		}

		final PriceLevel priceLevel = new PriceLevel();
		if (getTravelCommercePriceService()
				.isPriceInformationAvailable(autoPickProduct, PriceRowModel.TRAVELROUTECODE, travelRouteModel.getCode()))
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
			priceLevel.setValue(travelRouteModel.getCode());
		}
		else
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
		}

		final List<String> transportOfferingCodes = transportOfferingModels.stream().map(TransportOfferingModel::getCode)
				.collect(Collectors.toList());
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, passengerType);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(getCartService().getSessionCart());
		parameter.setEntryGroupNumbers(Collections.singleton(bundleEntryGroupNo));
		parameter.setBundleTemplate(childTemplate);
		parameter.setProduct(autoPickProduct);
		parameter.setQuantity(BUNDLE_PRODUCT_QUANTITY);

		final CommerceCartModification modification = addToCart(parameter);

		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel);
		}
		modificationList.add(modification);
	}

	protected void addAutopickProductsForTransportOfferings(final int bundleEntryGroupNo,
			final List<TransportOfferingModel> transportOfferingModels, final TravelRouteModel travelRouteModel,
			final TravellerModel travellerModel, final List<CommerceCartModification> modificationList,
			final BundleTemplateModel childTemplate, final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PassengerInformationModel travellerInfo = (PassengerInformationModel) travellerModel.getInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();

		if (!isTravellerEligibleForProduct(autoPickProduct, passengerType))
		{
			return;
		}
		for (final TransportOfferingModel transportOffering : transportOfferingModels)
		{
			final PriceLevel priceLevel = getTravelCommercePriceService()
					.getPriceLevelInfoForAncillary(autoPickProduct, transportOffering.getCode(), travelRouteModel.getCode());

			final List<String> transportOfferingCodes = new ArrayList<>();
			transportOfferingCodes.add(transportOffering.getCode());

			getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, passengerType);

			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(getCartService().getSessionCart());
			parameter.setEntryGroupNumbers(Collections.singleton(bundleEntryGroupNo));
			parameter.setBundleTemplate(childTemplate);
			parameter.setProduct(autoPickProduct);
			parameter.setQuantity(BUNDLE_PRODUCT_QUANTITY);

			final CommerceCartModification modification = addToCart(parameter);
			if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
			{
				updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel, Arrays.asList(transportOffering));
			}
			modificationList.add(modification);
		}
	}

	/**
	 * @param masterCartModel
	 * @param bundleNo
	 * @param unit
	 * @param transportOfferingModels
	 * @param travelRouteModel
	 * @param travellerModel
	 * @param modificationList
	 * @param childTemplate
	 * @param autoPickProduct
	 * @throws CommerceCartModificationException
	 * @deprecated since 4.0 use {@link #addAutopickProductsForRoute(int, List, TravelRouteModel, TravellerModel, List,
	 * BundleTemplateModel, ProductModel)} instead
	 */
	@Deprecated
	protected void addAutopickProductsForRoute(final CartModel masterCartModel, final int bundleNo, final UnitModel unit,
			final List<TransportOfferingModel> transportOfferingModels, final TravelRouteModel travelRouteModel,
			final TravellerModel travellerModel, final List<CommerceCartModification> modificationList,
			final BundleTemplateModel childTemplate, final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PassengerInformationModel travellerInfo = (PassengerInformationModel) travellerModel.getInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();

		if (!isTravellerEligibleForProduct(autoPickProduct, passengerType))
		{
			return;
		}

		final PriceLevel priceLevel = new PriceLevel();
		if (getTravelCommercePriceService().isPriceInformationAvailable(autoPickProduct, PriceRowModel.TRAVELROUTECODE,
				travelRouteModel.getCode()))
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
			priceLevel.setValue(travelRouteModel.getCode());
		}
		else
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
		}

		final List<String> transportOfferingCodes = transportOfferingModels.stream().map(TransportOfferingModel::getCode)
				.collect(Collectors.toList());
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, passengerType);

		final CommerceCartModification modification = addProductToCart(masterCartModel, autoPickProduct, BUNDLE_PRODUCT_QUANTITY,
				unit, true, bundleNo, childTemplate, null, false);
		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel);
		}
		modificationList.add(modification);
	}

	/**
	 * @param masterCartModel
	 * @param bundleNo
	 * @param unit
	 * @param transportOfferingModels
	 * @param travelRouteModel
	 * @param travellerModel
	 * @param modificationList
	 * @param childTemplate
	 * @param autoPickProduct
	 * @throws CommerceCartModificationException
	 * @deprecated since 4.0 use {@link #addAutopickProductsForTransportOfferings(int, List, TravelRouteModel, TravellerModel,
	 * List, BundleTemplateModel, ProductModel)} instead
	 */
	@Deprecated
	protected void addAutopickProductsForTransportOfferings(final CartModel masterCartModel, final int bundleNo,
			final UnitModel unit, final List<TransportOfferingModel> transportOfferingModels,
			final TravelRouteModel travelRouteModel, final TravellerModel travellerModel,
			final List<CommerceCartModification> modificationList, final BundleTemplateModel childTemplate,
			final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PassengerInformationModel travellerInfo = (PassengerInformationModel) travellerModel.getInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();

		if (!isTravellerEligibleForProduct(autoPickProduct, passengerType))
		{
			return;
		}

		for (final TransportOfferingModel transportOffering : transportOfferingModels)
		{
			final PriceLevel priceLevel = getTravelCommercePriceService().getPriceLevelInfoForAncillary(autoPickProduct,
					transportOffering.getCode(), travelRouteModel.getCode());

			final List<String> transportOfferingCodes = new ArrayList<>();
			transportOfferingCodes.add(transportOffering.getCode());

			getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, passengerType);

			final CommerceCartModification modification = addProductToCart(masterCartModel, autoPickProduct, BUNDLE_PRODUCT_QUANTITY,
					unit, true, bundleNo, childTemplate, null, false);
			if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
			{
				updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel, Arrays.asList(transportOffering));
			}
			modificationList.add(modification);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommerceCartModification> addPerLegBundleProductToCart(final String bundleTemplateId, final int bundleNo,
			final List<CartModificationData> cartModifications) throws CommerceBundleCartModificationException
	{
		final CartModel cartModel = getCartService().getSessionCart();

		final Map<String, Object> addBundleToCartParamsMap = getSessionService()
				.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);

		List<TransportOfferingModel> transportOfferingModels = new ArrayList<>();
		if (addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS) != null)
		{
			transportOfferingModels = (List<TransportOfferingModel>) addBundleToCartParamsMap
					.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS);
		}

		final TravelRouteModel travelRouteModel = (TravelRouteModel) addBundleToCartParamsMap
				.get(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE);

		final List<CommerceCartModification> modificationList = new ArrayList<>();

		final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
		final BundleTemplateModel rootBundleTemplate = getBundleTemplateService().getRootBundleTemplate(bundleTemplate);
		for (final BundleTemplateModel childTemplate : rootBundleTemplate.getChildTemplates())
		{
			if (!(childTemplate.getBundleSelectionCriteria() instanceof AutoPickPerLegBundleSelectionCriteriaModel))
			{
				continue;
			}
			final List<ProductModel> autoPickProducts = childTemplate.getProducts();
			for (final ProductModel autoPickProduct : autoPickProducts)
			{
				final Optional<CategoryModel> categoryModel = CollectionUtils.isNotEmpty(autoPickProduct.getSupercategories())
						? autoPickProduct.getSupercategories().stream().findFirst() : null;

				final CategoryModel offerGroup = Objects.nonNull(categoryModel) && categoryModel.isPresent() ? categoryModel.get()
						: null;
				if (Objects.isNull(offerGroup) || Objects.isNull(offerGroup.getTravelRestriction())
						|| Objects.isNull(offerGroup.getTravelRestriction().getAddToCartCriteria())
						|| !AddToCartCriteriaType.PER_LEG.equals(offerGroup.getTravelRestriction().getAddToCartCriteria()))
				{
					continue;
				}

				try
				{
					addAutopickPerLegProducts(cartModel, bundleNo, transportOfferingModels, travelRouteModel, modificationList,
							childTemplate, autoPickProduct);
				}
				catch (final CommerceCartModificationException ex)
				{
					LOG.warn("Couldn't add product of code " + autoPickProduct.getCode() + " to cart.", ex);
					throw new CommerceBundleCartModificationException(ex.getMessage(), ex);
				}
			}

		}
		return modificationList;
	}

	protected void addAutopickPerLegProducts(final CartModel cartModel, final int bundleNo,
			final List<TransportOfferingModel> transportOfferingModels, final TravelRouteModel travelRouteModel,
			final List<CommerceCartModification> modificationList, final BundleTemplateModel childTemplate,
			final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PriceLevel priceLevel = new PriceLevel();
		if (getTravelCommercePriceService().isPriceInformationAvailable(autoPickProduct, PriceRowModel.TRAVELROUTECODE,
				travelRouteModel.getCode()))
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
			priceLevel.setValue(travelRouteModel.getCode());
		}
		else
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
		}
		final List<String> transportOfferingCodes = transportOfferingModels.stream().map(TransportOfferingModel::getCode)
				.collect(Collectors.toList());
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, null);


		final CommerceCartModification modification = addProductToCart(cartModel, autoPickProduct, BUNDLE_PRODUCT_QUANTITY,
				autoPickProduct.getUnit(), true, bundleNo, childTemplate, null, false);

		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel, transportOfferingModels, false);
		}
		modificationList.add(modification);
	}

	protected void populateTravelRouteForEntry(final TravelRouteModel travelRouteModel, final Map<String, Object> params)
	{
		params.put(TravelOrderEntryInfoModel.TRAVELROUTE, travelRouteModel);
	}

	protected void populateTravellerForEntry(final AbstractOrderEntryModel orderEntryModel, final TravellerModel travellerModel,
			final Map<String, Object> params)
	{
		if (travellerModel != null)
		{
			final List<TravellerModel> travellersList = new ArrayList<>();
			if (orderEntryModel.getTravelOrderEntryInfo() != null)
			{
				final Collection<TravellerModel> existingTravellers = orderEntryModel.getTravelOrderEntryInfo().getTravellers();
				if (CollectionUtils.isNotEmpty(existingTravellers))
				{
					travellersList.addAll(existingTravellers);
				}
			}

			if (!travellersList.contains(travellerModel))
			{
				travellersList.add(travellerModel);
				params.put(TravelOrderEntryInfoModel.TRAVELLERS, travellersList);
			}

		}
	}

	protected void populatePriceLevelForEntry(final PriceLevel priceLevel, final Map<String, Object> params)
	{
		String priceLevelCode = StringUtils.EMPTY;
		if (priceLevel != null)
		{
			priceLevelCode = priceLevel.getCode();
		}
		params.put(TravelOrderEntryInfoModel.PRICELEVEL, priceLevelCode);
	}

	protected void populateTransportOfferingForEntry(final List<TransportOfferingModel> transportOfferings,
			final Map<String, Object> params)
	{
		if (CollectionUtils.isNotEmpty(transportOfferings))
		{
			params.put(TravelOrderEntryInfoModel.TRANSPORTOFFERINGS, transportOfferings);
		}
	}

	protected void populateActiveFlagForEntry(final Boolean active, final Map<String, Object> params)
	{
		params.put(AbstractOrderEntryModel.ACTIVE, active);
	}

	protected void populateAmendStatusForEntry(final AmendStatus amendStatus, final Map<String, Object> params)
	{
		params.put(AbstractOrderEntryModel.AMENDSTATUS, amendStatus);
	}

	protected boolean isTravellerEligibleForProduct(final ProductModel product, final String passengerType)
	{
		final TravelRestrictionModel travelRestriction = product.getTravelRestriction();
		if (travelRestriction != null && CollectionUtils.isNotEmpty(travelRestriction.getPassengerTypes()))
		{
			return travelRestriction.getPassengerTypes().stream()
					.filter(restrictedType -> StringUtils.equalsIgnoreCase(restrictedType, passengerType)).findAny().isPresent();
		}
		return true;
	}

	/**
	 * Checks whether the given <code>product</code> is already added to the given <code>bundleNo</code> and
	 * <code>bundleTemplate</code>. As it is not allowed to add the same product more than once to the same component an {@link
	 * CommerceCartModificationException} is thrown in case the product is already in the component.
	 */
	@Override
	protected void checkIsProductAlreadyInComponent(final CartModel masterCart, final int bundleNo,
			final BundleTemplateModel bundleTemplate, final ProductModel product) throws CommerceCartModificationException
	{
		if (bundleNo != NO_BUNDLE)
		{

			final List<CartEntryModel> cartEntries = getBundleCartEntryDao()
					.findEntriesByMasterCartAndBundleNoAndTemplate(masterCart, bundleNo, bundleTemplate);

			for (final CartEntryModel cartEntry : cartEntries)
			{
				if (cartEntry.getProduct().equals(product))
				{
					LOG.info("Product '" + product.getCode()
							+ "' is added to the cart for a different TransportOffering Code as the Product Category is defined at TransportOffering Level '"
							+ "' with bundleNo " + bundleNo);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPropertiesToCartEntry(final CartModel masterCartModel, final int orderEntryNo, final ProductModel product,
			final Map<String, Object> propertiesMap)
	{
		validateParameterNotNull(product, "Parameter productModel must not be null");
		validateParameterNotNull(orderEntryNo, "Parameter uniqueIdentifier must not be null");
		validateParameterNotNull(masterCartModel, "Parameter masterCartModel must not be null");
		validateParameterNotNull(propertiesMap, "Parameter propertiesMap must not be null");

		final Optional<AbstractOrderEntryModel> orderEntryModel = CollectionUtils.isNotEmpty(masterCartModel.getEntries())
				? masterCartModel.getEntries().stream().filter(entry -> entry.getEntryNumber().equals(Integer.valueOf(orderEntryNo)))
				.findFirst()
				: null;
		if (Objects.nonNull(orderEntryModel) && orderEntryModel.isPresent())
		{
			persistProperties(propertiesMap, orderEntryModel.get());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPropertiesToCartEntryForBundle(final CartModel masterCartModel, final int bundleNo, final ProductModel product,
			final Map<String, Object> propertiesMap)
	{
		validateParameterNotNull(product, "Parameter productModel must not be null");
		validateParameterNotNull(bundleNo, "Parameter uniqueIdentifier must not be null");

		final List<AbstractOrderEntryModel> orderEntries = getBundleCartEntryDao()
				.findEntriesByMasterCartAndBundleNoAndProduct(masterCartModel, bundleNo, product);
		if (CollectionUtils.isEmpty(orderEntries))
		{
			return;
		}

		for (final AbstractOrderEntryModel orderEntry : orderEntries)
		{
			persistProperties(propertiesMap, orderEntry);
		}

	}

	@Override
	public List<AbstractOrderEntryModel> getOrderEntriesForCategory(final CartModel cartModel, final CategoryModel categoryModel,
			final String travelRouteCode, final List<String> transportOfferingCodes, final String travellerUid)
	{
		final List<AbstractOrderEntryModel> orderEntries = new ArrayList<>();

		for (final AbstractOrderEntryModel orderEntry : cartModel.getEntries())
		{
			if (checkBundleAndCategory(orderEntry, categoryModel) && checkTraveller(orderEntry, travellerUid)
					&& ((travelRouteCode == null && orderEntry.getTravelOrderEntryInfo().getTravelRoute() == null)
					|| (travelRouteCode != null && orderEntry.getTravelOrderEntryInfo().getTravelRoute() != null
					&& travelRouteCode.equalsIgnoreCase(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getCode())))
					&& checkTransportOfferings(orderEntry, transportOfferingCodes))
			{
				orderEntries.add(orderEntry);
			}
		}
		return orderEntries;
	}

	/**
	 * Verifies if order entry bundle and product's category match
	 *
	 * @param orderEntry
	 * @param categoryModel
	 * @return true if bundle and product category match
	 */
	protected boolean checkBundleAndCategory(final AbstractOrderEntryModel orderEntry, final CategoryModel categoryModel)
	{
		return orderEntry.getBundleNo() == 0 && orderEntry.getProduct().getSupercategories().contains(categoryModel);
	}

	/**
	 * Verifies if order entry has a matching traveller
	 *
	 * @param orderEntry
	 * @param travellerUid
	 * @return true if traveller matches
	 */
	protected boolean checkTraveller(final AbstractOrderEntryModel orderEntry, final String travellerUid)
	{
		if (Objects.isNull(orderEntry.getTravelOrderEntryInfo())
				|| CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTravellers()))
		{
			return false;
		}

		final Optional<TravellerModel> travellerModel = orderEntry.getTravelOrderEntryInfo().getTravellers().stream().findFirst();

		return travellerModel.isPresent() ? travellerUid.equalsIgnoreCase(travellerModel.get().getUid()) : Boolean.FALSE;
	}

	/**
	 * Verifies if order entry has a matching list of transport offerings
	 *
	 * @param orderEntry
	 * @param transportOfferingCodes
	 * @return
	 */
	protected boolean checkTransportOfferings(final AbstractOrderEntryModel orderEntry, final List<String> transportOfferingCodes)
	{
		if (Objects.isNull(orderEntry.getTravelOrderEntryInfo())
				|| CollectionUtils.isEmpty(orderEntry.getTravelOrderEntryInfo().getTransportOfferings()))
		{
			return false;
		}
		final List<String> transportOfferingCodeList = orderEntry.getTravelOrderEntryInfo().getTransportOfferings().stream()
				.map(TransportOfferingModel::getCode).collect(Collectors.toList());
		return transportOfferingCodeList.containsAll(transportOfferingCodes);
	}

	protected void persistProperties(final Map<String, Object> propertiesMap, final AbstractOrderEntryModel orderEntry)
	{
		if (MapUtils.isEmpty(propertiesMap) || Objects.isNull(orderEntry))
		{
			return;
		}
		if (propertiesMap.containsKey(AbstractOrderEntryModel.ACTIVE))
		{
			getModelService().setAttributeValue(orderEntry, AbstractOrderEntryModel.ACTIVE,
					propertiesMap.get(AbstractOrderEntryModel.ACTIVE));
			propertiesMap.remove(AbstractOrderEntryModel.ACTIVE);
		}

		if (propertiesMap.containsKey(AbstractOrderEntryModel.AMENDSTATUS))
		{
			getModelService().setAttributeValue(orderEntry, AbstractOrderEntryModel.AMENDSTATUS,
					propertiesMap.get(AbstractOrderEntryModel.AMENDSTATUS));
			propertiesMap.remove(AbstractOrderEntryModel.AMENDSTATUS);
		}

		final TravelOrderEntryInfoModel orderEntryInfo = (orderEntry.getTravelOrderEntryInfo() == null)
				? getModelService().create(TravelOrderEntryInfoModel.class) : orderEntry.getTravelOrderEntryInfo();
		propertiesMap.entrySet().stream()
				.forEach(entry -> getModelService().setAttributeValue(orderEntryInfo, entry.getKey(), entry.getValue()));

		getModelService().save(orderEntryInfo);

		orderEntry.setTravelOrderEntryInfo(orderEntryInfo);
		getModelService().save(orderEntry);

	}

	@Override
	public void addSelectedAccommodationToCart(final String transportOfferingCode, final String travellerCode,
			final ConfiguredAccommodationModel configuredAccommodation)
	{
		final CartModel sessionCart = getCartService().getSessionCart();
		boolean selectedAccommodationExist = false;
		final List<SelectedAccommodationModel> selectedAccommodations = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(sessionCart.getSelectedAccommodations()))
		{
			for (final SelectedAccommodationModel selectedAccommodationModel : sessionCart.getSelectedAccommodations())
			{
				if (selectedAccommodationModel.getTransportOffering().getCode().equals(transportOfferingCode)
						&& String.valueOf(selectedAccommodationModel.getTraveller().getLabel()).equals(travellerCode))
				{
					selectedAccommodationModel.setConfiguredAccommodation(configuredAccommodation);
					getModelService().save(selectedAccommodationModel);
					selectedAccommodationExist = true;
					break;
				}
			}
			if (!selectedAccommodationExist)
			{
				selectedAccommodations.addAll(sessionCart.getSelectedAccommodations());
			}
		}
		if (!selectedAccommodationExist)
		{
			final SelectedAccommodationModel selectedAccommodation = createSelectedAccommodationModel(transportOfferingCode,
					travellerCode, sessionCart, configuredAccommodation);
			selectedAccommodations.add(selectedAccommodation);
			sessionCart.setSelectedAccommodations(selectedAccommodations);
			getModelService().save(sessionCart);
		}
	}

	protected SelectedAccommodationModel createSelectedAccommodationModel(final String transportOfferingCode,
			final String travellerCode, final CartModel sessionCart, final ConfiguredAccommodationModel configuredAccommodation)
	{
		final TransportOfferingModel transportOffering = getTransportOfferingService().getTransportOffering(transportOfferingCode);
		final TravellerModel traveller = getTravellerService().getTravellerFromCurrentCart(travellerCode);
		final SelectedAccommodationModel selectedAccommodation = getModelService().create(SelectedAccommodationModel.class);
		selectedAccommodation.setConfiguredAccommodation(configuredAccommodation);
		selectedAccommodation.setTransportOffering(transportOffering);
		selectedAccommodation.setTraveller(traveller);
		selectedAccommodation.setStatus(AccommodationStatus.OCCUPIED);
		selectedAccommodation.setOrder(sessionCart);
		getModelService().save(selectedAccommodation);
		return selectedAccommodation;
	}

	@Override
	public void removeSelectedAccommodationFromCart(final String transportOfferingCode, final String travellerCode,
			final String configuredAccommodationUid)
	{
		final CartModel sessionCart = getCartService().getSessionCart();
		final List<SelectedAccommodationModel> selectedAccommodations = sessionCart.getSelectedAccommodations();
		if (CollectionUtils.isEmpty(selectedAccommodations))
		{
			return;
		}
		final List<SelectedAccommodationModel> retainedSelectedAccommodations = new ArrayList<>();
		SelectedAccommodationModel selectedAccomModelToRemove = null;
		for (final SelectedAccommodationModel selectedAccommodationModel : selectedAccommodations)
		{
			final String selectedTransportOfferingCode = selectedAccommodationModel.getTransportOffering().getCode();
			final String selectedConfAccomUid = selectedAccommodationModel.getConfiguredAccommodation().getUid();
			if (!selectedTransportOfferingCode.equals(transportOfferingCode)
					|| !travellerCode.equals(String.valueOf(selectedAccommodationModel.getTraveller().getLabel()))
					|| !configuredAccommodationUid.equals(selectedConfAccomUid))
			{
				retainedSelectedAccommodations.add(selectedAccommodationModel);
			}
			else
			{
				selectedAccomModelToRemove = selectedAccommodationModel;
			}
		}
		sessionCart.setSelectedAccommodations(retainedSelectedAccommodations);
		getModelService().save(sessionCart);
		if (selectedAccomModelToRemove != null)
		{
			getModelService().remove(selectedAccomModelToRemove);
		}
	}

	@Override
	public List<CommerceCartModification> addAutoPickProductsFromDefaultBundle(final CartModel masterCartModel, final int bundleNo,
			final BundleTemplateModel bundleTemplate, final UnitModel unit) throws CommerceCartModificationException
	{
		return super.addAutoPickProductsToCart(masterCartModel, bundleNo, bundleTemplate, unit);
	}


	@Override
	protected void checkQuantityToAdd(final long quantityToAdd, final long maxQuantity) throws CommerceCartModificationException
	{
		if (quantityToAdd < 0)
		{
			throw new CommerceCartModificationException("The given quantityToAdd (" + quantityToAdd + ") is invalid");
		}
	}

	@Override
	public void removeCartEntriesForMinODRefNumber(final Integer odRefNum)
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel sessionCart = getCartService().getSessionCart();

		if (odRefNum.equals(0))
		{
			removeTravellers(sessionCart);
			removeFeeOrderEntries(sessionCart);
		}

		if (CollectionUtils.isNotEmpty(sessionCart.getSelectedAccommodations()))
		{
			final List<SelectedAccommodationModel> selectedAccommodations = sessionCart.getSelectedAccommodations().stream()
					.filter(accommodation -> accommodation.getTransportOffering().getTravelOrderEntryInfo().stream()
							.anyMatch(orderEntryInfo -> orderEntryInfo.getOriginDestinationRefNumber().compareTo(odRefNum) >= 0))
					.collect(Collectors.toList());
			getModelService().removeAll(selectedAccommodations);
		}

		final List<AbstractOrderEntryModel> cartEntries = sessionCart.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& !(ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel)
						&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().compareTo(odRefNum) >= 0)
				.collect(Collectors.toList());

		final Set<Integer> entryGroupNumbers = cartEntries.stream().flatMap(entry -> entry.getEntryGroupNumbers().stream())
				.collect(Collectors.toSet());

		removeTravelOrderEntries(cartEntries);
		getModelService().refresh(sessionCart);

		if (odRefNum.equals(0))
		{
			sessionCart.setEntryGroups(Collections.emptyList());
		}
		else
		{
			removeCartEntryGroupNumbers(sessionCart, entryGroupNumbers);
		}
		getModelService().save(sessionCart);
	}

	/**
	 * Remove cart entry group from the cart taking the set of entry group number from the order entries that needs to be removed
	 * and looking up for their parent groups.
	 *
	 * @param sessionCart
	 * 		the session cart
	 * @param entryGroupNumbers
	 * 		the entry group numbers
	 */
	protected void removeCartEntryGroupNumbers(final CartModel sessionCart, final Set<Integer> entryGroupNumbers)
	{
		final List<EntryGroup> entryGroups = sessionCart.getEntryGroups().stream()
				.filter(entry -> {
					final Optional<EntryGroup> any = entry.getChildren().stream()
							.filter(child -> entryGroupNumbers.contains(child.getGroupNumber())).findAny();
					return any.isPresent();
				})
				.collect(Collectors.toList());

		final List<EntryGroup> newEntryGroups = new LinkedList<>(sessionCart.getEntryGroups());
		newEntryGroups.removeAll(entryGroups);

		sessionCart.setEntryGroups(newEntryGroups);
	}

	@Override
	public void removeCartEntriesForODRefNumber(final Integer odRefNum, final CartModel cartModel)
	{
		if (Objects.isNull(cartModel) || CollectionUtils.isEmpty(cartModel.getEntries()) || Objects.isNull(odRefNum))
		{
			return;
		}

		final List<AbstractOrderEntryModel> cartEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& !(ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel)
						&& entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().compareTo(odRefNum) == 0)
				.collect(Collectors.toList());

		final Set<Integer> entryGroupNumbers = cartEntries.stream().flatMap(entry -> entry.getEntryGroupNumbers().stream())
				.collect(Collectors.toSet());

		if (CollectionUtils.isNotEmpty(cartModel.getSelectedAccommodations()))
		{
			final List<SelectedAccommodationModel> selectedAccommodations = cartModel.getSelectedAccommodations().stream()
					.filter(accommodation -> accommodation.getTransportOffering().getTravelOrderEntryInfo().stream()
							.anyMatch(orderEntryInfo -> orderEntryInfo.getOriginDestinationRefNumber().compareTo(odRefNum) == 0))
					.collect(Collectors.toList());
			getModelService().removeAll(selectedAccommodations);
		}

		removeTravelOrderEntries(cartEntries);
		getModelService().refresh(cartModel);

		removeCartEntryGroupNumbers(cartModel, entryGroupNumbers);
		getModelService().save(cartModel);
	}

	@Override
	public void setEntryAsCalculatedAndInitializePriceLevel(final Integer entryNumberToUpdate)
			throws CommerceCartModificationException
	{
		final CartEntryModel cartEntry = getCartService().getEntryForNumber(getCartService().getSessionCart(), entryNumberToUpdate);
		updateCartEntryWithTravelDetails(cartEntry, null);
		cartEntry.setCalculated(Boolean.TRUE);
		getModelService().save(cartEntry);
	}

	@Override
	public List<CommerceCartModification> addAutoPickProductsToCart(final ProductModel productModel, final String bundleTemplateId,
			final int bundleEntryGroupNo) throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);

		final BundleTemplateModel rootTemplate = bundleTemplate.getParentTemplate();

		List<TransportOfferingModel> transportOfferingModels = new ArrayList<>();
		TravelRouteModel travelRouteModel = null;
		TravellerModel travellerModel = null;

		if (Objects.nonNull(getSessionService().getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP)))
		{
			final Map<String, Object> addBundleToCartParamsMap = getSessionService()
					.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);
			if (addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS) != null)
			{
				transportOfferingModels = (List<TransportOfferingModel>) addBundleToCartParamsMap
						.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS);
			}

			travelRouteModel = (TravelRouteModel) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE);
			travellerModel = (TravellerModel) addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRAVELLER);
		}

		final List<CommerceCartModification> modificationList = new ArrayList<>();

		for (final BundleTemplateModel childTemplate : rootTemplate.getChildTemplates())
		{
			if (!getBundleTemplateService().isAutoPickComponent(childTemplate) || !childTemplate.getBundleSelectionCriteria()
					.getClass().equals(AutoPickBundleSelectionCriteriaModel.class))
			{
				continue;
			}
			final List<ProductModel> autoPickProducts = childTemplate.getProducts();
			for (final ProductModel autoPickProduct : autoPickProducts)
			{
				if (!autoPickProduct.getSupercategories().stream().findFirst().isPresent())
				{
					continue;
				}
				final Optional<CategoryModel> categoryModel = CollectionUtils.isNotEmpty(autoPickProduct.getSupercategories()) ?
						autoPickProduct.getSupercategories().stream().findFirst() :
						null;
				final String offerGroupCode =
						Objects.nonNull(categoryModel) && categoryModel.isPresent() ? categoryModel.get().getCode() : StringUtils.EMPTY;
				final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(offerGroupCode,
						getOfferGroupToOriginDestinationMapping()
								.getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
										TravelservicesConstants.TRAVEL_ROUTE));
				if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRANSPORT_OFFERING))
				{
					addAutopickProductsForTransportOfferings(bundleEntryGroupNo, transportOfferingModels, travelRouteModel,
							travellerModel, modificationList, childTemplate, autoPickProduct);
				}
				else if (StringUtils.equalsIgnoreCase(mapping, TravelservicesConstants.TRAVEL_ROUTE))
				{
					addAutopickProductsForRoute(bundleEntryGroupNo, transportOfferingModels, travelRouteModel, travellerModel,
							modificationList, childTemplate, autoPickProduct);
				}
			}
		}

		return modificationList;
	}

	@Override
	public List<CommerceCartModification> addPerLegBundleProductToCart(final String bundleTemplateId)
			throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);

		final BundleTemplateModel rootTemplate = bundleTemplate.getParentTemplate();

		final Map<String, Object> addBundleToCartParamsMap = getSessionService()
				.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);

		List<TransportOfferingModel> transportOfferingModels = new ArrayList<>();
		if (addBundleToCartParamsMap.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS) != null)
		{
			transportOfferingModels = (List<TransportOfferingModel>) addBundleToCartParamsMap
					.get(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS);
		}

		final TravelRouteModel travelRouteModel = (TravelRouteModel) addBundleToCartParamsMap
				.get(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE);

		final List<CommerceCartModification> modificationList = new ArrayList<>();

		for (final BundleTemplateModel childTemplate : rootTemplate.getChildTemplates())
		{
			if (!(childTemplate.getBundleSelectionCriteria() instanceof AutoPickPerLegBundleSelectionCriteriaModel))
			{
				continue;
			}
			final List<ProductModel> autoPickProducts = childTemplate.getProducts();
			for (final ProductModel autoPickProduct : autoPickProducts)
			{
				final Optional<CategoryModel> categoryModel = CollectionUtils.isNotEmpty(autoPickProduct.getSupercategories()) ?
						autoPickProduct.getSupercategories().stream().findFirst() :
						null;

				final CategoryModel offerGroup =
						Objects.nonNull(categoryModel) && categoryModel.isPresent() ? categoryModel.get() : null;
				if (Objects.isNull(offerGroup) || Objects.isNull(offerGroup.getTravelRestriction()) || Objects
						.isNull(offerGroup.getTravelRestriction().getAddToCartCriteria()) || !AddToCartCriteriaType.PER_LEG
						.equals(offerGroup.getTravelRestriction().getAddToCartCriteria()))
				{
					continue;
				}

				try
				{
					addAutopickPerLegProducts(transportOfferingModels, travelRouteModel, modificationList, childTemplate,
							autoPickProduct);
				}
				catch (final CommerceCartModificationException ex)
				{
					LOG.warn("Couldn't add product of code " + autoPickProduct.getCode() + " to cart.", ex);
					throw new CommerceBundleCartModificationException(ex.getMessage(), ex);
				}
			}

		}
		return modificationList;
	}

	protected void addAutopickPerLegProducts(final List<TransportOfferingModel> transportOfferingModels,
			final TravelRouteModel travelRouteModel, final List<CommerceCartModification> modificationList,
			final BundleTemplateModel childTemplate, final ProductModel autoPickProduct) throws CommerceCartModificationException
	{
		final PriceLevel priceLevel = new PriceLevel();
		if (getTravelCommercePriceService()
				.isPriceInformationAvailable(autoPickProduct, PriceRowModel.TRAVELROUTECODE, travelRouteModel.getCode()))
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
			priceLevel.setValue(travelRouteModel.getCode());
		}
		else
		{
			priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
		}
		final List<String> transportOfferingCodes = transportOfferingModels.stream().map(TransportOfferingModel::getCode)
				.collect(Collectors.toList());
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, null);


		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(getCartService().getSessionCart());
		parameter.setEntryGroupNumbers(Collections.emptySet());
		parameter.setBundleTemplate(childTemplate);
		parameter.setProduct(autoPickProduct);
		parameter.setQuantity(BUNDLE_PRODUCT_QUANTITY);

		final CommerceCartModification modification = addToCart(parameter);

		if (modification.getQuantityAdded() > 0 && modification.getEntry() != null)
		{
			setEntryAsCalculatedAndInitializePriceLevel(modification.getEntry().getEntryNumber());
			updateCartEntryWithTravelDetails(modification.getEntry(), priceLevel, transportOfferingModels, false);
		}
		modificationList.add(modification);
	}

	/**
	 * Removes the given list of {@link AbstractOrderEntryModel} and their {@link TravelOrderEntryInfoModel}.
	 *
	 * @param abstractOrderEntries
	 * 		the list of abstract order entry models
	 */
	protected void removeTravelOrderEntries(final List<AbstractOrderEntryModel> abstractOrderEntries)
	{
		if (CollectionUtils.isNotEmpty(abstractOrderEntries))
		{
			final List<TravelOrderEntryInfoModel> travelOrderEntryInfos = abstractOrderEntries.stream()
					.map(AbstractOrderEntryModel::getTravelOrderEntryInfo).collect(Collectors.toList());

			getModelService().removeAll(travelOrderEntryInfos);
			getModelService().removeAll(abstractOrderEntries);
		}
	}

	/**
	 * Removes the list of {@link TravellerModel} and {@link de.hybris.platform.travelservices.model.user.TravellerInfoModel}
	 * linked to the given {@link AbstractOrderModel}.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 */
	protected void removeTravellers(final AbstractOrderModel abstractOrderModel)
	{
		final List<TravellerModel> travellers = getTravellerService().getTravellers(abstractOrderModel.getEntries());
		if (CollectionUtils.isEmpty(travellers))
		{
			return;
		}
		getModelService().removeAll(travellers.stream().map(TravellerModel::getInfo).collect(Collectors.toList()));
		getModelService().removeAll(travellers);
	}

	/**
	 * Removes the list of {@link AbstractOrderEntryModel} that corresponds to a product of {@link ProductType} FEE.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 */
	protected void removeFeeOrderEntries(final AbstractOrderModel abstractOrderModel)
	{
		final List<AbstractOrderEntryModel> feeEntries = abstractOrderModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& (ProductType.FEE.equals(entry.getProduct().getProductType())) || entry.getProduct() instanceof FeeProductModel)
				.collect(Collectors.toList());
		getModelService().removeAll(feeEntries);
	}

	/**
	 * @return TravellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * @param travellerService
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * @return TransportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 */
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 * 		the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 * 		the offerGroupToOriginDestinationMapping to set
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	/**
	 * @return the sessionService
	 */
	@Override
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 * 		the sessionService to set
	 */
	@Override
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}

	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

}
