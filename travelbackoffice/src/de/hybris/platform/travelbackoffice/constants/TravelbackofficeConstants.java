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
 */
package de.hybris.platform.travelbackoffice.constants;

/**
 * Global class for all Ybackoffice constants. You can add global constants for your extension into this class.
 */
public final class TravelbackofficeConstants extends GeneratedTravelbackofficeConstants
{
	public static final String EXTENSIONNAME = "travelbackoffice";

	// implement here constants used by this extension
	public static final String TRAVELLER_TYPE_PASSENGER = "PASSENGER";

	public static final String TRAVEL_GLOBAL_NOTIFICATION_EVENT_TYPE = "TravelMessage";

	public static final String CREATE_INVENTORY_BOOKING_CLASS_TRANSPORT_OFFERING_RADIO_BUTTON_ID = "transportOffering";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_TRAVEL_SECTOR_RADIO_BUTTON_ID = "travelSector";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_TRAVEL_SCHEDULE_RADIO_BUTTON_ID = "travelSchedule";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_LEVEL_ITEM_ID = "manageStockLevelInfo";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_SCHEDULE_CONFIGURATION_ITEM_ID = "searchScheduleModel";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_TRAVEL_SECTOR_ITEM_ID = "travelSector";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_FLIGHT_ITEM_ID = "transportOffering";
	public static final String CREATE_INVENTORY_BOOKING_CLASS_MANAGE_STOCK_PROPERTIES_GRID_ID = "manageStockLevelProperties";

	public static final String CREATE_BUNDLE_TEMPLATE_ITEM_ID = "newBundleTemplate";
	public static final String CREATE_FARE_BUNDLE_TEMPLATE_ITEM_ID = "fareBundleTemplate";
	public static final String CREATE_ANCILLARY_BUNDLE_TEMPLATE_ITEM_ID = "ancillaryBundleTemplate";

	public static final String COLON = ":";
	public static final String SPACE = " ";
	public static final String DOT = ".";
	public static final String TYPE_REFERENCE = "T";
	public static final String TYPE_REFERENCE_CONSTANT = "TYPE_REFERENCE_CONST";

	public static final String TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE_PARAM_NAME = "defaultMinVal";
	public static final int TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE = 0;
	public static final String TRANSPORT_OFFERING_DURATION_HR_SPINNER_CONSTRAINT = "min 0";
	public static final String TRANSPORT_OFFERING_DURATION_MIN_SPINNER_CONSTRAINT = "min 0 max 59";
	public static final String CREATE_SCHEDULE_CONFIRMATION_POPUP = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.confirmationpopup.text";
	public static final String CREATE_SCHEDULE_VALIDATION_MESSAGE = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.validationpopup.text";
	public static final String CREATE_SCHEDULE_CRONJOB_SUCCESS_MESSAGE = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.cronjob.success.text";
	public static final String CREATE_SCHEDULE_DEPARTURETIME_HR = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.departuretime.hr";
	public static final String CREATE_SCHEDULE_DEPARTURETIME_MIN = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.departuretime.min";
	public static final String CREATE_SCHEDULE_NO_CHECKBOX_SELECTED_VALIDATION_MESSAGE = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.nocheckboxselected.text";
	public static final String MESSAGE_BOX_WARNING_TITLE = "com.hybris.cockpitng.widgets.configurableflow.messagebox.title.warning";
	public static final String CREATE_SCHEDULE_PREVIEW_CONFIRMATION_POPUP_TITLE = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.preview.confirmation.title";
	public static final String CREATE_SCHEDULE_COPY_BUTTON_LABEL = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.copy.label";
	public static final String CREATE_SCHEDULE_PASTE_BUTTON_LABEL = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.paste.label";
	public static final String MODIFY_SCHEDULE_NOT_AVAILABLE = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.notavailable";
	public static final String DUPLICATE_MODIFY_SCHEDULE_FOUND = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.duplicate";
	public static final String MODIFY_SCHEDULE_TOTAL_TRANSPORT_OFFERINGS_UPDATED = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.updated.totaltransportofferings";
	public static final String SCHEDULE_GRID_DURATION_LABEL = "com.hybris.cockpitng.widgets.configurableflow.create.scheduleconfiguration.scheduleconfigurationday.duration";
	public static final String ERROR_TITLE = "error.title.message";
	public static final String MODIFY_SCHEDULE_MISSING_MANDATORY_FIELDS = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.missingmandatoryfields";
	public static final String MODIFY_MISSING_SELECTED_SCHEDULE = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.missingselectedschedule";
	public static final String MODIFY_SCHEDULE_MISSING_TRAVEL_SECTOR = "de.hybris.platform.travelbackoffice.widget.modifywizard.modifyscheduleconfiguration.missingtravelsector";
	public static final String MODIFY_INVENTORY_TRANSPORTOFFERING_NO_STOCK = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.transportofferingnostock";
	public static final String MODIFY_INVENTORY_TRANSPORTOFFERING_NO_COMMON_STOCK = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.transportofferingnocommonstock";
	public static final String BOOKING_CLASS_LABEL = "create.inventory.bookingclass.preview.wizard.bookingclass";
	public static final String AVAILABLE_QUANTITY_LABEL = "create.inventory.bookingclass.preview.wizard.availablequantity";
	public static final String OVERSELLING_QUANTITY_LABEL = "create.inventory.bookingclass.preview.wizard.oversellingquantity";
	public static final String INSTOCK_STATUS_LABEL = "create.inventory.bookingclass.preview.wizard.instockstatus";
	public static final String RESERVED_QUANTITY_LABEL = "modify.inventory.transportoffering.reservedquantity";
	public static final String MODIFY_INVENTORY_NO_TRANSPORTOFFERING_SELECTED = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.notransportofferingselected";
	public static final String MODIFY_INVENTORY_NO_STOCK_SELECTED = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.validationpopup.nostockselected";
	public static final String MODIFY_INVENTORY_SELECT_CHECKBOX = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.selectcheckbox.text";
	public static final String MODIFY_INVENTORY_AVAILABLE_OR_OVERSELLING_QTY_EMPTY = "de.hybris.platform.travelbackoffice.widget.modifywizard.inventory.validationpopup.availableoroversellingempty";
	public static final String SEAT_MAP_FINDER_MANDATORY_FIELDS_MISSING = "de.hybris.platform.travelbackoffice.widget.seatmap.finder.missingmandatoryfields";

	public static final String SCHEDULE_CONFIGURATIONS = "scheduleConfigurations";
	public static final String IS_CHANGE_SECTOR_CONFIRMED = "isChangeSectorConfirmed";
	public static final String SEARCH_SCHEDULE_INFO = "searchScheduleInfo";
	public static final String ITEM = "item";
	public static final String SAVED_TRAVEL_SECTOR = "savedTravelSector";
	public static final String FROM_PREVIOUS_STEP = "fromPreviewStep";

	private TravelbackofficeConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
