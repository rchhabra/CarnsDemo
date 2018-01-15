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

package de.hybris.platform.travelbackoffice.display.providers;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.cockpitng.labels.LabelProvider;


/*
 * Overriding the label provider for Products.
 */
public class TravelProductLabelProvider implements LabelProvider<ProductModel>
{
	private CommonI18NService commonI18NService;

	public String getLabel(final ProductModel productModel)
	{
		if (Objects.isNull(productModel) || StringUtils.isEmpty(productModel.getCode()))
		{
			return null;
		}
		if (!(productModel instanceof FareProductModel) || CollectionUtils.isEmpty(productModel.getEurope1Prices()))
		{
			return productModel.getCode();
		}

		final String currentCurrency = getCommonI18NService().getBaseCurrency().getSymbol();
		final FareProductModel fareProductModel = (FareProductModel) productModel;

		final List<PriceRowModel> priceList = fareProductModel.getEurope1Prices().stream()
				.filter(priceRowModel -> priceRowModel.getCurrency().getSymbol().equals(currentCurrency))
				.sorted(new Comparator<PriceRowModel>()
				{
					@Override
					public int compare(final PriceRowModel o1, final PriceRowModel o2)
					{
						return o1.getPrice().compareTo(o2.getPrice());
					}
				}).collect(Collectors.toList());

		return CollectionUtils.isEmpty(priceList) ? fareProductModel.getCode()
				: fareProductModel.getBookingClass() + " - " + fareProductModel.getCode() + " - " + currentCurrency
						+ priceList.get(0).getPrice() + " - " + currentCurrency
						+ priceList.get(CollectionUtils.size(priceList) - 1).getPrice();
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Override
	public String getDescription(final ProductModel object)
	{
		return null;
	}

	@Override
	public String getIconPath(final ProductModel object)
	{
		return null;
	}
}
