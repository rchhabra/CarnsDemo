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
 */

package de.hybris.platform.travelacceleratorstorefront.strategies.asm;

import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.strategies.TravellerSortStrategy;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Redirect strategy for accommodation only journey responsible for building the required url parameters and redirect to the
 * accommodation details page.
 */
public class ASRedirectForAccommodationOnlyStrategy extends AbstractASRedirectStrategy
		implements AssistedServiceRedirectByJourneyTypeStrategy
{
	private static final String ACCOMMODATION_DETAILS_ROOT_URL = "/accommodation-details";
	private static final String DEFAULT_CART_REDIRECT = "/";

	private TravellerSortStrategy travellerSortStrategy;
	private PassengerTypeFacade passengerTypeFacade;

	@Override
	public String getRedirectPath(final CartModel cartModel)
	{
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
				TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);

		final String urlParameters = buildUrlParameters(cartModel);
		return StringUtils.isBlank(urlParameters) ? DEFAULT_CART_REDIRECT : ACCOMMODATION_DETAILS_ROOT_URL + urlParameters;
	}

	/**
	 * Returns the string with the url parameters required for the redirect to the accommodation-details page.
	 *
	 * @param cartModel
	 * 		as the cart model
	 *
	 * @return the string of the url parameters
	 */
	protected String buildUrlParameters(final CartModel cartModel)
	{
		final Map<String, String> urlParameters = new HashMap<>();

		final List<AbstractOrderEntryModel> accommodationEntries = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(accommodationEntries))
		{
			return null;
		}

		final List<AccommodationOrderEntryGroupModel> accommodationEntryGroups = accommodationEntries.stream()
				.map(AbstractOrderEntryModel::getEntryGroup).filter(AccommodationOrderEntryGroupModel.class::isInstance)
				.map(AccommodationOrderEntryGroupModel.class::cast).distinct().collect(Collectors.toList());

		final String destinationLocation = accommodationEntryGroups.get(0).getAccommodationOffering().getCode();

		urlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKIN_DATE, TravelDateUtils
				.convertDateToStringDate(accommodationEntryGroups.get(0).getStartingDate(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
		urlParameters.put(TravelacceleratorstorefrontWebConstants.CHECKOUT_DATE, TravelDateUtils
				.convertDateToStringDate(accommodationEntryGroups.get(0).getEndingDate(),
						TravelacceleratorstorefrontWebConstants.DATE_FORMAT));
		final long numberOfRooms = accommodationEntryGroups.stream().map(AccommodationOrderEntryGroupModel::getRoomStayRefNumber)
				.distinct().count();
		urlParameters.put(TravelacceleratorstorefrontWebConstants.NUMBER_OF_ROOMS, String.valueOf(numberOfRooms));

		IntStream.range(0, (int) numberOfRooms).forEach(roomNum ->
		{
			final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = accommodationEntryGroups.stream()
					.filter(entryGroup -> entryGroup.getRoomStayRefNumber() == roomNum).findAny().get();

			final StringBuilder guestsCountsStringBuilder = new StringBuilder();
			if (CollectionUtils.isNotEmpty(accommodationOrderEntryGroup.getGuestCounts()))
			{
				// Get guest counts from cart
				accommodationOrderEntryGroup.getGuestCounts().forEach(guestCountModel ->
				{
					guestsCountsStringBuilder.append(guestCountModel.getQuantity());
					guestsCountsStringBuilder.append(TravelacceleratorstorefrontWebConstants.HYPHEN);
					guestsCountsStringBuilder.append(guestCountModel.getPassengerType().getCode());
					guestsCountsStringBuilder.append(TravelacceleratorstorefrontWebConstants.COMMA);
				});
			}
			else
			{
				// Get default guest counts
				final List<PassengerTypeQuantityData> passengerTypeQuantityList = getDefaultPassengerTypeQuantityList();
				passengerTypeQuantityList.forEach(passengerTypeQuantityData ->
				{
					guestsCountsStringBuilder.append(passengerTypeQuantityData.getQuantity());
					guestsCountsStringBuilder.append(TravelacceleratorstorefrontWebConstants.HYPHEN);
					guestsCountsStringBuilder.append(passengerTypeQuantityData.getPassengerType().getCode());
					guestsCountsStringBuilder.append(TravelacceleratorstorefrontWebConstants.COMMA);
				});
			}

			String guestCountString = guestsCountsStringBuilder.toString();
			guestCountString = guestCountString.substring(0, guestCountString.length() - 1);
			urlParameters.put(TravelacceleratorstorefrontWebConstants.ROOM_QUERY_STRING_INDICATOR + roomNum, guestCountString);
		});

		return "/" + destinationLocation + "?" + urlParameters.toString().replace(", ", "&").replace("{", "").replace("}", "");
	}

	protected List<PassengerTypeQuantityData> getDefaultPassengerTypeQuantityList()
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantityList = new ArrayList<>();
		final List<PassengerTypeData> sortedPassengerTypes = getTravellerSortStrategy()
				.sortPassengerTypes(getPassengerTypeFacade().getPassengerTypes());
		for (final PassengerTypeData passengerTypeData : sortedPassengerTypes)
		{
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(passengerTypeData);
			if (passengerTypeData.getCode().equals(TravelfacadesConstants.PASSENGER_TYPE_CODE_ADULT))
			{
				passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_ADULT_QUANTITY);
			}
			else
			{
				passengerTypeQuantityData.setQuantity(TravelfacadesConstants.DEFAULT_GUEST_QUANTITY);
			}
			passengerTypeQuantityList.add(passengerTypeQuantityData);
		}
		return passengerTypeQuantityList;
	}

	/**
	 * @return the travellerSortStrategy
	 */
	protected TravellerSortStrategy getTravellerSortStrategy()
	{
		return travellerSortStrategy;
	}

	/**
	 * @param travellerSortStrategy
	 * 		the travellerSortStrategy to set
	 */
	@Required
	public void setTravellerSortStrategy(final TravellerSortStrategy travellerSortStrategy)
	{
		this.travellerSortStrategy = travellerSortStrategy;
	}

	/**
	 * @return the passengerTypeFacade
	 */
	protected PassengerTypeFacade getPassengerTypeFacade()
	{
		return passengerTypeFacade;
	}

	/**
	 * @param passengerTypeFacade
	 * 		the passengerTypeFacade to set
	 */
	@Required
	public void setPassengerTypeFacade(final PassengerTypeFacade passengerTypeFacade)
	{
		this.passengerTypeFacade = passengerTypeFacade;
	}
}
