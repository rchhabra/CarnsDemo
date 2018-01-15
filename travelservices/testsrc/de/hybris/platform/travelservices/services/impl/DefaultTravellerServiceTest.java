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

package de.hybris.platform.travelservices.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.exceptions.TravelKeyGeneratorException;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.service.keygenerator.TravelKeyGeneratorService;
import de.hybris.platform.travelservices.services.PassengerTypeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravellerServiceTest
{
	@Mock
	private ModelService modelService;

	@Mock
	private EnumerationService enumerationService;

	@Mock
	private PassengerTypeService passengerTypeService;

	@Mock
	private TravelKeyGeneratorService travelKeyGeneratorService;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration config;

	@Mock
	private CartService cartService;

	@InjectMocks
	private DefaultTravellerService defaultTravellerService;

	@Test
	public void testCreateTravellerPassenger()
	{
		final TravellerModel traveller = new TravellerModel();
		given(modelService.create(TravellerModel._TYPECODE)).willReturn(traveller);
		given(enumerationService.getEnumerationValue(TravellerType._TYPECODE, "PASSENGER")).willReturn(TravellerType.PASSENGER);
		final PassengerInformationModel paxInfoModel = new PassengerInformationModel();
		given(modelService.create(PassengerInformationModel._TYPECODE)).willReturn(paxInfoModel);
		final PassengerTypeModel adultModel = new PassengerTypeModel();
		adultModel.setCode("Adult");
		given(passengerTypeService.getPassengerType("Adult")).willReturn(adultModel);
		given(travelKeyGeneratorService.generateTravellerUid("0001", "2")).willReturn("0001_21234");
		willDoNothing().given(modelService).save(traveller);
		defaultTravellerService.createTraveller("PASSENGER", "Adult", "1234", 2, "0001");
		assertEquals("1234", traveller.getLabel());
		assertEquals("Adult", ((PassengerInformationModel) traveller.getInfo()).getPassengerType().getCode());
		assertEquals("0001_21234", traveller.getUid());
		verify(modelService).save(traveller);
	}

	@Test(expected = TravelKeyGeneratorException.class)
	public void testCreateTravellerPet()
	{
		final TravellerModel traveller = new TravellerModel();
		given(modelService.create(TravellerModel._TYPECODE)).willReturn(traveller);
		given(enumerationService.getEnumerationValue(TravellerType._TYPECODE, "PET")).willReturn(TravellerType.PET);

		given(travelKeyGeneratorService.generateTravellerUid("0001", "1")).willReturn("0001_11234");
		willThrow(ModelSavingException.class).given(modelService).save(traveller);
		given(config.getInt(TravelservicesConstants.TRAVEL_KEY_GENERATOR_ATTEMPT_LIMIT)).willReturn(2);
		given(configurationService.getConfiguration()).willReturn(config);
		defaultTravellerService.createTraveller("PET", "pet", "1234", 1, "0001");

	}

	@Test
	public void testGetTravellersPerLeg()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerModel traveller1 = testDataSetUp.createTraveller("1111");
		final TravellerModel traveller2 = testDataSetUp.createTraveller("2222");
		final TravellerModel traveller3 = testDataSetUp.createTraveller("3333");
		final TravellerModel traveller4 = testDataSetUp.createTraveller("4444");
		final TravellerModel traveller5 = testDataSetUp.createTraveller("5555");

		final List<TravellerModel> travellersOutbound = Stream.of(traveller1, traveller2).collect(Collectors.toList());
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true,
				travellersOutbound, 1);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true,
				Stream.of(traveller1).collect(Collectors.toList()),
				1);
		final List<TravellerModel> travellersInbound = Stream.of(traveller3, traveller4, traveller5).collect(Collectors.toList());
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ProductType.FEE, true,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, false,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);

		final AbstractOrderModel abstractOrderModel = testDataSetUp
				.createCart(Stream.of(entry2, entry1, entry3, entry4, entry5, entry6).collect(Collectors.toList()));
		final Map<Integer, List<TravellerModel>> travellerMap = defaultTravellerService.getTravellersPerLeg(abstractOrderModel);
		assertNotNull(travellerMap);
		assertFalse(travellerMap.isEmpty());
		assertEquals(2, travellerMap.get(1).size());
		assertEquals(3, travellerMap.get(2).size());
	}

	@Test
	public void testGetTravellerFromCurrentCart()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerModel traveller1 = testDataSetUp.createTraveller("1111");
		final TravellerModel traveller2 = testDataSetUp.createTraveller("2222");
		final TravellerModel traveller3 = testDataSetUp.createTraveller("3333");
		final TravellerModel traveller4 = testDataSetUp.createTraveller("4444");
		final TravellerModel traveller5 = testDataSetUp.createTraveller("5555");

		final List<TravellerModel> travellersOutbound = Stream.of(traveller1, traveller2).collect(Collectors.toList());
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true,
				travellersOutbound, 1);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true,
				Stream.of(traveller1).collect(Collectors.toList()), 1);
		final List<TravellerModel> travellersInbound = Stream.of(traveller3, traveller4, traveller5).collect(Collectors.toList());
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ProductType.FEE, true,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, false,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);

		final CartModel cart = testDataSetUp
				.createCart(Stream.of(entry2, entry1, entry3, entry4, entry5, entry6).collect(Collectors.toList()));
		given(cartService.getSessionCart()).willReturn(cart);

		assertEquals(traveller1, defaultTravellerService.getTravellerFromCurrentCart("1111"));
	}

	@Test
	public void testGetTravellerFromCurrentCartInvalidTraveller()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerModel traveller1 = testDataSetUp.createTraveller("1111");
		final TravellerModel traveller2 = testDataSetUp.createTraveller("2222");
		final TravellerModel traveller3 = testDataSetUp.createTraveller("3333");
		final TravellerModel traveller4 = testDataSetUp.createTraveller("4444");
		final TravellerModel traveller5 = testDataSetUp.createTraveller("5555");

		final List<TravellerModel> travellersOutbound = Stream.of(traveller1, traveller2).collect(Collectors.toList());
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true,
				travellersOutbound, 1);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true,
				Stream.of(traveller1).collect(Collectors.toList()), 1);
		final List<TravellerModel> travellersInbound = Stream.of(traveller3, traveller4, traveller5).collect(Collectors.toList());
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ProductType.FEE, true,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, false,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);

		final CartModel cart = testDataSetUp
				.createCart(Stream.of(entry2, entry1, entry3, entry4, entry5, entry6).collect(Collectors.toList()));
		given(cartService.getSessionCart()).willReturn(cart);

		assertNull(defaultTravellerService.getTravellerFromCurrentCart("0000"));
	}

	@Test
	public void testGetTravellers()
	{
		final TestDataSetUp testDataSetUp = new TestDataSetUp();
		final TravellerModel traveller1 = testDataSetUp.createTraveller("1111");
		final TravellerModel traveller2 = testDataSetUp.createTraveller("2222");
		final TravellerModel traveller3 = testDataSetUp.createTraveller("3333");
		final TravellerModel traveller4 = testDataSetUp.createTraveller("4444");
		final TravellerModel traveller5 = testDataSetUp.createTraveller("5555");

		final List<TravellerModel> travellersOutbound = Stream.of(traveller1, traveller2).collect(Collectors.toList());
		final AbstractOrderEntryModel entry1 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true,
				travellersOutbound, 1);
		final AbstractOrderEntryModel entry2 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true,
				Stream.of(traveller1).collect(Collectors.toList()), 1);
		final List<TravellerModel> travellersInbound = Stream.of(traveller3, traveller4, traveller5).collect(Collectors.toList());
		final AbstractOrderEntryModel entry3 = testDataSetUp.createCartEntryModel(ProductType.FARE_PRODUCT, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry4 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, true, travellersInbound,
				2);
		final AbstractOrderEntryModel entry5 = testDataSetUp.createCartEntryModel(ProductType.FEE, true,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);
		final AbstractOrderEntryModel entry6 = testDataSetUp.createCartEntryModel(ProductType.ANCILLARY, false,
				Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5).collect(Collectors.toList()), 0);
		final List<TravellerModel> expectedTravellers = Stream.of(traveller1, traveller2, traveller3, traveller4, traveller5)
				.collect(Collectors.toList());
		final List<TravellerModel> travellers = defaultTravellerService
				.getTravellers(Stream.of(entry2, entry1, entry3, entry4, entry5, entry6).collect(Collectors.toList()));

		assertTrue((expectedTravellers.size() == travellers.size()) && (expectedTravellers.containsAll(travellers)));
	}

	private class TestDataSetUp
	{
		private CartEntryModel createCartEntryModel(final ProductType productType, final boolean active,
				final List<TravellerModel> travellers, final int refNumber)
		{
			final CartEntryModel entry = new CartEntryModel();
			final ProductModel product = new ProductModel();
			final TravelOrderEntryInfoModel orderEntryInfo = new TravelOrderEntryInfoModel();
			product.setProductType(productType);
			entry.setProduct(product);
			entry.setActive(active);
			orderEntryInfo.setOriginDestinationRefNumber(refNumber);
			orderEntryInfo.setTravellers(travellers);
			entry.setTravelOrderEntryInfo(orderEntryInfo);
			entry.setType(OrderEntryType.TRANSPORT);
			return entry;
		}

		private TravellerModel createTraveller(final String code)
		{
			final TravellerModel traveller = new TravellerModel();
			traveller.setLabel(code);
			return traveller;
		}

		private CartModel createCart(final List<AbstractOrderEntryModel> entries)
		{
			final CartModel cart = new CartModel();
			cart.setEntries(entries);
			return cart;
		}
	}

}