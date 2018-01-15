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

package de.hybris.platform.travelrulesengine.services.impl;

import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.dao.RuleEngineContextDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineContextModel;
import de.hybris.platform.ruleengineservices.action.RuleActionService;
import de.hybris.platform.ruleengineservices.enums.FactContextType;
import de.hybris.platform.ruleengineservices.rao.providers.FactContextFactory;
import de.hybris.platform.ruleengineservices.rao.providers.RAOProvider;
import de.hybris.platform.ruleengineservices.rao.providers.impl.FactContext;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.travelrulesengine.services.TravelEngineService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Abstract class for the drools engine service
 */
public abstract class AbstractTravelEngineService implements TravelEngineService
{
	private static final Logger LOG = Logger.getLogger(AbstractTravelEngineService.class);
	private RuleEngineService commerceRuleEngineService;
	private RuleEngineContextDao ruleEngineContextDao;
	private String defaultRuleEngineContextName;
	private RuleActionService ruleActionService;
	private FactContextFactory factContextFactory;

	@Override
	public RuleEvaluationResult evaluate(final List<Object> factObjects, final FactContextType contextType)
	{
		final List<Object> facts = new ArrayList<>();
		facts.addAll(factObjects);
		final RuleEvaluationContext context = this
				.prepareContext(this.getFactContextFactory().createFactContext(contextType, facts));
		return this.getCommerceRuleEngineService().evaluate(context);
	}

	/**
	 * Method to create the rule evaluation context. The default context name set for the bean and the facts are set to
	 * the context.
	 *
	 * @param factContext
	 * 		the fact context
	 * @return rule evaluation context
	 */
	protected RuleEvaluationContext prepareContext(final FactContext factContext)
	{
		final Set convertedFacts = this.provideRAOs(factContext);
		final RuleEvaluationContext evaluationContext = new RuleEvaluationContext();
		try
		{
			final AbstractRuleEngineContextModel engineContext = this.getRuleEngineContextDao()
					.getRuleEngineContextByName(this.getDefaultRuleEngineContextName());
			evaluationContext.setRuleEngineContext(engineContext);
			evaluationContext.setFacts(convertedFacts);
			return evaluationContext;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.error("Model Not Found", e);
			return null;
		}
	}

	/**
	 * Method to create the RAO objects for the facts
	 *
	 * @param factContext
	 * 		the fact context
	 * @return set
	 */
	protected Set<Object> provideRAOs(final FactContext factContext)
	{
		final Set<Object> result = new HashSet<Object>();
		final Iterator factsItr = factContext.getFacts().iterator();

		while (factsItr.hasNext())
		{
			final Object fact = factsItr.next();
			final Iterator<RAOProvider> raoProviderItr = factContext.getProviders(fact).iterator();

			while (raoProviderItr.hasNext())
			{
				final RAOProvider raoProvider = raoProviderItr.next();
				result.addAll(raoProvider.expandFactModel(fact));
			}
		}
		return result;
	}

	/**
	 * @return the ruleEngineContextDao
	 */
	protected RuleEngineContextDao getRuleEngineContextDao()
	{
		return ruleEngineContextDao;
	}

	/**
	 * @param ruleEngineContextDao the ruleEngineContextDao to set
	 */
	public void setRuleEngineContextDao(final RuleEngineContextDao ruleEngineContextDao)
	{
		this.ruleEngineContextDao = ruleEngineContextDao;
	}

	/**
	 * @return the defaultRuleEngineContextName
	 */
	protected String getDefaultRuleEngineContextName()
	{
		return defaultRuleEngineContextName;
	}

	/**
	 * @param defaultRuleEngineContextName the defaultRuleEngineContextName to set
	 */
	@Required
	public void setDefaultRuleEngineContextName(final String defaultRuleEngineContextName)
	{
		this.defaultRuleEngineContextName = defaultRuleEngineContextName;
	}

	/**
	 * @return the ruleActionService
	 */
	protected RuleActionService getRuleActionService()
	{
		return ruleActionService;
	}

	/**
	 * @param ruleActionService the ruleActionService to set
	 */
	public void setRuleActionService(final RuleActionService ruleActionService)
	{
		this.ruleActionService = ruleActionService;
	}

	/**
	 * @return the commerceRuleEngineService
	 */
	protected RuleEngineService getCommerceRuleEngineService()
	{
		return commerceRuleEngineService;
	}

	/**
	 * @param commerceRuleEngineService the commerceRuleEngineService to set
	 */
	public void setCommerceRuleEngineService(final RuleEngineService commerceRuleEngineService)
	{
		this.commerceRuleEngineService = commerceRuleEngineService;
	}

	/**
	 * @return the factContextFactory
	 */
	protected FactContextFactory getFactContextFactory()
	{
		return factContextFactory;
	}

	/**
	 * @param factContextFactory the factContextFactory to set
	 */
	public void setFactContextFactory(final FactContextFactory factContextFactory)
	{
		this.factContextFactory = factContextFactory;
	}

}
