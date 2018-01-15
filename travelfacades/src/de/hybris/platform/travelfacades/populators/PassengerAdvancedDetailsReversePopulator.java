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
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate the advanced information (CheckIn Details) PassengerInformationModel from the
 * PassengerInformationData
 */
public class PassengerAdvancedDetailsReversePopulator implements Populator<PassengerInformationData, PassengerInformationModel>
{
	private PassengerTypeService passengerTypeService;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final PassengerInformationData source, final PassengerInformationModel target) throws ConversionException
	{
		if (target.getPassengerType() == null
				|| !StringUtils.equalsIgnoreCase(target.getPassengerType().getCode(), source.getPassengerType().getCode()))
		{
			target.setPassengerType(getPassengerTypeService().getPassengerType(source.getPassengerType().getCode()));
		}

		target.setDateOfBirth(source.getDateOfBirth());
		target.setDocumentType(source.getDocumentType());
		target.setDocumentNumber(source.getDocumentNumber());
		target.setDocumentExpiryDate(source.getDocumentExpiryDate());

		if (source.getCountryOfIssue() != null && source.getCountryOfIssue().getIsocode() != null)
		{
			target.setCountryOfIssue(getCommonI18NService().getCountry(source.getCountryOfIssue().getIsocode()));
		}

		if (source.getNationality() != null && source.getNationality().getIsocode() != null)
		{
			target.setNationality(getCommonI18NService().getCountry(source.getNationality().getIsocode()));
		}

		target.setAPISType(source.getAPIType());
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passengerTypeService
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 * 		the passengerTypeService to set
	 */
	@Required
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the commonI18NService to set
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}
