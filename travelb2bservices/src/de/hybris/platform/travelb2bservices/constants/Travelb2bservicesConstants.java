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
package de.hybris.platform.travelb2bservices.constants;

import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;


/**
 * Global class for all Travelb2bservices constants. You can add global constants for your extension into this class.
 */
public final class Travelb2bservicesConstants extends GeneratedTravelb2bservicesConstants
{
	public static final String EXTENSIONNAME = "travelb2bservices";

	private Travelb2bservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "travelb2bservicesPlatformLogo";

	public static final String FIND_ORDER_BY_UNIT = "select DISTINCT {o." + OrderModel.PK + "},{o." + OrderModel.CODE + "},{o."
			+ OrderModel.CREATIONTIME + "},{o." + OrderModel.USER + "},{o." + OrderModel.UNIT + "},{o." + OrderModel.TOTALPRICE
			+ "}, {o. " + OrderModel.TOTALPRICE + "}+{o." + OrderModel.TOTALTAX + "} AS " + Travelb2bservicesConstants.TOTAL_WITH_TAX
			+ " from {" + OrderModel._TYPECODE + " as o join " + B2BUnitModel._TYPECODE + " as u on {o." + OrderModel.UNIT + "}={u."
			+ B2BUnitModel.PK + "} join " + UserModel._TYPECODE + " as us on {us." + UserModel.PK + "}={o." + OrderModel.USER
			+ "} join " + OrderEntryModel._TYPECODE + " as oe on {oe." + OrderEntryModel.ORDER + "}={o." + OrderModel.PK + "} join "
			+ B2BCostCenterModel._TYPECODE + " as cc on {oe." + OrderEntryModel.COSTCENTER + "}={cc." + B2BCostCenterModel.PK
			+ "} join " + OrderStatus._TYPECODE + " as os on {o." + OrderModel.STATUS + "}={os.pk}} where {u." + B2BUnitModel.UID
			+ "} IN (?unitCodes) AND {os.code} = '" + OrderStatus.ACTIVE.getCode() + "' AND {o." + OrderModel.VERSIONID
			+ "} IS NULL";

	public static final String TOTAL_WITH_TAX = "TOTAL_WITH_TAX";

	public static final String FROM_DATE_RESTRICTION = " AND {o." + OrderModel.CREATIONTIME + "} >= ?fromDate";

	public static final String TO_DATE_RESTRICTION = " AND {o." + OrderModel.CREATIONTIME + "} <= ?toDate";

	public static final String USER_RESTRICTIONS = " AND {us." + UserModel.UID + "} = ?userId";

	public static final String COST_CENTER_RESTRICTION = " AND {cc." + B2BCostCenterModel.CODE + "} = ?costCenterId";

	public static final String CURRENCY_RESTRICTION = " AND {o." + OrderModel.CURRENCY + "} = ?currency";
}
