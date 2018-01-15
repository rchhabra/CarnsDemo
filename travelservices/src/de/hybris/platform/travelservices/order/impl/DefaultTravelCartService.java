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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.impl.DefaultCartService;
import de.hybris.platform.order.strategies.ordercloning.CloneAbstractOrderStrategy;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.BookingJourneyType;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.enums.TripType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.TravelOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.FareProductModel;
import de.hybris.platform.travelservices.model.product.FeeProductModel;
import de.hybris.platform.travelservices.model.travel.SelectedAccommodationModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.PassengerInformationModel;
import de.hybris.platform.travelservices.model.user.SpecialRequestDetailModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.user.TravellerPreferenceModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.order.daos.TravelCartDao;
import de.hybris.platform.travelservices.order.data.PaymentOptionInfo;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.TravellerService;
import de.hybris.platform.travelservices.stock.TravelCommerceStockService;
import de.hybris.platform.travelservices.strategies.payment.PaymentOptionCreationStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Extends DefaultCartService to handle travel specific services like creating cart from order
 */
public class DefaultTravelCartService extends DefaultCartService implements TravelCartService
{
	private static final Logger LOG = Logger.getLogger(DefaultTravelCartService.class);

	private CloneAbstractOrderStrategy cloneAbstractOrderStrategy;
	private CustomerAccountService customerAccountService;
	private BaseStoreService baseStoreService;
	private KeyGenerator orderCodeGenerator;
	private UserService userService;
	private TravelCartDao travelCartDao;
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	private CommonI18NService commonI18NService;
	private BookingService bookingService;
	private TravelCommerceStockService travelCommerceStockService;
	private StoreSessionService storeSessionService;
	private List<PaymentOptionCreationStrategy> paymentOptionCreationStrategies;
	private Map<OrderEntryType, List<PaymentOptionCreationStrategy>> paymentOptionsCreationStrategyMap;
	private TimeService timeService;
	private TravellerService travellerService;
	private EnumerationService enumerationService;

	@Override
	public CartModel getSessionCart()
	{
		final CartModel sessionCart = super.getSessionCart();

		if (Objects.isNull(sessionCart.getBookingJourneyType()))
		{
			final String sessionBookingJourney = getSessionService().getAttribute(TravelservicesConstants.SESSION_BOOKING_JOURNEY);
			if (StringUtils.isNotBlank(sessionBookingJourney))
			{
				final BookingJourneyType bookingJourneyType = getEnumerationService().getEnumerationValue(BookingJourneyType.class,
						sessionBookingJourney);
				if (Objects.isNull(bookingJourneyType))
				{
					LOG.error(sessionBookingJourney + " is not a valid value for the BookingJourneyType.");
				}
				sessionCart.setBookingJourneyType(bookingJourneyType);
			}
		}

		if (Objects.isNull(sessionCart.getTripType()))
		{
			final String sessionTripType = getSessionService().getAttribute(TravelservicesConstants.SESSION_TRIP_TYPE);
			if (StringUtils.isNotBlank(sessionTripType))
			{
				final TripType tripType = getEnumerationService().getEnumerationValue(TripType.class, sessionTripType);
				if (Objects.isNull(tripType))
				{
					LOG.error(tripType + " is not a valid value for the TripType.");
				}
				sessionCart.setTripType(tripType);
			}
		}
		return sessionCart;
	}

