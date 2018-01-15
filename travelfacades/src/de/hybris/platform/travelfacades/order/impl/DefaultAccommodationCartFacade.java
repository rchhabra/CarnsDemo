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

package de.hybris.platform.travelfacades.order.impl;

import de.hybris.platform.commercefacades.accommodation.RoomRateCartData;
import de.hybris.platform.commercefacades.accommodation.search.RoomStayCandidateData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.packages.cart.AddDealToCartData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.travel.AddToCartResponseData;
import de.hybris.platform.commercefacades.travel.TravellerData;
import de.hybris.platform.commercefacades.travel.ancillary.accommodation.data.ConfiguredAccommodationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.configurablebundlefacades.order.BundleCartFacade;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelfacades.facades.ConfiguredAccommodationFacade;
import de.hybris.platform.travelfacades.facades.TravelCommercePriceFacade;
import de.hybris.platform.travelfacades.facades.TravelRestrictionFacade;
import de.hybris.platform.travelfacades.facades.TravellerFacade;
import de.hybris.platform.travelfacades.facades.accommodation.forms.AccommodationAddToCartForm;
import de.hybris.platform.travelfacades.order.AccommodationCartFacade;
import de.hybris.platform.travelfacades.order.TravelCartFacade;
import de.hybris.platform.travelfacades.strategies.SelectedAccommodationStrategy;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.enums.ProductType;
import de.hybris.platform.travelservices.model.accommodation.ConfiguredAccommodationModel;
import de.hybris.platform.travelservices.model.accommodation.DateRangeModel;
import de.hybris.platform.travelservices.model.accommodation.RoomRateProductModel;
import de.hybris.platform.travelservices.model.deal.AccommodationBundleTemplateModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.order.TravelCartService;
import de.hybris.platform.travelservices.price.data.PriceLevel;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.services.accommodationmap.AccommodationMapService;
import de.hybris.platform.travelservices.utils.TravelDateUtils;
import de.hybris.platform.tx.Transaction;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationCartFacade}
 */
