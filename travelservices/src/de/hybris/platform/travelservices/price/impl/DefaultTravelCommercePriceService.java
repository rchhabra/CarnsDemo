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
package de.hybris.platform.travelservices.price.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.price.impl.DefaultCommercePriceService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.jalo.TravelPriceFactory;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of {@link TravelCommercePriceService}
 */
public class DefaultTravelCommercePriceService extends DefaultCommercePriceService implements TravelCommercePriceService
{

	private static final Logger LOG = Logger.getLogger(DefaultTravelCommercePriceService.class);

	private ModelService modelService;
	private ProductService productService;
	private Map<String, String> offerGroupToOriginDestinationMapping;
	private TransportOfferingService transportOfferingService;
	private SessionService sessionService;
	private TransportFacilityService transportFacilityService;
	private CartService cartService;
	private TravelCommerceCartService travelCommerceCartService;
	private TravellerService travellerService;
	private TimeService timeService;

	@Override
	public PriceInformation getProductWebPrice(final ProductModel product, final Map<String, String> searchCriteria)
	{
		validateParameterNotNull(product, "Product model cannot be null");
		List<PriceInformation> prices = null;
		try
		{
			final Product productItem = getModelService().getSource(product);
			prices = getCurrentPriceFactory().getProductPriceInformations(productItem, searchCriteria);
		}
		catch (final JaloPriceFactoryException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Retrieving price information failed for product " + product.getCode() + " with message " + e.getMessage(),
						e);
			}
		}

		if (CollectionUtils.isNotEmpty(prices))
		{
			PriceInformation minPriceForLowestQuantity = null;
			for (final PriceInformation price : prices)
			{
				if (minPriceForLowestQuantity == null || (((Long) minPriceForLowestQuantity.getQualifierValue("minqtd"))
						.longValue() > ((Long) price.getQualifierValue("minqtd")).longValue()))
				{
					minPriceForLowestQuantity = price;
				}
			}
			return minPriceForLowestQuantity;
		}
		return null;
	}

	protected TravelPriceFactory getCurrentPriceFactory()
	{
		return (TravelPriceFactory) OrderManager.getInstance().getPriceFactory();
	}

	@Override
	public List<TaxInformation> getProductTaxInformations(final ProductModel product)
	{
		List<TaxInformation> taxes = Collections.emptyList();
		final Product productItem = getModelService().getSource(product);
		try
		{
			taxes = getCurrentPriceFactory().getProductTaxInformations(JaloSession.getCurrentSession().getSessionContext(),
					productItem, getTimeService().getCurrentTime());
		}
		catch (final JaloPriceFactoryException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Retrieving tax information failed for product " + product.getCode() + " with message " + e.getMessage(),
						e);
			}
		}
		return taxes;
	}

	@Override
	public PriceInformation getPriceInformation(final ProductModel product, final String searchKey, final String searchValue)
	{
		Map<String, String> searchCriteria = new HashMap<>();

		if (searchKey == null)
		{
			searchCriteria = null;
		}
		else
		{
			searchCriteria.put(searchKey, searchValue);
		}
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Getting price information for productData (code: " + product.getCode() + ") and searchKey (key: " + searchKey
					+ ")");
		}
		final PriceInformation priceInfo = getProductWebPrice(product, searchCriteria);

		if (priceInfo == null && LOG.isDebugEnabled())
		{
			LOG.debug(
					"No price information for productData (code: " + product.getCode() + ") and searchKey (key: " + searchKey + ")");
		}
		return priceInfo;
	}

	@Override
	public PriceLevel getPriceLevelInfo(final String productCode, final List<String> transportOfferings, final String routeCode,
			final boolean isMultiSectorRoute)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		if (!(ProductType.FARE_PRODUCT.equals(productModel.getProductType()) || productModel instanceof FareProductModel))
		{
			final String transportOfferingCode = CollectionUtils.isNotEmpty(transportOfferings) ? transportOfferings.get(0) : null;
			return getPriceLevelInfoForAncillary(productModel, transportOfferingCode, routeCode);
		}
		else
		{
			return getPriceLevelInfoForFareProduct(productModel, transportOfferings, routeCode, isMultiSectorRoute);
		}
	}

	@Override
	public PriceLevel getPriceLevelInfoForFareProduct(final ProductModel productModel, final List<String> transportOfferings,
			final String routeCode, final boolean isMultiSectorRoute)
	{
		if (isMultiSectorRoute)
		{
			final PriceLevel priceLevel = new PriceLevel();
			if (isPriceInformationAvailable(productModel, PriceRowModel.TRAVELROUTECODE, routeCode))
			{
				priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
				priceLevel.setValue(routeCode);
			} else {
				priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
			}
			return priceLevel;
		}
		return getPriceLevelInfoByHierarchy(productModel, transportOfferings.get(0), routeCode);
	}

	/**
	 * Method to return price level info as a map, with price level as key and code as value.
	 *
	 * @param productModel
	 * @param transportOfferingCode
	 * @param routeCode
	 * @return price level info
	 */
	@Override
	public PriceLevel getPriceLevelInfoForAncillary(final ProductModel productModel, final String transportOfferingCode,
			final String routeCode)
	{
		final String offerGroupCode = productModel.getSupercategories().stream().findFirst().get().getCode();
		final String priceLevelMapping = getOfferGroupToOriginDestinationMapping().getOrDefault(offerGroupCode,
				getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
						TravelservicesConstants.TRAVEL_ROUTE));
		if (StringUtils.equalsIgnoreCase(priceLevelMapping, TravelservicesConstants.TRAVEL_ROUTE))
		{
			final PriceLevel priceLevel = new PriceLevel();
			if (routeCode != null && isPriceInformationAvailable(productModel, PriceRowModel.TRAVELROUTECODE, routeCode))
			{
				priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_ROUTE);
				priceLevel.setValue(routeCode);
			}
			else
			{
				priceLevel.setCode(TravelservicesConstants.PRICING_LEVEL_DEFAULT);
			}
			return priceLevel;
		}
		else
		{
			return getPriceLevelInfoByHierarchy(productModel, transportOfferingCode, routeCode);
		}
	}

	@Override
	public PriceLevel getPriceLevelInfoByHierarchy(final ProductModel product, final String transportOfferingCode,
			final String routeCode)
	{
		String priceLevel = TravelservicesConstants.PRICING_LEVEL_TRANSPORT_OFFERING;
		String priceSearchValue = transportOfferingCode;
		PriceInformation priceInfo = getPriceInformation(product, PriceRowModel.TRANSPORTOFFERINGCODE, transportOfferingCode);
		if (priceInfo == null)
		{
			final TransportOfferingModel transportOffering = getTransportOfferingService()
					.getTransportOffering(transportOfferingCode);
			priceLevel = TravelservicesConstants.PRICING_LEVEL_SECTOR;
			priceSearchValue = transportOffering.getTravelSector().getCode();
			priceInfo = getPriceInformation(product, PriceRowModel.TRAVELSECTORCODE, priceSearchValue);
		}
		if (priceInfo == null)
		{
			priceLevel = TravelservicesConstants.PRICING_LEVEL_ROUTE;
			priceSearchValue = routeCode;
			priceInfo = getPriceInformation(product, PriceRowModel.TRAVELROUTECODE, routeCode);
		}
		// Default price row has been chosen for display, so price level will be null.
		if (priceInfo == null)
		{
			priceLevel = TravelservicesConstants.PRICING_LEVEL_DEFAULT;
			priceSearchValue = null;
		}
		final PriceLevel priceLevelObj = new PriceLevel();
		priceLevelObj.setCode(priceLevel);
		priceLevelObj.setValue(priceSearchValue);
		return priceLevelObj;
	}

	@Override
	public boolean isPriceInformationAvailable(final ProductModel product, final String searchKey, final String searchValue)
	{
		final PriceInformation priceInfo = getPriceInformation(product, searchKey, searchValue);
		if (priceInfo == null)
		{
			return Boolean.FALSE;
		}
		else
		{
			return Boolean.TRUE;
		}
	}

	@Override
	public void setPriceAndTaxSearchCriteriaInContext(final PriceLevel priceLevel, final List<String> transportOfferingCodes,
			final String passengerType)
	{
		setPriceSearchCriteriaInContext(priceLevel);
		setTaxSearchCriteriaInContext(transportOfferingCodes, passengerType);
	}

	@Override
	public void setPriceSearchCriteriaInContext(final PriceLevel priceLevel)
	{
		final Map<String, String> params = new HashMap<>();
		if (priceLevel != null && !TravelservicesConstants.PRICING_LEVEL_DEFAULT.equals(priceLevel.getCode()))
		{
			String key = StringUtils.EMPTY;
			if (TravelservicesConstants.PRICING_LEVEL_TRANSPORT_OFFERING.equals(priceLevel.getCode()))
			{
				key = TravelservicesConstants.PRICE_ROW_TRANSPORT_OFFERING;
			}
			else if (TravelservicesConstants.PRICING_LEVEL_SECTOR.equals(priceLevel.getCode()))
			{
				key = TravelservicesConstants.PRICE_ROW_SECTOR;
			}
			else if (TravelservicesConstants.PRICING_LEVEL_ROUTE.equals(priceLevel.getCode()))
			{
				key = TravelservicesConstants.PRICE_ROW_ROUTE;
			}
			params.put(key, priceLevel.getValue());
		}
		getSessionService().setAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP, params);
	}

	@Override
	public void setTaxSearchCriteriaInContext(final List<String> transportOfferingCodes, final String passengerType)
	{
		final List<String> transportFacilityCodes = new ArrayList<>();
		final List<String> countryCodes = new ArrayList<>();
		final List<String> passengerTypeList = new ArrayList<>();
		if (transportOfferingCodes != null)
		{
			for (final String transportOffering : transportOfferingCodes)
			{
				final TransportOfferingModel transportOfferingModel = getTransportOfferingService()
						.getTransportOffering(transportOffering);
				final TransportFacilityModel transportFacilityModel = transportOfferingModel.getTravelSector().getOrigin();
				final LocationModel countryModel = getTransportFacilityService().getCountry(transportFacilityModel);

				transportFacilityCodes.add(transportFacilityModel.getCode());
				countryCodes.add(countryModel.getCode());
			}
		}
		if (passengerType != null)
		{
			passengerTypeList.add(passengerType);
		}

		final Map<String, List<String>> params = new HashMap<>();
		params.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, transportFacilityCodes);
		params.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, countryCodes);
		params.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, passengerTypeList);

		getSessionService().setAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP, params);
	}

	@Override
	public void addPropertyPriceLevelToCartEntry(final PriceLevel priceLevel, final String productCode, final int entryNo)
	{
		if (priceLevel == null)
		{
			return;
		}
		final Map<String, Object> params = new HashMap<>();
		params.put(TravelOrderEntryInfoModel.PRICELEVEL, priceLevel.getCode());
		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);
		getTravelCommerceCartService().addPropertiesToCartEntry(cartModel, entryNo, product, params);
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}

	/**
	 * Gets model service.
	 *
	 * @return model service
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
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transport offering service
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transport offering service
	 */
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
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
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets transport facility service.
	 *
	 * @return the transport facility service
	 */
	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	/**
	 * Sets transport facility service.
	 *
	 * @param transportFacilityService
	 * 		the transport facility service
	 */
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

	/**
	 * Gets cart service.
	 *
	 * @return the cart service
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * Sets cart service.
	 *
	 * @param cartService
	 * 		the cart service
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * Gets travel commerce cart service.
	 *
	 * @return the travel commerce cart service
	 */
	protected TravelCommerceCartService getTravelCommerceCartService()
	{
		return travelCommerceCartService;
	}

	/**
	 * Sets travel commerce cart service.
	 *
	 * @param travelCommerceCartService
	 * 		the travel commerce cart service
	 */
	public void setTravelCommerceCartService(final TravelCommerceCartService travelCommerceCartService)
	{
		this.travelCommerceCartService = travelCommerceCartService;
	}

	/**
	 * Gets traveller service.
	 *
	 * @return the traveller service
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 * 		the traveller service
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * Gets time service.
	 *
	 * @return timeService time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

}
