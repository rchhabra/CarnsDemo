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

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.search.data.SavedSearchData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.strategies.DecodeSavedSearchStrategy;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.model.user.SavedSearchModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populator to populate the SavedSearchData from the SavedSearchModel
 */
public class SavedSearchPopulator implements Populator<SavedSearchModel, SavedSearchData>
{

	private static final String DEPARTINGDATETIME = "departingDateTime";
	private static final String RETURNDATETIME = "returnDateTime";
	private static final String DEPARTURELOCATION = "departureLocation";
	private static final String ARRIVALLOCATION = "arrivalLocation";
	private static final String DEPARTURELOCATIONNAME = "departureLocationName";
	private static final String ARRIVALLOCATIONNAME = "arrivalLocationName";
	private static final String CABINCLASS = "cabinClass";
	private static final String TRIPTYPE = "tripType";
	private static final String ARRIVALLOCATIONSUGGESTIONTYPE = "arrivalLocationSuggestionType";
	private static final String DEPARTURELOCATIONSUGGESTIONTYPE = "departureLocationSuggestionType";

	private PassengerTypeFacade passengerTypeFacade;
	private TravellerSortStrategy travellerSortStrategy;
	private DecodeSavedSearchStrategy decodeSavedSearchStrategy;

	@Override
	public void populate(final SavedSearchModel source, final SavedSearchData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		if (source.getEncodedSearch() != null)
		{
			final Map<String, String> encodedDataMap = getDecodeSavedSearchStrategy()
					.getEncodedDataMap(source.getEncodedSearch());

			target.setPassengerTypeQuantities(getPassengerTypeQuantityData(encodedDataMap));
			target.setArrivalLocation(encodedDataMap.get(ARRIVALLOCATION));
			target.setDepartureLocation(encodedDataMap.get(DEPARTURELOCATION));
			target.setArrivalLocationName(encodedDataMap.get(ARRIVALLOCATIONNAME));
			target.setDepartureLocationName(encodedDataMap.get(DEPARTURELOCATIONNAME));
			target.setReturnDateTime(encodedDataMap.get(RETURNDATETIME));
			target.setDepartingDateTime(encodedDataMap.get(DEPARTINGDATETIME));
			target.setCabinClass(encodedDataMap.get(CABINCLASS));
			target.setTripType(encodedDataMap.get(TRIPTYPE));
			target.setPK(source.getPk().toString());
			target.setArrivalLocationSuggestionType(encodedDataMap.get(ARRIVALLOCATIONSUGGESTIONTYPE));
			target.setDepartureLocationSuggestionType(encodedDataMap.get(DEPARTURELOCATIONSUGGESTIONTYPE));

		}
	}

	/**
	 * Gets passenger type quantity data.
	 *
	 * @param encodedDataMap
	 * 		the encoded data map
	 * @return the passenger type quantity data
	 */
	protected List<PassengerTypeQuantityData> getPassengerTypeQuantityData(final Map<String, String> encodedDataMap)
	{
		final List<PassengerTypeData> passengerTypeDataList = getTravellerSortStrategy()
				.sortPassengerTypes(getPassengerTypeFacade().getPassengerTypes());
		final List<PassengerTypeQuantityData> passengerTypeQuantityDataList = new ArrayList<>(passengerTypeDataList.size());

		for (final PassengerTypeData passengerTypeData : passengerTypeDataList)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			passengerTypeQuantityData.setQuantity(Integer.parseInt(encodedDataMap.get(passengerTypeData.getCode())));
			passengerTypeQuantityDataList.add(passengerTypeQuantityData);
		}

		return passengerTypeQuantityDataList;

	}

	/**
	 * Gets passenger type facade.
	 *
	 * @return the passenger type facade
	 */
	protected PassengerTypeFacade getPassengerTypeFacade()
	{
		return passengerTypeFacade;
	}

	/**
	 * Sets passenger type facade.
	 *
	 * @param passengerTypeFacade
	 * 		the passengerTypeFacade to set
	 */
	@Required
	public void setPassengerTypeFacade(final PassengerTypeFacade passengerTypeFacade)
	{
		this.passengerTypeFacade = passengerTypeFacade;
	}

	/**
	 * Gets traveller sort strategy.
	 *
	 * @return the traveller sort strategy
	 */
	protected TravellerSortStrategy getTravellerSortStrategy()
	{
		return travellerSortStrategy;
	}

	/**
	 * Sets traveller sort strategy.
	 *
	 * @param travellerSortStrategy
	 * 		the travellerSortStrategy to set
	 */
	@Required
	public void setTravellerSortStrategy(final TravellerSortStrategy travellerSortStrategy)
	{
		this.travellerSortStrategy = travellerSortStrategy;
	}

	/**
	 * Gets decode saved search strategy.
	 *
	 * @return the decode saved search strategy
	 */
	protected DecodeSavedSearchStrategy getDecodeSavedSearchStrategy()
	{
		return decodeSavedSearchStrategy;
	}

	/**
	 * Sets decode saved search strategy.
	 *
	 * @param decodeSavedSearchStrategy
	 * 		the decodeSavedSearchStrategy to set
	 */
	@Required
	public void setDecodeSavedSearchStrategy(final DecodeSavedSearchStrategy decodeSavedSearchStrategy)
	{
		this.decodeSavedSearchStrategy = decodeSavedSearchStrategy;
	}
}
