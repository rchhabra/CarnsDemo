/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2016 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 *
 *
 */

package de.hybris.platform.travelbackoffice.renderer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Div;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.config.jaxb.wizard.ContentType;
import com.hybris.cockpitng.config.jaxb.wizard.Parameter;
import com.hybris.cockpitng.config.jaxb.wizard.PropertyListType;
import com.hybris.cockpitng.config.jaxb.wizard.PropertyType;
import com.hybris.cockpitng.config.jaxb.wizard.Renderer;
import com.hybris.cockpitng.config.jaxb.wizard.ViewType;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.util.BackofficeSpringUtil;
import com.hybris.cockpitng.widgets.common.WidgetComponentRenderer;
import com.hybris.cockpitng.widgets.configurableflow.renderer.ConfigurableFlowRenderer;


/**
 * Class extends the {@link ConfigurableFlowRenderer} to customize the OOTB behaviour of Configurable Flow Wizard.
 */
public class TravelConfigurableFlowRenderer extends ConfigurableFlowRenderer
{
	private static final Logger LOG = Logger.getLogger(TravelConfigurableFlowRenderer.class);

	@Override
	protected void renderContent(final Component parent, final ContentType content)
	{
		final List<Object> propertyOrPropertyListOrCustomView = content.getPropertyOrPropertyListOrCustomView();

		if (propertyOrPropertyListOrCustomView != null)
		{
			for (final Object contentElement : propertyOrPropertyListOrCustomView)
			{
				if (contentElement instanceof ViewType)
				{
					renderCustom(parent, (ViewType) contentElement);
				}
				else if (contentElement instanceof PropertyType)
				{
					final Div propDiv = new Div();
					propDiv.appendChild(createPropertyLine(StringUtils.EMPTY, (PropertyType) contentElement));
					parent.appendChild(propDiv);
				}
				else if (contentElement instanceof PropertyListType)
				{
					final PropertyListType propList = (PropertyListType) contentElement;
					final Boolean readonly = propList.isReadonly();
					final String root = propList.getRoot();
					for (final PropertyType prop : propList.getProperty())
					{
						if (prop.isReadonly() == null)
						{
							prop.setReadonly(readonly);
						}
						final Div propDiv = new Div();
						propDiv.appendChild(createPropertyLine(root, prop));
						parent.appendChild(propDiv);
					}
				}
			}
		}
	}

	/**
	 * Method responsible to render the custom view either by zul file or java class.
	 * 
	 * @param parent
	 * @param customView
	 */
	protected void renderCustom(final Component parent, final ViewType customView)
	{
		if (extractRenderer(customView.getContent()).isPresent())
		{
			renderByRendererBean(parent, customView);
		}
		else
		{
			if (ZUL.equals(customView.getLang()))
			{
				if (StringUtils.isNotBlank(customView.getSrc()))
				{
					renderByZulFile(parent, customView);
				}
				else if (StringUtils.isNotBlank(extractTextContent(customView.getContent())))
				{
					// render by zul code
					Executions.createComponentsDirectly(extractTextContent(customView.getContent()), null, parent, null);
				}
			}
		}
	}

	/**
	 * Extracts the text content from given list of {@link Serializable} mixed content.
	 * @param mixedContent
	 */
	protected String extractTextContent(final List<Serializable> mixedContent)
	{

		final StringBuilder textContet = new StringBuilder();
		for (final Serializable serializable : mixedContent)
		{
			if (serializable instanceof String)
			{
				textContet.append(serializable);
			}
		}
		return textContet.toString();

	}

	/**
	 * Method responsible to render view by zul file.
	 * 
	 * @param parent
	 * @param customView
	 */
	protected void renderByZulFile(final Component parent, final ViewType customView)
	{
		try
		{
			Executions.createComponents(customView.getSrc(), parent, null);
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Returns an instance of {@link Renderer} for given list of {@link Serializable} content.
	 * 
	 * @param mixedContent
	 */
	protected Optional<Renderer> extractRenderer(final List<Serializable> mixedContent)
	{
		Renderer ret = null;
		for (final Serializable serializable : mixedContent)
		{
			if (serializable instanceof JAXBElement && ((JAXBElement) serializable).getValue() instanceof Renderer)
			{
				ret = (Renderer) ((JAXBElement) serializable).getValue();
			}
		}
		return Optional.ofNullable(ret);
	}

	/**
	 * Method responsible to render view from java bean.
	 * 
	 * @param parent
	 * @param customView
	 */
	protected void renderByRendererBean(final Component parent, final ViewType customView)
	{
		final Optional<Renderer> customRenderer = extractRenderer(customView.getContent());

		customRenderer.ifPresent(renderer -> {
			if (StringUtils.isNotBlank(renderer.getSpringBean()))
			{
				final Object bean = BackofficeSpringUtil.getBean(renderer.getSpringBean(), WidgetComponentRenderer.class);
				if (bean instanceof WidgetComponentRenderer)
				{
					final WidgetComponentRenderer widgetComponentRenderer = (WidgetComponentRenderer) bean;
					final String typeCode = getTypeCodeFromWizardCtx();
					final DataType dataType = StringUtils.isNotEmpty(typeCode) ? loadDataTypeInternal(typeCode) : null;
					widgetComponentRenderer.render(parent, customView, extractParameters(renderer.getParameter()), dataType,
							getWidgetInstanceManager());
				}
				else
				{
					LOG.warn("Could not load renderer bean with id '" + renderer.getSpringBean() + "'.");
				}
			}
		});
	}

	/**
	 * Returns a map from list of {@link Parameter}
	 * 
	 * @param parameterList
	 */
	protected Map<String, String> extractParameters(final List<Parameter> parameterList)
	{
		final Map<String, String> parameters = Maps.newHashMap();
		if (parameterList != null)
		{
			for (final Parameter parameter : parameterList)
			{
				parameters.put(parameter.getName(), parameter.getValue());
			}
		}
		return parameters;
	}

	/**
	 * Loads the type using {@link TypeFacade} for given typeCode.
	 * 
	 * @param typeCode
	 */
	protected DataType loadDataTypeInternal(final String typeCode)
	{
		DataType result = null;
		try
		{
			result = getTypeFacade().load(typeCode);
		}
		catch (final TypeNotFoundException e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Type %s not found!", typeCode), e);
			}
		}
		return result;
	}
}
