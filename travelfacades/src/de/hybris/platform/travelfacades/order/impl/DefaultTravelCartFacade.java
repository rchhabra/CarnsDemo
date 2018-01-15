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

package de.hybris.platform.travelfacades.order.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartData;
import de.hybris.platform.commercefacades.travel.AddBundleToCartRequestData;
import de.hybris.platform.commercefacades.travel.PassengerTypeQuantityData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.order.PaymentOptionData;
import de.hybris.platform.commercefacades.travel.order.PaymentTransactionData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.subscriptionfacades.order.impl.DefaultSubscriptionCartFacade;
import de.hybris.platform.travelfacades.constants.TravelfacadesConstants;
import de.hybris.platform.travelfacades.facades.BookingFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.order.strategies.PopulatePropertyMapStrategy;
import de.hybris.platform.travelfacades.promotion.TravelPromotionsFacade;
import de.hybris.platform.travelrulesengine.services.TravelRulesService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.CommerceBundleCartModificationException;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.TravelCommerceCartService;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TransportOfferingService;
import de.hybris.platform.travelservices.services.TravelRouteService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * The class is responsible for updating certain properties information like transport offering code specific for a
 * travel bundle in the cart.
 */
public class DefaultTravelCartFacade extends DefaultSubscriptionCartFacade implements TravelCartFacade
{

	/**
	 * This constant value dictates the quantity of the fare product to be added in the cart.
	 */
	protected static final long MINIMUM_PRODUCT_QUANTITY = 1;
	/**
	 * The constant MAX_MESSAGE_LENGTH.
	 */
	protected static final int MAX_MESSAGE_LENGTH = 30;
	private static final Logger LOG = Logger.getLogger(DefaultTravelCartFacade.class);
	private TravelCommerceCartService travelCommerceCartService;
	private TravellerService travellerService;
	private TravelRouteService travelRouteService;
	private TransportOfferingService transportOfferingService;
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private AccommodationMapService accommodationMapService;
	private TravelCartService travelCartService;
	private VoucherFacade voucherFacade;
	private TravelPromotionsFacade travelPromotionsFacade;
	private TravellerFacade travellerFacade;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private SessionService sessionService;
	private BookingService bookingService;
	private TravelRulesService travelRulesService;
	private Map<String, PopulatePropertyMapStrategy> populateCartEntryPropertyStrategyMap;
	private CommonI18NService commonI18NService;
	private Converter<PaymentOptionInfo, PaymentOptionData> paymentOptionConverter;
	private BookingFacade bookingFacade;
	private BundleCartFacade bundleCartFacade;

	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(code);
		final CartModel cartModel = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setQuantity(quantity);
		parameter.setProduct(product);
		parameter.setUnit(product.getUnit());
		parameter.setCreateNewEntry(true);

		final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

