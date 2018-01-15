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

package de.hybris.platform.traveladdon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.travel.BaggageInfoData;
import de.hybris.platform.commercefacades.travel.BookingInfoData;
import de.hybris.platform.commercefacades.travel.CheckInRequestData;
import de.hybris.platform.commercefacades.travel.CheckInResponseData;
import de.hybris.platform.commercefacades.travel.OriginDestinationOptionData;
import de.hybris.platform.commercefacades.travel.PassengerInformationData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.TravellerTransportOfferingInfoData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationData;
import de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SelectedSeatData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.traveladdon.checkin.steps.CheckinStep;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.pages.steps.checkin.AbstractCheckinStepController;
import de.hybris.platform.traveladdon.forms.APIForm;
import de.hybris.platform.traveladdon.forms.CheckInForm;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.CheckInFacade;
import de.hybris.platform.travelfacades.facades.TravelI18NFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.DocumentType;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(
{ "/manage-booking/check-in" })
public class CheckInPageController extends AbstractCheckinStepController
{
	private static final String RESERVATION_DATA = "reservationData";
	private static final String CHECKIN_DETAILS_PAGE = "checkInDetailsPage";
	private static final String CHECKIN_SUCCESS_CMS_PAGE = "checkInSuccessPage";
	private static final String CHECKIN_FAILED_CMS_PAGE = "checkInFailedPage";
	private static final String CHECKIN_FORM_VALIDATION_ERROR = "checkin.form.validation.error";
	private static final String CHECK_IN = "check-in";
	private static final String TEXT_CHECKIN_NOT_POSSIBLE = "text.checkin.not.possible";
	private static final String CHECK_IN_RESPONSE = "checkInResponse";
	private static final String CHECK_IN_FORM = "checkInForm";

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "checkInFacade")
	private CheckInFacade checkInFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "travelI18NFacade")
	private TravelI18NFacade travelI18NFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "checkInValidator")
	private Validator checkInValidator;

	@Resource
	private SessionService sessionService;

	@Resource(name = "userService")
	private UserService userService;

	@RequestMapping(value = "/{bookingReference}/{originDestinationRefNumber}", method = RequestMethod.GET)
	@RequireHardLogIn
	public String getCheckInPage(final Model model, @PathVariable final String bookingReference,
			@RequestParam(value = "travellerReference", required = false) final String travellerReference,
			@PathVariable final int originDestinationRefNumber, final RedirectAttributes redirectAttributes)
			throws CMSItemNotFoundException
	{
		if (!bookingFacade.validateUserForBooking(bookingReference))
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					TraveladdonWebConstants.BOOKING_DETAILS_USER_VALIDATION_ERROR);
			return REDIRECT_PREFIX + "/";
		}

		final ReservationData reservationData = bookingFacade.getBookingByBookingReference(bookingReference);

		if (!checkInFacade.isCheckInPossible(reservationData, originDestinationRefNumber))
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER, TEXT_CHECKIN_NOT_POSSIBLE);
			return REDIRECT_PREFIX + "/";
		}

		if (!model.containsAttribute(CHECK_IN_FORM))
		{
			final CheckInForm checkInForm = initialiseCheckInForm(reservationData, travellerReference, originDestinationRefNumber);
			if (CollectionUtils.isEmpty(checkInForm.getApiFormList()))
			{
				GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER, TEXT_CHECKIN_NOT_POSSIBLE);
				return REDIRECT_PREFIX + "/";
			}
			model.addAttribute(CHECK_IN_FORM, checkInForm);
		}
		model.addAttribute(TraveladdonWebConstants.BOOKING_REFERENCE, bookingReference);
		model.addAttribute(TraveladdonWebConstants.ORIGIN_DESTINATION_REF_NUMBER, originDestinationRefNumber);
		model.addAttribute(TraveladdonWebConstants.TRAVELLERS,
				travellerFacade.retrieveTravellers(reservationData, originDestinationRefNumber));
		model.addAttribute(TraveladdonWebConstants.NATIONALITIES, travelI18NFacade.getAllCountries());
		model.addAttribute(TraveladdonWebConstants.COUNTRIES, travelI18NFacade.getAllCountries());
		model.addAttribute(RESERVATION_DATA, reservationData);

		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKIN_DETAILS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKIN_DETAILS_PAGE));

		return getViewForPage(model);
	}

	@RequestMapping(value = "/submit/{bookingReference}/{originDestinationRefNumber}", method = RequestMethod.POST)
	@RequireHardLogIn
	public String doCheckin(@Valid final CheckInForm checkInForm, final BindingResult bindingResult,
			@PathVariable final String bookingReference, @PathVariable final int originDestinationRefNumber,
			final RedirectAttributes redirectAttributes)
	{
		checkInValidator.validate(checkInForm, bindingResult);

		if (bindingResult.hasErrors())
		{
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.checkInForm", bindingResult);
			redirectAttributes.addFlashAttribute(CHECK_IN_FORM, checkInForm);
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, CHECKIN_FORM_VALIDATION_ERROR);
			return REDIRECT_PREFIX + TraveladdonWebConstants.CHECK_IN_URL + "/" + bookingReference + "/"
					+ originDestinationRefNumber;
		}

		final ReservationData reservationData = bookingFacade.getBookingByBookingReference(bookingReference);

		if (!checkInFacade.isCheckInPossible(reservationData, originDestinationRefNumber))
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER, TEXT_CHECKIN_NOT_POSSIBLE);
			return REDIRECT_PREFIX + "/";
		}

		final CheckInRequestData checkInRequest = prepareCheckInRequestFromForm(reservationData, checkInForm,
				originDestinationRefNumber);
		updateTravellerPassengerInformationData(checkInRequest);

		final CheckInResponseData checkInResponse = checkInFacade.doCheckin(checkInRequest);
		checkInResponse.setBookingReference(bookingReference);
		if (MapUtils.isNotEmpty(checkInResponse.getErrors()))
		{
			// ERRORS!!
			redirectAttributes.addFlashAttribute(CHECK_IN_RESPONSE, checkInResponse);
			return REDIRECT_PREFIX + TraveladdonWebConstants.CHECK_IN_FAILED_URL;
		}
		redirectAttributes.addFlashAttribute(CHECK_IN_RESPONSE, checkInResponse);
		sessionService.setAttribute(TraveladdonWebConstants.SESSION_BOOKING_REFERENCE, bookingReference);
		sessionService.setAttribute(TraveladdonWebConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER, originDestinationRefNumber);
		final List<String> travellersToCheckIn = checkInRequest.getTravellerTransportOfferingInfos().stream()
				.map(info -> info.getTraveller().getUid()).distinct().collect(Collectors.toList());
		sessionService.setAttribute(TraveladdonWebConstants.SESSION_TRAVELLERS_TO_CHECK_IN, travellersToCheckIn);

		return getCheckinStep().nextStep();
	}

	@RequestMapping(value = "/success", method = RequestMethod.GET)
	public String getCheckInSuccessPage(final Model model) throws CMSItemNotFoundException
	{

		if (sessionService.getAttribute(TraveladdonWebConstants.SESSION_BOOKING_REFERENCE) == null
				|| sessionService.getAttribute(TraveladdonWebConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER) == null
				|| sessionService.getAttribute(TraveladdonWebConstants.SESSION_TRAVELLERS_TO_CHECK_IN) == null)
		{
			return REDIRECT_PREFIX + "/";
		}

		final String bookingReference = sessionService.getAttribute(TraveladdonWebConstants.SESSION_BOOKING_REFERENCE);
		final int originDestinationRefNumber = sessionService
				.getAttribute(TraveladdonWebConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER);
		final List<String> travellersToCheckIn = sessionService
				.getAttribute(TraveladdonWebConstants.SESSION_TRAVELLERS_TO_CHECK_IN);

		if (travellersToCheckIn.isEmpty())
		{
			return REDIRECT_PREFIX + "/";
		}
		checkInFacade.startCheckInProcess(bookingReference, originDestinationRefNumber, travellersToCheckIn);
		getSessionService().removeAttribute(TravelservicesConstants.SESSION_ORIGIN_DESTINATION_REF_NUMBER);
		getSessionService().removeAttribute(TravelservicesConstants.SESSION_TRAVELLERS_TO_CHECK_IN);

		model.addAttribute(TraveladdonWebConstants.BOOKING_REFERENCE, bookingReference);

		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKIN_SUCCESS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKIN_SUCCESS_CMS_PAGE));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getViewForPage(model);
	}

	@RequestMapping(value = "/failed", method = RequestMethod.GET)
	public String getCheckInStatusPage(final Model model) throws CMSItemNotFoundException
	{
		if (!model.asMap().containsKey(CHECK_IN_RESPONSE))
		{
			return REDIRECT_PREFIX + "/";
		}

		storeCmsPageInModel(model, getContentPageForLabelOrId(CHECKIN_FAILED_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKIN_FAILED_CMS_PAGE));

		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		return getViewForPage(model);
	}

	/**
	 *
	 * This method will create a new CheckInRequestData with the information gathered from the given
	 * {@link ReservationData} and the AdvancePassengerInfo provided in the given CheckInForm
	 *
	 * @param reservationData
	 * @param checkInForm
	 * @return the CheckInRequestData populated
	 *
	 */
	protected CheckInRequestData prepareCheckInRequestFromForm(final ReservationData reservationData,
			final CheckInForm checkInForm, final int originDestinationRefNumber)
	{

		final CheckInRequestData checkInRequest = new CheckInRequestData();
		checkInRequest.setTravellerTransportOfferingInfos(new ArrayList<>());

		final Optional<ReservationItemData> optionalReservationItem = reservationData.getReservationItems().stream()
				.filter(rid -> rid.getOriginDestinationRefNumber() == originDestinationRefNumber).findAny();

		if (!optionalReservationItem.isPresent())
		{
			return null;
		}

		final ReservationItemData reservationItem = optionalReservationItem.get();

		for (final OriginDestinationOptionData odOption : reservationItem.getReservationItinerary().getOriginDestinationOptions())
		{
			for (final TransportOfferingData toData : odOption.getTransportOfferings())
			{
				// For each Transport Offering in the current itinerary
				for (final APIForm apiForm : checkInForm.getApiFormList())
				{
					final TravellerTransportOfferingInfoData travellerTOInfoData = new TravellerTransportOfferingInfoData();

					final Optional<TravellerData> optionalTraveller = reservationItem.getReservationItinerary().getTravellers()
							.stream().filter(tr -> StringUtils.equalsIgnoreCase(tr.getUid(), apiForm.getUid())).findAny();

					if (!optionalTraveller.isPresent())
					{
						continue;
					}

					final BookingInfoData bookingInfoData = new BookingInfoData();
					bookingInfoData.setBookingReference(reservationData.getCode());
					bookingInfoData.setOriginDestinationRefNumberList(Collections.singletonList(originDestinationRefNumber));
					travellerTOInfoData.setBookingInfo(bookingInfoData);

					final TravellerData traveller = optionalTraveller.get();
					travellerTOInfoData.setTraveller(traveller);
					updateAPIDataFromForm(apiForm, (PassengerInformationData) traveller.getTravellerInfo());
					travellerTOInfoData.setTransportOffering(toData);
					if (traveller.getSpecialRequestDetail() != null)
					{
						travellerTOInfoData.setSpecialServiceRequests(traveller.getSpecialRequestDetail().getSpecialServiceRequests());
					}
					travellerTOInfoData.setSeatBoardingInfo(new SelectedSeatData());

					checkInRequest.getTravellerTransportOfferingInfos().add(travellerTOInfoData);
				}
			}
		}

		checkInRequest.setBaggageInfo(new BaggageInfoData());
		checkInRequest.setMessageFunction(StringUtils.EMPTY);
		checkInRequest.setPaymentInfo(new CCPaymentInfoData());

		return checkInRequest;
	}

	/**
	 * This methdo initializes a CheckIn. If the travellerReference is empty the form will be initialized for all the
	 * travellers in the given ReservationData, otherwise for the specified traveller identified by the
	 * travellerReference
	 *
	 * @param reservationData
	 *           the ReservationData
	 * @param travellerReference
	 *           the travellerReference
	 * @param originDestinationRefNumber
	 *           the identifier of the leg for which we're checking in a/all traveller(s)
	 *
	 * @return CheckInForm
	 */
	protected CheckInForm initialiseCheckInForm(final ReservationData reservationData, final String travellerReference,
			final int originDestinationRefNumber)
	{

		final List<TravellerData> travellersInLeg = travellerFacade.retrieveTravellers(reservationData, originDestinationRefNumber);

		final CheckInForm checkInForm = new CheckInForm();
		checkInForm.setApiFormList(new ArrayList<>());

		final List<String> transportOfferingCodes = getTransportOfferingCodeList(reservationData, originDestinationRefNumber);

		if (StringUtils.isBlank(travellerReference))
		{
			travellersInLeg.stream().filter(travellerData -> checkInFacade.checkTravellerEligibility(travellerData.getLabel(),
					transportOfferingCodes, reservationData.getCode())).forEach(travellerData -> {
						fillCheckInFormWithAPIDetails(checkInForm, travellerData, reservationData);
					});
		}
		else
		{
			travellersInLeg.stream().filter(travellerData -> travellerData.getUid().equals(travellerReference))
					.filter(travellerData -> checkInFacade.checkTravellerEligibility(travellerData.getLabel(), transportOfferingCodes,
							reservationData.getCode()))
					.findFirst()
					.ifPresent(travellerToCheckIn -> fillCheckInFormWithAPIDetails(checkInForm, travellerToCheckIn, reservationData));
		}

		return checkInForm;
	}


	/**
	 * This method fills the checkin form with traveller's API details
	 *
	 * @param checkInForm
	 * @param travellerToCheckIn
	 * @param reservationData
	 */
	protected void fillCheckInFormWithAPIDetails(final CheckInForm checkInForm, final TravellerData travellerToCheckIn,
			final ReservationData reservationData)
	{
		final APIForm apiForm = new APIForm();
		apiForm.setUid(travellerToCheckIn.getUid());
		retrieveSavedTravellerIfExists(travellerToCheckIn, reservationData)
				.ifPresent(savedTraveller -> setAPIDetails(apiForm, savedTraveller));
		checkInForm.getApiFormList().add(apiForm);
	}

	/**
	 * Populate APIForm data with API details of the saved passenger if exists.
	 *
	 * @param apiForm
	 *           APIForm object.
	 * @param savedTravellerData
	 *           TravellerData object.
	 */
	public void setAPIDetails(final APIForm apiForm, final TravellerData savedTravellerData)
	{
		final PassengerInformationData passengerInformationData = (PassengerInformationData) savedTravellerData.getTravellerInfo();
		if (passengerInformationData != null)
		{
			apiForm.setAPIType(
					(passengerInformationData.getAPIType() == null) ? StringUtils.EMPTY : passengerInformationData.getAPIType());
			apiForm.setCountryOfIssue((passengerInformationData.getCountryOfIssue() == null) ? StringUtils.EMPTY
					: passengerInformationData.getCountryOfIssue().getIsocode());
			apiForm.setDateOfBirth((passengerInformationData.getDateOfBirth() == null) ? StringUtils.EMPTY
					: TravelDateUtils.convertDateToStringDate(passengerInformationData.getDateOfBirth(),
							TravelservicesConstants.DATE_PATTERN));
			apiForm.setDocumentExpiryDate((passengerInformationData.getDocumentExpiryDate() == null) ? StringUtils.EMPTY
					: TravelDateUtils.convertDateToStringDate(passengerInformationData.getDocumentExpiryDate(),
							TravelservicesConstants.DATE_PATTERN));
			apiForm.setDocumentNumber((passengerInformationData.getDocumentNumber() == null) ? StringUtils.EMPTY
					: passengerInformationData.getDocumentNumber());
			apiForm.setDocumentType((passengerInformationData.getDocumentType() == null) ? StringUtils.EMPTY
					: passengerInformationData.getDocumentType().toString());
			apiForm.setNationality((passengerInformationData.getNationality() == null) ? StringUtils.EMPTY
					: passengerInformationData.getNationality().getIsocode());
		}
	}

	/**
	 * Copy traveller's passengerInformationData from one TravellerData to another
	 *
	 * @param travellerDataFrom
	 *           TravellerData object.
	 * @param travellerDataTo
	 *           TravellerData object.
	 */
	protected void copyTravellerPassengerInformationData(final TravellerData travellerDataFrom,
			final TravellerData travellerDataTo)
	{
		final PassengerInformationData passengerInformationDataFrom = (PassengerInformationData) travellerDataFrom
				.getTravellerInfo();
		final PassengerInformationData passengerInformationDataTo = (PassengerInformationData) travellerDataTo.getTravellerInfo();
		if (passengerInformationDataFrom != null)
		{
			passengerInformationDataTo.setAPIType(passengerInformationDataFrom.getAPIType());
			passengerInformationDataTo.setCountryOfIssue(passengerInformationDataFrom.getCountryOfIssue());
			passengerInformationDataTo.setDateOfBirth(passengerInformationDataFrom.getDateOfBirth());
			passengerInformationDataTo.setDocumentExpiryDate(passengerInformationDataFrom.getDocumentExpiryDate());
			passengerInformationDataTo.setDocumentNumber(passengerInformationDataFrom.getDocumentNumber());
			passengerInformationDataTo.setDocumentType(passengerInformationDataFrom.getDocumentType());
			passengerInformationDataTo.setNationality(passengerInformationDataFrom.getNationality());
		}
	}


	/**
	 * Update traveller's passengerInformationData along with the passengerInformation of linked traveller
	 *
	 * @param checkInRequest
	 *           CheckInRequestData object
	 */
	protected void updateTravellerPassengerInformationData(final CheckInRequestData checkInRequest)
	{
		final List<TravellerData> travellerData = checkInRequest.getTravellerTransportOfferingInfos().stream()
				.map(TravellerTransportOfferingInfoData::getTraveller).distinct().collect(Collectors.toList());
		final List<TravellerData> savedTravellerData = new ArrayList<>(travellerData.size());
		travellerData.stream().filter(td -> StringUtils.isNotEmpty(td.getSavedTravellerUid())).forEach(td -> {
			final TravellerData savedTraveller = travellerFacade.getTraveller(td.getSavedTravellerUid());
			if (Objects.nonNull(savedTraveller))
			{
				copyTravellerPassengerInformationData(td, savedTraveller);
				savedTravellerData.add(savedTraveller);
			}
		});
		travellerData.addAll(savedTravellerData);

		travellerFacade.updateCustomerSavedTravellers(travellerData);
	}

	/**
	 * Returns TravellerData object based on the traveller status( i.e booker or traveller). if current user is booker
	 * for current booking then it checks if the traveller is a booker, returns the travellerData of
	 * CustomerTravellerInstanceData, else, checks if the traveller is associated with a saved traveller for the logged
	 * in customer and returns it, else returns empty.
	 *
	 * @param travellerData
	 *           TravellerData of the traveller attempting to check-in
	 * @param reservationData
	 *           the ReservationData
	 * @return Optional<TravellerData>
	 */
	protected Optional<TravellerData> retrieveSavedTravellerIfExists(final TravellerData travellerData,
			final ReservationData reservationData)
	{
		if (!userFacade.isAnonymousUser() && bookingFacade.isUserOrderOwner(reservationData.getCode()))
		{
			if (travellerData.isBooker())
			{
				return Optional.ofNullable(travellerFacade.getCustomerTravellerInstanceData());
			}

			if (Optional.ofNullable(travellerData.getSavedTravellerUid()).isPresent())
			{
				return Optional.ofNullable(travellerFacade.getTraveller(travellerData.getSavedTravellerUid()));
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns the list of transportOffering codes of the reservationData for the specific originDestinationRefNumber.
	 *
	 * @param reservationData
	 *           as the ReservationData
	 * @param originDestinationRefNumber
	 *           as the int representing the originDestinationRefNumber
	 * @return a List<String> of the transportOffering codes
	 */
	protected List<String> getTransportOfferingCodeList(final ReservationData reservationData,
			final int originDestinationRefNumber)
	{
		final List<OriginDestinationOptionData> odOptions = reservationData.getReservationItems().stream()
				.filter(reservationItem -> reservationItem.getOriginDestinationRefNumber() == originDestinationRefNumber)
				.flatMap(reservationItem -> reservationItem.getReservationItinerary().getOriginDestinationOptions().stream())
				.collect(Collectors.toList());

		return odOptions.stream().flatMap(odOption -> odOption.getTransportOfferings().stream()).map(TransportOfferingData::getCode)
				.collect(Collectors.toList());
	}

	/**
	 * This method creates a new AdvancePassengerInfoData that will be populated with all the information coming from the
	 * given ApiForm. The AdvancePassengerInfoData will be then linked to the given PassengerInformationData.
	 *
	 * @param apiForm
	 *           the ApiForm to get the API Details provided
	 * @param passengerInformationData
	 *           the PassengerInformationData to be updated with API Details
	 */
	protected void updateAPIDataFromForm(final APIForm apiForm, final PassengerInformationData passengerInformationData)
	{
		passengerInformationData.setDocumentType(DocumentType.valueOf(apiForm.getDocumentType()));
		passengerInformationData.setDocumentNumber(apiForm.getDocumentNumber());

		final CountryData countryOfIssue = new CountryData();
		countryOfIssue.setIsocode(apiForm.getCountryOfIssue());
		passengerInformationData.setCountryOfIssue(countryOfIssue);

		final CountryData nationality = new CountryData();
		nationality.setIsocode(apiForm.getNationality());
		passengerInformationData.setNationality(nationality);

		passengerInformationData.setDateOfBirth(
				TravelDateUtils.convertStringDateToDate(apiForm.getDateOfBirth(), TravelservicesConstants.DATE_PATTERN));
		passengerInformationData.setDocumentExpiryDate(
				TravelDateUtils.convertStringDateToDate(apiForm.getDocumentExpiryDate(), TravelservicesConstants.DATE_PATTERN));
		passengerInformationData.setAPIType(apiForm.getAPIType());
	}

	protected CheckinStep getCheckinStep()
	{
		return getCheckinStep(CHECK_IN);
	}
}
