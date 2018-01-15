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

package de.hybris.platform.travelfacades.fare.search.handlers.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.LocationData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ScheduledRouteData;
import de.hybris.platform.commercefacades.travel.TaxData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.travel.TravelRouteData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.TransportFacilityFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.fare.search.handlers.FareSearchHandler;
import de.hybris.platform.travelfacades.util.PricingUtils;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Concrete implementation of the {@link FareSearchHandler} interface. Handler is responsible for populating the taxed
 * for all the scheduled routes on the {@link FareSelectionData}
 */
public class TaxHandler implements FareSearchHandler
{
	private static final Logger LOG = Logger.getLogger(TaxHandler.class);

	private TransportFacilityFacade transportFacilityFacade;
	private TravelCommercePriceService travelCommercePriceService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private ProductService productService;
	private SessionService sessionService;
	private BaseStoreService baseStoreService;
	private ConfigurationService configurationService;
	private Map<String, String> offerGroupToOriginDestinationMapping;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void handle(final List<ScheduledRouteData> scheduledRoutes, final FareSearchRequestData fareSearchRequestData,
			final FareSelectionData fareSelectionData)
	{
		for (final PricedItineraryData pricedItinerary : fareSelectionData.getPricedItineraries())
		{
			if (!pricedItinerary.isAvailable())
			{
				continue;
			}
			final List<String> transportFacilityCodes = new ArrayList<String>();
			final List<String> countryCodes = new ArrayList<String>();
			populateTransportFacilityInfoFromItinerary(pricedItinerary, transportFacilityCodes, countryCodes);

			for (final ItineraryPricingInfoData itineraryPricingInfoData : pricedItinerary.getItineraryPricingInfos())
			{
				if (!itineraryPricingInfoData.isAvailable())
				{
					continue;
				}
				evaluateTaxesForPTCs(transportFacilityCodes, countryCodes, itineraryPricingInfoData, pricedItinerary);
			}
		}
	}

	/**
	 * Evaluate taxes for pt cs.
	 *
	 * @deprecated since Travel Accelerator 3.0 - please use evaluateTaxesForPTCs(transportFacilityCodes, countryCodes,
	 * itineraryPricingInfos, pricedItinerary)
	 *
	 * @param transportFacilityCodes
	 *           the transport facility codes
	 * @param countryCodes
	 *           the country codes
	 * @param itineraryPricingInfoData
	 *           the itinerary pricing info data
	 */
	protected void evaluateTaxesForPTCs(final List<String> transportFacilityCodes, final List<String> countryCodes,
			final ItineraryPricingInfoData itineraryPricingInfoData)
	{
		evaluateTaxesForPTCs(transportFacilityCodes, countryCodes, itineraryPricingInfoData, null);
	}

	/**
	 * Evaluate taxes for pt cs.
	 *
	 * @param transportFacilityCodes
	 * 		the transport facility codes
	 * @param countryCodes
	 * 		the country codes
	 * @param itineraryPricingInfoData
	 * 		the itinerary pricing info data
	 * @param pricedItinerary
	 * 		priced itinerary
	 */
	protected void evaluateTaxesForPTCs(final List<String> transportFacilityCodes, final List<String> countryCodes,
			final ItineraryPricingInfoData itineraryPricingInfoData, final PricedItineraryData pricedItinerary)
	{
		if (pricedItinerary == null)
		{
			return;
		}

		final String priceDisplayPassengerType = getConfigurationService().getConfiguration()
				.getString(TravelfacadesConstants.PRICE_DISPLAY_PASSENGER_TYPE);

		final List<PTCFareBreakdownData> nonEmptyPTCFareBreakdowns = itineraryPricingInfoData.getPtcFareBreakdownDatas().stream()
				.filter(ptc -> (ptc.getPassengerTypeQuantity().getQuantity() > 0) || StringUtils
						.equalsIgnoreCase(priceDisplayPassengerType, ptc.getPassengerTypeQuantity().getPassengerType().getCode()))
				.collect(Collectors.toList());


		for (final PTCFareBreakdownData ptcData : nonEmptyPTCFareBreakdowns)
		{
			final FareProductData fareProduct = ptcData.getFareInfos().get(0).getFareDetails().get(0).getFareProduct();
			final String passengerType = ptcData.getPassengerTypeQuantity().getPassengerType().getCode();

			final List<TaxData> taxes = getTaxesForProduct(fareProduct, transportFacilityCodes, countryCodes, passengerType);

			//calculate tax for each ancillary/included product.
			calculateTaxForExtras(itineraryPricingInfoData, taxes, passengerType, pricedItinerary);

			final List<TaxData> totalTaxes = new ArrayList<>();
			int taxQuantity = 0;
			do
			{
				totalTaxes.addAll(taxes);
				taxQuantity++;
			}
			while (taxQuantity < ptcData.getPassengerTypeQuantity().getQuantity());
			updateGrossPrice(ptcData, totalTaxes);
			ptcData.getPassengerFare().setTaxes(totalTaxes);
		}
	}

