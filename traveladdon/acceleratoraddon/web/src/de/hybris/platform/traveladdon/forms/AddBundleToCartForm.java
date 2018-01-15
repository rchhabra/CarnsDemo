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

package de.hybris.platform.traveladdon.forms;

import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;

import java.util.List;


/**
 * AddBundleToCartForm object used to bind with the AddBundleToCartForm and uses JSR303 validation.
 */
public class AddBundleToCartForm
{
	private String travelRouteCode;
	private ItineraryPricingInfoData itineraryPricingInfo;
	private List<PassengerTypeQuantityData> passengerTypeQuantityList;
	private String originDestinationRefNumber;


	/**
	 * @return the travelRouteCode
	 */
	public String getTravelRouteCode()
	{
		return travelRouteCode;
	}

	/**
	 * @param travelRouteCode
	 *           the travelRouteCode to set
	 */
	public void setTravelRouteCode(final String travelRouteCode)
	{
		this.travelRouteCode = travelRouteCode;
	}

	/**
	 * @return the itineraryPricingInfo
	 */
	public ItineraryPricingInfoData getItineraryPricingInfo()
	{
		return itineraryPricingInfo;
	}

	/**
	 * @param itineraryPricingInfo
	 *           the itineraryPricingInfo to set
	 */
	public void setItineraryPricingInfo(final ItineraryPricingInfoData itineraryPricingInfo)
	{
		this.itineraryPricingInfo = itineraryPricingInfo;
	}

	/**
	 * @return the passengerTypeQuantityList
	 */
	public List<PassengerTypeQuantityData> getPassengerTypeQuantityList()
	{
		return passengerTypeQuantityList;
	}

	/**
	 * @param passengerTypeQuantityList
	 *           the passengerTypeQuantityList to set
	 */
	public void setPassengerTypeQuantityList(
			final List<PassengerTypeQuantityData> passengerTypeQuantityList)
	{
		this.passengerTypeQuantityList = passengerTypeQuantityList;
	}

	/**
	 * @return the originDestinationRefNumber
	 */
	public String getOriginDestinationRefNumber()
	{
		return originDestinationRefNumber;
	}

	/**
	 * @param originDestinationRefNumber
	 *           the originDestinationRefNumber to set
	 */
	public void setOriginDestinationRefNumber(final String originDestinationRefNumber)
	{
		this.originDestinationRefNumber = originDestinationRefNumber;
	}

}