	@Override
	public CartModel createCartFromOrder(final String orderCode, final String guid)
	{
		final OrderModel orderModel = getOrder(orderCode);

		final String currentCurrencyIsocode = getCommonI18NService().getCurrentCurrency().getIsocode();
		final String orderCurrency = orderModel.getCurrency().getIsocode();
		if (!StringUtils.equalsIgnoreCase(currentCurrencyIsocode, orderCurrency))
		{
			forceCurrencyToOriginalOrderCurrency(orderCurrency);
		}

		final List<CartModel> oldCarts = getTravelCartDao().findCartsForOriginalOrder(orderModel);
		if (CollectionUtils.isNotEmpty(oldCarts))
		{
			getModelService().removeAll(oldCarts);
		}

		final CartModel cartModel = getCloneAbstractOrderStrategy().clone(null, null, orderModel,
				getOrderCodeGenerator().generate().toString(), CartModel.class, CartEntryModel.class);

		cartModel.setOriginalOrder(orderModel);
		cartModel.setUser(getUserService().getUserForUID(guid));
		if (null != orderModel.getBillingTime())
		{
			cartModel.setBillingTime(orderModel.getBillingTime());
		}

		cartModel.setCreationtime(getTimeService().getCurrentTime());
		cartModel.setDate(getTimeService().getCurrentTime());
		if (Objects.nonNull(orderModel.getPaymentAddress()) || Objects.nonNull(orderModel.getDeliveryAddress()))
		{
			cartModel.setDeliveryAddress(getModelService().clone(Objects.nonNull(orderModel.getDeliveryAddress())
					? orderModel.getDeliveryAddress() : orderModel.getPaymentAddress()));
		}
		if (Objects.nonNull(orderModel.getPaymentAddress()))
		{
			cartModel.setPaymentAddress(getModelService().clone(orderModel.getPaymentAddress()));
		}
		cartModel.setPaymentInfo(getModelService().clone(orderModel.getPaymentInfo()));

		// If the modified order's payment info has been removed, the owner attribute will be null after clone.
		if (Objects.nonNull(cartModel.getPaymentAddress()) && Objects.nonNull(cartModel.getPaymentInfo())
				&& Objects.isNull(cartModel.getPaymentInfo().getBillingAddress().getOwner()))
		{
			cartModel.getPaymentInfo().setBillingAddress(cartModel.getPaymentAddress());
		}
		getModelService().save(cartModel);

		getModelService().detachAll();

		final Map<TravellerModel, TravellerModel> clonedTravellerModelMap = cloneTravellers(cartModel, cartModel.getCode());

		cartModel.getEntries().forEach(entry -> cloneTravelOrderEntryInfo(entry, clonedTravellerModelMap));

		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(orderModel);
		accommodationOrderEntryGroups.forEach(this::cloneAccommodationOrderEntryGroup);

		final List<SelectedAccommodationModel> clonedSelectedAccommodationList = new LinkedList<>();
		cartModel.getSelectedAccommodations()
				.forEach(selectedAccommodationModel -> cloneSelectedAccommodation(selectedAccommodationModel, cartModel,
						clonedTravellerModelMap, clonedSelectedAccommodationList));
		cartModel.setSelectedAccommodations(clonedSelectedAccommodationList);

		getModelService().saveAll();

		getCommerceCartCalculationStrategy().calculateCart(cartModel);

		return cartModel;
	}

	protected OrderModel getOrder(final String orderCode)
	{
		final OrderModel orderModel = getCustomerAccountService().getOrderForCode(orderCode,
				getBaseStoreService().getCurrentBaseStore());
		return orderModel;
	}

	protected void cloneAccommodationOrderEntryGroup(final AccommodationOrderEntryGroupModel orderEntryGroupModel)
	{
		final AccommodationOrderEntryGroupModel clonedEntryGroup = getModelService().clone(orderEntryGroupModel);
		getModelService().save(clonedEntryGroup);
		orderEntryGroupModel.getEntries().stream().filter(entry -> entry instanceof CartEntryModel).forEach(entry -> {
			entry.setEntryGroup(clonedEntryGroup);
			getModelService().save(entry);
		});

		getModelService().refresh(orderEntryGroupModel);
	}

