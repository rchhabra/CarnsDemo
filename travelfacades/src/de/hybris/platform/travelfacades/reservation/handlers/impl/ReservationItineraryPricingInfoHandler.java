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

package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.travel.FareDetailsData;
import de.hybris.platform.commercefacades.travel.FareInfoData;
import de.hybris.platform.commercefacades.travel.FareProductData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.PTCFareBreakdownData;
import de.hybris.platform.commercefacades.travel.PassengerFareData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.TravellerBreakdownData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationPricingInfoData;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.enums.BundleType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;


/**
 * This handler is responsible for instantiating Itinerary Pricing Info for given leg which will contain summary of fare
 * products selected by user
 */
public class ReservationItineraryPricingInfoHandler implements ReservationHandler
{
	private Converter<FareProductModel, FareProductData> fareProductConverter;
	@Deprecated
	private PriceDataFactory priceDataFactory;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private CommonI18NService commonI18NService;
	private EnumerationService enumerationService;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		if (CollectionUtils.isNotEmpty(reservationData.getReservationItems()))
		{
			reservationData.getReservationItems().forEach(reservationItem -> {
				final ReservationPricingInfoData reservationPricingInfo = reservationItem.getReservationPricingInfo();
				if (reservationPricingInfo != null)
				{
					reservationPricingInfo.setItineraryPricingInfo(createItineraryPricingInfo(abstractOrderModel, reservationItem));
				}
			});
		}
	}

	/**
	 * Creates a new Itinerary Pricing Info to hold fare products summary for each leg of the journey
	 *
	 * @param abstractOrderModel
	 *           - given Abstract Order
	 * @param reservationItem
	 *           - Reservation Item of current leg
	 * @return new itinerary pricing info
	 */
	protected ItineraryPricingInfoData createItineraryPricingInfo(final AbstractOrderModel abstractOrderModel,
			final ReservationItemData reservationItem)
	{
		final ItineraryPricingInfoData itineraryPricingInfo = new ItineraryPricingInfoData();
		itineraryPricingInfo.setAvailable(Boolean.TRUE);
		final List<AbstractOrderEntryModel> bundledEntries = abstractOrderModel.getEntries().stream()
				.filter(e -> OrderEntryType.TRANSPORT.equals(e.getType()))
				.filter(
						e -> !(Objects.equals(ProductType.FEE, e.getProduct().getProductType()) || e.getProduct() instanceof FeeProductModel))
				.filter(e -> e.getActive() && e.getBundleNo() != 0
						&& e.getTravelOrderEntryInfo().getOriginDestinationRefNumber() != null && e.getTravelOrderEntryInfo()
								.getOriginDestinationRefNumber() == reservationItem.getOriginDestinationRefNumber())
				.collect(Collectors.toList());

		itineraryPricingInfo.setPtcFareBreakdownDatas(createPTCFareBreakdowns(bundledEntries, reservationItem));

		itineraryPricingInfo.setTravellerBreakdowns(createTravellerBreakdowns(bundledEntries, reservationItem));

		final Optional<AbstractOrderEntryModel> abstractOrderEntry = bundledEntries.stream()
				.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				.findAny();
		if (!abstractOrderEntry.isPresent() || Objects.isNull(abstractOrderEntry.get().getBundleTemplate()))
		{
			return itineraryPricingInfo;
		}
		final BundleTemplateModel bundleTemplateModel = abstractOrderEntry.get().getBundleTemplate();

		final BundleType bundleType = Objects.nonNull(bundleTemplateModel.getType()) ? bundleTemplateModel.getType()
				: bundleTemplateModel.getParentTemplate().getType();
		itineraryPricingInfo.setBundleType(bundleType.getCode());
		itineraryPricingInfo.setBundleTypeName(getEnumerationService().getEnumerationName(bundleType));

		return itineraryPricingInfo;
	}

	/**
	 * Creates price breakdown for each traveller.
	 *
	 * @param bundledEntries
	 *           - entries that are in a bundle for current leg
	 * @param reservationItem
	 *           - Reservation Item of current leg
	 * @return list of traveller breakdowns
	 */
	protected List<TravellerBreakdownData> createTravellerBreakdowns(final List<AbstractOrderEntryModel> bundledEntries,
			final ReservationItemData reservationItem)
	{
		final List<TravellerBreakdownData> travellerBreakdowns = new ArrayList<>(
				CollectionUtils.size(reservationItem.getReservationItinerary().getTravellers()));

		reservationItem.getReservationItinerary().getTravellers().forEach(traveller -> {
			final TravellerBreakdownData travellerBreakdown = createTravellerBreakdown(bundledEntries, traveller);
			travellerBreakdowns.add(travellerBreakdown);
		});
		return travellerBreakdowns;
	}

	/**
	 * Create a new travellerBreakdownData for the given traveller.
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param traveller
	 *           the traveller
	 * @return the newly created travellerBreakdownData
	 */
	protected TravellerBreakdownData createTravellerBreakdown(final List<AbstractOrderEntryModel> bundledEntries,
			final TravellerData traveller)
	{
		final TravellerBreakdownData travellerBreakdown = new TravellerBreakdownData();
		travellerBreakdown.setTraveller(traveller);
		travellerBreakdown.setQuantity(1);
		travellerBreakdown.setFareBasisCodes(retreiveFareBasisCodesForTraveller(bundledEntries, traveller));
		travellerBreakdown.setPassengerFare(calculateTravellerFare(bundledEntries, traveller));
		return travellerBreakdown;
	}

	/**
	 * Sums up all bundle products prices for given traveller
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param traveller
	 *           the traveller
	 * @return sum of bundle product prices
	 */
	protected PassengerFareData calculateTravellerFare(final List<AbstractOrderEntryModel> bundledEntries,
			final TravellerData traveller)
	{
		BigDecimal basePrice = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;

		for (final AbstractOrderEntryModel entry : bundledEntries)
		{
			if (CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers().stream()
					.filter(entryTraveller -> entryTraveller.getLabel().equals(traveller.getLabel())).collect(Collectors.toList())))
			{
				basePrice = basePrice.add(BigDecimal.valueOf(entry.getBasePrice()));
				totalPrice = totalPrice.add(BigDecimal.valueOf(entry.getTotalPrice()));
			}
		}

		final PassengerFareData passengerFare = new PassengerFareData();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePrice.doubleValue()));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue()));
		return passengerFare;
	}

	/**
	 * Collects all fare basis codes for given traveller
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param traveller
	 *           the traveller
	 * @return fare basis codes for traveller
	 */
	protected List<String> retreiveFareBasisCodesForTraveller(final List<AbstractOrderEntryModel> bundledEntries,
			final TravellerData traveller)
	{
		final List<String> fareBasisCodes = new ArrayList<>();

		final List<AbstractOrderEntryModel> fareProductEntries = bundledEntries.stream()
				.filter(entry -> ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				.collect(Collectors.toList());

		fareProductEntries.forEach(entry -> {
			if (CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers().stream()
					.filter(entryTraveller -> entryTraveller.getLabel().equals(traveller.getLabel())).collect(Collectors.toList())))
			{
				final FareProductModel fareProduct = (FareProductModel) entry.getProduct();
				fareBasisCodes.add(fareProduct.getFareBasisCode());
			}
		});
		return fareBasisCodes;
	}

	/**
	 * Creates price breakdown for each passenger type.
	 *
	 * @param bundledEntries
	 *           - entries that are in a bundle for current leg
	 * @param reservationItem
	 *           - Reservation Item of current leg
	 * @return list
	 */
	protected List<PTCFareBreakdownData> createPTCFareBreakdowns(final List<AbstractOrderEntryModel> bundledEntries,
			final ReservationItemData reservationItem)
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantities = retrievePassengerTypeQuantitiesFromTravellers(
				reservationItem.getReservationItinerary().getTravellers());

		final List<PTCFareBreakdownData> ptcFareBreakdowns = new ArrayList<>(passengerTypeQuantities.size());
		passengerTypeQuantities.forEach(passengerTypeQuantity -> {
			final PTCFareBreakdownData ptcFareBreakdown = createPTCFareBreakdown(bundledEntries, passengerTypeQuantity);
			ptcFareBreakdowns.add(ptcFareBreakdown);
		});

		return ptcFareBreakdowns;
	}

	/**
	 * Creates a new instance of price breakdown for given passengerType
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param passengerTypeQuantity
	 *           the passenger type quantity
	 * @return ptc fare breakdown data
	 */
	protected PTCFareBreakdownData createPTCFareBreakdown(final List<AbstractOrderEntryModel> bundledEntries,
			final PassengerTypeQuantityData passengerTypeQuantity)
	{
		final PTCFareBreakdownData ptcFareBreakdown = new PTCFareBreakdownData();
		ptcFareBreakdown.setPassengerTypeQuantity(passengerTypeQuantity);
		final String passengerTypeCode = passengerTypeQuantity.getPassengerType().getCode();
		ptcFareBreakdown.setFareBasisCodes(retreiveFareBasisCodesForPassengerType(bundledEntries, passengerTypeCode));
		ptcFareBreakdown.setFareInfos(createFareInfosForPassengerTypes(bundledEntries, passengerTypeCode));
		ptcFareBreakdown.setPassengerFare(retreivePerPassengerTypeFare(bundledEntries, passengerTypeCode));
		return ptcFareBreakdown;
	}

	/**
	 * Calculates the fare price per passenger type based on bundled entries
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param passengerTypeCode
	 *           the passenger type code
	 * @return passenger fare data
	 */
	protected PassengerFareData retreivePerPassengerTypeFare(final List<AbstractOrderEntryModel> bundledEntries,
			final String passengerTypeCode)
	{
		BigDecimal basePrice = BigDecimal.ZERO;
		BigDecimal totalPrice = BigDecimal.ZERO;
		for (final AbstractOrderEntryModel entry : bundledEntries)
		{
			final String entryPassengerTypeCode = getPassengerTypeCodeFromEntry(entry);
			if (StringUtils.isNotEmpty(entryPassengerTypeCode) && entryPassengerTypeCode.equals(passengerTypeCode))
			{
				totalPrice = totalPrice.add(BigDecimal.valueOf(entry.getTotalPrice()));
				if (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
				{
					basePrice = basePrice.add(BigDecimal.valueOf(entry.getBasePrice()));
				}
			}
		}
		final PassengerFareData passengerFare = new PassengerFareData();
		passengerFare.setBaseFare(getTravelCommercePriceFacade().createPriceData(basePrice.doubleValue()));
		passengerFare.setTotalFare(getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue()));
		return passengerFare;
	}

	/**
	 * Retrieves Fare information from entries based on passengerType
	 *
	 * @param bundledEntries
	 *           the bundled entries
	 * @param passengerTypeCode
	 *           the passenger type code
	 * @return list
	 */
	protected List<FareInfoData> createFareInfosForPassengerTypes(final List<AbstractOrderEntryModel> bundledEntries,
			final String passengerTypeCode)
	{
		final List<FareDetailsData> fareDetailsList = new ArrayList<>();
		final List<AbstractOrderEntryModel> fareProductEntries = bundledEntries.stream()
				.filter(entry -> entry.getProduct() instanceof FareProductModel
						|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
				.collect(Collectors.toList());

		fareProductEntries.forEach(entry -> {
			final String entryPassengerTypeCode = getPassengerTypeCodeFromEntry(entry);
			if (StringUtils.isNotEmpty(entryPassengerTypeCode) && entryPassengerTypeCode.equals(passengerTypeCode))
			{
				final FareProductModel fareProduct = (FareProductModel) entry.getProduct();
				final FareDetailsData fareDetailsData = createFareDetails(fareProductConverter.convert(fareProduct));
				fareDetailsList.add(fareDetailsData);

			}
		});

		final FareInfoData fareInfoData = createFareInfo(fareDetailsList);
		final List<FareInfoData> fareInfos = new ArrayList<>();
		fareInfos.add(fareInfoData);

		return fareInfos;
	}

	/**
	 * Method returns a new instance of FareInfoData with the list of FareDetailsData added
	 *
	 * @param fareDetailsDataList
	 *           the fare details data list
	 * @return FareInfoData fare info data
	 */
	protected FareInfoData createFareInfo(final List<FareDetailsData> fareDetailsDataList)
	{
		final FareInfoData fareInfoData = new FareInfoData();
		fareInfoData.setFareDetails(fareDetailsDataList);
		return fareInfoData;
	}

	/**
	 * Method returns a new instance of FareDetailsData with the FareProductData added
	 *
	 * @param fareProductData
	 *           the fare product data
	 * @return FareDetailsData fare details data
	 */
	protected FareDetailsData createFareDetails(final FareProductData fareProductData)
	{
		final FareDetailsData fareDetailsData = new FareDetailsData();
		fareDetailsData.setFareProduct(fareProductData);
		return fareDetailsData;
	}


	/**
	 * Retrieves fare basis codes from entries based on passenger type.
	 *
	 * @param bundledEntries
	 *           - entries which contain bundled products of given leg
	 * @param passengerTypeCode
	 *           - current passenger type
	 * @return list of fare basis codes for passenger type
	 */
	protected List<String> retreiveFareBasisCodesForPassengerType(final List<AbstractOrderEntryModel> bundledEntries,
			final String passengerTypeCode)
	{
		final List<String> fareBasisCodes = new ArrayList<>();
		final List<AbstractOrderEntryModel> fareProductEntries = bundledEntries.stream()
				.filter(entry -> entry.getProduct() instanceof FareProductModel
						|| ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType()))
				.collect(Collectors.toList());

		fareProductEntries.forEach(entry -> {
			final String entryPassengerTypeCode = getPassengerTypeCodeFromEntry(entry);
			if (StringUtils.isNotEmpty(entryPassengerTypeCode) && entryPassengerTypeCode.equals(passengerTypeCode))
			{
				final String fareBasisCode = ((FareProductModel) entry.getProduct()).getFareBasisCode();
				if (!fareBasisCodes.contains(fareBasisCode))
				{
					fareBasisCodes.add(fareBasisCode);
				}
			}
		});

		return fareBasisCodes;
	}

	/**
	 * Creates a list of Passenger Type Quantities which will define how many PTCFareBreakdowns are needed
	 *
	 * @param travellers
	 *           - travellers of the particular leg
	 * @return list of passenger type quantities
	 */
	protected List<PassengerTypeQuantityData> retrievePassengerTypeQuantitiesFromTravellers(final List<TravellerData> travellers)
	{
		final List<PassengerTypeQuantityData> passengerTypeQuantities = new ArrayList<>();
		travellers.forEach(traveller -> {
			if (TravellerType.PASSENGER.getCode().equals(traveller.getTravellerType()))
			{
				final PassengerInformationData passengerInfo = (PassengerInformationData) traveller.getTravellerInfo();
				final PassengerTypeQuantityData ptq = getPassengerTypeQuantityFromList(passengerTypeQuantities,
						passengerInfo.getPassengerType().getCode());
				if (ptq != null)
				{
					final int updatedCount = ptq.getQuantity() + 1;
					ptq.setQuantity(updatedCount);
				}
				else
				{
					final PassengerTypeQuantityData passengerTypeQuantity = new PassengerTypeQuantityData();
					passengerTypeQuantity.setPassengerType(passengerInfo.getPassengerType());
					passengerTypeQuantity.setQuantity(1);
					passengerTypeQuantities.add(passengerTypeQuantity);
				}
			}
		});
		return passengerTypeQuantities;
	}

	/**
	 * Checks whether there is already an entry for given passenger type
	 *
	 * @param passengerTypeQuantities
	 *           the passenger type quantities
	 * @param passengerTypeCode
	 *           the passenger type code
	 * @return passenger type quantity from list
	 */
	protected PassengerTypeQuantityData getPassengerTypeQuantityFromList(
			final List<PassengerTypeQuantityData> passengerTypeQuantities, final String passengerTypeCode)
	{
		for (final PassengerTypeQuantityData ptq : passengerTypeQuantities)
		{
			if (ptq.getPassengerType().getCode().equals(passengerTypeCode))
			{
				return ptq;
			}
		}
		return null;
	}

	/**
	 * Retrieves passenger type code from given abstract order entry
	 *
	 * @param entry
	 *           - abstract order entry containing traveller
	 * @return passenger type code for entry
	 */
	protected String getPassengerTypeCodeFromEntry(final AbstractOrderEntryModel entry)
	{
		if (CollectionUtils.isNotEmpty(entry.getTravelOrderEntryInfo().getTravellers()))
		{
			for (final TravellerModel traveller : entry.getTravelOrderEntryInfo().getTravellers())
			{
				if (TravellerType.PASSENGER.equals(traveller.getType()))
				{
					final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();
					return passengerInfo.getPassengerType().getCode();
				}
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Gets fare product converter.
	 *
	 * @return the fareProductConverter
	 */
	protected Converter<FareProductModel, FareProductData> getFareProductConverter()
	{
		return fareProductConverter;
	}

	/**
	 * Sets fare product converter.
	 *
	 * @param fareProductConverter
	 *           the fareProductConverter to set
	 */
	public void setFareProductConverter(final Converter<FareProductModel, FareProductData> fareProductConverter)
	{
		this.fareProductConverter = fareProductConverter;
	}

	/**
	 * Gets price data factory.
	 *
	 * @return the priceDataFactory
	 */
	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * Sets price data factory.
	 *
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * Gets common I18N service.
	 *
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common I18N service.
	 *
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
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
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
