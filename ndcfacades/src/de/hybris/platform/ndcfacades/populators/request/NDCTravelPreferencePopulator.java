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
package de.hybris.platform.ndcfacades.populators.request;

import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.TransportOfferingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.AirShoppingRQ;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelservices.services.CabinClassService;

import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The NDC Travel Preference Populator for {@link AirShoppingRQ}
 * Create TravelPreferences setting the cabinPreference and TransportOfferingType
 */
public class NDCTravelPreferencePopulator implements Populator<AirShoppingRQ, FareSearchRequestData>
{

	private ConfigurationService configurationService;
	private CabinClassService cabinClassService;

	@Override
	public void populate(final AirShoppingRQ airShoppingRQ, final FareSearchRequestData fareSearchRequestData)
	{
		final TravelPreferencesData travelPreferences = new TravelPreferencesData();

		populateCabinPreference(travelPreferences, airShoppingRQ);
		populateAirlinePreferences(fareSearchRequestData, airShoppingRQ);

		final TransportOfferingPreferencesData transportOfferingPreferencesData = new TransportOfferingPreferencesData();
		transportOfferingPreferencesData.setTransportOfferingType(TransportOfferingType.DIRECT);

		travelPreferences.setTransportOfferingPreferences(transportOfferingPreferencesData);
		fareSearchRequestData.setTravelPreferences(travelPreferences);
	}

	/**
	 * Populate airline preferences.
	 *
	 * @param fareSearchRequestData
	 * 		the fare search request data
	 * @param airShoppingRQ
	 * 		the air shopping rq
	 */
	protected void populateAirlinePreferences(final FareSearchRequestData fareSearchRequestData, final AirShoppingRQ airShoppingRQ)
	{
		if (Objects.nonNull(airShoppingRQ.getPreference()) && Objects.nonNull(airShoppingRQ.getPreference().getAirlinePreferences())
				&& Objects.nonNull(airShoppingRQ.getPreference().getAirlinePreferences().getAirline()))
		{
			if (CollectionUtils.isNotEmpty(airShoppingRQ.getPreference().getAirlinePreferences().getAirline())
					&& Objects.nonNull(airShoppingRQ.getPreference().getAirlinePreferences().getAirline().get(0).getAirlineID()))
			{
   			if (Objects.isNull(fareSearchRequestData.getTransportOfferingInfo()))
   			{
   				final TransportOfferingInfoData transportOfferingInfo = new TransportOfferingInfoData();
   				transportOfferingInfo.setTravelProvider(
   						airShoppingRQ.getPreference().getAirlinePreferences().getAirline().get(0).getAirlineID().getValue());
   				fareSearchRequestData.setTransportOfferingInfo(transportOfferingInfo);
   			}
   			else
   			{
   				fareSearchRequestData.getTransportOfferingInfo().setTravelProvider(
   						airShoppingRQ.getPreference().getAirlinePreferences().getAirline().get(0).getAirlineID().getValue());
   			}
			}
		}
	}

	/**
	 * Populate cabin preference.
	 *
	 * @param travelPreferences
	 * 		the travel preferences
	 * @param airShoppingRQ
	 * 		the air shopping rq
	 */
	protected void populateCabinPreference(final TravelPreferencesData travelPreferences, final AirShoppingRQ airShoppingRQ)
	{
		if (Objects.nonNull(airShoppingRQ.getPreference()) && Objects.nonNull(airShoppingRQ.getPreference().getCabinPreferences())
				&& Objects.nonNull(airShoppingRQ.getPreference().getCabinPreferences().getCabinType()))
		{
			if (CollectionUtils.isNotEmpty(airShoppingRQ.getPreference().getCabinPreferences().getCabinType())
					&& Objects.nonNull(airShoppingRQ.getPreference().getCabinPreferences().getCabinType().get(0).getCode()))
			{

				if (Objects.isNull(getCabinClassService()
						.getCabinClass(airShoppingRQ.getPreference().getCabinPreferences().getCabinType().get(0).getCode())))
				{
					throw new ConversionException(
							getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_CABIN_CODE));
				}

				travelPreferences
						.setCabinPreference(airShoppingRQ.getPreference().getCabinPreferences().getCabinType().get(0).getCode());
			}
		}
		else
		{
			travelPreferences
					.setCabinPreference(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.CABIN_CLASS));
		}
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets cabin class service.
	 *
	 * @return the cabin class service
	 */
	protected CabinClassService getCabinClassService()
	{
		return cabinClassService;
	}

	/**
	 * Sets cabin class service.
	 *
	 * @param cabinClassService
	 * 		the cabin class service
	 */
	public void setCabinClassService(final CabinClassService cabinClassService)
	{
		this.cabinClassService = cabinClassService;
	}
}
