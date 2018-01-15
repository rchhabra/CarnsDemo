<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${empty sessionBookingJourney }">
	<c:set var="sessionBookingJourney" value="BOOKING_TRANSPORT_ONLY"></c:set>
</c:if>
<input id="y_maxAllowedCheckInCheckOutDateDifference" type="hidden" value="${maxAllowedCheckInCheckOutDateDifference}" />
<%-- Nav tabs --%>
<ul class="nav nav-tabs modify-travel" role="tablist">
	<c:choose>
		<c:when test="${!showComponent}">
			<li role="presentation" class="">
				<span>
					<spring:theme code="text.cms.farefinder.modify.title" text="Modify Search" />
				</span>
			</li>
			<li role="presentation">
		</c:when>
		<c:otherwise>
			<li role="presentation" class="${sessionBookingJourney == 'BOOKING_TRANSPORT_ONLY' ? 'active' : ''}">
		</c:otherwise>
	</c:choose>
	<a href="#flight" class="componentSelector" aria-controls="flight" role="tab" data-toggle="tab" component-uid="${fn:escapeXml(fareFinderComponentUid)}">
		<spring:theme code="text.cms.travelfinder.flight.tab" text="Flight" />
	</a>
	</li>
	<li role="presentation">
		<a href="#hotel" class="componentSelector" aria-controls="hotel" role="tab" data-toggle="tab" component-uid="${fn:escapeXml(accommodationFinderComponentUid)}">
			<spring:theme code="text.cms.travelfinder.hotel.tab" text="Hotel" />
		</a>
	</li>
	<li role="presentation">
		<a href="#flight-hotel" class="componentSelector" aria-controls="flight-hotel" role="tab" data-toggle="tab" component-uid="${fn:escapeXml(travelFinderComponentUid)}">
			<spring:theme code="text.cms.travelfinder.flightandhotel.tab" text="Flight-Hotel" />
		</a>
	</li>
	<li role="presentation">
		<a href="#package" class="componentSelector" aria-controls="package" role="tab" data-toggle="tab" component-uid="${fn:escapeXml(packageFinderComponentUid)}">
			<spring:theme code="text.cms.travelfinder.package.tab" text="Packages" />
		</a>
	</li>
</ul>
<input type="hidden" value="${fn:escapeXml(component.uid)}" id="y_tabbedFinderComponentId" />
<div id="y_finderContainer" class="tab-content"></div>
