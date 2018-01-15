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

package de.hybris.platform.travelfacades.packages.strategies.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.packages.strategies.AddBundleToCartByTypeStrategy;
import de.hybris.platform.travelservices.model.deal.RouteBundleTemplateModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Concrete implementation of {@link AddBundleToCartByTypeStrategy} allowing to add products belonging to an
 * TransportBundleTemplate to the cart
 */
public class AddTransportBundleToCartStrategy implements AddBundleToCartByTypeStrategy
{
	private TravelCartFacade cartFacade;

	@Override
	public List<CartModificationData> addBundleToCart(final BundleTemplateModel bundleTemplate,
			final AddDealToCartData addDealToCartData) throws CommerceCartModificationException
	{
		final AddBundleToCartRequestData addBundleToCartRequestData = buildAddBundleToCartRequestData(bundleTemplate,
				addDealToCartData);
		return getCartFacade().addBundleToCart(addBundleToCartRequestData);
	}

	protected AddBundleToCartRequestData buildAddBundleToCartRequestData(final BundleTemplateModel bundleTemplate,
			final AddDealToCartData addDealToCartData)
	{

		final AddBundleToCartRequestData addBundleToCartRequestData = new AddBundleToCartRequestData();
		addBundleToCartRequestData.setPassengerTypes(addDealToCartData.getPassengerTypes());
		final List<AddBundleToCartData> addBundleToCartDatas = new ArrayList<>();

		for (final BundleTemplateModel childBundleTemplate : bundleTemplate.getChildTemplates())
		{
			final RouteBundleTemplateModel routeBundleTemplate = (RouteBundleTemplateModel) childBundleTemplate;
			final Integer originDestinationRefNumber = routeBundleTemplate.getOriginDestinationRefNumber();
			final ItineraryPricingInfoData itineraryPricingInfoData = addDealToCartData.getItineraryPricingInfos()
					.get(originDestinationRefNumber);
			final String travelRouteCode = routeBundleTemplate.getTravelRoute().getCode();
			for (final TravelBundleTemplateData bundleData : itineraryPricingInfoData.getBundleTemplates())
			{
				final AddBundleToCartData addBundleToCart = new AddBundleToCartData();

				final Optional<BundleTemplateModel> childTemplate = CollectionUtils.isNotEmpty(
						routeBundleTemplate.getChildTemplates()) ? routeBundleTemplate.getChildTemplates().stream().findFirst() : null;
				addBundleToCart.setBundleTemplateId(
						Objects.nonNull(childTemplate) && childTemplate.isPresent() ? childTemplate.get().getId() : StringUtils.EMPTY);
				addBundleToCart.setProductCode(bundleData.getFareProducts().get(0).getCode());
				addBundleToCart.setTransportOfferings(CollectionUtils.isNotEmpty(bundleData.getTransportOfferings())
						? bundleData.getTransportOfferings().stream().map(TransportOfferingData::getCode).collect(Collectors.toList())
						: Collections.emptyList());
				addBundleToCart.setOriginDestinationRefNumber(originDestinationRefNumber);
				addBundleToCart.setTravelRouteCode(travelRouteCode);
				addBundleToCartDatas.add(addBundleToCart);
			}
		}
		addBundleToCartRequestData.setAddBundleToCartData(addBundleToCartDatas);
		return addBundleToCartRequestData;
	}

	/**
	 *
	 * @return cartFacade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 *
	 * @param cartFacade
	 *           the cartFacade
	 */
	@Required
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}


}
