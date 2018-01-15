<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="accommodationReservation" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationreservation"%>
<%@ attribute name="accommodationAvailabilityResponse" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationAvailabilityResponseData"%>
<%@ attribute name="orderCode" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/manage-booking/change-dates" var="amendAccommodationBookingUrl" />
<div class="modal-dialog" role="document">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">x</span>
			</button>
			<h4 class="modal-title">
				<spring:theme code="accommodation.booking.details.change.dates" text="Change Dates" />
			</h4>
		</div>
		<div class="modal-body">
			<c:choose>
				<c:when test="${not empty errors}">
					<spring:theme code="accommodation.booking.details.change.dates.error.${errors}" text="Error Occured during amendment process of booking. Please check the logs." />
				</c:when>
				<c:when test="${!isAccommodationAvailable}">
					<spring:theme code="accommodation.booking.details.change.dates.accommodation.availability.false" text="No Accommodations Available" />
				</c:when>
				<c:otherwise>
					<h2 class="h4">
						<spring:theme code="accommodation.booking.details.change.dates.title" text="New Accomodation Details" />
					</h2>
					<div class="panel panel-primary panel-list booking room-details clearfix">
						<div class="panel panel-default my-account-secondary-panel clearfix">
							<c:forEach items="${accommodationAvailabilityResponse.roomStays}" var="roomStay" varStatus="idx">
								<div class="room panel-default">
									<c:if test="${idx.index eq 0}">
										<c:set var="checkInDate">
											<fmt:formatDate value="${roomStay.checkInDate}" pattern="dd/MM/yyyy" />
										</c:set>
										<c:set var="checkOutDate">
											<fmt:formatDate value="${roomStay.checkOutDate}" pattern="dd/MM/yyyy" />
										</c:set>
										<div class="panel-heading">
											<h3 class="panel-title">
												<spring:theme code="text.cms.accommodationbreakdown.stayat" text="Stay at" />
												&puncsp;${fn:escapeXml(accommodationAvailabilityResponse.accommodationReference.accommodationOfferingName)}
											</h3>
										</div>
									</c:if>
									<div class="panel-body">
										<div class="col-xs-12">
											<accommodationReservation:roomTypeBreakdown roomStay="${roomStay}" index="${idx.index}" isChangeDates="true" />
											<accommodationReservation:roomstaybreakdown roomStay="${roomStay}" index="${idx.index}" isAmendBookingDetails="false" />
										</div>
									</div>
								</div>
							</c:forEach>
							<div class="panel-default total">
								<div class="panel-body">
									<div class="col-xs-12">
										<dl class="text-right">
											<dt>
												<spring:theme code="accommodation.booking.details.change.dates.paid.price" text="Amount Already Paid" />
											</dt>
											<dd>${fn:escapeXml(paidAmount)}</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="panel-default total">
								<div class="panel-body">
									<div class="col-xs-12">
										<dl class="text-right">
											<dt>
												<spring:theme code="accommodation.booking.details.change.dates.payable.${isPayable}" text="Total Refundable / Payable" />
											</dt>
											<dd>${fn:escapeXml(payAmount)}</dd>
										</dl>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="fieldset form-group col-xs-12 col-sm-offset-6 col-sm-6">
									<form:form commandName="accommodationBookingChangeDateForm" class="y_accommodationAvailabilityResponse" action="${amendAccommodationBookingUrl}" method="post">
										<form:input type="hidden" path="checkInDateTime" value="${fn:escapeXml(checkInDate)}" />
										<form:input type="hidden" path="checkOutDateTime" value="${fn:escapeXml(checkOutDate)}" />
										<form:input type="hidden" path="bookingReference" value="${fn:escapeXml(orderCode)}" />
										<form:button type="submit" class="btn btn-primary btn-block">
											<spring:theme code="accommodation.booking.details.change.dates.continue" text="Continue" />
										</form:button>
									</form:form>
								</div>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
