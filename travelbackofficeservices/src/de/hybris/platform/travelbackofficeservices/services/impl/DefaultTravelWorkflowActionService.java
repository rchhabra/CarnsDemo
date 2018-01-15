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

package de.hybris.platform.travelbackofficeservices.services.impl;

import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.impl.DefaultWorkflowActionService;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;


/**
 * Class that adds completed and terminated WorkflowActionStatus to the list of statuses as we display only those
 * WorkflowActions( notifications ) injected in the bean definition, overriding the default OOTB behavior that
 * displays in progress notifications or tasks
 *
 */
public class DefaultTravelWorkflowActionService extends DefaultWorkflowActionService
{
	private List<WorkflowActionStatus> workflowActionStatuses;

	@Override
	public List<WorkflowActionModel> getAllUserWorkflowActionsWithAttachments(final List<String> attachments)
	{
		return this.getAllUserWorkflowActionsWithAttachments(attachments, getWorkflowActionStatuses());
	}

	/**
	 * @return the workflowActionStatuses
	 */
	protected List<WorkflowActionStatus> getWorkflowActionStatuses()
	{
		return workflowActionStatuses;
	}

	/**
	 * @param workflowActionStatuses
	 *           the workflowActionStatuses to set
	 */
	@Required
	public void setWorkflowActionStatuses(List<WorkflowActionStatus> workflowActionStatuses)
	{
		this.workflowActionStatuses = workflowActionStatuses;
	}
}
