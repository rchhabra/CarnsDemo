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

package de.hybris.platform.travelfacades.promotion.impl;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultTravelPromotionsFacadeTest
{
	private static final String CUSTOMER_GROUP_UID = "customergroup";
	private static final String ADMIN_GROUP_UID = "admingroup";

	@InjectMocks
	final DefaultTravelPromotionsFacade travelPromotionFacade = new DefaultTravelPromotionsFacade()
	{
		@Override
		protected Collection<PromotionData> convertPromotions(final List<ProductPromotionModel> promotions)
		{
			return TestData.createPromotionsDatas();
		};
	};

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private UserService userService;

	@Mock
	private PromotionsService promotionsService;

	@Mock
	private TimeService timeService;

	@Mock
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;

	private UserModel user;

	@Before
	public void setup()
	{
		user = new UserModel();
		given(userService.getCurrentUser()).willReturn(user);
	}

	@Test
	public void testIfUserIsEligibleForTravelPromotions()
	{
		final UserGroupModel customerUserGroup = new UserGroupModel();
		customerUserGroup.setUid(CUSTOMER_GROUP_UID);

		final UserGroupModel adminUserGroup = new UserGroupModel();
		adminUserGroup.setUid(ADMIN_GROUP_UID);

		final Set<UserGroupModel> userGroups = new HashSet<>();
		userGroups.add(customerUserGroup);
		userGroups.add(adminUserGroup);

		given(userService.getAllUserGroupsForUser(user)).willReturn(userGroups);
		given(userService.isAnonymousUser(user)).willReturn(false);

		final boolean result = travelPromotionFacade.isCurrentUserEligibleForTravelPromotions();

		Assert.assertTrue(result);
	}

	@Test
	public void testIfUserIsNotEligibleForTravelPromotions()
	{
		final UserGroupModel customerUserGroup = new UserGroupModel();
		customerUserGroup.setUid(CUSTOMER_GROUP_UID);

		final Set<UserGroupModel> userGroups = new HashSet<>();
		userGroups.add(customerUserGroup);

		given(userService.getAllUserGroupsForUser(user)).willReturn(userGroups);
		given(userService.isAnonymousUser(user)).willReturn(false);

		final boolean result = travelPromotionFacade.isCurrentUserEligibleForTravelPromotions();

		Assert.assertFalse(result);
	}

	@Test
	public void testIfUserIsAnonymousUser()
	{
		given(userService.isAnonymousUser(user)).willReturn(true);

		final boolean result = travelPromotionFacade.isCurrentUserEligibleForTravelPromotions();

		Assert.assertFalse(result);
	}

	@Test
	public void populatePotentialPromotionsTest()
	{
		final PromotionGroupModel defaultPromotionGroup = new PromotionGroupModel();

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setDefaultPromotionGroup(defaultPromotionGroup);

		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(promotionsService.getProductPromotions(Mockito.anyCollectionOf(PromotionGroupModel.class),
				Mockito.any(ProductModel.class), Mockito.anyBoolean(), Mockito.any(Date.class)))
						.willReturn(TestData.createPromotionsModels());

		final ProductData productData = new ProductData();

		travelPromotionFacade.populatePotentialPromotions(new ProductModel(), productData);

		Assert.assertTrue(CollectionUtils.isNotEmpty(productData.getPotentialPromotions()));
		Assert.assertEquals(2, CollectionUtils.size(productData.getPotentialPromotions()));

		final List<PromotionData> promotions = (List<PromotionData>) productData.getPotentialPromotions();

		Assert.assertEquals("halfprice", promotions.get(0).getCode());
		Assert.assertEquals("freedelivery", promotions.get(1).getCode());
	}

	@Test
	public void testForNoPromotions()
	{
		final PromotionGroupModel defaultPromotionGroup = new PromotionGroupModel();

		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setDefaultPromotionGroup(defaultPromotionGroup);

		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);
		given(promotionsService.getProductPromotions(Mockito.anyCollectionOf(PromotionGroupModel.class),
				Mockito.any(ProductModel.class), Mockito.anyBoolean(), Mockito.any(Date.class))).willReturn(null);

		final ProductData productData = new ProductData();
		travelPromotionFacade.populatePotentialPromotions(new ProductModel(), productData);

		Assert.assertNull(productData.getPotentialPromotions());
	}

	@Test
	public void testForNoPromotionGroups()
	{
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setDefaultPromotionGroup(null);

		given(baseSiteService.getCurrentBaseSite()).willReturn(baseSite);

		final ProductData productData = new ProductData();
		travelPromotionFacade.populatePotentialPromotions(new ProductModel(), productData);

		Assert.assertNull(productData.getPotentialPromotions());
	}

	private static class TestData
	{

		public static List<ProductPromotionModel> createPromotionsModels()
		{
			final List<ProductPromotionModel> promotions = new ArrayList<>();
			promotions.add(Mockito.mock(ProductPromotionModel.class));
			return promotions;
		}

		public static Collection<PromotionData> createPromotionsDatas()
		{
			final List<PromotionData> promotions = new ArrayList<>();
			promotions.add(createPromotionDate("halfprice"));
			promotions.add(createPromotionDate("freedelivery"));
			return promotions;
		}

		public static PromotionData createPromotionDate(final String code)
		{
			final PromotionData promotion = new PromotionData();
			promotion.setCode(code);
			return promotion;
		}
	}

}