	/**
	 * Method to calculate tax for every product included in bundle.
	 *
	 * @param itineraryPricingInfoData
	 * @param taxes
	 * @param passengerType
	 */
	protected void calculateTaxForExtras(final ItineraryPricingInfoData itineraryPricingInfoData, final List<TaxData> taxes,
			final String passengerType, final PricedItineraryData pricedItinerary)
	{
		final Map<String, String> transportOfferingCountryCodeMap = new HashMap<>();
		itineraryPricingInfoData.getBundleTemplates().forEach(bundleTemplateData -> {

			//calculate tax for non fare products.
			bundleTemplateData.getNonFareProducts().values().forEach(productDatas -> productDatas.forEach(productData -> {
				calculateTaxForProduct(productData, bundleTemplateData, taxes, transportOfferingCountryCodeMap, passengerType,
						pricedItinerary);
			}));

			//calculate tax for included products.
			bundleTemplateData.getIncludedAncillaries()
					.forEach(includedAncillaryData -> includedAncillaryData.getProducts().forEach(productData -> {
						calculateTaxForProduct(productData, bundleTemplateData, taxes, transportOfferingCountryCodeMap, passengerType,
								pricedItinerary);
					}));
		});
	}

	/**
	 * Method to calculate tax for every product on the basis of whether given product is per transportOfferingCode or
	 * per route.
	 *
	 * @param productData
	 * @param bundleTemplateData
	 * @param taxes
	 * @param transportOfferingCountryCodeMap
	 * @param passengerType
	 */
	protected void calculateTaxForProduct(final ProductData productData, final TravelBundleTemplateData bundleTemplateData,
			final List<TaxData> taxes, final Map<String, String> transportOfferingCountryCodeMap, final String passengerType,
			final PricedItineraryData pricedItinerary)
	{
		//check if the product is per transportOfferingCode or per route.
		if (checkIfProductPerTO(productData))
		{
			bundleTemplateData.getTransportOfferings().forEach(transportOffering -> {
				taxes.addAll(calculateTax(transportOffering, transportOfferingCountryCodeMap, productData, passengerType));
			});
		}
		else
		{
			final TravelRouteData travelRouteData = pricedItinerary.getItinerary().getRoute();
			if(Objects.nonNull(travelRouteData))
			{
				taxes.addAll(getTaxesForProduct(productData, Arrays.asList(travelRouteData.getOrigin().getCode()),
						Arrays.asList(travelRouteData.getOrigin().getLocation().getCode()), passengerType));
			}
		}
	}

	/**
	 * Method to calculate tax for given {@link ProductData}, {@link TransportOfferingData} and passengerType
	 *
	 * @param transportOffering
	 * @param transportOfferingCountryCodeMap
	 * @param productData
	 * @param passengerType
	 */
	protected List<TaxData> calculateTax(final TransportOfferingData transportOffering,
			final Map<String, String> transportOfferingCountryCodeMap, final ProductData productData, final String passengerType)
	{
		String countryCode = transportOfferingCountryCodeMap.get(transportOffering.getCode());
		if (StringUtils.isBlank(countryCode))
		{
			final LocationData countryData = getTransportFacilityFacade()
					.getCountry(transportOffering.getSector().getOrigin().getCode());
			if (Objects.nonNull(countryData))
			{
				countryCode = countryData.getCode();
				transportOfferingCountryCodeMap.put(transportOffering.getCode(), countryCode);
			}
		}
		return getTaxesForProduct(productData, Arrays.asList(transportOffering.getSector().getOrigin().getCode()),
				Arrays.asList(countryCode), passengerType);
	}

