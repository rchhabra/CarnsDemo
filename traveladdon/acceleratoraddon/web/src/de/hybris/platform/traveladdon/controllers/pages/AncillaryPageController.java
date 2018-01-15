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

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.travel.CheckInResponseData;
import de.hybris.platform.commercefacades.travel.FareSelectionData;
import de.hybris.platform.commercefacades.travel.PricedItineraryData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferRequestData;
import de.hybris.platform.commercefacades.travel.ancillary.data.OfferResponseData;
import de.hybris.platform.commercefacades.travel.seatmap.data.SeatMapResponseData;
import de.hybris.platform.commercefacades.user.UserFacade;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.checkin.steps.CheckinStep;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.controllers.pages.steps.checkin.AbstractCheckinStepController;
import de.hybris.platform.traveladdon.forms.AddBundleToCartForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.OffersFacade;
import de.hybris.platform.travelfacades.facades.TravelBundleTemplateFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.fare.search.UpgradeFareSearchFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.AncillaryOfferGroupDisplayStrategy;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Ancillary page
 */
@Controller
@RequestMapping(
{ "/ancillary", "/manage-booking/ancillary" })
public class AncillaryPageController extends AbstractCheckinStepController
{
	private static final Logger LOG = Logger.getLogger(AncillaryPageController.class);

	private static final String CHECK_IN_RESPONSE = "checkInResponse";
	private static final String IS_CHECK_IN_JOURNEY = "isCheckInJourney";
	private static final String OFFER_GROUPS_VIEW_MAP = "offerGroupsViewMap";
	private static final String CATEGORY_RESTRICTION_ERROR = "text.ancillary.category.restriction.error";
	private static final String RESTRICTION_ERRORS = "restrictionErrors";
	private static final String ERROR_RESULT = "errorResult";
	private static final String ANCILLARY_DEFAULT_CMS_PAGE = "ancillaryPage";
	private static final String ANCILLARY_CHECKIN_CMS_PAGE = "ancillaryCheckinPage";
	private static final String ANCILLARY_AMENDMENT_CMS_PAGE = "ancillaryAmendmentPage";
	private static final String OFFER_RESPONSE_DATA = "offerResponseData";
	private static final String NEXT_URL = "/ancillary/next";
	private static final String AMENDMENT_NEXT_URL = "/manage-booking/ancillary/next";
	private static final String BOOKING_DETAILS_URL = "/manage-booking/booking-details/";
	private static final String ITINERARIES_JSON = "itinerariesJson";
	private static final String TRAVELLERS_NAMES_MAP = "travellersNamesMap";
	private static final String PAYMENT_FLOW = "sop";
	private static final String ANCILLARY = "ancillary";
	private static final String GROUP_BOOKING = "isGroupBooking";

	@Resource(name = "offersFacade")
	private OffersFacade offersFacade;

	@Resource(name = "cartFacade")
	private TravelCartFacade travelCartFacade;

	@Resource(name = "userFacade")
	private UserFacade userFacade;

