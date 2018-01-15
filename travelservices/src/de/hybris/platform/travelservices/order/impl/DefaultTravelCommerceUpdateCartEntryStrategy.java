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

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceUpdateCartEntryStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.travelservices.enums.AmendStatus;
import de.hybris.platform.travelservices.enums.OrderEntryType;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;
import de.hybris.platform.travelservices.model.travel.TravelRouteModel;
import de.hybris.platform.travelservices.model.user.TravellerModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.travelservices.services.BookingService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Extension of DefaultCommerceUpdateCartEntryStrategy to add functionality specific to travel, such as adding amendment
 * tracking status to order entries
 */
public class DefaultTravelCommerceUpdateCartEntryStrategy extends DefaultCommerceUpdateCartEntryStrategy
{
	private BookingService bookingService;

	@Override
	protected CommerceCartModification modifyEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity)
	{
		// Now work out how many that leaves us with on this entry
		final long entryNewQuantity = entryToUpdate.getQuantity().longValue() + actualAllowedQuantityChange;

		final ModelService modelService = getModelService();

		if (entryNewQuantity <= 0)
		{
			if (AmendStatus.NEW.equals(entryToUpdate.getAmendStatus()))
			{
				return removeNewEntryFromCart(cartModel, entryToUpdate, newQuantity, modelService);
			}
			else
			{
				return updateEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity,
						entryNewQuantity, modelService);
			}
		}
		else
		{
			return updateEntry(cartModel, entryToUpdate, actualAllowedQuantityChange, newQuantity, maxOrderQuantity,
					entryNewQuantity, modelService);
		}

	}

	protected CommerceCartModification updateEntry(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final long actualAllowedQuantityChange, final long newQuantity, final Integer maxOrderQuantity,
			final long entryNewQuantity, final ModelService modelService)
	{
		// Adjust the entry quantity to the new value
		entryToUpdate.setQuantity(Long.valueOf(entryNewQuantity));
		if (Objects.nonNull(entryToUpdate.getAmendStatus()) && !AmendStatus.NEW.equals(entryToUpdate.getAmendStatus()))
		{
			updateAmendmentStatus(cartModel, entryToUpdate, entryNewQuantity);
		}

		modelService.save(entryToUpdate);
		modelService.refresh(cartModel);
		getCommerceCartCalculationStrategy().calculateCart(cartModel);

		modelService.refresh(entryToUpdate);

		// Return the modification data
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setQuantityAdded(actualAllowedQuantityChange);
		modification.setEntry(entryToUpdate);
		modification.setQuantity(entryNewQuantity);

		if (isMaxOrderQuantitySet(maxOrderQuantity) && entryNewQuantity == maxOrderQuantity.longValue())
		{
			modification.setStatusCode(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED);
		}
		else if (newQuantity == entryNewQuantity)
		{
			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		}
		else
		{
			modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		}
		return modification;
	}

	/**
	 * This method updates AmendStatus and active flag of the entry as follows:
	 * <p>
	 * 1. entryNewQuantity == 0 ? set active flag to false : set active flag to true
	 * <p>
	 * 2. If AmendStatus == SAME --> set AmendStatus to CHANGED
	 * <p>
	 * 3. If AmendStatus == CHANGED --> find a relevant entry in original order and then:
	 * <p>
	 * 3a. if entryNewQuantity == originalEntryQuantity --> set AmendStatus to SAME
	 * <p>
	 * 3b. if entryNewQuantity != originalEntryQuantity --> keep AmendStatus as CHANGED
	 *
	 * @param cartModel
	 * @param entryToUpdate
	 * @param entryNewQuantity
	 */
	protected void updateAmendmentStatus(final CartModel cartModel, final AbstractOrderEntryModel entryToUpdate,
			final Long entryNewQuantity)
	{
		entryToUpdate.setActive(entryNewQuantity == 0L ? Boolean.FALSE : Boolean.TRUE);

		if (AmendStatus.SAME.equals(entryToUpdate.getAmendStatus()))
		{
			entryToUpdate.setAmendStatus(AmendStatus.CHANGED);
			return;
		}

		final String productCode = entryToUpdate.getProduct().getCode();
		AbstractOrderEntryModel originalEntry;
		if (OrderEntryType.ACCOMMODATION.equals(entryToUpdate.getType()))
		{
			final AccommodationOrderEntryGroupModel entryGroup = (AccommodationOrderEntryGroupModel) entryToUpdate.getEntryGroup();
			final int roomStayRefNumber = entryGroup.getRoomStayRefNumber();
			originalEntry = getBookingService().getOriginalOrderEntry(cartModel.getOriginalOrder(), productCode, roomStayRefNumber);
		}
		else
		{
			final List<String> transportOfferingCodes = entryToUpdate.getTravelOrderEntryInfo().getTransportOfferings().stream()
					.map(TransportOfferingModel::getCode).collect(Collectors.toList());
			final List<String> travellerCodes = entryToUpdate.getTravelOrderEntryInfo().getTravellers().stream()
					.map(TravellerModel::getLabel).collect(Collectors.toList());
			final TravelRouteModel travelRoute = entryToUpdate.getTravelOrderEntryInfo().getTravelRoute();
			final String travelRouteCode = travelRoute != null ? travelRoute.getCode() : null;
			originalEntry = getBookingService().getOriginalOrderEntry(cartModel.getOriginalOrder().getCode(), productCode,
					travelRouteCode, transportOfferingCodes, travellerCodes, Boolean.TRUE);
		}

		if (Objects.isNull(originalEntry))
		{
			return;
		}

		if (entryNewQuantity.equals(originalEntry.getQuantity()))
		{
			entryToUpdate.setAmendStatus(AmendStatus.SAME);
		}
	}

	protected CommerceCartModification removeNewEntryFromCart(final CartModel cartModel,
			final AbstractOrderEntryModel entryToUpdate, final long newQuantity, final ModelService modelService)
	{
		final CartEntryModel entry = new CartEntryModel();
		entry.setProduct(entryToUpdate.getProduct());

		// The allowed new entry quantity is zero or negative
		// just remove the entry
		modelService.remove(entryToUpdate);
		modelService.refresh(cartModel);
		normalizeEntryNumbers(cartModel);
		getCommerceCartCalculationStrategy().calculateCart(cartModel);

		// Return an empty modification
		final CommerceCartModification modification = new CommerceCartModification();
		modification.setEntry(entry);
		modification.setQuantity(0);
		// We removed all the quantity from this row
		modification.setQuantityAdded(-entryToUpdate.getQuantity().longValue());

		if (newQuantity == 0)
		{
			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		}
		else
		{
			modification.setStatusCode(CommerceCartModificationStatus.LOW_STOCK);
		}
		return modification;
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

}
