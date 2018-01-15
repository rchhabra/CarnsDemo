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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.configurablebundleservices.bundle.BundleRuleService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.configurablebundleservices.model.ChangeProductPriceBundleRuleModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TransportOfferingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.util.PriceValue;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Implementation class for {@link TravelCommercePriceFacade}
 */
public class DefaultTravelCommercePriceFacade implements TravelCommercePriceFacade
{
	private static final String PRICING_LOOKUP_ATTRIBUTES_PROPERTY = "pricing.lookup.attributes";
	private static final String PRICE_LOOKUP_PROPERTY_DELIMITER = ",";

	private ProductService productService;
	private TravelCommercePriceService travelCommercePriceService;
	private TransportOfferingFacade transportOfferingFacade;
	private BundleRuleService bundleRuleService;
	private CommonI18NService commonI18NService;
	private TransportOfferingService transportOfferingService;
	private ConfigurationService configurationService;
	private TravelRulesService travelRulesService;
	private PriceDataFactory priceDataFactory;

	@Override
	public PriceInformation getPriceInformation(final String productCode)
	{
		final ProductModel product = getProductService().getProductForCode(productCode);
		return getTravelCommercePriceService().getPriceInformation(product, null, null);
	}

	@Override
	public PriceInformation getPriceInformation(final String productCode, final String searchKey, final String searchValue)
	{

		final ProductModel product = getProductService().getProductForCode(productCode);
		return getTravelCommercePriceService().getPriceInformation(product, searchKey, searchValue);
	}

	@Override
	public PriceInformation getPriceInformationByHierarchy(final String productCode, final String transportOfferingCode,
			final String sectorCode, final String routeCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);

		final String[] priceLookupAttributes = getPriceLookupAttributes();

