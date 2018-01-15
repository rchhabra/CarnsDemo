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

package de.hybris.platform.travelfacades.fare.search.converters.populator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.IncludedAncillaryData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.model.AutoPickBundleSelectionCriteriaModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.AutoPickPerLegBundleSelectionCriteriaModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Travel bundle template populator.
 *
 * @param <SOURCE>
 *           the type parameter
 * @param <TARGET>
 *           the type parameter
 */
public class TravelBundleTemplatePopulator<SOURCE extends BundleTemplateModel, TARGET extends BundleTemplateData>
		implements Populator<SOURCE, TARGET>
{

	private Converter<FareProductModel, FareProductData> fareProductConverter;
	private Converter<ProductModel, ProductData> productConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{

		validateParameterNotNullStandardMessage("target", target);
		validateParameterNotNullStandardMessage("source", source);

		target.setId(source.getId());
		target.setName(source.getName());
		target.setVersion(source.getVersion());

		if (!(target instanceof TravelBundleTemplateData))
		{
			return;
		}
		final TravelBundleTemplateData travelBundleTemplateData = (TravelBundleTemplateData) target;
		travelBundleTemplateData.setBundleType(source.getType().getCode());
		travelBundleTemplateData.setPromotional(BooleanUtils.toBoolean(source.getPromotional()));

		final Map<String, List<ProductData>> nonFareProducts = new HashMap<>();
		final List<IncludedAncillaryData> includedAncillaries = new ArrayList<>();
		for (final BundleTemplateModel childBundleTemplateModel : source.getChildTemplates())
		{
			if (childBundleTemplateModel.getBundleSelectionCriteria() == null)
			{
				continue;
			}

			//If childBundleTemplateModel has fare products then add them to bundleTemplate -> fareproducts
			final List<ProductModel> products = childBundleTemplateModel.getProducts();

			final Optional<ProductModel> productModel = CollectionUtils.isNotEmpty(products) ? products.stream().findFirst() :
					Optional.empty();
			if (productModel.isPresent() && (productModel.get().getProductType().equals(ProductType.FARE_PRODUCT)
					&& productModel.get() instanceof FareProductModel))
			{
				travelBundleTemplateData.setFareProductBundleTemplateId(childBundleTemplateModel.getId());
				travelBundleTemplateData.setFareProducts(convertFareProduct(products));
			}
			//if childBundleTemplateModel has ancillary products then add them to bundleTemplate -> nonFareProducts
			else
			{
				final List<ProductData> productDatas = convertProducts(products);

				if (childBundleTemplateModel.getBundleSelectionCriteria().getClass()
						.equals(AutoPickBundleSelectionCriteriaModel.class))
				{
					nonFareProducts.put(childBundleTemplateModel.getId(), productDatas);
				}
				else if (childBundleTemplateModel
						.getBundleSelectionCriteria() instanceof AutoPickPerLegBundleSelectionCriteriaModel)
				{
					final String bundleSelectionCriteria = AddToCartCriteriaType.PER_LEG.getCode();
					includedAncillaries.add(createIncludedAncillaryData(productDatas, bundleSelectionCriteria));
				}

			}
		}
		travelBundleTemplateData.setNonFareProducts(nonFareProducts);
		travelBundleTemplateData.setIncludedAncillaries(includedAncillaries);
		if(Objects.nonNull(source.getIgnoreRules()))
		{
			travelBundleTemplateData.setIgnoreRules(source.getIgnoreRules());
		}
	}

	/**
	 * Creates a new IncludedAncillaryData
	 *
	 * @param productDatas
	 *           the product datas
	 * @param bundleSelectionCriteria
	 *           the bundle selection criteria
	 * @return the IncludedAncillaryData
	 */
	protected IncludedAncillaryData createIncludedAncillaryData(final List<ProductData> productDatas,
			final String bundleSelectionCriteria)
	{
		final IncludedAncillaryData includedAncillaryData = new IncludedAncillaryData();

		includedAncillaryData.setCriteria(bundleSelectionCriteria);
		includedAncillaryData.setProducts(productDatas);

		return includedAncillaryData;
	}

	/**
	 * Method takes a list of ProductModel and returns a list of ProductData
	 *
	 * @param products
	 *           the products
	 * @return List<ProductData> list
	 */
	protected List<ProductData> convertProducts(final List<ProductModel> products)
	{
		final List<ProductData> productDatas = new ArrayList<>();
		for (final ProductModel product : products)
		{
			productDatas.add(getProductConverter().convert(product));
		}
		return productDatas;
	}

	/**
	 * Method takes a list of ProductModel and returns a list of FareProductData
	 *
	 * @param products
	 *           the products
	 * @return List<FareProductData> list
	 */
	protected List<FareProductData> convertFareProduct(final List<ProductModel> products)
	{
		final List<FareProductData> fareProducts = new ArrayList<>();
		for (final ProductModel productModel : products)
		{
			if (!(ProductType.FARE_PRODUCT.equals(productModel.getProductType()) || productModel instanceof FareProductModel))
			{
				continue;
			}
			final FareProductModel fareProductModel = (FareProductModel) productModel;
			fareProducts.add(getFareProductConverter().convert(fareProductModel));
		}
		return fareProducts;
	}

	/**
	 * Gets fare product converter.
	 *
	 * @return the fare product converter
	 */
	protected Converter<FareProductModel, FareProductData> getFareProductConverter()
	{
		return fareProductConverter;
	}

	/**
	 * Sets fare product converter.
	 *
	 * @param fareProductConverter
	 *           the fare product converter
	 */
	@Required
	public void setFareProductConverter(final Converter<FareProductModel, FareProductData> fareProductConverter)
	{
		this.fareProductConverter = fareProductConverter;
	}

	/**
	 * Gets product converter.
	 *
	 * @return the product converter
	 */
	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	/**
	 * Sets product converter.
	 *
	 * @param productConverter
	 *           the product converter
	 */
	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}

}
