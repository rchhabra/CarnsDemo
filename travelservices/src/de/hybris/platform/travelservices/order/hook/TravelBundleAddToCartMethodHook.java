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
import de.hybris.platform.commerceservices.order.hook.CommerceAddToCartMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.order.hook.BundleAddToCartMethodHook;
import de.hybris.platform.core.order.EntryGroup;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.util.Objects;


public class TravelBundleAddToCartMethodHook extends BundleAddToCartMethodHook implements CommerceAddToCartMethodHook
{
	/**
	 * Removed check if the bundles stored against entries with same order group correspond.
	 * We save the bundle containing the product added to the cart against the entry but
	 * assign the same group numbers / bundleNo to every entry belonging to the same root bundle,
	 * considering the whole hierarchy.
	 *
	 * @param parameter
	 * @throws CommerceCartModificationException
	 */
	@Override
	protected void checkBundleParameters(@Nonnull final CommerceCartParameter parameter) throws CommerceCartModificationException
	{
		final EntryGroup group = getBundleEntryGroup(parameter);

		if (group != null)
		{
			if (group.getExternalReferenceId() == null)
			{
				throw new CommerceCartModificationException(
						"Entry group #" + group.getGroupNumber() + " has null bundle component code");
			}
		}
	}

	/**
	 * We could need to iterate though the bundle tree to find a match,
	 * that is a child bundle containing the product we are going to add
	 *
	 * @param parameter
	 * @throws CommerceCartModificationException
	 */
	@Override
	protected void checkIsProductInComponentProductList(@Nonnull final CommerceCartParameter parameter)
			throws CommerceCartModificationException
	{
		final BundleTemplateModel bundleTemplateModel = parameter.getBundleTemplate();
		if (bundleTemplateModel.getProducts().contains(parameter.getProduct()))
		{
			return;
		}
		if (CollectionUtils.isNotEmpty(bundleTemplateModel.getChildTemplates()))
		{
			if (bundleTemplateModel.getChildTemplates().stream()
					.anyMatch(container -> container.getProducts().contains(parameter.getProduct())))
			{
				return;
			}
		}
		if (Objects.nonNull(bundleTemplateModel.getParentTemplate()))
		{
			BundleTemplateModel parentBundleTemplate = bundleTemplateModel.getParentTemplate();
			if (CollectionUtils.isNotEmpty(parentBundleTemplate.getChildTemplates()))
			{
				if (parentBundleTemplate.getChildTemplates().stream()
						.anyMatch(container -> container.getProducts().contains(parameter.getProduct())))
				{
					return;
				}
			}
		}
		throw new CommerceCartModificationException(
				"Product '" + parameter.getProduct().getCode() + "' is not either in the product list of component (bundle template) "
						+ getBundleTemplateService().getBundleTemplateName(parameter.getBundleTemplate())
						+ " or within one of its children");
	}

}

