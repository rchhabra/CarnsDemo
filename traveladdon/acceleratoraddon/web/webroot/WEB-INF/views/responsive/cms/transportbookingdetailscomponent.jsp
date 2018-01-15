<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/bookingdetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${empty pageNotAvailable}">
		<c:if test="${reservationData.bookingStatusCode != 'ACTIVE_DISRUPTED_PENDING'}">
			<div class="row">
				<div class="panel button-wrap">
					<div class="col-xs-12 col-md-4">
						<dl class="booking status">
							<dt>
								<spring:theme code="text.page.managemybooking.bookingstatus" text="Booking Status:" />
							</dt>
							<dd>
								<bookingDetails:status code="${reservationData.bookingStatusCode}" name="${reservationData.bookingStatusName}" />
							</dd>
						</dl>
					</div>
					<div class="col-xs-12 col-sm-5 col-md-4 pull-right">
						<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="AMEND_ANCILLARY" />
					</div>
					<div class="col-xs-12 col-sm-6 col-lg-4">
						<input type="hidden" value="${fn:escapeXml(reservationData.code)}" name="bookingReference" id="bookingReference">
						<booking:bookingaction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_BOOKING" />
					</div>
				</div>
			</div>
		</c:if>

		<bookingDetails:transportBookingDetails reservationData="${reservationData}" />
		<div class="y_cancelBookingConfirm"></div>
	</c:when>
	<c:otherwise>
		<div class="alert alert-danger " role="alert">
			<p>
				<spring:theme code="${fn:escapeXml(pageNotAvailable)}" />
			</p>
		</div>
	</c:otherwise>
</c:choose>

