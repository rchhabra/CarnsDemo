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

package de.hybris.platform.travelservices.order.impl;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.accommodation.RatePlanModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryInfoModel;
import de.hybris.platform.travelservices.model.product.AccommodationModel;
import de.hybris.platform.travelservices.order.AccommodationCommerceCartService;
import de.hybris.platform.travelservices.services.AccommodationOfferingService;
import de.hybris.platform.travelservices.services.BookingService;
import de.hybris.platform.travelservices.strategies.payment.changedates.ChangeDatesPaymentActionStrategy;
import de.hybris.platform.travelservices.utils.TravelDateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link AccommodationCommerceCartService}
 */
public class DefaultAccommodationCommerceCartService extends DefaultCommerceCartService
		implements AccommodationCommerceCartService
{
	private CartService cartService;
	private BookingService bookingService;
	private ProductService productService;
	private CategoryService categoryService;
	private AccommodationOfferingService accommodationOfferingService;
	private Map<String, ChangeDatesPaymentActionStrategy> changeDatesPaymentActionStrategyMap;

	@Override
	public void populateAccommodationDetailsOnRoomRateEntry(final int entryNumber, final List<Date> dates)
	{
		final AbstractOrderEntryModel entryToUpdate = getCartService().getEntryForNumber(getCartService().getSessionCart(),
				entryNumber);
		AccommodationOrderEntryInfoModel orderEntryInfo = entryToUpdate.getAccommodationOrderEntryInfo();
		if (orderEntryInfo == null)
		{
			orderEntryInfo = getModelService().create(AccommodationOrderEntryInfoModel.class);
			entryToUpdate.setAccommodationOrderEntryInfo(orderEntryInfo);
		}
		orderEntryInfo.setDates(dates);
		getModelService().save(orderEntryInfo);

		entryToUpdate.setType(OrderEntryType.ACCOMMODATION);
		entryToUpdate.setAmendStatus(AmendStatus.NEW);
		entryToUpdate.setActive(Boolean.TRUE);
		getModelService().save(entryToUpdate);
	}

	@Override
	public void createOrderEntryGroup(final List<Integer> entryNumbers, final Date checkInDate, final Date checkOutDate,
			final String accOffCode, final String accCode, final String ratePlanCode, final int refNumber)
	{
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup = getModelService()
				.create(AccommodationOrderEntryGroupModel.class);

		accommodationOrderEntryGroup.setRoomStayRefNumber(refNumber);

		accommodationOrderEntryGroup.setAccommodation((AccommodationModel) getProductService().getProductForCode(accCode));
		accommodationOrderEntryGroup
				.setAccommodationOffering(getAccommodationOfferingService().getAccommodationOffering(accOffCode));
		accommodationOrderEntryGroup.setRatePlan((RatePlanModel) getCategoryService().getCategoryForCode(ratePlanCode));
		accommodationOrderEntryGroup.setStartingDate(checkInDate);
		accommodationOrderEntryGroup.setEndingDate(checkOutDate);
		final CartModel sessionCart = getCartService().getSessionCart();
		accommodationOrderEntryGroup.setEntries(entryNumbers.stream()
				.map(number -> getCartService().getEntryForNumber(sessionCart, number)).collect(Collectors.toList()));
		getModelService().save(accommodationOrderEntryGroup);
	}

	@Override
	public boolean amendOrderEntryGroup(final AccommodationOrderEntryGroupModel accommodationOrderEntryGroup,
			final List<Integer> entryNumbers, final String paymentType)
	{
		return getChangeDatesPaymentActionStrategyMap().get(paymentType).takeAction(accommodationOrderEntryGroup, entryNumbers);
	}

	@Override
	public void rollbackAccommodationEntries(final String accommodationCode, final String accommodationOfferingCode,
			final String ratePlanCode)
	{
		final List<AccommodationOrderEntryGroupModel> affectedEntryGroups = getNewAccommodationOrderEntryGroups(
				accommodationOfferingCode, accommodationCode, ratePlanCode);
		removeAccommodationOrderEntryGroups(affectedEntryGroups, CollectionUtils.size(affectedEntryGroups));
	}

	@Override
	public void cleanupCartBeforeAddition(final String accommodationOfferingCode, final String checkInDateTime,
			final String checkOutDateTime)
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(sessionCart);

		if (!validateCart(accommodationOfferingCode, checkInDateTime, checkOutDateTime, entryGroups))
		{
			removeAccommodationOrderEntryGroups(entryGroups, CollectionUtils.size(entryGroups));
		}
	}

	@Override
	public boolean validateCart(final String accommodationOfferingCode, final String checkInDateTime,
			final String checkOutDateTime, final List<AccommodationOrderEntryGroupModel> entryGroups)
	{
		final Date checkInDate = TravelDateUtils.convertStringDateToDate(checkInDateTime, TravelservicesConstants.DATE_PATTERN);
		final Date checkOutDate = TravelDateUtils.convertStringDateToDate(checkOutDateTime, TravelservicesConstants.DATE_PATTERN);

		if (CollectionUtils.isEmpty(entryGroups) || checkInDate == null || checkOutDate == null)
		{
			return Boolean.TRUE;
		}

		return StringUtils.equalsIgnoreCase(entryGroups.get(0).getAccommodationOffering().getCode(), accommodationOfferingCode)
				&& DateUtils.isSameDay(checkInDate, entryGroups.get(0).getStartingDate())
				&& DateUtils.isSameDay(checkOutDate, entryGroups.get(0).getEndingDate());
	}

	@Override
	public void emptyCart()
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(sessionCart);

		if (CollectionUtils.isEmpty(entryGroups))
		{
			return;
		}

		removeAccommodationOrderEntryGroups(entryGroups, CollectionUtils.size(entryGroups));
	}

	@Override
	public int getNumberOfEntryGroupsInCart()
	{
		if (!getCartService().hasSessionCart())
		{
			return 0;
		}
		final CartModel sessionCart = getCartService().getSessionCart();
		return CollectionUtils.size(getBookingService().getAccommodationOrderEntryGroups(sessionCart));
	}

	@Override
	public List<AccommodationOrderEntryGroupModel> getNewAccommodationOrderEntryGroups(final String accommodationOfferingCode,
			final String accommodationCode, final String ratePlanCode)
	{
		if (!getCartService().hasSessionCart())
		{
			return Collections.emptyList();
		}
		final CartModel sessionCart = getCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(sessionCart);

		if (CollectionUtils.isEmpty(entryGroups))
		{
			return Collections.emptyList();
		}

		final List<AccommodationOrderEntryGroupModel> matchingEntryGroups = entryGroups.stream()
				.filter(entryGroup -> StringUtils.equalsIgnoreCase(entryGroup.getAccommodation().getCode(), accommodationCode)
						&& StringUtils.equalsIgnoreCase(entryGroup.getAccommodationOffering().getCode(), accommodationOfferingCode)
						&& StringUtils.equalsIgnoreCase(entryGroup.getRatePlan().getCode(), ratePlanCode))
				.collect(Collectors.toList());

		final List<AccommodationOrderEntryGroupModel> newEntryGroups = new ArrayList<>();

		matchingEntryGroups.forEach(entryGroup -> {
			final Optional<AbstractOrderEntryModel> optionalNotNewEntry = entryGroup.getEntries().stream()
					.filter(entry -> !AmendStatus.NEW.equals(entry.getAmendStatus())).findAny();
			if (!optionalNotNewEntry.isPresent())
			{
				newEntryGroups.add(entryGroup);
			}
		});

		return newEntryGroups;
	}

	@Override
	public List<CartEntryModel> getEntriesForProductAndAccommodation(final CartModel cartModel, final ProductModel product,
			final CartEntryModel cartEntryModel)
	{
		if (Objects.isNull(cartModel) || Objects.isNull(product) || Objects.isNull(cartEntryModel)
				|| Objects.isNull(cartEntryModel.getEntryGroup())
				|| !(cartEntryModel.getEntryGroup() instanceof AccommodationOrderEntryGroupModel)
				|| CollectionUtils.isEmpty(cartModel.getEntries()))
		{
			return Collections.emptyList();
		}

		final AccommodationOrderEntryGroupModel entryGroup = (AccommodationOrderEntryGroupModel) cartEntryModel.getEntryGroup();
		return cartModel.getEntries().stream()
				.filter(entry -> entry instanceof CartEntryModel && Objects.nonNull(entry.getEntryGroup())
						&& entry.getEntryGroup() instanceof AccommodationOrderEntryGroupModel
						&& StringUtils.equalsIgnoreCase(product.getCode(), entry.getProduct().getCode())
						&& StringUtils.equalsIgnoreCase(entryGroup.getAccommodation().getCode(),
								((AccommodationOrderEntryGroupModel) entry.getEntryGroup()).getAccommodation().getCode())
						&& StringUtils.equalsIgnoreCase(entryGroup.getAccommodationOffering().getCode(),
								((AccommodationOrderEntryGroupModel) entry.getEntryGroup()).getAccommodationOffering().getCode()))
				.map(entry -> (CartEntryModel) entry).collect(Collectors.toList());
	}

	@Override
	public int getMaxRoomStayRefNumber()
	{
		final CartModel sessionCart = getCartService().getSessionCart();
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(sessionCart);

		if (CollectionUtils.isEmpty(entryGroups))
		{
			return -1;
		}

		final OptionalInt maxRefNumberOptional = entryGroups.stream()
				.mapToInt(AccommodationOrderEntryGroupModel::getRoomStayRefNumber).max();

		return maxRefNumberOptional.isPresent() ? maxRefNumberOptional.getAsInt() : -1;
	}

	@Override
	public void removeAccommodationOrderEntryGroups(final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups,
			final int numberToRemove)
	{
		if (numberToRemove == CollectionUtils.size(accommodationOrderEntryGroups))
		{
			removeEntryGroupsFromCart(accommodationOrderEntryGroups);
		}
		else
		{
			final int listSize = CollectionUtils.size(accommodationOrderEntryGroups);
			final List<AccommodationOrderEntryGroupModel> entryGroupsToRemove = accommodationOrderEntryGroups
					.subList(listSize - numberToRemove, listSize);

			removeEntryGroupsFromCart(entryGroupsToRemove);
		}

		final CartModel sessionCart = getCartService().getSessionCart();
		getCommerceCartCalculationStrategy().calculateCart(sessionCart);

		normalizeRoomStayRefNumbers(sessionCart);
	}

	protected void removeEntryGroupsFromCart(final List<AccommodationOrderEntryGroupModel> entryGroupsToRemove)
	{
		entryGroupsToRemove.forEach(entryGroup -> getModelService().removeAll(entryGroup.getEntries()));
		getModelService().removeAll(entryGroupsToRemove);
	}

	@Override
	public Boolean removeAccommodationOrderEntryGroup(final int roomStayReference)
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayReference, cartModel);

		if (accommodationOrderEntryGroupModel == null)
		{
			return Boolean.FALSE;
		}

		removeAccommodationOrderEntryGroups(Collections.singletonList(accommodationOrderEntryGroupModel), 1);

		return Boolean.TRUE;
	}

	@Override
	public String getCurrentAccommodationOffering()
	{
		if (getCartService().hasSessionCart())
		{
			final CartModel sessionCart = getCartService().getSessionCart();
			final List<AccommodationOrderEntryGroupModel> accommodationOrderEntryGroups = getBookingService()
					.getAccommodationOrderEntryGroups(sessionCart);

			if (CollectionUtils.isNotEmpty(accommodationOrderEntryGroups))
			{
				return accommodationOrderEntryGroups.get(0).getAccommodationOffering().getCode();
			}
		}
		return null;
	}

	@Override
	public void removeRoomStay(final Integer roomStayRefNumber)
	{
		if (!getCartService().hasSessionCart())
		{
			return;
		}

		final CartModel cartModel = getCartService().getSessionCart();
		final AccommodationOrderEntryGroupModel accommodationOrderEntryGroupModel = getBookingService()
				.getAccommodationOrderEntryGroup(roomStayRefNumber, cartModel);

		if (accommodationOrderEntryGroupModel == null)
		{
			return;
		}

		removeEntryGroupsFromCart(Collections.singletonList(accommodationOrderEntryGroupModel));
	}

	protected void normalizeRoomStayRefNumbers(final AbstractOrderModel abstractOrderModel)
	{
		final List<AccommodationOrderEntryGroupModel> entryGroups = getBookingService()
				.getAccommodationOrderEntryGroups(abstractOrderModel);
		int index = 0;
		for (final AccommodationOrderEntryGroupModel entryGroup : entryGroups)
		{
			entryGroup.setRoomStayRefNumber(index);
			index++;
		}
		getModelService().saveAll(entryGroups);
	}

	@Override
	public boolean isNewRoomInCart()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		if (Objects.isNull(cartModel) || !getBookingService().checkIfAnyOrderEntryByType(cartModel, OrderEntryType.ACCOMMODATION))
		{
			return Boolean.FALSE;

		}
		return getBookingService().getAccommodationOrderEntryGroups(cartModel).stream()
				.anyMatch(entryGroup -> entryGroup.getEntries().stream()
						.allMatch(entry -> entry.getActive() && Objects.equals(AmendStatus.NEW, entry.getAmendStatus())));
	}

	/**
	 * @return the cartService
	 */
	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
	@Required
	public void setBookingService(final BookingService bookingService)
	{
		this.bookingService = bookingService;
	}

	/**
	 * @return the productService
	 */
	protected ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the categoryService
	 */
	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	/**
	 * @param categoryService
	 *           the categoryService to set
	 */
	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	/**
	 * @return the accommodationOfferingService
	 */
	protected AccommodationOfferingService getAccommodationOfferingService()
	{
		return accommodationOfferingService;
	}

	/**
	 * @param accommodationOfferingService
	 *           the accommodationOfferingService to set
	 */
	@Required
	public void setAccommodationOfferingService(final AccommodationOfferingService accommodationOfferingService)
	{
		this.accommodationOfferingService = accommodationOfferingService;
	}

	/**
	 * @return the changeDatesPaymentActionStrategyMap
	 */
	protected Map<String, ChangeDatesPaymentActionStrategy> getChangeDatesPaymentActionStrategyMap()
	{
		return changeDatesPaymentActionStrategyMap;
	}

	/**
	 * @param changeDatesPaymentActionStrategyMap
	 *           the changeDatesPaymentActionStrategyMap to set
	 */
	@Required
	public void setChangeDatesPaymentActionStrategyMap(
			final Map<String, ChangeDatesPaymentActionStrategy> changeDatesPaymentActionStrategyMap)
	{
		this.changeDatesPaymentActionStrategyMap = changeDatesPaymentActionStrategyMap;
	}
}