	protected void cloneTravelOrderEntryInfo(final AbstractOrderEntryModel orderEntry,
			final Map<TravellerModel, TravellerModel> clonedTravellerModelMap)
	{
		if (OrderEntryType.TRANSPORT.equals(orderEntry.getType()))
		{
			final TravelOrderEntryInfoModel clonedInfo = getModelService().clone(orderEntry.getTravelOrderEntryInfo(),
					TravelOrderEntryInfoModel.class);

			cloneTravelOrderEntryInfoAttributes(clonedInfo, orderEntry.getTravelOrderEntryInfo());

			if (CollectionUtils.isEmpty(clonedInfo.getTravellers()))
			{
				clonedInfo.setTravellers(null);
			}
			else
			{
				final List<TravellerModel> newTravellers = new LinkedList<>();
				clonedInfo.getTravellers().forEach(travellerModel -> newTravellers.add(clonedTravellerModelMap.get(travellerModel)));
				clonedInfo.setTravellers(newTravellers);
			}

			orderEntry.setTravelOrderEntryInfo(clonedInfo);
		}
		orderEntry.setAmendStatus(AmendStatus.SAME);
	}

	protected void cloneSelectedAccommodation(final SelectedAccommodationModel selectedAccommodationModel,
			final CartModel cartModel, final Map<TravellerModel, TravellerModel> travellerModelTravellerModelMap,
			final List<SelectedAccommodationModel> clonedSelectedAccommodationList)
	{
		final SelectedAccommodationModel clonedSelectedAccommodation = getModelService().clone(selectedAccommodationModel,
				SelectedAccommodationModel.class);

		clonedSelectedAccommodation.setOrder(cartModel);
		clonedSelectedAccommodation.setTraveller(travellerModelTravellerModelMap.get(selectedAccommodationModel.getTraveller()));
		clonedSelectedAccommodationList.add(clonedSelectedAccommodation);
	}

	protected void cloneTravelOrderEntryInfoAttributes(final TravelOrderEntryInfoModel clonedInfo,
			final TravelOrderEntryInfoModel travelOrderEntryInfo)
	{
		if (Objects.nonNull(travelOrderEntryInfo.getSpecialRequestDetail()))
		{
			clonedInfo.setSpecialRequestDetail(
					getModelService().clone(travelOrderEntryInfo.getSpecialRequestDetail(), SpecialRequestDetailModel.class));
		}

		if (CollectionUtils.isNotEmpty(travelOrderEntryInfo.getComments()))
		{
			final List<CommentModel> clonedComments = new LinkedList<>();
			travelOrderEntryInfo.getComments()
					.forEach(comment -> clonedComments.add(getModelService().clone(comment, CommentModel.class)));
			clonedInfo.setComments(clonedComments);
		}
	}

	protected Map<TravellerModel, TravellerModel> cloneTravellers(final CartModel cartModel, final String cartCode)
	{
		final Map<TravellerModel, TravellerModel> clonedTravellersMap = new HashMap<>();
		final Set<TravellerModel> travellers = cartModel.getEntries().stream()
				.filter(entry -> Objects.nonNull(entry.getTravelOrderEntryInfo())
						&& Objects.nonNull(entry.getTravelOrderEntryInfo().getTravellers()))
				.flatMap(entry -> entry.getTravelOrderEntryInfo().getTravellers().stream()).collect(Collectors.toSet());

		for (final TravellerModel traveller : travellers)
		{
			cloneTraveller(traveller, cartCode, clonedTravellersMap);
		}
		return clonedTravellersMap;
	}

	protected void cloneTraveller(final TravellerModel traveller, final String cartCode,
			final Map<TravellerModel, TravellerModel> clonedTravellersMap)
	{
		final TravellerModel clonedTraveller = getModelService().clone(traveller, TravellerModel.class);

		if (Objects.nonNull(traveller.getInfo()))
		{
			if (traveller.getInfo() instanceof PassengerInformationModel)
			{
				clonedTraveller.setInfo(getModelService().clone(traveller.getInfo(), PassengerInformationModel.class));
			}
			else
			{
				clonedTraveller.setInfo(getModelService().clone(traveller.getInfo()));
			}
		}

		if (Objects.nonNull(traveller.getSpecialRequestDetail()))
		{
			clonedTraveller.setSpecialRequestDetail(
					getModelService().clone(traveller.getSpecialRequestDetail(), SpecialRequestDetailModel.class));
		}

		if (CollectionUtils.isNotEmpty(traveller.getTravellerPreference()))
		{
			final Collection<TravellerPreferenceModel> clonedTravellerPreference = new LinkedList<>();
			traveller.getTravellerPreference().forEach(
					travellerPreferenceModel -> clonedTravellerPreference.add(getModelService().clone(travellerPreferenceModel)));
			clonedTraveller.setTravellerPreference(clonedTravellerPreference);
		}

		clonedTraveller.setTravelOrderEntryInfo(Collections.emptyList());

		clonedTraveller.setVersionID(cartCode);

		clonedTravellersMap.put(traveller, clonedTraveller);
	}