	@Resource(name = "bookingFacade")
	private BookingFacade bookingFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource(name = "travelRestrictionFacade")
	private TravelRestrictionFacade travelRestrictionFacade;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "ancillaryOfferGroupDisplayStrategy")
	private AncillaryOfferGroupDisplayStrategy ancillaryOfferGroupDisplayStrategy;

	@Resource(name = "offerGroupsViewMap")
	private Map<String, String> offerGroupsViewMap;

	@Resource(name = "offerGroupsViewMapForGroupBooking")
	private Map<String, String> offerGroupsViewMapForGroupBooking;

	@Resource(name = "travelBundleTemplateFacade")
	private TravelBundleTemplateFacade travelBundleTemplateFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "upgradeFareSearchFacade")
	private UpgradeFareSearchFacade upgradeFareSearchFacade;

	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String getAncillaryPage(final Model model) throws CMSItemNotFoundException
	{
		if (!travelCartFacade.validateOriginDestinationRefNumbersInCart())
		{
			return REDIRECT_PREFIX + "/";
		}

		getSessionService().setAttribute(IS_CHECK_IN_JOURNEY, false);
		return getAncillaryPage(model, ANCILLARY_DEFAULT_CMS_PAGE);
	}

	/**
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/amendment", method = RequestMethod.GET)
	public String getAmendmentAncillaryPage(final Model model) throws CMSItemNotFoundException
	{
		getSessionService().setAttribute(IS_CHECK_IN_JOURNEY, false);
		return getAncillaryPage(model, ANCILLARY_AMENDMENT_CMS_PAGE);
	}

	/**
	 * @param model
	 * @return String
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
	 * @param model
	 * @return String
	 * @throws CMSItemNotFoundException
	 */
	@RequestMapping(value = "/checkin", method = RequestMethod.GET)
	public String getCheckinAncillaryPage(final Model model) throws CMSItemNotFoundException
	{
		if (!model.asMap().containsKey(CHECK_IN_RESPONSE))
		{
			return REDIRECT_PREFIX + "/";
		}

		final CheckInResponseData checkInResponse = (CheckInResponseData) model.asMap().get(CHECK_IN_RESPONSE);
		final String bookingReference = checkInResponse.getBookingReference();
		bookingFacade.amendOrder(bookingReference, bookingFacade.getCurrentUserUid());
		getSessionService().setAttribute(IS_CHECK_IN_JOURNEY, true);
		return getAncillaryPage(model, ANCILLARY_CHECKIN_CMS_PAGE);
	}

	protected String getAncillaryPage(final Model model, final String ancillaryCmsPage) throws CMSItemNotFoundException
	{
		if (!travelCartFacade.isCurrentCartValid())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.HOME_PAGE_PATH;
		}
		storeCmsPageInModel(model, getContentPageForLabelOrId(ancillaryCmsPage));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(ancillaryCmsPage));

		final OfferRequestData offerRequest = offersFacade.getOffersRequest();
		final OfferResponseData offerResponseData = offersFacade.getOffers(offerRequest);

		ancillaryOfferGroupDisplayStrategy.filterOfferResponseData(getContentPageForLabelOrId(ancillaryCmsPage), offerResponseData);
		final List<TravellerData> travellers = offerResponseData.getItineraries().stream().findFirst().get().getTravellers();

		model.addAttribute(OFFER_RESPONSE_DATA, offerResponseData);
		model.addAttribute(ITINERARIES_JSON, getJson(offerResponseData.getItineraries(), "itineraries"));
		model.addAttribute(TRAVELLERS_NAMES_MAP, travellerFacade.populateTravellersNamesMap(travellers));

		if (CollectionUtils.size(travellers) < getConfigurationService().getConfiguration()
				.getInt(TravelfacadesConstants.MAX_PACKAGE_GUEST_QUANTITY))
		{
			model.addAttribute(OFFER_GROUPS_VIEW_MAP, offerGroupsViewMap);
		}
		else
		{
			model.addAttribute(OFFER_GROUPS_VIEW_MAP, offerGroupsViewMapForGroupBooking);
			model.addAttribute(GROUP_BOOKING, true);
		}

		model.addAttribute(TraveladdonWebConstants.AMEND, travelCartFacade.isAmendmentCart());
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		model.addAttribute(TraveladdonWebConstants.NEXT_URL, determineNextUrl());

		if (travelCartFacade.isAmendmentCart())
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
			model.addAttribute(TraveladdonWebConstants.ORIGINAL_ORDER_CODE, travelCartFacade.getOriginalOrderCode());
		}

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		if (sessionBookingJourney != null)
		{
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY, sessionBookingJourney);
		}
		else
		{
			sessionService.setAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
			model.addAttribute(TravelacceleratorstorefrontWebConstants.BOOKING_JOURNEY,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY);
		}

		return getViewForPage(model);
	}

	@ResponseBody
	@RequestMapping(value =
	{ "/accommodation-map" }, method =
	{ RequestMethod.POST, RequestMethod.GET }, produces =
	{ "application/json" })
	public SeatMapResponseData getAccommodations()
	{

		final OfferRequestData offerRequest = offersFacade.getOffersRequest();
		final OfferResponseData offerResponseData = offersFacade.getAccommodations(offerRequest);
		return offerResponseData.getSeatMap();
	}

	/**
	 * This method determines the next url of checkout flow. If the context is purchase flow, then the next url is
	 * "/ancillary/next" else if the context is Amendments then the next url is "/manage-booking/ancillary/next".
	 *
	 * @return next url
	 */
	protected String determineNextUrl()
	{
		return travelCartFacade.isAmendmentCart() ? AMENDMENT_NEXT_URL : NEXT_URL;
	}

	/**
	 * Performs a validation on the TravelRestriction for each OfferGroup available.
	 *
	 * @param model
	 * @return the location of the JSON object used to render the error in the front-end
	 */
	@RequestMapping(value = "/check-offer-groups-restriction", method = RequestMethod.GET)
	public String checkCategoryRestrictions(final Model model)
	{
		model.addAttribute(RESTRICTION_ERRORS, travelRestrictionFacade.getCategoryRestrictionErrors());
		return TraveladdonControllerConstants.Views.Pages.Ancillary.TravelRestrictionResponse;
	}

	/**
	 * Redirects user to the next checkout page which is traveller details
	 *
	 * @param redirectModel
	 * @return traveller details page
	 */
	@RequestMapping(value = "/next", method = RequestMethod.GET)
	public String nextPage(final RedirectAttributes redirectModel)
	{
		if (!travelRestrictionFacade.checkCategoryRestrictions())
		{
			redirectModel.addFlashAttribute(ERROR_RESULT, CATEGORY_RESTRICTION_ERROR);
			return REDIRECT_PREFIX + TraveladdonWebConstants.ANCILLARY_ROOT_URL;
		}

		final String sessionBookingJourney = getSessionService()
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);

		if (!travelCartFacade.isAmendmentCart())
		{
			if (StringUtils.isNotEmpty(sessionBookingJourney) && !StringUtils.equalsIgnoreCase(sessionBookingJourney,
					TravelfacadesConstants.BOOKING_TRANSPORT_ONLY))
			{
				final String accommodationQueryString = sessionService
						.getAttribute(TravelacceleratorstorefrontWebConstants.ACCOMMODATION_QUERY_STRING);
				return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.ACCOMMODATION_SEARCH_PATH + accommodationQueryString;
			}
		}

		if (userFacade.isAnonymousUser() && !travelCartFacade.isAmendmentCart())
		{
			return REDIRECT_PREFIX + TravelacceleratorstorefrontWebConstants.CHECKOUT_LOGIN;
		}

		if (travelCartFacade.isAmendmentCart())
		{
			if (!travelCartFacade.hasCartBeenAmended())
			{
				if (((Boolean) getSessionService().getAttribute(IS_CHECK_IN_JOURNEY)).booleanValue())
				{
					return REDIRECT_PREFIX + TraveladdonWebConstants.CHECK_IN_SUCCESS_URL;
				}

				return REDIRECT_PREFIX + BOOKING_DETAILS_URL + travelCartFacade.getOriginalOrderCode();
			}
			if (userFacade.isAnonymousUser())
			{
				getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
			}
			final String paymentFlowProperty = configurationService.getConfiguration().getString("payment.flow");
			if (StringUtils.isNotBlank(paymentFlowProperty))
			{
				return getCheckinStep().nextStep() + paymentFlowProperty;
			}
			return getCheckinStep().nextStep() + PAYMENT_FLOW;
		}

		return REDIRECT_PREFIX + TraveladdonWebConstants.TRAVELLER_DETAILS_PATH;
	}

	protected CheckinStep getCheckinStep()
	{
		return getCheckinStep(ANCILLARY);
	}

}
