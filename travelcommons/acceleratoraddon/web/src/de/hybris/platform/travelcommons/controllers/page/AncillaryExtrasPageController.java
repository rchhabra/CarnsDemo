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

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.accommodationaddon.constants.AccommodationaddonWebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.accommodation.AccommodationReservationData;
import de.hybris.platform.commercefacades.accommodation.AvailableServiceData;
import de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData;
import de.hybris.platform.commercefacades.accommodation.RoomPreferenceData;
import de.hybris.platform.commercefacades.accommodation.RoomStayData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.travelacceleratorstorefront.controllers.pages.TravelAbstractPageController;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.AddBundleToCartForm;
import de.hybris.platform.travelcommons.constants.TravelcommonsWebConstants;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.OffersFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.accommodation.AccommodationExtrasFacade;
import de.hybris.platform.travelfacades.facades.accommodation.RoomPreferenceFacade;
import de.hybris.platform.travelfacades.facades.packages.DealCartFacade;
import de.hybris.platform.travelfacades.facades.packages.PackageFacade;
import de.hybris.platform.travelfacades.fare.search.UpgradeFareSearchFacade;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.strategies.AncillaryOfferGroupDisplayStrategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Ancillary Extras page
 */
@Controller
@RequestMapping(
{ "/ancillary-extras", "/manage-booking/ancillary-extras" })
public class AncillaryExtrasPageController extends TravelAbstractPageController
{
	private static final String OFFER_GROUPS_VIEW_MAP = "offerGroupsViewMap";
	private static final String ANCILLARY_EXTRAS_DEFAULT_CMS_PAGE = "ancillaryExtrasPage";
	private static final String OFFER_RESPONSE_DATA = "offerResponseData";
	private static final String ITINERARIES_JSON = "itinerariesJson";
	private static final String TRAVELLERS_NAMES_MAP = "travellersNamesMap";
	private String defaultRoomBedPreferenceCode;

