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
package de.hybris.platform.ndcservices.setup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.ndcservices.constants.NdcservicesConstants;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


@SystemSetup(extension = NdcservicesConstants.EXTENSIONNAME)
public class NDCServicesSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(NDCServicesSystemSetup.class);
	private static final String IMPORT_NDC_DATA = "importNDCData";

	/**
	 * This method will be called during the system initialization and update.
	 *
	 * @param context the context provides the selected parameters and values
	 */
	@SystemSetup(type = SystemSetup.Type.ALL, process = SystemSetup.Process.ALL)
	public void createData(final SystemSetupContext context)
	{
		final boolean importNDCData = this.getBooleanSystemSetupParameter(context, IMPORT_NDC_DATA);
		if (importNDCData)
		{
			LOG.info("Importing ndc essential data");
			importImpexFile(context, "/impex/ndc-data.impex", true);
		}
	}

	@SystemSetupParameterMethod
	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

		params.add(createBooleanSystemSetupParameter(IMPORT_NDC_DATA, "Import NDC Data", true));
		return params;
	}
}
