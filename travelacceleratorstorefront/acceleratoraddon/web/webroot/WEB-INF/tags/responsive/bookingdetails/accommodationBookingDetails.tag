<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="accommodationBooking" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationbooking"%>
<%@ taglib prefix="accommodationReservation" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationreservation"%>
<%@ attribute name="accommodationReservationData" required="true" type="de.hybris.platform.commercefacades.accommodation.AccommodationReservationData"%>
<%@ attribute name="customerReviews" required="false" type="java.util.List"%>
<%@ attribute name="isTravelSite" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="booking room-details ${accommodationReservationData.bookingStatusCode == 'CANCELLED' ? 'booking-cancel' : ''}">
	<div class="panel panel-default my-account-secondary-panel clearfix">
		<c:forEach items="${accommodationReservationData.roomStays}" var="roomStay" varStatus="idx">
			<div class="room panel-default y_roomBooking">
				<c:if test="${idx.index eq 0}">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.cms.accommodationbreakdown.stayat" text="Stay at" />
							&nbsp;${fn:escapeXml(accommodationReservationData.accommodationReference.accommodationOfferingName)}
						</h3>
					</div>
				</c:if>
				<div class="panel-body">
					<div class="col-xs-12">
						<accommodationReservation:roomTypeBreakdown roomStay="${roomStay}" index="${idx.index}" />
						<accommodationReservation:roomstaybreakdown roomStay="${roomStay}" index="${idx.index}" />
                        <accommodationReservation:selectedRoomPreferences roomStay="${roomStay}" />
                        <div class="row">
                         <div class="form-group col-xs-12 col-sm-4 col-xs-offset-0 col-sm-offset-8">
                          <accommodationBooking:accommodationBookingAction roomStay="${roomStay}" bookingActionResponseData="${bookingActionResponse}" actionType="AMEND_EXTRAS" />
                          </div>
                         </div> 
						<section>
							<div class="row">
								<div class="panel-heading">
									<h3 class="panel-title title-collapse">
										<spring:theme code="accommodation.booking.details.page.request" />
									</h3>
									<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#rm${fn:escapeXml(roomStay.roomStayRefNumber)}_request" aria-expanded="true" aria-controls="rm${fn:escapeXml(roomStay.roomStayRefNumber)}_request">
										<spring:theme code="accommodation.request.collapse.expand" />
									</button>
								</div>
								<div id="rm${fn:escapeXml(roomStay.roomStayRefNumber)}_request" class="request-amendments panel-body collapse in">
									<c:if test="${not empty roomStay.specialRequestDetail.remarks}">
										<c:forEach var="savedRequest" items="${roomStay.specialRequestDetail.remarks}">
											<div class="alert request-list alert-dismissible" role="alert">
												<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_REQUEST" requestCode="${savedRequest.code}" />
												<strong>${fn:escapeXml(savedRequest.name)}</strong>
											</div>
										</c:forEach>
									</c:if>
									<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="ADD_REQUEST" roomStay="${roomStay}" />
								</div>
							</div>
						</section>
						<c:forEach var="review" items="${customerReviews}">
							<c:if test="${review.roomStayRefNumber eq roomStay.roomStayRefNumber}">
								<accommodationBooking:accommodationShowReview reviewData="${review}" />
							</c:if>
						</c:forEach>
						<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="WRITE_REVIEW" roomStay="${roomStay}" accommodationOffering="${accommodationReservationData.accommodationReference.accommodationOfferingCode}" />
					</div>
				</div>
			</div>
		</c:forEach>
		<c:if test="${!isPackageInOrder}">
			<accommodationBooking:accommodationBookingTotal totalRateFormattedValue="${accommodationReservationData.totalRate.actualRate.formattedValue}" totalRateValue="${accommodationReservationData.totalRate.actualRate.value}" partialPaymentPaid="${partialPaymentPaid}" />
		</c:if>
		<div id="date-update" class="clearfix">
			<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CHANGE_DATES" />
			<div class="row option-buttons">
				<div class="fieldset">
					<div class="row">
						<div class="col-xs-12 col-sm-4">
							<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="ADD_ROOM" />
						</div>
						<div class="col-xs-12 col-sm-4">
							<c:if test="${globalReservationData.reservationData != null}">
								<c:choose>
									<c:when test="${isTravelSite}">
										<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_ACCOMMODATION_BOOKING" />
									</c:when>
									<c:otherwise>
										<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="CANCEL_BOOKING" />
									</c:otherwise>
								</c:choose>
							</c:if>
						</div>
						<div class="col-xs-12 col-sm-4">
							<accommodationBooking:accommodationBookingAction bookingActionResponseData="${bookingActionResponse}" actionType="PAY_NOW" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
