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

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.PromotionData;
import de.hybris.platform.converters.Converters;
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
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;


/**
 * Implementation class for travel promotions.
 */
public class DefaultTravelPromotionsFacade implements TravelPromotionsFacade
{

	private static final String CUSTOMER_GROUP_UID = "customergroup";

	private BaseSiteService baseSiteService;
	private UserService userService;
	private PromotionsService promotionsService;
	private TimeService timeService;
	private Converter<AbstractPromotionModel, PromotionData> promotionsConverter;

	@Override
	public boolean isCurrentUserEligibleForTravelPromotions()
	{
		final UserModel user = getUserService().getCurrentUser();
		if (getUserService().isAnonymousUser(user))
		{
			return false;
		}
		final Set<UserGroupModel> userGroups = getUserService().getAllUserGroupsForUser(user);
		final Iterator<UserGroupModel> itr = userGroups.iterator();
		while (itr.hasNext())
		{
			final UserGroupModel userGroup = itr.next();
			if (userGroup.getUid().equals(CUSTOMER_GROUP_UID))
			{
				itr.remove();
			}
		}
		if (CollectionUtils.isNotEmpty(userGroups))
		{
			return true;
		}
		return false;
	}

	@Override
	public void populatePotentialPromotions(final ProductModel productModel, final ProductData productData)
	{
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
		final PromotionGroupModel defaultPromotionGroup = baseSiteModel.getDefaultPromotionGroup();
		if (defaultPromotionGroup == null)
		{
			return;
		}
		final List<ProductPromotionModel> promotions = getPromotionsService().getProductPromotions(
				Collections.singletonList(defaultPromotionGroup), productModel, true, getTimeService().getCurrentTime());
		if (promotions != null)
		{
			productData.setPotentialPromotions(convertPromotions(promotions));
		}
	}

	/**
	 * Convert promotions collection.
	 *
	 * @param promotions
	 * 		the promotions
	 * @return the collection
	 */
	protected Collection<PromotionData> convertPromotions(final List<ProductPromotionModel> promotions)
	{
		return Converters.convertAll(promotions, getPromotionsConverter());
	}

	/**
	 * Gets base site service.
	 *
	 * @return the baseSiteService
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Sets base site service.
	 *
	 * @param baseSiteService
	 * 		the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Gets user service.
	 *
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * Gets promotions service.
	 *
	 * @return the promotionsService
	 */
	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	/**
	 * Sets promotions service.
	 *
	 * @param promotionsService
	 * 		the promotionsService to set
	 */
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
	}

	/**
	 * Gets time service.
	 *
	 * @return timeService time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 * 		the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * Gets promotions converter.
	 *
	 * @return the promotionsConverter
	 */
	protected Converter<AbstractPromotionModel, PromotionData> getPromotionsConverter()
	{
		return promotionsConverter;
	}

	/**
	 * Sets promotions converter.
	 *
	 * @param promotionsConverter
	 * 		the promotionsConverter to set
	 */
	public void setPromotionsConverter(final Converter<AbstractPromotionModel, PromotionData> promotionsConverter)
	{
		this.promotionsConverter = promotionsConverter;
	}

}
