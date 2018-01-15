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

import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.travel.GlobalTravelReservationData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.reservation.manager.AccommodationReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.GlobalTravelReservationPipelineManager;
import de.hybris.platform.travelfacades.reservation.manager.ReservationPipelineManager;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link de.hybris.platform.travelfacades.facades.ReservationFacade} interface.
 */
public class DefaultReservationFacade implements ReservationFacade
{
	private ReservationPipelineManager reservationPipelineManager;
	private ReservationPipelineManager transportReservationSummaryPipelineManager;
	private AccommodationReservationPipelineManager accommmodationReservationSummaryPipelineManager;
	private AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager;
	private GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager;
	private GlobalTravelReservationPipelineManager cancelledGlobalTravelReservationPipelineManager;
	private ReservationPipelineManager packageTransportReservationSummaryPipelineManager;
	private ReservationPipelineManager reservationItemPipelineManager;
	private BookingService bookingService;

	private CartService cartService;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private ReservationPipelineManager reservationSummaryPipelineManager;

	@Override
	public ReservationData getReservationData(final AbstractOrderModel abstractOrderModel)
	{
		ReservationData reservationData = null;
		if (abstractOrderModel != null)
		{
			reservationData = reservationPipelineManager.executePipeline(abstractOrderModel);
		}
		return reservationData;
	}

	@Override
	public ReservationData getCurrentReservationData()
	{
		return getCartService().hasSessionCart() ? getReservationData(getCartService().getSessionCart()) : null;
	}

	@Override
	public ReservationData getCurrentReservationSummary()
	{
		return getCurrentReservationData(getTransportReservationSummaryPipelineManager());
	}

	@Override
	public ReservationData getCurrentPackageTransportReservationSummary()
	{
		return getCurrentReservationData(getPackageTransportReservationSummaryPipelineManager());
	}

	protected ReservationData getCurrentReservationData(final ReservationPipelineManager reservationPipelineManager)
	{
		if(getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			if (sessionCart != null)
			{
				return reservationPipelineManager.executePipeline(sessionCart);
			}
		}
		return null;
	}

	@Override
	public AccommodationReservationData getAccommodationReservationData(final AbstractOrderModel abstractOrderModel)
	{
		AccommodationReservationData accommodationReservationData = null;
		if (abstractOrderModel != null)
		{
			accommodationReservationData = getFullAccommodationReservationPipelineManager().executePipeline(abstractOrderModel);
		}
		return accommodationReservationData;
	}

	@Override
	public AccommodationReservationData getCurrentAccommodationReservation()
	{
		if(!getCartService().hasSessionCart())
		{
			return null;
		}
		final CartModel sessionCart = getCartService().getSessionCart();
		return getAccommodationReservationData(sessionCart);
	}

