<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<div id="location-search">
	<form:form id="y_destinationLocationSearchForm">
		<label for="y_tripFinderOriginLocation" class="sr-only">
			<spring:theme code="sr.geolocation.find" />
		</label>
		<input type="hidden" id="y_tripFinderActivity" name="activity" value="${fn:escapeXml(activity)}" />
		<c:set var="findLocationPlaceholder">
			<spring:theme code="geolocation.find.placeholder" />
		</c:set>
		<input id="y_tripFinderOriginLocation" class="y_tripFinderOriginLocation input-grid trip-finder-location form-control" name="originName" value="" autocomplete="off" placeholder="${fn:escapeXml(findLocationPlaceholder)}" />
		<span class="glyphicon glyphicon-map-marker"></span>
		<input type="hidden" class="y_tripFinderOriginLocationCode" name="origin" />
		<input type="hidden" class="y_tripFinderOriginLocationSuggestionType" name="originType" />
		<input type="hidden" name="city" class="y_tripFinderCityCode" />
	</form:form>
</div>
<div id="y_tripFinderOriginLocationSuggestions" class="autocomplete-suggestions-wrapper hidden"></div>
