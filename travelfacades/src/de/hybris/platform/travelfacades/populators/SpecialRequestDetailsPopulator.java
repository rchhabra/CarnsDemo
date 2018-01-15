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

import de.hybris.platform.commercefacades.travel.RemarkData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.travel.RemarkModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate the SpecialRequestDetailData from the SpecialRequestDetailModel
 */
public class SpecialRequestDetailsPopulator implements Populator<SpecialRequestDetailModel, SpecialRequestDetailData>
{
	private Converter<SpecialServiceRequestModel, SpecialServiceRequestData> specialServiceRequestConverter;
	private Converter<RemarkModel, RemarkData> remarkConverter;

	@Override
	public void populate(final SpecialRequestDetailModel source, final SpecialRequestDetailData target) throws ConversionException
	{
		if (Objects.nonNull(source.getSpecialServiceRequest()))
		{
			final List<SpecialServiceRequestData> specialServiceRequests = Converters.convertAll(source.getSpecialServiceRequest(),
					getSpecialServiceRequestConverter());
			target.setSpecialServiceRequests(specialServiceRequests);
		}

		if (Objects.nonNull(source.getSpecialServiceRequest()))
		{
			final List<RemarkData> remarks = Converters.convertAll(source.getRemarks(), getRemarkConverter());
			target.setRemarks(remarks);
		}
	}

	/**
	 * Gets special service request converter.
	 *
	 * @return the specialServiceRequestConverter
	 */
	protected Converter<SpecialServiceRequestModel, SpecialServiceRequestData> getSpecialServiceRequestConverter()
	{
		return specialServiceRequestConverter;
	}

	/**
	 * Sets special service request converter.
	 *
	 * @param specialServiceRequestConverter
	 * 		the specialServiceRequestConverter to set
	 */
	@Required
	public void setSpecialServiceRequestConverter(
			final Converter<SpecialServiceRequestModel, SpecialServiceRequestData> specialServiceRequestConverter)
	{
		this.specialServiceRequestConverter = specialServiceRequestConverter;
	}

	/**
	 * Gets remark converter.
	 *
	 * @return the remarkConverter
	 */
	protected Converter<RemarkModel, RemarkData> getRemarkConverter()
	{
		return remarkConverter;
	}

	/**
	 * Sets remark converter.
	 *
	 * @param remarkConverter
	 * 		the remarkConverter to set
	 */
	@Required
	public void setRemarkConverter(final Converter<RemarkModel, RemarkData> remarkConverter)
	{
		this.remarkConverter = remarkConverter;
	}

}
