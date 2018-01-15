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

package de.hybris.platform.travelbackoffice.widgets.inventory.handler;

import de.hybris.platform.core.Registry;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackoffice.utils.TravelbackofficeUtils;
import de.hybris.platform.travelbackofficeservices.stock.TravelBackofficeStockService;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelbackofficeservices.utils.TravelBackofficeNotificationUtils;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationUtils;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.util.MessageboxUtils;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Displays the message to user in a popup before executing actual logic.
 */
public class StockInventoryPreviewPopupHandler implements FlowActionHandler
{
	private static final String POPUP_CONFIRMATION_MESSAGE = "create.inventory.bookingclass.preview.popup.message";
	private static final String POPUP_TITLE_MESSAGE = "create.inventory.bookingclass.preview.popup.title";

	private TravelBackofficeStockService travelbackofficeStockService;
	private TravelBackofficeNotificationUtils travelBackofficeNotificationUtils;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{

		Messagebox.show(Labels.getLabel(POPUP_CONFIRMATION_MESSAGE), Labels.getLabel(POPUP_TITLE_MESSAGE),
				MessageboxUtils.NO_YES_OPTION, "z-messagebox-icon z-messagebox-information",
				clickEvent -> {
					if (Button.YES.equals(clickEvent.getButton()))
					{
						final ManageStockLevelInfo manageStockLevel = adapter.getWidgetInstanceManager().getModel().getValue(
								TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID,
								ManageStockLevelInfo.class);
						final List<StockLevelModel> stockLevelModels = new ArrayList<>();
						final Set<String> stockLevels = adapter.getWidgetInstanceManager().getModel().getValue("stockLevels",
								HashSet.class);
						if (CollectionUtils.isNotEmpty(stockLevels))
						{
							stockLevels.forEach(stockLevel -> {
								stockLevel = TravelbackofficeUtils.validateStockLevelCode(stockLevel);
								final StockLevelModel stockLevelModel = adapter.getWidgetInstanceManager().getModel().getValue(stockLevel,
										StockLevelModel.class);
								if (Objects.nonNull(stockLevelModel) && BooleanUtils.isTrue(stockLevelModel.getSelected()))
								{
									stockLevelModels.add(stockLevelModel);
								}
							});
						}
						final ManageStockLevelThread manageInventoryServiceJob = new ManageStockLevelThread(
								Registry.getCurrentTenant().getTenantID(), manageStockLevel, stockLevelModels);
						manageInventoryServiceJob.start();

						NotificationUtils.notifyUser(NotificationUtils.getWidgetNotificationSource(adapter.getWidgetInstanceManager()),
								TravelbackofficeConstants.TRAVEL_GLOBAL_NOTIFICATION_EVENT_TYPE, NotificationEvent.Level.SUCCESS,
								Labels.getLabel("create.inventory.bookingclass.preview.notification.success.message"));

						adapter.done();
					}
				});
	}

	/**
	 * The type Manage stock level thread.
	 */
	protected class ManageStockLevelThread extends Thread
	{
		private final String tenant;
		private final ManageStockLevelInfo manageStockLevel;
		private final List<StockLevelModel> stockLevels;

		/**
		 * Instantiates a new Manage stock level thread.
		 *
		 * @param tenant
		 * 		the tenant
		 * @param manageStockLevel
		 * 		the manage stock level
		 */
		protected ManageStockLevelThread(final String tenant, final ManageStockLevelInfo manageStockLevel,
				final List<StockLevelModel> stockLevels)
		{
			super();
			this.tenant = tenant;
			this.manageStockLevel = manageStockLevel;
			this.stockLevels = stockLevels;
		}

		@Override
		public void run()
		{
			if (Objects.isNull(manageStockLevel))
			{
				return;
			}

			try
			{
				Registry.setCurrentTenant(Registry.getTenantByID(tenant));

				switch (manageStockLevel.getStockItemType())
				{
					case TravelSectorModel._TYPECODE:
					{
						if (CollectionUtils.isEmpty(manageStockLevel.getTravelSectors()))
						{
							return;
						}
						final List<TravelSectorModel> travelSectors = new ArrayList<>(manageStockLevel.getTravelSectors());
						travelSectors.forEach(
								travelSector -> getTravelbackofficeStockService().createStockLevels(manageStockLevel, travelSector));
						break;

					}
					case TransportOfferingModel._TYPECODE:
					{
						if (CollectionUtils.isEmpty(manageStockLevel.getTransportOfferings()))
						{
							return;
						}
						final List<WarehouseModel> warehouses = new ArrayList<>(
								manageStockLevel.getTransportOfferings());
						if (BooleanUtils.isTrue(manageStockLevel.getModified()) && CollectionUtils.isNotEmpty(stockLevels))
						{
							getTravelbackofficeStockService().updateStockLevelsForTransportOffering(stockLevels, warehouses);
						}
						else if (BooleanUtils.isNotTrue(manageStockLevel.getModified()))
						{
							getTravelbackofficeStockService().createStockLevels(manageStockLevel, warehouses);
						}
						break;
					}
					case ScheduleConfigurationModel._TYPECODE:
					{
						if (CollectionUtils.isEmpty(manageStockLevel.getScheduleConfigurations()))
						{
							return;
						}
						final List<ScheduleConfigurationModel> scheduleConfigurations = new ArrayList<>(
								manageStockLevel.getScheduleConfigurations());
						scheduleConfigurations.forEach(scheduleConfiguration -> getTravelbackofficeStockService()
								.createStockLevels(manageStockLevel, scheduleConfiguration));
						break;
					}
					default:
				}

			}
			finally
			{
				getTravelBackofficeNotificationUtils().createNotificationAsWorkFlowAction(null, WorkflowActionStatus.COMPLETED,
						manageStockLevel.getStockItemType() + " successfully updated with stock");
				Registry.unsetCurrentTenant();

			}
		}
	}

	/**
	 * @return travelbackofficeStockService
	 */
	protected TravelBackofficeStockService getTravelbackofficeStockService()
	{
		return travelbackofficeStockService;
	}

	/**
	 * @param travelbackofficeStockService
	 * 		the travelbackofficeStockService to set
	 */
	@Required
	public void setTravelbackofficeStockService(final TravelBackofficeStockService travelbackofficeStockService)
	{
		this.travelbackofficeStockService = travelbackofficeStockService;
	}

	/**
	 * @return the travelBackofficeNotificationUtils
	 */
	protected TravelBackofficeNotificationUtils getTravelBackofficeNotificationUtils()
	{
		return travelBackofficeNotificationUtils;
	}

	/**
	 * @param travelBackofficeNotificationUtils
	 *           the travelBackofficeNotificationUtils to set
	 */
	@Required
	public void setTravelBackofficeNotificationUtils(final TravelBackofficeNotificationUtils travelBackofficeNotificationUtils)
	{
		this.travelBackofficeNotificationUtils = travelBackofficeNotificationUtils;
	}
}
