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
 */

package de.hybris.platform.travelservices.services.impl;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.dao.AccommodationCustomerReviewDao;
import de.hybris.platform.travelservices.dao.PagedAccommodationOfferingCustomerReviewDao;
import de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;



/**
 * Default implementation of
 * {@link de.hybris.platform.travelservices.services.AccommodationOfferingCustomerReviewService}
 */
public class DefaultAccommodationOfferingCustomerReviewService implements AccommodationOfferingCustomerReviewService
{
	private PagedAccommodationOfferingCustomerReviewDao pagedAccommodationOfferingCustomerReviewDao;
	private AccommodationCustomerReviewDao accommodationCustomerReviewDao;
	private UserService userService;
	private ModelService modelService;


	@Override
	public SearchPageData<CustomerReviewModel> getReviewForAccommodationOffering(final String accommodationOfferingCode,
			final PageableData pageableData)
	{
		return getPagedAccommodationOfferingCustomerReviewDao().findPagedCustomerReview(accommodationOfferingCode, pageableData);
	}

	@Override
	public List<CustomerReviewModel> retrieveCustomerReviewsForBooking(final String bookingReference,
			final String accommodationOfferingCode) throws ModelNotFoundException
	{
		return getAccommodationCustomerReviewDao().getCustomerReviewsForBooking(bookingReference, getUserService().getCurrentUser(),
				accommodationOfferingCode);
	}

	@Override
	public CustomerReviewModel retrieveCustomerReviewForRoomStay(final String bookingReference,
			final String accommodationOfferingCode, final Integer roomStayRefNumber) throws ModelNotFoundException
	{
		final Optional<CustomerReviewModel> optionalReview = getAccommodationCustomerReviewDao()
				.getCustomerReviewsForBooking(bookingReference, getUserService().getCurrentUser(), accommodationOfferingCode).stream()
				.filter(review -> review.getRoomStayRefNumber().equals(roomStayRefNumber)).findFirst();
		if (!optionalReview.isPresent())
		{
			throw new ModelNotFoundException("No result for the given query");
		}
		return optionalReview.get();
	}




	@Override
	public void saveReview(final CustomerReviewModel customerReview) throws ModelSavingException
	{
		getModelService().save(customerReview);
	}


	/**
	 * @return the pagedAccommodationOfferingCustomerReviewDao
	 */
	protected PagedAccommodationOfferingCustomerReviewDao getPagedAccommodationOfferingCustomerReviewDao()
	{
		return pagedAccommodationOfferingCustomerReviewDao;
	}

	/**
	 * @param pagedAccommodationOfferingCustomerReviewDao
	 *           the pagedAccommodationOfferingCustomerReviewDao to set
	 */
	@Required
	public void setPagedAccommodationOfferingCustomerReviewDao(
			final PagedAccommodationOfferingCustomerReviewDao pagedAccommodationOfferingCustomerReviewDao)
	{
		this.pagedAccommodationOfferingCustomerReviewDao = pagedAccommodationOfferingCustomerReviewDao;
	}

	/**
	 *
	 * @return accommodationCustomerReviewDao
	 */
	protected AccommodationCustomerReviewDao getAccommodationCustomerReviewDao()
	{
		return accommodationCustomerReviewDao;
	}

	/**
	 *
	 * @param accommodationCustomerReviewDao
	 *           the accommodationCustomerReviewDao to set
	 */
	@Required
	public void setAccommodationCustomerReviewDao(final AccommodationCustomerReviewDao accommodationCustomerReviewDao)
	{
		this.accommodationCustomerReviewDao = accommodationCustomerReviewDao;
	}

	/**
	 *
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 *
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 *
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
