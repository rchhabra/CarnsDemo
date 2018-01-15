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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchPipelineManager;
import de.hybris.platform.travelfacades.ancillary.search.manager.AncillarySearchRequestPipelineManager;
import de.hybris.platform.travelfacades.facades.OffersFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.OfferResponseFilterStrategy;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of OffersFacade
 */
public class DefaultOffersFacade implements OffersFacade
{

	private AncillarySearchPipelineManager ancillarySearchPipelineManager;
	private AncillarySearchRequestPipelineManager ancillarySearchRequestPipelineManager;
	private AncillarySearchPipelineManager accommodationSearchPipelineManager;
	private ReservationFacade reservationFacade;
	private TravelCartFacade travelCartFacade;
	private CartService cartService;
	private List<OfferResponseFilterStrategy> offerResponseFilterStrategyList;

	@Override
	public OfferResponseData getOffers(final OfferRequestData offerRequestData)
	{
		if (Objects.isNull(offerRequestData))
		{
			return null;
		}
		final OfferResponseData offerResponseData = getAncillarySearchPipelineManager().executePipeline(offerRequestData);
		filterOfferResponseData(offerResponseData);
		return offerResponseData;
	}

	@Override
	public OfferRequestData getOffersRequest()
	{
		final ReservationData reservationData = getReservationFacade().getCurrentReservationData();
		return getAncillarySearchRequestPipelineManager().executePipeline(reservationData);
	}

	protected void filterOfferResponseData(final OfferResponseData offerResponseData)
	{
		if (Objects.isNull(offerResponseData))
		{
			return;
		}
		for (final OfferGroupData offerGroupData : offerResponseData.getOfferGroups())
		{
			//check on availability
			if (CollectionUtils.isNotEmpty(offerGroupData.getOriginDestinationOfferInfos()))
			{
				offerGroupData.getOriginDestinationOfferInfos()
						.removeIf(odOfferInfo -> odOfferInfo == null && odOfferInfo.getOfferPricingInfos().isEmpty());
			}
		}

		offerResponseData.getOfferGroups()
				.removeIf(offerGroupData -> (CollectionUtils.isEmpty(offerGroupData.getOfferPricingInfos())
						&& CollectionUtils.isEmpty(offerGroupData.getOriginDestinationOfferInfos())));


		//if isAmendment apply strategies
		if (getTravelCartFacade().isAmendmentCart())
		{
			for (final OfferResponseFilterStrategy strategy : getOfferResponseFilterStrategyList())
			{
				strategy.filterOfferResponseData(offerResponseData);
			}
		}
	}

	@Override
	public OfferResponseData getAccommodations(final OfferRequestData offerRequestData)
	{
		if (Objects.isNull(offerRequestData))
		{
			return null;
		}
		final OfferResponseData offerResponseData = getAccommodationSearchPipelineManager().executePipeline(offerRequestData);
		filterSeatMap(offerResponseData.getSeatMap());
		return offerResponseData;
	}

	protected void filterSeatMap(final SeatMapResponseData seatMapResponseData)
	{
		if (getTravelCartFacade().isAmendmentCart())
		{
			for (final OfferResponseFilterStrategy strategy : getOfferResponseFilterStrategyList())
			{
				strategy.filterSeatMapData(seatMapResponseData);
			}
		}
	}

	/**
	 * @return the ancillarySearchPipelineManager
	 */
	protected AncillarySearchPipelineManager getAncillarySearchPipelineManager()
	{
		return ancillarySearchPipelineManager;
	}

	/**
	 * @param ancillarySearchPipelineManager
	 *           the ancillarySearchPipelineManager to set
	 */
	@Required
	public void setAncillarySearchPipelineManager(final AncillarySearchPipelineManager ancillarySearchPipelineManager)
	{
		this.ancillarySearchPipelineManager = ancillarySearchPipelineManager;
	}

	/**
	 * @return the ancillarySearchRequestPipelineManager
	 */
	protected AncillarySearchRequestPipelineManager getAncillarySearchRequestPipelineManager()
	{
		return ancillarySearchRequestPipelineManager;
	}

	/**
	 * @param ancillarySearchRequestPipelineManager
	 *           the ancillarySearchRequestPipelineManager to set
	 */
	@Required
	public void setAncillarySearchRequestPipelineManager(
			final AncillarySearchRequestPipelineManager ancillarySearchRequestPipelineManager)
	{
		this.ancillarySearchRequestPipelineManager = ancillarySearchRequestPipelineManager;
	}

	/**
	 * @return the reservationFacade
	 */
	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * @param reservationFacade
	 *           the reservationFacade to set
	 */
	@Required
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	/**
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * @param travelCartFacade
	 *           the travelCartFacade to set
	 */
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the offerResponseFilterStrategyList
	 */
	protected List<OfferResponseFilterStrategy> getOfferResponseFilterStrategyList()
	{
		return offerResponseFilterStrategyList;
	}

	/**
	 * @param offerResponseFilterStrategyList
	 *           the offerResponseFilterStrategyList to set
	 */
	public void setOfferResponseFilterStrategyList(final List<OfferResponseFilterStrategy> offerResponseFilterStrategyList)
	{
		this.offerResponseFilterStrategyList = offerResponseFilterStrategyList;
	}

	/**
	 * @return the accommodationSearchPipelineManager
	 */
	protected AncillarySearchPipelineManager getAccommodationSearchPipelineManager()
	{
		return accommodationSearchPipelineManager;
	}

	/**
	 * @param accommodationSearchPipelineManager
	 *           the accommodationSearchPipelineManager to set
	 */
	@Required
	public void setAccommodationSearchPipelineManager(final AncillarySearchPipelineManager accommodationSearchPipelineManager)
	{
		this.accommodationSearchPipelineManager = accommodationSearchPipelineManager;
	}
}
