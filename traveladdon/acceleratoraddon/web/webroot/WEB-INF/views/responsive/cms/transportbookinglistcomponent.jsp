<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="bookingDetails" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/bookingdetails"%>
<c:set var="datePattern" value="HH:mm dd MMM yyyy" />
<spring:url value="/manage-booking/booking-details" var="viewBookingUrl" />
<spring:htmlEscape defaultHtmlEscape="true" />
<style>
#my-bookings ul li:nth-child(n+${fn:escapeXml(pageSize+1)}) {
    display:none;
}
</style>

<div class="panel-heading">
    <h3 class="title title-collapse"><spring:theme code="text.page.mybookings" /></h3>
    <button class="show-hide-button" type="button" data-toggle="collapse" data-target="#my-bookings" aria-expanded="true" aria-controls="saved-payment"><spring:theme code="account.bookings.button.collapseExpand" text="Collapse / Expand" /></button>
</div>
<div class="panel-body collapse in" id="my-bookings">
	<c:choose>
		<c:when test="${myBookings == null || empty myBookings}">
			<div class="fieldset">
				<p>
					<spring:theme code="text.page.mybookings.not.found" />
				</p>
			</div>
		</c:when>
		<c:otherwise>
			<ul id="transport-items" class="col-xs-12 accommodation-items y_myBookings my-bookings">
				<c:forEach var="myBooking" items="${myBookings}" varStatus="idx">
					<li class="col-xs-12 booking-list">
						<div class="panel panel-spaced">
							<div class="panel-heading">
								<h3 class="panel-title">
									<spring:theme code="text.page.mybookings.bookingreference" />
									:${fn:escapeXml(myBooking.code)}
								</h3>
							</div>
							<div class="panel-body">
								<div class="row">
									<div class="col-xs-6 col-md-5 col-md-offset-4 text-right">
										<dl class="status">
											<dt>
												<spring:theme code="text.page.mybookings.bookingStatus" />
												:
											</dt>
											<dd>
												<bookingDetails:status code="${myBooking.bookingStatusCode}" name="${myBooking.bookingStatusName}" />
											</dd>
										</dl>
									</div>
									<div class="col-xs-6 col-md-3">
										<div class="form-group col-xs-12">
											<form id="mybookingForm" method="get" action="${fn:escapeXml(viewBookingUrl)}/${fn:escapeXml(myBooking.code)}">
												<button type="submit" class="btn btn-primary btn-block" id="view">
													<spring:theme code="button.page.mybookings.detail.view" />
												</button>
											</form>
										</div>
									</div>
								</div>
								<c:forEach var="reservationItem" items="${myBooking.reservationItems}">
									<h3 class="divided title">
										<span class="spacer"></span>${fn:escapeXml(reservationItem.reservationItinerary.route.origin.location.name)}
										(${fn:escapeXml(reservationItem.reservationItinerary.route.origin.code)}) - ${fn:escapeXml(reservationItem.reservationItinerary.route.destination.location.name)} (${fn:escapeXml(reservationItem.reservationItinerary.route.destination.code)})
									</h3>
									<div class="row">
										<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="transportOffering">
											<div class="col-xs-12">
												<div class="col-xs-12 col-sm-6 col-md-3">
													<dl>
														<dt>
															<spring:theme code="text.page.mybookings.flightnumber" />
															:
														</dt>
														<dd>${fn:escapeXml(transportOffering.travelProvider.code)}${fn:escapeXml(transportOffering.number)}</dd>
													</dl>
												</div>
												<div class="col-xs-12 col-sm-6 col-md-3">
													<dl>
														<dt>
															<spring:theme code="text.page.mybookings.departs" />
															:
														</dt>
														<dd>
															<fmt:formatDate value="${transportOffering.departureTime}" pattern="${datePattern}" />
														</dd>
													</dl>
												</div>
												<div class="col-xs-12 col-sm-6 col-md-3">
													<dl>
														<dt>
															<spring:theme code="text.page.mybookings.arrives" />
															:
														</dt>
														<dd>
															<fmt:formatDate value="${transportOffering.arrivalTime}" pattern="${datePattern}" />
														</dd>
													</dl>
												</div>
												<div class="col-xs-12 col-sm-6 col-md-3">
													<dl>
														<dt>
															<spring:theme code="text.page.mybookings.terminal" />
															:
														</dt>
														<dd>${fn:escapeXml(transportOffering.originTerminal.code)}</dd>
													</dl>
												</div>
											</div>
										</c:forEach>
									</div>
								</c:forEach>
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
		</c:otherwise>
	</c:choose>
</div>
