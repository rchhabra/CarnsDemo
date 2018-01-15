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

package de.hybris.platform.travelfacades.facades.packages.handlers.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchResponseData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.RatePlanData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationOfferingFacade;
import de.hybris.platform.travelfacades.facades.accommodation.search.AccommodationSearchFacade;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageReservedRoomStayHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of {@link PackageReservedRoomStayHandler} responsible for populating reserved room stays on
 * accommodation availability response based on the configuration provided by business. It makes a search in SOLR and
 * returns configured options for given room stay candidates.
 */
public class PackageConfiguredReservedRoomStaysHandler implements PackageReservedRoomStayHandler
{
	private static final Logger LOG = Logger.getLogger(PackageConfiguredReservedRoomStaysHandler.class);
	private static final int ROOM_COUNT = 1;

	private AccommodationSearchFacade accommodationSearchFacade;
	private AccommodationOfferingFacade accommodationOfferingFacade;
	private BookingFacade bookingFacade;

	@Override
	public void handle(final PackageRequestData packageRequestData,
			final AccommodationAvailabilityResponseData accommodationAvailabilityResponseData)
	{
		// This handler should only be executed if previous handler did not produce any reserved room stays
		if (CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays())
				&& accommodationAvailabilityResponseData.getReservedRoomStays().stream()
						.anyMatch(reservedRoomStay -> !reservedRoomStay.getNonModifiable()))
		{
			return;
		}

		final AccommodationSearchResponseData accommodationSearchResponse = getAccommodationSearchFacade()
				.doSearch(packageRequestData.getAccommodationPackageRequest().getAccommodationSearchRequest());

		if (CollectionUtils.isEmpty(accommodationSearchResponse.getProperties()))
		{
			return;
		}

		final List<String> ratePlanConfigs = accommodationSearchResponse.getProperties().get(0).getRatePlanConfigs();
		final List<RoomStayCandidateData> roomStayCandidates = packageRequestData.getAccommodationPackageRequest()
				.getAccommodationSearchRequest().getCriterion().getRoomStayCandidates();
		final int oldRoomStayRefNum = CollectionUtils.size(getBookingFacade().getOldAccommodationOrderEntryGroupRefs());
		// Rate plan configuration is only valid for a package if it offers exactly 1 room for each roomStayCandidate
		if (CollectionUtils.isNotEmpty(ratePlanConfigs) && CollectionUtils.isNotEmpty(roomStayCandidates)
				&& CollectionUtils.size(ratePlanConfigs) == CollectionUtils.size(roomStayCandidates))
		{
			if (checkForValidRatePlanConfig(ratePlanConfigs))
			{
				final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = createAccommodationAvailabilityRequestData(
						accommodationSearchResponse, oldRoomStayRefNum);

				final AccommodationAvailabilityResponseData configuredAccommodationAvailability = getAccommodationOfferingFacade()
						.getSelectedAccommodationOfferingDetails(accommodationAvailabilityRequestData);

				//check for rate plan availability.
				if (isRatePlanAvailable(configuredAccommodationAvailability))
				{
					final List<RoomStayData> roomStays = getAvailableRoomStays(configuredAccommodationAvailability.getRoomStays());
					final List<ReservedRoomStayData> packageReservedRoomStays = new ArrayList<>();

					if (CollectionUtils.isNotEmpty(accommodationAvailabilityResponseData.getReservedRoomStays()))
					{
						packageReservedRoomStays.addAll(accommodationAvailabilityResponseData.getReservedRoomStays());
					}
					if (CollectionUtils.isNotEmpty(roomStays))
					{
						packageReservedRoomStays.addAll(convertToReservedRoomStays(roomStays));
					}
					accommodationAvailabilityResponseData.setReservedRoomStays(packageReservedRoomStays);
				}
			}
		}

