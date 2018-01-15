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

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.commercefacades.travel.CheckInRequestData;
import de.hybris.platform.commercefacades.travel.CheckInResponseData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerTransportOfferingInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.CheckInProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.strategies.CheckInEvaluatorStrategy;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.PassengerTypeModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;


/**
 * Default implementation of CheckInFacade
 */
public class DefaultCheckInFacade implements CheckInFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCheckInFacade.class);

	private BaseStoreService baseStoreService;
	private TravellerService travellerService;
	private ModelService modelService;
	private BookingService bookingService;
	private BusinessProcessService businessProcessService;
	private CheckInEvaluatorStrategy checkInEvaluatorStrategy;
	private CommonI18NService commonI18NService;

	@Override
	public CheckInResponseData doCheckin(final CheckInRequestData checkInRequest)
	{
		final CheckInResponseData checkInResponseData = new CheckInResponseData();
		checkInResponseData.setWarnings(new ArrayList<>());

		final List<TravellerTransportOfferingInfoData> travellerTransportOfferingInfos = checkInRequest
				.getTravellerTransportOfferingInfos();

		if (CollectionUtils.isEmpty(travellerTransportOfferingInfos))
		{
			return handleErrorMessageWithLog("checkin.no.passenger.error.message", checkInResponseData, null);
		}

		for (final TravellerTransportOfferingInfoData travellerTransportOfferingInfoData : travellerTransportOfferingInfos)
		{
			final TravellerData traveller = travellerTransportOfferingInfoData.getTraveller();
			final String travellerUid = traveller.getUid();
			String travellerIdentifier = traveller.getLabel();
			final PassengerInformationData passengerInformationData = (PassengerInformationData) traveller.getTravellerInfo();

			final TravellerModel travellerModel = getTravellerService().getExistingTraveller(travellerUid);
			if (travellerModel == null)
			{
				return handleErrorMessageWithLog("checkin.no.traveller.found.error.message", checkInResponseData,
						travellerIdentifier);
			}

			if (TravellerType.PASSENGER.equals(travellerModel.getType()) && travellerModel.getInfo() != null)
			{
				final PassengerInformationModel passengerInformationModel = (PassengerInformationModel) travellerModel.getInfo();
				final PassengerTypeModel passengerTypeModel = passengerInformationModel.getPassengerType();
				final StringBuilder passengerNameBuilder = new StringBuilder(passengerInformationModel.getFirstName()).append(" ")
						.append(passengerInformationModel.getSurname());
				travellerIdentifier = passengerNameBuilder.toString();

				if (!isPassengerAgeValidAtTimeOfTravel(passengerTypeModel, passengerInformationData.getDateOfBirth(),
						travellerTransportOfferingInfoData.getTransportOffering().getArrivalTime()))
				{
					return handleErrorMessageWithLog("checkin.traveller.age.exceded.error.message", checkInResponseData,
							travellerIdentifier);
				}
				populatePassengerInformation(passengerInformationModel, passengerInformationData);
				getModelService().save(travellerModel);
			}
		}
		return checkInResponseData;
	}

	protected void populatePassengerInformation(final PassengerInformationModel passengerInformationModel,
			final PassengerInformationData passengerInformationData)
	{
		passengerInformationModel.setDateOfBirth(passengerInformationData.getDateOfBirth());
		passengerInformationModel.setDocumentType(passengerInformationData.getDocumentType());
		passengerInformationModel.setDocumentNumber(passengerInformationData.getDocumentNumber());
		passengerInformationModel.setDocumentExpiryDate(passengerInformationData.getDocumentExpiryDate());
		if (passengerInformationData.getCountryOfIssue() != null
				&& passengerInformationData.getCountryOfIssue().getIsocode() != null)
		{
			passengerInformationModel
					.setCountryOfIssue(getCommonI18NService().getCountry(passengerInformationData.getCountryOfIssue().getIsocode()));
		}
		if (passengerInformationData.getNationality() != null && passengerInformationData.getNationality().getIsocode() != null)
		{
			passengerInformationModel
					.setNationality(getCommonI18NService().getCountry(passengerInformationData.getNationality().getIsocode()));
		}
		passengerInformationModel.setAPISType(passengerInformationData.getAPIType());
		getModelService().save(passengerInformationModel);
	}

	protected CheckInResponseData handleErrorMessageWithLog(final String errorMessageKey,
			final CheckInResponseData checkInResponseData, final String errorValueCode)
	{
		LOG.error(errorMessageKey);
		return handleErrorMessage(errorMessageKey, checkInResponseData, errorValueCode);
	}

	protected CheckInResponseData handleErrorMessage(final String errorMessageKey, final CheckInResponseData checkInResponseData,
			final String errorValueCode)
	{
		List<String> errorValue = null;
		if (errorValueCode != null)
		{
			errorValue = new ArrayList<>();
			errorValue.add(errorValueCode);
		}
		populateErrorMessageMap(checkInResponseData, errorMessageKey, errorValue);
		return checkInResponseData;
	}

	@Override
	public String getCheckinFlowGroupForCheckout()
	{
		if (getBaseStoreService().getCurrentBaseStore() != null)
		{
			return getBaseStoreService().getCurrentBaseStore().getCheckinFlowGroup();
		}
		return null;
	}

	protected void populateErrorMessageMap(final CheckInResponseData checkInResponseData, final String errorMessageKey,
			final List<String> errorValue)
	{
		final Map<String, List<String>> errorMap = new HashMap<>();
		errorMap.put(errorMessageKey, errorValue);
		checkInResponseData.setErrors(errorMap);
	}

	protected boolean isPassengerAgeValidAtTimeOfTravel(final PassengerTypeModel passengerTypeModel, final Date dateOfBirth,
			final Date arrivalTime)
	{
		final long ageInYears = TravelDateUtils.getYearsBetweenDates(dateOfBirth, arrivalTime);
		return !(passengerTypeModel.getMaxAge() != null && ageInYears > passengerTypeModel.getMaxAge().longValue());
	}

	@Override
	public boolean isCheckInPossible(final ReservationData reservation, final int originDestinationRefNumber)
	{
		return getCheckInEvaluatorStrategy().isCheckInPossible(reservation, originDestinationRefNumber);
	}

	@Override
	public boolean checkTravellerEligibility(final String travellerCode, final List<String> transportOfferingCodes,
			final String abstractOrderCode)
	{
		//Retrieve order with bookingReferenceNumber
		final OrderModel orderModel = getBookingService().getOrderModelFromStore(abstractOrderCode);

		final List<ConsignmentModel> consignments = orderModel.getConsignments().stream()
				.filter(consignment -> transportOfferingCodes.contains(consignment.getWarehouse().getCode())
						&& consignment.getTraveller().getLabel().equals(travellerCode))
				.collect(Collectors.toList());

		return !consignments.stream().anyMatch(consignment -> consignment.getStatus().equals(ConsignmentStatus.CHECKED_IN));
	}

	@Override
	public void startCheckInProcess(final String bookingReference, final int originDestinationRefNumber,
			final List<String> travellersToCheckIn)
	{
		final OrderModel orderModel = getBookingService().getOrder(bookingReference);
		if (Objects.isNull(orderModel))
		{
			return;
		}

		final CheckInProcessModel checkInProcessModel = (CheckInProcessModel) getBusinessProcessService()
				.createProcess("check-in-process-" + orderModel.getCode() + "-" + System.currentTimeMillis(), "check-in-process");
		checkInProcessModel.setOrder(orderModel);
		checkInProcessModel.setOriginDestinationRefNumber(originDestinationRefNumber);
		checkInProcessModel.setTravellers(travellersToCheckIn);

		getModelService().save(checkInProcessModel);
		getBusinessProcessService().startProcess(checkInProcessModel);
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * @param travellerService
	 *           the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the checkInEvaluatorStrategy
	 */
	protected CheckInEvaluatorStrategy getCheckInEvaluatorStrategy()
	{
		return checkInEvaluatorStrategy;
	}

	/**
	 * @param checkInEvaluatorStrategy
	 *           the checkInEvaluatorStrategy to set
	 */
	public void setCheckInEvaluatorStrategy(final CheckInEvaluatorStrategy checkInEvaluatorStrategy)
	{
		this.checkInEvaluatorStrategy = checkInEvaluatorStrategy;
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
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the businessProcessService
	 */
	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	/**
	 * @param businessProcessService
	 *           the businessProcessService to set
	 */
	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	/**
	 * @return the commonI18NService
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

}
