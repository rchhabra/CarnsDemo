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

package de.hybris.platform.ndcfacades.populators.response;

import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageListRS;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList;
import de.hybris.platform.ndcfacades.ndc.DataListType.FlightList.Flight;
import de.hybris.platform.ndcfacades.ndc.DescriptionType.Text;
import de.hybris.platform.ndcfacades.ndc.ListOfFlightSegmentType;
import de.hybris.platform.ndcfacades.ndc.ObjectFactory;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.SegmentReferences;
import de.hybris.platform.ndcfacades.ndc.ServiceAssocType;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType.Associations;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType.Name;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType.Settlement;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType.Description;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ServiceListRS;
import de.hybris.platform.ndcfacades.ndc.ServicePriceType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * An abstract class that populates response body for {@link BaggageListRS} and {@link ServiceListRS}
 */
public abstract class NDCAbstractOfferRSPopulator
{
	private ConfigurationService configurationService;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	/**
	 * This method populates price data for every offer/ancillaries in {@link ServiceListRS} and {@link BaggageListRS}
	 *
	 * @param travelerList
	 *           the traveler list
	 * @param prices
	 *           the prices
	 * @param offerPricingInfo
	 *           the offer pricing info
	 */
	protected void populatePriceData(final List<PassengerType> travelerList, final List<ServicePriceType> prices,
			final OfferPricingInfoData offerPricingInfo)
	{
		final Map<BigDecimal, List<PassengerType>> map = createTravelerTypePriceMap(travelerList, offerPricingInfo);
		map.forEach((key, value) -> {
			final ServicePriceType priceType = new ServicePriceType();
			final CurrencyAmountOptType total = new CurrencyAmountOptType();
			total.setValue(key);
			priceType.setTotal(total);
			priceType.getPassengerReferences().addAll(value);
			prices.add(priceType);
		});
	}

	/**
	 * This method creates the map having offer value as key and associated {@link PassengerType}'s list as value.
	 *
	 * @param travelerList
	 *           the traveler list
	 * @param offerPricingInfo
	 *           the offer pricing info
	 * @return the map
	 */
	protected Map<BigDecimal, List<PassengerType>> createTravelerTypePriceMap(final List<PassengerType> travelerList,
			final OfferPricingInfoData offerPricingInfo)
	{
		final Map<BigDecimal, List<PassengerType>> map = new HashMap<>();
		final List<PassengerType> passengerTypes = new ArrayList<>();
		offerPricingInfo.getTravellerBreakdowns().forEach(travellerBreakdownData -> {
			final PassengerInformationData pid = (PassengerInformationData) travellerBreakdownData.getTraveller().getTravellerInfo();
			final BigDecimal offerValue = travellerBreakdownData.getPassengerFare().getTotalFare().getValue();

			final PassengerType passengerRef = getPassengerRefernceByCode(travelerList, pid.getPassengerType().getCode());
			if (!isPassengerRefExist(passengerRef, map, offerValue))
			{
				if (map.containsKey(offerValue))
				{
					map.get(offerValue).add(passengerRef);
				}
				else
				{
					passengerTypes.add(passengerRef);
					map.put(offerValue, passengerTypes);
				}
			}
		});
		return map;
	}

	/**
	 * This method returns the reference of {@link PassengerType} for given code.
	 *
	 * @param anonymousTravelers
	 *           the anonymous travelers
	 * @param code
	 *           the code
	 * @return the passenger refernce by code
	 */
	protected PassengerType getPassengerRefernceByCode(final List<PassengerType> anonymousTravelers, final String code)
	{
		final Optional<PassengerType> optional = anonymousTravelers.stream()
				.filter(anonymousTraveler -> anonymousTraveler.getPTC().equalsIgnoreCase(code)).findFirst();
		return optional.orElse(null);
	}

