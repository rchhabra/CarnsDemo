/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.travelseatmapservices.seatmap.response.handlers.impl;

import de.hybris.platform.travelseatmapservices.seatmap.response.SeatMapJSONObject;
import de.hybris.platform.travelseatmapservices.seatmap.response.handlers.SeatmapJsonObjectHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONML;


/**
 * This populates seatMapJsonObject with the SVG JSON.
 */
public class SeatmapJsonObjectSVGHandler implements SeatmapJsonObjectHandler
{
	private static final Logger LOG = Logger.getLogger(SeatmapJsonObjectSVGHandler.class);
	private static final String FEATURES_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

	@Override
	public void populate(final String seatMapPath, final SeatMapJSONObject seatMapJsonObject)
	{
		seatMapJsonObject.setSvg(getSVGJSON(seatMapPath));

	}

	protected Map<String, String> getSVGJSON(final String path)
	{
		final Map<String, String> map = new HashMap<>();
		try
		{
			final Path fileInputStream = Paths.get(path, "svg");
			Files.list(fileInputStream).filter(file -> !Files.isDirectory(file)).forEach(svgFile -> {
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Converting\t" + svgFile.getFileName() + "\t to JSON");
				}
				try
				{
					final InputStream svgFileInputStream = Files.newInputStream(svgFile);
					final String xml = IOUtils.toString(svgFileInputStream);
					svgFileInputStream.close();
					final String fileName = svgFile.getFileName().toString();
					map.put(fileName.substring(0, fileName.indexOf(".")), JSONML.toJSONObject(xml).toString());
				}
				catch (final IOException e)
				{
					LOG.error("Conversion Failed \t" + svgFile.getFileName() + "\t to JSON", e);
				}
			});
			return map;
		}
		catch (final IOException e)
		{
			LOG.error(e);
		}
		return map;
	}

	/**
	 * Returns the documentBuilder.
	 *
	 * @return the document builder
	 * @throws ParserConfigurationException
	 *            the parser configuration exception
	 */
	protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setFeature(FEATURES_DISALLOW_DOCTYPE_DECL, true);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		return factory.newDocumentBuilder();
	}
}
