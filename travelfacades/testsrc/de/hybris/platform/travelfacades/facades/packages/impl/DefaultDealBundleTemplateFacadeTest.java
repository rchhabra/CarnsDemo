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

package de.hybris.platform.travelfacades.facades.packages.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.enums.TripType;
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
import de.hybris.platform.travelfacades.facades.packages.manager.DealSearchResponsePipelineManager;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.TransportFacilityType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.DealBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.RouteBundleTemplateModel;
import de.hybris.platform.travelservices.model.deal.TransportBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.GuestCountModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.model.travel.CabinClassModel;
import de.hybris.platform.travelservices.model.travel.TransportFacilityModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.warehouse.AccommodationOfferingModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.DealBundleTemplateService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultDealBundleTemplateFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDealBundleTemplateFacadeTest
{
	@InjectMocks
	DefaultDealBundleTemplateFacade defaultDealBundleTemplateFacade;

	@Mock
	private CartService cartService;

	@Mock
	private BundleTemplateService bundleTemplateService;

	@Mock
	private DealBundleTemplateService dealBundleTemplateService;

	@Mock
	private Converter<PassengerTypeModel, PassengerTypeData> passengerTypeConverter;

	@Mock
	private Converter<BundleTemplateModel, BundleTemplateData> dealBundleTemplateConverter;

	@Mock
	private TimeService timeService;

	@Mock
	private DealSearchResponsePipelineManager dealSearchResponsePipelineManager;

	private TestDataSetUp testDataSetUp;
	private DealBundleTemplateModel dealBundleTemplateModel;

	@Before
	public void setUp()
	{
		Mockito.when(timeService.getCurrentDateWithTimeNormalized())
				.thenReturn(TravelDateUtils.convertStringDateToDate(
						TravelDateUtils.convertDateToStringDate(new Date(), TravelservicesConstants.DATE_PATTERN),
						TravelservicesConstants.DATE_PATTERN));
		Mockito.when(timeService.getCurrentTime())
				.thenReturn(TravelDateUtils.convertStringDateToDate(
						TravelDateUtils.convertDateToStringDate(new Date(), TravelservicesConstants.DATE_PATTERN),
						TravelservicesConstants.DATE_PATTERN));

		testDataSetUp = new TestDataSetUp();
		dealBundleTemplateModel = testDataSetUp.createDealBundleTemplateModel("TEST_DEAL_TEMPLATE_ID", "0 0 0 ? * *", 5);
	}

	@Test
	public void StringGetFormattedDealValidDatesForPast2MonthDepartureDate()
	{
		final Date pastDepartureDate = TravelDateUtils.addMonths(new Date(), -2);
		final String departureDate = TravelDateUtils.convertDateToStringDate(pastDepartureDate,
				TravelservicesConstants.DATE_PATTERN);
		Assert.assertTrue(
				CollectionUtils.size(defaultDealBundleTemplateFacade.getFormattedDealValidDates("0 0 0 ? * *", departureDate)) == 0);

	}

	@Test
	public void StringGetFormattedDealValidDatesForPresentDepartureDate()
	{
		final String departureDate = TravelDateUtils.convertDateToStringDate(new Date(), TravelservicesConstants.DATE_PATTERN);
		final int sizeOfValidDates = CollectionUtils
				.size(defaultDealBundleTemplateFacade.getDealValidDates("0 0 0 ? * *", departureDate));
		Assert.assertTrue(CollectionUtils
				.size(defaultDealBundleTemplateFacade.getFormattedDealValidDates("0 0 0 ? * *", departureDate)) == sizeOfValidDates);
	}


	@Test
	public void testGetDealValidDatesForEmptyDepartureDate()
	{
		Assert.assertTrue(
				CollectionUtils.isEmpty(defaultDealBundleTemplateFacade.getDealValidDates(StringUtils.EMPTY, StringUtils.EMPTY)));
	}

	@Test
	public void testGetDealValidDatesForPresentDepartureDate()
	{
		final String departureDate = TravelDateUtils.convertDateToStringDate(new Date(), TravelservicesConstants.DATE_PATTERN);
		Assert.assertTrue(
				CollectionUtils.size(defaultDealBundleTemplateFacade.getDealValidDates("0 0 0 ? * *", departureDate)) >= 29);
	}

	@Test
	public void testGetDealValidDatesForPast2MonthDepartureDate()
	{
		final Date pastDepartureDate = TravelDateUtils.addMonths(new Date(), -2);
		final String departureDate = TravelDateUtils.convertDateToStringDate(pastDepartureDate,
				TravelservicesConstants.DATE_PATTERN);
		Assert.assertTrue(
				CollectionUtils.size(defaultDealBundleTemplateFacade.getDealValidDates("0 0 0 ? * *", departureDate)) == 0);
	}

	@Test
	public void testGetDealValidDatesForSameEndDateAndTommorrowDate()
	{
		final Date pastDepartureDate = TravelDateUtils.addMonths(TravelDateUtils.addDays(new Date(), 1), -1);
		final String departureDate = TravelDateUtils.convertDateToStringDate(pastDepartureDate,
				TravelservicesConstants.DATE_PATTERN);
		Assert.assertTrue(
				CollectionUtils.size(defaultDealBundleTemplateFacade.getDealValidDates("0 0 0 ? * *", departureDate)) == 1);
	}

	@Test
	public void testGetDealValidDatesForFutureDepartureDate()
	{
		Mockito.when(timeService.getCurrentDateWithTimeNormalized()).thenReturn(new Date());
		final Date futureDepartureDate = TravelDateUtils.addMonths(new Date(), 2);
		final String departureDate = TravelDateUtils.convertDateToStringDate(futureDepartureDate,
				TravelservicesConstants.DATE_PATTERN);
		Assert.assertTrue(
				CollectionUtils.size(defaultDealBundleTemplateFacade.getDealValidDates("0 0 0 ? * *", departureDate)) >= 29);
	}

	@Test
	public void testGetPackageRequestDataForEmptyDepartureDateAndInValidDealId()
	{
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID"))
				.thenThrow(new ModelNotFoundException("Exception"));

		Assert.assertNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY));
	}

	@Test
	public void testGetPackageRequestDataForInvalidStartingDatePattern()
	{
		dealBundleTemplateModel.setStartingDatePattern("0 0 0");
		dealBundleTemplateModel.setChildTemplates(Collections.singletonList(new RouteBundleTemplateModel()));
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);

		Assert.assertNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY));
	}

	@Test
	public void testGetPackageRequestDataForEmptyDepartureDateAndValidDealIdAndEmptyTransportBundles()
	{
		dealBundleTemplateModel.setChildTemplates(Collections.singletonList(new RouteBundleTemplateModel()));
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);

		Assert.assertNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest());
	}

	@Test
	public void testGetPackageRequestDataForEmptyDepartureDateAndValidDealIdAndInvalidTransportBundles()
	{
		final TransportBundleTemplateModel transportBundleTemplateModel = new TransportBundleTemplateModel();
		transportBundleTemplateModel.setChildTemplates(Collections.emptyList());
		dealBundleTemplateModel.setChildTemplates(Collections.singletonList(transportBundleTemplateModel));
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);

		Assert.assertNotNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest());
		Assert.assertNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest().getFareSearchRequest());
	}

	@Test
	public void testGetPackageRequestDataForEmptyDepartureDateAndValidDealIdAndValidTransportBundlesForSingleJourney()
	{
		final RouteBundleTemplateModel routeBundleTemplateModel1 = testDataSetUp.createRouteBundleTemplateModel(0);

		final TransportBundleTemplateModel transportBundleTemplateModel = new TransportBundleTemplateModel();
		transportBundleTemplateModel.setChildTemplates(Arrays.asList(routeBundleTemplateModel1));
		dealBundleTemplateModel.setChildTemplates(Collections.singletonList(transportBundleTemplateModel));
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);

		Assert.assertNotNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest());
		Assert.assertNotNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest().getFareSearchRequest());
		Assert.assertTrue(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest().getFareSearchRequest().getTripType().equals(TripType.SINGLE));
	}

	@Test
	public void testGetPackageRequestDataForEmptyDepartureDateAndValidDealIdAndValidTransportBundlesForReturnJourneyAndValidAccommodationPackageRequest()
	{
		final RouteBundleTemplateModel routeBundleTemplateModel1 = testDataSetUp.createRouteBundleTemplateModel(0);
		final RouteBundleTemplateModel routeBundleTemplateModel2 = testDataSetUp.createRouteBundleTemplateModel(1);
		final AccommodationBundleTemplateModel accommodationBundleTemplateModel = testDataSetUp
				.createAccommodationBundleTemplateModel("TEST_ACCOMMODATION_BUNDLE_TEMPLATE_ID");
		accommodationBundleTemplateModel.setAccommodation(testDataSetUp.createAccommodationModel("TEST_ACCOMMODATION_CODE"));
		accommodationBundleTemplateModel
				.setAccommodationOffering(testDataSetUp.createAccommodationOfferingModel("TEST_ACCOMMODATION_OFFERING_CODE"));
		accommodationBundleTemplateModel.setRatePlan(testDataSetUp.createRatePlan("TEST_RATE_PLAN_CODE"));

		dealBundleTemplateModel.setGuestCounts(Arrays.asList(testDataSetUp.createGuestCountModel(2, "adult")));
		accommodationBundleTemplateModel.setParentTemplate(dealBundleTemplateModel);

		final TransportBundleTemplateModel transportBundleTemplateModel = new TransportBundleTemplateModel();
		transportBundleTemplateModel.setChildTemplates(Arrays.asList(routeBundleTemplateModel1, routeBundleTemplateModel2));
		dealBundleTemplateModel.setChildTemplates(Arrays.asList(transportBundleTemplateModel, accommodationBundleTemplateModel));
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);
		Mockito.when(passengerTypeConverter.convert(Matchers.any(PassengerTypeModel.class))).thenReturn(new PassengerTypeData());
		Assert.assertNotNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest().getFareSearchRequest());
		Assert.assertNotNull(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getAccommodationPackageRequest());
		Assert.assertTrue(defaultDealBundleTemplateFacade.getPackageRequestData("TEST_DEAL_ID", StringUtils.EMPTY)
				.getTransportPackageRequest().getFareSearchRequest().getTripType().equals(TripType.RETURN));
	}

	@Test
	public void testGetDealValidCronJobExpressionByIdForInvalidDealId()
	{
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID"))
				.thenThrow(new ModelNotFoundException("Exception"));
		Assert.assertNull(defaultDealBundleTemplateFacade.getDealValidCronJobExpressionById("TEST_DEAL_ID"));
	}

	@Test
	public void testGetDealValidCronJobExpressionByIdForValidDealId()
	{
		Mockito.when(dealBundleTemplateService.getDealBundleTemplateById("TEST_DEAL_ID")).thenReturn(dealBundleTemplateModel);
		Assert.assertNotNull(defaultDealBundleTemplateFacade.getDealValidCronJobExpressionById("TEST_DEAL_ID"));
	}

	@Test
	public void testGetPackageResponseDetailsForNullParameter()
	{
		Assert.assertNull(defaultDealBundleTemplateFacade.getPackageResponseDetails(null));
	}

	@Test
	public void testGetPackageResponseDetails()
	{
		Mockito.when(dealSearchResponsePipelineManager.executePipeline(Matchers.any())).thenReturn(new PackageResponseData());
		Assert.assertTrue(CollectionUtils.isNotEmpty(
				defaultDealBundleTemplateFacade.getPackageResponseDetails(new PackageRequestData()).getPackageResponses()));
	}

	@Test
	public void testIsDealBundleTemplateMatchesCartForEmptyCart()
	{
		Mockito.when(cartService.getSessionCart()).thenReturn(null);
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDealBundleTemplateMatchesCart("TEST_DEAL_BUNDLE_ID"));
	}

	@Test
	public void testIsDealBundleTemplateMatchesCartForEmptyCartEntries()
	{
		Mockito.when(cartService.getSessionCart()).thenReturn(new CartModel());
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDealBundleTemplateMatchesCart("TEST_DEAL_BUNDLE_ID"));
	}

	@Test
	public void testIsDealBundleTemplateMatchesCartForNoDealInCart()
	{
		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		entry1.setBundleTemplate(new BundleTemplateModel());
		entry1.setBundleNo(0);

		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setBundleTemplate(new BundleTemplateModel());
		entry1.setBundleNo(1);
		cartModel.setEntries(Arrays.asList(entry1, entry2));
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(dealBundleTemplateService.abstractOrderIsDeal(cartModel)).thenReturn(Boolean.FALSE);
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDealBundleTemplateMatchesCart("TEST_BUNDLE_TEMPLATE_ID"));
	}

	@Test
	public void testIsDealBundleTemplateMatchesCartForDealInCart()
	{
		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		entry1.setBundleTemplate(testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID_1"));
		entry1.setBundleNo(1);

		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setBundleTemplate(testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID"));
		entry2.setBundleNo(2);
		cartModel.setEntries(Arrays.asList(entry1, entry2));
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(dealBundleTemplateService.abstractOrderIsDeal(cartModel)).thenReturn(Boolean.TRUE);
		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		bundleTemplateModel.setId("TEST_BUNDLE_TEMPLATE_ID");
		Mockito.when(bundleTemplateService.getRootBundleTemplate(Matchers.any(BundleTemplateModel.class)))
				.thenReturn(testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID"));

		Assert.assertTrue(defaultDealBundleTemplateFacade.isDealBundleTemplateMatchesCart("TEST_BUNDLE_TEMPLATE_ID"));
	}

	@Test
	public void testIsDealBundleTemplateMatchesCartForDealInCartWhenBundlesIdMatch()
	{
		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final BundleTemplateModel bundleTemplateModel1 = testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID_1");
		entry1.setBundleTemplate(bundleTemplateModel1);
		entry1.setBundleNo(0);

		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final BundleTemplateModel bundleTemplateModel2 = testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID");
		entry2.setBundleTemplate(bundleTemplateModel2);
		entry2.setBundleNo(1);
		cartModel.setEntries(Arrays.asList(entry1, entry2));
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Mockito.when(dealBundleTemplateService.abstractOrderIsDeal(cartModel)).thenReturn(Boolean.TRUE);
		final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
		bundleTemplateModel.setId("TEST_BUNDLE_TEMPLATE_ID");
		Mockito.when(bundleTemplateService.getRootBundleTemplate(Matchers.any(BundleTemplateModel.class)))
				.thenReturn(testDataSetUp.createBundleTemplateModel("TEST_BUNDLE_TEMPLATE_ID"));

		Assert.assertTrue(defaultDealBundleTemplateFacade.isDealBundleTemplateMatchesCart("TEST_BUNDLE_TEMPLATE_ID"));
	}


	@Test
	public void testIsDepartureDateInCartEqualsForEmptyCart()
	{
		Mockito.when(cartService.getSessionCart()).thenReturn(null);
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDepartureDateInCartEquals(new Date()));
	}

	@Test
	public void testIsDepartureDateInCartEqualsForEmptyCartEntries()
	{
		Mockito.when(cartService.getSessionCart()).thenReturn(new CartModel());
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDepartureDateInCartEquals(new Date()));
	}

	@Test
	public void testIsDepartureDateInCartEqualsForEntriesWithoutTransportEntry()
	{
		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();

		cartModel.setEntries(Arrays.asList(entry1));
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDepartureDateInCartEquals(new Date()));
	}

	@Test
	public void testIsDepartureDateInCartEqualsForEntriesWithTransportEntry()
	{
		final CartModel cartModel = new CartModel();
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel travelOrderEntryInfo2 = new TravelOrderEntryInfoModel();
		travelOrderEntryInfo2.setOriginDestinationRefNumber(1);
		entry2.setTravelOrderEntryInfo(travelOrderEntryInfo2);

		final AbstractOrderEntryModel entry3 = new AbstractOrderEntryModel();
		final TravelOrderEntryInfoModel travelOrderEntryInfo3 = new TravelOrderEntryInfoModel();
		travelOrderEntryInfo3.setOriginDestinationRefNumber(0);

		final TransportOfferingModel transportOffering = new TransportOfferingModel();
		transportOffering.setDepartureTime(new Date());
		final Collection<TransportOfferingModel> outboundTransportOfferings = Collections.singletonList(transportOffering);
		travelOrderEntryInfo3.setTransportOfferings(outboundTransportOfferings);
		entry3.setTravelOrderEntryInfo(travelOrderEntryInfo3);
		cartModel.setEntries(Arrays.asList(entry1, entry2, entry3));
		Mockito.when(cartService.getSessionCart()).thenReturn(cartModel);
		Assert.assertTrue(defaultDealBundleTemplateFacade.isDepartureDateInCartEquals(new Date()));
		Assert.assertFalse(defaultDealBundleTemplateFacade.isDepartureDateInCartEquals(TravelDateUtils.addDays(new Date(), 1)));
	}

	@Test
	public void testisDealAbstractOrder()
	{
		Mockito.when(dealBundleTemplateService.abstractOrderIsDeal(Matchers.any(AbstractOrderModel.class)))
				.thenReturn(Boolean.TRUE);
		Assert.assertTrue(defaultDealBundleTemplateFacade.isDealAbstractOrder(new CartModel()));
	}

	private class TestDataSetUp
	{

		public RouteBundleTemplateModel createRouteBundleTemplateModel(final int originDestinationRefNumber)
		{
			final RouteBundleTemplateModel routeBundleTemplateModel = new RouteBundleTemplateModel();
			routeBundleTemplateModel.setOriginDestinationRefNumber(originDestinationRefNumber);
			routeBundleTemplateModel.setId("TEST_ROUTE_BUNDLE_TEMPLATE_ID_" + originDestinationRefNumber);
			routeBundleTemplateModel.setTravelRoute(createTravelRouteModel(originDestinationRefNumber));
			final CabinClassModel cabinClassModel = new CabinClassModel();
			cabinClassModel.setCode("TEST_CABIN_CLASS");
			routeBundleTemplateModel.setCabinClass(cabinClassModel);
			return routeBundleTemplateModel;
		}

		public TravelRouteModel createTravelRouteModel(final int originDestinationRefNumber)
		{
			final TravelRouteModel travelRouteModel = new TravelRouteModel();
			if (originDestinationRefNumber == 0)
			{
				travelRouteModel.setOrigin(createTransportFacilityModel("LHR", "London Heathrow Airport"));
				travelRouteModel.setDestination(createTransportFacilityModel("CDG", "Charles de Gaulle Airport"));
			}
			else
			{
				travelRouteModel.setDestination(createTransportFacilityModel("LHR", "London Heathrow Airport"));
				travelRouteModel.setOrigin(createTransportFacilityModel("CDG", "Charles de Gaulle Airport"));
			}
			return travelRouteModel;
		}

		private TransportFacilityModel createTransportFacilityModel(final String code, final String name)
		{
			final TransportFacilityModel transportFacility = new TransportFacilityModel();

			transportFacility.setCode(code);
			transportFacility.setName(name, Locale.ENGLISH);
			transportFacility.setType(TransportFacilityType.AIRPORT);

			return transportFacility;
		}

		public AccommodationBundleTemplateModel createAccommodationBundleTemplateModel(final String id)
		{
			final AccommodationBundleTemplateModel accommodationBundleTemplateModel = new AccommodationBundleTemplateModel();
			accommodationBundleTemplateModel.setId(id);
			return accommodationBundleTemplateModel;
		}

		public RatePlanModel createRatePlan(final String ratePlanCode)
		{
			final RatePlanModel ratePlan = new RatePlanModel();
			ratePlan.setCode(ratePlanCode);

			return ratePlan;
		}

		public AccommodationModel createAccommodationModel(final String accommodationCode)
		{
			final AccommodationModel accommodationModel = new AccommodationModel();
			accommodationModel.setCode(accommodationCode);
			return accommodationModel;
		}

		public AccommodationOfferingModel createAccommodationOfferingModel(final String accommodationOfferingCode)
		{
			final AccommodationOfferingModel accommodationOfferingModel = new AccommodationOfferingModel();
			accommodationOfferingModel.setCode(accommodationOfferingCode);
			return accommodationOfferingModel;
		}

		public DealBundleTemplateModel createDealBundleTemplateModel(final String id, final String startingDatePattern,
				final int lengthOfDeal)
		{
			final DealBundleTemplateModel dealBundleTemplateModel = new DealBundleTemplateModel();
			dealBundleTemplateModel.setId(id);
			dealBundleTemplateModel.setStartingDatePattern(startingDatePattern);
			dealBundleTemplateModel.setLength(lengthOfDeal);
			return dealBundleTemplateModel;
		}

		public BundleTemplateModel createBundleTemplateModel(final String id)
		{
			final BundleTemplateModel bundleTemplateModel = new BundleTemplateModel();
			bundleTemplateModel.setId(id);
			return bundleTemplateModel;
		}

		public GuestCountModel createGuestCountModel(final int guestQuantity, final String passengerTypeCode)
		{
			final GuestCountModel guestCountModel = new GuestCountModel();
			guestCountModel.setQuantity(guestQuantity);
			guestCountModel.setPassengerType(createPassengerTypeModel(passengerTypeCode));
			return guestCountModel;
		}

		public PassengerTypeModel createPassengerTypeModel(final String passengerTypeCode)
		{
			final PassengerTypeModel passengerTypeModel = new PassengerTypeModel();
			passengerTypeModel.setCode(passengerTypeCode);
			return passengerTypeModel;
		}
	}

}