		return getCartModificationConverter().convert(modification);
	}

	/**
	 * Method to add additional properties like Transport offering Code to a cart entry of a particular bundle using the
	 * default addToCartCriteria
	 *
	 * @param productCode
	 * 		productCode in Cart Entry
	 * @param bundleNo
	 * 		bundleNo in Cart Entry
	 * @param travelRouteCode
	 * 		travel Route Code for Cart Entry
	 * @param originDestinationRefNumber
	 * 		origin destination ref number for Cart Entry
	 * @param travellerUid
	 * 		traveller code unique in the Cart
	 * @param active
	 * 		flag to mark the entry active or not
	 * @param amendStatus
	 * 		status of the amendment for the current cart
	 * @deprecated Deprecated since version 2.0. Replaced by
	 * {@link #addPropertiesToCartEntry(String, int, List, String, int, String, Boolean, AmendStatus, String)}
	 */
	@Deprecated
	protected void addPropertiesToCartEntryForBundle(final String productCode, final int bundleNo, final String travelRouteCode,
			final int originDestinationRefNumber, final String travellerUid, final Boolean active, final AmendStatus amendStatus)
	{
		addPropertiesToCartEntryForBundle(productCode, bundleNo, travelRouteCode, originDestinationRefNumber, travellerUid, active,
				amendStatus, null);
	}

	/**
	 * Method to add additional properties like Transport offering Code to a cart entry of a particular bundle
	 *
	 * @param productCode
	 * 		productCode in Cart Entry
	 * @param bundleNo
	 * 		bundleNo in Cart Entry
	 * @param travelRouteCode
	 * 		travel Route Code for Cart Entry
	 * @param originDestinationRefNumber
	 * 		origin destination ref number for Cart Entry
	 * @param travellerUid
	 * 		traveller code unique in the Cart
	 * @param active
	 * 		flag to mark the entry active or not
	 * @param amendStatus
	 * 		status of the amendment for the current cart
	 * @param addToCartCriteria
	 * 		addToCartCriteria of the product to be added to the cart
	 */
	protected void addPropertiesToCartEntryForBundle(final String productCode, final int bundleNo, final String travelRouteCode,
			final int originDestinationRefNumber, final String travellerUid, final Boolean active, final AmendStatus amendStatus,
			final String addToCartCriteria)
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);

		final List<TravellerModel> travellersList = new ArrayList<>();
		final TravellerModel traveller = getTravellerService().getTravellerFromCurrentCartByUID(travellerUid, cartModel);
		travellersList.add(traveller);

		final Map<String, Object> propertiesMap = populatePropertiesMap(addToCartCriteria, travelRouteCode,
				originDestinationRefNumber, null, travellersList, active, amendStatus);
		getTravelCommerceCartService().addPropertiesToCartEntryForBundle(cartModel, bundleNo, product, propertiesMap);

	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated Deprecated since version 2.0. Use
	 * {@link #addPropertiesToCartEntry(String, int, List, String, int, String, Boolean, AmendStatus, String)} instead.
	 */
	@Override
	@Deprecated
	public void addPropertiesToCartEntry(final String productCode, final int entryNo, final List<String> transportOfferingCodes,
			final String travelRouteCode, final int originDestinationRefNumber, final String travellerCode, final Boolean active,
			final AmendStatus amendStatus)
	{
		addPropertiesToCartEntry(productCode, entryNo, transportOfferingCodes, travelRouteCode, originDestinationRefNumber,
				travellerCode, active, amendStatus, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPropertiesToCartEntry(final String productCode, final int entryNo, final List<String> transportOfferingCodes,
			final String travelRouteCode, final int originDestinationRefNumber, final String travellerCode, final Boolean active,
			final AmendStatus amendStatus, final String addToCartCriteriaType)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);

		final List<TravellerModel> travellersList = new ArrayList<>();
		final TravellerModel traveller = getTravellerService().getTravellerFromCurrentCart(travellerCode);
		travellersList.add(traveller);

		final Map<String, Object> propertiesMap = populatePropertiesMap(addToCartCriteriaType, travelRouteCode,
				originDestinationRefNumber, transportOfferingCodes, travellersList, active, amendStatus);
		getTravelCommerceCartService().addPropertiesToCartEntry(cartModel, entryNo, product, propertiesMap);

	}

	@Override
	public void addPropertiesToCartEntry(final String productCode, final int entryNo, final Map<String, Object> params)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final ProductModel product = getProductService().getProductForCode(productCode);
		if (!params.isEmpty())
		{
			getTravelCommerceCartService().addPropertiesToCartEntry(cartModel, entryNo, product, params);
		}
	}

	@Override
	public void addSelectedAccommodationToCart(final String transportOfferingCode, final String travellerCode,
			final String configuredAccommodationUid)
	{
		final ConfiguredAccommodationModel configuredAccommodation = accommodationMapService
				.getAccommodation(configuredAccommodationUid);
		travelCommerceCartService.addSelectedAccommodationToCart(transportOfferingCode, travellerCode, configuredAccommodation);
	}

	@Override
	public void removeSelectedAccommodationFromCart(final String transportOfferingCode, final String travellerCode,
			final String configuredAccommodationUid)
	{
		travelCommerceCartService
				.removeSelectedAccommodationFromCart(transportOfferingCode, travellerCode, configuredAccommodationUid);
	}

	/**
	 * Populate properties map map.
	 *
	 * @param travelRouteCode
	 * 		the travel route code
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param travellersList
	 * 		the travellers list
	 * @param active
	 * 		the active
	 * @param amendStatus
	 * 		the amend status
	 * @return map
	 * @deprecated Deprecated since version 2.0. Replaced by {@link #populatePropertiesMap(String, String, int, List, List, Boolean, AmendStatus)}
	 */
	@Deprecated
	protected Map<String, Object> populatePropertiesMap(final String travelRouteCode, final int originDestinationRefNumber,
			final List<String> transportOfferingCodes, final List<TravellerModel> travellersList, final Boolean active,
			final AmendStatus amendStatus)
	{
		return populatePropertiesMap(null, travelRouteCode, originDestinationRefNumber, transportOfferingCodes, travellersList,
				active, amendStatus);
	}

	/**
	 * Populate properties map map.
	 *
	 * @param addToCartCriteriaType
	 * 		the add to cart criteria type
	 * @param travelRouteCode
	 * 		the travel route code
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param travellersList
	 * 		the travellers list
	 * @param active
	 * 		the active
	 * @param amendStatus
	 * 		the amend status
	 * @return the map
	 */
	protected Map<String, Object> populatePropertiesMap(final String addToCartCriteriaType, final String travelRouteCode,
			final int originDestinationRefNumber, final List<String> transportOfferingCodes,
			final List<TravellerModel> travellersList, final Boolean active, final AmendStatus amendStatus)
	{
		String addToCartCriteria = addToCartCriteriaType;
		if (StringUtils.isEmpty(addToCartCriteria))
		{
			addToCartCriteria = TravelfacadesConstants.DEFAULT_ADD_TO_CART_CRITERIA;
		}

		final PopulatePropertyMapStrategy strategy = getPopulateCartEntryPropertyStrategyMap().get(addToCartCriteria);
		return strategy
				.populatePropertiesMap(travelRouteCode, originDestinationRefNumber, transportOfferingCodes, travellersList, active,
						amendStatus);
	}

	@Override
	public OrderEntryData getOrderEntry(final String productCode, final String travelRouteCode,
			final List<String> transportOfferingCodes, final String travellerCode, final boolean bundleNoCheckRequired)
	{
		final List<String> travellerCodes = new ArrayList<>();
		if (StringUtils.isNotBlank(travellerCode))
		{
			travellerCodes.add(travellerCode);
		}
		final AbstractOrderEntryModel orderEntryModel = getBookingService()
				.getOrderEntry(getCartService().getSessionCart(), productCode, travelRouteCode, transportOfferingCodes,
						travellerCodes, bundleNoCheckRequired);
		if (null != orderEntryModel)
		{
			return getOrderEntryConverter().convert(orderEntryModel);
		}
		return null;
	}

	@Override
	public Boolean isAmendmentCart()
	{
		if (!getTravelCartService().hasSessionCart())
		{
			return Boolean.FALSE;
		}
		final CartModel sessionCart = getTravelCartService().getSessionCart();
		return sessionCart != null ? (sessionCart.getOriginalOrder() != null) : Boolean.FALSE;
	}

	@Override
	public Boolean isAdditionalSecurityActive()
	{
		if (!getTravelCartService().hasSessionCart())
		{
			return Boolean.FALSE;
		}
		final CartModel sessionCart = getTravelCartService().getSessionCart();
		return sessionCart != null ? sessionCart.getAdditionalSecurity() : Boolean.FALSE;
	}

	@Override
	public void setAdditionalSecurity(final Boolean additionalSecurity)
	{
		getTravelCartService().setAdditionalSecurity(additionalSecurity);
	}

	@Override
	public void removeDeliveryAddress()
	{
		getTravelCartService().removeDeliveryAddress();
	}

	@Override
	public String getOriginalOrderCode()
	{
		if (!getTravelCartService().hasSessionCart())
		{
			return StringUtils.EMPTY;
		}

		final OrderModel orderModel = getTravelCartService().getSessionCart().getOriginalOrder();
		return (orderModel == null) ? StringUtils.EMPTY : orderModel.getCode();
	}

	@Override
	public void applyVoucher(final String voucherCode) throws VoucherOperationException
	{
		getVoucherFacade().applyVoucher(voucherCode);
		// The OOTB apply voucher functionality removes all the discounts and promotions from cart.
		// So we recalculate cart which will re-apply all the promotions and discounts.
		this.recalculateCart();
	}

	@Override
	public void removeVoucher(final String voucherCode) throws VoucherOperationException
	{
		getVoucherFacade().releaseVoucher(voucherCode);
		// The OOTB release voucher functionality removes all the discounts and promotions from cart.
		// So we recalculate cart which will re-apply all the promotions and discounts.
		this.recalculateCart();
	}

	@Override
	public void recalculateCart()
	{
		final CartModel sessionCart = getCartService().getSessionCart();
		try
		{
			getTravelCommerceCartService().recalculateCart(sessionCart);
		}
		catch (final CalculationException e)
		{
			LOG.error("Recalculation for cart : " + sessionCart.getCode() + " , reason : " + e.getMessage(), e);
		}

	}


	/**
	 * This method would trigger the adding bundle to cart.
	 *
	 * @param addBundleToCartRequestData
	 * @return list of CartModificationData
	 * @deprecated since 4.0 use {@link #addBundleToCart(AddBundleToCartRequestData)} instead
	 */
	@Deprecated
	@Override
	public List<CartModificationData> addToCartBundle(final AddBundleToCartRequestData addBundleToCartRequestData)
			throws CommerceCartModificationException
	{

		final List<AddBundleToCartData> addBundleToCartDataList = addBundleToCartRequestData.getAddBundleToCartData();
		final List<PassengerTypeQuantityData> passengerTypeQuantityDataList = addBundleToCartRequestData.getPassengerTypes();

		Map<String, PriceLevel> productPriceLevelMap = null;
		try
		{
			productPriceLevelMap = getProductPriceLevels(addBundleToCartDataList);
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.warn("Prices not found for the bundle products: " + e.getMessage());
			throw new CommerceBundleCartModificationException(e.getMessage(), e);
		}

		final List<CartModificationData> cartModifications = new ArrayList<>();
		int bundleNo = -1;
		final String currentCartCode = getCurrentCartCode();

		for (final PassengerTypeQuantityData passengerData : passengerTypeQuantityDataList)
		{
			for (int count = 1; count <= passengerData.getQuantity(); count++)
			{
				final Iterator<AddBundleToCartData> addBundleToCartDataIterator = addBundleToCartDataList.iterator();
				final StringBuilder travellerCode = new StringBuilder(passengerData.getPassengerType().getCode()).append(count);
				TravellerData travellerData = getTravellerData(travellerCode.toString());
				if (travellerData == null)
				{
					travellerData = getTravellerFacade()
							.createTraveller(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER, passengerData.getPassengerType().getCode(),
									travellerCode.toString(), count, currentCartCode, getCartCode(currentCartCode));
				}

				while (addBundleToCartDataIterator.hasNext())
				{
					final AddBundleToCartData addBundleToCartData = addBundleToCartDataIterator.next();
					final String productId = addBundleToCartData.getProductCode();
					final int originDestinationRefNumber = addBundleToCartData.getOriginDestinationRefNumber();

					final PriceLevel priceLevel = productPriceLevelMap.get(productId + "_" + originDestinationRefNumber);
					try
					{
						bundleNo = addProduct(productId, MINIMUM_PRODUCT_QUANTITY, bundleNo, addBundleToCartData, travellerData, false,
								priceLevel, Boolean.TRUE, AmendStatus.NEW, cartModifications);

					}
					catch (final CommerceCartModificationException ex)
					{
						LOG.warn("Couldn't add product of code " + productId + " to cart.", ex);
						throw new CommerceBundleCartModificationException(ex.getMessage(), ex);
					}
					bundleNo = Math.abs(bundleNo + 1) * -1;
				} // while
				bundleNo = Math.abs(bundleNo + 1) * -1;
			} // for
		}
		addPerLegBundleProductToCart(addBundleToCartDataList, bundleNo, cartModifications);

		cartModifications
				.forEach(modification -> setOrderEntryType(OrderEntryType.TRANSPORT, modification.getEntry().getEntryNumber()));
		return cartModifications;
	}

	@Override
	public List<CartModificationData> addBundleToCart(final AddBundleToCartRequestData addBundleToCartRequestData)
			throws CommerceCartModificationException
	{
		final List<AddBundleToCartData> addBundleToCartDataList = addBundleToCartRequestData.getAddBundleToCartData();
		final List<PassengerTypeQuantityData> passengerTypeQuantityDataList = addBundleToCartRequestData.getPassengerTypes();

		final Map<String, PriceLevel> productPriceLevelMap;
		try
		{
			productPriceLevelMap = getProductPriceLevels(addBundleToCartDataList);
		}
		catch (final CommerceCartModificationException e)
		{
			LOG.warn("Prices not found for the bundle products: " + e.getMessage());
			throw new CommerceBundleCartModificationException(e.getMessage(), e);
		}

		final List<CartModificationData> totalCartModifications = new ArrayList<>();
		List<CartModificationData> bundleModifications = new ArrayList<>();
		final String currentCartCode = getCurrentCartCode();

		for (final PassengerTypeQuantityData passengerData : passengerTypeQuantityDataList)
		{
			for (int count = 1; count <= passengerData.getQuantity(); count++)
			{

				final StringBuilder travellerCode = new StringBuilder(passengerData.getPassengerType().getCode()).append(count);
				TravellerData travellerData = getTravellerData(travellerCode.toString());
				if (travellerData == null)
				{
					travellerData = getTravellerFacade()
							.createTraveller(TravelfacadesConstants.TRAVELLER_TYPE_PASSENGER, passengerData.getPassengerType().getCode(),
									travellerCode.toString(), count, currentCartCode, getCartCode(currentCartCode));
				}

				for (final AddBundleToCartData addBundleToCartData : addBundleToCartDataList)
				{
					final String productId = addBundleToCartData.getProductCode();
					final int originDestinationRefNumber = addBundleToCartData.getOriginDestinationRefNumber();

					final PriceLevel priceLevel = productPriceLevelMap.get(productId + "_" + originDestinationRefNumber);
					try
					{
						bundleModifications = addProduct(productId, MINIMUM_PRODUCT_QUANTITY,
								ConfigurableBundleServicesConstants.NEW_BUNDLE, addBundleToCartData, travellerData, priceLevel,
								Boolean.TRUE, AmendStatus.NEW);
						if (CollectionUtils.isNotEmpty(bundleModifications) && bundleModifications.stream().allMatch(Objects::nonNull))
						{
							bundleModifications.forEach(
									modification -> setOrderEntryType(OrderEntryType.TRANSPORT, modification.getEntry().getEntryNumber()));
							recalculateCart();
							totalCartModifications.addAll(bundleModifications);
						}
					}
					catch (final CommerceCartModificationException ex)
					{
						LOG.warn("Couldn't add product of code " + productId + " to cart.", ex);
						throw new CommerceBundleCartModificationException(ex.getMessage(), ex);
					}
				}

			}
		}

		bundleModifications = addPerLegBundleProductToCart(addBundleToCartDataList);
		if (CollectionUtils.isNotEmpty(bundleModifications))
		{
			bundleModifications
					.forEach(modification -> setOrderEntryType(OrderEntryType.TRANSPORT, modification.getEntry().getEntryNumber()));
			totalCartModifications.addAll(bundleModifications);
		}
		return totalCartModifications;
	}

	protected List<CartModificationData> addPerLegBundleProductToCart(final List<AddBundleToCartData> addBundleToCartDataList)
			throws CommerceCartModificationException
	{
		final List<CommerceCartModification> modifications = new ArrayList<>();
		for (final AddBundleToCartData addBundleToCartData : addBundleToCartDataList)
		{
			setAddToCartParametersInContext(addBundleToCartData.getTransportOfferings(), addBundleToCartData.getTravelRouteCode(),
					null, null, addBundleToCartData.getOriginDestinationRefNumber(), Boolean.TRUE, AmendStatus.NEW);
			modifications
					.addAll(getTravelCommerceCartService().addPerLegBundleProductToCart(addBundleToCartData.getBundleTemplateId()));
		}
		return Converters.convertAll(modifications, getCartModificationConverter());
	}

	protected List<CartModificationData> addProduct(final String productId, final long quantity, final int bundleNo,
			final AddBundleToCartData addBundleToCartData, final TravellerData travellerData, final PriceLevel priceLevel,
			final Boolean active, final AmendStatus amendStatus) throws CommerceCartModificationException
	{
		final String bundleTemplateId = addBundleToCartData.getBundleTemplateId();
		final List<String> transportOfferingCodes = addBundleToCartData.getTransportOfferings();
		final String travelRouteCode = addBundleToCartData.getTravelRouteCode();
		final int originDestinationRefNumber = addBundleToCartData.getOriginDestinationRefNumber();

		final List<CartModificationData> cartModifications = new ArrayList<>();

		getTravelCommercePriceFacade().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, travellerData);
		setAddToCartParametersInContext(transportOfferingCodes, travelRouteCode, travellerData, priceLevel,
				originDestinationRefNumber, active, amendStatus);
		final Map<String, Object> addBundleToCartParamsMap = getSessionService()
				.getAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP);
		if (bundleNo < 0)
		{
			cartModifications.add(getBundleCartFacade().startBundle(bundleTemplateId, productId, quantity));
			if (CollectionUtils.isNotEmpty(cartModifications))
			{
				if (Objects.nonNull(addBundleToCartParamsMap))
				{
					getTravelCommerceCartService().setEntryAsCalculatedAndInitializePriceLevel(
							cartModifications.get(cartModifications.size() - 1).getEntry().getEntryNumber());
				}
			}
			if (CollectionUtils.isNotEmpty(cartModifications) && cartModifications.stream().allMatch(Objects::nonNull))
			{
				cartModifications.addAll(getCartModificationConverter().convertAll(getTravelCommerceCartService()
						.addAutoPickProductsToCart(getProductService().getProductForCode(productId), bundleTemplateId,
								cartModifications.get(cartModifications.size() - 1).getEntry().getEntryGroupNumbers().stream().findAny()
										.get())));
			}
		}
		else
		{
			cartModifications.add(getBundleCartFacade().addToCart(productId, quantity, bundleNo));
			if (CollectionUtils.isNotEmpty(cartModifications))
			{
				if (Objects.nonNull(addBundleToCartParamsMap))
				{
					getTravelCommerceCartService().setEntryAsCalculatedAndInitializePriceLevel(
							cartModifications.get(cartModifications.size() - 1).getEntry().getEntryNumber());
				}
			}
		}

		return cartModifications;
	}

	/**
	 * Returns the code that is assigned to the version ID field in the {@link TravellerModel}. In the current session cart does not
	 * have any original order it is returning null.
	 *
	 * @param currentCartCode
	 * @return
	 */
	protected String getCartCode(final String currentCartCode)
	{
		if (StringUtils.isNotEmpty(getOriginalOrderCode()))
		{
			return currentCartCode;
		}
		return null;
	}

	/**
	 * Returns the {@link TravellerData} corresponding to the given travellerCode taken from the existing cart entries.
	 *
	 * @param travellerCode
	 * 		as the traveller code
	 * @return the TravellerData if exists, null otherwise
	 */
	protected TravellerData getTravellerData(final String travellerCode)
	{
		final List<TravellerData> travellerDataList = getTravellerFacade().getTravellersForCartEntries();

		if (CollectionUtils.isNotEmpty(travellerDataList))
		{
			final Optional<TravellerData> optionalTravellerData = travellerDataList.stream()
					.filter(traveller -> traveller.getLabel().equals(travellerCode)).findFirst();

			if (optionalTravellerData.isPresent())
			{
				return optionalTravellerData.get();
			}
		}
		return null;
	}

	/**
	 * Add auto-pick PER_LEG products to the bundle in the cart.
	 *
	 * @param addBundleToCartDataList
	 * 		the add bundle to cart data list
	 * @param bundleNo
	 * 		the bundle no
	 * @param cartModifications
	 * 		the cart modifications
	 * @return the updated bundleNo
	 * @throws CommerceBundleCartModificationException
	 * 		the commerce bundle cart modification exception
	 */
	protected int addPerLegBundleProductToCart(final List<AddBundleToCartData> addBundleToCartDataList, final int bundleNo,
			final List<CartModificationData> cartModifications) throws CommerceBundleCartModificationException
	{
		int bundleNumber = bundleNo;
		for (final AddBundleToCartData addBundleToCartData : addBundleToCartDataList)
		{
			setAddToCartParametersInContext(addBundleToCartData.getTransportOfferings(), addBundleToCartData.getTravelRouteCode(),
					null, null, addBundleToCartData.getOriginDestinationRefNumber(), Boolean.TRUE, AmendStatus.NEW);
			final List<CommerceCartModification> modifications = getTravelCommerceCartService()
					.addPerLegBundleProductToCart(addBundleToCartData.getBundleTemplateId(), bundleNumber, cartModifications);
			cartModifications.addAll(Converters.convertAll(modifications, getCartModificationConverter()));

			bundleNumber = updateBundleNo(bundleNumber, modifications);
		}
		return bundleNumber;
	}

	/**
	 * Updates the bundleNo based on the last modifications
	 *
	 * @param bundleNo
	 * 		the bundle no
	 * @param modifications
	 * 		the modifications
	 * @return the updated bundleNo
	 */
	protected int updateBundleNo(final int bundleNo, final List<CommerceCartModification> modifications)
	{
		int bundleNumber = bundleNo;
		if (CollectionUtils.isNotEmpty(modifications))
		{
			bundleNumber = modifications.get(modifications.size() - 1).getEntry().getBundleNo();
			bundleNumber = Math.abs(bundleNumber + 1) * -1;
		}
		return bundleNumber;
	}

	@Override
	public boolean isProductAvailable(final String productCode, final List<String> transportOfferingCodes, final Long quantity)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final List<TransportOfferingModel> transportOfferingModels = transportOfferingCodes.stream()
				.map(transportOfferingCode -> getTransportOfferingService().getTransportOffering(transportOfferingCode))
				.collect(Collectors.<TransportOfferingModel>toList());

		final Long quantityToOffer = getQuantityToOffer(productModel, transportOfferingModels, quantity);

		return transportOfferingModels.stream().allMatch(transportOfferingModel ->
		{
			final Long availableStock = getTravelCartService().getAvailableStock(productModel, transportOfferingModel);
			if (availableStock == null || availableStock.longValue() >= quantityToOffer)
			{
				return true;
			}
			return false;
		});

	}

	/**
	 * This method calculates the quantity of a product in offer to compare against the available stock.
	 *
	 * @param productModel
	 * 		the product model
	 * @param transportOfferingModels
	 * 		the transport offering models
	 * @param quantity
	 * 		the quantity
	 * @return a Long
	 */
	protected Long getQuantityToOffer(final ProductModel productModel, final List<TransportOfferingModel> transportOfferingModels,
			final Long quantity)
	{
		final List<CartEntryModel> cartEntries = getTravelCartService()
				.getEntriesForProduct(getTravelCartService().getSessionCart(), productModel);
		return quantity + cartEntries.stream().filter(cartEntryModel -> transportOfferingModels
				.containsAll(cartEntryModel.getTravelOrderEntryInfo().getTransportOfferings()))
				.mapToLong(cartEntryModel -> cartEntryModel.getQuantity()).sum();
	}

	/**
	 * Method to populate price levels for all the products
	 *
	 * @param addBundleToCartDataList
	 * 		the add bundle to cart data list
	 * @return product price levels
	 * @throws CommerceCartModificationException
	 * 		the commerce cart modification exception
	 */
	protected Map<String, PriceLevel> getProductPriceLevels(final List<AddBundleToCartData> addBundleToCartDataList)
			throws CommerceCartModificationException
	{
		final Map<String, PriceLevel> productPriceLevelMap = new HashMap<>();
		final Iterator<AddBundleToCartData> fareBundleDataItr = addBundleToCartDataList.iterator();
		while (fareBundleDataItr.hasNext())
		{
			final AddBundleToCartData addBundleToCartData = fareBundleDataItr.next();
			final String productId = addBundleToCartData.getProductCode();
			final int originDestinationRefNumber = addBundleToCartData.getOriginDestinationRefNumber();

			final PriceLevel priceLevel = getTravelCommercePriceFacade()
					.getPriceLevelInfo(productId, addBundleToCartData.getTransportOfferings(),
							addBundleToCartData.getTravelRouteCode());
			if (priceLevel == null)
			{
				throw new CommerceCartModificationException("Price not available for the product " + productId);
			}
			productPriceLevelMap.put(productId + "_" + originDestinationRefNumber, priceLevel);
		}
		return productPriceLevelMap;
	}

	/**
	 * Method to add product to cart. This will include setting up the params required for pricing and to set travel
	 * specific params in the order entry.
	 *
	 * @param code
	 * 		the code
	 * @param qty
	 * 		the qty
	 * @param bundleNo
	 * 		the bundle no
	 * @param addBundleToCartData
	 * 		the add bundle to cart data
	 * @param travellerData
	 * 		the traveller data
	 * @param removeCurrentProducts
	 * 		the remove current products
	 * @param priceLevel
	 * 		the price level
	 * @param active
	 * 		the active
	 * @param amendStatus
	 * 		the amend status
	 * @param cartModifications
	 * 		the cart modifications
	 * @return int
	 * @throws CommerceCartModificationException
	 * 		the commerce cart modification exception
	 */
	protected int addProduct(final String code, final long qty, final int bundleNo, final AddBundleToCartData addBundleToCartData,
			final TravellerData travellerData, final boolean removeCurrentProducts, final PriceLevel priceLevel,
			final Boolean active, final AmendStatus amendStatus, final List<CartModificationData> cartModifications)
			throws CommerceCartModificationException
	{
		final String bundleTemplateId = addBundleToCartData.getBundleTemplateId();
		final List<String> transportOfferingCodes = addBundleToCartData.getTransportOfferings();
		final String travelRouteCode = addBundleToCartData.getTravelRouteCode();
		final int originDestinationRefNumber = addBundleToCartData.getOriginDestinationRefNumber();

		int bundleNoToReturn = bundleNo;
		getTravelCommercePriceFacade().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes, travellerData);
		setAddToCartParametersInContext(transportOfferingCodes, travelRouteCode, travellerData, priceLevel,
				originDestinationRefNumber, active, amendStatus);
		final List<CartModificationData> cartModificationDataList = getBundleCartFacade().addToCart(code, qty, bundleNo, bundleTemplateId,
				removeCurrentProducts);

		if (CollectionUtils.isNotEmpty(cartModificationDataList))
		{
			cartModifications.addAll(cartModificationDataList);
			bundleNoToReturn = cartModificationDataList.get(cartModificationDataList.size() - 1).getEntry().getBundleNo();
		}

		return bundleNoToReturn;
	}

	/**
	 * Method to set values in Session Context. These values will be used when the product is added to cart.
	 *
	 * @param transportOfferingCodes
	 * 		the transport offering codes
	 * @param travelRouteCode
	 * 		the travel route code
	 * @param travellerData
	 * 		the traveller data
	 * @param priceLevel
	 * 		the price level
	 * @param originDestinationRefNumber
	 * 		the origin destination ref number
	 * @param active
	 * 		the active
	 * @param amendStatus
	 * 		the amend status
	 */
	protected void setAddToCartParametersInContext(final List<String> transportOfferingCodes, final String travelRouteCode,
			final TravellerData travellerData, final PriceLevel priceLevel, final int originDestinationRefNumber,
			final Boolean active, final AmendStatus amendStatus)
	{
		final Map<String, Object> params = new HashMap<>();

		final List<TransportOfferingModel> transportOfferingModels = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(transportOfferingCodes))
		{
			transportOfferingCodes.forEach(transportOffering -> transportOfferingModels
					.add(getTransportOfferingService().getTransportOffering(transportOffering)));
		}
		params.put(TravelservicesConstants.CART_ENTRY_TRANSPORT_OFFERINGS, transportOfferingModels);

		final TravelRouteModel travelRouteModel = getTravelRouteService().getTravelRoute(travelRouteCode);
		params.put(TravelservicesConstants.CART_ENTRY_TRAVEL_ROUTE, travelRouteModel);

		if (travellerData != null)
		{
			final TravellerModel travellerModel = getTravellerService()
					.getExistingTraveller(travellerData.getUid(), getTravellerVersionIdFromCart());
			params.put(TravelservicesConstants.CART_ENTRY_TRAVELLER, travellerModel);
		}

		if (Objects.nonNull(priceLevel))
		{
			params.put(TravelservicesConstants.CART_ENTRY_PRICELEVEL, priceLevel.getCode());
		}

		params.put(TravelservicesConstants.CART_ENTRY_ORIG_DEST_REF_NUMBER, originDestinationRefNumber);
		params.put(TravelservicesConstants.CART_ENTRY_ACTIVE, active);
		params.put(TravelservicesConstants.CART_ENTRY_AMEND_STATUS, amendStatus);

		getSessionService().setAttribute(TravelservicesConstants.ADDBUNDLE_TO_CART_PARAM_MAP, params);
	}

	protected String getTravellerVersionIdFromCart()
	{
		if (!getCartService().hasSessionCart())
		{
			return StringUtils.EMPTY;
		}

		if(Objects.nonNull(getCartService().getSessionCart().getOriginalOrder()))
		{
			return getCartService().getSessionCart().getCode();
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getCurrentCartCode()
	{
		if (!getCartService().hasSessionCart())
		{
			return StringUtils.EMPTY;
		}
		return getCartService().getSessionCart().getCode();
	}

	@Override
	public boolean hasCartBeenAmended()
	{
		return getBookingService().hasCartBeenAmended();
	}

	@Override
	public void evaluateCart()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final List<CartEntryModel> cartEntries = getTravelRulesService().evaluateCart(cartModel);
		if (CollectionUtils.isEmpty(cartEntries))
		{
			return;
		}
		cartEntries.forEach(cartEntry ->
		{
			final Map<String, Object> propertiesMap = new HashMap<>();
			propertiesMap.put(AbstractOrderEntryModel.ACTIVE, Boolean.TRUE);
			propertiesMap.put(AbstractOrderEntryModel.AMENDSTATUS, AmendStatus.NEW);
			getTravelCommerceCartService().addPropertiesToCartEntry(cartModel, cartEntry.getEntryNumber(), cartEntry.getProduct(),
					propertiesMap);
		});
	}

	@Override
	public boolean isCurrentCartValid()
	{
		if (!hasEntries())
		{
			return false;
		}

		final CartModel cartModel = getCartService().getSessionCart();

		if (cartModel.getOriginalOrder() != null)
		{
			final OrderModel orderModel = cartModel.getOriginalOrder();
			if (OrderStatus.CANCELLED.equals(orderModel.getStatus()) || OrderStatus.CANCELLING.equals(orderModel.getStatus()))
			{
				getCartService().removeSessionCart();
				return false;
			}
		}

		return true;
	}

	@Override
	public PriceData getTotalToPayPrice()
	{
		if (!getCartService().hasSessionCart())
		{
			return getTravelCommercePriceFacade().createPriceData(BigDecimal.ZERO.doubleValue(), 2);
		}

		final CartModel cartModel = getCartService().getSessionCart();

		BigDecimal totalPrice;

		if (cartModel.getOriginalOrder() != null)
		{
			final OrderModel originalOrder = cartModel.getOriginalOrder();
			final BigDecimal oldTotalWithTaxes = BigDecimal.valueOf(originalOrder.getTotalPrice())
					.add(BigDecimal.valueOf(originalOrder.getTotalTax()));
			final BigDecimal orderTotalPaid = getBookingService().getOrderTotalPaid(originalOrder);
			final BigDecimal newTotalWithTaxes = BigDecimal.valueOf(cartModel.getTotalPrice())
					.add(BigDecimal.valueOf(cartModel.getTotalTax()));
			if (oldTotalWithTaxes.compareTo(orderTotalPaid) == 0)
			{
				totalPrice = newTotalWithTaxes.subtract(oldTotalWithTaxes);
			}
			else
			{
				totalPrice = orderTotalPaid.compareTo(newTotalWithTaxes) >= 0 ? BigDecimal.ZERO
						: newTotalWithTaxes.subtract(oldTotalWithTaxes);
			}
		}
		else
		{
			totalPrice = BigDecimal.valueOf(cartModel.getTotalPrice());
			if (cartModel.getNet())
			{
				totalPrice = totalPrice.add(BigDecimal.valueOf(cartModel.getTotalTax()));
			}
		}

		return getTravelCommercePriceFacade().createPriceData(totalPrice.doubleValue(), 2);
	}

	@Override
	public PriceData getTotalToPayPriceAfterChangeDates()
	{
		final CartModel cartModel = getCartService().getSessionCart();

		Double totalCartAmount = 0d;

		final List<AbstractOrderEntryModel> activeEntries = cartModel.getEntries().stream()
				.filter(entry -> entry.getActive() && OrderEntryType.ACCOMMODATION.equals(entry.getType()))
				.collect(Collectors.toList());

		for (final AbstractOrderEntryModel entry : activeEntries)
		{
			Double totalEntryPrice = 0d;

			totalEntryPrice = Double.sum(totalEntryPrice, entry.getTotalPrice());

			if (CollectionUtils.isNotEmpty(entry.getTaxValues()))
			{
				totalEntryPrice = Double.sum(totalEntryPrice,
						entry.getTaxValues().stream().mapToDouble(taxValue -> taxValue.getAppliedValue()).sum());
			}
			totalCartAmount = Double.sum(totalCartAmount, totalEntryPrice);
		}

		return getTravelCommercePriceFacade().createPriceData(totalCartAmount, 2);

	}

	@Override
	public PriceData getPartialPaymentAmount()
	{
		final List<PaymentTransactionData> transactions = sessionService.getAttribute("paymentTransactions");
		Double partialPrice = 0d;
		if (CollectionUtils.isEmpty(transactions))
		{
			return null;
		}
		partialPrice = Double.sum(partialPrice,
				(transactions.stream().map(transaction -> transaction.getTransactionAmount()).reduce(0d, Double::sum)));
		return getTravelCommercePriceFacade().createPriceData(partialPrice, 2);

	}

	@Override
	public PriceData getBookingTotal(final String originalOrderCode)
	{
		final PriceData totalPrice;
		if (StringUtils.equals(sessionService.getAttribute(TravelfacadesConstants.SESSION_PAY_NOW), originalOrderCode)
				&& !hasCartBeenAmended())
		{
			totalPrice = travelCommercePriceFacade.createPriceData(
					bookingFacade.getOrderTotalToPayForOrderEntryType(originalOrderCode, OrderEntryType.ACCOMMODATION).doubleValue(),
					2);
		}
		else if (StringUtils.equals(sessionService.getAttribute(TravelfacadesConstants.SESSION_CHANGE_DATES), originalOrderCode))
		{
			totalPrice = getTotalToPayPriceAfterChangeDates();
		}
		else
		{
			totalPrice = getTotalToPayPrice();
		}
		return totalPrice;
	}

	@Override
	public PriceData getCartTotal()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final BigDecimal cartTotalWithTaxes = BigDecimal.valueOf(cartModel.getTotalPrice())
				.add(BigDecimal.valueOf(cartModel.getTotalTax()));

		return getTravelCommercePriceFacade().createPriceData(cartTotalWithTaxes.doubleValue(), 2);
	}

	@Override
	public PriceData getBookingDueAmount(final PriceData totalAmount, final PriceData amountPaid)
	{
		if (Objects.nonNull(totalAmount) && Objects.nonNull(amountPaid))
		{
			final String currencyIso = totalAmount.getCurrencyIso();
			return getTravelCommercePriceFacade()
					.createPriceData(totalAmount.getValue().subtract(amountPaid.getValue()).doubleValue(), 2, currencyIso);
		}
		return null;
	}

	@Override
	public void setOrderEntryType(final OrderEntryType type, final int entryNumber)
	{
		final AbstractOrderEntryModel entryToUpdate = getCartService().getEntryForNumber(getCartService().getSessionCart(),
				entryNumber);
		entryToUpdate.setType(type);
		getModelService().save(entryToUpdate);
	}

	@Override
	public List<PaymentOptionData> getPaymentOptions()
	{
		final List<PaymentOptionData> paymentOptions = new ArrayList<>();
		paymentOptions.addAll(Converters.convertAll(getTravelCartService().getPaymentOptions(), getPaymentOptionConverter()));
		return paymentOptions;
	}

	@Override
	public List<PaymentOptionData> getPaymentOptions(final OrderEntryType orderEntryType)
	{
		return Converters.convertAll(getTravelCartService().getPaymentOptions(orderEntryType), getPaymentOptionConverter());
	}

	@Override
	public void deleteCurrentCart()
	{
		getTravelCartService().deleteCurrentCart();

	}

	@Override
	public boolean isValidPaymentOption(final List<PaymentTransactionData> transactions)
	{
		final List<PaymentOptionData> paymentOptions = getPaymentOptions();
		final Optional<PaymentOptionData> selectedPaymentOption = paymentOptions.stream()
				.filter(option -> isSelectedOption(option.getAssociatedTransactions(), transactions)).findAny();
		return selectedPaymentOption.isPresent();
	}

	/**
	 * Is selected option boolean.
	 *
	 * @param optionTransactions
	 * 		the option transactions
	 * @param submittedTransactions
	 * 		the submitted transactions
	 * @return the boolean
	 */
	protected boolean isSelectedOption(final List<PaymentTransactionData> optionTransactions,
			final List<PaymentTransactionData> submittedTransactions)
	{
		for (final PaymentTransactionData transaction : optionTransactions)
		{
			final Optional<PaymentTransactionData> correspondingTransaction = submittedTransactions.stream()
					.filter(submittedTransaction -> isSameTransaction(submittedTransaction, transaction)).findAny();
			if (!correspondingTransaction.isPresent())
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Is same transaction boolean.
	 *
	 * @param submittedTransactions
	 * 		the submitted transactions
	 * @param transaction
	 * 		the transaction
	 * @return the boolean
	 */
	protected boolean isSameTransaction(final PaymentTransactionData submittedTransactions,
			final PaymentTransactionData transaction)
	{
		return submittedTransactions.getTransactionAmount().equals(transaction.getTransactionAmount())
				&& submittedTransactions.getEntryNumbers().containsAll(transaction.getEntryNumbers());
	}

	@Override
	public Integer getNextBundleNumberToUse()
	{
		return getTravelCartService().getNextBundleNumberToUse();
	}

	@Override
	public void updateBundleEntriesWithBundleNumber(final List<Integer> entryNumbers, final Integer forcedBundleNumber)
	{
		getTravelCartService().updateBundleEntriesWithBundleNumber(entryNumbers, forcedBundleNumber);
	}

	@Override
	public void validateCart(final String departureLocation, final String arrivalLocation, final String departureDate,
			final String returnDate)
	{
		getTravelCartService().validateCart(departureLocation, arrivalLocation, departureDate, returnDate);
	}

	@Override
	public void cleanUpCartForMinOriginDestinationRefNumber(final Integer odRefNum)
	{
		getTravelCommerceCartService().removeCartEntriesForMinODRefNumber(odRefNum);
	}

	@Override
	public void removeEntriesForOriginDestinationRefNumber(final Integer odRefNum)
	{
		if (hasSessionCart())
		{
			getTravelCommerceCartService().removeCartEntriesForODRefNumber(odRefNum, getCartService().getSessionCart());
		}
	}

	@Override
	public boolean validateOriginDestinationRefNumbersInCart()
	{
		if (!hasSessionCart())
		{
			return false;
		}
		final List<AbstractOrderEntryModel> abstractOrderEntries = getTravelCartService().getSessionCart().getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& !(ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel))
				.collect(Collectors.toList());

		final int max = abstractOrderEntries.stream()
				.mapToInt(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()).max().getAsInt();
		return IntStream.range(0, max).allMatch(odRefNum -> abstractOrderEntries.stream()
				.anyMatch(entry -> odRefNum == entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber()));
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
	 * 		the travellerService to set
	 */
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * @return the orderEntryConverter
	 */
	@Override
	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	/**
	 * @param orderEntryConverter
	 * 		the orderEntryConverter to set
	 */
	@Override
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	/**
	 * Gets travel route service.
	 *
	 * @return the travelRouteService
	 */
	protected TravelRouteService getTravelRouteService()
	{
		return travelRouteService;
	}

	/**
	 * Sets travel route service.
	 *
	 * @param travelRouteService
	 * 		the travelRouteService to set
	 */
	@Required
	public void setTravelRouteService(final TravelRouteService travelRouteService)
	{
		this.travelRouteService = travelRouteService;
	}

	/**
	 * Gets transport offering service.
	 *
	 * @return the transportOfferingService
	 */
	protected TransportOfferingService getTransportOfferingService()
	{
		return transportOfferingService;
	}

	/**
	 * Sets transport offering service.
	 *
	 * @param transportOfferingService
	 * 		the transportOfferingService to set
	 */
	@Required
	public void setTransportOfferingService(final TransportOfferingService transportOfferingService)
	{
		this.transportOfferingService = transportOfferingService;
	}

	/**
	 * Gets travel commerce cart service.
	 *
	 * @return the travelCommerceCartService
	 */
	protected TravelCommerceCartService getTravelCommerceCartService()
	{
		return travelCommerceCartService;
	}

	/**
	 * Sets travel commerce cart service.
	 *
	 * @param travelCommerceCartService
	 * 		the travelCommerceCartService to set
	 */
	@Required
	public void setTravelCommerceCartService(final TravelCommerceCartService travelCommerceCartService)
	{
		this.travelCommerceCartService = travelCommerceCartService;
	}

	/**
	 * Gets accommodation map service.
	 *
	 * @return the accommodationMapService
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * Sets accommodation map service.
	 *
	 * @param accommodationMapService
	 * 		the accommodationMapService to set
	 */
	@Required
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

	/**
	 * Gets travel cart service.
	 *
	 * @return the travelCartService
	 */
	protected TravelCartService getTravelCartService()
	{
		return travelCartService;
	}

	/**
	 * Sets travel cart service.
	 *
	 * @param travelCartService
	 * 		the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * Gets voucher facade.
	 *
	 * @return the voucherFacade
	 */
	protected VoucherFacade getVoucherFacade()
	{
		return voucherFacade;
	}

	/**
	 * Sets voucher facade.
	 *
	 * @param voucherFacade
	 * 		the voucherFacade to set
	 */
	@Required
	public void setVoucherFacade(final VoucherFacade voucherFacade)
	{
		this.voucherFacade = voucherFacade;
	}

	/**
	 * Gets travel promotions facade.
	 *
	 * @return the travelPromotionsFacade
	 */
	protected TravelPromotionsFacade getTravelPromotionsFacade()
	{
		return travelPromotionsFacade;
	}

	/**
	 * Sets travel promotions facade.
	 *
	 * @param travelPromotionsFacade
	 * 		the travelPromotionsFacade to set
	 */
	@Required
	public void setTravelPromotionsFacade(final TravelPromotionsFacade travelPromotionsFacade)
	{
		this.travelPromotionsFacade = travelPromotionsFacade;
	}

	/**
	 * Gets traveller facade.
	 *
	 * @return the travellerFacade
	 */
	protected TravellerFacade getTravellerFacade()
	{
		return travellerFacade;
	}

	/**
	 * Sets traveller facade.
	 *
	 * @param travellerFacade
	 * 		the travellerFacade to set
	 */
	@Required
	public void setTravellerFacade(final TravellerFacade travellerFacade)
	{
		this.travellerFacade = travellerFacade;
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
	 * 		the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * Gets session service.
	 *
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets session service.
	 *
	 * @param sessionService
	 * 		the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * Gets booking service.
	 *
	 * @return the bookingService
	 */
	protected BookingService getBookingService()
	{
		return bookingService;
	}

	/**
	 * Sets booking service.
	 *
	 * @param bookingService
	 * 		the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * Gets travel rules service.
	 *
	 * @return the travelRulesService
	 */
	protected TravelRulesService getTravelRulesService()
	{
		return travelRulesService;
	}

	/**
	 * Sets travel rules service.
	 *
	 * @param travelRulesService
	 * 		the travelRulesService to set
	 */
	@Required
	public void setTravelRulesService(final TravelRulesService travelRulesService)
	{
		this.travelRulesService = travelRulesService;
	}

	/**
	 * Gets populate cart entry property strategy map.
	 *
	 * @return the populateCartEntryPropertyStrategyMap
	 */
	protected Map<String, PopulatePropertyMapStrategy> getPopulateCartEntryPropertyStrategyMap()
	{
		return populateCartEntryPropertyStrategyMap;
	}

	/**
	 * Sets populate cart entry property strategy map.
	 *
	 * @param populateCartEntryPropertyStrategyMap
	 * 		the populateCartEntryPropertyStrategyMap to set
	 */
	@Required
	public void setPopulateCartEntryPropertyStrategyMap(
			final Map<String, PopulatePropertyMapStrategy> populateCartEntryPropertyStrategyMap)
	{
		this.populateCartEntryPropertyStrategyMap = populateCartEntryPropertyStrategyMap;
	}

	/**
	 * Gets common i 18 n service.
	 *
	 * @return the common i 18 n service
	 */
	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Sets common i 18 n service.
	 *
	 * @param commonI18NService
	 * 		the common i 18 n service
	 */
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * Gets payment option converter.
	 *
	 * @return paymentOptionConverter payment option converter
	 */
	protected Converter<PaymentOptionInfo, PaymentOptionData> getPaymentOptionConverter()
	{
		return paymentOptionConverter;
	}

	/**
	 * Sets payment option converter.
	 *
	 * @param paymentOptionConverter
	 * 		the paymentOptionConverter to set
	 */
	@Required
	public void setPaymentOptionConverter(final Converter<PaymentOptionInfo, PaymentOptionData> paymentOptionConverter)
	{
		this.paymentOptionConverter = paymentOptionConverter;
	}

	/**
	 * @return the bookingFacade
	 */
	protected BookingFacade getBookingFacade()
	{
		return bookingFacade;
	}

	/**
	 * @param bookingFacade
	 * 		the bookingFacade to set
	 */
	public void setBookingFacade(final BookingFacade bookingFacade)
	{
		this.bookingFacade = bookingFacade;
	}

	/**
	 * @return the bundleCartFacade
	 */
	protected BundleCartFacade getBundleCartFacade()
	{
		return bundleCartFacade;
	}

	/**
	 * @param bundleCartFacade
	 * 		the bundleCartFacade to set
	 */
	@Required
	public void setBundleCartFacade(final BundleCartFacade bundleCartFacade)
	{
		this.bundleCartFacade = bundleCartFacade;
	}
}
