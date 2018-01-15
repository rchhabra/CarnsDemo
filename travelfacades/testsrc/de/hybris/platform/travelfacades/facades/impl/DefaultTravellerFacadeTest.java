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

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.travel.ItineraryData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.ReasonForTravelData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.SpecialServiceRequestData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelservices.enums.ReasonForTravel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;
import de.hybris.platform.travelservices.services.SpecialServiceRequestService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
 * Unit Test for the implementation of {@link TravellerFacade}.
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravellerFacadeTest
{
	@Mock
	private TravellerService travellerService;
	@Mock
	private Converter<TravellerModel, TravellerData> travellerDataConverter;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private CartService cartService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private SpecialServiceRequestService specialServiceRequestService;
	@Mock
	Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter;
	@Mock
	private Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter;
	@Mock
	private Converter<TravellerPreferenceData, TravellerPreferenceModel> travellerPreferenceReverseConverter;
	@Mock
	private Converter<TravellerPreferenceModel, TravellerPreferenceData> travellerPreferenceConverter;
	@Mock
	private PassengerTypeFacade passengerTypeFacade;

	@InjectMocks
	DefaultTravellerFacade defaultTravellerFacade = new DefaultTravellerFacade();

	@Before
	public void setUp() throws Exception
	{
		defaultTravellerFacade.setTravellerService(travellerService);
		defaultTravellerFacade.setTravellerDataConverter(travellerDataConverter);
		defaultTravellerFacade.setPassengerInformationReverseConverter(passengerInformationReverseConverter);
		defaultTravellerFacade.setPassengerInformationDataConverter(passengerInformationDataConverter);
		defaultTravellerFacade.setModelService(modelService);
		defaultTravellerFacade.setUserService(userService);
		defaultTravellerFacade.setCartService(cartService);
		defaultTravellerFacade.setEnumerationService(enumerationService);
		defaultTravellerFacade.setSpecialServiceRequestService(specialServiceRequestService);
		defaultTravellerFacade.setTravellerPreferenceConverter(travellerPreferenceConverter);
		defaultTravellerFacade.setTravellerPreferenceReverseConverter(travellerPreferenceReverseConverter);
	}

	@Test
	public void testCreateTraveller()
	{
		final TravellerModel traveller = new TravellerModel();
		final TravellerData travellerData = new TravellerData();
		travellerData.setLabel("adult");

		given(travellerService.createTraveller(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(),
				Matchers.anyString(), Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(traveller)).willReturn(travellerData);

		final TravellerData travellerResultData = defaultTravellerFacade.createTraveller("", "", "adult", 0, "");
		Assert.assertNotNull(travellerResultData);
		Assert.assertEquals(travellerResultData.getLabel(), "adult");
	}

	@Test
	public void testGetReasonForTravelTypes()
	{
		final ReasonForTravel reasonForTravelBusiness = ReasonForTravel.BUSINESS;
		final List<ReasonForTravel> reasonForTravelTypes = new ArrayList<ReasonForTravel>();
		reasonForTravelTypes.add(reasonForTravelBusiness);

		given(enumerationService.getEnumerationValues(ReasonForTravel.class)).willReturn(reasonForTravelTypes);

		final List<ReasonForTravelData> results = defaultTravellerFacade.getReasonForTravelTypes();
		Assert.assertNotNull(results);
		Assert.assertSame(results.get(0).getCode(), ReasonForTravel.BUSINESS.getCode());
	}

	@Test
	public void testGetTravellersForCartEntries()
	{
		final CartModel cartModel = new CartModel();
		final List<TravellerModel> travellers = new ArrayList<TravellerModel>();

		given(defaultTravellerFacade.getCartService().hasSessionCart()).willReturn(Boolean.TRUE);
		given(defaultTravellerFacade.getCartService().getSessionCart()).willReturn(cartModel);
		given(defaultTravellerFacade.getTravellerService().getTravellers(Matchers.anyListOf(AbstractOrderEntryModel.class)))
				.willReturn(travellers);

		final List<TravellerData> results = defaultTravellerFacade.getTravellersForCartEntries();
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetTraveller()
	{
		final TravellerModel traveller = new TravellerModel();
		final TravellerData travellerData = new TravellerData();

		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final TravellerData result = defaultTravellerFacade.getTraveller("");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetTravellerWithModelNotFoundException()
	{
		given(travellerService.getExistingTraveller(Matchers.anyString())).willThrow(new ModelNotFoundException("TEST_EXCEPTION"));

		Assert.assertNull(defaultTravellerFacade.getTraveller(StringUtils.EMPTY));
	}

	@Test
	public void testGetTravellerFromCurrentCart()
	{
		final TravellerModel traveller = new TravellerModel();
		final TravellerData travellerData = new TravellerData();

		given(travellerService.getTravellerFromCurrentCart(Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final TravellerData result = defaultTravellerFacade.getTravellerFromCurrentCart("");
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetCurrentUserDetails()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);
		final TravellerData travellerData = new TravellerData();

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final TravellerData result = defaultTravellerFacade.getCurrentUserDetails();
		Assert.assertNotNull(result);
	}

	@Test
	public void testIsAnonymousUser()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(defaultTravellerFacade.getUserService().isAnonymousUser(Matchers.any())).willReturn(true);

		final boolean isAnonymousUser = defaultTravellerFacade.isAnonymousUser();
		Assert.assertTrue(isAnonymousUser);
	}

	@Test
	public void testSavedTravellersForCurrentUser()
	{
		final CustomerModel customerModel = new CustomerModel();
		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final List<TravellerData> results = defaultTravellerFacade.getSavedTravellersForCurrentUser();
		Assert.assertNotNull(results);
	}

	@Test
	public void testUpdateTravellerDetailsForBooker()
	{
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setSaveDetails(true);

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setBooker(true);

		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		traveller.setUid("adult");
		customerModel.setCustomerTravellerInstance(traveller);

		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();

		given(travellerService.createTraveller("", "", "", 0, "")).willReturn(traveller);
		given(travellerDataConverter.convert(traveller)).willReturn(travellerData);
		given(passengerInformationReverseConverter.convert(passengerInformationData)).willReturn(passengerInformationModel);
		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerService.getTravellerFromCurrentCart(Matchers.anyString())).willReturn(traveller);
		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final List<TravellerData> travellers = new ArrayList<TravellerData>();
		travellers.add(travellerData);
		defaultTravellerFacade.updateTravellerDetails(travellers);
	}

	@Test
	public void testUpdateTravellerDetailsForNonBooker()
	{
		updateExistingTravellerDetails();

		final TravellerModel traveller = new TravellerModel();
		traveller.setUid("adult");
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("pet");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setSaveDetails(true);
		passengerInformationData.setPassengerType(passengerTypeData);

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setFormId("4");
		travellerData.setBooker(false);

		final CustomerModel customerModel = new CustomerModel();
		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		given(travellerService.createTraveller(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(),
				Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(traveller)).willReturn(travellerData);

		final List<TravellerData> travellers = new ArrayList<TravellerData>();
		travellers.add(travellerData);
		defaultTravellerFacade.updateTravellerDetails(travellers);
	}

	@Test
	public void testUpdateTravellerDetailsForNonBookerExistingTraveller()
	{
		updateExistingTravellerDetails();

		final TravellerModel traveller = new TravellerModel();
		traveller.setUid("adult");
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("pet");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setSaveDetails(true);
		passengerInformationData.setPassengerType(passengerTypeData);
		passengerInformationData.setSavedTravellerUId("traveller1");

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setFormId("4");
		travellerData.setBooker(false);

		final CustomerModel customerModel = new CustomerModel();
		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		given(travellerService.createTraveller(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(),
				Matchers.anyString())).willReturn(traveller);
		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(traveller)).willReturn(travellerData);
		final List<TravellerData> travellers = new ArrayList<TravellerData>();
		travellers.add(travellerData);
		defaultTravellerFacade.updateTravellerDetails(travellers);
	}

	@Test
	public void testUpdatePassengerInformation()
	{
		final PassengerTypeData passengerTypeData = new PassengerTypeData();
		passengerTypeData.setCode("pet");

		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setSaveDetails(true);
		passengerInformationData.setPassengerType(passengerTypeData);

		final SpecialServiceRequestData specialServiceRequestData = new SpecialServiceRequestData();


		final SpecialRequestDetailData specialRequestDetailData = new SpecialRequestDetailData();
		specialRequestDetailData.setSpecialServiceRequests(Stream.of(specialServiceRequestData).collect(Collectors.toList()));

		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setFormId("4");
		travellerData.setBooker(false);
		travellerData.setSpecialRequestDetail(specialRequestDetailData);

		final CustomerModel customerModel = new CustomerModel();
		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final TravellerModel traveller = new TravellerModel();
		given(travellerService.createTraveller(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(),
				Matchers.anyString())).willReturn(traveller);

		final SpecialServiceRequestModel specialServiceRequestModel = new SpecialServiceRequestModel();
		given(specialServiceRequestService.getSpecialServiceRequest(Matchers.anyString())).willReturn(specialServiceRequestModel);

		defaultTravellerFacade.updatePassengerInformation(travellerData);
	}

	@Test
	public void testRemoveSavedTraveller()
	{
		final TravellerModel traveller = new TravellerModel();
		final TravellerData travellerData = new TravellerData();

		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final TravellerData result = defaultTravellerFacade.removeSavedTraveller(StringUtils.EMPTY);
		Assert.assertNotNull(result);
	}

	@Test
	public void testRemoveSavedTravellerException()
	{
		given(travellerService.getExistingTraveller(Matchers.anyString())).willThrow(NoSuchElementException.class);
		defaultTravellerFacade.removeSavedTraveller(StringUtils.EMPTY);
	}

	@Test
	public void testRemoveSavedTravellerRemoveException()
	{
		final TravellerModel traveller = new TravellerModel();
		final TravellerData travellerData = new TravellerData();

		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		willThrow(ModelRemovalException.class).given(modelService).remove(traveller);

		defaultTravellerFacade.removeSavedTraveller(StringUtils.EMPTY);
	}

	@Test
	public void testRetrieveTravellers()
	{
		final TravellerData travellerData = new TravellerData();
		final ItineraryData itineraryData = new ItineraryData();
		itineraryData.setTravellers(Stream.of(travellerData).collect(Collectors.toList()));
		final ReservationItemData reservationItemData = new ReservationItemData();
		reservationItemData.setReservationItinerary(itineraryData);
		final ReservationData reservationData = new ReservationData();
		reservationData.setReservationItems(Stream.of(reservationItemData).collect(Collectors.toList()));
		final List<TravellerData> results = defaultTravellerFacade.retrieveTravellers(reservationData, 0);
		Assert.assertNotNull(results);
	}

	@Test
	public void testUpdateCurrentUserSpecialRequestDetails()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		defaultTravellerFacade.updateCurrentUserSpecialRequestDetails(Collections.emptyList());
	}

	@Test
	public void testUpdateCurrentUserSpecialRequestDetailsForNewTraveller()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		given(travellerService.createTraveller(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyInt(),
				Matchers.anyString())).willReturn(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final SpecialServiceRequestModel specialServiceRequestModel = new SpecialServiceRequestModel();
		given(specialServiceRequestService.getSpecialServiceRequest("serviceRequestCode")).willReturn(specialServiceRequestModel);

		defaultTravellerFacade.updateCurrentUserSpecialRequestDetails(Arrays.asList("serviceRequestCode"));
	}

	@Test
	public void testGetTravellerPreferences()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerPreferenceModel travellerPreferenceModel = new TravellerPreferenceModel();
		final TravellerModel traveller = new TravellerModel();
		traveller.setTravellerPreference(Arrays.asList(travellerPreferenceModel));
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final List<TravellerPreferenceData> results = defaultTravellerFacade.getTravellerPreferences();
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetTravellerPreferencesForNoPreference()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		final List<TravellerPreferenceData> results = defaultTravellerFacade.getTravellerPreferences();
		Assert.assertNotNull(results);
	}

	@Test
	public void testGetSaveTravellerPreferencesForEmpty()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		defaultTravellerFacade.getSaveTravellerPreferences(Collections.emptyList());
	}

	@Test
	public void testGetSaveTravellerPreferences()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);

		defaultTravellerFacade.getSaveTravellerPreferences(Arrays.asList(new TravellerPreferenceData()));
	}

	@Test
	public void testGetPassengerInformation()
	{
		final CustomerModel customerModel = new CustomerModel();
		final PassengerInformationModel advancePassengerInfoModel = new PassengerInformationModel();
		final TravellerModel traveller = new TravellerModel();
		traveller.setInfo(advancePassengerInfoModel);
		customerModel.setCustomerTravellerInstance(traveller);

		final PassengerInformationData passengerInformationData = new PassengerInformationData();

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(passengerInformationDataConverter.convert(Matchers.any())).willReturn(passengerInformationData);

		final PassengerInformationData result = defaultTravellerFacade.getPassengerInformation();
		Assert.assertNotNull(result);
	}

	@Test
	public void testGetCustomerTravellerInstanceData()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);
		final TravellerData travellerData = new TravellerData();

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final TravellerData result = defaultTravellerFacade.getCustomerTravellerInstanceData();
		Assert.assertNotNull(result);
	}

	private void updateExistingTravellerDetails()
	{
		final TravellerModel traveller = new TravellerModel();
		final PassengerInformationModel passengerInformationModel = new PassengerInformationModel();
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		given(travellerService.getExistingTraveller(Matchers.anyString())).willReturn(traveller);
		given(travellerService.getTravellerFromCurrentCart(Matchers.anyString())).willReturn(traveller);
		given(passengerInformationReverseConverter.convert(passengerInformationData)).willReturn(passengerInformationModel);
	}

	@Test
	public void testPopulateTravellersNamesMapForEmptyList()
	{
		given(passengerTypeFacade.getPassengerTypes()).willReturn(getPassengerTypeSampleData());
		Assert.assertTrue(MapUtils.isEmpty(defaultTravellerFacade.populateTravellersNamesMap(Collections.emptyList())));

	}

	@Test
	public void testPopulateTravellersNamesMap()
	{
		final List<TravellerData> travellers = new ArrayList<>();
		final PassengerTypeData adultPassengerType = createAdultPassengerType();
		final TravellerData firstAdultPassenger = createPassenger(adultPassengerType, "John", "Doe");
		final TravellerData secondAdultPassenger = createPassenger(adultPassengerType, "Mary", "Doe");
		final PassengerTypeData childrenPassengerType = createChildrenPassengerType();
		final TravellerData childPassenger = createPassenger(childrenPassengerType, "Jack", "Doe");
		final TravellerData childPassenger2 = createPassenger(childrenPassengerType, StringUtils.EMPTY, StringUtils.EMPTY);
		final TravellerData childPassenger3 = createPassenger(childrenPassengerType, "HACK", StringUtils.EMPTY);

		travellers.add(firstAdultPassenger);
		travellers.add(secondAdultPassenger);
		travellers.add(childPassenger);
		travellers.add(childPassenger2);
		travellers.add(childPassenger3);
		given(passengerTypeFacade.getPassengerTypes()).willReturn(getPassengerTypeSampleData());
		Assert.assertTrue(MapUtils.isNotEmpty(defaultTravellerFacade.populateTravellersNamesMap(travellers)));

	}

	@Test
	public void testUpdateExistingTravellerDetailsForUnSavedTraveller()
	{
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		final PassengerInformationModel passengerInformationModel = Mockito.mock(PassengerInformationModel.class);
		passengerInformationData.setSaveDetails(true);
		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setBooker(true);
		final TravellerModel travellerModel = Mockito.mock(TravellerModel.class);
		given(travellerModel.getInfo()).willReturn(null);
		given(travellerService.getTravellerFromCurrentCart("adult")).willReturn(travellerModel);
		given(travellerModel.getInfo()).willReturn(passengerInformationModel);
		given(passengerInformationReverseConverter.convert(passengerInformationData, passengerInformationModel))
				.willReturn(passengerInformationModel);
		defaultTravellerFacade.updateExistingTravellerDetails(travellerData, Boolean.FALSE);
	}

	@Test
	public void testUpdateExistingTravellerDetailsForSavedTraveller()
	{
		final PassengerInformationData passengerInformationData = new PassengerInformationData();
		passengerInformationData.setSaveDetails(true);
		final PassengerInformationModel passengerInformationModel = Mockito.mock(PassengerInformationModel.class);
		final TravellerData travellerData = new TravellerData();
		travellerData.setUid("adult");
		travellerData.setLabel("adult");
		travellerData.setTravellerInfo(passengerInformationData);
		travellerData.setBooker(true);
		final TravellerModel travellerModel = Mockito.mock(TravellerModel.class);

		given(travellerModel.getInfo()).willReturn(passengerInformationModel);
		given(travellerService.getExistingTraveller("adult")).willReturn(travellerModel);
		given(passengerInformationReverseConverter.convert(passengerInformationData, passengerInformationModel))
				.willReturn(passengerInformationModel);
		defaultTravellerFacade.updateExistingTravellerDetails(travellerData, Boolean.TRUE);
	}

	@Test
	public void testFindSavedTravellersUsingFirstName()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);
		final TravellerData travellerData = new TravellerData();

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(travellerService.findSavedTravellersUsingFirstNameText(Matchers.anyString(), Matchers.anyString(),
				Matchers.any(CustomerModel.class))).willReturn(Stream.of(traveller).collect(Collectors.toList()));
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final List<TravellerData> result = defaultTravellerFacade.findSavedTravellersUsingFirstName("test", "adult");
		Assert.assertTrue(CollectionUtils.isNotEmpty(result));
	}

	@Test
	public void testFindSavedTravellersUsingLastName()
	{
		final CustomerModel customerModel = new CustomerModel();
		final TravellerModel traveller = new TravellerModel();
		customerModel.setCustomerTravellerInstance(traveller);
		final TravellerData travellerData = new TravellerData();

		given(defaultTravellerFacade.getUserService().getCurrentUser()).willReturn(customerModel);
		given(travellerService.findSavedTravellersUsingLastNameText(Matchers.anyString(), Matchers.anyString(),
				Matchers.any(CustomerModel.class))).willReturn(Stream.of(traveller).collect(Collectors.toList()));
		given(travellerDataConverter.convert(Matchers.any())).willReturn(travellerData);

		final List<TravellerData> result = defaultTravellerFacade.findSavedTravellersUsingSurname("test", "adult");
		Assert.assertTrue(CollectionUtils.isNotEmpty(result));
	}

	/**
	 * Method that returns a list of PassengerType sample data
	 *
	 * @return List<PassengerType>
	 */
	private List<PassengerTypeData> getPassengerTypeSampleData()
	{

		final PassengerTypeData adult = new PassengerTypeData();
		adult.setIdentifier("adult");
		adult.setPassengerType("Adult");
		adult.setCode("adult");

		final PassengerTypeData children = new PassengerTypeData();
		children.setIdentifier("children");
		children.setPassengerType("Children");
		children.setCode("child");
		final PassengerTypeData infant = new PassengerTypeData();
		infant.setIdentifier("infant");
		infant.setPassengerType("Infant");
		infant.setCode("infant");
		final List<PassengerTypeData> passengerTypes = new ArrayList<>();
		passengerTypes.add(adult);
		passengerTypes.add(children);
		passengerTypes.add(infant);

		return passengerTypes;
	}

	private TravellerData createPassenger(final PassengerTypeData passengerType, final String firstName, final String surName)
	{
		final TravellerData firstAdultPassengerData = new TravellerData();
		final PassengerInformationData firstAdultPassenger = new PassengerInformationData();
		firstAdultPassengerData.setTravellerInfo(firstAdultPassenger);
		firstAdultPassengerData.setTravellerType("Passenger");
		firstAdultPassenger.setFirstName(firstName);
		firstAdultPassenger.setSurname(surName);
		firstAdultPassenger.setPassengerType(passengerType);
		firstAdultPassengerData.setLabel(passengerType.getCode());
		return firstAdultPassengerData;
	}

	private PassengerTypeData createAdultPassengerType()
	{
		final PassengerTypeData adultPassengerType = new PassengerTypeData();
		adultPassengerType.setCode("adult");
		adultPassengerType.setName("Adult");
		return adultPassengerType;
	}

	private PassengerTypeData createChildrenPassengerType()
	{
		final PassengerTypeData childrenPassengerType = new PassengerTypeData();
		childrenPassengerType.setCode("child");
		childrenPassengerType.setName("Child");
		return childrenPassengerType;
	}
}
