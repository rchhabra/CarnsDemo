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

package de.hybris.platform.travelservices.price.strategies.impl;

import de.hybris.platform.configurablebundleservices.bundle.impl.FindBundlePricingWithCurrentPriceFactoryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.order.strategies.calculation.FindDiscountValuesStrategy;
import de.hybris.platform.order.strategies.calculation.FindPriceStrategy;
import de.hybris.platform.order.strategies.calculation.FindTaxValuesStrategy;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.travel.LocationModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.TransportFacilityService;
import de.hybris.platform.util.PriceValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Travel implementation of price, taxes and discounts resolver strategies ({@link FindPriceStrategy},
 * {@link FindDiscountValuesStrategy}, {@link FindTaxValuesStrategy}) that resolves values for calculation from current
 * session's price factory. If no session price factory is set it uses {@link OrderManager#getPriceFactory()} which will
 * retrieve the default one according to system settings.
 */
public class FindTravelPricingWithCurrentPriceFactoryStrategy extends FindBundlePricingWithCurrentPriceFactoryStrategy
{
	private TransportFacilityService transportFacilityService;

	private SessionService sessionService;

	@Override
	public Collection<TaxValue> findTaxValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		setTaxQueryParametersToContext(entry);
		return getTaxValues(entry);
	}

	protected Collection<TaxValue> getTaxValues(final AbstractOrderEntryModel entry) throws CalculationException
	{
		return super.findTaxValues(entry);
	}

	@Override
	public PriceValue findBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		setPriceQueryParametersToContext(entry);
		return getBasePrice(entry);
	}

	protected PriceValue getBasePrice(final AbstractOrderEntryModel entry) throws CalculationException
	{
		return super.findBasePrice(entry);
	}

	/**
	 * Method to set parameters in the context. These will be used for querying prices in the factory class.
	 *
	 * @param entry
	 */
	protected void setPriceQueryParametersToContext(final AbstractOrderEntryModel entry)
	{
		final TravelOrderEntryInfoModel orderEntryInfo = entry.getTravelOrderEntryInfo();

		if (orderEntryInfo != null)
		{
			if (StringUtils.isBlank(orderEntryInfo.getPriceLevel()) || TravelservicesConstants.PRICING_LEVEL_DEFAULT
					.equals(orderEntryInfo.getPriceLevel()))
			{
				getSessionService().setAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP, null);
				return;
			}
			final List<TransportOfferingModel> transportOfferings = new ArrayList<TransportOfferingModel>(
					orderEntryInfo.getTransportOfferings());

			String priceLevel = StringUtils.EMPTY;
			String searchValue = null;
			if (TravelservicesConstants.PRICING_LEVEL_ROUTE.equals(orderEntryInfo.getPriceLevel()))
			{
				priceLevel = PriceRowModel.TRAVELROUTECODE;
				searchValue = orderEntryInfo.getTravelRoute().getCode();
			}
			else if (TravelservicesConstants.PRICING_LEVEL_SECTOR.equals(orderEntryInfo.getPriceLevel()))
			{
				priceLevel = PriceRowModel.TRAVELSECTORCODE;
				searchValue = transportOfferings.get(0).getTravelSector().getCode();
			}
			else if (TravelservicesConstants.PRICING_LEVEL_TRANSPORT_OFFERING.equals(orderEntryInfo.getPriceLevel()))
			{
				priceLevel = PriceRowModel.TRANSPORTOFFERINGCODE;
				searchValue = transportOfferings.get(0).getCode();
			}
			final Map<String, String> params = new HashMap<String, String>();
			params.put(priceLevel, searchValue);
			getSessionService().setAttribute(TravelservicesConstants.PRICING_SEARCH_CRITERIA_MAP, params);
		}
	}

	/**
	 * Method to set parameters in the context. These will be used for querying taxes in the factory class.
	 *
	 * @param entry
	 */
	protected void setTaxQueryParametersToContext(final AbstractOrderEntryModel entry)
	{
		final TravelOrderEntryInfoModel orderEntryInfo = entry.getTravelOrderEntryInfo();

		if (orderEntryInfo != null)
		{
			if (StringUtils.isBlank(orderEntryInfo.getPriceLevel()))
			{
				getSessionService().setAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP, null);
				return;
			}
			final List<TransportOfferingModel> transportOfferings = new ArrayList<TransportOfferingModel>(
					orderEntryInfo.getTransportOfferings());
			final List<String> transportFacilityCodes = new ArrayList<String>();
			final List<String> countryCodes = new ArrayList<String>();
			final List<String> passengerTypeList = new ArrayList<String>();

			for (final TransportOfferingModel transportOfferingModel : transportOfferings)
			{
				final LocationModel countryModel = getTransportFacilityService()
						.getCountry(transportOfferingModel.getTravelSector().getOrigin());

				transportFacilityCodes.add(transportOfferingModel.getTravelSector().getOrigin().getCode());
				countryCodes.add(countryModel.getCode());
			}
			for (final TravellerModel traveller : orderEntryInfo.getTravellers())
			{
				final PassengerInformationModel travellerInfo = (PassengerInformationModel) traveller.getInfo();
				passengerTypeList.add(travellerInfo.getPassengerType().getCode().toLowerCase());
			}

			final Map<String, List<String>> params = new HashMap<String, List<String>>();
			params.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, transportFacilityCodes);
			params.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, countryCodes);
			params.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, passengerTypeList);

			getSessionService().setAttribute(TravelservicesConstants.TAX_SEARCH_CRITERIA_MAP, params);
		}
	}

	/**
	 * @return instance of transportFacilityService
	 */
	protected TransportFacilityService getTransportFacilityService()
	{
		return transportFacilityService;
	}

	/**
	 * @param transportFacilityService
	 */
	public void setTransportFacilityService(final TransportFacilityService transportFacilityService)
	{
		this.transportFacilityService = transportFacilityService;
	}

	/**
	 * @return the sessionService
	 */
	@Override
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 * 		the sessionService to set
	 */
	@Override
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

}
