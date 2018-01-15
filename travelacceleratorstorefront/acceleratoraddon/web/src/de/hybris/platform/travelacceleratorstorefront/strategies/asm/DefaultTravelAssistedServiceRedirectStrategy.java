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
 */

package de.hybris.platform.travelacceleratorstorefront.strategies.asm;

import de.hybris.platform.assistedservicestorefront.constants.AssistedservicestorefrontConstants;
import de.hybris.platform.assistedservicestorefront.redirect.AssistedServiceRedirectStrategy;
import de.hybris.platform.assistedservicestorefront.redirect.impl.DefaultAssistedServiceRedirectStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.util.Config;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default travel implementation for {@link AssistedServiceRedirectStrategy}.
 * Based on the booking journey type of the session cart, a strategy is retrieve from a map and the correct redirect path is
 * returned.
 */
public class DefaultTravelAssistedServiceRedirectStrategy extends DefaultAssistedServiceRedirectStrategy
{
	private static final String DEFAULT_CART_REDIRECT = "/";
	private final static String DEFAULT_ORDER_REDIRECT = "/manage-booking/booking-details/%s";

	private TravelCartFacade travelCartFacade;
	private Map<String, AssistedServiceRedirectByJourneyTypeStrategy> redirectStrategyMap;

	@Override
	protected String getPathWithCart()
	{
		final CartModel sessionCart = getCartService().getSessionCart();
		if(getTravelCartFacade().isAmendmentCart())
		{
			return String.format(Config.getString(AssistedservicestorefrontConstants.REDIRECT_WITH_ORDER, DEFAULT_ORDER_REDIRECT),
					getTravelCartFacade().getOriginalOrderCode());
		}

		final BookingJourneyType bookingJourneyType = sessionCart.getBookingJourneyType();
		final AssistedServiceRedirectByJourneyTypeStrategy strategy = getRedirectStrategyMap().get(bookingJourneyType.getCode());
		if (Objects.nonNull(strategy))
		{
			return strategy.getRedirectPath(sessionCart);
		}

		return Config.getString(AssistedservicestorefrontConstants.REDIRECT_WITH_CART, DEFAULT_CART_REDIRECT);
	}

	/**
	 * @return the redirectStrategyMap
	 */
	protected Map<String, AssistedServiceRedirectByJourneyTypeStrategy> getRedirectStrategyMap()
	{
		return redirectStrategyMap;
	}

	/**
	 * @param redirectStrategyMap
	 * 		the redirectStrategyMap to set
	 */
	@Required
	public void setRedirectStrategyMap(final Map<String, AssistedServiceRedirectByJourneyTypeStrategy> redirectStrategyMap)
	{
		this.redirectStrategyMap = redirectStrategyMap;
	}

	/**
	 * @return the travelCartFacade
	 */
	protected TravelCartFacade getTravelCartFacade()
	{
		return travelCartFacade;
	}

	/**
	 * @param travelCartFacade
	 * 		the travelCartFacade to set
	 */
	@Required
	public void setTravelCartFacade(final TravelCartFacade travelCartFacade)
	{
		this.travelCartFacade = travelCartFacade;
	}
}
