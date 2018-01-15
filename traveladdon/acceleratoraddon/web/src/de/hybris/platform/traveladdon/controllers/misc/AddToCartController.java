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

package de.hybris.platform.traveladdon.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.commercefacades.travel.ItineraryPricingInfoData;
import de.hybris.platform.commercefacades.travel.TransportOfferingData;
import de.hybris.platform.commercefacades.travel.TravelBundleTemplateData;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.travelacceleratorstorefront.constants.TravelacceleratorstorefrontWebConstants;
import de.hybris.platform.traveladdon.constants.TraveladdonWebConstants;
import de.hybris.platform.traveladdon.controllers.TraveladdonControllerConstants;
import de.hybris.platform.traveladdon.forms.AddBundleToCartForm;
import de.hybris.platform.traveladdon.forms.AddRemoveAccommodation;
import de.hybris.platform.traveladdon.forms.AddToCartForm;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.ConfiguredAccommodationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.fare.search.resolvers.FareSearchHashResolver;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.AddAccommodationToCartStrategy;
import de.hybris.platform.travelfacades.strategies.AddBundleToCartValidationStrategy;
import de.hybris.platform.travelfacades.strategies.AddToCartValidationStrategy;
import de.hybris.platform.travelfacades.strategies.SelectedAccommodationStrategy;
import de.hybris.platform.travelfacades.strategies.impl.TravelRestrictionStrategy;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.order.CommerceBundleCartModificationException;
import de.hybris.platform.travelservices.price.data.PriceLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
public class AddToCartController extends AbstractController
{
	private static final Logger LOG = Logger.getLogger(AddToCartController.class);

	private static final String ADD_TO_CART_ERROR = "error";
	private static final String REMOVE_FROM_CART_ERROR = "error";
	private static final String ADD_PRODUCT_TO_CART_RESPONSE = "addProductToCartResponse";
	private static final String ADD_BUNDLE_TO_CART_RESPONSE = "addBundleToCartResponse";
	protected static final String ERROR_MSG_TYPE = "errorMsg";
	protected static final String BASKET_ERROR_OCCURRED = "basket.error.occurred";
	protected static final String ADD_BUNDLE_TO_CART_REQUEST_ERROR = "add.bundle.to.cart.request.error";
	protected static final int MAX_MESSAGE_LENGTH = 30;
	protected static final String VOUCHER_REDEEM_SUCCESS = "redeemSuccess";
	protected static final String VOUCHER_RELEASE_SUCCESS = "releaseSuccess";

	/**
	 * This constant value dictates the quantity of the fare product to be added in the cart.
	 */
	protected static final long PRODUCT_QUANTITY = 1;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cartFacade")
	private TravelCartFacade cartFacade;

	@Resource(name = "travellerFacade")
	private TravellerFacade travellerFacade;

	@Resource
	private L10NService l10NService;

	@Resource(name = "travelRestrictionFacade")
	private TravelRestrictionFacade travelRestrictionFacade;

	@Resource(name = "travelRestrictionStrategy")
	private TravelRestrictionStrategy travelRestrictionStrategy;

	@Resource(name = "addAccommodationToCartStrategy")
	private AddAccommodationToCartStrategy addAccommodationToCartStrategy;

	@Resource(name = "configuredAccommodationFacade")
	private ConfiguredAccommodationFacade configuredAccommodationFacade;

	@Resource(name = "travelCommercePriceFacade")
	private TravelCommercePriceFacade travelCommercePriceFacade;

	@Resource(name = "addToCartValidationStrategyList")
	private List<AddToCartValidationStrategy> addToCartValidationStrategyList;

	@Resource(name = "selectedAccommodationStrategyList")
	private List<SelectedAccommodationStrategy> selectedAccommodationStrategyList;

	@Resource(name = "addBundleToCartValidationStrategyList")
	private List<AddBundleToCartValidationStrategy> addBundleToCartValidationStrategyList;

	@Resource(name = "addPackageBundleToCartValidationStrategyList")
	private List<AddBundleToCartValidationStrategy> addPackageBundleToCartValidationStrategyList;

