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

import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.PassengerTypeData;
import de.hybris.platform.commercefacades.travel.ReasonForTravelData;
import de.hybris.platform.commercefacades.travel.SpecialRequestDetailData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerPreferenceData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.PassengerTypeFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelservices.enums.ReasonForTravel;
import de.hybris.platform.travelservices.enums.TravellerType;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.SpecialServiceRequestModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;
import de.hybris.platform.travelservices.services.PassengerTypeService;
import de.hybris.platform.travelservices.services.SpecialServiceRequestService;
import de.hybris.platform.travelservices.services.TravellerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade that provides Traveller specific services. The facade uses the TravellerService to get TravellerModel and uses
 * converter/populators to transfer TravellerData type.
 */
public class DefaultTravellerFacade implements TravellerFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultTravellerFacade.class);

	private Converter<TravellerModel, TravellerData> travellerDataConverter;
	private Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter;
	private Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter;
	private Converter<PassengerInformationData, PassengerInformationModel> passengerDetailsReverseConverter;
	private Converter<TravellerPreferenceModel, TravellerPreferenceData> travellerPreferenceConverter;
	private Converter<TravellerPreferenceData, TravellerPreferenceModel> travellerPreferenceReverseConverter;

	private TravellerService travellerService;
	private EnumerationService enumerationService;
	private UserService userService;
	private ModelService modelService;
	private PassengerTypeService passengerTypeService;
	private CartService cartService;
	private SpecialServiceRequestService specialServiceRequestService;
	private PassengerTypeFacade passengerTypeFacade;

	@Override
	public TravellerData createTraveller(final String travellerType, final String passengerType, final String travellerCode,
			final int passengerNumber, final String travellerUidPrefix)
	{
		return createTraveller(travellerType, passengerType, travellerCode, passengerNumber, travellerUidPrefix, null);
	}

	@Override
	public TravellerData createTraveller(final String travellerType, final String passengerType, final String travellerCode,
			final int passengerNumber, final String travellerUidPrefix, final String cartOrOrderCode)
	{
		final TravellerModel travellerModel = getTravellerService().createTraveller(travellerType, passengerType, travellerCode,
				passengerNumber, travellerUidPrefix, cartOrOrderCode);
		return getTravellerDataConverter().convert(travellerModel);
	}

	@Override
	public List<ReasonForTravelData> getReasonForTravelTypes()
	{
		final List<ReasonForTravel> reasonForTravelTypes = getEnumerationService().getEnumerationValues(ReasonForTravel.class);
		final List<ReasonForTravelData> reasonForTravel = new ArrayList<>();

		reasonForTravelTypes.forEach(rof ->
		{

			final ReasonForTravelData reasonForTravelData = new ReasonForTravelData();
			reasonForTravelData.setCode(rof.getCode());
			reasonForTravelData.setName(getEnumerationService().getEnumerationName(rof));

			reasonForTravel.add(reasonForTravelData);
		});
		return reasonForTravel;
	}

	@Override
	public List<TravellerData> getTravellersForCartEntries()
	{
		if(!getCartService().hasSessionCart())
		{
			return null;
		}
		final List<TravellerModel> travellers = getTravellerService().getTravellers(getCartService().getSessionCart().getEntries());
		return Converters.convertAll(travellers, getTravellerDataConverter());
	}

	@Override
	public List<TravellerData> getSavedTravellersForCurrentUser()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final List<TravellerData> travellerData = new ArrayList<>();
		travellerData.addAll(Converters.convertAll(currentUser.getSavedTraveller(), getTravellerDataConverter()));
		return travellerData;
	}

	@Override
	public void updateTravellerDetails(final List<TravellerData> travellers)
	{
		for (final TravellerData td : travellers)
		{
			final PassengerInformationData passengerInformation = (PassengerInformationData) td.getTravellerInfo();
			final TravellerData newSavedTraveller;
			// does the user want their details to be save
			if (passengerInformation.isSaveDetails())
			{
				// check if this user is the one making the booking and if so then update the current Users CustomerPassengerInstance
				// otherwise save the user against the current Users savedTravellers
				if (td.isBooker())
				{
					updateCurrentUserCustomerPassengerInstance(td, getPassengerInformationReverseConverter());
					newSavedTraveller = getCustomerTravellerInstanceData();
					td.setSavedTravellerUid(newSavedTraveller.getUid());
				}
				else
				{
					newSavedTraveller = saveAndGetTravellerAgainstCurrentUser(td);
					td.setSavedTravellerUid(newSavedTraveller.getUid());
				}
			}
			updateAndGetExistingTravellerDetails(td, getPassengerInformationReverseConverter());
		}
	}

	@Override
	public void updateCustomerSavedTravellers(final List<TravellerData> travellers)
	{
		travellers.forEach(
				travellerData -> updateAndGetSavedTravellerDetails(travellerData, getPassengerInformationReverseConverter()));
	}

	/**
	 * Method checks to see if the current User has a Customer Passenger Instance. If one exists then it will be updated
	 * with the information from passengerInformationData otherwise a new Customer Passenger Instance will be created and
	 * set against the current Users.
	 *
	 * @param travellerData
	 *           the traveller data
	 * @deprecated Deprecated since version 2.0. Use
	 * {@link #updateCurrentUserCustomerPassengerInstance(TravellerData, Converter)} instead.
	 */
	@Deprecated
	protected void updateCurrentUserCustomerPassengerInstance(final TravellerData travellerData)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel == null)
		{
			currentUser.setCustomerTravellerInstance(createTravellerModel(travellerData));
			modelService.save(currentUser);
		}
		else
		{
			travellerData.setUid(travellerModel.getUid());
			updateAndGetSavedTravellerDetails(travellerData, getPassengerInformationReverseConverter());
		}
	}

	/**
	 * Method checks to see if the current User has a Customer Passenger Instance. If one exists then it will be updated
	 * with the information from passengerInformationData otherwise a new Customer Passenger Instance will be created and
	 * set against the current Users.
	 *
	 * @param travellerData
	 *           the traveller data
	 * @param passengerInformationReverseConverter
	 *           the passenger information reverse converter
	 */
	protected void updateCurrentUserCustomerPassengerInstance(final TravellerData travellerData,
			final Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel == null)
		{
			currentUser.setCustomerTravellerInstance(createTravellerModel(travellerData));
			modelService.save(currentUser);
		}
		else
		{
			travellerData.setUid(travellerModel.getUid());
			updateAndGetSavedTravellerDetails(travellerData, passengerInformationReverseConverter);
		}
	}

	/**
	 * Method takes the travellerData and checks if these are details for an existing traveller. If so then an update
	 * will occur otherwise a new instance of traveller model will be created and populated with values from
	 * passengerInformationData users account. Travellers are then saved as savedTravellers against the current users
	 * account.
	 *
	 * @param travellerData
	 *           the traveller data
	 * @deprecated Deprecated since version 2.0. Use {@link #saveAndGetTravellerAgainstCurrentUser(TravellerData)} instead.
	 */
	@Deprecated
	protected void saveTravellerAgainstCurrentUser(final TravellerData travellerData)
	{
		final PassengerInformationData passengerInformationData = (PassengerInformationData) travellerData.getTravellerInfo();

		if (StringUtils.isNotBlank(passengerInformationData.getSavedTravellerUId()))
		{
			travellerData.setUid(passengerInformationData.getSavedTravellerUId());
			updateExistingTravellerDetails(travellerData, true);
		}
		else
		{
			saveNewTravellerDetail(travellerData);
		}
	}

	/**
	 * Method takes the travellerData and checks if these are details for an existing traveller. If so then an update
	 * will occur otherwise a new instance of traveller model will be created and populated with values from
	 * passengerInformationData users account. Travellers are then saved as savedTravellers against the current users
	 * account.
	 *
	 * @param travellerData
	 *           the traveller data
	 * @return travellerData traveller data
	 */
	protected TravellerData saveAndGetTravellerAgainstCurrentUser(final TravellerData travellerData)
	{
		final PassengerInformationData passengerInformationData = (PassengerInformationData) travellerData.getTravellerInfo();

		if (StringUtils.isNotBlank(passengerInformationData.getSavedTravellerUId()))
		{
			travellerData.setUid(passengerInformationData.getSavedTravellerUId());
			return updateAndGetSavedTravellerDetails(travellerData, getPassengerInformationReverseConverter());
		}
		else
		{
			return saveAndGetNewTravellerDetail(travellerData);
		}
	}

	/**
	 * Method responsible for creating a new traveller instance and saving it against the current users account
	 *
	 * @param travellerData
	 *           the traveller data
	 * @deprecated Deprecated since version 2.0. Use {@link #saveAndGetNewTravellerDetail(TravellerData)} instead.
	 */
	@Deprecated
	protected void saveNewTravellerDetail(final TravellerData travellerData)
	{
		final TravellerModel travellerModel = createTravellerModel(travellerData);

		// get current user
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		final List<TravellerModel> travellers = new ArrayList<>();
		travellers.add(travellerModel);

		// add any existing traveller to the list so that we don't loose them
		if (currentUser.getSavedTraveller() != null)
		{
			travellers.addAll(currentUser.getSavedTraveller());
		}

		currentUser.setSavedTraveller(travellers);
		getModelService().save(currentUser);
	}

	/**
	 * Method responsible for creating a new traveller instance and saving it against the current users account and
	 * return the same
	 *
	 * @param travellerData
	 *           the traveller data
	 * @return travellerData traveller data
	 */
	protected TravellerData saveAndGetNewTravellerDetail(final TravellerData travellerData)
	{
		final TravellerModel travellerModel = createTravellerModel(travellerData);

		// get current user
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();

		final List<TravellerModel> travellers = new ArrayList<>();
		travellers.add(travellerModel);

		// add any existing traveller to the list so that we don't loose them
		if (currentUser.getSavedTraveller() != null)
		{
			travellers.addAll(currentUser.getSavedTraveller());
		}

		currentUser.setSavedTraveller(travellers);
		getModelService().save(currentUser);
		return getTravellerDataConverter().convert(travellerModel);
	}

	/**
	 * Method responsible for creating and returning a new instance of TravellerModel
	 *
	 * @param travellerData
	 *           the traveller data
	 * @return traveller model
	 */
	protected TravellerModel createTravellerModel(final TravellerData travellerData)
	{
		final PassengerInformationData passengerInformationData = (PassengerInformationData) travellerData.getTravellerInfo();

		final TravellerModel travellerModel = getTravellerService().createTraveller(TravellerType.PASSENGER.toString(),
				passengerInformationData.getPassengerType().getCode(), travellerData.getLabel(),
				Integer.parseInt(travellerData.getFormId()), StringUtils.EMPTY);

		final PassengerInformationModel passengerInformationModel = passengerInformationReverseConverter
				.convert(passengerInformationData);

		travellerModel.setInfo(passengerInformationModel);
		travellerModel.setBooker(true);

		if (travellerData.getSpecialRequestDetail() != null
				&& CollectionUtils.isNotEmpty(travellerData.getSpecialRequestDetail().getSpecialServiceRequests()))
		{
			travellerModel.setSpecialRequestDetail(getSpecialRequestDetail(travellerData.getSpecialRequestDetail()));
		}

		travellerModel.setSavedTravellerUid(travellerData.getSavedTravellerUid());

		getModelService().save(passengerInformationModel);
		getModelService().save(travellerModel);

		return travellerModel;
	}

	/**
	 * Method responsible for updating an existing travellers details with the data provided by travellerData
	 *
	 * @param travellerData
	 *           the traveller data
	 * @param isSavedTraveller
	 *           the is saved traveller
	 * @deprecated Deprecated since version 2.0. Use {@link #updateAndGetSavedTravellerDetails(TravellerData, Converter)}  or
	 *             {@link #updateAndGetExistingTravellerDetails(TravellerData, Converter)} instead.
	 */
	@Deprecated
	protected void updateExistingTravellerDetails(final TravellerData travellerData, final boolean isSavedTraveller)
	{
		TravellerModel travellerModel;

		if (isSavedTraveller)
		{
			travellerModel = getTravellerService().getExistingTraveller(travellerData.getUid());
		}
		else
		{
			travellerModel = getTravellerService().getTravellerFromCurrentCart(travellerData.getLabel());
		}

		travellerModel.setBooker(travellerData.isBooker());
		travellerModel.setLabel(travellerData.getLabel());

		final PassengerInformationModel passengerInformationModel = Objects.nonNull(travellerModel.getInfo())
				? (PassengerInformationModel) travellerModel.getInfo() : new PassengerInformationModel();

		final PassengerInformationData passengerInformationData = (PassengerInformationData) travellerData.getTravellerInfo();
		passengerInformationReverseConverter.convert(passengerInformationData, passengerInformationModel);

		travellerModel.setInfo(passengerInformationModel);
		travellerModel.setSpecialRequestDetail(getSpecialRequestDetail(travellerData.getSpecialRequestDetail()));

		travellerModel.setSavedTravellerUid(travellerData.getSavedTravellerUid());

		getModelService().save(passengerInformationModel);
		getModelService().save(travellerModel);
	}

	/**
	 * Method responsible for updating an existing saved traveller details with the data provided by travellerData and
	 * return the same
	 *
	 * @param travellerData
	 *           the traveller data
	 * @param passengerInformationReverseConverter
	 *           the passenger information reverse converter
	 * @return travellerData traveller data
	 */
	protected TravellerData updateAndGetSavedTravellerDetails(final TravellerData travellerData,
			final Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter)
	{
		final TravellerModel travellerModel = getTravellerService().getExistingTraveller(travellerData.getUid());
		return updateAndGetTravellerDetails(travellerData, travellerModel, passengerInformationReverseConverter);
	}

	/**
	 * Method responsible for updating an existing traveller details with the data provided by travellerData and return
	 * the same
	 *
	 * @param travellerData
	 *           the traveller data
	 * @param passengerInformationReverseConverter
	 *           the passenger information reverse converter
	 * @return travellerData traveller data
	 */
	protected TravellerData updateAndGetExistingTravellerDetails(final TravellerData travellerData,
			final Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter)
	{
		final TravellerModel travellerModel = getTravellerService().getTravellerFromCurrentCart(travellerData.getLabel());
		return updateAndGetTravellerDetails(travellerData, travellerModel, passengerInformationReverseConverter);
	}

	/**
	 * Method responsible for updating an existing travellers details with the data provided by travellerData and return
	 * the same
	 *
	 * @param travellerData
	 *           the traveller data
	 * @param travellerModel
	 *           the traveller model
	 * @param passengerInformationReverseConverter
	 *           the passenger information reverse converter
	 * @return travellerData traveller data
	 */
	protected TravellerData updateAndGetTravellerDetails(final TravellerData travellerData, final TravellerModel travellerModel,
			final Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter)
	{
		final PassengerInformationModel passengerInformationModel = Objects.nonNull(travellerModel.getInfo())
				? (PassengerInformationModel) travellerModel.getInfo() : new PassengerInformationModel();

		final PassengerInformationData passengerInformationData = (PassengerInformationData) travellerData.getTravellerInfo();
		passengerInformationReverseConverter.convert(passengerInformationData, passengerInformationModel);
		travellerModel.setInfo(passengerInformationModel);
		travellerModel.setSpecialRequestDetail(getSpecialRequestDetail(travellerData.getSpecialRequestDetail()));
		travellerModel.setSavedTravellerUid(travellerData.getSavedTravellerUid());
		travellerModel.setBooker(travellerData.isBooker());
		travellerModel.setLabel(travellerData.getLabel());

		getModelService().save(passengerInformationModel);
		getModelService().save(travellerModel);

		return getTravellerDataConverter().convert(travellerModel);
	}

	/**
	 * Method returns a list of SpecialServiceRequestModel for the given requests list of SpecialServiceRequestData in
	 * specialRequestDetail
	 *
	 * @param specialRequestDetail
	 *           the special request detail
	 * @return special request detail
	 */
	protected SpecialRequestDetailModel getSpecialRequestDetail(final SpecialRequestDetailData specialRequestDetail)
	{
		if (specialRequestDetail != null && !specialRequestDetail.getSpecialServiceRequests().isEmpty())
		{
			final List<SpecialServiceRequestModel> specialServiceRequests = new ArrayList<>();

			specialRequestDetail.getSpecialServiceRequests().forEach(specialServiceRequest -> specialServiceRequests
					.add(specialServiceRequestService.getSpecialServiceRequest(specialServiceRequest.getCode())));

			final SpecialRequestDetailModel specialRequestDetailModel = new SpecialRequestDetailModel();
			specialRequestDetailModel.setSpecialServiceRequest(specialServiceRequests);

			getModelService().save(specialRequestDetailModel);

			return specialRequestDetailModel;
		}
		return null;
	}

	@Override
	public TravellerData getCurrentUserDetails()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel != null)
		{
			return getTravellerDataConverter().convert(currentUser.getCustomerTravellerInstance());
		}
		return null;
	}

	@Override
	public boolean isAnonymousUser()
	{
		return getUserService().isAnonymousUser(getUserService().getCurrentUser());
	}

	@Override
	public TravellerData getTraveller(final String travellerId)
	{
		return getTraveller(travellerId, null);
	}

	@Override
	public TravellerData getTraveller(final String travellerId, final String bookingInfo)
	{
		try
		{
			final TravellerModel travellerModel = getTravellerService().getExistingTraveller(travellerId, bookingInfo);
			return getTravellerDataConverter().convert(travellerModel);
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error(String.format("Unable to find traveller with ID %s", travellerId), e);
			return null;
		}
	}

	@Override
	public TravellerData getTravellerFromCurrentCart(final String travellerCode)
	{
		final TravellerModel traveller = getTravellerService().getTravellerFromCurrentCart(travellerCode);

		if (traveller != null)
		{
			return getTravellerDataConverter().convert(traveller);
		}

		return null;
	}

	@Override
	public TravellerData removeSavedTraveller(final String uid)
	{
		try
		{
			final TravellerModel travellerModel = getTravellerService().getExistingTraveller(uid);
			final TravellerData travellerData = getTravellerDataConverter().convert(travellerModel);
			modelService.remove(travellerModel);
			return travellerData;
		}
		catch (final NoSuchElementException nse)
		{
			LOG.error("Unable to find TravelModel with uid " + uid, nse);
		}
		catch (final ModelRemovalException mre)
		{
			LOG.error("Unable to remove TravelModel with uid " + uid, mre);
		}
		return null;
	}

	@Override
	public List<TravellerData> retrieveTravellers(final ReservationData reservationData, final int originDestinationRefNumber)
	{
		final List<ReservationItemData> reservationItems = reservationData.getReservationItems();

		final Optional<ReservationItemData> reservationItemDataOptional = reservationItems.stream()
				.filter(reservationItemData -> reservationItemData.getOriginDestinationRefNumber() == originDestinationRefNumber)
				.findFirst();

		if (!reservationItemDataOptional.isPresent())
		{
			return Collections.emptyList();
		}

		return reservationItemDataOptional.get().getReservationItinerary().getTravellers();
	}

	@Override
	public void updateCurrentUserSpecialRequestDetails(final List<String> specialServiceRequestCodes)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel == null)
		{
			travellerModel = createInitialTraveller();
			currentUser.setCustomerTravellerInstance(travellerModel);
		}

		if (!specialServiceRequestCodes.isEmpty())
		{
			final List<SpecialServiceRequestModel> specialServiceRequests = new ArrayList<>();

			specialServiceRequestCodes.forEach(specialServiceRequestCode -> specialServiceRequests
					.add(getSpecialServiceRequestService().getSpecialServiceRequest(specialServiceRequestCode)));

			final SpecialRequestDetailModel specialRequestDetailModel = new SpecialRequestDetailModel();
			specialRequestDetailModel.setSpecialServiceRequest(specialServiceRequests);

			getModelService().save(specialRequestDetailModel);

			travellerModel.setSpecialRequestDetail(specialRequestDetailModel);
		}
		else
		{
			if (travellerModel.getSpecialRequestDetail() != null)
			{
				getModelService().remove(travellerModel.getSpecialRequestDetail());
			}
		}

		getModelService().save(travellerModel);
		getModelService().save(currentUser);
	}

	@Override
	public List<TravellerPreferenceData> getTravellerPreferences()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel != null && travellerModel.getTravellerPreference() != null
				&& !travellerModel.getTravellerPreference().isEmpty())
		{
			return Converters.convertAll(travellerModel.getTravellerPreference(), getTravellerPreferenceConverter());
		}
		return Collections.emptyList();
	}

	@Override
	public void getSaveTravellerPreferences(final List<TravellerPreferenceData> selectedTravellerPreferences)
	{
		if (CollectionUtils.isNotEmpty(selectedTravellerPreferences))
		{
			final List<TravellerPreferenceModel> travellerPreferenceModels = new ArrayList<>();
			travellerPreferenceModels
					.addAll(Converters.convertAll(selectedTravellerPreferences, getTravellerPreferenceReverseConverter()));

			getModelService().saveAll(travellerPreferenceModels);

			savePreferencesToCustomer(travellerPreferenceModels);
		}
		else
		{
			savePreferencesToCustomer(null);
		}
	}

	/**
	 * Method which saves selected preferences to traveller instance of current customer
	 *
	 * @param travellerPreferenceModels
	 *           the traveller preference models
	 */
	protected void savePreferencesToCustomer(final List<TravellerPreferenceModel> travellerPreferenceModels)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel == null)
		{
			travellerModel = createInitialTraveller();
		}

		travellerModel.setTravellerPreference(travellerPreferenceModels);
		currentUser.setCustomerTravellerInstance(travellerModel);

		getModelService().save(travellerModel);
		getModelService().save(currentUser);
	}

	@Override
	public void updatePassengerInformation(final TravellerData travellerData)
	{
		updateCurrentUserCustomerPassengerInstance(travellerData, getPassengerInformationReverseConverter());
	}

	@Override
	public PassengerInformationData getPassengerInformation()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();

		if (travellerModel != null)
		{
			final PassengerInformationModel advancePassengerInfoModel = (PassengerInformationModel) travellerModel.getInfo();
			if (advancePassengerInfoModel != null)
			{
				return passengerInformationDataConverter.convert(advancePassengerInfoModel);
			}
		}
		return null;
	}

	@Override
	public TravellerData getCustomerTravellerInstanceData()
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final TravellerModel travellerModel = currentUser.getCustomerTravellerInstance();
		if (null != travellerModel)
		{
			return getTravellerDataConverter().convert(travellerModel);
		}
		return null;
	}

	@Override
	public Map<String, Map<String, String>> populateTravellersNamesMap(final List<TravellerData> travellerDatas)
	{
		final Map<String, Map<String, String>> travellerPassengerTypeMap = new HashMap<>();

		for (final PassengerTypeData passengerTypeData : getPassengerTypeFacade().getPassengerTypes())
		{
			final Map<String, String> travellerNameMap = new HashMap<>();

			int counter = 1;
			for (final TravellerData travellerData : travellerDatas)
			{
				if (!passengerTypeData.getCode()
						.equalsIgnoreCase(((PassengerInformationData) travellerData.getTravellerInfo()).getPassengerType().getCode()))
				{
					continue;
				}
				if (!travellerPassengerTypeMap.containsKey(passengerTypeData.getCode()))
				{
					travellerPassengerTypeMap.put(passengerTypeData.getCode(), travellerNameMap);
				}
				final PassengerInformationData passengerInfoData = ((PassengerInformationData) travellerData.getTravellerInfo());
				if (org.apache.solr.common.StringUtils.isEmpty(passengerInfoData.getFirstName())
						&& org.apache.solr.common.StringUtils.isEmpty(passengerInfoData.getSurname()))
				{
					travellerNameMap.put(travellerData.getLabel(), passengerInfoData.getPassengerType().getName() + " " + counter++);
				}
				else
				{
					travellerNameMap.put(travellerData.getLabel(),
							passengerInfoData.getFirstName() + " " + passengerInfoData.getSurname());
				}

			}
		}
		return travellerPassengerTypeMap;
	}

	@Override
	public List<TravellerData> findSavedTravellersUsingFirstName(final String text, final String passengerType)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		return Converters.convertAll(getTravellerService().findSavedTravellersUsingFirstNameText(text, passengerType, currentUser),
				travellerDataConverter);
	}

	@Override
	public List<TravellerData> findSavedTravellersUsingSurname(final String text, final String passengerType)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		return Converters.convertAll(getTravellerService().findSavedTravellersUsingLastNameText(text, passengerType, currentUser),
				travellerDataConverter);
	}

	/**
	 * Method responsible for creating an initial traveller instance with default settings
	 *
	 * @return traveller model
	 */
	protected TravellerModel createInitialTraveller()
	{
		return getTravellerService().createTraveller(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER, "adult", "adult1", 0,
				StringUtils.EMPTY);
	}

	/**
	 * Gets traveller data converter.
	 *
	 * @return the travellerDataConverter
	 */
	protected Converter<TravellerModel, TravellerData> getTravellerDataConverter()
	{
		return travellerDataConverter;
	}

	/**
	 * Sets traveller data converter.
	 *
	 * @param travellerDataConverter
	 *           the travellerDataConverter to set
	 */
	public void setTravellerDataConverter(final Converter<TravellerModel, TravellerData> travellerDataConverter)
	{
		this.travellerDataConverter = travellerDataConverter;
	}

	/**
	 * Gets traveller service.
	 *
	 * @return the travellerService
	 */
	protected TravellerService getTravellerService()
	{
		return travellerService;
	}

	/**
	 * Sets traveller service.
	 *
	 * @param travellerService
	 *           the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * Gets enumeration service.
	 *
	 * @return the enumeration service
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * Sets enumeration service.
	 *
	 * @param enumerationService
	 *           the enumeration service
	 */
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
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
	 *           the user service
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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
	 *           the model service
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Gets passenger type service.
	 *
	 * @return the passenger type service
	 */
	protected PassengerTypeService getPassengerTypeService()
	{
		return passengerTypeService;
	}

	/**
	 * Sets passenger type service.
	 *
	 * @param passengerTypeService
	 *           the passenger type service
	 */
	public void setPassengerTypeService(final PassengerTypeService passengerTypeService)
	{
		this.passengerTypeService = passengerTypeService;
	}

	/**
	 * Gets special service request service.
	 *
	 * @return the special service request service
	 */
	protected SpecialServiceRequestService getSpecialServiceRequestService()
	{
		return specialServiceRequestService;
	}

	/**
	 * Sets special service request service.
	 *
	 * @param specialServiceRequestService
	 *           the special service request service
	 */
	public void setSpecialServiceRequestService(final SpecialServiceRequestService specialServiceRequestService)
	{
		this.specialServiceRequestService = specialServiceRequestService;
	}

	/**
	 * Gets passenger information reverse converter.
	 *
	 * @return the passenger information reverse converter
	 */
	protected Converter<PassengerInformationData, PassengerInformationModel> getPassengerInformationReverseConverter()
	{
		return passengerInformationReverseConverter;
	}

	/**
	 * Sets passenger information reverse converter.
	 *
	 * @param passengerInformationReverseConverter
	 *           the passenger information reverse converter
	 */
	public void setPassengerInformationReverseConverter(
			final Converter<PassengerInformationData, PassengerInformationModel> passengerInformationReverseConverter)
	{
		this.passengerInformationReverseConverter = passengerInformationReverseConverter;
	}

	/**
	 * Gets passenger details reverse converter.
	 *
	 * @return the passengerDetailsReverseConverter
	 */
	protected Converter<PassengerInformationData, PassengerInformationModel> getPassengerDetailsReverseConverter()
	{
		return passengerDetailsReverseConverter;
	}

	/**
	 * Sets passenger details reverse converter.
	 *
	 * @param passengerDetailsReverseConverter
	 *           the passengerDetailsReverseConverter to set
	 */
	@Required
	public void setPassengerDetailsReverseConverter(
			final Converter<PassengerInformationData, PassengerInformationModel> passengerDetailsReverseConverter)
	{
		this.passengerDetailsReverseConverter = passengerDetailsReverseConverter;
	}

	/**
	 * Gets traveller preference converter.
	 *
	 * @return the traveller preference converter
	 */
	public Converter<TravellerPreferenceModel, TravellerPreferenceData> getTravellerPreferenceConverter()
	{
		return travellerPreferenceConverter;
	}

	/**
	 * Sets traveller preference converter.
	 *
	 * @param travellerPreferenceConverter
	 *           the traveller preference converter
	 */
	public void setTravellerPreferenceConverter(
			final Converter<TravellerPreferenceModel, TravellerPreferenceData> travellerPreferenceConverter)
	{
		this.travellerPreferenceConverter = travellerPreferenceConverter;
	}

	/**
	 * Gets traveller preference reverse converter.
	 *
	 * @return the traveller preference reverse converter
	 */
	protected Converter<TravellerPreferenceData, TravellerPreferenceModel> getTravellerPreferenceReverseConverter()
	{
		return travellerPreferenceReverseConverter;
	}

	/**
	 * Sets traveller preference reverse converter.
	 *
	 * @param travellerPreferenceReverseConverter
	 *           the traveller preference reverse converter
	 */
	public void setTravellerPreferenceReverseConverter(
			final Converter<TravellerPreferenceData, TravellerPreferenceModel> travellerPreferenceReverseConverter)
	{
		this.travellerPreferenceReverseConverter = travellerPreferenceReverseConverter;
	}

	/**
	 * Gets passenger information data converter.
	 *
	 * @return the passenger information data converter
	 */
	protected Converter<PassengerInformationModel, PassengerInformationData> getPassengerInformationDataConverter()
	{
		return passengerInformationDataConverter;
	}

	/**
	 * Sets passenger information data converter.
	 *
	 * @param passengerInformationDataConverter
	 *           the passenger information data converter
	 */
	public void setPassengerInformationDataConverter(
			final Converter<PassengerInformationModel, PassengerInformationData> passengerInformationDataConverter)
	{
		this.passengerInformationDataConverter = passengerInformationDataConverter;
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
	 *           the cart service
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * Gets passenger type facade.
	 *
	 * @return the passenger type facade
	 */
	protected PassengerTypeFacade getPassengerTypeFacade()
	{
		return passengerTypeFacade;
	}

	/**
	 * Sets passenger type facade.
	 *
	 * @param passengerTypeFacade
	 *           the passenger type facade
	 */
	public void setPassengerTypeFacade(final PassengerTypeFacade passengerTypeFacade)
	{
		this.passengerTypeFacade = passengerTypeFacade;
	}

}
