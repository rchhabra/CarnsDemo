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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 * Concrete implementation of the {@link AverageUserRatingAttributeHandler} interface. Handler is responsible for
 * populating the average on {@link PropertyData}
 */
public class AverageUserRatingAttributeHandler extends AbstractDynamicAttributeHandler<Double, AccommodationOfferingModel>
{
	public static final int MAX_PAGE_LIMIT = 100;

	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;

	@Override
	public Double get(final AccommodationOfferingModel accommodationOfferingModel)
	{
		if (accommodationOfferingModel == null)
		{
			throw new IllegalArgumentException("Accommodation Offering model is required");
		}
		final PageableData pageableData = createPageableData(0, 0, "byDate");
		final List<CustomerReviewModel> results = getAccommodationOfferingCustomerReviewService()
				.getReviewForAccommodationOffering(accommodationOfferingModel.getCode(), pageableData).getResults();
		if (CollectionUtils.isEmpty(results))
		{
			return null;
		}
		BigDecimal totalRating = BigDecimal.ZERO;
		for (final CustomerReviewModel result : results)
		{
			totalRating = totalRating.add(BigDecimal.valueOf(result.getRating()));
		}
		return totalRating.doubleValue() > 0
				? totalRating.divide(BigDecimal.valueOf(results.size()), 2, RoundingMode.FLOOR).doubleValue() : 0d;

	}

	/**
	 * @return the accommodationOfferingCustomerReviewService
	 */
	protected AccommodationOfferingCustomerReviewService getAccommodationOfferingCustomerReviewService()
	{
		return accommodationOfferingCustomerReviewService;
	}

	/**
	 * Sets accommodation Offering Customer Review Service.
	 *
	 * @param accommodationOfferingCustomerReviewService
	 *           the accommodation Offering Customer Review Service
	 */
	public void setAccommodationOfferingCustomerReviewService(
			final AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService)
	{
		this.accommodationOfferingCustomerReviewService = accommodationOfferingCustomerReviewService;
	}

	/**
	 * Creates the pageable data.
	 *
	 * @param pageNumber
	 *           the page number
	 * @param pageSize
	 *           the page size
	 * @param sortCode
	 *           the sort code
	 * @return the pageable data
	 */
	protected PageableData createPageableData(final int pageNumber, final int pageSize, final String sortCode)
	{
		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(pageNumber);
		pageableData.setSort(sortCode);
		pageableData.setPageSize(pageSize > 0 ? pageSize : MAX_PAGE_LIMIT);
		return pageableData;
	}

}