	/**
	 * This method checks if passenger reference exist for given offerValue in map
	 *
	 * @param passengerRef
	 *           the passenger ref
	 * @param map
	 *           the map
	 * @param offerValue
	 *           the offer value
	 * @return the boolean
	 */
	protected boolean isPassengerRefExist(final PassengerType passengerRef,
			final Map<BigDecimal, List<PassengerType>> map, final BigDecimal offerValue)
	{
		if (Objects.nonNull(passengerRef))
		{
			return CollectionUtils.isNotEmpty(map.get(offerValue)) ? map.get(offerValue).stream().anyMatch(
					anonymousTraveler -> StringUtils.equalsIgnoreCase(anonymousTraveler.getPTC(), passengerRef.getPTC()))
					: Boolean.FALSE;
		}
		return Boolean.FALSE;
	}

	/**
	 * This method create and returns {@link ServiceDescriptionType} instance
	 *
	 * @param productData
	 */
	protected ServiceDescriptionType getDescriptions(final ProductData productData)
	{
		final ServiceDescriptionType descriptionType = new ServiceDescriptionType();
		final Description description = new Description();

		final Text text = new Text();

		text.setValue(StringUtils.isNotEmpty(productData.getDescription()) ? productData.getDescription() : productData.getCode());
		description.setText(text);
		description.setLink(productData.getUrl());

		descriptionType.getDescription().add(description);
		return descriptionType;
	}