	@Resource(name = "offersFacade")
	private OffersFacade offersFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "packageFacade")
	private PackageFacade packageFacade;

	@Resource(name = "roomPreferenceFacade")
	RoomPreferenceFacade roomPreferenceFacade;

	@Resource(name = "accommodationExtrasFacade")
	private AccommodationExtrasFacade accommodationExtrasFacade;

	@Resource(name = "ancillaryOfferGroupDisplayStrategy")
	private AncillaryOfferGroupDisplayStrategy ancillaryOfferGroupDisplayStrategy;

	@Resource(name = "offerGroupsViewMap")
	private Map<String, String> offerGroupsViewMap;

	@Resource(name = "dealCartFacade")
	private DealCartFacade dealCartFacade;

	@Resource(name = "upgradeFareSearchFacade")
	private UpgradeFareSearchFacade upgradeFareSearchFacade;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getAncillaryPage(final Model model,
			@RequestParam(value = "roomStay", required = false) final Integer roomStayRefNumberToUpdate)
			throws CMSItemNotFoundException
	{

		if (!packageFacade.isPackageInCart())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.HOME_PAGE_PATH;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ANCILLARY_EXTRAS_DEFAULT_CMS_PAGE));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ANCILLARY_EXTRAS_DEFAULT_CMS_PAGE));

		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (StringUtils.isEmpty(sessionBookingJourney) || (StringUtils.isNotEmpty(sessionBookingJourney)
				&& !StringUtils.equals(sessionBookingJourney, TravelfacadesConstants.BOOKING_ACCOMMODATION_ONLY)))
		{
			final OfferRequestData offerRequest = offersFacade.getOffersRequest();
			final OfferResponseData offerResponseData = offersFacade.getOffers(offerRequest);

			ancillaryOfferGroupDisplayStrategy.filterOfferResponseData(getContentPageForLabelOrId(ANCILLARY_EXTRAS_DEFAULT_CMS_PAGE),
					offerResponseData);

			model.addAttribute(OFFER_RESPONSE_DATA, offerResponseData);
			model.addAttribute(ITINERARIES_JSON, getJson(offerResponseData.getItineraries(), "itineraries"));
			model.addAttribute(TRAVELLERS_NAMES_MAP, travellerFacade
					.populateTravellersNamesMap(offerResponseData.getItineraries().stream().findFirst().get().getTravellers()));
			model.addAttribute(OFFER_GROUPS_VIEW_MAP, offerGroupsViewMap);
		}
		if (StringUtils.isEmpty(sessionBookingJourney) || (StringUtils.isNotEmpty(sessionBookingJourney)
				&& !StringUtils.equals(sessionBookingJourney, TravelfacadesConstants.BOOKING_TRANSPORT_ONLY)))
		{
			final AccommodationReservationData accommodationReservationData = bookingFacade
					.getAccommodationReservationDataForGuestDetailsFromCart();

			if (Objects.nonNull(roomStayRefNumberToUpdate)
					|| getTravelCartFacade().isAmendmentCart() && accommodationCartFacade.isNewRoomInCart())
			{
				final List<Integer> roomStayRefNumbers = Objects.nonNull(roomStayRefNumberToUpdate)
						? Collections.singletonList(roomStayRefNumberToUpdate) : bookingFacade.getNewAccommodationOrderEntryGroupRefs();
				final List<ReservedRoomStayData> roomStaysToUpdate = accommodationReservationData.getRoomStays().stream()
						.filter(roomStay -> roomStayRefNumbers.contains(roomStay.getRoomStayRefNumber())).collect(Collectors.toList());
				if (CollectionUtils.isNotEmpty(roomStaysToUpdate))
				{
					accommodationReservationData.setRoomStays(roomStaysToUpdate);
				}
			}

			if (CollectionUtils.isNotEmpty(accommodationReservationData.getRoomStays()))
			{
				final Comparator<ReservedRoomStayData> reservedRoomStayDataComparator = (b1, b2) -> b1.getRoomStayRefNumber()
						.compareTo(b2.getRoomStayRefNumber());
				accommodationReservationData.getRoomStays().sort(reservedRoomStayDataComparator);
			}

			final List<AvailableServiceData> availableServices = accommodationExtrasFacade
					.getAvailableServices(accommodationReservationData);

			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_RESERVATION_DATA, accommodationReservationData);
			model.addAttribute(AccommodationaddonWebConstants.AVAILABLE_SERVICES, availableServices);

			final Map<Integer, List<RoomPreferenceData>> accommodationRoomPreferenceMap = accommodationReservationData.getRoomStays()
					.stream()
					.collect(Collectors.toMap(RoomStayData::getRoomStayRefNumber,
							roomStay -> roomPreferenceFacade.getRoomPreferencesForTypeAndAccommodation(
									AccommodationaddonWebConstants.ACCOMMODATION_ROOM_PREFERENCE_TYPE, roomStay.getRoomTypes())));
			model.addAttribute(AccommodationaddonWebConstants.ACCOMMODATION_ROOM_PREFERENCE_MAP, accommodationRoomPreferenceMap);

			model.addAttribute(TravelacceleratorstorefrontWebConstants.DEFAULT_ROOM_BED_PREFERENCE_CODE,
					getDefaultRoomBedPreferenceCode());
		}
		model.addAttribute(TravelacceleratorstorefrontWebConstants.AMEND, getTravelCartFacade().isAmendmentCart());
		model.addAttribute(TravelcommonsWebConstants.IS_DEAL_IN_CART, dealCartFacade.isDealInCart());


		if (getTravelCartFacade().isAmendmentCart())
		{
			model.addAttribute(TraveladdonWebConstants.ORIGINAL_ORDER_CODE, getTravelCartFacade().getOriginalOrderCode());
		}

		if (StringUtils.isNotEmpty(sessionBookingJourney))
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			getSessionService().setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_PACKAGE);
		}
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute(TraveladdonWebConstants.NEXT_URL, determineNextUrl());

		return getViewForPage(model);

	}

	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/upgrade-bundle-options", method = RequestMethod.GET, produces =
	{ "application/json" })
	public String getUpgradeBundleOptionsPage(final Model model)
	{
		final FareSelectionData fareSelectionData = upgradeFareSearchFacade.doUpgradeSearch();
		model.addAttribute(TraveladdonWebConstants.UPGRADE_BUNDLE_PRICED_ITINERARIES, fareSelectionData.getPricedItineraries());
		final AddBundleToCartForm addBundleToCartForm = new AddBundleToCartForm();
		model.addAttribute(TraveladdonWebConstants.ADD_BUNDLE_TO_CART_FORM, addBundleToCartForm);
		model.addAttribute(TravelacceleratorstorefrontWebConstants.ADD_BUNDLE_TO_CART_URL,
				TraveladdonWebConstants.UPGRADE_BUNDLE_URL);
		model.addAttribute(TraveladdonWebConstants.IS_UPGRADE_OPTION_AVAILABLE,
				isUpgradeAvailable(fareSelectionData.getPricedItineraries()));
		return TraveladdonControllerConstants.Views.Pages.Ancillary.UpdateBundleJSONData;
	}

	protected boolean isUpgradeAvailable(final List<PricedItineraryData> pricedItineraries)
	{
		return CollectionUtils.isNotEmpty(pricedItineraries)
				&& pricedItineraries.stream().allMatch(pricedItinerary -> pricedItinerary.isAvailable()
						&& CollectionUtils.isNotEmpty(pricedItinerary.getItineraryPricingInfos()));
	}

	/**
	 * Redirects user to the next checkout page which is personal details
	 *
	 * @param redirectModel
	 * @return personal details page
	 */
	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String nextPage(final RedirectAttributes redirectModel)
	{

		if (getTravelCartFacade().isAmendmentCart())
		{
			if (!getTravelCartFacade().hasCartBeenAmended())
			{
				return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.BOOKING_DETAILS_URL
						+ getTravelCartFacade().getOriginalOrderCode();
			}

			if (accommodationCartFacade.isNewRoomInCart())
			{
				return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PERSONAL_DETAILS_PATH;
			}

			if (getTravelCustomerFacade().isCurrentUserB2bCustomer())
			{
				return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_TYPE_PATH;
			}

			if (userFacade.isAnonymousUser())
			{
				getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
			}

			String paymentFlowProperty = getConfigurationService().getConfiguration().getString("payment.flow");
			paymentFlowProperty = StringUtils.isNotBlank(paymentFlowProperty) ? paymentFlowProperty
					: TravelacceleratorstorefrontWebConstants.PAYMENT_FLOW;
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PAYMENT_DETAILS_PATH + paymentFlowProperty;
		}

		if (userFacade.isAnonymousUser())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.CHECKOUT_LOGIN;
		}

		return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.PERSONAL_DETAILS_PATH;
	}

	/**
	 * This method determines the next url of checkout flow. If the context is purchase flow, then the next url is
	 * "/ancillary/next" else if the context is Amendments then the next url is "/manage-booking/ancillary/next".
	 *
	 * @return next url
	 */
	protected String determineNextUrl()
	{
		return (getTravelCartFacade().isAmendmentCart() ? TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_AMENDMENT_PATH
				: TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_PATH) + "/next";
	}

	/**
	 * @return the defaultRoomBedPreferenceCode
	 */
	protected String getDefaultRoomBedPreferenceCode()
	{
		return defaultRoomBedPreferenceCode;
	}

	/**
	 * @param defaultRoomBedPreferenceCode
	 *           the defaultRoomBedPreferenceCode to set
	 */
	@Required
	public void setDefaultRoomBedPreferenceCode(final String defaultRoomBedPreferenceCode)
	{
		this.defaultRoomBedPreferenceCode = defaultRoomBedPreferenceCode;
	}
}
