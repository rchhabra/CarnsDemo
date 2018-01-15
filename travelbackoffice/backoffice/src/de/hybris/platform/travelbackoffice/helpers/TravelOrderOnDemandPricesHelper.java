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

package de.hybris.platform.travelbackoffice.helpers;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.integration.commons.OndemandDiscountedOrderEntry;
import de.hybris.platform.integration.commons.services.OndemandPromotionService;
import de.hybris.platform.integration.commons.services.OndemandTaxCalculationService;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;


/**
 * This helper class is used to populate taxes and other travel related attributes for a order and order entry
 */
public class TravelOrderOnDemandPricesHelper
{

	private OndemandTaxCalculationService ondemandTaxCalculationService;
	private OndemandPromotionService onDemandPromotionService;


	public TravelOrderInfo estimateOrderInfo(final AbstractOrderModel order)
	{
		final TravelOrderInfo traveOrderInfo = new TravelOrderInfo();

		final BigDecimal taxValue = ondemandTaxCalculationService.calculateTotalTax(order);
		traveOrderInfo.setTotalTax(taxValue.toString());

		final List<OndemandDiscountedOrderEntry> entryList = onDemandPromotionService
				.calculateProportionalDiscountForEntries(order);
		BigDecimal total = BigDecimal.valueOf(ondemandTaxCalculationService.calculateShippingCost(order).doubleValue());
		for (final OndemandDiscountedOrderEntry entry : entryList)
		{
			traveOrderInfo.getTravelOrderEntryList().add(createEntryInfo(entry));
			total = total.add(entry.getDiscountedLinePrice());
		}
		traveOrderInfo.setTotalPrice(total.toString());
		traveOrderInfo.setTotalWithTax(taxValue.add(total).toString());

		return traveOrderInfo;
	}

	private TravelOrderEntryInfo createEntryInfo(final OndemandDiscountedOrderEntry entry)
	{
		final TravelOrderEntryInfo travelOrderEntryInfo = new TravelOrderEntryInfo();
		travelOrderEntryInfo.setEntryDesc(StringUtils.isNotBlank(entry.getOrderEntry().getProduct().getName())
				? entry.getOrderEntry().getProduct().getName() : entry.getOrderEntry().getProduct().getCode());
		travelOrderEntryInfo.setUnitPrice(entry.getDiscountedUnitPrice().toString());
		travelOrderEntryInfo.setEntryPrice(entry.getDiscountedLinePrice().toString());

		final AbstractOrderEntryModel orderEntry = entry.getOrderEntry();
		final BigDecimal unitTaxValue = ondemandTaxCalculationService.calculatePreciseUnitTax(orderEntry.getTaxValues(),
				orderEntry.getQuantity().doubleValue(), orderEntry.getOrder().getNet().booleanValue());
		travelOrderEntryInfo.setUnitTax(unitTaxValue.toString());

		final BigDecimal taxValue = unitTaxValue.multiply(BigDecimal.valueOf(orderEntry.getQuantity().doubleValue())).setScale(2);
		travelOrderEntryInfo.setEntryTax(taxValue.toString());
		travelOrderEntryInfo.setEntryTotal(entry.getDiscountedLinePrice().add(taxValue).toString());


		if (entry.getOrderEntry().getTravelOrderEntryInfo() != null)
		{
			final TravelOrderEntryInfoModel travelOrderEntryInfoModel = entry.getOrderEntry().getTravelOrderEntryInfo();
			travelOrderEntryInfo
					.setTransportOfferings(populateTransportOfferingNumbers(travelOrderEntryInfoModel.getTransportOfferings()));
			if (travelOrderEntryInfoModel.getTravelRoute() != null)
			{
				travelOrderEntryInfo.setTravelRoute(travelOrderEntryInfoModel.getTravelRoute().getName());
			}
			if (travelOrderEntryInfoModel.getOriginDestinationRefNumber() != null)
			{
				travelOrderEntryInfo.setOriginDestinationRefNumber(
						entry.getOrderEntry().getTravelOrderEntryInfo().getOriginDestinationRefNumber().toString());
			}
			travelOrderEntryInfo.setTravellers(populateTravellerNames(travelOrderEntryInfoModel.getTravellers()));
		}
		return travelOrderEntryInfo;
	}

