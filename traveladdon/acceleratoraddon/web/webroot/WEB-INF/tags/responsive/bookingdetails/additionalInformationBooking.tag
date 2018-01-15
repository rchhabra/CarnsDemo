<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ attribute name="currentIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerData" required="true" type="de.hybris.platform.commercefacades.travel.TravellerData"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<!-- Modal -->
<div class="modal fade" id="ancillariesModal_${currentIndex}" tabindex="-1" role="dialog" aria-labelledby="ancillariesModal_${currentIndex}">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<spring:theme code="text.page.additional.information.modal.heading" text="Additional information" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<div class="col-xs-12">
						<div class="traveller-details clearfix">
							<h4><spring:theme code="text.page.additional.information.ancillaries.summary" text="Ancillaries Summary:" /></h4>
						    <c:forEach items="${reservationData.reservationItems}" var="reservationItem" >
							<c:if test="${fn:length(reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings) gt 1}">
								<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="routeTransportOffering" varStatus="idx">
									<c:if test="${idx.first}">
										<c:set var="originCode" value="${routeTransportOffering.sector.origin.code}" />
									</c:if>
									<c:if test="${idx.last}">
										<c:set var="routeOriginDestinationCode" value="${originCode} - ${routeTransportOffering.sector.destination.code}" />
									</c:if>
								</c:forEach>
								<travellerdetails:routeAncillariesInfo routeOriginDestinationCode="${routeOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerData.label}" />
							</c:if>
							<c:forEach items="${reservationItem.reservationItinerary.originDestinationOptions[0].transportOfferings}" var="transportOffering">
								<c:set var="sectorOriginDestinationCode" value="${transportOffering.sector.origin.code} - ${transportOffering.sector.destination.code}" />
								<travellerdetails:sectorAncillariesInfo sectorOriginDestinationCode="${sectorOriginDestinationCode}" reservationItem="${reservationItem}" travellerCode="${travellerData.label}" />
							</c:forEach>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="row">
					<dl class="col-xs-12">
						<c:choose>
							<c:when test="${(not empty travellerData.travellerInfo.email) or (not empty travellerData.travellerInfo.reasonForTravel) or (not empty travellerData.travellerInfo.membershipNumber) or (fn:length(travellerData.specialRequestDetail.specialServiceRequests) > 0 ) or (additionalSecurity)}">
								<c:if test="${not empty travellerData.travellerInfo.email}">
									<div class="col-xs-6">
										<dt>
											<spring:theme code="text.page.travellerdetails.form.heading.email" text="Email address" />
										</dt>
										<dd>${travellerData.travellerInfo.email}</dd>
									</div>
								</c:if>
								<c:if test="${not empty travellerData.travellerInfo.reasonForTravel}">
									<div class="col-xs-6">
										<dt>
											<spring:theme code="booking.confirmation.modal.reason.for.travel" text="Reason for travel" />
										</dt>
										<dd>${travellerData.travellerInfo.reasonForTravel}</dd>
									</div>
								</c:if>
								<c:if test="${not empty travellerData.travellerInfo.membershipNumber}">
									<div class="col-xs-6">
										<dt>
											<spring:theme code="text.page.travellerdetails.form.placeholder.membershipnumber" text="Membership No." />
										</dt>
										<dd>${travellerData.travellerInfo.membershipNumber}</dd>
									</div>
								</c:if>
								<c:if test="${fn:length(travellerData.specialRequestDetail.specialServiceRequests) > 0 }">
									<div class="col-xs-6">
										<dt>
											<spring:theme code="booking.confirmation.modal.special.assistance" text="Special Assistance" />
										</dt>
										<dd>
											<c:forEach var="specialServiceRequestData" items="${travellerData.specialRequestDetail.specialServiceRequests}" varStatus="status">
												${fn:escapeXml(specialServiceRequestData.name)}
												<c:if test="${not status.last}">
													,&nbsp;
												</c:if>
											</c:forEach>
										</dd>
									</div>
								</c:if>
								<c:if test="${additionalSecurity}">
									<div class="col-xs-6">
										<dt>
											<spring:theme code="booking.confirmation.modal.passenger.reference" text="Passenger Reference" />
										</dt>
										<dd class="passenger-reference">
												${fn:escapeXml(travellerData.simpleUID)}
										</dd>
									</div>
								</c:if>
							</c:when>
							<c:otherwise>
								<div class="col-xs-6">
									<dt>
										<spring:theme code="booking.confirmation.modal.additional.information.not.present" text="Additional information not present" />
									</dt>
								</div>
							</c:otherwise>
						</c:choose>
					</dl>
				</div>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary col-xs-offset-0 col-sm-offset-6 col-xs-12 col-sm-6" data-dismiss="modal">
					<spring:theme code="text.page.additional.information.modal.close.button" text="Close" />
				</button>
			</div>
		</div>
	</div>
</div>
