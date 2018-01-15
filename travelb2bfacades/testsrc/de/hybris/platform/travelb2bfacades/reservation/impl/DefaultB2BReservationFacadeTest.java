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

package de.hybris.platform.travelb2bfacades.reservation.impl;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelb2bfacades.data.B2BReservationData;
import de.hybris.platform.travelb2bfacades.reservation.manager.B2BReservationPipelineManager;
import de.hybris.platform.travelb2bservices.order.TravelB2BOrderService;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * unit test for {@link DefaultB2BReservationFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BReservationFacadeTest
{
	@InjectMocks
	DefaultB2BReservationFacade defaultB2BReservationFacade;

	@Mock
	private TravelB2BOrderService travelb2bOrderService;

	@Mock
	private UserService userService;

	@Mock
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Mock
	private B2BReservationPipelineManager b2bReservationPipelineManager;

	private final List<String> b2bUnitCodes = new ArrayList<String>(Arrays.asList("TEST_BTB_UNIT_CODE"));
	private final String email = "TEST_EMAIL";
	private final Date date = new Date();
	private final String costCenterUid = "TEST_COST_CENTER_UID";
	private final String currency = "TEST_CURRENCY";

	@Test
	public void testFindReservations()
	{
		final PageableData pageableData = new PageableData();

		final SearchPageData<OrderModel> searchPageData = new SearchPageData<>();

		final SortData sortData1 = new SortData();
		sortData1.setSelected(Boolean.FALSE);
		final SortData sortData2 = new SortData();
		sortData2.setSelected(Boolean.TRUE);
		sortData2.setCode("code");

		searchPageData.setSorts(Stream.of(sortData1, sortData2).collect(Collectors.toList()));

		final OrderModel result = new OrderModel();
		searchPageData.setResults(Arrays.asList(result));
		when(travelb2bOrderService.getPagedOrders(pageableData, b2bUnitCodes, email, date, date, costCenterUid, currency))
				.thenReturn(searchPageData);

		final B2BReservationData reservationData = new B2BReservationData();
		when(b2bReservationPipelineManager.executePipeline(result)).thenReturn(reservationData);

		Assert.assertEquals(reservationData, defaultB2BReservationFacade
				.findReservations(null, pageableData, b2bUnitCodes, email, date, date, costCenterUid, currency).getResults().get(0));
	}

	@Test
	public void testCheckDatesInterval()
	{
		Assert.assertFalse(defaultB2BReservationFacade.checkDatesInterval(TravelDateUtils.addDays(date, 2), date));
		Assert.assertFalse(defaultB2BReservationFacade.checkDatesInterval(null, date));
		Assert.assertFalse(defaultB2BReservationFacade.checkDatesInterval(date, null));
		Assert.assertTrue(defaultB2BReservationFacade.checkDatesInterval(date, TravelDateUtils.addDays(date, 2)));
	}

	@Test
	public void testGetDefaultUnitForB2BCustomerModelWithB2BUnitModelGroups()
	{
		final UserModel user = new B2BCustomerModel();
		final PrincipalGroupModel principalGroup1 = new B2BUnitModel();
		principalGroup1.setUid("TEST_UID");
		final PrincipalGroupModel principalGroup2 = new PrincipalGroupModel();
		principalGroup2.setUid("TEST_UID");
		user.setGroups(Stream.of(principalGroup1, principalGroup2).collect(Collectors.toSet()));
		when(userService.getCurrentUser()).thenReturn(user);
		Assert.assertEquals("TEST_UID", defaultB2BReservationFacade.getDefaultUnit());
	}

	@Test
	public void testGetDefaultUnitForB2BCustomerModelWithDefaultB2BUnit()
	{
		final B2BCustomerModel user = new B2BCustomerModel();
		final PrincipalGroupModel principalGroup = new PrincipalGroupModel();
		principalGroup.setUid("TEST_UID");
		user.setGroups(Stream.of(principalGroup).collect(Collectors.toSet()));
		final B2BUnitModel b2bUnitModel = new B2BUnitModel();
		b2bUnitModel.setUid("TEST_DEFAULT_UID");
		user.setDefaultB2BUnit(b2bUnitModel);
		when(userService.getCurrentUser()).thenReturn(user);
		Assert.assertEquals("TEST_DEFAULT_UID", defaultB2BReservationFacade.getDefaultUnit());
	}

	@Test
	public void testGetDefaultUnitForNonB2BCustomerModel()
	{
		final UserModel user = new UserModel();
		final PrincipalGroupModel principalGroup = new B2BUnitModel();
		principalGroup.setUid("TEST_UID");
		user.setGroups(Stream.of(principalGroup).collect(Collectors.toSet()));
		when(userService.getCurrentUser()).thenReturn(user);
		Assert.assertNull(defaultB2BReservationFacade.getDefaultUnit());
	}

	@Test
	public void testValidateUnitForNonB2BCustomerModel()
	{
		final UserModel user = new UserModel();
		final PrincipalGroupModel principalGroup1 = new B2BUnitModel();
		principalGroup1.setUid("TEST_UID");
		final PrincipalGroupModel principalGroup2 = new PrincipalGroupModel();
		principalGroup2.setUid("TEST_UID");
		user.setGroups(Stream.of(principalGroup1, principalGroup2).collect(Collectors.toSet()));
		when(userService.getCurrentUser()).thenReturn(user);
		Assert.assertFalse(defaultB2BReservationFacade.validateUnit("TEST_UNIT_CODE"));
	}

	@Test
	public void testValidateUnitForB2BCustomerModel()
	{
		final UserModel user = new B2BCustomerModel();
		final PrincipalGroupModel principalGroup = new B2BUnitModel();
		principalGroup.setUid("TEST_UID");
		final PrincipalGroupModel principalGroup1 = new B2BUnitModel();
		principalGroup1.setUid("TEST_UID_1");
		final PrincipalGroupModel principalGroup2 = new PrincipalGroupModel();
		principalGroup2.setUid("TEST_UID");
		user.setGroups(Stream.of(principalGroup, principalGroup1, principalGroup2).collect(Collectors.toSet()));
		when(userService.getCurrentUser()).thenReturn(user);
		Assert.assertTrue(defaultB2BReservationFacade.validateUnit("TEST_UID"));
	}

	@Test
	public void testFindTotal()
	{
		when(travelb2bOrderService.findTotal(b2bUnitCodes, email, date, date, costCenterUid, currency)).thenReturn(100d);

		final PriceData priceData = new PriceData();
		priceData.setValue(BigDecimal.valueOf(100d));
		when(travelCommercePriceFacade.createPriceData(Matchers.any(PriceDataType.class), Matchers.any(BigDecimal.class),
				Matchers.anyString()))
				.thenReturn(priceData);
		Assert.assertEquals(priceData,
				defaultB2BReservationFacade.findTotal(b2bUnitCodes, email, date, date, costCenterUid, currency));

		when(travelb2bOrderService.findTotal(b2bUnitCodes, email, date, date, costCenterUid, currency)).thenReturn(null);
		Assert.assertNull(defaultB2BReservationFacade.findTotal(b2bUnitCodes, email, date, date, costCenterUid, currency));
	}
}
