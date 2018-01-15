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

package de.hybris.platform.travelfacades.facades.accommodation.populators;

import de.hybris.platform.commercefacades.accommodation.CancelPenaltyData;
import de.hybris.platform.commercefacades.accommodation.GuaranteeData;
import de.hybris.platform.commercefacades.accommodation.GuestOccupancyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.RatePlanInclusionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelservices.model.accommodation.CancelPenaltyModel;
import de.hybris.platform.travelservices.model.accommodation.GuaranteeModel;
import de.hybris.platform.travelservices.model.accommodation.GuestOccupancyModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanInclusionModel;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class will populate the {@link RatePlanData} from the {@link RatePlanModel} with the basic information
 */
public class RatePlanBasicPopulator<SOURCE extends RatePlanModel, TARGET extends RatePlanData>
implements Populator<SOURCE, TARGET>
{
	private Converter<CancelPenaltyModel, CancelPenaltyData> cancelPenaltyConverter;
	private Converter<GuaranteeModel, GuaranteeData> guaranteeConverter;
	private Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter;
	private Converter<RatePlanInclusionModel, RatePlanInclusionData> ratePlanInclusionConverter;
	private EnumerationService enumerationService;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setMaxLengthOfStay(source.getMaxStay());
		target.setMinLengthOfStay(source.getMinStay());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setCancelPenalties(getCancelPenaltyConverter().convertAll(source.getCancelPenalty()));
		target.setGuarantees(getGuaranteeConverter().convertAll(source.getGuarantee()));
		target.setOccupancies(getGuestOccupancyConverter().convertAll(source.getGuestOccupancies()));
		target.setRatePlanInclusions(getRatePlanInclusionConverter().convertAll(source.getRatePlanInclusion()));
		target.setMealTypes(source.getMealType().stream().map(mealtype -> getEnumerationService().getEnumerationName(mealtype))
				.collect(Collectors.toList()));
	}

	/**
	 * @return the cancelPenaltyConverter
	 */
	protected Converter<CancelPenaltyModel, CancelPenaltyData> getCancelPenaltyConverter() {
		return cancelPenaltyConverter;
	}

	/**
	 * @param cancelPenaltyConverter
	 *           the cancelPenaltyConverter to set
	 */
	@Required
	public void setCancelPenaltyConverter(final Converter<CancelPenaltyModel, CancelPenaltyData> cancelPenaltyConverter) {
		this.cancelPenaltyConverter = cancelPenaltyConverter;
	}

	/**
	 * @return the guaranteeConverter
	 */
	protected Converter<GuaranteeModel, GuaranteeData> getGuaranteeConverter()
	{
		return guaranteeConverter;
	}

	/**
	 * @param guaranteeConverter
	 *           the guaranteeConverter to set
	 */
	@Required
	public void setGuaranteeConverter(final Converter<GuaranteeModel, GuaranteeData> guaranteeConverter)
	{
		this.guaranteeConverter = guaranteeConverter;
	}

	/**
	 * @return the guestOccupancyConverter
	 */
	protected Converter<GuestOccupancyModel, GuestOccupancyData> getGuestOccupancyConverter()
	{
		return guestOccupancyConverter;
	}

	/**
	 * @param guestOccupancyConverter
	 *           the guestOccupancyConverter to set
	 */
	@Required
	public void setGuestOccupancyConverter(final Converter<GuestOccupancyModel, GuestOccupancyData> guestOccupancyConverter)
	{
		this.guestOccupancyConverter = guestOccupancyConverter;
	}

	/**
	 * @return the ratePlanInclusionConverter
	 */
	protected Converter<RatePlanInclusionModel, RatePlanInclusionData> getRatePlanInclusionConverter()
	{
		return ratePlanInclusionConverter;
	}

	/**
	 * @param ratePlanInclusionConverter
	 *           the ratePlanInclusionConverter to set
	 */
	@Required
	public void setRatePlanInclusionConverter(
			final Converter<RatePlanInclusionModel, RatePlanInclusionData> ratePlanInclusionConverter)
	{
		this.ratePlanInclusionConverter = ratePlanInclusionConverter;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
