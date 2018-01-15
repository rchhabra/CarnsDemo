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

package de.hybris.platform.travelcommons.controllers.page;


import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.forms.cms.AccommodationAvailabilityForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationAvailabilityValidator;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityRequestData;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AccommodationSearchRequestData;
import de.hybris.platform.commercefacades.accommodation.PropertyData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.search.CriterionData;
import de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData;
import de.hybris.platform.commercefacades.packages.request.AccommodationPackageRequestData;
import de.hybris.platform.commercefacades.packages.request.PackageRequestData;
import de.hybris.platform.commercefacades.packages.request.TransportPackageRequestData;
import de.hybris.platform.commercefacades.packages.response.PackageResponseData;
import de.hybris.platform.commercefacades.product.data.ReviewData;
import de.hybris.platform.commercefacades.travel.FareSearchRequestData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.forms.cms.FareFinderForm;
import de.hybris.platform.traveladdon.validators.FareFinderValidator;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class PackageDetailsPageController extends AbstractPackagePageController
{
	private static final String PACKAGE_DETAILS_CMS_PAGE = "packageDetailsPage";
	private static final String AMEND_CART_ERROR = "accommodation.booking.details.page.request.cart.error";


	@Resource(name = "accommodationAvailabilityValidator")
	private AccommodationAvailabilityValidator accommodationAvailabilityValidator;

	@Resource(name = "fareFinderValidator")
	private FareFinderValidator fareFinderValidator;

	@Resource(name = "packageFacade")
	private PackageFacade packageFacade;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@RequestMapping(value = "/package-details/{accommodationOfferingCode}", method = RequestMethod.GET)
	public String getPackageDetailsPage(@PathVariable("accommodationOfferingCode") final String accommodationOfferingCode,
			@Valid @ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM) final AccommodationAvailabilityForm accommodationAvailabilityForm,
			final BindingResult accommodationBindingResult,
			@ModelAttribute(TraveladdonWebConstants.FARE_FINDER_FORM) final FareFinderForm fareFinderForm,
			final BindingResult fareBindingResult, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		disableCachingForResponse(response);

		//TODO further evaluation needed during amendments
		setSessionJourney(model, TravelfacadesConstants.BOOKING_PACKAGE);

		accommodationAvailabilityForm.setRoomStayCandidates(createRoomStayCandidates(request));

		accommodationAvailabilityValidator.validate(accommodationAvailabilityForm, accommodationBindingResult);

		if (accommodationBindingResult.hasErrors())
		{
			redirectModel.addFlashAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM_BINDING_RESULT,
					accommodationBindingResult);
			return REDIRECT_PREFIX + "/";
		}

		sessionService.setAttribute(TravelservicesConstants.SEARCH_SEED, fareSearchHashResolver.generateSeed());

		initializeFareFinderForm(fareFinderForm, accommodationAvailabilityForm);

		fareFinderValidator.validate(fareFinderForm, fareBindingResult);

		if (fareBindingResult.hasErrors())
		{
			redirectModel.addFlashAttribute(TraveladdonWebConstants.FARE_FINDER_FORM_BINDING_RESULT, fareBindingResult);
			return REDIRECT_PREFIX + "/";
		}
		if (getTravelCartFacade().isAmendmentCart())
		{
			getTravelCartFacade().deleteCurrentCart();
		}

		packageFacade.cleanUpCartBeforeAddition(fareFinderForm.getDepartureLocation(), fareFinderForm.getArrivalLocation(),
				fareFinderForm.getDepartingDateTime(), fareFinderForm.getReturnDateTime(), accommodationOfferingCode,
				accommodationAvailabilityForm.getCheckInDateTime(), accommodationAvailabilityForm.getCheckOutDateTime(),
				accommodationAvailabilityForm.getRoomStayCandidates());
		final PackageRequestData packageRequestData = new PackageRequestData();
		populateAccommodationPackageRequestData(packageRequestData, accommodationAvailabilityForm, accommodationOfferingCode,
				Boolean.FALSE, request);
		populateTransportPackageRequestData(packageRequestData, fareFinderForm, request);
		populateCustomerReviews(model, accommodationOfferingCode);

		try
		{
			// We need to set this is the session for reservation component where image will be defined, it will be then removed
			// from session in itinerary totals component controller
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE,
					accommodationOfferingCode);
			final PackageResponseData packageResponseData = packageFacade.getPackageResponse(packageRequestData);

			// sort fareSelectionData by displayOrder
			final FareSearchRequestData fareSearchRequestData = packageRequestData.getTransportPackageRequest()
					.getFareSearchRequest();
			final String displayOrder = fareSearchRequestData.getSearchProcessingInfo().getDisplayOrder();
			final FareSelectionData fareSelectionData = packageResponseData.getTransportPackageResponse().getFareSearchResponse();
			sortFareSelectionData(fareSelectionData, displayOrder);

			populateFareSearchResponseInModel(fareSelectionData, model);
			populateModel(model, packageResponseData);

			model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_IN_CART, packageFacade.isPackageInCart());

			storeCmsPageInModel(model, getContentPageForLabelOrId(PACKAGE_DETAILS_CMS_PAGE));
			setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PACKAGE_DETAILS_CMS_PAGE));
			return getViewForPage(model);
		}
		catch (final ModelNotFoundException ex)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"accommodation.offering.not.found.error.message");
			return REDIRECT_PREFIX + "/";
		}
	}

	@RequestMapping(value = "/manage-booking/amend-package-details/{accommodationOfferingCode}", method = RequestMethod.GET)
	public String getAmendmentPackageDetailsPage(@PathVariable("accommodationOfferingCode") final String accommodationOfferingCode,
			@Valid @ModelAttribute(AccommodationaddonWebConstants.ACCOMMODATION_AVAILABILITY_FORM) final AccommodationAvailabilityForm accommodationAvailabilityForm,
			final BindingResult accommodationBindingResult, final HttpServletRequest request, final HttpServletResponse response,
			final RedirectAttributes redirectModel, final Model model) throws CMSItemNotFoundException
	{
		final boolean validateCart = validateAmendPackageInCart(accommodationOfferingCode, accommodationAvailabilityForm, request,
				accommodationBindingResult);
		if (!validateCart)
		{
			getTravelCartFacade().deleteCurrentCart();
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, AMEND_CART_ERROR);
			return REDIRECT_PREFIX + "/";
		}
		populateCustomerReviews(model, accommodationOfferingCode);

		// We need to set this is the session for reservation component where image will be defined, it will be then removed
		// from session in itinerary totals component controller
		getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_OFFERING_CODE,
				accommodationOfferingCode);

		final List<Integer> newAccommodationOrderEntryGroupRefs = getBookingFacade().getNewAccommodationOrderEntryGroupRefs();
		boolean isRoomsAddedInCart = CollectionUtils.isNotEmpty(newAccommodationOrderEntryGroupRefs);
		if (isRoomsAddedInCart && !(CollectionUtils.size(newAccommodationOrderEntryGroupRefs) == CollectionUtils
				.size(accommodationAvailabilityForm.getRoomStayCandidates())))
		{
			newAccommodationOrderEntryGroupRefs.forEach(newAccommodationOrderEntryGroupRef -> accommodationCartFacade
					.removeAccommodationOrderEntryGroup(newAccommodationOrderEntryGroupRef));
			isRoomsAddedInCart = Boolean.FALSE;
		}

		final PackageRequestData packageRequestData = new PackageRequestData();
		populateAccommodationPackageRequestData(packageRequestData, accommodationAvailabilityForm, accommodationOfferingCode,
				Boolean.TRUE, request);

		final PackageResponseData packageResponseData = packageFacade.getAmendPackageResponse(packageRequestData);

		populateModel(model, packageResponseData);

		model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_IN_CART, isRoomsAddedInCart);

		setSessionJourney(model, TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY);
		storeCmsPageInModel(model, getContentPageForLabelOrId(PACKAGE_DETAILS_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PACKAGE_DETAILS_CMS_PAGE));
		return getViewForPage(model);
	}

	protected void populateModel(final Model model, final PackageResponseData packageResponseData)
	{
		model.addAttribute(TravelcommonsWebConstants.IS_PACKAGE_UNAVAILABLE,
				!packageResponseData.isAvailable() ? Boolean.TRUE : Boolean.FALSE);
		model.addAttribute(TraveladdonWebConstants.DATE_FORMAT_LABEL, TraveladdonWebConstants.DATE_FORMAT);
		model.addAttribute(TraveladdonWebConstants.TIME_FORMAT_LABEL, TraveladdonWebConstants.TIME_FORMAT);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.GOOGLE_API_KEY,
				getConfigurationService().getConfiguration().getString(TravelfacadesConstants.GOOGLE_API_KEY));
		model.addAttribute(TravelcommonsWebConstants.PACKAGE_DETAILS_CHANGED_FLAG, packageResponseData
				.getAccommodationPackageResponse().getAccommodationAvailabilityResponse().getConfigRoomsUnavailable());
		model.addAttribute(TravelcommonsWebConstants.PACKAGE_AVAILABILITY_RESPONSE, packageResponseData);
		final boolean isAmendJourney = getTravelCartFacade().isAmendmentCart();
		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, isAmendJourney);
		model.addAttribute(TravelcommonsWebConstants.ADD_ROOM_PACKAGE_URL,
				isAmendJourney ? TravelacceleratorstorefrontWebConstants.ADD_ROOM_PACKAGE_DETAILS_AMENDMENT_PAGE
						: TravelacceleratorstorefrontWebConstants.ADD_ROOM_PACKAGE_DETAILS_PAGE);
		model.addAttribute(TraveladdonWebConstants.NEXT_URL,
				isAmendJourney ? TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_AMENDMENT_PATH
						: TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_PATH);

	}

	protected void setSessionJourney(final Model model, final String bookingJourney)
	{
		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY, bookingJourney);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, bookingJourney);
		}
	}


	protected void populateCustomerReviews(final Model model, final String accommodationOfferingCode)
	{
		final SearchPageData<ReviewData> customerReviewsSearchPageData = getPagedAccommodationOfferingCustomerReviews(
				accommodationOfferingCode, 0);
		model.addAttribute(AccommodationaddonWebConstants.CUSTOMER_REVIEW_SEARCH_PAGE_DATA, customerReviewsSearchPageData);

	}

	protected boolean validateAmendPackageInCart(final String accommodationOfferingCode,
			final AccommodationAvailabilityForm accommodationAvailabilityForm, final HttpServletRequest request,
			final BindingResult accommodationBindingResult)
	{
		if (!getTravelCartFacade().isCurrentCartValid() || !getTravelCartFacade().isAmendmentCart()
				|| !packageFacade.isPackageInCart() || Objects.isNull(accommodationAvailabilityForm)
				|| StringUtils.isEmpty(accommodationOfferingCode))
		{
			return Boolean.FALSE;
		}

		final String originalOrderCode = getTravelCartFacade().getOriginalOrderCode();
		if (StringUtils.isEmpty(originalOrderCode))
		{
			return Boolean.FALSE;
		}

		final AccommodationReservationData accommodationReservationData = reservationFacade
				.getAccommodationReservationSummary(originalOrderCode);
		if (!StringUtils.equals(accommodationOfferingCode,
				accommodationReservationData.getAccommodationReference().getAccommodationOfferingCode()))
		{
			return Boolean.FALSE;
		}
		final String orderCheckInDate = TravelDateUtils.convertDateToStringDate(
				accommodationReservationData.getRoomStays().get(0).getCheckInDate(), TravelservicesConstants.DATE_PATTERN);
		final String orderCheckOutDate = TravelDateUtils.convertDateToStringDate(
				accommodationReservationData.getRoomStays().get(0).getCheckOutDate(), TravelservicesConstants.DATE_PATTERN);

		if (StringUtils.isEmpty(accommodationAvailabilityForm.getCheckInDateTime())
				|| StringUtils.isEmpty(accommodationAvailabilityForm.getCheckOutDateTime())
				|| !StringUtils.equals(accommodationAvailabilityForm.getCheckInDateTime(), orderCheckInDate)
				|| !StringUtils.equals(accommodationAvailabilityForm.getCheckOutDateTime(), orderCheckOutDate))
		{
			return Boolean.FALSE;
		}

		final int numberOfAccommodationOrderEntryGroups = CollectionUtils
				.size(getBookingFacade().getOldAccommodationOrderEntryGroupRefs());
		accommodationAvailabilityForm
				.setRoomStayCandidates(createRoomStayCandidates(request, numberOfAccommodationOrderEntryGroups));
		final int maxAccommodationsQuantity = getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		if (maxAccommodationsQuantity < (numberOfAccommodationOrderEntryGroups
				+ CollectionUtils.size(accommodationAvailabilityForm.getRoomStayCandidates())))
		{
			return Boolean.FALSE;
		}
		accommodationAvailabilityValidator.validate(accommodationAvailabilityForm, accommodationBindingResult);
		return !accommodationBindingResult.hasErrors();

	}

	protected void populateAccommodationPackageRequestData(final PackageRequestData packageRequestData,
			final AccommodationAvailabilityForm accommodationAvailabilityForm, final String accommodationOfferingCode,
			final boolean useOldReservedRoomStays, final HttpServletRequest request)
	{
		final AccommodationAvailabilityRequestData accommodationAvailabilityRequestData = new AccommodationAvailabilityRequestData();
		final CriterionData criterion = new CriterionData();
		final StayDateRangeData stayDateRange = new StayDateRangeData();
		stayDateRange.setStartTime(TravelDateUtils.convertStringDateToDate(accommodationAvailabilityForm.getCheckInDateTime(),
				TravelservicesConstants.DATE_PATTERN));
		stayDateRange.setEndTime(TravelDateUtils.convertStringDateToDate(accommodationAvailabilityForm.getCheckOutDateTime(),
				TravelservicesConstants.DATE_PATTERN));
		criterion.setStayDateRange(stayDateRange);
		criterion.setRoomStayCandidates(accommodationAvailabilityForm.getRoomStayCandidates());
		final PropertyData accommodationReferenceData = new PropertyData();
		accommodationReferenceData.setAccommodationOfferingCode(accommodationOfferingCode);
		criterion.setAccommodationReference(accommodationReferenceData);
		accommodationAvailabilityRequestData.setCriterion(criterion);
		final List<ReservedRoomStayData> reservedRoomStays = new ArrayList<>();

		if (useOldReservedRoomStays)
		{
			final List<ReservedRoomStayData> oldreservedRoomStays = getBookingFacade().getOldReservedRoomStays();

			if (CollectionUtils.isNotEmpty(oldreservedRoomStays))
			{
				if (DateUtils.isSameDay(oldreservedRoomStays.get(0).getCheckInDate(), stayDateRange.getStartTime())
						&& DateUtils.isSameDay(oldreservedRoomStays.get(0).getCheckOutDate(), stayDateRange.getEndTime()))
				{
					reservedRoomStays.addAll(oldreservedRoomStays);
				}
			}
		}

		final List<ReservedRoomStayData> newReservedRoomStays = getBookingFacade().getNewReservedRoomStays();
		if (CollectionUtils.isNotEmpty(newReservedRoomStays))
		{
			if (DateUtils.isSameDay(newReservedRoomStays.get(0).getCheckInDate(), stayDateRange.getStartTime())
					&& DateUtils.isSameDay(newReservedRoomStays.get(0).getCheckOutDate(), stayDateRange.getEndTime()))
			{
				reservedRoomStays.addAll(newReservedRoomStays);
			}
		}
		accommodationAvailabilityRequestData.setReservedRoomStays(reservedRoomStays);
		final AccommodationSearchRequestData accommodationSearchRequestData = prepareAccommodationSearchRequestData(
				accommodationOfferingCode, accommodationAvailabilityForm);

		final AccommodationPackageRequestData accommodationPackageRequestData = new AccommodationPackageRequestData();
		accommodationPackageRequestData.setAccommodationAvailabilityRequest(accommodationAvailabilityRequestData);
		accommodationPackageRequestData.setAccommodationSearchRequest(accommodationSearchRequestData);
		packageRequestData.setAccommodationPackageRequest(accommodationPackageRequestData);
	}

	protected void populateTransportPackageRequestData(final PackageRequestData packageRequestData,
			final FareFinderForm fareFinderForm, final HttpServletRequest request)
	{
		final TransportPackageRequestData transportPackageRequestData = new TransportPackageRequestData();
		transportPackageRequestData.setFareSearchRequest(prepareFareSearchRequestData(fareFinderForm, request));
		packageRequestData.setTransportPackageRequest(transportPackageRequestData);
	}

	protected void initializeFareFinderForm(final FareFinderForm fareFinderForm,
			final AccommodationAvailabilityForm accommodationAvailabilityForm)
	{
		fareFinderForm.setDepartureLocationName(
				getLocationName(fareFinderForm.getDepartureLocation(), fareFinderForm.getDepartureLocationSuggestionType()));
		fareFinderForm.setArrivalLocationName(
				getLocationName(fareFinderForm.getArrivalLocation(), fareFinderForm.getArrivalLocationSuggestionType()));

		fareFinderForm.setPassengerTypeQuantityList(createPassengerTypeQuantityData(
				accommodationAvailabilityForm.getNumberOfRooms(), accommodationAvailabilityForm.getRoomStayCandidates()));
	}

	protected void disableCachingForResponse(final HttpServletResponse response)
	{
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	}
}
