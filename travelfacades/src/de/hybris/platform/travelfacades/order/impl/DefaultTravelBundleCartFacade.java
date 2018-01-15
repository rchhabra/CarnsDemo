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

package de.hybris.platform.travelfacades.order.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.configurablebundlefacades.order.impl.DefaultBundleCartFacade;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * The Default travel bundle cart facade.
 *
 * @deprecated since 4.0 methods no longer needed
 */
@Deprecated
public class DefaultTravelBundleCartFacade extends DefaultBundleCartFacade
{
	/**
	 * @param productCode
	 * @param quantity
	 * @param bundleNo
	 * @param bundleTemplateId
	 * @param removeCurrentProducts
	 * @return
	 * @throws CommerceCartModificationException
	 * @deprecated since 4.0
	 */
	@Deprecated
	@Override
	@Nonnull
	public List<CartModificationData> addToCart(@Nonnull final String productCode, final long quantity, final int bundleNo,
			@Nullable final String bundleTemplateId, final boolean removeCurrentProducts) throws CommerceCartModificationException
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);

		final String xml = getSubscriptionCartFacade().getProductAsXML(product);

		BundleTemplateModel bundleTemplate = null;
		if (StringUtils.isNotEmpty(bundleTemplateId))
		{
			if (bundleNo > 0)
			{
				final List<CartEntryModel> entries = getBundleCommerceCartService().getCartEntriesForBundle(cartModel, bundleNo);
				if (CollectionUtils.isEmpty(entries) || entries.get(0).getBundleTemplate() == null)
				{
					throw new CommerceCartModificationException("Can't determine parentBundleTemplateModel");
				}
				final BundleTemplateModel parentModel = entries.get(0).getBundleTemplate().getParentTemplate();

				bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId, parentModel.getVersion());
			}
			else
			{
				bundleTemplate = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);
			}
		}

		final List<CommerceCartModification> modifications = getBundleCommerceCartService().addToCart(cartModel, product, quantity,
				product.getUnit(), false, bundleNo, bundleTemplate, removeCurrentProducts, xml);
		return Converters.convertAll(modifications, getCartModificationConverter());
	}

}
