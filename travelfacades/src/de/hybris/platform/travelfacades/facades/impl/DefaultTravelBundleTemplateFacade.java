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

package de.hybris.platform.travelfacades.facades.impl;

import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.UpgradeOptionData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade that provides bundle related functionalities
 */
public class DefaultTravelBundleTemplateFacade implements TravelBundleTemplateFacade
{
	private TypeService typeService;
	private SessionService sessionService;
	private TransportOfferingService transportOfferingService;
	private ProductService productService;
	private TravelCommerceStockService commerceStockService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private BundleTemplateService bundleTemplateService;

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param pricedItinerary
	 * 		the priced itinerary
	 * @param bundleType
	 * 		the bundle type
	 *
	 * @return
	 */
	@Override
	@Deprecated
	public UpgradeOptionData createUpgradableOptionsData(final PricedItineraryData pricedItinerary, final String bundleType)
	{
		UpgradeOptionData upgradeOptionDataPerLeg = null;
		final List<ItineraryPricingInfoData> listPerLeg = new ArrayList<>();
		createUpgradableItineraryPricingInfoPerLeg(listPerLeg, pricedItinerary, bundleType);
		if (CollectionUtils.isNotEmpty(listPerLeg))
		{
			upgradeOptionDataPerLeg = new UpgradeOptionData();
			upgradeOptionDataPerLeg.setOriginDestinationReferenceNumber(pricedItinerary.getOriginDestinationRefNumber());
			upgradeOptionDataPerLeg.setItineraryPricingInfos(listPerLeg);
		}
		return upgradeOptionDataPerLeg;
	}

	/**
	 * method to create the list of itineraryPricingInfoData for each itinerary with each element in the list having
	 * bundle type of higher sequence number than the selected bundle's sequence number
	 *
	 * @param listPerLeg
	 *           the list per leg
	 * @param pricedItineraryData
	 *           the priced itinerary data
	 * @param selectedBundleType
	 *           the selected bundle type
	 *
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected void createUpgradableItineraryPricingInfoPerLeg(final List<ItineraryPricingInfoData> listPerLeg,
			final PricedItineraryData pricedItineraryData, final String selectedBundleType)
	{
		final List<ItineraryPricingInfoData> pricingInfoDataList = pricedItineraryData.getItineraryPricingInfos();
		final Optional<ItineraryPricingInfoData> selectedItineraryPricingInfoData = pricedItineraryData.getItineraryPricingInfos()
				.stream()
				.filter(itineraryPricingInfoData -> StringUtils.equals(selectedBundleType, itineraryPricingInfoData.getBundleType()))
				.findFirst();
		if (!selectedItineraryPricingInfoData.isPresent())
		{
			return;
		}

		final int selectedBundleSeqNumber = getSequenceNumber(selectedBundleType);
		for (final ItineraryPricingInfoData itineraryPricingInfoData : pricingInfoDataList)
		{
			final int seqNumber = getSequenceNumber(itineraryPricingInfoData.getBundleType());

			if (seqNumber > selectedBundleSeqNumber)
			{
				createUpgradeItineraryPricingInfoTotalPriceData(
						selectedItineraryPricingInfoData.get().getTotalFare().getTotalPrice().getValue(), itineraryPricingInfoData);
				listPerLeg.add(itineraryPricingInfoData);
			}
		}
	}

	@Override
	public void createUpgradeItineraryPricingInfoTotalPriceData(final BigDecimal selectedBundleTotalPrice,
			final ItineraryPricingInfoData availableUpgradeItineraryPricingInfoData)
	{
		final BigDecimal upgradeBundleTotalPrice = availableUpgradeItineraryPricingInfoData.getTotalFare().getTotalPrice()
				.getValue();
		final double diffAmountForUpgrade = (upgradeBundleTotalPrice.subtract(selectedBundleTotalPrice)).doubleValue();
		availableUpgradeItineraryPricingInfoData.getTotalFare()
				.setTotalPrice(getTravelCommercePriceFacade().createPriceData(diffAmountForUpgrade, 2));
	}

	@Override
	public String getMasterBundleTemplateId(final String bundleTemplateId)
	{
		final BundleTemplateModel bundleTemplateModel = getBundleTemplateService().getBundleTemplateForCode(bundleTemplateId);

		return getBundleTemplateService().getRootBundleTemplate(bundleTemplateModel).getId();
	}

	/**
	 * method to calculate the sequence number of any bundle type
	 */
	@Override
	public int getSequenceNumber(final String bundleType)
	{
		return getTypeService().getEnumerationValue(BundleType.valueOf(bundleType)).getSequenceNumber();
	}

	/**
	 * method to find those itineraryPricingInfoData objects out of a pricedItinerary which have selected attribute set
	 * to true i.e. which a customer has selected while booking a journey
	 */
	@Override
	public ItineraryPricingInfoData getSelectedItineraryPricingInfoData(final PricedItineraryData pricedItineraryData)
	{
		final Optional<ItineraryPricingInfoData> selectedItineraryPricingInfoData = pricedItineraryData.getItineraryPricingInfos()
				.stream().filter(ItineraryPricingInfoData::isSelected).findFirst();
		return selectedItineraryPricingInfoData.isPresent() ? selectedItineraryPricingInfoData.get() : null;

	}

	/**
	 * Gets type service.
	 *
	 * @return the type service
	 */
	protected TypeService getTypeService()
	{
		return typeService;
	}

	/**
	 * Sets type service.
	 *
	 * @param typeService
	 *           the type service
	 */
	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
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
	 *           the session service
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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
	 *           the transport offering service
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * Gets product service.
	 *
	 * @return the product service
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
	 * Gets commerce stock service.
	 *
	 * @return the commerce stock service
	 */
	protected TravelCommerceStockService getCommerceStockService()
	{
		return commerceStockService;
	}

	/**
	 * Sets commerce stock service.
	 *
	 * @param commerceStockService
	 *           the commerce stock service
	 */
	@Required
	public void setCommerceStockService(final TravelCommerceStockService commerceStockService)
	{
		this.commerceStockService = commerceStockService;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * @return the bundleTemplateService
	 */
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	/**
	 * @param bundleTemplateService
	 *           the bundleTemplateService to set
	 */
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}
}
