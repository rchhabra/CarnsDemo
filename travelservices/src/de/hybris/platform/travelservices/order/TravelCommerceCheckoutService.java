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

package de.hybris.platform.travelservices.order;

import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.travelservices.model.order.AccommodationOrderEntryGroupModel;

import java.math.BigDecimal;
import java.util.List;


/**
 * Interface to handle checkout related operations
 */
public interface TravelCommerceCheckoutService extends CommerceCheckoutService
{
	/**
	 * This method sets the list of {@link AbstractOrderEntryModel} against a payment transaction, retrieving them given
	 * the list of entry numbers
	 *
	 * @param paymentTransaction
	 *           the payment transaction
	 * @param entryNumbers
	 *           the entry numbers
	 */
	void setEntriesAgainstTransaction(PaymentTransactionModel paymentTransaction, List<Integer> entryNumbers);

	/**
	 * Returns the first payment transaction associated with the given order entries having amount equal to the
	 * authorization amount in order to avoid creating multiple authorization transactions
	 *
	 * @param entryNumbers
	 * @param parameter
	 * @return
	 */
	PaymentTransactionModel getExistingTransaction(List<Integer> entryNumbers, CommerceCheckoutParameter parameter);

	/**
	 * Creates a payment transaction for refund
	 *
	 * @param abstractOrder
	 *           the abstract order
	 * @param amountToRefund
	 *           the amount to refund
	 * @param entries
	 *           the entries the transaction is linked to
	 * @deprecated Deprecated since version 3.0.
	 * @return true if successfully created transaction
	 */
	@Deprecated
	PaymentTransactionModel createRefundPaymentTransaction(AbstractOrderModel abstractOrder, BigDecimal amountToRefund,
			List<AbstractOrderEntryModel> entries);

	/**
	 * This method will replace all the old RoomRateModel entries belonging to specific group transaction entries , with
	 * new Entries.
	 * 
	 * @param orderEntryGroup
	 * @param entries
	 * @return
	 */
	boolean linkEntriesToOldPaymentTransactions(AccommodationOrderEntryGroupModel orderEntryGroup,
			List<AbstractOrderEntryModel> entries);

	/**
	 * Create refund transaction entries within the correct payment transaction according with the amendment journey
	 *
	 * @param abstractOrder
	 * @param entries
	 * @return
	 */
	Boolean createRefundPaymentTransactionEntries(AbstractOrderModel abstractOrder, List<AbstractOrderEntryModel> entries);
}
