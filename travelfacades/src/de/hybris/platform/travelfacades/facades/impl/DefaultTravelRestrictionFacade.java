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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.strategies.impl.TravelRestrictionStrategy;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.OfferGroupRestrictionModel;
import de.hybris.platform.travelservices.model.travel.TravelRestrictionModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravelCategoryService;
import de.hybris.platform.travelservices.services.TravelRestrictionService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.facades.TravelRestrictionFacade} interface.
 */
public class DefaultTravelRestrictionFacade implements TravelRestrictionFacade
{
	private TravelCommerceCartService travelCommerceCartService;
	private CartService cartService;
	private ProductService productService;
	private TravelRestrictionStrategy travelRestrictionStrategy;
	private TravelCategoryService travelCategoryService;
	private BookingService bookingService;
	private TravelRestrictionService travelRestrictionService;
	private Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter;

	@Override
	public boolean checkIfProductCanBeAdded(final String productCode, final long quantity, final String travelRouteCode,
			final List<String> transportOfferingCodes, final String travellerUid)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		return checkIfProductCanBeAdded(productModel, quantity, travelRouteCode, transportOfferingCodes, travellerUid,
				getCartService().getSessionCart());
	}

	@Override
	public boolean checkIfProductCanBeAdded(final ProductModel productModel, final long quantity,
			final String travelRouteCode,
			final List<String> transportOfferingCodes, final String travellerUid, final AbstractOrderModel abstractOrder)
	{
		// check ProductRestriction
		final List<String> travellerCodes = new ArrayList<>();
		if (StringUtils.isNotBlank(travellerUid))
		{
			travellerCodes.add(travellerUid);
		}

		final AbstractOrderEntryModel existingOrderEntry = getBookingService().getOrderEntry(abstractOrder,
				productModel.getCode(), travelRouteCode, transportOfferingCodes, travellerCodes, false);

		final long finalProductQty = quantity + (existingOrderEntry != null ? existingOrderEntry.getQuantity().longValue() : 0);
		if (!getTravelRestrictionStrategy().checkQuantityForTravelRestriction(productModel.getTravelRestriction(), finalProductQty))
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean checkCategoryRestrictions()
	{
		return MapUtils.isEmpty(getCategoryRestrictionErrors());
	}

	@Override
	public Map<String, String> getCategoryRestrictionErrors()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final Map<String, String> restrictionErrors = new HashMap<>();

		final List<AbstractOrderEntryModel> entries = cartModel.getEntries().stream()
				.filter(entry -> entry.getProduct() instanceof FareProductModel
						|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
				.collect(Collectors.toList());

		entries.forEach(entry -> {
			final List<String> travellers = entry.getTravelOrderEntryInfo().getTravellers().stream().map(TravellerModel::getUid)
					.collect(Collectors.toList());
			final List<String> transportOfferingCodes = entry.getTravelOrderEntryInfo().getTransportOfferings().stream()
					.map(TransportOfferingModel::getCode)
					.collect(Collectors.toList());
			final List<CategoryModel> categories = getTravelCategoryService().getAncillaryCategories(transportOfferingCodes);

			evaluateRestrictionErrorsForEntry(restrictionErrors, entry, travellers, transportOfferingCodes, categories);
		});
		return restrictionErrors;
	}

	protected void evaluateRestrictionErrorsForEntry(final Map<String, String> restrictionErrors,
			final AbstractOrderEntryModel entry, final List<String> travellers, final List<String> transportOfferingCodes,
			final List<CategoryModel> categories)
	{
		travellers.forEach(traveller -> categories.forEach(category -> {
			final long categoryQuantity = getTravelCommerceCartService()
					.getOrderEntriesForCategory(getCartService().getSessionCart(), category,
							entry.getTravelOrderEntryInfo().getTravelRoute().getCode(),
							transportOfferingCodes, traveller)
					.size();
			if (!getTravelRestrictionStrategy().checkQuantityForMandatoryTravelRestriction(category.getTravelRestriction(),
					categoryQuantity))
			{
				restrictionErrors.put(category.getName(), category.getTravelRestriction().getTravellerMinOfferQty().toString());
			}
		}));
	}

	@Override
	public TravelRestrictionData getTravelRestrictionForProduct(final String productCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final TravelRestrictionModel travelRestrictionModel = productModel.getTravelRestriction();
		if (travelRestrictionModel == null)
		{
			return null;
		}
		final TravelRestrictionData travelRestrictionData = getTravelRestrictionConverter().convert(travelRestrictionModel);
		travelRestrictionData.setAddToCartCriteria(getAddToCartCriteria(productModel));
		return travelRestrictionData;
	}

	protected String getAddToCartCriteria(final ProductModel productModel)
	{
		return getTravelRestrictionService().getAddToCartCriteria(productModel).getCode();
	}

	@Override
	public TravelRestrictionData getTravelRestrictionForCategory(final String categoryCode)
	{
		final OfferGroupRestrictionModel offerGroupRestrictionModel = getTravelCategoryService().getCategoryForCode(categoryCode)
				.getTravelRestriction();
		if (offerGroupRestrictionModel == null)
		{
			return null;
		}
		final TravelRestrictionData travelRestrictionData = getTravelRestrictionConverter().convert(offerGroupRestrictionModel);
		travelRestrictionData.setAddToCartCriteria(getAddToCartCriteria(offerGroupRestrictionModel));

		return travelRestrictionData;
	}

	protected String getAddToCartCriteria(final OfferGroupRestrictionModel offerGroupRestrictionModel)
	{
		return offerGroupRestrictionModel.getAddToCartCriteria() != null
				? offerGroupRestrictionModel.getAddToCartCriteria().getCode() : TravelfacadesConstants.DEFAULT_ADD_TO_CART_CRITERIA;
	}

	@Override
	public String getAddToCartCriteria(final String productCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		return getAddToCartCriteria(productModel);
	}

	/**
	 * @return the travelCommerceCartService
	 */
	protected TravelCommerceCartService getTravelCommerceCartService()
	{
		return travelCommerceCartService;
	}

	/**
	 * @param travelCommerceCartService
	 *           as the travelCommerceCartService to set
	 */
	public void setTravelCommerceCartService(final TravelCommerceCartService travelCommerceCartService)
	{
		this.travelCommerceCartService = travelCommerceCartService;
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
	 *           as the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           as the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the travelRestrictionStrategy
	 */
	protected TravelRestrictionStrategy getTravelRestrictionStrategy()
	{
		return travelRestrictionStrategy;
	}

	/**
	 * @param travelRestrictionStrategy
	 *           as the travelRestrictionStrategy to set
	 */
	public void setTravelRestrictionStrategy(final TravelRestrictionStrategy travelRestrictionStrategy)
	{
		this.travelRestrictionStrategy = travelRestrictionStrategy;
	}

	/**
	 * @return the travelCategoryService
	 */
	protected TravelCategoryService getTravelCategoryService()
	{
		return travelCategoryService;
	}

	/**
	 * @param travelCategoryService
	 *           as the travelCategoryService to set
	 */
	public void setTravelCategoryService(final TravelCategoryService travelCategoryService)
	{
		this.travelCategoryService = travelCategoryService;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the travelRestrictionService
	 */
	protected TravelRestrictionService getTravelRestrictionService()
	{
		return travelRestrictionService;
	}

	/**
	 * @param travelRestrictionService
	 *           the travelRestrictionService to set
	 */
	public void setTravelRestrictionService(final TravelRestrictionService travelRestrictionService)
	{
		this.travelRestrictionService = travelRestrictionService;
	}

	/**
	 * @return the travelRestrictionConverter
	 */
	protected Converter<TravelRestrictionModel, TravelRestrictionData> getTravelRestrictionConverter()
	{
		return travelRestrictionConverter;
	}

	/**
	 * @param travelRestrictionConverter
	 *           the travelRestrictionConverter to set
	 */
	public void setTravelRestrictionConverter(
			final Converter<TravelRestrictionModel, TravelRestrictionData> travelRestrictionConverter)
	{
		this.travelRestrictionConverter = travelRestrictionConverter;
	}

}
