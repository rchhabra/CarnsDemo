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

package de.hybris.platform.travelbackoffice.editor;

import de.hybris.platform.travelbackoffice.constants.TravelbackofficeConstants;
import de.hybris.platform.travelservices.model.travel.ScheduleConfigurationDayModel;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Spinner;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


/**
 * Default spinner editor for flight duration for "create-schedule" wizard.
 */
public class DefaultTransportOfferingDurationSpinnerEditor implements CockpitEditorRenderer<Integer>
{
	@Override
	public void render(final Component parent, final EditorContext<Integer> context, final EditorListener<Integer> listener)
	{
		final Spinner spinnerView = new Spinner();
		Integer value = context.getInitialValue();
		if (value == null)
		{
			value = (Integer) context.getParameter(TravelbackofficeConstants.TRANSPORT_OFFERING_DURATION_SPINNER_MIN_VALUE_PARAM_NAME);
		}
		final boolean isDisabled = context.getParameterAsBoolean("isDisabled", false);
		if (isDisabled)
		{
			spinnerView.setValue(0);
		}
		spinnerView.setDisabled(isDisabled);
		spinnerView.setValue(value);
		spinnerView.setWidth("80px");
		spinnerView.setId((String) context.getParameter("editorProperty"));
		spinnerView.addEventListener(Events.ON_CHANGE, (event) -> {
			handleChangeEvent(listener, spinnerView, event);
		});

		spinnerView.setConstraint((String) context.getParameter("constraint"));
		spinnerView.setParent(parent);
	}

	/**
	 * Method that handles on change event for spinner.
	 *
	 * @param listener
	 * @param editorView
	 * @param event
	 */
	protected void handleChangeEvent(final EditorListener<Integer> listener, final Spinner editorView, final Event event)
	{
		final Optional<Component> optional = event.getTarget().getParent().getParent().getChildren().stream()
				.filter(component -> component instanceof Editor)
				.filter(component -> StringUtils.contains(((Editor) component).getId(), ScheduleConfigurationDayModel.SELECTED))
				.findFirst();
		if (optional.isPresent())
		{
			((Checkbox) ((Editor) optional.get()).getFirstChild()).setChecked(true);
		}

		final Row row = (Row) ((Editor) ((Spinner) event.getTarget()).getParent()).getParent();
		if (row.getFirstChild() instanceof Button)
		{
			((Button) row.getFirstChild()).setLabel(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL));
		}
		listener.onValueChanged(editorView.getValue());
	}
}
