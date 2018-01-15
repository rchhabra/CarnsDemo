<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="packagelisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting"%>
<%@ taglib prefix="refinement" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting/refinement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<json:object escapeXml="false">
	<json:property name="htmlContent">
		<c:if test="${not empty packageSearchResponseProperties}">
			<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
			<c:choose>
				<c:when test="${resultsView == 'LISTVIEW'}">
					<packagelisting:packageListView propertiesListParams="${packageSearchResponseProperties}" />
				</c:when>
				<c:otherwise>
					<packagelisting:packageGridView propertiesListParams="${packageSearchResponseProperties}" />
				</c:otherwise>
			</c:choose>
		</c:if>
	</json:property>
	<json:property name="hasMoreResults" value="${hasMoreResults}" />
	<json:property name="totalNumberOfResults" value="${totalNumberOfResults}" />
	<json:property name="totalNumberOfResultsText">
		<c:choose>
			<c:when test="${totalNumberOfResults gt 0}">
				<spring:theme code="${totalNumberOfResults eq 1 ? 'text.package.listing.found.package.single.number' : 'text.package.listing.found.package.number'}" arguments="${totalNumberOfResults}, ${packageSearchResponse.criterion.address.line2}" />
				<c:set var="stayDateRange" value="${packageSearchResponse.criterion.stayDateRange}" />
				<fmt:formatDate value="${stayDateRange.startTime}" var="formattedCheckInDate" type="both" pattern="dd/MM/yyyy" />
				<fmt:formatDate value="${stayDateRange.endTime}" var="formattedCheckOutDate" type="both" pattern="dd/MM/yyyy" />
				<span>&nbsp;${fn:escapeXml(formattedCheckInDate)} - ${fn:escapeXml(formattedCheckOutDate)}</span>
			</c:when>
			<c:otherwise>
				<spring:theme code="text.package.listing.empty.list" />
			</c:otherwise>
		</c:choose>
	</json:property>
	<json:property name="totalshownResults">
		<h3 class="h4">
			<spring:theme code="text.package.listing.shown.package.results" arguments="${totalshownResults}, ${totalNumberOfResults}" />
		</h3>
	</json:property>
</json:object>
