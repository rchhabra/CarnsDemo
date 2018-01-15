<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/booking" %>
<%@ taglib prefix="accommodationBooking" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationbooking" %>
<%@ taglib prefix="accommodationReservation" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationreservation" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<ycommerce:testId code="orderDetail_itemList_section">
	<div class="panel panel-primary panel-list account-list">
		<div class="panel-heading">
			<h3 class="title">
				<spring:theme code="text.account.order.orderEntry.status.pending"/>
			</h3>
		</div>
		<div class="panel-body">
			<c:if test="${not empty travelReservationData.reservationData}">
				<div class="panel panel-spaced">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.cms.transportsummary.title" text="Flight Summary"/>
						</h3>
					</div>
					<div class="panel-body">
						<div class="product-summary">
							<c:forEach var="reservationItem" items="${travelReservationData.reservationData.reservationItems}">
								<booking:itineraryitem reservationItem="${reservationItem}"/>
							</c:forEach>
						</div>
					</div>
				</div>
			</c:if>
			<c:if test="${not empty travelReservationData.accommodationReservationData}">
				<div class="panel panel-spaced">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.cms.accommodationsummary.hotelsummary" text="Hotel Summary"/>
						</h3>
					</div>
					<div class="panel-body">
						<div class="product-summary">
							<div class="accommodation-details col-xs-7 col-sm-9">
								<div class="row">
									<div class="col-xs-12 col-sm-12">
										<div class="clearfix">
											<div class="col-xs-12">
												<div class="row">
													<accommodationBooking:accommodationBookingDetails myBooking="${travelReservationData.accommodationReservationData}" accommodationRoomMapping="${accommodationRoomMapping}" b2bOrderDetailsForAccommodation="true"/>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
	<div class="panel panel-primary panel-list account-list">
		<div class="panel-heading">
			<h3 class="title">
				<spring:theme code="text.account.orderApprovalDetails.review.label"/>
			</h3>
		</div>
		<div class="panel panel-list my-account-secondary-panel">

			<c:if test="${not empty travelReservationData.reservationData}">
				<div class="panel-price-summary">
					<booking:priceSummary reservationData="${travelReservationData.reservationData}"
					                      b2bTravelOrderDetails="true"/>
				</div>
			</c:if>

			<c:if test="${not empty travelReservationData.accommodationReservationData}">
				<div class="panel-price-summary">
					<div class="panel-heading">
						<h3 class="panel-title">
							<spring:theme code="text.hotel.price.summary" text="Hotel Price Summary"/>
						</h3>
					</div>
					<c:forEach items="${travelReservationData.accommodationReservationData.roomStays}" var="roomStay"
					           varStatus="idx">
						<accommodationReservation:roomstaybreakdown roomStay="${roomStay}" index="${idx.index}"
						                                            b2bOrderDetailsForAccommodation="true"/>
					</c:forEach>
					<c:if test="${not empty travelReservationData.reservationData}">
						<div class="panel-default total">
							<div class="panel-body">
								<div class="col-xs-12">
									<dl class="text-right">
										<dt>
											<spring:theme code="text.cms.accommodationbreakdown.total"/>
										</dt>
										<dd>${fn:escapeXml(travelReservationData.accommodationReservationData.totalRate.actualRate.formattedValue)}</dd>
									</dl>
								</div>
							</div>
						</div>
					</c:if>
				</div>
				<div class="panel-price-summary">
					<accommodationBooking:accommodationBookingTotal totalRateFormattedValue="${bookingTotalFormattedValue}"/>
				</div>
			</c:if>
		</div>
	</div>
</ycommerce:testId>
