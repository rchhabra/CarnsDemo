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
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import org.springframework.util.StringUtils;



/**
 * Attribute Handler implementation responsible to populate the SimpleUID property of the {@link TravellerModel}. The passenger
 * UID is build of [order_code](optional)_[passenger_number]_[random_uid]. To create a unique 6 characters identifier inside a
 * single reservation the simpleUID is created concatenating the passenger_number and random_uid removing the "_". If the UID is
 * empty, null or cannot be split using the "_" an empty string is returned. If the UID is composed of 3 parts (contains the
 * order_code) the first one is removed to have a unique string inside the order.
 */
public class SimpleUIDAttributeHandler extends AbstractDynamicAttributeHandler<String, TravellerModel>
{
	final static int UID_LENGTH = 6;
	final static int PASSENGER_NUMBER = 1;
	final static int RANDOM_STRING = 2;
	final static int FULL_LENGTH_UID = 3;
	final static String DELIMITER = "_";
	final static int NAME_MAXIMUM_CHARS = 4;

	@Override
	public String get(final TravellerModel travellerModel)
	{
		if (StringUtils.isEmpty(travellerModel.getUid()))
		{
			return "";
		}
		final String travellerName = StringUtils
				.trimAllWhitespace(PassengerInformationModel.class.cast(travellerModel.getInfo()).getFirstName()).toLowerCase();

		final String[] uidParts = travellerModel.getUid().split(DELIMITER);
		final StringBuilder builder = new StringBuilder("");
		builder.append(travellerName
				.substring(0, travellerName.length() > NAME_MAXIMUM_CHARS ? NAME_MAXIMUM_CHARS : travellerName.length()))
				.append(DELIMITER);
		builder.append((uidParts.length == FULL_LENGTH_UID) ? uidParts[PASSENGER_NUMBER] + uidParts[RANDOM_STRING]
				.substring(0, UID_LENGTH).toUpperCase() :
		travellerModel.getUid().replace(DELIMITER, "").substring(0, UID_LENGTH).toUpperCase());

		return builder.toString();
	}
}
