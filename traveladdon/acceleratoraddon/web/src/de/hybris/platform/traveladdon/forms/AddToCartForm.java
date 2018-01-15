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

package de.hybris.platform.traveladdon.forms;

import java.util.List;

import javax.validation.constraints.NotNull;


/**
 * The AddToCartForm is used for adding non-fare product from the ancillary page.
 */
public class AddToCartForm
{
	@NotNull
	private String productCode;
	private String travellerCode;
	@NotNull
	private String travelRouteCode;
	private List<String> transportOfferingCodes;
	@NotNull
	private int originDestinationRefNumber;
	@NotNull
	private long qty;

	/**
	 * @return the productCode
	 */
	public String getProductCode()
	{
		return productCode;
	}

	/**
	 * @param productCode
	 *           the productCode to set
	 */
	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

	/**
	 * @return the travellerCode
	 */
	public String getTravellerCode()
	{
		return travellerCode;
	}

	/**
	 * @param travellerCode
	 *           the travellerCode to set
	 */
	public void setTravellerCode(final String travellerCode)
	{
		this.travellerCode = travellerCode;
	}
	/**
	 * @return the qty
	 */
	public long getQty()
	{
		return qty;
	}

	/**
	 * @param qty
	 *           the qty to set
	 */
	public void setQty(final long qty)
	{
		this.qty = qty;
	}

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
	 * @return the transportOfferingCodes
	 */
	public List<String> getTransportOfferingCodes()
	{
		return transportOfferingCodes;
	}

	/**
	 * @param transportOfferingCodes
	 *           the transportOfferingCodes to set
	 */
	public void setTransportOfferingCodes(final List<String> transportOfferingCodes)
	{
		this.transportOfferingCodes = transportOfferingCodes;
	}

	/**
	 * @return the originDestinationRefNumber
	 */
	public int getOriginDestinationRefNumber()
	{
		return originDestinationRefNumber;
	}

	/**
	 * @param originDestinationRefNumber
	 *           the originDestinationRefNumber to set
	 */
	public void setOriginDestinationRefNumber(final int originDestinationRefNumber)
	{
		this.originDestinationRefNumber = originDestinationRefNumber;
	}
}