	@Resource(name = "fareSearchHashResolver")
	private FareSearchHashResolver fareSearchHashResolver;

	@Resource(name = "accommodationCartFacade")
	private AccommodationCartFacade accommodationCartFacade;

	/**
	 * @param addToCartForm
	 * @param model
	 * @return String
	 */
	@RequestMapping(value = "/cart/add", method = RequestMethod.POST, produces = "application/json")
	public String addToCart(final AddToCartForm addToCartForm, final Model model)
	{
		performAddToCart(addToCartForm, model);
		model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(true, null, null));
		return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
	}

	/**
	 * Perform add to cart.
	 *
	 * @param addToCartForm
	 *           the add to cart form
	 * @param model
	 *           the model
	 * @return true, if successful
	 */
	protected boolean performAddToCart(final AddToCartForm addToCartForm, final Model model)
	{
		final String travellerCode = StringUtils.isNotBlank(addToCartForm.getTravellerCode()) ? addToCartForm.getTravellerCode()
				: null;
		final String travelRouteCode = StringUtils.isNotBlank(addToCartForm.getTravelRouteCode())
				? addToCartForm.getTravelRouteCode() : null;

		for (final AddToCartValidationStrategy strategy : addToCartValidationStrategyList)
		{
			final AddToCartResponseData response = strategy.validateAddToCart(addToCartForm.getProductCode(), addToCartForm.getQty(),
					travellerCode, addToCartForm.getTransportOfferingCodes(), travelRouteCode);
			if (!response.isValid())
			{
				model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, response);
				return false;
			}
		}

		final OrderEntryData existingOrderEntry = cartFacade.getOrderEntry(addToCartForm.getProductCode(), travelRouteCode,
				addToCartForm.getTransportOfferingCodes(), travellerCode, true);

		if (null == existingOrderEntry && addToCartForm.getQty() > 0)
		{
			try
			{
				final PriceLevel priceLevel = travelCommercePriceFacade.getPriceLevelInfo(addToCartForm.getProductCode(),
						addToCartForm.getTransportOfferingCodes(), travelRouteCode);
				if (priceLevel == null)
				{
					model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
					return false;
				}
				if (travellerCode != null)
				{
					travelCommercePriceFacade.setPriceAndTaxSearchCriteriaInContext(priceLevel,
							addToCartForm.getTransportOfferingCodes(), travellerFacade.getTravellerFromCurrentCart(travellerCode));
				}
				else
				{
					travelCommercePriceFacade.setPriceAndTaxSearchCriteriaInContext(priceLevel,
							addToCartForm.getTransportOfferingCodes());
				}

				final CartModificationData cartModification = cartFacade.addToCart(addToCartForm.getProductCode(),
						addToCartForm.getQty());
				cartFacade.setOrderEntryType(OrderEntryType.TRANSPORT, cartModification.getEntry().getEntryNumber());

				travelCommercePriceFacade.addPropertyPriceLevelToCartEntry(priceLevel,
						cartModification.getEntry().getProduct().getCode(), cartModification.getEntry().getEntryNumber());
				if (cartModification.getQuantityAdded() >= PRODUCT_QUANTITY)
				{
					addPropertiesToCartEntry(travellerCode, addToCartForm.getOriginDestinationRefNumber(), travelRouteCode,
							addToCartForm.getTransportOfferingCodes(), Boolean.TRUE, AmendStatus.NEW, cartModification);
					//Cart recalculation is needed after persisting pricelevel, traveller details, travel route and transport offering codes against cart entry
					//to make "pricing search criteria in context" logic work. Reason is that, OOTB cart calculation gets triggered while creation of a cart entry,
					//and by that time due to missing travel specific details (pricelevel, traveller details, travel route and transport offering codes), incorrect
					//cart calculation results are produced.
					cartFacade.recalculateCart();
				}
				else if (cartModification.getQuantityAdded() == 0L)
				{
					model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(false,
							"basket.information.quantity.noItemsAdded." + cartModification.getStatusCode(), null));
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.info("Product Code: " + addToCartForm.getProductCode() + ", Quantity: " + addToCartForm.getQty(), ex);
				model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
			}
		}
		else
		{
			try
			{
				final long newQuantity = existingOrderEntry.getQuantity().longValue() + addToCartForm.getQty();

				final CartModificationData cartModification = cartFacade.updateCartEntry(existingOrderEntry.getEntryNumber(),
						newQuantity);

				if (cartModification.getQuantityAdded() == newQuantity)
				{
					LOG.info("Product Code:" + cartModification.getEntry().getProduct().getCode() + "has been update with Quantity:"
							+ cartModification.getQuantityAdded());
				}
			}
			catch (final CommerceCartModificationException ex)
			{
				LOG.warn("Couldn't update product with the entry number: " + existingOrderEntry.getEntryNumber() + ".", ex);
			}
		}
		return true;
	}

	/**
	 * Adds the to cart.
	 *
	 * @param addToCartForms
	 *           the add to cart forms
	 * @param model
	 *           the model
	 * @return the string
	 */
	@RequestMapping(value = "/cart/add/group", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String addToCart(@RequestBody final List<AddToCartForm> addToCartForms, final Model model)
	{
		for (final AddToCartForm addToCartForm : addToCartForms)
		{
			if (!performAddToCart(addToCartForm, model))
			{
				return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
			}
		}

		model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(true, null, null));
		return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
	}

	@RequestMapping(value = "/cart/upgradeBundle", method = RequestMethod.POST)
	public String upgradeBundleInCart(@ModelAttribute("addBundleToCartForm") final AddBundleToCartForm addBundleToCartForm,
			final BindingResult bindingResult, final Model model, final RedirectAttributes redirectModel)
	{
		if (cartFacade.isAmendmentCart())
		{
			return getUpgradeBundleErrorMessage(ADD_BUNDLE_TO_CART_REQUEST_ERROR, redirectModel);
		}

		// populates the addBundleToCartRequestData
		final AddBundleToCartRequestData addBundleToCartRequestData = getAddBundleToCartRequestData(addBundleToCartForm);
		if (Objects.isNull(addBundleToCartRequestData))
		{
			return getUpgradeBundleErrorMessage(ADD_BUNDLE_TO_CART_REQUEST_ERROR, redirectModel);
		}

		// validation
		for (final AddBundleToCartValidationStrategy strategy : addBundleToCartValidationStrategyList)
		{
			final AddToCartResponseData response = strategy.validate(addBundleToCartRequestData);
			if (!response.isValid())
			{
				return getUpgradeBundleErrorMessage(ADD_BUNDLE_TO_CART_REQUEST_ERROR, redirectModel);
			}
		}
		// Remove cart of entries with Origin Destination Ref Num
		final Integer odRefNum = addBundleToCartRequestData.getAddBundleToCartData().get(0).getOriginDestinationRefNumber();
		cartFacade.removeEntriesForOriginDestinationRefNumber(odRefNum);

		// perform add to cart
		final String addBundleToCartErrorMessage = addBundleToCart(addBundleToCartRequestData);
		if (StringUtils.isNotEmpty(addBundleToCartErrorMessage))
		{
			return getUpgradeBundleErrorMessage(addBundleToCartErrorMessage, redirectModel);
		}
		else
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(true, null, odRefNum + 1));
		}

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		return REDIRECT_PREFIX + (StringUtils.equalsIgnoreCase(TravelfacadesConstants.BOOKING_PACKAGE, sessionBookingJourney)
				? TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_PATH : TraveladdonWebConstants.ANCILLARY_ROOT_URL);
	}

	protected String getUpgradeBundleErrorMessage(final String errorMsg, final RedirectAttributes redirectModel)
	{
		GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, errorMsg);

		final String sessionBookingJourney = sessionService
				.getAttribute(TravelacceleratorstorefrontWebConstants.SESSION_BOOKING_JOURNEY);
		return REDIRECT_PREFIX + (StringUtils.equalsIgnoreCase(TravelfacadesConstants.BOOKING_PACKAGE, sessionBookingJourney)
				? TravelacceleratorstorefrontWebConstants.ANCILLARY_EXTRAS_PATH : TraveladdonWebConstants.ANCILLARY_ROOT_URL);
	}

	/**
	 * Creates an AddToCartResponseData
	 *
	 * @param valid
	 * @param errorMessage
	 * @param minOriginDestinationRefNumber
	 * @return the AddToCartResponseData
	 */
	protected AddToCartResponseData createAddToCartResponse(final boolean valid, final String errorMessage,
			final Integer minOriginDestinationRefNumber)
	{
		final AddToCartResponseData response = new AddToCartResponseData();
		response.setValid(valid);
		if (StringUtils.isNotEmpty(errorMessage))
		{
			response.setErrors(Collections.singletonList(errorMessage));
		}
		if (minOriginDestinationRefNumber != null)
		{
			response.setMinOriginDestinationRefNumber(minOriginDestinationRefNumber);
		}
		return response;
	}

	/**
	 * Performs the addBundleToCart for the given addBundleToCartForm and returns the name of the jsp that builds the
	 * JSON response.
	 *
	 * @param addBundleToCartForm
	 *           the addBundleToCartForm
	 * @param model
	 *           the model
	 *
	 * @return String the name of the jsp that builds the JSON response.
	 */
	@RequestMapping(value = "/cart/addBundle", method = RequestMethod.POST, produces = "application/json")
	public String addToCartBundle(@ModelAttribute("addBundleToCartForm") final AddBundleToCartForm addBundleToCartForm,
			final Model model)
	{
		// populates the addBundleToCartRequestData
		final AddBundleToCartRequestData addBundleToCartRequestData = getAddBundleToCartRequestData(addBundleToCartForm);
		if (Objects.isNull(addBundleToCartRequestData))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		if (!fareSearchHashResolver.validItineraryIdentifier(addBundleToCartRequestData,
				addBundleToCartForm.getItineraryPricingInfo().getItineraryIdentifier(),
				addBundleToCartForm.getItineraryPricingInfo().getBundleType()))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		// validation
		for (final AddBundleToCartValidationStrategy strategy : addBundleToCartValidationStrategyList)
		{
			final AddToCartResponseData response = strategy.validate(addBundleToCartRequestData);
			if (!response.isValid())
			{
				model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, response);
				return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
			}
		}

		// Clear cart of entries with OD > current OD
		final Integer odRefNum = addBundleToCartRequestData.getAddBundleToCartData().get(0).getOriginDestinationRefNumber();
		cartFacade.cleanUpCartForMinOriginDestinationRefNumber(odRefNum);

		// perform add to cart
		final String addBundleToCartErrorMessage = addBundleToCart(addBundleToCartRequestData);
		if (StringUtils.isNotEmpty(addBundleToCartErrorMessage))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, addBundleToCartErrorMessage, odRefNum));
		}
		else
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(true, null, odRefNum + 1));
		}
		return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
	}

	/**
	 * Performs the addBundleToCart for the given addBundleToCartRequestData.
	 *
	 * @param addBundleToCartRequestData
	 *           as the addBundleToCartRequestData
	 *
	 * @return a string representing the error message if the addToCartBundle was unsuccessful, an empty string otherwise
	 */
	protected String addBundleToCart(final AddBundleToCartRequestData addBundleToCartRequestData)
	{
		try
		{
			final List<CartModificationData> cartModificationDataList = cartFacade.addBundleToCart(addBundleToCartRequestData);
			for (final CartModificationData cartModification : cartModificationDataList)
			{
				if (cartModification.getQuantityAdded() == 0L)
				{
					return "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode();
				}
				else if (cartModification.getQuantityAdded() < PRODUCT_QUANTITY)
				{
					return "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode();
				}
			}
			// Add fees and discounts
			cartFacade.evaluateCart();

		}
		catch (final CommerceBundleCartModificationException | UnknownIdentifierException ex)
		{
			LOG.info(ex.getMessage(), ex);
			cartFacade.cleanUpCartForMinOriginDestinationRefNumber(
					addBundleToCartRequestData.getAddBundleToCartData().get(0).getOriginDestinationRefNumber());
			return BASKET_ERROR_OCCURRED;
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			cartFacade.cleanUpCartForMinOriginDestinationRefNumber(
					addBundleToCartRequestData.getAddBundleToCartData().get(0).getOriginDestinationRefNumber());
			return BASKET_ERROR_OCCURRED;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Returns the AddBundleToCartRequestData built from the AddBundleToCartForm.
	 *
	 * @param addBundleToCartForm
	 *           as the addBundleToCartForm
	 *
	 * @return the AddBundleToCartRequestData
	 */
	protected AddBundleToCartRequestData getAddBundleToCartRequestData(final AddBundleToCartForm addBundleToCartForm)
	{
		final AddBundleToCartRequestData addBundleToCartRequestData = new AddBundleToCartRequestData();
		addBundleToCartRequestData.setPassengerTypes(addBundleToCartForm.getPassengerTypeQuantityList());

		final String travelRouteCode = addBundleToCartForm.getTravelRouteCode();
		final Integer originDestinationRefNumber;
		try
		{
			originDestinationRefNumber = Integer.parseInt(addBundleToCartForm.getOriginDestinationRefNumber());
		}
		catch (final NumberFormatException ex)
		{
			LOG.error("Cannot parse Origin Destination Ref Number to Integer", ex);
			return null;
		}
		final ItineraryPricingInfoData itineraryPricingInfoData = addBundleToCartForm.getItineraryPricingInfo();

		final List<AddBundleToCartData> addBundleToCartDatas = new ArrayList<>();
		for (final TravelBundleTemplateData bundleData : itineraryPricingInfoData.getBundleTemplates())
		{
			final AddBundleToCartData addBundleToCart = new AddBundleToCartData();
			addBundleToCart.setBundleTemplateId(bundleData.getFareProductBundleTemplateId());
			if (CollectionUtils.isEmpty(bundleData.getFareProducts()))
			{
				LOG.error("No Fare Products associated to the selected Bundle");
				return null;
			}
			addBundleToCart.setProductCode(bundleData.getFareProducts().get(0).getCode());
			addBundleToCart.setTransportOfferings(
					bundleData.getTransportOfferings().stream().map(TransportOfferingData::getCode).collect(Collectors.toList()));
			addBundleToCart.setOriginDestinationRefNumber(originDestinationRefNumber);
			addBundleToCart.setTravelRouteCode(travelRouteCode);
			addBundleToCartDatas.add(addBundleToCart);
		}

		addBundleToCartRequestData.setAddBundleToCartData(addBundleToCartDatas);
		return addBundleToCartRequestData;
	}

	/**
	 * Performs the addTransportBundleToCart for the given addBundleToCartForm and returns the name of the jsp that
	 * builds the JSON response.
	 *
	 * @param addBundleToCartForm
	 *           the addBundleToCartForm
	 * @param model
	 *           the model
	 *
	 * @return String the name of the jsp that builds the JSON response.
	 */
	@RequestMapping(value = "/cart/add-transport-bundle", method = RequestMethod.POST, produces = "application/json")
	public String addTransportBundleToCart(final AddBundleToCartForm addBundleToCartForm, final Model model)
	{
		final AddBundleToCartRequestData addBundleToCartRequestData = getAddBundleToCartRequestData(addBundleToCartForm);
		if (Objects.isNull(addBundleToCartRequestData))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		if (!fareSearchHashResolver.validPackageItineraryIdentifier(addBundleToCartForm.getItineraryPricingInfo()))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		final String addBundleToCartErrorMessage = addBundleToCart(addBundleToCartRequestData);
		if (StringUtils.isNotEmpty(addBundleToCartErrorMessage))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
			cartFacade.removeSessionCart();
		}
		else
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(true, null, null));
		}

		return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
	}

	/**
	 * Removes all cart entries for specific origin destination ref number and adds a newly selected bundle to cart for
	 * the same origin destination.
	 *
	 * @param addBundleToCartForm
	 *           the addBundleToCartForm
	 * @param model
	 *           the model
	 * @return String the name of the jsp that builds the JSON response.
	 */
	@RequestMapping(value = "/cart/package-change-transport", method = RequestMethod.POST, produces = "application/json")
	public String changeTransportBundle(final AddBundleToCartForm addBundleToCartForm, final Model model)
	{
		final AddBundleToCartRequestData addBundleToCartRequestData = getAddBundleToCartRequestData(addBundleToCartForm);
		if (Objects.isNull(addBundleToCartRequestData))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		if (!fareSearchHashResolver.validPackageItineraryIdentifier(addBundleToCartForm.getItineraryPricingInfo()))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, ADD_BUNDLE_TO_CART_REQUEST_ERROR, null));
			return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
		}

		// validation
		for (final AddBundleToCartValidationStrategy strategy : addPackageBundleToCartValidationStrategyList)
		{
			final AddToCartResponseData response = strategy.validate(addBundleToCartRequestData);
			if (!response.isValid())
			{
				model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, response);
				return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
			}
		}

		cartFacade.removeEntriesForOriginDestinationRefNumber(
				addBundleToCartRequestData.getAddBundleToCartData().get(0).getOriginDestinationRefNumber());

		final String addBundleToCartErrorMessage = addBundleToCart(addBundleToCartRequestData);
		if (StringUtils.isNotEmpty(addBundleToCartErrorMessage))
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
			cartFacade.removeSessionCart();
		}
		else
		{
			model.addAttribute(ADD_BUNDLE_TO_CART_RESPONSE, createAddToCartResponse(true, null, null));
		}

		return TraveladdonControllerConstants.Views.Pages.FareSelection.AddBundleToCartResponse;
	}

	/**
	 * Adds the selected accommodations to cart.
	 *
	 * @param addRemoveAccommodations
	 *           the add remove accommodations
	 * @param model
	 *           the model
	 * @return the string
	 */
	@RequestMapping(value =
	{ "/cart/addremove/accommodations", "/manage-booking/ancillary/cart/addremove/accommodations" }, method =
	{ RequestMethod.POST,
			RequestMethod.GET }, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String addSelectedAccommodationsToCart(
			@RequestBody final List<AddRemoveAccommodation> addRemoveAccommodations, final Model model)
	{
		final List<AddRemoveAccommodation> removeAccommodations = addRemoveAccommodations.stream().filter(
				addRemoveAccommodation -> StringUtils.isNotEmpty(addRemoveAccommodation.getPreviousSelectedAccommodationUid()))
				.collect(Collectors.toList());

		final List<AddRemoveAccommodation> addAccommodations = addRemoveAccommodations.stream()
				.filter(addRemoveAccommodation -> StringUtils.isNotEmpty(addRemoveAccommodation.getAccommodationUid()))
				.collect(Collectors.toList());

		for (final AddRemoveAccommodation removeAccommodation : removeAccommodations)
		{
			final boolean isRemoved = accommodationCartFacade.removeSelectedAccommodationFromCart(
					removeAccommodation.getPreviousSelectedAccommodationUid(), removeAccommodation.getTransportOfferingCode(),
					removeAccommodation.getTravellerCode(), removeAccommodation.getTravelRoute());

			if (!isRemoved)
			{
				model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
				return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
			}
		}

		for (final AddRemoveAccommodation addAccommodation : addAccommodations)
		{
			final boolean isBooked = accommodationCartFacade.addSelectedAccommodationToCart(
					addAccommodation.getAccommodationUid(), addAccommodation.getTransportOfferingCode(),
					addAccommodation.getTravellerCode(), addAccommodation.getOriginDestinationRefNo(),
					addAccommodation.getTravelRoute());

			if (!isBooked)
			{
				model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(false, BASKET_ERROR_OCCURRED, null));
				return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
			}
		}

		model.addAttribute(ADD_PRODUCT_TO_CART_RESPONSE, createAddToCartResponse(true, null, null));
		return TraveladdonControllerConstants.Views.Pages.Ancillary.AddProductToCartResponse;
	}

	/**
	 * Adds the properties to cart entry.
	 *
	 * @param travellerCode
	 *           the traveller code
	 * @param originDestinationRefNo
	 *           the origin destination ref no
	 * @param travelRoute
	 *           the travel route
	 * @param transportOfferingCodes
	 *           the transport offering codes
	 * @param active
	 *           the active
	 * @param amendStatus
	 *           the amend status
	 * @param cartModification
	 *           the cart modification
	 */
	protected void addPropertiesToCartEntry(final String travellerCode, final int originDestinationRefNo, final String travelRoute,
			final List<String> transportOfferingCodes, final Boolean active, final AmendStatus amendStatus,
			final CartModificationData cartModification)
	{
		final String productCode = cartModification.getEntry().getProduct().getCode();
		final String addToCartCriteria = travelRestrictionFacade.getAddToCartCriteria(productCode);

		cartFacade.addPropertiesToCartEntry(productCode, cartModification.getEntry().getEntryNumber(), transportOfferingCodes,
				travelRoute, originDestinationRefNo, travellerCode, active, amendStatus, addToCartCriteria);
	}

	/**
	 * Method to add voucher to cart
	 *
	 * @param voucherCode
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/cart/voucher/redeem", method =
	{ RequestMethod.POST })
	public String redeemVoucher(@RequestParam("voucherCode") final String voucherCode, final Model model)
	{
		try
		{
			cartFacade.applyVoucher(voucherCode);
			model.addAttribute(VOUCHER_REDEEM_SUCCESS, true);
		}
		catch (final VoucherOperationException e)
		{
			model.addAttribute(ERROR_MSG_TYPE, e.getMessage());
		}
		return TraveladdonControllerConstants.Views.Pages.Cart.VoucherJSONResponse;
	}

	/**
	 * Method to remove voucher from cart
	 *
	 * @param voucherCode
	 * @param model
	 * @return json page
	 */
	@RequestMapping(value = "/cart/voucher/release", method =
	{ RequestMethod.POST })
	public String releaseVoucher(@RequestParam("voucherCode") final String voucherCode, final Model model)
	{
		try
		{
			cartFacade.removeVoucher(voucherCode);
			model.addAttribute(VOUCHER_RELEASE_SUCCESS, true);
		}
		catch (final VoucherOperationException e)
		{
			model.addAttribute(ERROR_MSG_TYPE, e.getMessage());
		}
		return TraveladdonControllerConstants.Views.Pages.Cart.VoucherJSONResponse;
	}

	/**
	 * @return TravelCartFacade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * @param cartFacade
	 */
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	/**
	 * @return TravellerFacade
	 */
	protected TravellerFacade getTravellerFacade()
	{
		return travellerFacade;
	}

	/**
	 * @param travellerFacade
	 */
	public void setTravellerFacade(final TravellerFacade travellerFacade)
	{
		this.travellerFacade = travellerFacade;
	}

	/**
	 * @return L10NService
	 */
	protected L10NService getL10NService()
	{
		return l10NService;
	}

	/**
	 * @param l10nService
	 */
	public void setL10NService(final L10NService l10nService)
	{
		l10NService = l10nService;
	}

	/**
	 * @return TravelRestrictionStrategy
	 */
	protected TravelRestrictionStrategy getTravelRestrictionStrategy()
	{
		return travelRestrictionStrategy;
	}

	/**
	 * @param travelRestrictionStrategy
	 */
	public void setTravelRestrictionStrategy(final TravelRestrictionStrategy travelRestrictionStrategy)
	{
		this.travelRestrictionStrategy = travelRestrictionStrategy;
	}

	/**
	 * @return AddAccommodationToCartStrategy
	 */
	protected AddAccommodationToCartStrategy getAddAccommodationToCartStrategy()
	{
		return addAccommodationToCartStrategy;
	}

	/**
	 * @param addAccommodationToCartStrategy
	 */
	public void setAddAccommodationToCartStrategy(final AddAccommodationToCartStrategy addAccommodationToCartStrategy)
	{
		this.addAccommodationToCartStrategy = addAccommodationToCartStrategy;
	}

	/**
	 * @return ConfiguredAccommodationFacade
	 */
	protected ConfiguredAccommodationFacade getConfiguredAccommodationFacade()
	{
		return configuredAccommodationFacade;
	}

	/**
	 * @param configuredAccommodationFacade
	 */
	public void setConfiguredAccommodationFacade(final ConfiguredAccommodationFacade configuredAccommodationFacade)
	{
		this.configuredAccommodationFacade = configuredAccommodationFacade;
	}

}
