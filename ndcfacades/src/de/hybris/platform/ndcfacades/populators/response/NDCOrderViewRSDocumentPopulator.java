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
package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ndcfacades.ndc.OrderViewRS;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.converters.Populator;


/**
 * NDC Msg Document Type Populator
 */
public class NDCOrderViewRSDocumentPopulator extends NDCMsgDocumentTypePopulator
		implements Populator<OrderModel, OrderViewRS>
{
	@Override
	public void populate(final OrderModel orderModel, final OrderViewRS orderViewRS) throws ConversionException
	{
		orderViewRS.setDocument(getMsgDocumentType());
	}
}
