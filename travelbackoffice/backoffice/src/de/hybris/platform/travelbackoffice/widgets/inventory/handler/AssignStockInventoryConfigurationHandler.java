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

import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.travelservices.constants.TravelacceleratorstorefrontValidationConstants;
import de.hybris.platform.travelservices.stocklevel.StockLevelAttributes;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.hybris.cockpitng.components.Editor;



/**
 * This handler is used to handle the next step logic for Step2, in Create Inventory for Booking Class
 */
public class AssignStockInventoryConfigurationHandler extends AbstractStockInventoryConfigurationHandler
{

	@Override
	protected StockLevelAttributes createStockLevelAttribute(final Row row)
	{
		final List<Component> children = row.getChildren();
		final StockLevelAttributes stockAttribute = new StockLevelAttributes();
		final String productCode = ((Textbox) children.get(0)).getValue();
		final Integer availableQuantity = ((Intbox) children.get(1)).getValue();
		final Integer oversellingQuantity = ((Intbox) children.get(2)).getValue();

		if (StringUtils.isBlank(productCode) || Objects.isNull(availableQuantity) || availableQuantity < 0 || (
				Objects.nonNull(oversellingQuantity) && oversellingQuantity < 0))
		{
			return null;
		}

		if (StringUtils.isNumeric(productCode)
				|| !Pattern.matches(TravelacceleratorstorefrontValidationConstants.REGEX_ALPHANUMERIC, productCode))
		{
			throw new WrongValueException(children.get(0), "Invalid Product Code" + productCode);
		}

		stockAttribute.setCode(productCode);
		stockAttribute.setAvailableQuantity(availableQuantity);
		stockAttribute.setOversellingQuantity(Objects.isNull(oversellingQuantity) ? 0 : oversellingQuantity);
		final Editor instockStatus = ((Editor) children.get(3));
		if (Objects.nonNull(instockStatus.getValue()))
		{
			stockAttribute.setInStockStatus(InStockStatus.valueOf(instockStatus.getValue().toString()));
		}
		return stockAttribute;
	}
}
