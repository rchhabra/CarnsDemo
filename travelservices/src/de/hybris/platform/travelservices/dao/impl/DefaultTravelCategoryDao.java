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
package de.hybris.platform.travelservices.dao.impl;

import de.hybris.platform.category.daos.impl.DefaultCategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.travelservices.dao.TravelCategoryDao;
import de.hybris.platform.travelservices.enums.ProductType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DefaultTravelCategoryDao extends DefaultCategoryDao implements TravelCategoryDao
{

	@Override
	public List<CategoryModel> getAncillaryCategories(final List<String> transportOfferingsCodes)
	{

		final StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("SELECT DISTINCT ").append(" {c.pk} ").append(" FROM {categoryProductRelation AS cpr JOIN ")
				.append(CategoryModel._TYPECODE).append(" AS c ON").append(" {cpr.source}={c.pk} JOIN ")
				.append(ProductModel._TYPECODE).append(" AS p ON {p.").append(ProductModel.PK).append("} = {cpr.target} JOIN ")
				.append(StockLevelModel._TYPECODE).append(" AS sl ON {sl.").append(StockLevelModel.PRODUCTCODE).append("}={p.")
				.append(ProductModel.CODE).append("}").append(" JOIN ").append(WarehouseModel._TYPECODE).append(" as w on {sl.")
				.append(StockLevelModel.WAREHOUSE).append("} = {w.").append(WarehouseModel.PK).append("}").append(" JOIN ")
				.append(ProductType._TYPECODE).append(" as pt on {p.").append(ProductModel.PRODUCTTYPE).append("} = {pt.")
				.append("pk").append("}}").append(" WHERE ").append(" {pt.").append("code }")
				.append(" = '" + ProductType.ANCILLARY + "' AND ").append(" {w.").append(WarehouseModel.CODE)
				.append("} IN (?transportOfferingsCodes,'" + WarehouseModel.DEFAULT + "')");

		final Map<String, Object> params = new HashMap<>();
		params.put("transportOfferingsCodes", transportOfferingsCodes);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryBuilder.toString(), params);

		final SearchResult<CategoryModel> searchResult = getFlexibleSearchService().search(fsq);

		return searchResult.getResult();
	}

	@Override
	public List<CategoryModel> getAccommodationCategories(final List<String> transportOfferingCodes)
	{

		final StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("SELECT DISTINCT ").append(" {c.pk} ").append(" FROM {categoryProductRelation AS cpr JOIN ")
				.append(CategoryModel._TYPECODE).append(" AS c ON").append(" {cpr.source}={c.pk} JOIN ")
				.append(ProductModel._TYPECODE).append(" AS p ON {p.").append(ProductModel.PK).append("} = {cpr.target} JOIN ")
				.append(StockLevelModel._TYPECODE).append(" AS sl ON {sl.").append(StockLevelModel.PRODUCTCODE).append("}={p.")
				.append(ProductModel.CODE).append("}").append(" JOIN ").append(WarehouseModel._TYPECODE).append(" as w on {sl.")
				.append(StockLevelModel.WAREHOUSE).append("} = {w.").append(WarehouseModel.PK).append("}").append(" JOIN ")
				.append(ProductType._TYPECODE).append(" as pt on {p.").append(ProductModel.PRODUCTTYPE).append("} = {pt.")
				.append("pk").append("}}").append(" WHERE ").append(" {pt.").append("code }")
				.append(" = '" + ProductType.ACCOMMODATION + "' AND ").append(" {w.").append(WarehouseModel.CODE)
				.append("} IN (?transportOfferingsCodes,'" + WarehouseModel.DEFAULT + "')");

		final Map<String, Object> params = new HashMap<>();
		params.put("transportOfferingsCodes", transportOfferingCodes);

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(queryBuilder.toString(), params);

		final SearchResult<CategoryModel> searchResult = getFlexibleSearchService().search(fsq);

		return searchResult.getResult();

	}
}