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

package de.hybris.platform.travelbackoffice.controllers;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.zkoss.zk.ui.event.Events;

import com.hybris.backoffice.navigation.TreeNodeSelector;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import com.hybris.cockpitng.widgets.configurableflow.ConfigurableFlowContextParameterNames;


/**
 * Controller class to handle operations within the main panel widget
 */
public class MainPanelController extends DefaultWidgetController
{

	@ViewEvent(componentID = "createBundleBtn", eventName = Events.ON_CLICK)
	public void createBundle() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new LinkedHashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "BundleTemplate");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "createFareProductBtn", eventName = Events.ON_CLICK)
	public void createFareProduct() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "FareProduct");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "createAncillaryBtn", eventName = Events.ON_CLICK)
	public void createAncillaryProduct() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "AncillaryProduct");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "openSeatmapBtn", eventName = Events.ON_CLICK)
	public void openSeatmap() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		sendOutput("seatmapContext", outputCtx);
	}

	@ViewEvent(componentID = "merchandisingWorkspaceBtn", eventName = Events.ON_CLICK)
	public void openRulesExplorerTree() throws InterruptedException
	{
		sendOutput("treeNode", new TreeNodeSelector("hmc_typenode_all_source_rule_templates", true));
	}

	@ViewEvent(componentID = "createScheduleAnchor", eventName = Events.ON_CLICK)
	public void createSchedule() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "ScheduleConfiguration");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "modifyScheduleAnchor", eventName = Events.ON_CLICK)
	public void modifySchedule() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "ScheduleConfiguration");
		sendOutput("contextMapForModifyWizard", outputCtx);
	}

	@ViewEvent(componentID = "addBookingClassStockAnchor", eventName = Events.ON_CLICK)
	public void addBookingClassStock() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "ManageInventory");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "addAncillaryStockAnchor", eventName = Events.ON_CLICK)
	public void addAncillaryProductStock() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "ManageAncillaryInventory");
		sendOutput("contextMap", outputCtx);
	}

	@ViewEvent(componentID = "modifyInventoryAnchor", eventName = Events.ON_CLICK)
	public void modifyInventory() throws InterruptedException
	{
		final Map<String, Object> outputCtx = new HashMap<>();
		outputCtx.put(ConfigurableFlowContextParameterNames.TYPE_CODE.getName(), "ManageInventory");
		sendOutput("contextMapForModifyWizard", outputCtx);
	}

}