	/**
	 * Returns true if given {@link ProductData} is per transport offering otherwise false.
	 *
	 * @param productData
	 */
	protected boolean checkIfProductPerTO(final ProductData productData)
	{
		final String categoryCode = productData.getCategories().stream().findFirst().get().getCode();
		final String mapping = getOfferGroupToOriginDestinationMapping().getOrDefault(categoryCode,
				getOfferGroupToOriginDestinationMapping().getOrDefault(TravelservicesConstants.DEFAULT_OFFER_GROUP_TO_OD_MAPPING,
						TravelservicesConstants.TRAVEL_ROUTE));
		return StringUtils.equalsIgnoreCase(mapping, TravelfacadesConstants.TRANSPORT_OFFERING);
	}

	/**
	 * Method to update base price. If the prices are to be calculated in gross, taxes will be added to base price.
	 *
	 * @param ptcData
	 *           the ptc data
	 * @param taxes
	 *           the taxes
	 */
	protected void updateGrossPrice(final PTCFareBreakdownData ptcData, final List<TaxData> taxes)
	{
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		if (currentBaseStore.isNet())
		{
			return;
		}
		// For Gross price, the base price should include taxes.
		final BigDecimal basePrice = ptcData.getPassengerFare().getBaseFare().getValue();
		final BigDecimal totalPrice = basePrice.add(PricingUtils.getTotalTaxValue(taxes));
		ptcData.getPassengerFare().setBaseFare(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue(), 2,
				ptcData.getPassengerFare().getBaseFare().getCurrencyIso()));
	}

	/**
	 * Populate transport facility info from itinerary.
	 *
	 * @param pricedItinerary
	 *           the priced itinerary
	 * @param transportFacilityCodes
	 *           the transport facility codes
	 * @param countryCodes
	 *           the country codes
	 */
	protected void populateTransportFacilityInfoFromItinerary(final PricedItineraryData pricedItinerary,
			final List<String> transportFacilityCodes, final List<String> countryCodes)
	{
		for (final OriginDestinationOptionData originDestinationOptionData : pricedItinerary.getItinerary()
				.getOriginDestinationOptions())
		{
			for (final TransportOfferingData transportOffering : originDestinationOptionData.getTransportOfferings())
			{
				final LocationData countryData = getTransportFacilityFacade()
						.getCountry(transportOffering.getSector().getOrigin().getCode());
				transportFacilityCodes.add(transportOffering.getSector().getOrigin().getCode());
				countryCodes.add(countryData.getCode());
			}
		}
	}

	/**
	 * Method to retrieve taxes for the product based on transportFacility, country and passenger type.
	 *
	 * @param productData
	 *           the product data
	 * @param transportFacilityCodes
	 *           the transport facility codes
	 * @param countryCodes
	 *           the country codes
	 * @param passengerType
	 *           the passenger type
	 * @return taxes for product
	 */
	protected List<TaxData> getTaxesForProduct(final ProductData productData, final List<String> transportFacilityCodes,
			final List<String> countryCodes, final String passengerType)
	{
		setTaxSearchCriteriaInContext(transportFacilityCodes, countryCodes, passengerType);
		final ProductModel product = getProductService().getProductForCode(productData.getCode());
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieving tax informations for product:" + product.getCode() + ", transport facility "
					+ transportFacilityCodes + ", country " + countryCodes + " and passenger type:" + passengerType);
		}
		final List<TaxInformation> taxInfos = getTravelCommercePriceService().getProductTaxInformations(product);
		return createTaxData(productData.getPrice(), taxInfos);
	}

	/**
	 * Method to convert the Tax Information to Tax Data objects. Tax amount is calculated based on the absolute/relative
	 * value.
	 *
	 * @param priceData
	 *           the price data
	 * @param taxInfos
	 *           the tax infos
	 * @return list of TaxData objects.
	 */
	protected List<TaxData> createTaxData(final PriceData priceData, final List<TaxInformation> taxInfos)
	{
		final List<TaxData> taxes = new ArrayList<TaxData>();
		for (final TaxInformation taxInfo : taxInfos)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Tax code:" + taxInfo.getTaxValue().getCode() + " and value:" + taxInfo.getTaxValue().getValue());
			}
			final TaxData taxData = new TaxData();
			taxData.setCode(taxInfo.getTaxValue().getCode());

			double taxAmount;
			if (taxInfo.getTaxValue().isAbsolute())
			{
				taxAmount = taxInfo.getTaxValue().getValue();
			}
			else
			{
				final double productPrice = priceData.getValue().doubleValue();
				final double relativeTotalTaxRate = taxInfo.getTaxValue().getValue() / 100.0;
				taxAmount = productPrice * relativeTotalTaxRate;
			}
			taxData.setPrice(
					getTravelCommercePriceFacade().createPriceData(taxAmount, 2, taxInfo.getTaxValue().getCurrencyIsoCode()));
			taxes.add(taxData);
		}
		return taxes;
	}

	/**
	 * Method to create a new PriceData Object using PriceDataFactory
	 *
	 * @param priceValue
	 *           the price value
	 * @param currencyIsoCode
	 *           the currency iso code
	 *
	 * @deprecated Deprecated since version 3.0.
	 * @return PriceData price data
	 */
	@Deprecated
	protected PriceData createPriceData(final double priceValue, final String currencyIsoCode)
	{
		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue).setScale(2, RoundingMode.HALF_UP),
				currencyIsoCode);
	}

	/**
	 * Method to set values in Session Context. These values will be used to retrieve taxes during cart calculation when
	 * the product is added to cart.
	 *
	 * @param transportFacilityCodes
	 *           the transport facility codes
	 * @param countryCodes
	 *           the country codes
	 * @param passengerType
	 *           the passenger type
	 */
	protected void setTaxSearchCriteriaInContext(final List<String> transportFacilityCodes, final List<String> countryCodes,
			final String passengerType)
	{
		final List<String> passengerTypeList = new ArrayList<String>();
		passengerTypeList.add(passengerType.toLowerCase());

		final Map<String, List<String>> params = new HashMap<String, List<String>>();
		params.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, transportFacilityCodes);
		params.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, countryCodes);
		params.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, passengerTypeList);

		getSessionService().setAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP, params);
	}

	/**
	 * Gets transport facility facade.
	 *
	 * @return instance of transportFacilityFacade
	 */
	protected TransportFacilityFacade getTransportFacilityFacade()
	{
		return transportFacilityFacade;
	}

	/**
	 * Sets transport facility facade.
	 *
	 * @param transportFacilityFacade
	 *           the transport facility facade
	 */
	@Required
	public void setTransportFacilityFacade(final TransportFacilityFacade transportFacilityFacade)
	{
		this.transportFacilityFacade = transportFacilityFacade;
	}

	/**
	 * Gets travel commerce price service.
	 *
	 * @return instance of travelCommercePriceService
	 */
	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	/**
	 * Sets travel commerce price service.
	 *
	 * @param travelCommercePriceService
	 *           the travel commerce price service
	 */
	@Required
	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}

	/**
	 * Gets product service.
	 *
	 * @return instance of productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * Sets product service.
	 *
	 * @param productService
	 *           the product service
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * Gets price data factory.
	 *
	 * @return instance of priceDataFactory
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @param priceDataFactory
	 * 		the price data factory
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets session service.
	 *
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets base store service.
	 *
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * Sets base store service.
	 *
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
	 *           the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets offer group to origin destination mapping.
	 *
	 * @return the offer group to origin destination mapping
	 */
	protected Map<String, String> getOfferGroupToOriginDestinationMapping()
	{
		return offerGroupToOriginDestinationMapping;
	}

	/**
	 * Sets offer group to origin destination mapping.
	 *
	 * @param offerGroupToOriginDestinationMapping
	 * 		the offer group to origin destination mapping
	 */
	@Required
	public void setOfferGroupToOriginDestinationMapping(final Map<String, String> offerGroupToOriginDestinationMapping)
	{
		this.offerGroupToOriginDestinationMapping = offerGroupToOriginDestinationMapping;
	}
}
