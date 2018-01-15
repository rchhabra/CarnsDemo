/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 *
 */

package de.hybris.platform.travelbackoffice.widgets.bundle;


import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.config.jaxb.wizard.CustomType;
import com.hybris.cockpitng.core.impl.DefaultWidgetModel;
import com.hybris.cockpitng.core.model.WidgetModel;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandler;
import com.hybris.cockpitng.widgets.configurableflow.FlowActionHandlerAdapter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;


/**
 * Custom Handler for the custom navigation action defined in the First Step of the Bundle Wizard. This handler sets the target on
 * the customType based on the selected radio button.
 */
public class CreateBundleCustomFirstStepHandler implements FlowActionHandler
{

	protected static final String ATTRIBUTE_KEY = "mapping";
	public static final String CREATE_BUNDLE_WIZARD_FIRST_STEP_ERROR_MESSAGE = "create.bundle.wizard.first.step.error.message";
	public static final String CREATE_BUNDLE_WIZARD_FIRST_STEP_ERROR_TITLE = "create.bundle.wizard.first.step.error.title";

	@Override
	public void perform(final CustomType customType, final FlowActionHandlerAdapter flowActionHandlerAdapter,
			final Map<String, String> map)
	{
		final Widgetslot widgetslot = flowActionHandlerAdapter.getWidgetInstanceManager().getWidgetslot();
		if (Objects.nonNull(widgetslot))
		{
			if (!validateMandatoryFields(widgetslot))
			{
				Messagebox.show(Labels.getLabel(CREATE_BUNDLE_WIZARD_FIRST_STEP_ERROR_MESSAGE),
						Labels.getLabel(CREATE_BUNDLE_WIZARD_FIRST_STEP_ERROR_TITLE), Messagebox.OK, Messagebox.EXCLAMATION);
				return;
			}

			final WidgetModel widgetModel = flowActionHandlerAdapter.getWidgetInstanceManager().getModel();
			Selectors.find(widgetslot, "radio[selected=false]").stream()
					.filter(Radio.class::isInstance).map(Radio.class::cast)
					.forEach(radio -> resetReferenceSelection(widgetModel, radio.getId()));
		}
		flowActionHandlerAdapter.custom();
	}

	/**
	 * Validates if the mandatory reference editor is populated when the corresponding radio button is selected.
	 *
	 * @param widgetslot
	 * 		as the widget slot
	 *
	 * @return boolean true if it is valid, false otherwise
	 */
	protected boolean validateMandatoryFields(final Widgetslot widgetslot)
	{
		final Optional<Radio> radioOptional = Selectors.find(widgetslot, "radio[selected=true]").stream()
				.filter(Radio.class::isInstance).map(Radio.class::cast).findFirst();
		if (!radioOptional.isPresent())
		{
			return true;
		}

		final List<Component> components = Selectors.find(widgetslot, getReferenceEditorId(radioOptional.get()));
		if (CollectionUtils.isNotEmpty(components))
		{
			final Optional<Editor> editor = components.stream().filter(Editor.class::isInstance).map(Editor.class::cast)
					.findFirst();
			return editor.isPresent() && editor.get().getValue() != null;
		}
		return true;
	}

	/**
	 * Returns the id of the reference editor corresponding to the given radio
	 *
	 * @param radio
	 * 		as the radio
	 *
	 * @return the reference editor id
	 */
	protected String getReferenceEditorId(final Radio radio)
	{
		return "#" + radio.getId() + "ReferenceEditor";
	}

	/**
	 * Reset the selection in the reference editor corresponding to the radio button for the given radioId.
	 *
	 * @param widgetModel
	 * 		as the widget model.
	 * @param radioId
	 * 		as the id of the radio button
	 */
	protected void resetReferenceSelection(final WidgetModel widgetModel, final String radioId)
	{
		final DefaultWidgetModel defaultWidgetModel = (DefaultWidgetModel) widgetModel;
		final Map attributesValues = (Map) defaultWidgetModel.get(ATTRIBUTE_KEY);
		attributesValues.remove(radioId);
	}

}
