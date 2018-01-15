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

package de.hybris.platform.travelfulfilmentprocess.actions.b2b;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.enums.WorkflowTemplateType;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.actions.B2BPermissionResultHelperImpl;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.services.B2BWorkflowIntegrationService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowProcessingService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class StartWorkFlowForAdminTest
{
	@InjectMocks
	StartWorkFlowForAdmin startWorkFlowForAdmin;

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	private B2BWorkflowIntegrationService b2bWorkflowIntegrationService;

	@Mock
	private WorkflowProcessingService workflowProcessingService;

	@Mock
	private WorkflowService workflowService;

	@Mock
	private B2BPermissionResultHelperImpl permissionResultHelper;

	@Mock
	private UserService userService;

	@Mock
	private ModelService modelService;

	@Test
	public void testExecuteActionWithNOK()
	{
		final B2BApprovalProcessModel process = new B2BApprovalProcessModel();
		final OrderModel order = new OrderModel();
		final B2BCustomerModel customer = new B2BCustomerModel();
		order.setUser(customer);
		process.setOrder(order);

		final B2BUnitModel unit = new B2BUnitModel();
		Mockito.when(b2bUnitService.getParent(customer)).thenReturn(unit);

		Mockito.when(b2bUnitService.getUsersOfUserGroup(unit, B2BConstants.B2BADMINGROUP, true))
				.thenReturn(Collections.singletonList(customer));
		Mockito.doNothing().when(modelService).save(order);

		Assert.assertEquals(startWorkFlowForAdmin.executeAction(process), AbstractSimpleDecisionAction.Transition.NOK);

	}

	@Test
	public void testExecuteActionWithOK()
	{
		final B2BApprovalProcessModel process = new B2BApprovalProcessModel();
		final OrderModel order = new OrderModel();
		final B2BCustomerModel customer = new B2BCustomerModel();
		final B2BCustomerModel adminCustomer = new B2BCustomerModel();
		order.setUser(customer);
		process.setOrder(order);

		final B2BUnitModel unit = new B2BUnitModel();
		Mockito.when(b2bUnitService.getParent(customer)).thenReturn(unit);

		Mockito.when(b2bUnitService.getUsersOfUserGroup(unit, B2BConstants.B2BADMINGROUP, true))
				.thenReturn(Collections.singletonList(adminCustomer));
		Mockito.doNothing().when(modelService).saveAll();

		final B2BPermissionResultModel b2BPermissionResultModel = new B2BPermissionResultModel();
		Mockito.when(permissionResultHelper.filterResultByPermissionStatus(Mockito.any(Collection.class),
				Mockito.any(PermissionStatus.class))).thenReturn(Collections.singletonList(b2BPermissionResultModel));


		final UserGroupModel userGroup = new UserGroupModel();
		final UserGroupModel approverGroup = new UserGroupModel();
		Mockito.when(userService.getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP)).thenReturn(approverGroup);
		final Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(userGroup);
		adminCustomer.setGroups(groups);

		Mockito.when(
				b2bWorkflowIntegrationService.generateWorkflowTemplateCode(Mockito.anyString(), Mockito.anyListOf(UserModel.class)))
				.thenReturn("workflowTemplateCode");
		final WorkflowTemplateModel workflowTemplate = Mockito.mock(WorkflowTemplateModel.class);
		Mockito.when(workflowTemplate.getName()).thenReturn("workflowTemplate");
		Mockito.when(workflowTemplate.getOwner()).thenReturn(customer);
		Mockito.when(b2bWorkflowIntegrationService.createWorkflowTemplate(Mockito.anyListOf(UserModel.class), Mockito.anyString(),
				Mockito.anyString(), Mockito.any(WorkflowTemplateType.class))).thenReturn(workflowTemplate);
		final WorkflowModel workflow = new WorkflowModel();
		Mockito.when(workflowService.createWorkflow(Mockito.anyString(), Mockito.any(WorkflowTemplateModel.class),
				Mockito.anyListOf(ItemModel.class), Mockito.any(B2BCustomerModel.class))).thenReturn(workflow);
		Mockito.when(workflowProcessingService.startWorkflow(workflow)).thenReturn(true);

		Assert.assertEquals(startWorkFlowForAdmin.executeAction(process), AbstractSimpleDecisionAction.Transition.OK);

	}

	@Test
	public void testExecuteActionWithException()
	{
		final B2BApprovalProcessModel process = new B2BApprovalProcessModel();
		final OrderModel order = new OrderModel();
		final B2BCustomerModel customer = new B2BCustomerModel();
		order.setUser(customer);
		process.setOrder(order);

		Mockito.when(b2bUnitService.getParent(customer)).thenThrow(new IllegalStateException());

		Assert.assertEquals(startWorkFlowForAdmin.executeAction(process), AbstractSimpleDecisionAction.Transition.NOK);

	}

}
