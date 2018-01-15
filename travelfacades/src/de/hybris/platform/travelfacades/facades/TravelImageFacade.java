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

package de.hybris.platform.travelfacades.facades;

import de.hybris.platform.commercefacades.product.data.ImageData;


/**
 * This facade is responsible for providing image based on several parameters: destinationLocation, accommodationOfferingCode,
 * arrivalTransportFacility
 */
public interface TravelImageFacade
{
	/**
	 * Retrieves city location code from the destinationLocation parameter and then will get the image associated
	 * to the location of that city.
	 *
	 * @param destinationLocation
	 * 		code used in accommodation search built in following pattern:
	 * 		<district_code>|<city_code>|<country_code>, e.g. LON|GB or PAR|FR
	 * @return image image for destination location
	 */
	ImageData getImageForDestinationLocation(final String destinationLocation);

	/**
	 * Retrieves the image of a city location associated to given accommodation offering.
	 *
	 * @param accommodationOfferingCode
	 * 		- accommodation offering code
	 * @return image image for accommodation offering location
	 */
	ImageData getImageForAccommodationOfferingLocation(final String accommodationOfferingCode);

	/**
	 * Retrieves the image of a city location associated to given transport facility.
	 *
	 * @param arrivalTransportFacility
	 * 		- arrival transport facility code
	 * @return image image for arrival transport facility
	 */
	ImageData getImageForArrivalTransportFacility(final String arrivalTransportFacility);


	/**
	 * Retrieves the image based on transportation or accommodation selection that is currently in the cart
	 *
	 * @return image from cart
	 */
	ImageData getImageFromCart();
}
