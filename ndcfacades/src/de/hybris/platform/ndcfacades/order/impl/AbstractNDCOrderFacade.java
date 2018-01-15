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
package de.hybris.platform.ndcfacades.order.impl;

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ndcfacades.constants.NdcfacadesConstants;
import de.hybris.platform.ndcservices.exceptions.NDCOrderException;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * An abstract class to collect methods used across different NDC Order facades
 */
public class AbstractNDCOrderFacade
{
	private static final Logger LOG = Logger.getLogger(AbstractNDCOrderFacade.class);

	private ConfigurationService configurationService;
	private BusinessProcessService businessProcessService;
	private ModelService modelService;
	private BookingService bookingService;
	private ReservationFacade reservationFacade;
	private UserService userService;

	/**
	 * Method that triggers the order process
	 *
	 * @param order
	 * 		the order
	 */
	protected void startNDCOrderProcess(final OrderModel order)
	{
		final String fulfilmentProcessDefinitionName = getConfigurationService().getConfiguration()
				.getString(NdcfacadesConstants.NDC_ORDER_PROCESS);
		if (fulfilmentProcessDefinitionName == null)
		{
			LOG.warn("Unable to start fulfilment process for order [" + order.getCode() + "].");
		}
		else
		{
			if (fulfilmentProcessDefinitionName.isEmpty())
			{
				LOG.warn("Unable to start fulfilment process for order [" + order.getCode() + "].");
			}
			else
			{
				final String processCode = fulfilmentProcessDefinitionName + "-" + order.getCode() + "-" + System.currentTimeMillis();
				final OrderProcessModel businessProcessModel = getBusinessProcessService().createProcess(processCode,
						fulfilmentProcessDefinitionName);
				businessProcessModel.setOrder(order);
				getModelService().save(businessProcessModel);
				getBusinessProcessService().startProcess(businessProcessModel);
				if (LOG.isInfoEnabled())
				{
					LOG.info(String.format("Started the process %s", processCode));
				}
			}
		}
	}

	/**
	 * Retrieves an order based on the bookingReference number and the last name provided, {@link NDCOrderException} is
	 * thrown in case of invalid information are provided
	 *
	 * @param bookingReference
	 * 		the booking reference
	 * @param lastName
	 * 		the last name
	 *
	 * @return order by booking reference last name
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected OrderModel getOrderByBookingReferenceLastName(final String bookingReference, final String lastName)
			throws NDCOrderException
	{
		final OrderModel order = getBookingService().getOrderModelFromStore(bookingReference);

		if (Objects.isNull(order))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_MANAGE_BOOKING_INFORMATION));
		}

		final ReservationData reservationData = getReservationFacade().getBasicReservationData(order);

		if (reservationData != null)
		{
			final List<TravellerData> travellerDataList = reservationData.getReservationItems().stream()
					.flatMap(reservationItem -> reservationItem.getReservationItinerary().getTravellers().stream())
					.collect(Collectors.toList());

			if (!isValidLastName(lastName, travellerDataList))
			{
				throw new NDCOrderException(
						getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_MANAGE_BOOKING_INFORMATION));
			}
		}
		return order;
	}

	/**
	 * Retrieves an order based on the bookingReference number and the current user, {@link NDCOrderException} is
	 * thrown in case the booking wasn not place by the current user
	 *
	 * @param bookingReference
	 * 		the booking reference
	 *
	 * @return order by booking reference
	 * @throws NDCOrderException
	 * 		the ndc order exception
	 */
	protected OrderModel getOrderByBookingReference(final String bookingReference) throws NDCOrderException
	{
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(bookingReference);

		if (Objects.isNull(orderModel))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_ORDER_OR_USER));
		}

		final UserModel booker = orderModel.getUser();

		if (!booker.equals(getUserService().getCurrentUser()))
		{
			throw new NDCOrderException(
					getConfigurationService().getConfiguration().getString(NdcfacadesConstants.INVALID_ORDER_OR_USER));
		}

		return orderModel;
	}

	/**
	 * Checks if the last name has been provided in the request
	 *
	 * @param lastName
	 * 		the last name
	 * @param travellerDataList
	 * 		the traveller data list
	 *
	 * @return boolean
	 */
	protected boolean isValidLastName(final String lastName, final List<TravellerData> travellerDataList)
	{
		final Optional<PassengerInformationData> passengerInformation = travellerDataList.stream()
				.filter(travellerData -> StringUtils.equals(TravellerType.PASSENGER.toString(), travellerData.getTravellerType()))
				.map(travellerData -> (PassengerInformationData) travellerData.getTravellerInfo())
				.filter(passengerInfoData -> StringUtils.equalsIgnoreCase(lastName, passengerInfoData.getSurname())).findAny();

		return passengerInformation.isPresent();
	}

	/**
	 * Checks if the order has already been saved and, in case, deletes it.
	 *
	 * @param order
	 * 		the order
	 */
	protected void removeOrder(final OrderModel order)
	{
		if (!getModelService().isNew(order))
		{
			getModelService().refresh(order);
			if(CollectionUtils.isNotEmpty(order.getSelectedAccommodations()))
			{
				getModelService().removeAll(order.getSelectedAccommodations());
			}
			getModelService().remove(order);
		}
	}

	/**
	 * Checks if the guest has already been saved and, in case, deletes it.
	 *
	 * @param guest
	 * 		the guest
	 */
	protected void removeUser(final CustomerModel guest)
	{
		if (!getModelService().isNew(guest))
		{
			getModelService().refresh(guest);
			getModelService().remove(guest);
		}
	}

	/**
	 * Gets configuration service.
	 *
	 * @return the configuration service
	 */
	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * Sets configuration service.
	 *
	 * @param configurationService
	 * 		the configuration service
	 */
	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * Gets business process service.
	 *
	 * @return the business process service
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * Sets business process service.
	 *
	 * @param businessProcessService
	 * 		the business process service
	 */
	@Required
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * Gets model service.
	 *
	 * @return the model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 * 		the model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets booking service.
	 *
	 * @return the booking service
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 * 		the booking service
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * Gets reservation facade.
	 *
	 * @return the reservation facade
	 */
	protected ReservationFacade getReservationFacade()
	{
		return reservationFacade;
	}

	/**
	 * Sets reservation facade.
	 *
	 * @param reservationFacade
	 * 		the reservation facade
	 */
	@Required
	public void setReservationFacade(final ReservationFacade reservationFacade)
	{
		this.reservationFacade = reservationFacade;
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
