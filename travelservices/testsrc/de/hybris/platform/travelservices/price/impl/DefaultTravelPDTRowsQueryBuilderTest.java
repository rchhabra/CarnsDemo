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
*/

package de.hybris.platform.travelservices.price.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.platform.core.PK;
import de.hybris.platform.travelservices.constants.TravelservicesConstants;
import de.hybris.platform.travelservices.model.product.FareProductModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;


public class DefaultTravelPDTRowsQueryBuilderTest
{
	private final DefaultTravelPDTRowsQueryBuilder defaultTravelPDTRowsQueryBuilder = new DefaultTravelPDTRowsQueryBuilder(
			FareProductModel._TYPECODE);

	@Test
	public void testBuildWithAnyPrductAnyUser()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?anyProduct) and "
				+ "{userMatchQualifier} in (?anyUser)";
		defaultTravelPDTRowsQueryBuilder.withAnyProduct();
		defaultTravelPDTRowsQueryBuilder.withAnyUser();
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildWithProductUser()
	{
		final PK productPK = PK.fromLong(1l);
		final PK userPK = PK.fromLong(2l);
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?product) and "
				+ "{userMatchQualifier} in (?user)";
		defaultTravelPDTRowsQueryBuilder.withProduct(productPK);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildWithProductGroupUserGroup()
	{
		final PK productPK = PK.fromLong(1l);
		final PK userPK = PK.fromLong(2l);
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?productGroup) and "
				+ "{userMatchQualifier} in (?userGroup)";
		defaultTravelPDTRowsQueryBuilder.withProductGroup(productPK);
		defaultTravelPDTRowsQueryBuilder.withUserGroup(userPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildWithAnyPrductID()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId and "
				+ "{productId}=?productId";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildNoParams()
	{
		final String expected = "select {PK} from {FareProduct}";
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildWithProduct()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?product)";
		final PK productPK = PK.fromLong(1l);
		defaultTravelPDTRowsQueryBuilder.withProduct(productPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildWithAnyPrductIDUser()
	{
		final String expected = "select x.PK from ({{select {PK} from {FareProduct} where {userMatchQualifier} in (?user)}} UNION"
				+ " {{select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId and {productId}=?productId and"
				+ " {userMatchQualifier} in (?user)}}) x";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		final PK userPK = PK.fromLong(2l);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.build().getQuery());
	}

	@Test
	public void testBuildPriceQueryAndParamsProductNoParams()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId and "
				+ "{productId}=?productId and {travelRouteCode} is null and {travelSectorCode} is null and {transportOfferingCode} "
				+ "is null";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildPriceQueryAndParams(Collections.EMPTY_MAP).getQuery());
	}

	@Test
	public void testBuildPriceQueryAndParamsUserRouteParam()
	{
		final String expected = "select {PK} from {FareProduct} where {userMatchQualifier} in (?user) and {travelRouteCode} = "
				+ "?travelRouteCode";
		final Map<String, String> searchParams = new HashMap<>();
		searchParams.put("travelRouteCode", "LHR_DXB");
		final PK userPK = PK.fromLong(2l);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildPriceQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildPriceQueryAndParamsProductSectorParam()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?product) and "
				+ "{travelSectorCode} = ?travelSectorCode";
		final Map<String, String> searchParams = new HashMap<>();
		searchParams.put("travelSectorCode", "LHR_DXB");
		final PK productPK = PK.fromLong(1l);
		defaultTravelPDTRowsQueryBuilder.withProduct(productPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildPriceQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildPriceQueryAndParamsPrductIDUser()
	{
		final String expected = "select x.PK from ({{select {PK} from {FareProduct} where {userMatchQualifier} in (?user) and "
				+ "{travelRouteCode} = ?travelRouteCode}} UNION {{select {PK} from {FareProduct} where "
				+ "{productMatchQualifier}=?matchByProductId and {productId}=?productId and {userMatchQualifier} in (?user) and "
				+ "{travelRouteCode} = ?travelRouteCode}}) x";
		final Map<String, String> searchParams = new HashMap<>();
		searchParams.put("travelRouteCode", "LHR_DXB");
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		final PK userPK = PK.fromLong(2l);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildPriceQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildTaxQueryAndParamsProductNoParams()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId and "
				+ "{productId}=?productId and {originCountry} is null and {originTransportFacility} is null and {passengerType} is "
				+ "null";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		final Map<String, List<String>> searchParams = new HashMap<>();
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, Collections.EMPTY_LIST);
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, Collections.EMPTY_LIST);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildTaxQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildTaxQueryAndParamsProductOriginCountry()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId and "
				+ "{productId}=?productId and ((({originCountry} = ?originCountry and {passengerType} is null)) OR ("
				+ "({originTransportFacility} = ?originTransportFacility and {passengerType} is null)))";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		final Map<String, List<String>> searchParams = new HashMap<>();
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, Stream.of("LGW").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, Stream.of("GB").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, Collections.EMPTY_LIST);
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildTaxQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildTaxQueryAndParamsPassengerTypes()
	{
		final String expected = "select x.PK from ({{select {PK} from {FareProduct} where {userMatchQualifier} in (?user) and (("
				+ "({originCountry} = ?originCountry and {passengerType} = ?passengerType) OR ({originCountry} = ?originCountry "
				+ "and {passengerType} = ?passengerType)) OR (({originTransportFacility} = ?originTransportFacility and "
				+ "{passengerType} = ?passengerType) OR ({originTransportFacility} = ?originTransportFacility and {passengerType} "
				+ "= ?passengerType)))}} UNION {{select {PK} from {FareProduct} where {productMatchQualifier}=?matchByProductId "
				+ "and {productId}=?productId and {userMatchQualifier} in (?user) and ((({originCountry} = ?originCountry and "
				+ "{passengerType} = ?passengerType) OR ({originCountry} = ?originCountry and {passengerType} = ?passengerType)) "
				+ "OR (({originTransportFacility} = ?originTransportFacility and {passengerType} = ?passengerType) OR "
				+ "({originTransportFacility} = ?originTransportFacility and {passengerType} = ?passengerType)))}}) x";
		defaultTravelPDTRowsQueryBuilder.withProductId("1234");
		final PK userPK = PK.fromLong(2l);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		final Map<String, List<String>> searchParams = new HashMap<>();
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY, Stream.of("LGW").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, Stream.of("GB").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, Stream.of("Adult", "Child").collect(Collectors.toList()));
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildTaxQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildTaxQueryAndParamsMultiSectors()
	{
		final String expected = "select {PK} from {FareProduct} where {productMatchQualifier} in (?product) and "
				+ "{userMatchQualifier} in (?user) and ((({originCountry} IN (?originCountry0,?originCountry1) and {passengerType} "
				+ "= ?passengerType) OR ({originCountry} IN (?originCountry0,?originCountry1) and {passengerType} = ?passengerType)"
				+ ") OR (({originTransportFacility} IN (?originTransportFacility0,?originTransportFacility1) and {passengerType} = "
				+ "?passengerType) OR ({originTransportFacility} IN (?originTransportFacility0,?originTransportFacility1) and "
				+ "{passengerType} = ?passengerType)))";
		final PK productPK = PK.fromLong(1l);
		defaultTravelPDTRowsQueryBuilder.withProduct(productPK);
		final PK userPK = PK.fromLong(2l);
		defaultTravelPDTRowsQueryBuilder.withUser(userPK);
		final Map<String, List<String>> searchParams = new HashMap<>();
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINTRANSPORTFACILITY,
				Stream.of("LGW", "CDG").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_ORIGINCOUNTRY, Stream.of("GB", "FR").collect(Collectors.toList()));
		searchParams.put(TravelservicesConstants.SEARCH_PASSENGERTYPE, Stream.of("Adult", "Child").collect(Collectors.toList()));
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildTaxQueryAndParams(searchParams).getQuery());
	}

	@Test
	public void testBuildTaxQueryNoParams()
	{
		final String expected = "select {PK} from {FareProduct}";
		assertEquals(expected, defaultTravelPDTRowsQueryBuilder.buildTaxQueryAndParams(null).getQuery());
	}

}
