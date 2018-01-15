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

package de.hybris.platform.travelfacades.accommodation.autosuggestion.parser;

import de.hybris.platform.commercefacades.accommodation.AutocompletePredictionData;
import de.hybris.platform.commercefacades.accommodation.AutocompletionResponseData;
import de.hybris.platform.storelocator.GeolocationResponseParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * The Accommodation autosuggest response parser.
 */
public class AccommodationAutosuggestResponseParser implements GeolocationResponseParser<AutocompletionResponseData>
{
	private static final Logger LOGGER = Logger.getLogger(AccommodationAutosuggestResponseParser.class);

	private static String NODE_PLACE_ID = "place_id";
	private static String STATUS_OK = "OK";
	private static String NODE_TYPE = "type";
	private static String NODE_DESCRIPTION = "description";

	private static final String FEATURES_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

	@Override
	public AutocompletionResponseData extractData(final ClientHttpResponse response) throws IOException
	{
		final AutocompletionResponseData autocompleteResponseData = new AutocompletionResponseData();

		final XPath xpath = XPathFactory.newInstance().newXPath();

		final InputSource inputSource = new InputSource(response.getBody());
		try
		{
			final Document doc = createDocumentBuilder().parse(inputSource);
			autocompleteResponseData.setStatus(getStatus(doc, xpath));

			if (StringUtils.equalsIgnoreCase(autocompleteResponseData.getStatus(), STATUS_OK))
			{
				autocompleteResponseData.setPredictions(getPredictions(doc, xpath));
			}
			else
			{
				autocompleteResponseData.setMessage(getErrorMessage(doc, xpath));
			}
		}
		catch (final ParserConfigurationException e)
		{
			LOGGER.error("Error In Document Builder " + e);
		}
		catch (final SAXException e)
		{
			LOGGER.error("Error In  XML parsing " + e);
		}

		return autocompleteResponseData;
	}

	/**
	 * Returns the documentBuilder.
	 *
	 * @return the document builder
	 * @throws ParserConfigurationException
	 * 		the parser configuration exception
	 */
	protected DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setFeature(FEATURES_DISALLOW_DOCTYPE_DECL, true);
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		return factory.newDocumentBuilder();
	}

	@Override
	public AutocompletionResponseData parseResponseDocument(final org.dom4j.Document doc)
	{
		return null;
	}


	protected String getStatus(final Document doc, final XPath xpath)
	{
		try
		{
			final Node node = (Node) xpath.compile("//status").evaluate(doc, XPathConstants.NODE);

			return node.getTextContent();
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);
			return null;
		}
	}

	protected List<AutocompletePredictionData> getPredictions(final Document doc, final XPath xpath)
	{
		final List<AutocompletePredictionData> predictionDatas = new ArrayList<>();

		try
		{
			final NodeList rootNode = (NodeList) xpath.compile("//prediction").evaluate(doc, XPathConstants.NODESET);

			for (int idxNode = 0; idxNode < rootNode.getLength(); idxNode++)
			{
				final Node nNode = rootNode.item(idxNode);
				final AutocompletePredictionData predictionData = new AutocompletePredictionData();
				final Element eElement = (Element) nNode;
				predictionData.setDescription(eElement.getElementsByTagName(NODE_DESCRIPTION).item(0).getTextContent());
				predictionData.setPlaceId(eElement.getElementsByTagName(NODE_PLACE_ID).item(0).getTextContent());
				final NodeList eType = eElement.getElementsByTagName(NODE_TYPE);
				final List<String> types = new ArrayList<>();
				for (int idxType = 0; idxType < eType.getLength(); idxType++)
				{
					types.add(eType.item(idxType).getTextContent());
				}
				predictionData.setTypes(types);
				predictionDatas.add(predictionData);
			}
			return predictionDatas;
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);
			return Collections.emptyList();
		}
	}

	protected String getErrorMessage(final Document doc, final XPath xpath)
	{
		try
		{
			final Node node = (Node) xpath.compile("//error_message").evaluate(doc, XPathConstants.NODE);

			return node == null ? null : node.getTextContent();
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);
			return null;
		}
	}
}
