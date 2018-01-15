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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelservices.enums.ReasonForTravel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate basic details like First Name, Surname, Title etc PassengerInformationModel from the
 * PassengerInformationData
 */
public class PassengerBasicDetailsReversePopulator implements Populator<PassengerInformationData, PassengerInformationModel>
{
	private EnumerationService enumerationService;
	private UserService userService;

	@Override
	public void populate(final PassengerInformationData source, final PassengerInformationModel target) throws ConversionException
	{
		target.setFirstName(source.getFirstName());
		target.setSurname(source.getSurname());
		if (source.getTitle() != null && source.getTitle().getCode() != null)
		{
			target.setTitle(getUserService().getTitleForCode(source.getTitle().getCode()));
		}
		target.setGender(source.getGender());
		target.setMembershipNumber(source.getMembershipNumber());
		if (StringUtils.isNotEmpty(source.getReasonForTravel()))
		{
			target.setReasonForTravel(
					getEnumerationService().getEnumerationValue(ReasonForTravel.class, source.getReasonForTravel()));
		}
		target.setEmail(source.getEmail());
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 * 		the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 * Gets user service.
	 *
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the userService to set
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


}
