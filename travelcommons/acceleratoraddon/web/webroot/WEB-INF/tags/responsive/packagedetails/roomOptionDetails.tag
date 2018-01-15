<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ attribute name="roomStays" required="true" type="java.util.List"%>
<%@ attribute name="reservedRoomStayRefNumber" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel panel-accommodation panel-price-options panel-room-choices">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:theme code="text.package.details.room.options.title" arguments="${reservedRoomStayRefNumber+1}" text="Room ${fn:escapeXml(reservedRoomStayRefNumber+1)}" />
		</h3>
	</div>
	<div class="panel-body">
		<ul class="accommodation-items clearfix">
			<c:forEach var="roomStay" items="${roomStays}" varStatus="roomStayIdx">
				<c:if test="${roomStay.roomStayRefNumber eq reservedRoomStayRefNumber}">
					<div class="y_roomStayContainer">
						<fmt:formatDate value="${roomStay.checkInDate}" var="checkInDateFormatted" pattern="dd/MM/yyyy" />
						<fmt:formatDate value="${roomStay.checkOutDate}" var="checkOutDateFormatted" pattern="dd/MM/yyyy" />
						<input type="hidden" name="checkInDate" class="y_checkInDate" value="${fn:escapeXml(checkInDateFormatted)}"> <input type="hidden" name="checkOutDate" class="y_checkOutDate" value="${fn:escapeXml(checkOutDateFormatted)}"> <input type="hidden" name="roomStayRefNumber" class="y_roomStayRefNumber"
							value="${fn:escapeXml(reservedRoomStayRefNumber)}">
						<c:forEach var="roomType" items="${roomStay.roomTypes}">
							<li class="row price-option">
								<input type="hidden" name="accommodationCode" class="y_accommodationCode" value="${fn:escapeXml(roomType.code)}" />
								<div class="col-xs-7 col-sm-5 col-lg-5">
									<div class="col-xs-12">
										<div class="row">
											<h4>${fn:escapeXml(roomType.name)}</h4>
											<p>
												<a href="#" data-toggle="modal" data-target="#extra-room-details-modal-${fn:escapeXml(reservedRoomStayRefNumber)}-${roomStayIdx.index}">
													<spring:theme code="text.package.details.room.options.room.details.link" text="Room details" />
												</a>
											</p>
										</div>
									</div>
									<div class="col-xs-12">
										<div class="row">
											<dl>
												<dt>
													<spring:theme code="text.package.details.room.options.occupancy" text="Occupancy" />
												</dt>
												<dd>
													<span class="sr-only">
														<c:set var="maxGuests" value="0" />
														<c:forEach var="occupancy" items="${roomType.occupancies}">
															<c:set var="maxGuests" value="${ maxGuests + occupancy.quantityMax }" />
														</c:forEach>
														<spring:theme code="text.package.details.room.options.max.people.sr" arguments="${maxGuests}" />
													</span>
													<span aria-label="people">
														<c:forEach var="occupancy" items="${roomType.occupancies}" varStatus="guestOccupanciesIdx">
															<c:forEach begin="1" end="${occupancy.quantityMax}">
																<span class="person-icon ${occupancy.passengerType.code == 'child' || occupancy.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
															</c:forEach>
															<c:if test="${!guestOccupanciesIdx.last}">+ &nbsp;</c:if>
														</c:forEach>
													</span>
												</dd>
											</dl>
										</div>
									</div>
								</div>
								<div class="col-xs-5 col-sm-7 col-lg-7">
									<fieldset>
										<legend class="sr-only">
											<spring:theme code="text.package.details.room.selection.sr" arguments="Room selection" />
										</legend>
										<c:forEach var="ratePlan" items="${roomStay.ratePlans}" varStatus="planID">
											<p class="y_ratePlanAttributes clearfix">
												<span class="availability">
													<c:set var="stock" value="${ratePlan.availableQuantity}" />
													<c:choose>
														<c:when test="${stock gt 0}">
															<c:choose>
																<c:when test="${stock > 1}">
																	<spring:theme code="text.package.details.room.options.availability.multiple" arguments="${stock}" />
																</c:when>
																<c:otherwise>
																	<spring:theme code="text.package.details.room.options.availability.single" arguments="${stock}" />
																</c:otherwise>
															</c:choose>
														</c:when>
														<c:otherwise>
															<spring:theme code="text.package.details.room.options.fully.booked" text="FULLY BOOKED" />
														</c:otherwise>
													</c:choose>
												</span>
												<c:set var="ratePlanDescription">
													<ul class="small feature-list">
														<c:forEach var="guarantee" items="${ratePlan.guarantees}">
															<c:if test="${guarantee.longDescription != null}">
																<li>${fn:escapeXml(guarantee.longDescription)}</li>
															</c:if>
														</c:forEach>
														<c:forEach var="cancelPenalty" items="${ratePlan.cancelPenalties}">
															<c:if test="${cancelPenalty.formattedDescription != null}">
																<li>${fn:escapeXml(cancelPenalty.formattedDescription)}</li>
															</c:if>
														</c:forEach>
														<c:forEach var="ratePlanInclusion" items="${ratePlan.ratePlanInclusions}">
															<li>${fn:escapeXml(ratePlanInclusion.longDescription )}</li>
														</c:forEach>
														<c:forEach var="mealType" items="${ratePlan.mealTypes}">
															<li>${fn:escapeXml(mealType)}</li>
														</c:forEach>
													</ul>
												</c:set>
												<a tabindex="0" class="link-trigger" role="button" data-toggle="popover" data-html="true" data-trigger="focus" data-placement="bottom" data-content="${fn:escapeXml(ratePlanDescription)}">
													${fn:escapeXml(ratePlan.description)} <i class="glyphicon glyphicon-info-sign"></i>
												</a>
												<c:if test="${stock gt 0}">
													<input type="hidden" name="ratePlanCode" value="${fn:escapeXml(ratePlan.code)}" class="y_ratePlanCode" />
													<c:forEach var="roomRate" items="${ratePlan.roomRates}" varStatus="roomRateIdx">
														<fmt:formatDate value="${roomRate.stayDateRange.startTime}" var="roomRateDateFormatted" pattern="dd/MM/yyyy" />
														<input type="hidden" name="roomRate${roomRateIdx.index}" code="${fn:escapeXml(roomRate.code)}" value="${roomRateDateFormatted}" class="y_roomRate" />
													</c:forEach>
													<span class="price-select">
														<input id="room-${fn:escapeXml(reservedRoomStayRefNumber)}_ratePlan-${planID.index}_option-${roomStayIdx.index}" type="radio" name="room_${fn:escapeXml(reservedRoomStayRefNumber)}" class="y_changeAccommodationButton" ${fn:escapeXml(ratePlan.selected ? 'checked' : '' )} />
														<label for="room-${fn:escapeXml(reservedRoomStayRefNumber)}_ratePlan-${planID.index}_option-${roomStayIdx.index}">
															<span class="selected">
																<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
															</span>
															<span class="select">
																<spring:theme code="text.accommodation.details.accommodation.select.price" text="select" />
															</span>
															<span>${fn:escapeXml(ratePlan.priceDifference.formattedValue)}</span>
														</label>
													</span>
												</c:if>
											</p>
										</c:forEach>
									</fieldset>
								</div>
							</li>
							<packageDetails:packageRoomDetails roomType="${roomType}" roomStayIndex="${roomStayIdx.index}" reservedRoomStayRefNumber="${reservedRoomStayRefNumber}" />
						</c:forEach>
					</div>
				</c:if>
			</c:forEach>
		</ul>
	</div>
</div>
