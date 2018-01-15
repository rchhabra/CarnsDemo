<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="tripFinder" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/tripFinder"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<json:object escapeXml="false">
	<json:property name="htmlContent">
		<tripFinder:destinationLocations/>
	</json:property>
	<json:property name="originLocationName" value="${originLocationName}"/>
	<json:property name="originLocationCode" value="${originLocationCode}"/>
	<json:property name="originLocationSuggestionType" value="${originLocationSuggestionType}"/>
</json:object>
