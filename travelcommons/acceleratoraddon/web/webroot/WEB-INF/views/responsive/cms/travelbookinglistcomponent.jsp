<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="myaccount" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/myaccount"%>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/account"%>
<spring:url value="/manage-booking/booking-details" var="viewBookingUrl" />
<spring:url value="/manage-booking/unlink-booking" var="unlinkBookingUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />
<style>
#my-bookings ul li:nth-child(n+${fn:escapeXml(pageSize+1)}) {
    display:none;
}
</style>

<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.page.mybookings" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#my-bookings" aria-expanded="true" aria-controls="saved-payment">
		<spring:theme code="account.bookings.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="my-bookings">
	<c:choose>
		<c:when test="${not empty myBookings}">
			<ul class="col-xs-12 accommodation-items y_myBookings my-bookings">
				<c:forEach items="${myBookings}" var="myBooking" varStatus="bIdx">
					<li class="col-xs-12 booking-list">
						<c:if test="${not empty myBookingImages}">
							<div class="col-xs-5 col-sm-3 accommodation-image">
								<div class="row">
									<a href="#">
										<c:choose>
											<c:when test="${not empty myBooking.reservationData}">
												<img src="${myBookingImages[myBooking.reservationData.code].url}" class="img-responsive">
												<c:set var="bookingCode" value="${myBooking.reservationData.code}" />
											</c:when>
											<c:otherwise>
												<img src="${myBookingImages[myBooking.accommodationReservationData.code].url}" class="img-responsive">
												<c:set var="bookingCode" value="${myBooking.accommodationReservationData.code}" />
											</c:otherwise>
										</c:choose>
									</a>
								</div>
							</div>
						</c:if>
						<div class="accommodation-details col-xs-offset-5 col-xs-7 col-sm-offset-3 col-sm-9">
							<div class="row">
								<div class="col-xs-12 col-sm-12">
									<div class="clearfix">
										<div class="col-xs-12">
											<div class="row">
												<c:set var="isDisplayBookingReferenceCode" value="true" />
												<c:if test="${not empty myBooking.reservationData && !(myBooking.reservationData.bookingStatusCode == 'CANCELLED' 
												&& not empty myBooking.accommodationReservationData && myBooking.accommodationReservationData.bookingStatusCode != 'CANCELLED') }">
													<myaccount:travelBooking myBooking="${myBooking.reservationData}" />
													<c:set var="isDisplayBookingReferenceCode" value="false" />
												</c:if>
												<c:if test="${not empty myBooking.accommodationReservationData && !(myBooking.accommodationReservationData.bookingStatusCode == 'CANCELLED' 
												&& not empty myBooking.reservationData && myBooking.reservationData.bookingStatusCode != 'CANCELLED') }">
													<myaccount:accommodationBooking myBooking="${myBooking.accommodationReservationData}" isDisplayBookingReferenceCode="${isDisplayBookingReferenceCode}" />
												</c:if>
												<p>
													<c:if test="${removeLinks[bookingCode] eq true}">
														<a href="#" class="y_removeBooking disabled" data-href="${unlinkBookingUrl}/${bookingCode}" data-toggle="modal" data-target="#confirm-delete">
															<spring:theme code="button.page.mybookings.removeBooking" />
														</a>
													</c:if>
												</p>
											</div>
											<div class="row">
												<div class="col-xs-12 col-sm-6 pull-right">
													<a href="${viewBookingUrl}/${bookingCode}" class="btn btn-primary btn-block">
														<spring:theme code="button.page.mybookings.viewBookingDetails" />
													</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</li>
				</c:forEach>
			</ul>
			<c:if test="${fn:length(myBookings) > pageSize}">
				<div class="row">
					<div class="test col-xs-12">
						<div class="col-sm-5 col-sm-offset-7">
							<a href="#" class="col-xs-12 btn btn-primary y_myAccountMyBookingsShowMore" data-pagesize="${fn:escapeXml(pageSize)}">
								<spring:theme code="button.page.mybookings.showmore" text="Show More" />
							</a>
						</div>
					</div>
				</div>
			</c:if>
		</c:when>
		<c:otherwise>
			<spring:theme code="text.page.mybookings.not.found" />
		</c:otherwise>
	</c:choose>
</div>
<account:unlinkbooking />
