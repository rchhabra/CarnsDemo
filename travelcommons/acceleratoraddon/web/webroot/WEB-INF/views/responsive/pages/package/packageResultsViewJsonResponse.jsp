<%@ page trimDirectiveWhitespaces="true" contentType="application/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="packagelisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagelisting"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="resultsView" value="${fn:toUpperCase(resultsViewType)}" />
<c:choose>
	<c:when test="${not empty packageSearchResponseProperties}">
		<c:choose>
			<c:when test="${resultsView == 'MAPVIEW'}">
				<json:array items="${packageSearchResponseProperties}" var="property">
					<json:object escapeXml="false">
						<json:property name="htmlContent">
							<packagelisting:markPackageList packageData="${property}" urlParameters="${urlParameters}" stayDateRange="${packageSearchResponse.criterion.stayDateRange}" />
						</json:property>
						<json:property name="latitude" value="${property.position.latitude}" />
						<json:property name="longitude" value="${property.position.longitude}" />
						<json:property name="name" value="${property.accommodationOfferingName}" />
						<json:property name="totalNumberOfResults" value="${totalNumberOfResults}" />
						<json:property name="totalNumberOfResultsText">
							<c:choose>
								<c:when test="${totalNumberOfResults eq 1}">
									<spring:theme code="text.package.listing.found.package.single.number" arguments="${totalNumberOfResults}, ${packageSearchResponse.criterion.address.line2}" />
								</c:when>
								<c:when test="${totalNumberOfResults gt 1}">
									<spring:theme code="text.package.listing.found.package.number" arguments="${totalNumberOfResults}, ${packageSearchResponse.criterion.address.line2}" />
								</c:when>
								<c:otherwise>
									<spring:theme code="text.package.listing.empty.list" />
								</c:otherwise>
							</c:choose>
						</json:property>
					</json:object>
				</json:array>
			</c:when>
			<c:otherwise>
				<json:object escapeXml="false">
					<json:property name="htmlContent">
						<c:choose>
							<c:when test="${resultsView == 'LISTVIEW'}">
								<packagelisting:packageListView propertiesListParams="${packageSearchResponseProperties}" />
							</c:when>
							<c:otherwise>
								<packagelisting:packageGridView propertiesListParams="${packageSearchResponseProperties}" />
							</c:otherwise>
						</c:choose>
					</json:property>
					<json:property name="hasMoreResults" value="${hasMoreResults}" />
					<json:property name="totalNumberOfResults" value="${totalNumberOfResults}" />
					<json:property name="totalshownResults">
						<h3 class="h4">
							<spring:theme code="text.package.listing.shown.package.results" arguments="${totalshownResults}, ${totalNumberOfResults}" />
						</h3>
					</json:property>
				</json:object>
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<json:object escapeXml="false">
			<json:property name="totalNumberOfResultsText">
				<spring:theme code="text.package.listing.empty.list" />
			</json:property>
		</json:object>
	</c:otherwise>
</c:choose>
