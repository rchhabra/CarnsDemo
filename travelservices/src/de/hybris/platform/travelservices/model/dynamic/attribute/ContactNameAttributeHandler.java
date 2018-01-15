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

package de.hybris.platform.travelservices.model.dynamic.attribute;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;


public class ContactNameAttributeHandler extends AbstractDynamicAttributeHandler<String, AccommodationOrderEntryGroupModel>
{

	@Override
	public String get(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel)
	{
		return accommodationOrderEntryGroupModel.getFirstName() + " " + accommodationOrderEntryGroupModel.getLastName();
	}

	@Override
	public void set(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel, final String contactName)
	{
		// Override method to prevent exception while cloning the orderModel
	}

}
