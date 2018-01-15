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

package de.hybris.platform.travelfacades.populators;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.enums.DocumentType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;


/**
 * Populator to populate the PassengerInformationData from the PassengerInformationModel
 */
public class PassengerInformationDataPopulator implements Populator<PassengerInformationModel, PassengerInformationData>
{
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;
	private Converter<TitleModel, TitleData> titleConverter;
	private Converter<CountryModel, CountryData> countryConverter;

	@Override
	public void populate(final PassengerInformationModel source, final PassengerInformationData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setPassengerType(getPassengerTypeConverter().convert(source.getPassengerType()));
		target.setTitle(source.getTitle() == null ? null : getTitleConverter().convert(source.getTitle()));
		target.setFirstName(source.getFirstName());
		target.setSurname(source.getSurname());
		target.setDateOfBirth(source.getDateOfBirth());
		target.setMembershipNumber(source.getMembershipNumber());
		target.setGender(source.getGender());
		target.setReasonForTravel(source.getReasonForTravel() != null ? source.getReasonForTravel().getCode() : null);
		target.setDateOfBirth(source.getDateOfBirth());
		target.setDocumentType((source.getDocumentType() == null) ? null : DocumentType.valueOf(source.getDocumentType().name()));
		target.setDocumentNumber((source.getDocumentNumber() == null) ? StringUtils.EMPTY : source.getDocumentNumber());
		target.setAPIType((source.getAPISType() == null) ? StringUtils.EMPTY : source.getAPISType());
		target.setCountryOfIssue(
				source.getCountryOfIssue() == null ? null : getCountryConverter().convert(source.getCountryOfIssue()));
		target.setDocumentExpiryDate(source.getDocumentExpiryDate());
		target.setNationality(source.getNationality() == null ? null : getCountryConverter().convert(source.getNationality()));
		target.setEmail((source.getEmail() == null) ? StringUtils.EMPTY : source.getEmail());
	}

	/**
	 * Gets title converter.
	 *
	 * @return the titleConverter
	 */
	protected Converter<TitleModel, TitleData> getTitleConverter()
	{
		return titleConverter;
	}

	/**
	 * Sets title converter.
	 *
	 * @param titleConverter
	 * 		the titleConverter to set
	 */
	public void setTitleConverter(final Converter<TitleModel, TitleData> titleConverter)
	{
		this.titleConverter = titleConverter;
	}

	/**
	 * Gets passenger type converter.
	 *
	 * @return the passengerTypeConverter
	 */
	protected Converter<PassengerTypeModel, PassengerTypeData> getPassengerTypeConverter()
	{
		return passengerTypeConverter;
	}

	/**
	 * Sets passenger type converter.
	 *
	 * @param passengerTypeConverter
	 * 		the passengerTypeConverter to set
	 */
	public void setPassengerTypeConverter(final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}

	/**
	 * Gets country converter.
	 *
	 * @return the countryConverter
	 */
	protected Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	/**
	 * Sets country converter.
	 *
	 * @param countryConverter
	 * 		the countryConverter to set
	 */
	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}

}