	@Override
	public AccommodationReservationData getCurrentAccommodationReservationSummary()
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel cartModel = getCartService().getSessionCart();
			return getAccommmodationReservationSummaryPipelineManager().executePipeline(cartModel);
		}

		return null;
	}

	@Override
	public AccommodationReservationData getAccommodationReservationSummary(final String orderCode)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(orderCode);
		return getAccommmodationReservationSummaryPipelineManager().executePipeline(orderModel);
	}

	@Override
	public GlobalTravelReservationData getGlobalTravelReservationData(final AbstractOrderModel abstractOrderModel)
	{
		GlobalTravelReservationData globalTravelReservationData = null;
		if (abstractOrderModel != null)
		{
			globalTravelReservationData = getGlobalTravelReservationPipelineManager().executePipeline(abstractOrderModel);
		}
		return globalTravelReservationData;
	}

	@Override
	public GlobalTravelReservationData retrieveGlobalReservationData(final String bookingReference)
	{
		return getGlobalTravelReservationData(getBookingService().getOrderModelFromStore(bookingReference));
	}

	@Override
	public GlobalTravelReservationData getCancelledGlobalTravelReservationData(final AbstractOrderModel abstractOrderModel)
	{
		GlobalTravelReservationData globalTravelReservationData = null;
		if (abstractOrderModel != null)
		{
			globalTravelReservationData = getCancelledGlobalTravelReservationPipelineManager().executePipeline(abstractOrderModel);
		}
		return globalTravelReservationData;
	}

	@Override
	public ReservationData getBasicReservationData(final AbstractOrderModel abstractOrderModel)
	{
		ReservationData reservationData = null;

		if (abstractOrderModel != null)
		{
			reservationData = getReservationItemPipelineManager().executePipeline(abstractOrderModel);
		}
		return reservationData;
	}

	@Override
	public String getBookerEmailIDFromReservationData(final GlobalTravelReservationData globalReservationData,
			final String lastName, final String passengerReference)
	{
		if (globalReservationData.getReservationData().getAdditionalSecurity() && Objects.isNull(passengerReference))
		{
			return null;
		}

		final List<TravellerData> travellerDataList = globalReservationData.getReservationData().getReservationItems().stream()
				.flatMap(reservationItem -> reservationItem.getReservationItinerary().getTravellers().stream())
				.collect(Collectors.toList());

		for (final TravellerData travellerData : travellerDataList)
		{
			if (globalReservationData.getReservationData().getAdditionalSecurity())
			{
				if (checkTraveller(lastName, travellerData) && StringUtils
						.equalsIgnoreCase(passengerReference, travellerData.getSimpleUID()))
				{
					return getCustomerUid(globalReservationData.getCustomerData());
				}
			}
			else
			{
				if (checkTraveller(lastName, travellerData))
				{
					return getCustomerUid(globalReservationData.getCustomerData());
				}
			}
		}
		return null;
	}

	@Override
	public String getBookingJourneyType(final String orderCode)
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(orderCode);
		return Objects.nonNull(orderModel.getBookingJourneyType()) ? orderModel.getBookingJourneyType().getCode() : null;
	}

	/**
	 * Check traveller boolean.
	 *
	 * @param lastName
	 * 		the last name
	 * @param travellerData
	 * 		the traveller data
	 * @return the boolean
	 */
	protected boolean checkTraveller(final String lastName, final TravellerData travellerData)
	{
		if (!TravellerType.PASSENGER.toString().equals(travellerData.getTravellerType()))
		{
			return false;
		}
		final PassengerInformationData passengerInfoData = (PassengerInformationData) travellerData.getTravellerInfo();
		return lastName.equalsIgnoreCase(passengerInfoData.getSurname());
	}

	/**
	 * Gets customer uid.
	 *
	 * @param customerData
	 * 		the customer data
	 * @return the customer uid
	 */
	protected String getCustomerUid(final CustomerData customerData)
	{
		final String uid = customerData.getUid();
		if (CustomerType.GUEST.equals(customerData.getType()))
		{
			return StringUtils.substringAfter(uid, "|");
		}
		return uid;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the reservationPipelineManager
	 */
	protected ReservationPipelineManager getReservationPipelineManager()
	{
		return reservationPipelineManager;
	}

	/**
	 * @param reservationPipelineManager
	 *           the reservationPipelineManager to set
	 */
	public void setReservationPipelineManager(final ReservationPipelineManager reservationPipelineManager)
	{
		this.reservationPipelineManager = reservationPipelineManager;
	}

	/**
	 * @return the fullAccommodationReservationPipelineManager
	 */
	protected AccommodationReservationPipelineManager getFullAccommodationReservationPipelineManager()
	{
		return fullAccommodationReservationPipelineManager;
	}

	/**
	 * @param fullAccommodationReservationPipelineManager
	 *           the fullAccommodationReservationPipelineManager to set
	 */
	public void setFullAccommodationReservationPipelineManager(
			final AccommodationReservationPipelineManager fullAccommodationReservationPipelineManager)
	{
		this.fullAccommodationReservationPipelineManager = fullAccommodationReservationPipelineManager;
	}

	/**
	 * @return the globalTravelReservationPipelineManager
	 */
	protected GlobalTravelReservationPipelineManager getGlobalTravelReservationPipelineManager()
	{
		return globalTravelReservationPipelineManager;
	}

	/**
	 * @param globalTravelReservationPipelineManager
	 *           the globalTravelReservationPipelineManager to set
	 */
	public void setGlobalTravelReservationPipelineManager(
			final GlobalTravelReservationPipelineManager globalTravelReservationPipelineManager)
	{
		this.globalTravelReservationPipelineManager = globalTravelReservationPipelineManager;
	}

	/**
	 * @return
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	protected ReservationPipelineManager getReservationSummaryPipelineManager()
	{
		return reservationSummaryPipelineManager;
	}

	/**
	 * @param reservationSummaryPipelineManager
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	@Required
	public void setReservationSummaryPipelineManager(final ReservationPipelineManager reservationSummaryPipelineManager)
	{
		this.reservationSummaryPipelineManager = reservationSummaryPipelineManager;
	}

	/**
	 * @return the accommmodationReservationSummaryPipelineManager
	 */
	protected AccommodationReservationPipelineManager getAccommmodationReservationSummaryPipelineManager()
	{
		return accommmodationReservationSummaryPipelineManager;
	}

	/**
	 * @param accommmodationReservationSummaryPipelineManager
	 *           the accommmodationReservationSummaryPipelineManager to set
	 */
	@Required
	public void setAccommmodationReservationSummaryPipelineManager(
			final AccommodationReservationPipelineManager accommmodationReservationSummaryPipelineManager)
	{
		this.accommmodationReservationSummaryPipelineManager = accommmodationReservationSummaryPipelineManager;
	}

	/**
	 * @return the cancelledGlobalTravelReservationPipelineManager
	 */
	protected GlobalTravelReservationPipelineManager getCancelledGlobalTravelReservationPipelineManager()
	{
		return cancelledGlobalTravelReservationPipelineManager;
	}

	/**
	 * @param cancelledGlobalTravelReservationPipelineManager
	 *           the cancelledGlobalTravelReservationPipelineManager to set
	 */
	@Required
	public void setCancelledGlobalTravelReservationPipelineManager(
			final GlobalTravelReservationPipelineManager cancelledGlobalTravelReservationPipelineManager)
	{
		this.cancelledGlobalTravelReservationPipelineManager = cancelledGlobalTravelReservationPipelineManager;
	}

	/**
	 * @return the transportReservationSummaryPipelineManager
	 */
	protected ReservationPipelineManager getTransportReservationSummaryPipelineManager()
	{
		return transportReservationSummaryPipelineManager;
	}

	/**
	 * @param transportReservationSummaryPipelineManager
	 *           the transportReservationSummaryPipelineManager to set
	 */
	@Required
	public void setTransportReservationSummaryPipelineManager(
			final ReservationPipelineManager transportReservationSummaryPipelineManager)
	{
		this.transportReservationSummaryPipelineManager = transportReservationSummaryPipelineManager;
	}

	/**
	 * @return the packageTransportReservationSummaryPipelineManager
	 */
	protected ReservationPipelineManager getPackageTransportReservationSummaryPipelineManager()
	{
		return packageTransportReservationSummaryPipelineManager;
	}

	/**
	 * @param packageTransportReservationSummaryPipelineManager
	 *           the packageTransportReservationSummaryPipelineManager to set
	 */
	@Required
	public void setPackageTransportReservationSummaryPipelineManager(
			final ReservationPipelineManager packageTransportReservationSummaryPipelineManager)
	{
		this.packageTransportReservationSummaryPipelineManager = packageTransportReservationSummaryPipelineManager;
	}

	protected ReservationPipelineManager getReservationItemPipelineManager()
	{
		return reservationItemPipelineManager;
	}

	@Required
	public void setReservationItemPipelineManager(final ReservationPipelineManager reservationItemPipelineManager)
	{
		this.reservationItemPipelineManager = reservationItemPipelineManager;
	}

	/**
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * @param bookingService
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}
}
