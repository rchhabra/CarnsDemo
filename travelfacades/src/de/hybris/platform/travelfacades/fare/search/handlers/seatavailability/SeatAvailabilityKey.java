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
package de.hybris.platform.travelfacades.fare.search.handlers.seatavailability;

import java.util.Arrays;
import java.util.List;


/**
 *
 * Object used as key for seat availability map.
 */
public class SeatAvailabilityKey
{
	private int pricedItineraryId;

	private String transportOfferingCode;

	private List<String> bundleTemplateCodes;

	public SeatAvailabilityKey()
	{
		// default constructor
	}

	public SeatAvailabilityKey(final int pricedItineraryId, final String toCode, final List<String> btCodes)
	{
		this.pricedItineraryId = pricedItineraryId;
		this.transportOfferingCode = toCode;
		this.bundleTemplateCodes = btCodes;
	}

	public void setPricedItineraryId(final int pricedItineraryId)
	{
		this.pricedItineraryId = pricedItineraryId;
	}



	public int getPricedItineraryId()
	{
		return pricedItineraryId;
	}



	public void setTransportOfferingCode(final String transportOfferingCode)
	{
		this.transportOfferingCode = transportOfferingCode;
	}



	public String getTransportOfferingCode()
	{
		return transportOfferingCode;
	}



	public void setBundleTemplateCodes(final List<String> bundleTemplateCodes)
	{
		this.bundleTemplateCodes = bundleTemplateCodes;
	}



	public List<String> getBundleTemplateCodes()
	{
		return bundleTemplateCodes;
	}

	@Override
	public int hashCode()
	{
		return this.pricedItineraryId + this.transportOfferingCode.hashCode() +
				this.bundleTemplateCodes.stream().mapToInt(String::hashCode).sum();
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (!(obj instanceof SeatAvailabilityKey))
			return false;

		if (obj == this)
			return true;

		SeatAvailabilityKey sak = (SeatAvailabilityKey) obj;

		if (pricedItineraryId != sak.getPricedItineraryId())
			return false;

		if (!transportOfferingCode.equals(sak.getTransportOfferingCode()))
			return false;

		final boolean flag1 = bundleTemplateCodes.stream().anyMatch(code -> !sak.getBundleTemplateCodes().contains(code));
		final boolean flag2 = sak.getBundleTemplateCodes().stream().anyMatch(code -> !this.getBundleTemplateCodes().contains(code));

		if (flag1 || flag2)
			return false;

		return true;
	}
}
