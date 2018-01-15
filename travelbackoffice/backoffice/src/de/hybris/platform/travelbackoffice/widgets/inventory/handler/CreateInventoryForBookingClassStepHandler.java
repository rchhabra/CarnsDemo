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

import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelbackofficeservices.services.BackofficeScheduleConfigurationService;
import de.hybris.platform.travelbackofficeservices.stocklevel.ManageStockLevelInfo;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationModel;
import de.hybris.platform.travelservices.model.travel.TravelProviderModel;
import de.hybris.platform.travelservices.model.travel.TravelSectorModel;
import de.hybris.platform.travelservices.model.warehouse.TransportOfferingModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;


/**
 * Responsible for handling the next step logic for Step 1 in Create Inventory for Booking Class.
 */
public class CreateInventoryForBookingClassStepHandler implements FlowActionHandler
{
	private final static String ERROR_ALL_FIELDS_EMPTY = "create.inventory.bookingclass.error.fieldsmandatory.message";
	private final static String ERROR_SCHEDULES_NOT_FOUND = "create.inventory.bookingclass.error.schedulenotfound.message";
	private final static String ERROR_POPUP_TITLE = "com.hybris.cockpitng.widgets.configurableflow.error.title.message";
	private final static String AIRLINE_NUMBER_TEXTBOX_ID = "airlineNumber";

