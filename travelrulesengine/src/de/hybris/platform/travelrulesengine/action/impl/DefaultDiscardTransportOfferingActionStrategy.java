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

package de.hybris.platform.travelrulesengine.action.impl;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.travelrulesengine.model.TransportOfferingFilterResultModel;
import de.hybris.platform.travelrulesengine.rao.FilterTransportOfferingRAO;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Strategy responsible for filtering out the transport offering based on rules evaluation result
 */
public class DefaultDiscardTransportOfferingActionStrategy extends AbstractTravelRuleActionStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultDiscardTransportOfferingActionStrategy.class);

	@Override
	public List<TransportOfferingFilterResultModel> apply(final AbstractRuleActionRAO action)
	{
		if (!(action instanceof FilterTransportOfferingRAO))
		{
			LOG.debug(String.format("cannot apply %s, action is not of type FilterTransportOfferingRAO",
					this.getClass().getSimpleName()));
			return Collections.emptyList();
		}
		final FilterTransportOfferingRAO filterTransportOfferingAction = (FilterTransportOfferingRAO) action;
		final TransportOfferingFilterResultModel transportOfferingFilterResultModel = new TransportOfferingFilterResultModel();
		transportOfferingFilterResultModel.setTransportOfferingCode(filterTransportOfferingAction.getTransportOfferingCode());
		transportOfferingFilterResultModel.setValid(filterTransportOfferingAction.isValid());

		return Collections.singletonList(transportOfferingFilterResultModel);
	}

	@Override
	public void undo(final ItemModel arg0)
	{
		// empty method carried from abstract class but not used
	}

}
