<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="roomStayIndex" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="guestdetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/guestdetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="leadDetailsForm" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel-heading">
    <h3 class="panel-title">
        <spring:theme code="text.guest.details.extra.services.title" text="Add to Booking" />
    </h3>
</div>

<div class="panel-body collapse in y_booking-extras" id="booking-extras_${fn:escapeXml(roomStayIndex)}">
	<ul class="list-group no-padding">
		<c:set var="roomStayReferenceNumber" value="${availableServices[roomStayIndex].roomStayRefNumber}" />
		<c:forEach var="service" items="${availableServices[roomStayIndex].services}" varStatus="serviceIndex">
			<li class="list-group-item">
				<div class="row input-row">
					<div class="col-xs-6 extra-item">
						<span>${fn:escapeXml(service.serviceDetails.product.name)}
							<span class="extra-cost">${fn:escapeXml(service.price.basePrice.formattedValue)}</span>
						</span>
					</div>
					<div class="col-xs-6 extra-item-selection">
						<c:choose>
							<c:when test="${service.serviceDetails.restriction.minQuantity == service.serviceDetails.restriction.maxQuantity}">
								<guestdetails:singleOfferExtra roomStayIndex="${roomStayIndex}" roomStayReferenceNumber="${roomStayReferenceNumber}" service="${service}" index="${serviceIndex.index}" />
							</c:when>
							<c:otherwise>
								<guestdetails:multipleOfferExtra roomStayIndex="${roomStayIndex}" roomStayReferenceNumber="${roomStayReferenceNumber}" service="${service}" index="${serviceIndex.index}" />
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</li>
		</c:forEach>

		<guestdetails:roomPreference leadDetailsForm="${leadDetailsForm}" roomStayIndex="${roomStayIndex}"/>
		
	</ul>
</div>
