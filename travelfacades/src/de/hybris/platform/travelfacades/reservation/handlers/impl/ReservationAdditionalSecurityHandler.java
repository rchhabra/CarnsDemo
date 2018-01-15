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
package de.hybris.platform.travelfacades.reservation.handlers.impl;

import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelfacades.reservation.handlers.ReservationHandler;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * The type Reservation additional security handler.
 */
public class ReservationAdditionalSecurityHandler implements ReservationHandler
{
	private SessionService sessionService;

	@Override
	public void handle(final AbstractOrderModel abstractOrderModel, final ReservationData reservationData)
	{
		reservationData.setAdditionalSecurity(abstractOrderModel.getAdditionalSecurity());

		if (isFilterForAdditionalSecurityRequired(abstractOrderModel))
		{
			for (final ReservationItemData reservationItemData : reservationData.getReservationItems())
			{
				final Optional<TravellerData> traveller = reservationItemData.getReservationItinerary().getTravellers().stream()
						.filter(travellerData -> StringUtils
								.equalsIgnoreCase(getSessionService().getAttribute(TravelservicesConstants.PASSENGER_REFERENCE),
										travellerData.getSimpleUID())).findFirst();
				if(traveller.isPresent())
				{
					reservationItemData.getReservationItinerary().setTravellers(Collections.singletonList(traveller.get()));
				}
				else
				{
					reservationItemData.getReservationItinerary().setTravellers(Collections.emptyList());
				}
			}
			reservationData.setFilteredTravellers(Boolean.TRUE);
		}
		else
		{
			reservationData.setFilteredTravellers(Boolean.FALSE);
		}
	}

	/**
	 * Returns true if 1) the additional security is active, 2) the passenger open the reservation using the manage booking
	 * component, 3) the current user is not the one who place the booking and 4) the booking was placed with guest checkout no
	 * filter needs to be displayed
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return boolean
	 */
	protected boolean isFilterForAdditionalSecurityRequired(final AbstractOrderModel abstractOrderModel)
	{
		return BooleanUtils.isTrue(abstractOrderModel.getAdditionalSecurity())
				&& Objects.nonNull(getSessionService().getAttribute(TravelservicesConstants.PASSENGER_REFERENCE))
				&& !sameCustomer(abstractOrderModel)
				&& !isOrderPlacedWithGuestUser(abstractOrderModel);
	}

	/**
	 * Returns true if the provided order (or the one related to {@link AbstractOrderModel} cart) was placed through a
	 * guest checkout. The control on instanceof is done to prevent misbehaviour during the amendment journey. This because, during
	 * the amendment, a new user is created for a non registered traveller that is performing an amendment.
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return boolean
	 */
	protected boolean isOrderPlacedWithGuestUser(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.nonNull(abstractOrderModel.getOriginalOrder()))
		{
			return CustomerType.GUEST.equals(((CustomerModel) abstractOrderModel.getOriginalOrder().getUser()).getType());
		}
		else
		{
			return CustomerType.GUEST.equals(((CustomerModel) abstractOrderModel.getUser()).getType());
		}
	}

	/**
	 * Returns true if the UIDs of the passenger associated to the provided order matches with the one currently stored in session
	 *
	 * @param abstractOrderModel
	 * 		the abstract order model
	 * @return boolean
	 */
	protected boolean sameCustomer(final AbstractOrderModel abstractOrderModel)
	{
		if (Objects.isNull(getSessionService().getAttribute(TravelservicesConstants.USER)))
		{
			return false;
		}
		final String sessionCustomerUID = ((CustomerModel) getSessionService().getAttribute(TravelservicesConstants.USER)).getUid();
		final String orderUserUID;
		if (Objects.nonNull(abstractOrderModel.getOriginalOrder()))
		{
			orderUserUID = abstractOrderModel.getOriginalOrder().getUser().getUid();
		}
		else
		{
			orderUserUID = abstractOrderModel.getUser().getUid();
		}
		return StringUtils.equalsIgnoreCase(sessionCustomerUID, orderUserUID);
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
}
