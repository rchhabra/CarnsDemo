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

package de.hybris.platform.travelservices.services;

import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.util.List;


/**
 * Accommodation Service interface which provides functionality to manage Accommodation Offering.
 */
public interface AccommodationOfferingCustomerReviewService
{

	/**
	 * Returns a list of AccommodationModel for a given AccommodationOfferingCode
	 *
	 * @param accommodationOfferingCode
	 * 		the code of the accommodationOffering to use to get the list of AccommodationModel
	 * @param pageableData
	 * 		the pageable data
	 * @return the SearchPageData having list of CustomerReview corresponding to the AccommodationOffering
	 */
	SearchPageData<CustomerReviewModel> getReviewForAccommodationOffering(String accommodationOfferingCode,
			PageableData pageableData);

	/**
	 * Retrieve a list of customer reviews according with given criteria
	 *
	 * @param bookingReference
	 * @param accommodationOfferingCode
	 * @param roomStayRefNumber
	 * @param accommodationCode
	 * @return a list of ReviewData if a customer review matching criteria has been retrieved, null otherwise.
	 * @throws ModelNotFoundException
	 */
	List<CustomerReviewModel> retrieveCustomerReviewsForBooking(String bookingReference, String accommodationOfferingCode)
			throws ModelNotFoundException;


	/**
	 * Retrieve a customer review according with given criteria
	 *
	 * @param bookingReference
	 * @param accommodationOfferingCode
	 * @param roomStayRefNumber
	 * @param accommodationCode
	 * @return a ReviewData if a customer review matching criteria has been retrieved, null otherwise.
	 * @throws ModelNotFoundException
	 */
	CustomerReviewModel retrieveCustomerReviewForRoomStay(String bookingReference, String accommodationOfferingCode,
			Integer roomStayRefNumber)
			throws ModelNotFoundException;

	/**
	 * Creates and saves a review against the given accommodation offering
	 * 
	 * @param customerReview
	 * @param headline
	 * @param comment
	 * @param rating
	 * @param productCode
	 * @param accommodationOfferingCode
	 * @throws ModelSavingException
	 */
	void saveReview(CustomerReviewModel customerReview) throws ModelSavingException;
}
