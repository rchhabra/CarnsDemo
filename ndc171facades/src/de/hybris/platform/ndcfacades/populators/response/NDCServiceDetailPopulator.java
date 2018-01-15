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
import de.hybris.platform.converters.Populator;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.ndc.CurrencyAmountOptType;
import de.hybris.platform.ndcfacades.ndc.ServiceAssocType;
import de.hybris.platform.ndcfacades.ndc.ServiceCoreType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType;
import de.hybris.platform.ndcfacades.ndc.ServiceDescriptionType.Description;
import de.hybris.platform.ndcfacades.ndc.ServiceDetailType;
import de.hybris.platform.ndcfacades.ndc.ServiceIDType;
import de.hybris.platform.ndcfacades.ndc.ServicePriceType;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;

//TODO This (or part of this class) has been commented out due to 17.1 upgrade and needs to be revisited
/**
 * NDC Service Detail Populator
 * List of services is populated based on the ancillaries included in the boundles
 */
public class NDCServiceDetailPopulator implements Populator<ServiceDetailType, ProductData>
{
	private ConfigurationService configurationService;

	@Override
	public void populate(final ServiceDetailType serviceDetailType, final ProductData productData) throws ConversionException
	{
		final ServiceDetailType.Detail detail = new ServiceDetailType.Detail();
		final ServiceCoreType.Name name = new ServiceCoreType.Name();
		final ServiceIDType serviceId = new ServiceIDType();
		final ServicePriceType servicePrice = new ServicePriceType();
		final CurrencyAmountOptType total = new CurrencyAmountOptType();
		final ServiceCoreType.Associations associations = new ServiceCoreType.Associations();
		//final ServiceAssocType.Traveler traveller = new ServiceAssocType.Traveler();

		serviceDetailType.setObjectKey(productData.getCode());

		/*traveller.setAllTravelerInd(NdcfacadesConstants.SERVICE_TO_ALL_PTC);
		associations.setTraveler(traveller);*/
		serviceDetailType.getAssociations().add(associations);

		populateDescription(serviceDetailType, productData);

		total.setValue(productData.getPrice().getValue().setScale(NdcfacadesConstants.PRECISION, BigDecimal.ROUND_HALF_UP));
		servicePrice.setTotal(total);
		serviceDetailType.getPrice().add(servicePrice);

		serviceId.setOwner(getConfigurationService().getConfiguration().getString(NdcfacadesConstants.OWNER));

		serviceId.setValue(productData.getCode());
		serviceDetailType.setServiceID(serviceId);

		name.setValue(productData.getName());
		serviceDetailType.setName(name);

		serviceDetailType.setDetail(detail);
	}

	/**
	 * Populate description.
	 *
	 * @param serviceDetailType
	 * 		the service detail type
	 * @param productData
	 * 		the product data
	 */
	protected void populateDescription(final ServiceDetailType serviceDetailType, final ProductData productData)
	{
		final ServiceDescriptionType serviceDetail = new ServiceDescriptionType();
		final Description description = new Description();
		final Description name = new Description();
		final Description url = new Description();

		if(!Objects.isNull(productData.getDescription()))
		{
			description.setApplication(productData.getDescription());
			serviceDetail.getDescription().add(description);
		}

		if(!Objects.isNull(productData.getUrl()))
		{
			url.setApplication(productData.getUrl());
			serviceDetail.getDescription().add(url);
		}

		if(serviceDetail.getDescription().isEmpty())
		{
			name.setApplication(productData.getName());
			serviceDetail.getDescription().add(name);
		}

		serviceDetailType.setDescriptions(serviceDetail);
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
