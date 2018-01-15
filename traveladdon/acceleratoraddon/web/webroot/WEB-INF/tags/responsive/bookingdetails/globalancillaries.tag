<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty reservationData.offerPricingInfos}">
	<div class="journey-wrapper">
		<div class="panel panel-default my-account-secondary-panel">
			<div class="panel-heading">
				<h3 class="panel-title title-collapse">
					<spring:theme code="text.page.managemybooking.globalancillaries" text="Additional Products" />
				</h3>
			</div>
			<div class="panel-body collapse in" id="section-globalancillaries"></div>
		</div>
		
		<c:forEach var="offerPricingInfo" items="${reservationData.offerPricingInfos}">
			<c:if test="${not empty offerPricingInfo.travellerBreakdowns}">
				<c:set var="displayPassengerSummary" value="true" />
			</c:if>
			<c:if test="${not empty offerPricingInfo.bookingBreakdown}">
				<c:set var="displaySharedAncillariesSummary" value="true" />
			</c:if>
		</c:forEach>
		
		<c:if test="${displayPassengerSummary}">
			<div class="panel panel-default my-account-secondary-panel collapse in y_passenger_summary_collapse" id="passenger-summary-section-globalancillaries">
				<div class="panel-heading divided">
					<h3 class="panel-title">
						<spring:theme code="text.page.managemybooking.reservationitem.passengersummary" text="Passenger Summary" />
					</h3>
				</div>
				<div class="panel-body">
					<div class="fieldset">
						<%-- Passenger Row --%>
						<c:forEach var="travellerData" items="${reservationData.reservationItems[0].reservationItinerary.travellers}">
							<c:set var="items" value="" />
							<c:forEach var="offerPricingInfo" items="${reservationData.offerPricingInfos}">
								<c:forEach var="travellerBreakdown" items="${offerPricingInfo.travellerBreakdowns}">
									<c:if test="${travellerBreakdown.traveller.label eq travellerData.label}">
										<c:set var="displaySeat" value="${true}" />
										<c:set var="item" value="${travellerBreakdown.quantity}&nbsp;x&nbsp;${offerPricingInfo.product.name}" />
										<c:choose>
											<c:when test="${empty items}">
												<c:set var="items" value="${item}" />
											</c:when>
											<c:otherwise>
												<c:set var="items" value="${items}, ${item}" />
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:forEach>
							</c:forEach>
							
							<c:if test="${not empty items}">
								<div class="row passenger-row">
									<div class="col-xs-12 col-sm-6 name-row">
										<dl>
											<dt class="sr-only">
												<spring:theme code="text.page.managemybooking.reservationitem.passengersummary.passengerinformation.passengername" text="Passenger Name:" />
											</dt>
											<dd>${fn:escapeXml(travellerData.travellerInfo.firstName.concat(" ").concat(travellerData.travellerInfo.surname))}</dd>
										</dl>
									</div>
									<div class="col-xs-12 col-sm-6">
										<dl>
											<ul class="list-unstyled">
												<li>${fn:escapeXml(items)}</li>
											</ul>
										</dl>
									</div>
								</div>
							</c:if>
						</c:forEach>
						<%-- Passenger Row --%>
					</div>
				</div>
			</div>
		</c:if>
		<c:if test="${displaySharedAncillariesSummary}">
			<div class="panel panel-default my-account-secondary-panel collapse in ${displayPassengerSummary ? '' : 'no-passenger-summary' }" id="ancillary-shared-section-globalancillaries">
				<div class="panel-heading divided">
					<h3 class="panel-title">
						<spring:theme code="text.page.managemybooking.reservationitem.sharedancillariessummary" text="Others" />
					</h3>
				</div>
				<div class="panel-body">
					<div class="fieldset">
						<div class="row">
							<div class="col-xs-12 col-sm-6">
								<div class="col-xs-12 col-sm-6"></div>
								<div class="col-xs-12 col-sm-6 col-md-6">
									<dl>
										<c:set var="items" value="" />
										<c:forEach var="offerPricingInfo" items="${reservationData.offerPricingInfos}">
											<c:if test="${not empty offerPricingInfo.bookingBreakdown}">
												<c:set var="item" value="${offerPricingInfo.bookingBreakdown.quantity}&nbsp;x&nbsp;${offerPricingInfo.product.name}" />
												<c:choose>
													<c:when test="${empty items}">
														<c:set var="items" value="${item}" />
													</c:when>
													<c:otherwise>
														<c:set var="items" value="${items}, ${item}" />
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:forEach>
										<c:if test="${not empty items}">
											<ul class="list-unstyled">
												<li>${fn:escapeXml(items)}</li>
											</ul>
										</c:if>
									</dl>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
</c:if>
