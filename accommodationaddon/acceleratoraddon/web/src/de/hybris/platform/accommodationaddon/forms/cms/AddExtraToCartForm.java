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

package de.hybris.platform.accommodationaddon.forms.cms;

import de.hybris.platform.validation.annotations.NotEmpty;

import javax.validation.constraints.NotNull;


/**
 * The AddExtraToCartForm is used for adding extra services to cart on the Guest Details page
 */
public class AddExtraToCartForm
{

	@NotEmpty
	private String productCode;
	@NotNull
	private int roomStayReferenceNumber;
	@NotNull
	private long quantity;

	public String getProductCode()
	{
		return productCode;
	}

	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

	public int getRoomStayReferenceNumber()
	{
		return roomStayReferenceNumber;
	}

	public void setRoomStayReferenceNumber(final int roomStayReferenceNumber)
	{
		this.roomStayReferenceNumber = roomStayReferenceNumber;
	}

	public long getQuantity()
	{
		return quantity;
	}

	public void setQuantity(final long quantity)
	{
		this.quantity = quantity;
	}

}
