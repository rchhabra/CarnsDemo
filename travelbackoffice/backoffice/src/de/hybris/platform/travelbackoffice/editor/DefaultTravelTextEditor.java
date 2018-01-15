/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.travelbackoffice.editor;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


/**
 * Default Text Editor for TravelBackoffice for validating user input to accept only positive integer.
 */
public class DefaultTravelTextEditor implements CockpitEditorRenderer<String>
{
	@Override
	public void render(final Component parent, final EditorContext<String> context, final EditorListener<String> listener)
	{
		final Textbox editorView = new Textbox();
		editorView.setConstraint((comp, value) -> {
			validateInput(comp, (String) value);
		});
		if (StringUtils.isNotEmpty(String.valueOf(context.getInitialValue())))
		{
			editorView.setValue(String.valueOf(context.getInitialValue()));
		}
		editorView.setReadonly(!context.isEditable());
		editorView.addEventListener(Events.ON_CHANGING, event -> {
			if (event instanceof InputEvent)
			{
				handleChangingEvent(listener, editorView, ((InputEvent) event).getValue());
			}
		});
		editorView.setParent(parent);

	}

	/**
	 * Method responsible to handle user input event.
	 *
	 * @param listener
	 * @param editorView
	 * @param value
	 */
	protected void handleChangingEvent(final EditorListener<String> listener, final Textbox editorView, final String value)
	{
		validateInput(editorView, value);
		listener.onValueChanged(value);
	}

	/**
	 * Method responsible to validate user input and throws {@link WrongValueException} if user tries to enter negative
	 * or out of integer range values.
	 *
	 * @param component
	 * @param value
	 */
	protected void validateInput(final Component component, final String value)
	{
		if (StringUtils.isNotBlank(value))
		{
			if (!StringUtils.isNumeric(value))
			{
				throw new WrongValueException(component, "Value [" + value + "] is not a valid integer");
			}
			if (Integer.parseInt(value) < Integer.MIN_VALUE || Integer.parseInt(value) > Integer.MAX_VALUE)
			{
				throw new WrongValueException(component,
						"Value [" + value + "] is out of range: [" + Integer.MIN_VALUE + ";" + Integer.MAX_VALUE + "]");
			}
		}
	}
}
