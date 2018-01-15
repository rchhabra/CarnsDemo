<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="accommodationsearch" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationsearch"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:if test="${not empty accommodationSearchProperties}">
<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
<c:set var="properties" value="${accommodationSearchResponse.properties}" />
	<c:choose>
		<c:when test="${resultsView == 'MAPVIEW'}">
			<json:array items="${accommodationSearchProperties}" var="property">
				<json:object escapeXml="false">
					<json:property name="htmlContent">
						<accommodationsearch:markPropertyList property="${property}" urlParameters="${urlParameters}" stayDateRange="${accommodationSearchResponse.criterion.stayDateRange}" />
					</json:property>
					<json:property name="latitude" value="${property.position.latitude}" />
					<json:property name="longitude" value="${property.position.longitude}" />
					<json:property name="name" value="${property.accommodationOfferingName}" />
				</json:object>
			</json:array>
		</c:when>
		<c:otherwise>
			<json:object escapeXml="false">
				<json:property name="listGridhtmlContent">
					<c:choose>
						<c:when test="${resultsView == 'LISTVIEW'}">
							<accommodationsearch:propertyListView propertiesListParams="${properties}" />
						</c:when>
						<c:otherwise>
							<accommodationsearch:propertyGridView propertiesListParams="${properties}" />
						</c:otherwise>
					</c:choose>
				</json:property>
			</json:object>
		</c:otherwise>
	</c:choose>
</c:if>
