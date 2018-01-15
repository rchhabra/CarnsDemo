/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 */

package de.hybris.platform.travelfacades.facades.accommodation.impl;

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingCustomerReviewFacade;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link AccommodationOfferingCustomerReviewFacade}
 */
public class DefaultAccommodationOfferingCustomerReviewFacade implements AccommodationOfferingCustomerReviewFacade
{
	private AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService;
	private Converter<CustomerReviewModel, ReviewData> accommodationOfferingCustomerReviewConverter;
	private Converter<ReviewData, CustomerReviewModel> accommodationCustomerReviewReverseConverter;

	private static final Logger LOG = Logger.getLogger(DefaultAccommodationOfferingCustomerReviewFacade.class);

	@Override

	public SearchPageData<ReviewData> getAccommodationOfferingCustomerReviewDetails(final String accommodationOfferingCode,
			final PageableData pageableData)
	{
		return convertPageData(getAccommodationOfferingCustomerReviewService().getReviewForAccommodationOffering(
				accommodationOfferingCode, pageableData), getAccommodationOfferingCustomerReviewConverter());
	}



	protected <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		final SearchPageData<T> result = new SearchPageData<T>();
		result.setPagination(source.getPagination());
		result.setSorts(source.getSorts());
		result.setResults(Converters.convertAll(source.getResults(), converter));
		return result;
	}



	@Override
	public List<ReviewData> retrieveCustomerReviewByBooking(final String bookingReference, final String accommodationOfferingCode)
	{
		try
		{
			final List<CustomerReviewModel> reviewList = getAccommodationOfferingCustomerReviewService()
					.retrieveCustomerReviewsForBooking(bookingReference, accommodationOfferingCode);
			return getAccommodationOfferingCustomerReviewConverter().convertAll(reviewList);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.debug(String.format("No review found for booking %s", bookingReference), ex);
			return Collections.emptyList();
		}
	}

	@Override
	public ReviewData retrieveCustomerReviewByRefNumber(final String bookingReference, final Integer roomStayRefNumber,
			final String accommodationOfferingCode)
	{
		try
		{
			final CustomerReviewModel review = getAccommodationOfferingCustomerReviewService()
					.retrieveCustomerReviewForRoomStay(bookingReference, accommodationOfferingCode, roomStayRefNumber);
			return getAccommodationOfferingCustomerReviewConverter().convert(review);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.debug(String.format("No review found for booking %s", bookingReference), ex);
			return null;
		}

	}


	@Override
	public boolean postReview(final ReviewData reviewData)
	{
		try{

			getAccommodationOfferingCustomerReviewService()
					.saveReview(getAccommodationCustomerReviewReverseConverter().convert(reviewData));
		}
		catch (final ModelNotFoundException | ModelSavingException modEx)
		{
			LOG.error("Failed to save the submitted review", modEx);
			return false;
		}
		return true;
	}

	/**
	 * @return the accommodationOfferingCustomerReviewService
	 */
	protected AccommodationOfferingCustomerReviewService getAccommodationOfferingCustomerReviewService()
	{
		return accommodationOfferingCustomerReviewService;
	}

	/**
	 * @param accommodationOfferingCustomerReviewService
	 *           the accommodationOfferingCustomerReviewService to set
	 */
	@Required
	public void setAccommodationOfferingCustomerReviewService(
			final AccommodationOfferingCustomerReviewService accommodationOfferingCustomerReviewService)
	{
		this.accommodationOfferingCustomerReviewService = accommodationOfferingCustomerReviewService;
	}

	/**
	 * @return the accommodationOfferingCustomerReviewConverter
	 */
	protected Converter<CustomerReviewModel, ReviewData> getAccommodationOfferingCustomerReviewConverter()
	{
		return accommodationOfferingCustomerReviewConverter;
	}

	/**
	 * @param accommodationOfferingCustomerReviewConverter
	 *           the accommodationOfferingCustomerReviewConverter to set
	 */
	@Required
	public void setAccommodationOfferingCustomerReviewConverter(
			final Converter<CustomerReviewModel, ReviewData> accommodationOfferingCustomerReviewConverter)
	{
		this.accommodationOfferingCustomerReviewConverter = accommodationOfferingCustomerReviewConverter;
	}

	/**
	 *
	 * @return the accommodationCustomerReviewReverseConverter
	 */
	protected Converter<ReviewData, CustomerReviewModel> getAccommodationCustomerReviewReverseConverter()
	{
		return accommodationCustomerReviewReverseConverter;
	}


	/**
	 *
	 * @param accommodationCustomerReviewReverseConverter
	 *           the accommodationCustomerReviewReverseConverter to set
	 */
	@Required
	public void setAccommodationCustomerReviewReverseConverter(
			final Converter<ReviewData, CustomerReviewModel> accommodationCustomerReviewReverseConverter)
	{
		this.accommodationCustomerReviewReverseConverter = accommodationCustomerReviewReverseConverter;
	}



}
