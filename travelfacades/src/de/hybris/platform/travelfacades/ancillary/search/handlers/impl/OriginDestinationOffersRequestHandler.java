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

package de.hybris.platform.travelfacades.ancillary.search.handlers.impl;

import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravelRestrictionData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.travelfacades.ancillary.search.handlers.AncillarySearchRequestHandler;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelservices.enums.AddToCartCriteriaType;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Handler class to populate OriginDestinationOption in selected offers.
 */
public class OriginDestinationOffersRequestHandler implements AncillarySearchRequestHandler
{
	private Map<String, String> offerGroupToOriginDestinationMapping;
	private TravelRestrictionFacade travelRestrictionFacade;

	@Override
	public void handle(final ReservationData reservationData, final OfferRequestData offerRequestData)
	{
		for (final ItineraryData itineraryData : offerRequestData.getItineraries())
		{
			final List<OfferGroupData> filteredOfferGroups = getFilteredOfferGroups(offerRequestData);

			for (final OfferGroupData offerGroupData : filteredOfferGroups)
			{
				final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = getOriginDestinationInfos(offerGroupData,
						itineraryData);

				if (CollectionUtils.isEmpty(offerGroupData.getOriginDestinationOfferInfos()))
				{
					offerGroupData.setOriginDestinationOfferInfos(originDestinationOfferInfos);
				}
				else
				{
					offerGroupData.getOriginDestinationOfferInfos().addAll(originDestinationOfferInfos);
				}
			}
		}
	}

	/**
	 * Filters the offerGroups based of the {@link AddToCartCriteriaType}, returning the list of offerGroups with type
	 * PER_LEG or PER_LEG_PER_PAX
	 *
	 * @param offerResponseData
	 * @return
	 * @deprecated since version 4 use {@link #getFilteredOfferGroups(offerRequestData)}
	 * 
	 */
	@Deprecated
	protected List<OfferGroupData> getFilteredOfferGroups(final ReservationData reservationData,
			final OfferRequestData offerRequestData)
	{
		return getFilteredOfferGroups(offerRequestData);
	}


	/**
	 * Filters the offerGroups based of the {@link AddToCartCriteriaType}, returning the list of offerGroups with type
	 * PER_LEG or PER_LEG_PER_PAX
	 * 
	 * @param offerRequestData
	 * @return
	 */
	protected List<OfferGroupData> getFilteredOfferGroups(final OfferRequestData offerRequestData)
	{
		final List<OfferGroupData> offerGroups = offerRequestData.getSelectedOffers().getOfferGroups();

		return offerGroups.stream().filter(offerGroup -> {
			final TravelRestrictionData travelRestrictionData = getTravelRestrictionFacade()
					.getTravelRestrictionForCategory(offerGroup.getCode());
			return travelRestrictionData == null || StringUtils.isBlank(travelRestrictionData.getAddToCartCriteria())
					|| StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
							AddToCartCriteriaType.PER_LEG.getCode())
					|| StringUtils.equalsIgnoreCase(travelRestrictionData.getAddToCartCriteria(),
							AddToCartCriteriaType.PER_LEG_PER_PAX.getCode());
		}).collect(Collectors.toList());
	}

	/**
	 * Will update the OfferGroupData with a list of OriginDestinationOfferInfo based on the given ItineraryData
	 *
	 * @param offerGroupData
	 *           the OfferGroupData to update
	 * @param itineraryData
	 *           the ItineraryData
	 * @return list of OriginDestinationOfferInfoData
	 */
	protected final List<OriginDestinationOfferInfoData> getOriginDestinationInfos(final OfferGroupData offerGroupData,
			final ItineraryData itineraryData)
	{
		final List<OriginDestinationOfferInfoData> originDestinationOfferInfos = new ArrayList<OriginDestinationOfferInfoData>();
		final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(offerGroupData.getCode(),
				getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
						TravelservicesConstants.TRAVEL_ROUTE));
		if (StringUtils.equalsIgnoreCase(mapping, TravelfacadesConstants.TRAVEL_ROUTE))
		{
			for (final OriginDestinationOptionData originDestinationOption : itineraryData.getOriginDestinationOptions())
			{
				final OriginDestinationOfferInfoData newODOfferInfo = new OriginDestinationOfferInfoData();
				newODOfferInfo.setTransportOfferings(new ArrayList<>());
				newODOfferInfo.getTransportOfferings().addAll(originDestinationOption.getTransportOfferings());
				newODOfferInfo.setOriginDestinationRefNumber(originDestinationOption.getOriginDestinationRefNumber());
				newODOfferInfo.setTravelRouteCode(originDestinationOption.getTravelRouteCode());
				originDestinationOfferInfos.add(newODOfferInfo);
			}
		}
		else if (StringUtils.equalsIgnoreCase(mapping, TravelfacadesConstants.TRANSPORT_OFFERING))
		{

			for (final OriginDestinationOptionData originDestinationOption : itineraryData.getOriginDestinationOptions())
			{
				for (final TransportOfferingData transportOfferingData : originDestinationOption.getTransportOfferings())
				{
					final OriginDestinationOfferInfoData newODOfferInfo = new OriginDestinationOfferInfoData();
					newODOfferInfo.setTransportOfferings(new ArrayList<>());
					newODOfferInfo.getTransportOfferings().add(transportOfferingData);
					newODOfferInfo.setOriginDestinationRefNumber(originDestinationOption.getOriginDestinationRefNumber());
					newODOfferInfo.setTravelRouteCode(originDestinationOption.getTravelRouteCode());
					originDestinationOfferInfos.add(newODOfferInfo);
				}
			}
		}
		return originDestinationOfferInfos;
	}

	/**
	 * @return the offerGroupToOriginDestinationMapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * @param offerGroupToOriginDestinationMapping
	 *           the offerGroupToOriginDestinationMapping to set
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	/**
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * @param travelRestrictionFacade
	 *           the travelRestrictionFacade to set
	 */
	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
	}
}
