<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:url value="/cart/best-offer-accommodations/add" var="chooseBestOfferingsURL" />
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel panel-accommodation clearfix">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:theme code="text.accommodation.details.best.option.title.price" />${bestCombinationAccommodationPriceData.formattedValue}
			<c:choose>
				<c:when test="${bestCombinationAccommodationAvailabilityResponse.lengthOfStay gt 1}">
					<spring:theme code="text.accommodation.details.best.option.title.nights" arguments="${bestCombinationAccommodationAvailabilityResponse.lengthOfStay}" />
				</c:when>
				<c:otherwise>
					<spring:theme code="text.accommodation.details.best.option.title.night" arguments="${bestCombinationAccommodationAvailabilityResponse.lengthOfStay}" />
				</c:otherwise>
			</c:choose>
		</h3>
	</div>
	<ul class="accommodation-items clearfix group-option">
		<c:forEach var="roomStay" items="${bestCombinationAccommodationAvailabilityResponse.roomStays}" varStatus="idx">
			<c:if test="${not empty roomStay.groupIndex}">
				<c:forEach var="roomType" items="${roomStay.roomTypes}" varStatus="accommodationID">
					<li class="col-xs-12">
						<div class="col-xs-5 col-sm-4 accommodation-image">
							<div class="row">
								<a href="#" data-toggle="modal" data-target="#best-accommodation-gallery-modal-${fn:escapeXml(idx.index)}">
									<c:forEach items="${roomType.images}" var="accommodationImage">
										<c:if test="${accommodationImage.format == 'list'}">
											<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
										</c:if>
									</c:forEach>
								</a>
							</div>
						</div>
						<div class="accommodation-details col-xs-offset-5 col-xs-7 col-sm-offset-4 col-sm-8">
							<div class="row">
								<div class="col-xs-12">
									<div class="clearfix">
										<div class="col-xs-12">
											<div class="row">
												<h3>
													<spring:theme code="text.accommodation.details.best.option.accommodation.title" arguments="${roomStayGroupList[roomStay.groupIndex].quantity},${roomType.name}" />
												</h3>
											</div>
										</div>
										<div class="col-xs-12">
											<div class="row">
												<p>${fn:escapeXml(roomType.description)}</p>
											</div>
										</div>
									</div>
								</div>
								<div class="col-xs-12 col-sm-6 accommodation-number-people">
									<c:forEach var="plan" items="${roomStay.ratePlans}" varStatus="planID">
										<dl>
											<dt>
												<spring:theme code="text.accommodation.details.accommodation.occupancy" />
											</dt>
											<dd>
												<span class="sr-only">
													<c:set var="maxGuests" value="0"></c:set>
													<c:forEach var="occupancy" items="${plan.occupancies}">
														<c:set var="maxGuests" value="${ maxGuests + occupancy.quantityMax }"></c:set>
													</c:forEach>
													<spring:theme code="text.accommodation.details.accommodation.max.people.screen.reader" arguments="${maxGuests}" />
												</span>
												<span aria-label="people">
													<c:forEach var="occupancy" items="${plan.occupancies}" varStatus="guestOccupanciesIdx">
															<c:forEach begin="1" end="${occupancy.quantityMax}">
																<span class="person-icon ${occupancy.passengerType.code == 'child' || occupancy.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
															</c:forEach>
															 <c:if test="${!guestOccupanciesIdx.last}">+ &nbsp;</c:if>
													</c:forEach>
												</span>
											</dd>
										</dl>
									</c:forEach>
								</div>
							</div>
						</div>
					</li>
                    <accommodationDetails:accommodationGallery roomType="${roomType}" index="${accommodationID.index}" bestRoom="true"/>
				</c:forEach>
			</c:if>
		</c:forEach>
	</ul>
	<div class="select-option clearfix">
		<div class="col-xs-12 col-sm-offset-8 col-sm-offset-8 col-sm-4">
			<form:form modelAttribute="accommodationAddToCartBookingForm" class="y_accommodationAvailabilityResponse" action="${chooseBestOfferingsURL}" method="post">
				<c:forEach items="${bestCombinationAccommodationAvailabilityResponse.roomStays}" var="roomStay" varStatus="rmsidx">
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].accommodationOfferingCode" value="${bestCombinationAccommodationAvailabilityResponse.accommodationReference.accommodationOfferingCode}" />
					<c:set var="checkInDate">
						<fmt:formatDate value="${roomStay.checkInDate}" pattern="dd/MM/yyyy" />
					</c:set>
					<c:set var="checkOutDate">
						<fmt:formatDate value="${roomStay.checkOutDate}" pattern="dd/MM/yyyy" />
					</c:set>
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].checkInDateTime" value="${checkInDate}" />
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].checkOutDateTime" value="${checkOutDate}" />
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].accommodationCode" value="${roomStay.roomTypes[0].code}" />
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].ratePlanCode" value="${roomStay.ratePlans[0].code}" />
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].numberOfRooms" value="1" />
					<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].roomStayRefNumber" value="${roomStay.roomStayRefNumber}" />
					<c:forEach items="${roomStay.ratePlans[0].roomRates}" var="roomRate" varStatus="rmridx">
						<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].roomRateCodes[${rmridx.index}]" value="${roomRate.code}" />
						<fmt:formatDate var="roomRateCheckInDate" value="${roomRate.stayDateRange.startTime}" pattern="dd/MM/yyyy" />
						<form:input type="hidden" path="accommodationAddToCartForms[${rmsidx.index}].roomRateDates[${rmridx.index}]" value="${roomRateCheckInDate}" />
					</c:forEach>
				</c:forEach>
				<input type="hidden" name="accommodationDetailsPageURL" value="${accommodationDetailsPageURL}" />
				<form:button class="btn btn-secondary col-xs-12">
					<spring:message code="text.accommodation.details.best.option.choose.option" text="Choose this option" />
				</form:button>
			</form:form>
		</div>
	</div>
</div>
