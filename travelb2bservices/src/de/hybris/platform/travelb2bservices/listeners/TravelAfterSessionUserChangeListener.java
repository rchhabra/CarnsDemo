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

package de.hybris.platform.travelb2bservices.listeners;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.events.AfterSessionUserChangeListener;
import de.hybris.platform.servicelayer.event.events.AfterSessionUserChangeEvent;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Listener on AfterSessionUserChangeEvent detecting if the user is a B2BCustomer and eventually updating the branch in
 * session.
 */
public class TravelAfterSessionUserChangeListener extends AbstractEventListener<AfterSessionUserChangeEvent>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	private SessionService sessionService;

	private CartService cartService;

	private UserService userService;

	private static final Logger LOG = Logger.getLogger(AfterSessionUserChangeListener.class);

	@Override
	protected void onEvent(final AfterSessionUserChangeEvent event)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("AfterSessionUserChangeEvent received.");
		}
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof B2BCustomerModel)
		{
			getB2bUnitService().updateBranchInSession(getSessionService().getCurrentSession(), user);
		}
		getCartService().changeCurrentCartUser(user);
	}

	/**
	 * Gets B2B unit service.
	 *
	 * @return the B2B unit service
	 */
	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * Sets B2B unit service.
	 *
	 * @param b2bUnitService
	 * 		the B2B unit service
	 */
	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	/**
	 * Gets session service.
	 *
	 * @return the session service
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the session service
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets cart service.
	 *
	 * @return the cart service
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * Sets cart service.
	 *
	 * @param cartService
	 * 		the cart service
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * Gets user service.
	 *
	 * @return the user service
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * Sets user service.
	 *
	 * @param userService
	 * 		the user service
	 */
	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}


}