		if (CollectionUtils.size(accommodationAvailabilityResponseData.getReservedRoomStays()) != CollectionUtils
				.size(roomStayCandidates))
		{
			accommodationAvailabilityResponseData.setConfigRoomsUnavailable(Boolean.TRUE);
		}

	}

	/**
	 * Gets available room stays.
	 *
	 * @param roomStays
	 * 		the room stays
	 *
	 * @return the available room stays
	 */
	protected List<RoomStayData> getAvailableRoomStays(final List<RoomStayData> roomStays)
	{
		for (final RoomStayData roomStay : roomStays)
		{
			for (final RatePlanData ratePlan : roomStay.getRatePlans())
			{
				if (ratePlan.getAvailableQuantity() < CollectionUtils.size(roomStays))
				{
					return collectMinimumRoomStays(roomStays, ratePlan.getAvailableQuantity());
				}
			}
		}
		return roomStays;
	}

	/**
	 * Collect minimum room stays list.
	 *
	 * @param roomStays
	 * 		the room stays
	 * @param availableQuantity
	 * 		the available quantity
	 *
	 * @return the list
	 */
	protected List<RoomStayData> collectMinimumRoomStays(final List<RoomStayData> roomStays, final Integer availableQuantity)
	{
		final List<RoomStayData> minimumRoomStays = new ArrayList<>();
		for (int i = 0; i < availableQuantity; i++)
		{
			minimumRoomStays.add(roomStays.get(i));
		}
		return minimumRoomStays;
	}

	/**
	 * This method checks ratePlan availability, returns false if any ratePlan having 0 availability otherwise true.
	 *
	 * @param configuredAccommodationAvailability
	 * 		the configured accommodation availability
	 *
	 * @return boolean
	 */
	protected boolean isRatePlanAvailable(final AccommodationAvailabilityResponseData configuredAccommodationAvailability)
	{
		for (final RoomStayData roomStay : configuredAccommodationAvailability.getRoomStays())
		{
			final Optional<RatePlanData> unavailableRatePlan = roomStay.getRatePlans().stream()
					.filter(ratePlan -> ratePlan.getAvailableQuantity() <= 0).findAny();
			if (unavailableRatePlan.isPresent())
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;

	}

	/**
	 * Convert to reserved room stays list.
	 *
	 * @param roomStays
	 * 		the room stays
	 *
	 * @return the list
	 */
	protected List<ReservedRoomStayData> convertToReservedRoomStays(final List<RoomStayData> roomStays)
	{
		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();
		roomStays.forEach(roomStay -> {
			roomStay.setRoomStayRefNumber(roomStay.getRoomStayRefNumber());
			final ReservedRoomStayData reservedRoomStayData = (ReservedRoomStayData) roomStay;
			reservedRoomStayData.setNonModifiable(Boolean.FALSE);
			reservedRoomStays.add(reservedRoomStayData);
		});
		return reservedRoomStays;
	}

	/**
	 * This method returns true if every ratePlanConfig has single room count
	 *
	 * @param ratePlanConfigs
	 * 		the rate plan configs
	 *
	 * @return boolean
	 */
	protected boolean checkForValidRatePlanConfig(final List<String> ratePlanConfigs)
	{
		for (final String ratePlanConfig : ratePlanConfigs)
		{
			try
			{
				if (Integer.parseInt(ratePlanConfig.split("\\|", 3)[2]) != ROOM_COUNT)
				{
					return Boolean.FALSE;
				}
			}
			catch (final NumberFormatException ex)
			{
				LOG.error("Unable to parse quantity of rateplan config with code :\t" + ratePlanConfig);
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * Creates accommodation availability request based on accommodation search response
	 *
	 * @param accommodationSearchResponseData
	 * 		the accommodation search response data
	 * @param roomStayRefNum
	 * 		the room stay ref num
	 *
	 * @return accommodation availability request data
	 */
	protected AccommodationAvailabilityRequestData createAccommodationAvailabilityRequestData(
			final AccommodationSearchResponseData accommodationSearchResponseData, final int roomStayRefNum)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		criterion.setAccommodationReference(accommodationSearchResponseData.getCriterion().getAccommodationReference());
		criterion.setStayDateRange(accommodationSearchResponseData.getCriterion().getStayDateRange());

		final List<RoomStayCandidateData> roomStayCandidateDatas = new ArrayList<>();

		for (final PropertyData property : accommodationSearchResponseData.getProperties())
		{
			int refNum = roomStayRefNum;
			for (final String ratePlanConfig : property.getRatePlanConfigs())
			{
				final String[] ratePlanConfigSplit = ratePlanConfig.split("\\|", 3);

				final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
				roomStayCandidateData.setRatePlanCode(ratePlanConfigSplit[0]);
				roomStayCandidateData.setAccommodationCode(ratePlanConfigSplit[1]);
				roomStayCandidateData.setRoomStayCandidateRefNumber(refNum++);
				try
				{
					for (int i = 0; i < Integer.parseInt(ratePlanConfigSplit[2]); i++)
					{
						roomStayCandidateDatas.add(roomStayCandidateData);
					}
				}
				catch (final NumberFormatException ex)
				{
					LOG.error("Unable to parse quantity of rateplan config with code :\t" + ratePlanConfig);
					return null;
				}
			}
		}
		criterion.setRoomStayCandidates(roomStayCandidateDatas);
		accommodationAvailabilityRequestData.setCriterion(criterion);

		return accommodationAvailabilityRequestData;
	}

	/**
	 * Gets accommodation search facade.
	 *
	 * @return the accommodation search facade
	 */
	protected AccommodationSearchFacade getAccommodationSearchFacade()
	{
		return accommodationSearchFacade;
	}

	/**
	 * Sets accommodation search facade.
	 *
	 * @param accommodationSearchFacade
	 * 		the accommodation search facade
	 */
	@Required
	public void setAccommodationSearchFacade(final AccommodationSearchFacade accommodationSearchFacade)
	{
		this.accommodationSearchFacade = accommodationSearchFacade;
	}

	/**
	 * Gets accommodation offering facade.
	 *
	 * @return the accommodation offering facade
	 */
	protected AccommodationOfferingFacade getAccommodationOfferingFacade()
	{
		return accommodationOfferingFacade;
	}

	/**
	 * Sets accommodation offering facade.
	 *
	 * @param accommodationOfferingFacade
	 * 		the accommodation offering facade
	 */
	@Required
	public void setAccommodationOfferingFacade(final AccommodationOfferingFacade accommodationOfferingFacade)
	{
		this.accommodationOfferingFacade = accommodationOfferingFacade;
	}

	/**
	 * Gets booking facade.
	 *
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * Sets booking facade.
	 *
	 * @param bookingFacade
	 * 		the bookingFacade to set
	 */
	@Required
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}

}
