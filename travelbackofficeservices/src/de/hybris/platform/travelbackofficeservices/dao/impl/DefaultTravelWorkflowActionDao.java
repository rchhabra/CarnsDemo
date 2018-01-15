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

package de.hybris.platform.travelbackofficeservices.dao.impl;

import de.hybris.platform.constants.GeneratedCoreConstants;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.constants.GeneratedWorkflowConstants;
import de.hybris.platform.workflow.daos.impl.DefaultWorkflowActionDao;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Dao class that overrides the OOTB behavior to fetch WorkflowAcitons irrespective of attachment ItemType
 *
 */
public class DefaultTravelWorkflowActionDao extends DefaultWorkflowActionDao
{

	public DefaultTravelWorkflowActionDao(final String typecode)
	{
		super(typecode);
	}

	@Override
	public List<WorkflowActionModel> findWorkflowActionsByStatusAndAttachmentType(final List<ComposedTypeModel> attachmentTypes,
			final Collection<WorkflowActionStatus> actionStatuses)
	{
		final Map params = new HashMap();
		params.put("status", actionStatuses);

		final SearchResult<WorkflowActionModel> res = getFlexibleSearchService()
				.search("SELECT DISTINCT {actions:pk}, {actions:creationtime} FROM {WorkflowAction as actions JOIN " +
						GeneratedWorkflowConstants.Relations.WORKFLOWACTIONITEMATTACHMENTRELATION + "* AS rel ON {rel:"
						+ GeneratedCoreConstants.Attributes.Link.SOURCE + "}={actions:" + "pk" + "} " + "JOIN "
						+ "WorkflowItemAttachment" + " AS attachment ON {rel:" + GeneratedCoreConstants.Attributes.Link.TARGET
						+ "}={attachment:" + "pk" + "} " + "} " + "WHERE {actions:" + "status" + "} IN (?status) AND ({actions:"
						+ "principalAssigned" + "}=?session.user OR " + "{actions:" + "principalAssigned"
						+ "} IN (?session.user.allgroups)) " + "AND {rel:" + GeneratedCoreConstants.Attributes.Link.QUALIFIER + "} = '"
						+ GeneratedWorkflowConstants.Relations.WORKFLOWACTIONITEMATTACHMENTRELATION + "' AND {rel:"
						+ GeneratedCoreConstants.Attributes.Link.LANGUAGE + "} IS NULL " + "ORDER BY {actions:"
						+ "creationtime" + "} ASC", params);
		return res.getResult();
	}

}
