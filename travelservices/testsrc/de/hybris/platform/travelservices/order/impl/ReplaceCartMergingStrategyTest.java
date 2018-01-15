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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.order.CommerceAddToCartStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.travelservices.order.impl.ReplaceCartMergingStrategy.MergeAction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReplaceCartMergingStrategyTest
{
	@InjectMocks
	ReplaceCartMergingStrategy replaceCartMergingStrategy;

	@Mock
	private UserService userService;

	@Mock
	private CommerceCartService commerceCartService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CommerceAddToCartStrategy commerceAddToCartStrategy;

	@Before
	public void setup()
	{
	}

	@Test(expected = AccessDeniedException.class)
	public void testMergeCartsWithNullUser() throws CommerceCartMergingException
	{
		final CartModel fromCart = null;
		final CartModel toCart = null;
		final List<CommerceCartModification> modifications = null;
		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeCartsWithNullfromCart() throws CommerceCartMergingException
	{
		final CartModel fromCart = null;
		final CartModel toCart = null;
		final List<CommerceCartModification> modifications = null;
		final UserModel userModel = new UserModel();

		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testMergeCartsWithNullToCart() throws CommerceCartMergingException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = null;
		final List<CommerceCartModification> modifications = null;
		final UserModel userModel = new UserModel();

		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}

	@Test(expected = CommerceCartMergingException.class)
	public void testMergeCartsCurrentBaseSiteNotEqFromCartSite() throws CommerceCartMergingException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final List<CommerceCartModification> modifications = null;
		final UserModel userModel = new UserModel();

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(new BaseSiteModel());
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}

	@Test(expected = CommerceCartMergingException.class)
	public void testMergeCartsCurrentBaseSiteNotEqToCartSite() throws CommerceCartMergingException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final List<CommerceCartModification> modifications = null;
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);

		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}

	@Test(expected = CommerceCartMergingException.class)
	public void testMergeCartsFromCartGuidEqToCartGuid() throws CommerceCartMergingException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final List<CommerceCartModification> modifications = null;
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("Guid");
		toCart.setGuid("Guid");

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);

		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}


	@Test(expected = CommerceCartMergingException.class)
	public void testMergeCartsMergeActionEqMERGEWithException()
			throws CommerceCartModificationException, CommerceCartMergingException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("fromCartGuid");
		toCart.setGuid("toCartGuid");
		replaceCartMergingStrategy.setMergeAction(MergeAction.MERGE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List enteries = new ArrayList<AbstractOrderEntryModel>(1);
		enteries.add(entry);
		fromCart.setEntries(enteries);

		final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>(1);

		replaceCartMergingStrategy.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);
		modifications.add(commerceCartModification);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		Mockito.when(commerceAddToCartStrategy.addToCart(org.mockito.Matchers.any(CommerceCartParameter.class)))
				.thenThrow(new CommerceCartModificationException("CommerceCartModificationException for testing"));

		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

		Mockito.verify(replaceCartMergingStrategy).mergeCarts(fromCart, toCart, modifications);

	}


	@Test
	public void testMergeCartsMergeActionEqMERGE() throws CommerceCartMergingException, CommerceCartModificationException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("fromCartGuid");
		toCart.setGuid("toCartGuid");
		replaceCartMergingStrategy.setMergeAction(MergeAction.MERGE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List enteries = new ArrayList<AbstractOrderEntryModel>(1);
		enteries.add(entry);
		fromCart.setEntries(enteries);

		final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>(1);

		replaceCartMergingStrategy.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);
		modifications.add(commerceCartModification);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		Mockito.when(commerceAddToCartStrategy.addToCart(org.mockito.Matchers.any(CommerceCartParameter.class)))
				.thenReturn(commerceCartModification);

		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		Mockito.when(commerceCartModification.getEntry()).thenReturn(mockEntry);
		Mockito.when(mockEntry.getPk()).thenReturn(PK.createPK(3456));

		final ModelService modelService = Mockito.mock(ModelService.class);
		replaceCartMergingStrategy.setModelService(modelService);


		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

	}


	@Test
	public void testMergeCartsMergeActionEqMERGEStatusCodeEqSUCCESS() throws CommerceCartMergingException, CommerceCartModificationException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("fromCartGuid");
		toCart.setGuid("toCartGuid");
		replaceCartMergingStrategy.setMergeAction(MergeAction.MERGE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List enteries = new ArrayList<AbstractOrderEntryModel>(1);
		enteries.add(entry);
		fromCart.setEntries(enteries);

		final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>(1);

		replaceCartMergingStrategy.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);
		modifications.add(commerceCartModification);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		Mockito.when(commerceAddToCartStrategy.addToCart(org.mockito.Matchers.any(CommerceCartParameter.class)))
				.thenReturn(commerceCartModification);

		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		Mockito.when(commerceCartModification.getEntry()).thenReturn(mockEntry);
		Mockito.when(mockEntry.getPk()).thenReturn(PK.createPK(3456));
		Mockito.when(commerceCartModification.getStatusCode()).thenReturn(CommerceCartModificationStatus.SUCCESS);

		final ModelService modelService = Mockito.mock(ModelService.class);
		replaceCartMergingStrategy.setModelService(modelService);


		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

	}


	@Test
	public void testMergeCartsMergeActionEqMERGEWithNullEntryPk()
			throws CommerceCartMergingException, CommerceCartModificationException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("fromCartGuid");
		toCart.setGuid("toCartGuid");
		replaceCartMergingStrategy.setMergeAction(MergeAction.MERGE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List enteries = new ArrayList<AbstractOrderEntryModel>(1);
		enteries.add(entry);
		fromCart.setEntries(enteries);

		final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>(1);

		replaceCartMergingStrategy.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);
		modifications.add(commerceCartModification);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		Mockito.when(commerceAddToCartStrategy.addToCart(org.mockito.Matchers.any(CommerceCartParameter.class)))
				.thenReturn(commerceCartModification);

		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		Mockito.when(commerceCartModification.getEntry()).thenReturn(mockEntry);
		Mockito.when(mockEntry.getPk()).thenReturn(null);

		final ModelService modelService = Mockito.mock(ModelService.class);
		replaceCartMergingStrategy.setModelService(modelService);


		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

	}


	@Test
	public void testMergeCartsMergeActionEqOVERWRITE() throws CommerceCartMergingException, CommerceCartModificationException
	{
		final CartModel fromCart = new CartModel();
		final CartModel toCart = new CartModel();
		final UserModel userModel = new UserModel();
		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		fromCart.setSite(baseSiteModel);
		toCart.setSite(baseSiteModel);
		fromCart.setGuid("fromCartGuid");
		toCart.setGuid("toCartGuid");
		replaceCartMergingStrategy.setMergeAction(MergeAction.OVERWRITE);
		final AbstractOrderEntryModel entry = new AbstractOrderEntryModel();

		final List enteries = new ArrayList<AbstractOrderEntryModel>(1);
		enteries.add(entry);
		fromCart.setEntries(enteries);
		fromCart.setSaveTime(new Date());

		final List<CommerceCartModification> modifications = new ArrayList<CommerceCartModification>(1);

		replaceCartMergingStrategy.setCommerceAddToCartStrategy(commerceAddToCartStrategy);
		final CommerceCartModification commerceCartModification = Mockito.mock(CommerceCartModification.class);
		modifications.add(commerceCartModification);

		Mockito.when(replaceCartMergingStrategy.getBaseSiteService().getCurrentBaseSite()).thenReturn(baseSiteModel);
		Mockito.when(replaceCartMergingStrategy.getUserService().getCurrentUser()).thenReturn(userModel);
		Mockito.when(commerceAddToCartStrategy.addToCart(org.mockito.Matchers.any(CommerceCartParameter.class)))
				.thenReturn(commerceCartModification);

		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		Mockito.when(commerceCartModification.getEntry()).thenReturn(mockEntry);
		Mockito.when(mockEntry.getPk()).thenReturn(PK.createPK(3456));

		final ModelService modelService = Mockito.mock(ModelService.class);
		replaceCartMergingStrategy.setModelService(modelService);


		replaceCartMergingStrategy.mergeCarts(fromCart, toCart, modifications);

	}
}
