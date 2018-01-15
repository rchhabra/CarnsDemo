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

package de.hybris.platform.travelbackofficeservices.utils;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.travelbackofficeservices.model.travel.TravelWorkflowItemAttachmentModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowActionTemplateModel;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collections;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;


/**
 * Class to create notifications for backoffice under tasks section
 *
 */
public class TravelBackofficeNotificationUtils
{
	private ModelService modelService;
	private TimeService timeService;
	private I18NService i18nService;
	private SessionService sessionService;
	private UserService userService;

	/**
	 * Creates Notification as WorkFlowAciton on creation of a schedule
	 *
	 * @param model
	 *      as the model
	 * @param status
	 *      as the status
	 * @param message
	 *      as the message
	 */
	public void createNotificationAsWorkFlowAction(final ItemModel model,
			final WorkflowActionStatus status, String message)
	{
		final WorkflowTemplateModel workflowTemplate = getModelService().create(WorkflowTemplateModel.class);
		workflowTemplate.setCode("notificationWorkFlowTemplate_" + getTimeService().getCurrentTime());
		getModelService().save(workflowTemplate);

		final WorkflowModel workflow = getModelService().create(WorkflowModel.class);
		workflow.setCode("notificationWorkflow_" + getTimeService().getCurrentTime());
		workflow.setJob(workflowTemplate);

		final WorkflowActionTemplateModel actionTemplate = getModelService().create(WorkflowActionTemplateModel.class);
		actionTemplate.setCode("notificationWorkflowActionTemplate_" + getTimeService().getCurrentTime());
		actionTemplate.setWorkflow(workflowTemplate);
		final UserModel principalForWorkflow = getPrincipalForWorkflow();
		actionTemplate.setPrincipalAssigned(principalForWorkflow);
		getModelService().save(actionTemplate);

		final TravelWorkflowItemAttachmentModel attachment = getModelService().create(TravelWorkflowItemAttachmentModel.class);
		attachment.setCode("notificationWorkFlowAttachment_" + getTimeService().getCurrentTime());
		attachment.setWorkflow(workflow);
		if (Objects.nonNull(model))
		{
			attachment.setItem(model);
		}

		final WorkflowActionModel action = getModelService().create(WorkflowActionModel.class);
		action.setWorkflow(workflow);
		action.setTemplate(actionTemplate);
		action.setStatus(status);
		action.setPrincipalAssigned(principalForWorkflow);
		action.setActivated(getTimeService().getCurrentTime());
		action.setAttachments(Collections.singletonList(attachment));

		if (WorkflowActionStatus.TERMINATED.equals(status))
		{
			message = "Workflow terminated";
		}

		action.setName(message, getI18nService().getCurrentLocale());

		attachment.setActions(Collections.singletonList(action));
		workflow.setActions(Collections.singletonList(action));

		getModelService().save(workflow);
		getModelService().save(action);
		getModelService().save(attachment);
	}

	/**
	 * Returns the userModel to be assigned to the Principal User
	 *
	 * @return the UserModel of the admin User
	 */
	protected UserModel getPrincipalForWorkflow() {
		return getUserService().getAdminUser();
	}

	/**
	 * @return the i18nService
	 */
	protected I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the timeService
	 */
	protected TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the sessionService
	 */
	protected SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return the userService
	 */
	protected UserService getUserService() {
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
