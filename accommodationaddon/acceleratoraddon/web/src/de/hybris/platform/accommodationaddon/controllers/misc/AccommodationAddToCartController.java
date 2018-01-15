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

package de.hybris.platform.accommodationaddon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.accommodationaddon.controllers.AccommodationaddonControllerConstants;
import de.hybris.platform.accommodationaddon.forms.AccommodationAddToCartBookingForm;
import de.hybris.platform.accommodationaddon.forms.cms.AddExtraToCartForm;
import de.hybris.platform.accommodationaddon.validators.AccommodationAddToCartValidator;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.travelacceleratorstorefront.validators.AbstractTravelValidator;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.ReservationFacade;
import de.hybris.platform.travelfacades.facades.accommodation.RoomPreferenceFacade;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller providing functionality of adding accommodation specific products to cart
 */
@Controller
public class AccommodationAddToCartController extends AbstractController
{
	private static final String ADD_TO_CART_RESPONSE = "addToCartResponse";
	private static final String AMEND_CART_ERROR = "accommodation.booking.details.page.request.cart.error";
	private static final String NEXT_PAGE_REDIRECT_URL = "/accommodation-details/next";
	private static final String ERROR_ADD_ROOM_ROOM_REFERENCE_CODE = "error.page.bookingdetails.add.room.roomRefCode";
	private static final String ERROR_ADD_ROOM_QUANTITY = "error.page.bookingdetails.add.room.quantity";

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	@Resource(name = "roomPreferenceFacade")
	RoomPreferenceFacade roomPreferenceFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "accommodationAddToCartValidator")
	private AccommodationAddToCartValidator accommodationAddToCartValidator;

	@Resource(name = "reservationFacade")
	private ReservationFacade reservationFacade;


	@RequestMapping(value = "/cart/accommodation/add", method = RequestMethod.POST, produces = "application/json")
	public String addAccommodationToCart(@Valid final AccommodationAddToCartForm form, final Model model)
	{
		final List<RoomRateCartData> rates = accommodationCartFacade.collectRoomRates(form);

		if (CollectionUtils.isEmpty(rates))
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
		}

		if (!travelCartFacade.isAmendmentCart())
		{
			accommodationCartFacade.cleanUpCartBeforeAddition(form.getAccommodationOfferingCode(), form.getCheckInDateTime(),
					form.getCheckOutDateTime());
		}

		final int allowedNumberOfRooms = configurationService.getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);
		if (!accommodationCartFacade.validateNumberOfRoomsToAdd(form.getAccommodationOfferingCode(), form.getAccommodationCode(),
				form.getRatePlanCode(), form.getNumberOfRooms(), allowedNumberOfRooms))
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.QUANTITY_EXCEEDED));
			model.addAttribute(AccommodationaddonWebConstants.ALLOWED_NUMBER_OF_ROOMS, allowedNumberOfRooms);
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
		}

		try
		{
			final Date checkInDate = TravelDateUtils.convertStringDateToDate(form.getCheckInDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			final Date checkOutDate = TravelDateUtils.convertStringDateToDate(form.getCheckOutDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			accommodationCartFacade.addAccommodationsToCart(checkInDate, checkOutDate, form.getAccommodationOfferingCode(),
					form.getAccommodationCode(), rates, form.getNumberOfRooms(), form.getRatePlanCode());
			model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(true, null));
		}
		catch (final CommerceCartModificationException e)
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
		}

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
	}

	@RequestMapping(value = "/cart/accommodation/package-add", method = RequestMethod.POST, produces = "application/json")
	public String addPackageAccommodationToCart(@Valid final AccommodationAddToCartForm form, final BindingResult bindingResult,
			final Model model)
	{
		accommodationAddToCartValidator.validate(form, bindingResult);

		if (bindingResult.hasErrors() || !travelCartFacade.isCurrentCartValid())
		{
			return getAddToCartResponse(Boolean.FALSE, ERROR_ADD_ROOM_ROOM_REFERENCE_CODE, model);
		}

		return addAccommodationToPackage(form, bindingResult, model);
	}

	@RequestMapping(value = "/cart/accommodation/amend-package-add", method = RequestMethod.POST, produces = "application/json")
	public String addAmendPackageAccommodationToCart(@Valid final AccommodationAddToCartForm form,
			final BindingResult bindingResult, final Model model)
	{
		accommodationAddToCartValidator.validate(form, bindingResult);

		if (bindingResult.hasErrors() || !travelCartFacade.isCurrentCartValid())
		{
			return getAddToCartResponse(Boolean.FALSE, ERROR_ADD_ROOM_ROOM_REFERENCE_CODE, model);
		}

		final List<Integer> oldAccommodationOrderEntryGroupRefs = bookingFacade.getOldAccommodationOrderEntryGroupRefs();

		final boolean isValid = CollectionUtils.isNotEmpty(oldAccommodationOrderEntryGroupRefs)
				&& !oldAccommodationOrderEntryGroupRefs.contains(form.getRoomStayRefNumber());

		if (!isValid)
		{
			return getAddToCartResponse(isValid, ERROR_ADD_ROOM_ROOM_REFERENCE_CODE, model);
		}

		final AccommodationReservationData accommodationReservationData = reservationFacade
				.getCurrentAccommodationReservationSummary();

		final String orderCheckInDate = TravelDateUtils.convertDateToStringDate(
				accommodationReservationData.getRoomStays().get(0).getCheckInDate(), TravelservicesConstants.DATE_PATTERN);
		final String orderCheckOutDate = TravelDateUtils.convertDateToStringDate(
				accommodationReservationData.getRoomStays().get(0).getCheckOutDate(), TravelservicesConstants.DATE_PATTERN);

		if (StringUtils.isEmpty(form.getCheckInDateTime()) || StringUtils.isEmpty(form.getCheckOutDateTime())
				|| !StringUtils.equals(form.getCheckInDateTime(), orderCheckInDate)
				|| !StringUtils.equals(form.getCheckOutDateTime(), orderCheckOutDate))
		{
			return getAddToCartResponse(Boolean.FALSE, ERROR_ADD_ROOM_ROOM_REFERENCE_CODE, model);
		}

		return addAccommodationToPackage(form, bindingResult, model);
	}

	protected String addAccommodationToPackage(final AccommodationAddToCartForm form, final BindingResult bindingResult,
			final Model model)
	{
		final int numberOfRoomsInBooking = CollectionUtils.size(bookingFacade.getAccommodationOrderEntryGroupRefs());
		final int maxAccommodationsQuantity = configurationService.getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);

		if (maxAccommodationsQuantity < (numberOfRoomsInBooking + form.getNumberOfRooms()))
		{
			return getAddToCartResponse(false, ERROR_ADD_ROOM_QUANTITY, model);
		}

		final List<RoomRateCartData> rates = accommodationCartFacade.collectRoomRates(form);

		if (CollectionUtils.isEmpty(rates))
		{
			return getAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED, model);
		}

		try
		{
			final Date checkInDate = TravelDateUtils.convertStringDateToDate(form.getCheckInDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			final Date checkOutDate = TravelDateUtils.convertStringDateToDate(form.getCheckOutDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			accommodationCartFacade.addAccommodationToCart(checkInDate, checkOutDate, form.getAccommodationOfferingCode(),
					form.getAccommodationCode(), rates, form.getNumberOfRooms(), form.getRatePlanCode());
			model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(true, null));
		}
		catch (final CommerceCartModificationException e)
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			travelCartFacade.removeSessionCart();
		}

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
	}

	protected String getAddToCartResponse(final boolean isValid, final String errorMessage, final Model model)
	{
		model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(isValid, errorMessage));
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;

	}

	@RequestMapping(value = "/cart/accommodation/package-change", method = RequestMethod.POST, produces = "application/json")
	public String changePackageAccommodationInCart(@Valid final AccommodationAddToCartForm form, final Model model)
	{
		final List<Integer> accommodationOrderEntryGroupsInCart = bookingFacade.getAccommodationOrderEntryGroupRefs();
		final List<Integer> oldAccommodationOrderEntryGroupsInCart = bookingFacade.getOldAccommodationOrderEntryGroupRefs();

		if (form.getNumberOfRooms() > 1)
		{
			return getAddToCartResponse(false, ERROR_ADD_ROOM_QUANTITY, model);
		}

		if (!accommodationOrderEntryGroupsInCart.contains(form.getRoomStayRefNumber())
				|| oldAccommodationOrderEntryGroupsInCart.contains(form.getRoomStayRefNumber()))
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;

		}

		final List<RoomRateCartData> rates = accommodationCartFacade.collectRoomRates(form);

		if (CollectionUtils.isEmpty(rates))
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
		}

		try
		{
			final Date checkInDate = TravelDateUtils.convertStringDateToDate(form.getCheckInDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			final Date checkOutDate = TravelDateUtils.convertStringDateToDate(form.getCheckOutDateTime(),
					TravelservicesConstants.DATE_PATTERN);
			accommodationCartFacade.replaceAccommodationInCart(checkInDate, checkOutDate, form.getAccommodationOfferingCode(),
					form.getAccommodationCode(), rates, form.getNumberOfRooms(), form.getRatePlanCode(), form.getRoomStayRefNumber());
			model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(true, null));
		}
		catch (final CommerceCartModificationException e)
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			travelCartFacade.removeSessionCart();
		}

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddAccommodationToCartResponse;
	}

	@RequestMapping(value = "/cart/best-offer-accommodations/add", method = RequestMethod.POST)
	public String addBestOfferedAccommodationsToCart(
			@ModelAttribute("accommodationAddToCartBookingForm") final AccommodationAddToCartBookingForm accommodationAddToCartBookingForm,
			@RequestParam(value = "accommodationDetailsPageURL", required = true) final String accommodationDetailsPageURL,
			final RedirectAttributes redirectModel, final Model model, final BindingResult bindingResult)
	{
		if (CollectionUtils.isEmpty(accommodationAddToCartBookingForm.getAccommodationAddToCartForms()))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, AMEND_CART_ERROR);
			return REDIRECT_PREFIX + accommodationDetailsPageURL;
		}

		int totalNumberOfRoomsForBookingRequested = 0;
		int index = 0;

		for (final AccommodationAddToCartForm accommodationAddToCartForm : accommodationAddToCartBookingForm
				.getAccommodationAddToCartForms())
		{
			totalNumberOfRoomsForBookingRequested += accommodationAddToCartForm.getNumberOfRooms();
			validateForm(accommodationAddToCartValidator, accommodationAddToCartForm, index++, bindingResult,
					AccommodationaddonWebConstants.ACCOMMODATION_ADD_TO_CART_FORM);
		}

		final int maxBookingAllowed = configurationService.getConfiguration()
				.getInt(TravelfacadesConstants.MAX_ACCOMMODATION_QUANTITY);

		if (bindingResult.hasErrors()
				|| !(totalNumberOfRoomsForBookingRequested > 0 && totalNumberOfRoomsForBookingRequested <= maxBookingAllowed))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, AMEND_CART_ERROR);
			return REDIRECT_PREFIX + accommodationDetailsPageURL;
		}

		accommodationCartFacade.emptyCart();

		for (final AccommodationAddToCartForm form : accommodationAddToCartBookingForm.getAccommodationAddToCartForms())
		{
			final List<RoomRateCartData> rates = accommodationCartFacade.collectRoomRates(form);

			if (CollectionUtils.isEmpty(rates))
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, AMEND_CART_ERROR);
				return REDIRECT_PREFIX + accommodationDetailsPageURL;
			}
			try
			{
				final Date checkInDate = TravelDateUtils.convertStringDateToDate(form.getCheckInDateTime(),
						TravelservicesConstants.DATE_PATTERN);
				final Date checkOutDate = TravelDateUtils.convertStringDateToDate(form.getCheckOutDateTime(),
						TravelservicesConstants.DATE_PATTERN);
				accommodationCartFacade.addAccommodationToCart(checkInDate, checkOutDate, form.getAccommodationOfferingCode(),
						form.getAccommodationCode(), rates, form.getNumberOfRooms(), form.getRatePlanCode());
			}
			catch (final CommerceCartModificationException e)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, AMEND_CART_ERROR);
				return REDIRECT_PREFIX + accommodationDetailsPageURL;
			}
		}
		model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(true, null));
		return REDIRECT_PREFIX + NEXT_PAGE_REDIRECT_URL;
	}

	protected AddToCartResponseData createAddToCartResponse(final boolean valid, final String errorMessage)
	{
		final AddToCartResponseData response = new AddToCartResponseData();
		response.setValid(valid);
		response.setErrors(Collections.singletonList(errorMessage));
		return response;
	}

	@RequestMapping(value = "/cart/accommodation/add-extra", method = RequestMethod.POST, produces = "application/json")
	public String addExtraToCart(final AddExtraToCartForm addExtraToCartForm, final Model model)
	{
		final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		if (!validator.validate(addExtraToCartForm).isEmpty())
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
			return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddExtraToCartResponse;
		}

		try
		{
			accommodationCartFacade.addProductToCart(addExtraToCartForm.getProductCode(),
					addExtraToCartForm.getRoomStayReferenceNumber(), addExtraToCartForm.getQuantity());
			model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(true, null));
		}
		catch (final CommerceCartModificationException e)
		{
			model.addAttribute(ADD_TO_CART_RESPONSE,
					createAddToCartResponse(false, AccommodationaddonWebConstants.BASKET_ERROR_OCCURRED));
		}

		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddExtraToCartResponse;
	}

	/**
	 * Saves the room bed preference against the room stay ref number. If any of the information is incorrect, nothing is
	 * saved.
	 */
	@RequestMapping(value = "/cart/accommodation/save-room-preference", method = RequestMethod.POST, produces = "application/json")
	public String saveRoomPreference(final HttpServletRequest request, final HttpServletResponse response,
			@ModelAttribute(value = "roomStayRefNum") final String roomStayRefNum,
			@ModelAttribute(value = "roomPreferenceCode") final String roomPreferenceCode, final Model model)
	{
		if (StringUtils.isNotEmpty(roomPreferenceCode) || StringUtils.isNotEmpty(roomStayRefNum))
		{
			try
			{
				final int roomStayRefNumber = Integer.parseInt(roomStayRefNum);
				final boolean isSuccess = roomPreferenceFacade.saveRoomPreference(roomStayRefNumber,
						Collections.singletonList(roomPreferenceCode));
				model.addAttribute(ADD_TO_CART_RESPONSE, createAddToCartResponse(isSuccess,
						isSuccess ? StringUtils.EMPTY : AccommodationaddonWebConstants.ERROR_ROOM_BED_PREFERENCE_ADD));
			}
			catch (final NumberFormatException ex)
			{
				model.addAttribute(ADD_TO_CART_RESPONSE,
						createAddToCartResponse(Boolean.FALSE, AccommodationaddonWebConstants.ERROR_ROOM_BED_PREFERENCE_ADD));
			}
		}
		return AccommodationaddonControllerConstants.Views.Pages.Hotel.AddExtraToCartResponse;
	}

	/**
	 * Validate Form method
	 *
	 * @param accommodationAddToCartValidator
	 * @param accommodationAddToCartForm
	 * @param index
	 * @param bindingResult
	 * @param formName
	 */
	protected void validateForm(final AbstractTravelValidator accommodationAddToCartValidator,
			final AccommodationAddToCartForm accommodationAddToCartForm, final int index, final BindingResult bindingResult,
			final String formName)
	{
		accommodationAddToCartValidator.setTargetForm(AccommodationaddonWebConstants.ACCOMMODATION_ADD_TO_CART_FORM);
		accommodationAddToCartValidator.setAttributePrefix("accommodationAddToCartForms[" + index + "]");
		accommodationAddToCartValidator.validate(accommodationAddToCartForm, bindingResult);
	}
}
