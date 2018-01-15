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
package de.hybris.platform.ndcservices.services.impl;

import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.ndcservices.services.NDCOrderService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.price.TravelCommercePriceService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.strategies.TravelOrderCodeGenerationStrategy;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link NDCOrderService}
 */
public class DefaultNDCOrderService implements NDCOrderService
{
	private static final String UNIT_CODE = "pieces";

	private CommonI18NService commonI18NService;
	private BaseSiteService baseSiteService;
	private BaseStoreService baseStoreService;
	private ModelService modelService;
	private UnitService unitService;
	private TravelRouteService travelRouteService;
	private TravelCommercePriceService travelCommercePriceService;
	private TravelOrderCodeGenerationStrategy travelOrderCodeGenerationStrategy;

	@Override
	public void setOrderBasicInformation(final OrderModel order, final String currency)
	{
		order.setDate(new Date());
		order.setSite(getBaseSiteService().getCurrentBaseSite());
		order.setStore(getBaseStoreService().getCurrentBaseStore());
		order.setLanguage(getCommonI18NService().getCurrentLanguage());
		order.setDiscountsIncludePaymentCost(false);
		order.setSalesApplication(SalesApplication.NDC);
		order.setNet(true);
		order.setCurrency(getCommonI18NService().getCurrency(currency));
		order.setBookingJourneyType(BookingJourneyType.BOOKING_TRANSPORT_ONLY);
	}

	@Override
	public OrderEntryModel populateOrderEntry(final OrderModel order, final ProductModel product,
			final BundleTemplateModel bundleTemplate, final int bundleNo, final String ndcOfferItemId,
			final List<TransportOfferingModel> transportOfferings, final List<TravellerModel> travellers, final String routeCode,
			final int originDestinationRefNumber, final int quantity)
	{
		final OrderEntryModel orderEntry = getModelService().create(OrderEntryModel.class);
		final TravelOrderEntryInfoModel travelOrderEntryInfo = new TravelOrderEntryInfoModel();
		populateTravelOrderEntryInfoModel(travelOrderEntryInfo, product, originDestinationRefNumber, transportOfferings, routeCode,
				travellers);

		orderEntry.setProduct(product);
		orderEntry.setUnit(getUnitService().getUnitForCode(UNIT_CODE));
		orderEntry.setQuantity((long) quantity);
		orderEntry.setRejected(false);
		orderEntry.setActive(true);
		orderEntry.setType(OrderEntryType.TRANSPORT);
		orderEntry.setAmendStatus(AmendStatus.NEW);
		orderEntry.setGiveAway(false);
		orderEntry.setEntryNumber(order.getEntries().size());
		orderEntry.setBundleNo(bundleNo);
		orderEntry.setOrder(order);
		orderEntry.setTravelOrderEntryInfo(travelOrderEntryInfo);
		orderEntry.setBundleTemplate(bundleTemplate);
		orderEntry.setNdcOfferItemID(ndcOfferItemId);

		getModelService().save(orderEntry);
		return orderEntry;
	}

	/**
	 * Populates the {@link TravelOrderEntryInfoModel}
	 *
	 * @param travelOrderEntryInfo
	 * 		the travel order entry info
	 * @param product
	 * 		the product
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param routeCode
	 * 		the route code
	 * @param travellers
	 * 		the travellers
	 */
	protected void populateTravelOrderEntryInfoModel(final TravelOrderEntryInfoModel travelOrderEntryInfo,
			final ProductModel product, final int originDestinationRefNumber, final List<TransportOfferingModel> transportOfferings,
			final String routeCode, final List<TravellerModel> travellers)
	{
		final String priceLevelCode = getPriceLevelCode(product, transportOfferings, routeCode);

		travelOrderEntryInfo.setOriginDestinationRefNumber(originDestinationRefNumber);
		travelOrderEntryInfo.setTransportOfferings(transportOfferings);
		travelOrderEntryInfo.setPriceLevel(priceLevelCode);
		travelOrderEntryInfo.setTravelRoute(getTravelRouteService().getTravelRoute(routeCode));
		travelOrderEntryInfo.setTravellers(travellers);
	}

	/**
	 * Returns the price level associated to a particular {@link ProductModel}
	 *
	 * @param product
	 * 		the product
	 * @param transportOfferings
	 * 		the transport offerings
	 * @param routeCode
	 * 		the route code
	 *
	 * @return price level code
	 */
	protected String getPriceLevelCode(final ProductModel product, final List<TransportOfferingModel> transportOfferings,
			final String routeCode)
	{
		if (!Objects.isNull(getTravelCommercePriceService().getPriceInformation(product, PriceRowModel.TRAVELROUTECODE, routeCode)))
		{
			return TravelservicesConstants.PRICING_LEVEL_ROUTE;
		}
		else if (CollectionUtils.isNotEmpty(transportOfferings) && !Objects.isNull(getTravelCommercePriceService()
				.getPriceInformation(product, PriceRowModel.TRAVELSECTORCODE, transportOfferings.get(0).getTravelSector().getCode())))
		{
			return TravelservicesConstants.PRICING_LEVEL_SECTOR;
		}
		else
		{
			return TravelservicesConstants.PRICING_LEVEL_DEFAULT;
		}
	}

	@Override
	public void setPNRAsOrderCode(final AbstractOrderModel abstractOrder)
	{
		getTravelOrderCodeGenerationStrategy().generateTravelOrderCode(abstractOrder);
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets base site service.
	 *
	 * @return the base site service
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Sets base site service.
	 *
	 * @param baseSiteService
	 * 		the base site service
	 */
	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Gets base store service.
	 *
	 * @return the base store service
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * Sets base store service.
	 *
	 * @param baseStoreService
	 * 		the base store service
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
	 * Gets unit service.
	 *
	 * @return the unit service
	 */
	protected UnitService getUnitService()
	{
		return unitService;
	}

	/**
	 * Sets unit service.
	 *
	 * @param unitService
	 * 		the unit service
	 */
	@Required
	public void setUnitService(final UnitService unitService)
	{
		this.unitService = unitService;
	}

	/**
	 * Gets travel route service.
	 *
	 * @return the travel route service
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * Sets travel route service.
	 *
	 * @param travelRouteService
	 * 		the travel route service
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
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

	/**
	 * Gets travel order code generation strategy.
	 *
	 * @return travelOrderCodeGenerationStrategy travel order code generation strategy
	 */
	protected TravelOrderCodeGenerationStrategy getTravelOrderCodeGenerationStrategy()
	{
		return travelOrderCodeGenerationStrategy;
	}

	/**
	 * Sets travel order code generation strategy.
	 *
	 * @param travelOrderCodeGenerationStrategy
	 * 		the travelOrderCodeGenerationStrategy to set
	 */
	@Required
	public void setTravelOrderCodeGenerationStrategy(final TravelOrderCodeGenerationStrategy travelOrderCodeGenerationStrategy)
	{
		this.travelOrderCodeGenerationStrategy = travelOrderCodeGenerationStrategy;
	}

}
