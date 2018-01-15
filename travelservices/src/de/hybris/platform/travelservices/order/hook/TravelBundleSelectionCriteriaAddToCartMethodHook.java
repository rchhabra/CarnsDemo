/*
 *  [y] hybris Platform
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

package de.hybris.platform.travelservices.order.hook;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.model.BundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickExactlyNBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.PickNToMBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.order.hook.BundleSelectionCriteriaAddToCartMethodHook;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.order.EntryGroup;
import org.apache.commons.collections.CollectionUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class TravelBundleSelectionCriteriaAddToCartMethodHook extends BundleSelectionCriteriaAddToCartMethodHook
{
	@Override
	public void beforeAddToCart(final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		if (parameter.getBundleTemplate() == null)
		{
			return;
		}

		EntryGroup bundleEntryGroup = null;
		try
		{
			bundleEntryGroup = getBundleTemplateService().getBundleEntryGroup(parameter.getCart(), parameter.getEntryGroupNumbers());
		}
		catch (final IllegalArgumentException e)
		{
			throw new CommerceCartModificationException(e.getMessage(), e);
		}
		if (bundleEntryGroup == null)
		{
			return;
		}

		validateParameterNotNullStandardMessage("parameter.cart", parameter.getCart());

		final int maxItemsAllowed;
		final BundleSelectionCriteriaModel selectionCriteria = parameter.getBundleTemplate().getBundleSelectionCriteria();

		if (selectionCriteria instanceof PickNToMBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickNToMBundleSelectionCriteriaModel) selectionCriteria).getM();
		}
		else if (selectionCriteria instanceof PickExactlyNBundleSelectionCriteriaModel)
		{
			maxItemsAllowed = ((PickExactlyNBundleSelectionCriteriaModel) selectionCriteria).getN();
		}
		else
		{
			return;
		}

		final long total = getProjectedTotalQuantityInBundle(parameter, bundleEntryGroup.getGroupNumber());
		if (total > maxItemsAllowed)
		{
			final String message = getL10NService().getLocalizedString("bundleservices.validation.selectioncriteriaexceeded",
					new Object[] { getBundleTemplateService().getBundleTemplateName(parameter.getBundleTemplate()),
							String.valueOf(maxItemsAllowed), String.valueOf(total) });
			throw new CommerceCartModificationException(message);
		}
	}

	/**
	 * Quantity check. Quantity in cart cannot exceed the limit stored against each bundle rule, considering
	 * all the entries related to the same bundle, same group, same product.
	 *
	 * @param parameter
	 * @param entryGroupNumber
	 * @return
	 */
	protected long getProjectedTotalQuantityInBundle(CommerceCartParameter parameter, Integer entryGroupNumber)
	{
		return parameter.getCart().getEntries().stream().filter(entry -> parameter.getProduct().equals(entry.getProduct())).filter(
				entry -> CollectionUtils.isNotEmpty(entry.getEntryGroupNumbers()) && entry.getEntryGroupNumbers()
						.contains(entryGroupNumber)).mapToLong(AbstractOrderEntryModel::getQuantity).sum() + parameter.getQuantity();
	}
}