	private BackofficeScheduleConfigurationService backofficeScheduleConfigurationService;

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter adapter, final Map<String, String> parameters)
	{
		if (Objects.nonNull(adapter.getWidgetInstanceManager().getWidgetslot()))
		{
			final Radio radioTravelSector = (Radio) adapter.getWidgetInstanceManager().getWidgetslot()
					.getFellow(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_TRAVEL_SECTOR_RADIO_BUTTON_ID);
			final Radio radioTransportOffering = (Radio) adapter.getWidgetInstanceManager().getWidgetslot()
					.getFellow(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_TRANSPORT_OFFERING_RADIO_BUTTON_ID);
			final Radio radioTravelSchedule = (Radio) adapter.getWidgetInstanceManager().getWidgetslot()
					.getFellow(TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_TRAVEL_SCHEDULE_RADIO_BUTTON_ID);
			final ManageStockLevelInfo manageStockLevel = adapter.getWidgetInstanceManager().getModel().getValue(
					TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID, ManageStockLevelInfo.class);

			if (radioTravelSector.isSelected())
			{
				manageStockLevel.setStockItemType(TravelSectorModel._TYPECODE);
			}
			else if (radioTransportOffering.isSelected())
			{
				manageStockLevel.setStockItemType(TransportOfferingModel._TYPECODE);
			}
			else if (radioTravelSchedule.isSelected())
			{
				manageStockLevel.setStockItemType(ScheduleConfigurationModel._TYPECODE);
			}
			else
			{
				manageStockLevel.setStockItemType(StringUtils.EMPTY);
			}

			String errorMessage = ERROR_ALL_FIELDS_EMPTY;
			switch (manageStockLevel.getStockItemType())
			{
				case TravelSectorModel._TYPECODE:
				{
					errorMessage = validateSectorSelection(manageStockLevel);
					break;
				}
				case TransportOfferingModel._TYPECODE:
				{
					errorMessage = validateFlightSelection(manageStockLevel);
					break;
				}
				case ScheduleConfigurationModel._TYPECODE:
				{
					errorMessage = validateScheduleSelection(adapter, manageStockLevel);
					break;
				}
				default:
					displayErrorMessage(errorMessage, ERROR_POPUP_TITLE);
			}

			if (StringUtils.isNotEmpty(errorMessage))
			{
				displayErrorMessage(errorMessage, ERROR_POPUP_TITLE);
			}
			else
			{
				emptySelection(manageStockLevel, adapter);
				adapter.next();
			}
		}
	}

	/**
	 * This method is used to empty the previous user data for radio buttons except "radioButtonSelected"
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @param adapter
	 * 		the adapter
	 */
	protected void emptySelection(final ManageStockLevelInfo manageStockLevel, final FlowActionHandlerAdapter adapter)
	{
		if (!StringUtils.equals(TravelSectorModel._TYPECODE, manageStockLevel.getStockItemType()))
		{
			manageStockLevel.setTravelSectors(Collections.emptyList());
		}

		if (!StringUtils.equals(TransportOfferingModel._TYPECODE, manageStockLevel.getStockItemType()))
		{
			manageStockLevel.setTransportOfferings(Collections.emptyList());
		}

		if (!StringUtils.equals(ScheduleConfigurationModel._TYPECODE, manageStockLevel.getStockItemType()))
		{
			final ScheduleConfigurationModel scheduleConfigurationModel = adapter.getWidgetInstanceManager().getModel().getValue(
					TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_SCHEDULE_CONFIGURATION_ITEM_ID,
					ScheduleConfigurationModel.class);
			scheduleConfigurationModel.setNumber(StringUtils.EMPTY);
			scheduleConfigurationModel.setTravelProvider(null);
		}

	}

	/**
	 * Validate schedule selection string.
	 *
	 * @param adapter
	 * 		the adapter
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @return the string
	 */
	protected String validateScheduleSelection(final FlowActionHandlerAdapter adapter, final ManageStockLevelInfo manageStockLevel)
	{
		final ScheduleConfigurationModel scheduleConfigurationModel = adapter.getWidgetInstanceManager().getModel().getValue(
				TravelbackofficeConstants.CREATE_INVENTORY_BOOKING_CLASS_SCHEDULE_CONFIGURATION_ITEM_ID,
				ScheduleConfigurationModel.class);
		final Textbox airlineNumber = (Textbox) adapter.getWidgetInstanceManager().getWidgetslot()
				.getFellow(AIRLINE_NUMBER_TEXTBOX_ID);
		scheduleConfigurationModel.setNumber(airlineNumber.getValue());
		if (Objects.isNull(scheduleConfigurationModel.getTravelProvider())
				|| StringUtils.isBlank(scheduleConfigurationModel.getNumber()))
		{
			return ERROR_ALL_FIELDS_EMPTY;
		}

		final TravelProviderModel airline = scheduleConfigurationModel.getTravelProvider();
		final String number = scheduleConfigurationModel.getNumber();
		final List<ScheduleConfigurationModel> listOfSchedules = getBackofficeScheduleConfigurationService()
				.getScheduleConfigurationModel(number, airline);
		if (CollectionUtils.isEmpty(listOfSchedules))
		{
			return ERROR_SCHEDULES_NOT_FOUND;
		}
		manageStockLevel.setScheduleConfigurations(listOfSchedules);
		return StringUtils.EMPTY;
	}

	/**
	 * Validate sector selection string.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @return the string
	 */
	protected String validateSectorSelection(final ManageStockLevelInfo manageStockLevel)
	{
		if (CollectionUtils.isEmpty(manageStockLevel.getTravelSectors()))
		{
			return ERROR_ALL_FIELDS_EMPTY;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Validate flight selection string.
	 *
	 * @param manageStockLevel
	 * 		the manage stock level
	 * @return the string
	 */
	protected String validateFlightSelection(final ManageStockLevelInfo manageStockLevel)
	{
		if (CollectionUtils.isEmpty(manageStockLevel.getTransportOfferings()))
		{
			return ERROR_ALL_FIELDS_EMPTY;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Displays the error message pop-up.
	 *
	 * @param errorMsg
	 * 		the error msg
	 * @param title
	 * 		the title
	 */
	protected void displayErrorMessage(final String errorMsg, final String title)
	{
		Messagebox.show(Labels.getLabel(errorMsg), Labels.getLabel(title), Messagebox.OK, Messagebox.ERROR, null);
	}

	/**
	 * Gets backoffice schedule configuration service.
	 *
	 * @return the backoffice schedule configuration service
	 */
	protected BackofficeScheduleConfigurationService getBackofficeScheduleConfigurationService()
	{
		return backofficeScheduleConfigurationService;
	}

	/**
	 * Sets backoffice schedule configuration service.
	 *
	 * @param backofficeScheduleConfigurationService
	 * 		the backoffice schedule configuration service
	 */
	@Required
	public void setBackofficeScheduleConfigurationService(
			final BackofficeScheduleConfigurationService backofficeScheduleConfigurationService)
	{
		this.backofficeScheduleConfigurationService = backofficeScheduleConfigurationService;
	}
}
