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

package de.hybris.platform.travelrulesengine.converters.populator;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.travelrulesengine.rao.OfferRequestRAO;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeQuantityRAO;
import de.hybris.platform.travelrulesengine.rao.PassengerTypeRAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This class populates PassengerTypeQuantity information to OfferRequestRao
 */
public class OfferRequestRaoPassengerTypeQuantityPopulator implements Populator<OfferRequestData, OfferRequestRAO>
{

	private Converter<PassengerTypeData, PassengerTypeRAO> passengerTypeRaoConverter;

	@Override
	public void populate(final OfferRequestData source, final OfferRequestRAO target) throws ConversionException
	{
		final List<PassengerTypeQuantityRAO> passengerTypeQuantities = new ArrayList<>();
		final List<TravellerInfoData> travellerInfos = source.getItineraries().get(0).getTravellers().stream()
				.map(TravellerData::getTravellerInfo).collect(Collectors.toList());
		travellerInfos.forEach(traveller -> {
			Optional<PassengerTypeQuantityRAO> passengerTypeRao = passengerTypeQuantities.stream().filter(passenger -> passenger
					.getPassengerType().getCode().equals(((PassengerInformationData) traveller).getPassengerType().getCode())).findAny();
			if (passengerTypeRao.isPresent())
			{
				passengerTypeRao.get().setQuantity(passengerTypeRao.get().getQuantity() + 1);
			}
			else
			{
				passengerTypeQuantities.add(createPassengerTypeQuantityRAO(((PassengerInformationData) traveller)));
			}
		});
		target.setPassengerTypeQuantities(passengerTypeQuantities);
	}

	/**
	 * Create passenger type quantity rao.
	 *
	 * @param passengerTypeQuantityData
	 *           the passenger type quantity data
	 * @return the passenger type quantity rao
	 */
	private PassengerTypeQuantityRAO createPassengerTypeQuantityRAO(final PassengerInformationData passengerInformation)
	{
		final PassengerTypeQuantityRAO passengerTypeQuantityRAO = new PassengerTypeQuantityRAO();
		passengerTypeQuantityRAO.setQuantity(1);
		final PassengerTypeData passengerTye = (passengerInformation.getPassengerType());
		passengerTypeQuantityRAO
				.setPassengerType(getPassengerTypeRaoConverter().convert(passengerTye));

		return passengerTypeQuantityRAO;
	}

	/**
	 * Sets passenger type rao converter.
	 *
	 * @param passengerTypeRaoConverter
	 *           the passenger type rao converter
	 */
	@Required
	public void setPassengerTypeRaoConverter(final Converter<PassengerTypeData, PassengerTypeRAO> passengerTypeRaoConverter)
	{
		this.passengerTypeRaoConverter = passengerTypeRaoConverter;
	}

	/**
	 * Gets passenger type rao converter.
	 *
	 * @return the passenger type rao converter
	 */
	protected Converter<PassengerTypeData, PassengerTypeRAO> getPassengerTypeRaoConverter()
	{
		return passengerTypeRaoConverter;
	}

}
