<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="guestdetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/guestdetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<section class="panel panel-primary panel-hotel">
	<%-- AddExtraToCartForm --%>
	<c:url value="/cart/accommodation/add-extra" var="addExtraToCartUrl" />
	<form:form id="y_addExtraToCartForm" name="addExtraToCartForm" action="${addExtraToCartUrl}" method="post">
		<input id="y_productCode" name="productCode" type="hidden" />
		<input id="y_roomStayReferenceNumber" name="roomStayReferenceNumber" type="hidden" />
		<input id="y_quantity" name="quantity" type="hidden" />
	</form:form>
	<%-- /AddExtraToCartForm --%>
	<div class="panel-heading">
		<h3 class="title">
			<spring:message code="text.ancillary.extras.accommodation.services.title" />
		</h3>
		<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#panel-hotel-extra" aria-expanded="true" aria-controls="panel-hotel-extra">Collapse / Expand</button>
	</div>
	<div class="panel-body collapse in">
		<div class="panel-hotel-extra row collapse in" id="panel-hotel-extra">
			<div class="tab-wrapper left-tabs">
				<ul class="nav nav-tabs" role="tablist">
					<c:forEach var="roomStay" items="${reservationData.roomStays}" varStatus="roomStayIndex">
						<li role="presentation" class="${roomStayIndex.first ? 'active':''}">
							<a href="#tab-${roomStayIndex.index}" aria-controls="tab-${roomStayIndex.index}" role="tab" data-toggle="tab">
								<span>
									<spring:message code="text.ancillary.extras.accommodation.services.rooms.tab.title" arguments="${availableServices[roomStayIndex.index].roomStayRefNumber + 1}" />
								</span>
								<span class="tab-line-1">
									${fn:escapeXml(roomStay.roomTypes[0].name)}
								</span>
							</a>
						</li>
					</c:forEach>
				</ul>
				<div class="tab-content">
					<c:forEach var="roomStay" items="${reservationData.roomStays}" varStatus="roomStayIndex">
						<c:set var="roomStayReferenceNumber" value="${availableServices[roomStayIndex.index].roomStayRefNumber}" />
						<c:if test="${not empty roomStay.roomPreferences and not empty roomStay.roomPreferences[0].code}">
							<c:set var="roomStayPreferenceCode" value="${roomStay.roomPreferences[0].code}" />
						</c:if>
						<div role="tabpanel" class="tab-pane fade in ${roomStayIndex.first ? 'active':''}" id="tab-${roomStayIndex.index}">
							<div class="hotel-ancillary-extras y_booking-extras" id="booking-extras_${roomStayIndex.index}">
								<ul class="list-group">
									<c:forEach var="service" items="${availableServices[roomStayIndex.index].services}" varStatus="serviceIndex">
										<li class="list-group-item">
											<div class="row input-row">
												<div class="col-xs-6 extra-item">
													<span>
														${fn:escapeXml(service.serviceDetails.product.name)}&nbsp;
														<span class="extra-cost">${fn:escapeXml(service.price.basePrice.formattedValue)}</span>
													</span>
												</div>
												<div class="col-xs-6 extra-item-selection">
													<c:choose>
														<c:when test="${service.serviceDetails.restriction.minQuantity == service.serviceDetails.restriction.maxQuantity}">
															<guestdetails:singleOfferExtra roomStayIndex="${roomStayIndex.index}" roomStayReferenceNumber="${roomStayReferenceNumber}" service="${service}" index="${serviceIndex.index}" />
														</c:when>
														<c:otherwise>
															<guestdetails:multipleOfferExtra roomStayIndex="${roomStayIndex.index}" roomStayReferenceNumber="${roomStayReferenceNumber}" service="${service}" index="${serviceIndex.index}" />
														</c:otherwise>
													</c:choose>
												</div>
											</div>
										</li>
									</c:forEach>
									<c:if test="${not empty accommodationRoomPreferenceMap}">
                                        <c:set var="roomPreferences" value="${accommodationRoomPreferenceMap[roomStayReferenceNumber]}" />
										<li class="list-group-item">
											<div class="row input-row">
												<div class="col-xs-6 extra-item">
													<span>
														<spring:theme code="accommodation.room.preference.title" text="Room Preferences" />
													</span>
												</div>
												<div class="col-xs-6 extra-item-selection">
													<label for="rm_1-extra-item-04" class="sr-only">
														<spring:theme code="accommodation.room.preference.title" text="Room Preferences" />
													</label>
													<select class="form-control room-preferences y_roomPreference" data-roomstayrefnum="${fn:escapeXml(roomStayReferenceNumber)}">
														<option id="y_roomPreferenceDefaultValue" ${empty roomStayPreferenceCode ? 'selected' : ''} value="${fn:escapeXml(defaultRoomBedPreferenceCode)}">
															<spring:theme code="accommodation.room.preference.default" text="No Room Preference" />
														</option>
														<c:forEach items="${roomPreferences}" var="roomPreference">
															<c:if test="${not empty roomPreference.value }">
																<option value="${fn:escapeXml(roomPreference.code)}" ${not empty roomStayPreferenceCode and roomStayPreferenceCode eq roomPreference.code ? 'selected' :''}>${fn:escapeXml(roomPreference.value)}</option>
															</c:if>
														</c:forEach>
													</select>
												</div>
											</div>
										</li>
									</c:if>
								</ul>
							</div>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>
</section>
