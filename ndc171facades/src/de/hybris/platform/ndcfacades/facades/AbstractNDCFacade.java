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
package de.hybris.platform.ndcfacades.facades;

import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcfacades.resolvers.NDCOfferItemIdResolver;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.ndcservices.services.NDCPassengerTypeService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.product.AncillaryProductModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

/**
 * An abstract class to collect methods used across different NDC facades
 */
public abstract class AbstractNDCFacade
{
	private ConfigurationService configurationService;
	private TimeService timeService;
	private TransportFacilityFacade transportFacilityFacade;
	private SessionService sessionService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private ModelService modelService;
	private TravelCommercePriceService travelCommercePriceService;

	private NDCPassengerTypeService ndcPassengerTypeService;
	private NDCOfferItemIdResolver ndcOfferItemIdResolver;

	/**
	 * Return the child Bundle that contains the fare ancillaries
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 *
	 * @return ancillary child bundle
	 */
	protected BundleTemplateModel getAncillaryChildBundle(final BundleTemplateModel bundleTemplate)
	{
		for (final BundleTemplateModel childTemplate : bundleTemplate.getChildTemplates())
		{
			final List<ProductModel> products = childTemplate.getProducts();

			if (CollectionUtils.isNotEmpty(products)
					&& products.stream().allMatch(product -> (product instanceof AncillaryProductModel
							|| Objects.equals(ProductType.ANCILLARY, product.getProductType()))))
			{
				return childTemplate;
			}
		}
		return null;
	}

	/**
	 * Return the child Bundle that contains the fare product
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 *
	 * @return fare product child bundle
	 */
	protected BundleTemplateModel getFareProductChildBundle(final BundleTemplateModel bundleTemplate)
	{
		for (final BundleTemplateModel childTemplate : bundleTemplate.getChildTemplates())
		{
			final List<ProductModel> products = childTemplate.getProducts();

			if (CollectionUtils.isNotEmpty(products)
					&& products.stream().allMatch(product -> (product instanceof FareProductModel
							|| Objects.equals(ProductType.FARE_PRODUCT, product.getProductType()))))
			{
				return childTemplate;
			}
		}

		throw new ConversionException(NdcfacadesConstants.NO_FARE_PRODUCT);
	}

	/**
	 * Return the PriceInformation for the specified product depending on the bundle, transportOfferings and route
	 *
	 * @param bundleTemplate
	 * 		the bundle template
	 * @param product
	 * 		the product
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param routeCode
	 * 		the route code
	 *
	 * @return the price information
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected PriceInformation getPriceInformation(final BundleTemplateModel bundleTemplate, final ProductModel product,
			final List<TransportOfferingModel> transportOfferings, final String routeCode) throws NDCOrderException
	{
		PriceInformation priceInfo = null;

		if (!Objects.isNull(bundleTemplate)
				&& !(StringUtils.equalsIgnoreCase(ProductType.FARE_PRODUCT.getCode(), product.getProductType().toString())
						|| product instanceof FareProductModel))
		{
			priceInfo = getTravelCommercePriceFacade().getPriceInformationByProductPriceBundleRule(bundleTemplate,
					product.getCode());
		}

		if (Objects.isNull(priceInfo))
		{
			if (isMultiSector(transportOfferings))
			{
				priceInfo = getTravelCommercePriceFacade()
						.getPriceInformation(product.getCode(), PriceRowModel.TRAVELROUTECODE, routeCode);

				if (Objects.isNull(priceInfo))
				{
					priceInfo = getTravelCommercePriceFacade().getPriceInformation(product.getCode(), null, null);
				}
			}
			else
			{
				if (CollectionUtils.isNotEmpty(transportOfferings))
				{
					priceInfo = getTravelCommercePriceFacade().getPriceInformationByHierarchy(product.getCode(),
							transportOfferings.get(0).getCode(), transportOfferings.get(0).getTravelSector().getCode(), routeCode);
				}
				else
				{
					priceInfo = getTravelCommercePriceFacade().getPriceInformation(product.getCode(), null, null);
				}
			}
		}

		if (Objects.isNull(priceInfo) || Objects.isNull(priceInfo.getPriceValue()))
		{
			throw new NDCOrderException(getConfigurationService().getConfiguration()
					.getString(NdcfacadesConstants.BASE_PRICE_NOT_FOUND + product.getCode()));
		}

		return priceInfo;
	}

	/**
	 * Return true if the number of transport offering is greater than one (multi sector flight)
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @return boolean true if is a multisector, false otherwise
	 */
	protected boolean isMultiSector(final List<TransportOfferingModel> transportOfferings)
	{
		return CollectionUtils.size(transportOfferings) > 1;
	}

