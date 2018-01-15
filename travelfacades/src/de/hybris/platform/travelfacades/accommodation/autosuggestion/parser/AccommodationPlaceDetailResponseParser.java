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

import de.hybris.platform.commercefacades.accommodation.PlaceDetailsResponseData;
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
 * The type Accommodation place detail response parser.
 */
public class AccommodationPlaceDetailResponseParser implements GeolocationResponseParser<PlaceDetailsResponseData>
{
	private static final Logger LOGGER = Logger.getLogger(AccommodationPlaceDetailResponseParser.class);
	private static String NODE_LATITUDE = "lat";
	private static String NODE_LONGITUDE = "lng";
	private static String STATUS_OK = "OK";

	private static final String FEATURES_DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

	@Override
	public PlaceDetailsResponseData extractData(final ClientHttpResponse response) throws IOException
	{
		final PlaceDetailsResponseData placeDetailsResponseData = new PlaceDetailsResponseData();

		final XPath xpath = XPathFactory.newInstance().newXPath();
		final InputSource inputSource = new InputSource(response.getBody());
		try
		{
			final Document doc = createDocumentBuilder().parse(inputSource);
			placeDetailsResponseData.setStatus(getStatus(doc, xpath));
			if (StringUtils.equalsIgnoreCase(placeDetailsResponseData.getStatus(), STATUS_OK))
			{
				populateLocation(doc, xpath, placeDetailsResponseData);
				placeDetailsResponseData.setFormattedAddress(getFormattedAddress(doc, xpath));
				placeDetailsResponseData.setTypes(getPlaceTypes(doc, xpath, placeDetailsResponseData));
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

		return placeDetailsResponseData;
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
	public PlaceDetailsResponseData parseResponseDocument(final org.dom4j.Document doc)
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
			return StringUtils.EMPTY;
		}
	}

	protected void populateLocation(final Document doc, final XPath xpath,
			final PlaceDetailsResponseData placeDetailsResponseData)
	{
		try
		{
			final NodeList rootNode = (NodeList) xpath.compile("//location").evaluate(doc, XPathConstants.NODESET);

			for (int idxNode = 0; idxNode < rootNode.getLength(); idxNode++)
			{
				final Element eElement = (Element) rootNode.item(idxNode);
				placeDetailsResponseData
						.setLatitude(eElement.getElementsByTagName(NODE_LATITUDE).item(0).getTextContent());
				placeDetailsResponseData.setLongitude(eElement.getElementsByTagName(NODE_LONGITUDE).item(0).getTextContent());

			}
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);

		}
	}


	protected String getFormattedAddress(final Document doc, final XPath xpath)
	{
		try
		{
			final Node node = (Node) xpath.compile("/PlaceDetailsResponse/result/formatted_address").evaluate(doc,
					XPathConstants.NODE);

			return node.getTextContent();
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);
			return StringUtils.EMPTY;
		}
	}

	protected List<String> getPlaceTypes(final Document doc, final XPath xpath,
			final PlaceDetailsResponseData placeDetailsResponseData)
	{
		try
		{
			final NodeList rootNode = (NodeList) xpath.compile("/PlaceDetailsResponse/result/type").evaluate(doc,
					XPathConstants.NODESET);
			final List<String> types = new ArrayList<>(rootNode.getLength());
			for (int idxNode = 0; idxNode < rootNode.getLength(); idxNode++)
			{
				final Element eElement = (Element) rootNode.item(idxNode);
				types.add(eElement.getTextContent());
			}
			return types;
		}
		catch (final XPathExpressionException e)
		{
			LOGGER.error("Error In XPath Expression evaluation " + e);
			return Collections.emptyList();
		}
	}
}
