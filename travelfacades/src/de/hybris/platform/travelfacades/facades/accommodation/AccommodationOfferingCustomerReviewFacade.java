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

package de.hybris.platform.travelfacades.facades.accommodation;

import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;


/**
 * Interface to get Customer Reviews for Accommodation Offerings
 */
public interface AccommodationOfferingCustomerReviewFacade
{

	/**
	 * Gets accommodation offering customer review details.
	 *
	 * @param accommodationOfferingCode
	 * 		the accommodation offering code
	 * @param pageableData
	 * 		the pageable data
	 * @return the accommodation offering customer review details
	 */
	SearchPageData<ReviewData> getAccommodationOfferingCustomerReviewDetails(String accommodationOfferingCode,
			PageableData pageableData);

	/**
	 * Retrieve a customer review for a specific accommodation belonging to a booking
	 *
	 * @param bookingReference
	 * @param roomStayRefNumber
	 * @param accommodationOfferingCode 
	 * @param accommodationCode
	 * @return a ReviewData if a customer review matching criteria has been retrieved, null otherwise.
	 */
	ReviewData retrieveCustomerReviewByRefNumber(String bookingReference, Integer roomStayRefNumber,
			String accommodationOfferingCode);

	/**
	 * Retrieve customer reviews belonging to a booking
	 *
	 * @param bookingReference
	 * @param accommodationOfferingCode 
	 * @param roomStayRefNumber
	 * @param accommodationCode
	 * @return a ReviewData if a customer review matching criteria has been retrieved, null otherwise.
	 */
	List<ReviewData> retrieveCustomerReviewByBooking(String bookingReference, String accommodationOfferingCode);

	/**
	 * Save the review against the accommodation offering, the accommodation and the current user
	 *
	 * @param reviewData
	 * @return false if some error occurs
	 */
	boolean postReview(ReviewData reviewData);
}