	/**
	 * Method to return the PassengerTypeModel based on the NDC PTC
	 *
	 * @param ptc
	 * 		the ptc
	 *
	 * @return passenger type model
	 */
	protected PassengerTypeModel getPassengerTypeModel(final String ptc)
	{
		final PassengerTypeModel passengerType = getNdcPassengerTypeService().getPassengerType(ptc);

		if (Objects.isNull(passengerType))
		{
			throw new ConversionException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_PASSENGER_TYPE));
		}
		return passengerType;
	}

	/**
	 * Gets country codes from transport offerings.
	 *
	 * @param transportOfferings
	 * 		the transport offerings
	 *
	 * @return the country codes from transport offerings
	 */
	protected List<String> getCountryCodesFromTransportOfferings(final List<TransportOfferingModel> transportOfferings)
	{
		final List<String> countryCodes = new LinkedList<>();
		for (final TransportOfferingModel transportOffering : transportOfferings)
		{
			final LocationData countryData = getTransportFacilityFacade()
					.getCountry(transportOffering.getTravelSector().getOrigin().getCode());
			countryCodes.add(countryData.getCode());
		}
		return countryCodes;
	}

	/**
	 * Method to set values in Session Context. These values will be used to retrieve taxes during cart calculation when
	 * the product is added to cart.
	 *
	 * @param transportFacilityCodes
	 * 		the transport facility codes
	 * @param countryCodes
	 * 		the country codes
	 * @param passengerType
	 * 		the passenger type
	 */
	protected void setTaxSearchCriteriaInContext(final List<String> transportFacilityCodes, final List<String> countryCodes,
			final String passengerType)
	{
		final List<String> passengerTypeList = new ArrayList<>();
		passengerTypeList.add(passengerType.toLowerCase());

		final Map<String, List<String>> params = new HashMap<>();
		params.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, transportFacilityCodes);
		params.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, countryCodes);
		params.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, passengerTypeList);

		getSessionService().setAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP, params);
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

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the time service
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Gets ndc passenger type service.
	 *
	 * @return the ndc passenger type service
	 */
	protected NDCPassengerTypeService getNdcPassengerTypeService()
	{
		return ndcPassengerTypeService;
	}

	/**
	 * Sets ndc passenger type service.
	 *
	 * @param ndcPassengerTypeService
	 * 		the ndc passenger type service
	 */
	@Required
	public void setNdcPassengerTypeService(final NDCPassengerTypeService ndcPassengerTypeService)
	{
		this.ndcPassengerTypeService = ndcPassengerTypeService;
	}

	/**
	 * Gets transport facility facade.
	 *
	 * @return the transport facility facade
	 */
	protected TransportFacilityFacade getTransportFacilityFacade()
	{
		return transportFacilityFacade;
	}

	/**
	 * Sets transport facility facade.
	 *
	 * @param transportFacilityFacade
	 * 		the transport facility facade
	 */
	@Required
	public void setTransportFacilityFacade(final TransportFacilityFacade transportFacilityFacade)
	{
		this.transportFacilityFacade = transportFacilityFacade;
	}

	/**
	 * Gets session service.
	 *
	 * @return the session service
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the session service
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travel commerce price facade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 * 		the travel commerce price facade
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets ndc offer item id resolver.
	 *
	 * @return the ndc offer item id resolver
	 */
	protected NDCOfferItemIdResolver getNdcOfferItemIdResolver()
	{
		return ndcOfferItemIdResolver;
	}

	/**
	 * Sets ndc offer item id resolver.
	 *
	 * @param ndcOfferItemIdResolver
	 * 		the ndc offer item id resolver
	 */
	@Required
	public void setNdcOfferItemIdResolver(final NDCOfferItemIdResolver ndcOfferItemIdResolver)
	{
		this.ndcOfferItemIdResolver = ndcOfferItemIdResolver;
	}

	/**
	 * Gets travel commerce price service.
	 *
	 * @return the travel commerce price service
	 */
	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	/**
	 * Sets travel commerce price service.
	 *
	 * @param travelCommercePriceService
	 * 		the travel commerce price service
	 */
	@Required
	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}
}