	/**
	 * This method create and returns {@link ServiceIDType} instance.
	 *
	 * @param productData
	 *           the product data
	 * @return the service ID type
	 */
	protected ServiceIDType createServiceIdType(final ProductData productData)
	{
		final ServiceIDType idType = new ServiceIDType();
		idType.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));
		idType.setValue(productData.getCode());
		return idType;
	}

	/**
	 * This method create and returns {@link Settlement} instance.
	 *
	 * @return the settlement
	 */
	protected Settlement getSettlement()
	{
		final Settlement settlement = new Settlement();
		settlement.setMethod(NdcfacadesConstants.SETTLEMENT_METHOD);
		return settlement;
	}

	/**
	 * This method creates and return an instance of {@link Associations} for given params.
	 *
	 * @param dataListType
	 *           the data list type
	 * @param associationList
	 *           the association list
	 * @param productData
	 *           the product data
	 * @param offerInfoData
	 *           the offer info data
	 */
	protected void populateAssociation(final DataListType dataListType, final List<Associations> associationList,
			final ProductData productData, final OriginDestinationOfferInfoData offerInfoData)
	{

		String categoryCode;
		if (Objects.isNull(productData) || CollectionUtils.isEmpty(productData.getCategories()))
		{
			categoryCode = StringUtils.EMPTY;
		}
		else
		{
			final Optional<CategoryData> categoryData = productData.getCategories().stream().findFirst();
			categoryCode = categoryData.map(CategoryData::getCode).orElse(StringUtils.EMPTY);
		}
		final String offerGroupToODMapping = getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode,
				getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
						TravelservicesConstants.TRAVEL_ROUTE));

		Associations associations;
		ServiceAssocType.Flight flight;
		if (CollectionUtils.isNotEmpty(associationList))
		{
			associations = associationList.get(0);
			flight = associations.getFlight();
			if (Objects.isNull(flight))
			{
				flight = new ServiceAssocType.Flight();
			}
		}
		else
		{
			associations = new Associations();
			flight = new ServiceAssocType.Flight();
			associations.setFlight(flight);
			associationList.add(associations);
		}
		if (StringUtils.equalsIgnoreCase(offerGroupToODMapping, TravelservicesConstants.TRANSPORT_OFFERING))
		{
			populateApplicableSegment(dataListType.getFlightList(), flight, offerInfoData.getTransportOfferings());
		}
		else if (StringUtils.equalsIgnoreCase(offerGroupToODMapping, TravelservicesConstants.TRAVEL_ROUTE)
				&& !isOriginDestinationExist(flight, offerInfoData.getTravelRouteCode()))
		{
			final ObjectFactory factory = new ObjectFactory();
			final OriginDestination originDestination = getEligibleOriginDestination(
					dataListType.getOriginDestinationList().getOriginDestination(), offerInfoData.getTravelRouteCode());
			final JAXBElement<List<Object>> element = factory.createOriginDestinationReferences(Arrays.asList(originDestination));
			flight.getOriginDestinationReferencesOrSegmentReferences().add(element);
		}
	}

	/**
	 * Checks if is origin destination exist.
	 *
	 * @param flight
	 *           the flight
	 * @param travelRouteCode
	 *           the travel route code
	 * @return true, if is origin destination exist
	 */
	protected boolean isOriginDestinationExist(final ServiceAssocType.Flight flight, final String travelRouteCode)
	{
		final List<JAXBElement<List<Object>>> originDestinationRefs = flight.getOriginDestinationReferencesOrSegmentReferences()
				.stream().filter(originDestinationRef -> originDestinationRef instanceof JAXBElement)
				.map(originDestinationRef -> (JAXBElement<List<Object>>) originDestinationRef).collect(Collectors.toList());
		final List<OriginDestination> originDestinations = originDestinationRefs.stream()
				.flatMap(originDestinationRef -> originDestinationRef.getValue().stream())
				.filter(originDestinationRef -> originDestinationRef instanceof OriginDestination)
				.map(originDestinationRef -> (OriginDestination) originDestinationRef).collect(Collectors.toList());

		return originDestinations.stream().anyMatch(
				originDestination -> StringUtils.equalsIgnoreCase(originDestination.getOriginDestinationKey(), travelRouteCode));
	}

	/**
	 * This method returns instance of {@link OriginDestination} from list that matches given travelRouteCode.
	 *
	 * @param originDestinations
	 *           the origin destinations
	 * @param travelRouteCode
	 *           the travel route code
	 * @return the eligible origin destination
	 */
	protected OriginDestination getEligibleOriginDestination(final List<OriginDestination> originDestinations,
			final String travelRouteCode)
	{
		final Optional<OriginDestination> opt = originDestinations.stream()
				.filter(
						originDestination -> StringUtils.equalsIgnoreCase(originDestination.getOriginDestinationKey(), travelRouteCode))
				.findFirst();
		return opt.orElse(null);
	}

	/**
	 * This method populates the {@link SegmentReferences} from given transport offering code and {@link FlightList}.
	 *
	 * @param flightList
	 *           the flight list
	 * @param flight
	 *           the flight
	 * @param transportOfferingDatas
	 *           the transport offering datas
	 */
	protected void populateApplicableSegment(final FlightList flightList, final ServiceAssocType.Flight flight,
			final List<TransportOfferingData> transportOfferingDatas)
	{
		for (final Flight dataListFlight : flightList.getFlight())
		{
			final List<String> transportOfferingCodes = transportOfferingDatas.stream().map(TransportOfferingData::getCode)
					.collect(Collectors.toList());

			final List<String> segmentKeys = dataListFlight.getSegmentReferences().getValue().stream()
					.map(obj -> ((ListOfFlightSegmentType) (obj)).getSegmentKey()).collect(Collectors.toList());

			populateSegment(segmentKeys, transportOfferingCodes, dataListFlight, flight);
		}
	}

	/**
	 * This method populates applicable {@link SegmentReferences}, if it is not already exist, to
	 * {@link ServiceAssocType.Flight}
	 *
	 * @param segmentKeys
	 *           the segment keys
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param dataListFlight
	 *           the data list flight
	 * @param flight
	 *           the flight
	 */
	protected void populateSegment(final List<String> segmentKeys, final List<String> transportOfferingCodes,
			final Flight dataListFlight, final ServiceAssocType.Flight flight)
	{
		for (final String transportOfferingCode : transportOfferingCodes)
		{
			final Optional<String> optional = segmentKeys.stream()
					.filter(segmentKey -> StringUtils.equalsIgnoreCase(segmentKey, transportOfferingCode)).findFirst();
			if (optional.isPresent() && !checkIfSegmentRefExist(flight, transportOfferingCode))
			{
				if (CollectionUtils.isNotEmpty(flight.getOriginDestinationReferencesOrSegmentReferences()))
				{
					addSegmentToExistingRef(flight, optional.get(), dataListFlight);
				}
				else
				{
					final ListOfFlightSegmentType applicableSegment = getSegmentForKey(optional.get(),
							dataListFlight.getSegmentReferences().getValue());
					if (Objects.nonNull(applicableSegment))
					{
						final SegmentReferences segmentReferences = new SegmentReferences();
						segmentReferences.getValue().add(applicableSegment);
						flight.getOriginDestinationReferencesOrSegmentReferences().add(segmentReferences);
					}
				}
				break;
			}
		}
	}

	/**
	 * This method add an instance of {@link ListOfFlightSegmentType} to list of references.
	 *
	 * @param flight
	 *           the flight
	 * @param segmentKey
	 *           the segment key
	 * @param dataListFlight
	 *           the data list flight
	 */
	protected void addSegmentToExistingRef(final ServiceAssocType.Flight flight, final String segmentKey,
			final Flight dataListFlight)
	{
		flight.getOriginDestinationReferencesOrSegmentReferences().forEach(obj -> {
			final SegmentReferences references = (SegmentReferences) obj;
			final ListOfFlightSegmentType segment = getSegmentForKey(segmentKey, dataListFlight.getSegmentReferences().getValue());
			if (Objects.nonNull(segment))
			{
				references.getValue().add(segment);
			}
		});
	}

	/**
	 * This method returns {@link ListOfFlightSegmentType} from dataListFlight for given segmentKey.
	 *
	 * @param segmentKey
	 *           the segment key
	 * @param value
	 *           the value
	 * @return the segment for key
	 */
	protected ListOfFlightSegmentType getSegmentForKey(final String segmentKey, final List<Object> value)
	{
		final Optional<Object> opt = value.stream()
				.filter(obj -> StringUtils.equalsIgnoreCase(((ListOfFlightSegmentType) (obj)).getSegmentKey(), segmentKey))
				.findFirst();
		if (opt.isPresent())
		{
			return (ListOfFlightSegmentType) opt.get();
		}
		return null;

	}

	/**
	 * This method checks if {@link SegmentReferences} already exist in {@link Flight}.
	 *
	 * @param flight
	 *           the flight
	 * @param transportOfferingCode
	 *           the transport offering code
	 * @return true, if successful
	 */
	protected boolean checkIfSegmentRefExist(final ServiceAssocType.Flight flight, final String transportOfferingCode)
	{
		for (final Object obj : flight.getOriginDestinationReferencesOrSegmentReferences())
		{
			final SegmentReferences references = (SegmentReferences) obj;
			for (final Object value : references.getValue())
			{
				final ListOfFlightSegmentType flightSegmentType = (ListOfFlightSegmentType) value;
				if (StringUtils.equalsIgnoreCase(flightSegmentType.getSegmentKey(), transportOfferingCode))
				{
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * This method create and returns {@link Name} instance.
	 *
	 * @param productData
	 *           the product data
	 * @return the service name
	 */
	protected Name getServiceName(final ProductData productData)
	{
		final Name name = new Name();

		if (Objects.isNull(productData) || CollectionUtils.isEmpty(productData.getCategories()))
		{
			name.setValue(StringUtils.EMPTY);
			return name;
		}

		final Optional<CategoryData> category = productData.getCategories().stream().findFirst();
		name.setValue(StringUtils.isNotEmpty(productData.getName()) ? productData.getName()
				: category.map(CategoryData::getCode).orElse(StringUtils.EMPTY));
		return name;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

}
