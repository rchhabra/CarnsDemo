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
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS.Services;
import de.hybris.platform.ndcfacades.ndc.ServicePriceRS.Services.Service;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;


/**
 * The NDC offer populator for {@link ServicePriceRS}
 */
public class NDCServicePriceRSPopulator extends NDCAbstractOfferRSPopulator
		implements Populator<OfferResponseData, ServicePriceRS>
{
	@Override
	public void populate(final OfferResponseData source, final ServicePriceRS target) throws ConversionException
	{
		final Services services = new Services();
		final Map<String, Service> map = new HashMap<>();
		source.getOfferGroups().forEach(offerGroupData -> {

			final List<OriginDestinationOfferInfoData> offerInfoDatas = offerGroupData.getOriginDestinationOfferInfos();
			offerInfoDatas.forEach(offerinfoData -> offerinfoData.getOfferPricingInfos().forEach(offerPricingInfo -> {
				final ProductData productData = offerPricingInfo.getProduct();
				final String productCode = productData.getCode();
				Service service = map.get(productCode);
				if (Objects.isNull(service))
				{
					service = new Service();
					service.setServiceID(createServiceIdType(productData));
					service.setName(getServiceName(productData));
					service.setObjectKey(productCode);
					service.setSettlement(getSettlement());
					service.setDescriptions(getDescriptions(productData));
					populatePriceData(target.getDataLists().getAnonymousTravelerList(), service.getPrice(), offerPricingInfo);
					map.put(productData.getCode(), service);
					services.getService().add(service);
				}
				populateAssociation(target.getDataLists(), service.getAssociations(), productData, offerinfoData);
			}));
		});
		if (CollectionUtils.isNotEmpty(services.getService()))
		{
			target.setServices(services);
		}
		else
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.SERVICE_OUT_OF_STOCK));
		}
	}
}
