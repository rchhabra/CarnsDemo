<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/template"%>
<%@ taglib prefix="guestdetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/guestdetails"%>
<%@ taglib prefix="reservation" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/reservation"%>
<%@ taglib prefix="progress" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/progress"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:url var="submitUrl" value="/checkout/guest-details" />
<template:page pageTitle="${pageTitle}">
	<progress:bookingProgressBar stage="personalDetails" amend="${amend}" bookingJourney="${bookingJourney}" />
	<div class="container">
		<h2 class="h2">
			<spring:theme code="text.guest.details.title" text="Guest Details Page" />
		</h2>
		<div class="row">
			<%-- Begin Main content --%>
			<div class="col-xs-12 col-sm-9 y_nonItineraryContentArea">

				<%-- AddExtraToCartForm --%>
				<c:url value="/cart/accommodation/add-extra" var="addExtraToCartUrl" />
				<form:form id="y_addExtraToCartForm" name="addExtraToCartForm" action="${addExtraToCartUrl}" method="post">
					<input id="y_productCode" name="productCode" type="hidden" />
					<input id="y_roomStayReferenceNumber" name="roomStayReferenceNumber" type="hidden" />
					<input id="y_quantity" name="quantity" type="hidden" />
				</form:form>
				<%-- /AddExtraToCartForm --%>

				<form:form id="y_guestDetailsForm" modelAttribute="leadGuestDetailsForms" action="${submitUrl}" method="POST">
					<c:set var="numberOfRooms" value="${fn:length(reservationData.roomStays)}" />
					<input type="hidden" id="y_numberOfRooms" value="${fn:escapeXml(numberOfRooms)}" />

					<%-- Room Details --%>
					<div class="panel panel-primary panel-list room-details">
						<div class="panel-heading">
							<h3 class="title">
								<spring:theme code="text.guest.details.room.details.title"
									arguments="${reservationData.accommodationReference.accommodationOfferingName}" />
							</h3>
						</div>
						<div class="panel panel-default my-account-secondary-panel clearfix">
							<c:forEach var="roomStay" items="${reservationData.roomStays}" varStatus="idx">
								<div class="room clearfix y_roomStayDetails">
									<div class="panel-body">
										<div class="col-xs-12">
											<guestdetails:roomtype roomStay="${roomStay}" />
										</div>
										<div class="col-xs-12">
											<guestdetails:leadDetails leadDetailsForm="leadForms[${idx.index}]" formValues="${leadGuestDetailsForms.leadForms[idx.index] }" roomStayRefNumber="${roomStay.roomStayRefNumber }" roomNum="${idx.index+1}" index="${idx.index}" checkInDate="${roomStay.checkInDate}"
												passengerTypeMaxQuantityMap="${passengerTypeMaxQuantityMapPerRoom[roomStay.roomStayRefNumber]}" />
										</div>
										<div class="col-xs-12">
											<div class="row">
												<section class="panel panel-default">
													<guestdetails:extraservices roomStayIndex="${idx.index}" leadDetailsForm="leadForms[${idx.index}]" />
												</section>
											</div>
										</div>
										<%--  Remove room button --%>
										<c:if test="${roomStay.roomStayRefNumber > 0 && !isAmendExtras}">
											<div class="col-xs-12 col-sm-4 col-sm-offset-8">
												<div class="row">
													<div class="form-group col-xs-12">
														<c:url var="removeRoomUrl" value="guest-details/remove-room/${roomStay.roomStayRefNumber}" />
														<a href="#" class="btn btn-primary btn-block y_guestDetailsRemoveRoom" data-href="${removeRoomUrl}">
															<spring:theme code="text.guest.details.room.remove.button" text="Remove Room" />
														</a>
													</div>
												</div>
											</div>
										</c:if>
										<%--  /Remove room button --%>
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
					<%-- /Room Details --%>

					<%-- Continue button --%>
					<div class="row bottom-row">
						<div class="col-xs-12 col-sm-4 pull-right">
							<button type="submit" class="btn btn-secondary col-xs-12 ">
								<spring:theme code="text.guest.details.btn.continue" />
							</button>
						</div>
					</div>
					<%-- /Continue button --%>
				</form:form>
			</div>
			<%-- End Main content --%>

			<%-- Itinerary --%>
			<div class="col-xs-12 col-sm-3">
				<aside id="sidebar" class="y_reservationSideBar reservation">
					<div class="main-wrap">
						<cms:pageSlot position="Reservation" var="feature" element="div">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
					<div class="promotions hidden-xs">
						<cms:pageSlot position="SideContent" var="feature" element="section">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
				</aside>
			</div>
			<%-- /Itinerary --%>

		</div>
	</div>
	<reservation:fullReservationOverlay />
	<guestdetails:addExtraToCartErrorModal />	
	<guestdetails:removeRoomResultMessageModal />
</template:page>