		PriceInformation priceInfo = null;
		if (ArrayUtils.contains(priceLookupAttributes, PriceRowModel.TRANSPORTOFFERINGCODE))
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(productModel, PriceRowModel.TRANSPORTOFFERINGCODE,
					transportOfferingCode);
		}
		if (priceInfo == null && ArrayUtils.contains(priceLookupAttributes, PriceRowModel.TRAVELSECTORCODE))
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(productModel, PriceRowModel.TRAVELSECTORCODE,
					sectorCode);
		}
		if (priceInfo == null && ArrayUtils.contains(priceLookupAttributes, PriceRowModel.TRAVELROUTECODE))
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(productModel, PriceRowModel.TRAVELROUTECODE, routeCode);
		}
		if (priceInfo == null)
		{
			priceInfo = getTravelCommercePriceService().getPriceInformation(productModel, null, null);
		}
		return priceInfo;
	}

	/**
	 * This method returns an array of the attributes used for the pricing lookup. The array of the attributes used is
	 * taken from the property pricing.lookup.attributes
	 *
	 * @return the String array
	 *
	 */
	protected String[] getPriceLookupAttributes()
	{
		final String priceLookupAttributes = getConfigurationService().getConfiguration()
				.getString(PRICING_LOOKUP_ATTRIBUTES_PROPERTY);

		if (StringUtils.isBlank(priceLookupAttributes))
		{
			return new String[0];
		}

		return priceLookupAttributes.split(PRICE_LOOKUP_PROPERTY_DELIMITER);
	}

	@Override
	public PriceLevel getPriceLevelInfo(final String productCode, final List<String> transportOfferings, final String routeCode)
	{
		final boolean isMultiSectorRoute = getTransportOfferingFacade().isMultiSectorRoute(transportOfferings);
		return getTravelCommercePriceService().getPriceLevelInfo(productCode, transportOfferings, routeCode, isMultiSectorRoute);
	}

	@Override
	public PriceLevel getPriceLevelInfoByHierarchy(final ProductModel product, final String transportOfferingCode,
			final String routeCode)
	{
		return getTravelCommercePriceService().getPriceLevelInfoByHierarchy(product, transportOfferingCode, routeCode);
	}

	@Override
	public PriceInformation getPriceInformationByProductPriceBundleRule(final BundleTemplateModel bundleTemplate,
			final String productCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		if (productModel != null)
		{
			final ChangeProductPriceBundleRuleModel changePriceBundleRule = getBundleRuleService()
					.getChangePriceBundleRule(bundleTemplate, productModel, productModel, getCommonI18NService().getCurrentCurrency());
			if (changePriceBundleRule != null)
			{
				final PriceValue priceValue = new PriceValue(getCommonI18NService().getCurrentCurrency().getIsocode(),
						changePriceBundleRule.getPrice().doubleValue(), false);

				return new PriceInformation(priceValue);
			}
		}
		return null;
	}


	@Override
	public void setPriceAndTaxSearchCriteriaInContext(final PriceLevel priceLevel, final List<String> transportOfferingCodes,
			final TravellerData travellerData)
	{
		final PassengerInformationData travellerInfo = (PassengerInformationData) travellerData.getTravellerInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, passengerType);
	}

	@Override
	public void setPriceAndTaxSearchCriteriaInContext(final PriceLevel priceLevel, final List<String> transportOfferingCodes)
	{
		getTravelCommercePriceService().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, null);
	}

	@Override
	public void setPriceSearchCriteriaInContext(final PriceLevel priceLevel)
	{
		getTravelCommercePriceService().setPriceSearchCriteriaInContext(priceLevel);
	}


	@Override
	public void setTaxSearchCriteriaInContext(final List<String> transportOfferingCodes, final TravellerData travellerData)
	{
		final PassengerInformationData travellerInfo = (PassengerInformationData) travellerData.getTravellerInfo();
		final String passengerType = travellerInfo.getPassengerType().getCode().toLowerCase();
		getTravelCommercePriceService().setTaxSearchCriteriaInContext(transportOfferingCodes, passengerType);
	}


	@Override
	public boolean isPriceInformationAvailable(final ProductModel product, final String searchKey, final String searchValue)
	{
		return getTravelCommercePriceService().isPriceInformationAvailable(product, searchKey, searchValue);
	}


	@Override
	public void addPropertyPriceLevelToCartEntry(final PriceLevel priceLevel, final String productCode, final int entryNo)
	{
		getTravelCommercePriceService().addPropertyPriceLevelToCartEntry(priceLevel, productCode, entryNo);
	}

	@Override
	public BigDecimal getBookingFeesAndTaxes()
	{
		final double fee = getTravelRulesService().getTotalFee();
		return BigDecimal.valueOf(fee);
	}

	@Override
	public PriceData getPaidAmount(final AccommodationReservationData reservationData)
	{
		return createPriceData(reservationData.getTotalRate().getActualRate().getValue().doubleValue()
				- reservationData.getTotalToPay().getValue().doubleValue(), 2, reservationData.getCurrencyIso());
	}

	@Override
	public PriceData getDueAmount(final AccommodationReservationData reservationData, final PriceData amountPaid)
	{
		final double totalAmmount = reservationData.getTotalRate().getActualRate().getValue().doubleValue();
		if (totalAmmount > amountPaid.getValue().doubleValue())
		{
			return createPriceData(totalAmmount - amountPaid.getValue().doubleValue(), 2, reservationData.getCurrencyIso());
		}
		return null;
	}

	@Override
	public PriceData createPriceData(final double price)
	{
		return createPriceData(PriceDataType.BUY, BigDecimal.valueOf(price),
				getCommonI18NService().getCurrentCurrency().getIsocode());
	}

	@Override
	public PriceData createPriceData(final double price, final String currencyIsoCode)
	{
		return createPriceData(PriceDataType.BUY, BigDecimal.valueOf(price), currencyIsoCode);
	}

	@Override
	public PriceData createPriceData(final PriceDataType priceType, final BigDecimal value, final String currencyIso)
	{
		return getPriceDataFactory().create(priceType, value, currencyIso);
	}

	@Override
	public PriceData createPriceData(final double price, final int scale)
	{
		BigDecimal priceData = BigDecimal.valueOf(price);
		priceData = priceData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		return createPriceData(PriceDataType.BUY, priceData, getCommonI18NService().getCurrentCurrency().getIsocode());
	}

	@Override
	public PriceData createPriceData(final double price, final int scale, final String currencyIso)
	{
		BigDecimal priceData = BigDecimal.valueOf(price);
		priceData = priceData.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
		return createPriceData(PriceDataType.BUY, priceData, currencyIso);
	}

	@Override
	public PriceData createPriceData(final PriceDataType priceType, final BigDecimal value, final CurrencyModel currency)
	{
		return getPriceDataFactory().create(priceType, value, currency);
	}

	/**
	 * @return instance of productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return instance of travelCommercePriceService
	 */
	protected TravelCommercePriceService getTravelCommercePriceService()
	{
		return travelCommercePriceService;
	}

	/**
	 * @param travelCommercePriceService
	 */
	public void setTravelCommercePriceService(final TravelCommercePriceService travelCommercePriceService)
	{
		this.travelCommercePriceService = travelCommercePriceService;
	}

	/**
	 * @return the transportOfferingFacade
	 */
	protected TransportOfferingFacade getTransportOfferingFacade()
	{
		return transportOfferingFacade;
	}

	/**
	 * @param transportOfferingFacade
	 *           the transportOfferingFacade to set
	 */
	public void setTransportOfferingFacade(final TransportOfferingFacade transportOfferingFacade)
	{
		this.transportOfferingFacade = transportOfferingFacade;
	}

	/**
	 * @return the bundleRuleService
	 */
	protected BundleRuleService getBundleRuleService()
	{
		return bundleRuleService;
	}

	/**
	 * @param bundleRuleService
	 *           the bundleRuleService to set
	 */
	public void setBundleRuleService(final BundleRuleService bundleRuleService)
	{
		this.bundleRuleService = bundleRuleService;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * @param transportOfferingService
	 *           the transportOfferingService to set
	 */
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * @return the configurationService
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the travelRulesService
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * @param travelRulesService
	 *           the travelRulesService to set
	 */
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 * @return the priceDataFactory
	 */
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}
}
