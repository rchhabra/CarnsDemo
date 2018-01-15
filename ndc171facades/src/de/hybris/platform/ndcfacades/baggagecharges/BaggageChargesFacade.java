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

package de.hybris.platform.ndcfacades.baggagecharges;

import de.hybris.platform.ndcfacades.ndc.BaggageChargesRQ;
import de.hybris.platform.ndcfacades.ndc.BaggageChargesRS;


/**
 * Interface for the Baggage Charges Facade
 */
public interface BaggageChargesFacade
{

	/**
	 * This method returns an instance of {@link BaggageChargesRS} having information for a set of checked baggages
	 *
	 * @param baggageListRQ
	 * 		the baggage list rq
	 *
	 * @return the baggage charges
	 */
	BaggageChargesRS getBaggageCharges(BaggageChargesRQ baggageListRQ);

}