public class DefaultAccommodationCartFacade extends DefaultCartFacade implements AccommodationCartFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultAccommodationCartFacade.class);

	private AccommodationCommerceCartService accommodationCommerceCartService;
	private BookingService bookingService;
	private TravelCartService travelCartService;
	private TravelCartFacade cartFacade;
	private ModelService modelService;
	private EnumerationService enumerationService;
	private BundleCartFacade bundleCartFacade;
	private ConfiguredAccommodationFacade configuredAccommodationFacade;
	private AccommodationMapService accommodationMapService;
	private TravelCommercePriceFacade travelCommercePriceFacade;
	private TravelRestrictionFacade travelRestrictionFacade;
	private TravellerFacade travellerFacade;

	private List<SelectedAccommodationStrategy> selectedAccommodationStrategyList;

	/**
	 * This constant value dictates the quantity of the products to be added in the cart.
	 */
	protected static final long PRODUCT_QUANTITY = 1;


	@Override
	public void addAccommodationToCart(final Date checkInDate, final Date checkOutDate, final String accommodationOfferingCode,
			final String accommodationCode, final List<RoomRateCartData> rates, final int numberOfRooms, final String ratePlanCode)
			throws CommerceCartModificationException
	{
		final int currentMaxRefNumber = getAccommodationCommerceCartService().getMaxRoomStayRefNumber();
		final int newRefNumber = currentMaxRefNumber != -1 ? currentMaxRefNumber + 1 : 0;

		for (int i = 0; i < numberOfRooms; i++)
		{
			final List<Integer> entryNumbers = new ArrayList<>(rates.size());
			for (final RoomRateCartData rate : rates)
			{
				addRoomRateToCart(rate, entryNumbers, accommodationCode, accommodationOfferingCode, ratePlanCode);
			}

			getAccommodationCommerceCartService().createOrderEntryGroup(entryNumbers, checkInDate, checkOutDate,
					accommodationOfferingCode, accommodationCode, ratePlanCode, newRefNumber + i);
		}
	}

	@Override
	public void addAccommodationsToCart(final Date checkInDate, final Date checkOutDate, final String accommodationOfferingCode,
			final String accommodationCode, final List<RoomRateCartData> rates, final int numberOfRooms, final String ratePlanCode)
			throws CommerceCartModificationException
	{
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getAccommodationCommerceCartService()
				.getNewAccommodationOrderEntryGroups(accommodationOfferingCode, accommodationCode, ratePlanCode);
		final int roomQuantityToAdd = numberOfRooms - CollectionUtils.size(accommodationOrderEntryGroups);

		if (roomQuantityToAdd > 0)
		{
			addAccommodationToCart(checkInDate, checkOutDate, accommodationOfferingCode, accommodationCode, rates, roomQuantityToAdd,
					ratePlanCode);
		}
		else if (roomQuantityToAdd < 0)
		{
			getAccommodationCommerceCartService().removeAccommodationOrderEntryGroups(accommodationOrderEntryGroups,
					Math.abs(roomQuantityToAdd));
		}
	}

	@Override
	public Boolean addAccommodationsToCart(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup,
			final String accommodationOfferingCode, final String accommodationCode, final List<RoomRateCartData> rates,
			final int numberOfRooms, final String ratePlanCode, final String paymentType) throws CommerceCartModificationException
	{
		for (int i = 0; i < numberOfRooms; i++)
		{
			final List<Integer> entryNumbers = new ArrayList<>(rates.size());
			for (final RoomRateCartData rate : rates)
			{
				addRoomRateToCart(rate, entryNumbers, accommodationCode, accommodationOfferingCode, ratePlanCode);
			}

			final boolean isSuccess = getAccommodationCommerceCartService().amendOrderEntryGroup(accommodationOrderEntryGroup,
					entryNumbers, paymentType);
			if (!isSuccess)
			{
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

	@Override
	public void rollbackAccommodationEntries(final String accommodationCode, final String accommodationOfferingCode,
			final String ratePlanCode)
	{
		getAccommodationCommerceCartService().rollbackAccommodationEntries(accommodationCode, accommodationOfferingCode,
				ratePlanCode);
	}

	@Override
	public void cleanUpCartBeforeAddition(final String accommodationOfferingCode, final String checkInDateTime,
			final String checkOutDateTime)
	{
		getAccommodationCommerceCartService().cleanupCartBeforeAddition(accommodationOfferingCode, checkInDateTime,
				checkOutDateTime);
	}

	@Override
	public void cleanUpCartBeforeAddition(final String accommodationOfferingCode, final String checkInDate,
			final String checkOutDate, final List<RoomStayCandidateData> roomStayCandidates)
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(sessionCart);

		if (!getAccommodationCommerceCartService().validateCart(accommodationOfferingCode, checkInDate, checkOutDate, entryGroups)
				|| !validateRoomStayCandidates(roomStayCandidates, entryGroups))
		{
			removeSessionCart();
		}
	}

	protected boolean validateRoomStayCandidates(final List<RoomStayCandidateData> roomStayCandidates,
			final List<AccommodationOrderEntryGroupModel> entryGroups)
	{
		if (CollectionUtils.size(entryGroups) != CollectionUtils.size(roomStayCandidates))
		{
			return Boolean.FALSE;
		}

		for (final RoomStayCandidateData roomStayCandidate : roomStayCandidates)
		{
			final Optional<AccommodationOrderEntryGroupModel> entryForCandidate = entryGroups.stream().filter(
					entryGroup -> Objects.equals(entryGroup.getRoomStayRefNumber(), roomStayCandidate.getRoomStayCandidateRefNumber()))
					.findAny();

			if (!entryForCandidate.isPresent())
			{
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	@Override
	public void emptyCart()
	{
		getAccommodationCommerceCartService().emptyCart();
	}

	@Override
	public boolean validateNumberOfRoomsToAdd(final String accommodationOfferingCode, final String accommodationCode,
			final String ratePlanCode, final int numberOfRooms, final int allowedNumberOfRooms)
	{
		final int numberOfEntryGroupsInCart = getAccommodationCommerceCartService().getNumberOfEntryGroupsInCart();
		final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getAccommodationCommerceCartService()
				.getNewAccommodationOrderEntryGroups(accommodationOfferingCode, accommodationCode, ratePlanCode);
		final int roomQuantityToAdd = numberOfRooms - CollectionUtils.size(accommodationOrderEntryGroups);

		return (numberOfEntryGroupsInCart + roomQuantityToAdd) <= allowedNumberOfRooms;
	}

	/**
	 * Overriding the method to force creation of new entry. This is because we need a separate entry of the same product
	 * to represent a different accommodation order entry group.
	 *
	 * @param code
	 * @param quantity
	 * @return
	 * @throws CommerceCartModificationException
	 */
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
	 * Add room rate to cart.
	 *
	 * @param rate
	 *           the rate
	 * @param entryNumbers
	 *           the entry numbers
	 * @param accCode
	 *           the acc code
	 * @param accOffCode
	 *           the acc off code
	 * @param ratePlanCode
	 *           the rate plan code
	 * @throws CommerceCartModificationException
	 *            the commerce cart modification exception
	 */
	protected void addRoomRateToCart(final RoomRateCartData rate, final List<Integer> entryNumbers, final String accCode,
			final String accOffCode, final String ratePlanCode) throws CommerceCartModificationException
	{
		final CartModificationData cartModification = addToCart(rate.getCode(), rate.getCardinality());
		if (cartModification.getQuantityAdded() < rate.getCardinality())
		{
			rollbackAccommodationEntries(accCode, accOffCode, ratePlanCode);
			throw new CommerceCartModificationException("The request cannot be entirely fulfilled");
		}
		getAccommodationCommerceCartService()
				.populateAccommodationDetailsOnRoomRateEntry(cartModification.getEntry().getEntryNumber(), rate.getDates());
		entryNumbers.add(cartModification.getEntry().getEntryNumber());
	}

	@Override
	public void addProductToCart(final String productCode, final int roomStayReferenceNumber, final long quantity)
			throws CommerceCartModificationException
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayReferenceNumber, getTravelCartService().getSessionCart());

		if (accommodationOrderEntryGroupModel == null)
		{
			return;
		}

		final Optional<AbstractOrderEntryModel> existingEntryOptional = accommodationOrderEntryGroupModel.getEntries().stream()
				.filter(entry -> entry.getProduct().getCode().equals(productCode)).findAny();

		if (!existingEntryOptional.isPresent())
		{
			if (quantity <= 0)
			{
				return;
			}
			//createNewOrderEntry
			final CartModificationData cartModificationData = addToCart(productCode, quantity);

			final AbstractOrderEntryModel newOrderEntry = getCartService().getEntryForNumber(getCartService().getSessionCart(),
					cartModificationData.getEntry().getEntryNumber());

			newOrderEntry.setType(OrderEntryType.ACCOMMODATION);
			newOrderEntry.setAmendStatus(AmendStatus.NEW);
			newOrderEntry.setActive(Boolean.TRUE);
			getModelService().save(newOrderEntry);

			final List<AbstractOrderEntryModel> groupEntries = new ArrayList<>(accommodationOrderEntryGroupModel.getEntries());
			groupEntries.add(newOrderEntry);
			accommodationOrderEntryGroupModel.setEntries(groupEntries);
			getModelService().save(accommodationOrderEntryGroupModel);

		}
		else
		{
			// update cartEntry with newQuantity
			final AbstractOrderEntryModel existingOrderEntry = existingEntryOptional.get();
			final long newQuantity = quantity;

			try
			{
				getCartFacade().updateCartEntry(existingOrderEntry.getEntryNumber(), newQuantity);

			}
			catch (final CommerceCartModificationException e)
			{
				LOG.error("Couldn't update product with the entry number: " + existingOrderEntry.getEntryNumber() + ".", e);
				throw e;
			}
		}

	}

	@Override
	public List<RoomRateCartData> collectRoomRates(final AccommodationAddToCartForm form)
	{
		final List<RoomRateCartData> rates = new ArrayList<>();

		final List<String> roomRateCodes = form.getRoomRateCodes();
		final List<String> roomRateDates = form.getRoomRateDates();

		if (CollectionUtils.size(roomRateCodes) != CollectionUtils.size(roomRateDates))
		{
			return Collections.emptyList();
		}

		for (int i = 0; i < CollectionUtils.size(roomRateCodes); i++)
		{
			final String roomRateCode = roomRateCodes.get(i);
			final String roomRateDate = roomRateDates.get(i);

			final Optional<RoomRateCartData> optionalRate = rates.stream()
					.filter(rate -> StringUtils.equalsIgnoreCase(rate.getCode(), roomRateCode)).findFirst();

			if (optionalRate.isPresent())
			{
				final RoomRateCartData rate = optionalRate.get();
				rate.setCardinality(rate.getCardinality() + 1);
				rate.getDates().add(TravelDateUtils.convertStringDateToDate(roomRateDate, TravelservicesConstants.DATE_PATTERN));
			}
			else
			{
				final RoomRateCartData rate = new RoomRateCartData();
				rate.setCode(roomRateCode);
				rate.setCardinality(1);
				final List<Date> dates = new ArrayList<>();
				dates.add(TravelDateUtils.convertStringDateToDate(roomRateDate, TravelservicesConstants.DATE_PATTERN));
				rate.setDates(dates);
				rates.add(rate);
			}
		}

		return rates;
	}

	@Override
	public Boolean isAmendmentForServices()
	{
		final CartModel cartModel = getTravelCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> currentCartAccommodationOrderEntryGroupModels = getBookingService()
				.getAccommodationOrderEntryGroups(cartModel);
		if (Objects.isNull(cartModel.getOriginalOrder()))
		{
			return false;
		}
		final List<AccommodationOrderEntryGroupModel> originalOrderAccommodationOrderEntryGroupModels = getBookingService()
				.getAccommodationOrderEntryGroups(cartModel.getOriginalOrder());
		return CollectionUtils.size(currentCartAccommodationOrderEntryGroupModels) == CollectionUtils
				.size(originalOrderAccommodationOrderEntryGroupModels);
	}

	@Override
	public List<CartModificationData> addAccommodationBundleToCart(
			final AccommodationBundleTemplateModel accommodationBundleTemplateModel, final AddDealToCartData addDealToCartData)
			throws CommerceCartModificationException
	{
		final List<CartModificationData> cartModificationDatas = new ArrayList<>();
		final List<RoomRateCartData> roomRates = collectRoomRates(accommodationBundleTemplateModel, addDealToCartData);
		if (CollectionUtils.isEmpty(roomRates))
		{
			throw new CommerceCartModificationException("No room rate can be added to the cart. Aborting operation");
		}

		for (final RoomRateCartData roomRate : roomRates)
		{
			final CartModificationData cartModification = CollectionUtils.isEmpty(cartModificationDatas)
					? getBundleCartFacade().startBundle(accommodationBundleTemplateModel.getId(), roomRate.getCode(),
							roomRate.getCardinality())
					: getBundleCartFacade().addToCart(roomRate.getCode(), roomRate.getCardinality(),
							cartModificationDatas.stream().findAny().get().getEntry().getEntryGroupNumbers().stream().findFirst().get());
			getAccommodationCommerceCartService()
					.populateAccommodationDetailsOnRoomRateEntry(cartModification.getEntry().getEntryNumber(), roomRate.getDates());
			cartModificationDatas.add(cartModification);
		}

		getAccommodationCommerceCartService().createOrderEntryGroup(
				cartModificationDatas.stream().map(cartModification -> cartModification.getEntry().getEntryNumber())
						.collect(Collectors.toList()),
				addDealToCartData.getStartingDate(), addDealToCartData.getEndingDate(),
				accommodationBundleTemplateModel.getAccommodationOffering().getCode(),
				accommodationBundleTemplateModel.getAccommodation().getCode(),
				accommodationBundleTemplateModel.getRatePlan().getCode(), 0);

		return cartModificationDatas;

	}

	@Override
	public void replaceAccommodationInCart(final Date checkInDate, final Date checkOutDate, final String accommodationOfferingCode,
			final String accommodationCode, final List<RoomRateCartData> rates, final int numberOfRooms, final String ratePlanCode,
			final Integer roomStayRefNumber) throws CommerceCartModificationException
	{
		// Transaction is used to revert the removal of room stay from cart in case adding a new option failed.
		final Transaction tx = Transaction.current();

		try
		{
			tx.begin();

			getAccommodationCommerceCartService().removeRoomStay(roomStayRefNumber);

			for (int i = 0; i < numberOfRooms; i++)
			{
				final List<Integer> entryNumbers = new ArrayList<>(rates.size());
				for (final RoomRateCartData rate : rates)
				{
					addRoomRateToCart(rate, entryNumbers, accommodationCode, accommodationOfferingCode, ratePlanCode);
				}

				getAccommodationCommerceCartService().createOrderEntryGroup(entryNumbers, checkInDate, checkOutDate,
						accommodationOfferingCode, accommodationCode, ratePlanCode, roomStayRefNumber);
			}
		}
		catch (final CommerceCartModificationException ex)
		{
			tx.rollback();
			throw ex;
		}

		if (tx.isRunning())
		{
			tx.commit();
		}

	}

	protected List<RoomRateCartData> collectRoomRates(final AccommodationBundleTemplateModel accommodationBundleTemplate,
			final AddDealToCartData addDealToCartData)
	{
		Instant instant = Instant.ofEpochMilli(addDealToCartData.getStartingDate().getTime());
		final LocalDateTime startDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		instant = Instant.ofEpochMilli(addDealToCartData.getEndingDate().getTime());
		final LocalDateTime endDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		final List<RoomRateCartData> roomRates = new ArrayList<>();
		Stream.iterate(startDate, date -> date.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate)).forEach(date -> {
			final Optional<ProductModel> optionalRoomRate = accommodationBundleTemplate.getProducts().stream().filter(
					roomRate -> validateRoomRateAgainstDate(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()), roomRate))
					.findFirst();
			if (optionalRoomRate.isPresent())
			{
				if (roomRates.stream().map(RoomRateCartData::getCode).collect(Collectors.toList())
						.contains(optionalRoomRate.get().getCode()))
				{
					final RoomRateCartData roomRateCartData = roomRates.stream()
							.filter(roomRate -> roomRate.getCode().equals(optionalRoomRate.get().getCode())).findFirst().get();
					roomRateCartData.setCardinality(roomRateCartData.getCardinality() + 1);
					roomRateCartData.getDates().add(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

				}
				else
				{
					roomRates.add(buildNewRoomRateCartData(optionalRoomRate.get(), date));
				}
			}
		});
		return roomRates;
	}

	protected boolean validateRoomRateAgainstDate(final Date date, final ProductModel roomRate)
	{
		final RoomRateProductModel roomRateProduct = (RoomRateProductModel) roomRate;
		for (final DateRangeModel dateRange : roomRateProduct.getDateRanges())
		{
			if (!date.before(dateRange.getStartingDate()) && !date.after(dateRange.getEndingDate())
					&& isValidDayOfWeek(date, roomRateProduct.getDaysOfWeek()))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean isValidDayOfWeek(final Date date, final List<DayOfWeek> daysOfWeek)
	{
		final LocalDate localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
		final DayOfWeek dayOfWeek = getEnumerationService().getEnumerationValue(DayOfWeek.class,
				localDate.getDayOfWeek().toString());
		return daysOfWeek.contains(dayOfWeek);
	}

	protected RoomRateCartData buildNewRoomRateCartData(final ProductModel productModel, final LocalDateTime date)
	{
		final RoomRateCartData roomRateCartData = new RoomRateCartData();
		roomRateCartData.setCode(productModel.getCode());
		roomRateCartData.setCardinality(1);
		roomRateCartData.setDates(new ArrayList<Date>(Arrays.asList(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()))));
		return roomRateCartData;
	}

	@Override
	public boolean isNewRoomInCart()
	{
		return getAccommodationCommerceCartService().isNewRoomInCart();
	}

	@Override
	public Boolean removeAccommodationOrderEntryGroup(final int roomStayReference)
	{
		return getAccommodationCommerceCartService().removeAccommodationOrderEntryGroup(roomStayReference);
	}

	@Override
	public boolean validateAccommodationCart()
	{
		return getAccommodationCommerceCartService().getNumberOfEntryGroupsInCart() > 0;
	}

	@Override
	public String getCurrentAccommodationOffering()
	{
		return getAccommodationCommerceCartService().getCurrentAccommodationOffering();
	}

	@Override
	public boolean addSelectedAccommodationToCart(final String accommodationUid, final String transportOfferingCode,
			final String travellerCode, final String originDestinationRefNo, final String travelRoute)
	{
		if (StringUtils.isEmpty(accommodationUid) || StringUtils.isEmpty(transportOfferingCode) || StringUtils.isEmpty(travellerCode)
				|| StringUtils.isEmpty(originDestinationRefNo) || StringUtils.isEmpty(travelRoute))
		{
			return Boolean.FALSE;
		}

		for (final SelectedAccommodationStrategy strategy : getSelectedAccommodationStrategyList())
		{
			final AddToCartResponseData response = strategy.validateSelectedAccommodation(accommodationUid, StringUtils.EMPTY,
					transportOfferingCode, travellerCode, originDestinationRefNo, travelRoute);

			if (!response.isValid())
			{
				return Boolean.FALSE;
			}
		}

		final ConfiguredAccommodationModel accommodation = getAccommodationMapService().getAccommodation(accommodationUid);
		final TravellerData traveller = getTravellerFacade().getTravellerFromCurrentCart(travellerCode);
		final Boolean isAccommodationAvailableForBooking = getAccommodationMapService()
				.isAccommodationAvailableForBooking(accommodation, transportOfferingCode, traveller);

		if (!isAccommodationAvailableForBooking || !hasSessionCart())
		{
			return Boolean.FALSE;
		}

		final CartModel cart = getCartService().getSessionCart();

		if (getAccommodationMapService().isSeatInCart(cart, accommodation) || !getAccommodationMapService()
				.isSeatProductReferencedByFareProductInOrder(accommodation.getProduct(), transportOfferingCode, cart))
		{
			return Boolean.FALSE;
		}


		final List<String> transportOfferingCodes = new ArrayList<>();
		transportOfferingCodes.add(transportOfferingCode);
		final ProductModel associatedProduct = accommodation.getProduct();

		if (Objects.nonNull(associatedProduct) && (associatedProduct instanceof AccommodationModel
				|| ProductType.ACCOMMODATION.equals(associatedProduct.getProductType())))
		{
			final String productCode = associatedProduct.getCode();
			final OrderEntryData existingOrderEntry = getCartFacade().getOrderEntry(productCode, travelRoute, transportOfferingCodes,
					travellerCode, false);
			if (Objects.isNull(existingOrderEntry))
			{
				final long quantity = 1;
				try
				{
					final PriceLevel priceLevel = getTravelCommercePriceFacade().getPriceLevelInfo(productCode, transportOfferingCodes,
							travelRoute);
					if (Objects.isNull(priceLevel))
					{
						return Boolean.FALSE;
					}
					getTravelCommercePriceFacade().setPriceAndTaxSearchCriteriaInContext(priceLevel, transportOfferingCodes,
							traveller);

					final CartModificationData cartModification = getCartFacade().addToCart(productCode, quantity);
					getCartFacade().setOrderEntryType(OrderEntryType.TRANSPORT, cartModification.getEntry().getEntryNumber());

					getTravelCommercePriceFacade().addPropertyPriceLevelToCartEntry(priceLevel,
							cartModification.getEntry().getProduct().getCode(), cartModification.getEntry().getEntryNumber());
					if (cartModification.getQuantityAdded() >= PRODUCT_QUANTITY)
					{
						addPropertiesToCartEntry(travellerCode, Integer.parseInt(originDestinationRefNo), travelRoute,
								transportOfferingCodes, Boolean.TRUE, AmendStatus.NEW, cartModification);
						//Cart recalculation is needed after persisting pricelevel, traveller details, travel route and transport offering codes against cart entry
						//to make "pricing search criteria in context" logic work. Reason is that, OOTB cart calculation gets triggered while creation of a cart entry,
						//and by that time due to missing travel specific details (pricelevel, traveller details, travel route and transport offering codes), incorrect
						//cart calculation results are produced.
						getCartFacade().recalculateCart();
					}
					else if (cartModification.getQuantityAdded() == 0L)
					{
						return Boolean.FALSE;
					}

				}
				catch (final CommerceCartModificationException ex)
				{
					LOG.info(ex.getMessage(), ex);
					return Boolean.FALSE;
				}
			}
			else if (existingOrderEntry.getQuantity() == 0L)
			{
				try
				{
					final long newQuantity = 1L;
					final CartModificationData cartModification = getCartFacade().updateCartEntry(existingOrderEntry.getEntryNumber(),
							newQuantity);

					if (cartModification.getQuantityAdded() == newQuantity)
					{
						LOG.debug("Product Code:" + cartModification.getEntry().getProduct().getCode()
								+ "has been update with Quantity:" + cartModification.getQuantityAdded());
					}
				}
				catch (final CommerceCartModificationException ex)
				{
					LOG.debug("Couldn't update product with the entry number: " + existingOrderEntry.getEntryNumber() + ".", ex);
				}
			}
		}

		getCartFacade().addSelectedAccommodationToCart(transportOfferingCode, travellerCode, accommodationUid);
		return Boolean.TRUE;
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
		final String addToCartCriteria = getTravelRestrictionFacade().getAddToCartCriteria(productCode);

		getCartFacade().addPropertiesToCartEntry(productCode, cartModification.getEntry().getEntryNumber(), transportOfferingCodes,
				travelRoute, originDestinationRefNo, travellerCode, active, amendStatus, addToCartCriteria);
	}

	@Override
	public boolean removeSelectedAccommodationFromCart(final String accommodationUid, final String transportOfferingCode,
			final String travellerCode, final String travelRoute)
	{
		if (StringUtils.isEmpty(accommodationUid) || StringUtils.isEmpty(transportOfferingCode)
				|| StringUtils.isEmpty(travellerCode) || StringUtils.isEmpty(travelRoute))
		{
			return Boolean.FALSE;
		}

		for (final SelectedAccommodationStrategy strategy : getSelectedAccommodationStrategyList())
		{
			final AddToCartResponseData response = strategy.validateSelectedAccommodation(accommodationUid, null,
					transportOfferingCode, travellerCode, null, travelRoute);

			if (!response.isValid())
			{
				return Boolean.FALSE;
			}
		}

		final ConfiguredAccommodationData accommodation = getConfiguredAccommodationFacade().getAccommodation(accommodationUid);
		final ProductData associatedProduct = accommodation.getProduct();
		if (associatedProduct != null)
		{
			final List<String> transportOfferingCodes = new ArrayList<>();
			transportOfferingCodes.add(transportOfferingCode);
			final String productCode = associatedProduct.getCode();
			final OrderEntryData existingOrderEntry = getCartFacade().getOrderEntry(productCode, travelRoute, transportOfferingCodes,
					travellerCode, false);
			if (existingOrderEntry != null)
			{
				final int bundleNo = existingOrderEntry.getBundleNo();
				//remove cart entry only if it does not belong to a bundle
				if (bundleNo == 0)
				{
					try
					{
						getCartFacade().updateCartEntry(existingOrderEntry.getEntryNumber(), 0);
					}
					catch (final CommerceCartModificationException ex)
					{
						LOG.info(ex.getMessage(), ex);
						return Boolean.FALSE;
					}
				}
			}
		}
		getCartFacade().removeSelectedAccommodationFromCart(transportOfferingCode, travellerCode, accommodationUid);
		return Boolean.TRUE;
	}

	/**
	 * Gets accommodation commerce cart service.
	 *
	 * @return the accommodationCommerceCartService
	 */
	protected AccommodationCommerceCartService getAccommodationCommerceCartService()
	{
		return accommodationCommerceCartService;
	}

	/**
	 * Sets accommodation commerce cart service.
	 *
	 * @param accommodationCommerceCartService
	 *           the accommodationCommerceCartService to set
	 */
	@Required
	public void setAccommodationCommerceCartService(final AccommodationCommerceCartService accommodationCommerceCartService)
	{
		this.accommodationCommerceCartService = accommodationCommerceCartService;
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
	 *           the bookingService to set
	 */
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
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
	 *           the travelCartService to set
	 */
	@Required
	public void setTravelCartService(final TravelCartService travelCartService)
	{
		this.travelCartService = travelCartService;
	}

	/**
	 * Gets cart facade.
	 *
	 * @return the cartFacade
	 */
	protected TravelCartFacade getCartFacade()
	{
		return cartFacade;
	}

	/**
	 * Sets cart facade.
	 *
	 * @param cartFacade
	 *           the cartFacade to set
	 */
	@Required
	public void setCartFacade(final TravelCartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	/**
	 * Gets model service.
	 *
	 * @return the modelService
	 */
	@Override
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets model service.
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 *
	 * @return the enumerationService
	 */
	protected EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	/**
	 *
	 * @param enumerationService
	 *           the enumerationService to set
	 */
	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	/**
	 *
	 * @return the bundleCartFacade
	 */
	protected BundleCartFacade getBundleCartFacade()
	{
		return bundleCartFacade;
	}

	/**
	 *
	 * @param bundleCartFacade
	 *           the bundleCartFacade to set
	 */
	@Required
	public void setBundleCartFacade(final BundleCartFacade bundleCartFacade)
	{
		this.bundleCartFacade = bundleCartFacade;
	}

	/**
	 * @return the selectedAccommodationStrategyList
	 */
	protected List<SelectedAccommodationStrategy> getSelectedAccommodationStrategyList()
	{
		return selectedAccommodationStrategyList;
	}

	/**
	 * @param selectedAccommodationStrategyList
	 *           the selectedAccommodationStrategyList to set
	 */
	@Required
	public void setSelectedAccommodationStrategyList(final List<SelectedAccommodationStrategy> selectedAccommodationStrategyList)
	{
		this.selectedAccommodationStrategyList = selectedAccommodationStrategyList;
	}

	/**
	 * @return the configuredAccommodationFacade
	 */
	protected ConfiguredAccommodationFacade getConfiguredAccommodationFacade()
	{
		return configuredAccommodationFacade;
	}

	/**
	 * @param configuredAccommodationFacade
	 *           the configuredAccommodationFacade to set
	 */
	@Required
	public void setConfiguredAccommodationFacade(final ConfiguredAccommodationFacade configuredAccommodationFacade)
	{
		this.configuredAccommodationFacade = configuredAccommodationFacade;
	}

	/**
	 * @return the accommodationMapService
	 */
	protected AccommodationMapService getAccommodationMapService()
	{
		return accommodationMapService;
	}

	/**
	 * @param accommodationMapService
	 *           the accommodationMapService to set
	 */
	@Required
	public void setAccommodationMapService(final AccommodationMapService accommodationMapService)
	{
		this.accommodationMapService = accommodationMapService;
	}

	/**
	 * @return the travelCommercePriceFacade
	 */
	protected TravelCommercePriceFacade getTravelCommercePriceFacade()
	{
		return travelCommercePriceFacade;
	}

	/**
	 * @param travelCommercePriceFacade
	 *           the travelCommercePriceFacade to set
	 */
	@Required
	public void setTravelCommercePriceFacade(final TravelCommercePriceFacade travelCommercePriceFacade)
	{
		this.travelCommercePriceFacade = travelCommercePriceFacade;
	}

	/**
	 * @return the travelRestrictionFacade
	 */
	protected TravelRestrictionFacade getTravelRestrictionFacade()
	{
		return travelRestrictionFacade;
	}

	/**
	 * @param travelRestrictionFacade
	 *           the travelRestrictionFacade to set
	 */
	@Required
	public void setTravelRestrictionFacade(final TravelRestrictionFacade travelRestrictionFacade)
	{
		this.travelRestrictionFacade = travelRestrictionFacade;
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
	@Required
	public void setTravellerFacade(final TravellerFacade travellerFacade)
	{
		this.travellerFacade = travellerFacade;
	}
}
