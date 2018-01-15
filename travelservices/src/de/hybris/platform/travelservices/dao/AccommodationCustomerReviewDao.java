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

package de.hybris.platform.travelservices.dao;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.Dao;

import java.util.List;


/**
 * Interface for dao handling accommodation customer reviews
 */
public interface AccommodationCustomerReviewDao extends Dao
{
	/**
	 * Retrieves a customer review added against a booking and a certain accommodation belonging to a room stay
	 *
	 * @param bookingReference
	 * @param roomStayRefNumber
	 * @param user
	 * @param accommodationCode
	 * @return
	 */
	List<CustomerReviewModel> getCustomerReviewsForBooking(String bookingReference, UserModel user,
			String accommodationCode) throws ModelNotFoundException;
}