	/**
	 * Changes currency so that amendment is done in the same currency as original order
	 *
	 * @param isocode
	 */
	protected void forceCurrencyToOriginalOrderCurrency(final String isocode)
	{
		getStoreSessionService().setCurrentCurrency(isocode);
		final UserModel user = getUserService().getCurrentUser();
		user.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		getModelService().save(user);
	}

	@Override
	public CartModel cancelTraveller(final String orderCode, final String cancelledTravellerCode,
			final String cancelledTravellerUid, final String guid)
	{
		final CartModel amendmentCart = createCartFromOrder(orderCode, guid);
		final List<AbstractOrderEntryModel> travellerEntries = amendmentCart.getEntries().stream()
				.filter(entry -> entry.getTravelOrderEntryInfo() != null
						&& CollectionUtils.size(entry.getTravelOrderEntryInfo().getTravellers()) == 1 && entry.getTravelOrderEntryInfo()
								.getTravellers().iterator().next().getLabel().equalsIgnoreCase(cancelledTravellerCode))
				.collect(Collectors.toList());

		travellerEntries.forEach(entry -> {
			entry.setQuantity(0L);
			entry.setActive(Boolean.FALSE);
			entry.setAmendStatus(AmendStatus.CHANGED);
			getModelService().save(entry);
		});

		removeBookedSeatForTraveller(amendmentCart, cancelledTravellerUid);
		getCommerceCartCalculationStrategy().calculateCart(amendmentCart);
		return amendmentCart;
	}

	protected void removeBookedSeatForTraveller(final CartModel amendmentCart, final String cancelledTravellerUid)
	{
		final List<SelectedAccommodationModel> selectedAccommodations = amendmentCart.getSelectedAccommodations();
		if (CollectionUtils.isNotEmpty(selectedAccommodations))
		{
			final List<SelectedAccommodationModel> toBeRemoved = new ArrayList<>();
			final List<SelectedAccommodationModel> remainingSelectedAccoms = new ArrayList<>();
			for (final SelectedAccommodationModel selectedAccommodationModel : selectedAccommodations)
			{
				final TravellerModel traveller = selectedAccommodationModel.getTraveller();
				if (traveller.getUid().equals(cancelledTravellerUid))
				{
					toBeRemoved.add(selectedAccommodationModel);
				}
				else
				{
					remainingSelectedAccoms.add(selectedAccommodationModel);
				}
			}
			if (CollectionUtils.isNotEmpty(toBeRemoved))
			{
				for (final SelectedAccommodationModel selectedAccommodationModel : toBeRemoved)
				{
					getModelService().remove(selectedAccommodationModel);
				}

			}
			amendmentCart.setSelectedAccommodations(remainingSelectedAccoms);
			getModelService().save(amendmentCart);
		}
	}

	@Override
	public void removeDeliveryAddress()
	{
		final CartModel cart = getSessionCart();
		if (cart != null)
		{
			cart.setDeliveryAddress(null);
			getModelService().save(cart);
		}
	}

	/**
	 * @return the cloneAbstractOrderStrategy
	 */
	@Override
	public CloneAbstractOrderStrategy getCloneAbstractOrderStrategy()
	{
		return cloneAbstractOrderStrategy;
	}

