<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="amendBookingResult" required="true" type="java.lang.String"%>
<%@ attribute name="amendBookingErrorResult" required="true" type="java.lang.String"%>
<%@ attribute name="amendBookingRefundAmount" required="true" type="java.lang.String"%>
<%@ attribute name="isAccommodationAvailable" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:choose>
	<c:when test="${not empty amendBookingErrorResult || not empty isAccommodationAvailable}">
		<div class="alert alert-danger alert-dismissible" role="alert">
			<button type="button" class="close" data-dismiss="alert" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			<p class="y_amendBookingResultContent">
				<c:choose>
					<c:when test="${not empty isAccommodationAvailable && !isAccommodationAvailable}">
						<spring:theme code="accommodation.booking.details.change.dates.accommodation.availability.false" text="No Accommodations Available" />
					</c:when>
					<c:otherwise>
						<spring:theme code="accommodation.booking.details.change.dates.error.${amendBookingErrorResult}" text="Error Occured during amendment process of booking. Please check the logs." />
					</c:otherwise>
				</c:choose>
			</p>
		</div>
	</c:when>
	<c:otherwise>
		<c:if test="${not empty amendBookingResult}">
			<div class="alert alert-success alert-dismissible" role="alert">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<p class="y_amendBookingResultContent">
					<c:choose>
						<c:when test="${amendBookingResult=='REFUND'}">
							<spring:theme code="accommodation.booking.details.change.dates.payment.type.${amendBookingResult}" arguments="${amendBookingRefundAmount}" argumentSeparator="#" />
						</c:when>
						<c:otherwise>
							<spring:theme code="accommodation.booking.details.change.dates.payment.type.${amendBookingResult}" />
						</c:otherwise>
					</c:choose>
				</p>
			</div>
		</c:if>
	</c:otherwise>
</c:choose>
