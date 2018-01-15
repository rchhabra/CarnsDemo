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

package de.hybris.platform.travelfacades.facades.packages.manager.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.AccommodationPackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.travelfacades.facades.accommodation.manager.AccommodationDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.handlers.PackageResponseHandler;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageDetailsPipelineManager;
import de.hybris.platform.travelfacades.facades.packages.manager.PackageReservedRoomStaysPipelineManager;
import de.hybris.platform.travelfacades.fare.search.FareSearchFacade;
import de.hybris.platform.travelfacades.fare.search.manager.FareSearchPipelineManager;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation class for the {@link PackageDetailsPipelineManager}. This pipeline manager will further invoke
 * {@link AccommodationDetailsPipelineManager} and {@link FareSearchPipelineManager} to populate the
 * {@link PackageResponseData}.
 */
public class DefaultPackageDetailsPipelineManager implements PackageDetailsPipelineManager
{
	private AccommodationDetailsPipelineManager accommodationDetailsPipelineManager;
	private PackageReservedRoomStaysPipelineManager packageReservedRoomStaysPipelineManager;
	private FareSearchFacade fareSearchFacade;
	private List<PackageResponseHandler> handlers;

	@Override
	public PackageResponseData executePipeline(final PackageRequestData packageRequestData)
	{
		final PackageResponseData packageResponseData = new PackageResponseData();
		packageResponseData.setAccommodationPackageResponse(evaluateAccommodationDetailsPipeline(packageRequestData));
		packageResponseData.setTransportPackageResponse(evaluateTransportPackageDetails(packageRequestData));
		getHandlers().forEach(handler -> handler.handle(packageRequestData, packageResponseData));

		return packageResponseData;
	}

	protected TransportPackageResponseData evaluateTransportPackageDetails(final PackageRequestData packageRequestData)
	{
		final TransportPackageResponseData transportPackageResponseData = new TransportPackageResponseData();

		if (Objects.isNull(packageRequestData.getTransportPackageRequest()))
		{
			return null;
		}

		final FareSelectionData fareSelectionData = getFareSearchFacade()
				.doSearch(packageRequestData.getTransportPackageRequest().getFareSearchRequest());

		if (fareSelectionData == null || CollectionUtils.isEmpty(fareSelectionData.getPricedItineraries()))
		{
			return null;
		}

		transportPackageResponseData.setFareSearchResponse(fareSelectionData);

		return transportPackageResponseData;
	}

	/**
	 * This method populates the {@link AccommodationPackageResponseData} from {@link PackageRequestData}
	 *
	 * @param packageRequestData
	 * @return the accommodation package response Data
	 */
	protected AccommodationPackageResponseData evaluateAccommodationDetailsPipeline(final PackageRequestData packageRequestData)
	{
		final AccommodationPackageResponseData accommodationPackageResponse = new AccommodationPackageResponseData();
		final AccommodationAvailabilityResponseData availabilityResponseData = getAccommodationDetailsPipelineManager()
				.executePipeline(packageRequestData.getAccommodationPackageRequest().getAccommodationAvailabilityRequest());

		//check for every roomStayCandidate, we must have at least one roomStay.
		if (!verifyRoomStayCandidate(
				packageRequestData.getAccommodationPackageRequest().getAccommodationAvailabilityRequest().getCriterion()
						.getRoomStayCandidates(), availabilityResponseData.getRoomStays()))
		{
			availabilityResponseData.setReservedRoomStays(Collections.emptyList());
		}
		else if (CollectionUtils.isEmpty(availabilityResponseData.getReservedRoomStays())
				&& packageRequestData.getAccommodationPackageRequest().getAccommodationSearchRequest() != null)
		{
			// Only execute this pipeline manager if there are no selected room stays currently in cart
			getPackageReservedRoomStaysPipelineManager().executePipeline(packageRequestData, availabilityResponseData);
		}

		accommodationPackageResponse.setAccommodationAvailabilityResponse(availabilityResponseData);
		return accommodationPackageResponse;
	}

	/**
	 * This method verifies the list of {@link RoomStayCandidateData}, for every {@link RoomStayCandidateData} there must
	 * be at least single {@link RoomStayData}
	 *
	 * @param roomStayCandidates
	 * @param roomStayDatas
	 * @return
	 */
	protected boolean verifyRoomStayCandidate(final List<RoomStayCandidateData> roomStayCandidates,
			final List<RoomStayData> roomStayDatas)
	{
		if (CollectionUtils.isEmpty(roomStayDatas))
		{
			return Boolean.FALSE;
		}

		for (final RoomStayCandidateData roomStayCandidate : roomStayCandidates)
		{
			final Optional<RoomStayData> filteredRoomStayDatas = roomStayDatas.stream().filter(roomStayData -> Objects
					.equals(roomStayData.getRoomStayRefNumber(), roomStayCandidate.getRoomStayCandidateRefNumber())).findAny();

			if (!filteredRoomStayDatas.isPresent())
			{
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	/**
	 * @return the accommodationDetailsPipelineManager
	 */
	protected AccommodationDetailsPipelineManager getAccommodationDetailsPipelineManager()
	{
		return accommodationDetailsPipelineManager;
	}

	/**
	 * @param accommodationDetailsPipelineManager
	 *           the accommodationDetailsPipelineManager to set
	 */
	@Required
	public void setAccommodationDetailsPipelineManager(
			final AccommodationDetailsPipelineManager accommodationDetailsPipelineManager)
	{
		this.accommodationDetailsPipelineManager = accommodationDetailsPipelineManager;
	}

	/**
	 * @return the packageReservedRoomStaysPipelineManager
	 */
	protected PackageReservedRoomStaysPipelineManager getPackageReservedRoomStaysPipelineManager()
	{
		return packageReservedRoomStaysPipelineManager;
	}

	/**
	 * @param packageReservedRoomStaysPipelineManager
	 *           the packageReservedRoomStaysPipelineManager to set
	 */
	@Required
	public void setPackageReservedRoomStaysPipelineManager(
			final PackageReservedRoomStaysPipelineManager packageReservedRoomStaysPipelineManager)
	{
		this.packageReservedRoomStaysPipelineManager = packageReservedRoomStaysPipelineManager;
	}

	/**
	 * @return the fareSearchFacade
	 */
	protected FareSearchFacade getFareSearchFacade()
	{
		return fareSearchFacade;
	}

	/**
	 * @param fareSearchFacade
	 *           the fareSearchFacade to set
	 */
	@Required
	public void setFareSearchFacade(final FareSearchFacade fareSearchFacade)
	{
		this.fareSearchFacade = fareSearchFacade;
	}

	/**
	 * @return the handlers
	 */
	protected List<PackageResponseHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * @param handlers
	 *           the handlers to set
	 */
	@Required
	public void setHandlers(final List<PackageResponseHandler> handlers)
	{
		this.handlers = handlers;
	}
}
