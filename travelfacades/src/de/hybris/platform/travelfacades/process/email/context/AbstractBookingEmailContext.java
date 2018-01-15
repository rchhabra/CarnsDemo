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

package de.hybris.platform.travelfacades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelservices.email.context.AbstractTravelBookingEmailContext;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Required;

/**
 * Abstract class that exposes the common methods for the Booking Amendment Email Context.
 */
public abstract class AbstractBookingEmailContext extends AbstractTravelBookingEmailContext
{
	private static final String ADDITIONAL_NOTIFICATION_EMAILS = "additionalNotificationEmails";

	private ReservationFacade reservationFacade;

	private String storeName;

	private PriceData zeroPrice;

	private DateTool date;

	private TravellerService travellerService;

	private BookingService bookingService;

	private TravelCommercePriceFacade travelCommercePriceFacade;

	/**
	 * @deprecated Deprecated since version 3.0.
	 */
	@Deprecated
	private PriceDataFactory priceDataFactory;

	@Override
	public void init(final OrderProcessModel orderProcessModel, final EmailPageModel emailPageModel)
	{
		super.init(orderProcessModel, emailPageModel);

		final Set<String> additionalNotificationEmails = getAdditionalEmails(orderProcessModel);
		put(ADDITIONAL_NOTIFICATION_EMAILS, additionalNotificationEmails);

		zeroPrice = getTravelCommercePriceFacade().createPriceData(PriceDataType.BUY, BigDecimal.ZERO,
				orderProcessModel.getOrder().getCurrency());
		storeName = orderProcessModel.getOrder().getStore().getName();
	}

	/**
	 * Init guests emails.
	 *
	 * @param orderProcessModel
	 * 		the order process model
	 * @param additionalEmails
	 * 		the additional emails
	 */
	protected void initGuestsEmails(final OrderProcessModel orderProcessModel, final Set<String> additionalEmails)
	{
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups =
				bookingService.getAccommodationOrderEntryGroups(orderProcessModel.getOrder());

		if (CollectionUtils.isEmpty(accommodationOrderEntryGroups))
		{
			return;
		}

		for (final AccommodationOrderEntryGroupModel aoegm : accommodationOrderEntryGroups)
		{
			if (StringUtils.isNotBlank(aoegm.getContactEmail()))
			{
				additionalEmails.add(aoegm.getContactEmail());
			}
		}
	}

	/**
	 * Init travellers emails.
	 *
	 * @param orderProcessModel
	 * 		the order process model
	 * @param additionalEmails
	 * 		the additional emails
	 */
	protected void initTravellersEmails(final OrderProcessModel orderProcessModel, final Set<String> additionalEmails)
	{
		final List<TravellerModel> travellers = getTravellerService().getTravellers(orderProcessModel.getOrder().getEntries());

		for (final TravellerModel traveller : travellers)
		{
			if (!(traveller.getInfo() instanceof PassengerInformationModel))
			{
				continue;
			}

			final PassengerInformationModel passengerInfo = (PassengerInformationModel) traveller.getInfo();
			if (StringUtils.isNotBlank(passengerInfo.getEmail()))
			{
				additionalEmails.add(passengerInfo.getEmail());
			}
		}
	}

	/**
	 * Gets additional emails.
	 *
	 * @return the additional emails
	 */
	protected abstract Set<String> getAdditionalEmails(final OrderProcessModel orderProcessModel);

	@Override
	protected BaseSiteModel getSite(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getSite();
	}

	@Override
	protected CustomerModel getCustomer(final OrderProcessModel orderProcessModel)
	{
		return (CustomerModel) orderProcessModel.getOrder().getUser();
	}

	@Override
	protected LanguageModel getEmailLanguage(final OrderProcessModel orderProcessModel)
	{
		return orderProcessModel.getOrder().getLanguage();
	}

	/**
	 * @return the reservationFacade
	 */
	public ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * @param reservationFacade
	 *           the reservationFacade to set
	 */
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
	}

	/**
	 * @return the zeroPrice
	 */
	public PriceData getZeroPrice()
	{
		return zeroPrice;
	}

	/**
	 * @param zeroPrice
	 *           the zeroPrice to set
	 */
	public void setZeroPrice(final PriceData zeroPrice)
	{
		this.zeroPrice = zeroPrice;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @return the priceDataFactory
	 */
	@Deprecated
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	/**
	 * @deprecated Deprecated since version 3.0.
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	@Deprecated
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	/**
	 * @return the date
	 */
	public DateTool getDate()
	{
		return date;
	}

	/**
	 * @param date
	 *           the date to set
	 */
	public void setDate(final DateTool date)
	{
		this.date = date;
	}

	/**
	 * @return String
	 */
	public String getStoreName()
	{
		return storeName;
	}

	public TravellerService getTravellerService()
	{
		return travellerService;
	}

	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	public BookingService getBookingService()
	{
		return bookingService;
	}

	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}


	/**
	 * Gets travel commerce price facade.
	 *
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * Sets travel commerce price facade.
	 *
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}
}
