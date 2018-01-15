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
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS.BaggageCharge;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS.BaggageCharge.OriginDestination.Services;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS.BaggageCharge.OriginDestination.Services.Service;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS.BaggageCharge.TotalPrice;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.DataListType;
import de.hybris.platform.ndcfacades.ndc.OriginDestination;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;


/**
 * The NDC offer populator for {@link BaggageChargesRS}
 */
public class NDCBaggageChargesRSPopulator extends NDCAbstractOfferRSPopulator
		implements Populator<OfferResponseData, BaggageChargesRS>
{

	private static final String SERVICE_KEY_SEPARATOR = "_";

	@Override
	public void populate(final OfferResponseData source, final BaggageChargesRS target) throws ConversionException
	{
		final BaggageCharge baggageCharge = new BaggageCharge();
		final TotalPrice totalprice = new TotalPrice();
		final CurrencyAmountOptType baseAmount = new CurrencyAmountOptType();
		baseAmount.setValue(BigDecimal.ZERO);
		totalprice.setBaseAmount(baseAmount);
		baggageCharge.setTotalPrice(totalprice);
		target.setBaggageCharge(baggageCharge);

		source.getOfferGroups().forEach(offerGroupData -> {
			final List<OriginDestinationOfferInfoData> offerInfoDatas = offerGroupData.getOriginDestinationOfferInfos();
			offerInfoDatas.forEach(offerInfoData -> {

				final Services services = new Services();
				final Map<String, Service> map = new HashMap<>();
				offerInfoData.getOfferPricingInfos().forEach(offerPricingInfo -> {
					final ProductData productData = offerPricingInfo.getProduct();
					if (!map.containsKey(productData.getCode()))
					{
						final Service service = new Service();
						service.setServiceID(createServiceIdType(productData));
						service.setName(getServiceName(productData));
						final StringBuilder stringBuilder = new StringBuilder(productData.getCode());
						service.setObjectKey(
								stringBuilder.append(SERVICE_KEY_SEPARATOR).append(offerInfoData.getTravelRouteCode()).toString());
						service.setSettlement(getSettlement());
						service.setDescriptions(getDescriptions(productData));
						populatePriceData(target.getDataLists().getAnonymousTravelerList(), service.getPrice(), offerPricingInfo);
						populateAssociation(target.getDataLists(), service.getAssociations(), productData, offerInfoData);
						map.put(productData.getCode(), service);
						services.getService().add(service);
					}
				});
				if (CollectionUtils.isNotEmpty(services.getService()))
				{
					populateOriginDestination(target, services, offerInfoData.getTravelRouteCode());
				}
			});
		});

		if (CollectionUtils.isEmpty(target.getBaggageCharge().getOriginDestination()))
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.BAGGAGE_OUT_OF_STOCK));
		}
	}

	/**
	 * This method populates {@link BaggageCharge.OriginDestination} for each {@link OriginDestination} from
	 * {@link DataListType}
	 *
	 * @param target
	 * 		the target
	 * @param services
	 * 		the services
	 * @param route
	 * 		the route
	 */
	protected void populateOriginDestination(final BaggageChargesRS target, final Services services, final String route)
	{
		if (Objects.nonNull(target.getDataLists()) && Objects.nonNull(target.getDataLists().getOriginDestinationList()))
		{
			final OriginDestination originDestination = getEligibleOriginDestination(
					target.getDataLists().getOriginDestinationList().getOriginDestination(), route);
			final BaggageCharge.OriginDestination baggageChargeOD = new BaggageCharge.OriginDestination();
			baggageChargeOD.setServices(services);
			baggageChargeOD.setArrivalCode(originDestination.getArrivalCode());
			baggageChargeOD.setDepartureCode(originDestination.getDepartureCode());
			target.getBaggageCharge().getOriginDestination().add(baggageChargeOD);
		}
	}
}