	public TravelOrderEntryInfo estimateOrderEntryInfo(final AbstractOrderEntryModel orderEntry)
	{
		final TravelOrderEntryInfo travelOrderEntryInfo = new TravelOrderEntryInfo();

		final BigDecimal unitTaxValue = ondemandTaxCalculationService.calculatePreciseUnitTax(orderEntry.getTaxValues(),
				orderEntry.getQuantity().doubleValue(), orderEntry.getOrder().getNet().booleanValue());
		travelOrderEntryInfo.setUnitTax(unitTaxValue.toString());

		final BigDecimal taxValue = unitTaxValue.multiply(BigDecimal.valueOf(orderEntry.getQuantity().doubleValue()).setScale(2));
		travelOrderEntryInfo.setEntryTax(taxValue.toString());


		final AbstractOrderModel order = orderEntry.getOrder();
		final List<OndemandDiscountedOrderEntry> entryList = onDemandPromotionService
				.calculateProportionalDiscountForEntries(order);
		BigDecimal linePrice = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		for (final OndemandDiscountedOrderEntry entry : entryList)
		{
			if (!(orderEntry.getPk().equals(entry.getOrderEntry().getPk())))
			{
				continue;
			}
			linePrice = entry.getDiscountedLinePrice();
			unitPrice = entry.getDiscountedUnitPrice();

		}

		travelOrderEntryInfo.setEntryPrice(linePrice.toString());
		travelOrderEntryInfo.setEntryTotal(linePrice.add(taxValue).setScale(2).toString());
		travelOrderEntryInfo.setUnitPrice(unitPrice.toString());
		travelOrderEntryInfo.setEntryDesc(StringUtils.isNotBlank(orderEntry.getProduct().getName())
				? orderEntry.getProduct().getName() : orderEntry.getProduct().getCode());

		if (null != orderEntry.getTravelOrderEntryInfo())
		{
			travelOrderEntryInfo.setTransportOfferings(
					populateTransportOfferingNumbers(orderEntry.getTravelOrderEntryInfo().getTransportOfferings()));

			if (Objects.nonNull(orderEntry.getTravelOrderEntryInfo().getTravelRoute()))
			{
				travelOrderEntryInfo.setTravelRoute(orderEntry.getTravelOrderEntryInfo().getTravelRoute().getName());
			}

			if (Objects.nonNull(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
			{
				travelOrderEntryInfo
						.setOriginDestinationRefNumber(orderEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber().toString());
			}

			travelOrderEntryInfo.setTravellers(populateTravellerNames(orderEntry.getTravelOrderEntryInfo().getTravellers()));
		}

		return travelOrderEntryInfo;
	}

	protected String populateTransportOfferingNumbers(final Collection<TransportOfferingModel> transportOfferings)
	{
		final StringBuilder transportOfferingNumbers = new StringBuilder();
		int count = 1;
		for (final TransportOfferingModel transportOffering : transportOfferings)
		{
			if (StringUtils.isNotBlank(transportOffering.getNumber()))
			{
				transportOfferingNumbers.append(transportOffering.getNumber());
				if (count < transportOfferings.size())
				{
					transportOfferingNumbers.append(" , ");
					count++;
				}
			}
		}
		return transportOfferingNumbers.toString();
	}

	protected String populateTravellerNames(final Collection<TravellerModel> travellers)
	{
		final StringBuilder travellersNames = new StringBuilder();
		int count = 1;
		for (final TravellerModel traveller : travellers)
		{
			switch (traveller.getType().getCode())
			{
				case TravelbackofficeConstants.TRAVELLER_TYPE_PASSENGER:
					final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();
					travellersNames.append(passengerInfo.getFirstName()).append(" ").append(passengerInfo.getSurname());
					break;
			}
			if (count < travellers.size())
			{
				travellersNames.append(" , ");
				count++;
			}
		}
		return travellersNames.toString();
	}

	/**
	 * @return the ondemandTaxCalculationService
	 */
	protected OndemandTaxCalculationService getOndemandTaxCalculationService()
	{
		return ondemandTaxCalculationService;
	}

	/**
	 * @param ondemandTaxCalculationService
	 *           the ondemandTaxCalculationService to set
	 */
	public void setOndemandTaxCalculationService(final OndemandTaxCalculationService ondemandTaxCalculationService)
	{
		this.ondemandTaxCalculationService = ondemandTaxCalculationService;
	}

	/**
	 * @return the onDemandPromotionService
	 */
	protected OndemandPromotionService getOnDemandPromotionService()
	{
		return onDemandPromotionService;
	}

	/**
	 * @param onDemandPromotionService
	 *           the onDemandPromotionService to set
	 */
	public void setOnDemandPromotionService(final OndemandPromotionService onDemandPromotionService)
	{
		this.onDemandPromotionService = onDemandPromotionService;
	}
}