	/**
	 * @param cloneAbstractOrderStrategy
	 *           the cloneAbstractOrderStrategy to set
	 */
	@Override
	public void setCloneAbstractOrderStrategy(final CloneAbstractOrderStrategy cloneAbstractOrderStrategy)
	{
		this.cloneAbstractOrderStrategy = cloneAbstractOrderStrategy;
	}

	@Override
	public Long getAvailableStock(final ProductModel productModel, final TransportOfferingModel transportOfferingModel)
	{
		final Long quantityInStock = getTravelCommerceStockService().getStockLevel(productModel,
				Stream.of(transportOfferingModel).collect(Collectors.<TransportOfferingModel> toList()));
		if (quantityInStock == null)
		{
			return null;
		}
		if (super.getSessionCart().getOriginalOrder() == null)
		{
			return quantityInStock;
		}
		final Long quantityInOrder = getBookingService().getProductQuantityInOrderForTransportOffering(
				super.getSessionCart().getOriginalOrder().getCode(), productModel, transportOfferingModel);
		return quantityInStock + quantityInOrder;
	}

	@Override
	public List<AbstractOrderEntryModel> getFareProductEntries(final AbstractOrderModel abstractOrderModel)
	{
		return abstractOrderModel.getEntries().stream().filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType()))
				.filter(entry -> (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FareProductModel)
						&& entry.getActive())
				.collect(Collectors.toList());
	}

	@Override
	public String getCurrentDestination()
	{
		if (hasSessionCart())
		{
			final CartModel sessionCart = getSessionCart();

			final List<AbstractOrderEntryModel> transportationEntries = sessionCart.getEntries().stream()
					.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
							&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
									|| entry.getProduct() instanceof FareProductModel))
					.collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(transportationEntries))
			{
				final Optional<AbstractOrderEntryModel> firstLegEntry = transportationEntries.stream()
						.filter(entry -> entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber() == 0).findFirst();
				if (firstLegEntry.isPresent())
				{
					return firstLegEntry.get().getTravelOrderEntryInfo().getTravelRoute().getDestination().getCode();
				}
			}
		}

		return null;
	}

	@Override
	public List<PaymentOptionInfo> getPaymentOptions()
	{
		if (!hasSessionCart())
		{
			return Collections.emptyList();
		}
		final CartModel sessionCart = getSessionCart();
		final List<PaymentOptionInfo> paymentOptions = new ArrayList<>();
		getPaymentOptionCreationStrategies().forEach(strategy -> paymentOptions.add(strategy.create(sessionCart)));
		return paymentOptions.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<PaymentOptionInfo> getPaymentOptions(final OrderEntryType orderEntryType)
	{
		if (!hasSessionCart())
		{
			return Collections.emptyList();
		}
		final CartModel sessionCart = getSessionCart();
		final List<PaymentOptionInfo> paymentOptions = new ArrayList<>();

		getPaymentOptionsCreationStrategyMap().get(orderEntryType)
				.forEach(strategy -> paymentOptions.add(strategy.create(sessionCart)));

		return paymentOptions;
	}

	@Override
	public void deleteCurrentCart()
	{
		if (hasSessionCart())
		{
			final CartModel cartModel = getSessionCart();
			getModelService().remove(cartModel);
		}
	}

	@Override
	public CartModel cancelPartialOrder(final String orderCode, final OrderEntryType orderEntryType, final String guid)
	{
		final CartModel amendmentCart = createCartFromOrder(orderCode, guid);

		final List<AbstractOrderEntryModel> orderEntriesForType = amendmentCart.getEntries().stream()
				.filter(entry -> Objects.equals(entry.getType(), orderEntryType)).collect(Collectors.toList());

		orderEntriesForType.forEach(entry -> {
			entry.setQuantity(0L);
			entry.setActive(Boolean.FALSE);
			entry.setAmendStatus(AmendStatus.CHANGED);
			getModelService().save(entry);
		});

		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setEnableHooks(true);
		commerceCartParameter.setCart(amendmentCart);
		getCommerceCartCalculationStrategy().calculateCart(commerceCartParameter);

		return amendmentCart;
	}

	@Override
	public Integer getNextBundleNumberToUse()
	{
		final CartModel sessionCart = getSessionCart();
		final OptionalInt optionalMaxBundleNumber = sessionCart.getEntries().stream()
				.mapToInt(entry -> entry.getBundleNo().intValue()).max();
		return optionalMaxBundleNumber.isPresent() ? optionalMaxBundleNumber.getAsInt() + 1 : 0;
	}

	@Override
	public void updateBundleEntriesWithBundleNumber(final List<Integer> entryNumbers, final Integer forcedBundleNumber)
	{
		final List<AbstractOrderEntryModel> entries = entryNumbers.stream()
				.map(number -> getEntryForNumber(getSessionCart(), number)).collect(Collectors.toList());
		final Integer nextBundleNumber = Objects.nonNull(forcedBundleNumber) ? forcedBundleNumber : getNextBundleNumberToUse();
		entries.forEach(entry -> entry.setBundleNo(nextBundleNumber));
		getModelService().saveAll(entries);
	}

	@Override
	public void validateCart(final String departureLocation, final String arrivalLocation, final String departureDate,
			final String returnDate)
	{
		if (!hasSessionCart())
		{
			return;
		}

		final CartModel cartModel = getSessionCart();

		final Optional<AbstractOrderEntryModel> optionalOutboundEntry = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& !(ProductType.FEE.equals(entry.getProduct().getProductType()) || entry.getProduct() instanceof FeeProductModel)
						&& TravelservicesConstants.OUTBOUND_REFERENCE_NUMBER == entry.getTravelOrderEntryInfo()
								.getOriginDestinationRefNumber())
				.findAny();

		if (optionalOutboundEntry.isPresent() && !isCartValid(departureLocation, arrivalLocation, departureDate, returnDate,
				cartModel, optionalOutboundEntry.get()))
		{
			removeSessionCart();
		}

	}

	@Override
	public void setAdditionalSecurity(final Boolean additionalSecurity)
	{
		if (!hasSessionCart())
		{
			return;
		}

		final CartModel cartModel = getSessionCart();
		cartModel.setAdditionalSecurity(additionalSecurity);
		getModelService().save(cartModel);
	}

	protected boolean isCartValid(final String departureLocation, final String arrivalLocation, final String departureDate,
			final String returnDate, final CartModel cartModel, final AbstractOrderEntryModel outboundEntry)
	{

		final TravelRouteModel travelRoute = outboundEntry.getTravelOrderEntryInfo().getTravelRoute();
		if (!((travelRoute.getOrigin().getCode().equals(departureLocation)
				|| travelRoute.getOrigin().getLocation().getCode().equals(departureLocation))
				&& (travelRoute.getDestination().getCode().equals(arrivalLocation)
						|| travelRoute.getDestination().getLocation().getCode().equals(arrivalLocation))))
		{
			return Boolean.FALSE;
		}

		final Optional<TransportOfferingModel> firstTransportOfferingOptional = outboundEntry.getTravelOrderEntryInfo()
				.getTransportOfferings().stream().findFirst();

		if (!firstTransportOfferingOptional.isPresent()
				|| !TravelDateUtils.isSameDate(firstTransportOfferingOptional.get().getDepartureTime(),
						TravelDateUtils.convertStringDateToDate(departureDate, TravelservicesConstants.DATE_PATTERN)))
		{
			return Boolean.FALSE;
		}

		final Optional<AbstractOrderEntryModel> optionalInboundEntry = cartModel.getEntries().stream()
				.filter(entry -> OrderEntryType.TRANSPORT.equals(entry.getType())
						&& (ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
								|| entry.getProduct() instanceof FareProductModel)
						&& TravelservicesConstants.INBOUND_REFERENCE_NUMBER == entry.getTravelOrderEntryInfo()
								.getOriginDestinationRefNumber())
				.findAny();

		if (optionalInboundEntry.isPresent() && StringUtils.isEmpty(returnDate)
				|| !optionalInboundEntry.isPresent() && StringUtils.isNotEmpty(returnDate))
		{
			return Boolean.FALSE;
		}

		if (StringUtils.isNotEmpty(returnDate) && (!optionalInboundEntry.isPresent()
				|| !optionalInboundEntry.get().getTravelOrderEntryInfo().getTransportOfferings().stream().findFirst().isPresent()))
		{
			return Boolean.FALSE;
		}

		if (optionalInboundEntry.isPresent() && StringUtils.isNotEmpty(returnDate))
		{
			final Optional<TransportOfferingModel> transportOfferingModel = Objects
					.nonNull(optionalInboundEntry.get().getTravelOrderEntryInfo())
					&& CollectionUtils.isNotEmpty(optionalInboundEntry.get().getTravelOrderEntryInfo().getTransportOfferings())
							? optionalInboundEntry.get().getTravelOrderEntryInfo().getTransportOfferings().stream().findFirst() : null;
			final TransportOfferingModel firstTransportOffering = Objects.nonNull(transportOfferingModel)
					&& transportOfferingModel.isPresent() ? transportOfferingModel.get() : null;

			return Objects.isNull(firstTransportOffering) ? Boolean.FALSE
					: TravelDateUtils.isSameDate(firstTransportOffering.getDepartureTime(),
							TravelDateUtils.convertStringDateToDate(returnDate, TravelservicesConstants.DATE_PATTERN));
		}

		return Boolean.TRUE;
	}

	@Override
	public BigDecimal getTransportTotalByEntries(final AbstractOrderModel order, final List<AbstractOrderEntryModel> entries)
	{

		final double totalPrice = entries.stream().mapToDouble(AbstractOrderEntryModel::getTotalPrice).sum();

		final Double taxes = entries.stream().flatMap(entry -> entry.getTaxValues().stream())
				.mapToDouble(taxValue -> taxValue.getAppliedValue()).sum();
		final Double globalDiscounts = order.getGlobalDiscountValues().stream().mapToDouble(discount -> discount.getAppliedValue())
				.sum();
		final Double entriesDiscounts = entries.stream()
				.filter(entryModel -> ProductType.FARE_PRODUCT.equals(entryModel.getProduct().getProductType())
						|| entryModel.getProduct() instanceof FareProductModel || entryModel.getBundleNo() == 0)
				.flatMap(entry -> entry.getDiscountValues().stream()).mapToDouble(discount -> discount.getAppliedValue()).sum();
		final Double totalExtrasPrice = calculateTotalExtras(entries);
		return BigDecimal.valueOf(totalPrice + taxes - globalDiscounts - entriesDiscounts + totalExtrasPrice);

	}

	protected Double calculateTotalExtras(final List<AbstractOrderEntryModel> entries)
	{
		final List<AbstractOrderEntryModel> globalExtrasEntries = entries.stream()
				.filter(entry -> !(ProductType.FEE.equals(entry.getProduct().getProductType())
						|| entry.getProduct() instanceof FeeProductModel)
						&& !(ProductType.FARE_PRODUCT.equals(entry.getProduct().getProductType())
								|| entry.getProduct() instanceof FareProductModel)
						&& Objects.isNull(entry.getTravelOrderEntryInfo().getOriginDestinationRefNumber())
						&& Objects.isNull(entry.getTravelOrderEntryInfo().getTravelRoute()))
				.collect(Collectors.toList());

		BigDecimal totalExtrasPrice = BigDecimal.ZERO;

		for (final AbstractOrderEntryModel entry : globalExtrasEntries)
		{
			if (entry.getBundleNo() == 0)
			{
				double extrasPriceValue;
				if (entry.getQuantity() == 0)
				{
					extrasPriceValue = (CollectionUtils.isEmpty(entry.getDiscountValues())) ? entry.getTotalPrice()
							: entry.getBasePrice();
				}
				else
				{
					extrasPriceValue = entry.getBasePrice() * entry.getQuantity();
				}
				totalExtrasPrice = totalExtrasPrice.add(BigDecimal.valueOf(extrasPriceValue));
			}
		}
		return totalExtrasPrice.doubleValue();
	}

	/**
	 * @return the customerAccountService
	 */
	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
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
	 * @return the orderCodeGenerator
	 */
	protected KeyGenerator getOrderCodeGenerator()
	{
		return orderCodeGenerator;
	}

	/**
	 * @param orderCodeGenerator
	 *           the orderCodeGenerator to set
	 */
	public void setOrderCodeGenerator(final KeyGenerator orderCodeGenerator)
	{
		this.orderCodeGenerator = orderCodeGenerator;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the travelCartDao
	 */
	protected TravelCartDao getTravelCartDao()
	{
		return travelCartDao;
	}

	/**
	 * @param travelCartDao
	 *           the travelCartDao to set
	 */
	public void setTravelCartDao(final TravelCartDao travelCartDao)
	{
		this.travelCartDao = travelCartDao;
	}

	/**
	 * @return the commerceCartCalculationStrategy
	 */
	protected CommerceCartCalculationStrategy getCommerceCartCalculationStrategy()
	{
		return commerceCartCalculationStrategy;
	}

	/**
	 * @param commerceCartCalculationStrategy
	 *           the commerceCartCalculationStrategy to set
	 */
	public void setCommerceCartCalculationStrategy(final CommerceCartCalculationStrategy commerceCartCalculationStrategy)
	{
		this.commerceCartCalculationStrategy = commerceCartCalculationStrategy;
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
	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the storeSessionService
	 */
	protected StoreSessionService getStoreSessionService()
	{
		return storeSessionService;
	}

	/**
	 * @param storeSessionService
	 *           the storeSessionService to set
	 */
	public void setStoreSessionService(final StoreSessionService storeSessionService)
	{
		this.storeSessionService = storeSessionService;
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
	 * @return the travelCommerceStockService
	 */
	protected TravelCommerceStockService getTravelCommerceStockService()
	{
		return travelCommerceStockService;
	}

	/**
	 * @param travelCommerceStockService
	 *           the travelCommerceStockService to set
	 */
	public void setTravelCommerceStockService(final TravelCommerceStockService travelCommerceStockService)
	{
		this.travelCommerceStockService = travelCommerceStockService;
	}

	/**
	 * @return the paymentOptionCreationStrategies
	 */
	protected List<PaymentOptionCreationStrategy> getPaymentOptionCreationStrategies()
	{
		return paymentOptionCreationStrategies;
	}

	/**
	 * @param paymentOptionCreationStrategies
	 *           the paymentOptionCreationStrategies to set
	 */
	public void setPaymentOptionCreationStrategies(final List<PaymentOptionCreationStrategy> paymentOptionCreationStrategies)
	{
		this.paymentOptionCreationStrategies = paymentOptionCreationStrategies;
	}

	/**
	 * Gets time service.
	 *
	 * @return the time service
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * Sets time service.
	 *
	 * @param timeService
	 *           the time service
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the paymentOptionsCreationStrategyMap
	 */
	protected Map<OrderEntryType, List<PaymentOptionCreationStrategy>> getPaymentOptionsCreationStrategyMap()
	{
		return paymentOptionsCreationStrategyMap;
	}

	/**
	 * @param paymentOptionsCreationStrategyMap
	 *           the paymentOptionsCreationStrategyMap to set
	 */
	@Required
	public void setPaymentOptionsCreationStrategyMap(
			final Map<OrderEntryType, List<PaymentOptionCreationStrategy>> paymentOptionsCreationStrategyMap)
	{
		this.paymentOptionsCreationStrategyMap = paymentOptionsCreationStrategyMap;
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
	@Required
	public void setTravellerService(final TravellerService travellerService)
	{
		this.travellerService = travellerService;
	}

	/**
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}
}
