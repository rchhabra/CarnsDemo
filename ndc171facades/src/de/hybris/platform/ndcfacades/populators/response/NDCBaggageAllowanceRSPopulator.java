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

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS.DataLists.PassengerList;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS.OriginDestination.Services;
import de.hybris.platform.ndcfacades.ndc.BaggageAllowanceRS.OriginDestination.Services.Service;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.ndcfacades.ndc.PassengerType;
import de.hybris.platform.ndcfacades.ndc.ServicePriceType;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

/**
 * The NDC offer populator for {@link BaggageAllowanceRS}
 */
public class NDCBaggageAllowanceRSPopulator extends NDCAbstractOfferRSPopulator
		implements Populator<OfferResponseData, BaggageAllowanceRS>
{

	private static final String SERVICE_KEY_SEPARATOR = "_";

	@Override
	public void populate(final OfferResponseData source, final BaggageAllowanceRS target) throws ConversionException
	{
		source.getOfferGroups().forEach(offerGroupData -> {
			final List<OriginDestinationOfferInfoData> offerInfoDatas = offerGroupData.getOriginDestinationOfferInfos();
			offerInfoDatas.forEach(offerInfoData -> {

				final Services services = new Services();
				final Map<String, Service> productCodeServiceMap = new HashMap<>();
				offerInfoData.getOfferPricingInfos().forEach(offerPricingInfo -> {
					final ProductData productData = offerPricingInfo.getProduct();
					if (!productCodeServiceMap.containsKey(productData.getCode()))
					{
						final Service service = new Service();
						service.setServiceID(createServiceIdType(productData));
						service.setName(getServiceName(productData));
						final StringBuilder stringBuilder = new StringBuilder(productData.getCode());
						service.setObjectKey(
								stringBuilder.append(SERVICE_KEY_SEPARATOR).append(offerInfoData.getTravelRouteCode()).toString());
						service.setSettlement(getSettlement());
						service.setDescriptions(getDescriptions(productData));
						populatePriceData(target.getDataLists().getPassengerList(), service.getPrice(), offerPricingInfo);
						populateAssociation(target.getDataLists(), service.getAssociations(), productData, offerInfoData);
						productCodeServiceMap.put(productData.getCode(), service);
						services.getService().add(service);
					}
				});
				if (CollectionUtils.isNotEmpty(services.getService()))
				{
					populateOriginDestination(target, services, offerInfoData.getTravelRouteCode());
				}
			});
		});

		if (CollectionUtils.isEmpty(target.getOriginDestination()))
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.BAGGAGE_OUT_OF_STOCK));
		}
	}

	/**
	 * This method populates price data for every offer/ancillaries in {@link BaggageAllowanceRS}.
	 *
	 * @param passengerList
	 *           the passenger list
	 * @param prices
	 *           the prices
	 * @param offerPricingInfo
	 *           the offer pricing info
	 */
	protected void populatePriceData(final PassengerList passengerList, final List<ServicePriceType> prices,
			final OfferPricingInfoData offerPricingInfo)
	{
		final Map<BigDecimal, List<PassengerType>> map = createPassengerTypePriceMap(passengerList, offerPricingInfo);
		map.entrySet().forEach(entry -> {
			final ServicePriceType priceType = new ServicePriceType();
			final CurrencyAmountOptType total = new CurrencyAmountOptType();
			total.setValue(entry.getKey());
			priceType.setTotal(total);
			priceType.getPassengerReferences().addAll(entry.getValue());
			prices.add(priceType);
		});
	}

	/**
	 * This method creates the map having offer value as key and associated {@link PassengerType}'s list as value.
	 *
	 * @param passengerList
	 *           the passenger list
	 * @param offerPricingInfo
	 *           the offer pricing info
	 * @return the map
	 */
	protected Map<BigDecimal, List<PassengerType>> createPassengerTypePriceMap(final PassengerList passengerList,
			final OfferPricingInfoData offerPricingInfo)
	{
		final Map<BigDecimal, List<PassengerType>> pricePassengerTypesMap = new HashMap<>();
		final Map<String, Integer> passengerTypeCountMap = new HashMap<>();

		final List<PassengerType> passengerTypes = new ArrayList<>();
		offerPricingInfo.getTravellerBreakdowns().forEach(travellerBreakdownData -> {
			final PassengerInformationData pid = (PassengerInformationData) travellerBreakdownData.getTraveller().getTravellerInfo();
			final BigDecimal offerValue = travellerBreakdownData.getPassengerFare().getTotalFare().getValue()
					.setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP);
			final String ptc = pid.getPassengerType().getCode();
			passengerTypeCountMap.merge(ptc, 1, Integer::sum);

			final PassengerType passengerRef = getPassengerRefernceByPassengerId(passengerList,
					ptc + passengerTypeCountMap.get(ptc));
			passengerTypes.add(passengerRef);
			pricePassengerTypesMap.put(offerValue, passengerTypes);
		});
		return pricePassengerTypesMap;
	}

	/**
	 * This method returns the reference of {@link PassengerType} for given code.
	 *
	 * @param passengerList
	 *           the passenger list
	 * @param passengerId
	 *           the passenger id
	 * @return the passenger refernce by passenger id
	 */
	protected PassengerType getPassengerRefernceByPassengerId(final PassengerList passengerList, final String passengerId)
	{
		final Optional<PassengerType> optional = passengerList.getPassenger().stream()
				.filter(passengerType -> passengerType.getPassengerID().equals(passengerId)).findFirst();
		return optional.orElse(null);
	}

	/**
	 * This method populates {@link BaggageAllowanceRS.OriginDestination} for each {@link OriginDestination} from
	 * {@link DataListType}
	 *
	 * @param target
	 *           the target
	 * @param services
	 *           the services
	 * @param route
	 *           the route
	 */
	protected void populateOriginDestination(final BaggageAllowanceRS target, final Services services, final String route)
	{
		if (Objects.nonNull(target.getDataLists()) && Objects.nonNull(target.getDataLists().getOriginDestinationList()))
		{
			final OriginDestination originDestination = getEligibleOriginDestination(
					target.getDataLists().getOriginDestinationList().getOriginDestination(), route);
			final BaggageAllowanceRS.OriginDestination baggageAllowanceOD = new BaggageAllowanceRS.OriginDestination();
			baggageAllowanceOD.setServices(services);
			baggageAllowanceOD.setArrivalCode(originDestination.getArrivalCode());
			baggageAllowanceOD.setDepartureCode(originDestination.getDepartureCode());
			target.getOriginDestination().add(baggageAllowanceOD);
		}
	}
}
