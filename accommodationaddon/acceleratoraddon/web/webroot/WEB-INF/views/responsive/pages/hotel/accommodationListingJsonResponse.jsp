<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="accommodationsearch" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationsearch"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<json:object escapeXml="false">
	<json:property name="htmlContent">
		<c:if test="${not empty accommodationSearchProperties}">
			<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
			<c:choose>
				<c:when test="${resultsView == 'LISTVIEW'}">
					<accommodationsearch:propertyListView propertiesListParams="${accommodationSearchProperties}" />
				</c:when>
				<c:otherwise>
					<accommodationsearch:propertyGridView propertiesListParams="${accommodationSearchProperties}" />
				</c:otherwise>
			</c:choose>
		</c:if>
	</json:property>
	<json:property name="hasMoreResults" value="${hasMoreResults}" />
	<json:property name="totalshownResults">
		<h3 class="h4">
			<spring:theme code="text.accommodation.listing.shown.hotels.results" arguments="${totalshownResults}, ${totalNumberOfResults}" />
		</h3>
	</json:property>
</json:object>
