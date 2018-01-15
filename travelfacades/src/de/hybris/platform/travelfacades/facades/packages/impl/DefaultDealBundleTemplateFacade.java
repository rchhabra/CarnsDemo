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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.packages.response.PackagesResponseData;
import de.hybris.platform.commercefacades.travel.DealOriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.OriginDestinationInfoData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.SearchProcessingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingPreferencesData;
import de.hybris.platform.commercefacades.travel.TravelPreferencesData;
import de.hybris.platform.commercefacades.travel.enums.FareSelectionDisplayOrder;
import de.hybris.platform.commercefacades.travel.enums.TransportOfferingType;
import de.hybris.platform.commercefacades.travel.enums.TripType;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.travelfacades.facades.packages.DealBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.packages.manager.DealSearchResponsePipelineManager;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.LocationType;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.RouteBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.TransportBundleTemplateModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.support.CronSequenceGenerator;


/**
 * Default implementation of the {@link DealBundleTemplateFacade}
 */
public class DefaultDealBundleTemplateFacade implements DealBundleTemplateFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultDealBundleTemplateFacade.class);

	private CartService cartService;
	private BundleTemplateService bundleTemplateService;
	private DealBundleTemplateService dealBundleTemplateService;
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;
	private Converter<BundleTemplateModel, BundleTemplateData> dealBundleTemplateConverter;
	private TimeService timeService;
	private DealSearchResponsePipelineManager dealSearchResponsePipelineManager;

	@Override
	public List<String> getFormattedDealValidDates(final String dealStartingDatePattern, final String dealDepartureDate)
	{
		final List<Date> validDates = getDealValidDates(dealStartingDatePattern, dealDepartureDate);

		if (CollectionUtils.isEmpty(validDates))
		{
			return Collections.emptyList();
		}

		final List<String> datePickerFormatedValidDates = new ArrayList<>(validDates.size());
		for (final Date validDate : validDates)
		{
			datePickerFormatedValidDates
					.add(TravelDateUtils.convertDateToStringDate(validDate, TravelservicesConstants.DATE_PATTERN));
		}
		return datePickerFormatedValidDates;
	}

	@Override
	public List<Date> getDealValidDates(final String dealStartingDatePattern, final String dealDepartureDate)
	{
		final Date tomorrowDate = TravelDateUtils.addDays(getTimeService().getCurrentDateWithTimeNormalized(), 1);
		Date dealStartDate = TravelDateUtils.getDate(dealDepartureDate, TravelservicesConstants.DATE_PATTERN);

		if (Objects.isNull(dealStartDate))
		{
			return Collections.emptyList();
		}

		Date dealEndDate = TravelDateUtils.addMonths(dealStartDate, 1);
		if (dealStartDate.before(tomorrowDate))
		{
			dealStartDate = tomorrowDate;
		}
		if (TravelDateUtils.isSameDate(dealEndDate, tomorrowDate))
		{
			dealEndDate = TravelDateUtils.addDays(tomorrowDate, 1);
		}
		else if (dealEndDate.before(tomorrowDate))
		{
			dealEndDate = tomorrowDate;
		}

		return TravelDateUtils.getValidDates(dealStartingDatePattern, TravelDateUtils.addDays(dealStartDate, -1), dealEndDate);
	}

	@Override
	public PackageRequestData getPackageRequestData(final String dealBundleTemplateId, final String selectedDepartureDate)
	{
		Date departureDate = null;
		if (StringUtils.isNotEmpty(selectedDepartureDate))
		{
			departureDate = TravelDateUtils.convertStringDateToDate(selectedDepartureDate, TravelservicesConstants.DATE_PATTERN);
			final Date currentDate = getTimeService().getCurrentTime();
			if (TravelDateUtils.isSameDate(currentDate, departureDate) || departureDate.before(currentDate))
			{
				return null;
			}
		}

		return getPackageRequestData(dealBundleTemplateId, departureDate);

	}

	protected PackageRequestData getPackageRequestData(final String dealBundleTemplateId, final Date departureDate)
	{
		final DealBundleTemplateModel dealBundleTemplateModel = getDealBundleTemplateById(dealBundleTemplateId);

		if (Objects.isNull(dealBundleTemplateModel))
		{
			return null;
		}
		Date dealDepartureDate = departureDate;
		if (Objects.isNull(dealDepartureDate))
		{
			dealDepartureDate = getFirstDealDate(dealBundleTemplateModel);
		}
		if (Objects.isNull(dealDepartureDate))
		{
			LOG.warn("No available dates for dealBundleTemplate id: " + dealBundleTemplateId);
			return null;
		}
		final Date returnDate = TravelDateUtils.addDays(dealDepartureDate, dealBundleTemplateModel.getLength());

		final PackageRequestData packageRequestData = new PackageRequestData();

		packageRequestData.setPackageId(dealBundleTemplateModel.getId());
		packageRequestData
				.setTransportPackageRequest(getTransportPackageRequest(dealBundleTemplateModel, dealDepartureDate, returnDate));
		packageRequestData.setAccommodationPackageRequest(
				getAccommodationPackageRequest(dealBundleTemplateModel, dealDepartureDate, returnDate));
		packageRequestData.setBundleTemplates(getBundleTemplates(dealBundleTemplateModel));
		packageRequestData.setStartingDatePattern(dealBundleTemplateModel.getStartingDatePattern());

		return packageRequestData;
	}

	@Override
	public DealBundleTemplateModel getDealBundleTemplateById(final String dealBundleTemplateId)
	{
		try
		{
			return getDealBundleTemplateService().getDealBundleTemplateById(dealBundleTemplateId);
		}
		catch (final ModelNotFoundException ex)
		{
			LOG.warn("No dealBundleTemplate found for id: " + dealBundleTemplateId);
			LOG.debug(ex);
			return null;
		}
	}

	@Override
	public String getDealValidCronJobExpressionById(final String dealBundleTemplateId)
	{
		final DealBundleTemplateModel dealBundleTemplateModel = getDealBundleTemplateById(dealBundleTemplateId);
		if (Objects.isNull(dealBundleTemplateModel))
		{
			return null;
		}
		return dealBundleTemplateModel.getStartingDatePattern();
	}

	/**
	 * Returns the TransportPackageRequest created for the given dealBundleTemplate
	 *
	 * @param dealBundleTemplateModel
	 *           the dealBundleTemplateModel
	 * @param departureDate
	 *           the departureDate
	 * @param returnDate
	 *           the returnDate
	 *
	 * @return the TransportPackageRequestData
	 */
	protected TransportPackageRequestData getTransportPackageRequest(final DealBundleTemplateModel dealBundleTemplateModel,
			final Date departureDate, final Date returnDate)
	{
		final Optional<TransportBundleTemplateModel> transportBundleTemplateOptional = dealBundleTemplateModel.getChildTemplates()
				.stream().filter(TransportBundleTemplateModel.class::isInstance).map(TransportBundleTemplateModel.class::cast)
				.findFirst();

		if (!transportBundleTemplateOptional.isPresent())
		{
			return null;
		}

		final TransportPackageRequestData transportPackageRequestData = new TransportPackageRequestData();

		transportPackageRequestData
				.setFareSearchRequest(getFareSearchRequest(transportBundleTemplateOptional.get(), departureDate, returnDate));

		return transportPackageRequestData;
	}

	/**
	 * Returns the FareSearchRequestData created from the given transportBundleTemplateModel
	 *
	 * @param transportBundleTemplateModel
	 *           the transportBundleTemplateModel
	 * @param departureDate
	 *           the departureDate
	 * @param returnDate
	 *           the returnDate
	 *
	 * @return the FareSearchRequestData
	 */
	protected FareSearchRequestData getFareSearchRequest(final BundleTemplateModel transportBundleTemplateModel,
			final Date departureDate, final Date returnDate)
	{
		final List<RouteBundleTemplateModel> routeBundleTemplateModels = transportBundleTemplateModel.getChildTemplates().stream()
				.filter(RouteBundleTemplateModel.class::isInstance).map(RouteBundleTemplateModel.class::cast)
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(routeBundleTemplateModels))
		{
			return null;
		}

		final FareSearchRequestData fareSearchRequestData = new FareSearchRequestData();

		// Transport Offering Preferences Data
		final TransportOfferingPreferencesData transportOfferingPreferencesData = new TransportOfferingPreferencesData();
		transportOfferingPreferencesData.setTransportOfferingType(TransportOfferingType.DIRECT);

		// Travel Preferences
		final TravelPreferencesData travelPreferences = new TravelPreferencesData();
		travelPreferences.setTransportOfferingPreferences(transportOfferingPreferencesData);
		fareSearchRequestData.setTravelPreferences(travelPreferences);

		// Passenger types
		fareSearchRequestData.setPassengerTypes(createPassengerTypeQuantities(transportBundleTemplateModel.getParentTemplate()));

		// Origin Destination Info Data List
		routeBundleTemplateModels.sort(Comparator.comparing(RouteBundleTemplateModel::getOriginDestinationRefNumber));

		final List<OriginDestinationInfoData> originDestinationInfoData = routeBundleTemplateModels.stream()
				.map(routeBundleTemplateModel -> {
					final DealOriginDestinationInfoData infoData = new DealOriginDestinationInfoData();
					final Integer originDestinationRefNumber = routeBundleTemplateModel.getOriginDestinationRefNumber();
					final TravelRouteModel route = routeBundleTemplateModel.getTravelRoute();
					infoData.setDepartureLocation(route.getOrigin().getCode());
					infoData.setDepartureLocationType(LocationType.AIRPORTGROUP);
					infoData.setArrivalLocation(route.getDestination().getCode());
					infoData.setArrivalLocationType(LocationType.AIRPORTGROUP);
					infoData.setReferenceNumber(originDestinationRefNumber);
					infoData.setPackageId(routeBundleTemplateModel.getId());
					infoData.setCabinClass(routeBundleTemplateModel.getCabinClass().getCode());
					return infoData;
				}).collect(Collectors.toList());

		originDestinationInfoData.get(0).setDepartureTime(departureDate);
		originDestinationInfoData.get(CollectionUtils.size(originDestinationInfoData) - 1).setDepartureTime(returnDate);

		fareSearchRequestData.setOriginDestinationInfo(originDestinationInfoData);

		fareSearchRequestData.setTripType(routeBundleTemplateModels.size() == 1 ? TripType.SINGLE : TripType.RETURN);

		fareSearchRequestData.setSalesApplication(SalesApplication.WEB);

		final SearchProcessingInfoData searchProcessingInfoData = new SearchProcessingInfoData();
		searchProcessingInfoData.setDisplayOrder(FareSelectionDisplayOrder.DEPARTURE_TIME.toString());
		fareSearchRequestData.setSearchProcessingInfo(searchProcessingInfoData);

		return fareSearchRequestData;
	}

	/**
	 * Returns the list of PassengerTypeQuantityData created from the given GuestCountModel list
	 *
	 * @param bundleTemplateModel
	 *           the list of GuestCountModel
	 *
	 * @return List<PassengerTypeQuantityData>
	 */
	protected List<PassengerTypeQuantityData> createPassengerTypeQuantities(final BundleTemplateModel bundleTemplateModel)
	{
		if (!(bundleTemplateModel instanceof DealBundleTemplateModel))
		{
			return null;
		}

		return ((DealBundleTemplateModel) bundleTemplateModel).getGuestCounts().stream().map(guestCountModel -> {
			final PassengerTypeQuantityData passengerTypeQuantityData = new PassengerTypeQuantityData();
			passengerTypeQuantityData.setPassengerType(getPassengerTypeConverter().convert(guestCountModel.getPassengerType()));
			passengerTypeQuantityData.setQuantity(guestCountModel.getQuantity());
			return passengerTypeQuantityData;
		}).collect(Collectors.toList());
	}

	/**
	 * Returns the AccommodationPackageRequest created for the given dealBundleTemplate
	 *
	 * @param dealBundleTemplateModel
	 *           the dealBundleTemplateModel
	 * @param departureDate
	 *           the departureDate
	 * @param returnDate
	 *           the returnDate
	 *
	 * @return the AccommodationPackageRequest
	 */
	protected AccommodationPackageRequestData getAccommodationPackageRequest(final DealBundleTemplateModel dealBundleTemplateModel,
			final Date departureDate, final Date returnDate)
	{
		final Optional<AccommodationBundleTemplateModel> accommodationBundleTemplateOptional = dealBundleTemplateModel
				.getChildTemplates().stream().filter(AccommodationBundleTemplateModel.class::isInstance)
				.map(AccommodationBundleTemplateModel.class::cast).findFirst();

		if (!accommodationBundleTemplateOptional.isPresent())
		{
			return null;
		}

		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(
				getAccommodationAvailabilityRequest(accommodationBundleTemplateOptional.get(), departureDate, returnDate));

		return accommodationPackageRequestData;
	}

	/**
	 * Returns the AccommodationAvailabilityRequestData created from the given accommodationBundleTemplateModel
	 *
	 * @param accommodationBundleTemplateModel
	 *           the accommodationBundleTemplateModel
	 * @param departureDate
	 *           the departureDate
	 * @param returnDate
	 *           the returnDate
	 *
	 * @return the AccommodationAvailabilityRequestData
	 */
	protected AccommodationAvailabilityRequestData getAccommodationAvailabilityRequest(
			final AccommodationBundleTemplateModel accommodationBundleTemplateModel, final Date departureDate, final Date returnDate)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();

		// StayDateRange
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(departureDate);
		stayDateRange.setEndTime(returnDate);
		criterion.setStayDateRange(stayDateRange);

		// RoomStayCandidates
		final RoomStayCandidateData roomStayCandidateData = new RoomStayCandidateData();
		roomStayCandidateData.setAccommodationCode(accommodationBundleTemplateModel.getAccommodation().getCode());
		roomStayCandidateData.setRatePlanCode(accommodationBundleTemplateModel.getRatePlan().getCode());
		roomStayCandidateData.setRoomStayCandidateRefNumber(0);
		roomStayCandidateData
				.setPassengerTypeQuantityList(createPassengerTypeQuantities(accommodationBundleTemplateModel.getParentTemplate()));

		criterion.setRoomStayCandidates(Arrays.asList(roomStayCandidateData));

		// AccommodationReference
		final PropertyData accommodationReferenceData = new PropertyData();
		accommodationReferenceData
				.setAccommodationOfferingCode(accommodationBundleTemplateModel.getAccommodationOffering().getCode());
		criterion.setAccommodationReference(accommodationReferenceData);

		criterion.setBundleTemplateId(accommodationBundleTemplateModel.getId());

		accommodationAvailabilityRequestData.setCriterion(criterion);

		return accommodationAvailabilityRequestData;
	}

	/**
	 * Returns the list of BundleTemplateData created for the given dealBundleTemplate
	 *
	 * @param dealBundleTemplateModel
	 *           the dealBundleTemplateModel
	 *
	 * @return the list of BundleTemplateData
	 */
	protected List<BundleTemplateData> getBundleTemplates(final DealBundleTemplateModel dealBundleTemplateModel)
	{
		final List<BundleTemplateModel> standardBundleTemplates = dealBundleTemplateModel.getChildTemplates().stream()
				.filter(bundleTemplateModel -> !(bundleTemplateModel instanceof TransportBundleTemplateModel)
						&& !(bundleTemplateModel instanceof AccommodationBundleTemplateModel))
				.collect(Collectors.toList());

		return standardBundleTemplates.stream()
				.map(bundleTemplateModel -> getDealBundleTemplateConverter().convert(bundleTemplateModel))
				.collect(Collectors.toList());
	}

	/**
	 * Returns the first date from the current date that matches the startingDate pattern for the given packageId
	 *
	 * @param dealBundleTemplateModel
	 *           the dealBundleTemplateModel
	 *
	 * @return the first date from the current date that matches the startingDate pattern for the given packageId
	 */
	protected Date getFirstDealDate(final DealBundleTemplateModel dealBundleTemplateModel)
	{
		final String startingDatePattern = dealBundleTemplateModel.getStartingDatePattern();
		final CronSequenceGenerator cronSequenceGenerator;
		try
		{
			cronSequenceGenerator = new CronSequenceGenerator(startingDatePattern);
		}
		catch (final IllegalArgumentException ex)
		{
			LOG.debug(ex);
			LOG.warn("The cronExpression " + startingDatePattern + " is not valid.");
			return null;
		}

		return cronSequenceGenerator.next(getTimeService().getCurrentTime());
	}

	@Override
	public PackagesResponseData getPackageResponseDetails(final PackageRequestData packageRequestData)
	{
		if (Objects.isNull(packageRequestData))
		{
			return null;
		}

		final PackageResponseData packageResponseData = getDealSearchResponsePipelineManager().executePipeline(packageRequestData);

		final PackagesResponseData packagesResponseData = new PackagesResponseData();
		packagesResponseData.setPackageResponses(Arrays.asList(packageResponseData));

		return packagesResponseData;
	}

	@Override
	public boolean isDealBundleTemplateMatchesCart(final String dealBundleTemplateId)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (Objects.isNull(cartModel) || CollectionUtils.isEmpty(cartModel.getEntries()))
		{
			return false;
		}

		if (getDealBundleTemplateService().abstractOrderIsDeal(cartModel))
		{
			return cartModel.getEntries().stream().filter(entry -> entry.getBundleNo() > 0)
					.allMatch(entry -> StringUtils.equals(dealBundleTemplateId,
							getBundleTemplateService().getRootBundleTemplate(entry.getBundleTemplate()).getId()));
		}

		return false;
	}

	@Override
	public boolean isDepartureDateInCartEquals(final Date dealDepartureDate)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (Objects.isNull(cartModel) || CollectionUtils.isEmpty(cartModel.getEntries()))
		{
			return false;
		}

		final Optional<AbstractOrderEntryModel> optionalTransportEntry = cartModel.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo()))
				.filter(transportEntry -> Objects.equals(0, transportEntry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()))
				.findFirst();
		if (optionalTransportEntry.isPresent())
		{
			final Collection<TransportOfferingModel> outboundTransportOfferings = optionalTransportEntry.get()
					.getTravelOrderEntryInfo().getTransportOfferings();
			if (CollectionUtils.isNotEmpty(outboundTransportOfferings))
			{
				final Optional<TransportOfferingModel> outBoundTransportOffering = outboundTransportOfferings.stream().findFirst();
				if (outBoundTransportOffering.isPresent())
				{
					final Date outboundTransportDepartureDate = outBoundTransportOffering.get().getDepartureTime();
					if (DateUtils.isSameDay(outboundTransportDepartureDate, dealDepartureDate))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean isDealAbstractOrder(final AbstractOrderModel abstractOrderModel)
	{
		return getDealBundleTemplateService().abstractOrderIsDeal(abstractOrderModel);
	}

	@Override
	public String getDealBundleTemplateIdFromAbstractOrder(final AbstractOrderModel abstractOrderModel)
	{
		if (!isDealAbstractOrder(abstractOrderModel))
		{
			return StringUtils.EMPTY;
		}

		final Optional<BundleTemplateModel> rootBundleTemplateOptional = abstractOrderModel.getEntries().stream()
				.filter(entry -> entry.getBundleNo() > 0)
				.map(entry -> getBundleTemplateService().getRootBundleTemplate(entry.getBundleTemplate())).findAny();

		return rootBundleTemplateOptional.map(BundleTemplateModel::getId).orElse(StringUtils.EMPTY);
	}

	/**
	 * @return the dealBundleTemplateService
	 */
	protected DealBundleTemplateService getDealBundleTemplateService()
	{
		return dealBundleTemplateService;
	}

	/**
	 * @param dealBundleTemplateService
	 *           the dealBundleTemplateService to set
	 */
	@Required
	public void setDealBundleTemplateService(final DealBundleTemplateService dealBundleTemplateService)
	{
		this.dealBundleTemplateService = dealBundleTemplateService;
	}

	/**
	 * @return the passengerTypeConverter
	 */
	protected Converter<PassengerTypeModel, PassengerTypeData> getPassengerTypeConverter()
	{
		return passengerTypeConverter;
	}

	/**
	 * @param passengerTypeConverter
	 *           the passengerTypeConverter to set
	 */
	@Required
	public void setPassengerTypeConverter(final Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter)
	{
		this.passengerTypeConverter = passengerTypeConverter;
	}

	/**
	 * @return the dealBundleTemplateConverter
	 */
	protected Converter<BundleTemplateModel, BundleTemplateData> getDealBundleTemplateConverter()
	{
		return dealBundleTemplateConverter;
	}

	/**
	 * @param dealBundleTemplateConverter
	 *           the dealBundleTemplateConverter to set
	 */
	@Required
	public void setDealBundleTemplateConverter(
			final Converter<BundleTemplateModel, BundleTemplateData> dealBundleTemplateConverter)
	{
		this.dealBundleTemplateConverter = dealBundleTemplateConverter;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the dealSearchResponsePipelineManager
	 */
	protected DealSearchResponsePipelineManager getDealSearchResponsePipelineManager()
	{
		return dealSearchResponsePipelineManager;
	}

	/**
	 * @param dealSearchResponsePipelineManager
	 *           the dealSearchResponsePipelineManager to set
	 */
	@Required
	public void setDealSearchResponsePipelineManager(final DealSearchResponsePipelineManager dealSearchResponsePipelineManager)
	{
		this.dealSearchResponsePipelineManager = dealSearchResponsePipelineManager;
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
