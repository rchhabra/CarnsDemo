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
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator to populate the TravellerData from the TravellerModel
 */
public class TravellerDataPopulator implements Populator<TravellerModel, TravellerData>
{
	private Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter;
	private Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter;

	@Override
	public void populate(final TravellerModel source, final TravellerData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setLabel(source.getLabel());
		target.setUid(source.getUid());

		if (source.getBooker() == null)
		{
			target.setBooker(false);
		}
		else
		{
			target.setBooker(source.getBooker());
		}

		if (null != source.getType() && null != source.getType().getCode())
		{
			target.setTravellerType(source.getType().getCode());

			if (source.getType().getCode().equalsIgnoreCase(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER))
			{
				target.setTravellerInfo(getPassengerInformationDataConverter().convert((PassengerInformationModel) source.getInfo()));
			}
		}

		if (source.getSpecialRequestDetail() != null)
		{
			target.setSpecialRequestDetail(getSpecialRequestDetailsConverter().convert(source.getSpecialRequestDetail()));
		}

		if (source.getSavedTravellerUid() != null)
		{
			target.setSavedTravellerUid(source.getSavedTravellerUid());
		}

		if (source.getVersionID() != null)
		{
			target.setVersionID(source.getVersionID());
		}

		if (StringUtils.isNotBlank(PassengerInformationModel.class.cast(source.getInfo()).getFirstName()))
		{
			if (Objects.nonNull(source.getSimpleUID()))
			{
				target.setSimpleUID(source.getSimpleUID());
			}
		}

	}

	/**
	 * Gets passenger information data converter.
	 *
	 * @return the passengerInformationDataConverter
	 */
	protected Converter<PassengerInformationModel, PassengerInformationData> getPassengerInformationDataConverter()
	{
		return passengerInformationDataConverter;
	}

	/**
	 * Sets passenger information data converter.
	 *
	 * @param passengerInformationDataConverter
	 * 		the passengerInformationDataConverter to set
	 */
	@Required
	public void setPassengerInformationDataConverter(
			final Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter)
	{
		this.passengerInformationDataConverter = passengerInformationDataConverter;
	}

	/**
	 * Gets special request details converter.
	 *
	 * @return the special request details converter
	 */
	protected Converter<SpecialRequestDetailModel, SpecialRequestDetailData> getSpecialRequestDetailsConverter()
	{
		return specialRequestDetailsConverter;
	}

	/**
	 * Sets special request details converter.
	 *
	 * @param specialRequestDetailsConverter
	 * 		the special request details converter
	 */
	@Required
	public void setSpecialRequestDetailsConverter(
			final Converter<SpecialRequestDetailModel, SpecialRequestDetailData> specialRequestDetailsConverter)
	{
		this.specialRequestDetailsConverter = specialRequestDetailsConverter;
	}

}
