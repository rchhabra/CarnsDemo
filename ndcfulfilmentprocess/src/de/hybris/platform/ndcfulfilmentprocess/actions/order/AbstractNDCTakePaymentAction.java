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
package de.hybris.platform.ndcfulfilmentprocess.actions.order;

import de.hybris.platform.basecommerce.enums.FraudStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.fraud.impl.FraudServiceResponse;
import de.hybris.platform.fraud.impl.FraudSymptom;
import de.hybris.platform.fraud.model.FraudReportModel;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.travelfulfilmentprocess.actions.order.AbstractOrderAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Abstract action for NDC Take Payment Action which define 3 possible transitions (OK,REMOVE_PAY_LATER,NOK)
 */
public abstract class AbstractNDCTakePaymentAction<T extends OrderProcessModel> extends AbstractOrderAction<T>
{
	/**
	 * The enum Transition.
	 */
	public enum Transition
	{
		REMOVE_PAY_LATER, OK, NOK;

		public static Set<String> getStringValues()
		{
			final Set<String> res = new HashSet<String>();
			for (final Transition transitions : Transition.values())
			{
				res.add(transitions.toString());
			}
			return res;
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		return Transition.getStringValues();
	}

	@Override
	public final String execute(final T process) throws Exception
	{
		return executeAction(process).toString();
	}

	/**
	 * Executes this <code>Action</code>'s business logic working on the given
	 * {@link de.hybris.platform.processengine.model.BusinessProcessModel}.
	 *
	 * @param process
	 * 		The process context to work on.
	 *
	 * @return OK ; NOK
	 * @throws Exception
	 * 		the exception
	 */
	public abstract Transition executeAction(T process) throws Exception;
}
