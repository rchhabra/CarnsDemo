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
import de.hybris.platform.travelservices.model.travel.TerminalModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;

import com.hybris.cockpitng.components.Editor;
import com.hybris.cockpitng.editors.CockpitEditorRenderer;
import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;
import com.hybris.cockpitng.labels.LabelService;


/**
 * Default combobox editor for airport terminals for "create-schedule" wizard.
 */
public class DefaultAirportTerminalDropDownEditor implements CockpitEditorRenderer<TerminalModel>
{
	@Resource(name = "labelService")
	private LabelService labelService;

	@Override
	public void render(final Component parent, final EditorContext<TerminalModel> context,
			final EditorListener<TerminalModel> listener)
	{
		final Combobox combobox = new Combobox();
		combobox.setId((String) context.getParameter("editorProperty"));
		final List<TerminalModel> terminals = Objects.isNull(context.getParameter("terminals")) ? Collections.emptyList()
				: (List<TerminalModel>) context.getParameter("terminals");
		final ListModelList<TerminalModel> model = new ListModelList<>(terminals);
		model.setSelection(Collections.singletonList(context.getInitialValue()));
		combobox.setModel(model);
		combobox.setParent(parent);
		final boolean isDisabled = context.getParameterAsBoolean("isDisabled", false);
		if (isDisabled)
		{
			combobox.setValue(null);
		}
		combobox.setDisabled(isDisabled);
		combobox.setReadonly(true);
		combobox.setAutodrop(true);
		combobox.setItemRenderer(new ComboitemRenderer<TerminalModel>()
		{
			@Override
			public void render(final Comboitem item, final TerminalModel data, final int index)
			{
				item.setValue(data);
				String label = labelService.getObjectLabel(data);
				if (StringUtils.isBlank(label))
				{
					label = data.getName();
				}
				item.setLabel(label);
			}
		});
		combobox.addEventListener(Events.ON_OPEN, new EventListener<OpenEvent>()
		{
			TerminalModel valueOnOpen = null;

			@Override
			public void onEvent(final OpenEvent event) throws Exception
			{
				final Optional<Component> optional = event.getTarget().getParent().getParent().getChildren().stream()
						.filter(component -> component instanceof Editor)
						.filter(component -> StringUtils.contains(((Editor) component).getId(), ScheduleConfigurationDayModel.SELECTED))
						.findFirst();
				if (optional.isPresent())
				{
					((Checkbox) ((Editor) optional.get()).getFirstChild()).setChecked(true);
				}

				final Row row = (Row) ((Editor) ((Combobox) event.getTarget()).getParent()).getParent();
				if (row.getFirstChild() instanceof Button)
				{
					((Button) row.getFirstChild())
							.setLabel(Labels.getLabel(TravelbackofficeConstants.CREATE_SCHEDULE_COPY_BUTTON_LABEL));
				}
				if (event.isOpen())
				{
					valueOnOpen = getSelectedItemValue(combobox);
				}
				else
				{
					final TerminalModel selectedVal = getSelectedItemValue(combobox);
					if (ObjectUtils.notEqual(selectedVal, valueOnOpen))
					{
						listener.onValueChanged(selectedVal);
					}
				}
			}
		});
	}

	/**
	 * Returns selected {@link TerminalModel} from combobox.
	 *
	 * @param combobox
	 */
	protected TerminalModel getSelectedItemValue(final Combobox combobox)
	{
		TerminalModel selectedVal = null;

		final Comboitem selectedItem = combobox.getSelectedItem();
		if (selectedItem != null)
		{
			selectedVal = selectedItem.getValue();
		}
		return selectedVal;
	}

}
